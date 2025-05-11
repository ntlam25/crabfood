package com.example.crabfood.model;

public class OptionChoiceResponse {
    private Long id;
    private String name;
    private double priceAdjustment;
    private boolean isDefault;

    public OptionChoiceResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(double priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OptionChoiceResponse other = (OptionChoiceResponse) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
