package com.example.crabfood.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.crabfood.MainActivity;
import com.example.crabfood.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.crabfood.R;
import com.example.crabfood.helpers.Resource;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private TextInputEditText editTextIdentifier, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Khởi tạo các view
        editTextIdentifier = findViewById(R.id.et_Identifier);
        editTextPassword = findViewById(R.id.et_password);
        buttonLogin = findViewById(R.id.btn_login_email);
        textViewRegister = findViewById(R.id.btn_register_login);
        progressBar = findViewById(R.id.progress_bar);

        // Kiểm tra trạng thái đăng nhập
        if (authViewModel.isLoggedIn()) {
            navigateToMain();
        }

        // Thiết lập listener cho nút đăng nhập
        buttonLogin.setOnClickListener(v -> attemptLogin());

        // Thiết lập listener cho nút đăng ký
        textViewRegister.setOnClickListener(v -> navigateToRegister());

        // Quan sát trạng thái xác thực
        authViewModel.getAuthenticationState().observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData()) {
                navigateToMain();
            }
        });
    }

    private void attemptLogin() {
        String identifier = editTextIdentifier.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Kiểm tra tính hợp lệ
        if (identifier.isEmpty()) {
            editTextIdentifier.setError("Please enter your email or username");
            editTextIdentifier.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        // Hiển thị progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Thực hiện đăng nhập
        authViewModel.login(identifier, password).observe(this, response -> {
            progressBar.setVisibility(View.GONE);

            switch (response.getStatus()) {
                case SUCCESS:
                    // Login successful, navigation handled by authenticationState observer
                    break;
                case ERROR:
                    Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Already showing progress bar
                    break;
            }
        });
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}