package com.example.image_management;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Image extends Activity {
    String path;
    ImageView back;
    ImageView myImage;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view);
        path = getIntent().getStringExtra("path");
        System.out.println("PATH" + path);
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        myImage = findViewById(R.id.img_show);
        myImage.setImageBitmap(myBitmap);
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(view->{
            this.finish();
        });
    }
}
