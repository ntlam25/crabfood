package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

public class RouteResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("routes")
    private Route[] routes;

    @SerializedName("waypoints")
    private Waypoint[] waypoints;

    public String getCode() {
        return code;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public Waypoint[] getWaypoints() {
        return waypoints;
    }

    public static class Route {
        @SerializedName("geometry")
        private String geometry;

        @SerializedName("legs")
        private Leg[] legs;

        @SerializedName("distance")
        private double distance;

        @SerializedName("duration")
        private double duration;

        public String getGeometry() {
            return geometry;
        }

        public Leg[] getLegs() {
            return legs;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }

    public static class Leg {
        @SerializedName("steps")
        private Step[] steps;

        @SerializedName("distance")
        private double distance;

        @SerializedName("duration")
        private double duration;

        public Step[] getSteps() {
            return steps;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }

    public static class Step {
        @SerializedName("geometry")
        private String geometry;

        @SerializedName("maneuver")
        private String maneuver;

        @SerializedName("instruction")
        private String instruction;

        @SerializedName("distance")
        private double distance;

        @SerializedName("duration")
        private double duration;

        public String getGeometry() {
            return geometry;
        }

        public String getManeuver() {
            return maneuver;
        }

        public String getInstruction() {
            return instruction;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }

    public static class Waypoint {
        @SerializedName("name")
        private String name;

        @SerializedName("location")
        private double[] location;

        public String getName() {
            return name;
        }

        public double[] getLocation() {
            return location;
        }
    }
}