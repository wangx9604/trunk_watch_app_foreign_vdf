package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.TracePointInf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryTraceListActivity extends NormalActivity {

    private LinearLayout iv_title_back_layout;
    private TextView tv_title;
    private ImageButton ib_sort;
    private ListView trace_list;
    public ArrayList<TracePointInf> list;
    private TraceAdapter adapter;
    private String curPosTime;

    Comparator<TracePointInf> comparator_inc;
    Comparator<TracePointInf> comparator_dec;
    private int sort = 0;//0 zheng xu;1 ni xu;
    private int isDaysHistory = 0;//0 one day;1 three days;2 five days.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_trace_list);
        comparator_inc = new Comparator<TracePointInf>() {
            public int compare(TracePointInf s1, TracePointInf s2) {
                if (s1.mTimeStamp != s2.mTimeStamp) {
                    return s1.mTimeStamp.compareTo(s2.mTimeStamp);
                }
                return -100;
            }
        };
        comparator_dec = new Comparator<TracePointInf>() {
            public int compare(TracePointInf s1, TracePointInf s2) {
                if (s1.mTimeStamp != s2.mTimeStamp) {
                    return s2.mTimeStamp.compareTo(s1.mTimeStamp);
                }
                return -100;
            }
        };
        list = getIntent().getParcelableArrayListExtra("list");
        curPosTime = getIntent().getStringExtra("ptime");
        isDaysHistory = getIntent().getIntExtra("days",0);
        String title_time = getIntent().getStringExtra("title_time");
        adapter = new TraceAdapter(this,list);
        trace_list = findViewById(R.id.trace_list);
        trace_list.setAdapter(adapter);
        trace_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(HistoryTraceListActivity.this,GoogleMapHistoryTraceActivity.class);
                it.putExtra("cur_point",list.get(i).mTimeStamp);
                setResult(0,it);
                finish();
            }
        });
        Collections.sort(list,comparator_inc);
        for(int i =0;i<list.size();i++){
            if(list.get(i).mTimeStamp.equals(curPosTime)){
                trace_list.setSelection(i);
                break;
            }
        }
        iv_title_back_layout = findViewById(R.id.iv_title_back_layout);
        iv_title_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HistoryTraceListActivity.this,GoogleMapHistoryTraceActivity.class);
                it.putExtra("cur_point",curPosTime);
                setResult(0,it);
                finish();
            }
        });

        tv_title = findViewById(R.id.tv_title);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dateFormat.parse(title_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat format = new SimpleDateFormat(getString(R.string.format_time_month_date),
                Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(isDaysHistory == 1){
            cal.add(Calendar.DAY_OF_MONTH, -2);
            tv_title.setText(getString(R.string.start_time_to_end_time, format.format(cal.getTime()), format.format(date)));
        }else if(isDaysHistory == 2){
            cal.add(Calendar.DAY_OF_MONTH, -4);
            tv_title.setText(getString(R.string.start_time_to_end_time, format.format(cal.getTime()), format.format(date)));
        }else{
            tv_title.setText(format.format(date));
        }
        ib_sort = findViewById(R.id.sortbtn);
        ib_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortList();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();//new Intent(HistoryTraceListActivity.this,HistoryTraceActivity.class);
        it.putExtra("cur_point",curPosTime);
        setResult(0,it);
        super.onBackPressed();
    }

    class TraceAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<TracePointInf> data;
        private List<viewHolder> holdlist = new ArrayList<viewHolder>();
        private LayoutInflater mInflate;

        public TraceAdapter(Context context, ArrayList<TracePointInf> list){
            this.context = context;
            this.data = list;
            mInflate = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            viewHolder holder = null;
            if(view == null){
                holder = new viewHolder();
                view = mInflate.inflate(R.layout.history_trace_item,null);
                holder.time = view.findViewById(R.id.item_time);
                holder.txt = view.findViewById(R.id.item_txt);
                holder.time_yr = view.findViewById(R.id.item_time_yr);
                view.setTag(holder);
                holdlist.add(holder);
            }else{
                holder = (viewHolder)view.getTag();
            }
            if(curPosTime.equals(data.get(i).mTimeStamp)){
                holder.txt.setTextColor(context.getResources().getColor(R.color.device_list_focus_name));
            }else{
                holder.txt.setTextColor(Color.BLACK);
            }
            holder.txt.setText(data.get(i).mAddressDesc);
            SimpleDateFormat df = new SimpleDateFormat(getString(R.string.format_time_month_date));
            Date cur = TimeUtil.getDataFromTimeStamp(data.get(i).mTimeStamp);
            holder.time_yr.setText(df.format(cur));
            holder.time.setText(data.get(i).mTimeStamp.substring(8,10) + ":" + data.get(i).mTimeStamp.substring(10,12));
            return view;
        }

        class viewHolder{
            TextView time;
            TextView time_yr;
            TextView txt;
        }
    }

    private void sortList(){
        if(sort == 1) {
            Collections.sort(list, comparator_inc);
            sort = 0;
        }else{
            Collections.sort(list, comparator_dec);
            sort = 1;
        }
    }
}
