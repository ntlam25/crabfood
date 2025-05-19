package com.example.crabfood.ui.food;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.crabfood.R;
import com.example.crabfood.adapter.FoodAdapter;
import com.example.crabfood.databinding.FragmentFoodListBinding;
import com.example.crabfood.model.FoodResponse;

import java.util.ArrayList;
import java.util.List;

public class FoodListFragment extends Fragment
    implements FoodAdapter.OnFoodClickListener{


    private static final String ARG_FOOD_TYPE = "food_type";
    private static final String ARG_VENDOR_ID = "vendorId";
    private static final String TAG = "Food list";
    private FragmentFoodListBinding binding;
    private String foodType;
    private FoodListViewModel viewModel;
    private List<FoodResponse> foods = new ArrayList<>();

    private FoodAdapter adapter;

    private long vendorId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFoodListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public static FoodListFragment newInstance(String type, long vendorId) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOOD_TYPE, type);
        args.putLong(ARG_VENDOR_ID, vendorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FoodListViewModel.class);
        if (getArguments() != null) {
            foodType = getArguments().getString(ARG_FOOD_TYPE);
            vendorId = getArguments().getLong(ARG_VENDOR_ID);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new FoodAdapter();
        adapter.setOnFoodClickListener(this::onFoodClick);

        if (foodType != null) {
            loadData(foodType, vendorId);
        }
        setupRecyclerView();
        observe();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setAdapter(adapter);
        // Set layout manager programmatically in case we need to modify it
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);
    }

    private void observe() {
        viewModel.getFoods().observe(getViewLifecycleOwner(), foods -> {
            if (foods != null && !foods.isEmpty()) {
                this.foods = foods;
                adapter.submitList(foods);
                Log.d(TAG, "setupRecyclerView: " + foods.size());
                binding.tvNoFoods.setVisibility(View.GONE);
            } else {
                binding.tvNoFoods.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadData(String foodType, long vendorId) {
        switch (foodType) {
            case "Phổ biến":
                viewModel.loadPopularItems(vendorId);
                break;
            case "Nổi bật":
                viewModel.loadFeaturedItems(vendorId);
                break;
            case "Món mới":
                viewModel.loadNewItems(vendorId);
                break;
        }
    }

    @Override
    public void onFoodClick(FoodResponse food) {
        Log.d(TAG, "onFoodClick: " + food.getName());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putLong("foodId", food.getId());
        args.putLong("vendorId", food.getVendorId());
        navController.navigate(R.id.action_to_food_detail, args);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}