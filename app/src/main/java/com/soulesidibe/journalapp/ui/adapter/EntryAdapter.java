package com.soulesidibe.journalapp.ui.adapter;

import com.soulesidibe.journalapp.R;
import com.soulesidibe.journalapp.model.data.Entry;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created on 6/27/18 at 10:37 PM
 * Project name : JournalApp
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private final List<Entry> entries;

    private final OnItemClick listener;

    public EntryAdapter(List<Entry> entries, OnItemClick listener) {
        this.entries = entries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(root);
    }

    public void update(List<Entry> data) {
        entries.clear();
        entries.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.title.setText(entry.getTitle());
        holder.date.setText("" + entry.getDate());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public interface OnItemClick {

        void onclick(Entry entry);

    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        TextView date;

        public EntryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.id_item_entry_title);
            date = itemView.findViewById(R.id.id_item_entry_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Entry entry = entries.get(getAdapterPosition());
                    listener.onclick(entry);
                }
            });
        }
    }
}
