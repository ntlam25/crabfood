package com.example.crabfood.ui.address;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.crabfood.helpers.Resource;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.AddressRequest;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.repository.AddressRepository;
import com.example.crabfood.repository.AuthRepository;

import java.util.List;

public class AddressViewModel extends ViewModel {
    private static final String TAG = "AddressViewModel";
    private final AddressRepository repository;

    private final MutableLiveData<AddressResponse> selectedAddress = new MutableLiveData<>();
    private final MediatorLiveData<Resource<List<AddressResponse>>> _addresses = new MediatorLiveData<>();

    public AddressViewModel() {
        repository = new AddressRepository();
    }

    public LiveData<Resource<AddressResponse>> createAddress(AddressRequest request) {
        return repository.createAddress(request);
    }

    public LiveData<Resource<List<AddressResponse>>> getUserAddresses() {
        LiveData<Resource<List<AddressResponse>>> source = repository.getUserAddresses();
        _addresses.addSource(source, resource -> {
            Log.d(TAG, "LiveData emitted with status: " + resource.getStatus());

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

    public LiveData<AddressResponse> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(AddressResponse address) {
        selectedAddress.setValue(address);
    }
}