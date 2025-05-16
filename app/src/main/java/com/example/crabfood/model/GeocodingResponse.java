package com.example.crabfood.model;

import java.util.List;

public class GeocodingResponse {
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {
        private String id;
        private String place_name;
        private List<Double> center; // [longitude, latitude]
        private String place_type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlaceName() {
            return place_name;
        }

        public void setPlaceName(String place_name) {
            this.place_name = place_name;
        }

        public List<Double> getCenter() {
            return center;
        }

        public void setCenter(List<Double> center) {
            this.center = center;
        }

        public String getPlaceType() {
            return place_type;
        }

        public void setPlaceType(String place_type) {
            this.place_type = place_type;
        }

        public double getLongitude() {
            return center != null && center.size() >= 1 ? center.get(0) : 0;
        }

        public double getLatitude() {
            return center != null && center.size() >= 2 ? center.get(1) : 0;
        }
    }
}
