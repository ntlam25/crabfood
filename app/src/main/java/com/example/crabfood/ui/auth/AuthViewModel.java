package com.example.crabfood.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.AuthResponse;
import com.example.crabfood.model.SignupRequest;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.repository.AuthRepository;
import com.example.crabfood.repository.UserRepository;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.UserService;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final UserRepository userRepository;
    private final MediatorLiveData<Resource<Boolean>> authenticationState = new MediatorLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        sessionManager = new SessionManager(application);
        userRepository = new UserRepository();

        // Kiểm tra trạng thái đăng nhập khi khởi tạo
        if (sessionManager.isLoggedIn()) {
            authenticationState.setValue(Resource.success(true));
        } else {
            authenticationState.setValue(Resource.success(false));
        }
    }

    public LiveData<Resource<AuthResponse>> login(String identifier, String password) {
        LiveData<Resource<AuthResponse>> loginResponse = authRepository.login(identifier, password);

        authenticationState.addSource(loginResponse, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                // Lưu thông tin phiên đăng nhập
                sessionManager.saveAuthResponse(resource.getData());
                authenticationState.setValue(Resource.success(true));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                authenticationState.setValue(Resource.error(resource.getMessage(), false));
            }
        });

        return loginResponse;
    }

    public LiveData<Resource<Boolean>> getAuthenticationState() {
        return authenticationState;
    }

    public void logout() {
        sessionManager.logout();
        authenticationState.setValue(Resource.success(false));
    }

    public LiveData<UserResponse> getCurrentUser(){
        return userRepository.getUserById(sessionManager.getUserId());
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public boolean hasRole(String role) {
        return sessionManager.hasRole(role);
    }

    public String getUsername() {
        return sessionManager.getUsername();
    }

    public Long getUserId() {
        return sessionManager.getUserId();
    }
}

