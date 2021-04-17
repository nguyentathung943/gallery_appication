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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class Image extends Activity {
    String path;
    ImageView back;
    ImageView myImage;
    ImageView info;
    public void shareOn(){
        File file = new File(path);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(shareIntent, "Share File"),1011);
//        startActivity(shareIntent);
    }
    public void callScanItent(Context context,String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,null);
    }
    private void deleteOn(){
        File a = new File(path);
        a.delete();
        callScanItent(getApplicationContext(),path);
        Toast.makeText(this,"Image deleted",Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressed(){
        finish();
    }
    public void imageViewMenu(View view){
        switch (view.getId()){
            case R.id.btn_share:
                shareOn();
                break;
            case R.id.btn_delete:
                deleteOn();
                break;
        }
    }
    public void ImageInfo(String path){
        File a = new File(path);
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(a.toPath(), BasicFileAttributes.class);
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

        Button walllpp = new Button(this);
        Button cpy = new Button(this);
        Button secureFolder = new Button(this);
        LinearLayout btnLay = new LinearLayout(this);
        btnLay.setOrientation(LinearLayout.VERTICAL);
        btnLay.setGravity(Gravity.CENTER_HORIZONTAL);
        btnLay.addView(walllpp);
        btnLay.addView(cpy);
        btnLay.addView(secureFolder);
        name.setText("Name: " + a.getName());
        name.setTextSize(20);
        date.setText("Creation date: " + attr.creationTime());
        date.setTextSize(20);
        size.setText("Size: " + attr.size() + " bytes");
        size.setTextSize(20);
        layout.addView(name);
        layout.addView(date);
        layout.addView(size);
        layout.addView(btnLay);
        builder.setView(layout);
        builder.setNegativeButton("OK",null);
        walllpp.setText("Set image as wallpaper");
        cpy.setText("Copy to clipboard");
        secureFolder.setText("Move to Secure Folder");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        walllpp.setOnClickListener(view ->{
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.parse(path),"image/*");
            intent.putExtra("mimeType", "image/*");
            startActivity(Intent.createChooser(intent, "Set as:"));
        });
        cpy.setOnClickListener(view ->{
            Uri uri = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    new File(path));
            ClipboardManager mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newUri(getApplicationContext().getContentResolver(), "a Photo", uri);
            mClipboard.setPrimaryClip(clip);
            Toast.makeText(this,"Image copied to clipboard",Toast.LENGTH_SHORT).show();
        });
        secureFolder.setOnClickListener(view -> {
            File old_file = new File(path);
            String old = new File(path).getName();
            String newFilename = old.replaceAll("\\.jpg|.png|.jpeg$", ".txt");
            File f1 = new File(new File(path).getParentFile(),newFilename);
            old_file.renameTo(f1);
            callScanItent(getApplicationContext(),path);
            Toast.makeText(this,"Image moved to Secure Folder",Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view);
        info = findViewById(R.id.info);
        path = getIntent().getStringExtra("path");
        System.out.println("Path " + path);
        System.out.println(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        myImage = findViewById(R.id.img_show);
        myImage.setImageBitmap(myBitmap);
        info.setOnClickListener(view->{
            ImageInfo(path);
        });
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(view->{
            this.finish();
        });

    }

    public static int PESDK_RESULT = 1;

    private SettingsList createPesdkSettingsList() {
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }

    public void openEditor(View v) {
        Uri inputImage = Uri.fromFile(new File(path));
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToUri(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new EditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT);

        setResult(RESULT_OK, null);
        finish();
    }
//    public void openEditor(View v){
//        Intent intent = new Intent(this, ImageEditor.class);
//        intent.putExtra("uri", path);
//        startActivityForResult(intent, 1);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK){
            recreate();
        }
    }
}