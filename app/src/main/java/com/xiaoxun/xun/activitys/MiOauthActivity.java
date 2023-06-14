package com.xiaoxun.xun.activitys;

import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huangyouyang on 2018/6/5.
 */

public class MiOauthActivity extends NormalActivity {

    private static final String TAG = "MiOauthActivity ";

    private ImageButton mBtnBack;
    private TextView tvTitle;
    private TextView tvMiOauthState;
    private Button btnMiOauth;
    private XiaomiOAuthResults results;

    private WatchData focusWatch;
    String appId;
    String appSecret;

    public static final String CLIENTID_XIAOAI_MIAO = "2882303761517802378";
    public static final String CLIENTSECRET_XIAOAI_MIAO = "gZkXzbgnWCbI9Reyd2rAnw==";
    public static final String REDIRECTURI_XIAOAI_MIAO = "http://bbwatch.mycoo.com";
    public static final String CLIENTID_XIAOAI_MIAO_703 = "2882303761517863854";
    public static final String CLIENTSECRET_XIAOAI_MIAO_703 = "/8INyvTcUZ9kkfZ6mNLmGA==";
    public static final String CLIENTID_XIAOAI_MIAO_705 = "2882303761517863818";
    public static final String CLIENTSECRET_XIAOAI_MIAO_705 = "JA+pLeXiUZrrVQoUIHWpjQ==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xiaomi_oauth);
        initView();
        initData();
        initListener();
        getAccessTokenFromServer();
    }

    private void initView() {

        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mi_account_oauth);
        mBtnBack = findViewById(R.id.iv_title_back);

        tvMiOauthState = findViewById(R.id.tv_mi_oauth_state);
        btnMiOauth = findViewById(R.id.btn_mi_oauth);
    }


    private void initData() {
        focusWatch = myApp.getCurUser().getFocusWatch();
        appId = CLIENTID_XIAOAI_MIAO;
        appSecret = CLIENTSECRET_XIAOAI_MIAO;
        if (focusWatch.isDevice730()) {
            appId = CLIENTID_XIAOAI_MIAO;
            appSecret = CLIENTSECRET_XIAOAI_MIAO;
        } else if (focusWatch.isDevice703()) {
            appId = CLIENTID_XIAOAI_MIAO_703;
            appSecret = CLIENTSECRET_XIAOAI_MIAO_703;
        } else if (focusWatch.isDevice705()) {
            appId = CLIENTID_XIAOAI_MIAO_705;
            appSecret = CLIENTSECRET_XIAOAI_MIAO_705;
        }
    }

    private void initListener() {

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiOauthActivity.this.finish();
            }
        });

        btnMiOauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAuthCode();
            }
        });
    }

    private void requestAuthCode() {

        XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                .setKeepCookies(true)
                .setAppId(Long.valueOf(appId))
                .setRedirectUrl(REDIRECTURI_XIAOAI_MIAO)
//                .setScope(new int[]{20000})
                .setPlatform(XiaomiOAuthConstants.PLATFORM_DEV)
                .startGetOAuthCode(MiOauthActivity.this);
        getOauthCodeResult(future);
    }

    @SuppressLint("StaticFieldLeak")
    private <V> void getOauthCodeResult(final XiaomiOAuthFuture<V> future) {

        new AsyncTask<Void, Void, V>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                showResult("waiting for Future result...");
            }

            @Override
            protected V doInBackground(Void... params) {
                V v = null;
                try {
                    v = future.getResult();
                } catch (IOException e1) {
                    this.e = e1;
                } catch (OperationCanceledException e1) {
                    this.e = e1;
                } catch (XMAuthericationException e1) {
                    this.e = e1;
                }
                return v;
            }

            @Override
            protected void onPostExecute(V v) {
                if (v != null) {
                    if (v instanceof XiaomiOAuthResults) {
                        results = (XiaomiOAuthResults) v;
                    }
                    String code = results.getCode();
                    reportAuthCodeToServer(code);
                    reportAuthFlag();
//                    getAccessToken(code);
                    showResult(v.toString());
                } else if (e != null && e instanceof XMAuthericationException) {
                    showResult(e.toString());
                } else if (e != null) {
                    showResult(e.toString());
                } else {
                    showResult("done and ... get no result :(");
                }
            }
        }.execute();
    }

    private void getAccessTokenFromServer() {

        MyMsgData mapset = new MyMsgData();
        mapset.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    tvMiOauthState.setText(R.string.mi_account_oauth_success);
                    btnMiOauth.setVisibility(View.VISIBLE);
                } else if (rc == -114) {
                    tvMiOauthState.setText(R.string.mi_account_oauth_need);
                    btnMiOauth.setVisibility(View.VISIBLE);
                } else if (rc == -115) {
                    tvMiOauthState.setText(R.string.mi_account_oauth_expire);
                    btnMiOauth.setVisibility(View.VISIBLE);
                } else {
                    tvMiOauthState.setVisibility(View.GONE);
                    btnMiOauth.setVisibility(View.VISIBLE);
                }
            }
        });

        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MIOAUTH_GET_TOKEN, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(mapset);
    }

    private void reportAuthCodeToServer(String auth_code) {

        MyMsgData req = new MyMsgData();
        req.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    ToastUtil.show(MiOauthActivity.this, getString(R.string.mi_account_oauth_success));
                    tvMiOauthState.setText(R.string.mi_account_oauth_success);
                    btnMiOauth.setVisibility(View.VISIBLE);
                }
            }
        });

        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.WATCH_MIOAUTH_REPORT_AUTHCODE, auth_code);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
        req.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MIOAUTH_REPORT_CODE, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(req);
    }

    private void reportAuthFlag() {

        MsgCallback callback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    LogUtil.i(TAG + "reportAuthFlag success");
                }
            }
        };

        if (myApp.getNetService() != null)
            myApp.getNetService().sendMapSetMsg(focusWatch.getEid(), focusWatch.getFamilyId(),
                    CloudBridgeUtil.WATCH_MIOAUTH_FLAG, "1", callback);
    }

    private void getAccessToken(String auth_code) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://account.xiaomi.com/oauth2/token?" + "client_id=" + appId + "&redirect_uri=" + REDIRECTURI_XIAOAI_MIAO + "&client_secret="
                + URLEncoder.encode(appSecret) + "&grant_type=authorization_code" + "&code=" + auth_code;
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i(TAG + " results = " + results);
            }
        });
    }

    private void showResult(String text) {
        LogUtil.e(TAG + "Oauth:" + "result:" + text);
    }
}
