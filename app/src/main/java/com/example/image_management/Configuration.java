package com.example.image_management;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Configuration {
    Context context;
    int isDarkMode;
    String language;
    int isDefault;
    Configuration(Context a) {
        context = a;
    }
    public int ThemeMode() {
        return isDarkMode;
    }
    public String languageState() {
        return language;
    }
    public int DefaultMode(){return isDefault;}
    protected boolean getConfig(){
        String filename = "config.txt";
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            isDarkMode = Integer.parseInt(line);
            language = reader.readLine();
            line = reader.readLine();
            isDefault = Integer.parseInt(line);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    protected void saveConfig(int Theme, String Lan,int Default) {
        try {
            FileOutputStream fout = context.openFileOutput("config.txt", Context.MODE_PRIVATE);
            fout.write((String.valueOf(Theme) + '\n').getBytes());
            fout.write((Lan + "\n").getBytes());
            fout.write((String.valueOf(Default) + '\n').getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
