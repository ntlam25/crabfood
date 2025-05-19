package com.example.crabfood.service;

import com.example.crabfood.model.OrderRequest;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.PaymentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    @POST("api/orders")
    Call<OrderResponse> createOrder(@Body OrderRequest orderRequest);

    @POST("api/orders/online")
    Call<PaymentResponse> createOrderWithPayment(@Body OrderRequest orderRequest);

    @GET("api/orders/{orderId}")
    Call<OrderResponse> getOrderById(@Path("orderId") Long orderId,
                                     @Query("customerId") Long customerId);

    @GET("api/orders/upcoming")
    Call<List<OrderResponse>> getOrdersUpcoming(@Query("customerId") Long customerId);

    @GET("api/orders/history")
    Call<List<OrderResponse>> getOrdersHistory(@Query("customerId") Long customerId);

    @PUT("api/orders/{orderId}/cancel")
    Call<OrderResponse> cancelOrder(
            @Path("orderId") Long orderId,
            @Query("reason") String reason,
            @Query("customerId") Long customerId
    );

    @GET("api/orders/customer/{customerId}")
    Call<List<OrderResponse>> getUserOrders(@Path("customerId") long customerId);

    @POST("api/vnpay")
    Call<PaymentResponse> createUrlPayment(@Query("orderId") int orderId);

}