package com.example.crabfood.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.AddressService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private final String TAG = "AddressRepository";
    private AddressService service;
    private List<AddressResponse> result = new ArrayList<>();

    public AddressRepository() {
        service = ApiUtils.getAddressService();
    }

    public LiveData<Resource<AddressResponse>> createAddress(AddressRequest request) {
        MutableLiveData<Resource<AddressResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        service.createAddress(request).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Tạo địa chỉ thất bại", null));
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<AddressResponse>>> getUserAddresses() {
        MutableLiveData<Resource<List<AddressResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        service.getUserAddresses().enqueue(new Callback<List<AddressResponse>>() {
            @Override
            public void onResponse(Call<List<AddressResponse>> call, Response<List<AddressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Không thể tải danh sách địa chỉ", null));
                }
            }

            @Override
            public void onFailure(Call<List<AddressResponse>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }
}
