package com.example.crabfood.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.crabfood.ui.food.FoodListFragment;

public class FoodPagerAdapter extends FragmentStateAdapter {
    private final String[] types = {"Phổ biến", "Nổi bật", "Món mới"};
    private final long vendorId;
    public FoodPagerAdapter(@NonNull Fragment fragment, long vendorId) {
        super(fragment);
        this.vendorId = vendorId;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("ADAPTER", "Creating fragment for position: " + position);
        switch (position) {
            case 0: return FoodListFragment.newInstance("Phổ biến", vendorId);
            case 1: return FoodListFragment.newInstance("Nổi bật", vendorId);
            case 2: return FoodListFragment.newInstance("Món mới", vendorId);
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
