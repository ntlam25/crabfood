package com.example.crabfood.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.model.AuthResponse;
import com.example.crabfood.model.SignupRequest;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.repository.AuthRepository;

import java.util.regex.Pattern;

public class RegisterViewModel extends ViewModel {
    private static final String TAG = "RegisterViewModel";

    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<Boolean>> resultRegisterState = new MediatorLiveData<>();

    // Form fields MutableLiveData
    private final MutableLiveData<String> fullName = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> username = new MutableLiveData<>("");
    private final MutableLiveData<String> phone = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");

    // Form validation errors
    private final MutableLiveData<String> fullNameError = new MutableLiveData<>();
    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    private final MutableLiveData<String> phoneError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();

    // Form validity status
    private final MediatorLiveData<Boolean> isFormValid = new MediatorLiveData<>();

    // Registration result
    private LiveData<Resource<UserResponse>> registrationResult;

    public RegisterViewModel() {
        authRepository = new AuthRepository();
        setupFormValidation();
    }

    public MediatorLiveData<Resource<Boolean>> getResultRegisterState() {
        return resultRegisterState;
    }

    // Setup form validation checks
    private void setupFormValidation() {
        // Add sources to monitor all form fields
        isFormValid.addSource(fullName, value -> validateForm());
        isFormValid.addSource(email, value -> validateForm());
        isFormValid.addSource(username, value -> validateForm());
        isFormValid.addSource(phone, value -> validateForm());
        isFormValid.addSource(password, value -> validateForm());
        isFormValid.addSource(confirmPassword, value -> validateForm());

        // Initial value
        isFormValid.setValue(false);
    }

    private void validateForm() {
        boolean isValid = validateFullName() &&
                validateEmail() &&
                validateUsername() &&
                validatePhone() &&
                validatePassword() &&
                validateConfirmPassword();

        isFormValid.setValue(isValid);
    }

    // Individual field validations
    public boolean validateFullName() {
        String value = fullName.getValue();
        if (TextUtils.isEmpty(value)) {
            fullNameError.setValue("Full name is required");
            return false;
        } else if (value.length() < 3) {
            fullNameError.setValue("Full name must be at least 3 characters");
            return false;
        } else {
            fullNameError.setValue(null);
            return true;
        }
    }

    public boolean validateEmail() {
        String value = email.getValue();
        if (TextUtils.isEmpty(value)) {
            emailError.setValue("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            emailError.setValue("Please enter a valid email address");
            return false;
        } else {
            emailError.setValue(null);
            return true;
        }
    }

    public boolean validateUsername() {
        String value = username.getValue();
        if (TextUtils.isEmpty(value)) {
            usernameError.setValue("Username is required");
            return false;
        } else if (value.length() < 4) {
            usernameError.setValue("Username must be at least 4 characters");
            return false;
        } else if (!Pattern.compile("^[a-zA-Z0-9_]+$").matcher(value).matches()) {
            usernameError.setValue("Username can only contain letters, numbers, and underscores");
            return false;
        } else {
            usernameError.setValue(null);
            return true;
        }
    }

    public boolean validatePhone() {
        String value = phone.getValue();
        if (TextUtils.isEmpty(value)) {
            phoneError.setValue("Phone number is required");
            return false;
        } else if (!Patterns.PHONE.matcher(value).matches()) {
            phoneError.setValue("Please enter a valid phone number");
            return false;
        } else {
            phoneError.setValue(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String value = password.getValue();
        if (TextUtils.isEmpty(value)) {
            passwordError.setValue("Password is required");
            return false;
        } else if (value.length() < 8) {
            passwordError.setValue("Password must be at least 8 characters");
            return false;
        } else if (!Pattern.compile(".*[A-Z].*").matcher(value).matches()) {
            passwordError.setValue("Password must contain at least one uppercase letter");
            return false;
        } else if (!Pattern.compile(".*[a-z].*").matcher(value).matches()) {
            passwordError.setValue("Password must contain at least one lowercase letter");
            return false;
        } else if (!Pattern.compile(".*\\d.*").matcher(value).matches()) {
            passwordError.setValue("Password must contain at least one number");
            return false;
        } else {
            passwordError.setValue(null);
            return true;
        }
    }

    public boolean validateConfirmPassword() {
        String passwordValue = password.getValue();
        String confirmValue = confirmPassword.getValue();

        if (TextUtils.isEmpty(confirmValue)) {
            confirmPasswordError.setValue("Please confirm your password");
            return false;
        } else if (!confirmValue.equals(passwordValue)) {
            confirmPasswordError.setValue("Passwords do not match");
            return false;
        } else {
            confirmPasswordError.setValue(null);
            return true;
        }
    }

    // Register user
    public LiveData<Resource<UserResponse>> register() {
        SignupRequest signupRequest = new SignupRequest(
                email.getValue(),
                username.getValue(),
                password.getValue(),
                fullName.getValue(),
                phone.getValue()
        );
        registrationResult = authRepository.register(signupRequest);

        resultRegisterState.addSource(registrationResult, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                // Lưu thông tin phiên đăng nhập
                resultRegisterState.setValue(Resource.success(true));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                resultRegisterState.setValue(Resource.error(resource.getMessage(), false));
            }
        });

        return registrationResult;
    }

    // Getters for form fields
    public MutableLiveData<String> getFullName() {
        return fullName;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    // Getters for validation errors
    public LiveData<String> getFullNameError() {
        return fullNameError;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getUsernameError() {
        return usernameError;
    }

    public LiveData<String> getPhoneError() {
        return phoneError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }

    // Getter for form validity
    public LiveData<Boolean> getIsFormValid() {
        return isFormValid;
    }
}
