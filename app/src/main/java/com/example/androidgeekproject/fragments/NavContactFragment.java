package com.example.androidgeekproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidgeekproject.R;

public class NavContactFragment extends Fragments {

    boolean isFragmentAlreadyLoaded = false;
    ConstraintLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null && !isFragmentAlreadyLoaded){
            layout = (ConstraintLayout) inflater.inflate(R.layout.nav_contactfragment_layout, container, false);
            isFragmentAlreadyLoaded = true;
        }
        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
