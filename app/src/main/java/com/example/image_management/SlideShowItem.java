package com.example.image_management;

import android.graphics.drawable.Drawable;

public class SlideShowItem {
    Drawable draw;
    String name;
    SlideShowItem(Drawable draw, String name){
        this.draw = draw;
        this.name = name;
    }
}
