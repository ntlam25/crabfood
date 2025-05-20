package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {

    @SerializedName("bounds")
    Object bounds;

    @SerializedName("legs")
    List<Leg> legs;

    @SerializedName("overview_polyline")
    Polyline overviewPolyline;

    String summary;

    @SerializedName("warnings")
    List<Object> warnings;

    @SerializedName("waypoint_order")
    List<Object> waypointOrder;

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public Polyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(Polyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Object getBounds() {
        return bounds;
    }

    public void setBounds(Object bounds) {
        this.bounds = bounds;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    public List<Object> getWaypointOrder() {
        return waypointOrder;
    }

    public void setWaypointOrder(List<Object> waypointOrder) {
        this.waypointOrder = waypointOrder;
    }
}
