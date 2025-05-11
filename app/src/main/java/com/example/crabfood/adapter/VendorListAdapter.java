package com.example.crabfood.adapter;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crabfood.R;
import com.example.crabfood.model.VendorResponse;
public class VendorListAdapter extends PagingDataAdapter<VendorResponse, VendorListAdapter.VendorViewHolder> {

    public VendorListAdapter(@NonNull DiffUtil.ItemCallback<VendorResponse> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        VendorResponse vendor = getItem(position);
        if (vendor != null) {
            holder.bind(vendor);
        }
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView distanceTextView;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.vendorName);
            distanceTextView = itemView.findViewById(R.id.vendorDistance);
        }

        public void bind(VendorResponse vendor) {
            nameTextView.setText(vendor.getName());
            distanceTextView.setText(String.format("%.2f km", vendor.getDistance()));
        }
    }

    public static class VendorDiffCallback extends DiffUtil.ItemCallback<VendorResponse> {
        @Override
        public boolean areItemsTheSame(@NonNull VendorResponse oldItem, @NonNull VendorResponse newItem) {
            return oldItem.getId() == (newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull VendorResponse oldItem, @NonNull VendorResponse newItem) {
            return oldItem.equals(newItem);
        }
    }
}