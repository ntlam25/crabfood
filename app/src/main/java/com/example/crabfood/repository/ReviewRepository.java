package com.example.crabfood.repository;

import android.util.Log;

import com.example.crabfood.model.ApiResponse;
import com.example.crabfood.model.PageResponse;
import com.example.crabfood.model.ReviewResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.ReviewApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private static final String TAG = "ReviewRepository";
    private ReviewApiService apiService;

    public ReviewRepository() {
        this.apiService = ApiUtils.getReviewService();
    }

    public interface ReviewCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    public void createReview(ReviewResponse request, ReviewCallback<ReviewResponse> callback) {
        Call<ApiResponse<ReviewResponse>> call = apiService.createReview(request);
        call.enqueue(new Callback<ApiResponse<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponse>> call, Response<ApiResponse<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ReviewResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Lỗi tạo đánh giá");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponse>> call, Throwable t) {
                Log.e(TAG, "Tạo đánh giá không thành công", t);
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getVendorReviews(Long vendorId, int page, int size, ReviewCallback<PageResponse<ReviewResponse>> callback) {
        Call<ApiResponse<PageResponse<ReviewResponse>>> call = apiService.getVendorReviews(vendorId, page, size);
        call.enqueue(new Callback<ApiResponse<PageResponse<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<ReviewResponse>>> call, Response<ApiResponse<PageResponse<ReviewResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<ReviewResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get vendor reviews");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<ReviewResponse>>> call, Throwable t) {
                Log.e(TAG, "Get vendor reviews failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getMyReviews(int page, int size, ReviewCallback<PageResponse<ReviewResponse>> callback) {
        Call<ApiResponse<PageResponse<ReviewResponse>>> call = apiService.getMyReviews(page, size);
        call.enqueue(new Callback<ApiResponse<PageResponse<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<ReviewResponse>>> call, Response<ApiResponse<PageResponse<ReviewResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<ReviewResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get user reviews");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<ReviewResponse>>> call, Throwable t) {
                Log.e(TAG, "Get user reviews failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getReviewByOrderId(Long orderId, ReviewCallback<ReviewResponse> callback) {
        apiService.getReviewByOrderId(orderId).enqueue(new Callback<ApiResponse<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponse>> call, Response<ApiResponse<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to get review");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}