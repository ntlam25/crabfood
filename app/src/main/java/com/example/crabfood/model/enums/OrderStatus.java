package com.example.crabfood.model.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    ACCEPTED("Đã xác nhận"),
    PREPARING("Đang chuẩn bị"),
    READY("Đơn sẵn sàng"),
    PICKED_UP("Đã nhận đơn"),
    ON_THE_WAY("Đang giao"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã huỷ"),
    REJECTED("Đã từ chối");

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
