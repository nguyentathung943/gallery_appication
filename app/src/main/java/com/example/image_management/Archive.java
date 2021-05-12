package com.example.image_management;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<ArrayList<Item>> listPhotoGroup;
    ArrayList<String> listDate;
    ArrayList<String> slideShowItems;
    DisplayAdapter displayAdapter;
    Configuration config;
    Boolean isSecure;
    GroupPhotoAdapter groupPhotoAdapter;
    TextView headerTitle;
    int VIEW_REQUEST = 555;
    String album;
    public static AlertDialog alertDialog;
    String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.SIZE
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading);
        alertDialog = builder.create();
        isSecure = getIntent().getBooleanExtra("secure",false);
        headerTitle = (TextView) findViewById(R.id.header_title);
        if(isSecure)
            headerTitle.setText(R.string.secure_folder);
        else if(album.equals(""))
            headerTitle.setText(R.string.archive);
        else
            headerTitle.setText(album);
        config = new Configuration(getApplicationContext());
        config.getConfig();
        if(isSecure){
            getSecureFolder();
        }
        else if(album.equals(getString(R.string.favourite))){
            getFavouritePhoto();
        }
        else if(album.equals(getString(R.string.face_recognition))){
            getFaceRecognition();
        }
        else
        {
            try {
                getAllImages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void init() {
        SlideShowData.list = new ArrayList<>();
        slideShowItems = new ArrayList<>();
        listItem = new ArrayList<>();
        displayAdapter = new DisplayAdapter(this);
        listPhotoGroup = new ArrayList<>();
        listDate = new ArrayList<>();
        album = getIntent().getStringExtra("album");
        if(album == null)
            album = "";
    }
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public void getFaceRecognition(){
        ArrayList<String> listFace = (ArrayList<String>) getIntent().getSerializableExtra("face_path");
        for(String facePath: listFace)
        {
            if(isImageFile(facePath)){
                listItem.add(new Item(facePath,"",1));// IMAGE
                slideShowItems.add(facePath);
            }
            else{
                long duration = 0;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.fromFile(new File(facePath)));
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
                listItem.add(new Item(facePath,durationTime,3));// VIDEO
            }
        }
        recyclerView = findViewById(R.id.group_photo_recyclerView);
        recyclerView.setHasFixedSize(true);
        listPhotoGroup.add(listItem);
        if(listFace.isEmpty()){
            listDate.add(getString(R.string.empty));
        }
        else{
            listDate.add(getString(R.string.list_face));
        }
        groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(groupPhotoAdapter);
    }

    public void getFavouritePhoto(){
        String filename = "like.txt";
        FileInputStream fis = null;
        String favouritePath;
        try {
            fis = this.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            while((favouritePath = reader.readLine()) != null){
                File temp = new File(favouritePath);
                if(!temp.exists()){
                    continue;
                }
                if(isImageFile(favouritePath)){
                    listItem.add(new Item(favouritePath,"",1));// IMAGE
                    slideShowItems.add(favouritePath);
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
        recyclerView = findViewById(R.id.group_photo_recyclerView);
        recyclerView.setHasFixedSize(true);
        listPhotoGroup.add(listItem);
        if(listItem.isEmpty()){
            listDate.add(getString(R.string.empty));
        }
        else{
            listDate.add(getString(R.string.all_favor));
        }
        groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(groupPhotoAdapter);
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
                slideShowItems.add(media.getAbsolutePath());
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
        recyclerView = findViewById(R.id.group_photo_recyclerView);
        recyclerView.setHasFixedSize(true);
        listPhotoGroup.add(listItem);
        if(listItem.isEmpty()){
            listDate.add(getString(R.string.empty));
        }
        else{
            listDate.add(getString(R.string.all_secure));
        }
        groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(groupPhotoAdapter);
    }

    public String getDate(long val){
        val*=1000L;
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(val));
    }
    public void getAllImages() throws IOException {
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
        int curDate = -1, curMonth = -1, curYear = -1;
        ArrayList<Item> listPhotoSameDate = new ArrayList<>();
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            int typeData = cursor.getInt(columnMediaType);
            System.out.println("Image Path " + absolutePathOfImage);
            File temp = new File(absolutePathOfImage);
            if(!temp.exists()){
                continue;
            }
            if(!absolutePathOfImage.contains(album))
                continue;
            if(typeData ==1 ){
                slideShowItems.add(absolutePathOfImage);
            }
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

            Date d = new Date(new File(absolutePathOfImage).lastModified());
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int date = c.get(Calendar.DATE);
            System.out.println("Date " + year +"-"+month+"-"+date);
            if(curYear == -1)
            {
                listDate.add(date + "/" + month + "/" + year);
                curDate = date;
                curMonth = month;
                curYear = year;
                listPhotoSameDate.add(new Item(absolutePathOfImage, durationTime, typeData));
            }
            else{
                if(curDate != date || curMonth != month || curYear != year){
                    listPhotoGroup.add(listPhotoSameDate);
                    listPhotoSameDate = new ArrayList<>();
                    listPhotoSameDate.add(new Item(absolutePathOfImage, durationTime, typeData));
                    curDate = date;
                    curMonth = month;
                    curYear = year;
                    System.out.println("Date " + year +"-"+month+"-"+date);
                    listDate.add(date + "/" + month + "/" + year);
                }
                else{
                    listPhotoSameDate.add(new Item(absolutePathOfImage, durationTime, typeData));
                }
            }
        }
        cursor.close();
        if(listPhotoSameDate.size() == 0){
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            listPhotoGroup.add(listItem);
            listDate.add(getString(R.string.empty));
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }
        else{
            listPhotoGroup.add(listPhotoSameDate);
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }
        System.out.println("Total photo = " + slideShowItems.size());
    }
    void open_with_photos(Item item){
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", new File(item.getPath()));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if(item.getType() == 1)
            intent.setDataAndType(photoURI, "image/*");
        else
            intent.setDataAndType(photoURI, "video/*");
        startActivityForResult(intent, VIEW_REQUEST);
    }
    void openwithThis(Item item){
        Intent intent;
        if(item.getType() == 1)
            intent = new Intent(this, Image.class);
        else
            intent = new Intent(this, Video.class);
        intent.putExtra("path", item.getPath());
        intent.putExtra("secure", isSecure);
        startActivityForResult(intent, VIEW_REQUEST);
    }
    @Override
    public void onClick(Item item) {
        if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        {
            if(config.isDefault==1){
                openwithThis(item);
            }
            else {
                open_with_photos(item);
            }
        }
        else if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        {
            if(config.isDefault==1){
                openwithThis(item);
            }
            else {
                open_with_photos(item);
            }
        }
    }
    public void back(View v){
        if(!SlideShowData.list.isEmpty())
        {
            SlideShowData.clearList();
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!SlideShowData.list.isEmpty())
        {
            SlideShowData.clearList();
        }
        this.finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            recreate();
        }
        else if(requestCode == VIEW_REQUEST){
            recreate();
        }
        else if(requestCode==444){
            alertDialog.dismiss();
        }
    }
    public void SlideShowOngo(View v){
        if(slideShowItems.size()==0){
            Toast.makeText(this,getString(R.string.list_empty),Toast.LENGTH_SHORT).show();
        }
        else{
            alertDialog.show();
            Intent slideShow = new Intent(this, SlideShow.class);
            SlideShowData.setList(slideShowItems);
            startActivityForResult(slideShow,444);
        }
    }

    public void ChangeDisplay(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(Archive.this);
        builder.setTitle(R.string.display);
        builder.setNegativeButton(R.string.cancel,null);
        builder.setAdapter(displayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                groupPhotoAdapter.column = position + 1;
                groupPhotoAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}