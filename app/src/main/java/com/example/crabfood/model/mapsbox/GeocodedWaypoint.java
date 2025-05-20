package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

public class GeocodedWaypoint {
    @SerializedName("geocoder_status")
    String status;

    @SerializedName("place_id")
    String placeId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
