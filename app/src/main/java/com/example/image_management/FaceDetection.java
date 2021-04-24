package com.example.image_management;

import android.graphics.Bitmap;

public class FaceDetection {
    private float[][] embadding;
    private Bitmap face;
    private String path;

    public FaceDetection(float[][] embadding, String path, Bitmap face) {
        this.embadding = embadding;
        this.path = path;
        this.face = face;
    }

    public float[][] getEmbadding() {
        return embadding;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getFace() {return face;}
}
