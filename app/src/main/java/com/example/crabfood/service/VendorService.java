package com.example.crabfood.service;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.VendorNearbyResponse;
import com.example.crabfood.model.VendorResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VendorService {
    @GET("api/vendors/nearby")
    Call<VendorNearbyResponse> getNearbyVendors(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") Double radius,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("cuisineType") String cuisineType,
            @Query("minRating") Double minRating,
            @Query("isOpen") Boolean isOpen,
            @Query("sortBy") String sortBy
    );

    @GET("api/vendors/{id}")
    Call<VendorResponse> getVendor(@Path("id") long id);

    @GET("api/vendors/{id}/categories")
    Call<List<CategoryResponse>> getCategories(@Path("id") int vendorId,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset);
}
