package com.example.crabfood.model;

import java.io.Serializable;

public class AddressResponse implements Serializable {
    private Long id;
    private String label;
    private String fullAddress;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private String recipientName;
    private String recipientPhone;
    
    // Constructors
    public AddressResponse() {
        this.isDefault = false;
    }
    
    // Builder constructor
    public AddressResponse(Long id, String fullAddress, String addressLine, String city, String district, String ward,
                           Double latitude, Double longitude, Boolean isDefault,
                           String recipientName, String recipientPhone) {
        this.id = id;
        this.fullAddress = fullAddress;
        this.addressLine = addressLine;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault != null ? isDefault : false;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAddressLine() {
        return addressLine;
    }
    
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getWard() {
        return ward;
    }
    
    public void setWard(String ward) {
        this.ward = ward;
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
    
    public Boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
    public String getFullAddress() {
        return fullAddress;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
