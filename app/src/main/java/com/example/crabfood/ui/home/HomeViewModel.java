package com.example.crabfood.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.repository.HomeRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final HomeRepository repository;
    private final MediatorLiveData<List<Object>> _categories = new MediatorLiveData<>();
    private final MediatorLiveData<List<VendorResponse>> _vendors = new MediatorLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public HomeViewModel() {
        repository = new HomeRepository();
    }

    public LiveData<List<Object>> getCategories() {
        return _categories;
    }

    public LiveData<List<VendorResponse>> getVendors() {
        return _vendors;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void loadCategories(){
        // Load categories
        LiveData<List<CategoryResponse>> source = repository.getCategories();

        _categories.addSource(source, categories -> {
            if (categories != null) {
                List<Object> combinedList = new ArrayList<>(categories);
                Log.d("HomeVM", combinedList.size() + "");
                combinedList.add("Xem thêm");
                _categories.postValue(combinedList);
            } else {
                _error.postValue("Tải danh sách danh mục thất bại");
            }
            _categories.removeSource(source);
        });
    }
    public void loadVendors(double lat, double lng, double radius) {
        // Load vendors
        LiveData<List<VendorResponse>> source = repository.getNearbyVendorsDefault(lat, lng, radius);

        _vendors.addSource(source, vendors -> {
            if (vendors != null) {
                _vendors.postValue(vendors);
            } else {
                _error.postValue("Tải danh sách nhà hàng thất bại");
            }
            _vendors.removeSource(source);
        });
    }
}