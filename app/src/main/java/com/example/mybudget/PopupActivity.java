package com.example.mybudget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PopupActivity extends Activity {

    private static final String TAG = "PopupActivity";
    private Button btn_save;
    private Spinner spinner1;
    private EditText cat_name, budget, categoryCost;
    private String category, butgetCost, catCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        cat_name=(EditText)findViewById(R.id.categoryName);
        budget=(EditText)findViewById(R.id.editTextBudget);
        categoryCost=(EditText)findViewById(R.id.categoryCost);

        Intent myIntent=getIntent();
        if(myIntent.hasExtra("ButtonID"))
        {
            int intValue = myIntent.getIntExtra("ButtonID", 0);
            if(intValue==R.id.editBudget)
            {
                cat_name.setVisibility(View.GONE);
                categoryCost.setVisibility(View.GONE);
                budget.setVisibility(View.VISIBLE);

                btn_save=(Button)findViewById(R.id.btn_save);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        butgetCost = budget.getText().toString();
                        if (!(butgetCost.trim().equals(""))) { //Make sure it not empty string
                            Log.d(TAG, "onClick: Name" + butgetCost);
                            Intent intent = new Intent();
                            intent.putExtra("Budget", butgetCost);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(PopupActivity.this, "Please Enter something.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if(intValue==R.id.addCategory)
            {
                budget.setVisibility(View.GONE);
                cat_name.setVisibility(View.VISIBLE);
                categoryCost.setVisibility(View.VISIBLE);

                btn_save=(Button)findViewById(R.id.btn_save);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = cat_name.getText().toString();
                        catCost= categoryCost.getText().toString();
                        if (!(category.trim().equals(""))) { //Make sure it not empty string
                            Log.d(TAG, "onClick: Name" + category);

                            Intent intent = new Intent();
                            intent.putExtra("Category", category);
                            intent.putExtra("CategoryCost", catCost);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(PopupActivity.this, "Please Enter something.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

//        btn_save=(Button)findViewById(R.id.btn_save);
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                category = cat_name.getText().toString();
//                if (!(category.trim().equals(""))) { //Make sure it not empty string
//                    Log.d(TAG, "onClick: Name" + category);
//
//                    Intent intent = new Intent();
//                    intent.putExtra("DATA", category);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//                else
//                {
//                    Toast.makeText(PopupActivity.this, "Please Enter something.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.5));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);

    }
}
