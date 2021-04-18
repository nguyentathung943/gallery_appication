package com.example.image_management;

import java.time.format.DateTimeFormatter;

public class Item {
    private String path;
    private String time;
    private Integer type;
    public Item(String path, String time, Integer type) {
        this.path = path;
        this.time = time;
        this.type = type;
    }
    public String getTime() {
        return time;
    }
    public String getPath() {
        return path;
    }
    public Integer getType() {
        return type;
    }
}
