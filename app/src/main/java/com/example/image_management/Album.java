package com.example.image_management;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Album extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);
        System.out.println("ALBUM");
        String path = Environment.getExternalStorageDirectory().toString()+"/DCIM";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            System.out.println("FileName:" + files[i].getName());
        }
    }

    public void back(View v){
        this.finish();
    }
}
