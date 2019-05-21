package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    //TAG
    private static final String TAG="ProfileActivity";

    private Button logoutButton, startNewBudget;
    private FirebaseAuth mAuth;
    private TextView email;
    private FirebaseUser myuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        logoutButton=(Button) findViewById(R.id.buttonLogOut);
        email=(TextView) findViewById(R.id.textViewEmail);
        startNewBudget=(Button) findViewById(R.id.buttonStartNewBudget);

        myuser= FirebaseAuth.getInstance().getCurrentUser();
        if(myuser!=null){
            email.setText(myuser.getEmail());
        }

        mAuth= FirebaseAuth.getInstance();

        //Logs user out
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        //start new
        startNewBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOldBudget();
            }
        });

        initNavigation();

    }

    //Logs out user and brings them back to the log in page
    private void LogOut() {
        mAuth.signOut();
        finish();
        Intent myIntent= new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

    //Delete all the budgets and images from database for user to start a new one
    private void deleteOldBudget(){
        final DatabaseReference databaseuser= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid());
        databaseuser.child("Category").removeValue();
        databaseuser.child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get each images and delete from the storage
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String imageName=dataSnapshot1.child("name").getValue(String.class);
                    Log.d(TAG, imageName);
                    StorageReference storageReference=FirebaseStorage.getInstance().getReference().child(myuser.getUid()).child(imageName);
                    storageReference.delete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseuser.child("Images").removeValue();
    }

    //Sets up navigation bar
    private void initNavigation() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu=navigation.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //switches to activity based on what was pressed
                switch (item.getItemId()) {
                    case R.id.profile:
                        return true;
                    case R.id.myBudget:
                        Intent myintent =new Intent(ProfileActivity.this, BudgetActivity.class);
                        startActivity(myintent);
                        return true;
                    case R.id.images:
                        Intent imageIntent =new Intent(ProfileActivity.this, ImageActivity.class);
                        startActivity(imageIntent);
                        return true;
                }
                return false;
            }
        });
    }
}
