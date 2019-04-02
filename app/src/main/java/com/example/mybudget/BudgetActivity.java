package com.example.mybudget;

import android.content.DialogInterface;
import android.content.Intent;
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
    private Button addBudgetButton;
    private TextView textView;
    private double amount;
    private String mybudget;
    private Button addCategoryButton;
    private User user;
    private DatabaseReference mDataBaseUsers;
    private FirebaseAuth currentUser;
    private FirebaseUser myuser;

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
        textView=(TextView) findViewById(R.id.textViewBudget);
//        editText=(EditText) findViewById(R.id.editTextBudget);
        addCategoryButton =(Button)findViewById(R.id.addCategory);
        addBudgetButton=(Button)findViewById(R.id.editBudget);
        myuser= FirebaseAuth.getInstance().getCurrentUser();
        //user=currentUser;

        mDataBaseUsers= FirebaseDatabase.getInstance().getReference("/");

        user=new User();
        myCategory=new ArrayList<>();

        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(BudgetActivity.this, PopupActivity.class);
                myintent.putExtra("ButtonID", addBudgetButton.getId());
                startActivityForResult(myintent, REQUEST_CODE);
            }
        });

//        editText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBudgetClick(v);
//            }
//        });

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(BudgetActivity.this, PopupActivity.class);
                myintent.putExtra("ButtonID", addCategoryButton.getId());
                startActivityForResult(myintent, REQUEST_CODE);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                if(data.hasExtra("Category")){

                    Category category=new Category(data.getStringExtra("Category"));
                    user.addCategory(category);
                    for(int i=0; i<user.getUserCategory().size(); i++)
                    {
                        Log.d(TAG, user.getUserCategory().get(i).getCategory());
                    }
                    //myCategory.add(category);
                    populateListView();

                    Log.d(TAG, "Adding to database");
                    mDataBaseUsers.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                Log.d(TAG, "Entering");
                                if (postSnapshot.getKey().equals(myuser.getUid()))
                                {
                                    User a_user = postSnapshot.getValue(User.class);
                                    Log.d(TAG, a_user.getEmail());
                                    mDataBaseUsers.child(a_user.getUserID()).child("Category").setValue(user.getUserCategory());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if(data.hasExtra("Budget"))
                {
                    mybudget=data.getStringExtra("Budget");
                    if (!(mybudget.trim().equals(""))) { //Make sure it not empty string

                        amount = Double.parseDouble(mybudget);
                    } else {
                        amount = 0.00; //Set amount to 0
                    }
                    user.setBudget(amount);
                    textView.setText(getString(R.string.budget, String.format("%.2f", amount)));
                }
            }
        }
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
        CategoryAdapter adapter=new CategoryAdapter(this, user.getUserCategory());
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
