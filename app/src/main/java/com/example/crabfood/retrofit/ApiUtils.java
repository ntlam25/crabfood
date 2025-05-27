package com.example.crabfood.retrofit;

import com.example.crabfood.service.AddressService;
import com.example.crabfood.service.AuthService;
import com.example.crabfood.service.CartService;
import com.example.crabfood.service.CategoryService;
import com.example.crabfood.service.FoodService;
import com.example.crabfood.service.GoongGeocodeService;
import com.example.crabfood.service.OrderService;
import com.example.crabfood.service.OrderTrackingService;
import com.example.crabfood.service.UserService;
import com.example.crabfood.service.VendorService;

public class ApiUtils {
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static FoodService getFoodService()
    {
        return RetrofitClient.getClient(BASE_URL).create(FoodService.class);
    }
    public static CategoryService getCategoryService()
    {
        return RetrofitClient.getClient(BASE_URL).create(CategoryService.class);
    }

    public static VendorService getVendorService()
    {
        return RetrofitClient.getClient(BASE_URL).create(VendorService.class);
    }

    public static CartService getCartService()
    {
        return RetrofitClient.getClient(BASE_URL).create(CartService.class);
    }

    public static AuthService getAuthService()
    {
        return RetrofitClient.getClient(BASE_URL).create(AuthService.class);
    }

    public static AddressService getAddressService()
    {
        return RetrofitClient.getClient(BASE_URL).create(AddressService.class);
    }

    public static UserService getUserService()
    {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static OrderService getOrderService()
    {
        return RetrofitClient.getClient(BASE_URL).create(OrderService.class);
    }

    public static OrderTrackingService getOrderTrackingService()
    {
        return RetrofitClient.getClient(BASE_URL).create(OrderTrackingService.class);
    }
}
