package com.android.myproj.minesweeper.shape;

import android.app.Activity;
import android.graphics.RectF;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.android.myproj.minesweeper.R;

public class MyProgressBar {

    private final Activity activity;
    private final ViewGroup container;
    private boolean isInitialized;
    private float barLength;
    private float barHeight;

    public MyProgressBar(Activity activity, ViewGroup container) {
        this.activity = activity;
        this.container = container;
        this.isInitialized = false;
    }

    public void setDimension(float barLength, float barHeight) {
        if (isInitialized) {
            return;
        }
        this.barLength = barLength;
        this.barHeight = barHeight;
        this.isInitialized = true;
    }

    public void drawProgressBar(float left, float bottom) {
        // Remove previously drawn rectangle
        this.container.removeAllViews();

        float right = left + this.barLength;  // left + rectLength
        // rectF height = RECT_HEIGHT_DP to pixel
        float top = bottom - this.barHeight;

        this.container.addView(new MyRectF(
                this.activity,
                ContextCompat.getColor(this.activity, R.color.purple_700),
                new RectF(left, top, right, bottom)
        ));
    }

}
