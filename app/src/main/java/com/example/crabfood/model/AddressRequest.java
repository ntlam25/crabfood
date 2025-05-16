package com.example.crabfood.model;

public class AddressRequest {
    private String addressName;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private String recipientName;
    private String recipientPhone;

    public AddressRequest() {
    }

    public AddressRequest(String fullAddress, String addressName, String district,
                          String ward, Double latitude, Double longitude,
                          Boolean isDefault, String recipientName,
                          String recipientPhone) {
        this.fullAddress = fullAddress;
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String city) {
        this.fullAddress = city;
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

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
}