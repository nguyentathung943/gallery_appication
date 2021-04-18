package com.example.image_management;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<String> path;
    ArrayList<Integer> type;
    ArrayList<DateTimeFormatter> duration;
    DisplayAdapter displayAdapter;
    Configuration config;
    ListAdapter listAdapter;
    int VIEW_REQUEST = 555;
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
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        for(String p:path){
            System.out.println(p);
        }
        listAdapter = new ListAdapter(listItem, path, this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(listAdapter);
    }
    public void init() {
        path = new ArrayList<>();
        type = new ArrayList<>();
        duration = new ArrayList<>();
        listItem = new ArrayList<>();
        displayAdapter = new DisplayAdapter(this);
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
            Long durationData =  cursor.getLong(columnDuration);
            Instant instant = Instant.ofEpochMilli(durationData);
            ZonedDateTime zdt = ZonedDateTime.ofInstant ( instant , ZoneOffset.UTC );
            DateTimeFormatter formatter;
            String durationTime = "";
            if(durationData >= 3600000)
            {
                formatter = DateTimeFormatter.ofPattern ( "HH:mm:ss" );
                durationTime = formatter.format(zdt);
            }
            else if(durationData > 0)
            {
                formatter = DateTimeFormatter.ofPattern("mm:ss");
                durationTime = formatter.format(zdt);
            }
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            System.out.println("Data");
            System.out.println("Duration " + durationTime);
            System.out.println("Size " + cursor.getString(columnSize));
            System.out.println("Type " + cursor.getString(columnMediaType));
            path.add(absolutePathOfImage);
            type.add(cursor.getInt(columnMediaType));
            listItem.add(new Item(absolutePathOfImage, durationTime));
        }
        cursor.close();
    }
    void open_with_photos(int position){
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", new File(path.get(position)));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.setDataAndType(photoURI, "image/*");
        startActivity(intent);
    }
    void openwithThis(int position){
        Intent intent = new Intent(this, Image.class);
        intent.putExtra("path", path.get(position));
        intent.putExtra("secure", false);
        startActivityForResult(intent, VIEW_REQUEST);
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
            intent.putExtra("secure", false);
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
        else if(requestCode==VIEW_REQUEST){
            recreate();
        }
    }
    public void ChangeDisplay(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(Archive.this);
        builder.setTitle(R.string.display);
        builder.setNegativeButton(R.string.cancel,null);
        builder.setAdapter(displayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                recyclerView.setLayoutManager(new GridLayoutManager(Archive.this, position + 1));
                recyclerView.setAdapter(listAdapter);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}