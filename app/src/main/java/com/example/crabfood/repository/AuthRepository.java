package com.example.crabfood.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.model.AuthResponse;
import com.example.crabfood.model.LoginRequest;
import com.example.crabfood.model.SignupRequest;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final AuthService apiService;

    public AuthRepository() {
        apiService = ApiUtils.getAuthService();
    }

    public LiveData<Resource<AuthResponse>> login(String identifier, String password) {
        MutableLiveData<Resource<AuthResponse>> loginResult = new MutableLiveData<>();
        loginResult.setValue(Resource.loading(null));

        LoginRequest loginRequest = new LoginRequest(identifier, password);
        apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(Resource.success(response.body()));
                } else {
                    String errorMessage = "Login failed: " + response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    loginResult.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
                Log.e(TAG, "Login network error", t);
            }
        });

        return loginResult;
    }

    public LiveData<Resource<UserResponse>> register(SignupRequest signupRequest) {
        MutableLiveData<Resource<UserResponse>> registerResult = new MutableLiveData<>();
        registerResult.setValue(Resource.loading(null));

        apiService.register(signupRequest).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(Resource.success(response.body()));
                } else {
                    String errorMessage = "Registration failed: " + response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    registerResult.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                registerResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
                Log.e(TAG, "Register network error", t);
            }
        });

        return registerResult;
    }

    public LiveData<Resource<String>> verifyEmail(String token) {
        MutableLiveData<Resource<String>> verificationResult = new MutableLiveData<>();
        verificationResult.setValue(Resource.loading(null));

        apiService.verifyEmail(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    verificationResult.setValue(Resource.success(response.body()));
                } else {
                    String errorMessage = "Verification failed: " + response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    verificationResult.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                verificationResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
                Log.e(TAG, "Email verification network error", t);
            }
        });

        return verificationResult;
    }
}