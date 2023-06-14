package com.xiaoxun.xun.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.xiaoxun.xun.R;


/**
 * author：wangxin
 * date：2022/8/17
 * des： 录制按钮
 */
public class RecordView extends View implements View.OnLongClickListener, View.OnClickListener {
    private static final int PROGRESS_INTERVAL = 100;
    private int mBgColor;
    private int mStrokeColor;
    private int mStrokeWidth;
    private int mDuration;
    private int mWidth;
    private int mHeight;
    private int mProgressValue;
    private boolean isRecording;
    private RectF mArcRectF;
    private Paint mBgPaint, mBgPaint2, mProgressPaint;
    private OnRecordListener mOnRecordListener;
    private long mStartRecordTime;

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public int getmProgressValue() {
        return mProgressValue;
    }

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView);
        mBgColor = typedArray.getColor(R.styleable.RecordView_record_color, Color.WHITE);
        mStrokeColor = typedArray.getColor(R.styleable.RecordView_stroke_color, Color.GREEN);
        mStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.RecordView_stroke_width, SizeUtils.dp2px(6));
        mDuration = typedArray.getInteger(R.styleable.RecordView_record_duration, 16);
        typedArray.recycle();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBgColor);
        mBgPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint2.setStyle(Paint.Style.STROKE);
        mBgPaint2.setColor(0xFFDBDBDB);
        mBgPaint2.setStrokeWidth(mStrokeWidth);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setColor(mStrokeColor);
        mProgressPaint.setStrokeWidth(mStrokeWidth);

        setEvent();
    }

    private void setEvent() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mProgressValue++;
                if (isRecording) {
                    postInvalidate();
                }
                if (mProgressValue < mDuration * 10) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                } else {
                    finishRecord();
                }
            }
        };
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mStartRecordTime = System.currentTimeMillis();
                handler.sendEmptyMessage(0);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long duration = System.currentTimeMillis() - mStartRecordTime;
                //是否大于系统设定的最小长按时间
                if (duration > ViewConfiguration.getLongPressTimeout()) {
                    finishRecord();
                }
                handler.removeCallbacksAndMessages(null);
            }
            return false;
        });
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    private void finishRecord() {
        if (isRecording) {
            mStartRecordTime = 0;
            mProgressValue = 0;
            isRecording = false;
            postInvalidate();
            if (mOnRecordListener != null) {
                mOnRecordListener.onFinish();
            }
        }
    }

    public void resetRecord() {
        mStartRecordTime = 0;
        mProgressValue = 0;
        isRecording = false;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = Math.max(w, h);
        mHeight = Math.max(w, h);
        mArcRectF = new RectF(mStrokeWidth / 2f, mStrokeWidth / 2f,
                mWidth - mStrokeWidth / 2f, mHeight - mStrokeWidth / 2f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, mWidth / 2f, mBgPaint);
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, (mWidth - mStrokeWidth) / 2f, mBgPaint2);
        if (isRecording) {
            float sweepAngle = 360f * mProgressValue / (mDuration * 10);
            Log.i("sweepAngle", sweepAngle + "");
            canvas.drawArc(mArcRectF, 180, sweepAngle, false, mProgressPaint);
        }

    }

    @Override
    public boolean onLongClick(View v) {
        isRecording = true;
        if (mOnRecordListener != null) {
            mOnRecordListener.onRecordVideo();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (mOnRecordListener != null) {
            mOnRecordListener.onTackPicture();
        }
    }

    public interface OnRecordListener {
        void onTackPicture();

        void onRecordVideo();

        void onFinish();
    }

}
