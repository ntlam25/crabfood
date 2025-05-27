package com.example.crabfood.ui.order;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentOrderTrackingBinding;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.LocationUpdateResponse;
import com.example.crabfood.model.OrderTrackingInfo;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.retrofit.RetrofitInstance;
import com.example.crabfood.service.OrderTrackingService;
import com.example.crabfood.websocket.WebSocketClient;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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

    private FragmentOrderTrackingBinding binding;
    private MapboxMap mapboxMap;
    private OrderTrackingService trackingService;
    private WebSocketClient webSocketClient;
    private SessionManager sessionManager;

    private long orderId;
    private OrderTrackingInfo trackingInfo;
//    private SymbolManager symbolManager;
//    private PointAnnotationManager pointAnnotationManager;
//    private Symbol riderSymbol;
//    private final List<Symbol> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        binding = FragmentOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize services
        trackingService = ApiUtils.getOrderTrackingService();
        sessionManager = new SessionManager(this);

        // Get orderId from intent
        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        setupMapView(savedInstanceState);
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
        binding.cancelOrderButton.setOnClickListener(v -> {
            // Implement cancel order functionality
            Toast.makeText(this, "Cancel order functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
        binding.detailsButton.setOnClickListener(v -> {
            // Implement order details functionality
            Toast.makeText(this, "Order details functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMapView(Bundle savedInstanceState) {
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            // Map is set up and the style has loaded
            // Now load the tracking info for the order
            loadTrackingInfo();

            // Add symbol managers for markers
//            symbolManager = new SymbolManager(binding.mapView, mapboxMap, style);
//            symbolManager.setIconAllowOverlap(true);
//            symbolManager.setTextAllowOverlap(true);

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

        trackingService.getOrderTrackingInfo(orderId, customerId)
                .enqueue(new Callback<OrderTrackingInfo>() {
                    @Override
                    public void onResponse(Call<OrderTrackingInfo> call, Response<OrderTrackingInfo> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            trackingInfo = response.body();
                            displayTrackingInfo();

                            // Connect to WebSocket for real-time updates
                            connectToWebSocket();
                        } else {
                            Toast.makeText(OrderTrackingActivity.this,
                                    "Failed to load tracking information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderTrackingInfo> call, Throwable t) {
                        Log.e(TAG, "Error loading tracking info", t);
                        Toast.makeText(OrderTrackingActivity.this,
                                "Error loading tracking information: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
        // Check if status is null to avoid NPE
        if (status == null) return;

        // Reset all icons to gray
        binding.restaurantIcon.setColorFilter(getResources().getColor(R.color.gray));
        binding.packageIcon.setColorFilter(getResources().getColor(R.color.gray));
        binding.deliveryIcon.setColorFilter(getResources().getColor(R.color.gray));
        binding.completeIcon.setColorFilter(getResources().getColor(R.color.gray));

        binding.line1.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.line2.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.line3.setBackgroundColor(getResources().getColor(R.color.gray));

        // Set appropriate icons to orange based on status
        int accentColor = getResources().getColor(R.color.primary_400);

        switch (status) {
            case "PENDING":
            case "ACCEPTED":
            case "PREPARING":
                binding.restaurantIcon.setColorFilter(accentColor);
                break;

            case "READY_FOR_PICKUP":
            case "PICKED_UP":
                binding.restaurantIcon.setColorFilter(accentColor);
                binding.line1.setBackgroundColor(accentColor);
                binding.packageIcon.setColorFilter(accentColor);
                break;

            case "ON_THE_WAY":
                binding.restaurantIcon.setColorFilter(accentColor);
                binding.line1.setBackgroundColor(accentColor);
                binding.packageIcon.setColorFilter(accentColor);
                binding.line2.setBackgroundColor(accentColor);
                binding.deliveryIcon.setColorFilter(accentColor);
                break;

            case "DELIVERED":
                binding.restaurantIcon.setColorFilter(accentColor);
                binding.line1.setBackgroundColor(accentColor);
                binding.packageIcon.setColorFilter(accentColor);
                binding.line2.setBackgroundColor(accentColor);
                binding.deliveryIcon.setColorFilter(accentColor);
                binding.line3.setBackgroundColor(accentColor);
                binding.completeIcon.setColorFilter(accentColor);
                break;
        }
    }

    private void addMarkersToMap() {
//        if (mapboxMap == null || trackingInfo == null || symbolManager == null) return;
//
//        // Clear existing markers
//        symbolManager.delete(markers);
//        markers.clear();
//
//        // Add restaurant marker
//        if (trackingInfo.getSourceLatitude() != null && trackingInfo.getSourceLongitude() != null) {
//            Symbol restaurantMarker = symbolManager.create(new SymbolOptions()
//                    .withLatLng(new LatLng(trackingInfo.getSourceLatitude(), trackingInfo.getSourceLongitude()))
//                    .withIconImage(ICON_RESTAURANT_ID)
//                    .withIconSize(1.5f));
//            markers.add(restaurantMarker);
//        }
//
//        // Add destination marker
//        if (trackingInfo.getDestinationLatitude() != null && trackingInfo.getDestinationLongitude() != null) {
//            Symbol destinationMarker = symbolManager.create(new SymbolOptions()
//                    .withLatLng(new LatLng(trackingInfo.getDestinationLatitude(), trackingInfo.getDestinationLongitude()))
//                    .withIconImage(ICON_DESTINATION_ID)
//                    .withIconSize(1.5f));
//            markers.add(destinationMarker);
//        }

        // Add rider marker
        updateRiderLocation(trackingInfo.getCurrentLatitude(), trackingInfo.getCurrentLongitude());

        // Move camera to fit all markers
        zoomToFitMarkers();
    }

    private void updateRiderLocation(Double latitude, Double longitude) {
//        if (mapboxMap == null || symbolManager == null || latitude == null || longitude == null) return;
//
//        LatLng position = new LatLng(latitude, longitude);
//
//        // If rider marker already exists, update its position
//        if (riderSymbol != null) {
//            riderSymbol.setLatLng(position);
//            symbolManager.update(riderSymbol);
//        } else {
//            // Create new rider marker
//            riderSymbol = symbolManager.create(new SymbolOptions()
//                    .withLatLng(position)
//                    .withIconImage(ICON_RIDER_ID)
//                    .withIconSize(1.5f));
//        }
//
//        // Move camera to rider position
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
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
        if (trackingInfo == null) return;

        // For now, we'll just draw a straight line between points
        // In a real application, you'd call a routing API to get actual routing directions

        List<Point> routePoints = new ArrayList<>();

        // Start from restaurant
        if (trackingInfo.getSourceLatitude() != null && trackingInfo.getSourceLongitude() != null) {
            routePoints.add(Point.fromLngLat(trackingInfo.getSourceLongitude(), trackingInfo.getSourceLatitude()));
        }

        // Add rider's current position
        if (trackingInfo.getCurrentLatitude() != null && trackingInfo.getCurrentLongitude() != null) {
            routePoints.add(Point.fromLngLat(trackingInfo.getCurrentLongitude(), trackingInfo.getCurrentLatitude()));
        }

        // End at delivery destination
        if (trackingInfo.getDestinationLatitude() != null && trackingInfo.getDestinationLongitude() != null) {
            routePoints.add(Point.fromLngLat(trackingInfo.getDestinationLongitude(), trackingInfo.getDestinationLatitude()));
        }

        if (routePoints.size() >= 2) {
            // Update the GeoJsonSource with the LineString
            mapboxMap.getStyle(style -> {
                GeoJsonSource source = style.getSourceAs(SOURCE_ID);
                if (source != null) {
                    LineString lineString = LineString.fromLngLats(routePoints);
                    Feature feature = Feature.fromGeometry(lineString);
                    source.setGeoJson(FeatureCollection.fromFeature(feature));
                }
            });
        }
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
                    updateRiderLocation(locationUpdate.getLatitude(), locationUpdate.getLongitude());

                    // Update estimated time
                    if (locationUpdate.getEstimatedArrivalTime() != null) {
                        binding.estimatedTimeText.setText(locationUpdate.getEstimatedArrivalTime());
                    }

                    // Update delivery status if changed
                    if (locationUpdate.getStatus() != null) {
                        updateDeliveryStatus(locationUpdate.getStatus());
                    }

                    // Refresh the route
                    trackingInfo.setCurrentLatitude(locationUpdate.getLatitude());
                    trackingInfo.setCurrentLongitude(locationUpdate.getLongitude());
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
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}