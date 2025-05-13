package com.example.crabfood.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.CartSyncRequest;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Create a deep copy of the options map to avoid reference issues
//    public static List<OptionChoiceResponse> deepCopyOptions(List<OptionChoiceResponse> original) {
//        if (original == null) return null;
//
//        List<OptionChoiceResponse> copy = new HashMap<>();
//        for (Map.Entry<Long, List<OptionChoiceResponse>> entry : original.entrySet()) {
//            List<OptionChoiceResponse> choicesCopy = new ArrayList<>();
//            for (OptionChoiceResponse choice : entry.getValue()) {
//                // Since Choice is serializable, we can use Gson for deep copy
//                Gson gson = new Gson();
//                choicesCopy.add(gson.fromJson(gson.toJson(choice), OptionChoiceResponse.class));
//            }
//            copy.put(entry.getKey(), choicesCopy);
//        }
//
//        return copy;
//    }

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

//    public void syncWithBackend() {
//        executorService.execute(() -> {
//            // Lấy tất cả các item chưa được đồng bộ
//            List<CartItemEntity> unSyncedItems = cartDao.getUnSyncedCartItems();
//            if (unSyncedItems.isEmpty()) {
//                Log.d(TAG, "No unSynced items to sync");
//                return;
//            }
//
//            // Tạo request object
//            CartSyncRequest syncRequest = new CartSyncRequest();
//            syncRequest.setItems(unSyncedItems);
//            Log.d(TAG, "syncWithBackend: " + syncRequest);
//            // Gọi API đồng bộ
//            apiService.syncCart(syncRequest).enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    if (response.isSuccessful()) {
//                        Log.d(TAG, "Cart synced successfully with backend");
//                        executorService.execute(() -> {
//                            // Đánh dấu các item đã được đồng bộ
//                            for (CartItemEntity item : unSyncedItems) {
//                                cartDao.markItemAsSynced(item.getId());
//                            }
//                        });
//                    } else {
//                        Log.e(TAG, "Failed to sync cart with backend: " + response.code());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    Log.e(TAG, "Error syncing cart with backend", t);
//                }
//            });
//        });
//    }
//
//    public void fetchCartFromBackend() {
//        apiService.getCart().enqueue(new Callback<List<CartItemEntity>>() {
//            @Override
//            public void onResponse(Call<List<CartItemEntity>> call, Response<List<CartItemEntity>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<CartItemEntity> serverItems = response.body();
//                    Log.d(TAG, "Fetched " + serverItems.size() + " items from backend");
//
//                    executorService.execute(() -> {
//                        // Xử lý đồng bộ từ server về local
//                        // Nếu giỏ hàng local đang trống, thay thế hoàn toàn
//                        Integer count = cartDao.getCartItemCountSync();
//                        if (count == 0 || count == null) {
//                            cartDao.clearCart();
//                            if (serverItems.size() > 0) {
//                                vendorId = serverItems.get(0).getVendorId();
//                            }
//                            for (CartItemEntity item : serverItems) {
//                                item.setSynced(true);
//                                cartDao.insertCartItem(item);
//                            }
//                        } else {
//                            // Merge giỏ hàng
//                            mergeCartItems(serverItems);
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "Failed to fetch cart from backend: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<CartItemEntity>> call, Throwable t) {
//                Log.e(TAG, "Error fetching cart from backend", t);
//            }
//        });
//    }
//
//    private void mergeCartItems(List<CartItemEntity> serverItems) {
//        List<CartItemEntity> localItems = cartDao.getAllCartItemsSync();
//
//        // Kiểm tra vendorId
//        if (!serverItems.isEmpty() && !localItems.isEmpty()) {
//            Long serverVendorId = serverItems.get(0).getVendorId();
//            Long localVendorId = localItems.get(0).getVendorId();
//
//            if (!serverVendorId.equals(localVendorId)) {
//                // Nếu khác vendorId, ưu tiên data local
//                // Đồng bộ data local lên server
//                syncWithBackend();
//                return;
//            }
//        }
//
//        // Tạo map từ serverItems để dễ tra cứu
//        Map<Long, CartItemEntity> serverItemMap = new HashMap<>();
//        for (CartItemEntity item : serverItems) {
//            serverItemMap.put(item.getFoodId(), item);
//        }
//
//        // Duyệt qua từng item local
//        for (CartItemEntity localItem : localItems) {
//            CartItemEntity serverItem = serverItemMap.get(localItem.getFoodId());
//            if (serverItem != null) {
//                // Nếu item tồn tại ở cả hai nơi
//                if (localItem.getLastUpdated().after(serverItem.getLastUpdated())) {
//                    // Local mới hơn, giữ local
//                    serverItemMap.remove(localItem.getFoodId());
//                } else {
//                    // Server mới hơn, xóa local để thêm server
//                    cartDao.deleteCartItemById(localItem.getId());
//                }
//            }
//        }
//
//        // Thêm các item từ server vào local
//        for (CartItemEntity serverItem : serverItemMap.values()) {
//            serverItem.setSynced(true);
//            cartDao.insertCartItem(serverItem);
//        }
//    }
}