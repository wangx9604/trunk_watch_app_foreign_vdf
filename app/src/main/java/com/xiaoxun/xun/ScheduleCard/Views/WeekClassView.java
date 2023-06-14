package com.xiaoxun.xun.ScheduleCard.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.xiaoxun.xun.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeekClassView extends LinearLayout {

    private Context mContext;
    @BindView(R.id.cardview_0)
    CardView mCardView0;
    @BindView(R.id.tv_show_info_0)
    TextView mShowInfo0;
    @BindView(R.id.tv_show_time_0)
    TextView mShowTime0;

    @BindView(R.id.cardview_1)
    CardView mCardView1;
    @BindView(R.id.tv_show_info_1)
    TextView mShowInfo1;
    @BindView(R.id.tv_show_time_1)
    TextView mShowTime1;


    public WeekClassView(Context context) {
        this(context,null);
    }

    public WeekClassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        View view = View.inflate(context, R.layout.custom_week_class_item, null);
        ButterKnife.bind(this, view);
        addView(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
    }

    public void setCardView0ForShow(String getmWeekClassName, String mWeekTime, int mColor) {
        mShowInfo0.setText(getmWeekClassName);
        mShowTime0.setText(mWeekTime);
        if(mColor != -1)
        mCardView0.setCardBackgroundColor(mColor);
    }

    public void setCardView1ForShow(String getmWeekClassName, String mWeekTime, int mColor) {
        mShowInfo1.setText(getmWeekClassName);
        mShowTime1.setText(mWeekTime);
        if(mColor != -1)
        mCardView1.setCardBackgroundColor(mColor);
    }
}
