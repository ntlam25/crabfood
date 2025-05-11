package com.example.crabfood;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.crabfood.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private long backPressedTime = 0;
    private Toast toast;
//    private CartViewModel cartViewModel;

    // Danh sách 5 ID fragment cần hiển thị Bottom Navigation
    private final Set<Integer> fragmentsWithBottomNav = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.ordersFragment,
            R.id.favoriteFragment,
            R.id.cartFragment,
            R.id.profileFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setupCart();
        setContentView(binding.getRoot());
        setupNavigationView();
    }

    private void setupCart() {
        // Khởi tạo CartViewModel cho toàn ứng dụng
//        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
//
//        // Đảm bảo giỏ hàng được đồng bộ
//        cartViewModel.refreshCartData();
//
//        // Quan sát số lượng sản phẩm trong giỏ hàng để hiển thị badge
//        cartViewModel.getCartItems().observe(this, cartItems -> {
//            // Cập nhật badge giỏ hàng
//            updateCartBadge(cartItems.size());
//        });
    }
    private void updateCartBadge(int itemCount) {
        // Cập nhật badge trên biểu tượng giỏ hàng
        // Tùy thuộc vào cách triển khai UI
    }

    private void setupNavigationView() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        // Kết nối Bottom Navigation với Navigation Controller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int destinationId = navController.getCurrentDestination().getId();

            if (item.getItemId() == R.id.homeFragment && destinationId != R.id.homeFragment) {
                navController.popBackStack(R.id.homeFragment, false);
                return true;
            }

            // Cho phép NavigationUI xử lý bình thường
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             Bundle arguments) {
                // Kiểm tra xem fragment hiện tại có thuộc 5 fragment cần hiển thị không
                if (fragmentsWithBottomNav.contains(destination.getId())) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);

                // Nếu không phải HomeFragment, pop back stack như bình thường
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.homeFragment) {
                    navController.popBackStack();
                }
                // Nếu đang ở HomeFragment, kiểm tra 2 lần back để thoát
                else {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        // Thoát app nếu bấm Back lần 2 trong 2 giây
                        if (toast != null) toast.cancel();
                        finish();
                    } else {
                        // Hiện thông báo "Bấm Back lần nữa để thoát"
                        toast = Toast.makeText(MainActivity.this, "Nhấn BACK lần nữa để thoát", Toast.LENGTH_SHORT);
                        toast.show();
                        backPressedTime = System.currentTimeMillis();
                    }
                }
            }
        });
    }
}
