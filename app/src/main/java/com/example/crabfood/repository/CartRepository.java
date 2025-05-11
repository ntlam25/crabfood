package com.example.crabfood.repository;

import static com.example.crabfood.helpers.CartManager.areOptionsEqual;
import static com.example.crabfood.helpers.CartManager.deepCopyOptions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.database.CartDao;
import com.example.crabfood.model.CartItem;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.CartSyncRequest;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.service.CartService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public boolean addToCart(Context context, FoodResponse food, int quantity, Map<Long, List<OptionChoiceResponse>> selectedOptions) {
        if (vendorId != null && vendorId != food.getVendorId()) {
            Toast.makeText(context, "Không thể thêm món ăn từ nhà hàng khác", Toast.LENGTH_SHORT).show();
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
                        cartDao.updateItemQuantity(item.getId(),item.getQuantity() + quantity);
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
                        deepCopyOptions(selectedOptions)
                );
                cartDao.insertCartItem(newItem);
            }
        });
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void removeItem(long itemId) {
        executorService.execute(() -> {
            cartDao.deleteCartItemById(itemId);
            vendorId = null;
            syncWithBackend();
        });
    }

    public void updateItemQuantity(long itemId, int quantity) {
        executorService.execute(() -> {
            if (quantity <= 0) {
                cartDao.deleteCartItemById(itemId);
            } else {
                cartDao.updateItemQuantity(itemId, quantity);
            }
            syncWithBackend();
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

    // Backend synchronization
    public void syncWithBackend() {
        executorService.execute(() -> {
            List<CartItemEntity> unsyncedItems = cartDao.getUnsyncedCartItems();
            if (unsyncedItems.isEmpty()) {
                return;
            }

            // Convert entities to sync request
            List<CartSyncRequest.CartItemRequest> itemRequests = new ArrayList<>();
            for (CartItemEntity entity : unsyncedItems) {
                CartSyncRequest.CartItemRequest request = new CartSyncRequest.CartItemRequest(
                        entity.getId(),
                        entity.getFoodId(),
                        entity.getQuantity(),
                        entity.getSelectedOptions()
                );
                itemRequests.add(request);
            }

            CartSyncRequest syncRequest = new CartSyncRequest(itemRequests);
            apiService.syncCart(syncRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        executorService.execute(() -> {
                            // Mark items as synced
                            for (CartItemEntity entity : unsyncedItems) {
                                cartDao.markItemAsSynced(entity.getId());
                            }
                            Log.d(TAG, "Cart synced with backend successfully");
                        });
                    } else {
                        Log.e(TAG, "Failed to sync cart with backend: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error syncing cart with backend", t);
                }
            });
        });
    }

    // Fetch cart from backend and update local database
    public void fetchCartFromBackend() {
        apiService.getCart().enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body() != null) {
                    List<CartItem> serverItems = response.body();
                    executorService.execute(() -> {
                        // Clear existing cart and replace with server data
                        cartDao.clearCart();
                        for (CartItem item : serverItems) {
                            CartItemEntity entity = new CartItemEntity(
                                    item.getFoodId(),
                                    item.getFoodName(),
                                    item.getImageUrl(),
                                    item.getPrice(),
                                    item.getQuantity(),
                                    item.getVendorId(),
                                    item.getSelectedOptions()
                            );
                            entity.setSynced(true);
                            cartDao.insertCartItem(entity);
                        }
                        Log.d(TAG, "Cart updated from backend");
                    });
                } else {
                    Log.e(TAG, "Failed to get cart from backend");
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e(TAG, "Error getting cart from backend", t);
            }
        });
    }
}
