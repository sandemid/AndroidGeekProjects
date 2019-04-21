package com.example.androidgeekproject.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidgeekproject.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<String[]> records;

    RecyclerViewAdapter(List<String[]> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_history_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        String[] record = records.get(i);
        viewHolder.city.setText(record[0]);
        viewHolder.date.setText(record[1]);
        viewHolder.temp.setText(record[2]);
        viewHolder.details.setText(record[3]);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    void updateView(List<String[]> records) {
        this.records.clear();
        this.records = records;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        TextView date;
        TextView temp;
        TextView details;

        ViewHolder(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.textView_city);
            date = itemView.findViewById(R.id.textView_date);
            temp = itemView.findViewById(R.id.textView_temp);
            details = itemView.findViewById(R.id.textView_details);
        }
    }
}
