package com.example.rmp;

public class CategoryItem {
    private String name;
    private String imageUrl;
    private String id;
    private String amount;
    public CategoryItem() {}

    public CategoryItem(String name, String imageUrl, String id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
        this.amount = "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

}


