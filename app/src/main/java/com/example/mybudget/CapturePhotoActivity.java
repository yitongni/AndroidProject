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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private File imageFile;
    String pathToFile;
    private boolean cameraPermission = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog_layout);

        myuser = FirebaseAuth.getInstance().getCurrentUser();
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
                        Uri imageUri= FileProvider.getUriForFile(CapturePhotoActivity.this, "com.example.mybudget", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }
            }
        });
        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent cameraIntent){
        if(requestCode==CAMERA_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Log.d(TAG, "Setting Image");
                Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
                //Picasso.with(CapturePhotoActivity.this).load(pathToFile).into(imageView);
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
