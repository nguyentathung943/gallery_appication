package com.example.image_management;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LikeImage{
    public static List<String> listImage = new ArrayList<>();
    static Context context;
    public static boolean checkLiked(String path){
        if (listImage.isEmpty()){
            return false;
        }
        return listImage.contains(path);
    }
    public static void init(Context contextData){
        listImage = new ArrayList<>();
        context = contextData;
        String filename = "like.txt";
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String cur;
            while((cur = reader.readLine()) != null){
                listImage.add(cur);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveData(){
        try {
            FileOutputStream fout = context.openFileOutput("like.txt", Context.MODE_PRIVATE);
            for(String cur : listImage){
                fout.write((cur + "\n").getBytes());
            }
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addLikeImage(String imagePath){
        listImage.add(imagePath);
        saveData();
    }
    public static void removeLikeImage(String imagePath){
        int index = listImage.indexOf(imagePath);
        listImage.remove(index);
        saveData();
    }
}