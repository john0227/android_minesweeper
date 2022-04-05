package com.android.myproj.minesweeper.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.ColorInt;

public class MyArc extends View {

    private final int color;
    private final float sweepAngle;
    private final Paint paint;
    private final RectF rectF;

    public MyArc(Context context, @ColorInt int color, float sweepAngle, RectF rectF) {
        super(context);
        this.color = color;
        this.sweepAngle = sweepAngle == 0 ? 1 : sweepAngle;
        this.rectF = rectF;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        canvas.drawArc(rectF, -90, this.sweepAngle, true, paint);
    }

}
