package com.example.mybudget;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "Image Activity";

    private FirebaseUser myuser;
    private FloatingActionButton floatingActionButton;
    private boolean cameraPermission = false;
    private static final int CAMERA_REQUEST_CODE = 100;
    private boolean imageTaken=false;
    private GridView album;
    private ArrayList<ImageInformation> images=new ArrayList<>();
    private StorageReference storage;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        myuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Images");

        album=(GridView)findViewById(R.id.album);
        storage= FirebaseStorage.getInstance().getReference();

        floatingActionButton=(FloatingActionButton)findViewById(R.id.capture_image);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent=new Intent(ImageActivity.this, CapturePhotoActivity.class);
                startActivity(cameraIntent);
            }
        });

        initNavigationBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart " + myuser.getUid());
        retrieveImages();
    }

    private void retrieveImages(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, postSnapshot.getKey());
                    Log.d(TAG, postSnapshot.getValue(String.class));
                    ImageInformation imageInformation=new ImageInformation(postSnapshot.getValue(String.class), postSnapshot.getKey());
                    images.add(imageInformation);
                }
                populateAlbum();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void populateAlbum(){
        //StorageReference filePath = storage.child(myuser.getUid());
        album.setAdapter(new ImageAdapter(ImageActivity.this, images));
    }


    private void initNavigationBar() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu=navigation.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Log.d(TAG, "Clicked Profile");
                        Intent myintent =new Intent(ImageActivity.this, ProfileActivity.class);
                        startActivity(myintent);
                        return true;
                    case R.id.myBudget:
                        Intent imageIntent =new Intent(ImageActivity.this, BudgetActivity.class);
                        startActivity(imageIntent);
                        return true;
                    case R.id.images:
                        return true;
                }
                return false;
            }
        });
    }
}
