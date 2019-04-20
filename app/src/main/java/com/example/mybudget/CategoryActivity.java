package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private TextView mTextView;
    private Category mCat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTextView=(TextView)findViewById(R.id.textViewCategory);
        init();
    }

    private void init(){
        Log.d(TAG, "Initiating");
        Intent intent = getIntent();
        if (intent.hasExtra("Category")) {
            mCat = (Category) intent.getSerializableExtra("Category");
        }
        populateView();
    }

    private void populateView(){
        Log.d(TAG, "Populating View");

        mTextView.setText(mCat.getCategory());

        CategoryAdapter adapter=new CategoryAdapter(this, mCat.getCost());

        ListView listView = (ListView) findViewById(R.id.costListView);
        listView.setAdapter(adapter);
    }
}
