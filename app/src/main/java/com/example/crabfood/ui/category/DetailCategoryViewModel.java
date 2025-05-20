package com.example.crabfood.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.repository.CategoriesRepository;
import com.example.crabfood.repository.FoodRepository;

import java.util.List;

public class DetailCategoryViewModel extends ViewModel {
    private final CategoriesRepository categoriesRepository;
    private final FoodRepository foodRepository;

    private final MediatorLiveData<CategoryResponse> _category = new MediatorLiveData<>();
    private final MediatorLiveData<List<FoodResponse>> _foods = new MediatorLiveData<>();

    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public DetailCategoryViewModel() {
        categoriesRepository = new CategoriesRepository();
        foodRepository = new FoodRepository();
    }

    public LiveData<CategoryResponse> getCategory() {
        return _category;
    }

    public LiveData<List<FoodResponse>> getFoods() {
        return _foods;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void getCategoryById(Long id) {
        LiveData<CategoryResponse> source = categoriesRepository.getCategoryById(id);

        _category.addSource(source, categories -> {
            if (categories != null) {
                _category.postValue(categories);
            } else {
                _error.postValue("Tải danh sách danh mục thất bại");
            }
            _category.removeSource(source);
        });
    }

    public void getFoodByCategoryId(Long id) {
        LiveData<List<FoodResponse>> source = foodRepository.findByCategoryId(id);

        _foods.addSource(source, foodResponses -> {
            if (foodResponses != null) {
                _foods.postValue(foodResponses);
            } else {
                _error.postValue("Tải danh sách thức ăn thất bại");
            }
            _foods.removeSource(source);
        });
    }

    public void getFoodByVendorIdAndCategoryId(Long vendorId, Long categoryId) {
        LiveData<List<FoodResponse>> source = foodRepository.findByVendorIdAndCategoryId(vendorId, categoryId);

        _foods.addSource(source, foodResponses -> {
            if (foodResponses != null) {
                _foods.postValue(foodResponses);
            } else {
                _error.postValue("Tải danh sách thức ăn thất bại");
            }
            _foods.removeSource(source);
        });
    }
}