package com.xiaoxun.xun.health.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.xiaoxun.xun.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarViewDialog extends Dialog {

    public interface OnDateSelectListener{
        void onSelect(int year,int month,int day);
    }

    private CalendarView calendarView;

    private OnDateSelectListener listener;

    public CalendarViewDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.calendarview_dialog_ly);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_date =findViewById(R.id.tv_date);
        calendarView = findViewById(R.id.calendar);
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if(isClick) {
                    listener.onSelect(calendar.getYear(), calendar.getMonth(), calendar.getDay());
                    dismiss();
                }
            }
        });
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                String date = context.getString(R.string.health_report_calendar_date
                        ,String.valueOf(year),String.valueOf(month));
                tv_date.setText(date);
            }
        });
        calendarView.setAllMode();
        String now = context.getString(R.string.health_report_calendar_date
                ,String.valueOf(calendarView.getCurYear()),String.valueOf(calendarView.getCurMonth()));
        tv_date.setText(now);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    public void setOnDateSelectListener(OnDateSelectListener lis){
        listener = lis;
    }
    public void setDate(String dateStr){//yyyyMMdd
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(dateStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(d);
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            calendarView.scrollToCalendar(year, month, day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
