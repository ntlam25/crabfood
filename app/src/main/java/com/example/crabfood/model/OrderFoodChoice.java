package com.example.crabfood.model;

import java.io.Serializable;

// Inner class for order food choices
public class OrderFoodChoice implements Serializable {
    private Long id;
    private Long orderFoodId;
    private Long optionId;
    private String optionName;
    private Long choiceId;
    private String choiceName;
    private double priceAdjustment;

    // Constructors
    public OrderFoodChoice() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderFoodId() {
        return orderFoodId;
    }

    public void setOrderFoodId(Long orderFoodId) {
        this.orderFoodId = orderFoodId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public double getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(double priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

}
