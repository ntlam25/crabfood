package com.example.crabfood.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Model class for handling location update responses from the server
 */
public class LocationUpdateResponse {
    @SerializedName("riderId")
    private Long riderId;

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("bearing")
    private Double bearing;

    @SerializedName("speed")
    private Double speed;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("estimatedArrivalTime")
    private String estimatedArrivalTime;

    @SerializedName("estimatedMinutes")
    private Integer estimatedMinutes;

    @SerializedName("status")
    private String status;

    public LocationUpdateResponse(Long riderId, Long orderId, Double latitude,
                                  Double longitude, Double bearing, Double speed,
                                  String estimatedArrivalTime,
                                  Integer estimatedMinutes, String status) {
        this.riderId = riderId;
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.speed = speed;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.estimatedMinutes = estimatedMinutes;
        this.status = status;
    }

    // Getters and Setters
    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(String estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}