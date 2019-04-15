package com.example.androidgeekproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidgeekproject.R;
import com.example.androidgeekproject.views.CustomBtnCircleView;
import com.example.androidgeekproject.views.CustomTextView;

public class NavMyProfileFragment extends Fragments {

    private Boolean sensorChange = true;
    private static String currentTemperature;
    private static String currentHumidity;
    ConstraintLayout layout;
    CustomTextView customTextView;
    TextView textView;
    CustomBtnCircleView customBtnCircleView;
    boolean isFragmentAlreadyLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null && !isFragmentAlreadyLoaded){

            layout = (ConstraintLayout) inflater.inflate(R.layout.nav_myprofilefragment_layout, container, false);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layout.setBackgroundColor(getResources().getColor(R.color.colorFirst));
            initTextView(params);

            customTextView = initCustomTextView(params);
            layout.addView(customTextView);

            customBtnCircleView = initCustomView(params);
            layout.addView(customBtnCircleView);

            customBtnCircleView.setOnClickListener(v -> {
                sensorChange = !sensorChange;
                if (sensorChange) {
                    customTextView.setText("Влажность = " + currentHumidity);
                } else {
                    customTextView.setText("Температура = " + currentTemperature);
                }
                customTextView.invalidate();
            });
            isFragmentAlreadyLoaded = true;
        }
        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private CustomTextView initCustomTextView(ConstraintLayout.LayoutParams params) {
        CustomTextView customTextView = new CustomTextView(getActivity());
        customTextView.setLayoutParams(params);
        Log.d("customTextView", "addView");
        return customTextView;
    }

    private CustomBtnCircleView initCustomView(ConstraintLayout.LayoutParams params) {
        CustomBtnCircleView customBtnCircleView = new CustomBtnCircleView(getActivity());
        customBtnCircleView.setLayoutParams(params);
        Log.d("CustomView", "addView");
        return customBtnCircleView;
    }

    private void initTextView(ConstraintLayout.LayoutParams params) {
        textView = layout.findViewById(R.id.city_field);
    }

    public static void setCurrentTemperature(String s) {
        currentTemperature = s;
    }

    public static void setCurrentHumidity(String s) {
        currentHumidity = s;
    }
}
