package com.example.image_management;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

public class SlideShow extends AppCompatActivity {
    SliderView sliderView;
    ImageView back;
    SliderAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_show);
        Bundle bundle = getIntent().getExtras();
        String jsonString = bundle.getString("listSlide");
        Gson gson = new Gson();
        Type listSlide = new TypeToken<List<String>>() {}.getType();
        ArrayList<String> list = gson.fromJson(jsonString,listSlide);
        ArrayList<SlideShowItem> items = new ArrayList<>();
        for(String path : list){
            items.add(new SlideShowItem(Drawable.createFromPath(path), new File(path).getName()));
        }
        System.out.println("Slideshow size:"  + list.size());
        sliderView = findViewById(R.id.slide_show);
        back = findViewById(R.id.btn_back_slide_show);
        slideAdapter = new SliderAdapter(items);

        sliderView.setSliderAdapter(slideAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }
    public void backSlideShow(View view){
        finish();
    }
}