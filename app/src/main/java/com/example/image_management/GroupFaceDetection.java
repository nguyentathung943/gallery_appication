package com.example.image_management;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class GroupFaceDetection {
    private Bitmap face;
    private ArrayList<String> listImage;
    private float[][] embadding;
    public GroupFaceDetection(Bitmap face, float[][] embadding, String path) {
        this.face = face;
        this.embadding = embadding;
        this.listImage = new ArrayList<>();
        this.listImage.add(path);
    }

    public Bitmap getFace() {
        return face;
    }

    public ArrayList<String> getListImage() {
        return listImage;
    }

    public float[][] getEmbadding(){
        return embadding;
    }

    public void AddListImage(String path){
        listImage.add(path);
    }
}
