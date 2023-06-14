package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.util.HashMap;

public class StepsSettingActivity extends NormalActivity implements View.OnClickListener,
                                                        MsgCallback, LoadingDialog.OnConfirmClickListener {

    private RelativeLayout layer_people_setting;
    private RelativeLayout layer_target_setting;
    private RelativeLayout layer_offon_setting;
    private ImageView      iv_steps_notication_offon;
    private ImageView      iv_steps_offon;
    private ImageView      switch_steps_offon;
    private TextView       tv_head_title;
    private ImageView      tv_head_back;
    private TextView       tv_steps_value;

    private String watchEid;
    private boolean StepsOffOn = false;//102和105的计步开关，302和305默认开启
    private boolean StepsNoticationOffOn = false; //计步开关的信息
    private int targetSteps = 0;
    private LoadingDialog loadingdlg;

    private HashMap<String, String> settingSnInfo = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_setting);
        initView();
        //延时对话框
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
        watchEid = myApp.getCurUser().getFocusWatch().getEid();
        //本地数据和网络同步数据
        getStepsSettingFromLocal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTargetStepsForLocal();
    }

    /**
    * 类名称：StepsSettingActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/4 11:05
    * 方法描述：初始化控件和设置控件点击和处理事件函数
    */
    private void initView(){
        tv_steps_value = findViewById(R.id.steps_target);
        tv_head_title = findViewById(R.id.tv_title);
        tv_head_title.setText(getString(R.string.steps_control_setting));
        tv_head_back = findViewById(R.id.iv_title_back);
        tv_head_back.setOnClickListener(this);
        layer_people_setting = findViewById(R.id.layer_steps_info);
        layer_people_setting.setOnClickListener(this);
        layer_target_setting = findViewById(R.id.layer_target_steps);
        layer_target_setting.setOnClickListener(this);
        switch_steps_offon   = findViewById(R.id.switch_steps_offon);
        switch_steps_offon.setOnClickListener(this);

        layer_offon_setting = findViewById(R.id.layer_steps_offon);
        iv_steps_offon = findViewById(R.id.line_steps_offon);
        if(myApp.getCurUser().getFocusWatch().isDevice102() || myApp.getCurUser().getFocusWatch().isDevice105()){
            layer_offon_setting.setVisibility(View.VISIBLE);
            iv_steps_offon.setVisibility(View.VISIBLE);
        }else{
            layer_offon_setting.setVisibility(View.GONE);
            iv_steps_offon.setVisibility(View.GONE);
        }

        iv_steps_notication_offon = findViewById(R.id.switch_steps_notication_offon);
        iv_steps_notication_offon.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.e(requestCode + ":" + resultCode + ":" + data);
        if(resultCode == 1) {
            super.onActivityResult(requestCode, resultCode, data);
            targetSteps = data.getIntExtra("targetsteps", 8000);
            if (loadingdlg != null && !loadingdlg.isShowing()) {
                loadingdlg.enableCancel(false);
                loadingdlg.changeStatus(1, getResources().getString(R.string.synch_szone_message));
                loadingdlg.show();
            }
            mapSetSettingData(targetSteps, CloudBridgeUtil.STEPS_TARGET_LEVEL);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layer_steps_info:
                Intent _intent = new Intent(this, DeviceDetailActivity.class);
                _intent.putExtra("isSteps", "1");
                startActivity(_intent);
                break;
            case R.id.layer_target_steps:
                startActivityForResult(new Intent(this, StepsTargetActivity.class), 1);
                break;
            case R.id.switch_steps_notication_offon:
                if (loadingdlg != null && !loadingdlg.isShowing()) {
                    loadingdlg.enableCancel(false);
                    loadingdlg.changeStatus(1, getResources().getString(R.string.synch_szone_message));
                    loadingdlg.show();
                }
                if(!StepsNoticationOffOn) {
                    mapSetSettingData(1, CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
                }else{
                    ToastUtil.show(this,getResources().getString(R.string.steps_notice_onoff_title));
                    mapSetSettingData(0, CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
                }
                break;
            case R.id.switch_steps_offon:
                if (loadingdlg != null && !loadingdlg.isShowing()) {
                    loadingdlg.enableCancel(false);
                    loadingdlg.changeStatus(1, getResources().getString(R.string.synch_szone_message));
                    loadingdlg.show();
                }
                if(!StepsOffOn) {
                    mapSetSettingData(1, CloudBridgeUtil.STEPS_ONOFF_SETTING);
                }else{
                    mapSetSettingData(0, CloudBridgeUtil.STEPS_ONOFF_SETTING);
                }

                break;
            case R.id.iv_title_back:
                finish();
                break;
        }
    }
    /**
    * 类名称：StepsSettingActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/1 10:02
    * 方法描述：从本地获取到计步器的设置信息和计步资料
    */
    private void getStepsSettingFromLocal(){
        String steps_level ;
        String steps_notication_on;
        if(myApp.getCurUser().getFocusWatch().isDevice102()){
            steps_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "0");
        }else {
            steps_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "1");
        }
        if(steps_level == null || steps_level.equals("0")){
            StepsOffOn = false;
            switch_steps_offon.setImageResource(R.drawable.switch_off);
        }else if(steps_level.equals("1")){
            StepsOffOn = true;
            switch_steps_offon.setImageResource(R.drawable.switch_on);
        }
        steps_notication_on = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "1");
        if(steps_notication_on == null || steps_notication_on.equals("0")){
            StepsNoticationOffOn = false;
            iv_steps_notication_offon.setImageResource(R.drawable.switch_off);
        }else if(steps_level.equals("1")){
            StepsNoticationOffOn = true;
            iv_steps_notication_offon.setImageResource(R.drawable.switch_on);
        }

        //目标步数
        getTargetStepsForLocal();
    }
    /**
    * 类名称：StepsSettingActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/7 19:20
    * 方法描述：从本地配置信息中获取到设置的目标步数
    */
    private void getTargetStepsForLocal(){
        String steps_target_level = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid()
                + CloudBridgeUtil.STEPS_TARGET_LEVEL, "0");
        if (steps_target_level == null || steps_target_level.equals("0")) {
            tv_steps_value.setText("");
        } else {
            tv_steps_value.setText(getString(R.string.unit_steps_with_number, steps_target_level));
        }
        tv_steps_value.invalidate();
    }

    /**
    * 类名称：StepsSettingActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/1 10:05
    * 方法描述：发送mapset信息，同步更新计步设置信息
    */
    private void mapSetSettingData(int value, String key){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(key, Integer.toString(value));
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchEid);
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, myApp.getCurUser().getFocusWatch().getFamilyId());
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        StringBuffer sms = new StringBuffer();
        sms.append("<" + sn + "," + getMyApp().getCurUser().getEid() + "," + "E501"
                + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent
                (CloudBridgeUtil.CID_MAPSET, sn, myApp.getToken(), pl));
        settingSnInfo.put(String.valueOf(sn),key);

        if(myApp.getNetService() != null){
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                int sn = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_SN);
                String key = String.valueOf(sn);
                String info = settingSnInfo.get(key);
                if(loadingdlg != null && loadingdlg.isShowing()){
                    loadingdlg.dismiss();
                }
                if (rc > 0) {
                    switch (info) {
                        case CloudBridgeUtil.STEPS_ONOFF_SETTING:

                            if (!StepsOffOn) {
                                StepsOffOn = true;
                                switch_steps_offon.setImageResource(R.drawable.switch_on);
                                saveStepsSettingDateToLocal(CloudBridgeUtil.STEPS_ONOFF_SETTING, "1");
                            } else {
                                StepsOffOn = false;
                                switch_steps_offon.setImageResource(R.drawable.switch_off);
                                saveStepsSettingDateToLocal(CloudBridgeUtil.STEPS_ONOFF_SETTING, "0");
                            }

                            break;
                        case CloudBridgeUtil.STEPS_TARGET_LEVEL:
                            if (targetSteps == 0) {
                                targetSteps = 8000;
                            }
                            saveStepsSettingDateToLocal(CloudBridgeUtil.STEPS_TARGET_LEVEL, String.valueOf(targetSteps));
                            tv_steps_value.setText(getString(R.string.unit_steps_with_number,String.valueOf(targetSteps)));
                            tv_steps_value.invalidate();
                            break;
                        case CloudBridgeUtil.STEPS_NOTIFICATION_SETTING:
                            if (!StepsNoticationOffOn) {
                                StepsNoticationOffOn = true;
                                iv_steps_notication_offon.setImageResource(R.drawable.switch_on);
                                saveStepsSettingDateToLocal(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "1");
                            } else {
                                StepsNoticationOffOn = false;
                                iv_steps_notication_offon.setImageResource(R.drawable.switch_off);
                                saveStepsSettingDateToLocal(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "0");
                            }

                            break;
                    }
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this, getString(R.string.phone_set_timeout), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error), Toast.LENGTH_SHORT);
                }
                settingSnInfo.remove(key);
                break;
        }
    }
    /**
    * 类名称：StepsSettingActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/4 14:15
    * 方法描述：本地保存计步开关
    */
    private void saveStepsSettingDateToLocal(String key, String value) {
        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + key, value);
    }

    @Override
    public void confirmClick() {

    }
}
