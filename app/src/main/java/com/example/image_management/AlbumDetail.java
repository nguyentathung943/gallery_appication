package com.example.image_management;

import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetail extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    DisplayAdapter displayAdapter;
    Configuration config;
    ListAdapter listAdapter;
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
        for(Item i : listItem)
            System.out.println("Item data " + i.getPath());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listAdapter = new ListAdapter(listItem,  this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(listAdapter);
    }
    public void init() {
        listItem = new ArrayList<>();
        displayAdapter = new DisplayAdapter(this);
    }
    public void getAllImages() {
        String path = getIntent().getStringExtra("path");
        int position = getIntent().getIntExtra("position", 0);
        if(position != 0){
            File dir = new File(Environment.getExternalStorageDirectory().toString()+"/DCIM/" + path);
            File[] listAlbumPath = dir.listFiles();

            for(File i : listAlbumPath){
                String mimeType = URLConnection.guessContentTypeFromName(i.toString());
                if(mimeType != null && mimeType.startsWith("image"))
                    listItem.add(new Item(i.toString(), ""));
                else{
//                    long durationData = MediaPlayer.create(this, Uri.fromFile(i)).getDuration();
//                    System.out.println("VIDEO " + i.getPath());
//                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                    retriever.setDataSource(i.getPath());
//                    long durationData = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

//                    retriever.release();
                    MediaPlayer mp = MediaPlayer.create(this, Uri.parse(i.getPath()));
                    long durationData = mp.getDuration();
//                    System.out.println("Duration " + durationData);
//                    mp.release();
//                    long durationData = 0;
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
                    listItem.add(new Item(i.toString(), durationTime));
                }
            }
            for(Item i : listItem){
                System.out.println("ITEM " + i.getPath() + " " + i.getTime());
            }
        }
    }
    void open_with_photos(int position){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(
                Uri.parse(listItem.get(position).getPath()),"image/*");
        startActivity(intent);
    }
    void openwithThis(int position){
        Intent intent = new Intent(this, Image.class);
        intent.putExtra("path", listItem.get(position).getPath());
        startActivityForResult(intent, 1);
    }
    @Override
    public void onClick(int position) {
//        if(type.get(position) == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
//        {
//            System.out.println("Image show: " + config.isDefault);
//            if(config.isDefault==1){
//                openwithThis(position);
//            }
//            else {
//                open_with_photos(position);
//            }
//        }
//        else if(type.get(position) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
//        {
//            Intent intent = new Intent(this, Video.class);
//            intent.putExtra("path", path.get(position));
//            startActivity(intent);
//        }
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
    public void ChangeDisplay(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(AlbumDetail.this);
        builder.setTitle(R.string.display);
        builder.setNegativeButton(R.string.cancel,null);
        builder.setAdapter(displayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                recyclerView.setLayoutManager(new GridLayoutManager(AlbumDetail.this, position + 1));
                recyclerView.setAdapter(listAdapter);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}