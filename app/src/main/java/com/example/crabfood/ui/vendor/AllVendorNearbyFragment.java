package com.example.crabfood.ui.vendor;

import android.location.Location;
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
import androidx.navigation.Navigation;

import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentAllVendorNearbyBinding;
import com.example.crabfood.helpers.KeyboardHelper;
import com.example.crabfood.helpers.LocationHelper;
import com.google.android.material.snackbar.Snackbar;

public class AllVendorNearbyFragment extends Fragment {
    private static final String TAG = "All vendor nearby";
    private FragmentAllVendorNearbyBinding binding;
    private AllVendorNearbyViewModel viewModel;
    private VendorListFragment vendorListFragment;
    private double latitude;
    private double longitude;
    private double radius;
    private Location location;

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
        loadData();
        observe();
    }

    private void getCurrentLocation() {
        LocationHelper.getCurrentLocation(requireActivity(), new LocationHelper.LocationCallbackInterface() {
            @Override
            public void onLocationResult(Location location) {
                if (location != null) {
                    AllVendorNearbyFragment.this.location = location;
                    // Xử lý khi có vị trí
                    String locationText = String.format("Vị trí hiện tại:\n" +
                                    "- Vĩ độ (Latitude): %.6f\n" +
                                    "- Kinh độ (Longitude): %.6f\n" +
                                    "- Độ chính xác: %.1f mét",
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getAccuracy());
                    viewModel.loadVendors(location.getLatitude(), location.getLongitude(), 5, null, null, null, null, null, null);
                    Log.d(TAG, "Location updated: " + locationText);
                } else {
                    Snackbar.make(binding.getRoot(),"Không thể lấy được vị trí!",Snackbar.LENGTH_SHORT).show();
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

    private void loadData() {
        // Load data
        viewModel.loadVendors(latitude, longitude, radius, null, null, null, null, null, null);
    }

    private void observe() {
        // Observe vendors data
        viewModel.getVendors().observe(getViewLifecycleOwner(), vendorResponses -> {
            if (vendorResponses != null) {
                vendorListFragment.setVendors(vendorResponses);
            }
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
}