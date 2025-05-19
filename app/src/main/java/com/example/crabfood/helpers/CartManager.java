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

}