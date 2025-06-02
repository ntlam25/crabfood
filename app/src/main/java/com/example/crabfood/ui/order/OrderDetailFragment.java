package com.example.crabfood.ui.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.adapter.CartAdapter;
import com.example.crabfood.adapter.MenuCheckoutAdapter;
import com.example.crabfood.cores.TaggedError;
import com.example.crabfood.cores.enums.ErrorSource;
import com.example.crabfood.databinding.FragmentOrderDetailBinding;
import com.example.crabfood.helpers.CustomDialogConfirm;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.MenuItemProfile;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.model.OrderFood;
import com.example.crabfood.model.OrderFoodChoice;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.enums.MenuAction;
import com.example.crabfood.model.enums.OrderStatus;
import com.example.crabfood.ui.cart.CartItemListFragment;
import com.example.crabfood.ui.vendor.VendorListFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OrderDetailFragment extends Fragment {

    private static final String TAG = "Order Detail";
    private FragmentOrderDetailBinding binding;
    private OrderViewModel viewModel;
    private Long orderId;
    private OrderResponse order;
    private MenuCheckoutAdapter menuCheckoutAdapter;
    private CartAdapter cartAdapter;

    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            orderId = args.getLong("orderId");
            Log.d(TAG, "onCreateView: " + orderId);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(OrderViewModel.class);

        loadData(orderId);
        setupToolbar();
        setupMenuItem();
        setupRecyclerView();
        observe();
    }

    private void setupToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigateUp());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(null, null, false);
        binding.recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrderItems.setAdapter(cartAdapter);
    }

    private void loadData(Long id) {
        viewModel.loadOrderDetail(id);
    }

    private void observe() {
        viewModel.getOrderDetail().observe(getViewLifecycleOwner(), orderResponse -> {
            if (orderResponse != null) {
                order = orderResponse;
                cartAdapter.updateCartItems(orderResponse.getOrderFoods().stream().map(this::fromOrderFood)
                        .collect(Collectors.toList()));
                setupInfo();
                setupButtonClick();
                binding.toolbarDetail.setTitle("#" + order.getOrderNumber());
            }
        });

        viewModel.getCancelledOrder().observe(getViewLifecycleOwner(), orderResponse -> {
            if (orderResponse != null) {
                Toast.makeText(requireContext(), "Huỷ đơn thành công", Toast.LENGTH_SHORT).show();
                Snackbar.make(binding.getRoot(), "Huỷ đơn thành công", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(),event -> {
            TaggedError error = event.getContentIfNotHandled();
            if (error != null && error.source == ErrorSource.DETAIL) {
                Toast.makeText(getContext(), error.message, Toast.LENGTH_SHORT).show();
                Snackbar.make(binding.getRoot(), error.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupInfo(){
        binding.tvOrderStatus.setText(order.getOrderStatus().getDisplayName());
        binding.textViewSubtotal.setText(String.format(Locale.getDefault(), "%,.0f đ", order.getSubtotal()));
        binding.textViewTotal.setText(String.format(Locale.getDefault(), "%,.0f đ", order.getTotalAmount()));
        binding.textViewDeliveryFee.setText(String.format(Locale.getDefault(), "%,.0f đ", order.getDeliveryFee()));
        menuCheckoutAdapter.updateSelectedContentByAction(MenuAction.MY_LOCATIONS, order.getDeliveryAddressText());
        menuCheckoutAdapter.updateSelectedContentByAction
                (MenuAction.PAYMENT_METHODS,
                        order.getPaymentMethod() == "CASH"
                                ? "Tiền mặt" : order.getPaymentMethod());
        menuCheckoutAdapter.updateSelectedContentByAction(MenuAction.MY_PROMOTIONS, order.getCouponId()+"");

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            binding.bottomLayout.setVisibility(View.GONE);
        }
    }

    private void setupMenuItem() {
        RecyclerView recyclerView = binding.recyclerViewOrderItemsMenu;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MenuItemProfile> menuItems = Arrays.asList(
                new MenuItemProfile(R.drawable.ic_map_point_bold, "Giao hàng đến", MenuAction.MY_LOCATIONS, R.color.primary_500, "Chọn địa chỉ của bạn"),
                new MenuItemProfile(R.drawable.ic_payment_bold, "Phương thức thanh toán", MenuAction.PAYMENT_METHODS, R.color.primary_500, "Chọn phương thức thanh toán"),
                new MenuItemProfile(R.drawable.ic_promotion_bold, "Mã giảm giá", MenuAction.MY_PROMOTIONS, R.color.primary_500, "Chọn mã giảm giá")
        );
        recyclerView.setEnabled(false);
        recyclerView.setClickable(false);
        menuCheckoutAdapter = new MenuCheckoutAdapter(menuItems, null);
        recyclerView.setAdapter(menuCheckoutAdapter);
    }
    
    public void setupButtonClick() {
        binding.btnCancel.setOnClickListener(v -> showDialogConfirm(order.getId()));
    }

    private void showDialogConfirm(long orderId) {
        new CustomDialogConfirm(requireContext())
                .setTitle("Xác nhận huỷ đơn hàng")
                .setMessage("Bạn có muốn huỷ đơn này không")
                .setButtonPositiveText("Huỷ đơn")
                .setButtonNegativeText("Huỷ")
                .setListener(() -> {
                    viewModel.cancelOrder(orderId);
                })
                .show();
    }

    public CartItemEntity fromOrderFood(OrderFood orderFood) {
        // Chuyển danh sách OrderFoodChoice thành OptionChoiceResponse
        List<OptionChoiceResponse> selectedOptions = new ArrayList<>();
        for (OrderFoodChoice choice : orderFood.getChoices()) {
            OptionChoiceResponse option = new OptionChoiceResponse();
            option.setId(choice.getChoiceId());
            option.setName(choice.getChoiceName());
            option.setOptionName(choice.getOptionName());
            option.setOptionId(choice.getOptionId());
            option.setPriceAdjustment(choice.getPriceAdjustment());
            selectedOptions.add(option);
        }

        // Tạo đối tượng CartItemEntity
        CartItemEntity item = new CartItemEntity(
                orderFood.getFoodId(),
                orderFood.getFoodName(),
                orderFood.getFoodImageUrl(),
                orderFood.getFoodPrice(),
                orderFood.getQuantity(),
                order.getVendor().getId(),
                selectedOptions
        );
        return item;
    }

}