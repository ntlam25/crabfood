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
import com.example.crabfood.adapter.UpcomingOrderAdapter;
import com.example.crabfood.cores.TaggedError;
import com.example.crabfood.cores.enums.ErrorSource;
import com.example.crabfood.databinding.FragmentUpcomingBinding;
import com.example.crabfood.helpers.CustomDialogConfirm;
import com.example.crabfood.model.OrderResponse;
import com.example.crabfood.model.enums.OrderStatus;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {
    private FragmentUpcomingBinding binding;
    private OrderViewModel viewModel;
    private RecyclerView recyclerView;
    private UpcomingOrderAdapter adapter;
    private TextView emptyView;
    private List<OrderResponse> orderResponses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerViewUpcoming;
        emptyView = binding.emptyViewUpcoming;

        setupRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Share ViewModel with parent fragment
        viewModel = new ViewModelProvider(requireParentFragment()).get(OrderViewModel.class);
        loadData();
        setupSwipeRefresh();
        observeViewModel();
    }
    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        viewModel.loadUserOrdersUpcoming();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UpcomingOrderAdapter(
                new UpcomingOrderAdapter.OrderActionListener() {
                    @Override
                    public void onCancelClicked(OrderResponse order) {
                        showDialogConfirm(order.getId());
                    }

                    @Override
                    public void onTrackClicked(OrderResponse order) {
                        viewModel.trackOrder(order.getId());
                    }

                    @Override
                    public void onItemClicked(OrderResponse order) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("orderId", order.getId());
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.orderDetailFragment, bundle);
                    }
                });
        recyclerView.setAdapter(adapter);
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

    private void observeViewModel() {
        viewModel.getUpcomingOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                orderResponses = orders;
                adapter.submitList(orders);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getCancelledOrder().observe(getViewLifecycleOwner(), orderResponse -> {
            if (orderResponse != null && orderResponse.getOrderStatus() == OrderStatus.CANCELLED) {
                Toast.makeText(requireContext(), "Huỷ đơn thành công", Toast.LENGTH_SHORT).show();
                Snackbar.make(binding.getRoot(), "Huỷ đơn thành công", Snackbar.LENGTH_SHORT).show();

            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            TaggedError error = event.getContentIfNotHandled();
            if (error != null && error.source == ErrorSource.UPCOMING) {
                Toast.makeText(getContext(), error.message, Toast.LENGTH_SHORT).show();
                Snackbar.make(binding.getRoot(), error.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}