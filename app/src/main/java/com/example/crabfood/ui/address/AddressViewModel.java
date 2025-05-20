package com.example.crabfood.ui.address;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.repository.AddressRepository;

import java.util.List;

public class AddressViewModel extends ViewModel {
    private static final String TAG = "AddressViewModel";
    private final AddressRepository repository;

    private final MutableLiveData<AddressResponse> selectedAddress = new MutableLiveData<>();
    private final MutableLiveData<AddressResponse> createdAddress = new MutableLiveData<>();
    private final MediatorLiveData<Resource<List<AddressResponse>>> _addresses = new MediatorLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public AddressViewModel() {
        repository = new AddressRepository();
    }

    public LiveData<String> getError() {
        return _error;
    }

    public LiveData<AddressResponse> getAddressCreated(){
        return createdAddress;
    }

    public LiveData<Resource<List<AddressResponse>>> getUserAddresses() {
        LiveData<Resource<List<AddressResponse>>> source = repository.getUserAddresses();
        _addresses.addSource(source, resource -> {
            Log.d(TAG, "getUserAddresses: " + resource.getStatus());

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                // Tìm địa chỉ mặc định
                for (AddressResponse addr : resource.getData()) {
                    if (Boolean.TRUE.equals(addr.isDefault())) {
                        selectedAddress.setValue(addr);
                        break;
                    }
                }
            }

            // Gán giá trị cho _addresses để fragment quan sát
            _addresses.setValue(resource);

            // Xóa source nếu chỉ muốn lắng nghe một lần (không bắt buộc)
            _addresses.removeSource(source);
        });
        return source;
    }
    public void addAddress(AddressRequest request) {
        repository.createAddress(request, new AddressRepository.AddressCallback() {
            @Override
            public void onSuccess(AddressResponse response) {
                createdAddress.postValue(response);
            }

            @Override
            public void onFailure(String message) {
                _error.postValue(message);
            }
        });
    }

    public LiveData<AddressResponse> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(AddressResponse address) {
        selectedAddress.setValue(address);
    }
}