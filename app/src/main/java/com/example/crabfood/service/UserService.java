package com.example.crabfood.service;

import com.example.crabfood.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("api/users/{id}")
    Call<UserResponse> findUserById(@Path("id") Long id);

    @PUT("api/users/{id}")
    Call<UserResponse> updateUserById(@Path("id") Long id);
}
