package com.example.crabfood.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.Cart;
import com.example.crabfood.model.CartItem;

import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isCartEmpty = new MutableLiveData<>(true);

    public CartViewModel() {
        refreshCartData();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Boolean> getIsCartEmpty() {
        return isCartEmpty;
    }

    public void refreshCartData() {
        Cart cart = Cart.getInstance();
        List<CartItem> items = cart.getItems();

        cartItems.setValue(items);
        totalPrice.setValue(cart.getTotalPrice());
        isCartEmpty.setValue(items.isEmpty());
    }

    public void updateItemQuantity(int position, int quantity) {
        Cart.getInstance().updateItemQuantity(position, quantity);
        refreshCartData();
    }

    public void removeItem(int position) {
        Cart.getInstance().removeItem(position);
        refreshCartData();
    }

    public void clearCart() {
        Cart.getInstance().clear();
        refreshCartData();
    }
}