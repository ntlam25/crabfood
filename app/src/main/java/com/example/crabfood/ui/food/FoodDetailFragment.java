package com.example.crabfood.ui.food;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.crabfood.R;
import com.example.crabfood.databinding.FragmentFoodDetailBinding;
import com.example.crabfood.model.FoodOptionResponse;
import com.example.crabfood.model.FoodResponse;
import com.example.crabfood.model.OptionChoiceResponse;
import com.example.crabfood.ui.cart.CartViewModel;
import com.example.crabfood.ui.vendor.VendorDetailViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodDetailFragment extends Fragment {

    private final String TAG = "Food Detail";
    private FragmentFoodDetailBinding binding;
    private FoodDetailViewModel viewModel;
    private VendorDetailViewModel vendorDetailViewModel;

    private CartViewModel cartViewModel;
    private Long foodId;
    private Long vendorId;
    private FoodResponse food;
    private boolean isExpanded = false;
    // Store selected options for cart
    private List<OptionChoiceResponse> selectedChoices = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FoodDetailViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        vendorDetailViewModel = new ViewModelProvider(this).get(VendorDetailViewModel.class);

        if (getArguments() != null) {
            foodId = getArguments().getLong("foodId", 0);
            vendorId = getArguments().getLong("vendorId", 0);
        }

        setupToolbar();
        setupQuantityButtons();
        setupAddToCartButton();
        observer();
        loadData();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void loadData() {
        if (foodId != null) {
            viewModel.loadFood(foodId);
        }
    }

    private void observer() {
        viewModel.getFoods().observe(getViewLifecycleOwner(), foodResponse -> {
            if (foodResponse != null) {
                food = foodResponse;
                setupFoodInfo();
                setupFoodOptions();
            }
        });

        // Observe loading errors
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Log.d("Food Detail: ", errorMsg);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        vendorDetailViewModel.findVendorById(vendorId).observe(getViewLifecycleOwner(), vendorResponse -> {
            binding.textViewVendorName.setText(vendorResponse.getName());
        });

        // Observe quantity changes
        viewModel.getQuantity().observe(getViewLifecycleOwner(), quantity -> {
            binding.textViewQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        });
    }


    private void setupFoodInfo() {
        TextView textViewDescription = binding.textViewDescription;
        TextView textViewSeeMore = binding.textViewSeeMore;
        binding.textViewFoodName.setText(food.getName());
        binding.textViewNewPrice.setText(String.format("%.2f", food.getPrice()));
        binding.textViewDescription.setText(food.getDescription());
        binding.textViewRating.setText(String.format("%.1f", food.getRating()));

        // Update total price on initial load
        updateTotalPrice();

        Glide.with(binding.getRoot().getContext())
                .load(food.getImageUrl())
                .placeholder(R.drawable.dessert_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.ivCoverImage);

        textViewDescription.post(() -> {
            int lineCount = textViewDescription.getLineCount();
            if (lineCount > 3) {
                textViewSeeMore.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý sự kiện click "Xem thêm"
        textViewSeeMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;

            if (isExpanded) {
                textViewDescription.setMaxLines(Integer.MAX_VALUE); // Hiển thị tất cả dòng
                textViewDescription.setEllipsize(null); // Bỏ dấu "..."
                textViewSeeMore.setText("Thu gọn");
            } else {
                textViewDescription.setMaxLines(3); // Giới hạn 3 dòng
                textViewDescription.setEllipsize(TextUtils.TruncateAt.END); // Thêm dấu "..." nếu cần
                textViewSeeMore.setText("Xem thêm");
            }
        });
    }

    private void updateTotalPrice() {
        if (food != null && viewModel.getQuantity().getValue() != null) {
            // Base price
            double totalPrice = food.getPrice();

            // Add price adjustments from selected options
            for (OptionChoiceResponse choice : selectedChoices) {
                totalPrice += choice.getPriceAdjustment();
            }

            // Multiply by quantity
            totalPrice *= viewModel.getQuantity().getValue();

            binding.buttonAddToCart.setText(getString(R.string.add_to_cart) + "\n" + String.format("%,.0f đ", totalPrice));
        }
    }

    private void setupQuantityButtons() {
        // Decrease quantity button
        binding.buttonDecrease.setOnClickListener(v -> {
            viewModel.decreaseQuantity();
        });

        // Increase quantity button
        binding.buttonIncrease.setOnClickListener(v -> {
            viewModel.increaseQuantity();
        });
    }

    private void setupAddToCartButton() {
        binding.buttonAddToCart.setOnClickListener(v -> {
            addToCart();
        });
    }

    private void addToCart() {
        if (food != null && viewModel.getQuantity().getValue() != null && viewModel.getVendorId().getValue() != null) {
            // Validate all required options are selected
            boolean allRequiredSelected = true;
            StringBuilder missingOptions = new StringBuilder();

            if (food.getOptions() != null) {
                for (FoodOptionResponse option : food.getOptions()) {
                    // Count selections for this option
                    long optionId = option.getOptionId();
                    int selectionCount = 0;

                    // Count how many selections belong to this option
                    for (OptionChoiceResponse selectedChoice : selectedChoices) {
                        for (OptionChoiceResponse optionChoice : option.getChoices()) {
                            if (selectedChoice.equals(optionChoice)) {
                                selectionCount++;
                                break;
                            }
                        }
                    }

                    // Check required options
                    if (option.isRequired() && selectionCount == 0) {
                        allRequiredSelected = false;
                        missingOptions.append(option.getName()).append(", ");
                    }
                    // Check minimum selections for optional options
                    else if (!option.isRequired() && option.getMinSelection() > 0) {
                        if (selectionCount < option.getMinSelection()) {
                            allRequiredSelected = false;
                            missingOptions.append(option.getName())
                                    .append(" (cần chọn ít nhất ")
                                    .append(option.getMinSelection())
                                    .append("), ");
                        }
                    }
                }
            }

            if (!allRequiredSelected) {
                // Remove trailing comma and space
                if (missingOptions.length() > 2) {
                    missingOptions.setLength(missingOptions.length() - 2);
                }
                Toast.makeText(requireContext(),
                        "Vui lòng chọn các tùy chọn bắt buộc: " + missingOptions.toString(),
                        Toast.LENGTH_LONG).show();
                return;
            }

            int quantity = viewModel.getQuantity().getValue();

            // Note: Your CartViewModel will need to be updated to accept a List instead of a Map
            boolean success = cartViewModel.addToCart(requireContext(), food, quantity, selectedChoices);

            if (success) {
                // Navigate back or to cart
                Navigation.findNavController(requireView()).navigateUp();
            }
        } else {
            Toast.makeText(requireContext(), "Không thể thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFoodOptions() {
        // Clear existing options
        binding.optionsContainer.removeAllViews();

        // Clear and reinitialize the list for storing selected choices
        selectedChoices.clear();

        // Hide options section if there are no options
        if (food.getOptions() == null || food.getOptions().isEmpty()) {
            binding.textViewOptions.setVisibility(View.GONE);
            binding.optionsContainer.setVisibility(View.GONE);
            return;
        } else {
            binding.textViewOptions.setVisibility(View.VISIBLE);
            binding.optionsContainer.setVisibility(View.VISIBLE);
        }

        // Create vertical layout for options
        LinearLayout optionsLayout = new LinearLayout(requireContext());
        optionsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        optionsLayout.setOrientation(LinearLayout.VERTICAL);

        // Add each option group
        for (FoodOptionResponse option : food.getOptions()) {
            // Create a container for each option group
            LinearLayout optionGroup = new LinearLayout(requireContext());
            optionGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            optionGroup.setOrientation(LinearLayout.VERTICAL);
            optionGroup.setPadding(0, 16, 0, 16);

            // Add option group title
            TextView optionTitle = new TextView(requireContext());
            optionTitle.setText(option.getName());
            optionTitle.setTextSize(16);
            optionTitle.setTextColor(getResources().getColor(R.color.black, null));
            optionTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            optionGroup.addView(optionTitle);

            final Long optionId = option.getOptionId();
            Log.d("Food detail", "setupFoodOptions: option Id " + optionId);

            if (option.isRequired()) {
                // Create radio group for required options (single selection)
                RadioGroup radioGroup = new RadioGroup(requireContext());
                radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                radioGroup.setOrientation(LinearLayout.VERTICAL);

                // Track whether this is a new selection
                final boolean[] isFirstSelection = {true};

                // Add radio buttons for each choice
                boolean defaultFound = false;
                RadioButton firstButton = null;

                for (OptionChoiceResponse choice : option.getChoices()) {
                    RadioButton radioButton = new RadioButton(requireContext());
                    radioButton.setId(View.generateViewId());
                    radioButton.setText(getChoiceText(choice));
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.MATCH_PARENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT));

                    // Save first button reference
                    if (firstButton == null) {
                        firstButton = radioButton;
                    }

                    // Store the option ID and choice object as tags
                    radioButton.setTag(R.id.tag_option_id, optionId);
                    radioButton.setTag(R.id.tag_choice, choice);

                    // Check default choice
                    if (choice.isDefault()) {
                        radioButton.setChecked(true);
                        defaultFound = true;

                        // Add default choice to selected choices
                        Log.d(TAG, "setupFoodOptions: " + option.getOptionId());
                        choice.setOptionId(option.getOptionId());
                        choice.setOptionName(option.getName());
                        selectedChoices.add(choice);
                    }

                    radioGroup.addView(radioButton);
                }

                // If no default is set, select the first item
                if (!defaultFound && firstButton != null) {
                    firstButton.setChecked(true);

                    // Add first choice to selected choices
                    OptionChoiceResponse firstChoice = (OptionChoiceResponse) firstButton.getTag(R.id.tag_choice);
                    Log.d(TAG, "setupFoodOptions: " + option.getOptionId());
                    firstChoice.setOptionId(option.getOptionId());
                    firstChoice.setOptionName(option.getName());
                    selectedChoices.add(firstChoice);
                }

                // Set listener for radio group
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    // Skip the first automatic selection event
                    if (isFirstSelection[0]) {
                        isFirstSelection[0] = false;
                        return;
                    }

                    RadioButton selectedButton = group.findViewById(checkedId);
                    if (selectedButton != null) {
                        OptionChoiceResponse selectedChoice = (OptionChoiceResponse) selectedButton.getTag(R.id.tag_choice);

                        // Remove any previous selections for this option
                        Iterator<OptionChoiceResponse> iterator = selectedChoices.iterator();
                        while (iterator.hasNext()) {
                            OptionChoiceResponse existingChoice = iterator.next();
                            // Check if this choice belongs to the current option by comparing with choices list
                            for (OptionChoiceResponse optionChoice : option.getChoices()) {
                                if (existingChoice.equals(optionChoice)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }

                        // Add the new selection
                        Log.d(TAG, "setupFoodOptions: " + option.getOptionId());
                        selectedChoice.setOptionId(option.getOptionId());
                        selectedChoice.setOptionName(option.getName());
                        selectedChoices.add(selectedChoice);

                        // Update total price
                        updateTotalPrice();
                    }
                });

                optionGroup.addView(radioGroup);
            } else {
                // Create checkboxes for optional selections
                LinearLayout checkboxGroup = new LinearLayout(requireContext());
                checkboxGroup.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                checkboxGroup.setOrientation(LinearLayout.VERTICAL);

                // Add limits info if applicable
                if (option.getMaxSelection() < option.getChoices().size() ||
                        option.getMinSelection() > 0) {
                    TextView limitsInfo = new TextView(requireContext());
                    limitsInfo.setText(String.format("Chọn từ %d đến %d",
                            option.getMinSelection(), option.getMaxSelection()));
                    limitsInfo.setTextSize(14);
                    limitsInfo.setTextColor(getResources().getColor(R.color.neutral_400, null));
                    checkboxGroup.addView(limitsInfo);
                }

                // Counter for current selections in this option group
                final int[] selectedCount = {0};

                // Add checkbox for each choice
                for (OptionChoiceResponse choice : option.getChoices()) {
                    CheckBox checkBox = new CheckBox(requireContext());
                    checkBox.setText(getChoiceText(choice));
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    // Store the option ID and choice object as tags
                    checkBox.setTag(R.id.tag_option_id, optionId);
                    checkBox.setTag(R.id.tag_choice, choice);

                    // Check default choice
                    if (choice.isDefault()) {
                        checkBox.setChecked(true);
                        selectedCount[0]++;

                        // Add default choice to selected choices
                        Log.d(TAG, "setupFoodOptions: " + option.getOptionId());
                        choice.setOptionId(option.getOptionId());
                        choice.setOptionName(option.getName());
                        selectedChoices.add(choice);
                    }

                    // Checkbox listener
                    final int maxSelection = option.getMaxSelection();

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        OptionChoiceResponse selectedChoice = (OptionChoiceResponse) buttonView.getTag(R.id.tag_choice);

                        if (isChecked) {
                            // Check if we've already selected the maximum number of choices for this option
                            int currentSelectionCount = 0;
                            for (OptionChoiceResponse existingChoice : selectedChoices) {
                                for (OptionChoiceResponse optionChoice : option.getChoices()) {
                                    if (existingChoice.equals(optionChoice)) {
                                        currentSelectionCount++;
                                        break;
                                    }
                                }
                            }

                            if (currentSelectionCount >= maxSelection) {
                                // Uncheck this checkbox
                                buttonView.setChecked(false);
                                Toast.makeText(requireContext(),
                                        "Bạn chỉ có thể chọn tối đa " + maxSelection + " lựa chọn",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            selectedCount[0]++;
                            Log.d(TAG, "setupFoodOptions: " + option.getOptionId());
                            selectedChoice.setOptionId(option.getOptionId());
                            selectedChoice.setOptionName(option.getName());
                            selectedChoices.add(selectedChoice);
                        } else {
                            selectedCount[0]--;

                            // Remove this choice
                            Iterator<OptionChoiceResponse> iterator = selectedChoices.iterator();
                            while (iterator.hasNext()) {
                                if (iterator.next().equals(selectedChoice)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }

                        // Update total price
                        updateTotalPrice();
                    });

                    checkboxGroup.addView(checkBox);
                }

                optionGroup.addView(checkboxGroup);
            }

            optionsLayout.addView(optionGroup);

            // Add divider if not the last option
            if (food.getOptions().indexOf(option) < food.getOptions().size() - 1) {
                View divider = new View(requireContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(getResources().getColor(R.color.neutral_100, null));
                optionsLayout.addView(divider);
            }
        }

        binding.optionsContainer.addView(optionsLayout);
    }

    // Helper method to find the option ID tag associated with a choice
    private Long findTagForChoice(OptionChoiceResponse choice) {
        for (FoodOptionResponse option : food.getOptions()) {
            if (option.getChoices().contains(choice)) {
                return option.getOptionId();
            }
        }
        return null;
    }

    private String getChoiceText(OptionChoiceResponse choice) {
        if (choice.getPriceAdjustment() > 0) {
            return choice.getName() + " (+" + String.format("%,.0f đ", choice.getPriceAdjustment()) + ")";
        } else if (choice.getPriceAdjustment() < 0) {
            return choice.getName() + " (" + String.format("%,.0f đ", choice.getPriceAdjustment()) + ")";
        } else {
            return choice.getName();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}