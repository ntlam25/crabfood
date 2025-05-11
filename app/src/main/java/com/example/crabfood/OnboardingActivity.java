package com.example.crabfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.crabfood.adapter.OnboardingAdapter;
import com.example.crabfood.model.OnboardingItem;
import com.example.crabfood.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity implements
        OnboardingAdapter.OnSkipClickListener,
        OnboardingAdapter.OnNextClickListener,
        OnboardingAdapter.OnGetStartedClickListener {

    private ViewPager2 onboardingViewPager;
    private OnboardingAdapter onboardingAdapter;

    private static final String PREFS_NAME = "onboarding_prefs";
    private static final String PREF_FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if (!isFirstLaunch()) {
            launchMainActivity();
            finish();
            return;
        }

        setupOnboardingItems();

        onboardingViewPager = findViewById(R.id.viewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        // Disable swiping if needed
        // onboardingViewPager.setUserInputEnabled(false);
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem orderFood = new OnboardingItem(
                R.drawable.food_image, // Replace with your pizza image resource
                R.drawable.ic_food, // Replace with your food icon resource
                "Order For Food",
                "Lorem ipsum dolor sit amet, conse ctetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna."
        );

        OnboardingItem easyPayment = new OnboardingItem(
                R.drawable.dessert_image, // Replace with your dessert/ice cream image resource
                R.drawable.ic_payment, // Replace with your payment icon resource
                "Easy Payment",
                "Lorem ipsum dolor sit amet, conse ctetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna."
        );

        OnboardingItem fastDelivery = new OnboardingItem(
                R.drawable.coffee_image, // Replace with your coffee image resource
                R.drawable.ic_delivery, // Replace with your delivery icon resource
                "Fast Delivery",
                "Lorem ipsum dolor sit amet, conse ctetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna."
        );

        onboardingItems.add(orderFood);
        onboardingItems.add(easyPayment);
        onboardingItems.add(fastDelivery);

        onboardingAdapter = new OnboardingAdapter(
                onboardingItems,
                this, // skipClickListener
                this, // nextClickListener
                this  // getStartedClickListener
        );
    }

    @Override
    public void onSkipClick() {
        finishOnboarding();
    }

    @Override
    public void onNextClick(int position) {
        if (position < onboardingAdapter.getItemCount() - 1) {
            onboardingViewPager.setCurrentItem(position + 1);
        }
    }

    @Override
    public void onGetStartedClick() {
        finishOnboarding();
    }

    private void finishOnboarding() {
        // This is where you would navigate to your main app activity
        // For example: startActivity(new Intent(this, HomeActivity.class));
        // finish();

        // For demo purposes, we'll just show a toast and finish the activity
        setFirstLaunchFalse();
        launchMainActivity();
        Toast.makeText(this, "Getting started with the app", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(PREF_FIRST_LAUNCH, true);
    }

    private void setFirstLaunchFalse() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_FIRST_LAUNCH, false);
        editor.apply();
    }

    private void launchMainActivity() {
        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
    }
}
