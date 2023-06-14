package com.xiaoxun.xun.securityarea.adapter;

import android.content.Context;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.securityarea.bean.SchoolGuardTimeBean;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class LeaveSchoolTimeAdapter extends BaseQuickAdapter<SchoolGuardTimeBean, BaseViewHolder> {
    Context context;
    TextView tv_leave_day_set;

    public LeaveSchoolTimeAdapter(Context context, List<SchoolGuardTimeBean> data) {
        super(R.layout.adapter_school_guard_time, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SchoolGuardTimeBean item) {
        TextView tv_leave_time = helper.getView(R.id.tv_leave_time);
        TextView tv_leave_guard_time = helper.getView(R.id.tv_leave_guard_time);
        TextView tv_leave_time_title = helper.getView(R.id.tv_leave_time_title);
        tv_leave_day_set = helper.getView(R.id.tv_leave_day_set);
        tv_leave_time.setText(item.getTime());

        switch (item.getDays()) {
            case "1111111":
                tv_leave_day_set.setText(context.getText(R.string.device_alarm_reset_3));
                break;
            case "1111100":
                tv_leave_day_set.setText("工作日");
                break;
            case "00000011":
                tv_leave_day_set.setText("休息日");
                break;
            default:
                setDay(item.getDays());
                break;
        }

        String[] goSchool = item.getFl().split(",");
        //上学区间前3/5 后2/5
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String[] ai = item.getTime().split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ai[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(ai[1]));
        cal.add(Calendar.MINUTE, -Integer.parseInt(goSchool[0]));
        String arr1_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String arr1_m = decimalFormat.format(cal.get(Calendar.MINUTE));

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ai[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(ai[1]));
        cal.add(Calendar.MINUTE, Integer.parseInt(goSchool[1]));
        String arr2_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String arr2_m = decimalFormat.format(cal.get(Calendar.MINUTE));

        tv_leave_guard_time.setText(context.getString(R.string.guard_school_guard_time, arr1_h, arr1_m, arr2_h, arr2_m));
    }

    //判断连续天数,设置显示。。  连续三天及以上用~省略
    private void setDay(String day) {
        int startDay = -1;
        int endDay = -1;
        String[] mDayArray = new String[7];
        String[] mDayText = new String[7];
        for (int i = 0; i < 7; i++) {
            mDayArray[i] = String.valueOf(day.charAt(i));
        }
        mDayText[0] = mDayArray[0].equals("1") ? " " + context.getString(R.string.week_1) : "";
        mDayText[1] = mDayArray[1].equals("1") ? " " + context.getString(R.string.week_2) : "";
        mDayText[2] = mDayArray[2].equals("1") ? " " + context.getString(R.string.week_3) : "";
        mDayText[3] = mDayArray[3].equals("1") ? " " + context.getString(R.string.week_4) : "";
        mDayText[4] = mDayArray[4].equals("1") ? " " + context.getString(R.string.week_5) : "";
        mDayText[5] = mDayArray[5].equals("1") ? " " + context.getString(R.string.week_6) : "";
        mDayText[6] = mDayArray[6].equals("1") ? " " + context.getString(R.string.week_0) : "";
        if (day.equals("1110111")) {
            tv_leave_day_set.setText(context.getString(R.string.week_1) + "~" + context.getString(R.string.week_3) + " " + context.getString(R.string.week_5) + "~" + context.getString(R.string.week_0));
        } else {
            for (int i = 0; i < 5; i++) {
                startDay = -1;
                endDay = -1;
                if (mDayArray[i].equals("1")) {
                    startDay = i;
                    endDay = i;
                    for (int j = i + 1; j < 7; j++) {
                        if (mDayArray[j].equals("1")) {
                            endDay = j;
                        } else {
                            break;
                        }
                    }
                }
                if (endDay - startDay >= 2) {
                    for (int k = startDay + 1; k < endDay; k++) {
                        mDayText[k] = "";
                    }
                    if (endDay == 2) {
                        mDayText[endDay] = "~" + context.getString(R.string.week_3);
                    } else if (endDay == 3) {
                        mDayText[endDay] = "~" + context.getString(R.string.week_4);
                    } else if (endDay == 4) {
                        mDayText[endDay] = "~" + context.getString(R.string.week_5);
                    } else if (endDay == 5) {
                        mDayText[endDay] = "~" + context.getString(R.string.week_6);
                    } else {
                        mDayText[endDay] = "~" + context.getString(R.string.week_0);
                    }
                    break;
                }
            }
            tv_leave_day_set.setText(mDayText[0] + mDayText[1] + mDayText[2] + mDayText[3] + mDayText[4] + mDayText[5] + mDayText[6]);
        }

    }
}
