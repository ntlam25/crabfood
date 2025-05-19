package com.example.crabfood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemUpcomingOrderBinding;
import com.example.crabfood.helpers.TimeHelper;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.VendorResponse;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class UpcomingOrderAdapter extends ListAdapter<OrderResponse, UpcomingOrderAdapter.ViewHolder> {
    private static final String TAG = "Upcoming Adapter";
    private final OrderActionListener listener;

    public interface OrderActionListener {
        void onCancelClicked(OrderResponse order);
        void onTrackClicked(OrderResponse order);
        void onItemClicked(OrderResponse order);
    }

    public UpcomingOrderAdapter(OrderActionListener listener) {
        super(new OrderDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUpcomingOrderBinding binding = ItemUpcomingOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = getItem(position);
        holder.bind(order);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUpcomingOrderBinding binding;

        public ViewHolder(@NonNull ItemUpcomingOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderResponse order) {
            VendorResponse vendor = order.getVendor();
            // Set restaurant logo
            if (vendor != null) {
                binding.tvRestaurantName.setText(order.getVendor().getName());
                Glide.with(binding.getRoot())
                        .load(vendor.getLogoUrl())
                        .placeholder(R.drawable.avata_placeholder)
                        .error(R.drawable.avatar_error)
                        .into(binding.imgRestaurantLogo);
            }
            // Set order information
            binding.tvItemCount.setText(order.getOrderFoods().size() + " món");
            binding.tvRestaurantName.setText(order.getVendor().getName());
            binding.tvOrderNumber.setText("#" + order.getOrderNumber());

            // Set estimated time
            if (order.getEstimatedDeliveryTime() != null) {
                long minutesRemaining = calculateMinutesRemaining(TimeHelper.parseIsoToLocalDateTime(order.getEstimatedDeliveryTime()));
                binding.tvEstimatedTime.setText(minutesRemaining + " phút");
            }

            // Set status
            binding.tvStatus.setText(order.getOrderStatus().getDisplayName());

            // Setup button actions
            binding.btnCancel.setOnClickListener(v -> listener.onCancelClicked(order));
            binding.btnTrackOrder.setOnClickListener(v -> listener.onTrackClicked(order));
            binding.getRoot().setOnClickListener(v -> listener.onItemClicked(order));
        }


        private long calculateMinutesRemaining(LocalDateTime estimatedTime) {
            long estimatedMillis = estimatedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long nowMillis = System.currentTimeMillis();
            long diffMillis = estimatedMillis - nowMillis;
            return Math.max(0, diffMillis / (60 * 1000));
        }
    }

    public static class OrderDiffCallback extends DiffUtil.ItemCallback<OrderResponse> {
        @Override
        public boolean areItemsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId()) &&
                    oldItem.getOrderStatus().equals(newItem.getOrderStatus());
        }
    }
}
