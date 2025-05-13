package com.example.crabfood.service;

import com.example.crabfood.model.OrderRequest;
import com.example.crabfood.model.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderService {
    @POST("api/orders")
    Call<OrderResponse> createOrder(@Body OrderRequest orderRequest);

    @GET("api/orders/{id}")
    Call<OrderResponse> getOrderById(@Path("id") long id);
    @GET("api/orders/customer/{customerId}")
    Call<List<OrderResponse>> getUserOrders(@Path("customerId") long customerId);
}