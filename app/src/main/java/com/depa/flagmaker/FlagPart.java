package com.depa.flagmaker;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import com.depa.flagmaker.Constants.*;
import static com.depa.flagmaker.Constants.px;

class FlagPart {
    private Pos pos;
    private PartType pt;
    Inset inset;
    private int color;
    private float size; //0 to 1

    FlagPart(Pos _pos, PartType _pt, int _color, float _size){
        pos = _pos;
        pt = _pt;
        color = _color;
        inset = new Inset();
        size = _size;

        makeInset();
    }

    private void makeInset() {
        inset = new Inset();
        if (pt == PartType.rhombus) {
            int i = px(80 - 80 * size);
            inset = new Inset(i, i, i, i);
        }
        if(pt == PartType.canton)
            if (pos != Pos.end)
                inset = new Inset(0, 0, px(150), px(100));
            else
                inset = new Inset(px(150), px(100), 0, 0);
        else if (pt == PartType.shield)
            inset = new Inset(
                    px(125) + 100 * getSign(), px(70),
                    px(125) - 100 * getSign(), px(70));
        else if (pt == PartType.circle) {
            int i = px(80 - 80 * size);
            inset = new Inset(
                    i + 100 + 100 * getSign(), i,
                    i + 100 - 100 * getSign(), i);
        } else if (pt == PartType.star) {
            int i = px(100 - 100 * size);
            inset = new Inset(
                    i + 100 + 120 * getSign(), i,
                    i + 100 - 120 * getSign(), i);
        } else {
            if (pt == PartType.fill)
                inset = new Inset();
            else if (pt == PartType.chevron) {
                if (pos != Pos.end)
                    inset = new Inset(0, 0, px(300 - 300 * size), 0);
                else
                    inset = new Inset(px(300 - 300 * size), 0, 0, 0);
            } else if (pt == PartType.nordic)
                inset = new Inset();
            else if (pt == PartType.bend)
                inset = new Inset(-px(100), -px(100), -px(100), -px(100));
            else if (pos == Pos.end)
                switch (pt) {      //else: is a band
                    case band_h:
                        inset.top = px(200 - 200 * size);
                        break;
                    case band_v:
                        inset.left = px(300 - 300 * size);
                        break;
                }
            else if (pos == Pos.start)
                switch (pt) {
                    case band_h:
                        inset.bottom = px(200 - 200 * size);
                        break;
                    case band_v:
                        inset.right = px(300 - 300 * size);
                        break;
                }
            else if (pos == Pos.center)
                switch (pt) {
                    case band_h:
                        inset.bottom = px(200 - 200 * size) / 2;
                        inset.top = px(200 - 200 * size) / 2;
                        break;
                    case band_v:
                        inset.right = px(300 - 300 * size) / 2;
                        inset.left = px(300 - 300 * size) / 2;
                        break;
                }
        }
    }

    Drawable makeDrawable () {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        switch (pt) {
            case band_v:
            case band_h:
            case fill:
            case canton:
                drawable.setShape(GradientDrawable.RECTANGLE);
                return drawable;

            case bend:
                drawable.setShape(GradientDrawable.LINE);
                drawable.setStroke((int) (size * 96), color);
                return getRotateDrawable(drawable, Math.toDegrees(Math.atan(2f/3 * (pos == Pos.end ? -1 : 1))));

            case nordic:
                return getCrossDrawable();

            case chevron:
                return new Chevron(color, pos == Pos.end);

            case circle:
                drawable.setShape(GradientDrawable.OVAL);
                return drawable;

            case shield:
                return new Shield(color);

            case star:
                return new Star(color);

            case rhombus:
                return new Rhombus(color);

            default:
                return drawable;
        }
    }

    void setType(PartType _pt) {
        pt = _pt;
        makeInset();
    }

    void setPos(Pos _p) {
        pos = _p;
        makeInset();
    }

    void setColor(int _c) {
        color = _c;
    }

    void setSize(int _level){
        size = (_level + 1f) / 12f;
        makeInset();
    }

    private Drawable getRotateDrawable(final Drawable d, final double angle) {
        final Drawable[] arD = { d };
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate((float) angle, getBounds().centerX(), getBounds().centerY());
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    Drawable getSmallDrawable() {
        if (pt == PartType.nordic) {
            LayerDrawable ld = (LayerDrawable) makeDrawable();
            ld.setLayerInset(0,
                    0,px(100 - 50 * size)/3,
                    0,px(100 - 50 * size)/3);
            ld.setLayerInset(1,
                    px(100 - 50 * size)/3,0,
                    px(200 - 50 * size)/3,0);
            return ld;
        }
        final Drawable[] arD = { makeDrawable() };
        LayerDrawable ld = new LayerDrawable(arD);
        ld.setLayerInset(0, inset.left/3, inset.top/3, inset.right/3, inset.bottom/3);
        return ld;
    }


    private Drawable getCrossDrawable() {
        GradientDrawable[] darr = new GradientDrawable[]{
                new GradientDrawable(),
                new GradientDrawable()
        };

        darr[0].setShape(GradientDrawable.RECTANGLE);
        darr[1].setShape(GradientDrawable.RECTANGLE);

        darr[0].setColor(color);
        darr[1].setColor(color);

        LayerDrawable ld = new LayerDrawable(darr);

        ld.setLayerInset(0,
                0,px(100 - 50 * size),
                0,px(100 - 50 * size));
        ld.setLayerInset(1,
                px(100 - 50 * size),0,
                px(200 - 50 * size),0);

        return ld;
    }

    private int getSign() {
        switch (pos) {
            case start:
                return -1;
            case center:
                return 0;
            case end:
                return 1;
        }
        return -2;
    }
}
