package com.example.crabfood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemVendorBinding;
import com.example.crabfood.model.VendorResponse;

import java.util.ArrayList;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private static final String TAG = "VendorAdapter";
    private final OnFavoriteClick favoriteClick;
    private final OnVendorClick vendorClick;
    private List<VendorResponse> items;

    public VendorAdapter(List<VendorResponse> items, OnFavoriteClick favoriteClick, OnVendorClick vendorClick) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
        this.favoriteClick = favoriteClick;
        this.vendorClick = vendorClick;
    }

    public interface OnFavoriteClick{
        void onFavoriteClick(VendorResponse vendor, int position);
    }
    public interface OnVendorClick{
        void onVendorClick(VendorResponse vendor, int position);
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVendorBinding binding = ItemVendorBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false);
        return new VendorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    public void setVendors(List<VendorResponse> vendors) {
        this.items = new ArrayList<>(vendors);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0 ;
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {
        private final ItemVendorBinding binding;

        public VendorViewHolder(@NonNull ItemVendorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(VendorResponse vendor, int position) {
            if (vendor == null) {
                Log.e(TAG, "Attempted to bind null vendor");
                return;
            }

            // Set text với fallback value
            binding.vendorName.setText(vendor.getName() != null ? vendor.getName() : "N/A");
            binding.vendorCuisineType.setText(vendor.getCuisineType() != null ? vendor.getCuisineType() : "Không xác định");

            // Set favorite icon based on vendor's favorite status
            binding.favoriteButton.setImageResource(vendor.isFavorite() ?
                    R.drawable.ic_heart_bold_svg : R.drawable.ic_heart_svg);

            // Sử dụng try-catch để xử lý các trường hợp ngoại lệ
            try {
                double distance = vendor.getDistance();
                binding.vendorDistance.setText(String.format("%.2f km", distance));
            } catch (Exception e) {
                Log.e(TAG, "Error setting distance", e);
                binding.vendorDistance.setText("? km");
            }

            try {
                double rating = vendor.getRating();
                binding.vendorRating.setText(String.format("%.1f", rating));
            } catch (Exception e) {
                Log.e(TAG, "Error setting rating", e);
                binding.vendorRating.setText("?");
            }

            // Load ảnh với xử lý lỗi
            try {
                String imageUrl = vendor.getCoverImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(binding.getRoot().getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_gallery)
                            .error(R.drawable.error_image)
                            .into(binding.vendorImage);
                } else {
                    // Use placeholder directly if URL is null or empty
                    binding.vendorImage.setImageResource(R.drawable.ic_gallery);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
                binding.vendorImage.setImageResource(R.drawable.error_image);
            }

            binding.favoriteButton.setOnClickListener(v -> {
                if (favoriteClick != null) {
                    favoriteClick.onFavoriteClick(vendor, position);
                }
            });
            binding.getRoot().setOnClickListener(v -> {
                if (vendorClick != null) {
                    vendorClick.onVendorClick(vendor, position);
                }
            });
        }
    }
}