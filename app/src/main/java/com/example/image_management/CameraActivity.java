package com.example.image_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.image_management.R;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.Manifest;
import static java.sql.DriverManager.println;

public class CameraActivity extends AppCompatActivity {
    String currentPhotoPath;
    int REQUEST_IMAGE_CAPTURE = 1; //OPEN CAMERA CODE
    int CAMERA_PERM_CODE=101; // CAMERA PERMISSION CODE
    int REQUEST_SAVE_FILE = 2; // SAVE FILE CODE
    CardView camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
    public void askCameraPermission(){
        int permissionCheckCam = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckRead = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        System.out.println(permissionCheckCam + permissionCheckRead + permissionCheckWrite);
        if((permissionCheckCam + permissionCheckRead + permissionCheckWrite) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
            ){
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder.setTitle("Please grant those permissions");
                builder.setMessage("Camera, Storage read/write access!");
                builder.setPositiveButton("Ok", (dialog, which) ->
                        ActivityCompat.requestPermissions(CameraActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        CAMERA_PERM_CODE));
                builder.setNegativeButton("No",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        CAMERA_PERM_CODE);
            }
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        }
        else{
            Toast.makeText(this,"Permission granted!",Toast.LENGTH_LONG).show();
            openCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_PERM_CODE){
            if(grantResults.length > 0 && (grantResults[0]+grantResults[1]+grantResults[2]) == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
            else{
                Toast.makeText(this, "Camera permission is required!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File storageDir = new File(filepath + "/DCIM/Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            Toast.makeText(this,"Image saved",Toast.LENGTH_SHORT).show();
        }
    }
    private void galleryAddPic() {
        System.out.println("Hello");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void openCamera() {
//        Intent open = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(open,REQUEST_IMAGE_CAPTURE);

        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//
//        }
    }
    public void main_menu_onclick(View view) {
        switch (view.getId()) {
            case R.id.mn_camera: {
                askCameraPermission();
                break;
            }
            case R.id.mn_setting: {
                setContentView(R.layout.setting);
                break;
            }
            case R.id.mn_PIN: {
                setContentView(R.layout.password);
                break;
            }
            case R.id.mn_album: {
                break;
            }
            case R.id.mn_archive: {
                Intent x = new Intent(CameraActivity.this, Archive.class);
                startActivity(x);

                break;
            }
            case R.id.mn_theme: {
                setContentView(R.layout.theme);
                break;
            }
            case R.id.btn_back: {
                setContentView(R.layout.main_menu);
                break;
            }
        }
    }
}