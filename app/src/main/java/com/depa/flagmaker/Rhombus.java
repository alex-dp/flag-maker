package com.depa.flagmaker;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Rhombus extends Drawable {

    private Paint paint;
    private int color;
    private int alpha;

    Rhombus(int color) {
        this.color = color;
        this.paint = new Paint();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = this.getBounds();

        Path star = new Path();

        star.moveTo(bounds.centerX(), bounds.top);
        star.lineTo(bounds.right, bounds.centerY());
        star.lineTo(bounds.centerX(), bounds.bottom);
        star.lineTo(bounds.left, bounds.centerY());


        this.paint.setColor(this.color);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(star, this.paint);
    }


    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public int getAlpha() {
        return this.alpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }
}