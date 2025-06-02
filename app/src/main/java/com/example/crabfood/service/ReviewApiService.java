package com.example.crabfood.service;

import com.example.crabfood.model.ApiResponse;
import com.example.crabfood.model.PageResponse;
import com.example.crabfood.model.ReviewResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface ReviewApiService {

    @POST("api/reviews")
    Call<ApiResponse<ReviewResponse>> createReview(@Body ReviewResponse request);

    @GET("api/reviews/vendor/{vendorId}")
    Call<ApiResponse<PageResponse<ReviewResponse>>> getVendorReviews(
            @Path("vendorId") Long vendorId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/reviews/user/my-reviews")
    Call<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/reviews/order/{orderId}")
    Call<ApiResponse<ReviewResponse>> getReviewByOrderId(@Path("orderId") Long orderId);
}