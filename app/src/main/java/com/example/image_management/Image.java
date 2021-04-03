package com.example.image_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image extends Activity {
    String path;
    ImageView back;
    ImageView myImage;
    ImageView info;
    public void shareOn(){
        File file = new File(path);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share File"));
    }
    @Override
    public void onBackPressed(){
        finish();
    }
    public void imageViewMenu(View view){
        switch (view.getId()){
            case R.id.btn_share:
                shareOn();
                break;
        }
    }
    public void ImageInfo(String path){

        File a = new File(path);
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(a.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("creationTime: " + attr.creationTime());
        System.out.println("lastAccessTime: " + attr.lastAccessTime());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(this);
        TextView date = new TextView(this);
        TextView size = new TextView(this);
        TextView walllpp = new Button(this);
        name.setText("Name: " + a.getName());
        name.setTextSize(20);
        date.setText("Creation date: " + attr.creationTime());
        date.setTextSize(20);
        size.setText("Size: " + attr.size() + " bytes");
        size.setTextSize(20);
        layout.addView(name);
        layout.addView(date);
        layout.addView(size);
        layout.addView(walllpp);
        builder.setView(layout);
        builder.setNegativeButton("OK",null);
        walllpp.setText("Set image as wallpaper");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        walllpp.setOnClickListener(view ->{
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.parse(path),"image/*");
            intent.putExtra("mimeType", "image/*");
            startActivity(Intent.createChooser(intent, "Set as:"));
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view);
        info = findViewById(R.id.info);
        path = getIntent().getStringExtra("path");
        System.out.println("PATH" + path);
        Bitmap myBitmap = BitmapFactory.decodeFile(path);


        myImage = findViewById(R.id.img_show);
        myImage.setImageBitmap(myBitmap);
        info.setOnClickListener(view->{
            ImageInfo(path);
        });
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(view->{
            this.finish();
        });
    }
}
