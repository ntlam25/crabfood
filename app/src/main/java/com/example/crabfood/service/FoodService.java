package com.example.crabfood.service;



import com.example.crabfood.model.FoodResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FoodService {
    @GET("api/foods")
    Call<FoodResponse> getAll();

    @GET("api/foods/{id}")
    Call<FoodResponse> findById(@Path("id") long id);

    @GET("api/foods/category/{id}")
    Call<List<FoodResponse>> findByCategoryId(@Path("id") long id);

    @GET("api/foods/feature/{vendorId}")
    Call<List<FoodResponse>> findFeaturedFoodsByVendorId(@Path("vendorId") long vendorId);

    @GET("api/foods/new/{vendorId}")
    Call<List<FoodResponse>> findNewFoodsByVendorId(@Path("vendorId") long vendorId);

    @GET("api/foods/popular/{vendorId}")
    Call<List<FoodResponse>> findPopularFoodsByVendorId(@Path("vendorId") long vendorId);
}
