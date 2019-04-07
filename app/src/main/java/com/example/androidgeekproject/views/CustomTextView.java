package com.example.androidgeekproject.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.example.androidgeekproject.R;

public class CustomTextView extends View {

    private final static String TAG = "CustomTextView";
    private boolean onClick = false;
    private Paint paint;
    private int color = Color.BLACK;
    private String text;

    public CustomTextView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        Log.d(TAG, "Constructor");
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize(70.0f);

    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d(TAG, "onDraw");
        super.onDraw(canvas);

        if (!onClick) {
            paint.setColor(getResources().getColor(R.color.colorFirst));
            canvas.drawText("", 200, 800, paint);
            paint.setColor(color);
            onClick = true;
            return;
        }

        canvas.drawText(text, 200, 800, paint);
    }
}
