package com.example.crabfood.model;

import java.util.List;


import com.example.crabfood.model.OptionChoiceResponse;

import java.util.List;
import java.util.Map;

public class CartSyncRequest {
    private List<CartItemRequest> items;

    public CartSyncRequest(List<CartItemRequest> items) {
        this.items = items;
    }

    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }

    public static class CartItemRequest {
        private long id;
        private Long foodId;
        private int quantity;
        private Map<Long, List<OptionChoiceResponse>> selectedOptions;

        public CartItemRequest(long id, Long foodId, int quantity, Map<Long, List<OptionChoiceResponse>> selectedOptions) {
            this.id = id;
            this.foodId = foodId;
            this.quantity = quantity;
            this.selectedOptions = selectedOptions;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Long getFoodId() {
            return foodId;
        }

        public void setFoodId(Long foodId) {
            this.foodId = foodId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Map<Long, List<OptionChoiceResponse>> getSelectedOptions() {
            return selectedOptions;
        }

        public void setSelectedOptions(Map<Long, List<OptionChoiceResponse>> selectedOptions) {
            this.selectedOptions = selectedOptions;
        }
    }
}