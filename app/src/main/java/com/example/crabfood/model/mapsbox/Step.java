package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

public class Step {
    @SerializedName("distance")
    KeyValue distance;

    @SerializedName("duration")
    KeyValue duration;

    @SerializedName("start_location")
    Location startLocation;
    @SerializedName("end_location")
    Location endLocation;

    @SerializedName("html_instructions")
    String htmlInstructions;

    String maneuver;

    Polyline polyline;

    @SerializedName("travel_mode")
    String travelMode;
}
