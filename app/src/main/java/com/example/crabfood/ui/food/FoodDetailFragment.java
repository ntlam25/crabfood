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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDetailFragment extends Fragment {

    private FragmentFoodDetailBinding binding;
    private FoodDetailViewModel viewModel;

    private CartViewModel cartViewModel;
    private Long foodId;
    private FoodResponse food;
    private boolean isExpanded = false;
    // Store selected options for cart
    private Map<Long, List<OptionChoiceResponse>> selectedChoices = new HashMap<>();
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

        if (getArguments() != null) {
            foodId = getArguments().getLong("foodId", 0);
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
            for (Map.Entry<Long, List<OptionChoiceResponse>> entry : selectedChoices.entrySet()) {
                for (OptionChoiceResponse choice : entry.getValue()) {
                    totalPrice += choice.getPriceAdjustment();
                }
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
                    if (option.isRequired() &&
                            (selectedChoices.get(option.getOptionId()) == null ||
                                    selectedChoices.get(option.getOptionId()).isEmpty())) {
                        allRequiredSelected = false;
                        missingOptions.append(option.getName()).append(", ");
                    } else if (!option.isRequired() && option.getMinSelection() > 0) {
                        // Check if minimum selections requirement is met
                        if (selectedChoices.get(option.getOptionId()).size() < option.getMinSelection()) {
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
            Long vendorId = viewModel.getVendorId().getValue();

            // Add to cart using CartHelper
            // You'll need to modify your CartHelper to accept the selected options
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

            // Initialize list for storing selected choices for this option
            selectedChoices.put(option.getOptionId(), new ArrayList<>());

            if (option.isRequired()) {
                // Create radio group for required options (single selection)
                RadioGroup radioGroup = new RadioGroup(requireContext());
                radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                radioGroup.setOrientation(LinearLayout.VERTICAL);

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

                    // Check default choice
                    if (choice.isDefault()) {
                        radioButton.setChecked(true);
                        defaultFound = true;

                        // Add default choice to selected choices
                        List<OptionChoiceResponse> choices = new ArrayList<>();
                        choices.add(choice);
                        selectedChoices.put(option.getOptionId(), choices);
                    }

                    // Set tag for retrieving choice data later
                    radioButton.setTag(choice);

                    radioGroup.addView(radioButton);
                }

                // If no default is set, select the first item
                if (!defaultFound && firstButton != null) {
                    firstButton.setChecked(true);

                    // Add first choice to selected choices
                    OptionChoiceResponse firstChoice = (OptionChoiceResponse) firstButton.getTag();
                    List<OptionChoiceResponse> choices = new ArrayList<>();
                    choices.add(firstChoice);
                    selectedChoices.put(option.getOptionId(), choices);
                }

                // Set listener for radio group
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton selectedButton = group.findViewById(checkedId);
                    if (selectedButton != null) {
                        OptionChoiceResponse selectedChoice = (OptionChoiceResponse) selectedButton.getTag();

                        // Update selected choices
                        List<OptionChoiceResponse> choices = new ArrayList<>();
                        choices.add(selectedChoice);
                        selectedChoices.put(option.getOptionId(), choices);

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

                // Counter for default selections
                final int[] selectedCount = {0};

                // Add checkbox for each choice
                for (OptionChoiceResponse choice : option.getChoices()) {
                    CheckBox checkBox = new CheckBox(requireContext());
                    checkBox.setText(getChoiceText(choice));
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    // Check default choice
                    if (choice.isDefault()) {
                        checkBox.setChecked(true);
                        selectedCount[0]++;

                        // Add default choice to selected choices
                        selectedChoices.get(option.getOptionId()).add(choice);
                    }

                    // Set tag for retrieving choice data later
                    checkBox.setTag(choice);

                    // Checkbox listener
                    final Long optionId = option.getOptionId();
                    final int maxSelection = option.getMaxSelection();

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        OptionChoiceResponse selectedChoice = (OptionChoiceResponse) buttonView.getTag();

                        if (isChecked) {
                            // Check if we've already selected the maximum number of choices
                            if (selectedCount[0] >= maxSelection) {
                                // Uncheck this checkbox
                                buttonView.setChecked(false);
                                Toast.makeText(requireContext(),
                                        "Bạn chỉ có thể chọn tối đa " + maxSelection + " lựa chọn",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            selectedCount[0]++;
                            selectedChoices.get(optionId).add(selectedChoice);
                        } else {
                            selectedCount[0]--;
                            selectedChoices.get(optionId).remove(selectedChoice);
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