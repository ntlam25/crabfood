package com.example.crabfood.adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabfood.R;
import com.example.crabfood.model.MenuItemProfile;
import com.example.crabfood.model.enums.MenuAction;

import java.util.List;

public class MenuProfileAdapter extends RecyclerView.Adapter<MenuProfileAdapter.MenuViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MenuAction action);
    }

    private List<MenuItemProfile> itemList;
    private OnItemClickListener listener;

    public MenuProfileAdapter(List<MenuItemProfile> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public MenuViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }

        public void bind(final MenuItemProfile item, final OnItemClickListener listener) {
            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());
            if (item.getIconResId() != 0 && item.getIconColorRes() != 0) {
                icon.setColorFilter(ContextCompat.getColor(itemView.getContext(), item.getIconColorRes()), PorterDuff.Mode.SRC_IN);
            } else {
                icon.clearColorFilter(); // màu mặc định
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item.getAction()));
        }
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_profile, parent, false);
        return new MenuViewHolder(view);
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
