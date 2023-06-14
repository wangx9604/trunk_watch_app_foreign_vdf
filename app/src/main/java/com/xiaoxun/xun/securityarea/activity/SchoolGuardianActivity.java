package com.xiaoxun.xun.securityarea.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.xiaoxun.mapadapter.utils.CoordinateConvertUtils;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.activitys.SecurityZoneSettingGoogle;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.securityarea.WaitingDialog;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.SecurityZone;
import com.xiaoxun.xun.utils.SelectTimeUtils;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.WatchWifiUtils;
import com.xiaoxun.xun.views.CustomerPickerView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SchoolGuardianActivity extends NormalActivity implements View.OnClickListener, MsgCallback {
    private static final String TAG = "SchoolGuardianActivity";

    public static final int SECURITY_SETTING_HOME_REQ = 0x10;
    public static final int SECURITY_SETTING_SCHOOL_REQ = 0x11;
    public static final int SECURITY_SETTING_HOMESETTING_REQ = 0x12;

    private static final int ERROR_CODE_NO_HOME_ADDRESS = -1;
    private static final int ERROR_CODE_NO_SCHOOL_ADDRESS = -2;
    private static final int ERROR_CODE_LEAVE_TIME_WRONG = -3;
    private static final int ERROR_CODE_TIME_NERALY = -4;
    private static final int ERROR_CODE_REPEAT_NO_SEL = -5;
    private static final int ERROR_CODE_NO_ADDRESS = -6;

    public static final String SCHOOL_ARRIVE_TIME_INTEVAL = "20,10";
    public static final String SCHOOL_LEAVE_TIME_INTEVAL = "2,28";

    ImageButton iv_back;
    ImageButton iv_save;
    ToggleButton guard_enable;
    ConstraintLayout home_address_ly;
    ConstraintLayout school_address_ly;
    LinearLayout arrive_time_ly;
    LinearLayout leave_time_ly;
    RelativeLayout repeat_ly;
    ToggleButton festival_enable;

    RelativeLayout home_wifi_ly;
    TextView home_wifi;
    TextView home_wifi_title;

    TextView tv_title;
    TextView home_address;
    TextView school_address;
    TextView arrive_time;
    TextView leave_time;
    TextView repeat_days;
    TextView arrive_time_inteval;
    TextView leave_time_inteval;
    TextView home_address_title;
    TextView school_address_title;
    TextView arrive_time_title;
    TextView leave_time_title;
    TextView arrive_inteval_lab;
    TextView leave_inteval_lab;
    TextView repeat_lab;
    TextView fetival_lab;
    View arrive_time_select_view;
    View leave_time_select_view;

    private WaitingDialog waitingDialog;

    SecurityZone homeEfence;
    SecurityZone schoolEfence;
    SchoolGuardInfo guardInfoInServer = new SchoolGuardInfo();
    SchoolGuardInfo guardInfo = new SchoolGuardInfo();

    int homeE2cSn, schoolE2cSn;
    boolean homeEfenceReady = true;
    boolean schoolEfenceReady = true;
    int saveStatus = 0; //0 未改动 1 有改动未保存  2 已保存

    String familySsid = "", familyBssid = "";

    GuardHandler checkHandler;

    ImibabyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_guardian);
        myApp = (ImibabyApp) getApplication();
        checkHandler = new GuardHandler(this);
        initViews();
        guardToggleStatus(false);
        homeEfence = addDefaultSecurity("EFID1");
        schoolEfence = addDefaultSecurity("EFID2");
        getListItemFromCloudBridge();
        getGuardListFromCloud();
        reqFamilyWifi();

        //1:隐私权限申请
//        PermissionUtils.checkInitPermission(myApp, this);
    }

    private void initViews() {
        iv_back = findViewById(R.id.iv_title_back);
        iv_back.setOnClickListener(this);
        iv_save = findViewById(R.id.iv_title_menu);
        iv_save.setBackgroundResource(R.drawable.schedule_save);
        iv_save.setOnClickListener(this);

        guard_enable = findViewById(R.id.guard_enable);
        guard_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        guard_enable.setChecked(!b);
                        return;
                    }
                    if (b) {
                        showGuardSwitchOnDlg();
                    } else {
                        guardInfo.onoff = "0";
                        guardToggleStatus(false);
                        saveStatus = 1;
                    }
                }
            }
        });
        home_address_ly = findViewById(R.id.home_address_ly);
        home_address_ly.setOnClickListener(this);
        school_address_ly = findViewById(R.id.school_address_ly);
        school_address_ly.setOnClickListener(this);
        arrive_time_ly = findViewById(R.id.arrive_time_ly);
        arrive_time_ly.setOnClickListener(this);
        leave_time_ly = findViewById(R.id.leave_time_ly);
        leave_time_ly.setOnClickListener(this);
        repeat_ly = findViewById(R.id.repeat_ly);
        repeat_ly.setOnClickListener(this);
        festival_enable = findViewById(R.id.festival_enable);
        festival_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        festival_enable.setChecked(!b);
                        return;
                    }
                    guardInfo.holiday_onoff = b ? "1" : "0";
                    saveStatus = 1;
                }
            }
        });

        home_wifi_ly = findViewById(R.id.home_wifi_ly);
        home_wifi_ly.setOnClickListener(this);
        home_wifi = findViewById(R.id.home_wifi);
        home_wifi_title = findViewById(R.id.home_wifi_title);

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.guard_school));
        home_address = findViewById(R.id.home_address);
        school_address = findViewById(R.id.school_address);
        arrive_time = findViewById(R.id.arrive_time);
        leave_time = findViewById(R.id.leave_time);
        repeat_days = findViewById(R.id.repeat_days);
        arrive_time_inteval = findViewById(R.id.arrive_time_inteval);
        leave_time_inteval = findViewById(R.id.leave_time_inteval);
        school_address_title = findViewById(R.id.school_address_title);
        home_address_title = findViewById(R.id.home_address_title);
        arrive_time_title = findViewById(R.id.arrive_time_title);
        leave_time_title = findViewById(R.id.leave_time_title);
        arrive_inteval_lab = findViewById(R.id.arrive_inteval_lab);
        leave_inteval_lab = findViewById(R.id.leave_inteval_lab);
        repeat_lab = findViewById(R.id.repeat_lab);
        fetival_lab = findViewById(R.id.fetival_lab);

        if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
            iv_save.setVisibility(View.INVISIBLE);
        } else {
            iv_save.setVisibility(View.VISIBLE);
        }


        waitingDialog = new WaitingDialog(this, R.style.Theme_DataSheet);
        waitingDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_title_back:
                if (isDiffWithCloud()) {
                    DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_not_save),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }, getString(R.string.confirm),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, getString(R.string.cancel)).show();
                } else {
                    finish();
                }
                break;
            case R.id.iv_title_menu:
                if (!isDiffWithCloud()) {
                    finish();
                    return;
                }

                if (checkInfoBeforeUpload() < 0) {
                    return;
                }
                //上传 上下学守护 消息
                waitingDialog.setWaitingText(getString(R.string.repair_upload_data));
                waitingDialog.show();
                if (homeEfenceReady && schoolEfenceReady) {
                    sendGuardListToCloud();
                } else {
                    ToastUtil.show(getApplicationContext(), getString(R.string.guard_school_set_failed_tips));
                }
                break;
            case R.id.home_address_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                //设置家安全区域
                Intent intent_home = new Intent();
                intent_home.setClass(SchoolGuardianActivity.this, SecurityZoneSettingGoogle.class);
                Bundle bundle = new Bundle();
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_NAME, homeEfence.sName);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_RADIUS,
                        String.valueOf(homeEfence.sRadius));
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER, homeEfence.sCenter);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD, homeEfence.sCenterBD);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_ONOFF, homeEfence.onOff);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_EFID, homeEfence.keyEFID);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO, homeEfence.info);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW, homeEfence.preview);
                intent_home.putExtra(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
                intent_home.putExtra("inzone", bundle);
                startActivityForResult(intent_home, SECURITY_SETTING_HOME_REQ);
                home_address.setTextColor(getResources().getColor(R.color.ring_textcolor));
                break;
            case R.id.school_address_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                //设置学校安全区域
                Intent intent_school = new Intent();
                intent_school.setClass(SchoolGuardianActivity.this, SecurityZoneSettingGoogle.class);
                Bundle bundle_school = new Bundle();
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_NAME, schoolEfence.sName);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_RADIUS, String.valueOf(schoolEfence.sRadius));
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER, schoolEfence.sCenter);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD, schoolEfence.sCenterBD);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_ONOFF, schoolEfence.onOff);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_EFID, schoolEfence.keyEFID);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO, schoolEfence.info);
                bundle_school.putString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW, schoolEfence.preview);
                intent_school.putExtra(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
                intent_school.putExtra("inzone", bundle_school);
                startActivityForResult(intent_school, SECURITY_SETTING_SCHOOL_REQ);
                school_address.setTextColor(getResources().getColor(R.color.ring_textcolor));
                break;
            case R.id.arrive_time_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                if (arrive_time_select_view != null && arrive_time_select_view.getVisibility() == View.VISIBLE) {
                    return;
                }
                //设置到校时间
                arrive_time_select_view = SelectTimeUtils.selectGuardTimeView(SchoolGuardianActivity.this, guardInfo.arriveT_hour, guardInfo.arriveT_min, 1,
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                if (!text.equals(guardInfo.arriveT_hour)) {
                                    saveStatus = 1;
                                }
                                guardInfo.arriveT_hour = text;
                                arrive_time.setText(getString(R.string.guard_school_time_format, guardInfo.arriveT_hour, guardInfo.arriveT_min));
                                calculateArriveTime();
                            }
                        },
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                if (!text.equals(guardInfo.arriveT_min)) {
                                    saveStatus = 1;
                                }
                                guardInfo.arriveT_min = text;
                                arrive_time.setText(getString(R.string.guard_school_time_format, guardInfo.arriveT_hour, guardInfo.arriveT_min));
                                calculateArriveTime();
                            }
                        });
                arrive_time.setTextColor(getResources().getColor(R.color.color_low_content));
                break;
            case R.id.leave_time_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                if (leave_time_select_view != null && leave_time_select_view.getVisibility() == View.VISIBLE) {
                    return;
                }
                //设置离校时间
                leave_time_select_view = SelectTimeUtils.selectGuardTimeView(SchoolGuardianActivity.this, guardInfo.leaveT_hour, guardInfo.leaveT_min, 2,
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                if (!text.equals(guardInfo.leaveT_hour)) {
                                    saveStatus = 1;
                                }
                                guardInfo.leaveT_hour = text;
                                leave_time.setText(getString(R.string.guard_school_time_format, guardInfo.leaveT_hour, guardInfo.leaveT_min));
                                calculateLeaveTime();
                            }
                        },
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                if (!text.equals(guardInfo.leaveT_min)) {
                                    saveStatus = 1;
                                }
                                guardInfo.leaveT_min = text;
                                leave_time.setText(getString(R.string.guard_school_time_format, guardInfo.leaveT_hour, guardInfo.leaveT_min));
                                calculateLeaveTime();
                            }
                        });
                leave_time.setTextColor(getResources().getColor(R.color.color_low_content));
                break;
            case R.id.repeat_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                //设置重复
                openSelectWeekDialog();
                repeat_days.setTextColor(getResources().getColor(R.color.color_low_content));
                break;
            case R.id.home_wifi_ly:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
//                Intent intent = new Intent(SchoolGuardianActivity.this,
//                        HomeWiFiSettingActivity.class);
//                intent.putExtra("ssid", familySsid == null ? "" : familySsid);
//                intent.putExtra("bssid", familyBssid == null ? "" : familyBssid);
//                startActivityForResult(intent, SECURITY_SETTING_HOMESETTING_REQ);
                ToastUtil.show(this, "暂无法设置家庭wifi");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1 || resultCode == 2 || resultCode == 3) {
            Bundle bundle = data.getBundleExtra("outzone");
            int sRadius = Integer.parseInt(bundle.getString(CloudBridgeUtil.SECURITY_ZONE_RADIUS));
            String sCenter = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_CENTER);
            String sName = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_NAME);
            String info = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO);
            String preview = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW);
            String sCenterBD = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD);
            if (requestCode == SECURITY_SETTING_HOME_REQ) {
                saveStatus = 1;
                homeEfence.sRadius = sRadius;
                homeEfence.sCenter = sCenter;
                homeEfence.sName = sName;
                homeEfence.info = info;
                homeEfence.preview = preview;
                homeEfence.sCenterBD = sCenterBD;
                home_address.setText(homeEfence.info);
                familySsid = data.getStringExtra("ssid");
                familyBssid = data.getStringExtra("bssid");
                home_wifi.setText(StrUtil.isNotBlank(familySsid) ? familySsid : getString(R.string.family_wifi_null_tip));
                waitingDialog.show();
                homeEfenceReady = false;
                homeE2cSn = sendPicToServer(homeEfence.preview, picIdFormat(homeEfence.sCenter));
            } else if (requestCode == SECURITY_SETTING_SCHOOL_REQ) {
                saveStatus = 1;
                schoolEfence.sRadius = sRadius;
                schoolEfence.sCenter = sCenter;
                schoolEfence.sName = sName;
                schoolEfence.info = info;
                schoolEfence.preview = preview;
                schoolEfence.sCenterBD = sCenterBD;
                school_address.setText(schoolEfence.info);
                waitingDialog.show();
                schoolEfenceReady = false;
                schoolE2cSn = sendPicToServer(schoolEfence.preview, picIdFormat(schoolEfence.sCenter));
            } else if (requestCode == SECURITY_SETTING_HOMESETTING_REQ) {
                saveStatus = 1;
                familySsid = data.getStringExtra("ssid");
                familyBssid = data.getStringExtra("bssid");
                home_wifi.setText(StrUtil.isNotBlank(familySsid) ? familySsid : getString(R.string.family_wifi_null_tip));
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == SECURITY_SETTING_HOMESETTING_REQ) {
                saveStatus = 1;
                familySsid = data.getStringExtra("ssid");
                familyBssid = data.getStringExtra("bssid");
                home_wifi.setText(StrUtil.isNotBlank(familySsid) ? familySsid : getString(R.string.family_wifi_null_tip));
            }
        } else if (requestCode == 1024) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // 授权成功
                    if (PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                        myApp.initFileDirs();
                    } else {
                        if (PermissionUtils.hasRefusedPermissions(this, PermissionUtils.storagePermissions)) {
                            PermissionUtils.showPermissionPromptDialog(myApp, getString(R.string.need_storage_new), new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, true);
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    PermissionUtils.getNoGrantedPermissions(this, PermissionUtils.storagePermissions), Constants.PERMISSION_RESULT_INIT);
                        }
                    }
                } else {
                    // 授权失败
                    ToastUtil.show(this, "未授权成功");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Constants.PERMISSION_RESULT_INIT == requestCode) {
            if (PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                myApp.initFileDirs();
            } else {
                PermissionUtils.showPermissionPromptDialog(myApp, getString(R.string.need_storage_new), new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (saveStatus == 1) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_not_save),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }, getString(R.string.confirm),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.cancel)).show();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveStatus = 0;
        if (checkHandler != null) {
            checkHandler.removeCallbacksAndMessages(null);
            checkHandler = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private SecurityZone addDefaultSecurity(String efid) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        SecurityZone securityZone = new SecurityZone();
        if (efid.equals("EFID1")) {
            securityZone.sName = getResources().getString(R.string.security_zone_home);
            securityZone.sCenter = getResources().getString(R.string.security_zone_default_home);
            securityZone.sCenterBD = getResources().getString(R.string.security_zone_default_home);
        } else if (efid.equals("EFID2")) {
            securityZone.sName = getResources().getString(R.string.security_zone_school);
            securityZone.sCenter = getResources().getString(R.string.security_zone_default_school);
            securityZone.sCenterBD =
                    getResources().getString(R.string.security_zone_default_school);
        }
        securityZone.sRadius = 500;
        securityZone.onOff = "0";
        securityZone.keyEFID = efid;
        securityZone.info = getResources().getString(R.string.security_zone_default_info);
        securityZone.preview = getResources().getString(R.string.security_default);
        return securityZone;
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:27
     * 方法描述：从网络端获取到安全区域的同步数据。
     */
    private void getListItemFromCloudBridge() {

        MyMsgData eFenceMsg = new MyMsgData();
        eFenceMsg.setCallback(SchoolGuardianActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
        eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_GET,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                myApp.getToken(), pl));

        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(eFenceMsg);
        }
    }

    private SecurityZone findEfenceFromListByEFID(String efid, JSONObject pl) {
        boolean hasfind = false;
        SecurityZone zone = new SecurityZone();
        for (Map.Entry<String, Object> entry : pl.entrySet()) {
            String key = entry.getKey();
            //初始化一个安全区域的结构体。
            JSONObject zone_content = (JSONObject) entry.getValue();
            if (key.equals(efid)) {
                hasfind = true;
                if (key.equals("EFID1")) {
                    zone.sName = getString(R.string.security_zone_home);
                } else if (key.equals("EFID2")) {
                    zone.sName = getString(R.string.security_zone_school);
                } else {
                    zone.sName = (String) zone_content.get(CloudBridgeUtil.KEY_NAME_NAME);
                }
                zone.sRadius = (Integer) zone_content.get(CloudBridgeUtil.KEY_NAME_EFID_RADIUS);
                zone.onOff = "1";
                zone.keyEFID = key;
                zone.info = (String) zone_content.get(CloudBridgeUtil.KEY_NAME_EFID_DESC);
                Double lat = (Double) zone_content.get(CloudBridgeUtil.KEY_NAME_LAT);
                Double lng = (Double) zone_content.get(CloudBridgeUtil.KEY_NAME_LNG);
                LatLng latlng = new LatLng(lat, lng);
                zone.sCenter = latlng.toString();
                Double latbd = (Double) zone_content.get(CloudBridgeUtil.KEY_NAME_LATBD);
                Double lngbd = (Double) zone_content.get(CloudBridgeUtil.KEY_NAME_LNGBD);
                if (latbd == null || lngbd == null) {
                    LatLng pos = latlng;
                    zone.sCenterBD =
                            "lat/lng:(" + pos.latitude + "," + pos.longitude + ")";
                } else {
                    LatLng latlngbd =
                            new LatLng(latbd, lngbd);
                    zone.sCenterBD =
                            "lat/lng:(" + latlngbd.latitude + "," + latlngbd.longitude + ")";
                }
                zone.preview = picIdForMatForLoad(lat.toString() + lng.toString());
            }
        }
        return (hasfind ? zone : null);
    }

    private void openSelectWeekDialog() {

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(getText(R.string.week_fn_1).toString());
        itemList.add(getText(R.string.week_fn_2).toString());
        itemList.add(getText(R.string.week_fn_3).toString());
        itemList.add(getText(R.string.week_fn_4).toString());
        itemList.add(getText(R.string.week_fn_5).toString());
        itemList.add(getText(R.string.week_fn_6).toString());
        itemList.add(getText(R.string.week_fn_7).toString());

        Dialog dlg = CustomSelectDialogUtil.CustomItemMultSelectDialogSilence(SchoolGuardianActivity.this, itemList, getText(R.string.device_alarm_reset).toString(),
                guardInfo.days,
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                        String days_temp = text.replace(",", "");
                        ;
                        if (days_temp.equals(guardInfo.days)) {
                            saveStatus = 1;
                        }
                        guardInfo.days = days_temp;
//                        if (days_temp.equals("0000000")) {
//                            ToastUtil.show(getApplicationContext(), getString(R.string.guard_school_set_err_5));
//                        }
                        updateWeekView();
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void sendGuardListToCloud() {
        MyMsgData msg = new MyMsgData();
        msg.setCallback(this);
        msg.setTimeout(10000);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, myApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, myApp.getCurUser().getFocusWatch().getEid());
        JSONObject guardlist = new JSONObject();
        String arrive_time = guardInfo.arriveT_hour + ":" + guardInfo.arriveT_min;
        guardlist.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_ARRIVETIME, arrive_time);
        String leave_time = guardInfo.leaveT_hour + ":" + guardInfo.leaveT_min;
        guardlist.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LEAVETIME, leave_time);
        guardlist.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_ARRIVETIME_INTEVAL, SCHOOL_ARRIVE_TIME_INTEVAL);
        guardlist.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LEAVETIME_INTEVAL, SCHOOL_LEAVE_TIME_INTEVAL);
        guardlist.put(CloudBridgeUtil.DAYS, guardInfo.days);
        guardlist.put(CloudBridgeUtil.ONOFF, guardInfo.onoff);
        guardlist.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_HOLIDAY_ONOFF, guardInfo.holiday_onoff);
        pl.put(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LIST, guardlist.toJSONString());
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        msg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(msg);
        }
    }

    private void getGuardListFromCloud() {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(this);

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LIST);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(getMyApp().obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()) {
            getMyApp().getNetService().sendNetMsg(mapget);
        }
    }

    private int sendPicToServer(String fileName, String picId) {
        int headE2cSn = 0;
        File cropTemp = new File(fileName);
        try {
            byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);
            byte[] tmp = AESUtil.getInstance().decrypt(bitmapArray);
            byte[] headBitmapBytes = tmp;
            if (headBitmapBytes != null) {
                headE2cSn = sendPreviewImageE2c(myApp.getCurUser().getFocusWatch().getEid(), picId, headBitmapBytes);
            } else {
                LogUtil.i("操作错误" + "  " + "没有数据。");
            }
        } catch (FileNotFoundException e) {
            LogUtil.e("图片数据失败：" + "  " + e.toString());
            myApp.sdcardLog("securityzone Activity1:" + e.toString());
        } catch (Exception e) {
            LogUtil.e("异常操作：" + "  " + e.toString());
            myApp.sdcardLog("securityzone Activity2:" + e.toString());
        }
        return headE2cSn;
    }

    private int sendPreviewImageE2c(String eid, String PicId, byte[] mapBytes) {
        MyMsgData e2c = new MyMsgData();
        int sn;
        e2c.setCallback(SchoolGuardianActivity.this);
        JSONObject pl = new JSONObject();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_SECURITYPREVIEW);
        key.append(PicId);
        JSONObject chat = new JSONObject();
        String baseData = Base64.encodeToString(mapBytes, Base64.NO_WRAP);
        LogUtil.i("securityzone:" + "  " + "" + baseData.length());

        chat.put(CloudBridgeUtil.SECURITY_ZONE_PREVIEW_DATA, Base64.encodeToString(mapBytes, Base64.NO_WRAP));
        pl.put(key.toString(), chat);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C_UP,
                sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2c);
        return sn;
    }

    private void sendWatchEFenceMsg(SecurityZone securityZone, String info, String onoff) {
        MyMsgData eFenceMsg = new MyMsgData();
        eFenceMsg.setCallback(SchoolGuardianActivity.this);
        JSONObject pl = new JSONObject();

        if (securityZone.keyEFID.equals("EFID1")) {
            if (!securityZone.sName.equals(getResources().getString(R.string.security_zone_home))) {
                securityZone.sName = getResources().getString(R.string.security_zone_home);
            }
        }

        if (onoff.equals("1")) {
            JSONObject efid = new JSONObject();
            double lat = 0.0;
            double lng = 0.0;
            //if(myApp.getIntValue(Const.SHARE_PREF_FIELD_CHANEG_MAP,1) == 2)
            {
                lat = Double.parseDouble(securityZone.sCenter.substring(securityZone.sCenter.indexOf("(") + 1, securityZone.sCenter.indexOf(",")));
                lng = Double.parseDouble(securityZone.sCenter.substring(securityZone.sCenter.indexOf(",") + 1, securityZone.sCenter.indexOf(")")));
                efid.put(CloudBridgeUtil.SECURITY_ZONE_COORDINATETYPE, "1");
            }

            efid.put(CloudBridgeUtil.KEY_NAME_NAME, securityZone.sName);
            efid.put(CloudBridgeUtil.KEY_NAME_EFID_DESC, info);
            efid.put(CloudBridgeUtil.KEY_NAME_LAT, lat);
            efid.put(CloudBridgeUtil.KEY_NAME_LNG, lng);
            efid.put(CloudBridgeUtil.KEY_NAME_EFID_RADIUS, (securityZone.sRadius));
            pl.put(securityZone.keyEFID, efid);
            pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());

            eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_SET,
                    Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                    myApp.getToken(), pl));
        } else if (onoff.equals("0")) {
            JSONArray keyList = new JSONArray();
            keyList.add(securityZone.keyEFID);
            pl.put(CloudBridgeUtil.KEY_NAME_EFID, keyList);
            pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());

            eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_DEL,
                    Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                    myApp.getToken(), pl));
        }

        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(eFenceMsg);
        }
    }

    private void reqFamilyWifi() {
        WatchWifiUtils.getFamilyWifi(SchoolGuardianActivity.this, myApp.getCurUser().getFocusWatch().getEid(), myApp.getNetService(), new WatchWifiUtils.OperationCallback() {
            @Override
            public void onSuccess(Object result) {

                try {
                    JSONObject respMsg = (JSONObject) result;
                    familySsid = (String) ((JSONObject) ((JSONArray) respMsg.get("WIFIS")).get(0)).get("ssid");
                    familyBssid = (String) ((JSONObject) ((JSONArray) respMsg.get("WIFIS")).get(0)).get("bssid");
//                    getMyApp().setValue(HomeWiFiSettingActivity.HOMEWIFI_SSID, familySsid);
//                    getMyApp().setValue(HomeWiFiSettingActivity.HOMEWIFI_BSSID, familyBssid);
                } catch (Exception e) {

                }
                home_wifi.setText(StrUtil.isNotBlank(familySsid) ? familySsid : getString(R.string.family_wifi_null_tip));
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_EFENCE_GET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl != null && pl.size() > 0) {
                        homeEfence = findEfenceFromListByEFID("EFID1", pl);
                        schoolEfence = findEfenceFromListByEFID("EFID2", pl);
                        if (homeEfence == null) {
                            homeEfence = addDefaultSecurity("EFID1");
                        } else {
                            home_address.setText(homeEfence.info);
                        }
                        if (schoolEfence == null) {
                            schoolEfence = addDefaultSecurity("EFID2");
                        } else {
                            school_address.setText(schoolEfence.info);
                        }
                        initListFormCloud(pl);
                    }
                } else {
                    ToastUtil.show(this, "get efence failed.cause : " + rc);
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (pl != null && !pl.isEmpty()) {
                    String gl = (String) pl.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LIST);
                    if (gl != null && !gl.equals("")) {
                        JSONObject guardlist = (JSONObject) JSONValue.parse(gl);
                        //arrive time
                        String arrive_time = (String) guardlist.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_ARRIVETIME);
                        String[] at = arrive_time.split(":");
                        guardInfo.arriveT_hour = at[0];
                        guardInfo.arriveT_min = at[1];
                        //leave time
                        String leave_time = (String) guardlist.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LEAVETIME);
                        String[] lt = leave_time.split(":");
                        guardInfo.leaveT_hour = lt[0];
                        guardInfo.leaveT_min = lt[1];
                        //arrive time Inteval
                        guardInfo.arrieT_inteval = (String) guardlist.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_ARRIVETIME_INTEVAL);
                        //leave time inteval
                        guardInfo.leaveT_inteval = (String) guardlist.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LEAVETIME_INTEVAL);
                        //days
                        guardInfo.days = (String) guardlist.get(CloudBridgeUtil.DAYS);
                        //holiday
                        guardInfo.holiday_onoff = (String) guardlist.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_HOLIDAY_ONOFF);
                        //onoff
                        guardInfo.onoff = (String) guardlist.get(CloudBridgeUtil.ONOFF);

                        guardInfoInServer.copy(guardInfo);

                        updateSchoolGuardView();
                    } else {
                        guardToggleStatus(false);
                    }
                }
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    LogUtil.e("Guard mapset succ.");
                    saveStatus = 2;
                    ToastUtil.show(SchoolGuardianActivity.this, getString(R.string.phone_set_success));
                    waitingDialog.dismiss();
                    finish();
                }
                break;
            case CloudBridgeUtil.CID_E2C_DOWN:   //上传数据图片
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    if (CloudBridgeUtil.getCloudMsgSN(respMsg) == homeE2cSn) {
                        sendWatchEFenceMsg(homeEfence, homeEfence.info, "1");
                        LogUtil.i("home efence img upload succ");
                    } else if (CloudBridgeUtil.getCloudMsgSN(respMsg) == schoolE2cSn) {
                        sendWatchEFenceMsg(schoolEfence, schoolEfence.info, "1");
                        LogUtil.i("school efence img upload succ");
                    }
                } else {
                    LogUtil.i("efence img upload failed");
                }
                break;
            case CloudBridgeUtil.CID_EFENCE_SET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject setPl = (JSONObject) reqMsg.get("PL");
                    int efenceType = -1;    //区分是家还是学校 0 家 1 学校
                    for (String key : setPl.keySet()) {
                        if (key.equals("EFID1")) {
                            efenceType = 0;
                            break;
                        }
                        if (key.equals("EFID2")) {
                            efenceType = 1;
                            break;
                        }
                    }
                    if (efenceType == 0) {
                        LogUtil.e("home efence set succ.");
                        homeEfence.onOff = "1";
                        updateEfenceItem(homeEfence);
                        homeEfenceReady = true;
                    } else if (efenceType == 1) {
                        LogUtil.e("school efence set succ.");
                        schoolEfence.onOff = "1";
                        updateEfenceItem(schoolEfence);
                        schoolEfenceReady = true;
                    }
                    ToastUtil.showInThread(getApplicationContext(), getString(R.string.guard_setting_efence_succ_tips));
                } else {
                    ToastUtil.showInThread(getApplicationContext(), getString(R.string.guard_setting_efence_failed_tips));
                }
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                break;
        }
    }

    private void updateSchoolGuardView() {
        arrive_time.setText(getString(R.string.guard_school_time_format, guardInfo.arriveT_hour, guardInfo.arriveT_min));
        leave_time.setText(getString(R.string.guard_school_time_format, guardInfo.leaveT_hour, guardInfo.leaveT_min));

        //计算到校时间段
        calculateArriveTime();

        //计算离校时间段
        calculateLeaveTime();

        //重复日期
        updateWeekView();

        //节假日开关
        festival_enable.setChecked(guardInfo.holiday_onoff.equals("1"));

        //开关
        guard_enable.setChecked(guardInfo.onoff.equals("1"));
        guardToggleStatus(guard_enable.isChecked());
        saveStatus = 0;
    }

    private void calculateArriveTime() {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String[] ai = guardInfo.arrieT_inteval.split(",");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.arriveT_hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(guardInfo.arriveT_min));
        cal.add(Calendar.MINUTE, -Integer.parseInt(ai[0]));
        String arr1_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String arr1_m = decimalFormat.format(cal.get(Calendar.MINUTE));

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.arriveT_hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(guardInfo.arriveT_min));
        cal.add(Calendar.MINUTE, Integer.parseInt(ai[1]));
        String arr2_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String arr2_m = decimalFormat.format(cal.get(Calendar.MINUTE));
        arrive_time_inteval.setText(getString(R.string.guard_school_time_inteval_format, arr1_h, arr1_m, arr2_h, arr2_m));
    }

    private void calculateLeaveTime() {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String[] li = guardInfo.leaveT_inteval.split(",");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.leaveT_hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(guardInfo.leaveT_min));
        cal.add(Calendar.MINUTE, -Integer.parseInt(li[0]));
        String lea1_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String lea1_m = decimalFormat.format(cal.get(Calendar.MINUTE));

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.leaveT_hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(guardInfo.leaveT_min));
        cal.add(Calendar.MINUTE, Integer.parseInt(li[1]));
        String lea2_h = decimalFormat.format(cal.get(Calendar.HOUR_OF_DAY));
        String lea2_m = decimalFormat.format(cal.get(Calendar.MINUTE));
        leave_time_inteval.setText(getString(R.string.guard_school_time_inteval_format, lea1_h, lea1_m, lea2_h, lea2_m));
    }

    private void updateWeekView() {
        switch (guardInfo.days) {
            case "1111111":
                repeat_days.setText(getText(R.string.device_alarm_reset_3));
                break;
            case "1111100":
                repeat_days.setText(getText(R.string.device_alarm_reset_2));
                break;
            case "0000000":
                repeat_days.setText(getText(R.string.silence_week_click_select));
                break;
            default:
                repeat_days.setText((guardInfo.days.substring(0, 1).equals("1") ? " " + getText(R.string.week_1) : "")
                        + (guardInfo.days.substring(1, 2).equals("1") ? " " + getText(R.string.week_2) : "")
                        + (guardInfo.days.substring(2, 3).equals("1") ? " " + getText(R.string.week_3) : "")
                        + (guardInfo.days.substring(3, 4).equals("1") ? " " + getText(R.string.week_4) : "")
                        + (guardInfo.days.substring(4, 5).equals("1") ? " " + getText(R.string.week_5) : "")
                        + (guardInfo.days.substring(5, 6).equals("1") ? " " + getText(R.string.week_6) : "")
                        + (guardInfo.days.substring(6, 7).equals("1") ? " " + getText(R.string.week_0) : ""));
                break;
        }
    }

    private int checkInfoBeforeUpload() {
        int ret = 0;
        if (homeEfence.sCenter.equals(getString(R.string.security_zone_default_home))
                && schoolEfence.sCenter.equals(getString(R.string.security_zone_default_school))) {
            ret = ERROR_CODE_NO_ADDRESS;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }
        if (homeEfence.sCenter.equals(getString(R.string.security_zone_default_home))) {
            ret = ERROR_CODE_NO_HOME_ADDRESS;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }

        if (schoolEfence.sCenter.equals(getString(R.string.security_zone_default_school))) {
            ret = ERROR_CODE_NO_SCHOOL_ADDRESS;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }

        Calendar cal_arr = Calendar.getInstance();
        Calendar cal_lea = Calendar.getInstance();
        cal_arr.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.arriveT_hour));
        cal_arr.set(Calendar.MINUTE, Integer.parseInt(guardInfo.arriveT_min));
        cal_lea.set(Calendar.HOUR_OF_DAY, Integer.parseInt(guardInfo.leaveT_hour));
        cal_lea.set(Calendar.MINUTE, Integer.parseInt(guardInfo.leaveT_min));
        if (cal_arr.getTimeInMillis() >= cal_lea.getTimeInMillis()) {
            ret = ERROR_CODE_LEAVE_TIME_WRONG;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }
        cal_arr.add(Calendar.MINUTE, 30);
        if (cal_arr.getTimeInMillis() > cal_lea.getTimeInMillis()) {
            ret = ERROR_CODE_TIME_NERALY;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }

        if (guardInfo.days.equals("0000000")) {
            ret = ERROR_CODE_REPEAT_NO_SEL;
            promptErrView(ret);
            showErrDialog(ret);
            return ret;
        }
        return ret;
    }

    private void showErrDialog(int err) {
        if (err == ERROR_CODE_NO_HOME_ADDRESS) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_1),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else if (err == ERROR_CODE_NO_SCHOOL_ADDRESS) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_2),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else if (err == ERROR_CODE_LEAVE_TIME_WRONG) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_3),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else if (err == ERROR_CODE_TIME_NERALY) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_4),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else if (err == ERROR_CODE_REPEAT_NO_SEL) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_5),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else if (err == ERROR_CODE_NO_ADDRESS) {
            DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_school_set_err_6),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.confirm)).show();
        } else {
            LogUtil.e("it's alright");
        }
    }

    private void promptErrView(int cause) {
        switch (cause) {
            case ERROR_CODE_NO_HOME_ADDRESS:
                home_address.setTextColor(getResources().getColor(R.color.warining_red));
                break;
            case ERROR_CODE_NO_SCHOOL_ADDRESS:
                school_address.setTextColor(getResources().getColor(R.color.warining_red));
                break;
            case ERROR_CODE_LEAVE_TIME_WRONG:
            case ERROR_CODE_TIME_NERALY:
                arrive_time.setTextColor(getResources().getColor(R.color.warining_red));
                leave_time.setTextColor(getResources().getColor(R.color.warining_red));
                break;
            case ERROR_CODE_REPEAT_NO_SEL:
                repeat_days.setTextColor(getResources().getColor(R.color.warining_red));
                break;
            case ERROR_CODE_NO_ADDRESS:
                home_address.setTextColor(getResources().getColor(R.color.warining_red));
                school_address.setTextColor(getResources().getColor(R.color.warining_red));
                break;
        }
    }

    private void guardToggleStatus(boolean status) {
        if (status) {
            home_address.setTextColor(getResources().getColor(R.color.ring_textcolor));
            school_address.setTextColor(getResources().getColor(R.color.ring_textcolor));
            arrive_time.setTextColor(getResources().getColor(R.color.color_low_content));
            leave_time.setTextColor(getResources().getColor(R.color.color_low_content));
            repeat_days.setTextColor(getResources().getColor(R.color.color_low_content));
            arrive_time_inteval.setTextColor(getResources().getColor(R.color.guard_time_text_color));
            leave_time_inteval.setTextColor(getResources().getColor(R.color.guard_time_text_color));
            home_address_title.setTextColor(getResources().getColor(R.color.color_tile_content));
            school_address_title.setTextColor(getResources().getColor(R.color.color_tile_content));
            arrive_time_title.setTextColor(getResources().getColor(R.color.color_tile_content));
            leave_time_title.setTextColor(getResources().getColor(R.color.color_tile_content));
            arrive_inteval_lab.setTextColor(getResources().getColor(R.color.guard_time_text_color));
            leave_inteval_lab.setTextColor(getResources().getColor(R.color.guard_time_text_color));
            repeat_lab.setTextColor(getResources().getColor(R.color.color_tile_content));
            fetival_lab.setTextColor(getResources().getColor(R.color.color_tile_content));
            home_wifi.setTextColor(getResources().getColor(R.color.ring_textcolor));
            home_wifi_title.setTextColor(getResources().getColor(R.color.color_tile_content));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                festival_enable.setBackgroundDrawable(getDrawable(R.drawable.toggle_selector));
            }
            if (guardInfo.holiday_onoff.equals("1")) {
                festival_enable.setChecked(true);
            }

            home_address_ly.setClickable(true);
            school_address_ly.setClickable(true);
            arrive_time_ly.setClickable(true);
            leave_time_ly.setClickable(true);
            repeat_ly.setClickable(true);
            festival_enable.setClickable(true);
            home_wifi_ly.setClickable(true);
        } else {
            home_address.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            school_address.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            arrive_time.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            leave_time.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            repeat_days.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            arrive_time_inteval.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            leave_time_inteval.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            home_address_title.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            school_address_title.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            arrive_time_title.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            leave_time_title.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            arrive_inteval_lab.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            leave_inteval_lab.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            repeat_lab.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            fetival_lab.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            home_wifi.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            home_wifi_title.setTextColor(getResources().getColor(R.color.alipay_aaaaaa));
            if (guardInfo.holiday_onoff.equals("1")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    festival_enable.setBackgroundDrawable(getDrawable(R.drawable.switch_on_grey));
                }
            }

            home_address_ly.setClickable(false);
            school_address_ly.setClickable(false);
            arrive_time_ly.setClickable(false);
            leave_time_ly.setClickable(false);
            repeat_ly.setClickable(false);
            festival_enable.setClickable(false);
            home_wifi_ly.setClickable(false);
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/25 16:32
     * 方法描述：标准化上传文件名。
     */
    private String picIdFormat(String source) {
        String fileid = source;
        fileid = fileid.replace("lat/lng:", "");
        fileid = fileid.replace(" ", "");
        fileid = fileid.replace("(", "");
        fileid = fileid.replace(")", "");
        fileid = fileid.replace(".", "");
        fileid = fileid.replace(",", "");

        return fileid;
    }

    /**
     * 创建人：zhangjun5
     * 创建时间：2015/12/25 16:33
     * 方法描述：兼容ios的lat和lng为小数点后14的文件下载名问题
     */
    private String picIdForMatForLoad(String source) {
        String fileid = source;
        fileid = fileid.replace(" ", "");
        fileid = fileid.replace(".", "");
        fileid = fileid.replace(",", "");

        return fileid;
    }

    ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();

    private void saveListItemToLocalJason() {
        JSONObject pl = new JSONObject();
        JSONArray arr = new JSONArray();
        pl.put("list", arr);
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            JSONObject item = new JSONObject();
            item.put("Name", securityZone.sName);
            item.put("Center_amap", securityZone.sCenter);
            item.put("Radius", securityZone.sRadius);
            item.put("Onoff", securityZone.onOff);
            item.put("Efid", securityZone.keyEFID);
            item.put("Info", securityZone.info);
            item.put("Preview", securityZone.preview);
            item.put("Center_bd", securityZone.sCenterBD);
            item.put("Coordinate", securityZone.sCoordinate);
            arr.add(item);
        }
        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY,
                pl.toJSONString());
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:22
     * 方法描述：把一个安全区域对象转化为adapter的数据对象
     */
    private void setItemAdapterMap(HashMap<String, Object> map, SecurityZone securityZone) {
        if (securityZone.keyEFID.equals("EFID1")) {
            map.put("logImg", R.drawable.green_home_0);
        } else if (securityZone.keyEFID.equals("EFID2")) {
            map.put("logImg", R.drawable.blue_school_0);
        } else {
            map.put("logImg", R.drawable.customizel_2);
        }
        if (securityZone.preview.equals(getResources().getString(R.string.security_default))) {
            map.put("title", securityZone.sCenter);
        } else {
            map.put("title",
                    securityZone.sName + " " + getText(R.string.radius) + securityZone.sRadius + getText(R.string.unit_meter));
        }

        map.put("info", securityZone.info);
        if (securityZone.onOff.equals("1"))
            map.put("img", R.drawable.switch_on);
        else
            map.put("img", R.drawable.switch_off);
        if ("".equals(securityZone.preview) || securityZone.preview.equals(getResources().getString(R.string.security_default))) {
            if (securityZone.sName.equals(this.getResources().getString(R.string.security_zone_home))) {
                map.put("preview", R.drawable.security_default_home);
            } else if (securityZone.sName.equals(this.getResources().getString(R.string.security_zone_school))) {
                map.put("preview", R.drawable.security_default_school);
            } else {
                map.put("preview", R.drawable.security_default);
            }
        } else {
            map.put("preview", securityZone.preview);
        }
        map.put("securityObject", securityZone);
    }

    private void updateEfenceItem(SecurityZone efence) {
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            if (efence.keyEFID.equals(securityZone.keyEFID)) {
                listItem.remove(securityZone);
                break;
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        setItemAdapterMap(map, efence);
        listItem.add(map);
        saveListItemToLocalJason();
    }

    private void initListFormCloud(JSONObject pl) {
        for (Map.Entry<String, Object> entry : pl.entrySet()) {
            String key = entry.getKey();
            //初始化一个安全区域的结构体。
            JSONObject efid = (JSONObject) entry.getValue();
            SecurityZone securityZone = new SecurityZone();
            if (key.equals("EFID1")) {
                securityZone.sName = getString(R.string.security_zone_home);
            } else if (key.equals("EFID2")) {
                securityZone.sName = getString(R.string.security_zone_school);
            } else {
                securityZone.sName =
                        (String) efid.get(CloudBridgeUtil.KEY_NAME_NAME);
            }
            securityZone.sRadius =
                    (Integer) efid.get(CloudBridgeUtil.KEY_NAME_EFID_RADIUS);
            int temp = securityZone.sRadius;
            if (temp >= 500) {
                securityZone.sRadius = 500;
            }
            securityZone.onOff = "1";
            securityZone.keyEFID = key;
            securityZone.info =
                    (String) efid.get(CloudBridgeUtil.KEY_NAME_EFID_DESC);
            if (securityZone.info == null || securityZone.info.equals("")) {
                break;
            }
            Double lat = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LAT);
            Double lng = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LNG);
            LatLng latlng = new LatLng(lat, lng);
            securityZone.sCenter = latlng.toString();

            Double latbd = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LATBD);
            Double lngbd = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LNGBD);
            if (latbd == null || lngbd == null) {
                LatLng pos = latlng;
                securityZone.sCenterBD =
                        "lat/lng:(" + String.valueOf(pos.latitude) + "," + String.valueOf(pos.longitude) + ")";
            } else {
                LatLng latlngbd =
                        new LatLng(latbd, lngbd);
                securityZone.sCenterBD =
                        "lat/lng:(" + String.valueOf(latlngbd.latitude) + "," + String.valueOf(latlngbd.longitude) + ")";
            }
            securityZone.preview = "";
            updateEfenceItem(securityZone);
        }
    }

    private void showGuardSwitchOnDlg() {
        Dialog dlg = DialogUtil.CustomNormalDialog(SchoolGuardianActivity.this, getString(R.string.prompt), getString(R.string.guard_setting_switch_on_tips),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        guard_enable.setChecked(false);
                    }
                }, getString(R.string.cancel),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        guardInfo.onoff = "1";
                        guardToggleStatus(true);
                        saveStatus = 1;
                    }
                }, getString(R.string.confirm));
        dlg.setCancelable(false);
        dlg.show();
    }

    private boolean isDiffWithCloud() {
        if (guardInfo.onoff.equals(guardInfoInServer.onoff) && guardInfo.holiday_onoff.equals(guardInfoInServer.holiday_onoff)
                && guardInfo.leaveT_hour.equals(guardInfoInServer.leaveT_hour) && guardInfo.leaveT_min.equals(guardInfoInServer.leaveT_min)
                && guardInfo.days.equals(guardInfoInServer.days) && guardInfo.arriveT_hour.equals(guardInfoInServer.arriveT_hour)
                && guardInfo.arriveT_min.equals(guardInfoInServer.arriveT_min) && guardInfo.leaveT_inteval.equals(guardInfoInServer.leaveT_inteval)
                && guardInfo.arrieT_inteval.equals(guardInfoInServer.arrieT_inteval)) {
            return false;
        } else {
            return true;
        }
    }

    public static class GuardHandler extends Handler {
        public static final int MSG_CHECK_EFENCE_SET_READY = 1;

        public int checkTimes = 0;

        private WeakReference<SchoolGuardianActivity> weakReference;

        public GuardHandler(SchoolGuardianActivity act) {
            weakReference = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SchoolGuardianActivity act = weakReference.get();
            if (act == null) {
                return;
            }
            if (msg.what == MSG_CHECK_EFENCE_SET_READY) {
                LogUtil.e("GuardHandler : checktimes = " + checkTimes);

            }
        }
    }

    static class SchoolGuardInfo {
        String arriveT_hour;
        String arriveT_min;
        String leaveT_hour;
        String leaveT_min;
        String arrieT_inteval;
        String leaveT_inteval;
        String days;
        String onoff;
        String holiday_onoff;

        public SchoolGuardInfo() {
            arriveT_hour = "8";
            arriveT_min = "00";
            leaveT_hour = "17";
            leaveT_min = "00";
            days = "1111100";
            onoff = "0";
            holiday_onoff = "1";
            arrieT_inteval = "20,10";
            leaveT_inteval = "2,28";
        }

        public void copy(SchoolGuardInfo info) {
            arriveT_hour = info.arriveT_hour;
            arriveT_min = info.arriveT_min;
            leaveT_hour = info.leaveT_hour;
            leaveT_min = info.leaveT_min;
            days = info.days;
            onoff = info.onoff;
            holiday_onoff = info.holiday_onoff;
            arrieT_inteval = info.arrieT_inteval;
            leaveT_inteval = info.leaveT_inteval;
        }
    }
}