package com.xiaoxun.xun.activitys;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.NoticeManagerView;

import net.minidev.json.JSONObject;

public class NoticeManagerActivity extends NormalActivity implements View.OnClickListener {

    private String mWatchEid;
    private WatchData mCurWatch;
    private ImibabyApp mApp;

    private NoticeManagerView mLocationView;
    private NoticeManagerView mBatteryView;
    private NoticeManagerView mStepsView;
    private NoticeManagerView mGroupView;
    private NoticeManagerView mSmsView;
    private NoticeManagerView mDownloadView;
    private NoticeManagerView mSystemView;

    private ImageButton mTitleMenu;

    private JSONObject mNoticeSettiong;

    private static final String LOCATION_TAG = "location";
    private static final String BATTERY_TAG = "battery";
    private static final String STEPS_TAG = "steps";
    private static final String GROUP_TAG = "group";
    private static final String SMS_TAG = "sms";
    private static final String DOWNLOAD_TAG = "download";
    private static final String SYSTEM_TAG = "system";

    private NetService mNetService = null;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mNetService = ((NetService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_manager);
        mApp = (ImibabyApp) getApplication();
        mWatchEid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        mCurWatch = mApp.getCurUser().queryWatchDataByEid(mWatchEid);
        mNoticeSettiong = mApp.getNoticeSetting(mWatchEid);
        if (mNoticeSettiong == null || TextUtils.isEmpty(mNoticeSettiong.toString())) {
            mNoticeSettiong = mApp.initNoticeSetting(mWatchEid);
        }
        initViews();
        initService();
        try {
            updateNoticeSettingView();
        } catch (Exception e) {

        }
    }

    private void initViews() {

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.notice_setting);
        findViewById(R.id.iv_title_back).setBackgroundResource(R.drawable.btn_cancel_selector);
        findViewById(R.id.iv_title_back).setOnClickListener(this);

        mTitleMenu = findViewById(R.id.iv_title_menu);
        mTitleMenu.setVisibility(View.VISIBLE);
        mTitleMenu.setBackgroundResource(R.drawable.btn_confirm_selector);
        mTitleMenu.setOnClickListener(this);

        mLocationView = findViewById(R.id.location_notice_view);
        mLocationView.setOnCkeckListener(LOCATION_TAG, this);

        mBatteryView = findViewById(R.id.battery_notice_view);
        mBatteryView.setOnCkeckListener(BATTERY_TAG, this);

        mStepsView = findViewById(R.id.steps_notice_view);
        mStepsView.setOnCkeckListener(STEPS_TAG, this);

        mGroupView = findViewById(R.id.group_notice_view);
        mGroupView.setOnCkeckListener(GROUP_TAG, this);

        mSmsView = findViewById(R.id.sms_notice_view);
        mSmsView.setOnCkeckListener(SMS_TAG, this);

        mDownloadView = findViewById(R.id.download_notice_view);
        mDownloadView.setOnCkeckListener(DOWNLOAD_TAG, this);

        mSystemView = (NoticeManagerView) findViewById(R.id.system_notice_view);
        mSystemView.setOnCkeckListener(SYSTEM_TAG, this);

        if (mCurWatch.isSupportStepNotice()) {
            mStepsView.setVisibility(View.VISIBLE);
        } else {
            mStepsView.setVisibility(View.GONE);
        }

        if (mCurWatch.isSupportDownloadNotice()) {
            mDownloadView.setVisibility(View.VISIBLE);
        } else {
            mDownloadView.setVisibility(View.GONE);
        }

    }

    private void initService() {
        Intent it = new Intent(NoticeManagerActivity.this, NetService.class);
        bindService(it, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateNoticeSettingView() {
        boolean locationChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION).equals("1");
        mLocationView.setChecked(locationChecked);

        boolean batteryChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY).equals("1");
        mBatteryView.setChecked(batteryChecked);

        boolean stepsChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS).equals("1");
        mStepsView.setChecked(stepsChecked);

        boolean groupChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY).equals("1");
        mGroupView.setChecked(groupChecked);

        boolean smsChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER).equals("1");
        mSmsView.setChecked(smsChecked);

        boolean downloadChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE).equals("1");
        mDownloadView.setChecked(downloadChecked);

        boolean systemChecked = mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE).equals("1") ||
                mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX).equals("1");
        mSystemView.setChecked(systemChecked);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_title_back) {
            doSaveNoticeSetting();
        } else if (v.getId() == R.id.iv_title_menu) {
            JSONObject result = getCurrentNoticeSetting();
            if (!isNoticeSettingChanged(result)) {
                finish();
            } else {
                sendNoticeSetting(result);
            }
        } else {
            String tag = (String) v.getTag();
            if (LOCATION_TAG.equals(tag)) {
                boolean checked = mLocationView.isChecked();
                mLocationView.setChecked(!checked);
            } else if (BATTERY_TAG.equals(tag)) {
                boolean checked = mBatteryView.isChecked();
                mBatteryView.setChecked(!checked);
            } else if (STEPS_TAG.equals(tag)) {
                boolean checked = mStepsView.isChecked();
                mStepsView.setChecked(!checked);
            } else if (GROUP_TAG.equals(tag)) {
                boolean checked = mGroupView.isChecked();
                mGroupView.setChecked(!checked);
            } else if (SMS_TAG.equals(tag)) {
                boolean checked = mSmsView.isChecked();
                mSmsView.setChecked(!checked);
            } else if (SYSTEM_TAG.equals(tag)) {
                boolean checked = mSystemView.isChecked();
                mSystemView.setChecked(!checked);
            } else if (DOWNLOAD_TAG.equals(tag)) {
                boolean checked = mDownloadView.isChecked();
                mDownloadView.setChecked(!checked);
            }
        }
    }

    private void doSaveNoticeSetting() {
        final JSONObject result = getCurrentNoticeSetting();
        if (!isNoticeSettingChanged(result)) {
            finish();
        } else {
            DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                    getString(R.string.prompt),
                    getString(R.string.prompt_not_saved),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    NoticeManagerActivity.this.finish();
                                }
                            }, 500);

                        }
                    },
                    getText(R.string.quit_without_save).toString(),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            sendNoticeSetting(result);
                        }
                    },
                    getText(R.string.save_edit).toString());
        }

    }

    private void sendNoticeSetting(final JSONObject result) {
        if (mNetService != null && mNetService.isCloudBridgeClientOk()) {
            mNetService.setNoticeSetting(mWatchEid, result, new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
                    if (cid == CloudBridgeUtil.CID_SET_NOTICE_SETTING_RESP) {
                        if (rc == CloudBridgeUtil.RC_SUCCESS) {
                            mApp.setNoticeSetting(mWatchEid, result);
                            ToastUtil.showMyToast(NoticeManagerActivity.this, getString(R.string.phone_set_success), Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showMyToast(NoticeManagerActivity.this, getString(R.string.set_error) + rc, Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        } else {
            ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
        }
    }

    private JSONObject getCurrentNoticeSetting() {
        JSONObject json = new JSONObject();
        json.put(CloudBridgeUtil.KEY_NAME_EID, mWatchEid);
        if (mLocationView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION, "0");
        }

        if (mBatteryView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY, "0");
        }

        if (mStepsView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS, "0");
        }

        if (mGroupView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY, "0");
        }

        if (mSmsView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX, "0");
        }

        if (mDownloadView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE, "0");
        }


        if (mSystemView.isChecked()) {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE, "1");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX, "1");
        } else {
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE, "0");
            json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX, "0");
        }


        return json;
    }

    private boolean isNoticeSettingChanged(JSONObject result) {
try {
    return !(((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY))
            && ((String) result.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE)).equals((String) mNoticeSettiong.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE))
    );
} catch (Exception e) {
    return true;
}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public void onBackPressed() {
        doSaveNoticeSetting();
    }
}
