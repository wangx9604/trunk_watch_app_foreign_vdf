package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.motion.views.LayoutStateView;
import com.xiaoxun.xun.motion.views.SelectStateView;
import com.xiaoxun.xun.networkv2.beans.MotionSettingBean;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.views.CustomSelectorDialog;

import net.minidev.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionScheduleSettingActivity extends NormalActivity {

    private ImibabyApp myApp;
    private WatchData mCurWatch;
    private MotionSettingBean motionSettingBean;
    private MotionSettingBean motionOldSettingBean;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        saveDataToSettingBean();
        if(motionOldSettingBean.isDiff(motionSettingBean)){
            new CustomSelectorDialog.Builder(this)
                    .setmDailogType(4)
                    .setTitle(getString(R.string.prompt))
                    .setContent(getString(R.string.is_save_change))
                    .setmLeftBtnTxt(getString(R.string.cancel))
                    .setmRightBtnTxt(getString(R.string.confirm))
                    .setmSmallBtnLeftListener(new InterfacesUtil.UpdateViewData() {
                        @Override
                        public void UpdateView(View view, String resq) {
                            finish();
                        }
                    })
                    .setmSmallBtnRightListener(new InterfacesUtil.UpdateViewData() {
                        @Override
                        public void UpdateView(View view, String resq) {
                            sendDataToCloudService();
                        }
                    }).build().show();

        }else{
            finish();
        }
    }

    @BindView(R.id.tv_layout_title_1)
    public TextView mTvTitle1;
    @BindView(R.id.ssv_motion_0)
    public SelectStateView mSSVMotion0;
    @BindView(R.id.ssv_motion_1)
    public SelectStateView mSSVMotion1;
    @BindView(R.id.ssv_motion_2)
    public SelectStateView mSSVMotion2;
    @BindView(R.id.ssv_make_target_0)
    public SelectStateView mSSVTarget0;
    @BindView(R.id.ssv_make_target_1)
    public SelectStateView mSSVTarget1;
    @BindView(R.id.ssv_make_target_2)
    public SelectStateView mSSVTarget2;
    @BindView(R.id.layout_func_1)
    public LayoutStateView mLayoutState1;
    @BindView(R.id.layout_func_2)
    public LayoutStateView mLayoutState2;
    @BindView(R.id.layout_func_3)
    public LayoutStateView mLayoutState3;
    @BindView(R.id.layout_func_4)
    public LayoutStateView mLayoutState4;
    @BindView(R.id.layout_func_5)
    public LayoutStateView mLayoutState5;
    @BindView(R.id.tv_set_value)
    public TextView mTvSetValue;

    @OnClick({R.id.ssv_motion_0, R.id.ssv_motion_1, R.id.ssv_motion_2, R.id.ssv_make_target_0,
    R.id.ssv_make_target_1, R.id.ssv_make_target_2, R.id.cv_func_1, R.id.cv_func_2, R.id.cv_func_3,
    R.id.cv_func_4, R.id.cv_func_5, R.id.tv_save})
    public void onActionMotionSelectState(View view){
        switch(view.getId()){
            case R.id.tv_save:
                saveDataToSettingBean();
                if(motionOldSettingBean.isDiff(motionSettingBean)) {
                    sendDataToCloudService();
                }else{
                    finish();
                }
                break;
            case R.id.ssv_motion_0:
                MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                        mSSVMotion1,mSSVMotion2,20);
                break;
            case R.id.ssv_motion_1:
                MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                        mSSVMotion1,mSSVMotion2,30);
                break;
            case R.id.ssv_motion_2:
                MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                        mSSVMotion1,mSSVMotion2,40);
                break;
            case R.id.ssv_make_target_0:
                MotionUtils.setSelectStateView(this,mSSVTarget0,
                        mSSVTarget1, mSSVTarget2, mSSVTarget0);
                break;
            case R.id.ssv_make_target_1:
                MotionUtils.setSelectStateView(this,mSSVTarget0,
                        mSSVTarget1, mSSVTarget2, mSSVTarget1);
                break;
            case R.id.ssv_make_target_2:
                MotionUtils.setSelectStateView(this,mSSVTarget0,
                        mSSVTarget1, mSSVTarget2, mSSVTarget2);
                break;
            case R.id.cv_func_1:
                if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
                MotionUtils.ShowSelectMinsDialog(this,
                        getString(R.string.motion_type_title, getString(R.string.motion_func_1)),
                        MotionUtils.getStatValue(this,
                        MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"5")
                                ),
                        new InterfacesUtil.UpdateViewData() {
                            @Override
                            public void UpdateView(View view, String resq) {
                                LogUtil.e("CalendarAddCustom:retData:"+resq);
                                if(TextUtils.isEmpty(resq)) return;
                                mLayoutState1.setTvState(resq);
                                saveDataToSettingBean();
                                updateSumValue();
                            }
                        });
                break;
            case R.id.cv_func_2:
                if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
                MotionUtils.ShowSelectMinsDialog(this,
                        getString(R.string.motion_type_title, getString(R.string.motion_func_2)),
                        MotionUtils.getStatValue(this,
                        MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"6")
                        ),
                        new InterfacesUtil.UpdateViewData() {
                            @Override
                            public void UpdateView(View view, String resq) {
                                LogUtil.e("CalendarAddCustom:retData:"+resq);
                                if(TextUtils.isEmpty(resq)) return;
                                mLayoutState2.setTvState(resq);
                                saveDataToSettingBean();
                                updateSumValue();
                            }
                        });
                break;
            case R.id.cv_func_3:
                if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
                MotionUtils.ShowSelectMinsDialog(this,
                        getString(R.string.motion_type_title, getString(R.string.motion_func_3)),
                        MotionUtils.getStatValue(this,
                        MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"11")
                        ),
                        new InterfacesUtil.UpdateViewData() {
                            @Override
                            public void UpdateView(View view, String resq) {
                                LogUtil.e("CalendarAddCustom:retData:"+resq);
                                if(TextUtils.isEmpty(resq)) return;
                                mLayoutState3.setTvState(resq);
                                saveDataToSettingBean();
                                updateSumValue();
                            }
                        });
                break;
            case R.id.cv_func_4:
                if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
                MotionUtils.ShowSelectMinsDialog(this,
                        getString(R.string.motion_type_title, getString(R.string.motion_func_4)),
                        MotionUtils.getStatValue(this,
                        MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"8")
                        ),
                        new InterfacesUtil.UpdateViewData() {
                            @Override
                            public void UpdateView(View view, String resq) {
                                LogUtil.e("CalendarAddCustom:retData:"+resq);
                                if(TextUtils.isEmpty(resq)) return;
                                mLayoutState4.setTvState(resq);
                                saveDataToSettingBean();
                                updateSumValue();
                            }
                        });
                break;
            case R.id.cv_func_5:
                if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
                MotionUtils.ShowSelectMinsDialog(this,
                        getString(R.string.motion_type_title, getString(R.string.motion_func_5)),
                        MotionUtils.getStatValue(this,
                            MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"9")
                        ),
                        new InterfacesUtil.UpdateViewData() {
                            @Override
                            public void UpdateView(View view, String resq) {
                                LogUtil.e("CalendarAddCustom:retData:"+resq);
                                if(TextUtils.isEmpty(resq)) return;
                                mLayoutState5.setTvState(resq);
                                saveDataToSettingBean();
                                updateSumValue();
                            }
                        });

                break;

        }
    }

    private void sendDataToCloudService() {
        if(getMyApp().getNetService() != null){
            getMyApp().getNetService().sendMapSetMsg(mCurWatch.getEid(), mCurWatch.getFamilyId(),
                    CloudBridgeUtil.MOTION_SCHEDULE_LIST, new Gson().toJson(motionSettingBean),
                    new MsgCallback() {
                        @Override
                        public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                            if (rc == CloudBridgeUtil.RC_SUCCESS) {
                                startActivity(new Intent(MotionScheduleSettingActivity.this, MotionMainActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }

    private void saveDataToSettingBean() {
        if(motionSettingBean == null) motionSettingBean = new MotionSettingBean();
        motionSettingBean.setTotal_strength(
                Integer.valueOf(mTvSetValue.getText().toString()));
        motionSettingBean.setSport_test(
                MotionUtils.saveToTestList(mSSVTarget0, mSSVTarget1,mSSVTarget2));
        motionSettingBean.setSport_strength(
                MotionUtils.saveToScheduleList(this,mLayoutState1,
                        mLayoutState2,mLayoutState3,mLayoutState4,mLayoutState5));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_schedule_setting);

        ButterKnife.bind(this);
        mTvTitle.setText(getString(R.string.motion_edit_title));
        myApp = getMyApp();
        mCurWatch = myApp.getCurUser().getFocusWatch();

        //1：从本地获取数据
        motionSettingBean = MotionUtils.getMotionDataByLocal(myApp, mCurWatch.getEid());
        motionOldSettingBean = new MotionSettingBean();
        motionOldSettingBean.clone(motionSettingBean);

        //2:刷新视图
        updateMotionView();

    }

    private void updateMotionView() {
        int mBmiType = getIntent().getIntExtra(Constants.BMI_SUGGEST, 1);
        if(mBmiType == 1){
            MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                    mSSVMotion1,mSSVMotion2,30);
        }else{
            MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                    mSSVMotion1,mSSVMotion2,20);
        }
        if(motionSettingBean == null) return ;
        //1:更新状态信息
        updateSumValue();
        MotionUtils.setShowMotionStrengerView(mTvSetValue,mSSVMotion0,
                mSSVMotion1,mSSVMotion2,motionSettingBean.getTotal_strength());

        mLayoutState1.setTvState(MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"5"));
        mLayoutState2.setTvState(MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"6"));
        mLayoutState3.setTvState(MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"11"));
        mLayoutState4.setTvState(MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"8"));
        mLayoutState5.setTvState(MotionUtils.getScheduleStateByType(this,motionSettingBean.getSport_strength(),"9"));

        mSSVTarget0.setSelect(MotionUtils.getScheduleSelectByType(motionSettingBean.getSport_test(),"5"));
        mSSVTarget1.setSelect(MotionUtils.getScheduleSelectByType(motionSettingBean.getSport_test(),"6"));
        mSSVTarget2.setSelect(MotionUtils.getScheduleSelectByType(motionSettingBean.getSport_test(),"11"));

    }

    private void updateSumValue() {
        mTvTitle1.setText(MotionUtils.getSchdeleSum(this, motionSettingBean.getSport_strength()));
    }

}