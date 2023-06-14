package com.xiaoxun.xun.health.outside;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.outside.fragments.day.OutsideDayFragment;
import com.xiaoxun.xun.health.outside.fragments.week.OutsideWeekFragment;
import com.xiaoxun.xun.utils.StatusBarUtil;

public class OutSideMainActivity extends AppCompatActivity {

    public final static String OUTSIDE_URL = "https://sportpk.xunkids.com/sunshine/getStatistics";
    public final static String OUTSIDE_URL_TEST = "https://sporttest.xunkids.com/sunshine/getStatistics";

    private ImibabyApp mApp;
    private WatchData curWatch;

    ConstraintLayout ly_fragments;
    OutsideDayFragment dayFragment;
    OutsideWeekFragment weekFragment;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_side_main);
        StatusBarUtil.changeStatusBarColor(this,getResources().getColor(R.color.schedule_class_bg));

        mApp = (ImibabyApp)getApplication();
        String eid = getIntent().getStringExtra("eid");
        if (eid != null) {
            //从H5跳转
            curWatch = mApp.getCurUser().queryWatchDataByEid(eid);
        } else {
            //从natie跳转
            curWatch = mApp.getCurUser().getFocusWatch();
        }
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

        ly_fragments = findViewById(R.id.ly_fragments);
        dayFragment = OutsideDayFragment.newInstance("day",curWatch.getEid());
        weekFragment = OutsideWeekFragment.newInstance("week",curWatch.getEid());
        TextView tv_tab_day = findViewById(R.id.tv_tab_day);
        TextView tv_tab_week = findViewById(R.id.tv_tab_week);
        tv_tab_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof OutsideDayFragment){
                    return;
                }
                tv_tab_day.setBackground(getDrawable(R.drawable.shape_white_corner_5dp));
                tv_tab_week.setBackgroundColor(getResources().getColor(R.color.transparent));
                switchFragment(dayFragment);
            }
        });

        tv_tab_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof  OutsideWeekFragment){
                    return;
                }
                tv_tab_day.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv_tab_week.setBackground(getDrawable(R.drawable.shape_white_corner_5dp));
                switchFragment(weekFragment);
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
}