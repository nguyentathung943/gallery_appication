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
    ImageView playBtn, videoImage, likeIcon;
    MediaPlayer mediaPlayer;
    LikeImage likeImage;
    boolean isLiked;
    ImageView back;
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

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(view->{
            this.finish();
        });

        likeIcon = (ImageView) findViewById(R.id.like_video);
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
//                likeImage.addLikeImage(path);
            }
            else
            {
                likeIcon.setImageResource(R.drawable.non_liked_icon);
//                likeImage.removeLikeImage(path);
            }
        });
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
