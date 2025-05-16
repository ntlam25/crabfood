package com.example.crabfood.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.repository.AuthRepository;

public class EmailVerificationViewModel extends ViewModel {
    private static final String TAG = "EmailVerificationViewModel";

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<String>> verificationStatus = new MutableLiveData<>();

    public EmailVerificationViewModel() {
        authRepository = new AuthRepository();
    }

    public LiveData<Resource<String>> verifyEmail(String token) {
        return authRepository.verifyEmail(token);
    }

    public LiveData<Resource<String>> getVerificationStatus() {
        return verificationStatus;
    }

    // Method to handle verification response
    public void handleVerification(String token) {
        verificationStatus.setValue(Resource.loading(null));

        LiveData<Resource<String>> result = authRepository.verifyEmail(token);

        // This is a simplified way to observe LiveData from within a ViewModel
        // In a real app, you might want to use a more sophisticated approach
        result.observeForever(response -> {
            verificationStatus.setValue(response);
        });
    }
}