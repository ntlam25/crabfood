package com.example.crabfood.security;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Lấy user ID từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = String.valueOf(prefs.getLong("user_id", -1));
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);

        Request.Builder requestBuilder = originalRequest.newBuilder();

        // Thêm user ID nếu có
        if (userId != null && !userId.equals("-1")) {
            requestBuilder.header("X-User-Id", userId);
        }

        // Thêm Authorization token nếu có
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }
}