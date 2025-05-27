package com.example.crabfood.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String slug;
    private boolean isGlobal;
    private int displayOrder;
    private boolean isActive;

    @SerializedName("foods")
    private List<FoodResponse> foods;       // Danh sách món ăn thuộc category

    // Constructors
    public CategoryResponse() {}

    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryResponse(Long id, String name, String description, String imageUrl, String slug,
                            boolean isGlobal, int displayOrder, boolean isActive,
                            List<FoodResponse> foods) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.slug = slug;
        this.isGlobal = isGlobal;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.foods = foods;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<FoodResponse> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodResponse> foods) {
        this.foods = foods;
    }
}

