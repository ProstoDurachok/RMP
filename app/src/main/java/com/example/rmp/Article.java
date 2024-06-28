package com.example.rmp;

public class Article {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String content;
    private boolean favorite;
    public Article() {}
    public Article(String id, String title, String description, String imageUrl, String content, boolean favorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.content = content;
        this.favorite = favorite;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFavorite() {
        return favorite;
    }

}
