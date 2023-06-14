package com.xiaoxun.xun.motion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;

public class LayoutStateView extends LinearLayout {

    private Bitmap ivIcon;
    private CharSequence tvTitle;
    private String tvState;
    private boolean isHasNext;

    private TextView mState;

    public LayoutStateView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LayoutStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public LayoutStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.motion_layout);
        BitmapDrawable ivIconBitmapDrawable=(BitmapDrawable) ta.getDrawable(R.styleable.motion_layout_motion_layout_icon);
        if (ivIconBitmapDrawable != null)
            ivIcon=ivIconBitmapDrawable.getBitmap();
        tvTitle = ta.getText(R.styleable.motion_layout_motion_layout_title);
        tvState = ta.getString(R.styleable.motion_layout_motion_layout_state);
        isHasNext = ta.getBoolean(R.styleable.motion_layout_motion_layout_next, false);
        ta.recycle();

        View view = View.inflate(context, R.layout.layout_state_view, null);
        ImageView ivIconView = view.findViewById(R.id.iv_icon);
        TextView mTitle = view.findViewById(R.id.tv_title);
        mState = view.findViewById(R.id.tv_state);
        ImageView mNext = view.findViewById(R.id.iv_next_to);

        ivIconView.setImageBitmap(ivIcon);
        mTitle.setText(tvTitle);
        mState.setText(tvState);
        if(isHasNext){
            mNext.setVisibility(VISIBLE);
        }else{
            mNext.setVisibility(GONE);
        }

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        addView(view, params);
    }

    public void setTvState(String tvState) {
        String defState = getContext().getString(R.string.motion_no_setting);
        if(tvState.equals(defState) || "00".equals(tvState)
        ){
            mState.setText(defState);
            this.tvState = defState;
        }else {
            this.tvState = String.valueOf(Integer.valueOf(tvState));
            mState.setText(this.tvState + getContext().getString(R.string.unit_minute));
        }
    }

    public String getTvState(){
        return tvState;
    }
}