package com.example.rmp;

import android.net.Uri;

public class FinanceItem {
    private String name;
    private String amount;
    private String imageUrl;

    public FinanceItem(String name, String amount, String imageUrl) {
        this.name = name;
        this.amount = amount;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
