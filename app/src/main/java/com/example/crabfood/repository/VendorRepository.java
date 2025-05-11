package com.example.crabfood.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.model.VendorNearbyResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.VendorService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class VendorRepository {
    private VendorService service;

    public VendorRepository() {
        service = ApiUtils.getVendorService();
    }

    public LiveData<List<VendorResponse>>
        getNearbyVendors(double latitude, double longitude, double radius,
                     Integer limit,
                     Integer offset,
                     String cuisineType,
                     Double minRating,
                     Boolean isOpen,
                     String sortBy) {
        MutableLiveData<List<VendorResponse>> data = new MutableLiveData<>();
        service.getNearbyVendors(latitude, longitude, radius, limit,
                        offset, cuisineType, minRating, isOpen, sortBy)
                .enqueue(new Callback<VendorNearbyResponse>() {
                    @Override
                    public void onResponse(Call<VendorNearbyResponse> call, Response<VendorNearbyResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.postValue(response.body().getContent());
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

    public LiveData<VendorResponse> findVendorById(Long id) {
        MutableLiveData<VendorResponse> data = new MutableLiveData<>();
        service.getVendor(id)
                .enqueue(new Callback<VendorResponse>() {
                    @Override
                    public void onResponse(Call<VendorResponse> call, Response<VendorResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.postValue(response.body());
                        } else {
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<VendorResponse> call, Throwable t) {
                        data.postValue(null);
                    }
                });
        return data;
    }


}
