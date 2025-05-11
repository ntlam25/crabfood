package com.example.crabfood.service;


import com.example.crabfood.model.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryService {
    @GET("api/categories")
    Call<List<CategoryResponse>> getAllCategories();

    @GET("api/categories/global")
    Call<List<CategoryResponse>> getAllGlobalCategories();
    @GET("api/categories/{id}")
    Call<CategoryResponse> getCategoryById(@Path("id") long id);

    @GET("api/categories/vendor/{vendorId}")
    Call<List<CategoryResponse>> getCategoryByVendorId(@Path("vendorId") long vendorId);
}
