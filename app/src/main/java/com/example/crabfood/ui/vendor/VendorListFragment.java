package com.example.crabfood.ui.vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crabfood.R;
import com.example.crabfood.adapter.VendorAdapter;
import com.example.crabfood.model.VendorResponse;
import com.example.crabfood.helpers.KeyboardHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.crabfood.helpers.StringHelper.removeDiacritics;

public class VendorListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvNoResults;
    private VendorAdapter adapter;
    private List<VendorResponse> allVendors = new ArrayList<>();
    private List<VendorResponse> filteredVendors = new ArrayList<>();
    private String currentQuery = "";

    // Optional listener for customizable click events
    public interface VendorListListener {
        void onVendorClick(VendorResponse vendor, int position);
        void onFavoriteClick(VendorResponse vendor, int position);
    }

    private VendorListListener listener;

    // Flag to determine if we use internal click handlers or external ones
    private boolean useInternalClickHandlers = true;

    public VendorListFragment() {
    }

    // Set external listener and disable internal handlers
    public void setListener(VendorListListener listener) {
        this.listener = listener;
        this.useInternalClickHandlers = false;
    }

    // Enable internal click handlers (default behavior)
    public void useInternalClickHandlers() {
        this.useInternalClickHandlers = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendor_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardHelper.hideKeyboardOnClickOutside(view, requireActivity());
        recyclerView = view.findViewById(R.id.rv_vendors);
        tvNoResults = view.findViewById(R.id.tv_no_results);

        Log.d("Vendor list", allVendors.size() + "");

        // Setup adapter with appropriate click handlers
        adapter = new VendorAdapter(
                filteredVendors,
                // Favorite button click handler
                (vendor, position) -> {
                    if (!useInternalClickHandlers && listener != null) {
                        // Use external handler if available
                        listener.onFavoriteClick(vendor, position);
                    } else {
                        // Use internal handler
                        handleFavoriteClick(vendor, position);
                    }
                },
                // Vendor item click handler
                (vendor, position) -> {
                    if (!useInternalClickHandlers && listener != null) {
                        // Use external handler if available
                        listener.onVendorClick(vendor, position);
                    } else {
                        // Use internal handler
                        handleVendorClick(vendor, position);
                    }
                }
        );

        Log.d("Vendor list", adapter.getItemCount() + "");

        recyclerView.setAdapter(adapter);

        updateVisibility();
    }

    private void handleVendorClick(VendorResponse vendor, int position) {
        // Hide keyboard if it's visible
        KeyboardHelper.hideKeyboardOnClickOutside(requireView(), requireActivity());

        // Navigate to vendor detail screen
        if (vendor != null) {
            // Show toast for now
            Toast.makeText(requireContext(), "Chi tiết : " + vendor.getName(), Toast.LENGTH_SHORT).show();

             Bundle args = new Bundle();
             args.putLong("vendorId", vendor.getId());
             Navigation.findNavController(requireView()).navigate(R.id.action_to_detail_vendor, args);
        }
    }

    private void handleFavoriteClick(VendorResponse vendor, int position) {
        // Hide keyboard if it's visible
        KeyboardHelper.hideKeyboardOnClickOutside(requireView(), requireActivity());

        if (vendor != null) {
            // Toggle favorite status
            boolean isFavorite = !vendor.isFavorite(); // Assuming there's a favorite flag in VendorResponse
            vendor.setFavorite(isFavorite); // Assuming there's a setter in VendorResponse

            // Show toast message
            String message = isFavorite ?
                    "Added " + vendor.getName() + " to favorites" :
                    "Removed " + vendor.getName() + " from favorites";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            // Update the UI
            adapter.notifyItemChanged(position);

            // TODO: Update favorite status in database
            // viewModel.updateFavoriteStatus(vendor.getId(), isFavorite);
        }
    }

    public void setVendors(List<VendorResponse> vendors) {
        this.allVendors.clear();
        if (vendors != null) {
            this.allVendors.addAll(vendors);
        }
        filterVendors(currentQuery);
    }

    public void filterVendors(String query) {
        this.currentQuery = query;
        filteredVendors.clear();

        if (query == null || query.isEmpty()) {
            filteredVendors.addAll(allVendors);
        } else {
            for (VendorResponse vendor : allVendors) {
                if (removeDiacritics(vendor.getName().toLowerCase())
                        .contains(removeDiacritics(query.toLowerCase()))
                        || removeDiacritics(vendor.getCuisineType().toLowerCase())
                        .contains(removeDiacritics(query.toLowerCase()))) {
                    filteredVendors.add(vendor);
                }
            }
        }

        if (adapter != null) {
            adapter.setVendors(filteredVendors);
        }

        updateVisibility();
    }

    private void updateVisibility() {
        if (tvNoResults != null) {
            tvNoResults.setVisibility(filteredVendors.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public void setFavorite(int position) {
        if (adapter != null) {
            adapter.notifyItemChanged(position);
        }
    }

    public List<VendorResponse> getVendors() {
        return allVendors;
    }

    public List<VendorResponse> getFilteredVendors() {
        return filteredVendors;
    }
}