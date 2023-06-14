package com.xiaoxun.xun.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;

/**
 * @author cuiyufeng
 * @Description: CustomSettingNewView
 * @date 2018/12/13 16:01
 */
public class CustomSettingNewView extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvTitle;
//    private TextView tvState;
    private ImageView redPoint;
//    private ImageView ivArrow;

    private Bitmap ivIconBitmap;
    private CharSequence tvTitleString;
//    private CharSequence tvStateString;
    private boolean isRedPointVisible;
    private float tvTitleSize;

    public CustomSettingNewView(Context context) {
        this(context, null);
    }

    public CustomSettingNewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSettingNewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        getResourceFromAttrs(context, attrs);
        initView(context);
    }

    private void getResourceFromAttrs(Context context, AttributeSet attrs) {

        //获得类型数组，参数二是属性集合对应的id
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.CustomSettingView);
        //获得指定名称的属性
        BitmapDrawable ivIconBitmapDrawable=(BitmapDrawable) ta.getDrawable(R.styleable.CustomSettingView_iv_icon);
        if (ivIconBitmapDrawable != null)
            ivIconBitmap=ivIconBitmapDrawable.getBitmap();
        tvTitleString = ta.getText(R.styleable.CustomSettingView_tv_title);
        //tvStateString = ta.getText(R.styleable.CustomSettingView_tv_state);
        isRedPointVisible = ta.getBoolean(R.styleable.CustomSettingView_is_redpoint_visible, false);
        tvTitleSize = ta.getDimension(R.styleable.CustomSettingView_tv_title_size, 0);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_setting_me, null);
        ivIcon= view.findViewById(R.id.iv_setting_icon);
        tvTitle= view.findViewById(R.id.tv_setting_text);
        //tvState= (TextView) view.findViewById(R.id.setting_state);
        redPoint= view.findViewById(R.id.iv_redpoint);
        //ivArrow= (ImageView) view.findViewById(R.id.iv_arrow);

        if (ivIconBitmap != null) {
            ivIcon.setImageBitmap(ivIconBitmap);
        } else {
            ivIcon.setVisibility(GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            tvTitle.setLayoutParams(layoutParams);
        }
        tvTitle.setText(tvTitleString);
        //tvState.setText(tvStateString);
        if (tvTitleSize != 0)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,tvTitleSize);

        if (isRedPointVisible){
            redPoint.setVisibility(VISIBLE);
        }else {
            redPoint.setVisibility(GONE);
        }
        addView(view);
    }

    /**
     * 设置小红点是否可见
     * @param visible 是否可见
     */
    public void setRedPointVisible(boolean visible){
        if (visible){
            redPoint.setVisibility(VISIBLE);
        }else{
            redPoint.setVisibility(GONE);
        }
    }

    /**
     * 设置条目标题内容
     * @param titleString 标题内容
     */
    public void setTitle(String titleString){
        tvTitle.setText(titleString);
    }

    public void setIvIcon(int icon){
        ivIcon.setVisibility(View.VISIBLE);
        ivIcon.setBackgroundResource(icon);
    }

    /**
     * get tvTitle
     */
    public TextView getTvTitle(){
        return tvTitle;
    }

    /**
     * Touch事件给中断掉
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
