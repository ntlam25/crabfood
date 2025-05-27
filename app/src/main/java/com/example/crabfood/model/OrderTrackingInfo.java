package com.example.crabfood.model;

import com.example.crabfood.model.enums.OrderTrackingStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class OrderTrackingInfo implements Serializable {
    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("riderId")
    private Long riderId;

    @SerializedName("riderName")
    private String riderName;

    @SerializedName("riderRating")
    private Double riderRating;

    @SerializedName("riderImageUrl")
    private String riderImageUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("sourceLatitude")
    private Double sourceLatitude;

    @SerializedName("sourceLongitude")
    private Double sourceLongitude;

    @SerializedName("destinationLatitude")
    private Double destinationLatitude;

    @SerializedName("destinationLongitude")
    private Double destinationLongitude;

    @SerializedName("currentLatitude")
    private Double currentLatitude;

    @SerializedName("currentLongitude")
    private Double currentLongitude;

    @SerializedName("estimatedDeliveryTime")
    private String estimatedDeliveryTime;

    @SerializedName("restaurantName")
    private String restaurantName;

    // Getters and setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public Double getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(Double riderRating) {
        this.riderRating = riderRating;
    }

    public String getRiderImageUrl() {
        return riderImageUrl;
    }

    public void setRiderImageUrl(String riderImageUrl) {
        this.riderImageUrl = riderImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(Double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}