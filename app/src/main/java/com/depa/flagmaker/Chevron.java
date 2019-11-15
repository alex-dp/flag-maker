package com.depa.flagmaker;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Chevron extends Drawable {

    private Paint paint;
    private int color;
    private int alpha;
    private boolean right;

    Chevron(int color, boolean right) {
        this.color = color;
        this.right = right;
        this.paint = new Paint();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = this.getBounds();

        Path triangle = new Path();
        if (right) {
            triangle.moveTo(bounds.right, bounds.top);
            triangle.lineTo(bounds.left, bounds.top + (bounds.bottom - bounds.top) / 2);
            triangle.lineTo(bounds.right, bounds.bottom);
            triangle.lineTo(bounds.right, bounds.top);
        } else {
            triangle.moveTo(bounds.left, bounds.top);
            triangle.lineTo(bounds.right, bounds.top + (bounds.bottom - bounds.top) / 2);
            triangle.lineTo(bounds.left, bounds.bottom);
            triangle.lineTo(bounds.left, bounds.top);
        }

        this.paint.setColor(this.color);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(triangle, this.paint);
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