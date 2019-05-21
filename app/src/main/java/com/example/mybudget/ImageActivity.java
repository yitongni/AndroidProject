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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

        //Click button to take a picture
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.capture_image);
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

    //Retrieve the images
    private void retrieveImages(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                Log.d(TAG, "OnStart: retreiving" );
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, postSnapshot.getKey());
                    //Log.d(TAG, postSnapshot.getValue(String.class));

                    ImageInformation imageInformation=new ImageInformation(postSnapshot.child("uri").getValue(String.class), postSnapshot.child("id").getValue(String.class), postSnapshot.child("name").getValue(String.class));
                    images.add(imageInformation);
                }
                populateAlbum();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Populate the view with images from firebase
    private void populateAlbum(){

        final ImageAdapter imageAdapter=new ImageAdapter(ImageActivity.this, images);
        album.setAdapter(imageAdapter);

        album.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteImage(images.get(i));
                images.remove(i);
                imageAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    //When you click and hold an image it will delete it
    private void deleteImage(final ImageInformation imageInformation){
        StorageReference storageReference = storage.child(myuser.getUid()).child(imageInformation.getName());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ImageActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
                databaseReference.child(imageInformation.getId()).removeValue();
            }
        });

    }


    //Initiate navigation bar
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
