package com.example.crabfood.ui.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentOrderTrackingBinding;

public class OrderTrackingActivity extends AppCompatActivity {
    private FragmentOrderTrackingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}