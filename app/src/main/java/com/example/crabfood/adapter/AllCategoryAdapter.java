package com.example.crabfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemCategoryBinding;
import com.example.crabfood.databinding.ItemMoreCategoryBinding;
import com.example.crabfood.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener categoryClickListener;
    private List<CategoryResponse> items = new ArrayList<>();

    public interface OnCategoryClickListener {
        void onCategoryClick(long categoryId, String categoryName);
    }

    public AllCategoryAdapter(List<CategoryResponse> items,
                              OnCategoryClickListener categoryClickListener
    ) {
        this.categoryClickListener = categoryClickListener;
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind((CategoryResponse) items.get(position),
                categoryClickListener);
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }


    @Override
    public void onViewRecycled(@NonNull CategoryViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(((CategoryViewHolder) holder).binding.getRoot())
                .clear(((CategoryViewHolder) holder).binding.categoryImage);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoryResponse category, OnCategoryClickListener listener) {
            binding.categoryName.setText(category.getName());

            // Xử lý ảnh an toàn
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot())
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.error_image)
                        .into(binding.categoryImage);
            } else {
                binding.categoryImage.setImageResource(R.drawable.ic_menu);
            }

            binding.getRoot().setOnClickListener(v -> {
                listener.onCategoryClick(category.getId(), category.getName());
            });
        }
    }

    public void updateData(List<CategoryResponse> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

}

