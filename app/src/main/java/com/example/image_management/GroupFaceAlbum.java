package com.example.image_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetector;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroupFaceAlbum extends AppCompatActivity implements FaceAdapter.ClickFaceListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<ArrayList<Item>> listPhotoGroup;
    ArrayList<String> listDate;
    ArrayList<String> slideShowItems;
    DisplayAdapter displayAdapter;
    Configuration config;
    GroupPhotoAdapter groupPhotoAdapter;
    Task<List<Face>> detect;
    TextView headerTitle;
    ArrayList<Boolean> listDetectFace;
    ArrayList<String> listFacePath;
    ArrayList<Boolean> listFaceDetect;
    Integer lengthCheckFace;
    ArrayList<FaceDetection> listFaceDetection;
    ArrayList<GroupFaceDetection> listGroupFaceDetection;
    FaceAdapter faceAdapter;
    protected Interpreter tflite;
    private  int imageSizeX;
    private  int imageSizeY;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    public static Bitmap cropped;



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
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_face);
        headerTitle = (TextView) findViewById(R.id.list_faces);
        headerTitle.setText(getString(R.string.list_faces));
        init();
        config = new Configuration(getApplicationContext());
        alertDialog.show();
        getFaceRecognition();
    }

    public void renderUI(){
        recyclerView = findViewById(R.id.group_face_recyclerView);
        recyclerView.setHasFixedSize(true);
        faceAdapter = new FaceAdapter(listGroupFaceDetection, this, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(faceAdapter);
    }

    public void init() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading);
        alertDialog = builder.create();
        slideShowItems = new ArrayList<>();
        listItem = new ArrayList<>();
        displayAdapter = new DisplayAdapter(this);
        listPhotoGroup = new ArrayList<>();
        listDate = new ArrayList<>();
        listDetectFace = new ArrayList<>();
        listFaceDetect = new ArrayList<>();
        lengthCheckFace = 0;
        listFaceDetection = new ArrayList<>();
        listGroupFaceDetection = new ArrayList<>();
        album = getIntent().getStringExtra("album");
        if(album == null)
            album = "";
        try{
            tflite=new Interpreter(loadmodelfile(this));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFaceRecognition() {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
        Cursor cursor = cursorLoader.loadInBackground();
        listFacePath = new ArrayList<>();
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            File temp = new File(absolutePathOfImage);
            if(!temp.exists()){
                continue;
            }
            listFacePath.add(absolutePathOfImage);
        }
        cursor.close();
        if(listFacePath.size() == 0){
            TextView faceStatus = (TextView) findViewById(R.id.face_status);
            faceStatus.setText(getString(R.string.empty));
            faceStatus.setVisibility(View.VISIBLE);
        }
        else{
            for(String i : listFacePath){
                faceDetector(i);
            }
            Tasks.whenAll(detect).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    if(listFaceDetection.size() == 0){
                        TextView faceStatus = (TextView) findViewById(R.id.face_status);
                        faceStatus.setText(getString(R.string.empty));
                        faceStatus.setVisibility(View.VISIBLE);
                    }
                    else{
                        FaceDetection firstImg = listFaceDetection.get(0);
                        if(listFaceDetection.size() > 0){
                            listGroupFaceDetection.add(new GroupFaceDetection(firstImg.getFace(), firstImg.getEmbadding(), firstImg.getPath()));
                            for(int i = 1; i < listFaceDetection.size(); i++){
                                int j = 0;
                                for(; j < listGroupFaceDetection.size(); j++)
                                {
                                    double cal = calculate_distance(listFaceDetection.get(i).getEmbadding(), listGroupFaceDetection.get(j).getEmbadding());

                                    if(cal < 6.0 && !listGroupFaceDetection.get(j).getListImage().get(0).equals(listFaceDetection.get(i).getPath()))
                                    {
                                        listGroupFaceDetection.get(j).AddListImage(listFaceDetection.get(i).getPath());
                                        break;
                                    }
                                }
                                if(j == listGroupFaceDetection.size())
                                {
                                    listGroupFaceDetection.add(new GroupFaceDetection(listFaceDetection.get(i).getFace(), listFaceDetection.get(i).getEmbadding(), listFaceDetection.get(i).getPath()));

                                }
                            }
                        }
                        renderUI();
                    }
                    alertDialog.dismiss();
                }
            });
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
    public void SlideShowOngo(View v){
        Intent slideShow = new Intent(this, SlideShow.class);
        Gson gson = new Gson();
        String listSlide = gson.toJson(slideShowItems);
        slideShow.putExtra("listSlide",listSlide);
        startActivity(slideShow);
    }
    public void ChangeDisplay(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupFaceAlbum.this);
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

    private double calculate_distance(float[][] ori_embedding, float[][] test_embedding) {
        double sum =0.0;
        float x = 0;
        float y = 0;
        for(int i=0;i<128;i++){
            x += ori_embedding[0][i];
            y += test_embedding[0][i];
            sum=sum+Math.pow((ori_embedding[0][i]-test_embedding[0][i]),2.0);
        }
        return Math.sqrt(sum);
    }

    private TensorImage loadImage(final Bitmap bitmap, TensorImage inputImageBuffer ) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("Qfacenet.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    public void faceDetector(String path){
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", new File(path));
        ArrayList<Bitmap> listCrop = new ArrayList<>();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
            FaceDetector faceDetector = com.google.mlkit.vision.face.FaceDetection.getClient();
            detect = faceDetector.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            for (Face face : faces) {
                                Rect bounds = face.getBoundingBox();
                                try {
                                    cropped = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height());
                                    float[][] embaddingData = get_embaddings(cropped);
                                    listCrop.add(cropped);
                                    listFaceDetection.add(new FaceDetection(embaddingData, path, cropped));
                                }catch (Exception e){                            
                                    System.out.println("Path error: " + path);
                                    e.printStackTrace();

                                }
                            }
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float[][] get_embaddings(Bitmap bitmap){
        TensorImage inputImageBuffer;
        float[][] embedding = new float[1][128];

        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];

        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);

        inputImageBuffer = loadImage(bitmap,inputImageBuffer);

        tflite.run(inputImageBuffer.getBuffer(),embedding);
        return embedding;
    }
    public static AlertDialog alertDialog;
    @Override
    public void onClick(GroupFaceDetection groupFaceDetection) {
        Intent intent = new Intent(this, Archive.class);
        intent.putExtra("secure",false);
        intent.putExtra("album", getString(R.string.face_recognition));
        intent.putExtra("face_path", groupFaceDetection.getListImage());
        startActivity(intent);

    }

}
