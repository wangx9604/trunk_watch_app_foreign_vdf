package com.xiaoxun.xun.activitys;

import static com.xiaoxun.xun.activitys.NewLoginActivity.REQUEST_CODE_REGION_SELECT;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.region.RegionSelectActivity;
import com.xiaoxun.xun.region.WatchSelectActivity;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.views.CustomSettingView;

import net.minidev.json.JSONObject;

import java.util.Locale;

/**
 * Created by guxiaolong on 2016/6/30.
 */
public class AppAboutActivity extends NormalActivity implements View.OnClickListener {

    private ImageButton mBackbtn;
    private CustomSettingView layoutPrivacy;
    private CustomSettingView layoutAgreement;
    private CustomSettingView mLayoutApnConfig;
    private CustomSettingView mLayoutDevelopMode;
    private CustomSettingView mLayoutSelectRegion;
    private CustomSettingView layoutTransMap;
    private View iv_selector;
    private Button mBtnLogout;
    private TextView tv_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_about);
        ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.setting_watch_about));

        mBackbtn = findViewById(R.id.iv_title_back);
        mBackbtn.setOnClickListener(this);
        iv_selector = findViewById(R.id.iv_selector);
        layoutPrivacy = findViewById(R.id.layout_privacy);
        layoutPrivacy.setOnClickListener(this);

        layoutAgreement = (CustomSettingView) findViewById(R.id.layout_agreement);
        layoutAgreement.setOnClickListener(this);
        //APN
        mLayoutApnConfig = findViewById(R.id.layout_apnconfig);
        mLayoutApnConfig.setOnClickListener(this);
        mLayoutApnConfig.setVisibility(View.GONE);

        String curAppVer = Params.getInstance(getApplicationContext()).getAppVerName();
        ((TextView) findViewById(R.id.tv_app_about_version)).setText("V" + curAppVer);
        ImibabyApp mApp = (ImibabyApp) getApplication();
        if (mApp != null && mApp.needAutoLogin()) {
            layoutPrivacy.setVisibility(View.VISIBLE);
        } else {
            layoutPrivacy.setVisibility(View.GONE);
            iv_selector.setVisibility(View.GONE);
        }
        if (!mApp.isAutoLogin() || mApp.getCurUser().getWatchList() == null || mApp.getCurUser().getWatchList().size() == 0
                || mApp.getCurUser().getFocusWatch().isDevice708_A06() || mApp.getCurUser().getFocusWatch().isDevice709_A03()) {
            if (mApp.isAutoLogin() && mApp.getCurUser().getFocusWatch() != null && mApp.getCurUser().getFocusWatch().isDevice707_A05()) {
                layoutPrivacy.setVisibility(View.VISIBLE);
                layoutAgreement.setVisibility(View.VISIBLE);
            } else {
                layoutPrivacy.setVisibility(View.GONE);
                layoutAgreement.setVisibility(View.GONE);
            }
        } else {
            layoutPrivacy.setVisibility(View.VISIBLE);
            layoutAgreement.setVisibility(View.VISIBLE);
        }
        //退出
        mBtnLogout = findViewById(R.id.btn_logout);
        mBtnLogout.setOnClickListener(this);
        if (myApp != null && myApp.needAutoLogin()) {
            mBtnLogout.setVisibility(View.VISIBLE);
        } else {
            mBtnLogout.setVisibility(View.GONE);
        }
        // 切换服务器&选择地图&选择地图
        mLayoutSelectRegion = findViewById(R.id.layout_select_region);
        layoutTransMap = findViewById(R.id.layout_map_trans);

        mLayoutSelectRegion.setOnClickListener(this);
        layoutTransMap.setOnClickListener(this);
        //开发者选项
        mLayoutDevelopMode = findViewById(R.id.layout_develop_mode);
        mLayoutDevelopMode.setOnClickListener(this);
        if (BuildConfig.ISDEBUG) {
            if (BuildConfig.VERSION_TYPE.equals("Normal")) {
                mLayoutDevelopMode.setVisibility(View.VISIBLE);
            } else if (BuildConfig.VERSION_TYPE.equals("Monkey")) {
                mLayoutDevelopMode.setVisibility(View.GONE);
            }
        } else {
            mLayoutDevelopMode.setVisibility(View.GONE);
        }
        mLayoutSelectRegion.setVisibility(View.VISIBLE);
        tv_nickname = findViewById(R.id.tv_nickname);
        showNickAndAccountType();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackbtn) {
            finish();
        } else if (v == layoutPrivacy) {
            showAgreementAndPrivacy("2");
        } else if (v == layoutAgreement) {
            showAgreementAndPrivacy("1");
        } else if (v == mLayoutApnConfig) {
            startActivity(new Intent(AppAboutActivity.this, APNConfigActivity.class));
        }else if (v == layoutTransMap) {
//            startActivity(new Intent(this, MapSettingActivity.class));
        } else if (v == mLayoutSelectRegion) {
            startActivity(new Intent(AppAboutActivity.this, RegionSelectActivity.class).putExtra("entry_type", 2));
        } else if (v == mBtnLogout) {
            Dialog dlg = DialogUtil.CustomNormalDialog(AppAboutActivity.this,
                    getString(R.string.prompt),
                    getString(R.string.logout_really),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            sendUserLogout();
                            MiPushClient.unsetAlias(getApplicationContext(), myApp.getCurUser().getEid(), null);
                            myApp.doLogout("normal do logout");
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        } else if (v == mLayoutDevelopMode) {
            startActivity(new Intent(this, DevOptActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendUserLogout() {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(null);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONObject msg = new JSONObject();
        msg.put("CID", CloudBridgeUtil.CID_USER_LOGOUT);
        if (myApp.getToken() != null) {
            msg.put(CloudBridgeUtil.KEY_NAME_SID, myApp.getToken());
        }
        msg.put("SN", sn);

        mapget.setReqMsg(msg);
        myApp.getNetService().sendNetMsg(mapget);
    }

    private void showNickAndAccountType() {
        String nickName = "";
        String type = "";
        if (myApp != null) {
            nickName = myApp.getStringValueNoDecrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, "");
        }
        String loginId = myApp.getLoginId();
        if (loginId == null) {
            type = "";
            return;
        }
        if (loginId.startsWith("facebook")) {
            type = "Facebook" + ":";
        } else if (loginId.startsWith("google")) {
            type = "Google" + ":";
        } else if (loginId.startsWith("alipay")) {
            type = getString(R.string.alipay) + ":";
        } else {
            type = getString(R.string.account_type_xiaomi) + ":";
        }
        if (myApp.needAutoLogin()) {
            tv_nickname.setText("" + type + nickName);
        }
    }

    private void showAgreementAndPrivacy(String type) {
        JSONObject params = new JSONObject();
        params.put("packageName", this.getPackageName());
        if (myApp.getCurUser().getFocusWatch() != null) {
            params.put("deviceType", myApp.getCurUser().getFocusWatch().getDeviceType());
        }
        params.put("type", type);
        params.put("language", Locale.getDefault().getLanguage());
        params.put("countryCode", Locale.getDefault().getCountry());
        params.put("lang", Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
        Intent intent = new Intent(AppAboutActivity.this, HelpWebActivity.class);
        if ("1".equals(type)) {
            intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_AGREEMENT);
        } else {
            intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_PRIVACY_POLICY);
        }
        intent.putExtra(Const.KEY_HELP_URL, FunctionUrl.POST_AGREEMENT_AND_PRIVACY_URL);
        intent.putExtra(Const.KEY_PARAMS, params.toJSONString());
        startActivity(intent);
    }
}
