package com.example.crabfood.ui.order;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.crabfood.R;
import com.example.crabfood.adapter.OrderPagerAdapter;
import com.example.crabfood.databinding.FragmentOrderBinding;
import com.example.crabfood.helpers.CustomDialogNotify;
import com.example.crabfood.ui.cart.CartViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    private static final String TAG = "OrderFragment";
    private OrderViewModel viewModel;
    private CartViewModel cartViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        setupToolbar();
        // Setup UI components
        setupViews();
        // Check for payment status from arguments
        checkPaymentStatus();

        return binding.getRoot();
    }

    private void setupToolbar() {
        binding.toolbarOrder.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment)
        );
    }

    private void setupViews() {
        // Initialize TabLayout and ViewPager2
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        // Set up the ViewPager with sections adapter
        OrderPagerAdapter pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Đang đợi");
                    break;
                case 1:
                    tab.setText("Lịch sử");
                    break;
            }
        }).attach();


    }

    private void checkPaymentStatus() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("payment_status")) {
            String status = args.getString("payment_status");
            String message = "Thanh toán " + ("PAID".equals(status) ? "thành công" : "thất bại");
            int iconRes = "PAID".equals(status) ? R.drawable.ic_check : R.drawable.ic_cancel;
            showResultDialog("Kết quả thanh toán", message, iconRes);
            args.clear();
        } else if (args != null && args.containsKey("payment_method")) {
            String paymentMethod = args.getString("payment_method");
            if ("CASH".equals(paymentMethod)) {
                showResultDialog("Đặt hàng", "Đặt hàng thành công", R.drawable.ic_check);
                cartViewModel.clearCart();
            }
            args.clear();
        }
    }

    private void showResultDialog(String title, String message, int iconRes) {
        // Display dialog with payment result
        new CustomDialogNotify(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setButtonText("OK")
                .setIconDialog(iconRes)
                .show();
    }
}