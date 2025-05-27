package com.example.crabfood.model;

import com.example.crabfood.model.enums.OrderTrackingStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LocationUpdateRequest {
    @SerializedName("riderId")
    private Long riderId;

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("bearing")
    private Double bearing; // direction in degrees

    @SerializedName("speed")
    private Double speed; // in m/s

    // Constructor
    public LocationUpdateRequest(Long riderId, Long orderId, Double latitude, Double longitude, Double bearing, Double speed) {
        this.riderId = riderId;
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.speed = speed;
    }

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
}