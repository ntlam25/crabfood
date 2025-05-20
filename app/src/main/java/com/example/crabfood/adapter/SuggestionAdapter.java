package com.example.crabfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.crabfood.R;
import com.example.crabfood.model.mapsbox.AutoComplete;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position, String placeId);
    }

    private List<AutoComplete> suggestions;
    private OnItemClickListener listener;

    public SuggestionAdapter(List<AutoComplete> suggestions, OnItemClickListener onItemClickListener) {
        this.suggestions = suggestions;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_suggestion_layout, parent, false);
        return new SuggestionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        AutoComplete item = suggestions.get(position);
        if(item != null){
            holder.suggestionText.setText(item.getDescription());
            holder.placeIdText.setText(item.getPlaceId());
        }
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void updateSuggestions(List<AutoComplete> newSuggestions) {
        suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView suggestionText;
        TextView placeIdText;

        SuggestionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.suggestion_text);
            placeIdText = itemView.findViewById(R.id.place_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(position);
//                        AutoComplete item = suggestions.get(position);
                        String placeId = placeIdText != null && placeIdText.getText() != null ? placeIdText.getText().toString() : null;
                        listener.onItemClick(position, placeId);
                    }
                }
            });
        }
    }
}
