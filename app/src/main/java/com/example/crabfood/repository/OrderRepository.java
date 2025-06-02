package com.example.crabfood.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.cores.enums.ErrorSource;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.OrderRequest;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.OrderTrackingInfo;
import com.example.crabfood.model.PaymentResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "Order Repository";
    private static volatile OrderRepository instance;
    private final OrderService apiService;
    private final SessionManager sessionManager;

    // Callback interface for order operations
    public interface OrderCallback {
        void onOrderSuccess(OrderResponse response);

        void onOrderFailure(String errorMsg);
    }
    public interface PaymentCallBack {
        void onPaymentSuccess(PaymentResponse response);

        void onPaymentFailure(String errorMessage);
    }

    private OrderRepository(Context context) {
        apiService = ApiUtils.getOrderService();
        sessionManager = new SessionManager(context);
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
                callback.onOrderFailure("Lỗi : " + t.getMessage());
            }
        });
    }

    public void placeOrderWithPayment(OrderRequest orderRequest, PaymentCallBack callback) {
        Call<PaymentResponse> call = apiService.createOrderWithPayment(orderRequest);
        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onPaymentSuccess(response.body());
                } else {
                    String errorMsg = "Thanh toán thất bại. Vui lòng thử lại sau.";
                    if (response.errorBody() != null) {
                        // You could parse the error body for more specific error message
                        errorMsg = "Lỗi: " + response.code() + " - " + response.message();
                    }
                    callback.onPaymentFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                callback.onPaymentFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getOrderById(long orderId, OrderCallback callback) {
        Call<OrderResponse> call = apiService.getOrderById(orderId, sessionManager.getUserId());
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

    public void getUserOrdersHistory(long userId, OrdersCallback callback) {
        Call<List<OrderResponse>> call = apiService.getOrdersHistory(userId);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
                callback.onOrdersFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void getUserOrdersUpcoming(long userId, OrdersCallback callback) {
        Call<List<OrderResponse>> call = apiService.getOrdersUpcoming(userId);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
                callback.onOrdersFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void cancelOrder(Long orderId, String reason, Long customerId,  OrderCallback callback) {
        Call<OrderResponse> call = apiService.cancelOrder(orderId, reason, customerId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onOrderSuccess(response.body());
                } else {
                    String errorMsg = "Không thể lấy danh sách đơn hàng.";
                    if (response.errorBody() != null) {
                        errorMsg = "Lỗi: " + response.code() + " - " + response.message();
                    }
                    callback.onOrderFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onOrderFailure("Lỗi " + t.getMessage());
            }
        });
    }

    public interface OrdersCallback {
        void onOrdersSuccess(List<OrderResponse> responses);

        void onOrdersFailure(String errorMessage);
    }

}