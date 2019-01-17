package com.manu.mdatepicker;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Powered by jzman.
 * Created on 2019/1/16 0016.
 */
public class Util {
    /**
     * get screen width.
     * @param context
     * @return
     */
    public static float getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * dp to pix
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * pix to dp
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
