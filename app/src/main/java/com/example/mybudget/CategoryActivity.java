package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private TextView mTextView;
    private Category mCat;
    private FirebaseUser myuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTextView=(TextView)findViewById(R.id.textViewCategory);
        myuser= FirebaseAuth.getInstance().getCurrentUser();
        init();

    }

    //Getting Category
    private void init(){
        Log.d(TAG, "Initiating");
        Intent intent = getIntent();
        if (intent.hasExtra("Category")) {
            mCat = (Category) intent.getSerializableExtra("Category");
        }
        populateView();
    }

    //Delete on user touch
    private void deleteCost(int position){
        Log.d(TAG, "Deleting");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("/").child("user").child(myuser.getUid()).child("Category");
    }

    private void populateView(){
        Log.d(TAG, "Populating View");

        mTextView.setText(mCat.getCategory());

        CategoryAdapter adapter=new CategoryAdapter(this, mCat.getCost());

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
