package com.manu.mdatepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.manu.mdatepicker.Util.dpToPx;
import static com.manu.mdatepicker.Util.getScreenWidth;

/**
 * Powered by jzman.
 * Created on 2019/1/3 0003.
 */
public class MDatePickerDialog extends Dialog implements MPickerView.OnSelectListener, View.OnClickListener {
    private static final String TAG = MDatePickerDialog.class.getSimpleName();
    private static final int SPACE = 5;

    private Context mContext;

    private MPickerView mpvDialogDay;
    private MPickerView mpvDialogYear;
    private MPickerView mpvDialogMonth;
    private MPickerView mpvDialogHour;
    private MPickerView mpvDialogMinute;

    private TextView tvDialogTitle;
    private TextView tvDialogTopCancel;
    private TextView tvDialogTopConfirm;
    private TextView tvDialogBottomCancel;
    private TextView tvDialogBottomConfirm;
    private LinearLayout llDialogBottom;
    private LinearLayout llDialog;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private int mCurrentHour;
    private int mCurrentMinute;

    private String mTitle;
    private int mGravity;
    private boolean isCanceledTouchOutside;
    private boolean isSupportTime;
    private boolean isTwelveHour;
    private float mConfirmTextSize;
    private float mCancelTextSize;
    private int mConfirmTextColor;
    private int mCancelTextColor;
    private OnDateResultListener mOnDateResultListener;

    private MDatePickerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);
        initView();
        initData();
    }

    private void initView() {
        mpvDialogDay = findViewById(R.id.mpvDialogDay);
        mpvDialogYear = findViewById(R.id.mpvDialogYear);
        mpvDialogMonth = findViewById(R.id.mpvDialogMonth);
        mpvDialogHour = findViewById(R.id.mpvDialogHour);
        mpvDialogMinute = findViewById(R.id.mpvDialogMinute);
        tvDialogTitle = findViewById(R.id.tvDialogTitle);

        tvDialogTopCancel = findViewById(R.id.tvDialogTopCancel);
        tvDialogTopConfirm = findViewById(R.id.tvDialogTopConfirm);
        tvDialogBottomCancel = findViewById(R.id.tvDialogBottomCancel);
        tvDialogBottomConfirm = findViewById(R.id.tvDialogBottomConfirm);
        llDialogBottom = findViewById(R.id.llDialogBottom);
        llDialog = findViewById(R.id.llDialog);

        mpvDialogYear.setOnSelectListener(this);
        mpvDialogMonth.setOnSelectListener(this);
        mpvDialogDay.setOnSelectListener(this);
        mpvDialogHour.setOnSelectListener(this);
        mpvDialogMinute.setOnSelectListener(this);

        tvDialogTopCancel.setOnClickListener(this);
        tvDialogTopConfirm.setOnClickListener(this);
        tvDialogBottomCancel.setOnClickListener(this);
        tvDialogBottomConfirm.setOnClickListener(this);
    }

    private void initData() {
        Calendar mCalendar = Calendar.getInstance();
        int mMaxYear = mCalendar.get(Calendar.YEAR) + SPACE;
        int mMinYear = mCalendar.get(Calendar.YEAR) - SPACE;

        List<String> mDataYear = new ArrayList<>();
        List<String> mDataMonth = new ArrayList<>();
        List<String> mDataHour = new ArrayList<>();
        List<String> mDataMinute = new ArrayList<>();

        mpvDialogYear.setText(mContext.getString(R.string.strDateYear));
        mpvDialogMonth.setText(mContext.getString(R.string.strDateMonth));
        mpvDialogDay.setText(mContext.getString(R.string.strDateDay));
        mpvDialogHour.setText(mContext.getString(R.string.strDateHour));
        mpvDialogMinute.setText(mContext.getString(R.string.strDateMinute));

        //Year
        for (int i = mMinYear; i <= mMaxYear; i++) {
            mDataYear.add(String.valueOf(i));
        }
        mpvDialogYear.setData(mDataYear);
        mCurrentYear = mCalendar.get(Calendar.YEAR);

        //Month+
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                mDataMonth.add("0" + i);
            } else {
                mDataMonth.add(String.valueOf(i));
            }
        }
        mpvDialogMonth.setData(mDataMonth);
        mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;
        mpvDialogMonth.setDefaultValue(String.valueOf(mCurrentMonth), DateType.MONTH, "-1");

        //Day
        mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        updateDay(mCurrentYear, mCurrentMonth);

        //Hour
        if (isTwelveHour) {
            mCurrentHour = mCalendar.get(Calendar.HOUR);
            addTimeData(mDataHour, 13, 12);
            mpvDialogHour.setData(mDataHour);
            mpvDialogHour.setDefaultValue(String.valueOf(mCurrentHour), DateType.HOUR_12, "12");
        } else {
            mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            addTimeData(mDataHour, 25, 24);
            mpvDialogHour.setData(mDataHour);
            mpvDialogHour.setDefaultValue(String.valueOf(mCurrentHour), DateType.HOUR_12, "24");
        }

        //Minute
        addTimeData(mDataMinute, 61, 60);
        mpvDialogMinute.setData(mDataMinute);
        mCurrentMinute = mCalendar.get(Calendar.MINUTE);
        mpvDialogMinute.setDefaultValue(String.valueOf(mCurrentMinute), DateType.MINUTE, "60");

        //Setting
        if (!TextUtils.isEmpty(mTitle)) tvDialogTitle.setText(mTitle);

        Window window = getWindow();
        WindowManager.LayoutParams params = Objects.requireNonNull(window).getAttributes();
        params.gravity = mGravity;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setBackgroundDrawableResource(android.R.color.transparent);

        setCanceledOnTouchOutside(isCanceledTouchOutside);

        if (mGravity == Gravity.BOTTOM) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            llDialogBottom.setVisibility(View.GONE);
            llDialog.setBackgroundResource(R.drawable.dialog_date_picker_bottom_bg);
        } else if (mGravity == Gravity.CENTER) {
            params.width = (int) (getScreenWidth((Activity) mContext) * 8 / 9);
            tvDialogTopCancel.setVisibility(View.GONE);
            tvDialogTopConfirm.setVisibility(View.GONE);
            llDialog.setBackgroundResource(R.drawable.dialog_date_picker_center_bg);
        } else {
            params.width = (int) (getScreenWidth((Activity) mContext) * 8 / 9);
            tvDialogTopCancel.setVisibility(View.GONE);
            tvDialogTopConfirm.setVisibility(View.GONE);
            llDialog.setBackgroundResource(R.drawable.dialog_date_picker_center_bg);
        }

        if (isSupportTime) {
            mpvDialogHour.setVisibility(View.VISIBLE);
            mpvDialogMinute.setVisibility(View.VISIBLE);
            float weight = -0.4f * mContext.getResources().getDisplayMetrics().density + 2.6f;
            mpvDialogYear.setLayoutParams(
                    new LinearLayout.LayoutParams(0, dpToPx(mContext, 160), weight));
        } else {
            mpvDialogHour.setVisibility(View.GONE);
            mpvDialogMinute.setVisibility(View.GONE);
            mpvDialogYear.setLayoutParams(
                    new LinearLayout.LayoutParams(0, dpToPx(mContext, 160), 1.0f));
        }

        if (mConfirmTextSize != 0.0f && mConfirmTextSize != -1.0f) {
            tvDialogTopConfirm.setTextSize(mConfirmTextSize);
            tvDialogBottomConfirm.setTextSize(mConfirmTextSize);
        }

        if (mConfirmTextColor != 0 && mConfirmTextColor != -1) {
            tvDialogTopConfirm.setTextColor(mConfirmTextColor);
            tvDialogBottomConfirm.setTextColor(mConfirmTextColor);
        }

        if (mCancelTextSize != 0.0f && mCancelTextSize != -1.0f) {
            tvDialogTopCancel.setTextSize(mCancelTextSize);
            tvDialogBottomCancel.setTextSize(mCancelTextSize);
        }

        if (mCancelTextColor != 0 && mCancelTextColor != -1) {
            tvDialogTopCancel.setTextColor(mCancelTextColor);
            tvDialogBottomCancel.setTextColor(mCancelTextColor);
        }
        window.setAttributes(params);
    }


    @Override
    public void onSelect(View v, String data) {
        int i = v.getId();
        if (i == R.id.mpvDialogYear) {
            mCurrentYear = Integer.valueOf(data);
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (i == R.id.mpvDialogMonth) {
            mCurrentMonth = Integer.valueOf(data);
            updateDay(mCurrentYear, mCurrentMonth);
        } else if (i == R.id.mpvDialogDay) {
            mCurrentDay = Integer.valueOf(data);
        } else if (i == R.id.mpvDialogHour) {
            mCurrentHour = Integer.valueOf(data);
        } else if (i == R.id.mpvDialogMinute) {
            mCurrentMinute = Integer.valueOf(data);
        }
        Log.i(TAG,  mCurrentYear + "-" + mCurrentMonth + "-" + mCurrentDay + "-" + mCurrentHour + "-" + mCurrentMinute);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvDialogTopCancel || i == R.id.tvDialogBottomCancel) {
            dismiss();

        } else if (i == R.id.tvDialogTopConfirm || i == R.id.tvDialogBottomConfirm) {
            Log.i(TAG, "---" + mCurrentYear + "-" + mCurrentMonth + "-" + mCurrentDay + "-" + mCurrentHour + "-" + mCurrentMinute);
            if (mOnDateResultListener != null) {
                if (isSupportTime) {
                    mOnDateResultListener.onDateResult(getDateMills(mCurrentYear, mCurrentMonth, mCurrentDay, mCurrentHour, mCurrentMinute));
                } else {
                    mOnDateResultListener.onDateResult(getDateMills(mCurrentYear, mCurrentMonth, mCurrentDay, 0, 0));
                }
                dismiss();
            }
        }
    }

    public void setOnDateResultListener(OnDateResultListener onDateResultListener) {
        this.mOnDateResultListener = onDateResultListener;
    }

    private int getDayByYearMonth(int year, int month) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month - 1);
        return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void updateDay(int year, int month) {
        List<String> mDataDay = new ArrayList<>();
        int daySize = getDayByYearMonth(year, month);
        addTimeData(mDataDay, daySize + 1, 32);
        mpvDialogDay.setData(mDataDay);
        mpvDialogDay.setDefaultValue(String.valueOf(mCurrentDay), DateType.DAY, "-1");
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

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private int mGravity;
        private boolean isCanceledTouchOutside;
        private boolean isSupportTime;
        private boolean isTwelveHour;
        private float mConfirmTextSize;
        private float mCancelTextSize;
        private int mConfirmTextColor;
        private int mCancelTextColor;
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

        public Builder setCanceledTouchOutside(boolean canceledTouchOutside) {
            isCanceledTouchOutside = canceledTouchOutside;
            return this;
        }

        public Builder setSupportTime(boolean supportTime) {
            isSupportTime = supportTime;
            return this;
        }

        public Builder setTwelveHour(boolean twelveHour) {
            isTwelveHour = twelveHour;
            return this;
        }

        public Builder setConfirmStatus(float textSize, int textColor) {
            this.mConfirmTextSize = textSize;
            this.mConfirmTextColor = textColor;
            return this;
        }

        public Builder setCancelStatus(float textSize, int textColor) {
            this.mCancelTextSize = textSize;
            this.mCancelTextColor = textColor;
            return this;
        }

        public Builder setOnDateResultListener(OnDateResultListener onDateResultListener) {
            this.mOnDateResultListener = onDateResultListener;
            return this;
        }

        private void applyConfig(MDatePickerDialog dialog) {
            if (this.mGravity == 0) this.mGravity = Gravity.CENTER;
            dialog.mContext = this.mContext;
            dialog.mTitle = this.mTitle;
            dialog.mGravity = this.mGravity;
            dialog.isSupportTime = this.isSupportTime;
            dialog.isTwelveHour = this.isTwelveHour;
            dialog.mConfirmTextSize = this.mConfirmTextSize;
            dialog.mConfirmTextColor = this.mConfirmTextColor;
            dialog.mCancelTextSize = this.mCancelTextSize;
            dialog.mCancelTextColor = this.mCancelTextColor;
            dialog.isCanceledTouchOutside = this.isCanceledTouchOutside;
            dialog.mOnDateResultListener = this.mOnDateResultListener;
        }

        public MDatePickerDialog build() {
            MDatePickerDialog dialog = new MDatePickerDialog(mContext);
            applyConfig(dialog);
            return dialog;
        }
    }

    public interface OnDateResultListener {
        void onDateResult(long date);
    }
}
