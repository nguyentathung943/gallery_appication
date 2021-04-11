package com.example.image_management;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class Video extends AppCompatActivity {
    String path;
    VideoView video;
    ImageView playBtn, videoImage;
    MediaPlayer mediaPlayer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
}
