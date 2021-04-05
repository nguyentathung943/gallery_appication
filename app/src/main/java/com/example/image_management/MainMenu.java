package com.example.image_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainMenu extends AppCompatActivity {
    private static final int SETTING_CONSTANT = 10;
    String currentPhotoPath;
    int REQUEST_IMAGE_CAPTURE = 1; //OPEN CAMERA CODE
    int CAMERA_PERM_CODE=101; // CAMERA PERMISSION CODE
    int REQUEST_VIDEO_RECORD = 3;
    Configuration config;
    CardView camera;
    Switch sw;
    int REQUEST_SAVE_FILE = 2; // SAVE FILE CODE
    private int requestCode;
    private int resultCode;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        config = new Configuration(getApplicationContext());
        config.getConfig();
        System.out.println("DARK MODE " + config.isDarkMode);
        askPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        sw = findViewById(R.id.themeSwitch);
        if(config.ThemeMode()==1){
            sw.setChecked(true);
            sw.setText(R.string.dark);
        }
        else{
            sw.setChecked(false);
            sw.setText(R.string.light);
        }
        sw.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                config.saveConfig(1,config.language);
                sw.setText("Dark");
                sw.setChecked(true);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                config.saveConfig(0,config.language);
                sw.setText("Light");
                sw.setChecked(false);
            }
            finish();
            startActivity(new Intent(MainMenu.this, MainMenu.this.getClass()));
        });
    }
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
    public void ShowChangePIN(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle(R.string.change_pin);
        builder.setMessage("4 digits are required");
        builder.setView(input);
        builder.setPositiveButton("Ok", (dialog, which) ->{
                    String a = input.getText().toString();
                    if (a.length() < 4){
                        Toast.makeText(this,"PIN must contains 4 digits", Toast.LENGTH_SHORT).show();
                        ShowChangePIN();
                    }
                    else{
                        Context context = getApplicationContext();
                        try {
                            FileOutputStream fout = context.openFileOutput("PIN.txt", Context.MODE_PRIVATE);
                            fout.write(input.getText().toString().getBytes());
                            Toast.makeText(this, "PIN Changed Successfully",Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
        });
        builder.setNegativeButton("No",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void askPermission(){
        int permissionCheckCam = ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.CAMERA);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckRead = ContextCompat.checkSelfPermission(MainMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        System.out.println(permissionCheckCam + permissionCheckRead + permissionCheckWrite);
        if((permissionCheckCam + permissionCheckRead + permissionCheckWrite) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this,Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this,Manifest.permission.READ_EXTERNAL_STORAGE)
            ){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                builder.setTitle("Please grant those permissions to continue using this app!");
                builder.setMessage("Camera, Storage");
                builder.setPositiveButton("Ok", (dialog, which) ->
                        ActivityCompat.requestPermissions(MainMenu.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        CAMERA_PERM_CODE));
                builder.setNegativeButton("No",(dialog, which) ->
                        askPermission()
                 );
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
//        else{
//
////            Toast.makeText(this,"Permission granted!",Toast.LENGTH_LONG).show();
//            //openCamera();
//        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_PERM_CODE){
            if(grantResults.length > 0 && (grantResults[0]+grantResults[1]+grantResults[2]) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions are granted",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Permissions are required!",Toast.LENGTH_LONG).show();
                askPermission();
            }
        }
    }
    void openVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createMediaFile(".mp4");
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takeVideoIntent .putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_RECORD );
        }
    }
    public void openCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createMediaFile(".jpg");
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
    }
    public void optionCamera(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle("Camera Option");
        builder.setMessage("Do you want to use camera for image or video");
        LinearLayout layout= new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        Button Camera = new Button(this);
        Camera.setText("Camera");
        Button Video = new Button(this);
        Video.setText("Video");
        layout.addView(Camera);
        layout.addView(Video);
        builder.setView(layout);
        builder.setNegativeButton("CANCEL",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Camera.setOnClickListener(view ->{
            alertDialog.hide();
            openCamera();
        });
        Video.setOnClickListener(view ->{
            alertDialog.hide();
            openVideo();
        });
    }
    private File createMediaFile(String suffix) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File storageDir = new File(filepath + "/DCIM/Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                suffix,         /* suffix */
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
            galleryAddFile();
            Toast.makeText(this,"Image saved",Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == SETTING_CONSTANT){
            recreate();
        }
        else if  (requestCode == REQUEST_VIDEO_RECORD  && resultCode == RESULT_OK){
            galleryAddFile();
            Toast.makeText(this,"Video saved",Toast.LENGTH_SHORT).show();
        }
    }
    private void galleryAddFile() {
        System.out.println("Hello");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    public void main_menu_onclick(View view) {
        switch (view.getId()) {
            case R.id.mn_camera: {
                optionCamera();
                break;
            }
            case R.id.mn_setting: {
                Intent intent = new Intent(MainMenu.this, Setting.class);
                intent.putExtra("theme", config.ThemeMode());
                intent.putExtra("language", config.languageState());
                startActivityForResult(intent, SETTING_CONSTANT);
                break;
            }
            case R.id.mn_PIN: {
                ShowChangePIN();
                break;
            }
            case R.id.mn_album: {
                startActivity(new Intent(MainMenu.this, Album.class));
                break;
            }
            case R.id.mn_archive: {
                Intent x = new Intent(MainMenu.this, Archive.class);
                startActivity(x);
                break;
            }
            case R.id.btn_back: {
                setContentView(R.layout.main_menu);
                break;
            }
        }
    }
}