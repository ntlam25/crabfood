package com.example.crabfood.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.repository.CategoriesRepository;

import java.util.List;

public class AllCategoriesViewModel extends ViewModel {
    private final CategoriesRepository repository;

    private final MediatorLiveData<List<CategoryResponse>> _categories = new MediatorLiveData<>();

    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public AllCategoriesViewModel() {
        repository = new CategoriesRepository();
    }

    public LiveData<List<CategoryResponse>> getCategories() {
        return _categories;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public void loadCategories(){
        LiveData<List<CategoryResponse>> source = repository.getAllCategories();
        // Load categories
        _categories.addSource(source, categories -> {
            if (categories != null) {
                _categories.postValue(categories);
            } else {
                _error.postValue("Tải tất cả danh mục thất bại");
            }
            _categories.removeSource(source);
        });
    }

}