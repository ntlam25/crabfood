package com.example.crabfood.model;

import java.util.List;
import java.util.Objects;

public class FoodResponse {
    private Long id;
    private Long vendorId;
    private String name;
    private String description;
    private double price;
    private List<String> categories;
    private String imageUrl;
    private int preparationTime;
    private List<FoodOptionResponse> options;
    private boolean available;
    private boolean featured;
    private double rating;

    private boolean isFavorite;

    public FoodResponse() {
    }

    public FoodResponse(Long id, Long vendorId, String name, String description,
                        double price, List<String> categories, String imageUrl,
                        int preparationTime, List<FoodOptionResponse> options, boolean available) {
        this.id = id;
        this.vendorId = vendorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categories = categories;
        this.imageUrl = imageUrl;
        this.preparationTime = preparationTime;
        this.options = options;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public List<FoodOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<FoodOptionResponse> options) {
        this.options = options;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodResponse that = (FoodResponse) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (preparationTime != that.preparationTime) return false;
        if (available != that.available) return false;
        if (featured != that.featured) return false;
        if (Double.compare(that.rating, rating) != 0) return false;
        if (isFavorite != that.isFavorite) return false;
        if (!id.equals(that.id)) return false;
        if (!vendorId.equals(that.vendorId)) return false;
        if (!name.equals(that.name)) return false;
        if (!description.equals(that.description)) return false;
        if (!Objects.equals(categories, that.categories)) return false;
        if (!Objects.equals(imageUrl, that.imageUrl)) return false;
        return Objects.equals(options, that.options);
    }
}