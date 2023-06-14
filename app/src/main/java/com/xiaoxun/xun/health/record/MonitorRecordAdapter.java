package com.xiaoxun.xun.health.record;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class MonitorRecordAdapter extends RecyclerView.Adapter<MonitorRecordHolder> {
    private Context context;
    private List<MonitorRecord> datas;

    public MonitorRecordAdapter(Context ctxt,List<MonitorRecord> list){
        context = ctxt;
        datas = list;
    }

    @NonNull
    @Override
    public MonitorRecordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LogUtil.e("MonitorRecordAdapter onCreateViewHolder i = " + i);
        View view = LayoutInflater.from(context).inflate(R.layout.ly_monitor_record_item,viewGroup,false);
        return new MonitorRecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonitorRecordHolder monitorRecordHolder, int i) {
        MonitorRecord item = datas.get(i);
        monitorRecordHolder.tv_date.setText(formatDate(item.getDayTime()));

        monitorRecordHolder.mList.clear();
        monitorRecordHolder.mList.addAll(datas.get(i).getRecord());
        if(monitorRecordHolder.adapter == null) {
            LogUtil.e("MonitorRecordAdapter onBindViewHolder adpter null");
            monitorRecordHolder.initListView(context);
        }else{
            LogUtil.e("MonitorRecordAdapter onBindViewHolder adpter not null");
            monitorRecordHolder.adapter.setPosition(i);
            monitorRecordHolder.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public String formatDate(String d){
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.format_time), Locale.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date sd = format1.parse(d);
            String date = format.format(sd);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
