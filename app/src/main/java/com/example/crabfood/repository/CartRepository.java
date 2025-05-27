package com.example.crabfood.repository;

import static com.example.crabfood.helpers.CartHelper.areOptionsEqual;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.database.CartDao;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.CartService;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static final String TAG = "CartRepository";
    private final CartDao cartDao;
    private final CartService apiService;
    private final ExecutorService executorService;
    private static CartRepository instance;

    private Long vendorId;

    private CartRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        cartDao = db.cartDao();
        apiService = ApiUtils.getCartService();
        executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context);
        }
        return instance;
    }

    // Local operations
    public LiveData<List<CartItemEntity>> getAllCartItems() {
        return cartDao.getAllCartItems();
    }

    public LiveData<Integer> getCartItemCount() {
        return cartDao.getCartItemCount();
    }

    public LiveData<Integer> getTotalItemQuantity() {
        return cartDao.getTotalItemQuantity();
    }

    public boolean addToCart(Context context, FoodResponse food, int quantity, List<OptionChoiceResponse> selectedOptions) {
        if (vendorId != null && vendorId != food.getVendorId()) {
            Toast.makeText(context, "Không thể thêm món ăn từ nhà hàng khác", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "addToCart: Không thể thêm món ăn từ nhà hàng khác");
            return false;
        }

        if (vendorId == null) {
            vendorId = food.getVendorId();
        }

        executorService.execute(() -> {
            List<CartItemEntity> cartItems = cartDao.getAllCartItemsSync();
            boolean itemExists = false;
            for (CartItemEntity item : cartItems) {
                if (item.getFoodId().equals(food.getId())) {
                    // Check if options are the same
                    if (areOptionsEqual(item.getSelectedOptions(), selectedOptions)) {
                        // Update quantity
                        cartDao.updateItemQuantity(item.getId(), item.getQuantity() + quantity);
                        itemExists = true;
                        break;
                    }
                }
            }

            // If item doesn't exist, add new item
            if (!itemExists) {
                CartItemEntity newItem = new CartItemEntity(
                        food.getId(),
                        food.getName(),
                        food.getImageUrl(),
                        food.getPrice(),
                        quantity,
                        vendorId,
                        new ArrayList<>(selectedOptions)
                );
                cartDao.insertCartItem(newItem);
            }
        });
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean reorder(Context context, OrderResponse order) {
        if (order == null || order.getOrderFoods() == null || order.getOrderFoods().isEmpty()) {
            return false;
        }

        // Check if vendor is the same as current cart
        if (vendorId != null && vendorId != order.getVendorResponse().getId()) {
            Toast.makeText(context, "Không thể thêm món ăn từ nhà hàng khác", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Set vendor ID if cart is empty
        if (vendorId == null) {
            vendorId = order.getVendorResponse().getId();
        }

        // Convert order items to cart items
        List<CartItemEntity> cartItems = order.getOrderFoods().stream()
                .map(orderFood -> {
                    CartItemEntity cartItem = new CartItemEntity();
                    cartItem.setFoodId(orderFood.getFoodId());
                    cartItem.setQuantity(orderFood.getQuantity());
                    cartItem.setPrice(orderFood.getFoodPrice());
                    cartItem.setFoodName(orderFood.getFoodName());
                    cartItem.setImageUrl(orderFood.getFoodImageUrl());
                    cartItem.setVendorId(order.getVendorResponse().getId());
                    cartItem.setSelectedOptions(orderFood.getChoices()
                            .stream().map(choice -> {
                                OptionChoiceResponse option = new OptionChoiceResponse();
                                option.setId(choice.getId());
                                option.setName(choice.getChoiceName());
                                option.setOptionId(choice.getOptionId());
                                option.setOptionName(choice.getOptionName());
                                option.setPriceAdjustment(choice.getPriceAdjustment());
                                return option;
                            }).collect(Collectors.toList()));
                    return cartItem;
                })
                .collect(Collectors.toList());

        // Add items to cart
        for (CartItemEntity item : cartItems) {
            insertCartItem(item);
        }

        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void removeItem(long itemId) {
        executorService.execute(() -> {
            cartDao.deleteCartItemById(itemId);
            vendorId = null;
        });
    }

    public void insertCartItem(CartItemEntity item) {
        executorService.execute(() -> {
            cartDao.insertCartItem(item);
            if (vendorId == null) {
                vendorId = item.getVendorId();
            }
        });
    }

    public void updateItemQuantity(long itemId, int quantity) {
        executorService.execute(() -> {
            if (quantity <= 0) {
                cartDao.deleteCartItemById(itemId);
            } else {
                cartDao.updateItemQuantity(itemId, quantity);
            }
        });
    }

    public void clearCart() {
        executorService.execute(() -> {
            cartDao.clearCart();
            vendorId = null;
            // Notify backend that cart was cleared
            apiService.clearCart().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Cart cleared on backend");
                    } else {
                        Log.e(TAG, "Failed to clear cart on backend");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error clearing cart on backend", t);
                }
            });
        });
    }
}
