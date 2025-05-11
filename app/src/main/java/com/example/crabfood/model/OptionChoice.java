package com.example.crabfood.model;

import java.io.Serializable;

public class OptionChoice implements Serializable {
    private Long id;
    private String name;
    private double priceAdjustment;
    private boolean isDefault;
    private Long optionId;
    
    // Constructors
    public OptionChoice() {
        this.priceAdjustment = 0.0;
        this.isDefault = false;
    }
    
    // Getters and Setters
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
    
    public double getPriceAdjustment() {
        return priceAdjustment;
    }
    
    public void setPriceAdjustment(double priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    
    public Long getOptionId() {
        return optionId;
    }
    
    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
    
    // Helper methods
    public String getFormattedPriceAdjustment() {
        if (priceAdjustment == 0) {
            return "";
        } else if (priceAdjustment > 0) {
            return String.format("+$%.2f", priceAdjustment);
        } else {
            return String.format("-$%.2f", Math.abs(priceAdjustment));
        }
    }
}
