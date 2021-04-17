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
//        for(int i = 1; i < listFile.size(); i++){
//            File dir = new File(Environment.getExternalStorageDirectory().toString()+"/DCIM/" + listFile.get(i));
////            List<File> filezz = new ArrayList<>();
////            filezz.addAll(Arrays.asList(dir.listFiles()));
//            File[] filezz = dir.listFiles();
//            for(File x : filezz){
//                System.out.println("zzz " + x);
//            }
//        }
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

        Intent intent = new Intent(this, AlbumDetail.class);
        if(position != 0)
            intent.putExtra("position", position);
        intent.putExtra("path", listFile.get(position));
        startActivityForResult(intent, 1);
    }
}
