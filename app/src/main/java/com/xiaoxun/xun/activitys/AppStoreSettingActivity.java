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

public class AppStoreSettingActivity extends NormalActivity  implements MsgCallback {

    private ImageButton mBtnBack;
    private TextView mTitle;
    private ImageButton btnAutoUpdate;

    private WatchData mCurWatch;
    private NetService mNetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_store_setting);

        initView();
        initData();
        initListener();
        mapgetSettingState();
        updateSettingState();
    }

    private void initView() {

        mBtnBack = findViewById(R.id.iv_title_back);
        mTitle = findViewById(R.id.tv_title);
        btnAutoUpdate = findViewById(R.id.btn_auto_update);

        mTitle.setText(R.string.app_store_setting);
    }

    private void initData(){

        mCurWatch=myApp.getCurUser().getFocusWatch();
        mNetService=myApp.getNetService();
    }

    private void initListener(){

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppStoreSettingActivity.this.finish();
            }
        });

        btnAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String settingState = myApp.getStringValue(mCurWatch.getEid() +Const.SHARE_PREF_APP_AUTO_UPDATE, "1");
                if ("0".equals(settingState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_APPSTORE_AUTO_UPDATE, "1");
                else if ("1".equals(settingState))
                    mapSetFieldMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_APPSTORE_AUTO_UPDATE, "0");
            }
        });
    }

    private void updateSettingState() {

        String settingState = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid() +Const.SHARE_PREF_APP_AUTO_UPDATE, "1");
        if ("0".endsWith(settingState))
            btnAutoUpdate.setImageResource(R.drawable.switch_off);
        else if ("1".endsWith(settingState))
            btnAutoUpdate.setImageResource(R.drawable.switch_on);
    }

    private void mapgetSettingState() {

        String[] keys = new String[1];
        keys[0] =  CloudBridgeUtil.KEY_APPSTORE_AUTO_UPDATE;
        if (mNetService != null)
            mNetService.sendMapMGetMsg(mCurWatch.getEid(), keys, this);
    }

    private void mapSetFieldMsg(String eid,String field, String isOn) {
        String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
        if (mNetService != null)
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
                    String settingState = (String) mapgetPl.get(CloudBridgeUtil.KEY_APPSTORE_AUTO_UPDATE);
                    if (settingState != null && !"".equals(settingState)) {
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_APP_AUTO_UPDATE, settingState);
                    }
                    updateSettingState();
                }
                break;

            case CloudBridgeUtil.CID_MAPSET_RESP:
                int mapsetRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapsetRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String settingState = (String) pl.get(CloudBridgeUtil.KEY_APPSTORE_AUTO_UPDATE);
                    if (settingState != null) {
                        myApp.setValue(mCurWatch.getEid() + Const.SHARE_PREF_APP_AUTO_UPDATE, settingState);
                    }
                    updateSettingState();
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
