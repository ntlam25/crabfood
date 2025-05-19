package com.example.crabfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemCartBinding;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.OptionChoiceResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemEntity> cartItems;
    private final CartItemListener listener;
    private boolean canAction = true;

    public interface CartItemListener {
        void onQuantityChanged(long itemId, int quantity);

        void onRemoveItem(long itemId);
    }

    public CartAdapter(List<CartItemEntity> cartItems, CartItemListener listener, boolean canAction) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.canAction = canAction;
    }

    public void updateCartItems(List<CartItemEntity> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        if (cartItems != null && position < cartItems.size()) {
            holder.bind(cartItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        public CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItemEntity item) {
            Context context = binding.getRoot().getContext();

            // Set basic item information
            binding.textViewFoodName.setText(item.getFoodName());
            binding.textViewFoodPrice.setText(String.format(Locale.getDefault(), "%,.0f đ", item.getPrice()));
            binding.textViewQuantity.setText(String.valueOf(item.getQuantity()));
            binding.textViewTotalPrice.setText(String.format(Locale.getDefault(), "%,.0f đ", item.getTotalPrice()));

            // Set sync status indicator
//            binding.syncIndicator.setVisibility(item.isSynced() ? View.GONE : View.VISIBLE);

            // Load image
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.dessert_image)
                        .error(R.drawable.error_image)
                        .into(binding.imageViewFood);
            } else {
                binding.imageViewFood.setImageResource(R.drawable.dessert_image);
            }

            // Setup options text
            StringBuilder optionsText = new StringBuilder();
            List<OptionChoiceResponse> options = item.getSelectedOptions();
            if (options != null && !options.isEmpty()) {
                for (OptionChoiceResponse choice : options) {
                    optionsText.append("• ").append(choice.getName());
                    if (choice.getPriceAdjustment() > 0) {
                        optionsText.append(" (+").append(String.format(Locale.getDefault(), "%,.0f đ", choice.getPriceAdjustment())).append(")");
                    }
                    optionsText.append("\n");
                }
                binding.textViewOptions.setText(optionsText.toString().trim());
                binding.textViewOptions.setVisibility(View.VISIBLE);
            } else {
                binding.textViewOptions.setVisibility(View.GONE);
            }

            // Setup buttons
            binding.buttonIncrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                listener.onQuantityChanged(item.getId(), newQuantity);
            });

            binding.buttonDecrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity <= 0) {
                    newQuantity = 1;
                }
                listener.onQuantityChanged(item.getId(), newQuantity);
            });

            binding.buttonRemove.setOnClickListener(v -> listener.onRemoveItem(item.getId()));
            if (!canAction) {
                binding.buttonDecrease.setVisibility(View.GONE);
                binding.buttonIncrease.setVisibility(View.GONE);
                binding.textViewQuantity.setText("Số lượng: " + item.getQuantity());
                binding.buttonRemove.setVisibility(View.GONE);
            }
        }
    }
}