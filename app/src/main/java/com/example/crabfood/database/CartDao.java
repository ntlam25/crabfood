package com.example.crabfood.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.crabfood.model.CartItemEntity;

import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCartItem(CartItemEntity cartItem);

    @Update
    void updateCartItem(CartItemEntity cartItem);

    @Delete
    void deleteCartItem(CartItemEntity cartItem);

    @Query("DELETE FROM cart_items WHERE id = :id")
    void deleteCartItemById(long id);

    @Query("DELETE FROM cart_items")
    void clearCart();

    @Query("SELECT * FROM cart_items ORDER BY lastUpdated DESC")
    LiveData<List<CartItemEntity>> getAllCartItems();

    @Query("SELECT * FROM cart_items ORDER BY lastUpdated DESC")
    List<CartItemEntity> getAllCartItemsSync();

    @Query("SELECT * FROM cart_items WHERE isSynced = 0")
    List<CartItemEntity> getUnsyncedCartItems();

    @Query("UPDATE cart_items SET isSynced = 1 WHERE id = :id")
    void markItemAsSynced(long id);

    @Query("SELECT COUNT(*) FROM cart_items")
    LiveData<Integer> getCartItemCount();

    @Query("SELECT SUM(quantity) FROM cart_items")
    LiveData<Integer> getTotalItemQuantity();

    @Query("SELECT * FROM cart_items WHERE foodId = :foodId")
    CartItemEntity getCartItemByFoodId(long foodId);

    @Query("UPDATE cart_items SET quantity = :quantity, isSynced = 0 WHERE id = :id")
    void updateItemQuantity(long id, int quantity);
}

