package com.example.image_management;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener {
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<String> path;
    ArrayList<Integer> type;
    Configuration config;
    String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.ALBUM
    };
    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive);
        init();
        config = new Configuration(getApplicationContext());
        config.getConfig();
        getAllImages();
        recyclerView = findViewById(R.id.recyclerView);
        listItem = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        for(String i : path){
            listItem.add(new Item(i));
        }
        ListAdapter listAdapter = new ListAdapter(listItem, path, this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(listAdapter);
    }
    public void init() {
        path = new ArrayList<>();
        type = new ArrayList<>();
    }
    public void getAllImages() {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        Cursor cursor = cursorLoader.loadInBackground();
        int columnMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION);
        int columnSize = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
        while (cursor.moveToNext()) {
            long duration = cursor.getLong(columnDuration);
            Instant instant = Instant.ofEpochMilli(duration);
            ZonedDateTime zdt = ZonedDateTime.ofInstant ( instant , ZoneOffset.UTC );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "HH:mm:ss" );
            String output = formatter.format ( zdt );
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            System.out.println("Data");
            System.out.println("Duration " + output);
            System.out.println("Size " + cursor.getString(columnSize));
            System.out.println("Type " + cursor.getString(columnMediaType));
            path.add(absolutePathOfImage);
            type.add(cursor.getInt(columnMediaType));
        }
        cursor.close();
    }
    void open_with_photos(int position){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(
                Uri.parse(path.get(position)),"image/*");
        startActivity(intent);
    }
    void openwithThis(int position){
        Intent intent = new Intent(this, Image.class);
        intent.putExtra("path", path.get(position));
        startActivityForResult(intent, 1);
    }
    @Override
    public void onClick(int position) {
        System.out.println("Type " + type.get(position));
        System.out.println("Image type " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        if(type.get(position) == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        {
            System.out.println("Image show: " + config.isDefault);
            if(config.isDefault==1){
                openwithThis(position);
            }
            else {
                open_with_photos(position);
            }
        }
        else if(type.get(position) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        {
            Intent intent = new Intent(this, Video.class);
            intent.putExtra("path", path.get(position));
            startActivity(intent);
        }
    }
    public void back(View v){
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            recreate();
        }
    }
}