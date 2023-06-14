package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.motion.views.LayoutStateView;
import com.xiaoxun.xun.motion.views.SelectStateView;
import com.xiaoxun.xun.networkv2.beans.MotionSettingBean;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionMainActivity extends NormalActivity {

    private final static String TAG = MotionMainActivity.class.getSimpleName();
    private ImibabyApp myApp;
    private WatchData mCurWatch;
    private MotionSettingBean motionSettingBean;

    @BindView(R.id.tv_new_sum_vaule)
    public TextView mTvSumValue;
    @BindView(R.id.tv_new_add_vaule)
    public TextView mTvAddValue;
    @BindView(R.id.tv_title)
    public TextView mTvTitle;
    @BindView(R.id.tv_layout_title_0)
    public TextView mTopTitle0;
    @BindView(R.id.tv_layout_title_1)
    public TextView mTopTitle1;
    @BindView(R.id.layout_func_1)
    public LayoutStateView mStateView1;
    @BindView(R.id.layout_func_2)
    public LayoutStateView mStateView2;
    @BindView(R.id.layout_func_3)
    public LayoutStateView mStateView3;
    @BindView(R.id.layout_func_4)
    public LayoutStateView mStateView4;
    @BindView(R.id.layout_func_5)
    public LayoutStateView mStateView5;
    @BindView(R.id.ssv_make_target_0)
    public SelectStateView mSSView0;
    @BindView(R.id.ssv_make_target_1)
    public SelectStateView mSSView1;
    @BindView(R.id.ssv_make_target_2)
    public SelectStateView mSSView2;


    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }
    @OnClick({R.id.tv_edit,R.id.cv_func_2})
    public void onActionToMakeEdit(View view){
        switch (view.getId()){
            case R.id.tv_edit:
                startActivity(new Intent(this, MotionScheduleSettingActivity.class));
                break;
            case R.id.cv_func_2:
                startActivity(new Intent(this, MotionScoreActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_main);

        LogUtil.e(TAG+"onCreate");
        ButterKnife.bind(this);
        mTvTitle.setText(getString(R.string.motion_title));
        myApp = getMyApp();
        String eid = getIntent().getStringExtra("eid");
        if (eid != null) {
            //从H5跳转
            mCurWatch = myApp.getCurUser().queryWatchDataByEid(eid);
        } else {
            //从natie跳转
            mCurWatch = myApp.getCurUser().getFocusWatch();
        }

        //1：从本地获取数据
        motionSettingBean = MotionUtils.getMotionDataByLocal(myApp, mCurWatch.getEid());

        //2:刷新视图
        updateMotionView();

        //3:获取运动计划数据
        MGetMotionScheduleInfo();
        MGetMotionScoreInfo();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e(TAG+"onNewIntent");
        //1:获取运动计划数据
        MGetMotionScheduleInfo();
    }


    private void updateMotionView() {
        if(motionSettingBean == null) return ;
        //1:更新状态信息
        mTopTitle1.setText(MotionUtils.getSchdeleSum(this, motionSettingBean.getSport_strength()));
        mTopTitle0.setText(getString(R.string.motion_main_title_1, motionSettingBean.getTotal_strength()));
        MotionUtils.showStateView(getString(R.string.motion_no_setting),
                MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"5"),
                mStateView1);
        MotionUtils.showStateView(getString(R.string.motion_no_setting),
                MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"6"),
                mStateView2);
        MotionUtils.showStateView(getString(R.string.motion_no_setting),
                MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"11"),
                mStateView3);
        MotionUtils.showStateView(getString(R.string.motion_no_setting),
                MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"8"),
                mStateView4);
        MotionUtils.showStateView(getString(R.string.motion_no_setting),
                MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"9"),
                mStateView5);

        MotionUtils.setMainExeMotionStateView(this,motionSettingBean.getSport_test(), mSSView0, mSSView1, mSSView2);
        //2:更新积分信息

    }

    private void MGetMotionScoreInfo() {
        MotionUtils.getMotonScoreInfo(getMyApp(),
                getMyApp().getCurUser().getFocusWatch().getEid(),
                new MsgCallback() {
                    @Override
                    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        if (rc == CloudBridgeUtil.RC_SUCCESS) {
                            JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                            String mScoreTotal = (String) maggetPl.get(CloudBridgeUtil.MOTION_TOTAL_SCORE);
                            if(!TextUtils.isEmpty(mScoreTotal)) {
                                //1:刷新视图
                                mTvSumValue.setText(mScoreTotal);
                            }
                            String mScoreDaily = (String) maggetPl.get(CloudBridgeUtil.MOTION_DAILY_SCORE);
                            if(!TextUtils.isEmpty(mScoreDaily)) {
                                //1:刷新视图
                                mTvAddValue.setText(mScoreDaily);
                            }
                        }
                    }
                });
    }

    private void MGetMotionScheduleInfo() {
        MotionUtils.getMotonScheduleInfo(getMyApp(),
                getMyApp().getCurUser().getFocusWatch().getEid(),
                new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String mScheuleInfo = (String) maggetPl.get(CloudBridgeUtil.MOTION_SCHEDULE_LIST);
                    if(!TextUtils.isEmpty(mScheuleInfo)) {
                        //1:本地数据持久化
                        myApp.setValue(CloudBridgeUtil.MOTION_SCHEDULE_LIST + mCurWatch.getEid(), mScheuleInfo);
                        //2:刷新视图
                        motionSettingBean = new Gson().fromJson(mScheuleInfo, MotionSettingBean.class);
                        updateMotionView();
                    }else{
                        startActivity(new Intent(MotionMainActivity.this, MotionGuideActivity.class));
                        finish();
                    }
                }
            }
        });
    }
}