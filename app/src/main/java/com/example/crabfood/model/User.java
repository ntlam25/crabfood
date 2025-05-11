package com.example.crabfood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private String avatarUrl;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean isActive;
    private Date lastLogin;
    private List<Address> addresses = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
    private String token;
    
    // Constructors
    public User() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public boolean isPhoneVerified() {
        return phoneVerified;
    }
    
    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public List<Address> getAddresses() {
        return addresses;
    }
    
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    // Helper methods
    public Address getDefaultAddress() {
        if (addresses != null && !addresses.isEmpty()) {
            for (Address address : addresses) {
                if (address.isDefault()) {
                    return address;
                }
            }
            return addresses.get(0);
        }
        return null;
    }
    
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
