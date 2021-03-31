package com.example.image_management;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Image extends AppCompatActivity {
    String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getIntent().getStringExtra("path");
        System.out.println("PATH" + path);
        setContentView(R.layout.img_view);
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        ImageView myImage = (ImageView) findViewById(R.id.img_show);
        myImage.setImageBitmap(myBitmap);
    }
}
