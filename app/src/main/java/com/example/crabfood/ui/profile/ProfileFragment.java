package com.example.crabfood.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentProfileBinding;
import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.ui.auth.AuthViewModel;
import com.example.crabfood.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private ProfileViewModel viewModel;
    private AuthViewModel authViewModel;

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
        setupInfo();
    }

    private void setupInfo() {
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