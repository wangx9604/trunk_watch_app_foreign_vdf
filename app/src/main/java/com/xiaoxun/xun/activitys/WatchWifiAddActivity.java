package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomSettingView;

import net.minidev.json.JSONObject;

/**
 * Created by huangyouyang on 2017/5/2.
 */

public class WatchWifiAddActivity extends NormalActivity implements View.OnClickListener, MsgCallback {

    private TextView tvTitle;
    private ImageButton btnBack;
    private ImageButton btnConfirm;
    private CustomSettingView layoutWifiName;
    private CustomSettingView layoutWifiPwd;

    private WatchData curWatch;
    private Context context;
    private String wifiName;
    private String wifiPwd = "";
    private String wifiBssid;
    private long profId;
    private boolean isEdit;
    private boolean isModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_wifi_add);

        context = WatchWifiAddActivity.this;
        curWatch = myApp.getCurUser().getFocusWatch();
        initView();
        updateViewShow();
        initListener();
        initData();
    }

    private void initView() {

        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.iv_title_back);
        btnConfirm = findViewById(R.id.iv_title_menu);
        layoutWifiName = findViewById(R.id.layout_wifi_name);
        layoutWifiPwd = findViewById(R.id.layout_wifi_pwd);
    }

    private void initListener() {

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        layoutWifiName.setOnClickListener(this);
        layoutWifiPwd.setOnClickListener(this);
    }

    private void initData() {

        profId = getIntent().getLongExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF, -1);
        wifiName = getIntent().getStringExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
        wifiBssid = getIntent().getStringExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID);
        if (profId != -1 && wifiName != null) {
            isEdit = true;
            layoutWifiName.setState(wifiName);
            layoutWifiName.setClickable(false);
            tvTitle.setText(getString(R.string.manual_edit_wifi));
        }
    }

    private void updateViewShow() {

        tvTitle.setText(getString(R.string.manual_add_wifi));
        btnConfirm.setBackgroundResource(R.drawable.btn_confirm_selector);
        btnConfirm.setVisibility(View.VISIBLE);
        btnBack.setBackgroundResource(R.drawable.btn_cancel_selector);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_title_back:
                WatchWifiAddActivity.this.finish();
                break;

            case R.id.iv_title_menu:
                if (wifiName == null) {
                    ToastUtil.show(context, getString(R.string.edit_name_alert));
                    return;
                }
                if (!isEdit) {
                    Intent intent = new Intent();
                    intent.putExtra("wifiname", wifiName);
                    intent.putExtra("wifipwd", wifiPwd);
                    setResult(RESULT_OK, intent);
                    WatchWifiAddActivity.this.finish();
                } else {
                    if (isModify) {
                        e2eEditSavedWifi(profId, wifiPwd, wifiBssid);
                        ToastUtil.show(WatchWifiAddActivity.this, getString(R.string.edit_saved_wifi_ing));
                    } else {
                        WatchWifiAddActivity.this.finish();
                    }
                }
                break;

            case R.id.layout_wifi_name:
                openInputNameDialog();
                break;

            case R.id.layout_wifi_pwd:
                openInputPwdDialog();
                break;
        }

    }

    private void openInputNameDialog() {

        Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(context,
                32, 0,
                context.getString(R.string.wifi_name),
                null, getString(R.string.edit_name_alert), new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {

                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text0) {
                        wifiName = text0;
                        layoutWifiName.setState(wifiName);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void openInputPwdDialog() {

        Dialog dlg = CustomSelectDialogUtil.CustomInputPwdDialog(context,
                32, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
                context.getString(R.string.wifi_password),
                null, getString(R.string.edit_password_alert), new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {

                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text0) {
                        wifiPwd = text0;
                        isModify = true;
                        String pwdShow = getString(R.string.wifi_password_show).substring(0, wifiPwd.length());
                        layoutWifiPwd.setState(pwdShow);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void e2eEditSavedWifi(long profId, String pwd, String bssid) {

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_SAVE_DEVICE_WIFI_DATA);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD, 1);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF, profId);
        pl.put(CloudBridgeUtil.KEY_WIFI_PWD, pwd);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID, bssid);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (myApp.getNetService() != null)
            myApp.getNetService().sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiAddActivity.this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        if (pl == null) {
            //处理异常弹框提示
            int tmpRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
            if (tmpRc == CloudBridgeUtil.RC_NETERROR) { //网络连接异常
                ToastUtil.showMyToast(this, getText(R.string.network_error_prompt).toString(), Toast.LENGTH_LONG);
            } else if (tmpRc == CloudBridgeUtil.ERROR_CODE_E2E_OFFLINE) { //手表不在线
                ToastUtil.showMyToast(this, getText(R.string.watch_offline).toString(), Toast.LENGTH_LONG);
            } else if (tmpRc == CloudBridgeUtil.RC_TIMEOUT) {  //设置超时
//                        ToastUtil.showMyToast(this, getText(R.string.phone_set_timeout).toString(), Toast.LENGTH_LONG);
            } else if (tmpRc < 0) { //网络不好,有时候手表关机服务器没有返回值,所以也存在手表不在线的可能
                ToastUtil.showMyToast(this, getText(R.string.watch_offline).toString(), Toast.LENGTH_LONG);
            }
            return;
        }
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int sub_action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
        if (rc == CloudBridgeUtil.RC_SUCCESS && sub_action == CloudBridgeUtil.SUB_ACTION_SAVE_DEVICE_WIFI_DATA) {
            int cmdId = (Integer) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD);
            if (cmdId == 1) {
                ToastUtil.show(WatchWifiAddActivity.this, getString(R.string.modify_success));
                WatchWifiAddActivity.this.finish();
            }
        }
    }
}
