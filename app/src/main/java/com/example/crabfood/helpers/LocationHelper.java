package com.example.crabfood.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private final Context context;
    private final LocationManager locationManager;
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> locationErrorLiveData = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
            locationLiveData.postValue(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Provider disabled: " + provider);
            locationErrorLiveData.postValue("Location provider disabled: " + provider);
        }
    };

    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationErrorLiveData.setValue("Location permission not granted");
            Log.e(TAG, "Location permissions are not granted");
            return;
        }

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d(TAG, "GPS enabled: " + gpsEnabled + ", Network enabled: " + networkEnabled);

        if (!gpsEnabled && !networkEnabled) {
            locationErrorLiveData.setValue("Location services are disabled");
            Log.e(TAG, "Both GPS and Network providers are disabled");
            return;
        }

        // Thử sử dụng cả LocationManager và FusedLocationProvider để có kết quả tốt hơn

        // 1. Sử dụng LocationManager (API truyền thống)
        try {
            if (networkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                Log.d(TAG, "Requested updates from NETWORK_PROVIDER");
            }

            if (gpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                Log.d(TAG, "Requested updates from GPS_PROVIDER");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates from LocationManager", e);
        }

        // 2. Sử dụng FusedLocationProvider (API hiện đại hơn)
        try {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    .setMinUpdateIntervalMillis(2000)
                    .setMaxUpdateDelayMillis(5000)
                    .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        Location location = locationResult.getLastLocation();
                        Log.d(TAG, "FusedLocation update: " + location.getLatitude() + ", " + location.getLongitude());
                        locationLiveData.postValue(location);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            Log.d(TAG, "Requested updates from FusedLocationProvider");
        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates from FusedLocationProvider", e);
        }

        // Thử lấy vị trí cuối cùng đã biết
        try {
            Location lastKnownLocation = null;

            // Thử từ LocationManager trước
            if (gpsEnabled) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d(TAG, "Last known GPS location: " + (lastKnownLocation != null));
            }

            if (lastKnownLocation == null && networkEnabled) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d(TAG, "Last known Network location: " + (lastKnownLocation != null));
            }

            // Nếu không có từ LocationManager, thử từ FusedLocationProvider
            if (lastKnownLocation == null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d(TAG, "Got last location from FusedLocationProvider: " + location.getLatitude() + ", " + location.getLongitude());
                        locationLiveData.setValue(location);
                    } else {
                        Log.d(TAG, "No last location from FusedLocationProvider");
                    }
                });
            } else {
                locationLiveData.setValue(lastKnownLocation);
                Log.d(TAG, "Using last known location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting last known location", e);
        }
    }

    public void stopLocationUpdates() {
        try {
            locationManager.removeUpdates(locationListener);
            if (locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
            Log.d(TAG, "Location updates stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping location updates", e);
        }
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<String> getLocationErrorLiveData() {
        return locationErrorLiveData;
    }

    public interface LocationCallbackInterface {
        void onLocationResult(Location location);

        void onLocationError(String error);
    }

    public static void getCurrentLocation(Activity activity, LocationCallbackInterface callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Requesting location permissions");
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1001);

            callback.onLocationError("Permission not granted");
            return;
        }

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(activity, "Please enable location services", Toast.LENGTH_LONG).show();
            callback.onLocationError("Location services disabled");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.d(TAG, "Got location from FusedLocationClient: " + location.getLatitude() + ", " + location.getLongitude());
                callback.onLocationResult(location);
            } else {
                Log.d(TAG, "Last location is null, requesting new location");
                requestNewLocation(activity, callback);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get last location", e);
            requestNewLocation(activity, callback);
        });
    }

    private static void requestNewLocation(Context context, LocationCallbackInterface callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationError("Permission not granted");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(5000)
                .setMaxUpdates(1)  // Chỉ yêu cầu một vị trí
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG, "New location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                    callback.onLocationResult(location);
                } else {
                    Log.d(TAG, "New location request returned null");
                    callback.onLocationError("Could not get location");
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            Log.d(TAG, "Requested new location");
        } catch (Exception e) {
            Log.e(TAG, "Error requesting new location", e);
            callback.onLocationError("Error requesting location: " + e.getMessage());
        }
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        try {
            // Thử nhiều provider để tăng khả năng lấy được vị trí
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            return lastKnownLocation;
        } catch (Exception e) {
            Log.e(TAG, "Error getting last known location", e);
            return null;
        }
    }
}