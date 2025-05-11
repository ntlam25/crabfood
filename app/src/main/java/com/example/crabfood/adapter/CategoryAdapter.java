package com.example.crabfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemCategoryBinding;
import com.example.crabfood.databinding.ItemMoreCategoryBinding;
import com.example.crabfood.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_MORE = 1;

    private final OnCategoryClickListener categoryClickListener;
    private final OnMoreClickListener moreClickListener;
    private List<Object> items = new ArrayList<>();

    public interface OnCategoryClickListener {
        void onCategoryClick(long categoryId, String categoryName);
    }

    public interface OnMoreClickListener {
        void onMoreClick();
    }

    public CategoryAdapter(List<Object> items,
                           OnCategoryClickListener categoryClickListener,
                           OnMoreClickListener moreClickListener
    ) {
        this.categoryClickListener = categoryClickListener;
        this.moreClickListener = moreClickListener;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_MORE : TYPE_CATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            ItemCategoryBinding binding = ItemCategoryBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CategoryViewHolder(binding);
        } else {
            ItemMoreCategoryBinding binding = ItemMoreCategoryBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MoreViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).bind((CategoryResponse) items.get(position),
                    categoryClickListener);
        } else if (holder instanceof MoreViewHolder) {
            ((MoreViewHolder) holder).bind(moreClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CategoryViewHolder) {
            Glide.with(((CategoryViewHolder) holder).binding.getRoot())
                    .clear(((CategoryViewHolder) holder).binding.categoryImage);
        }
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

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        private final ItemMoreCategoryBinding binding;

        public MoreViewHolder(@NonNull ItemMoreCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OnMoreClickListener listener) {
            binding.moreImageView.setImageResource(R.drawable.ic_menu_dots);
            binding.moreTextView.setText("Xem thêm");

            binding.getRoot().setOnClickListener(v -> {
                listener.onMoreClick();
            });
        }
    }

    public void updateData(List<Object> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

}

