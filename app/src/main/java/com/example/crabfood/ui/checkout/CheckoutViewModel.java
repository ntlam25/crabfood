package com.example.crabfood.ui.checkout;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.OrderRequest;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.repository.CartRepository;
import com.example.crabfood.repository.OrderRepository;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CheckoutViewModel extends AndroidViewModel {
    private static final String TAG = "CheckoutViewModel";
    private final SessionManager sessionManager;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    // LiveData for order state
    private final MutableLiveData<OrderState> orderState = new MutableLiveData<>(OrderState.IDLE);
    private final MutableLiveData<String> orderError = new MutableLiveData<>();
    private final MutableLiveData<OrderResponse> placedOrder = new MutableLiveData<>();

    // Payment method selection
    private final MutableLiveData<String> selectedPaymentMethod = new MutableLiveData<>("CASH");

    // Coupon code
    private final MutableLiveData<String> couponCode = new MutableLiveData<>("");

    // Track total amounts
    private final MutableLiveData<Double> deliveryFee = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> discountAmount = new MutableLiveData<>(0.0);

    public enum OrderState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        cartRepository = CartRepository.getInstance(application);
        orderRepository = OrderRepository.getInstance(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<OrderState> getOrderState() {
        return orderState;
    }

    public LiveData<String> getOrderError() {
        return orderError;
    }

    public LiveData<OrderResponse> getPlacedOrder() {
        return placedOrder;
    }

    public LiveData<String> getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(String method) {
        selectedPaymentMethod.setValue(method);
    }

    public LiveData<String> getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String code) {
        couponCode.setValue(code);
    }

    public LiveData<Double> getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double fee) {
        deliveryFee.setValue(fee);
    }

    public LiveData<Double> getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double amount) {
        discountAmount.setValue(amount);
    }

    public double calculateDeliveryFee(double distanceInKm, double baseDeliveryFee, double radius, boolean hasFreeShipVoucher) {
        double extraPerKm = 2000; // Phí thêm cho mỗi km vượt quá radius

        double calculatedFee = baseDeliveryFee;
        if (distanceInKm > radius) {
            calculatedFee += (distanceInKm - radius) * extraPerKm;
        }

        if (hasFreeShipVoucher) {
            calculatedFee = 0;
        }

        deliveryFee.setValue(calculatedFee);
        return calculatedFee;
    }

    public void placeOrder(AddressResponse address, Double subtotal, Double total, List<CartItemEntity> cartItems) {
        // Validate order parameters
        if (address == null) {
            orderError.setValue("Vui lòng chọn địa chỉ giao hàng");
            orderState.setValue(OrderState.ERROR);
            return;
        }

        // Set loading state
        orderState.setValue(OrderState.LOADING);

        // Get items from cart
        Log.d(TAG, "placeOrder: cartItems size: " + cartItems.size());
        if (cartItems == null || cartItems.isEmpty()) {
            orderError.setValue("Giỏ hàng trống");
            orderState.setValue(OrderState.ERROR);
            return;
        }

        // Get the vendor ID from the first cart item
        Long vendorId = cartItems.get(0).getVendorId();

        // Create order request
        OrderRequest orderRequest = createOrderRequest(
                vendorId,
                sessionManager.getUserId(),
                address,
                subtotal,
                deliveryFee.getValue(),
                discountAmount.getValue(),
                total,
                selectedPaymentMethod.getValue(),
                couponCode.getValue(),
                cartItems
        );

        // Send order request
        orderRepository.placeOrder(orderRequest, new OrderRepository.OrderCallback() {
            @Override
            public void onOrderSuccess(OrderResponse response) {
                placedOrder.postValue(response);
                orderState.postValue(OrderState.SUCCESS);

                // Clear cart after successful order
                cartRepository.clearCart();
            }

            @Override
            public void onOrderFailure(String errorMessage) {
                orderError.postValue(errorMessage);
                orderState.postValue(OrderState.ERROR);
            }
        });
    }

    private OrderRequest createOrderRequest(Long vendorId, Long customerId, AddressResponse address,
                                            Double subtotal, Double deliveryFee, Double discountAmount,
                                            Double totalAmount, String paymentMethod, String couponCode,
                                            List<CartItemEntity> items) {
        OrderRequest request = new OrderRequest();


        request.setVendorId(vendorId);
        request.setCustomerId(customerId);

        // Address details
        if (address != null) {
            request.setDeliveryAddressId(address.getId());
            request.setDeliveryAddressText(address.getFullAddress());
            request.setDeliveryLatitude(address.getLatitude());
            request.setDeliveryLongitude(address.getLongitude());
        }

        // Optional delivery notes
        request.setDeliveryNotes("");

        // Order amounts
        request.setSubtotal(subtotal);
        request.setDeliveryFee(deliveryFee);
        request.setDiscountAmount(discountAmount);
        request.setTotalAmount(totalAmount);

        // Payment and status
        request.setPaymentMethod(OrderRequest.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        request.setOrderStatus("PENDING");
        request.setCouponCode(couponCode);

        // Order items
        request.setItems(items);

        return request;
    }
}