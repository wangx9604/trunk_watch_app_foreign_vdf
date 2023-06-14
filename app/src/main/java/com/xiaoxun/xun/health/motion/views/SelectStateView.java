package com.xiaoxun.xun.health.motion.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiaoxun.xun.R;

@SuppressLint("AppCompatCustomView")
public class SelectStateView extends TextView {

    boolean isSelect = false;
    String mState = "1";

    public SelectStateView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SelectStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public SelectStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.motion);
        isSelect = ta.getBoolean(R.styleable.motion_motion_select, false);
        mState = ta.getString(R.styleable.motion_motion_state);

        ta.recycle();
        updateView();

    }

    private void updateView() {
        switch (mState){
            case "1":
                if(isSelect){
                    setTextColor(Color.WHITE);
                    setBackgroundResource(R.drawable.motion_rect_0_orag);
                }else{
                    setTextColor(Color.BLACK);
                    setBackgroundResource(R.drawable.motion_rect_0_grey);
                }
                break;
            case "2":
                if(isSelect){
                    setTextColor(Color.WHITE);
                    setBackgroundResource(R.drawable.motion_rect_1_orag);
                }else{
                    setTextColor(Color.BLACK);
                    setBackgroundResource(R.drawable.motion_rect_1_grep);
                }
                break;
        }
    }

    public void setSelect(boolean select) {
        isSelect = select;
        updateView();
    }

    public boolean isSelect() {
        return isSelect;
    }
}