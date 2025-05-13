package com.example.crabfood.ui.address;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.crabfood.R;
import com.example.crabfood.adapter.AddressAdapter;
import com.example.crabfood.databinding.FragmentAddressBinding;
import com.example.crabfood.model.AddressResponse;

import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends Fragment implements
        AddressAdapter.OnAddressSelectedListener {

    private final String TAG = "Address Fragment";
    private FragmentAddressBinding binding;
    private AddressViewModel viewModel;
    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private List<AddressResponse> addresses = new ArrayList<>();
    private AddressResponse selectedAddress = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);

        rvAddresses = binding.rvAddresses;

        setupToolbar();
        observe();
    }

    private void observe() {
        viewModel.getUserAddresses().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    // show loading
                    break;
                case SUCCESS:
                    addresses = resource.getData();

                    // Kiểm tra xem có địa chỉ nào isDefault = true không
                    // Tìm địa chỉ mặc định
                    selectedAddress = findDefaultAddress(addresses);
                    // Nếu có địa chỉ default, gán làm selected address
                    // Nếu không, để selected address là null
                    viewModel.setSelectedAddress(selectedAddress);
                    setupRecyclerView();
                    setupButtons();

                    break;
                case ERROR:
                    // thông báo lỗi
                    break;
            }
        });

        viewModel.getSelectedAddress().observe(getViewLifecycleOwner(), addressResponse -> {
            if (addressResponse != null) {
                selectedAddress = addressResponse;

                // Cập nhật selectedId trong adapter
                if (addressAdapter != null) {
                    addressAdapter.setSelectedId(addressResponse.getId());
                }
            }
        });
    }

    private void setupRecyclerView() {
        addressAdapter = new AddressAdapter(addresses, this::onAddressSelected,
                selectedAddress != null ? selectedAddress.getId() : 0);
        rvAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAddresses.setAdapter(addressAdapter);
    }

    private void setupButtons() {
        binding.buttonAddAddress.setOnClickListener(v -> {
            // Handle add new address
            showAddAddressDialog();
        });

        binding.buttonApply.setOnClickListener(v -> {
            if (selectedAddress != null) {
                // Nếu không có địa chỉ tạm thời nhưng có địa chỉ được chọn
                Log.d("Address Frag", "setupButtons: " + selectedAddress.getLabel());
                viewModel.setSelectedAddress(selectedAddress);
                addressAdapter.setSelectedId(selectedAddress.getId());
                Toast.makeText(requireContext(), "Đã chọn: " +
                        selectedAddress.getLabel(), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireContext(), "Hãy chọn 1 địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        binding.toolbarAddress.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void showAddAddressDialog() {
        // Implement your add address dialog
    }

    private void showEditAddressDialog(AddressResponse address) {
        // Implement your edit address dialog
    }

    private AddressResponse findDefaultAddress(List<AddressResponse> addresses) {
        if (addresses != null) {
            for (AddressResponse addr : addresses) {
                if (Boolean.TRUE.equals(addr.isDefault())) {
                    return addr;
                }
            }
        }
        return null;
    }

    @Override
    public void onAddressSelected(AddressResponse address) {
        viewModel.setSelectedAddress(address);
    }
}