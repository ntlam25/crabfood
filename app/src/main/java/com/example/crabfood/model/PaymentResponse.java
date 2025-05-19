package com.example.crabfood.model;

public class PaymentResponse {
    private long orderId;
    private String message;
    private String status;
    private String paymentUrl;

    public PaymentResponse() {
    }

    public PaymentResponse(long orderId, String message, String status, String paymentUrl) {
        this.orderId = orderId;
        this.message = message;
        this.status = status;
        this.paymentUrl = paymentUrl;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}

