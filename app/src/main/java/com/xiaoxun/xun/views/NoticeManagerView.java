package com.xiaoxun.xun.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;


public class NoticeManagerView extends LinearLayout {

    private TextView titleView;
    private ImageView checkView;

    private CharSequence title;
    private boolean checked;
    private float titleSize;

    public NoticeManagerView(Context context) {
        this(context, null);
    }

    public NoticeManagerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoticeManagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getResourceFromAttrs(context, attrs);
        initView(context);
    }

    private void getResourceFromAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NoticeManagerView);
        title = ta.getText(R.styleable.NoticeManagerView_notice_title);
        checked = ta.getBoolean(R.styleable.NoticeManagerView_checked, true);
        titleSize = ta.getDimension(R.styleable.NoticeManagerView_notice_title_size, 0);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_notice_manager, null);
        checkView = view.findViewById(R.id.iv_checked);
        titleView = view.findViewById(R.id.tv_title);

        titleView.setText(title);
        if (titleSize != 0) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
        if (checked) {
            checkView.setImageResource(R.drawable.switch_on);
        } else {
            checkView.setImageResource(R.drawable.switch_off);
        }

        addView(view);
    }

    public void setOnCkeckListener(String tag, OnClickListener listener) {
        checkView.setOnClickListener(listener);
        checkView.setTag(tag);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked) {
            checkView.setImageResource(R.drawable.switch_on);
        } else {
            checkView.setImageResource(R.drawable.switch_off);
        }
    }

    public boolean isChecked() {
        return this.checked;
    }
}
