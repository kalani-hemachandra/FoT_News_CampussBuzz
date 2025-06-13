package com.fotnews.campusbuzz.model;

public class News {
    private String title;
    private String content;
    private String date;
    private String description;
    private String imageUrl;
    private String newsType;

    public News() {
        // Empty constructor required for Firestore
    }

    public News(String title, String content, String date, String description, String imageUrl, String newsType) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
        this.newsType = newsType;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNewsType() {
        return newsType;
    }

    // Setters (required for Firestore)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }
}