package com.example.crabfood.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesRepository {
    private final CategoryService categoryService;

    public CategoriesRepository() {
        categoryService = ApiUtils.getCategoryService();
    }

    public LiveData<List<CategoryResponse>> getAllCategories() {
        MutableLiveData<List<CategoryResponse>> data = new MutableLiveData<>();

        categoryService.getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }

    public LiveData<CategoryResponse> getCategoryById(Long id) {
        MutableLiveData<CategoryResponse> data = new MutableLiveData<>();

        categoryService.getCategoryById(id).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }

    // get categories by vendor id or global
    public LiveData<List<CategoryResponse>> getByVendorIdOrGlobal(Long vendorId) {
        MutableLiveData<List<CategoryResponse>> data = new MutableLiveData<>();

        categoryService.getCategoryByVendorId(vendorId).enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }
}
