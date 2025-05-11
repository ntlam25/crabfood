package com.example.crabfood.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.crabfood.model.AuthResponse;
import com.example.crabfood.model.LoginRequest;
import com.example.crabfood.model.SignupRequest;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.AuthService;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenRepository {
    private final AuthService authService;
    private final SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "auth_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";

    public AuthenRepository(Context context) {

        authService = ApiUtils.getAuthService();
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(String identifier, String password, Callback<AuthResponse> callback) {
        LoginRequest request = new LoginRequest(identifier, password);
        authService.login(request).enqueue(callback);
    }

    public void register(SignupRequest request, Callback<UserResponse> callback) {
        authService.register(request).enqueue(callback);
    }

    public void saveAuthData(AuthResponse response) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, response.getAccessToken());
        editor.putString(KEY_REFRESH_TOKEN, response.getRefreshToken());
        editor.putLong(KEY_USER_ID, response.getUserId());
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
