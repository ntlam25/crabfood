package com.example.crabfood.ui.vendor;

import static com.example.crabfood.helpers.TimeHelper.formatOpeningHours;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.adapter.AllCategoryAdapter;
import com.example.crabfood.adapter.FoodPagerAdapter;
import com.example.crabfood.databinding.FragmentVendorDetailBinding;
import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.VendorResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class VendorDetailFragment extends Fragment
    implements AllCategoryAdapter.OnCategoryClickListener{

    private FragmentVendorDetailBinding binding;
    private VendorDetailViewModel viewModel;
    private Long vendorId;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private VendorResponse vendor;

    private AllCategoryAdapter categoryAdapter;
    private List<CategoryResponse> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVendorDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VendorDetailViewModel.class);

        if (getArguments() != null) {
            vendorId = getArguments().getLong("vendorId", 0);
        }
        setupTabLayout();
        setupToolbar();

        setupCategoriesRecycleView();
        observe();
        loadData();
    }

    private void setupTabLayout() {
        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;
        viewPager.setAdapter(new FoodPagerAdapter(this, vendorId));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Phổ biến"); break;
                case 1: tab.setText("Nổi bật"); break;
                case 2: tab.setText("Món mới"); break;
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d("VIEW_PAGER", "Page selected: " + position);
                super.onPageSelected(position);
            }
        });
    }

    private void loadData() {
        if (vendorId != null) {
            viewModel.loadVendor(vendorId);
            viewModel.loadCategoriesByVendorId(vendorId);
        }
    }
    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void setupCategoriesRecycleView(){
        categoryAdapter = new AllCategoryAdapter(categories, this::onCategoryClick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvCategories.setLayoutManager(layoutManager);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void observe() {
        viewModel.getVendor().observe(getViewLifecycleOwner(), vendorResponse -> {
            if (vendorResponse != null) {
                vendor = vendorResponse;
                Log.d("Vendor", vendor.getName());
                setupVendorInfo();
            }
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), categoryResponses -> {
            if (categoryResponses !=null) {
                categories = categoryResponses;
                categoryAdapter.updateData(categories);
            }
        });

        // Observe loading errors
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Log.d("Vendor Detail: ", errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVendorInfo() {
        binding.tvVendorName.setText(vendor.getName());
        binding.ratingBar.setRating(((float) vendor.getRating()));
        binding.tvRating.setText(String.format("%.1f", vendor.getRating()));
        binding.tvCuisineType.setText("Kiểu nhà hàng: "+vendor.getCuisineType());
        binding.tvOpeningHours.setText(formatOpeningHours(vendor.getOpeningTime(), vendor.getClosingTime()));
        binding.tvAddress.setText("Địa chỉ: " + vendor.getAddress());
        binding.tvDescription.setText(vendor.getDescription());
        binding.tvDistance.setText(String.format("%.2f",vendor.getDistance()));


        Glide.with(binding.getRoot().getContext())
                .load(vendor.getCoverImageUrl())
                .placeholder(R.drawable.food_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.ivCoverImage);

        Glide.with(binding.getRoot().getContext())
                .load(vendor.getLogoUrl())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.error_image)
                .circleCrop()
                .into(binding.ivVendorLogo);

        binding.fabActionCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Navigation.findNavController(requireView()).navigate(R.id.action_vendorDetailFragment_to_cartFragment);
            }
        });
    }

    @Override
    public void onCategoryClick(long categoryId, String categoryName) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putLong("categoryId", categoryId);
        args.putString("categoryName", categoryName);
        navController.navigate(R.id.action_to_detail_category, args);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}