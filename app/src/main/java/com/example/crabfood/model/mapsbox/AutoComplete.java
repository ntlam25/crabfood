package com.example.crabfood.model.mapsbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutoComplete {
    @SerializedName("place_id")
    private String placeId;

    @SerializedName("description")
    private String description;

    @SerializedName("matched_substrings")
    List<Object> matchedSubstrings;

    String reference;

    @SerializedName("structured_formatting")
    private StructuredFormatting structuredFormatting;

    @SerializedName("terms")
    List<Object> terms;

    @SerializedName("has_children")
    boolean hasChildren;

    @SerializedName("display_type")
    String displayType;

    @SerializedName("score")
    Double score;

    @SerializedName("plus_code")
    PlusCode plusCode;

    @SerializedName("geometry")
    private Geometry geometry;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Object> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    public void setMatchedSubstrings(List<Object> matchedSubstrings) {
        this.matchedSubstrings = matchedSubstrings;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public StructuredFormatting getStructuredFormatting() {
        return structuredFormatting;
    }

    public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
        this.structuredFormatting = structuredFormatting;
    }

    public List<Object> getTerms() {
        return terms;
    }

    public void setTerms(List<Object> terms) {
        this.terms = terms;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Double getLatitude() {
        return geometry != null && geometry.getLocation() != null ? geometry.getLocation().getLat() : null;
    }

    public Double getLongitude() {
        return geometry != null && geometry.getLocation() != null ? geometry.getLocation().getLng() : null;
    }

    public static class StructuredFormatting {
        @SerializedName("main_text")
        private String mainText;

        @SerializedName("secondary_text")
        private String secondaryText;

        public String getMainText() {
            return mainText;
        }

        public void setMainText(String mainText) {
            this.mainText = mainText;
        }

        public String getSecondaryText() {
            return secondaryText;
        }

        public void setSecondaryText(String secondaryText) {
            this.secondaryText = secondaryText;
        }
    }

    public static class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private Double lat;

        @SerializedName("lng")
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

    class PlusCode{
        @SerializedName("compound_code")
        String compoundCode;

        @SerializedName("global_code")
        String globalCode;
    }
}
