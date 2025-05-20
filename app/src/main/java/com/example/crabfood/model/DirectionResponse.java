package com.example.crabfood.model;

import com.example.crabfood.model.mapsbox.GeocodedWaypoint;
import com.example.crabfood.model.mapsbox.Route;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionResponse {

    @SerializedName("geocoded_waypoints")
    List<GeocodedWaypoint> geocodedWaypoints;

    List<Route> routes;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
