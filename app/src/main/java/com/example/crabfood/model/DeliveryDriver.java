package com.example.crabfood.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class DeliveryDriver {
    private String driverId;
    private String name;
    private String imageUrl;
    private double rating;
    private String phoneNumber;
    private LatLng currentLocation;
    private String vehicleType;
    private String vehicleNumber;

    // Constructors
    public DeliveryDriver() {
    }

    public DeliveryDriver(String driverId, String name, double rating, String imageUrl) {
        this.driverId = driverId;
        this.name = name;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
