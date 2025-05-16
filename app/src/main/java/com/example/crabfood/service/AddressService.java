package com.example.crabfood.service;

import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.model.GeocodingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AddressService {

    @GET("api/addresses/address-by-user")
    Call<List<AddressResponse>> getUserAddresses();

    @POST("/api/addresses")
    Call<AddressResponse> createAddress(@Body AddressRequest request);

    @GET("geocoding/v5/mapbox.places/{searchText}.json")
    Call<GeocodingResponse> searchAddress(
            @Query("access_token") String accessToken,
            @Query("country") String country,
            @Query("language") String language,
            @Query("limit") int limit
    );

    // API để reverse geocoding (lấy địa chỉ từ tọa độ)
    @GET("geocoding/v5/mapbox.places/{longitude},{latitude}.json")
    Call<GeocodingResponse> getAddressFromCoordinates(
            @Query("access_token") String accessToken,
            @Query("language") String language
    );
}
