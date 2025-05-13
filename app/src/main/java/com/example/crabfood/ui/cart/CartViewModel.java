package com.example.crabfood.ui.cart;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.repository.CartRepository;

import java.util.List;
import java.util.Map;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final LiveData<List<CartItemEntity>> cartItems;
    private final MediatorLiveData<Double> totalPrice = new MediatorLiveData<>();
    private final MediatorLiveData<Long> currentVendorId = new MediatorLiveData<>();
    private final LiveData<Boolean> isCartEmpty;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = CartRepository.getInstance(application);
        cartItems = cartRepository.getAllCartItems();

        isCartEmpty = Transformations.map(cartRepository.getCartItemCount(), count -> count == null || count == 0);

        // Calculate total price whenever cart items change
        totalPrice.addSource(cartItems, items -> {
            double total = 0;
            if (items != null) {
                for (CartItemEntity item : items) {
                    total += item.getTotalPrice();
                }
            }
            totalPrice.setValue(total);
        });

        currentVendorId.addSource(cartItems, items -> {
            if (items != null && !items.isEmpty()) {
                currentVendorId.setValue(items.get(0).getVendorId());
            } else {
                currentVendorId.setValue(null);
            }
        });

        // Initial fetch from backend
//        refreshCartData();
    }

    public LiveData<List<CartItemEntity>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Boolean> getIsCartEmpty() {
        return isCartEmpty;
    }

    public LiveData<Long> getCurrentVendorIdLive() {
        return currentVendorId;
    }

    public boolean addToCart(Context context, FoodResponse food, int quantity, List<OptionChoiceResponse> selectedOptions) {
        return cartRepository.addToCart(context, food, quantity, selectedOptions);
    }

    public void refreshCartData() {
        cartRepository.getAllCartItems();
    }

    public void updateItemQuantity(long itemId, int quantity) {
        cartRepository.updateItemQuantity(itemId, quantity);
    }

    public void removeItem(long itemId) {
        cartRepository.removeItem(itemId);
    }

    public void clearCart() {
        cartRepository.clearCart();
    }

}
