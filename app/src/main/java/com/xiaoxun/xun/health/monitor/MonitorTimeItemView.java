package com.xiaoxun.xun.health.monitor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitorTimeItemView extends LinearLayout {

    @BindView(R.id.tv_show_title)
    TextView mTvTitle;
    @BindView(R.id.tv_show_subtitle)
    TextView mTvSubTitle;
    @BindView(R.id.tv_show_subtitle1)
    TextView mTvSubTitle1;
    @BindView(R.id.iv_show_onoff)
    ImageView mIvOnOff;

    private Context mContext;

    private OnItemClickListener mItemListen;
    private OnClickListener mOnClickListener;

    //防打扰改版
    private MonitorTimeBean focusTimeBean;

    public MonitorTimeItemView(Context context){
        this(context, null);
        mContext = context;
    }
    public MonitorTimeItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = View.inflate(context, R.layout.focustime_item_ly, null);
        ButterKnife.bind(this, view);
        addView(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListen != null)
                    mItemListen.onItemClick(v,0);
            }
        });

        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(focusTimeBean.getType() == 1){
                    return true;
                }
                if (mItemListen != null)
                    mItemListen.onItemLongClick(v,0);
                return true;
            }
        });

        mIvOnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener != null)
                    mOnClickListener.onClick(v);
            }
        });

    }

    public void onRefreshOnOffView(){
        if(focusTimeBean.onoff.equals("1")){
            focusTimeBean.onoff = "0";
            mIvOnOff.setBackgroundResource(R.drawable.switch_toggle_off);
        }else{
            focusTimeBean.onoff = "1";
            mIvOnOff.setBackgroundResource(R.drawable.switch_toggle_on);
        }
    }

    public OnClickListener getmOnClickListener() {
        return mOnClickListener;
    }

    public void setmOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener ItemListen){
        this.mItemListen = ItemListen;
    }
    public void setFocusTimeBean(MonitorTimeBean bean){
        focusTimeBean = bean;
        updateViewBySetFocustTime();
    }
    public MonitorTimeBean getFocusTimeBean(){
        return focusTimeBean;
    }

    private void updateViewBySetFocustTime(){
        mIvOnOff.setBackgroundResource(
                focusTimeBean.onoff.equals("1")?R.drawable.switch_toggle_on:R.drawable.switch_toggle_off
        );
        mTvSubTitle.setTextColor(getResources().getColor(R.color.focustime_add_time));
        mTvSubTitle1.setTextColor(getResources().getColor(R.color.focustime_add_time));
        mTvTitle.setText(getFocusTimeTimeZoneByHour());
        mTvSubTitle.setText(getWeeksInfoByDays(mContext, focusTimeBean.days));
        mTvSubTitle1.setText(focusTimeBean.getName());
    }

    public String getFocusTimeTimeZoneByHour(){
        return focusTimeBean.starthour + ":" + focusTimeBean.startmin + "  " + "-" + "  " +
                focusTimeBean.endhour + ":" + focusTimeBean.endmin;
    }

    private String getWeeksInfoByDays(Context mContext, String mDays){
        String mWeekInfo;
        if (mDays.equals("0111110")) {
            mWeekInfo = mContext.getString(R.string.focustime_week_work_day);
        } else if (mDays.equals("1111111")) {
            mWeekInfo = mContext.getString(R.string.device_alarm_reset_3);
        } else if(mDays.equals("1000001")){
            mWeekInfo = mContext.getString(R.string.focustime_week_rest_day);
        }else {
            mWeekInfo =  ((mDays.substring(0, 1).equals("1") ? mContext.getString(R.string.week_0) + "" : "") +
                    (mDays.substring(1, 2).equals("1") ? mContext.getString(R.string.week_1) + " " : "") +
                    (mDays.substring(2, 3).equals("1") ? mContext.getString(R.string.week_2) + " " : "") +
                    (mDays.substring(3, 4).equals("1") ? mContext.getString(R.string.week_3) + " " : "") +
                    (mDays.substring(4, 5).equals("1") ? mContext.getString(R.string.week_4) + " " : "") +
                    (mDays.substring(5, 6).equals("1") ? mContext.getString(R.string.week_5) + " " : "") +
                    (mDays.substring(6, 7).equals("1") ? mContext.getString(R.string.week_6) + " " : ""));
        }

        return mWeekInfo;
    }
}
