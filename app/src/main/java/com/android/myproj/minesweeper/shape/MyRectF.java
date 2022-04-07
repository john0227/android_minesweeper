package com.android.myproj.minesweeper.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.ColorInt;

public class MyRectF extends View {

    private final int color;
    private final Paint paint;
    private final RectF rectF;

    public MyRectF(Context context, @ColorInt int color, RectF rectF) {
        super(context);
        this.color = color;
        this.rectF = rectF;
        paint = new Paint();

        this.setWillNotDraw(false);
        this.bringToFront();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        canvas.drawRect(rectF, paint);
    }

}
