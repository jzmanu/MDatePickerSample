package com.manu.mdatepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.manu.mdatepicker.databinding.DialogDatePickerBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import static com.manu.mdatepicker.Util.dpToPx;
import static com.manu.mdatepicker.Util.getScreenWidth;

/**
 * Powered by jzman.
 * Created on 2019/1/3 0003.
 */
public class MDatePicker extends Dialog implements MPickerView.OnSelectListener, View.OnClickListener {
    private static final String TAG = MDatePicker.class.getSimpleName();
    private static final int YEAR_SPACE = 5;
    private static final int MAX_YEAR = 9999;

    private Context mContext;
    private DialogDatePickerBinding binding;
    private Resources mResources;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private int mCurrentHour;
    private int mCurrentMinute;

    private String mTitle;
    private String mFontType;
    private int mGravity;
    private int mYearValue;
    private int mMonthValue;
    private int mDayValue;
    private boolean isCanceledTouchOutside;
    private boolean isSupportTime;
    private boolean isOnlyYearMonth;
    private boolean isTwelveHour;
    private int mConfirmTextColor;
    private int mCancelTextColor;

    private OnDateResultListener mOnDateResultListener;

    private MDatePicker(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogDatePickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onView();
        onData();
    }

    public static Builder create(Context context) {
        return new Builder(context);
    }

    private void onView() {
        binding.mpvDialogYear.setOnSelectListener(this);
        binding.mpvDialogMonth.setOnSelectListener(this);
        binding.mpvDialogDay.setOnSelectListener(this);
        binding.mpvDialogHour.setOnSelectListener(this);
        binding.mpvDialogMinute.setOnSelectListener(this);

        binding.tvDialogTopCancel.setOnClickListener(this);
        binding.tvDialogTopConfirm.setOnClickListener(this);
        binding.tvDialogBottomCancel.setOnClickListener(this);
        binding.tvDialogBottomConfirm.setOnClickListener(this);
    }

    private void onData() {
        Calendar mCalendar = Calendar.getInstance();
        mResources = mContext.getResources();
        List<String> mDataYear = new ArrayList<>();
        List<String> mDataMonth = new ArrayList<>();
        List<String> mDataHour = new ArrayList<>();
        List<String> mDataMinute = new ArrayList<>();

        binding.mpvDialogYear.setText(mContext.getString(R.string.strDateYear));
        binding.mpvDialogMonth.setText(mContext.getString(R.string.strDateMonth));
        binding.mpvDialogDay.setText(mContext.getString(R.string.strDateDay));
        binding.mpvDialogHour.setText(mContext.getString(R.string.strDateHour));
        binding.mpvDialogMinute.setText(mContext.getString(R.string.strDateMinute));

        // Year
        if (mYearValue > MAX_YEAR || mYearValue < YEAR_SPACE) {
            mCurrentYear = mCalendar.get(Calendar.YEAR);
            if (mYearValue != -1) {
                Log.w(TAG, "year init value is illegal, so select current year.");
            }
        } else {
            mCurrentYear = mYearValue;
        }

        int mMaxYear = mCurrentYear + YEAR_SPACE;
        int mMinYear = mCurrentYear - YEAR_SPACE;
        for (int i = mMinYear; i <= mMaxYear; i++) {
            mDataYear.add(String.valueOf(i));
        }
        binding.mpvDialogYear.setData(mDataYear);

        // Month
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                mDataMonth.add("0" + i);
            } else {
                mDataMonth.add(String.valueOf(i));
            }
        }
        binding.mpvDialogMonth.setData(mDataMonth);

        if (mMonthValue > 12 || mMonthValue < 1) {
            mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;
            if (mYearValue != -1) {
                Log.w(TAG, "month init value is illegal, so select current month.");
            }
        } else {
            mCurrentMonth = mMonthValue;
        }
        binding.mpvDialogMonth.setDefaultValue(String.valueOf(mCurrentMonth), DateType.MONTH, "-1");

        // Day
        int daySize = getDayByYearMonth(mCurrentYear, mCurrentMonth);
        if (mDayValue > daySize || mDayValue < 1) {
            mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            if (mYearValue != -1) {
                Log.w(TAG, "day init value is illegal, so select current day.");
            }
        } else {
            mCurrentDay = mDayValue;
        }
        updateDay(mCurrentYear, mCurrentMonth);

        // Hour
        if (isTwelveHour) {
            mCurrentHour = mCalendar.get(Calendar.HOUR);
            addTimeData(mDataHour, 13, 12);
            binding.mpvDialogHour.setData(mDataHour);
            binding.mpvDialogHour.setDefaultValue(String.valueOf(mCurrentHour), DateType.HOUR_12, "12");
        } else {
            mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            addTimeData(mDataHour, 25, 24);
            binding.mpvDialogHour.setData(mDataHour);
            binding.mpvDialogHour.setDefaultValue(String.valueOf(mCurrentHour), DateType.HOUR_12, "24");
        }

        // Minute
        addTimeData(mDataMinute, 61, 60);
        binding.mpvDialogMinute.setData(mDataMinute);
        mCurrentMinute = mCalendar.get(Calendar.MINUTE);
        binding.mpvDialogMinute.setDefaultValue(String.valueOf(mCurrentMinute), DateType.MINUTE, "60");

        // Setting
        if (!TextUtils.isEmpty(mTitle)) binding.tvDialogTitle.setText(mTitle);

        Window window = getWindow();
        WindowManager.LayoutParams params = Objects.requireNonNull(window).getAttributes();
        params.gravity = mGravity;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        // cancel dialog default padding.
        window.getDecorView().setPadding(0, 0, 0, 0);
        setCanceledOnTouchOutside(isCanceledTouchOutside);

        if (mGravity == Gravity.BOTTOM) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            binding.llDialogBottom.setVisibility(View.GONE);
            binding.llDialog.setBackgroundResource(R.drawable.dialog_date_picker_bottom_bg);
        } else if (mGravity == Gravity.CENTER) {
            params.width = (int) (getScreenWidth((Activity) mContext) * 8 / 9);
            binding.tvDialogTopCancel.setVisibility(View.GONE);
            binding.tvDialogTopConfirm.setVisibility(View.GONE);
            binding.llDialog.setBackgroundResource(R.drawable.dialog_date_picker_center_bg);
        } else {
            params.width = (int) (getScreenWidth((Activity) mContext) * 8 / 9);
            binding.tvDialogTopCancel.setVisibility(View.GONE);
            binding.tvDialogTopConfirm.setVisibility(View.GONE);
            binding.llDialog.setBackgroundResource(R.drawable.dialog_date_picker_center_bg);
        }
//        window.setBackgroundDrawableResource(android.R.color.white);

        // support day
        if (isOnlyYearMonth) {
            isSupportTime = false;
        }

        // support time
        if (isSupportTime) {
            binding.mpvDialogHour.setVisibility(View.VISIBLE);
            binding.mpvDialogMinute.setVisibility(View.VISIBLE);
            float weight = -0.4f * mResources.getDisplayMetrics().density + 2.6f;
            Log.i(TAG, "weight:" + weight);
            binding.mpvDialogYear.setLayoutParams(
                    new LinearLayout.LayoutParams(0, dpToPx(mContext, 160), weight));
        } else {
            binding.mpvDialogHour.setVisibility(View.GONE);
            binding.mpvDialogMinute.setVisibility(View.GONE);

            if (isOnlyYearMonth) {
                binding.mpvDialogDay.setVisibility(View.GONE);
            }
        }

        // set font size
        float fontSize = getTextSize(mFontType);
        binding.tvDialogTopConfirm.setTextSize(fontSize);
        binding.tvDialogTopCancel.setTextSize(fontSize);
        binding.tvDialogBottomConfirm.setTextSize(fontSize);
        binding.tvDialogBottomCancel.setTextSize(fontSize);
        binding.tvDialogTitle.setTextSize(fontSize + 1);

        if (mConfirmTextColor != 0 && mConfirmTextColor != -1) {
            binding.tvDialogTopConfirm.setTextColor(mConfirmTextColor);
            binding.tvDialogBottomConfirm.setTextColor(mConfirmTextColor);
        }

        if (mCancelTextColor != 0 && mCancelTextColor != -1) {
            binding.tvDialogTopCancel.setTextColor(mCancelTextColor);
            binding.tvDialogBottomCancel.setTextColor(mCancelTextColor);
        }
        window.setAttributes(params);
    }


    @Override
    public void onSelect(View v, String data) {
        int i = v.getId();
        if (i == R.id.mpvDialogYear) {
            mCurrentYear = Integer.parseInt(data);
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (i == R.id.mpvDialogMonth) {
            mCurrentMonth = Integer.parseInt(data);
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (i == R.id.mpvDialogDay) {
            mCurrentDay = Integer.parseInt(data);
        } else if (i == R.id.mpvDialogHour) {
            mCurrentHour = Integer.parseInt(data);
        } else if (i == R.id.mpvDialogMinute) {
            mCurrentMinute = Integer.parseInt(data);
        }
        Log.i(TAG, mCurrentYear + "-" + mCurrentMonth + "-" + mCurrentDay + " " + mCurrentHour + ":" + mCurrentMinute);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvDialogTopCancel || i == R.id.tvDialogBottomCancel) {
            dismiss();
        } else if (i == R.id.tvDialogTopConfirm || i == R.id.tvDialogBottomConfirm) {
            Log.i(TAG, mCurrentYear + "-" + mCurrentMonth + "-" + mCurrentDay + " " + mCurrentHour + ":" + mCurrentMinute);
            if (mOnDateResultListener != null) {
                if (isSupportTime) {
                    mOnDateResultListener.onDateResult(getDateMills(mCurrentYear, mCurrentMonth, mCurrentDay, mCurrentHour, mCurrentMinute));
                } else {
                    if (isOnlyYearMonth) {
                        mOnDateResultListener.onDateResult(getDateMills(mCurrentYear, mCurrentMonth, 1, 0, 0));
                    } else {
                        mOnDateResultListener.onDateResult(getDateMills(mCurrentYear, mCurrentMonth, mCurrentDay, 0, 0));
                    }
                }
                dismiss();
            }
        }
    }

    public void setOnDateResultListener(OnDateResultListener onDateResultListener) {
        this.mOnDateResultListener = onDateResultListener;
    }

    private int getDayByYearMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void updateDay(int year, int month) {
        List<String> mDataDay = new ArrayList<>();
        int daySize = getDayByYearMonth(year, month);
        Log.i(TAG, "updateDay > year:" + year + ",month:" + month + ",daySize:" + daySize + ",mCurrentDay:" + mCurrentDay);
        addTimeData(mDataDay, daySize + 1, 32);
        binding.mpvDialogDay.setData(mDataDay);
        if (mCurrentDay > mDataDay.size()) {
            mCurrentDay = mDataDay.size();
        }
        binding.mpvDialogDay.setDefaultValue(String.valueOf(mCurrentDay), DateType.DAY, "-1");
    }

    private void addTimeData(List<String> list, int size, int equal) {
        for (int i = 1; i < size; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else if (i == equal) {
                list.add("00");
            } else {
                list.add(String.valueOf(i));
            }
        }
    }

    private long getDateMills(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    private float getTextSize(@FontType.Type String type) {
        float fontSize;
        if (FontType.LARGE.equals(type)) {
            fontSize = mResources.getDimension(R.dimen.date_picker_setting_large);
        } else if (FontType.MEDIUM.equals(type)) {
            fontSize = mResources.getDimension(R.dimen.date_picker_setting_medium);
        } else if (FontType.NORMAL.equals(type)) {
            fontSize = mResources.getDimension(R.dimen.date_picker_setting_normal);
        } else if (FontType.SMALL.equals(type)) {
            fontSize = mResources.getDimension(R.dimen.date_picker_setting_small);
        } else {
            fontSize = mResources.getDimension(R.dimen.date_picker_setting_normal);
        }
        return Util.pxToSp(mContext, fontSize);
    }

    public static class Builder {
        private final Context mContext;
        private String mTitle;
        private int mGravity;
        private int mYearValue = -1;
        private int mMonthValue = -1;
        private int mDayValue = -1;
        private boolean isCanceledTouchOutside;
        private boolean isSupportTime;
        private boolean isOnlyYearMonth;
        private boolean isTwelveHour;
        private int mConfirmTextColor;
        private int mCancelTextColor;
        private String mFontType = FontType.MEDIUM;
        private OnDateResultListener mOnDateResultListener;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setTitle(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder setGravity(int mGravity) {
            this.mGravity = mGravity;
            return this;
        }

        public Builder setYearValue(@IntRange(from = 5, to = 9999) int yearValue) {
            this.mYearValue = yearValue;
            return this;
        }

        public Builder setMonthValue(@IntRange(from = 1, to = 12) int monthValue) {
            this.mMonthValue = monthValue;
            return this;
        }

        public Builder setDayValue(int dayValue) {
            this.mDayValue = dayValue;
            return this;
        }

        public Builder setCanceledTouchOutside(boolean canceledTouchOutside) {
            isCanceledTouchOutside = canceledTouchOutside;
            return this;
        }

        public Builder setSupportTime(boolean supportTime) {
            isSupportTime = supportTime;
            return this;
        }

        public Builder setOnlyYearMonth(boolean onlyYearMonth) {
            isOnlyYearMonth = onlyYearMonth;
            return this;
        }

        public Builder setTwelveHour(boolean twelveHour) {
            isTwelveHour = twelveHour;
            return this;
        }

        public Builder setFontType(@FontType.Type String type) {
            this.mFontType = type;
            return this;
        }

        public Builder setOnDateResultListener(OnDateResultListener onDateResultListener) {
            this.mOnDateResultListener = onDateResultListener;
            return this;
        }

        private void applyConfig(MDatePicker dialog) {
            if (this.mGravity == 0) this.mGravity = Gravity.CENTER;
            dialog.mContext = this.mContext;
            dialog.mTitle = this.mTitle;
            dialog.mFontType = this.mFontType;
            dialog.mGravity = this.mGravity;
            dialog.mYearValue = this.mYearValue;
            dialog.mMonthValue = this.mMonthValue;
            dialog.mDayValue = this.mDayValue;
            dialog.isSupportTime = this.isSupportTime;
            dialog.isOnlyYearMonth = this.isOnlyYearMonth;
            dialog.isTwelveHour = this.isTwelveHour;
            dialog.mConfirmTextColor = this.mConfirmTextColor;
            dialog.mCancelTextColor = this.mCancelTextColor;
            dialog.isCanceledTouchOutside = this.isCanceledTouchOutside;
            dialog.mOnDateResultListener = this.mOnDateResultListener;
        }

        public MDatePicker build() {
            MDatePicker dialog = new MDatePicker(mContext);
            applyConfig(dialog);
            return dialog;
        }
    }

    public interface OnDateResultListener {
        void onDateResult(long date);
    }
}
