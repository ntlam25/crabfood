package com.example.crabfood.model;


import java.util.List;

public class OrderRequest {
    private String orderNumber;
    private Long vendorId;
    private Long customerId;
    private Long deliveryAddressId;
    private String deliveryAddressText;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryNotes;
    private Double subtotal;
    private Double deliveryFee;
    private Double discountAmount;
    private Double totalAmount;
    private PaymentMethod paymentMethod;
    private String orderStatus;
    private String couponCode;
    private List<CartItemEntity> items;

    // Enum for payment methods
    public enum PaymentMethod {
        CASH("CASH"),
        VNPAY("VNPAY");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Nested class for order items
    public static class OrderItemRequest {
        private Long id;
        private Long foodId;
        private String foodName;
        private String imageUrl;
        private Double price;
        private Integer quantity;
        private Long vendorId;
        private List<OptionChoiceResponse> selectedOptions;

        // Constructors, getters, and setters
        public OrderItemRequest() {}

        // Add getters and setters
        public Long getFoodId() { return foodId; }
        public void setFoodId(Long foodId) { this.foodId = foodId; }
        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Constructors
    public OrderRequest() {}

    // Getters and Setters
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Long getDeliveryAddressId() { return deliveryAddressId; }
    public void setDeliveryAddressId(Long deliveryAddressId) { this.deliveryAddressId = deliveryAddressId; }
    public List<CartItemEntity> getItems() { return items; }
    public void setItems(List<CartItemEntity> items) { this.items = items; }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryAddressText() {
        return deliveryAddressText;
    }

    public void setDeliveryAddressText(String deliveryAddressText) {
        this.deliveryAddressText = deliveryAddressText;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}