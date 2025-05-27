package com.example.crabfood.helpers;

import android.content.Context;

import com.example.crabfood.database.AppDatabase;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.model.OrderFood;
import com.example.crabfood.model.OrderResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartHelper {
    private static CartHelper instance;
    private Long vendorId;
    private final AppDatabase database;

    private CartHelper(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public static synchronized CartHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CartHelper(context);
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

    public static List<CartItemEntity> orderConvertToCart(OrderResponse orderFoods) {
        return orderFoods.getOrderFoods().stream()
                .map(orderFood -> {
                    CartItemEntity cartItem = new CartItemEntity();
                    cartItem.setId(orderFood.getId());
                    cartItem.setFoodId(orderFood.getFoodId());
                    cartItem.setQuantity(orderFood.getQuantity());
                    cartItem.setPrice(orderFood.getFoodPrice());
                    cartItem.setFoodName(orderFood.getFoodName());
                    cartItem.setImageUrl(orderFood.getFoodImageUrl());
                    cartItem.setVendorId(orderFoods.getVendorResponse().getId());
                    cartItem.setSelectedOptions(orderFood.getChoices()
                            .stream().map(
                                    choice -> {
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
    }

}