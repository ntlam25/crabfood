package com.example.crabfood.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.AddressService;
import com.example.crabfood.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final String TAG = "UserRepository";
    private UserService service;

    public UserRepository() {
        service = ApiUtils.getUserService();
    }

    public LiveData<UserResponse> getUserById(Long id) {
        MutableLiveData<UserResponse> result = new MutableLiveData<>();

        service.findUserById(id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                result.postValue(null);
            }
        });

        return result;
    }
}
