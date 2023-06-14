package com.xiaoxun.xun.health.HeartRate;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HeartRateSettingActivity extends NormalActivity implements MsgCallback {

    private static final int WARN_MIN = 100;
    private static final int WARN_MAX = 160;

    ToggleButton all_day_switch;
    ToggleButton warning_switch;
    TextView warning_value;
    LinearLayout value_ly;
    TextView warning_txt;
    CustomerPickerView pickerView;
    LinearLayout warn_ly;

    int heart_on = 1;
    int warning_on = 0;
    String warningValue = "120";
    String curWarningValue = "120";
    int mapSetMode = 0; //1 上传全天检测开关 2 上传预警开关和值 3 上传预警值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_setting);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.heart_rate_setting));
        ImageView iv = findViewById(R.id.iv_title_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        all_day_switch = findViewById(R.id.all_day_switch);
        all_day_switch.setChecked(false);
        all_day_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()){
                    if (b) {
                        mapSetHeartSettingAllDay(1);
                    } else {
                        mapSetHeartSettingAllDay(0);
                    }
                }
            }
        });

        warning_switch = findViewById(R.id.warning_switch);
        warning_switch.setChecked(false);
        warning_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()){
                    if(b){
                        mapSetHeartSettingWarning(1,warningValue);
                    }else{
                        mapSetHeartSettingWarning(0,warningValue);
                    }
                }
            }
        });
        warning_value = findViewById(R.id.warning_value);
        warning_value.setText(getString(R.string.heart_rate_warning_value_format,warningValue));
        value_ly = findViewById(R.id.value_ly);
        value_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openValueSelectDialog();
            }
        });
        warning_txt = findViewById(R.id.warning_txt);
        warn_ly = findViewById(R.id.warn_ly);
        WatchData mWatch = getMyApp().getCurUser().getFocusWatch();
//        if(getMyApp().getConfigFormDeviceType(mWatch.getDeviceType(),mWatch.getVerCur(),mWatch.getMachSn()).getSwitch_heart_rate_warn()){
            warn_ly.setVisibility(View.VISIBLE);
//        }else{
//            warn_ly.setVisibility(View.INVISIBLE);
//        }
        mapGetHeartSetting();
    }

    private void updateView(){
        if(heart_on == 1){
            warning_switch.setClickable(true);
            warning_switch.setBackgroundDrawable(getDrawable(R.drawable.switch_toggle_selector));
            warning_txt.setTextColor(getResources().getColor(R.color.txt_coupon_color));
            all_day_switch.setChecked(true);
            warning_switch.setChecked(warning_on == 1);
            if(warning_on == 1) {
                value_ly.setClickable(true);
                warning_value.setTextColor(getResources().getColor(R.color.bg_color_dark_orange));
            }else {
                value_ly.setClickable(false);
                warning_value.setTextColor(getResources().getColor(R.color.color_low_content));
            }
        }else{
            warning_switch.setClickable(false);
            value_ly.setClickable(false);
            warning_switch.setBackgroundDrawable(getDrawable(R.drawable.switch_on_grey));
            warning_txt.setTextColor(getResources().getColor(R.color.color_low_content));
            warning_value.setTextColor(getResources().getColor(R.color.color_low_content));
        }
        warning_value.setText(getString(R.string.heart_rate_warning_value_format,warningValue));
    }

    private void openValueSelectDialog(){
        final Dialog dlg = new Dialog(HeartRateSettingActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) HeartRateSettingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.heart_rate_setting_warning_value_pickview, null);
        pickerView = layout.findViewById(R.id.value_pv);
        View valuepv = layout.findViewById(R.id.value_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        final int hheight = valuepv.getBackground().getMinimumHeight();
        TextView cancel = layout.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        TextView confirm = layout.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapSetHeartSettingWarningValue(curWarningValue);
                dlg.dismiss();
            }
        });
        View mask_1 = layout.findViewById(R.id.iv_mask_1);
        mask_1.setTranslationX(width * 5 / 10 - 40);
        mask_1.setTranslationY(hheight / 11);
        List<String> warningValues = new ArrayList<>();
        for(int i = WARN_MIN;i <= WARN_MAX;i++){
            warningValues.add(getString(R.string.heart_rate_warning_value_format,String.valueOf(i)));
        }
        pickerView.setData(warningValues);
        pickerView.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                curWarningValue = text;
                curWarningValue = curWarningValue.replace(getString(R.string.heart_rate_warning_value1),"");
            }
        });
        pickerView.setSelected(curWarningValue + getString(R.string.heart_rate_warning_value1));
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        dlg.show();
    }

    private void mapGetHeartSetting(){
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(this);

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_ALL_DAY_ON);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_ONOFF);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_VALUE);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(getMyApp().obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()) {
            getMyApp().getNetService().sendNetMsg(mapget);
        }
    }

    private void mapSetHeartSettingAllDay(int allDayOnOff){
        mapSetMode = 1;
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_ALL_DAY_ON, String.valueOf(allDayOnOff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, getMyApp().getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    private void mapSetHeartSettingWarning(int warningOnOff,String warningValue){
        mapSetMode = 2;
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_ONOFF, String.valueOf(warningOnOff));
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_VALUE,warningValue);
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, getMyApp().getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    private void mapSetHeartSettingWarningValue(String warningValue){
        mapSetMode = 3;
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_VALUE,warningValue);
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, getMyApp().getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid){
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                if(rc == CloudBridgeUtil.RC_SUCCESS){
                    JSONObject pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    if(pl != null && !pl.isEmpty()){
                        String allDayOn = (String)pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_ALL_DAY_ON);
                        String warnOnOff = (String)pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_ONOFF);
                        String warnValue = (String)pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_VALUE);
                        if(!TextUtils.isEmpty(allDayOn)) {
                            if (Integer.parseInt(allDayOn) == 1) {
                                heart_on = 1;
                            } else {
                                heart_on = 0;
                            }
                        }else{
                            heart_on = 0;
                        }
                        if(!TextUtils.isEmpty(warnOnOff)){
                            warning_on = Integer.parseInt(warnOnOff);
                        }
                        if(!TextUtils.isEmpty(warnValue)){
                            warningValue = warnValue;
                            curWarningValue = warningValue;
                        }
                    }
                }else{
                    LogUtil.e("HeartRateSettingActivity mapGet failed.");
                }
                updateView();
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if(mapSetMode == 1) {
                    if (rc == CloudBridgeUtil.RC_SUCCESS) {
                        LogUtil.e("loc_3d_toggle CID_MAPSET_RESP success.");
                        if (heart_on != 1 && all_day_switch.isChecked()) {
                            heart_on = 1;
                            ToastUtil.show(HeartRateSettingActivity.this, getString(R.string.phone_set_success));
                        } else {
                            heart_on = 0;
                        }
                    } else {
                        all_day_switch.setChecked(false);
                        ToastUtil.show(this, getString(R.string.set_error));
                    }
                    updateView();
                }else if(mapSetMode == 2){
                    if (rc == CloudBridgeUtil.RC_SUCCESS){
                        warning_on = warning_switch.isChecked() ? 1 : 0;
                        ToastUtil.show(HeartRateSettingActivity.this, getString(R.string.phone_set_success));
                    }else{
                        warning_switch.setChecked(warning_on == 1);
                        ToastUtil.show(this, getString(R.string.set_error));
                    }
                    updateView();
                }else{
                    if(rc == CloudBridgeUtil.RC_SUCCESS){
                        warningValue = curWarningValue;
                        ToastUtil.show(HeartRateSettingActivity.this, getString(R.string.phone_set_success));
                    }else{
                        ToastUtil.show(this, getString(R.string.set_error));
                    }
                    warning_value.setText(getString(R.string.heart_rate_warning_value_format,warningValue));
                }
                break;
        }
    }
}