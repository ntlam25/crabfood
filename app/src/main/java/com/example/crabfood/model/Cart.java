package com.example.crabfood.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private List<CartItem> items;
    private Long vendorId;

    private Cart() {
        items = new ArrayList<>();
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addItem(CartItem item) {
        // Check if food already exists in cart
        for (int i = 0; i < items.size(); i++) {
            CartItem existingItem = items.get(i);

            // If same food with same choices, just update quantity
            if (existingItem.getFoodId().equals(item.getFoodId())) {
//                    areChoicesEqual(existingItem.getSelectedOptions(), item.getSelectedOptions())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }

        // If not found, add new item
        items.add(item);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
        }
    }

    public void updateItemQuantity(int position, int quantity) {
        if (position >= 0 && position < items.size()) {
            CartItem item = items.get(position);
            if (quantity <= 0) {
                items.remove(position);
            } else {
                item.setQuantity(quantity);
            }
        }
    }

    public void clear() {
        items.clear();
        vendorId = null;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}