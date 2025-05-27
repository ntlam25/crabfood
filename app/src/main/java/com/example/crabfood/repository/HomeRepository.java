package com.example.crabfood.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.PageResponse;
import com.example.crabfood.model.VendorNearbyResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.CategoryService;
import com.example.crabfood.service.VendorService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private final CategoryService categoryService;
    private final VendorService vendorService;

    public HomeRepository() {
        this.categoryService = ApiUtils.getCategoryService();
        this.vendorService = ApiUtils.getVendorService();
    }

    public LiveData<List<CategoryResponse>> getCategories() {
        MutableLiveData<List<CategoryResponse>> data = new MutableLiveData<>();
        categoryService.getAllGlobalCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    Log.d("API_RESPONSE", "Categories count: " + response.body().size());
                    List<CategoryResponse> categories = response.body().size() > 8
                            ? response.body().subList(0, 8)
                            : response.body();
                    data.postValue(categories);
                } else {
                    Log.e("API_RESPONSE", "Response error code: " + response.code());
                    data.postValue(new ArrayList<>()); // trả về danh sách rỗng nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Log.e("API_RESPONSE", "Failure: " + t.getMessage());
                data.postValue(null); // báo lỗi
            }
        });
        return data;
    }

    public LiveData<List<VendorResponse>> getNearbyVendorsDefault(double latitude, double longitude, double radius) {
        MutableLiveData<List<VendorResponse>> data = new MutableLiveData<>();
        vendorService.getNearbyVendors(latitude, longitude, radius, null, null, null, null, null, null)
                .enqueue(new Callback<VendorNearbyResponse>() {
                    @Override
                    public void onResponse(Call<VendorNearbyResponse> call, Response<VendorNearbyResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<VendorResponse> result = response.body().getContent().size() > 4
                                    ? response.body().getContent().subList(0,4)
                                    : response.body().getContent();

                            data.postValue(result);
                        } else {
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<VendorNearbyResponse> call, Throwable t) {
                        data.postValue(null);
                    }
                });
        return data;
    }
}

