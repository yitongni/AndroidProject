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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
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

    private User currentUser=new User();

    private FirebaseUser myuser;

    private DatabaseReference mDataBaseUsers;
    private ArrayList<Category> myCategory;

    static final int REQUEST_CODE = 0;

    //Navigation Bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
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

        //Set up buttons and textView
        textView=(TextView) findViewById(R.id.textViewBudget);
        addCategoryButton =(Button)findViewById(R.id.addCategory);
        addBudgetButton=(Button)findViewById(R.id.editBudget);

        //Get currently log in user from database


        myuser= FirebaseAuth.getInstance().getCurrentUser();

        //Gets specific user
        mDataBaseUsers= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());

        //currentUser=
        myCategory=new ArrayList<>();

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

        //retrieveCurrentUserInformation();
        //showsChart();

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
                // A contact was picked.  Here we will just display it
                // to the user.
                if(data.hasExtra("Category")){
                    String categoryName=data.getStringExtra("Category");

                    Double cost=Double.parseDouble(data.getStringExtra("CategoryCost"));

                    //If User made the category already
                    if(currentUser.containsCategory(categoryName)){
                        currentUser.getUserCategory().get(currentUser.getCategoryPosition(categoryName)).addCost(cost);
                    }
                    else {
                        Category category=new Category(categoryName);
                        category.addCost(cost);
                        //myCategory.add(category);
                        currentUser.addCategory(category);
                    }
                    updateCategoryDatabase();
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
                    //textView.setText(getString(R.string.budget, String.format("%.2f", amount)));
                    updateBudgetDatabase();
                }
            }
        }
    }

    //updatesCategoryDatabase
    private void updateCategoryDatabase() {
        Log.d(TAG, "Updating Category");
        final DatabaseReference databaseuser3=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterating through all the nodes
                //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Entering");

//                    User a_user = postSnapshot.getValue(User.class);
//                    Log.d(TAG, a_user.getEmail());
//                    Log.d(TAG, Double.toString(a_user.getBudget()));
                    for(int i=0; i<currentUser.getUserCategory().size(); i++)
                    {
                        Log.d(TAG, currentUser.getUserCategory().get(i).getCategory());
                    }
                    databaseuser3.child("Category").setValue(currentUser.getUserCategory());
                //}
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
                //iterating through all the nodes
                Log.d(TAG, "Updating Budget2");
                //User a_user = postSnapshot.getValue(User.class);
                databaseuser2.child("budget").setValue(currentUser.getBudget());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

//    public void populateListView(ArrayList<Category> categories) {
//        CategoryAdapter adapter=new CategoryAdapter(this, categories);
//        ListView listView = (ListView) findViewById(R.id.list_item);
//        listView.setAdapter(adapter);
//    }


    public void retrieveCurrentUserInformation() {
        Log.d(TAG, "Retrieving user information");

        final DatabaseReference databaseuser=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());

        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myCategory.clear();


//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    if(postSnapshot.child("userID").getValue(String.class).equals(myuser.getUid())) {
                        //Log.d(TAG, postSnapshot.child("email").getValue(String.class));
                currentUser.setEmail(dataSnapshot.child("email").getValue(String.class));
                Log.d(TAG, "Email: " + currentUser.getEmail());

                currentUser.setUserID(dataSnapshot.child("userID").getValue(String.class));
                Log.d(TAG, "UserID: " + currentUser.getUserID());

                        //if(!(postSnapshot.child("budget").getValue(String.class).trim().equals(""))) {
                String mybudget = dataSnapshot.child("budget").getValue(Double.class).toString();
                currentUser.setBudget(Double.parseDouble(mybudget));
                Log.d(TAG, "Budget: " + currentUser.getBudget());

//                        Log.d(TAG, "Budget" + currentUser.getBudget());
//                    if(!mybudget.equals(null)) {
//
//                    }
                        //}
                //DataSnapshot postSnapShot=dataSnapshot.child("Category");
                for(DataSnapshot postSnapShot : dataSnapshot.child("Category").getChildren()) {

                    String categoryName=postSnapShot.getValue(Category.class).getCategory();
                    ArrayList<Double> cost=postSnapShot.getValue(Category.class).getCost();
                    if(currentUser.containsCategory(categoryName)){
                        currentUser.getUserCategory().get(currentUser.getCategoryPosition(categoryName)).setCost(cost);
                    }
                    else {
                        Category category=new Category(categoryName);
                        category.setCost(cost);
                        //myCategory.add(category);
                        currentUser.addCategory(category);
                    }
//                    Category newCat = new Category(postSnapShot.getValue(Category.class).getCategory());
//                    newCat.setCost(postSnapShot.getValue(Category.class).getCost());
                    //currentUser.addCategory(newCat);
                    //myCategory.add(newCat);
                }
//                        newCat.setCost(postSnapshot.getValue(Category.class).getCost());
//
                //currentUser.addCategory(newCat);
                for (int i = 0; i < currentUser.getUserCategory().size(); i++) {
                    Log.d(TAG, "Category" + currentUser.getUserCategory().get(i).getCategory());
                    for (int j = 0; j < currentUser.getUserCategory().get(i).getCost().size(); j++) {
                        Log.d(TAG, "Cost" + currentUser.getUserCategory().get(i).getCost().get(j));
                    }
                }
                showPieChart();
                //populateListView(currentUser.getUserCategory());
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

        Double spent=currentUser.getTotalSpent();
        float totalSpent = spent.floatValue();
        for(int i=0; i<currentUser.getUserCategory().size(); i++)
        {
            Double cost=currentUser.getUserCategory().get(i).getTotalCost();
            float totalCost = cost.floatValue();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            pieData.add(new SliceValue(totalCost, color).setLabel(currentUser.getUserCategory().get(i).getCategory()+": "+ String.valueOf(totalCost)));
        }
        final PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(20);
        pieChartView.setPieChartData(pieChartData);
        pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                Log.d(TAG, "You pressed: " + currentUser.getUserCategory().get(arcIndex).getCategory());
                Intent newIntent=new Intent(BudgetActivity.this, CategoryActivity.class);
                newIntent.putExtra("Category", currentUser.getUserCategory().get(arcIndex));
                startActivity(newIntent);
            }

            @Override
            public void onValueDeselected() {

            }
        });

    }
}
