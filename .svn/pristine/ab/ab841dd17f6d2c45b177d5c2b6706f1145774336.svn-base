package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

/**
 * Created by huangyouyang on 2017/9/11.
 */

public class WatchWifiSettingActivity extends NormalActivity implements MsgCallback{

    private ImageButton mBtnBack;
    private TextView mTitle;
    private TextView mTvKeepConnectDesc;
    private ImageButton btnKeepConnect;
    private ImageButton btnAutoConnect;
    private View layoutAutoConnect;
    private View layoutAutoConnectDivider;

    private WatchData mCurWatch;
    private NetService mNetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watch_wifi_setting);
        String eid = getIntent().getStringExtra("eid");
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(eid);
        if (mCurWatch == null)
            mCurWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();

        initView();
//        updateViewShow();
        initData();
        mapgetWifiSettingState();
        initListener();
        updateWifiSettingState();
    }

    private void initView() {

        mBtnBack = findViewById(R.id.iv_title_back);
        mTitle = findViewById(R.id.tv_title);
        btnKeepConnect = findViewById(R.id.btn_keep_connect);
        btnAutoConnect = findViewById(R.id.btn_auto_connect);
        mTvKeepConnectDesc = findViewById(R.id.tv_keep_connect_desc);
        layoutAutoConnect = findViewById(R.id.setting_auto_connect);
        layoutAutoConnectDivider = findViewById(R.id.setting_auto_connect_divider);
    }

    private void updateViewShow() {

        if (mCurWatch.isDevice701() || mCurWatch.isDevice710() || mCurWatch.isDevice703()
                || mCurWatch.isDevice705()) {
            layoutAutoConnect.setVisibility(View.GONE);
            layoutAutoConnectDivider.setVisibility(View.GONE);
        } else {
            layoutAutoConnect.setVisibility(View.GONE);
            layoutAutoConnectDivider.setVisibility(View.GONE);
        }
    }

    private void initData() {

        mTitle.setText(getText(R.string.title_keep_wifilist));
        btnKeepConnect.setImageResource(R.drawable.switch_off);
        btnAutoConnect.setBackgroundResource(R.drawable.switch_off);

//        if (mCurWatch.isDevice710()) {
//            mTvKeepConnectDesc.setText(R.string.keep_wifi_connect_desc_other);
//        } else
//            mTvKeepConnectDesc.setText(R.string.keep_wifi_connect_desc);
    }

    private void mapgetWifiSettingState() {

        String[] keys = new String[2];
        keys[0] =  CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT;
        keys[1] =  CloudBridgeUtil.KEY_AUTO_CONNECT_WIFI;
        if (mNetService != null)
            mNetService.sendMapMGetMsg(mCurWatch.getEid(), keys, this);
    }

    private void updateWifiSettingState() {

        String keepConnectState = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() +Const.SHARE_PREF_KEEP_WIFI_CONNECT, "0");
        if ("0".endsWith(keepConnectState))
            btnKeepConnect.setImageResource(R.drawable.switch_off);
        else if ("1".endsWith(keepConnectState))
            btnKeepConnect.setImageResource(R.drawable.switch_on);

        String autoConnectState = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() +Const.SHARE_PREF_AUTO_CONNECT_WIFI, "0");
        if ("0".endsWith(autoConnectState))
            btnAutoConnect.setBackgroundResource(R.drawable.switch_off);
        else if ("1".endsWith(autoConnectState))
            btnAutoConnect.setBackgroundResource(R.drawable.switch_on);
    }

    private void initListener() {

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchWifiSettingActivity.this.finish();
            }
        });

        btnKeepConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keepConnectState = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() +Const.SHARE_PREF_KEEP_WIFI_CONNECT, "0");
                if ("0".equals(keepConnectState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT, "1");
                else if ("1".equals(keepConnectState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT, "0");
            }
        });

        btnAutoConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String autoConnectState = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() +Const.SHARE_PREF_AUTO_CONNECT_WIFI, "0");
                if ("0".equals(autoConnectState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_AUTO_CONNECT_WIFI, "1");
                else if ("1".equals(autoConnectState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_AUTO_CONNECT_WIFI, "0");
            }
        });
    }

    private void mapSetFieldMsg(String eid,String field, String isOn) {
        String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
        mNetService.sendMapSetMsg(eid, familyid, field, isOn, this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                int mapgetRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapgetRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject mapgetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String keepConnectState = (String) mapgetPl.get(CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT);
                    if (keepConnectState != null && !"".equals(keepConnectState)) {
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_KEEP_WIFI_CONNECT, keepConnectState);
                    }
                    String autoConnectState = (String) mapgetPl.get(CloudBridgeUtil.KEY_AUTO_CONNECT_WIFI);
                    if (autoConnectState != null && !"".equals(autoConnectState)) {
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_AUTO_CONNECT_WIFI, autoConnectState);
                    }
                    updateWifiSettingState();
                }
                break;

            case CloudBridgeUtil.CID_MAPSET_RESP:
                int mapsetRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapsetRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String keepConnectState = (String) pl.get(CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT);
                    if (keepConnectState!=null){
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_KEEP_WIFI_CONNECT, keepConnectState);
                    }
                    String autoConnectState = (String) pl.get(CloudBridgeUtil.KEY_AUTO_CONNECT_WIFI);
                    if (autoConnectState!=null){
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_AUTO_CONNECT_WIFI, autoConnectState);
                    }
                    updateWifiSettingState();
                } else if (mapsetRc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this,getString(R.string.phone_set_timeout),Toast.LENGTH_SHORT);
                } else if (mapsetRc == CloudBridgeUtil.RC_NETERROR || mapsetRc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt),Toast.LENGTH_SHORT);
                } else if (mapsetRc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error), Toast.LENGTH_SHORT);
                }
                break;
            default:
                break;
        }
    }
}
