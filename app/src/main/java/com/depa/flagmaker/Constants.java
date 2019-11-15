package com.depa.flagmaker;

import android.content.res.Resources;
import android.util.TypedValue;

abstract class Constants {

    static final int REQ_SAVE_IMAGE = 1;

    enum Pos {
        end, start, center
    }

    enum PartType {
        band_h, band_v, bend, nordic, chevron, fill, circle, shield, canton, star, rhombus
    }

    static class Inset {
        int left, top, right, bottom;

        Inset(){
            left = 0;
            top = 0;
            right = 0;
            bottom = 0;
        }

        Inset(int l, int t, int r, int b){
            left = l;
            top = t;
            right = r;
            bottom = b;
        }
    }

    static int px(float dp) {
        Resources r = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }
}
