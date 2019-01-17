package com.manu.mdatepicker;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class DateType {

    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String HOUR_12 = "hour_12";
    static final String HOUR_24 = "hour_24";
    static final String MINUTE = "minute";

    @StringDef({YEAR, MONTH, DAY, HOUR_12, HOUR_24, MINUTE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }
}
