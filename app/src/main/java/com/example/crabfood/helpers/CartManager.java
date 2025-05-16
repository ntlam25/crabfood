package com.example.crabfood.helpers;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public boolean addToCart(Context context,FoodResponse food, int quantity, List<OptionChoiceResponse> selectedOptions) {
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
    public static boolean areOptionsEqual(List<OptionChoiceResponse> options1, List<OptionChoiceResponse> options2) {
        if (options1 == null && options2 == null) return true;
        if (options1 == null || options2 == null) return false;
        if (options1.size() != options2.size()) return false;


        Set<Long> ids1 = options1.stream()
                .map(OptionChoiceResponse::getId)
                .collect(Collectors.toSet());

        Set<Long> ids2 = options2.stream()
                .map(OptionChoiceResponse::getId)
                .collect(Collectors.toSet());

        return ids1.equals(ids2);
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