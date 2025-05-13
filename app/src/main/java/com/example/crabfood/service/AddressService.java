package com.example.crabfood.service;

import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressService {

    @GET("api/addresses/address-by-user")
    Call<List<AddressResponse>> getUserAddresses();

    @POST("/api/addresses")
    Call<AddressResponse> createAddress(@Body AddressRequest request);
}
