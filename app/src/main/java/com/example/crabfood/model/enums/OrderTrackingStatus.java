package com.example.crabfood.model.enums;

import com.google.gson.annotations.SerializedName;

public enum OrderTrackingStatus {
    @SerializedName("PENDING")
    PENDING,

    @SerializedName("ACCEPTED")
    ACCEPTED,

    @SerializedName("PREPARING")
    PREPARING,

    @SerializedName("READY_FOR_PICKUP")
    READY_FOR_PICKUP,

    @SerializedName("PICKED_UP")
    PICKED_UP,

    @SerializedName("ON_THE_WAY")
    ON_THE_WAY,

    @SerializedName("ARRIVED")
    ARRIVED,

    @SerializedName("DELIVERED")
    DELIVERED,

    @SerializedName("CANCELLED")
    CANCELLED
}