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
import android.widget.TableLayout;
import android.widget.TableRow;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private TextView mTextView, totalSpentTextView;
    private FirebaseUser myuser;
//    private Category mCat;
    private HashMap<String, Category> expenses;
    private String category;
    private ArrayList<Category> categories=new ArrayList<>();
//    private ArrayList<Double> cost=new ArrayList<Double>();
//    private Category myCat;
    private CategoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTextView=(TextView)findViewById(R.id.textViewCategory);
        totalSpentTextView=(TextView)findViewById(R.id.textViewTotalSpent);
        myuser= FirebaseAuth.getInstance().getCurrentUser();
        expenses=new HashMap<>();
        init();
    }

    //Getting Category
    private void init(){
        Log.d(TAG, "Initiating");
        Intent intent = getIntent();
        if (intent.hasExtra("Category")) {
            category = intent.getStringExtra("Category");
        }
        retrieveInformation();
    }

    //Delete on user touch
    private void deleteCost(final int position){
        Log.d(TAG, "Deleting");
        //cost.remove(position);
        final String description=categories.get(position).getDescription();
        final Double cost=categories.get(position).getCost();
        final String id=categories.get(position).getId();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(category).child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                databaseReference.removeValue();
//                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
//                    Log.d(TAG, "Deleting: " + postSnapShot.child("category").getValue(String.class));
//                    Log.d(TAG, "Deleting: " + postSnapShot.child("cost").getValue(Double.class).toString());
//
//                    if(postSnapShot.child("category").getValue(String.class).equals(description)){
//                        if(postSnapShot.child("cost").getValue(Double.class).equals(cost)){
//                            Log.d(TAG, "Deleting: " + postSnapShot.child("category").getValue(String.class));
//                            Log.d(TAG, "Deleting: " + postSnapShot.child("cost").getValue(Double.class).toString());
//                            databaseReference.setValue(categories);
//                            //postSnapShot.child("cost").getRef().removeValue();
//                            break;
//                        }
//                    }
//                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }
//
    private void retrieveInformation(){
        Query query= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Retrieving from database");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Category category=new Category(postSnapshot.child("description").getValue(String.class), postSnapshot.child("cost").getValue(Double.class), postSnapshot.child("id").getValue(String.class), postSnapshot.child("category").getValue(String.class));
                    categories.add(category);
                }
                for(int i=0; i<categories.size(); i++){
                    Log.d(TAG, categories.get(i).getCategory());
                    Log.d(TAG, categories.get(i).getCost().toString());
                    Log.d(TAG, categories.get(i).getDescription());
                    Log.d(TAG, categories.get(i).getId());
                }
                populateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void calculateTotalSpent(){
        Double totalspent=0.0;
        for(int i=0; i<categories.size(); i++){
            totalspent+=categories.get(i).getCost(); //Calculate user total price
        }
        totalSpentTextView.setText(String.format(getString(R.string.totalCost), totalspent.toString()));
    }
    private void populateView(){
        Log.d(TAG, "Populating View");

        mTextView.setText(category);

        calculateTotalSpent();

        adapter=new CategoryAdapter(this, categories);

        ListView listView = (ListView) findViewById(R.id.costListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteCost(position);
                categories.remove(position);
                calculateTotalSpent();
            }
        });

    }
}
