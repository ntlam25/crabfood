package com.example.crabfood.ui.category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.crabfood.R;
import com.example.crabfood.adapter.AllCategoryAdapter;
import com.example.crabfood.adapter.CategoryAdapter;
import com.example.crabfood.databinding.FragmentAllCategoriesBinding;
import com.example.crabfood.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class AllCategoriesFragment extends Fragment
        implements CategoryAdapter.OnCategoryClickListener {

    private FragmentAllCategoriesBinding binding;
    private AllCategoriesViewModel viewModel;

    private AllCategoryAdapter adapter;

    private List<CategoryResponse> categories = new ArrayList<>();

    public static AllCategoriesFragment newInstance() {
        return new AllCategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAllCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        viewModel = new ViewModelProvider(this).get(AllCategoriesViewModel.class);
        setupCategoryRecyclerView();
        observeData();

        // Load data
        loadData();
    }

    private void loadData() {
        viewModel.loadCategories();
    }

    private void observeData() {
        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), objects -> {
            categories.clear();
            if (objects != null && !objects.isEmpty()) {
                categories.addAll(objects);
                adapter.updateData(categories);
                binding.tvNoCategory.setVisibility(View.GONE);
            } else {
                binding.tvNoCategory.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading errors
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Log.d("All Categories: ", errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoryRecyclerView() {
        adapter = new AllCategoryAdapter(categories, this::onCategoryClick);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.allCategoriesRecyclerView.setLayoutManager(layoutManager);
        binding.allCategoriesRecyclerView.setAdapter(adapter);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    @Override
    public void onCategoryClick(long categoryId, String categoryName) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putLong("categoryId", categoryId);
        args.putString("categoryName", categoryName);
        navController.navigate(R.id.action_to_detail_category, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}