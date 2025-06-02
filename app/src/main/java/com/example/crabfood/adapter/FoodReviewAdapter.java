package com.example.crabfood.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.model.FoodReviewRequest;
import com.example.crabfood.model.FoodReviewResponse;
import com.example.crabfood.model.OrderFood;

import java.util.ArrayList;
import java.util.List;

public class FoodReviewAdapter extends RecyclerView.Adapter<FoodReviewAdapter.ViewHolder> {
    private List<OrderFood> orderItems;
    private List<FoodReviewResponse> foodReviews;

    private boolean itemsEnabled = true;

    // Phương thức để bật/tắt items
    public void setItemsEnabled(boolean enabled) {
        this.itemsEnabled = enabled;
        notifyDataSetChanged(); // Cập nhật giao diện
    }

    public FoodReviewAdapter(List<OrderFood> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        this.foodReviews = new ArrayList<>();

        // Initialize food reviews for each order item
        for (OrderFood item : this.orderItems) {
            FoodReviewResponse review = new FoodReviewResponse();
            review.setFoodId(item.getFoodId());
            this.foodReviews.add(review);
        }
    }

    public void setFoodReviews(List<FoodReviewResponse> foodReviewResponse) {
        if (foodReviewResponse != null && foodReviewResponse.size() == orderItems.size()) {
            this.foodReviews = foodReviewResponse;
        } else {
            this.foodReviews.clear();
            for (OrderFood item : orderItems) {
                FoodReviewResponse review = new FoodReviewResponse();
                review.setFoodId(item.getFoodId());
                this.foodReviews.add(review);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderFood orderItem = orderItems.get(position);
        FoodReviewResponse foodReview = foodReviews.get(position);

        holder.bind(orderItem, foodReview, position);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }


    public List<FoodReviewResponse> getFoodReviews() {
        List<FoodReviewResponse> validReviews = new ArrayList<>();
        for (FoodReviewResponse review : foodReviews) {
            if (review.getRating() != null && review.getRating() > 0) {
                validReviews.add(review);
            }
        }
        return validReviews;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;
        private TextView foodPrice;
        private TextView quantity;
        private RatingBar ratingBar;
        private EditText commentEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            quantity = itemView.findViewById(R.id.quantity);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            commentEdit = itemView.findViewById(R.id.commentEdit);
        }

        public void bind(OrderFood orderItem, FoodReviewResponse foodReview, int position) {
            // Load food image
            Glide.with(itemView.getContext())
                    .load(orderItem.getFoodImageUrl())
                    .placeholder(R.drawable.food_image)
                    .error(R.drawable.error_image)
                    .into(foodImage);

            // Set food details
            foodName.setText(orderItem.getFoodName());
            foodPrice.setText(String.format("%,.0f đ", orderItem.getFoodPrice()));
            quantity.setText("SL: " + orderItem.getQuantity());

            // Set current rating if exists
            if (foodReview.getRating() != null) {
                ratingBar.setRating(foodReview.getRating().floatValue());
                ratingBar.setIsIndicator(true);
            } else {
                ratingBar.setRating(0);
            }

            // Set current comment if exists
            if (foodReview.getComment() != null || foodReview.getRating() != null) {
                commentEdit.setText(foodReview.getComment());
                commentEdit.setFocusable(false);
                commentEdit.setFocusableInTouchMode(false);
                commentEdit.setClickable(false);
                commentEdit.setLongClickable(false);
                commentEdit.setCursorVisible(false);
            } else {
                commentEdit.setText("");
                commentEdit.setFocusable(true);
                commentEdit.setFocusableInTouchMode(true);
                commentEdit.setClickable(true);
                commentEdit.setLongClickable(true);
                commentEdit.setCursorVisible(true);
            }

            // Enable rating bar and comment edit text
            ratingBar.setEnabled(true);
            commentEdit.setEnabled(true);

            if (!itemsEnabled) {
                ratingBar.setEnabled(false);
                commentEdit.setEnabled(false);
            }


            // Setup rating change listener
            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    foodReviews.get(position).setRating((double) rating);
                }
            });

            // Setup comment change listener
            commentEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String comment = s.toString().trim();
                    foodReviews.get(position).setComment(comment.isEmpty() ? null : comment);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}