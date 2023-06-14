package com.xiaoxun.xun.health.record;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonitorRecordDayAdapter extends RecyclerView.Adapter<MonitorRecordDayHolder> {
    private Context context;
    private List<Record> datas;

    private int mPosition;

    public MonitorRecordDayAdapter(Context ctxt,List<Record> list){
        context = ctxt;
        datas = list;
    }

    @NonNull
    @Override
    public MonitorRecordDayHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ly_monitor_record_day_item,viewGroup,false);
        return new MonitorRecordDayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonitorRecordDayHolder monitorRecordDayHolder, int i) {
        Record record = datas.get(i);
        int value = record.getValue();
        if(record.getType() == 0){
            monitorRecordDayHolder.iv_icon.setImageResource(R.drawable.health_icon_heart);
           setItemHeartRateName(value,monitorRecordDayHolder.tv_type);
           setItemHeartRateStatus(value,monitorRecordDayHolder.tv_status);
        }else if(record.getType() == 1){
            monitorRecordDayHolder.iv_icon.setImageResource(R.drawable.health_icon_xueyang);
            setItemOxyName(value,monitorRecordDayHolder.tv_type);
            setItemOxyStatus(value,monitorRecordDayHolder.tv_status);
        }
        monitorRecordDayHolder.tv_time.setText(formatUnixTime(record.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    private void setItemHeartRateName(int value, TextView view){
       view.setText(context.getString(R.string.heart_rate_warning_value_format,String.valueOf(value)));
    }
    private void setItemHeartRateStatus(int value, TextView view){
        if(value < 60){
            view.setVisibility(View.GONE);
        }else if(value < 100){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_daily));
            view.setText(context.getString(R.string.health_monitor_record_daily));
        }else if(value < 120){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_warmbody));
            view.setText(context.getString(R.string.health_monitor_record_warmbody));
        }else if(value < 140){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_burning));
            view.setText(context.getString(R.string.health_monitor_record_burning));
        }else if(value < 160){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_aerobic));
            view.setText(context.getString(R.string.health_monitor_record_aerobic));
        }else if(value < 180){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_anaerobic));
            view.setText(context.getString(R.string.health_monitor_record_anaerobic));
        }else if(value < 200){
            view.setTextColor(context.getResources().getColor(R.color.health_record_heartrate_limit));
            view.setText(context.getString(R.string.health_monitor_record_limit));
        }else {
            view.setVisibility(View.GONE);
        }
    }
    private void setItemOxyName(int value, TextView view){
        view.setText(value + "%");
    }
    private void setItemOxyStatus(int value, TextView view){
        if(value >= 90){
            view.setTextColor(context.getResources().getColor(R.color.health_report_legend_green));
            view.setText(context.getString(R.string.health_report_oxy_normal));
        }else if(value >= 80){
            view.setTextColor(context.getResources().getColor(R.color.health_report_legend_light_yellow));
            view.setText(context.getString(R.string.health_report_oxy_light));
        }else if(value >= 70){
            view.setTextColor(context.getResources().getColor(R.color.health_report_legend_light_orange));
            view.setText(context.getString(R.string.health_report_oxy_middle));
        }else{
            view.setTextColor(context.getResources().getColor(R.color.health_report_legend_red));
            view.setText(context.getString(R.string.health_report_oxy_severe));
        }
    }

    private String formatUnixTime(String time){
        Date d = TimeUtil.unixTimeToDate(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(d);
    }
}
