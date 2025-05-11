package com.example.crabfood.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.crabfood.model.AuthResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLES = "roles";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthResponse(AuthResponse authResponse) {
        editor.putString(KEY_ACCESS_TOKEN, authResponse.getAccessToken());

        // Lưu refresh token nếu có
        if (authResponse.getRefreshToken() != null) {
            editor.putString(KEY_REFRESH_TOKEN, authResponse.getRefreshToken());
        }

        editor.putLong(KEY_USER_ID, authResponse.getUserId());
        editor.putString(KEY_USERNAME, authResponse.getUsername());
        editor.putString(KEY_EMAIL, authResponse.getEmail());

        // Lưu roles dưới dạng Set<String>
        if (authResponse.getRoles() != null && !authResponse.getRoles().isEmpty()) {
            Set<String> rolesSet = new HashSet<>(authResponse.getRoles());
            editor.putStringSet(KEY_ROLES, rolesSet);
        }

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public List<String> getRoles() {
        Set<String> rolesSet = sharedPreferences.getStringSet(KEY_ROLES, new HashSet<>());
        return new ArrayList<>(rolesSet);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean hasRole(String role) {
        Set<String> rolesSet = sharedPreferences.getStringSet(KEY_ROLES, new HashSet<>());
        return rolesSet.contains(role);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void updateToken(String newToken) {
        editor.putString(KEY_ACCESS_TOKEN, newToken);
        editor.apply();
    }
}
