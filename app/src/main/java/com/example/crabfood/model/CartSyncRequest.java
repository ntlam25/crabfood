package com.example.crabfood.model;

import java.util.List;


import com.example.crabfood.model.OptionChoiceResponse;

import java.util.List;
import java.util.Map;

public class CartSyncRequest {
    private List<CartItemEntity> items;
    public List<CartItemEntity> getItems() {
        return items;
    }

    public void setItems(List<CartItemEntity> items) {
        this.items = items;
    }
}