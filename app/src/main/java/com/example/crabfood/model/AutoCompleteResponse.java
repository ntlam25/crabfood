package com.example.crabfood.model;

import com.example.crabfood.model.mapsbox.AutoComplete;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutoCompleteResponse {
    @SerializedName("predictions")
    private List<AutoComplete> predictions;

    @SerializedName("status")
    private String status;

    public List<AutoComplete> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<AutoComplete> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
