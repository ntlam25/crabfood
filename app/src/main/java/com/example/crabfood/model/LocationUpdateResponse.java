package com.example.crabfood.model;

import com.example.crabfood.model.enums.OrderStatus;

/**
 * Model class for handling location update responses from the server
 */
public class LocationUpdateResponse {
    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private Double riderLatitude;
    private Double riderLongitude;
    private String riderName;
    private String riderPhone;
    private String estimatedDeliveryTime;
    private String currentLocation;
    private String nextLocation;

    public LocationUpdateResponse() {
    }

    public LocationUpdateResponse(Long orderId, String orderNumber, OrderStatus status,
                                Double riderLatitude, Double riderLongitude, String riderName,
                                String riderPhone, String estimatedDeliveryTime,
                                String currentLocation, String nextLocation) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.status = status;
        this.riderLatitude = riderLatitude;
        this.riderLongitude = riderLongitude;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.currentLocation = currentLocation;
        this.nextLocation = nextLocation;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getRiderLatitude() {
        return riderLatitude;
    }

    public void setRiderLatitude(Double riderLatitude) {
        this.riderLatitude = riderLatitude;
    }

    public Double getRiderLongitude() {
        return riderLongitude;
    }

    public void setRiderLongitude(Double riderLongitude) {
        this.riderLongitude = riderLongitude;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(String nextLocation) {
        this.nextLocation = nextLocation;
    }
}