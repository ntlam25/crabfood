package com.example.crabfood.model;

import java.util.List;

public class ReviewResponse {
    private Long id;
    private Long orderId;
    private String orderCode;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long vendorId;
    private String vendorName;
    private Long riderId;
    private String riderName;
    private Double foodRating;
    private String foodComment;
    private Double deliveryRating;
    private String deliveryComment;
    private List<String> images;
    private String createdAt;
    private List<FoodReviewResponse> foodReviews;

    public ReviewResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public Double getFoodRating() { return foodRating; }
    public void setFoodRating(Double foodRating) { this.foodRating = foodRating; }

    public String getFoodComment() { return foodComment; }
    public void setFoodComment(String foodComment) { this.foodComment = foodComment; }

    public Double getDeliveryRating() { return deliveryRating; }
    public void setDeliveryRating(Double deliveryRating) { this.deliveryRating = deliveryRating; }

    public String getDeliveryComment() { return deliveryComment; }
    public void setDeliveryComment(String deliveryComment) { this.deliveryComment = deliveryComment; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<FoodReviewResponse> getFoodReviews() { return foodReviews; }
    public void setFoodReviews(List<FoodReviewResponse> foodReviews) { this.foodReviews = foodReviews; }
}