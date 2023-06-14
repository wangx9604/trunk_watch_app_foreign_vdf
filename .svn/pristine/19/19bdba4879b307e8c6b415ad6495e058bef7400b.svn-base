package com.xiaoxun.xun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;

/**
 * Created by zhangjun5 on 2019/7/17.
 */

public class view_item_doc extends RelativeLayout {

    private TextView tv_hint1;
    private TextView tv_hint2;

    public view_item_doc(Context context) {
        this(context,null);
    }

    public view_item_doc(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.viewgroup_text_hint, null);
        tv_hint1 = view.findViewById(R.id.tv_hint1);
        tv_hint2 = view.findViewById(R.id.tv_hint2);
        addView(view);
    }

    public void setHint1AndHint2(String hint1,String hint2){
        tv_hint1.setText(hint1);
        tv_hint2.setText(hint2);
    }


}
