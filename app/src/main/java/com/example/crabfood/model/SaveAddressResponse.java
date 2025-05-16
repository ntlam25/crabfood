package com.example.crabfood.model;

public class SaveAddressResponse {
    private boolean success;
    private String message;
    private AddressRequest data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AddressRequest getData() {
        return data;
    }

    public void setData(AddressRequest data) {
        this.data = data;
    }
}