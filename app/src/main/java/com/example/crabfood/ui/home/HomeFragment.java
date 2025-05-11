package com.example.crabfood.ui.home;

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
import com.example.crabfood.adapter.CategoryAdapter;
import com.example.crabfood.adapter.VendorAdapter;
import com.example.crabfood.databinding.FragmentHomeBinding;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.ui.vendor.VendorListFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements CategoryAdapter.OnMoreClickListener,
        CategoryAdapter.OnCategoryClickListener,
        VendorAdapter.OnVendorClick{

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;

    private HomeViewModel viewModel;
    private CategoryAdapter categoryAdapter;

    private VendorListFragment vendorListFragment;
    private List<Object> categories = new ArrayList<>();
    private List<VendorResponse> vendors = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupCategoryRecyclerView();
        setupVendorRecyclerView();
        observeData();
        setupSwipeRefresh();

        // Load data
        loadData();
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(categories, this::onCategoryClick, this::onMoreClick);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvCategories.setLayoutManager(layoutManager);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupVendorRecyclerView() {
        vendorListFragment = new VendorListFragment();

        // Gắn fragment vào layout
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_vendor_list_container, vendorListFragment)
                .commit();
        binding.tvViewAllRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle args = new Bundle();
                args.putFloat("latitude", 21.02795F);
                args.putFloat("longitude", 105.83416F);
                args.putFloat("radius", 5F);
                navController.navigate(R.id.action_home_to_view_all_vendor_nearby, args);

                Toast.makeText(requireContext(), "Tất cả nhà hàng gần đây", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeData() {
        showLoading(true);
        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), objects -> {
            categories.clear();
            if (objects != null && !objects.isEmpty()) {
                categories.addAll(objects);
                binding.tvNoCategory.setVisibility(View.GONE);
            } else {
                binding.tvNoCategory.setVisibility(View.VISIBLE);
            }
            categoryAdapter.updateData(categories);
            binding.swipeRefresh.setRefreshing(false);
        });

        // Observe vendors
        viewModel.getVendors().observe(getViewLifecycleOwner(), vendorResponses -> {
            Log.d(TAG, "Vendors received: " + (vendorResponses != null ? vendorResponses.size() : 0));

            if (vendorResponses != null && !vendorResponses.isEmpty()) {
                vendors = vendorResponses;
                vendorListFragment.setVendors(vendors);
                binding.fragmentVendorListContainer.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentVendorListContainer.setVisibility(View.GONE);
                Log.e(TAG, "No vendors received or empty list");
            }
            binding.swipeRefresh.setRefreshing(false);
        });

        // Observe loading errors
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Log.d("Home", errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        showLoading(true);
        binding.swipeRefresh.setRefreshing(true);
        viewModel.loadCategories();
        viewModel.loadVendors(21.02795, 105.83416, 5);
        showLoading(false);
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
    public void onMoreClick() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_homeFragment_to_allCategoriesFragment);

        Toast.makeText(requireContext(), "Tất cả danh mục", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onVendorClick(VendorResponse vendor, int position) {
        Log.d(TAG, "Click on vendor");
        // Handle vendor click
    }
}