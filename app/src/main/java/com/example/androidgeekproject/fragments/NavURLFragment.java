package com.example.androidgeekproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.example.androidgeekproject.R;

public class NavURLFragment extends Fragments {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LinearLayout layout;
        layout = (LinearLayout) inflater.inflate(R.layout.nav_urlfragment_layout, container, false);
        WebView webView = layout.findViewById(R.id.browse);
        webView.loadUrl("https://geekbrains.ru/");
        return layout;
    }
}
