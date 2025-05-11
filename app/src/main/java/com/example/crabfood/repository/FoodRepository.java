package com.example.crabfood.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.FoodService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodRepository {
    private FoodService service;

    public FoodRepository() {
        service = ApiUtils.getFoodService();
    }

    public LiveData<List<FoodResponse>> findByCategoryId(Long id){
        MutableLiveData<List<FoodResponse>> data = new MutableLiveData<>();

        service.findByCategoryId(id).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }

    public LiveData<List<FoodResponse>> findFeaturedFood(Long vendorId){
        MutableLiveData<List<FoodResponse>> data = new MutableLiveData<>();

        service.findFeaturedFoodsByVendorId(vendorId).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<FoodResponse>> findPopularFoods(Long vendorId){
        MutableLiveData<List<FoodResponse>> data = new MutableLiveData<>();

        service.findPopularFoodsByVendorId(vendorId).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }

    public LiveData<List<FoodResponse>> findNewFoods(Long vendorId){
        MutableLiveData<List<FoodResponse>> data = new MutableLiveData<>();

        service.findNewFoodsByVendorId(vendorId).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }

    public LiveData<FoodResponse> findById(Long id){
        MutableLiveData<FoodResponse> data = new MutableLiveData<>();

        service.findById(id).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }
}
