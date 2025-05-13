package com.example.crabfood.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.databinding.ItemMenuCheckoutBinding;
import com.example.crabfood.model.MenuItemProfile;
import com.example.crabfood.model.enums.MenuAction;

import java.util.List;

public class MenuCheckoutAdapter extends RecyclerView.Adapter<MenuCheckoutAdapter.MenuViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(MenuAction action);
    }

    private List<MenuItemProfile> itemList;
    private OnItemClickListener listener;

    public MenuCheckoutAdapter(List<MenuItemProfile> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }
    public void updateSelectedContentByAction(MenuAction action, String newContent) {
        for (int i = 0; i < itemList.size(); i++) {
            MenuItemProfile item = itemList.get(i);
            if (item.getAction() == action) {
                item.setSelectedContent(newContent);
                notifyItemChanged(i);
                break;
            }
        }
    }
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ItemMenuCheckoutBinding binding;

        public MenuViewHolder(@NonNull ItemMenuCheckoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final MenuItemProfile item, final OnItemClickListener listener) {
            binding.icon.setImageResource(item.getIconResId());
            binding.title.setText(item.getTitle());
            binding.icon.setColorFilter(ContextCompat.getColor(itemView.getContext(), item.getIconColorRes()), PorterDuff.Mode.SRC_IN);
            binding.selectedContent.setText(item.getSelectedContent());
            itemView.setOnClickListener(v -> listener.onItemClick(item.getAction()));
        }
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMenuCheckoutBinding binding = ItemMenuCheckoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.bind(itemList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
