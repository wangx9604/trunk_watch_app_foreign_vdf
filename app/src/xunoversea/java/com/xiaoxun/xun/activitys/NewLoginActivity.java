/**
 * Creation Date:2015-2-28
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.facebook.FacebookLoginManager;
import com.xiaoxun.xun.facebook.OnLoginSuccessListener;
import com.xiaoxun.xun.google.GoogleLoginManager;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.region.RegionSelectActivity;
import com.xiaoxun.xun.region.WatchSelectActivity;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.SystemUtils;
import com.xiaoxun.xun.utils.Timer;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.XimalayaUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-2-28
 */
public class NewLoginActivity extends NormalActivity implements MsgCallback, OnClickListener {

    private ImibabyApp myApp;
    private BroadcastReceiver mBaseReciever = null;

    private Group loginGroup;

    private Button btnXiaoLogin;
    private String redirectUri = "http://bbwatch.mycoo.com";
    private String accessToken;
    private String openId;
    private String xiaoMiNick = null;
    private String xiaoMiPhone = null;
    private String xiaomiId = null;
    private boolean isXiaoMiLoging = false;
    private XiaomiOAuthResults results;

    private Button facebookLoginButton;
    private boolean isFacebookLogin = false;
    private String facebookOpenId;
    private String facebookUnionId;
    private String facebookAccessToken;
    private String facebookNickName;

    private Button googleLoginButton;
    private boolean isGoogleLogin = false;
    private String googleOpenId;
    private String googleUnionId;
    private String googleNickName;
    private String googleAccessToken;

    private String notificationSOS = null;

    private String jumpWhere;
    private String store_url;
    private String showBack;

    private String flag;
    private String cp;
    ImageView mBackbtn;
    private Button btn_region_select;

    public static final String GOBIND = "goBind";

    private static final String EMAIL = "email";

    static final String[] initPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    private static final int PERMISSION_RESULT_INIT = 0xaa;
    private static final int PERMISSION_RESULT_LOGIN = 0xbb;

    public static final int REQUEST_CODE_REGION_SELECT = 0xa1;
    public static final int RESULT_CODE_REGION_SELECT = 0xa1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_RESULT_INIT:
                if (PermissionUtils.hasPermissions(NewLoginActivity.this, loginPermissions)) {
                    dealInitNeedPermission();
                }
                break;

            case PERMISSION_RESULT_LOGIN:
                if (PermissionUtils.hasPermissions(NewLoginActivity.this, loginPermissions)) {
                    dealInitNeedPermission();
                } else {
                    PermissionUtils.showPermissionPromptDialog(myApp, getString(R.string.run_need_storage_permission));
                }
                break;
            default:
                break;
        }
    }
    static String[] loginPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlogin);
        flag = getIntent().getStringExtra("flag");
        cp = getIntent().getStringExtra("cp");
        myApp = getMyApp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            loginPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            loginPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        myApp.setBindAutoLogin(false);
        myApp.sdcardLog("WelcomeActivity onCreate 22:" + this.toString() + "taskid:" + getTaskId());
        myApp.setMainActivityOpen(false);

        if (getIntent() != null && getIntent().getExtras() != null) {
            jumpWhere = getIntent().getExtras().getString(NewMainActivity.jumpWhere);

            store_url = getIntent().getExtras().getString(StoreActivity.store_url);
            showBack = getIntent().getExtras().getString(NewMainActivity.isShowBack);
        }

        ImageView iv_welcom_xun = findViewById(R.id.iv_welcom_xun);
        Glide.with(this)
                .load(R.drawable.welcom_mitu)
                .placeholder(R.drawable.welcom_mitu)
                .dontAnimate()
                .centerCrop()
                .into(iv_welcom_xun);

        loginGroup = findViewById(R.id.group_login);

        btnXiaoLogin = findViewById(R.id.btn_xiaomi_login);
        btnXiaoLogin.setOnClickListener(this);

        setTintColor(getResources().getColor(R.color.welcome2_ff5a02));

        initservice();
        dealPushMessage(getIntent());

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        if (uid != null) {
            if (!uid.equals(myApp.getLoginXiaomiId())) {
                myApp.setLoginId(null);
            } else {

            }
        }

        if (myApp.getNetService() != null) {
            myApp.getNetService().setKickdownFlag(false);
        }
        notificationSOS = getIntent().getStringExtra(Const.KEY_JSON_MSG);

        mBackbtn = findViewById(R.id.iv_loginback);
        if (!TextUtils.isEmpty(showBack) && showBack.equals("true")) {
            mBackbtn.setVisibility(View.VISIBLE);
        } else {
            mBackbtn.setVisibility(View.GONE);
        }
        mBackbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        btn_region_select = findViewById(R.id.btn_region_select);
        String region = myApp.getStringValue("region","中国");
        btn_region_select.setText(region);
        btn_region_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NewLoginActivity.this, RegionSelectActivity.class),REQUEST_CODE_REGION_SELECT);
            }
        });

        initFacebookLogin();
        initGoogleLogin();
        checkInitPermission();
        privacyPolicy();
        initFCM();
    }

    private void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener((OnCompleteListener<String>) task -> {
                    if (!task.isSuccessful()) {
                        LogUtil.e( "Fetching FCM registration token failed = "+ task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    LogUtil.e("Fetching FCM registration token success = " +token);
                    myApp.setValue(Const.KEY_FCM_TOKEN,token);
//                    Toast.makeText(NewMainActivity.this, token, Toast.LENGTH_SHORT).show();
                });
    }

    private void initFacebookLogin() {

        facebookLoginButton = findViewById(R.id.facebook_login_button);
//        facebookLoginButton.setPermissions(Arrays.asList("email"));
        facebookLoginButton.setOnClickListener(this);

//        FacebookLoginManager.getInstance().facebookKeyHash(this);

        FacebookLoginManager.getInstance().initFaceBook(this, new OnLoginSuccessListener() {
            @Override
            public void OnSuccess(LoginResult result) {
                AccessToken accessToken = result.getAccessToken();
                facebookAccessToken = accessToken.getToken();
                facebookOpenId = "facebook@" + accessToken.getUserId();
                facebookUnionId = facebookOpenId;
                FacebookLoginManager.getInstance().getFacebookInfo(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(org.json.JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            changeUIbyState(0);
                        } else {
                            facebookNickName = object.optString("name");
                            changeUIbyState(1);
                            sendFacebookLogin();
                        }
                    }
                });

//                profileTracker = new ProfileTracker() {
//                    @Override
//                    protected void onCurrentProfileChanged(
//                            Profile oldProfile,
//                            Profile currentProfile) {
//                        facebookNickName = currentProfile.getName();
//                        changeUIbyState(1);
//                        sendFacebookLogin();
//                    }
//                };
            }

            @Override
            public void onCancel() {
                changeUIbyState(0);
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private void initGoogleLogin() {
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        googleLoginButton.setOnClickListener(this);

        GoogleLoginManager.getInstance().initGoogleLoginManger(NewLoginActivity.this, new com.xiaoxun.xun.google.OnLoginSuccessListener() {
            @Override
            public void onSuccess(GoogleSignInAccount account) {
                googleAccessToken = account.getAccount().toString();
                googleOpenId = "google@" + account.getId();
                googleUnionId = googleOpenId;
                googleNickName = account.getDisplayName();
                changeUIbyState(1);
                sendGoogleLogin();
            }

            @Override
            public void onFailure() {
                changeUIbyState(0);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, 0);
        }
        return true;
    }

    private void checkInitPermission() {
        if (Build.VERSION.SDK_INT < 23 || (PermissionUtils.hasPermissions(this, initPermissions))) {
            dealInitNeedPermission();
        } else {
            ActivityCompat.requestPermissions(NewLoginActivity.this, PermissionUtils.getNoGrantedPermissions(this, initPermissions), PERMISSION_RESULT_INIT);
        }
    }

    private void dealInitNeedPermission() {
        myApp.resetCurUser();
        goToWhere();
    }

    private void goToWhere() {
        if (needAutoLogin()) {
            changeUIbyState(1);
            myApp.sdcardLog("NewLoginActivity needAutoLogin");
            ToMainActivity();
        } else {
            changeUIbyState(0);
        }
    }

    private void ToMainActivity() {
        btnXiaoLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myApp.getToken() != null && myApp.getToken().length() > 0) {

                    Intent intent = new Intent(NewLoginActivity.this, NewMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Const.KEY_JSON_MSG, notificationSOS);
                    startActivity(intent);

                    myApp.sdcardLog("NewLoginActivity open MainActivity BG LOGIN");
                } else {
                    changeUIbyState(0);
                }
            }
        }, 2000);
    }

    //点击登录15秒超时
    private boolean timeout = true;

    private Timer reqTimer = null;
    private int timeCount = 30;
    private boolean loginEnter = false;
    private int mLoginRetryTime = 3;

    private void startLoginTimeout(final boolean showToast) {
        timeCount = 30;
        reqTimer = new Timer(1000, new Runnable() {
            public void run() {
                if (reqTimer != null) {
                    timeCount -= 1;
                    if (timeCount <= 0) {
                        //超时了
                        //增加toast，超时提示，
                        if (!loginEnter && showToast) {
                            ToastUtil.showMyToast(NewLoginActivity.this, getString(R.string.login_timeout_tips), Toast.LENGTH_SHORT);
                        }
                        changeUIbyState(0);
                        cancelLoginTimeout();
                    } else {
                        reqTimer.restart();
                    }
                }
            }
        });
        reqTimer.start();
    }

    private void cancelLoginTimeout() {
        if (reqTimer != null) {
            reqTimer.stop();
            reqTimer = null;
        }
    }

    private void changeUIbyState(int state) {
        if (state == 0) {
            loginGroup.setVisibility(View.VISIBLE);
            cancelLoginTimeout();
            mLoginRetryTime = 3;
            if (!TextUtils.isEmpty(showBack) && showBack.equals("true")) {
                mBackbtn.setVisibility(View.VISIBLE);
            }
        } else if (state == 1) {
            loginGroup.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(showBack) && showBack.equals("true")) {
                mBackbtn.setVisibility(View.GONE);
            }
        }
    }

    private boolean needAutoLogin() {
        return (myApp.getLastLoginState() == Const.LOGIN_STATE_LOGIN
                && myApp.getLoginId() != null
                && myApp.getLoginId().length() > 0
                && myApp.getLastppssww() != null
                && myApp.getLastppssww().length() > 0
                && (myApp.getCurUser().getWatchList() != null && myApp.getCurUser().getWatchList().size() > 0));
    }

    private void dealPushMessage(Intent intent) {

        boolean isVideoCallMsg = intent.getBooleanExtra(Const.VIDEO_CALL_PUSH_MESSAGE, false);
        if (isVideoCallMsg) {
            ToastUtil.show(myApp, myApp.getString(R.string.videocall_request_is_unavailable));
        }
    }

    private NetService mNetService = null;

    /**
     * 初始化连接netservice服务
     */
    private void initservice() {

        Intent it = new Intent(NewLoginActivity.this, NetService.class);
        bindService(it, conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                mNetService = ((NetService.MyBinder) service).getService();
                if (mNetService != null) {
                    getMyApp().setNetService(mNetService);
                    //if auto login
                    if (needAutoLogin()) {

                    } else {

                    }

                }
            }
        }, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        myApp.isCurrentRunningForeground = true;
        super.onResume();
        if (flag != null && flag.length() > 0 && flag.equals("kickoff")) {
            DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                    getString(R.string.prompt),
                    cp,
                    null,
                    null,
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            flag = null;
                        }
                    },
                    getText(R.string.confirm).toString());
        }
        sendBroadcast(new Intent(Const.ACTION_CHECK_WEBSOCKET_STATE));
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    private ServiceConnection conn;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //FacebookLoginManager.getInstance().onDestroy();
        //GoogleLoginManager.getInstance().signOut(NewLoginActivity.this);
        myApp.sdcardLog("WelcomeActivity onDestroy ");
        unbindService(conn);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        // TODO Auto-generated method stub
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        if (cid == CloudBridgeUtil.CID_QUERY_MYGROUPS_RESP) {
            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
            myApp.sdcardLog("WelcomeActivity CID_QUERY_MYGROUPS_RESP rc:" + rc);
            if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_SESSION_ILLEGAL) {

            } else if (rc == CloudBridgeUtil.RC_SUCCESS) {
                //刷新groups
                myApp.parseJSONObjectGroups(CloudBridgeUtil.getCloudMsgPLArray(respMsg));
                myApp.setIsNeedInvalidFamilyDialog(false);

                //刷新login的数据库,当登陆账户切换时，需要必要的初始化
                myApp.saveLoginOKResult(Const.LOGIN_STATE_LOGIN);

                if (xiaomiId != null && TextUtils.isEmpty(myApp.getCurUser().getXiaomiId())) {
                    sendSetXiaoMIId();
                }
                myApp.setValue(Const.SHARE_PREF_CURRENT_USER_REFLECT_ID, myApp.getCurUser().getUid());
                myApp.initMapType();
                myApp.getNetService().getNoticeSetting(null);
                {
                    if (!TextUtils.isEmpty(store_url)) { //这种情况只有商城登录时才能进入
                        //myApp.setIsStoreLogin("true");//商城登录做一个标记
                        Intent intent = new Intent(NewLoginActivity.this, StoreActivity.class);
                        intent.putExtra("targetUrl", store_url);
                        intent.putExtra("wherego", "toStoreActivity");
                        startActivity(intent);
                        myApp.setIsLoginToStore("3");
                        finish();
                    } else {
                        //未绑定
                        if (getMyApp().getCurUser().getWatchList() == null || getMyApp().getCurUser().getWatchList().size() == 0) {
                            //无设备 登录和添加手表两种状态
                            if (!TextUtils.isEmpty(jumpWhere) && jumpWhere.equals("goBindNewActivity")) {
                                Intent intent = new Intent(NewLoginActivity.this, BindNewActivity.class);
//                                Intent intent = new Intent(NewLoginActivity.this, MipcaActivityCapture.class);
//                                intent.putExtra(GOBIND, "goBind");
//                                intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "bind");
//                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(NewLoginActivity.this, NewMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            //已经绑定设备 登录和添加手表两种状态
                            if (!TextUtils.isEmpty(jumpWhere) && jumpWhere.equals("goBindNewActivity")) {
                                if (myApp.getCurUser().getWatchList() != null && myApp.getCurUser().getWatchList().size() >= Const.DEVICE_MAX_LIMIT) {
                                    ToastUtil.showMyToast(NewLoginActivity.this, getString(R.string.max_watch_num_prompt_msg), Toast.LENGTH_SHORT);
                                    Intent intent = new Intent(NewLoginActivity.this, NewMainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(NewLoginActivity.this, BindNewActivity.class);
//                                    Intent intent = new Intent(NewLoginActivity.this, MipcaActivityCapture.class);
//                                    intent.putExtra(GOBIND, "goBind");
//                                    intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "bind");
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(NewLoginActivity.this, NewMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Const.KEY_JSON_MSG, notificationSOS);
                                intent.putExtra("wherego", "toNewMainActivity");
                                startActivity(intent);
                                myApp.sdcardLog("WelcomeActivity open MainActivity");
                                sendBroadcast(new Intent(Const.ACTION_GET_OFFLINE_CHAT_MSG));
                                finish();
                            }
                        }
                    }
                }

            } else if (rc == CloudBridgeUtil.RC_TIMEOUT
                    || rc == CloudBridgeUtil.RC_NETERROR) {
                changeUIbyState(0);
                ToastUtil.showMyToast(this, getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);

            } else {
                changeUIbyState(0);
                ToastUtil.showMyToast(this, getString(R.string.login_timeout_tips), Toast.LENGTH_SHORT);
                LogUtil.e("resp rc error:" + rc);
                LogUtil.e("resp error rn" + CloudBridgeUtil.getCloudMsgRN(respMsg));
            }
        } else if (cid == CloudBridgeUtil.CID_USER_LOGIN_RESP) {
            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
            myApp.sdcardLog("WelcomeActivity CID_USER_LOGIN_RESP rc:" + rc);
            loginEnter = true;
            if (rc == CloudBridgeUtil.RC_SUCCESS) {
                String sid = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_SID);
                myApp.setToken(sid);
                //save token
                JSONObject jo = CloudBridgeUtil.getCloudMsgPL(respMsg);
                String eid = (String) jo.get(CloudBridgeUtil.KEY_NAME_EID);

                myApp.getCurUser().setEid(eid);
                myApp.setValue(Const.SHARE_PREF_FIELD_LOGIN_EID, eid);
                myApp.saveLoginOKResult(Const.LOGIN_STATE_LOGIN);
                if (myApp.getNetService() != null)
                    myApp.getNetService().setNetServiceLoginOK(true);
                if (myApp.getMiPushRegister()) {
                    myApp.setMiPushAlias();
                }
                if (isXiaoMiLoging) {
                    sendUserNicknameSetMsg(xiaoMiNick);
                } else if (isFacebookLogin) {
                    sendUserNicknameSetMsg(facebookNickName);
                } else if (isGoogleLogin) {
                    sendUserNicknameSetMsg(googleNickName);
                }
                //设置语言信息
                myApp.getNetService().sendSetLangInfo();
                //获取群组信息
                getMyGroups();
            } else if (rc == CloudBridgeUtil.RC_TIMEOUT
                    || rc == CloudBridgeUtil.RC_NETERROR) {
                changeUIbyState(0);
                ToastUtil.showMyToast(this, getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);
            } else if (rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                if (mLoginRetryTime > 0) {
                    btnXiaoLogin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loginGroup.getVisibility() != View.VISIBLE) {
                                if (isXiaoMiLoging) {
                                    sendXiaoMiLogin();
                                } else if (isFacebookLogin) {
                                    sendFacebookLogin();
                                } else if (isGoogleLogin) {
                                    sendGoogleLogin();
                                } else {
                                    changeUIbyState(0);
                                }
                            }
                        }
                    }, 3000);
                    mLoginRetryTime--;
                } else {
                    changeUIbyState(0);
                    ToastUtil.showMyToast(this, getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);
                }
            } else if (rc == CloudBridgeUtil.RC_ACCOUNT_NOT_FOUND) {
                if (isXiaoMiLoging) {
                    sendXiaoMiRegister();
                } else if (isFacebookLogin) {
                    sendFacebookRegister();
                } else if (isGoogleLogin) {
                    sendGoogleRegister();
                } else {
                    changeUIbyState(0);
                }
            } else {
                changeUIbyState(0);
                ToastUtil.showMyToast(this, getText(R.string.login_error).toString() + rc, Toast.LENGTH_SHORT);
            }
        } else if (cid == CloudBridgeUtil.CID_THIRD_REG_RESP) {
            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
            if (rc == CloudBridgeUtil.RC_SUCCESS) {
                if (isXiaoMiLoging) {
                    sendXiaoMiLogin();
                } else if (isFacebookLogin) {
                    sendFacebookLogin();
                } else if (isGoogleLogin) {
                    sendGoogleLogin();
                }
            } else {
                changeUIbyState(0);
                ToastUtil.showMyToast(this, "error:" + rc, Toast.LENGTH_SHORT);
            }
        } else if (cid == CloudBridgeUtil.CID_USER_SET_RESP) {
            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
            if (rc == CloudBridgeUtil.RC_SUCCESS) {
                if (isXiaoMiLoging) {
                    myApp.getCurUser().setNickname(xiaoMiNick);
                    myApp.setStringValueNoEncrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, xiaoMiNick + "");
                } else if (isFacebookLogin) {
                    myApp.getCurUser().setNickname(facebookNickName);
                    myApp.setStringValueNoEncrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, facebookNickName + "");
                } else if (isGoogleLogin) {
                    myApp.getCurUser().setNickname(googleNickName);
                    myApp.setStringValueNoEncrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, googleNickName + "");
                }
            }
        }
    }

    private void getMyGroups() {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(NewLoginActivity.this);
        //set msg body
        queryGroupsMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_QUERY_MYGROUPS, null));

        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }

    }

    /**
     * 判断 用户是否安装客户端
     */

    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_xiaomi_login:
                if (Build.VERSION.SDK_INT < 23 || (PermissionUtils.hasPermissions(this, loginPermissions))) {
                    if (myApp.getNetService() != null && myApp.getNetService().isPermissionDedied()) {
                        ToastUtil.show(NewLoginActivity.this, R.string.permission_denied_toast);
                        return;
                    }
                    try {
                        isXiaoMiLoging = true;
                        isFacebookLogin = false;
                        isGoogleLogin = false;
                        getToken();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(NewLoginActivity.this, PermissionUtils.getNoGrantedPermissions(this, loginPermissions), PERMISSION_RESULT_LOGIN);
                }
                break;
            case R.id.facebook_login_button:
                if (Build.VERSION.SDK_INT < 23 || (PermissionUtils.hasPermissions(this, loginPermissions))) {
                    isXiaoMiLoging = false;
                    isFacebookLogin = true;
                    isGoogleLogin = false;
                    changeUIbyState(1);
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    FacebookLoginManager.getInstance().faceBookLogin(NewLoginActivity.this);
                } else {
                    ActivityCompat.requestPermissions(NewLoginActivity.this, PermissionUtils.getNoGrantedPermissions(this, loginPermissions), PERMISSION_RESULT_LOGIN);
                }
                break;
            case R.id.google_login_button:
                if (Build.VERSION.SDK_INT < 23 || (PermissionUtils.hasPermissions(this, loginPermissions))) {
                    isXiaoMiLoging = false;
                    isFacebookLogin = false;
                    isGoogleLogin = true;
                    changeUIbyState(1);
                    GoogleLoginManager.getInstance().signIn(NewLoginActivity.this);
                } else {
                    ActivityCompat.requestPermissions(NewLoginActivity.this, PermissionUtils.getNoGrantedPermissions(this, loginPermissions), PERMISSION_RESULT_LOGIN);
                }
                break;
            default:
                break;
        }
    }

    private void sendXiaoMiLogin() {
        sendLogin(openId, xiaomiId, xiaoMiNick);
        myApp.setLoginXiaomiId(xiaomiId);
    }

    private void sendXiaoMiRegister() {
        sendRegister(accessToken, openId, xiaomiId, xiaoMiNick, xiaoMiPhone);
    }

    private void sendSetXiaoMIId() {
        MyMsgData captchaMsg = new MyMsgData();
        captchaMsg.setCallback(NewLoginActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_XIAOMIID, xiaomiId);
        captchaMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));
        myApp.getNetService().sendNetMsg(captchaMsg);
    }

    private void getToken() {
        changeUIbyState(1);
        myApp.sdcardLog("getToken");
        XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize().setKeepCookies(true)
                .setAppId(Const.clientId)
                .setRedirectUrl(redirectUri)
                .startGetAccessToken(NewLoginActivity.this);
        waitAndShowFutureResult(future);
        startLoginTimeout(false);
    }

    /**
     * 备注：setSkipConfirm 当用户已经授权过，不会再让用户确认，用户此时无法切换帐号。 如果用户没有授权过，会再次弹起授权页面
     */

    private Long getAppId() {
        return Const.clientId;
    }

    private void getOpenId() {
        myApp.sdcardLog("getOpenId");
        XiaomiOAuthFuture<String> future = new XiaomiOAuthorize()
                .callOpenApi(NewLoginActivity.this,
                        getAppId(),
                        XiaomiOAuthConstants.OPEN_API_PATH_OPEN_ID,
                        results.getAccessToken(),
                        results.getMacKey(),
                        results.getMacAlgorithm());
        getOpenIdFutureResult(future);
    }

    private void getUserInfo() {
        XiaomiOAuthFuture<String> future = new XiaomiOAuthorize()
                .callOpenApi(NewLoginActivity.this,
                        getAppId(),
                        XiaomiOAuthConstants.OPEN_API_PATH_PROFILE,
                        results.getAccessToken(),
                        results.getMacKey(),
                        results.getMacAlgorithm());
        getUserinfoFutureResult(future);
    }

    private <V> void waitAndShowFutureResult(final XiaomiOAuthFuture<V> future) {
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
                cancelLoginTimeout();
                if (v != null) {
                    if (v instanceof XiaomiOAuthResults) {
                        results = (XiaomiOAuthResults) v;
                    }
                    changeUIbyState(1);
                    getOpenId();
                    accessToken = results.getAccessToken();
                    int errorCode = results.getErrorCode();
                    String errorMsg = results.getErrorMessage();
                    if (errorCode == -1002) {
                        ToastUtil.showMyToast(NewLoginActivity.this,
                                getString(R.string.xiaomi_login_failed),
                                Toast.LENGTH_SHORT);
                        changeUIbyState(0);
                    } else {
                        startLoginTimeout(true);
                    }
                    showResult(v.toString());
                } else if (e != null) {
                    showResult(e.toString());
                    changeUIbyState(0);
                    myApp.removeCookie(getApplicationContext());
                } else {
                    changeUIbyState(0);
                    myApp.removeCookie(getApplicationContext());
                    showResult("done and ... get no result :(");
                }
            }
        }.execute();
    }

    private <V> void getOpenIdFutureResult(final XiaomiOAuthFuture<V> future) {
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
                cancelLoginTimeout();
                if (v != null) {
                    try {
                        JSONObject jo = (JSONObject) JSONValue.parse(v.toString());
                        JSONObject data = (JSONObject) jo.get("data");
                        openId = (String) data.get("openId");// 考虑到服务器必须对原小米openid兼容，小米openid就不加类型前缀，服务器默认处理为小米openid
                        getUserInfo();
                        startLoginTimeout(true);
                    } catch (Exception e) {
                        changeUIbyState(0);
                    }
                    showResult(v.toString());
                } else if (e != null) {
                    changeUIbyState(0);
                    showResult(e.toString());
                } else {
                    changeUIbyState(0);
                    showResult("done and ... get no result :(");
                }
            }
        }.execute();
    }

    private <V> void getUserinfoFutureResult(final XiaomiOAuthFuture<V> future) {
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
                cancelLoginTimeout();
                if (v != null) {
                    try {
                        JSONObject jo = (JSONObject) JSONValue.parse(v.toString());
                        JSONObject data = (JSONObject) jo.get("data");
                        Log.i("cui", "data = " + data.toString());
                        xiaoMiNick = (String) data.get("miliaoNick");
                        xiaomiId = data.get("unionId").toString();

                        Log.i("cui", "*unionId***************" + xiaomiId);
                        //getUserphone();
                        sendXiaoMiLogin();
                        startLoginTimeout(true);
                    } catch (Exception e) {
                        changeUIbyState(0);
                    }
                    showResult(v.toString());
                } else if (e != null) {
                    changeUIbyState(0);
                    showResult(e.toString());
                } else {
                    changeUIbyState(0);
                    showResult("done and ... get no result :(");
                }
            }
        }.execute();
    }

    private void showResult(String text) {
        LogUtil.e("Oauth:" + "result:" + text);
        myApp.sdcardLog("Oauth:" + "result:" + text);
    }

    private void sendUserNicknameSetMsg(String nickName) {
        MyMsgData nicknameMsg = new MyMsgData();
        nicknameMsg.setCallback(NewLoginActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_NICKNAME, nickName);
        nicknameMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(nicknameMsg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isFacebookLogin) {
            FacebookLoginManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == REQUEST_CODE_REGION_SELECT){
            if(resultCode == RESULT_CODE_REGION_SELECT){
                String region = myApp.getStringValue("select","中国");
                btn_region_select.setText(region);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (isGoogleLogin) {
            GoogleLoginManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void privacyPolicy() {
        boolean isPrivacyPolicyAgreed = myApp.getBoolValue(Const.SHARE_PREF_PRIVACY_POLICY_AGREED, false);
        if (!isPrivacyPolicyAgreed) {
            String content = getString(R.string.app_name) + getString(R.string.privacy_policy_info);
            SpannableStringBuilder builder1 = new SpannableStringBuilder(content);
            ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url;
                    if (getPackageName().equals(Params.PACKAGE_NAME_XUN)) {
                        content_url = Uri.parse(FunctionUrl.APP_PRIVACY_POLICY_URL + "?pn=9&lang=" + Locale.getDefault().getLanguage());
                    } else {
                        content_url = Uri.parse(FunctionUrl.APP_PRIVACY_POLICY_URL + "?pn=2");
                    }
                    intent.setData(content_url);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.bg_color_orange));
                }
            };
            String privacyPolicy = getString(R.string.privacy_policy);
            int start = content.indexOf(privacyPolicy);
            builder1.setSpan(clickableSpan1, start, start + privacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            DialogUtil.CustomNormalSpanDialog(this,
                    getString(R.string.privacy_policy_title),
                    builder1,
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    },
                    getString(R.string.quit),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            myApp.setValue(Const.SHARE_PREF_PRIVACY_POLICY_AGREED, true);
                        }
                    },
                    getString(R.string.agree_and_continue)).show();
            return;
        }
    }

    private void sendFacebookLogin() {
        sendLogin(facebookOpenId, facebookUnionId, facebookNickName);
    }

    private void sendFacebookRegister() {
        sendRegister(facebookAccessToken, facebookOpenId, facebookUnionId, facebookNickName, null);
    }

    private void sendGoogleLogin() {
        sendLogin(googleOpenId, googleUnionId, googleNickName);
    }

    private void sendGoogleRegister() {
        sendRegister(googleAccessToken, googleOpenId, googleUnionId, googleNickName, null);
    }

    private void sendLogin(String openId, String unionId, String nickname) {
        if (myApp.getNetService() == null) {
            changeUIbyState(0);
            ToastUtil.showMyToast(this, getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);
            return;
        }
        MyMsgData loginMessage = new MyMsgData();
        loginMessage.setCallback(NewLoginActivity.this);

        String region = XimalayaUtil.getMipushRegion(this);
        LogUtil.e("mipush app region11:"+region);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_NAME, openId);
        pl.put(CloudBridgeUtil.KEY_NAME_PASSWORD, StrUtil.getXioaoMiPsw(openId, getMyApp().getAccesskey()));
        pl.put(CloudBridgeUtil.KEY_NAME_UNIONID, unionId);
        pl.put(CloudBridgeUtil.KEY_NAME_TYPE, CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        pl.put(CloudBridgeUtil.KEY_NAME_ADS, SystemUtils.getDeviceInfo(getApplicationContext()));
        pl.put(CloudBridgeUtil.KEY_NAME_REGION, region);
        pl.put(Const.SHARE_PREF_FIELD_PUSH_TOKEN,myApp.getStringValue(Const.KEY_FCM_TOKEN,""));
        loginMessage.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_LOGIN, pl));
        myApp.getNetService().sendLoginMsg(loginMessage);

        myApp.setLoginId(openId);
        StringBuilder buf = new StringBuilder();
        if (nickname != null) {
            if (nickname.length() > Const.NICKNAME_MAXLEN) {
                nickname = nickname.substring(0, Const.NICKNAME_MAXLEN);
            }
            buf.append(nickname);
        }

        myApp.getCurUser().setNickname(buf.toString());
        myApp.setLastppssww(StrUtil.getXioaoMiPsw(openId, getMyApp().getAccesskey()));
        myApp.setLastUnionId(unionId);
    }

    private void sendRegister(String accessToken, String openId, String unionId, String nickname, String phoneNumber) {
        MyMsgData captchaMsg = new MyMsgData();
        captchaMsg.setCallback(NewLoginActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TOKEN, accessToken);
        pl.put(CloudBridgeUtil.KEY_NAME_NAME, openId);
        pl.put(CloudBridgeUtil.KEY_NAME_UNIONID, unionId);
        pl.put(CloudBridgeUtil.KEY_NAME_ACCESSKEY, getMyApp().getAccesskey());

        if (phoneNumber != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CELLPHONE, phoneNumber);
        }

        if (nickname != null) {
            if (nickname.length() > Const.NICKNAME_MAXLEN) {
                nickname = nickname.substring(0, Const.NICKNAME_MAXLEN);
            }
            pl.put(CloudBridgeUtil.KEY_NAME_NICKNAME, nickname);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_ALIAS, nickname);
        pl.put(CloudBridgeUtil.KEY_NAME_EMAIL, openId + "@b.com");
        captchaMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_THIRD_REG, pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendLoginMsg(captchaMsg);
        }
    }

}
