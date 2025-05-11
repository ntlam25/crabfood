package com.example.crabfood;

import android.app.Application;

import com.example.crabfood.retrofit.RetrofitClient;
import com.example.crabfood.security.TokenInterceptor;

public class CrabFoodApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo ApiClient với token interceptor
        initApiClient();
    }

    private void initApiClient() {
        // Đảm bảo thêm token vào mọi request API
        TokenInterceptor tokenInterceptor = new TokenInterceptor(this);
        RetrofitClient.init(tokenInterceptor);
    }
}