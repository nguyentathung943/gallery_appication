package com.example.image_management;

import android.graphics.drawable.Drawable;

public class SlideShowItem {
    Drawable draw;
    String name;
    String order;
    SlideShowItem(Drawable draw, String name, String order){
        this.draw = draw;
        this.name = name;
        this.order = order;
    }
}
