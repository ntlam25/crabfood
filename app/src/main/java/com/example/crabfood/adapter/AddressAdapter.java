package com.example.crabfood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.model.AddressResponse;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final String TAG = "AddressAdapter";
    private List<AddressResponse> addressList;
    private Long selectedId = null;
    private OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressResponse address);
    }

    public AddressAdapter(List<AddressResponse> addressList, OnAddressSelectedListener listener, Long selectedId) {
        this.addressList = addressList;
        this.listener = listener;
        this.selectedId = selectedId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView textLabel, textAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            textLabel = itemView.findViewById(R.id.text_label);
            textAddress = itemView.findViewById(R.id.text_address);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddressResponse address = addressList.get(position);
        holder.textLabel.setText(address.getLabel());
        holder.textAddress.setText(address.getFullAddress());
        // Kiểm tra nếu address này có id trùng với selectedId thì check radio button
        holder.radioButton.setChecked(selectedId != null && address.getId().equals(selectedId));

        holder.itemView.setOnClickListener(v -> {
            selectedId = address.getId();
            listener.onAddressSelected(address);
            Log.d(TAG, "onBindViewHolder: " + selectedId);
            notifyDataSetChanged(); // refresh all
        });
    }

    public void setSelectedId(Long selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public Long getSelectedId() {
        return selectedId;
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
