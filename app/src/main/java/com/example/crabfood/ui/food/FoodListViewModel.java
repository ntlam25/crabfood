package com.example.crabfood.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.repository.FoodRepository;

import java.util.List;

public class FoodListViewModel extends ViewModel {
    private FoodRepository repository;
    private final MediatorLiveData<List<FoodResponse>> _foods = new MediatorLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public FoodListViewModel() {
        repository = new FoodRepository();
    }

    public MediatorLiveData<List<FoodResponse>> getFoods() {
        return _foods;
    }

    public MutableLiveData<String> getError() {
        return _error;
    }

    public void loadPopularItems(long vendorId) {
        // Xử lý load dữ liệu phổ biến
        LiveData<List<FoodResponse>> source = repository.findPopularFoods(vendorId);
        _foods.addSource(source, foodResponses -> {
            if (foodResponses != null){
                _foods.postValue(foodResponses);
            } else {
                _error.postValue("Không thấy món ăn phù hợp");
            }
            _foods.removeSource(source);
        });
    }

    public void loadFeaturedItems(long vendorId) {
        // Xử lý load dữ liệu nổi bật
        LiveData<List<FoodResponse>> source = repository.findFeaturedFood(vendorId);
        _foods.addSource(source, foodResponses -> {
            if (foodResponses != null){
                _foods.postValue(foodResponses);
            } else {
                _error.postValue("Không thấy món ăn phù hợp");
            }
            _foods.removeSource(source);
        });
    }

    public void loadNewItems(long vendorId) {
        // Xử lý load dữ liệu món mới
        LiveData<List<FoodResponse>> source = repository.findNewFoods(vendorId);
        _foods.addSource(source, foodResponses -> {
            if (foodResponses != null){
                _foods.postValue(foodResponses);
            } else {
                _error.postValue("Không thấy món ăn phù hợp");
            }
            _foods.removeSource(source);
        });
    }
}