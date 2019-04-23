package com.example.mybudget;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class BudgetActivity extends AppCompatActivity {

    private static final String TAG = "BudgetActivity";

    //private EditText editText;
    private Button addBudgetButton, addCategoryButton;
    private TextView textView;

    private User currentUser;
    private FirebaseUser myuser;

    private HashMap<String, ArrayList<Category>> userExpenses;

    static final int REQUEST_CODE = 0;

    //Navigation Bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    Intent myintent =new Intent(BudgetActivity.this, ProfileActivity.class);
                    startActivity(myintent);
                    return true;
                case R.id.myBudget:
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        currentUser=new User();
        userExpenses=new HashMap<>();

        //Set up buttons and textView
        textView=(TextView) findViewById(R.id.textViewBudget);
        addCategoryButton =(Button)findViewById(R.id.addCategory);
        addBudgetButton=(Button)findViewById(R.id.editBudget);

        //Get currently log in user from database
        myuser= FirebaseAuth.getInstance().getCurrentUser();

        //On Button Clicks
        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(BudgetActivity.this, PopupActivity.class);
                myintent.putExtra("ButtonID", addBudgetButton.getId());
                startActivityForResult(myintent, REQUEST_CODE);
            }
        });

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(BudgetActivity.this, PopupActivity.class);
                myintent.putExtra("ButtonID", addCategoryButton.getId());
                startActivityForResult(myintent, REQUEST_CODE);
            }
        });

        //Navigation bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //Retrieves data from database
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart " + myuser.getUid());
        retrieveCurrentUserInformation();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if(data.hasExtra("Category")){
                    String categoryName=data.getStringExtra("Category");
                    String description=data.getStringExtra("Description");
                    Double cost=Double.parseDouble(data.getStringExtra("CategoryCost"));

                    updateCategoryDatabase(categoryName, description, cost);
                }
                else if(data.hasExtra("Budget")) {
                    String mybudget = data.getStringExtra("Budget");
                    Log.d(TAG, "Budget= " + mybudget);
                    double amount;
                    if (!(mybudget.trim().equals(""))) { //Make sure it not empty string
                        amount = Double.parseDouble(mybudget);
                    } else {
                        amount = 0.00; //Set amount to 0
                    }
                    Log.d(TAG, "Budget= " + amount);
                    currentUser.setBudget(amount);
                    updateBudgetDatabase();
                }
            }
        }
    }

    //updatesCategoryDatabase when new expenses is added
    private void updateCategoryDatabase(final String name, final String description, final Double cost) {
        Log.d(TAG, "Updating Category");
        final DatabaseReference databaseuser3=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Updating");

                Log.d(TAG, databaseuser3.push().getKey());
                String id=databaseuser3.push().getKey();
                Category category=new Category(description, cost, id, name);
                currentUser.addCategory(name, description, cost, id);
                databaseuser3.child("Category").child(name).child(id).setValue(category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //updateBudgetDatabase
    private void updateBudgetDatabase(){
        Log.d(TAG, "Updating Budget");
        final DatabaseReference databaseuser2=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Updating Budget2");
                databaseuser2.child("budget").setValue(currentUser.getBudget());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void retrieveCurrentUserInformation() {
        Log.d(TAG, "Retrieving user information");

        final DatabaseReference databaseuser=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userExpenses.clear();
                currentUser=new User();

                currentUser.setEmail(dataSnapshot.child("email").getValue(String.class));
                Log.d(TAG, "Email: " + currentUser.getEmail());

                currentUser.setUserID(dataSnapshot.child("userID").getValue(String.class));
                Log.d(TAG, "UserID: " + currentUser.getUserID());

                String mybudget = dataSnapshot.child("budget").getValue(Double.class).toString();
                currentUser.setBudget(Double.parseDouble(mybudget));
                Log.d(TAG, "Budget: " + currentUser.getBudget());

                //Gets the categeory
                for(DataSnapshot postSnapShot : dataSnapshot.child("Category").getChildren()){
                    for(DataSnapshot postSnapShot2 : postSnapShot.getChildren()){
                        Log.d(TAG, "Retrieving");
                        Log.d(TAG, "Food: " + postSnapShot2.getValue(Category.class).getCategory());
                        Log.d(TAG, "Description: " + postSnapShot2.getValue(Category.class).getDescription());
                        Log.d(TAG, "Cost: " + postSnapShot2.getValue(Category.class).getCost());
                        Log.d(TAG, "ID: " + postSnapShot2.getValue(Category.class).getId());

                        Category category=new Category(postSnapShot2.getValue(Category.class).getDescription(),
                                postSnapShot2.getValue(Category.class).getCost(), postSnapShot2.getValue(Category.class).getId(),
                                postSnapShot2.getValue(Category.class).getCategory());

                        ArrayList<Category> mylist =new ArrayList<>();
                        mylist.add(category);
                        if(userExpenses.get(category.getCategory())==null){
                            Log.d(TAG, "Adding to map");
                            userExpenses.put(category.getCategory(),  mylist);
                        }
                        else {
                            Log.d(TAG, "Adding to existing map");
                            userExpenses.get(category.getCategory()).add(category);
                        }
                    }
                }
                for (Map.Entry<String, ArrayList<Category>> entry : userExpenses.entrySet()) {

                    String key = entry.getKey();
                    Log.d(TAG, "KEY: " + key);

                    ArrayList<Category> categories=entry.getValue();
                    for(int i=0; i<categories.size(); i++){
                        Log.d(TAG, "Cost: " + categories.get(i).getCost());
                        currentUser.addCategory(key, categories.get(i).getDescription(), categories.get(i).getCost(), categories.get(i).getId());
                    }
                }
                showPieChart();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showPieChart(){
        textView.setText(getString(R.string.budget, String.format("%.2f", currentUser.getBudget())));
        Log.d(TAG, "Showing Pie Chart");

        final PieChartView pieChartView = findViewById(R.id.chart);

        final List<SliceValue> pieData = new ArrayList<>();

        //Iterate map and get each value for pie chart
        for (Map.Entry<String, ArrayList<Category>> entry : currentUser.getUserCategory().entrySet()) {
            Log.d(TAG, "Category: "+entry.getKey());
            ArrayList<Category> categories=entry.getValue();
            Double cost=0.0; //Calculate user total spending for that category
            for(int i=0; i<categories.size(); i++){
                cost+=categories.get(i).getCost();
            }
            Log.d(TAG, "Cost: " + cost);
            float totalCost = cost.floatValue();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            pieData.add(new SliceValue(totalCost, color).setLabel(entry.getKey()+": "+ String.valueOf(totalCost)));
        }
        final PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(20);
        pieChartView.setPieChartData(pieChartData);
        pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                String string=String.copyValueOf(value.getLabelAsChars());
                int iend = string.indexOf(":"); //Finds the first occurrence of ":"

                String subString= string.substring(0 , iend); //this will give category
                Log.d(TAG, "You pressed: " + subString);

                Intent newIntent=new Intent(BudgetActivity.this, CategoryActivity.class);
                newIntent.putExtra("Category", subString);
                startActivity(newIntent);
            }

            @Override
            public void onValueDeselected() {

            }
        });

    }
}
