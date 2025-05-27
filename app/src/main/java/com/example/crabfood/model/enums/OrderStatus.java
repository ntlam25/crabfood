package com.example.crabfood.model.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    ACCEPTED("Đã xác nhận"),
    PICKED_UP("Nguời giao đã lấy hàng"),
    ON_THE_WAY("Đang giao"),
    SUCCESS("Đã giao"),
    CANCELLED("Đã huỷ");

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
