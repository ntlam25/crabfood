package com.example.crabfood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFood implements Serializable {
    private Long id;
    private Long orderId;
    private Long foodId;
    private String foodName;
    private int quantity;
    private double foodPrice;
    private String foodImageUrl;
    private Date createdAt;
    private List<OrderFoodChoice> choices = new ArrayList<>();

    // Constructors
    public OrderFood() {
        this.quantity = 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderFoodChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<OrderFoodChoice> choices) {
        this.choices = choices;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    // Helper methods
    public double getTotalPrice() {
        double choicesPrice = 0;
        for (OrderFoodChoice choice : choices) {
            choicesPrice += choice.getPriceAdjustment();
        }
        return (foodPrice + choicesPrice) * quantity;
    }

    public String getFormattedChoices() {
        if (choices == null || choices.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++) {
            OrderFoodChoice choice = choices.get(i);
            sb.append(choice.getChoiceName());

            if (choice.getPriceAdjustment() > 0) {
                sb.append(String.format(" (+$%.2f)", choice.getPriceAdjustment()));
            }

            if (i < choices.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
