package com.example.crabfood.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.crabfood.R;

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
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private TextInputEditText editTextEmail, editTextUsername, editTextPassword,
            editTextConfirmPassowrd, editTextFullName, editTextPhone;
    private Button buttonRegister,buttonLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Khởi tạo các view
        editTextEmail = findViewById(R.id.et_email);
        editTextUsername = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password_register);
        editTextConfirmPassowrd = findViewById(R.id.et_confirm_password);
        editTextFullName = findViewById(R.id.et_full_name);
        editTextPhone = findViewById(R.id.et_phone);
        buttonRegister = findViewById(R.id.btn_register);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        // Thiết lập listener cho nút đăng ký
        buttonRegister.setOnClickListener(v -> attemptRegister());

        // Thiết lập listener cho nút đăng nhập
        buttonLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // Kiểm tra tính hợp lệ
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editTextUsername.setError("Please enter your username");
            editTextUsername.requestFocus();
            return;
        }

        if (username.length() < 6) {
            editTextUsername.setError("Username must be at least 6 characters");
            editTextUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            editTextFullName.setError("Please enter your full name");
            editTextFullName.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^0\\d{9,10}$")) {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        // Hiển thị progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Thực hiện đăng ký
        authViewModel.register(email, username, password, fullName, phone).observe(this, response -> {
            progressBar.setVisibility(View.GONE);

            switch (response.getStatus()) {
                case SUCCESS:
                    Toast.makeText(RegisterActivity.this,
                            "Registration successful. Please check your email for verification",
                            Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(RegisterActivity.this,
                            response.getMessage(),
                            Toast.LENGTH_LONG).show();
                    break;
                case LOADING:
                    // Already showing progress bar
                    break;
            }
        });
    }
}