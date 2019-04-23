package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private TextView mTextView;
    private FirebaseUser myuser;
    private Category mCat;
    private HashMap<String, Category> expenses;
    private ArrayList<Double> cost=new ArrayList<Double>();
    private Category myCat;
    private CategoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTextView=(TextView)findViewById(R.id.textViewCategory);
        myuser= FirebaseAuth.getInstance().getCurrentUser();
        expenses=new HashMap<>();
        init();
    }

    //Getting Category
    private void init(){
        Log.d(TAG, "Initiating");
        Intent intent = getIntent();
        if (intent.hasExtra("Category")) {
            mCat = (Category)intent.getSerializableExtra("Category");
        }
        retrieveInformation();
    }

    //Delete on user touch
    private void deleteCost(int position){
        Log.d(TAG, "Deleting");
        cost.remove(position);
        adapter.notifyDataSetChanged();
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(mCat.getCategory()).child("cost");
//        databaseReference.removeValue()
    }

    private void retrieveInformation(){
        Query query= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(mCat.getCategory()).child("cost");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Retrieving from database");

                GenericTypeIndicator<ArrayList<Double>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Double>>(){};
                cost=dataSnapshot.getValue(genericTypeIndicator);
                for(int i=0; i<cost.size(); i++){
                    Log.d(TAG, cost.get(i).toString());
                }
                populateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void populateView(){
        Log.d(TAG, "Populating View");

        mTextView.setText(mCat.getCategory());

        adapter=new CategoryAdapter(this, cost);

        ListView listView = (ListView) findViewById(R.id.costListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteCost(position);
            }
        });

    }
}
