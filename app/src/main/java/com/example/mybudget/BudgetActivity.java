package com.example.mybudget;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class BudgetActivity extends AppCompatActivity {

    private static final String TAG = "BudgetActivity";

    private TextView totalSpent;
    private User currentUser;
    private FirebaseUser myuser;
    private FloatingActionButton floatingActionButton;

    private HashMap<String, ArrayList<Category>> userExpenses;
    private ArrayList<String> categoryList; //Determines all unique category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        currentUser=new User();
        userExpenses=new HashMap<>();
        categoryList=new ArrayList<>();

        //Set up buttons and textView
        totalSpent=(TextView) findViewById(R.id.textViewSpending);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.floating_action_button);

        //Get currently log in user from database
        myuser= FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryDialog();
            }
        });
        initNavigationBar();
    }

    //Shows Nagivation Bar
    private void initNavigationBar() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu=navigation.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Intent myintent =new Intent(BudgetActivity.this, ProfileActivity.class);
                        startActivity(myintent);
                        return true;
                    case R.id.myBudget:
                        return true;
                    case R.id.images:
                        Intent imageIntent =new Intent(BudgetActivity.this, ImageActivity.class);
                        startActivity(imageIntent);
                        return true;
                }
                return false;
            }
        });
    }

    //Retrieves data from database
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart " + myuser.getUid());
        retrieveCurrentUserInformation();
    }

    //Show dialog to enter budget
    private void showCategoryDialog(){
        //Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        final EditText editTextDescription = (EditText) dialogView.findViewById(R.id.description);
        final EditText editTextCost = (EditText) dialogView.findViewById(R.id.categoryCost);
        final AutoCompleteTextView editTextCategory = (AutoCompleteTextView) dialogView.findViewById(R.id.editTextCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,categoryList);
        editTextCategory.setAdapter(adapter);
        final Button btn=(Button)dialogView.findViewById(R.id.chooseDate);
        final TextView textview =(TextView)dialogView.findViewById(R.id.date);

        //Allows user to pick a date
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();

                int day= calendar.get(Calendar.DAY_OF_MONTH);
                int month= calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                Log.d(TAG, "" + calendar.get(Calendar.YEAR));
                Log.d(TAG, "" + (calendar.get(Calendar.MONTH)+1));
                Log.d(TAG, "" + calendar.get(Calendar.DAY_OF_MONTH));

                DatePickerDialog datePickerDialog=new DatePickerDialog(BudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        textview.setText(day + "/" +(month+1) +"/" +year);
                    }
                }, day, month, year);
                //Set calender to current day
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        //When click save, retrieve budget
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String description =editTextDescription.getText().toString();
                Double cost=0.0;
                if(!(editTextCost.getText().toString().equals(""))){
                    cost= Double.parseDouble(editTextCost.getText().toString()); //get cost
                }
                String category=editTextCategory.getText().toString(); //get category
                String date=textview.getText().toString(); //get date
                if (!(description.trim().equals("")) && !(category.trim().equals("")) && !(date.trim().equals(""))) { //Make sure it not empty string
                    updateCategoryDatabase(category, description, cost, date);
                }
                else{
                    Toast.makeText(BudgetActivity.this, "Please fill out everything", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //updates database when new expenses is added
    private void updateCategoryDatabase(final String name, final String description, final Double cost, final String date) {
        Log.d(TAG, "Updating Category");
        final DatabaseReference databaseuser3=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Updating");
                Log.d(TAG, databaseuser3.push().getKey());
                String id=databaseuser3.push().getKey(); //gets the push key
                Category category=new Category(description, cost, id, name, date); //set it to the category
                currentUser.addCategory(name, description, cost, id, date);
                databaseuser3.child("Category").child(name).child(id).setValue(category); //add to database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Get current user information
    private void retrieveCurrentUserInformation() {
        Log.d(TAG, "Retrieving user information");

        final DatabaseReference databaseuser=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userExpenses.clear();
                currentUser=new User();
                categoryList.clear();

                currentUser.setEmail(dataSnapshot.child("email").getValue(String.class));
                Log.d(TAG, "Email: " + currentUser.getEmail());

                currentUser.setUserID(dataSnapshot.child("userID").getValue(String.class));
                Log.d(TAG, "UserID: " + currentUser.getUserID());

                //Gets the categeory
                for(DataSnapshot postSnapShot : dataSnapshot.child("Category").getChildren()){
                    Log.d(TAG, "Your Category: " + postSnapShot.getKey());
                    categoryList.add(postSnapShot.getKey());
                    for(DataSnapshot postSnapShot2 : postSnapShot.getChildren()){
                        Log.d(TAG, "Retrieving");
                        Log.d(TAG, "Category: " + postSnapShot2.getValue(Category.class).getCategory());
                        Log.d(TAG, "Description: " + postSnapShot2.getValue(Category.class).getDescription());
                        Log.d(TAG, "Cost: " + postSnapShot2.getValue(Category.class).getCost());
                        Log.d(TAG, "ID: " + postSnapShot2.getValue(Category.class).getId());

                        //Retrieving category
                        Category category=new Category(postSnapShot2.getValue(Category.class).getDescription(),
                                postSnapShot2.getValue(Category.class).getCost(), postSnapShot2.getValue(Category.class).getId(),
                                postSnapShot2.getValue(Category.class).getCategory(), postSnapShot2.getValue(Category.class).getDate());

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

                //Adding to current user
                for (Map.Entry<String, ArrayList<Category>> entry : userExpenses.entrySet()) {
                    String key = entry.getKey();
                    Log.d(TAG, "KEY: " + key);

                    ArrayList<Category> categories=entry.getValue();
                    for(int i=0; i<categories.size(); i++){
                        Log.d(TAG, "Cost: " + categories.get(i).getCost());
                        currentUser.addCategory(key, categories.get(i).getDescription(), categories.get(i).getCost(), categories.get(i).getId(), categories.get(i).getDate());
                    }
                }
                showPieChart();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //display the user data as a pie chart
    private void showPieChart(){
        Log.d(TAG, "Showing Pie Chart");

        final PieChartView pieChartView = findViewById(R.id.chart);

        final List<SliceValue> pieData = new ArrayList<>();

        //Displays how much the user has spent
        totalSpent.setText(String.format(getResources().getString(R.string.totalSpent), String.format("%.2f", currentUser.getTotalSpent())));
        Log.d(TAG, "Total Spent: " + currentUser.getTotalSpent().toString());

        //Iterate map and get total spent for each category and display it on a pie chart
        for (Map.Entry<String, ArrayList<Category>> entry : currentUser.getUserCategory().entrySet()) {
            Log.d(TAG, "Category: "+entry.getKey());
            Double cost=currentUser.getTotalSpentForSingleCategory(entry.getKey());

            Log.d(TAG, "Cost: " + cost);

            //Convert to float for the use of pie chart
            float totalCost = cost.floatValue();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            pieData.add(new SliceValue(totalCost, color).setLabel(entry.getKey()+": "+ String.valueOf(totalCost)));
        }

        //Display the pie chart
        final PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(15);
        pieChartView.setPieChartData(pieChartData);

        //Selects section you clicked and lets you view the section in detail
        pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                String string=String.copyValueOf(value.getLabelAsChars());
                int index = string.indexOf(":"); //Finds the first occurrence of ":"

                String subString= string.substring(0 , index); //this will give category
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
