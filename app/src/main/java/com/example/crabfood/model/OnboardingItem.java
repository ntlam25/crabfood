package com.example.crabfood.model;

public class OnboardingItem {
    private int image;
    private int icon;
    private String title;
    private String description;

    public OnboardingItem(int image, int icon, String title, String description) {
        this.image = image;
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}