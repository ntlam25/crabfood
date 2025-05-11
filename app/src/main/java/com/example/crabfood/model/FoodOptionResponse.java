package com.example.crabfood.model;

import java.util.List;

public class FoodOptionResponse {
    private Long optionId;
    private String name;
    private int minSelection;
    private int maxSelection;
    private List<OptionChoiceResponse> choices;
    private boolean required;

    public FoodOptionResponse() {
    }
    public FoodOptionResponse(Long optionId, String name, int minSelection, int maxSelection, List<OptionChoiceResponse> choices, boolean required) {
        this.optionId = optionId;
        this.name = name;
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.choices = choices;
        this.required = required;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(int minSelection) {
        this.minSelection = minSelection;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public List<OptionChoiceResponse> getChoices() {
        return choices;
    }

    public void setChoices(List<OptionChoiceResponse> choices) {
        this.choices = choices;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}

