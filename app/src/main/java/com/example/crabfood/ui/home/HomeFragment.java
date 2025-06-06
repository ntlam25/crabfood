package com.example.crabfood.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.crabfood.R;
import com.example.crabfood.adapter.AllCategoryAdapter;
import com.example.crabfood.adapter.CategoryAdapter;
import com.example.crabfood.adapter.VendorAdapter;
import com.example.crabfood.databinding.FragmentHomeBinding;
import com.example.crabfood.helpers.LocationHelper;
import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.ui.vendor.VendorListFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements
        CategoryAdapter.OnCategoryClickListener{

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;

    private HomeViewModel viewModel;

    private AllCategoryAdapter categoryAdapter;
    private LocationHelper locationHelper;

    private VendorListFragment vendorListFragment;
    private List<CategoryResponse> categories = new ArrayList<>();
    private List<VendorResponse> vendors = new ArrayList<>();
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo LocationHelper
        locationHelper = new LocationHelper(requireContext());

        // Đăng ký observers cho LiveData
        locationHelper.getLocationLiveData().observe(this, location -> {
            if (location != null) {
                String locationText = "Vĩ độ: " + location.getLatitude() +
                        "\nKinh độ: " + location.getLongitude();
                this.location = location;
                viewModel.loadVendors(location.getLatitude(), location.getLongitude(), 5);
                Log.d(TAG, "Location updated: " + locationText);
            }
        });

        locationHelper.getLocationErrorLiveData().observe(this, error -> {
            if (error != null) {
                Log.e(TAG, "Location error: " + error);
            }
        });
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
        categoryAdapter = new AllCategoryAdapter(categories, this::onCategoryClick);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvCategories.setLayoutManager(layoutManager);
        binding.rvCategories.setAdapter(categoryAdapter);

        binding.tvAllCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_homeFragment_to_allCategoriesFragment);
                Toast.makeText(requireContext(), "Tất cả danh mục", Toast.LENGTH_SHORT).show();
            }
        });
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
                args.putFloat("latitude", (float) location.getLatitude());
                args.putFloat("longitude", (float) location.getLongitude());
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
        if (checkLocationPermissions()) {
            locationHelper.startLocationUpdates();
        }
        showLoading(true);
        binding.swipeRefresh.setRefreshing(true);
        viewModel.loadCategories();
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

    private boolean checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Nếu chưa có quyền, yêu cầu người dùng
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1001);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã cấp quyền, tiếp tục lấy vị trí
                locationHelper.startLocationUpdates();
            } else {
                // Người dùng từ chối cấp quyền
                showPermissionExplanationDialog();
            }
        }
    }
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cần quyền truy cập vị trí")
                .setMessage("Ứng dụng cần quyền truy cập vị trí để hoạt động chính xác. Vui lòng cấp quyền trong cài đặt.")
                .setPositiveButton("Cài đặt", (dialog, which) -> {
                    // Mở cài đặt ứng dụng để người dùng cấp quyền thủ công
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", null, HomeFragment.TAG);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) ->
                        Toast.makeText(requireContext(), "Không thể lấy vị trí khi không có quyền", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }
}