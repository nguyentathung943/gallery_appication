package com.example.image_management;

import java.time.format.DateTimeFormatter;

public class Item {
    private String path;
    private String time;

    public String getTime() {
        return time;
    }

    public Item(String path, String time) {
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }
}
