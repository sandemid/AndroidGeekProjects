package com.example.androidgeekproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NavMyProfileFragment extends Fragments {

    private Boolean sensorChange = true;
    private static String currentTemperature;
    private static String currentHumidity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ConstraintLayout layout = new ConstraintLayout(getActivity());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layout.setBackgroundColor(getResources().getColor(R.color.colorFirst));
        layout.addView(initTextView(params));

        CustomTextView customTextView = initCustomTextView(params);
        layout.addView(customTextView);

        CustomBtnCircleView customBtnCircleView = initCustomView(params);
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

        return layout;
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

    private TextView initTextView(ConstraintLayout.LayoutParams params) {
        TextView text = new TextView(getActivity());
        text.setText("Нажимай на кружок, читай датчики");
        text.setTextSize(14);
        text.setTextColor(Color.BLACK);
        text.setLayoutParams(params);
        text.setPadding(140,50,0,0);
        Log.d("TextView", "addView");
        return text;
    }

    public static void setCurrentTemperature(String s) {
        currentTemperature = s;
    }

    public static void setCurrentHumidity(String s) {
        currentHumidity = s;
    }
}
