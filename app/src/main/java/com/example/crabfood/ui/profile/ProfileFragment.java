package com.example.crabfood.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.adapter.MenuProfileAdapter;
import com.example.crabfood.databinding.FragmentProfileBinding;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.MenuItemProfile;
import com.example.crabfood.model.UserResponse;
import com.example.crabfood.model.enums.MenuAction;
import com.example.crabfood.ui.auth.AuthViewModel;
import com.example.crabfood.ui.auth.LoginActivity;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private ProfileViewModel viewModel;
    private AuthViewModel authViewModel;
    private UserResponse user;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupToolbar();
        observe();
        setupClickLogout();
        setupListMenu();
    }

    private void observe() {
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), userResponse -> {
            if (userResponse != null){
                user = userResponse;
                setupInfo();
            }
        });
    }

    private void setupInfo() {
        binding.profileName.setText(user.getFullName());
        binding.tvPhoneNumber.setText(user.getPhone());
        binding.tvEmail.setText(user.getEmail());

        Glide.with(binding.getRoot().getContext())
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.avata_placeholder)
                .error(R.drawable.avatar_error)
                .into(binding.imageViewAvatar);
    }

    private void setupToolbar() {
        binding.toolbarProfile.setNavigationOnClickListener(v ->
            Navigation.findNavController(requireView()).navigateUp());
    }

    private void setupListMenu() {
        RecyclerView recyclerView = binding.rvListMenu;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MenuItemProfile> menuItems = Arrays.asList(
                new MenuItemProfile(R.drawable.map_point, "Quản lý địa chỉ", MenuAction.MY_LOCATIONS),
                new MenuItemProfile(R.drawable.ic_promotion, "Mã giảm giá", MenuAction.MY_PROMOTIONS),
                new MenuItemProfile(R.drawable.ic_payment_svg, "Phương thức thanh toán", MenuAction.PAYMENT_METHODS),
                new MenuItemProfile(R.drawable.ic_message, "Tin nhắn", MenuAction.MESSAGES),
                new MenuItemProfile(R.drawable.ic_help_center, "Trung tâm hỗ trợ", MenuAction.HELP_CENTER)
        );
        MenuProfileAdapter adapter = new MenuProfileAdapter(menuItems, action -> {
            switch (action) {
                case MY_LOCATIONS:
                    Navigation.findNavController(requireView()).navigate(R.id.action_to_my_address);
                    break;
                case MY_PROMOTIONS:
                    // TODO: open promotions screen
                    break;
                case PAYMENT_METHODS:
                    // ...
                    break;
                case HELP_CENTER:
                    // ...
                    break;
                // xử lý các case khác
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupClickLogout() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            // TODO: Thực hiện xóa session/token
            authViewModel.logout();

            // Ví dụ: chuyển về màn hình login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            dialog.dismiss();
        });

        dialog.show();
    }
}