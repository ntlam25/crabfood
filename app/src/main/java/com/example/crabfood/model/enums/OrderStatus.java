package com.example.crabfood.model.enums;

public enum OrderStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    READY_FOR_PICKUP("Ready for Pickup"),
    ON_THE_WAY("On the Way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OrderStatus fromString(String statusName) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(statusName)) {
                return status;
            }
        }
        return PENDING; // Default value
    }
}
