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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<ArrayList<Item>> listPhotoGroup;
    ArrayList<String> listDate;
    DisplayAdapter displayAdapter;
    Configuration config;
    Boolean isSecure;
    ListAdapter listAdapter;
    GroupPhotoAdapter groupPhotoAdapter;
    int VIEW_REQUEST = 555;
    String album;
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
        isSecure = getIntent().getBooleanExtra("secure",false);
        TextView headerTitle = (TextView) findViewById(R.id.header_title);
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
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            listPhotoGroup.add(listItem);
            listDate.add(getString(R.string.empty));
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }
        else if(album.equals("Favourite")){
            getFavouritePhoto();
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            listPhotoGroup.add(listItem);
            listDate.add(getString(R.string.all_time));
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }else
        {
            getAllImages();
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }
    }
    public void init() {
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

//    void getCreationTime(String path){
//        BasicFileAttributes attributes = null;
//        try
//        {
//            attributes =
//                    Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
//        }
//        catch (IOException e)67u;.
//        {
//            e.printStackTrace();
//        }
//        long milliseconds = attributes.creationTime().to(TimeUnit.MILLISECONDS);
//        if((milliseconds > Long.MIN_VALUE) && (milliseconds < Long.MAX_VALUE))
//        {
//            Date creationDate =
//                    new Date(attributes.creationTime().to(TimeUnit.MILLISECONDS));
//
//            System.out.println("File favourite " + attributes.creationTime() + " " +
//                    creationDate.getDate() + "/" +
//                    (creationDate.getMonth() + 1) + "/" +
//                    (creationDate.getYear() + 1900));
//        }
//    }
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
                MediaStore.Files.FileColumns.DATE_TAKEN + " DESC"
        );
        Cursor cursor = cursorLoader.loadInBackground();
        int columnMediaType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION);
        int columnSize = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
        int columnDate = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN);
        int curDate = -1, curMonth = -1, curYear = -1;
        ArrayList<Item> listPhotoSameDate = new ArrayList<>();
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            File temp = new File(absolutePathOfImage);
            if(!temp.exists()){
                continue;
            }
            if(!absolutePathOfImage.contains(album))
                continue;

//            Calendar date = Calendar.getInstance();
//            date.setTimeInMillis(cursor.getLong(columnDate)*1000);
////            Date date = new Date(cursor.getLong(columnDate));
////            System.out.println("Date " + date. + " " + date.getDate() + " " + date.getMonth() + " " + date.getYear());
//            System.out.println("Date " + date.MONTH + " " + date.YEAR);
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
            long timestampLong = cursor.getLong(columnDate);
            Date d = new Date(timestampLong);
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
                    listDate.add(date + "/" + month + "/" + year);
                }
                else{
                    listPhotoSameDate.add(new Item(absolutePathOfImage, durationTime, typeData));
                }
            }
        }
        listPhotoGroup.add(listPhotoSameDate);
        cursor.close();
        for(int i = 0; i < listDate.size(); i++){
            System.out.println("Group Date " + listDate.get(i));
            for(Item x : listPhotoGroup.get(i)){
                System.out.println(x.getPath());
            }
        }
        System.out.println("Date length " + listDate.size() + " " + listPhotoGroup.size());
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
        startActivity(intent);
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
                groupPhotoAdapter.column = position + 1;
                groupPhotoAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}