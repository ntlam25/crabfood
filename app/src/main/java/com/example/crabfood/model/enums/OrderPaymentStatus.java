package com.example.crabfood.model.enums;

public enum OrderPaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    FAILED("Failed"),
    REFUNDED("Refunded");
    
    private final String displayName;
    
    OrderPaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OrderPaymentStatus fromString(String statusName) {
        for (OrderPaymentStatus status : OrderPaymentStatus.values()) {
            if (status.name().equalsIgnoreCase(statusName)) {
                return status;
            }
        }
        return PENDING; // Default value
    }
}
