package com.example.image_management;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    DisplayAdapter displayAdapter;
    Configuration config;
    Boolean isSecure;
    ListAdapter listAdapter;
    int VIEW_REQUEST = 555;
    String album;
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
        isSecure = getIntent().getBooleanExtra("secure",false);
        TextView headerTitle = (TextView) findViewById(R.id.header_title);
        if(isSecure)
            headerTitle.setText("Secure");
        else if(album.equals(""))
            headerTitle.setText(R.string.archive);
        else
            headerTitle.setText(album);
        config = new Configuration(getApplicationContext());
        config.getConfig();
        if(isSecure){
            getSecureFolder();
        }
        else if(album.equals("Favourite")){
            getFavouritePhoto();
        }else
        {
            getAllImages();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listAdapter = new ListAdapter(listItem, this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(listAdapter);
    }
    public void init() {
        listItem = new ArrayList<>();
        displayAdapter = new DisplayAdapter(this);
        album = getIntent().getStringExtra("album");
        if(album == null)
            album = "";
    }
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public void getFavouritePhoto(){
        String filename = "like.txt";
        FileInputStream fis = null;
        String favouritePath;
        try {
            fis = getApplicationContext().openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            while((favouritePath = reader.readLine()) != null){
                if(isImageFile(favouritePath)){
                    listItem.add(new Item(favouritePath,"",1));// IMAGE
                }
                else{
                    long duration = 0;
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getApplicationContext(), Uri.fromFile(new File(favouritePath)));
                    if(retriever != null){
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        duration = Long.parseLong(time);
                        retriever.release();
                    }
                    Instant instant = Instant.ofEpochMilli(duration);
                    DateTimeFormatter formatter;
                    String durationTime = "";
                    ZonedDateTime zdt = ZonedDateTime.ofInstant ( instant , ZoneOffset.UTC );
                    if(duration >= 3600000)
                    {
                        formatter = DateTimeFormatter.ofPattern ( "HH:mm:ss" );
                        durationTime = formatter.format(zdt);
                    }
                    else if(duration > 0)
                    {
                        formatter = DateTimeFormatter.ofPattern("mm:ss");
                        durationTime = formatter.format(zdt);
                    }
                    listItem.add(new Item(favouritePath,durationTime,3));// VIDEO
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getSecureFolder(){
        String securePath = getApplicationInfo().dataDir + "/files/Secure";
        File storageDir = new File(securePath);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        for (File media : storageDir.listFiles()){
            System.out.println(media.getAbsolutePath());
            if(isImageFile(media.getAbsolutePath())){
                listItem.add(new Item(media.getAbsolutePath(),"",1));// IMAGE
            }
            else{
                long duration = 0;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.fromFile(new File(media.getAbsolutePath())));
                if(retriever != null){
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    duration = Long.parseLong(time);
                    retriever.release();
                }
                Instant instant = Instant.ofEpochMilli(duration);
                DateTimeFormatter formatter;
                String durationTime = "";
                ZonedDateTime zdt = ZonedDateTime.ofInstant ( instant , ZoneOffset.UTC );
                if(duration >= 3600000)
                {
                    formatter = DateTimeFormatter.ofPattern ( "HH:mm:ss" );
                    durationTime = formatter.format(zdt);
                }
                else if(duration > 0)
                {
                    formatter = DateTimeFormatter.ofPattern("mm:ss");
                    durationTime = formatter.format(zdt);
                }
                listItem.add(new Item(media.getAbsolutePath(),durationTime,3));// VIDEO
            }
        }
    }
    public void getAllImages() {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        System.out.println(queryUri.getPath());
        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
        Cursor cursor = cursorLoader.loadInBackground();
        int columnMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION);
        int columnSize = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            File temp = new File(absolutePathOfImage);
            if(!temp.exists()){
                continue;
            }
            if(!absolutePathOfImage.contains(album))
                continue;
            Long durationData = cursor.getLong(columnDuration);
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
            System.out.println("Data");
            System.out.println("Duration " + durationTime);
            System.out.println("Size " + cursor.getString(columnSize));
            System.out.println("Type " + cursor.getString(columnMediaType));
            int typeData = cursor.getInt(columnMediaType);
            listItem.add(new Item(absolutePathOfImage, durationTime, typeData));
        }
        cursor.close();
    }

    void open_with_photos(int position, int type){
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", new File(listItem.get(position).getPath()));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if(type == 1)
            intent.setDataAndType(photoURI, "image/*");
        else
            intent.setDataAndType(photoURI, "video/*");
        startActivity(intent);
    }
    void openwithThis(int position, int type){
        Intent intent;
        if(type == 1)
            intent = new Intent(this, Image.class);
        else
            intent = new Intent(this, Video.class);
        intent.putExtra("path", listItem.get(position).getPath());
        intent.putExtra("secure", isSecure);
        startActivityForResult(intent, VIEW_REQUEST);
    }
    @Override
    public void onClick(int position) {
        if(listItem.get(position).getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        {
            if(config.isDefault==1){
                openwithThis(position, 1);
            }
            else {
                open_with_photos(position, 1);
            }
        }
        else if(listItem.get(position).getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        {
            if(config.isDefault==1){
                openwithThis(position, 3);
            }
            else {
                open_with_photos(position, 3);
            }
        }
    }
    public void back(View v){
//        if(isSecure)
//        {
//            startActivity(new Intent(this, SecureFolder.class));
//        }
//        else{
            this.finish();
//        }
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