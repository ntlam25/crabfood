package com.example.crabfood.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.crabfood.ui.order.OrderHistoryFragment;
import com.example.crabfood.ui.order.UpcomingFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UpcomingFragment();
            case 1:
                return new OrderHistoryFragment();
            default:
                return new UpcomingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Upcoming and History tabs
    }
}