package com.example.crabfood.model;


import com.example.crabfood.model.enums.OrderPaymentMethod;
import com.example.crabfood.model.enums.OrderPaymentStatus;
import com.example.crabfood.model.enums.OrderStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long vendorId;
    private Long riderId;
    private Long deliveryAddressId;
    private String deliveryAddressText;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryNotes;
    private double subtotal;
    private double deliveryFee;
    private double discountAmount;
    private double totalAmount;
    private OrderPaymentMethod paymentMethod;
    private OrderPaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private String cancellationReason;
    private Long couponId;
    private Date estimatedDeliveryTime;
    private Date actualDeliveryTime;
    private Date createdAt;
    private List<OrderFood> orderFoods = new ArrayList<>();
    
    // Constructors
    public Order() {
        this.discountAmount = 0.0;
        this.paymentStatus = OrderPaymentStatus.PENDING;
        this.orderStatus = OrderStatus.PENDING;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getVendorId() {
        return vendorId;
    }
    
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
    
    public Long getRiderId() {
        return riderId;
    }
    
    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }
    
    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }
    
    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
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
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(OrderPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public OrderPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(OrderPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public Long getCouponId() {
        return couponId;
    }
    
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
    
    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }
    
    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }
    
    public Date getActualDeliveryTime() {
        return actualDeliveryTime;
    }
    
    public void setActualDeliveryTime(Date actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<OrderFood> getOrderFoods() {
        return orderFoods;
    }
    
    public void setOrderFoods(List<OrderFood> orderFoods) {
        this.orderFoods = orderFoods;
    }
}
