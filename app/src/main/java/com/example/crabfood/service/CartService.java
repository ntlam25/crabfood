package com.example.crabfood.service;

import com.example.crabfood.model.CartItem;
import com.example.crabfood.model.CartSyncRequest;
import com.example.crabfood.model.CartSyncResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartService {
    @GET("api/cart")
    Call<List<CartItem>> getCart();

    @POST("api/cart/sync")
    Call<Void> syncCart(@Body CartSyncRequest request);

    @DELETE("api/cart")
    Call<Void> clearCart();
}
