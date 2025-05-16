package com.example.crabfood.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.crabfood.R;
import com.example.crabfood.databinding.ActivityRegisterBinding;
import com.example.crabfood.helpers.KeyboardHelper;
import com.example.crabfood.helpers.Resource;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Set up UI components
        setupUI();
        setupListeners();
        observeViewModel();
    }

    private void setupUI() {
        // Set up UI initial state
        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        // Full Name field
        binding.etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getFullName().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Email field
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getEmail().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Username field
        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getUsername().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Phone field
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getPhone().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password field
        binding.etPasswordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getPassword().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm Password field
        binding.etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getConfirmPassword().setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Register button
        binding.btnRegister.setOnClickListener(v -> {
            // Hide keyboard
            KeyboardHelper.hideKeyboard(this);

            // Perform individual validations for better UX
            boolean isFullNameValid = viewModel.validateFullName();
            boolean isEmailValid = viewModel.validateEmail();
            boolean isUsernameValid = viewModel.validateUsername();
            boolean isPhoneValid = viewModel.validatePhone();
            boolean isPasswordValid = viewModel.validatePassword();
            boolean isConfirmPasswordValid = viewModel.validateConfirmPassword();

            if (isFullNameValid && isEmailValid && isUsernameValid &&
                    isPhoneValid && isPasswordValid && isConfirmPasswordValid) {
                // Register user
                viewModel.register();
                observeResult();
            }
        });

        // Login button
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void observeViewModel() {
        // Observe validation errors
        viewModel.getFullNameError().observe(this, error -> {
            binding.tilFullName.setError(error);
        });

        viewModel.getEmailError().observe(this, error -> {
            binding.tilEmail.setError(error);
        });

        viewModel.getUsernameError().observe(this, error -> {
            binding.tilUsername.setError(error);
        });

        viewModel.getPhoneError().observe(this, error -> {
            binding.tilPhone.setError(error);
        });

        viewModel.getPasswordError().observe(this, error -> {
            binding.tilPassword.setError(error);
        });

        viewModel.getConfirmPasswordError().observe(this, error -> {
            binding.tilConfirmPassword.setError(error);
        });

        // Observe form validity
        viewModel.getIsFormValid().observe(this, isValid -> {
            binding.btnRegister.setEnabled(isValid);
            binding.btnRegister.setAlpha(isValid ? 1.0f : 0.6f);
        });

        // Observe registration result
        viewModel.register().observe(this, result -> {
            if (result.getStatus() == Resource.Status.LOADING) {
                showLoading(true);
            } else if (result.getStatus() == Resource.Status.SUCCESS) {
                showLoading(false);
                showRegistrationSuccess();
            } else if (result.getStatus() == Resource.Status.ERROR) {
                showLoading(false);
                showError(result.getMessage());
            }
        });
    }

    private void observeResult() {
        viewModel.getResultRegisterState().observe(this, isSuccess -> {
            switch (isSuccess.getStatus()) {
                case SUCCESS:
                    showRegistrationSuccess();
                    break;
                case ERROR:
                    Toast.makeText(RegisterActivity.this, isSuccess.getMessage(), Toast.LENGTH_LONG).show();
                    Snackbar.make(findViewById(android.R.id.content), isSuccess.getMessage(), 2000).show();
                    break;
                case LOADING:
                    // Already showing progress bar
                    break;
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!isLoading);
        binding.btnRegister.setText(isLoading ? "" : getString(R.string.btn_register));
    }

    private void showRegistrationSuccess() {
        Toast.makeText(this, "Registration successful! Please verify your email.", Toast.LENGTH_LONG).show();

        // Navigate to email verification activity or login screen
//        Intent intent = new Intent(RegisterActivity.this, EmailVerificationActivity.class);
//        intent.putExtra("email", viewModel.getEmail().getValue());
//        startActivity(intent);
        finish();
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}