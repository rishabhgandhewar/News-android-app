package com.example.newsapp.ui;

import com.google.gson.annotations.Expose;

public class News {

    @Expose
    private String title;

    @Expose
    private String id;

    @Expose
    private String time;

    @Expose
    private String category;

    @Expose
    private String image;

    @Expose
    private String url;

    @Expose
    private String date;

    public News(String title, String id, String time, String category, String image, String url, String date) {
        this.title = title;
        this.id = id;
        this.image = image;
        this.time = time;
        this.category = category;
        this.url = url;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

}