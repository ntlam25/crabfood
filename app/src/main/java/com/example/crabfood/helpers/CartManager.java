package com.example.crabfood.helpers;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Long vendorId;
    private final AppDatabase database;

    private CartManager(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public boolean addToCart(Context context,FoodResponse food, int quantity, Map<Long, List<OptionChoiceResponse>> selectedOptions) {
        if (vendorId != null && vendorId != food.getVendorId()) {
            Toast.makeText(context, "Không thể thêm món ăn từ nhà hàng khác", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (vendorId == null) {
            vendorId = food.getVendorId();
        }

        CartItemEntity existingItem = database.cartDao().getCartItemByFoodId(food.getId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            database.cartDao().updateCartItem(existingItem);
        } else {
            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setFoodId(food.getId());
            cartItem.setFoodName(food.getName());
            cartItem.setPrice(food.getPrice());
            cartItem.setImageUrl(food.getImageUrl());
            cartItem.setVendorId(food.getVendorId());
            cartItem.setSelectedOptions(selectedOptions);
            cartItem.setQuantity(quantity);

            database.cartDao().insertCartItem(cartItem);
        }
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void removeFromCart(CartItemEntity cartItem) {
        database.cartDao().deleteCartItem(cartItem);

        if (database.cartDao().getAllCartItemsSync().isEmpty()) {
            vendorId = null;
        }
    }

    // Helper method to compare two option maps
    public static boolean areOptionsEqual(Map<Long, List<OptionChoiceResponse>> options1, Map<Long, List<OptionChoiceResponse>> options2) {
        if (options1 == null && options2 == null) return true;
        if (options1 == null || options2 == null) return false;
        if (options1.size() != options2.size()) return false;

        for (Map.Entry<Long, List<OptionChoiceResponse>> entry : options1.entrySet()) {
            Long optionId = entry.getKey();
            List<OptionChoiceResponse> choices1 = entry.getValue();

            if (!options2.containsKey(optionId)) return false;

            List<OptionChoiceResponse> choices2 = options2.get(optionId);
            if (choices1.size() != choices2.size()) return false;

            // Compare each choice by ID
            for (OptionChoiceResponse choice : choices1) {
                boolean found = false;
                for (OptionChoiceResponse otherChoice : choices2) {
                    if (choice.getId().equals(otherChoice.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }

        return true;
    }

    // Create a deep copy of the options map to avoid reference issues
    public static Map<Long, List<OptionChoiceResponse>> deepCopyOptions(Map<Long, List<OptionChoiceResponse>> original) {
        if (original == null) return null;

        Map<Long, List<OptionChoiceResponse>> copy = new HashMap<>();
        for (Map.Entry<Long, List<OptionChoiceResponse>> entry : original.entrySet()) {
            List<OptionChoiceResponse> choicesCopy = new ArrayList<>();
            for (OptionChoiceResponse choice : entry.getValue()) {
                // Since Choice is serializable, we can use Gson for deep copy
                Gson gson = new Gson();
                choicesCopy.add(gson.fromJson(gson.toJson(choice), OptionChoiceResponse.class));
            }
            copy.put(entry.getKey(), choicesCopy);
        }

        return copy;
    }

    public void updateQuantity(CartItemEntity cartItem, int newQuantity) {
        cartItem.setQuantity(newQuantity);
        database.cartDao().updateItemQuantity(cartItem.getId(), newQuantity);
    }

    public List<CartItemEntity> getCartItems() {
        return database.cartDao().getAllCartItemsSync();
    }

    public LiveData<List<CartItemEntity>> getCartItemsLiveData() {
        return database.cartDao().getAllCartItems();
    }

    public long getCurrentVendorId() {
        return vendorId;
    }

    public void clearCart() {
        database.cartDao().clearCart();
        vendorId = null;
    }

    public boolean canAddCartItem(long vendorId) {
        return this.vendorId == null || this.vendorId == vendorId;
    }
}