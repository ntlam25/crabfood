package com.example.crabfood.model.enums;

public enum OrderPaymentMethod {
    CASH_ON_DELIVERY("Cash on Delivery"),
    CREDIT_CARD("Credit Card"),
    VNPAY("VNPay"),
    MOMO("MoMo"),
    ZALOPAY("ZaloPay");
    
    private final String displayName;
    
    OrderPaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OrderPaymentMethod fromString(String methodName) {
        for (OrderPaymentMethod method : OrderPaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return CASH_ON_DELIVERY; // Default value
    }
}
