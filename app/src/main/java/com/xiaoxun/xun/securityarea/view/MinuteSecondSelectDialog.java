package com.xiaoxun.xun.securityarea.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xiaoxun.xun.R;

import java.util.ArrayList;
import java.util.List;

public class MinuteSecondSelectDialog extends Dialog {
    private Context context;
    private int type;//0 上午到校时间 1 下午放学时间 2 最晚到家时间  3上学路程  4放学路程
    private String hour;
    private String minute;
    ConstraintLayout clHourMinute;
    ConstraintLayout clMinute;
    CustomerPickerView1 pvHour;
    CustomerPickerView1 pvMinute;
    CustomerPickerView1 pv_minute;
    TextView title;
    TextView sure;
    private View.OnClickListener sureClick;

    private List<String> hourList1 = new ArrayList<>();
    private List<String> hourList2 = new ArrayList<>();
    private List<String> hourList3 = new ArrayList<>();
    private List<String> minList1 = new ArrayList<>();
    private List<String> minList2 = new ArrayList<>();

    public MinuteSecondSelectDialog(Context context, int type, String hour, String minute, View.OnClickListener clickListener) {
        super(context, R.style.Theme_DataSheet);
        this.context = context;
        this.hour = hour;
        this.minute = minute;
        this.type = type;
        sureClick = clickListener;
        initView();
    }

    public MinuteSecondSelectDialog(Context context, int type, String minute, View.OnClickListener clickListener) {
        super(context, R.style.Theme_DataSheet);
        this.context = context;
        this.minute = minute;
        this.type = type;
        sureClick = clickListener;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_minute_second_select, null);
        hourInit();
        clHourMinute = layout.findViewById(R.id.cl_hour_minute);
        clMinute = layout.findViewById(R.id.cl_minute);
        pvHour = (CustomerPickerView1) layout.findViewById(R.id.start_hour_pv);
        pvHour.setMarginAlphaValue((float) 3.8, "H");
        pvMinute = (CustomerPickerView1) layout.findViewById(R.id.start_min_pv);
        pvMinute.setMarginAlphaValue((float) 3.8, "M");
        pv_minute = (CustomerPickerView1) layout.findViewById(R.id.pv_minute);
        pv_minute.setMarginAlphaValue((float) 3.8, "M");
        title = layout.findViewById(R.id.title);
        sure = layout.findViewById(R.id.sure);
        if (type == 0) {
            title.setText(context.getString(R.string.guard_school_arrrive_time_title));
            pvHour.setData(hourList1);
            pvMinute.setData(minList1);
            pvHour.setSelected(getPositionInList(hour, hourList1));
            pvMinute.setSelected(getPositionInList(minute, minList1));
            clHourMinute.setVisibility(View.VISIBLE);
            clMinute.setVisibility(View.GONE);
        } else if (type == 1) {
            title.setText(context.getString(R.string.guard_school_leave_time_title));
            pvHour.setData(hourList2);
            pvMinute.setData(minList1);
            pvHour.setSelected(getPositionInList(hour, hourList2));
            pvMinute.setSelected(getPositionInList(minute, minList1));
            clHourMinute.setVisibility(View.VISIBLE);
            clMinute.setVisibility(View.GONE);
        } else if (type == 2) {
            title.setText(context.getString(R.string.lastest_time_arrive_home));
            pvHour.setData(hourList3);
            pvMinute.setData(minList1);
            pvHour.setSelected(getPositionInList(hour, hourList3));
            pvMinute.setSelected(getPositionInList(minute, minList1));
            clHourMinute.setVisibility(View.VISIBLE);
            clMinute.setVisibility(View.GONE);
        } else if (type == 3) {
            title.setText("上学路程");
            pv_minute.setData(minList2);
            pv_minute.setSelected(getPositionInList(minute, minList2));
            clHourMinute.setVisibility(View.GONE);
            clMinute.setVisibility(View.VISIBLE);
        } else if (type == 4) {
            title.setText("放学路程");
            pv_minute.setData(minList2);
            pv_minute.setSelected(getPositionInList(minute, minList2));
            clHourMinute.setVisibility(View.GONE);
            clMinute.setVisibility(View.VISIBLE);
        }


        pvHour.setOnSelectListener(new CustomerPickerView1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = text;
            }
        });
        pvMinute.setOnSelectListener(new CustomerPickerView1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = text;
            }
        });
        pv_minute.setOnSelectListener(new CustomerPickerView1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = text;
            }
        });
        sure.setOnClickListener(sureClick);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        setContentView(layout);
    }

    private void hourInit() {
        for (int i = 6; i < 12; i++) {
            hourList1.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 12; i < 21; i++) {
            hourList2.add("" + i);
        }
        for (int i = 15; i < 22; i++) {
            hourList3.add("" + i);
        }
        for (int i = 0; i < 60; i++) {
            minList1.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 5; i <= 60; i += 5) {
            minList2.add("" + i);
        }
    }


    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    private int getPositionInList(String s, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (s.equals(list.get(i)))
                return i;
        }
        return 0;
    }
}
