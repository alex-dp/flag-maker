package com.depa.flagmaker;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Shield extends Drawable {

    private Paint paint;
    private int color;
    private int alpha;

    Shield(int color) {
        this.color = color;
        this.paint = new Paint();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = this.getBounds();

        Path shield = new Path();

        shield.moveTo(bounds.left, bounds.top);
        shield.lineTo(bounds.right, bounds.top);
        shield.lineTo(bounds.right, bounds.top + .6f * bounds.height());

        RectF r = new RectF(bounds.left,
                bounds.top + .6f * bounds.height(),
                bounds.right,
                bounds.bottom);

        shield.arcTo(r,0, 180);

        this.paint.setColor(this.color);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(shield, this.paint);
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