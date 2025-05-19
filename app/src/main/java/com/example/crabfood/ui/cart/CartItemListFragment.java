package com.example.crabfood.ui.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.crabfood.R;
import com.example.crabfood.adapter.CartAdapter;
import com.example.crabfood.databinding.FragmentCartItemListBinding;
import com.example.crabfood.model.CartItemEntity;

import java.util.ArrayList;
import java.util.List;

public class CartItemListFragment extends Fragment {
    private FragmentCartItemListBinding binding;
    private CartViewModel viewModel;
    private CartAdapter adapter;
    private List<CartItemEntity> cartItems = new ArrayList<>();
    private boolean canAction = false;

    public CartItemListFragment(boolean canAction) {
        this.canAction = canAction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartItemListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        if (canAction) {
            adapter = new CartAdapter(cartItems, new CartAdapter.CartItemListener() {
                @Override
                public void onQuantityChanged(long itemId, int quantity) {
                    viewModel.updateItemQuantity(itemId, quantity);
                }

                @Override
                public void onRemoveItem(long itemId) {
                    viewModel.removeItem(itemId);
                }
            }, true);
        } else {
            adapter = new CartAdapter(cartItems, null, true);
        }

        binding.recyclerViewCart.setAdapter(adapter);
        updateVisibility();
    }

    private void updateVisibility() {
        if (binding.tvNoCartItem != null) {
            binding.tvNoCartItem.setVisibility((cartItems.isEmpty()) ? View.VISIBLE : View.GONE);
        }
    }

    public void setCartItems(List<CartItemEntity> cartItems) {
        if (cartItems != null) {
            this.cartItems.clear();
            this.cartItems.addAll(cartItems);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment becomes visible
        viewModel.refreshCartData();
    }

}