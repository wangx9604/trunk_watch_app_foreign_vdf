package com.xiaoxun.xun.focustime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FocusTimeWeekSettingActivity extends NormalAppCompatActivity {
    List<WeekdaysBean> list;
    String days = "0000000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_time_week_setting);
        StatusBarUtil.setStatusBarColor(this,R.color.schedule_no_class);
        days = getIntent().getStringExtra("days");
        initViews();
    }

    private void initViews(){
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RecyclerView rv_list = findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(manager);
        list = new ArrayList<>();
        WeekdaysBean sundaybean = new WeekdaysBean(getString(R.string.week_value_7),days.substring(0,1).equals("1"));
        list.add(sundaybean);
        for(int i = 1;i<7;i++){
            String s = "week_value_" + i;
            int resId = getResources().getIdentifier(s,"string", BuildConfig.APPLICATION_ID);
            String c = days.substring(i,i+1);
            WeekdaysBean bean = new WeekdaysBean(getString(resId),c.equals("1"));
            list.add(bean);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,list);
        rv_list.setAdapter(adapter);

        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                StringBuilder wd = new StringBuilder();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).checked){
                        wd.append(1);
                    }else{
                        wd.append(0);
                    }
                }
                if(wd.toString().equals("0000000")){
                    ToastUtil.show(getApplicationContext(),getString(R.string.guard_school_set_err_5));
                    return;
                }
                it.putExtra("days",wd.toString());
                setResult(2,it);
                finish();
            }
        });
    }

    static class WeekdaysBean{
        boolean checked;
        String title;
        public WeekdaysBean(String t,boolean c){
            title = t;
            checked = c;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        ImageView iv_check;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }

    class SimpleAdapter extends RecyclerView.Adapter<ItemViewHolder>{
        Context mContext;
        List<WeekdaysBean> weekdays;
        public SimpleAdapter(Context context,List<WeekdaysBean> w){
            mContext = context;
            weekdays = w;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.focustime_week_repeat_item_ly,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            final WeekdaysBean s = weekdays.get(i);
            itemViewHolder.tv_title.setText(s.title);
            if(s.checked){
                itemViewHolder.iv_check.setVisibility(View.VISIBLE);
            }else{
                itemViewHolder.iv_check.setVisibility(View.GONE);
            }
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s.checked = !s.checked;
                    notifyItemChanged(itemViewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return (weekdays == null ? 0 : weekdays.size());
        }
    }
}