package com.example.crabfood.ui.vendor;

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
import androidx.navigation.Navigation;

import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentAllVendorNearbyBinding;
import com.example.crabfood.helpers.KeyboardHelper;
import com.example.crabfood.helpers.LocationHelper;
import com.example.crabfood.ui.home.HomeFragment;
import com.google.android.material.snackbar.Snackbar;

public class AllVendorNearbyFragment extends Fragment {
    private static final String TAG = "All vendor nearby";
    private FragmentAllVendorNearbyBinding binding;
    private AllVendorNearbyViewModel viewModel;
    private VendorListFragment vendorListFragment;
    private LocationHelper locationHelper;
    private double radius;
    private double latitue, longitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationHelper = new LocationHelper(requireContext());

        // Đăng ký observers cho LiveData
        locationHelper.getLocationLiveData().observe(this, location -> {
            if (location != null) {
                String locationText = "Vĩ độ: " + location.getLatitude() +
                        "\nKinh độ: " + location.getLongitude();
                viewModel.loadVendors(location.getLatitude(), location.getLongitude(),
                        radius, null, null, null,
                        null, null, null);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAllVendorNearbyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardHelper.hideKeyboardOnClickOutside(binding.getRoot(), requireActivity());
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllVendorNearbyViewModel.class);
        getCurrentLocation();
        // Get location parameters from arguments
        if (getArguments() != null) {
            radius = getArguments().getFloat("radius", 5F);
        }

        setupToolbar();
        setupRecyclerView();
        setupSearchView();
        setupSwipeRefresh();
        loadData();
        observe();
    }

    private void getCurrentLocation() {
        LocationHelper.getCurrentLocation(requireActivity(), new LocationHelper.LocationCallbackInterface() {
            @Override
            public void onLocationResult(Location location) {
                if (location != null) {
                    // Xử lý khi có vị trí
                    String locationText = String.format("Vị trí hiện tại:\n" +
                                    "- Vĩ độ (Latitude): %.6f\n" +
                                    "- Kinh độ (Longitude): %.6f\n" +
                                    "- Độ chính xác: %.1f mét",
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getAccuracy());
                    viewModel.loadVendors(location.getLatitude(), location.getLongitude(),
                            radius, null, null, null,
                            null, null, null);
                    Log.d(TAG, "Location updated: " + locationText);
                } else {
                    Snackbar.make(binding.getRoot(), "Không thể lấy được vị trí!", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLocationError(String error) {
                // Xử lý khi có lỗi
                Toast.makeText(requireContext(),
                        "Lỗi lấy vị trí: " + error, Toast.LENGTH_SHORT).show();
                Snackbar.make(requireView(), "Lỗi lấy vị trí: " + error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        // Set navigation icon click listener
        binding.toolbarVendors.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        if (checkLocationPermissions()) {
            locationHelper.startLocationUpdates();
        }
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    private void observe() {
        // Observe vendors data
        viewModel.getVendors().observe(getViewLifecycleOwner(), vendorResponses -> {
            if (vendorResponses != null) {
                vendorListFragment.setVendors(vendorResponses);
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        // Observe loading errors
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Log.d("All vendor", errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        vendorListFragment = new VendorListFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.vendor_list_container, vendorListFragment)
                .commit();
    }

    private void setupSearchView() {
        // Setup search functionality with EditText
        binding.searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vendorListFragment.filterVendors(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Not needed
            }
        });

        // Handle IME search action
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                vendorListFragment.filterVendors(binding.searchEditText.getText().toString());

                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                        requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Setup filter icon click listener
        binding.filterIcon.setOnClickListener(v -> {
            // Show filter options (can be implemented later)
            android.widget.Toast.makeText(requireContext(), "Filter options", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                    Uri uri = Uri.fromParts("package", null, AllVendorNearbyFragment.TAG);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) ->
                        Toast.makeText(requireContext(), "Không thể lấy vị trí khi không có quyền", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }
}