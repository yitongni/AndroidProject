package com.example.mybudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CategoryFillActivity extends AppCompatActivity {

    private static final String TAG = "CategoryFillActivity";
    private TextView name;
    private EditText category;
    private String mCategory;
    private Button addCostButton;
    private EditText cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryfill);

        category=(EditText)findViewById(R.id.editTextCategory);
        name=(TextView) findViewById(R.id.textViewCategory);
        addCostButton=(Button)findViewById(R.id.addCost);
        cost=(EditText) findViewById(R.id.editTextAddCost);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick(v);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick(v);
            }
        });

        addCostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick(v);
            }
        });

    }

    private void init(View view) {
        if(view.getId()== R.id.editTextCategory || view.getId()==R.id.textViewCategory) {
            category.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN ||
                            event.getAction() == KeyEvent.KEYCODE_ENTER) {
                        getCategory();
                    }
                    return false;
                }
            });
        }
        else if(view.getId()==R.id.addCost)
        {
            getCost();
        }
    }

    public void getCategory()
    {
        mCategory = category.getText().toString(); //Get EditText
        if (!(mCategory.trim().equals(""))) { //Make sure it not empty string
            name.setText(mCategory);
        } else {
        }

        category.setVisibility(View.GONE); //Make EditText disappear
        name.setVisibility(View.VISIBLE); //Make TextView Appear
    }

    public void getCost()
    {
    }

    public void onCategoryClick(View view)
    {
        if(view.getId()== R.id.editTextCategory || view.getId()==R.id.textViewCategory)
        {
            Log.d(TAG, "onCategoryClick: Entered");
            name.setVisibility(View.GONE);
            category.setVisibility(View.VISIBLE);
            init(view);
        }
        else if(view.getId()==R.id.addCost)
        {
            addCostButton.setVisibility(View.GONE);
            cost.setVisibility(View.VISIBLE);
            init(view);
        }
    }
}
