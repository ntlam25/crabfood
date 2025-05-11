package com.example.crabfood.service;

import com.example.crabfood.model.AuthResponse;
import com.example.crabfood.model.LoginRequest;
import com.example.crabfood.model.SignupRequest;
import com.example.crabfood.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("/api/auth/register")
    Call<UserResponse> register(@Body SignupRequest request);

    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("/api/auth/verify-email")
    Call<String> verifyEmail(@Query("token") String token);
}
