package com.example.image_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.acl.Permission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.serializer._3.IMGLYFileWriter;

public class Video extends Activity {
    String path;
    VideoView video;
    ImageView playBtn, videoImage, likeIcon;
    MediaPlayer mediaPlayer;
    LikeImage likeImage;
    Boolean isLiked;
    ImageView back;
    Boolean isSecure;
    ImageView info;
    public void shareOn(){
        File file = new File(path);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(shareIntent, "Share File"),1011);
    }
    public void  callScanItent(Context context,String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,null);
    }
    private void deleteOn(){
        File a = new File(path);
        a.delete();
        callScanItent(getApplicationContext(),path);
        Toast.makeText(this,R.string.delete_video,Toast.LENGTH_SHORT).show();
        finish();
    }


    private void DeleteFile(String path){
        File a = new File(path);
        a.delete();
        callScanItent(getApplicationContext(),path);
        finish();
    }
    @Override
    public void onBackPressed(){
        finish();
    }
    public void videoViewMenu(View view){
        switch (view.getId()){
            case R.id.btn_share:
                shareOn();
                break;
            case R.id.btn_delete:
                deleteOn();
                break;
        }
    }
    private void RemoveFromSecure(String oldPath, String suffix) throws  IOException{
        File oldFile = new File(oldPath);
        String newPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
        File storageDir = new File(newPath);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        int dot = oldFile.getName().lastIndexOf(".");
        String fileName = oldFile.getName().substring(0,dot);
        String source = newPath + "/" + fileName+suffix;
        copyFile(new File(this.path),new File(source));
        callScanItent(getApplicationContext(),source);
        DeleteFile(this.path);
    }
    public void copyFile(File source, File destination) throws IOException {
        FileUtils.copy(new FileInputStream(source), new FileOutputStream(destination));
    }
    private void MoveFileToSecure(String oldPath,String suffix) throws IOException {
        likeImage = ((LikeImage)getApplicationContext());
        if(likeImage.checkLiked(oldPath)){
            likeImage.removeLikeImage(oldPath);
            likeImage.saveData();
        }
        File oldFile = new File(oldPath);
        String newPath = getApplicationInfo().dataDir + "/files/Secure";
        File storageDir = new File(newPath);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        int dot = oldFile.getName().lastIndexOf(".");
        String fileName = oldFile.getName().substring(0,dot);
        String source = newPath + "/"+ fileName+suffix;
        copyFile(new File(oldPath),new File(source));
        DeleteFile(oldPath);
    }
    private String getExtension(String path){
        File a = new File(path);
        int dot = a.getName().lastIndexOf(".");
        return a.getName().substring(dot);
    }
    public void ImageInfo(String path){
        File a = new File(path);
        TextView location = new TextView(this);
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(a.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ExifInterface exif = new ExifInterface(a);
            float[] latLong = new float[2];
            boolean hasLatLong = exif.getLatLong(latLong);
            if (hasLatLong) {
                location.setText(R.string.latitude + ": " + latLong[0] +"\n" + R.string.longitude + ": "+latLong[1]);
            }
            else{
                location.setText(R.string.location + ": " + R.string.none);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("creationTime: " + attr.creationTime());
        System.out.println("lastAccessTime: " + attr.lastAccessTime());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(this);
        TextView date = new TextView(this);
        TextView size = new TextView(this);
        Button secureFolder = new Button(this);
        LinearLayout btnLay = new LinearLayout(this);
        btnLay.setOrientation(LinearLayout.VERTICAL);
        btnLay.setGravity(Gravity.CENTER_HORIZONTAL);
        btnLay.addView(secureFolder);
        name.setText(R.string.name + ": " + a.getName());
        name.setTextSize(20);
        date.setText(R.string.create_date + ": " + attr.creationTime());
        date.setTextSize(20);
        size.setText(R.string.size + ": " + attr.size() + " bytes");
        size.setTextSize(20);
        location.setTextSize(20);
        layout.addView(name);
        layout.addView(date);
        layout.addView(size);
        layout.addView(location);
        layout.addView(btnLay);
        builder.setView(layout);
        builder.setNegativeButton(R.string.ok,null);
        if(isSecure){
            secureFolder.setText(R.string.remove_secure);
        }
        else{

            secureFolder.setText(R.string.move_secure);
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        secureFolder.setOnClickListener(view -> {
            try {
                String exten = getExtension(path);
                if (isSecure){
                    RemoveFromSecure(path,exten);
                }
                else {
                    MoveFileToSecure(path, exten);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        path = getIntent().getStringExtra("path");
        video = (VideoView) findViewById(R.id.video);
        playBtn = (ImageView) findViewById(R.id.play_btn);
        videoImage = (ImageView) findViewById(R.id.video_image);
        Glide.with(this)
                .load(path)
                .centerCrop()
                .into(videoImage);
        Uri uri = Uri.parse(path);
        video.setVideoURI(uri);
        likeIcon = (ImageView) findViewById(R.id.like_video);
        info = findViewById(R.id.info);
        path = getIntent().getStringExtra("path");
        isSecure = getIntent().getBooleanExtra("secure", false);
        info.setOnClickListener(view->{
            ImageInfo(path);
        });
        back = findViewById(R.id.btn_back_video_view);
        back.setOnClickListener(view->{
            finish();
        });
        if(!isSecure){
            likeImage = ((LikeImage)getApplicationContext());
            if(likeImage.listImage.contains(path))
            {
                likeIcon.setImageResource(R.drawable.liked_icon);
                isLiked = true;
            }
            else
            {
                likeIcon.setImageResource(R.drawable.non_liked_icon);
                isLiked = false;
            }
            likeIcon.setOnClickListener(view->{
                isLiked = !isLiked;
                if(isLiked)
                {
                    likeIcon.setImageResource(R.drawable.liked_icon);
                    likeImage.addLikeImage(path);
                }
                else
                {
                    likeIcon.setImageResource(R.drawable.non_liked_icon);
                    likeImage.removeLikeImage(path);
                }
            });
        }
        else{
            likeIcon.setVisibility(View.INVISIBLE);
        }

    }
        public void click(View v){
        if(!video.isPlaying()){
            playBtn.setVisibility(View.GONE);
            videoImage.setVisibility(View.GONE);
            video.start();
        }
        else{
            playBtn.setVisibility(View.VISIBLE);
            videoImage.setVisibility(View.VISIBLE);
            video.pause();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK){
            recreate();
        }
    }
}