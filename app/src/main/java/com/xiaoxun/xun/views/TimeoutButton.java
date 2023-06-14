package com.xiaoxun.xun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.ToastUtil;


public class TimeoutButton extends ImageButton {
    private int mInterval = 13;
    private Boolean mTimeFlag = false;
    private Boolean mRepeatAction = false;
    private int mCancelFlag = 1;

    private int mCancel_dimen_top = 0;
    private int mCancel_dimen_bottom = 0;
    private int mCancel_dimen_left = 0;
    private int mCancel_dimen_right = 0;

    public Boolean getmTimeFlag() {
        return mTimeFlag;
    }

    public void setmTimeFlag(Boolean mTimeFlag) {
        this.mTimeFlag = mTimeFlag;
    }

    private Runnable mThread = new Runnable() {
        public void run() {
            if (true) {//isPressed()
                if (mInterval > 0) {
                    mInterval--;
                    if (mInterval == 3) {
                        ToastUtil.showMyToast(getContext(), getResources().getString(R.string.timeout_btn_three_sec_txt), Toast.LENGTH_LONG);
                    }
                    postDelayed(this, 1000);
                } else {
                    mTimeFlag = true;
                    myTouch();
                }
            }
        }
    };
    private MotionEvent myEvent;

    public boolean myTouch() {
        removeCallbacks(mThread);
        myEvent.setAction(MotionEvent.ACTION_UP);
        mCancelFlag = -1;
        return super.dispatchTouchEvent(myEvent);
    }

    public TimeoutButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public TimeoutButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeoutButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean isPointOutSideButton(float x, float y) {
        return x < -10 || x > mCancel_dimen_right
                || y < mCancel_dimen_top;//slide up to cancel.|| y > mCancel_dimen_bottom
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        myEvent = event;
        mCancel_dimen_top = 0 - DensityUtil.dip2px(getContext(), 80);//slide up to cancel.value set
        mCancel_dimen_bottom = getHeight() + DensityUtil.dip2px(getContext(), 5);
        mCancel_dimen_left = 0 - DensityUtil.dip2px(getContext(), 5);
        mCancel_dimen_right = getWidth() + DensityUtil.dip2px(getContext(), 5);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mInterval = 14;
            mTimeFlag = false;
            mRepeatAction = false;
            mCancelFlag = 1;
            postDelayed(mThread, 1000);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isPointOutSideButton(event.getX(), event.getY()) == true && 1 == mCancelFlag) {
                mCancelFlag = 2;
            }
            if (mCancelFlag == 2) {
                removeCallbacks(mThread);
                event.setAction(MotionEvent.ACTION_UP);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            removeCallbacks(mThread);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            removeCallbacks(mThread);
        }
        return super.dispatchTouchEvent(event);
    }

    public Boolean getmRepeatAction() {
        return mRepeatAction;
    }

    public void setmRepeatAction(Boolean mRepeatAction) {
        this.mRepeatAction = mRepeatAction;
    }

    public int getmCancelFlag() {
        return mCancelFlag;
    }

    public void setmCancelFlag(int mCancelFlag) {
        this.mCancelFlag = mCancelFlag;
    }
}
