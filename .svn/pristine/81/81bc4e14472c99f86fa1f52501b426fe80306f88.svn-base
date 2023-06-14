package com.xiaoxun.xun.ScheduleCard.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.views.CustomSettingView;

import net.minidev.json.JSONObject;

public class ScheduleSettingsActivity extends NormalActivity implements View.OnClickListener , MsgCallback {

    private ImageView mBackImageView;
    private CustomSettingView mScheduleTimeSettingView;
    private CustomSettingView mScheduleMeSettingView;
    private ImageButton mBtnNotifyOnoff;

    private String mScheduleInfo;
    private Boolean isNotifyOnOff = true;  //默认开关开启
    private WatchData mCurWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_settings);
        initActManage();
        initViews();
        getScheduleNotiOnOff();
    }

    private void getScheduleNotiOnOff() {
        if(getMyApp().getNetService() != null){
            getMyApp().getNetService().sendMapGetMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_WATCH_SCHEDULE_NOTIFY,this);
        }
    }

    private void SetScheduleNotiOnOff(String switchOnOff) {
        if(getMyApp().getNetService() != null){
            getMyApp().getNetService().sendMapSetMsg(mCurWatch.getEid(),mCurWatch.getFamilyId(),
                    CloudBridgeUtil.KEY_WATCH_SCHEDULE_NOTIFY, switchOnOff,this);
        }
    }

    private void initViews() {
        mBackImageView = findViewById(R.id.iv_back);
        mScheduleTimeSettingView = findViewById(R.id.layout_schedule_time);
        mScheduleMeSettingView = findViewById(R.id.layout_schedule_class);
        mBtnNotifyOnoff = findViewById(R.id.btn_notify_onoff);

        mBackImageView.setOnClickListener(this);
        mScheduleMeSettingView.setOnClickListener(this);
        mScheduleTimeSettingView.setOnClickListener(this);
        mBtnNotifyOnoff.setOnClickListener(this);
    }

    private void initActManage() {
        mScheduleInfo = getIntent().getStringExtra(Constants.SCHEDULE_CARD_INFO);
        String mWatchData = getIntent().getStringExtra(Constants.WATCH_EID_DATA);
        mCurWatch = getMyApp().getCurUser().queryWatchDataByEid(mWatchData);
    }

    private void onBackAction(){
        Intent intent = getIntent();
        intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
        setResult(1, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null || resultCode != 1) return;
        mScheduleInfo = data.getStringExtra(Constants.SCHEDULE_CARD_INFO);
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackAction();
                break;
            case R.id.layout_schedule_class:
            {
                Intent intent = new Intent(ScheduleSettingsActivity.this,
                        ScheduleClassSetActivity.class);
                intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                startActivityForResult(intent,1);
            }
                break;
            case R.id.layout_schedule_time:
            {
                Intent intent = new Intent(ScheduleSettingsActivity.this,
                        ScheduleTimeSetActivity.class);
                intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                intent.putExtra(Constants.SCHEDULE_SETTING_FIRST, false);
                startActivityForResult(intent,1);
            }
                break;
            case R.id.btn_notify_onoff:
                //mapset设置关闭开关
                if(isNotifyOnOff){
                    isNotifyOnOff = false;
                    SetScheduleNotiOnOff("0");
                }else{
                    isNotifyOnOff = true;
                    SetScheduleNotiOnOff("1");
                }
                updateWatchScheduleNotifyOnOff();

                break;
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid){
            case CloudBridgeUtil.CID_MAPGET_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String mKeyNotify = (String) pl.get(CloudBridgeUtil.KEY_WATCH_SCHEDULE_NOTIFY);
                    if (mKeyNotify != null && !mKeyNotify.equals("") && !mKeyNotify.equals("1")) {
                        isNotifyOnOff = false;
                    }else{
                        isNotifyOnOff = true;
                    }
                }
                //更新手表课表提示开关视图
                updateWatchScheduleNotifyOnOff();
                break;
        }
    }

    private void updateWatchScheduleNotifyOnOff() {
        if(isNotifyOnOff){
            mBtnNotifyOnoff.setImageResource(R.drawable.switch_on);
        } else {
            mBtnNotifyOnoff.setImageResource(R.drawable.switch_off);
        }
    }
}
