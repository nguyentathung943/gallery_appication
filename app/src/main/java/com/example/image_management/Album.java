package com.example.image_management;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Album extends AppCompatActivity implements AlbumAdapter.ClickItemListener{
    ArrayList<String> listFile;
    AlbumAdapter albumAdapter;
    RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        listFile = new ArrayList<>();
        listFile.add("Favourite");
        String path = Environment.getExternalStorageDirectory().toString()+"/DCIM";
        File directory = new File(path);
        File[] files = directory.listFiles();

        for (File i : files)
        {
            if(i.isDirectory())
            {
                listFile.add(i.getName());
            }
        }
        recyclerView = findViewById(R.id.album_recyclerView);
        recyclerView.setHasFixedSize(true);
        albumAdapter = new AlbumAdapter(listFile, this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(albumAdapter);
    }

    public void back(View v){
        this.finish();
    }

    @Override
    public void onClick(int position) {
        System.out.println("POS" + position);
        System.out.println("Ablum " + listFile.get(position));
        Intent intent = new Intent(this, Archive.class);
        intent.putExtra("album", listFile.get(position));
        startActivity(intent);
    }
}
