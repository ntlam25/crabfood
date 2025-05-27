package com.example.crabfood.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.adapter.OrderHistoryAdapter;
import com.example.crabfood.cores.TaggedError;
import com.example.crabfood.cores.enums.ErrorSource;
import com.example.crabfood.databinding.FragmentOrderHistoryBinding;
import com.example.crabfood.helpers.CartHelper;
import com.example.crabfood.model.CartItemEntity;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.ui.cart.CartViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private static final String TAG = "Order history";
    private FragmentOrderHistoryBinding binding;
    private OrderViewModel viewModel;
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private CartViewModel cartViewModel;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewHistory;
        emptyView = binding.emptyViewHistory;

        setupRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Share ViewModel with parent fragment
        viewModel = new ViewModelProvider(requireParentFragment()).get(OrderViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        loadData();
        setupSwipeRefresh();
        observeViewModel();
    }

    private void loadData() {
        viewModel.loadUserOrdersHistory();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderHistoryAdapter(
                new OrderHistoryAdapter.OrderHistoryListener() {
                    @Override
                    public void onRateClicked(OrderResponse order) {
                        // Handle rating
                        Snackbar.make(requireView(), "Click vào đánh giá", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReorderClicked(OrderResponse order) {
                        if (cartViewModel.reOrder(requireContext(), order)) {
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.cartFragment);
                        }
                    }
                });
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void observeViewModel() {
        viewModel.getPastOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                adapter.submitList(new ArrayList<>(orders));
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            TaggedError error = event.getContentIfNotHandled();
            if (error != null && error.source == ErrorSource.HISTORY) {
                Toast.makeText(getContext(), error.message, Toast.LENGTH_SHORT).show();
                Snackbar.make(requireView(), error.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}