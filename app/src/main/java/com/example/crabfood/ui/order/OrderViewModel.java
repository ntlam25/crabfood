package com.example.crabfood.ui.order;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.cores.Event;
import com.example.crabfood.cores.TaggedError;
import com.example.crabfood.cores.enums.ErrorSource;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.model.enums.OrderStatus;
import com.example.crabfood.repository.OrderRepository;
import com.example.crabfood.repository.VendorRepository;
import com.example.crabfood.model.OrderTrackingInfo;
import com.example.crabfood.model.LocationUpdateResponse;
import com.example.crabfood.websocket.WebSocketClient;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private static final String TAG = "Order ViewModel";
    private final OrderRepository repository;
    private final VendorRepository vendorRepository;
    private SessionManager sessionManager;
    private final MutableLiveData<OrderResponse> cancelledOrder = new MutableLiveData<>();
    private final MutableLiveData<OrderResponse> orderDetail = new MutableLiveData<>();
    private final MutableLiveData<List<OrderResponse>> upcomingOrders = new MutableLiveData<>();
    private final MutableLiveData<List<OrderResponse>> pastOrders = new MutableLiveData<>();
    private final MutableLiveData<Event<TaggedError>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<OrderTrackingInfo> trackingInfo = new MutableLiveData<>();
    private final MutableLiveData<LocationUpdateResponse> locationUpdate = new MutableLiveData<>();
    private WebSocketClient webSocketClient;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        repository = OrderRepository.getInstance(application);
        vendorRepository = new VendorRepository();
        sessionManager = new SessionManager(application);
        loadUserOrdersUpcoming();
        loadUserOrdersHistory();
    }

    public LiveData<List<OrderResponse>> getUpcomingOrders() {
        return upcomingOrders;
    }

    public LiveData<OrderResponse> getCancelledOrder() {
        return cancelledOrder;
    }

    public LiveData<OrderResponse> getOrderDetail() {
        return orderDetail;
    }

    public LiveData<List<OrderResponse>> getPastOrders() {
        return pastOrders;
    }

    public LiveData<Event<TaggedError>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<OrderTrackingInfo> getTrackingInfo() {
        return trackingInfo;
    }

    public LiveData<LocationUpdateResponse> getLocationUpdate() {
        return locationUpdate;
    }

    public void loadUserOrdersHistory() {
        isLoading.setValue(true);
        // Get user ID from session or another source
        long userId = getUserId();

        repository.getUserOrdersHistory(userId, new OrderRepository.OrdersCallback() {
            @Override
            public void onOrdersSuccess(List<OrderResponse> responses) {
                pastOrders.postValue(responses);
                isLoading.postValue(false);
            }

            @Override
            public void onOrdersFailure(String errorMsg) {
                errorMessage.postValue(new Event<>(new TaggedError(ErrorSource.HISTORY, errorMsg)));
                isLoading.postValue(false);
            }
        });
    }

    public void loadUserOrdersUpcoming() {
        isLoading.setValue(true);
        // Get user ID from session or another source
        long userId = getUserId();

        repository.getUserOrdersUpcoming(userId, new OrderRepository.OrdersCallback() {
            @Override
            public void onOrdersSuccess(List<OrderResponse> responses) {
                upcomingOrders.postValue(responses);
                isLoading.postValue(false);
            }

            @Override
            public void onOrdersFailure(String errorMsg) {
                errorMessage.postValue(new Event<>(new TaggedError(ErrorSource.UPCOMING, errorMsg)));
                isLoading.postValue(false);
            }
        });
    }

    public void loadOrderDetail(Long orderId) {
        isLoading.setValue(true);
        // Get user ID from session or another source

        repository.getOrderById(orderId, new OrderRepository.OrderCallback() {
            @Override
            public void onOrderSuccess(OrderResponse responses) {
                orderDetail.postValue(responses);
                isLoading.postValue(false);
            }

            @Override
            public void onOrderFailure(String errorMsg) {
                errorMessage.postValue(new Event<>(new TaggedError(ErrorSource.DETAIL, errorMsg)));
                isLoading.postValue(false);
            }
        });
    }

    public void cancelOrder(long orderId) {
        isLoading.setValue(true);

        long customerId = getUserId();

        repository.cancelOrder(orderId, "", customerId, new OrderRepository.OrderCallback() {
            @Override
            public void onOrderSuccess(OrderResponse response) {
                if (response != null) {
                    if (response.getOrderStatus() == OrderStatus.CANCELLED) {
                        cancelledOrder.setValue(response);
                        List<OrderResponse> currentUpcoming = new ArrayList<>(upcomingOrders.getValue());
                        currentUpcoming.removeIf(order -> order.getId() == response.getId());
                        upcomingOrders.setValue(currentUpcoming);

                        // 2. Thêm vào danh sách history
                        List<OrderResponse> currentHistory = new ArrayList<>(pastOrders.getValue());
                        currentHistory.add(0, response);
                        pastOrders.setValue(currentHistory);
                    } else {
                        errorMessage.postValue(new Event<>
                                (new TaggedError(ErrorSource.UPCOMING,
                                        "Không thể huỷ đơn hàng đã chuẩn bị")));
                    }
                }
                isLoading.postValue(false);
            }

            @Override
            public void onOrderFailure(String errorMsg) {
                errorMessage.postValue(new Event<>(new TaggedError(ErrorSource.UPCOMING, errorMsg)));
                isLoading.postValue(false);
            }
        });
    }

    // Helper methods
    private long getUserId() {
        return sessionManager.getUserId();
    }
}