package com.example.image_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Archive extends AppCompatActivity implements ListAdapter.ClickImageListener{
    RecyclerView recyclerView;
    ArrayList<Item> listItem;
    ArrayList<ArrayList<Item>> listPhotoGroup;
    ArrayList<String> listDate;
    DisplayAdapter displayAdapter;
    Configuration config;
    Boolean isSecure;
    ListAdapter listAdapter;
    ArrayList<float[][]> listOribitmap;
    ArrayList<float[][]> listTestbitmap;
    GroupPhotoAdapter groupPhotoAdapter;
    Task<List<Face>> detect;
    Boolean isSameFace;
    TextView headerTitle;
    ArrayList<Boolean> listDetectFace;
    ArrayList<String> listFacePath;
    Task<Void>  checkSameFace, isSameFunc;
    ArrayList<Boolean> listFaceDetect;
    Integer lengthCheckFace;
    ArrayList<FaceDetection> listFaceDetection;
    ArrayList<GroupFaceDetection> listGroupFaceDetection;
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
        }
        else if(album.equals("Face Recognition")){
            System.out.println("Face face");
            getFaceRecognition();
            recyclerView = findViewById(R.id.group_photo_recyclerView);
            recyclerView.setHasFixedSize(true);
            groupPhotoAdapter = new GroupPhotoAdapter(this, listPhotoGroup, listDate);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupPhotoAdapter);
        }
        else
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

    public void getFaceRecognition() {
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
        listFacePath = new ArrayList<>();
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            System.out.println("Image Path " + absolutePathOfImage);
            File temp = new File(absolutePathOfImage);
            if(!temp.exists()){
                continue;
            }
            listFacePath.add(absolutePathOfImage);
        }
        cursor.close();
        for(String i : listFacePath){
            faceDetector(i);
        }
        Tasks.whenAll(detect).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Total face whenall" + listFaceDetection.size());
                FaceDetection firstImg = listFaceDetection.get(0);
                if(listFaceDetection.size() > 0){
                    listGroupFaceDetection.add(new GroupFaceDetection(firstImg.getFace(), firstImg.getEmbadding(), firstImg.getPath()));
                    for(int i = 1; i < listFaceDetection.size(); i++){
                        int j = 0;
                        for(; j < listGroupFaceDetection.size(); j++)
                        {
                            double cal = calculate_distance(listFaceDetection.get(i).getEmbadding(), listGroupFaceDetection.get(j).getEmbadding());
                            System.out.println("cal " + cal);

                            if(cal < 6.0 && !listGroupFaceDetection.get(j).getListImage().get(0).equals(listFaceDetection.get(i).getPath()))
                            {
                                System.out.println("add new");
                                listGroupFaceDetection.get(j).AddListImage(listFaceDetection.get(i).getPath());
                                break;
                            }
                        }
                        if(j == listGroupFaceDetection.size())
                        {
                            System.out.println("create new");
                            listGroupFaceDetection.add(new GroupFaceDetection(listFaceDetection.get(i).getFace(), listFaceDetection.get(i).getEmbadding(), listFaceDetection.get(i).getPath()));

                        }
                    }
                }
                System.out.println("SizeGroup " + listGroupFaceDetection.size());
                for(GroupFaceDetection i : listGroupFaceDetection){
                    System.out.println("----");
//                    for(String path : i.getListImage())
                    System.out.println("GroupFaceDetection " + i.getListImage().size());
                }

            }
        });
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
        listFacePath = new ArrayList<>(); ///
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            System.out.println("Image Path " + absolutePathOfImage);
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
        if(listFacePath.size() > 0)
            lengthCheckFace = listFacePath.size() * (listFacePath.size() - 1) / 2;
        listPhotoGroup.add(listPhotoSameDate);
        cursor.close();
        for(int i = 0; i < listDate.size(); i++){
            System.out.println("Group Date " + listDate.get(i));
            for(Item x : listPhotoGroup.get(i)){
                System.out.println(x.getPath());
            }
        }
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

    //////////////////// Face recognition
    protected Interpreter tflite;
    private  int imageSizeX;
    private  int imageSizeY;
    boolean isHaveFace;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    double distance;
    public Bitmap oribitmap,testbitmap;
    public static Bitmap cropped;

    private double calculate_distance(float[][] ori_embedding, float[][] test_embedding) {
        double sum =0.0;
        float x = 0;
        float y = 0;
        for(int i=0;i<128;i++){
            x += ori_embedding[0][i];
            y += test_embedding[0][i];
            sum=sum+Math.pow((ori_embedding[0][i]-test_embedding[0][i]),2.0);
        }
        System.out.println("Float " + x + " - " + y);
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
                                cropped = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height());
                                float[][] embaddingData = get_embaddings(cropped);
                                listCrop.add(cropped);
                                listFaceDetection.add(new FaceDetection(embaddingData, path, cropped));
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

}