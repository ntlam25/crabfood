package com.example.crabfood.model;

public class AddressRequest {
    private String label;

    private String fullAddress;

    private double latitude;

    private double longitude;
    private boolean isDefault = false;
    public AddressRequest() {
        isDefault = false;
    }

    public AddressRequest(String label, String fullAddress,
                          double latitude, double longitude,
                        boolean isDefault) {
        this.label = label;
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}