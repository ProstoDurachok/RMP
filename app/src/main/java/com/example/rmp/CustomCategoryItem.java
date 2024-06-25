package com.example.rmp;

public class CustomCategoryItem {
    private String name;
    private String imageUrl;
    private String amount;
    private String categoryName;
    private String uuid;

    public CustomCategoryItem() {
        // Default constructor required for Firebase
    }
    public CustomCategoryItem(String name, String imageUrl, String amount, String uuid) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.amount = amount;
        this.uuid = uuid;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
