package com.example.androidgeekproject.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.androidgeekproject.R;

public class CustomBtnCircleView extends View  {

    private final static String TAG = "CustomBtnCircleView";
    private Paint paint;
    private int radius = 50;
    private int color = Color.BLACK;

    private boolean pressed = false;
    private OnClickListener listener = v -> Toast.makeText(getContext(), "Нажали Custom View",
            Toast.LENGTH_SHORT).show();

    public CustomBtnCircleView(Context context) {
        super(context);
        initView();
    }

    public CustomBtnCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initView();
    }

    public CustomBtnCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView();
    }

    private void initView() {
        Log.d(TAG, "Constructor");
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    // Обработка параметров в xml
    private void initAttr(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomBtnCircleView,
                0, 0);
        setRadius(typedArray.getResourceId(R.styleable.CustomBtnCircleView_cv_Radius, 100));
        setColor(typedArray.getResourceId(R.styleable.CustomBtnCircleView_cv_Color, Color.BLUE));
        typedArray.recycle();
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d(TAG, "onDraw");
        super.onDraw(canvas);
        if(pressed) {
            canvas.drawCircle(getWidth() / 2, 250, radius/10, paint);
        }
        else {
            canvas.drawCircle(getWidth() / 2, 250, radius, paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int Action = event.getAction();
        if(Action == MotionEvent.ACTION_DOWN){ // Нажали
            pressed = true;
            invalidate();           // Перерисовка элемента
            if (listener != null) listener.onClick(this);
        } else if(Action == MotionEvent.ACTION_UP) { // Отпустили
            pressed = false;
            invalidate();           // Перерисовка элемента
        }
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        listener = l;

    }


}

