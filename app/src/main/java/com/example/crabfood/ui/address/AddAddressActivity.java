package com.example.crabfood.ui.address;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.crabfood.R;
import com.example.crabfood.adapter.SuggestionAdapter;
import com.example.crabfood.databinding.ActivityAddAddressBinding;
import com.example.crabfood.helpers.KeyboardHelper;
import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AutoCompleteResponse;
import com.example.crabfood.model.GeocodeResponse;
import com.example.crabfood.model.PlaceDetailResponse;
import com.example.crabfood.model.mapsbox.AutoComplete;
import com.example.crabfood.retrofit.RetrofitClient;
import com.example.crabfood.retrofit.RetrofitInstance;
import com.example.crabfood.service.GoongGeocodeService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {
    private static final String TAG = "Add Address";
    private ActivityAddAddressBinding binding;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SuggestionAdapter adapter;
    private AddressViewModel viewModel;
    private List<AutoComplete> allSuggestions = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Call<AutoCompleteResponse> currentCall;

    private LatLng selectedLocation;
    private String selectedAddress;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        setContentView(binding.getRoot());

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        setupSearchView();
        setupCurrentLocationButton();
        setupSaveButton();
        observe();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestionAdapter(allSuggestions, (position, placeId) -> {
            fetchPlaceDetails(placeId);
            binding.recyclerView.setVisibility(View.GONE);
        });
        binding.recyclerView.setAdapter(adapter);
    }

    private void observe() {
        viewModel.getAddressCreated().observe(this, response -> {
            // Hiển thị kết quả hoặc chuyển màn hình
            Toast.makeText(this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSaveButton() {
        binding.saveAddressFab.setOnClickListener(v -> {
            if (selectedLocation == null || selectedAddress == null) {
                Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show();
                return;
            }

            String locationName = binding.etLocationName.getText().toString().trim();
            if (locationName.isEmpty()) {
                Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create address request
            AddressRequest request = new AddressRequest();
            request.setLabel(locationName);
            request.setFullAddress(selectedAddress);
            request.setLatitude(selectedLocation.getLatitude());
            request.setLongitude(selectedLocation.getLongitude());
            request.setDefault(binding.checkboxDefaultAddress.isChecked());
            // Save address
            saveAddress(request);
        });
    }

    private void saveAddress(AddressRequest request) {
        // Show loading
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.saveAddressFab.setEnabled(false);

        // Call API to save address
        // TODO: Implement your API call here
        // For now, just show success message
        new Handler().postDelayed(() -> {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.saveAddressFab.setEnabled(true);

            viewModel.addAddress(request);
        }, 1000);
    }

    private void setupCurrentLocationButton() {
        binding.currentLocationFab.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMapLocation(currentLatLng);
                            reverseGeocode(location);
                        }
                    });
        }
    }

    private void reverseGeocode(Location location) {
        GoongGeocodeService service = RetrofitInstance.getRetrofitInstance("https://rsapi.goong.io/")
                .create(GoongGeocodeService.class);

        String latLng = location.getLatitude() + "," + location.getLongitude();
        Call<GeocodeResponse> call = service.reverseGeocode(latLng, getString(R.string.goong_api_key));

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeocodeResponse data = response.body();
                    if (data.getResults() != null && data.getResults().length > 0) {
                        String address = data.getResults()[0].getFormattedAddress();
                        binding.etLocation.setText(address);
                        selectedAddress = address;
                        selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                } else {
                    Toast.makeText(AddAddressActivity.this, "Failed to get address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Failed to get address: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        String uri = getResources().getString(R.string.goong_map_url) +
                "/assets/goong_map_web.json?api_key=" +
                getResources().getString(R.string.goong_map_key);

        mapboxMap.setStyle(new Style.Builder().fromUri(uri), style -> {
            // Map is ready
            if (checkLocationPermission()) {
                getCurrentLocation();
            }
        });
        mapView.setOnClickListener(v -> KeyboardHelper.hideKeyboard(AddAddressActivity.this));
    }

    private void setupSearchView() {
        Handler handler = new Handler();
        final Runnable[] workRunnable = {null};

        binding.searchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (workRunnable[0] != null) handler.removeCallbacks(workRunnable[0]);

                String query = s.toString().trim();
                binding.recyclerView.setVisibility(View.GONE);
                workRunnable[0] = () -> search(query);
                handler.postDelayed(workRunnable[0], 400);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        binding.searchAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                KeyboardHelper.hideKeyboard(AddAddressActivity.this);
                binding.recyclerView.setVisibility(View.GONE);
            } else if (!binding.searchAddress.getText().toString().isEmpty()) {
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void search(String textSearch) {
        if (currentCall != null) {
            currentCall.cancel(); // Hủy call cũ nếu có
        }
        if (binding.recyclerView != null) {
            binding.recyclerView.setVisibility(View.VISIBLE);
        }

        GoongGeocodeService service = RetrofitInstance.getRetrofitInstance(getString(R.string.goong_api_url))
                .create(GoongGeocodeService.class);

        currentCall = service.getAutoComplete(textSearch, getString(R.string.goong_api_key));
        currentCall.enqueue(new Callback<AutoCompleteResponse>() {
            @Override
            public void onResponse(Call<AutoCompleteResponse> call, Response<AutoCompleteResponse> response) {
                if (!call.isCanceled() && response.isSuccessful() && response.body() != null) {
                    AutoCompleteResponse data = response.body();
                    if (data.getPredictions() != null) {
                        Log.d(TAG, "onResponse: " + data.getPredictions().size());
                        adapter.updateSuggestions(data.getPredictions());
                        if (data.getPredictions().isEmpty()) {
                            binding.recyclerView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(AddAddressActivity.this, "Search failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AutoCompleteResponse> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlaceDetails(String placeId) {
        GoongGeocodeService service = RetrofitInstance.getRetrofitInstance(getString(R.string.goong_api_url))
                .create(GoongGeocodeService.class);

        Call<PlaceDetailResponse> call = service.getPlaceDetail(placeId, getString(R.string.goong_api_key));
        call.enqueue(new Callback<PlaceDetailResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailResponse> call, Response<PlaceDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaceDetailResponse data = response.body();
                    if (data.getResult() != null) {
                        PlaceDetailResponse.Result result = data.getResult();
                        LatLng location = new LatLng(result.getGeometry().getLocation().getLat(),
                                result.getGeometry().getLocation().getLng());
                        updateMapLocation(location);
                        binding.etLocation.setText(result.getFormattedAddress());
                        binding.searchAddress.setText(result.getFormattedAddress());
                        binding.searchAddress.clearFocus();
                        KeyboardHelper.hideKeyboard(AddAddressActivity.this);
                        selectedAddress = result.getFormattedAddress();
                        selectedLocation = location;
                        binding.recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AddAddressActivity.this, "Failed to get place details: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailResponse> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Failed to get place details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapLocation(LatLng location) {
        if (mapboxMap != null) {
            mapboxMap.clear();
            mapboxMap.addMarker(new MarkerOptions().position(location));
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(location)
                            .zoom(15)
                            .build()
            ), 1000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}