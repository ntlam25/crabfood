package com.example.crabfood.model;

import java.util.List;

public class CategoryRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String slug;
    private boolean isGlobal;
    private int displayOrder;
    private boolean isActive;
    private Long vendorId;         // Gửi ID vendor
    private List<Long> foodIds;    // Gửi danh sách ID món ăn

    // Constructors
    public CategoryRequest() {}

    public CategoryRequest(String name, String description, String imageUrl, String slug,
                           boolean isGlobal, int displayOrder, boolean isActive,
                           Long vendorId, List<Long> foodIds) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.slug = slug;
        this.isGlobal = isGlobal;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.vendorId = vendorId;
        this.foodIds = foodIds;
    }
    // Getters and Setters

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

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public List<Long> getFoodIds() {
        return foodIds;
    }

    public void setFoodIds(List<Long> foodIds) {
        this.foodIds = foodIds;
    }
}
