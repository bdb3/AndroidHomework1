package com.example.android.newsapp.models;

/**
 * Created by Danny on 6/29/2017.
 */

public class NewsItem {
    private String title;
    private String description;
    private String url;
    private String date;
    //added imgUrl to load thumbnail image, as well as getters and setters
    private String imgUrl;

    public NewsItem(String title, String description, String url, String date, String imgUrl){
        this.title = title;
        this.description = description;
        this.url = url;
        this.date = date;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
