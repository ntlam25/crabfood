package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg {
    @SerializedName("distance")
    KeyValue distance;

    @SerializedName("duration")
    KeyValue duration;

    @SerializedName("start_location")
    Location startLocation;

    @SerializedName("end_location")
    Location endLocation;

    @SerializedName("start_address")
    String startAddress;

    @SerializedName("end_address")
    String endAddress;

    @SerializedName("steps")
    List<Step> steps;

    public KeyValue getDistance() {
        return distance;
    }

    public void setDistance(KeyValue distance) {
        this.distance = distance;
    }

    public KeyValue getDuration() {
        return duration;
    }

    public void setDuration(KeyValue duration) {
        this.duration = duration;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
