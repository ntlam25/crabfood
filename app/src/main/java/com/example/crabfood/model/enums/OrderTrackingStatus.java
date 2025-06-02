package com.example.crabfood.model.enums;

import com.google.gson.annotations.SerializedName;

public enum OrderTrackingStatus {
    @SerializedName("PENDING")
    PENDING,

    @SerializedName("ACCEPTED")
    ACCEPTED,

    @SerializedName("PICKED_UP")
    PICKED_UP,

    @SerializedName("ON_THE_WAY")
    ON_THE_WAY,

    @SerializedName("SUCCESS")
    SUCCESS,

    @SerializedName("CANCELLED")
    CANCELLED
}