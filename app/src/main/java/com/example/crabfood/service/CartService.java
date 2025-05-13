package com.example.crabfood.service;

import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.CartSyncRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CartService {
    @GET("/api/cart")
    Call<List<CartItemEntity>> getCart();

    @POST("/api/cart/sync")
    Call<Void> syncCart(@Body CartSyncRequest request);

    @DELETE("/api/cart")
    Call<Void> clearCart();
}
