package com.xiaoxun.xun.health.record;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoxun.xun.R;

import java.util.ArrayList;
import java.util.List;

public class MonitorRecordHolder extends RecyclerView.ViewHolder{

    TextView tv_date;
    RecyclerView rv_monitor_list;
    MonitorRecordDayAdapter adapter;

    List<Record> mList = new ArrayList<>();

    public MonitorRecordHolder(@NonNull View itemView) {
        super(itemView);
        tv_date = itemView.findViewById(R.id.tv_date);
        rv_monitor_list = itemView.findViewById(R.id.rv_monitor_list);
    }

    public void initListView(Context context){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        rv_monitor_list.setLayoutManager(manager);
        adapter = new MonitorRecordDayAdapter(context,mList);
        rv_monitor_list.setAdapter(adapter);
    }
}
