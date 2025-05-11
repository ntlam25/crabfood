package com.example.crabfood.model;

import java.util.HashSet;
import java.util.Set;

public class SignupRequest {
    private String email;
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private Set<String> roles;

    public SignupRequest(String email, String username, String password, String fullName, String phone) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.roles = new HashSet<>();
        this.roles.add("CUSTOMER"); // Default role
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Thêm vai trò cho người dùng
    public void addRole(String role) {
        this.roles.add(role);
    }
}