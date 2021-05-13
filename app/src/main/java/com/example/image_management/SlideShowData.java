package com.example.image_management;

import java.util.ArrayList;

public class SlideShowData {
    public static ArrayList<ImageData> list;
    public static void setList(ArrayList<ImageData> data){
        list = new ArrayList<>(data);
    }
    public static void clearList(){
        list.clear();
    }
}