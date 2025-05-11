package com.example.crabfood.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.model.OnboardingItem;

import java.util.List;


public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> onboardingItems;
    private OnSkipClickListener skipClickListener;
    private OnNextClickListener nextClickListener;
    private OnGetStartedClickListener getStartedClickListener;

    public interface OnSkipClickListener {
        void onSkipClick();
    }

    public interface OnNextClickListener {
        void onNextClick(int position);
    }

    public interface OnGetStartedClickListener {
        void onGetStartedClick();
    }

    public OnboardingAdapter(List<OnboardingItem> onboardingItems,
                             OnSkipClickListener skipClickListener,
                             OnNextClickListener nextClickListener,
                             OnGetStartedClickListener getStartedClickListener) {
        this.onboardingItems = onboardingItems;
        this.skipClickListener = skipClickListener;
        this.nextClickListener = nextClickListener;
        this.getStartedClickListener = getStartedClickListener;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_onboarding, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(onboardingItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView iconView;
        private TextView titleView;
        private TextView descriptionView;
        private Button buttonNext;
        private Button buttonGetStarted;
        private Button buttonSkip;
        private LinearLayout indicatorsContainer;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageOnboarding);
            iconView = itemView.findViewById(R.id.iconOnboarding);
            titleView = itemView.findViewById(R.id.titleOnboarding);
            descriptionView = itemView.findViewById(R.id.descriptionOnboarding);
            buttonNext = itemView.findViewById(R.id.buttonNext);
            buttonGetStarted = itemView.findViewById(R.id.buttonGetStarted);
            buttonSkip = itemView.findViewById(R.id.buttonSkip);
            indicatorsContainer = itemView.findViewById(R.id.indicatorsContainer);
        }

        void bind(OnboardingItem onboardingItem, int position) {
            imageView.setImageResource(onboardingItem.getImage());
            iconView.setImageResource(onboardingItem.getIcon());
            titleView.setText(onboardingItem.getTitle());
            descriptionView.setText(onboardingItem.getDescription());

            // Setup indicators
            setupIndicators();
            setCurrentIndicator(position);

            // Show/hide buttons
            if (position == getItemCount() - 1) {
                buttonNext.setVisibility(View.GONE);
                buttonGetStarted.setVisibility(View.VISIBLE);
            } else {
                buttonNext.setVisibility(View.VISIBLE);
                buttonGetStarted.setVisibility(View.GONE);
            }

            // Set click listeners
            buttonSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (skipClickListener != null) {
                        skipClickListener.onSkipClick();
                    }
                }
            });

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nextClickListener != null) {
                        nextClickListener.onNextClick(getAdapterPosition());
                    }
                }
            });

            buttonGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getStartedClickListener != null) {
                        getStartedClickListener.onGetStartedClick();
                    }
                }
            });
        }

        private void setupIndicators() {
            indicatorsContainer.removeAllViews();
            View[] indicators = new View[getItemCount()];

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    80, 20
            );
            layoutParams.setMargins(8, 0, 8, 0);

            for (int i = 0; i < indicators.length; i++) {
                indicators[i] = new View(itemView.getContext());
                indicators[i].setBackgroundResource(R.drawable.indicator_inactive);
                indicators[i].setLayoutParams(layoutParams);
                indicatorsContainer.addView(indicators[i]);
            }
        }

        private void setCurrentIndicator(int index) {
            int childCount = indicatorsContainer.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = indicatorsContainer.getChildAt(i);
                if (i == index) {
                    child.setBackgroundResource(R.drawable.indicator_active);
                } else {
                    child.setBackgroundResource(R.drawable.indicator_inactive);
                }
            }
        }
    }
}
