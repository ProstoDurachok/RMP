package com.example.rmp;

public class CategoryItem {
    private String name;
    private String imageUrl;
    private String id;
    private String amount; // Add this field for amount

    public CategoryItem() {
        // Default constructor required for Firebase
    }

    public CategoryItem(String name, String imageUrl, String id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
        this.amount = "0";
    }


    // Getter and Setter methods for all fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public void setAmount(String amount) {
        this.amount = amount;
    }
}


