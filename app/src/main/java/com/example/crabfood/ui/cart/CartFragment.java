package com.example.crabfood.ui.cart;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.crabfood.R;
import com.example.crabfood.adapter.CartAdapter;
import com.example.crabfood.databinding.FragmentCartBinding;
import com.example.crabfood.model.CartItemEntity;

import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private FragmentCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter cartAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupCheckoutButton();
        setupSwipeRefresh();
        observeViewModel();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshCartData();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_cart);
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_clear_cart) {
                showClearCartDialog();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(null, this);
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCart.setAdapter(cartAdapter);
    }

    private void setupCheckoutButton() {
        binding.buttonCheckout.setOnClickListener(v -> {
            // Ensure cart is synced before proceeding to checkout
            Navigation.findNavController(requireView()).navigate(R.id.action_cartFragment_to_checkoutFragment);
        });
    }

    private void observeViewModel() {
        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            cartAdapter.updateCartItems(cartItems);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            binding.textViewSubtotal.setText(String.format("%,.0f đ", totalPrice));
            binding.textViewTotal.setText(String.format("%,.0f đ", totalPrice));
        });

        viewModel.getIsCartEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty) {
                binding.emptyCartLayout.setVisibility(View.VISIBLE);
                binding.recyclerViewCart.setVisibility(View.GONE);
                binding.bottomLayout.setVisibility(View.GONE);
            } else {
                binding.emptyCartLayout.setVisibility(View.GONE);
                binding.recyclerViewCart.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showClearCartDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa giỏ hàng")
                .setMessage(R.string.cart_clear_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    viewModel.clearCart();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onQuantityChanged(long itemId, int quantity) {
        viewModel.updateItemQuantity(itemId, quantity);
    }

    @Override
    public void onRemoveItem(long itemId) {
        viewModel.removeItem(itemId);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment becomes visible
        viewModel.refreshCartData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}