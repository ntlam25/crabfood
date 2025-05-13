package com.example.crabfood.repository;

import android.content.Context;

import com.example.crabfood.model.OrderRequest;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static volatile OrderRepository instance;
    private final OrderService apiService;

    // Callback interface for order operations
    public interface OrderCallback {
        void onOrderSuccess(OrderResponse response);
        void onOrderFailure(String errorMessage);
    }

    private OrderRepository(Context context) {
        apiService = ApiUtils.getOrderService();
    }

    public static OrderRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                if (instance == null) {
                    instance = new OrderRepository(context);
                }
            }
        }
        return instance;
    }

    /**
     * Places a new order
     */
    public void placeOrder(OrderRequest orderRequest, OrderCallback callback) {
        Call<OrderResponse> call = apiService.createOrder(orderRequest);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onOrderSuccess(response.body());
                } else {
                    String errorMsg = "Đặt hàng thất bại. Vui lòng thử lại sau.";
                    if (response.errorBody() != null) {
                        // You could parse the error body for more specific error message
                        errorMsg = "Lỗi: " + response.code() + " - " + response.message();
                    }
                    callback.onOrderFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onOrderFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Get order by ID
     */
    public void getOrderById(long orderId, OrderCallback callback) {
        Call<OrderResponse> call = apiService.getOrderById(orderId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onOrderSuccess(response.body());
                } else {
                    String errorMsg = "Không thể lấy thông tin đơn hàng.";
                    if (response.errorBody() != null) {
                        errorMsg = "Lỗi: " + response.code() + " - " + response.message();
                    }
                    callback.onOrderFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onOrderFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Get orders for the current user
     */
    public void getUserOrders(long userId, OrdersCallback callback) {
        Call<List<OrderResponse>> call = apiService.getUserOrders(userId);
        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onOrdersSuccess(response.body());
                } else {
                    String errorMsg = "Không thể lấy danh sách đơn hàng.";
                    if (response.errorBody() != null) {
                        errorMsg = "Lỗi: " + response.code() + " - " + response.message();
                    }
                    callback.onOrdersFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                callback.onOrdersFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Callback interface for getting multiple orders
     */
    public interface OrdersCallback {
        void onOrdersSuccess(List<OrderResponse> responses);
        void onOrdersFailure(String errorMessage);
    }
}