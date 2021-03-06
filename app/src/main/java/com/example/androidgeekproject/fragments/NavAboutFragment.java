package com.example.androidgeekproject.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidgeekproject.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NavAboutFragment extends Fragments {

    LinearLayout layout;
    Button startServiceBtn;
    Button startAsyncBtn;
    TextView resultText;
    TextView timeText;
    ProgressBar progressBar;
    Handler handler;
    BroadcastReceiver broadcastReceiverTime;
    boolean isFragmentAlreadyLoaded = false;
    public static final String BROADCAST_ACTION = "com.example.androidgeekproject.fragments.NavAboutFragment";
    public static final String KEY_FROM_FRAGMENT = "SERVICE_START";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(savedInstanceState == null && !isFragmentAlreadyLoaded) {
            layout = (LinearLayout) inflater.inflate(R.layout.nav_aboutfragment_layout, container, false);
            handler = new Handler();
            initViews(layout);
            setOnClickListeners();
            startBroadcastReceiverTime();
            isFragmentAlreadyLoaded = true;
        }
        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void setOnClickListeners() {
        startAsyncBtn.setOnClickListener(v -> {
            CalculateAsyncTask asyncTask = new CalculateAsyncTask();
            asyncTask.execute("");
        });

        startServiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(KEY_FROM_FRAGMENT, "SERVICE_START");
            Objects.requireNonNull(getActivity()).sendBroadcast(intent);
        });
    }

    class CalculateAsyncTask extends AsyncTask<String,Integer,Double>{

        @Override
        protected Double doInBackground(String... strings) {
            double r = 1;
            for (int j = 0; j < 100000; j++) {
                r = r*0.01+Math.floor(Math.random()*j);
                double value = (double) j * 100 / 100000;
                Integer intValue = (int) value;
                publishProgress(intValue);
            }
            return r;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(final Double aDouble) {
            super.onPostExecute(aDouble);
            handler.post(() -> {
                final double value = aDouble;
                resultText.setText(new StringBuilder().append("Результат = ")
                        .append(new BigDecimal(value).setScale(2, RoundingMode.DOWN).toString()));
            });
        }
    }

    private void initViews(LinearLayout layout) {
        startAsyncBtn = layout.findViewById(R.id.button_start_async);
        startServiceBtn = layout.findViewById(R.id.button_start_service);
        progressBar = layout.findViewById(R.id.progressBar);
        resultText = layout.findViewById(R.id.textView_result);
        timeText = layout.findViewById(R.id.textView_time);
        setTimeText();
    }

    private void setTimeText() {
        DateFormat simpleDateFormat =  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        String date = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        date = "Текущее время: " + date;
        timeText.setText(date);
    }

    private void startBroadcastReceiverTime() {
        broadcastReceiverTime = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setTimeText();
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiverTime, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Objects.requireNonNull(getActivity()).unregisterReceiver(this.broadcastReceiverTime);
    }
}
