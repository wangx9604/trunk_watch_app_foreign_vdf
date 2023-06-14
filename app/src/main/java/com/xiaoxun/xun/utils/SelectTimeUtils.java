package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.views.CustomerPickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyouyang on 2016/12/8.
 */

public class SelectTimeUtils {

    /**
     * 选择时间的PickerView
     * @param context Activity  需要上下文、窗体操作
     * @param paramHour  时
     * @param paramMinute  分
     * @param type  0,notype  1,starttime  2,endtime
     * @param listenerHour  选择时的回调
     * @param listenerMin  选择分的回调
     */
    public static void openSelectTimeView(Activity context, String paramHour, String paramMinute, int type,
                                          CustomerPickerView.onSelectListener listenerHour, CustomerPickerView.onSelectListener listenerMin) {

        CustomerPickerView pvHour;
        CustomerPickerView pvMinute;
        TextView tvHour;
        TextView tvMinute;

        View view = View.inflate(context, R.layout.select_time_view, null);
        pvHour = view.findViewById(R.id.start_hour_pv);
        pvHour.setMarginAlphaValue((float) 3.8, "H");
        pvMinute = view.findViewById(R.id.start_min_pv);
        pvMinute.setMarginAlphaValue((float) 3.8, "M");

        TextView tvSettingType= view.findViewById(R.id.tv_settime_type);
        if (type == 0) {
            tvSettingType.setVisibility(View.GONE);
        }
        if (type == 1) {
            tvSettingType.setText(context.getString(R.string.start_time));
        } else if (type == 2) {
            tvSettingType.setText(context.getString(R.string.end_time));
        }

        List<String> hours = new ArrayList<>();
        final List<String> mins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            mins.add(i < 10 ? "0" + i : "" + i);
        }

        pvHour.setData(hours);
        pvHour.setOnSelectListener(listenerHour);
        pvHour.setSelected(Integer.valueOf(paramHour));

        pvMinute.setData(mins);
        pvMinute.setOnSelectListener(listenerMin);
        pvMinute.setSelected(Integer.valueOf(paramMinute));

        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度
        tvHour = view.findViewById(R.id.start_hour_pv_hour);
        //因为计算尺寸是按照1080宽度，所以这里计算padding值需要按比例求出
        tvHour.setPadding((width - 17 * width / 1080) / 4 + 48 * width / 1080, 0, 0, 0);
        tvHour.setTextColor(0xffdf5600);
        tvMinute = view.findViewById(R.id.start_min_pv_min);
        tvMinute.setPadding(width - (width - 17 * width / 1080) / 4 + 28 * width / 1080, 0, 0, 0);
        tvMinute.setTextColor(0xffdf5600);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        context.addContentView(view, params);
    }

    public static boolean CheckClassTimeToPass(String mStartTime, String mEndTime) {
        boolean isPass ;
        if(mStartTime.length() < 4 || mEndTime.length() < 4 ) return false;
        int mStartHour = Integer.parseInt(mStartTime.substring(0,2));
        int mStartMin = Integer.parseInt(mStartTime.substring(2));
        int mEndHour = Integer.parseInt(mEndTime.substring(0,2));
        int mSEndMin = Integer.parseInt(mEndTime.substring(2));
        if(mStartHour > mEndHour){
            isPass = false;
        }else isPass = mStartHour != mEndHour || mStartMin < mSEndMin;

        return isPass;
    }

    public static void onTimeSelectForSchedule(Context context, final RelativeLayout layout,
                                               String mStartHour, String mStartMinute,
                                               String mEndHour, String mEndMinute,
                                               CustomerPickerView.onSelectListener sHourListener,
                                               CustomerPickerView.onSelectListener sMinListener,
                                               CustomerPickerView.onSelectListener eHourListener,
                                               CustomerPickerView.onSelectListener eMinListener,
                                               final View.OnClickListener mCloseListener){
        CustomerPickerView mStartHourView;
        CustomerPickerView mStartMinView;
        CustomerPickerView mEndHourView;
        CustomerPickerView mEndMinView;

        final View view = View.inflate(context, R.layout.select_time_schedule, null);
        mStartHourView =  view.findViewById(R.id.start_hour_pv);
        mStartHourView.setMarginAlphaValue((float) 3.8, "H");
        mStartMinView = view.findViewById(R.id.start_min_pv);
        mStartMinView.setMarginAlphaValue((float) 3.8, "M");
        mEndHourView =  view.findViewById(R.id.end_hour_pv);
        mEndHourView.setMarginAlphaValue((float) 3.8, "H");
        mEndMinView = view.findViewById(R.id.end_min_pv);
        mEndMinView.setMarginAlphaValue((float) 3.8, "M");

        ImageView close_btn = view.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                layout.removeView(view);
                if(mCloseListener != null){
                    mCloseListener.onClick(v);
                }
            }
        });

        List<String> StartHours = new ArrayList<>();
        List<String> StartMins = new ArrayList<>();
        List<String> EndHours = new ArrayList<>();
        List<String> EndMins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hours = i < 10 ? "0" + i : "" + i;
            StartHours.add(hours);
            EndHours.add(hours);
        }
        for (int i = 0; i < 60; i++) {
            String min = i < 10 ? "0" + i : "" + i;
            StartMins.add(min);
            EndMins.add(min);
        }

        mStartHourView.setData(StartHours);
        mStartHourView.setOnSelectListener(sHourListener);
        mStartHourView.setSelected(Integer.valueOf(mStartHour));

        mStartMinView.setData(StartMins);
        mStartMinView.setOnSelectListener(sMinListener);
        mStartMinView.setSelected(Integer.valueOf(mStartMinute));

        mEndHourView.setData(EndHours);
        mEndHourView.setOnSelectListener(eHourListener);
        mEndHourView.setSelected(Integer.valueOf(mEndHour));

        mEndMinView.setData(EndMins);
        mEndMinView.setOnSelectListener(eMinListener);
        mEndMinView.setSelected(Integer.valueOf(mEndMinute));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layout.removeAllViews();
        layout.addView(view, params);
    }

    /**
     *
     * @param context
     * @param paramHour
     * @param paramMinute
     * @param type 0 未设置 1 到校时间 2 离校时间
     * @param listenerHour
     * @param listenerMin
     */
    public static View selectGuardTimeView(final Activity context, String paramHour, String paramMinute, int type,
                                           CustomerPickerView.onSelectListener listenerHour, CustomerPickerView.onSelectListener listenerMin) {

        CustomerPickerView pvHour;
        CustomerPickerView pvMinute;
        TextView tvHour;
        TextView tvMinute;
        ImageView hide_view;
        View touch_outside;

        final View view = View.inflate(context, R.layout.select_time_view, null);
        pvHour = (CustomerPickerView) view.findViewById(R.id.start_hour_pv);
        pvHour.setMarginAlphaValue((float) 3.8, "H");
        pvMinute = (CustomerPickerView) view.findViewById(R.id.start_min_pv);
        pvMinute.setMarginAlphaValue((float) 3.8, "M");
        hide_view = (ImageView) view.findViewById(R.id.hide_view);
        hide_view.setVisibility(View.VISIBLE);
        touch_outside = view.findViewById(R.id.touch_outside);
        touch_outside.setVisibility(View.VISIBLE);

        TextView tvSettingType=((TextView) view.findViewById(R.id.tv_settime_type));
        if (type == 0) {
            tvSettingType.setVisibility(View.GONE);
        }
        if (type == 1) {
            tvSettingType.setText(context.getString(R.string.guard_school_arrrive_time_title));
        } else if (type == 2) {
            tvSettingType.setText(context.getString(R.string.guard_school_leave_time_title));
        }

        List<String> hours = new ArrayList<>();
        final List<String> mins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i+=5) {
            mins.add(i < 10 ? "0" + i : "" + i);
        }

        pvHour.setData(hours);
        pvHour.setOnSelectListener(listenerHour);
        pvHour.setSelected(Integer.valueOf(paramHour));

        pvMinute.setData(mins);
        pvMinute.setOnSelectListener(listenerMin);
        pvMinute.setSelected(Integer.valueOf(paramMinute));

        hide_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View hideview) {
                TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                mHiddenAction.setDuration(500);
                view.setVisibility(View.INVISIBLE);
                view.setAnimation(mHiddenAction);
                ((ViewGroup) view.getParent()).removeView(view);
            }
        });

        touch_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View touchview) {
                TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                mHiddenAction.setDuration(500);
                view.setVisibility(View.INVISIBLE);
                view.setAnimation(mHiddenAction);
                ((ViewGroup) view.getParent()).removeView(view);
            }
        });

        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度
        tvHour = (TextView) view.findViewById(R.id.start_hour_pv_hour);
        //因为计算尺寸是按照1080宽度，所以这里计算padding值需要按比例求出
        tvHour.setPadding((width - 17 * width / 1080) / 4 + 48 * width / 1080, 0, 0, 0);
        tvHour.setTextColor(0xffdf5600);
        tvMinute = (TextView) view.findViewById(R.id.start_min_pv_min);
        tvMinute.setPadding(width - (width - 17 * width / 1080) / 4 + 28 * width / 1080, 0, 0, 0);
        tvMinute.setTextColor(0xffdf5600);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        context.addContentView(view, params);
        return view;
    }

    public static void openSelectTimeViewTwo(Activity context, String paramHour, String paramMinute, int type,CustomerPickerView.onSelectListener listenerHour, CustomerPickerView.onSelectListener listenerMin) {
        CustomerPickerView pvHour;
        CustomerPickerView pvMinute;
        TextView tvHour;
        TextView tvMinute;

        View view = View.inflate(context, R.layout.select_time_viewtwo, null);
        pvHour = view.findViewById(R.id.start_hour_pv);
        pvHour.setMarginAlphaValue((float) 3.8, "H");
        pvMinute = view.findViewById(R.id.start_min_pv);
        pvMinute.setMarginAlphaValue((float) 3.8, "M");

        TextView tvSettingType= view.findViewById(R.id.tv_settime_type);
        if (type == 0) {
            tvSettingType.setVisibility(View.GONE);
        }
        if (type == 1) {
            tvSettingType.setText(context.getString(R.string.start_time));
        } else if (type == 2) {
            tvSettingType.setText(context.getString(R.string.end_time));
        }

        List<String> hours = new ArrayList<>();
        final List<String> mins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            mins.add(i < 10 ? "0" + i : "" + i);
        }

        pvHour.setData(hours);
        pvHour.setOnSelectListener(listenerHour);
        pvHour.setSelected(Integer.valueOf(paramHour));

        pvMinute.setData(mins);
        pvMinute.setOnSelectListener(listenerMin);
        pvMinute.setSelected(Integer.valueOf(paramMinute));

        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度
        tvHour = view.findViewById(R.id.start_hour_pv_hour);
        //因为计算尺寸是按照1080宽度，所以这里计算padding值需要按比例求出
        tvHour.setPadding((width - 17 * width / 1080) / 4 + 48 * width / 1080, 0, 0, 0);
        tvHour.setTextColor(0xffdf5600);
        tvMinute = view.findViewById(R.id.start_min_pv_min);
        tvMinute.setPadding(width - (width - 17 * width / 1080) / 4 + 28 * width / 1080, 0, 0, 0);
        tvMinute.setTextColor(0xffdf5600);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        context.addContentView(view, params);
    }
}
