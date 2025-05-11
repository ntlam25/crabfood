package com.example.crabfood.ui.vendor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.repository.CategoriesRepository;
import com.example.crabfood.repository.VendorRepository;

import java.util.List;

public class VendorDetailViewModel extends ViewModel {
    private VendorRepository repository;
    private CategoriesRepository categoriesRepository;

    private final MediatorLiveData<VendorResponse> _vendor = new MediatorLiveData<>();
    private final MediatorLiveData<List<CategoryResponse>> _categories = new MediatorLiveData<>();

    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public VendorDetailViewModel() {
        repository = new VendorRepository();
        categoriesRepository = new CategoriesRepository();
    }

    public LiveData<VendorResponse> getVendor() {
        return _vendor;
    }
    public LiveData<List<CategoryResponse>> getCategories() {
        return _categories;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void loadVendor(Long id){
        LiveData<VendorResponse> source = repository.findVendorById(id);

        _vendor.addSource(source, vendorResponse -> {
            if (vendorResponse != null){
                _vendor.postValue(vendorResponse);
            } else {
                _error.postValue("Không thấy dữ liệu nhà hàng");
            }
            _vendor.removeSource(source);
        });
    }

    public void loadCategoriesByVendorId(Long id){
        LiveData<List<CategoryResponse>> source = categoriesRepository.getByVendorIdOrGlobal(id);

        _categories.addSource(source, categoryResponses -> {
            if (categoryResponses != null){
                _categories.postValue(categoryResponses);
            } else {
                _error.postValue("Không thấy dữ liệu danh mục");
            }
            _categories.removeSource(source);
        });
    }
}