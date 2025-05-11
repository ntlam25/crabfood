package com.example.crabfood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemFoodBinding;
import com.example.crabfood.model.FoodResponse;

public class FoodAdapter extends ListAdapter<FoodResponse, FoodAdapter.FoodViewHolder> {

    private static final String TAG = "Food Adapter";
    private OnFoodClickListener listener;
    private OnFavoriteFoodClickListener favoriteListener;

    public interface OnFavoriteFoodClickListener {
        void onFavoriteClick(FoodResponse food); // optional nếu muốn lắng nghe toggle
    }

    public interface OnFoodClickListener {
        void onFoodClick(FoodResponse food);
    }

    public FoodAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.listener = listener;
    }
    public void setOnFavoriteClickListener(OnFavoriteFoodClickListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    private static final DiffUtil.ItemCallback<FoodResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<FoodResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull FoodResponse oldItem, @NonNull FoodResponse newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FoodResponse oldItem, @NonNull FoodResponse newItem) {
            return oldItem.equals(newItem); // override equals trong FoodResponse để so sánh nội dung
        }
    };

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodResponse food = getItem(position);
        holder.bind(food);
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding;

        public FoodViewHolder(@NonNull ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Log.d(TAG, "FoodViewHolder: click on " + getItem(position).getName());
                    listener.onFoodClick(getItem(position));
                }
            });

            binding.favoriteButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    FoodResponse food = getItem(position);
                    food.setFavorite(!food.isFavorite());
                    notifyItemChanged(position); // hoặc submitList(list.clone()) nếu mutable
                    if (favoriteListener != null) favoriteListener.onFavoriteClick(food);
                }
            });
        }

        public void bind(FoodResponse food) {
            binding.foodName.setText(food.getName());
            binding.itemPrice.setText(String.format("%.2f", food.getPrice()));
            binding.foodRating.setText(String.format("%.1f", food.getRating()));

            Glide.with(binding.getRoot().getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_gallery)
                    .error(R.drawable.error_image)
                    .into(binding.foodImage);

            updateFavoriteIcon(food.isFavorite());

        }

        private void updateFavoriteIcon(boolean isFavorite) {
            binding.favoriteButton.setImageResource(
                    isFavorite ? R.drawable.ic_heart_bold_svg : R.drawable.ic_heart_svg
            );
        }
    }
}
