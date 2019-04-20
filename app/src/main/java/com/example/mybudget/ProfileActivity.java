package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG="ProfileActivity";
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private String userid;
    private TextView email;
    private FirebaseUser myuser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    return true;
                case R.id.myBudget:
                    Intent myintent =new Intent(ProfileActivity.this, BudgetActivity.class);
                    startActivity(myintent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        logoutButton=(Button) findViewById(R.id.buttonLogOut);
        email=(TextView) findViewById(R.id.textViewEmail);

        myuser= FirebaseAuth.getInstance().getCurrentUser();
        email.setText(myuser.getEmail());

        mAuth= FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getUid();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, user.getUid());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void LogOut() {
        mAuth.signOut();
        finish();
        Intent myIntent= new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(myIntent);
    }
}
