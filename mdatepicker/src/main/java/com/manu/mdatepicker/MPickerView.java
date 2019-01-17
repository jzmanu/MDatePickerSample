package com.manu.mdatepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.manu.mdatepicker.Util.dpToPx;

/**
 * Powered by jzman.
 * Created on 2018/12/25 0025.
 */
public class MPickerView extends View {
    private static final String TAG = MPickerView.class.getSimpleName();

    public static final float SPEED = 5;
    //行距与mTextSizeNormal之比，保证View内显示的内容在适当的位置
    private float RATE = 2.7f;

    private Paint mPaintNormal;
    private Paint mPaintSelect;
    private Paint mPaintText;
    private Paint mPaintLine;

    private float mTextSizeNormal;
    private float mTextSizeSelect;

    private float mTextAlphaSelect;
    private float mTextAlphaNormal;

    private float mPaddingStart;
    private float mPaddingEnd;

    //选中的位置
    private int mSelectPosition;
    //开始触摸的位置
    private float mStartTouchY;
    //手指滑动的距离
    private float mMoveDistance;
    private int mWidth;
    private int mHeight;
    private String mText;

    private Timer mTimer;
    private MTimerTask mTask;
    private Handler mHandler;
    private Context mContext;
    private List<String> mData;
    private OnSelectListener mOnSelectListener;


    public MPickerView(Context context) {
        this(context, null);
    }

    public MPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintNormal.setTextAlign(Paint.Align.CENTER);
        mPaintSelect.setTextAlign(Paint.Align.CENTER);
        mPaintNormal.setTextSize(mTextSizeNormal);
        mPaintSelect.setTextSize(mTextSizeSelect);
        mPaintText.setTextSize(dpToPx(context, 15));
        mPaintSelect.setColor(0xffa000);
        mPaintNormal.setColor(0xa2a2a2);
        mPaintText.setColor(Color.parseColor("#ffa000"));
        mPaintLine.setColor(Color.parseColor("#dbdbdb"));
        mPaintLine.setStrokeWidth(dpToPx(context, 0.5f));
        mHandler = new MHandler(this);
        mData = new ArrayList<>();
        mTimer = new Timer();
        mTextAlphaSelect = 255;
        mTextAlphaNormal = 120;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mTextSizeSelect = mContext.getResources().getDisplayMetrics().density * 70 / 3;
        mTextSizeNormal = mTextSizeSelect / 2f;

        //默认宽高
        mPaintSelect.setTextSize(mTextSizeSelect);
        mPaintNormal.setTextSize(mTextSizeNormal);
        int mDefaultWidth = (int) (mPaintSelect.measureText("0000") + mPaintText.measureText("时") * 2);
        Paint.FontMetricsInt mAnIntSelect = mPaintSelect.getFontMetricsInt();
        Paint.FontMetricsInt mAnIntNormal = mPaintNormal.getFontMetricsInt();
        int mDefaultHeight = mAnIntSelect.bottom - mAnIntSelect.top + (mAnIntNormal.bottom - mAnIntNormal.top) * 4;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mDefaultWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mDefaultHeight);
        }

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaddingStart = getPaddingStart();
        mPaddingEnd = getPaddingEnd();
        //绘制中间位置
        draw(canvas, 1, 0, mPaintSelect);
        //绘制上方数据
        for (int i = 1; i < mSelectPosition - 1; i++) {
            draw(canvas, -1, i, mPaintNormal);
        }
        //绘制下方数据
        for (int i = 1; (mSelectPosition + i) < mData.size(); i++) {
            draw(canvas, 1, i, mPaintNormal);
        }
        invalidate();
    }

    private void draw(Canvas canvas, int type, int position, Paint paint) {
        float space = RATE * mTextSizeNormal * position + type * mMoveDistance;
        float scale = parabola(mHeight / 4.0f, space);
        float size = (mTextSizeSelect - mTextSizeNormal) * scale + mTextSizeNormal;
        int alpha = (int) ((mTextAlphaSelect - mTextAlphaNormal) * scale + mTextAlphaNormal);
        paint.setTextSize(size);
        paint.setAlpha(alpha);

        float x = mWidth / 2.0f;
        float y = mHeight / 2.0f + type * space;
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        float baseline = y + (fmi.bottom - fmi.top) / 2.0f - fmi.descent;
        float width = mPaintText.measureText("年");
        canvas.drawText(mData.get(mSelectPosition + type * position), x - width / 2 + mPaddingStart - mPaddingEnd, baseline, paint);

        if (position == 0) {
            mPaintSelect.setTextSize(mTextSizeSelect);
            float startX;
            if (mData.get(mSelectPosition).length() == 4) {
                startX = mPaintSelect.measureText("0000") / 2 + x - width / 2 + dpToPx(mContext, 2.0f) + mPaddingStart - mPaddingEnd;
            } else {
                startX = mPaintSelect.measureText("00") / 2 + x - width / 2 + dpToPx(mContext, 2.0f) + mPaddingStart - mPaddingEnd;
            }

            Paint.FontMetricsInt anInt = mPaintText.getFontMetricsInt();
            if (!TextUtils.isEmpty(mText))
                canvas.drawText(mText, startX, mHeight / 2.0f + (anInt.bottom - anInt.top) / 2.0f - anInt.descent, mPaintText);

            Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
            float line = mHeight / 2.0f + (metricsInt.bottom - metricsInt.top) / 2.0f - metricsInt.descent;
            canvas.drawLine(0, line + metricsInt.ascent - dpToPx(mContext, 2.0f), mWidth, line + metricsInt.ascent - dpToPx(mContext, 2.0f), mPaintLine);
            canvas.drawLine(0, line + metricsInt.descent + dpToPx(mContext, 2.0f), mWidth, line + metricsInt.descent + dpToPx(mContext, 2.0f), mPaintLine);
            canvas.drawLine(0, dpToPx(mContext, 0.5f), mWidth, dpToPx(mContext, 0.5f), mPaintLine);
            canvas.drawLine(0, mHeight - dpToPx(mContext, 0.5f), mWidth, mHeight - dpToPx(mContext, 0.5f), mPaintLine);
        }
    }

    private float parabola(float zero, float x) {
        float y = (float) (1 - Math.pow(x / zero, 2));
        return y < 0 ? 0 : y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveDistance += (event.getY() - mStartTouchY);
                if (mMoveDistance > RATE * mTextSizeNormal / 2) {//向下滑动
                    moveTailToHead();
                    mMoveDistance = mMoveDistance - RATE * mTextSizeNormal;
                } else if (mMoveDistance < -RATE * mTextSizeNormal / 2) {//向上滑动
                    moveHeadToTail();
                    mMoveDistance = mMoveDistance + RATE * mTextSizeNormal;
                }
                mStartTouchY = event.getY();
                invalidate();
//                Log.i(TAG, "-mMoveDistance---" + mMoveDistance);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mMoveDistance) < 0.0001) {
                    mMoveDistance = 0;
                    return true;
                }
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                mTask = new MTimerTask(mHandler);
                mTimer.schedule(mTask, 0, 10);
        }
        return true;
    }

    public void setData(@NonNull List<String> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            mSelectPosition = data.size() / 2;
        }
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    private void moveHeadToTail() {
        if (mData.size() > 0) {
            String head = mData.get(0);
            mData.remove(0);
            mData.add(head);
        }
    }

    private void moveTailToHead() {
        if (mData.size() > 0) {
            String tail = mData.get(mData.size() - 1);
            mData.remove(mData.size() - 1);
            mData.add(0, tail);
        }
    }

    private void setSelectPosition(int position) {
        mSelectPosition = position;
        int value = mData.size() / 2 - mSelectPosition;
        if (value < 0) {
            for (int i = 0; i < -value; i++) {
                moveHeadToTail();
                mSelectPosition--;
            }
        } else if (value > 0) {
            for (int i = 0; i < value; i++) {
                moveTailToHead();
                mSelectPosition++;
            }
        }
        invalidate();
    }

    public void setDefaultValue(@NonNull String value) {
        if (mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                if (value.equals(mData.get(i))) {
                    setSelectPosition(i);
                    break;
                }
            }
        }
    }

    public void setDefaultValue(@NonNull String value, @DateType.Type String type, String replace) {
        if (mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                String data = getStringToNumber(type, mData.get(i), replace);
                if (value.equals(data)) {
                    setSelectPosition(i);
                    break;
                }
            }
        }
    }

    private String getStringToNumber(@DateType.Type String type, String value, String replace) {
        switch (type) {
            case DateType.MONTH:
            case DateType.DAY:
                if (value.startsWith("0") && value.length() == 2) {
                    return value.substring(1);
                }
            case DateType.HOUR_12:
            case DateType.HOUR_24:
            case DateType.MINUTE:
                if (value.equals("00")) {
                    return replace;
                } else if (value.startsWith("0") && value.length() == 2) {
                    return value.substring(1);
                }
            default:
                return value;
        }
    }

    public String getSelectValue() {
        return mData.size() > 0 ? mData.get(mSelectPosition) : "";
    }

    static class MHandler extends Handler {
        private WeakReference<View> mWeakReference;

        MHandler(View view) {
            this.mWeakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MPickerView mPickerView = (MPickerView) mWeakReference.get();
            if (mPickerView != null) {
                if (Math.abs(mPickerView.mMoveDistance) < SPEED) {
                    mPickerView.mMoveDistance = 0;
                    if (mPickerView.mTask != null) {
                        mPickerView.mTask.cancel();
                        mPickerView.mTask = null;
                        if (mPickerView.mOnSelectListener != null)
                            mPickerView.mOnSelectListener.onSelect(mPickerView, mPickerView.mData.get(mPickerView.mSelectPosition));
                    }
                } else {
                    mPickerView.mMoveDistance = mPickerView.mMoveDistance - mPickerView.mMoveDistance /
                            Math.abs(mPickerView.mMoveDistance) * SPEED;
                }
                mPickerView.invalidate();
            }
        }
    }

    class MTimerTask extends TimerTask {
        Handler handler;

        MTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public interface OnSelectListener {
        void onSelect(View view, String data);
    }

}
