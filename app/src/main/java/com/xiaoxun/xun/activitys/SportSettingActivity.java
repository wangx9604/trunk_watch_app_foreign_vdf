package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.alipayLoginUtil.NetworkRequestUtils;
import com.xiaoxun.xun.views.CustomSettingView;

import net.minidev.json.JSONObject;

public class SportSettingActivity extends NormalActivity implements View.OnClickListener, MsgCallback {

    private CustomSettingView view_height;
    private CustomSettingView view_weight;
    private CustomSettingView view_target_step;
    private TextView tv_status_null_info;

    private String target;
    private WatchData mCurWatch;
    private Double tempWatchWeight;
    private Double tempWatchHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_setting);

        initManager();
        initView();
        initListener();
        updateViewData();
    }

    private void initManager() {
        String mWatchData = getIntent().getStringExtra(Constants.WATCH_EID_DATA);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchData);
        tempWatchWeight = mCurWatch.getWeight();
        tempWatchHeight = mCurWatch.getHeight();
        target = getIntent().getStringExtra(CloudBridgeUtil.STEPS_TARGET_LEVEL);
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.sport_setting_hing3);
        view_height = findViewById(R.id.view_height);
        view_weight = findViewById(R.id.view_weight);
        view_target_step = findViewById(R.id.view_target_step);
        tv_status_null_info = findViewById(R.id.tv_status_null);
    }

    private void updateViewData() {
        view_target_step.setState(target +" "+getString(R.string.steps_unit_steps));
        if(tempWatchWeight < 10){
            view_weight.setState(getString(R.string.sport_set_statue_null));
        }else {
            view_weight.setState(String.format("%d%s", tempWatchWeight.intValue() , " "+getString(R.string.str_kg)));
        }
        if(tempWatchHeight < 60){
            view_height.setState(getString(R.string.sport_set_statue_null));
        }else {
            view_height.setState(String.format("%d%s", tempWatchHeight.intValue() , " "+getString(R.string.str_cm)));
        }
        if(tempWatchHeight < 60 || tempWatchWeight <10){
            tv_status_null_info.setVisibility(View.VISIBLE);
        }else{
            tv_status_null_info.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        view_height.setOnClickListener(this);
        view_weight.setOnClickListener(this);
        view_target_step.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_height:
                String curHeight;
                if (mCurWatch.getHeight() < 60) {
                    curHeight = "110.0";
                }else{
                    curHeight = String.valueOf(mCurWatch.getHeight());
                }
                DialogUtil.openHeightEditDialog(SportSettingActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Double tempHeight=Double.valueOf(DialogUtil.height);
                        tempWatchHeight = tempHeight;
                        view_height.setState(String.format("%d%s", tempHeight.intValue(), getText(R.string.str_cm)));
                        NetworkRequestUtils.sendDeviceSet(myApp, SportSettingActivity.this,
                                mCurWatch.getEid(),
                                CloudBridgeUtil.KEY_NAME_HEIGHT, tempHeight);
                    }
                }, curHeight);
                break;
            case R.id.view_weight:
                String curWeight;
                if(mCurWatch.getWeight() < 8) {
                    curWeight = "18.0";
                }else{
                    curWeight = String.valueOf(mCurWatch.getWeight());
                }
                DialogUtil.openWeightSelDialog(SportSettingActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Double tempWeight=Double.valueOf(DialogUtil.weight);
                        tempWatchWeight = tempWeight;
                        view_weight.setState(String.format("%d%s", tempWeight.intValue(), getText(R.string.str_kg)));
                        NetworkRequestUtils.sendDeviceSet(myApp, SportSettingActivity.this,
                                mCurWatch.getEid(),
                                CloudBridgeUtil.KEY_NAME_WEIGHT, tempWeight);
                    }
                }, curWeight);
                break;
            case R.id.view_target_step:
                Intent _intent = new Intent(this, StepsTargetActivity.class);
                _intent.putExtra(CloudBridgeUtil.STEPS_TARGET_LEVEL,target);
                startActivityForResult(_intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.e(requestCode + ":" + resultCode + ":" + data);
        if(resultCode == 1) {
            super.onActivityResult(requestCode, resultCode, data);
            int targetSteps = data.getIntExtra("targetsteps", 8000);
            target = String.valueOf(targetSteps);
            if(myApp.getNetService() != null)
                myApp.getNetService().sendMapSetMsg(mCurWatch.getEid(),mCurWatch.getFamilyId(),
                        CloudBridgeUtil.STEPS_TARGET_LEVEL,target,this);
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if(mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    updateViewData();
                    myApp.setValue(mCurWatch.getEid() + CloudBridgeUtil.STEPS_TARGET_LEVEL, target);
                }
                break;
            case CloudBridgeUtil.CID_DEVICE_SET_RESP:
                if(mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    if(tempWatchWeight >= 8)
                        mCurWatch.setWeight(tempWatchWeight);
                    if(tempWatchHeight >= 60)
                        mCurWatch.setHeight(tempWatchHeight);

                    WatchDAO.getInstance(getApplicationContext()).addWatch(mCurWatch);
                }
                break;

        }
    }
}
