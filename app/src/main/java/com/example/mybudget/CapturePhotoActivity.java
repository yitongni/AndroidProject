package com.example.mybudget;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CapturePhotoActivity extends AppCompatActivity {

    private static final String TAG = "Photo Activity";
    private FirebaseUser myuser;
    private AutoCompleteTextView editTextCategory;
    private ImageButton btnTakePicture;
    private Button savePicture;
    private ImageView imageView;
    private Uri imageUri;

    FirebaseStorage storage;
    StorageReference storageReference;

    private DatabaseReference databaseReference;
    private File imageFile;
    private String pathToFile;
    private boolean cameraPermission = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog_layout);

        myuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Images");
        editTextCategory = (AutoCompleteTextView) findViewById(R.id.textCategory);
        btnTakePicture=(ImageButton)findViewById(R.id.takePicture);
        savePicture=(Button)findViewById(R.id.saveImage);
        imageView=(ImageView)findViewById(R.id.picture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraPermission();
                if(cameraPermission){
                    createImageFile();
                    if(imageFile!=null){
                        pathToFile=imageFile.getAbsolutePath();
                        Intent cameraIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageUri= FileProvider.getUriForFile(CapturePhotoActivity.this, "com.example.mybudget", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }
            }
        });
        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category=editTextCategory.getText().toString();
                if(!category.trim().equals(""))
                {
                    storage=FirebaseStorage.getInstance();
                    storageReference=storage.getReference();

                    final StorageReference ref = storageReference.child(myuser.getUid()).child(category).child(imageFile.getName());
                    ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            //After saving goes back to previous activity
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imageuri=uri.toString();
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String id=databaseReference.push().getKey();
                                            databaseReference.child(id).setValue(imageuri);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                            Toast.makeText(CapturePhotoActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Intent myIntent=new Intent(CapturePhotoActivity.this, ImageActivity.class);
                            finish();
                            startActivity(myIntent);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CapturePhotoActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(CapturePhotoActivity.this, "Uploading", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent cameraIntent){
        if(requestCode==CAMERA_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Log.d(TAG, "Setting Image");
                Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void createImageFile(){
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File storage=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            imageFile=File.createTempFile(timestamp, ".jpg", storage);
        }
        catch (IOException error){
            Log.d(TAG, "Error"+ error);
        }
    }

    private void getCameraPermission(){
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_REQUEST_CODE);
        }
        else {
            cameraPermission=true;
        }
    }
}