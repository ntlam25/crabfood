package com.example.crabfood.ui.checkout;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.crabfood.R;
import com.example.crabfood.adapter.CartAdapter;
import com.example.crabfood.adapter.MenuCheckoutAdapter;
import com.example.crabfood.databinding.FragmentCheckoutBinding;
import com.example.crabfood.model.AddressResponse;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.MenuItemProfile;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.model.enums.MenuAction;
import com.example.crabfood.ui.address.AddressViewModel;
import com.example.crabfood.ui.cart.CartViewModel;
import com.example.crabfood.ui.vendor.VendorDetailViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class CheckoutFragment extends Fragment implements CartAdapter.CartItemListener {

    private FragmentCheckoutBinding binding;
    private CheckoutViewModel viewModel;

    private CartViewModel cartViewModel;
    private VendorDetailViewModel vendorViewModel;
    private CartAdapter cartAdapter;
    private MenuCheckoutAdapter menuCheckoutAdapter;
    private VendorResponse vendor;
    private Long vendorId;
    private Double subTotal;
    private Double total;
    private Double deliveryFee;
    private AddressViewModel sharedAddressViewModel;

    private AddressResponse address;
    private List<CartItemEntity> cartItemEntities;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        vendorViewModel = new ViewModelProvider(requireActivity()).get(VendorDetailViewModel.class);
        sharedAddressViewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);

        setupToolbar();
        setupMenuItem();
        setupRecyclerViewItem();
        setupOrderButton();
        observeData();
    }

    private void setupOrderButton() {
        binding.buttonPlaceOrder.setOnClickListener(v -> {
            if (validateOrder()) {
                placeOrder();
            }
        });
    }

    private boolean validateOrder() {
        if (address == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(),binding.getRoot(), "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (subTotal == null || subTotal <= 0) {
            Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(),binding.getRoot(),"Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra xem đã chọn phương thức thanh toán chưa
        if (viewModel.getSelectedPaymentMethod().getValue() == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(),binding.getRoot(),"Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        // Hiển thị xác nhận đặt hàng
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận đặt hàng")
                .setMessage("Bạn có chắc chắn muốn đặt đơn hàng này?")
                .setPositiveButton("Đặt hàng", (dialog, which) -> {
                    // Gửi yêu cầu đặt hàng đến ViewModel
                    viewModel.placeOrder(address, subTotal, total, cartItemEntities);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void calculateTotalOrder() {
        if (subTotal != null && deliveryFee != null) {
            // Lấy giá trị giảm giá từ ViewModel (nếu có)
            Double discountAmount = viewModel.getDiscountAmount().getValue() != null ?
                    viewModel.getDiscountAmount().getValue() : 0.0;

            // Tính tổng tiền sau khi trừ giảm giá
            total = subTotal + deliveryFee - discountAmount;
            binding.textViewTotal.setText(String.format("%,.0f đ", total));
            binding.textViewBottomTotal.setText(String.format("%,.0f đ", total));

            // Hiển thị giảm giá nếu có
            if (discountAmount > 0) {
                binding.textViewDiscount.setVisibility(View.VISIBLE);
//                binding.textViewDiscountLabel.setVisibility(View.VISIBLE);
                binding.textViewDiscount.setText(String.format("-%,.0f đ", discountAmount));
            } else {
                binding.textViewDiscount.setVisibility(View.GONE);
//                binding.textViewDiscountLabel.setVisibility(View.GONE);
            }
        }
    }

    private void observeData() {
        // Observe CartViewModel data
        cartViewModel.getCurrentVendorIdLive().observe(getViewLifecycleOwner(), aLong -> {
            if (aLong != null) {
                vendorId = aLong;
                vendorViewModel.loadVendor(vendorId);
                vendorViewModel.getVendor().observe(getViewLifecycleOwner(), vendorResponse -> {
                    if (vendorResponse != null) {
                        vendor = vendorResponse;
                        deliveryFee = viewModel.calculateDeliveryFee(
                                vendor.getDistance(),
                                vendor.getDeliveryFee(),
                                vendor.getServiceRadiusKm(),
                                false
                        );
                        binding.textViewDeliveryFee.setText(String.format("%,.0f đ", deliveryFee));
                        calculateTotalOrder();
                    }
                });
            }
        });

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            cartItemEntities = cartItems;
            cartAdapter.updateCartItems(cartItems);
        });

        cartViewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            subTotal = totalPrice;
            binding.textViewSubtotal.setText(String.format("%,.0f đ", totalPrice));
            calculateTotalOrder();
        });

        // Observe address changes
        sharedAddressViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                this.address = address;
                menuCheckoutAdapter.updateSelectedContentByAction(MenuAction.MY_LOCATIONS, address.getFullAddress());
            }
        });

        // Observe payment method changes
        viewModel.getSelectedPaymentMethod().observe(getViewLifecycleOwner(), paymentMethod -> {
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                String displayText = paymentMethod.equals("CASH") ? "Tiền mặt" : paymentMethod;
                menuCheckoutAdapter.updateSelectedContentByAction(MenuAction.PAYMENT_METHODS, displayText);
            }
        });

        // Observe coupon code changes
        viewModel.getCouponCode().observe(getViewLifecycleOwner(), code -> {
            if (code != null && !code.isEmpty()) {
                menuCheckoutAdapter.updateSelectedContentByAction(MenuAction.MY_PROMOTIONS, code);
            }
        });

        // Observe discount amount changes
        viewModel.getDiscountAmount().observe(getViewLifecycleOwner(), discount -> {
            calculateTotalOrder();
        });

        // Observe Order State for handling the order process
        viewModel.getOrderState().observe(getViewLifecycleOwner(), orderState -> {
            switch (orderState) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    handleOrderSuccess();
                    break;
                case ERROR:
                    showLoading(false);
                    handleOrderError();
                    break;
                case IDLE:
                    showLoading(false);
                    break;
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.buttonPlaceOrder.setEnabled(!isLoading);
    }

    private void handleOrderSuccess() {
        viewModel.getPlacedOrder().observe(getViewLifecycleOwner(), orderResponse -> {
            if (orderResponse != null) {
                // Navigate to order success screen with order ID
                Snackbar.make(binding.getRoot(), "Đặt hàng thành công!", Snackbar.LENGTH_LONG).show();
                cartViewModel.clearCart();
                Bundle bundle = new Bundle();
                bundle.putLong("orderId", orderResponse.getId());
                Navigation.findNavController(requireView()).navigate(
                        R.id.homeFragment);
            }
        });
    }

    private void handleOrderError() {
        viewModel.getOrderError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerViewItem() {
        cartAdapter = new CartAdapter(null, this);
        binding.recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrderItems.setAdapter(cartAdapter);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void setupMenuItem() {
        RecyclerView recyclerView = binding.recyclerViewListMenu;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MenuItemProfile> menuItems = Arrays.asList(
                new MenuItemProfile(R.drawable.ic_map_point_bold, "Giao hàng đến", MenuAction.MY_LOCATIONS, R.color.primary_500, "Chọn địa chỉ của bạn"),
                new MenuItemProfile(R.drawable.ic_payment_bold, "Phương thức thanh toán", MenuAction.PAYMENT_METHODS, R.color.primary_500, "Chọn phương thức thanh toán"),
                new MenuItemProfile(R.drawable.ic_promotion_bold, "Mã giảm giá", MenuAction.MY_PROMOTIONS, R.color.primary_500, "Chọn mã giảm giá")
        );
        menuCheckoutAdapter = new MenuCheckoutAdapter(menuItems, action -> {
            switch (action) {
                case MY_LOCATIONS:
                    Navigation.findNavController(requireView()).navigate(R.id.action_to_my_address);
                    break;
                case MY_PROMOTIONS:
                    navigateToPromotionScreen();
                    break;
                case PAYMENT_METHODS:
                    showPaymentMethodSelector();
                    break;
            }
        });

        recyclerView.setAdapter(menuCheckoutAdapter);
    }

    private void navigateToPromotionScreen() {
        // Có thể chuyển đến màn hình chọn mã giảm giá
        // Hoặc hiển thị dialog chọn mã giảm giá đơn giản
        Toast.makeText(requireContext(), "Đang phát triển tính năng mã giảm giá", Toast.LENGTH_SHORT).show();
    }

    private void showPaymentMethodSelector() {
        String[] paymentMethods = {"Tiền mặt", "Chuyển khoản ngân hàng"};
        String[] paymentValues = {"CASH", "BANK_TRANSFER"};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chọn phương thức thanh toán")
                .setSingleChoiceItems(paymentMethods, getSelectedPaymentMethodIndex(paymentValues), (dialog, which) -> {
                    viewModel.setSelectedPaymentMethod(paymentValues[which]);
                    dialog.dismiss();
                })
                .show();
    }

    private int getSelectedPaymentMethodIndex(String[] values) {
        String currentMethod = viewModel.getSelectedPaymentMethod().getValue();
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(currentMethod)) {
                return i;
            }
        }
        return 0; // Default to first option
    }

    @Override
    public void onQuantityChanged(long itemId, int quantity) {
        cartViewModel.updateItemQuantity(itemId, quantity);
    }

    @Override
    public void onRemoveItem(long itemId) {
        cartViewModel.removeItem(itemId);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment becomes visible
        cartViewModel.refreshCartData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}