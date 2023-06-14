package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.SmsUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

/**
 * Created by huangyouyang on 2017/3/17.
 */

public class APNConfigActivity extends NormalActivity {

    private static final String logFlag = "HYY ";

    private TextView tvTitle;
    private ImageButton btnBack;
    private EditText etApnPhonenumber;
    private EditText etApnImei;
    private EditText etApnType;
    private EditText etApnPort;
    private Button btnApnConfirm;
    private Button btnApnDefault;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apn_config);
        context = this;

        initView();
        initListener();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.setting_apnconfig));
        btnBack = findViewById(R.id.iv_title_back);
        etApnPhonenumber = findViewById(R.id.et_apn_phonenumber);
        etApnImei = findViewById(R.id.et_apn_imei);
        etApnType = findViewById(R.id.et_apn_type);
        etApnPort = findViewById(R.id.et_apn_port);
        btnApnConfirm = findViewById(R.id.btn_apn_confirm);
        btnApnDefault = findViewById(R.id.btn_apn_default);
    }

    private void initListener() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APNConfigActivity.this.finish();
            }
        });

        btnApnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apnPhonenumber = etApnPhonenumber.getText().toString().trim().replace(" ", "");
                String apnImei = etApnImei.getText().toString().trim().replace(" ", "");
                String apnType = etApnType.getText().toString().trim().replace(" ", "");
                String apnPort = etApnPort.getText().toString().trim().replace(" ", "");

                if (!StrUtil.isMobileNumber(apnPhonenumber, 2)) {
                    ToastUtil.show(context, getString(R.string.format_error));
                    return;
                }
                if (!StrUtil.isDeviceImei(apnImei)) {
                    ToastUtil.show(context, getString(R.string.apnconfig_imei_error));
                    return;
                }
                if (TextUtils.isEmpty(apnType)) {
                    ToastUtil.show(context, getString(R.string.apnconfig_type_erroe));
                    return;
                }
                if (TextUtils.isEmpty(apnPort)) {
                    ToastUtil.show(context, getString(R.string.apnconfig_port_error));
                    return;
                }

                sendApnConfigSms(apnImei,apnType,apnPort,apnPhonenumber,0);
            }
        });

        btnApnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apnPhonenumber = etApnPhonenumber.getText().toString().trim().replace(" ", "");
                String apnImei = etApnImei.getText().toString().trim().replace(" ", "");

                if (!StrUtil.isMobileNumber(apnPhonenumber, 2)) {
                    ToastUtil.show(context, getString(R.string.format_error));
                    return;
                }
                if (!StrUtil.isDeviceImei(apnImei)) {
                    ToastUtil.show(context, getString(R.string.apnconfig_imei_error));
                    return;
                }

                sendApnConfigSms(apnImei,Const.APN_TYPE_DEFAULT,"80",apnPhonenumber,1);
            }
        });
    }

    // type 0,配置；1，默认
    private void sendApnConfigSms(final String apnImei, final String apnType, final String apnPort, final String apnPhonenumber, int type) {

        String content = "", title = "";
        if (type == 0) {
            title = getString(R.string.apnconfig_confirm);
            content = getString(R.string.apnconfig_confirm_desc);
        } else if (type == 1) {
            title = getString(R.string.apnconfig_default);
            content = getString(R.string.apnconfig_default_desc);
        }
        Dialog dlg = DialogUtil.CustomNormalDialog(APNConfigActivity.this,
                title, content,
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, getString(R.string.cancel),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer apnInfo = new StringBuffer();
                        apnInfo.append(Const.APN_ID + MD5.md5_string(apnImei + Const.APN_IMEI_MD5_SALT) + ",");
                        apnInfo.append(Const.APN_TYPE + apnType + ",");
                        apnInfo.append(Const.APN_PORT + apnPort + ",");
                        apnInfo.append(Const.APN_ACCOUTNAME + TimeUtil.getTimeStampLocal().substring(8, 14) + ",");
                        apnInfo.append(Const.APN_HOMEPAGE + ",");
                        SmsUtil.sendMsgToWatch(context, apnPhonenumber, apnInfo.toString());
                    }
                }, getString(R.string.confirm));
        dlg.show();
    }
}
