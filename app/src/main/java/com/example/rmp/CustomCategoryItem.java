package com.example.rmp;

public class CustomCategoryItem {
    private String name;
    private String imageUrl;
    private String amount;
    private String uuid;
    public CustomCategoryItem() {}
    public CustomCategoryItem(String name, String imageUrl, String amount, String uuid) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.amount = amount;
        this.uuid = uuid;
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
    public String getAmount() {
        return amount;
    }
    public String getUuid() {
        return uuid;
    }
}
