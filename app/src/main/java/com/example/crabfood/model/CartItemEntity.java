package com.example.crabfood.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.crabfood.helpers.OptionChoiceConverters;

import java.util.List;
import java.util.Map;

@Entity(tableName = "cart_items")
@TypeConverters(OptionChoiceConverters.class)
public class CartItemEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Long foodId;
    private String foodName;
    private String imageUrl;
    private double price;
    private int quantity;
    private Long vendorId;
    private Map<Long, List<OptionChoiceResponse>> selectedOptions;
    private boolean isSynced;
    private long lastUpdated;

    public CartItemEntity() {
    }

    public CartItemEntity(Long foodId, String foodName, String imageUrl, double price,
                          int quantity, Long vendorId,
                          Map<Long, List<OptionChoiceResponse>> selectedOptions) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.vendorId = vendorId;
        this.selectedOptions = selectedOptions;
        this.isSynced = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Map<Long, List<OptionChoiceResponse>> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<Long, List<OptionChoiceResponse>> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Calculate total price including options
    public double getTotalPrice() {
        double basePrice = price;

        // Add price adjustments from options
        if (selectedOptions != null) {
            for (Map.Entry<Long, List<OptionChoiceResponse>> entry : selectedOptions.entrySet()) {
                for (OptionChoiceResponse choice : entry.getValue()) {
                    basePrice += choice.getPriceAdjustment();
                }
            }
        }

        return basePrice * quantity;
    }
}
