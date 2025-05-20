package com.example.crabfood.service;

import com.example.crabfood.model.AutoCompleteResponse;
import com.example.crabfood.model.DirectionResponse;
import com.example.crabfood.model.GeocodeResponse;
import com.example.crabfood.model.PlaceDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoongGeocodeService {
    @GET("Place/AutoComplete")
    Call<AutoCompleteResponse> getAutoComplete(
            @Query("input") String input,
            @Query("api_key") String apiKey
    );

    @GET("Place/Detail")
    Call<PlaceDetailResponse> getPlaceDetail(
            @Query("place_id") String placeId,
            @Query("api_key") String apiKey
    );

    @GET("Geocode")
    Call<GeocodeResponse> reverseGeocode(
            @Query("latlng") String latlng,
            @Query("api_key") String apiKey
    );

    @GET("Direction")
    Call<DirectionResponse> getDirection(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("vehicle") String vehicle,
            @Query("api_key") String apiKey
    );
}