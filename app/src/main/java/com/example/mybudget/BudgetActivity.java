package com.example.mybudget;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BudgetActivity extends AppCompatActivity {

    private static final String TAG = "BudgetActivity";
    //private EditText editText;
    private Button addBudgetButton, addCategoryButton;
    private TextView textView;

    private User user;
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

        mDataBaseUsers= FirebaseDatabase.getInstance().getReference("/").child("users");
//        currentUser=mDataBaseUsers.child(myuser.getUid());
        //Log.d(TAG, currentUser.child("Category").getKey());

        user=new User();
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
    }

    //Retrieves data from database
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "OnStart " + myuser.getUid());

        DatabaseReference databaseuser=mDataBaseUsers.child(myuser.getUid()).child("Category");
        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myCategory.clear();
                Log.d(TAG, "Entering");
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Category newCat=postSnapshot.getValue(Category.class);
                    //Log.d(TAG, r);
                    Category newCategory=new Category(newCat.getCategory());
                    user.addCategory(newCategory);
                    myCategory.add(newCategory);
                }
                populateListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                if(data.hasExtra("Category")){

                    Category category=new Category(data.getStringExtra("Category"));
                    myCategory.add(category);
                    user.addCategory(category);
                    updateDatabase();
                }
                else if(data.hasExtra("Budget")) {
                    String mybudget = data.getStringExtra("Budget");
                    double amount;
                    if (!(mybudget.trim().equals(""))) { //Make sure it not empty string

                        amount = Double.parseDouble(mybudget);
                    } else {
                        amount = 0.00; //Set amount to 0
                    }
                    user.setBudget(amount);
                    textView.setText(getString(R.string.budget, String.format("%.2f", amount)));
                    updateDatabase();
                }
            }
        }
    }

    //updatesDatabase
    private void updateDatabase() {
        Log.d(TAG, "Adding to database");
        mDataBaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Entering");
                    if (postSnapshot.getKey().equals(myuser.getUid())) {

                        User a_user = postSnapshot.getValue(User.class);
                        Log.d(TAG, a_user.getEmail());
                        Log.d(TAG, Double.toString(a_user.getBudget()));
                        for(int i=0; i<a_user.getUserCategory().size(); i++)
                        {
                            Log.d(TAG, a_user.getUserCategory().get(i).getCategory());
                        }
                        mDataBaseUsers.child(a_user.getUserID()).child("Category").setValue(myCategory);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private void init() {
//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
//                        actionId == EditorInfo.IME_ACTION_DONE ||
//                        event.getAction() == KeyEvent.ACTION_DOWN ||
//                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
//                    getMyBudget();
//                }
//                return false;
//            }
//        });
//    }

//    public void getMyBudget() {
//        mybudget = editText.getText().toString(); //Get EditText
//        if (!(mybudget.trim().equals(""))) { //Make sure it not empty string
//
//            amount = Double.parseDouble(mybudget);
//        } else {
//            amount = 0.00; //Set amount to 0
//        }
//
//        textView.setText(getString(R.string.budget, String.format("%.2f", amount))); //Set the text to display amount
//        //editText.setVisibility(View.GONE); //Make EditText disappear
//        //textView.setVisibility(View.VISIBLE); //Make TextView Appear
//    }

    public void populateListView()
    {
        CategoryAdapter adapter=new CategoryAdapter(this, myCategory);
        ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(adapter);
    }

//    public void onBudgetClick(View view)
//    {
////        textView.setVisibility(View.GONE);
////        editText.setVisibility(View.VISIBLE);
//        init();
//    }
}
