package com.example.crabfood.ui.order;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentOrderTrackingBinding;
import com.example.crabfood.helpers.LocationHelper;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.GoongDirectionsResponse;
import com.example.crabfood.model.LocationUpdateResponse;
import com.example.crabfood.model.OrderTrackingInfo;
import com.example.crabfood.model.enums.OrderTrackingStatus;
import com.example.crabfood.service.GoongDirectionsService;
import com.example.crabfood.websocket.WebSocketClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "OrderTrackingActivity";
    private static final String SOURCE_ID = "route-source-id";
    private static final String LAYER_ID = "route-layer-id";
    private static final String ICON_RESTAURANT_ID = "restaurant-icon-id";
    private static final String ICON_DESTINATION_ID = "destination-icon-id";
    private static final String ICON_RIDER_ID = "rider-icon-id";
    private static final String GOONG_BASE_URL = "https://rsapi.goong.io/";
    private static final String GOONG_API_KEY = "mRZ2f9dpAibVZozcAJiF26CdI4R2Q0OZHy4lV8iu";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FragmentOrderTrackingBinding binding;
    private MapboxMap mapboxMap;
    private GoongDirectionsService goongService;
    private WebSocketClient webSocketClient;
    private SessionManager sessionManager;
    private OrderViewModel viewModel;
    private LocationHelper locationHelper;

    private long orderId;
    private OrderTrackingInfo trackingInfo;
    private List<LatLng> routePoints;
    private ValueAnimator riderAnimator;
    private Marker riderMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        sessionManager = new SessionManager(this);
        binding = FragmentOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize location helper
        locationHelper = new LocationHelper(this);
        locationHelper.getLocationLiveData().observe(this, location -> {
            if (location != null) {
                Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
            }
        });
        locationHelper.getLocationErrorLiveData().observe(this, error -> {
            if (error != null) {
                Log.e(TAG, "Location error: " + error);
                Toast.makeText(this, "Location error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        setupMapView(savedInstanceState);
        observeViewModel();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationHelper.getCurrentLocation(this, new LocationHelper.LocationCallbackInterface() {
            @Override
            public void onLocationResult(android.location.Location location) {
                if (location != null) {
                    Log.d(TAG, "Current location: " + location.getLatitude() + ", " + location.getLongitude());
                } else {
                    Log.e(TAG, "Failed to get current location");
                }
            }

            @Override
            public void onLocationError(String error) {
                Log.e(TAG, "Location error: " + error);
                Toast.makeText(OrderTrackingActivity.this, "Location error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission required for tracking", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.callButton.setOnClickListener(v -> {
            // Implement call functionality
            Toast.makeText(this, "Call functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
        binding.chatButton.setOnClickListener(v -> {
            // Implement chat functionality
            Toast.makeText(this, "Chat functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMapView(Bundle savedInstanceState) {
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
    }

    private void observeViewModel() {
        viewModel.getTrackingInfo().observe(this, this::updateTrackingInfo);
        viewModel.getLocationUpdate().observe(this, this::updateRiderLocation);
        viewModel.getErrorMessage().observe(this, event -> {
            if (event != null && !event.isHasBeenHandled()) {
                Toast.makeText(this, event.peekContent().message, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            // Map is set up and the style has loaded
            // Now load the tracking info for the order
            loadTrackingInfo();

            // Add custom marker images
            loadIcons(style);

            // Initialize the GeoJsonSource for the route
            style.addSource(new GeoJsonSource(SOURCE_ID));

            // Add the LineLayer for the route
            style.addLayer(new LineLayer(LAYER_ID, SOURCE_ID)
                    .withProperties(
                            lineWidth(5f),
                            lineColor("#FF5722"),
                            lineCap(Property.LINE_CAP_ROUND),
                            lineJoin(Property.LINE_JOIN_ROUND)
                    ));
        });
    }

    private void loadIcons(Style style) {
        // Load custom icons for restaurant, destination, and rider
        style.addImage(ICON_RESTAURANT_ID, getResources().getDrawable(R.drawable.ic_shop));
        style.addImage(ICON_DESTINATION_ID, getResources().getDrawable(R.drawable.ic_delivery));
        style.addImage(ICON_RIDER_ID, getResources().getDrawable(R.drawable.ic_package));
    }

    private void loadTrackingInfo() {
        Long customerId = sessionManager.getUserId();
        if (customerId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


    }

    private void displayTrackingInfo() {
        if (trackingInfo == null) return;

        // Update rider info
        binding.driverNameText.setText(trackingInfo.getRiderName());
        binding.driverRatingText.setText(String.valueOf(trackingInfo.getRiderRating()));
        binding.driverIdText.setText("ID " + trackingInfo.getRiderId());

        if (trackingInfo.getRiderImageUrl() != null && !trackingInfo.getRiderImageUrl().isEmpty()) {
            Glide.with(binding.getRoot())
                    .load(trackingInfo.getRiderImageUrl())
                    .placeholder(R.drawable.avata_placeholder)
                    .error(R.drawable.avatar_error)
                    .into(binding.driverImageView);
        }

        // Update delivery status
        updateDeliveryStatus(trackingInfo.getStatus());

        // Update estimated time
        binding.estimatedTimeText.setText(trackingInfo.getEstimatedDeliveryTime());

        // Add markers and route to map
        addMarkersToMap();
        fetchAndDisplayRoute();

        // Show driver info if available
        binding.findingDriverLayout.setVisibility(
                trackingInfo.getRiderId() != null ? View.GONE : View.VISIBLE);
        binding.driverInfoLayout.setVisibility(
                trackingInfo.getRiderId() != null ? View.VISIBLE : View.GONE);
    }

    private void updateDeliveryStatus(String status) {
        OrderTrackingStatus trackingStatus = OrderTrackingStatus.valueOf(status);
        String statusText;
        switch (trackingStatus) {
            case PENDING:
                statusText = "Waiting for restaurant to accept";
                break;
            case ACCEPTED:
                statusText = "Restaurant accepted your order";
                break;
            case PICKED_UP:
                statusText = "Rider picked up your order";
                break;
            case ON_THE_WAY:
                statusText = "Rider is on the way";
                break;
            case SUCCESS:
                statusText = "Order delivered";
                break;
            case CANCELLED:
                statusText = "Order cancelled";
                break;
            default:
                statusText = "Unknown status";
        }
        binding.statusText.setText(statusText);
    }

    private void addMarkersToMap() {
        if (mapboxMap == null || trackingInfo == null) return;

        // Add restaurant marker
        if (trackingInfo.getSourceLatitude() != null && trackingInfo.getSourceLongitude() != null) {
            addMarker(new LatLng(trackingInfo.getSourceLatitude(), trackingInfo.getSourceLongitude()), ICON_RESTAURANT_ID);
        }

        // Add destination marker
        if (trackingInfo.getDestinationLatitude() != null && trackingInfo.getDestinationLongitude() != null) {
            addMarker(new LatLng(trackingInfo.getDestinationLatitude(), trackingInfo.getDestinationLongitude()), ICON_DESTINATION_ID);
        }

        // Add rider marker
        if (trackingInfo.getCurrentLatitude() != null && trackingInfo.getCurrentLongitude() != null) {
            addMarker(new LatLng(trackingInfo.getCurrentLatitude(), trackingInfo.getCurrentLongitude()), ICON_RIDER_ID);
        }

        // Move camera to fit all markers
        zoomToFitMarkers();
    }

    private void addMarker(LatLng position, String iconId) {
        if (mapboxMap == null) return;
        mapboxMap.getStyle(style -> {
            GeoJsonSource source = style.getSourceAs(SOURCE_ID);
            if (source != null) {
                Point point = Point.fromLngLat(position.getLongitude(), position.getLatitude());
                Feature feature = Feature.fromGeometry(point);
                feature.addStringProperty("icon", iconId);
                source.setGeoJson(FeatureCollection.fromFeature(feature));
            }
        });
    }

    private void zoomToFitMarkers() {
        if (mapboxMap == null || trackingInfo == null) return;

        List<LatLng> points = new ArrayList<>();

        // Add restaurant location
        if (trackingInfo.getSourceLatitude() != null && trackingInfo.getSourceLongitude() != null) {
            points.add(new LatLng(trackingInfo.getSourceLatitude(), trackingInfo.getSourceLongitude()));
        }

        // Add destination location
        if (trackingInfo.getDestinationLatitude() != null && trackingInfo.getDestinationLongitude() != null) {
            points.add(new LatLng(trackingInfo.getDestinationLatitude(), trackingInfo.getDestinationLongitude()));
        }

        // Add rider's current location
        if (trackingInfo.getCurrentLatitude() != null && trackingInfo.getCurrentLongitude() != null) {
            points.add(new LatLng(trackingInfo.getCurrentLatitude(), trackingInfo.getCurrentLongitude()));
        }

        if (points.size() >= 2) {
            // Find the center point
            double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
            double minLng = Double.MAX_VALUE, maxLng = Double.MIN_VALUE;

            for (LatLng point : points) {
                minLat = Math.min(minLat, point.getLatitude());
                maxLat = Math.max(maxLat, point.getLatitude());
                minLng = Math.min(minLng, point.getLongitude());
                maxLng = Math.max(maxLng, point.getLongitude());
            }

            LatLng center = new LatLng((minLat + maxLat) / 2, (minLng + maxLng) / 2);

            // Calculate appropriate zoom level
            double latSpan = maxLat - minLat;
            double lngSpan = maxLng - minLng;
            double maxSpan = Math.max(latSpan, lngSpan);

            // Adjust zoom level based on span
            double zoom = 14;
            if (maxSpan > 0.05) zoom = 12;
            if (maxSpan > 0.1) zoom = 10;

            // Animate camera to fit all points
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(center)
                            .zoom(zoom)
                            .build()
            ));
        }
    }

    private void fetchAndDisplayRoute() {
        if (trackingInfo == null) {
            Log.e(TAG, "Cannot fetch route: trackingInfo is null");
            return;
        }

        try {
            if (trackingInfo.getSourceLatitude() == null || trackingInfo.getSourceLongitude() == null ||
                trackingInfo.getDestinationLatitude() == null || trackingInfo.getDestinationLongitude() == null) {
                Log.e(TAG, "Cannot fetch route: missing coordinates");
                return;
            }

            String origin = trackingInfo.getSourceLatitude() + "," + trackingInfo.getSourceLongitude();
            String destination = trackingInfo.getDestinationLatitude() + "," + trackingInfo.getDestinationLongitude();

            Log.d(TAG, "Fetching route from " + origin + " to " + destination);

            goongService.getDirections(origin, destination, "car", GOONG_API_KEY)
                    .enqueue(new Callback<GoongDirectionsResponse>() {
                        @Override
                        public void onResponse(Call<GoongDirectionsResponse> call, Response<GoongDirectionsResponse> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    GoongDirectionsResponse directionsResponse = response.body();
                                    if (!directionsResponse.getRoutes().isEmpty()) {
                                        routePoints = decodePolyline(directionsResponse.getRoutes().get(0).getLegs().get(0).getSteps());
                                        if (!routePoints.isEmpty()) {
                                            displayRouteOnMap();
                                        } else {
                                            Log.e(TAG, "No route points decoded");
                                        }
                                    } else {
                                        Log.e(TAG, "No routes found in response");
                                    }
                                } else {
                                    Log.e(TAG, "Error response from Goong API: " + response.code());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing route response", e);
                            }
                        }

                        @Override
                        public void onFailure(Call<GoongDirectionsResponse> call, Throwable t) {
                            Log.e(TAG, "Error fetching directions", t);
                            try {
                                Toast.makeText(OrderTrackingActivity.this,
                                        "Error fetching route: " + t.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error showing toast", e);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchAndDisplayRoute", e);
        }
    }

    private List<LatLng> decodePolyline(List<GoongDirectionsResponse.Step> steps) {
        List<LatLng> points = new ArrayList<>();
        for (GoongDirectionsResponse.Step step : steps) {
            String encodedPoints = step.getPolyline().getPoints();
            // Split the encoded string into pairs of coordinates
            String[] pairs = encodedPoints.split(" ");
            for (String pair : pairs) {
                String[] coords = pair.split(",");
                if (coords.length == 2) {
                    try {
                        double lat = Double.parseDouble(coords[0]);
                        double lng = Double.parseDouble(coords[1]);
                        points.add(new LatLng(lat, lng));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing coordinates: " + pair, e);
                    }
                }
            }
        }
        return points;
    }

    private void displayRouteOnMap() {
        if (mapboxMap == null || routePoints == null || routePoints.isEmpty()) return;

        mapboxMap.getStyle(style -> {
            GeoJsonSource source = style.getSourceAs(SOURCE_ID);
            if (source != null) {
                List<Point> points = new ArrayList<>();
                for (LatLng point : routePoints) {
                    points.add(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
                }
                LineString lineString = LineString.fromLngLats(points);
                Feature feature = Feature.fromGeometry(lineString);
                source.setGeoJson(FeatureCollection.fromFeature(feature));
            }
        });
    }

    private void connectToWebSocket() {
        if (trackingInfo == null) return;

        // Initialize WebSocketClient with order ID
        webSocketClient = new WebSocketClient(getApplicationContext(), orderId);
        webSocketClient.setLocationUpdateListener(new WebSocketClient.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(LocationUpdateResponse locationUpdate) {
                runOnUiThread(() -> {
                    // Update rider marker position
                    updateRiderLocation(locationUpdate);

                    // Update estimated time
                    if (locationUpdate.getEstimatedDeliveryTime() != null) {
                        binding.estimatedTimeText.setText(locationUpdate.getEstimatedDeliveryTime());
                    }

                    // Update delivery status if changed
                    if (locationUpdate.getStatus() != null) {
                        binding.statusText.setText(locationUpdate.getStatus().getDisplayName());
                    }

                    // Refresh the route
                    trackingInfo.setCurrentLatitude(locationUpdate.getRiderLatitude());
                    trackingInfo.setCurrentLongitude(locationUpdate.getRiderLongitude());
                    fetchAndDisplayRoute();
                });
            }

            @Override
            public void onConnectionError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(OrderTrackingActivity.this,
                                "WebSocket connection error: " + message,
                                Toast.LENGTH_SHORT).show()
                );
            }
        });

        // Connect to WebSocket
        webSocketClient.connect();
    }

    private void updateRiderLocation(LocationUpdateResponse update) {
        if (update == null || mapboxMap == null) {
            Log.e(TAG, "Cannot update rider location: update or mapboxMap is null");
            return;
        }

        if (update.getRiderLatitude() == null || update.getRiderLongitude() == null) {
            Log.e(TAG, "Cannot update rider location: missing coordinates");
            return;
        }

        try {
            LatLng newPosition = new LatLng(update.getRiderLatitude(), update.getRiderLongitude());

            mapboxMap.getStyle(style -> {
                try {
                    if (riderMarker == null) {
                        // Create rider marker if it doesn't exist
                        Bitmap bitmap = style.getImage(ICON_RIDER_ID);
                        if (bitmap != null) {
                            IconFactory iconFactory = IconFactory.getInstance(this);
                            Icon riderIcon = iconFactory.fromBitmap(bitmap);
                            riderMarker = mapboxMap.addMarker(new MarkerOptions()
                                    .position(newPosition)
                                    .icon(riderIcon));
                            Log.d(TAG, "Created new rider marker at " + newPosition);
                        } else {
                            Log.e(TAG, "Failed to get rider icon bitmap");
                        }
                    } else {
                        // Animate rider marker movement
                        if (riderAnimator != null) {
                            riderAnimator.cancel();
                        }

                        LatLng oldPosition = riderMarker.getPosition();
                        double distance = calculateDistance(oldPosition, newPosition);

                        // Adjust animation duration based on distance
                        long duration = Math.min(1000, Math.max(300, (long)(distance * 100)));

                        riderAnimator = ValueAnimator.ofFloat(0f, 1f);
                        riderAnimator.setDuration(duration);
                        riderAnimator.addUpdateListener(animation -> {
                            try {
                                float fraction = animation.getAnimatedFraction();
                                double lat = oldPosition.getLatitude() + (newPosition.getLatitude() - oldPosition.getLatitude()) * fraction;
                                double lng = oldPosition.getLongitude() + (newPosition.getLongitude() - oldPosition.getLongitude()) * fraction;
                                riderMarker.setPosition(new LatLng(lat, lng));
                            } catch (Exception e) {
                                Log.e(TAG, "Error updating marker position", e);
                            }
                        });
                        riderAnimator.start();
                        Log.d(TAG, "Animated rider marker from " + oldPosition + " to " + newPosition);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating rider marker", e);
                }
            });

            // Update estimated time if available
            if (update.getEstimatedDeliveryTime() != null) {
                binding.estimatedTimeText.setText(update.getEstimatedDeliveryTime());
            }

            // Update delivery status if changed
            if (update.getStatus() != null) {
                binding.statusText.setText(update.getStatus().getDisplayName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateRiderLocation", e);
        }
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        double R = 6371e3; // Earth's radius in meters
        double φ1 = Math.toRadians(point1.getLatitude());
        double φ2 = Math.toRadians(point2.getLatitude());
        double Δφ = Math.toRadians(point2.getLatitude() - point1.getLatitude());
        double Δλ = Math.toRadians(point2.getLongitude() - point1.getLongitude());

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c; // Distance in meters
    }

    private void updateTrackingInfo(OrderTrackingInfo orderTrackingInfo) {
        if (orderTrackingInfo == null) return;

        trackingInfo = orderTrackingInfo;
        displayTrackingInfo();

        // Update rider marker position
        if (trackingInfo.getCurrentLatitude() != null && trackingInfo.getCurrentLongitude() != null) {
            LatLng riderPosition = new LatLng(trackingInfo.getCurrentLatitude(), trackingInfo.getCurrentLongitude());
            addMarker(riderPosition, ICON_RIDER_ID);
        }

        // Update delivery status
        updateDeliveryStatus(trackingInfo.getStatus());

        // Update estimated time
        if (trackingInfo.getEstimatedDeliveryTime() != null) {
            binding.estimatedTimeText.setText(trackingInfo.getEstimatedDeliveryTime());
        }

        // Show/hide driver info based on rider availability
        binding.findingDriverLayout.setVisibility(
                trackingInfo.getRiderId() != null ? View.GONE : View.VISIBLE);
        binding.driverInfoLayout.setVisibility(
                trackingInfo.getRiderId() != null ? View.VISIBLE : View.GONE);

        // Update driver info if available
        if (trackingInfo.getRiderId() != null) {
            binding.driverNameText.setText(trackingInfo.getRiderName());
            binding.driverRatingText.setText(String.valueOf(trackingInfo.getRiderRating()));
            binding.driverIdText.setText("ID " + trackingInfo.getRiderId());

            if (trackingInfo.getRiderImageUrl() != null && !trackingInfo.getRiderImageUrl().isEmpty()) {
                Glide.with(binding.getRoot())
                        .load(trackingInfo.getRiderImageUrl())
                        .placeholder(R.drawable.avata_placeholder)
                        .error(R.drawable.avatar_error)
                        .into(binding.driverImageView);
            }
        }

        // Add markers and route to map
        addMarkersToMap();
        fetchAndDisplayRoute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        try {
            if (webSocketClient != null) {
                webSocketClient.disconnect();
            }
            if (riderAnimator != null) {
                riderAnimator.cancel();
            }
            if (locationHelper != null) {
                locationHelper.stopLocationUpdates();
            }
            binding.mapView.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}