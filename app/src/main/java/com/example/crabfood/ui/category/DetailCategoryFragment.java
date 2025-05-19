package com.example.crabfood.ui.category;

import static com.example.crabfood.helpers.StringHelper.removeDiacritics;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.adapter.FoodAdapter;
import com.example.crabfood.databinding.FragmentDetailCategoryBinding;
import com.example.crabfood.model.CategoryResponse;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.helpers.KeyboardHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class DetailCategoryFragment extends Fragment
    implements FoodAdapter.OnFoodClickListener{

    private FragmentDetailCategoryBinding binding;
    private DetailCategoryViewModel viewModel;
    private FoodAdapter foodAdapter;
    private List<FoodResponse> foodList = new ArrayList<>();
    private List<FoodResponse> filteredFoodList = new ArrayList<>();

    private CategoryResponse category;
    private long categoryId;
    private String categoryName;

    public static DetailCategoryFragment newInstance() {
        return new DetailCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardHelper.hideKeyboardOnClickOutside(binding.getRoot(), requireActivity());
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DetailCategoryViewModel.class);

        // Get categoryId and categoryName from arguments
        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", 1L);
            categoryName = getArguments().getString("categoryName", "Food");
        }
        // Setup RecyclerView
        setupRecyclerView();

        // Setup toolbar
        setupToolbar();

        // Setup search functionality
        setupSearchView();

        loadData();

        observe();

        binding.fabActionCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_cart_fragment);
            }
        });
    }

    private void loadData() {
        // Load data
        viewModel.getCategoryById(categoryId);
        viewModel.getFoodByCategoryId(categoryId);
    }

    private void observe() {
        // Observe category data
        viewModel.getCategory().observe(getViewLifecycleOwner(), categoryResponse -> {
            if (categoryResponse != null) {
                category = categoryResponse;
                updateToolbar();
            }
        });

        // Observe foods data
        viewModel.getFoods().observe(getViewLifecycleOwner(), foodResponses -> {
            if (foodResponses != null && !foodResponses.isEmpty()) {
                foodList.clear();
                foodList.addAll(foodResponses);
                filteredFoodList.clear();
                filteredFoodList.addAll(foodResponses);
                foodAdapter.submitList(filteredFoodList);
                binding.tvNoResults.setVisibility(View.GONE);
            } else {
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter();
        foodAdapter.setOnFoodClickListener(this::onFoodClick);
        binding.rvFoods.setAdapter(foodAdapter);

        // Set layout manager programmatically in case we need to modify it
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvFoods.setLayoutManager(layoutManager);
    }

    private void setupToolbar() {
        // Set navigation icon click listener
        binding.toolbarCategory.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );

        // Initial setup with arguments
        updateToolbar();
    }

    private void updateToolbar() {
        // Clear existing views to avoid duplicates
        binding.toolbarCategory.removeAllViews();
        
        // Create custom toolbar view for centered layout
        View customToolbarView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_center_toolbar, null);
        ImageView iconImageView = customToolbarView.findViewById(R.id.toolbar_icon);
        TextView titleTextView = customToolbarView.findViewById(R.id.toolbar_title);
        // Set title
        if (categoryName != null) {
            titleTextView.setText(categoryName);
        }

        // Add icon if available
        if ((category != null && category.getImageUrl() != null)) {
            String iconUrl = category.getImageUrl();

            // Load image with Glide
            Glide.with(requireContext())
                    .load(iconUrl)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .override(48, 48)
                    .into(iconImageView);
        } else {
            // Use a default icon
            iconImageView.setImageResource(R.drawable.ic_logo);
        }

        // Create layout parameters for the custom view to position it centrally
        MaterialToolbar.LayoutParams layoutParams = new MaterialToolbar.LayoutParams(
                MaterialToolbar.LayoutParams.WRAP_CONTENT,
                MaterialToolbar.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        // Add custom view to toolbar
        binding.toolbarCategory.addView(customToolbarView, layoutParams);
        // Set navigation icon
        binding.toolbarCategory.setNavigationIcon(R.drawable.ic_back);
    }

    private void setupSearchView() {
        // Setup search functionality with EditText
        binding.searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Not needed
            }
        });

        // Handle IME search action
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                filterFoods(binding.searchEditText.getText().toString());

                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                        requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Setup filter icon click listener
        binding.filterIcon.setOnClickListener(v -> {
            // Show filter options (can be implemented later)
            android.widget.Toast.makeText(requireContext(), "Filter options", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
    private void filterFoods(String query) {
        if (query.isEmpty()) {
            filteredFoodList.clear();
            filteredFoodList.addAll(foodList);
        } else {
            filteredFoodList.clear();
            for (FoodResponse food : foodList) {
                if (removeDiacritics(food.getName().toLowerCase()).contains(removeDiacritics(query.toLowerCase()))) {
                    filteredFoodList.add(food);
                }
            }
        }
        foodAdapter.submitList(filteredFoodList);
    }

    @Override
    public void onFoodClick(FoodResponse food) {
        Toast.makeText(requireContext(), food.getName(), Toast.LENGTH_SHORT).show();
        Log.d("Detail Cate", "onFoodClick: " + food.getName());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putLong("foodId", food.getId());
        args.putLong("vendorId", food.getVendorId());
        navController.navigate(R.id.action_to_food_detail, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}