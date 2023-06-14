/**
 * Creation Date:2015-3-12
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.calendar.LoadingDialog.OnConfirmClickListener;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.UserRelationDAO;
import com.xiaoxun.xun.focustime.FocusTimeMainActivity;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomSettingView;
import com.xiaoxun.xun.views.CustomerPickerView;
import com.xiaoxun.xun.views.SharedPreferencesFinalKeyUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Description Of The Class<br>
 *
 * @author fushiqing
 * @version 1.000, 2015-7-30
 */
public class WatchManagerActivity extends NormalActivity implements OnClickListener, MsgCallback,
        OnConfirmClickListener {

    private TextView mTvTitle;
    private View btnBack;
    private Button mBtnWatchUnbind;
    private RelativeLayout mLayoutWhiteList;
    private RelativeLayout mLayoutReportFault;
    private RelativeLayout mLayoutSossms;
    private RelativeLayout mLayoutFcm;
    private ImageButton mBtnReportFault;
    private ImageButton mBtnWhiteList;
    private ImageButton mBtnSosSmg;
    private ImageButton mBtnAppNotify;
    private ImageButton mBtnFcm;
    private RelativeLayout mLayoutCloudPhotos;
    private ImageButton mBtnCloudPhotos;
    private TextView mTvBatteryValue;
    private TextView mTvBatteryTag;
    private TextView mBatteryTagTime;
    private View layerWaiting;
    private LoadingDialog loadingdlg;

    private ScrollView mRootScrollView;
    private CustomSettingView mLayoutFunctionControl;
    private CustomSettingView mLayoutFlowStatitics;
    private CustomSettingView mLayoutFindwatch;
    private CustomSettingView mLayoutDeviceWifi;
    private CustomSettingView mLayoutDeviceShutdown;
    private CustomSettingView mLayoutDeviceLoss;
    private CustomSettingView mLayoutRingVibrute;
    private CustomSettingView mLayoutALarmclock;
    private CustomSettingView mLayoutSilencetime;
    private CustomSettingView mLayoutOperationmode;
    private CustomSettingView mLayoutPowerSave;
    private CustomSettingView mLayoutSleepmode;
    private ConstraintLayout mLayoutAutoanswer;
    private CustomSettingView mLayoutLowPowerDisable;

    private ConstraintLayout mLayoutSmsFilter;
    private TextView mTvSmsFilterState, device_autoanswer_state;

    private String gid;
    private WatchData focusWatch;
    private NetService mNetService;
    private BroadcastReceiver mMsgReceiver = null;
    private int formalSmsFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_manager_setting);

        String eid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        if (eid != null)
            focusWatch = myApp.getCurUser().queryWatchDataByEid(eid);
        else
            focusWatch = myApp.getCurUser().getFocusWatch();
        if (focusWatch == null) {
            finish();
            return;
        }
        gid = focusWatch.getFamilyId();
        mNetService = myApp.getNetService();
        mapMGetWatchState();
        mapMGetWatchSettingState();
        //e2eGetWatchWifiState();
        initView();
        initListener();
        scrollToBottom();
        initBroadcastReceiver();

//        LogUtil.i("HYY isNotificationEnabled=" + SystemUtils.isNotificationEnabled
//        (mHostActivity));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewShow();
        updateWatchSettingStateShow();  //防打扰、休眠、模式、通话白名单、故障上报
        //updateWatchWifiConnectState();  //wifi连接
        //休眠时段
        if (myApp.isOpenSleepTime(focusWatch.getEid())) {
            mLayoutSleepmode.setState(getString(R.string.open_state));
        } else {
            mLayoutSleepmode.setState(getString(R.string.close));
        }
    }

    private void initView() {

        mTvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.iv_title_back);
        mBtnWatchUnbind = findViewById(R.id.bt_watch_unbind);
        mTvBatteryValue = findViewById(R.id.battery_value);
        mTvBatteryTag = findViewById(R.id.battery_tag);
        mBatteryTagTime = findViewById(R.id.battery_tag_time);

        mRootScrollView = findViewById(R.id.root_scroll_view);
        mLayoutFunctionControl = findViewById(R.id.layout_function_control);
        mLayoutFlowStatitics = findViewById(R.id.layout_flow_statistics);
        mLayoutFindwatch = findViewById(R.id.layout_find_watch);
        mLayoutDeviceWifi = findViewById(R.id.layout_device_wifi);
        mLayoutDeviceShutdown = findViewById(R.id.layout_device_shutdown);
        mLayoutDeviceLoss = findViewById(R.id.layout_device_remote_loss);
        mLayoutRingVibrute = findViewById(R.id.layout_ring_vibrate);
        mLayoutALarmclock = findViewById(R.id.layout_device_alarmclock);
        mLayoutSilencetime = findViewById(R.id.layout_device_silence);
        mLayoutOperationmode = findViewById(R.id.layout_device_operationmode);
        mLayoutOperationmode.setVisibility(View.GONE);
        mLayoutPowerSave = findViewById(R.id.layout_device_powersave);
        mLayoutSleepmode = findViewById(R.id.layout_device_sleepmode);
        mLayoutAutoanswer = findViewById(R.id.layout_device_autoanswer);
        device_autoanswer_state = findViewById(R.id.device_autoanswer_state);
        mLayoutWhiteList = findViewById(R.id.setting_watch_white_list);
        mBtnWhiteList = findViewById(R.id.btn_watch_white_list);

        mLayoutReportFault = findViewById(R.id.setting_report_fault_onoff);
        mBtnReportFault = findViewById(R.id.btn_report_fault_onoff);
        mLayoutSmsFilter = findViewById(R.id.layout_device_smsfilter);
        mTvSmsFilterState = findViewById(R.id.smsfilter_state);
        mLayoutSossms = findViewById(R.id.setting_sos_sms);
        mBtnSosSmg = findViewById(R.id.btn_sos_sms);
        mBtnAppNotify = findViewById(R.id.btn_app_notify);
        mLayoutCloudPhotos = findViewById(R.id.setting_cloud_photos_onoff);
        mBtnCloudPhotos = findViewById(R.id.btn_cloud_photos);
        mLayoutSossms = findViewById(R.id.setting_sos_sms);
        mLayoutFcm = findViewById(R.id.setting_watch_fcm);
        mBtnFcm = findViewById(R.id.btn_watch_fcm);
        layerWaiting = findViewById(R.id.layer_waiting);
        mLayoutLowPowerDisable = findViewById(R.id.layout_low_power_disable);
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
        loadingdlg.hideReloadView();
    }

    private void updateViewShow() {
        String settingManger;
        if (focusWatch.isWatch()) {
            settingManger = getString(R.string.setting_watch_manager2);
            mLayoutFindwatch.setTitle(getString(R.string.find_watch));
        } else {
            settingManger = getString(R.string.setting_device_manager2);
            mLayoutFindwatch.setTitle(getString(R.string.find_device));
        }
        mTvTitle.setText(settingManger);
        setBatteryValueAndTime();

        if (focusWatch.isSupportDeviceWifi()) {
            mLayoutDeviceWifi.setVisibility(View.VISIBLE);
        } else {
            mLayoutDeviceWifi.setVisibility(View.GONE);
        }


//        updateShutdownState();
        if ((myApp.isControledByVersion(focusWatch, false, "T26") && focusWatch.isDevice102()) || focusWatch.isDevice105()) {
            mLayoutRingVibrute.setTitle(getString(R.string.volume_vibrate_led));
        } else if (focusWatch.isDevice501() || focusWatch.isDevice502() || focusWatch.isDevice303_A02()
                || focusWatch.isDevice701() || focusWatch.isDevice710() || focusWatch.isDevice703()
                || focusWatch.isDevice705() || focusWatch.isDevice305() || focusWatch.isDevice307()
                || focusWatch.isDevice706_A02() || focusWatch.isDevice900_A03() || focusWatch.isDevice206_A02()
                || focusWatch.isDevice203_A03() || focusWatch.isDevice306_A03() || focusWatch.isDevice708_A06()
                || focusWatch.isDevice709_A03() || focusWatch.isDevice709_A05() || focusWatch.isDevice708_A07() || focusWatch.isDevice707_H01() || focusWatch.isDevice709_H01()) {
            mLayoutRingVibrute.setVisibility(View.GONE);
        } else {
            mLayoutRingVibrute.setTitle(getString(R.string.volume_vibrate));
        }

        if (focusWatch.isSupportAlarmClock()) {
            mLayoutALarmclock.setVisibility(View.VISIBLE);
        } else {
            mLayoutALarmclock.setVisibility(View.GONE);
        }

        if (focusWatch.isSupportSlienceTime()) {
            mLayoutSilencetime.setVisibility(View.VISIBLE);
        } else {
            mLayoutSilencetime.setVisibility(View.GONE);
        }

        if (focusWatch.isSupportPhoneCall()) {
            mLayoutAutoanswer.setVisibility(View.VISIBLE);
            mLayoutWhiteList.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_watch_white_list_selector).setVisibility(View.VISIBLE);
        } else {
            mLayoutAutoanswer.setVisibility(View.GONE);
            mLayoutWhiteList.setVisibility(View.GONE);
            findViewById(R.id.iv_watch_white_list_selector).setVisibility(View.GONE);
        }

        if (focusWatch.isSupportSmsFilter()) {
            mLayoutSmsFilter.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_watch_smsfilter_divider).setVisibility(View.VISIBLE);
            mLayoutSossms.setVisibility(View.GONE);
            findViewById(R.id.iv_setting_sos_sms_divider).setVisibility(View.GONE);
        } else {
            mLayoutSmsFilter.setVisibility(View.GONE);
            findViewById(R.id.iv_watch_smsfilter_divider).setVisibility(View.GONE);
            mLayoutSossms.setVisibility(View.GONE);
            findViewById(R.id.iv_setting_sos_sms_divider).setVisibility(View.GONE);
        }

        if (focusWatch.isSupportFunctionControl()) {
            mLayoutFunctionControl.setVisibility(View.VISIBLE);
        } else {
            mLayoutFunctionControl.setVisibility(View.GONE);
        }

        if (focusWatch.isSupportFlowStatitics()) {
            mLayoutFlowStatitics.setVisibility(View.VISIBLE);
        } else {
            mLayoutFlowStatitics.setVisibility(View.GONE);
        }

        if ((focusWatch.isDevice501() && myApp.isControledByVersion(focusWatch, false, "T17"))
            /*|| focusWatch.isDevice701() || focusWatch.isDevice710()*/) {
            mLayoutCloudPhotos.setVisibility(View.GONE);
            findViewById(R.id.iv_cloud_photos_selector).setVisibility(View.GONE);
        } else {
            mLayoutCloudPhotos.setVisibility(View.GONE);
            findViewById(R.id.iv_cloud_photos_selector).setVisibility(View.GONE);
        }

        if (focusWatch.isDevice701()) {
            mLayoutReportFault.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_report_fault_onoff_selector).setVisibility(View.VISIBLE);
        } else {
            mLayoutReportFault.setVisibility(View.GONE);
            findViewById(R.id.iv_report_fault_onoff_selector).setVisibility(View.GONE);
        }

        if (focusWatch.isDevice707_H01() || (focusWatch.isDevice709_H01()&&!focusWatch.isDevice607())) {
            mLayoutDeviceLoss.setVisibility(View.VISIBLE);
            mLayoutLowPowerDisable.setVisibility(View.VISIBLE);
            mLayoutFunctionControl.setVisibility(View.GONE);
            mLayoutDeviceWifi.setVisibility(View.GONE);
            mLayoutALarmclock.setVisibility(View.GONE);
        } else {
            mLayoutDeviceLoss.setVisibility(View.GONE);
            mLayoutLowPowerDisable.setVisibility(View.GONE);
        }
        if(focusWatch.isDevice607()){
            mLayoutFunctionControl.setVisibility(View.GONE);
            mLayoutDeviceWifi.setVisibility(View.GONE);
            mLayoutALarmclock.setVisibility(View.GONE);
            mLayoutAutoanswer.setVisibility(View.GONE);
        }

        mLayoutFcm.setVisibility(View.GONE);
        findViewById(R.id.iv_watch_fcm_selector).setVisibility(View.GONE);
        mLayoutPowerSave.setVisibility(View.GONE);
        mLayoutPowerSave.setRedPointVisible(false);
        mLayoutSleepmode.setVisibility(View.GONE);
    }

    private boolean powerSettingIsShow() {
        return myApp.getBoolValue(SharedPreferencesFinalKeyUtil.SHARE_INTELLIGENT_POWER_SAVING,
                false);
    }

    private void initListener() {

        btnBack.setOnClickListener(this);
        mBtnWatchUnbind.setOnClickListener(this);
        mBtnWhiteList.setOnClickListener(this);
        mBtnSosSmg.setOnClickListener(this);
        mBtnAppNotify.setOnClickListener(this);
        mBtnCloudPhotos.setOnClickListener(this);
        mBtnReportFault.setOnClickListener(this);
        mBtnFcm.setOnClickListener(this);
        mLayoutLowPowerDisable.setOnClickListener(this);
        mLayoutFunctionControl.setOnClickListener(this);
        mLayoutFlowStatitics.setOnClickListener(this);
        mLayoutFindwatch.setOnClickListener(this);
        mLayoutDeviceWifi.setOnClickListener(this);
        mLayoutDeviceShutdown.setOnClickListener(this);
        mLayoutDeviceLoss.setOnClickListener(this);
        mLayoutRingVibrute.setOnClickListener(this);
        mLayoutALarmclock.setOnClickListener(this);
        mLayoutSilencetime.setOnClickListener(this);
        mLayoutOperationmode.setOnClickListener(this);
        mLayoutPowerSave.setOnClickListener(this);
        mLayoutSleepmode.setOnClickListener(this);
        mLayoutAutoanswer.setOnClickListener(this);
        mLayoutSmsFilter.setOnClickListener(this);

    }

    private void scrollToBottom() {

        Handler handler = new Handler();
        int result = getIntent().getIntExtra(Const.IF_SCROLL_TO_BOTTOM, 0);
        if (result == Const.IS_SCROLL_TO_BOTTOM) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRootScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, Const.SCROLL_TO_BOTTOM_POST_DELAYED);
        }
    }

    private void setBatteryValueAndTime() {

        String eid = myApp.getCurUser().getFocusWatch().getEid();
        mTvBatteryValue.setText(focusWatch.getBattery() + "");

        String update_time = myApp.getStringValue(eid + CloudBridgeUtil.BATTERY_TIMESTAMP,
                Const.DEFAULT_NEXT_KEY);
        if (update_time.equals(Const.DEFAULT_NEXT_KEY)) {
            mBatteryTagTime.setVisibility(View.INVISIBLE);
            return;
        } else {
            mBatteryTagTime.setVisibility(View.VISIBLE);
        }

        Integer status = myApp.getmChargeState().get(eid);
        if (eid != null && status != null) {
            if (status == Const.WATCH_CHARGE_IS_ON) {
                mTvBatteryTag.setText(getApplication().getString(R.string.charge_status_str));
            } else {
                mTvBatteryTag.setText(getApplication().getString(R.string.battery_level_str));
            }
        }
        Date time = TimeUtil.getDataFromTimeStamp(update_time);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd HH:mm");
        String text = dateFormat.format(time);
        mBatteryTagTime.setText(getString(R.string.steps_update_time, text));
    }

    private void updateWatchSettingStateShow() {
        //防打扰
        if (focusWatch.isDevice707_H01() || (focusWatch.isDevice709_H01()&&!focusWatch.isDevice607())) {
            String onoff = myApp.getStringValue(CloudBridgeUtil.KEY_FOCUS_TIME_ONOFF_STATUS, "0");
            if (onoff.equals("1")) {
                mLayoutSilencetime.setState(getString(R.string.open_state));
            } else {
                mLayoutSilencetime.setState(getString(R.string.close));
            }
        } else {
            if (myApp.isOpenSilenceTime(focusWatch.getEid())) {
                mLayoutSilencetime.setState(getString(R.string.open));
            } else {
                mLayoutSilencetime.setState(getString(R.string.close));
            }
        }
        //手机模式
        int operationModeSelect =
                myApp.getIntValue(focusWatch.getEid() + CloudBridgeUtil.OPERATION_MODE_VALUE,
                        Const.DEFAULT_OPERATIONMODE_VALUE);
        switch (operationModeSelect) {
            case 1:
            case 4:
                mLayoutOperationmode.setState(getString(R.string.powerSaving_mode_setting));
                break;
            case 2:
                mLayoutOperationmode.setState(getString(R.string.normal_mode_setting));
                break;
            case 5:
                mLayoutOperationmode.setState(getString(R.string.fast_mode_setting));
                break;
            case 3:
            default:
                mLayoutOperationmode.setState(getString(R.string.performance_mode_setting));
        }
        //休眠时段
        if (myApp.isOpenSleepTime(focusWatch.getEid())) {
            mLayoutSleepmode.setState(getString(R.string.open_state));
        } else {
            mLayoutSleepmode.setState(getString(R.string.close));
        }
        //自动接听设置
        String autoAnswer;
        if ((focusWatch.isDevice709_H01()&&!focusWatch.isDevice607()) || focusWatch.isDevice707_H01()) {
            autoAnswer =
                    myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_AUTO_RECEIVE,
                            "0");
        } else {
            autoAnswer =
                    myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_AUTO_RECEIVE,
                            "1");
        }
        if (autoAnswer.equals("0")) {
            setState(getString(R.string.no_auto_answer));
        } else if (autoAnswer.equals("1")) {
            if (focusWatch.isDevice709_A03() || focusWatch.isDevice708_A06() || focusWatch.isDevice708_A07() || focusWatch.isDevice709_A05()) {
                setState(getString(R.string.auto_answer_after_15s));
            } else if (focusWatch.isDevice707_H01() || focusWatch.isDevice709_H01()) {
                setState(getString(R.string.auto_answer_after_new));
            } else {
                setState(getString(R.string.auto_answer_after));
            }
        } else if (autoAnswer.equals("2")) {
            setState(getString(R.string.auto_answer_rightnow));
        }
        //通话白名单
        String defaultWhitelist = "0";
        if (focusWatch.isDevice105())
            defaultWhitelist = "1";
        if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, defaultWhitelist).equals("0")) {
            mBtnWhiteList.setImageResource(R.drawable.switch_off);
        } else {
            mBtnWhiteList.setImageResource(R.drawable.switch_on);
        }
        // 短信白名单
        String smsFilter =
                myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_SMS_FILTER, "1");
        setSmsFilterSelect(Integer.parseInt(smsFilter));
        // 云相册
        if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_CLOUD_PHOTOS, "0").equals("0")) {
            mBtnCloudPhotos.setImageResource(R.drawable.switch_off);
        } else {
            mBtnCloudPhotos.setImageResource(R.drawable.switch_on);
        }
        //故障上报
        if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_REPORT_FAULT_ONOFF, "0").equals("0")) {
            mBtnReportFault.setImageResource(R.drawable.switch_off);
        } else {
            mBtnReportFault.setImageResource(R.drawable.switch_on);
        }
        //sos短信开关
        String sosSmsOnoff =
                myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_SOS_SMS, "0");
        if (sosSmsOnoff.equals("0")) {
            mBtnSosSmg.setImageResource(R.drawable.switch_off);
        } else if (sosSmsOnoff.equals("1")) {
            mBtnSosSmg.setImageResource(R.drawable.switch_on);
        }
        //防沉迷开关
        String fcmOnoff =
                myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_FCM_ONOFF, "1");
        if (fcmOnoff.equals("0")) {
            mBtnFcm.setImageResource(R.drawable.switch_off);
        } else if (fcmOnoff.equals("1")) {
            mBtnFcm.setImageResource(R.drawable.switch_on);
        }
    }

    private void setState(String string) {

        device_autoanswer_state.setText(string);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_title_back:
                finish();
                break;

            case R.id.layout_function_control:
                startActivity(new Intent(WatchManagerActivity.this, FunctionControlActivity.class));
                break;

            case R.id.layout_flow_statistics:
                startActivity(new Intent(WatchManagerActivity.this, FlowStatiticsActivity.class));
                break;

            case R.id.layout_device_remote_loss:
                startActivity(new Intent(WatchManagerActivity.this, WRemoteLossActivity.class));
                break;

            case R.id.layout_find_watch:
                String title = getString(R.string.find_watch);
                String des = getString(R.string.find_watch_prompt_msg);
                if (!focusWatch.isWatch()) {
                    title = getString(R.string.find_device);
                    des = getString(R.string.find_device_prompt_msg);
                }
                Dialog dlg = DialogUtil.CustomNormalDialog(WatchManagerActivity.this,
                        title,
                        des,
                        new OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        },
                        getText(R.string.cancel).toString(),
                        new OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                                if (myApp.isInSilenceTime(focusWatch.getEid()) > 0) {
                                    ToastUtil.show(WatchManagerActivity.this,
                                            WatchManagerActivity.this.getString(R.string.watch_state_silence));
                                }
                                deviceFindWatch();
                            }
                        },
                        getText(R.string.confirm).toString());
                dlg.show();
                break;

            case R.id.layout_device_wifi:
                startActivity(new Intent(WatchManagerActivity.this,
                        WatchWifiSettingActivity.class));
                break;

            case R.id.layout_device_shutdown:
                openShutdownDialog();
                break;

            case R.id.layout_device_operationmode:
                startActivity(new Intent(WatchManagerActivity.this, OperationMode.class));
                break;

            case R.id.layout_device_powersave:
                myApp.setValue(SharedPreferencesFinalKeyUtil.SHARE_INTELLIGENT_POWER_SAVING, true);
                mLayoutPowerSave.setRedPointVisible(!powerSettingIsShow());

                startActivity(new Intent(WatchManagerActivity.this,
                        PowersaveSettingActivity.class));
                break;

            case R.id.layout_device_alarmclock:
                startActivity(new Intent(WatchManagerActivity.this, AlarmClockActivity.class));
                break;

            case R.id.layout_device_silence:
                if (focusWatch.isDevice707_H01() || (focusWatch.isDevice709_H01()&&!focusWatch.isDevice607())) {
                    startActivity(new Intent(WatchManagerActivity.this, FocusTimeMainActivity.class));
                } else {
                    startActivity(new Intent(WatchManagerActivity.this, SilenceVolumeActivity.class));
                }
                break;

            case R.id.layout_device_sleepmode:
                startActivity(new Intent(WatchManagerActivity.this, SleepTimeActivity.class));
                break;

            case R.id.layout_low_power_disable:
                String eid = myApp.getCurUser().getFocusWatch().getEid();
                Intent it = new Intent(WatchManagerActivity.this, LowPowerDisableActivity.class);
                it.putExtra("watch_eid", eid);
                startActivity(it);
                break;

            case R.id.layout_ring_vibrate:
                startActivity(new Intent(WatchManagerActivity.this, VolumeActivity.class));
                break;

            case R.id.layout_device_autoanswer:
                startActivity(new Intent(WatchManagerActivity.this,
                        DeviceAutoAnswerActivity.class));
                break;

            case R.id.layout_device_smsfilter:
                openSelectSmsFilterDialog();
                break;

            case R.id.btn_watch_white_list:
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.showMyToast(WatchManagerActivity.this,
                            getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, "1").equals("0")) {
                    Dialog promptDlg = DialogUtil.CustomNormalDialog(WatchManagerActivity.this,
                            getString(R.string.prompt),
                            getString(R.string.watch_white_list_open_prompt),
                            new OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    mapSetMsg(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST, "1");
                                }
                            }, getString(R.string.confirm));
                    promptDlg.show();
                } else {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST, "0");
                }
                break;

            case R.id.btn_sos_sms:
//                if (!myApp.isMeAdmin()) {
//                    ToastUtil.showMyToast(WatchManagerActivity.this, "需要管理员权限", Toast
//                    .LENGTH_LONG);
//                    return;
//                }
                if (myApp.getStringValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_SOS_SMS, "0"
                ).equals("0")) {
                    mapSetSosSmsMsg("1");
                } else {
                    mapSetSosSmsMsg("0");
                }
                break;

            case R.id.bt_watch_unbind:
                openUnbindDialog();
                break;

            case R.id.btn_cloud_photos:
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.showMyToast(WatchManagerActivity.this,
                            getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_CLOUD_PHOTOS, "1").equals("0")) {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS, "1");
                } else {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS, "0");
                }
                break;
            case R.id.btn_report_fault_onoff:
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.showMyToast(WatchManagerActivity.this,
                            getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_REPORT_FAULT_ONOFF, "1").equals("0")) {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF, "1");
                } else {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF, "0");
                }
                break;
            case R.id.btn_watch_fcm:
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.showMyToast(WatchManagerActivity.this,
                            getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                if (myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_FCM_ONOFF, "1").equals("0")) {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_FCM_ONOFF, "1");
                } else {
                    mapSetMsg(CloudBridgeUtil.KEY_NAME_FCM_ONOFF, "0");
                }
                break;

            default:
                break;
        }
    }

    private void initBroadcastReceiver() {

        mMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.ACTION_WATCH_SHUTDOWN_CHECKRESULT)) {
                    JSONObject respPl;
                    String resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    String seid =
                            CloudBridgeUtil.getCloudMsgSEID((JSONObject) JSONValue.parse(resp));
                    String focus = myApp.getCurUser().getFocusWatch().getEid();
                    if (resp != null && seid != null && focus != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            int recode = CloudBridgeUtil.getCloudMsgRC(respPl);
                            if (recode == CloudBridgeUtil.RC_SUCCESS && focus.equals(seid)) {
//                                mLayoutDeviceShutdown.getTvTitle().setTextColor(getResources()
//                                .getColor(R.color.txt_grey));
//                                mLayoutDeviceShutdown.setClickable(false);
                                focusWatch.setOffLine(true);
                            }
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE)
                        || intent.getAction().equals(Const.ACTION_REFRESH_WATCH_TITLE)) {
                    setBatteryValueAndTime();
//                    updateShutdownState();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_WATCH_SHUTDOWN_CHECKRESULT);
        filter.addAction(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
        filter.addAction(Const.ACTION_REFRESH_WATCH_TITLE);
        registerReceiver(mMsgReceiver, filter);
    }

    private void updateWatchWifiConnectState() {

        boolean isWifiConnect = focusWatch.getIsWifiConnect();
        if (isWifiConnect) {
            String wifiName = focusWatch.getDeviceWifiName();
//            mLayoutDeviceWifi.setVisibility(View.VISIBLE);
            if (wifiName != null) {
                mLayoutDeviceWifi.setState(wifiName);
            }
        } else {
            mLayoutDeviceWifi.setState("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMsgReceiver != null) {
            unregisterReceiver(mMsgReceiver);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void openShutdownDialog() {

        boolean isOnTimer;    //是否支持定时开机
        isOnTimer = focusWatch.isDevice102() && !myApp.isControledByVersion(focusWatch, false,
                "T29");
        if (isOnTimer) {
            Dialog dlg = DialogUtil.CustomNormalDialog(WatchManagerActivity.this,
                    getString(R.string.setting_watch_power),
                    getString(R.string.confirm_to_shutdown_and_power_on),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            powerOnTime = 0;
                            deviceShutDown(powerOnTime);
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        } else {
            openShutdownTimeDialog();
        }
    }

    private void openSelectSmsFilterDialog() {

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(getText(R.string.device_sms_filer_all).toString());
        itemList.add(getText(R.string.device_sms_filer_group).toString());
        if (!focusWatch.isDevice701())  //701暂时隐去“接收所有短信”
            itemList.add(getText(R.string.device_sms_filer_no).toString());
        Dialog dlg =
                CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(WatchManagerActivity.this,
                        getText(R.string.device_sms_filer).toString(),
                        getString(R.string.device_sms_filer_desc_new), itemList,
                        new CustomSelectDialogUtil.AdapterItemClickListener() {
                            @Override
                            public void onClick(View v, int position) {
                                if (formalSmsFilter != position - 1) {
                                    int newSmsFilter = position - 1;
                                    mapSetMsg(CloudBridgeUtil.SMS_FILTER, Integer.toString(newSmsFilter));
                                }
                            }
                        },
                        formalSmsFilter + 1, new CustomSelectDialogUtil.CustomDialogListener() {
                            @Override
                            public void onClick(View v, String text) {
                            }
                        }, getText(R.string.cancel).toString());
        dlg.show();
    }

    private void setSmsFilterSelect(int noticeValue) {

        formalSmsFilter = noticeValue;
        if (noticeValue == 0)
            mTvSmsFilterState.setText(R.string.device_sms_filer_all);
        else if (noticeValue == 1)
            mTvSmsFilterState.setText(R.string.device_sms_filer_group);
        else if (noticeValue == 2)
            mTvSmsFilterState.setText(R.string.device_sms_filer_no);
    }

    private void openUnbindDialog() {

        if (getMyApp().getCurUser().isMeAdminByWatch(focusWatch)) {
            Dialog dlg = DialogUtil.CustomALertDialog(WatchManagerActivity.this,
                    getText(R.string.unbind).toString(),
                    getText(R.string.clean_group_alert).toString(),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            reqRemoveAllEndpointFormGroupWithNotice(gid);
                            lockUI();
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        } else {
            Dialog dlg = DialogUtil.CustomALertDialog(WatchManagerActivity.this,
                    getString(R.string.prompt),
                    getString(R.string.really_unbind_watch),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            doGetContactsFromServer();
                            lockUI();
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        }
    }

    private void mapMGetWatchState() {

        String[] keys = new String[2];
        String eid = myApp.getCurUser().getFocusWatch().getEid();
        keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
        keys[1] = CloudBridgeUtil.KEY_NAME_WATCH_STATE;
        if (mNetService != null) {
            mNetService.mapMGet(eid, keys);
        }
    }

    private void mapMGetWatchSettingState() {

        String[] keys = new String[10];
        String eid = myApp.getCurUser().getFocusWatch().getEid();
        keys[0] = CloudBridgeUtil.SILENCE_LIST;
        keys[1] = CloudBridgeUtil.OPERATION_MODE_VALUE;
        keys[2] = CloudBridgeUtil.SLEEP_LIST;
        keys[3] = CloudBridgeUtil.AUTO_ANSWER;
        keys[4] = CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST;
        keys[5] = CloudBridgeUtil.SMS_FILTER;
        keys[6] = CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS;
        keys[7] = CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS;
        keys[8] = CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF;
        keys[9] = CloudBridgeUtil.KEY_NAME_FCM_ONOFF;
        if (mNetService != null) {
            mNetService.sendMapMGetMsg(eid, keys, WatchManagerActivity.this);
        }
    }

    private void e2eGetWatchWifiState() {

        if (mNetService != null &&
                ((focusWatch.isDevice501() && myApp.isControledByVersion(focusWatch, true, "T16"))
                        || focusWatch.isDevice505() || focusWatch.isDevice502() || focusWatch.isDevice710()
                        || focusWatch.isDevice705() || focusWatch.isDevice703() || focusWatch.isDevice305()))
            mNetService.sendE2EMsg(focusWatch.getEid(),
                    CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE, 120 * 1000, true,
                    WatchManagerActivity.this);
    }

    private void mapSetMsg(String key, String value) {
        String eid = myApp.getCurUser().getFocusWatch().getEid();
        String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
        if (mNetService != null) {
            mNetService.sendMapSetMsg(eid, familyid, key, value, WatchManagerActivity.this);
        }
    }

    private void mapSetSosSmsMsg(String isOn) {
        JSONArray phonelist = new JSONArray();
        JSONObject admin = new JSONObject();
        admin.put(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS_PHONENUMBER,
                myApp.getAdminPhonenumber(focusWatch.getEid()));
        phonelist.add(admin);
        JSONObject sosSmsJo = new JSONObject();
        sosSmsJo.put(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS_PHONELIST, phonelist);
        sosSmsJo.put(CloudBridgeUtil.ONOFF, isOn);
        mapSetMsg(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS, sosSmsJo.toJSONString());
    }

    private int reqRemoveEndpointFormGroupWithNotice(String eid, String gid, String nickname) {

        mFamilyList = myApp.getCurUser().getFamilyList();
        int sn;
        MyMsgData removeMsg = new MyMsgData();
        removeMsg.setCallback(WatchManagerActivity.this);
        //set msg body
        JSONObject newPl = new JSONObject();
        newPl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        newPl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        //notice info
        newPl.put(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE,
                Integer.valueOf(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP).toString());
        newPl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, TimeUtil.getTimeStampLocal());
        newPl.put(CloudBridgeUtil.KEY_NAME_NICKNAME, nickname);
        newPl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        newPl.put(CloudBridgeUtil.KEY_NAME_SMS_DATA,
                getNoticeGroupChangeSMS(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP, sn,
                        eid, gid, TimeUtil.getTimeStampLocal()));

        removeMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_REMOVE_EID_FROM_GROUP,
                        sn, getMyApp().getToken(), newPl));
        if (mNetService != null)
            mNetService.sendNetMsg(removeMsg);
        return sn;
    }

    private ArrayList<FamilyData> mFamilyList;

    private int reqRemoveAllEndpointFormGroupWithNotice(String gid) {

        mFamilyList = myApp.getCurUser().getFamilyList();
        int sn;
        MyMsgData removeMsg = new MyMsgData();
        removeMsg.setCallback(WatchManagerActivity.this);
        //set msg body
        JSONObject newPl = new JSONObject();
        newPl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        newPl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_REMOVE_OUT_PRE_NOTICE);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

        removeMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_REMOVE_EID_FROM_GROUP,
                        sn, getMyApp().getToken(), newPl));
        if (mNetService != null)
            mNetService.sendNetMsg(removeMsg);
        return sn;
    }

    private void deviceFindWatch() {

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_WATCH_BUZZER);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, Integer.valueOf(1).toString());
        String[] teid;
        teid = new String[1];
        teid[0] = focusWatch.getEid();
        StringBuffer sms = new StringBuffer();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        sms.append("<" + sn + "," + getMyApp().getCurUser().getEid() + "," + "E" +
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_WATCH_BUZZER + "," + 1 + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        if (mNetService != null)
            mNetService.sendE2EMsg(focusWatch.getEid(), sn, pl, 120 * 1000, false,
                    WatchManagerActivity.this);
    }

    private void deviceShutDown(int powerOnTime) {
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, Integer.valueOf(powerOnTime).toString());
        String[] teid;
        teid = new String[1];
        teid[0] = focusWatch.getEid();
        StringBuffer sms = new StringBuffer();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        sms.append("<" + sn + "," + getMyApp().getCurUser().getEid() + "," + "E" +
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE + "," + powerOnTime +
                ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        if (mNetService != null)
            mNetService.sendE2EMsg(focusWatch.getEid(), sn, pl, 120 * 1000, true,
                    WatchManagerActivity.this);
    }

    //发送e2g通知消息
    private void e2gNotify(String familyid, int subaction, String watcheid, int state,
                           String time) {
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subaction);
        pl.put(CloudBridgeUtil.PL_KEY_EID, watcheid);
        pl.put(CloudBridgeUtil.KEY_NAME_WATCH_STATE, Integer.toString(state));
        pl.put(CloudBridgeUtil.KEY_NAME_WATCH_STATE_TIMESTAMP, time);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2GMsg(familyid, sn, pl, 120 * 1000, true, WatchManagerActivity.this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPGET_RESP:
                if (rc == 1) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    //whitelist
                    String sTmp = (String) pl.get(CloudBridgeUtil.PHONE_WHITE_LIST);
                    if (sTmp != null && sTmp.length() > 0) {
                        mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                        String familyNickname =
                                getMyApp().getCurUser().getRelation(focusWatch.getEid());
                        //删除usereid匹配的联系人
                        PhoneNumber findPhone = null;
                        if (mBindWhiteList != null && mBindWhiteList.size() > 0) {
                            for (PhoneNumber member : mBindWhiteList) {
                                if (member.userEid != null && member.userEid.equals(myApp.getCurUser().getEid())) {
                                    if (member.id != null) {
                                        //获取要解绑联系人
                                        familyNickname = myApp.getPhoneNumberFamilyNickname(member);
                                        findPhone = member;
                                    }
                                    break;
                                }
                            }
                            if (findPhone != null) {
                                //移除解绑联系人之后，重新set联系人
                                mBindWhiteList.remove(findPhone);
                                sendMapsetWhiteList();
                            }
                        }
                        reqRemoveEndpointFormGroupWithNotice(myApp.getCurUser().getEid(), gid,
                                familyNickname);
                        //持久化
                        myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY,
                                sTmp);
                    }
                } else {//失败
                    if (removeDelMapgetSN == (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_SN)) {
                        ToastUtil.showMyToast(this, getString(R.string.unbind_failed),
                                Toast.LENGTH_SHORT);
                        //解除界面锁定
                        unlockUI();
                    }
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                JSONObject plMGet = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    if (plMGet.containsKey(CloudBridgeUtil.SILENCE_LIST)) {
                        String jstr = (String) plMGet.get(CloudBridgeUtil.SILENCE_LIST);
                        //拿到数据后本地存储逻辑放到NetService的方法里进行
                        myApp.getNetService().updateSilenceTimeData(focusWatch.getEid(), jstr);
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.OPERATION_MODE_VALUE)) {
                        String operationModeValue =
                                (String) plMGet.get(CloudBridgeUtil.OPERATION_MODE_VALUE);
                        if (operationModeValue != null && operationModeValue.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + CloudBridgeUtil.OPERATION_MODE_VALUE, Integer.parseInt(operationModeValue));
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                        String jstr = (String) plMGet.get(CloudBridgeUtil.SLEEP_LIST);
                        myApp.getNetService().updateSleepListData(focusWatch.getEid(), jstr);
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.AUTO_ANSWER)) {
                        String autoAnswerState = (String) plMGet.get(CloudBridgeUtil.AUTO_ANSWER);
                        if (autoAnswerState != null && autoAnswerState.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_AUTO_RECEIVE, autoAnswerState);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST)) {
                        String whitelistState =
                                (String) plMGet.get(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST);
                        if (whitelistState != null && whitelistState.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, whitelistState);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.SMS_FILTER)) {
                        String smsFilter = (String) plMGet.get(CloudBridgeUtil.SMS_FILTER);
                        if (smsFilter != null && smsFilter.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_SMS_FILTER, smsFilter);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS)) {
                        String cloudPhotos =
                                (String) plMGet.get(CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS);
                        if (cloudPhotos != null && cloudPhotos.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_CLOUD_PHOTOS, cloudPhotos);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF)) {
                        String reportFaultOnoff =
                                (String) plMGet.get(CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF);
                        if (reportFaultOnoff != null && reportFaultOnoff.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_REPORT_FAULT_ONOFF, reportFaultOnoff);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.KEY_NAME_FCM_ONOFF)) {
                        String fcmOnoff = (String) plMGet.get(CloudBridgeUtil.KEY_NAME_FCM_ONOFF);
                        if (fcmOnoff != null && fcmOnoff.length() > 0) {
                            myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_FCM_ONOFF
                                    , fcmOnoff);
                        }
                    }
                    if (plMGet.containsKey(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS)) {
                        String sosSmsData =
                                (String) plMGet.get(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS);
                        if (sosSmsData != null && !TextUtils.isEmpty(sosSmsData)) {
                            JSONObject sosSmsJo = (JSONObject) JSONValue.parse(sosSmsData);
                            String sosSmsOnoff = (String) sosSmsJo.get(CloudBridgeUtil.ONOFF);
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_SOS_SMS, sosSmsOnoff);
                        }
                    }
                    updateWatchSettingStateShow();
                } else {
                    //不作处理
                }
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if (rc == 1) {
                    JSONObject pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String whitelist = (String) pl.get(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST);
                    if (whitelist != null) {
                        if (whitelist.equals("0")) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, "0");
                            mBtnWhiteList.setImageResource(R.drawable.switch_off);
                        } else if (whitelist.equals("1")) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, "1");
                            mBtnWhiteList.setImageResource(R.drawable.switch_on);
                        }
                    }
                    String smsFilter = (String) pl.get(CloudBridgeUtil.SMS_FILTER);
                    if (smsFilter != null) {
                        setSmsFilterSelect(Integer.parseInt(smsFilter));
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_SMS_FILTER, smsFilter);
                    }
                    String reportFaultOnoff =
                            (String) pl.get(CloudBridgeUtil.KEY_NAME_REPORT_FAULT_ONOFF);
                    if (reportFaultOnoff != null) {
                        if (reportFaultOnoff.equals("0"))
                            mBtnReportFault.setImageResource(R.drawable.switch_off);
                        else if (reportFaultOnoff.equals("1"))
                            mBtnReportFault.setImageResource(R.drawable.switch_on);
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_REPORT_FAULT_ONOFF, reportFaultOnoff);
                    }
                    String cloudPhotos =
                            (String) pl.get(CloudBridgeUtil.KEY_NAME_DEVICE_CLOUD_PHOTOS);
                    if (cloudPhotos != null) {
                        if (cloudPhotos.equals("0"))
                            mBtnCloudPhotos.setImageResource(R.drawable.switch_off);
                        else if (cloudPhotos.equals("1"))
                            mBtnCloudPhotos.setImageResource(R.drawable.switch_on);
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_CLOUD_PHOTOS, cloudPhotos);
                    }
                    String fcmOnOff = (String) pl.get(CloudBridgeUtil.KEY_NAME_FCM_ONOFF);
                    if (fcmOnOff != null) {
                        if (fcmOnOff.equals("0"))
                            mBtnFcm.setImageResource(R.drawable.switch_off);
                        else if (fcmOnOff.equals("1"))
                            mBtnFcm.setImageResource(R.drawable.switch_on);
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_FCM_ONOFF, fcmOnOff);
                    }
                    String sosSmsData = (String) pl.get(CloudBridgeUtil.KEY_NAME_DEVICE_SOS_SMS);
                    if (sosSmsData != null) {
                        JSONObject sosSmsJo = (JSONObject) JSONValue.parse(sosSmsData);
                        String sosSmsOnoff = (String) sosSmsJo.get(CloudBridgeUtil.ONOFF);
                        if (sosSmsOnoff.equals("0")) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_SOS_SMS, "0");
                            mBtnSosSmg.setImageResource(R.drawable.switch_off);
                        } else if (sosSmsOnoff.equals("1")) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_SOS_SMS, "1");
                            mBtnSosSmg.setImageResource(R.drawable.switch_on);
                        }
                    }
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this,
                            getString(R.string.phone_set_timeout),
                            Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt),
                            Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error),
                            Toast.LENGTH_SHORT);
                }
                break;
            case CloudBridgeUtil.CID_E2E_DOWN:
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                JSONObject plreqMsg = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                rc = CloudBridgeUtil.getCloudMsgRC(pl);
                if (rc == -204) {  //rc值有的在pl中，有的在respMsg中，理论上应该都在respMsg中
                    rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                }
                if (pl == null) {
                    if (CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE == (Integer) plreqMsg.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION)) {
                        //如果是获取wifi连接状态失败，不作弹出提示
                        focusWatch.setIsWifiConnect(false);
                        focusWatch.setDeviceWifiName("");
                        updateWatchWifiConnectState();
                        break;
                    }
                    int tmpRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (tmpRc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                        ToastUtil.showMyToast(WatchManagerActivity.this,
                                getText(R.string.set_error3).toString(), Toast.LENGTH_SHORT);
                    } else if (tmpRc == CloudBridgeUtil.RC_NETERROR) {
                        ToastUtil.showMyToast(this,
                                getText(R.string.network_error_prompt).toString(),
                                Toast.LENGTH_SHORT);
                    } else if (tmpRc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {

                    } else if (tmpRc == CloudBridgeUtil.RC_HALF_SUCCESS) {
                        int value = (Integer) plreqMsg.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                        if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE == value) {
                            dealShutdownReqSuccess();
                        }
                    } else if (tmpRc == CloudBridgeUtil.ERROR_CODE_E2E_OFFLINE) {//手表不在线
                        ToastUtil.show(this, getString(R.string.watch_offline));
                    } else if (tmpRc < 0 && tmpRc != CloudBridgeUtil.RC_TIMEOUT) {//网络不好,
                        // 有时候手表关机服务器没有返回值,所以也存在手表不在线的可能
                        ToastUtil.showMyToast(this, getText(R.string.watch_offline).toString(),
                                Toast.LENGTH_SHORT);
                    }
                    break;
                }
                if (rc == CloudBridgeUtil.RC_SUCCESS || rc == CloudBridgeUtil.RC_HALF_SUCCESS) {
                    int value = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE == value) {
                        dealShutdownReqSuccess();
                        //关机成功
                    } else if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_WATCH_BUZZER == value) {
                        //找手表成功;
                    } else if (CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE == value) {
                        //wifi连接状态
                        setDeviceWifiState(pl);
                    }
                } else if (rc < 0) {
                    if (CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE == (Integer) plreqMsg.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION)) {
                        //如果是获取wifi连接状态失败，不作弹出提示
                        break;
                    }
                    ToastUtil.showMyToast(this, getText(R.string.set_error).toString(),
                            Toast.LENGTH_SHORT);
                }
                break;

            case CloudBridgeUtil.CID_REMOVE_EID_FROM_GROUP_RESP:
                //本地列表中删除这个组，本eid和组的关系全部清除设备也从本地列表中溢出
                if (rc == CloudBridgeUtil.RC_SUCCESS || rc == -1) {//已经解绑后，再解绑会返回-1
                    mFamilyList = myApp.getCurUser().getFamilyList();
                    JSONObject reqPl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                    String gid = (String) reqPl.get(CloudBridgeUtil.KEY_NAME_GID);

                    ArrayList<FamilyData> tempFamilyList = mFamilyList;
                    //remove local
                    if (tempFamilyList != null && tempFamilyList.size() > 0) {
                        for (FamilyData family : tempFamilyList) {
                            if (gid.equals(family.getFamilyId())) {
                                mFamilyList.remove(family);
                                //clear old group del db
                                UserRelationDAO.getInstance(getApplicationContext()).cleanUsersByGid(gid);
                                //remove  watch
                                myApp.getCurUser().getWatchList().remove(focusWatch);
                                //reset focus
                                if (myApp.getCurUser().getWatchList().size() > 0) {
                                    myApp.getCurUser().setFocusWatch(myApp.getCurUser().getWatchList().get(0));
                                }
                                break;
                            }
                        }
                    }
                    tempFamilyList = null;
                    //多个家庭立即刷新，保持同步
                    if (myApp.getCurUser().getWatchList().size() > 0) {
                        myApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH));
                    } else {
                        // 自己解绑，只有一个手表的情况下
                        myApp.doLogout("WATCHMANAGER CID_REMOVE_EID_FROM_GROUP_RESP self remove " +
                                "group");
                        //myApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const
                        // .ACTION_UNBIND_RESET_FOCUS_WATCH));
                    }
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.unbind_failed),
                                Toast.LENGTH_SHORT);
                    }
                }
                //解除界面锁定
                unlockUI();
                break;

            case CloudBridgeUtil.CID_GET_CONTACT_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl1 = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单
                    JSONArray obj =
                            (JSONArray) pl1.get(CloudBridgeUtil.KEY_NAME_CONTACT_SYNC_ARRAY);
                    String sTmp = obj.toString();

                    mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                    String familyNickname =
                            getMyApp().getCurUser().getRelation(focusWatch.getEid());
                    //删除usereid匹配的联系人
                    if (mBindWhiteList != null && mBindWhiteList.size() > 0) {
                        for (PhoneNumber member : mBindWhiteList) {
                            if (member.userEid != null && member.userEid.equals(myApp.getCurUser().getEid())) {
                                if (member.id != null) {
                                    sendDelContactReq(focusWatch.getEid(),
                                            focusWatch.getFamilyId(), member.id);
                                    familyNickname = myApp.getPhoneNumberFamilyNickname(member);
                                }
                                break;
                            }
                        }
                    }
                    reqRemoveEndpointFormGroupWithNotice(myApp.getCurUser().getEid(), gid,
                            familyNickname);
                    //持久化
                    myApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, sTmp);
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.unbind_failed),
                                Toast.LENGTH_SHORT);
                        //解除界面锁定
                        unlockUI();
                    } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_MESSAGE_ILLEGAL) {//一般错误
                        //没有联系人。不用操作直接解绑
                        reqRemoveEndpointFormGroupWithNotice(myApp.getCurUser().getEid(), gid,
                                getMyApp().getCurUser().getRelation(focusWatch.getEid()));
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.unbind_failed),
                                Toast.LENGTH_SHORT);
                        //解除界面锁定
                        unlockUI();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void dealShutdownReqSuccess() {

//        mLayoutDeviceShutdown.getTvTitle().setTextColor(getResources().getColor(R.color
//        .txt_grey));
//        mLayoutDeviceShutdown.setClickable(false);
        focusWatch.setOffLine(true);
        if (myApp.timeWatchShutDown == null)
            myApp.timeWatchShutDown = new HashMap<>();
        myApp.timeWatchShutDown.put(focusWatch.getEid(), System.currentTimeMillis());

        if (powerOnTime != 0) {
            ToastUtil.showMyToast(this, getString(R.string.watch_poweroff_already) + powerOnText,
                    Toast.LENGTH_SHORT);
            if (myApp.getNetService() != null) {
                Date d = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String poweron = df.format(new Date(d.getTime() + powerOnTime * 60 * 1000));
                myApp.getNetService().sendMapSetMsg(focusWatch.getEid(), focusWatch.getFamilyId()
                        , CloudBridgeUtil.DEVICE_POWER_ON_TIME, poweron, WatchManagerActivity.this);
                myApp.setValue(focusWatch.getEid() + CloudBridgeUtil.DEVICE_POWER_ON_TIME, poweron);
            }
        } else {
            ToastUtil.showMyToast(this, getString(R.string.watch_poweroff_prompt),
                    Toast.LENGTH_SHORT);
            myApp.setValue(focusWatch.getEid() + CloudBridgeUtil.DEVICE_POWER_ON_TIME, "0");
            myApp.getNetService().sendMapSetMsg(focusWatch.getEid(), focusWatch.getFamilyId(),
                    CloudBridgeUtil.DEVICE_POWER_ON_TIME, "0", WatchManagerActivity.this);
        }

        Intent it = new Intent(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
        it.putExtra(Const.KEY_WATCH_ID, myApp.getCurUser().getFocusWatch().getEid());
        it.putExtra(Const.KEY_WATCH_STATE, Const.WATCH_STATE_POWER_OFF);
        sendBroadcast(it);
        // 同步iOS修改逻辑，不再发送e2g消息
//        e2gNotify(myApp.getCurUser().getFocusWatch().getFamilyId(),
//                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_STATE_CHANGE_NOTICE,
//                myApp.getCurUser().getFocusWatch().getEid(),
//                Const.WATCH_STATE_POWER_OFF,
//                TimeUtil.getTimeStampLocal().substring(0, 12));
    }

    @Override
    public void onBackPressed() {
        if (isLockUI) {//lock时候不准返回

        } else {
            super.onBackPressed();
        }
    }

    private boolean isLockUI = false;

    private void lockUI() {
        layerWaiting.setVisibility(View.VISIBLE);
        isLockUI = true;
        if (!loadingdlg.isShowing()) {
            loadingdlg.enableKeyBack(false);
            loadingdlg.enableCancel(false);
            loadingdlg.changeStatus(1, getString(R.string.unbinding));
            loadingdlg.show();
        }
    }

    private void unlockUI() {
        isLockUI = false;
        loadingdlg.dismiss();
        layerWaiting.setVisibility(View.GONE);
    }

    private String getNoticeGroupChangeSMS(int type, int sN2, String eid, String addGroupGid2,
                                           String timeStampLocal) {
        StringBuilder buff = new StringBuilder();
        buff.append("<");
        buff.append(Integer.valueOf(sN2).toString());
        buff.append(",");
        buff.append(getMyApp().getCurUser().getEid());
        buff.append(",");
        buff.append("G202");
        buff.append(",");
        buff.append(Integer.valueOf(type).toString());
        buff.append("@");
        buff.append(addGroupGid2);
        buff.append("@");
        buff.append(timeStampLocal);
        buff.append(">");
        return buff.toString();
    }

    @Override
    public void confirmClick() {

    }

    private CustomerPickerView mPickShutdownTime;
    int powerOnTime = 0;
    String powerOnText = "";//需要赋初值

    private void openShutdownTimeDialog() {
        powerOnText = getString(R.string.watch_shut_down_rightnow);//需要赋初值
        final Dialog dlg = new Dialog(WatchManagerActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater =
                (LayoutInflater) WatchManagerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_shutdown, null);
        mPickShutdownTime = layout.findViewById(R.id.pick_shutdown_time);
        mPickShutdownTime.setMarginAlphaValue((float) 3.8, "H");
        View pickerView = layout.findViewById(R.id.shutdown_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        final int hheight = pickerView.getBackground().getMinimumHeight();

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerOnText.equals(getString(R.string.watch_shut_down_rightnow))) {
                    powerOnTime = 0;
                } else {
                    String regEx = "[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(powerOnText);
                    int hour = Integer.parseInt(m.replaceAll("").trim());
                    powerOnTime = hour * 60;
                }
                deviceShutDown(powerOnTime);
                dlg.dismiss();
            }
        });

        View mask_1 = layout.findViewById(R.id.iv_mask_1);
        mask_1.setTranslationX(width * 5 / 10 - 40);
        mask_1.setTranslationY(hheight / 11);
        List<String> shutdownTimes = new ArrayList<String>();
        shutdownTimes.add(getString(R.string.watch_shut_down_rightnow));
        for (int i = 1; i <= 12; i++) {
            shutdownTimes.add(getString(R.string.boot_after_hours, i));
        }
        mPickShutdownTime.setData(shutdownTimes);
        mPickShutdownTime.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                powerOnText = text;
            }
        });
        mPickShutdownTime.setSelected(0);

        // set a large value put it in bottom
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

    private ArrayList<PhoneNumber> mBindWhiteList = new ArrayList<PhoneNumber>();
    private int removeDelMapgetSN = -1;

    //区分手表类型，采用不同接口同步联系人
    private void doGetContactsFromServer() {
        if (focusWatch.isDevice102()) {
            removeDelMapgetSN = getPhoneWhiteListByMapget();
        } else {
            sendGetAllContactReq();
        }
    }

    //从服务器MAPGET_MGET的方式取数据
    private int getPhoneWhiteListByMapget() {

        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(WatchManagerActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, focusWatch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.PHONE_WHITE_LIST);
        int sn = Long.valueOf(TimeUtil.getTimeStampLocal()).intValue();
        queryGroupsMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
                        sn, myApp.getToken(), pl));
        if (mNetService != null) {
            mNetService.sendNetMsg(queryGroupsMsg);
        }
        return sn;
    }

    private int sendGetAllContactReq() {

        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(WatchManagerActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, focusWatch.getEid());
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (mNetService != null) {
            mNetService.sendNetMsg(req);
        }
        return sn;
    }

    private int sendDelContactReq(String watcheid, String watchGid, String contactId) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(WatchManagerActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_GID, watchGid);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watcheid);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, contactId);
        int opt = 2;
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_OPT_TYPE, opt);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,
                CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_OPT_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (mNetService != null) {
            mNetService.sendNetMsg(req);
        }
        return sn;
    }

    private JSONArray plA = new JSONArray();

    int sendMapsetWhiteList() {
        if (mBindWhiteList.size() >= 0) {
            plA.clear();
            for (int i = 0; i < mBindWhiteList.size(); i++) {
                JSONObject plO = new JSONObject();
                plO.put("number", mBindWhiteList.get(i).number);
                if (mBindWhiteList.get(i).subNumber != null && mBindWhiteList.get(i).subNumber.length() > 0) {
                    plO.put("sub_number", mBindWhiteList.get(i).subNumber);
                }
                if (mBindWhiteList.get(i).ring != null) {
                    plO.put("ring", mBindWhiteList.get(i).ring);
                }
                if (mBindWhiteList.get(i).userEid != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID,
                            mBindWhiteList.get(i).userEid);
                }
                if (mBindWhiteList.get(i).userGid != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID,
                            mBindWhiteList.get(i).userGid);
                }
                if (mBindWhiteList.get(i).nickname != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, mBindWhiteList.get(i).nickname);
                }
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, mBindWhiteList.get(i).weight);
                plO.put("attri", mBindWhiteList.get(i).attri);
                plO.put("timeStampId", mBindWhiteList.get(i).timeStampId);
                plA.add(plO);
            }
        }
        if (myApp.getNetService() != null) {
            return myApp.getNetService().sendMapSetMsg(focusWatch.getEid(),
                    focusWatch.getFamilyId(),
                    CloudBridgeUtil.PHONE_WHITE_LIST, plA.toString(), WatchManagerActivity.this);
        }
        return -1;
    }

    /**
     * 获取手表wifi连接状态后，将状态保存到watchdata中
     */
    private void setDeviceWifiState(JSONObject pl) {

        Integer wifiState = (Integer) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_STATE);
        if ((wifiState & 0x04) == 0x04) {    //if(status&0x04==0x04) connected
            focusWatch.setIsWifiConnect(true);
            focusWatch.setDeviceWifiName((String) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID));
        } else {
            focusWatch.setIsWifiConnect(false);
            focusWatch.setDeviceWifiName("");
        }
        updateWatchWifiConnectState();
    }
}
