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
    private String specialInstructions;
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
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
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
    
    // Inner class for order food choices
    public static class OrderFoodChoice implements Serializable {
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
}
