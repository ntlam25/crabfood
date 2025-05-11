package com.example.crabfood.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.repository.CategoriesRepository;
import com.example.crabfood.repository.FoodRepository;
import com.example.crabfood.repository.VendorRepository;

import java.util.List;

public class FoodDetailViewModel extends ViewModel {
    private FoodRepository repository;

    private final MediatorLiveData<FoodResponse> _food = new MediatorLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<Long> vendorId = new MutableLiveData<>();

    public FoodDetailViewModel() {
        repository = new FoodRepository();
    }

    public LiveData<FoodResponse> getFoods() {
        return _food;
    }

    public LiveData<String> getError() {
        return _error;
    }
    public LiveData<Integer> getQuantity() {
        return quantity;
    }

    public LiveData<Long> getVendorId() {
        return vendorId;
    }

    public void loadFood(Long id){
        LiveData<FoodResponse> source = repository.findById(id);

        _food.addSource(source, foodResponse -> {
            if (foodResponse != null){
                _food.postValue(foodResponse);
                vendorId.setValue(foodResponse.getVendorId());
            } else {
                _error.postValue("Không thấy dữ liệu món ăn");
            }
            _food.removeSource(source);
        });
    }

    public void increaseQuantity() {
        if (quantity.getValue() != null) {
            int current = quantity.getValue();
            if (current < 99) { // Set a reasonable upper limit
                quantity.setValue(current + 1);
            }
        }
    }

    public void decreaseQuantity() {
        Integer currentQuantity = quantity.getValue();
        if (currentQuantity != null && currentQuantity > 1) {
            quantity.setValue(currentQuantity - 1);
        }
    }

    public void setQuantity(int newQuantity) {
        if (newQuantity > 0) {
            quantity.setValue(newQuantity);
        }
    }
}