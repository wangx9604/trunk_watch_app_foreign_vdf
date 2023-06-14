package com.xiaoxun.xun.health.report;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.health.bean.FatigueData;
import com.xiaoxun.xun.health.bean.FatigueSightBean;
import com.xiaoxun.xun.health.customview.HealthFatigueView;
import com.xiaoxun.xun.health.report.fragments.day.DayFragment;
import com.xiaoxun.xun.health.report.fragments.month.MonthFragment;
import com.xiaoxun.xun.health.report.fragments.week.WeekFragment;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends NormalAppCompatActivity {

    public static final String RECORDS_URL = "https://dcenter.xunkids.com/hcxl/healthyData";

    ConstraintLayout ly_fragments;
    DayFragment dayFragment;
    WeekFragment weekFragment;
    MonthFragment monthFragment;
    Fragment currentFragment;

    private int targetSteps = 0;
    private String sign = "step";

    private String eid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        targetSteps = getIntent().getIntExtra("target_step",0);
        sign = getIntent().getStringExtra("sign");
        eid = getIntent().getStringExtra("eid");
        initViews();
        //test();
    }

    private void initViews(){
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ly_fragments = findViewById(R.id.ly_fragments);
        dayFragment = DayFragment.newInstance(sign,String.valueOf(targetSteps),eid);
        weekFragment = WeekFragment.newInstance("week",eid);
        monthFragment = MonthFragment.newInstance("month",eid);
        TextView tv_tab_day = findViewById(R.id.tv_tab_day);
        TextView tv_tab_week = findViewById(R.id.tv_tab_week);
        TextView tv_tab_month = findViewById(R.id.tv_tab_month);
        tv_tab_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof DayFragment){
                    return;
                }
                tv_tab_day.setBackground(getDrawable(R.drawable.shape_white_corner_5dp));
                tv_tab_week.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_tab_month.setBackgroundColor(getResources().getColor(R.color.transparent));
                switchFragment(dayFragment);
            }
        });

        tv_tab_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof  WeekFragment){
                    return;
                }
                tv_tab_day.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_tab_week.setBackground(getDrawable(R.drawable.shape_white_corner_5dp));
                tv_tab_month.setBackgroundColor(getResources().getColor(R.color.transparent));
                switchFragment(weekFragment);
            }
        });

        tv_tab_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof MonthFragment){
                    return;
                }
                tv_tab_day.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_tab_week.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_tab_month.setBackground(getDrawable(R.drawable.shape_white_corner_5dp));
                switchFragment(monthFragment);
            }
        });
        setDefaultFragment();
    }

    private void setDefaultFragment(){
        currentFragment = dayFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ly_fragments,dayFragment);
        transaction.commit();
    }

    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.ly_fragments, targetFragment)
                    .commit();
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commit();
        }
        currentFragment = targetFragment;
    }

    private void test(){
        HealthFatigueView fv_test = findViewById(R.id.fv_test);
        List<FatigueSightBean> list = new ArrayList<>();
        for(int i= 0;i<7;i++) {
            FatigueSightBean sightBean = new FatigueSightBean("20220401");
            FatigueData n = new FatigueData();
            n.setDuration(500 + i * 20);
            FatigueData t = new FatigueData();
            n.setDuration(300 + i * 20);
            FatigueData a = new FatigueData();
            a.setDuration(100 + i * 20);
            FatigueData s = new FatigueData();
            s.setDuration(50 + i * 20);
            sightBean.setNormal(n);
            sightBean.setTiny(t);
            sightBean.setAbit(a);
            sightBean.setObvious(s);
            list.add(sightBean);
        }
        fv_test.setSightDatas(list);
    }
}
