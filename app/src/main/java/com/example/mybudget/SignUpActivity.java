package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    //TAG
    private static final String TAG="SignUp";

    //Widget
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText confirmPassword;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDataBaseUsers;
    private User myuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth= FirebaseAuth.getInstance();
        mDataBaseUsers=FirebaseDatabase.getInstance().getReference("/").child("users");

        myuser=new User();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword=(EditText) findViewById(R.id.confirmPassword);

        Button buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        TextView textViewLogin = (TextView) findViewById(R.id.textViewLogIn);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent myIntent= new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //Get user username and password
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String password2 = confirmPassword.getText().toString().trim();

        //Makes sure username is not empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        //Makes sure password is not empty
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //Makes sure user entered identical password
        if(!(password.equals(password2))){
            Toast.makeText(this,"Passwords does not match",Toast.LENGTH_LONG).show();
            return;
        }

        createAccount(email, password);
    }

    //Creates an account with email and password, adds it to database and signs them in
    private void createAccount(final String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            myuser.setEmail(user.getEmail());
                            myuser.setUserID(user.getUid());
                            mDataBaseUsers.child(user.getUid()).setValue(myuser);
                            finish();
                            Intent myintent=new Intent(SignUpActivity.this, ProfileActivity.class);
                            startActivity(myintent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
