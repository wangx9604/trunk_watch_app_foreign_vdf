package com.xiaoxun.xun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;

/**
 * Created by zhangjun5 on 2019/7/17.
 */

public class view_ranks_hints extends RelativeLayout {

    private ImageView iv_hint1;
    private TextView tv_hint1;
    private TextView tv_hint2;

    public view_ranks_hints(Context context) {
        this(context,null);
    }

    public view_ranks_hints(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.viewgroup_ranks_hint, null);
        iv_hint1 = view.findViewById(R.id.iv_head_icon);
        tv_hint1 = view.findViewById(R.id.tv_hint1);
        tv_hint2 = view.findViewById(R.id.tv_hint2);
        addView(view,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    public void setHint1AndHint2(int resId, String hint1, String hint2){
        iv_hint1.setBackgroundResource(resId);
        tv_hint1.setText(hint1);
        tv_hint2.setText(hint2);

        iv_hint1.invalidate();
        tv_hint1.invalidate();
        tv_hint2.invalidate();
    }


}
