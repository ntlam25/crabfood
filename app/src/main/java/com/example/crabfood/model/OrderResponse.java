package com.example.crabfood.model;

import androidx.annotation.Nullable;

import com.example.crabfood.model.enums.OrderPaymentStatus;
import com.example.crabfood.model.enums.OrderStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderResponse implements Serializable {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private VendorResponse vendorResponse;
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

    private OrderStatus orderStatus;

    private OrderPaymentStatus paymentStatus;

    private String paymentMethod;
    private String cancellationReason;
    private Long couponId;
    private String estimatedDeliveryTime;
    private String actualDeliveryTime;
    private String customerName;
    @SerializedName("items")
    private List<OrderFood> orderFoods = new ArrayList<>();

    // Constructors
    public OrderResponse() {
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

    public VendorResponse getVendor() {
        return vendorResponse;
    }

    public void setVendorId(VendorResponse vendorResponse) {
        this.vendorResponse = vendorResponse;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
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

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(String actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public List<OrderFood> getOrderFoods() {
        return orderFoods;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderResponse that = (OrderResponse) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(orderNumber, that.orderNumber) &&
                orderStatus == that.orderStatus &&
                paymentStatus == that.paymentStatus &&
                Objects.equals(estimatedDeliveryTime, that.estimatedDeliveryTime) &&
                Objects.equals(actualDeliveryTime, that.actualDeliveryTime) &&
                Double.compare(that.totalAmount, totalAmount) == 0 &&
                Objects.equals(orderFoods, that.orderFoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, orderStatus, paymentStatus,
                estimatedDeliveryTime, actualDeliveryTime, totalAmount, orderFoods);
    }

    public void setOrderFoods(List<OrderFood> orderFoods) {
        this.orderFoods = orderFoods;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
