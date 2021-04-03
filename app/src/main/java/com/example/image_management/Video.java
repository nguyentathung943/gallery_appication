package com.example.image_management;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Video extends AppCompatActivity {
    String path;
    VideoView video;
    ImageView playBtn;
    MediaPlayer mediaPlayer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        path = getIntent().getStringExtra("path");
        video = (VideoView) findViewById(R.id.video);
        playBtn = (ImageView) findViewById(R.id.play_btn);
        Uri uri = Uri.parse(path);
        video.setVideoURI(uri);
    }

    public void click(View v){
        if(!video.isPlaying()){
            playBtn.setVisibility(View.GONE);
            video.start();
        }
        else{
            playBtn.setVisibility(View.VISIBLE);
            video.pause();
        }

    }
}
