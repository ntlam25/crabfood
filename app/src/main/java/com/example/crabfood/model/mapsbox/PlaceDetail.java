package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

public class PlaceDetail {

    @SerializedName("place_id")
    String placeId;

    @SerializedName("formatted_address")
    String address;


    @SerializedName("geometry")
    Geometry geometry;

    @SerializedName("name")
    String name;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
