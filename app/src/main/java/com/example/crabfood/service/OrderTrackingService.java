package com.example.crabfood.service;

import android.content.Context;

import com.example.crabfood.helpers.GeoJsonUtil;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.LocationUpdateRequest;
import com.example.crabfood.model.LocationUpdateResponse;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.OrderTrackingInfo;
import com.example.crabfood.model.mapsbox.RouteResponse;
import com.example.crabfood.retrofit.ApiUtils;
import com.example.crabfood.retrofit.RetrofitInstance;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderTrackingService {

    /**
     * Get tracking information for an order
     *
     * @param orderId The ID of the order to track
     * @param customerId The ID of the customer requesting the information
     * @return Order tracking information
     */
    @GET("api/tracking/orders/{orderId}")
    Call<OrderTrackingInfo> getOrderTrackingInfo(
            @Path("orderId") long orderId,
            @Query("customerId") long customerId
    );

    /**
     * Update the location of a rider
     *
     * @param request Location update information
     * @return Location update response
     */
    @POST("api/tracking/location/update")
    Call<LocationUpdateResponse> updateRiderLocation(@Body LocationUpdateRequest request);
}