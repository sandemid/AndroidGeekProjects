package com.example.androidgeekproject.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.androidgeekproject.R;
import com.example.androidgeekproject.database.DBTables;
import com.example.androidgeekproject.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NavWeatherHistoryFragment extends Fragments {
    boolean isFragmentAlreadyLoaded = false;
    ConstraintLayout layout;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    Spinner spinner;
    List<String[]> recordsView;
    String[] recordsSpinner;
    SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null && !isFragmentAlreadyLoaded){
            layout = (ConstraintLayout) inflater.inflate(R.layout.nav_weather_history_layout, container, false);
            database = new DatabaseHelper(Objects.requireNonNull(getActivity()).getApplicationContext()).getWritableDatabase();
            initSpinnerData();
            initSpinner();
            initRecyclerView();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String item = (String)parent.getItemAtPosition(position);
                    loadViewData(item);
                    recyclerViewAdapter.updateView(recordsView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            isFragmentAlreadyLoaded = true;
        }
        return layout;
    }

    private void initRecyclerView() {
        recyclerView = layout.findViewById(R.id.recyclerView);
        recordsView = new ArrayList<>(0);
        recyclerViewAdapter = new RecyclerViewAdapter(recordsView);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initSpinner() {
        spinner = layout.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, recordsSpinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    private void initSpinnerData() {
        recordsSpinner = DBTables.getCity(database);
    }

    private void loadViewData(String city) {
        recordsView = DBTables.getWeather(city, database);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
