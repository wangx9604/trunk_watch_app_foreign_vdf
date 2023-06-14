package com.xiaoxun.xun.health.motion.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.motion.views.MotionWeekKickView;
import com.xiaoxun.xun.motion.views.MotionWeekSportView;
import com.xiaoxun.xun.networkv2.beans.MotionReportBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionWeekRecordActivity extends NormalActivity {


    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.motion_fun_1)
    MotionWeekKickView mFunc1;
    @BindView(R.id.motion_fun_2)
    MotionWeekKickView mFunc2;
    @BindView(R.id.motion_fun_3)
    MotionWeekKickView mFunc3;
    @BindView(R.id.motion_sport_0)
    MotionWeekSportView mMotionSport0;
    @BindView(R.id.motion_sport_1)
    MotionWeekSportView mMotionSport1;

    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }

    private MotionReportBean mReportBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_week_record);

        ButterKnife.bind(this);
        mTvTitle.setText(getString(R.string.motion_week_title));

        getWeeklyRecordData();
        UpdeteRecordView();
    }

    private void UpdeteRecordView() {
        if(false){
            //1：无数据展示
        }else{
            //2:数据刷新
            mFunc1.setMotionData(MotionUtils.getReportByType(mReportBean.getSportList(),5));
            mFunc2.setMotionData(MotionUtils.getReportByType(mReportBean.getSportList(),6));
            mFunc3.setMotionData(MotionUtils.getReportByType(mReportBean.getSportList(),11));
            mMotionSport0.setMotionData(MotionUtils.getReportByName(mReportBean.getStepsList(),"time"));
        }
    }

    private void getWeeklyRecordData() {
        //1:从本地获取数据

        //2:从网络获取数据

        //test:写死的数据格式
        ArrayList<MotionReportBean.MotionSportBean> mSportList = new ArrayList<>();
        ArrayList<MotionReportBean.MotionStepBean> mStepList = new ArrayList<>();
        ArrayList<String> mDatas = new ArrayList<>();
        mDatas.add("20220328,40");
        mDatas.add("20220329,20");
        mDatas.add("20220330,50");
        mDatas.add("20220331,60");
        mDatas.add("20220401,30");
        mDatas.add("20220402,60");
        mDatas.add("20220403,10");

        mSportList.add(new MotionReportBean.MotionSportBean(5, 95,80,75,
                mDatas));
        mSportList.add(new MotionReportBean.MotionSportBean(6, 85,70,65,
                mDatas));
        mSportList.add(new MotionReportBean.MotionSportBean(11, 75,60,55,
                mDatas));
        mStepList.add(new MotionReportBean.MotionStepBean("time", 110,15,90,
                mDatas));
        mStepList.add(new MotionReportBean.MotionStepBean("calorie", 120,25,100,
                mDatas));

        mReportBean = new MotionReportBean(0,"msg success",
                mSportList,mStepList
                );

    }
}