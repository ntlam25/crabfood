package com.example.crabfood.ui.vendor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.repository.VendorRepository;

import java.util.List;

public class AllVendorNearbyViewModel extends ViewModel {
    private VendorRepository repository;
    private final MediatorLiveData<List<VendorResponse>> _vendors = new MediatorLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public AllVendorNearbyViewModel() {
        repository = new VendorRepository();
    }

    public LiveData<List<VendorResponse>> getVendors() {
        return _vendors;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void loadVendors(double latitude, double longitude, double radius,
                            Integer limit,
                            Integer offset,
                            String cuisineType,
                            Double minRating,
                            Boolean isOpen,
                            String sortBy){
        // Load vendors
        LiveData<List<VendorResponse>> source = repository.getNearbyVendors(
                latitude, longitude, radius, limit,
                offset, cuisineType, minRating, isOpen, sortBy);

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