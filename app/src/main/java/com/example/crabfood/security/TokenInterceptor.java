package com.example.crabfood.security;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.ui.auth.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private final Context context;
    private final SessionManager sessionManager;

    public TokenInterceptor(Context context, SessionManager sessionManager) {
        this.context = context.getApplicationContext();
        this.sessionManager = sessionManager;
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
        Response response = chain.proceed(newRequest);

        // Nếu gặp lỗi 401 (token hết hạn hoặc sai)
        if (response.code() == 401) {
            // Clear session
            sessionManager.logout();
            // Chuyển về màn hình đăng nhập
            // Cần dùng Handler vì interceptor chạy trong background thread
            new android.os.Handler(context.getMainLooper()).post(() -> {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            });
        }

        return response;
    }
}