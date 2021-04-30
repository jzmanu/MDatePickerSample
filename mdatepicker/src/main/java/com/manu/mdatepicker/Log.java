package com.manu.mdatepicker;

/**
 * Powered by jzman.
 * Created on 2021/4/27 23:02.
 */
public class Log {
    private static final String TAG = "MDatePicker";
    private static final boolean debug = false;

    public static void i(String tag, String msg) {
        if (debug) {
            android.util.Log.i(TAG, tag + " > " + msg);
        }
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + " > " + msg);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(TAG, tag + " > " + msg);
    }
}
