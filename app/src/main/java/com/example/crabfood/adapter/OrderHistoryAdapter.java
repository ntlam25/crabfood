package com.example.crabfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemOrderHistoryBinding;
import com.example.crabfood.helpers.TimeHelper;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.model.enums.OrderStatus;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class OrderHistoryAdapter extends ListAdapter<OrderResponse, OrderHistoryAdapter.ViewHolder> {
    private final OrderHistoryListener listener;

    public interface OrderHistoryListener {
        void onRateClicked(OrderResponse order);
        void onReorderClicked(OrderResponse order);
    }

    public OrderHistoryAdapter(OrderHistoryListener listener) {
        super(new OrderDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = getItem(position);
        holder.bind(order);
    }

    class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public ViewHolder(@NonNull ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderResponse order) {
            VendorResponse vendor = order.getVendor();
            if (vendor != null) {
                binding.tvRestaurantName.setText(vendor.getName());
                Glide.with(binding.getRoot())
                        .load(vendor.getLogoUrl())
                        .placeholder(R.drawable.avata_placeholder)
                        .error(R.drawable.avatar_error)
                        .into(binding.imgRestaurantLogo);
            }

            // Thời gian giao hàng thực tế
            if (order.getActualDeliveryTime() != null) {
                binding.tvOrderDate.setText(TimeHelper
                        .formatLocalDateTime(Objects.requireNonNull(TimeHelper
                                .parseIsoToLocalDateTime(order.getActualDeliveryTime()))));
            }

            binding.tvOrderNumber.setText(order.getOrderNumber());
            binding.tvItemCount.setText(order.getOrderFoods().size() + " món");
            binding.tvOrderStatus.setText(order.getOrderStatus().getDisplayName());
            if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                binding.btnRate.setEnabled(false);
            } else {
                binding.btnRate.setOnClickListener(v -> listener.onRateClicked(order));
                binding.btnRate.setEnabled(true);
            }
            if (!order.getOrderStatus().name().equals("DELIVERED")) {
                binding.statusDot.setBackgroundResource(R.drawable.circle_red);
            } else {
                binding.statusDot.setBackgroundResource(R.drawable.circle_green);
            }

            binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f đ", order.getTotalAmount()));

            binding.btnReorder.setOnClickListener(v -> listener.onReorderClicked(order));
        }
    }

    public static class OrderDiffCallback extends DiffUtil.ItemCallback<OrderResponse> {
        @Override
        public boolean areItemsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return oldItem.equals(newItem);
        }
    }
}
