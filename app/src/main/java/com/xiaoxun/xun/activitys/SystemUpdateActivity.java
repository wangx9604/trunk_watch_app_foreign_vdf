package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.beans.WatchUpDateInfo;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AndroidUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.RoundProgressBar;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

public class SystemUpdateActivity extends NormalActivity implements MsgCallback, OnClickListener {
    private static final String TAG = "SystemUpdateActivity";
    private static final String TAGSD = "SystemUpdate : ";
    private static final int MY_REQUEST_CODE = 0x15;

    // 升级flag，是手表升级还是APP升级
    private int WATCH_UPDATE = 0;
    private int APP_UPDATE = 1;
    private int mSystemUpdateType = -1; //直接升级  0 watch update,1 app update

    private int mCheckUpdate = -1;

    private boolean mAppHaveNewVerion = false;
    private boolean mWatchHaveNewVerion = false;

    private int totalPercent = 0;
    private RoundProgressBar mRoundProgressBar1;

    private UpdateHandler mMyHandler = null;

    private View lineWatchUpdate;
    private Button btnAppUpdate;
    private Button btnWatchUpdate;
    private Button btnAppHaveUpdate;
    private Button btnWatchHaveUpdate;
    private Button btnWatchVerinfo;
    private ProgressBar watchLoadingBar;

    private View btnBack;
    private TextView tvState;
    private ImageView ivState;

    private TextView tvAppVersion;
    private TextView tvWatchVersion;
    private TextView tvWatchNewVersion;

    int NETWORK_MOBILE_ENABLED = 1;
    int NETWORK_WIFI_ENABLED = 2;
    int NETWORK_NOT_AVAILABLE = 0;
    int NETWORK_ETHERNET_ENABLED = 3;

    private int mAppForceUpdate = -1; // 当前APP是否是强制升级
    private String mAppUpdateDesc = null; // 当前APP升级描述
    private boolean mAppDownloading = false; // 是否正在下载新的APP
    ////
    //// 手表升级相关
    ////
    private Handler scanHelpHandler;

    private WatchUpDateInfo watchUpDateInfo;
    private BluetoothAdapter mBluetoothAdapter;

    private final int MESSAGE_UPDATE = 0;

    private int mWatchForceUpdate = -1; // 当前固件是否是强制升级
    private int step = 0;//0,还未下载 1，下载完成，还未蓝牙匹配  2蓝牙匹配 完成，还未传输，3蓝牙传输中，4
    private final static int STEP_NEED_DOWNLOAD = 0;
    private final static int STEP_NEED_BT_MATCH = 1;
    private final static int STEP_NEED_BT_SCAN = 2;
    private final static int STEP_BT_TRANING = 3;
    private final static int STEP_BT_TRANS_END = 4;
    private final static int STEP_ALL_END = 5;
    private final static int STEP_FOTA_ERROR = 6;
    private final static int STEP_EXIT = 7;
    private final static int STEP_TIME_OUT = 8;

    private final static int FIRMWARE_UPDATE_TIMEOUT_MSG = 10;
    private final static int FIRMWARE_UPDATE_TIMEOUT = 720 * 1000;
    //    private final static int FIRMWARE_UPDATE_TIMEOUT = 360 * 1000 * 2; //临时修改固件升级超时时间为12分钟
    private final static int FIRMWARE_UPDATE_RECONNECT_MSG = 11;
    private final static int FIRMWARE_UPDATE_RECONNECT_TIMEOUT = 20 * 1000;

    private static int E2E_MSG_RETRY_TIMES = 0;

    private BroadcastReceiver mMsgReceiver = null;
    private BluetoothPairingReceiver mBtPairingReceiver = null;

    private static final String PROGRESS_NULL = "- %";
    private static final String SEND_OVER = "sendOver";
    private static final int MSG_SEND_DP_TIMEOUT = 60 * 1000;

    ////M : update via bt signals begin
    /// M : send bin via bt success
    public static final int FOTA_SEND_VIA_BT_SUCCESS = 2;
    // update via bin success
    public static final int FOTA_UPDATE_VIA_BT_SUCCESS = 3;
    // update via bt errors
    public static final int FOTA_UPDATE_VIA_BT_COMMON_ERROR = -1;
    // FP write file failed
    public static final int FOTA_UPDATE_VIA_BT_WRITE_FILE_FAILED = -2;
    // FP disk full error
    public static final int FOTA_UPDATE_VIA_BT_DISK_FULL = -3;
    // FP data transfer failed
    public static final int FOTA_UPDATE_VIA_BT_DATA_TRANSFER_ERROR = -4;
    // FP update Fota trigger failed
    public static final int FOTA_UPDATE_VIA_BT_TRIGGER_FAILED = -5;
    // FP update fot failed
    public static final int FOTA_UPDATE_VIA_BT_FAILED = -6;

    public static final int FILE_NOT_FOUND_ERROR = -100;
    public static final int READ_FILE_FAILED = -101;
    //// gmobi define
    // data send timeout
    public static final int FOTA_UPDATE_TIMEOUT = -200;
    // bt disconnect
    public static final int FOTA_UPDATE_DISCONNECT = -201;
    public static final int FOTA_UPDATE_SEND_PROGRESS = 100;

    public static final int FOTA_RECODE_MD5_ERROR = -10;//蓝牙传输后MD5校验失败

    private static final UUID ANDROID_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private static final String OWER_CNF = "MTKSPPForMMI";
    private static final String FOTA = "FOTA";

    private ImibabyApp mApp = null;

    private long updateStartTime = 0;

    private int tmpAutoUpgrade = 1;
    private WatchData mCurWatch;
    private String watchVerInfoDesc;
    private ImageButton btnAutoUpdate;
    private TextView tvAutoUpdateDesc;
    // 新增仅wifi下下载固件
    private ImageButton btnUpdatebinOnlyWifi;
    private ConstraintLayout layoutUpdatebinOnlyWifi;

    private View btnHelp;
    private String curVer;
    private boolean watchUpdateChecked = false;
    private boolean appUpdateChecked = false;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_update);

        mApp = (ImibabyApp) getApplication();
        mCurWatch = mApp.getCurUser().getFocusWatch();
        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.setting_check_update2));
        watchVerInfoDesc = getWatchVerInfoByWatch(mCurWatch);
        curVer = mCurWatch.getVerCur();
        initView();
        refreshUserVerInfo();
        mMyHandler = new UpdateHandler();
        initWatchUpdate();


        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkAppUpdateStatus(false);

        mSystemUpdateType = getIntent().getIntExtra("SystemUpdateType", -1);
        mCheckUpdate = getIntent().getIntExtra("CheckUpdate", -1);

        mApp.sdcardLog(TAGSD + "SystemUpdateType " + mSystemUpdateType + ", CheckUpdate " + mCheckUpdate);

        sendDeviceGet(mApp.getCurUser().getFocusWatch().getEid());
        mapgetWatchUpgradeState(mCurWatch.getEid());//刷新
        // 检测是否有新版本
//        if (mCheckUpdate == 1) {
//            if (checkNetworkState() == NETWORK_NOT_AVAILABLE) {
//                ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
//            } else {
//                ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.is_check_update_ing), Toast.LENGTH_SHORT);
//                myApp.checkUpdate(SystemUpdateActivity.this, 1, false);
//            }
//        }

        // APP启动时候，直接启动更新，需传入响应的信息参数，直接开始升级
//        if (mSystemUpdateType == WATCH_UPDATE) {
//            updateUpgradeInfo(true);
//            checkWatchUpdateState(false);
//        } else if (mSystemUpdateType == APP_UPDATE) {
//            updateUpgradeInfo(true);
//            startAppUpdate(false);
//        }

        mApp.sdcardLog(TAGSD + ">>>>>>>>>>>>>onCreate end ");
        mApp.setSystemUpdateActivityOpen(true);//设置flag
    }

    @Override
    protected void onDestroy() {
        mApp.sdcardLog(TAGSD + "onDestroy");
        super.onDestroy();

        //// 退出前清理所有的Handler消息、callbacks
        if (mMyHandler != null) {
            mMyHandler.removeCallbacksAndMessages(null);
        }
        if (scanHelpHandler != null) {
            scanHelpHandler.removeCallbacksAndMessages(null);
        }
        if (mDeviceConnectHandler != null) {
            mDeviceConnectHandler.removeCallbacksAndMessages(null);
        }
        if (mHandler2 != null) {
            mHandler2.removeCallbacksAndMessages(null);
        }

        E2E_MSG_RETRY_TIMES = 0;

        setStep(STEP_EXIT);

        clearReceivers();
        if (watchUpDateInfo != null) {
            File file = watchUpDateInfo.getDownloadFile();
            if (file.exists()) {
                file.delete();
            }
        }
        mApp.setSystemUpdateActivityOpen(false);//设置flag
    }

    @Override
    public void onBackPressed() {
        if (mSystemUpdateType == WATCH_UPDATE) {
            if (btnWatchUpdate.getVisibility() != View.VISIBLE
                    && getStep() < STEP_ALL_END
                    && getStep() > STEP_BT_TRANING) {
                ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.please_wait_update_complete), Toast.LENGTH_SHORT);
                return;
            } else if (btnWatchUpdate.getVisibility() != View.VISIBLE
                    && getStep() == STEP_BT_TRANING) {
                ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.please_wait_transmission_complete), Toast.LENGTH_SHORT);
                return;
            }
        } else if (mSystemUpdateType == APP_UPDATE && mAppDownloading) {
            Dialog dlg = DialogUtil.CustomNormalDialog(SystemUpdateActivity.this,
                    getString(R.string.stop_newversion_download), getString(R.string.stop_newversion_download_prompt), new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, getString(R.string.cancel), new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            cancelDownloadAppAndExit();
                        }
                    }, getString(R.string.confirm));
            dlg.show();
            return;
        }

        mSystemUpdateType = -1;
        super.onBackPressed();
    }

    private void cancelDownloadAppAndExit() {
        myApp.cancelDownNewVersion();
        mSystemUpdateType = -1;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getStep() == STEP_BT_TRANS_END) {
            long nowtime = System.currentTimeMillis();
            if (Math.abs(nowtime - updateStartTime) >= FIRMWARE_UPDATE_TIMEOUT) {
                mApp.sdcardLog(TAGSD + "onResume update firmware timeout");
                requestWatchVersion();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (btnBack == v) {
            onBackPressed();
        } else if (v == btnWatchUpdate) {
            if (mCurWatch.isDevice710() || mCurWatch.isDevice705() || mCurWatch.isDevice703() || mCurWatch.isDevice709_A03()
                    || mCurWatch.isDevice708_A06() || mCurWatch.isDevice709_A05() || mCurWatch.isDevice708_A07()) {
                showWathUpdateDesc();
                return;
            }
            if (isBluetoothUsed()) {
                return;
            }
            mSystemUpdateType = WATCH_UPDATE;

            mApp.sdcardLog(TAGSD + "onClick btnWatchUpdate curStep:" + getStep());

            if (getStep() == STEP_NEED_DOWNLOAD) {
                checkWatchUpdateState(true);
            } else if (getStep() == STEP_NEED_BT_SCAN
                    || getStep() == STEP_BT_TRANING
                    || getStep() == STEP_BT_TRANS_END) {
                setWatchUpdateBtnEnabled(false);
                setStep(STEP_NEED_BT_SCAN);
                startBTScan();
            } else if (getStep() == STEP_NEED_BT_MATCH) {
                setWatchUpdateBtnEnabled(false);
                e2e();
            } else if (getStep() == STEP_FOTA_ERROR || getStep() == STEP_TIME_OUT) {
                startWatchUpdate();
            } else {
                mApp.sdcardLog(TAGSD + "Error Step");
            }
        } else if (v == btnAppUpdate) {
            mSystemUpdateType = APP_UPDATE;
            if (getString(R.string.install).equals(btnAppUpdate.getText().toString())) {
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            } else {
                checkAppUpdateStatus(true);
            }
        } else if (v == btnAutoUpdate) {//自动升级开关
            if (mCurWatch.getAutoUpdate() == 1) {
                tmpAutoUpgrade = 0;
            } else {
                tmpAutoUpgrade = 1;
            }
            mapSetMsg(CloudBridgeUtil.WATCH_AUTO_UPGRADE, Integer.toString(tmpAutoUpgrade));
        } else if (v == btnUpdatebinOnlyWifi) {//仅wifi下下载开关
            int updateOnlyWifi;
            if (mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
                updateOnlyWifi = mApp.getIntValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, 0);
            } else {
                updateOnlyWifi = mApp.getIntValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, 1);
            }
            if (updateOnlyWifi == 0)
                mapSetMsg(CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, "1");
            else
                mapSetMsg(CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, "0");
        } else if (v == btnWatchVerinfo) {
            if (watchVerInfoDesc != null) {
                DialogUtil.CustomNormalDialog(SystemUpdateActivity.this,
                        getString(R.string.newversion_details),
                        watchVerInfoDesc,
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        },
                        getText(R.string.donothing_text).toString()).show();
            }
        } else if (v == btnHelp) {
            startActivity(myApp.getHelpCenterIntent(SystemUpdateActivity.this, "systemUpdate"));
        }
    }

    private void initView() {

        mRoundProgressBar1 = findViewById(R.id.round_progressbar_1);
        btnBack = findViewById(R.id.iv_title_back);
        btnBack.setOnClickListener(this);
        btnHelp = findViewById(R.id.ib_help_web);
        btnHelp.setVisibility(View.GONE);
        btnHelp.setOnClickListener(this);

        btnAutoUpdate = findViewById(R.id.btn_atuo_update);
        btnAutoUpdate.setOnClickListener(this);
        tvAutoUpdateDesc = findViewById(R.id.tv_atuo_update_desc);
        lineWatchUpdate = findViewById(R.id.watch_update);
        btnWatchVerinfo = findViewById(R.id.btn_watch_info);
        btnWatchVerinfo.setOnClickListener(this);
        btnAppUpdate = findViewById(R.id.btn_app_update);
        btnAppUpdate.setVisibility(View.INVISIBLE);
        btnAppUpdate.setOnClickListener(this);
        btnWatchUpdate = findViewById(R.id.btn_watch_update);
        btnWatchUpdate.setVisibility(View.INVISIBLE);
        btnWatchUpdate.setOnClickListener(this);
        tvState = findViewById(R.id.tv_state);
        ivState = findViewById(R.id.iv_state);
        tvAppVersion = findViewById(R.id.tv_app_version);
        tvWatchVersion = findViewById(R.id.tv_watch_version);
        tvWatchNewVersion = findViewById(R.id.tv_watch_new_version);
        btnAppHaveUpdate = findViewById(R.id.btn_app_have_update);
        btnWatchHaveUpdate = findViewById(R.id.btn_watch_have_update);
        watchLoadingBar = findViewById(R.id.watch_loading_bar);

        layoutUpdatebinOnlyWifi = findViewById(R.id.layout_updatebin_only_wifi);
        btnUpdatebinOnlyWifi = findViewById(R.id.btn_updatebin_only_wifi);
        btnUpdatebinOnlyWifi.setOnClickListener(this);

        btnAppHaveUpdate.setVisibility(View.INVISIBLE);
        btnWatchHaveUpdate.setVisibility(View.INVISIBLE);

        watchLoadingBar.setVisibility(View.GONE);

        tvState.setVisibility(View.INVISIBLE);
    }

    private void initWatchUpdate() {
        scanHelpHandler = new Handler();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initNetMsgReceiver();

        mBtPairingReceiver = new BluetoothPairingReceiver();
        IntentFilter btFilter = new IntentFilter();
        btFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        registerReceiver(mBtPairingReceiver, btFilter);

        Serializable obj = getIntent().getSerializableExtra(WatchUpDateInfo.WATCH_UPDATE_INFO);
        if (null != obj) {
            watchUpDateInfo = (WatchUpDateInfo) obj;
            getIntent().putExtra(EXTRA, watchUpDateInfo.getBtMac());
        } else {
            mApp.sdcardLog(TAGSD + "initWatchUpdate watchUpDateInfo is null");
        }
    }

    private void setWatchUpdateBtnEnabled(boolean enable) {
        if (enable) {
            btnWatchUpdate.setTextColor(getResources().getColor(R.color.color_13));
        } else {
            btnWatchUpdate.setTextColor(getResources().getColor(R.color.color_9));
        }
        btnWatchUpdate.setEnabled(enable);
    }

    private void setAppUpdateBtnEnabled(String text, boolean enable) {
        Log.d("hahahha2", "setAppUpdateBtnEnabled: " + text);
        btnAppUpdate.setText(text);
        if (enable) {
            btnAppUpdate.setTextColor(getResources().getColor(R.color.color_13));
        } else {
            btnAppUpdate.setTextColor(getResources().getColor(R.color.color_9));
        }
        btnAppUpdate.setEnabled(enable);
    }

    private void refreshUserVerInfo() {

        setAutoUpgradeView();
        setUpgradeOnlyWifiView();

        String curAppVer = Params.getInstance(getApplicationContext()).getAppVerName();

        StringBuilder appVerBuff = new StringBuilder();
        appVerBuff.append(getString(R.string.current_version));
        appVerBuff.append(": ");
        appVerBuff.append(curAppVer);
        tvAppVersion.setText(appVerBuff.toString());
        if (myApp.getCurUser().getFocusWatch() == null) {
            return;
        }
        String curWatchVer = myApp.getCurUser().getFocusWatch().getVerCur();
        if (curWatchVer != null && curWatchVer.length() > 20)
            curWatchVer = curWatchVer.substring(15, 18 + 5);

        StringBuilder watchVerBuff = new StringBuilder();
        watchVerBuff.append(getString(R.string.current_version));
        watchVerBuff.append(": ");
        if (curWatchVer != null) watchVerBuff.append(curWatchVer);
        tvWatchVersion.setText(watchVerBuff.toString());

        if (watchVerInfoDesc == null) {
            btnWatchVerinfo.setVisibility(View.INVISIBLE);
        }
    }

    private void startWatchUpdate() {
        if (checkNetworkState() == NETWORK_NOT_AVAILABLE) {
            mApp.sdcardLog(TAGSD + "startWatchUpdate notwork not available.");
            ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
            return;
        }
        if (mBluetoothAdapter == null) {
            mApp.sdcardLog(TAGSD + "BT Adapter is null");
            ToastUtil.showMyToast(this, getString(R.string.error_bluetooth_not_supported),
                    Toast.LENGTH_SHORT);
            setStep(STEP_FOTA_ERROR);
            return;
        }

        // focuswatch为空时不让升级
        if (myApp.getCurUser().getFocusWatch() == null) {
            updateStatusText(getString(R.string.get_device_info_fail));
            setStep(STEP_FOTA_ERROR);
            return;
        }

        // 判断电量
        final WatchData watch = myApp.getCurUser().getFocusWatch();
//        if (watch.getBattery() <= 60) {
//            ToastUtil.showMyToast(getApplicationContext(), getString(R.string.device_battery_low), Toast.LENGTH_SHORT);
//            btnWatchUpdate.setVisibility(View.VISIBLE);
//            return;
//        }

        boolean btEnabled = mBluetoothAdapter.isEnabled();
        if (!btEnabled) {
            if (!mBluetoothAdapter.enable()) {
                mApp.sdcardLog(TAGSD + "BT enable fail");
                updateStatusText(R.string.blu_open_error);
                setStep(STEP_FOTA_ERROR);
                return;
            }
        }

        ivState.setBackgroundResource(R.drawable.up_mark);
        setBackgroundColorByUpdateStatus(true);
        btnWatchUpdate.setVisibility(View.GONE);
        tvState.setVisibility(View.VISIBLE);
        watchLoadingBar.setVisibility(View.VISIBLE);

        if (watchUpDateInfo == null) {
            mApp.sdcardLog(TAGSD + "startWatchUpdate - watchUpDateInfo is null, get it again.");
            getWatchUpdateDescAndUpdateWatchInfo();
            if (watchUpDateInfo == null) {
                mApp.sdcardLog(TAGSD + "startWatchUpdate - ERROR can't get watchUpDateInfo");
                updateStatusText(getString(R.string.get_device_info_fail));
                setStep(STEP_FOTA_ERROR);
                return;
            }
        }

        //step-2:e2e指令
        setStep(STEP_NEED_DOWNLOAD);
        startDownloadBin();

        mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
        mMyHandler.sendEmptyMessageDelayed(FIRMWARE_UPDATE_TIMEOUT_MSG, FIRMWARE_UPDATE_TIMEOUT);
    }

    private void checkWatchUpdateState(final boolean needShowUpdateDesc) {

        int curNetwork = checkNetworkState();
        if (curNetwork == NETWORK_NOT_AVAILABLE) {
            ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
        } else if (curNetwork == NETWORK_MOBILE_ENABLED) {
            Dialog dlg = DialogUtil.CustomNormalDialog(SystemUpdateActivity.this,
                    getString(R.string.download_in_mobilenet_prompt), null, new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            btnWatchUpdate.setVisibility(View.VISIBLE);
                        }
                    }, getString(R.string.cancel), new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            if (needShowUpdateDesc) {
                                showWathUpdateDesc();
                            } else {
                                startWatchUpdate();
                            }
                        }
                    }, getString(R.string.goon));
            dlg.show();
        } else {
            if (needShowUpdateDesc) {
                showWathUpdateDesc();
            } else {
                startWatchUpdate();
            }
        }
    }

    private void showWathUpdateDesc() {
        String updateDesc = getWatchUpdateDescAndUpdateWatchInfo();
        Dialog dlg = DialogUtil.CustomNormalDialog(SystemUpdateActivity.this,
                getString(R.string.title_watch_update),
                updateDesc,
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        btnWatchUpdate.setVisibility(View.VISIBLE);
                    }
                },
                getText(R.string.update_later).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurWatch.isDevice710() || mCurWatch.isDevice705() || mCurWatch.isDevice703()
                                || mCurWatch.isDevice709_A03() || mCurWatch.isDevice708_A06()
                                || mCurWatch.isDevice709_A05() || mCurWatch.isDevice708_A07()) {
                            if (myApp.getNetService() != null)
                                myApp.getNetService().sendWatchUpdateE2eMsg(mCurWatch.getEid());
                        } else {
                            startWatchUpdate();
                        }
                    }
                },
                getText(R.string.update_now).toString());
        dlg.show();
    }

    private String getWatchUpdateNewVersionName() {
        WatchData watch = myApp.getCurUser().getFocusWatch();
        String updateDesc = null;
        String reqstr = myApp.getWatchUpdateReqJsonString(watch);
        String updateInfo = myApp.getWatchUpdateJsonByReq(reqstr);
        if (updateInfo != null) {
            try {
                org.json.JSONObject watchJo = new org.json.JSONObject(updateInfo);
                String watchVer = watch.getVerCur();

                if (watchVer != null && watchVer.length() > 7) {
                    updateDesc = watchJo.getString("ver");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return updateDesc;
    }

    private String getWatchUpdateDescAndUpdateWatchInfo() {
        WatchData watch = myApp.getCurUser().getFocusWatch();
        String updateDesc = null;
        String reqstr = myApp.getWatchUpdateReqJsonString(watch);
        String updateInfo = myApp.getWatchUpdateJsonByReq(reqstr);
        if (updateInfo != null) {
            try {
                org.json.JSONObject watchJo = new org.json.JSONObject(updateInfo);
                String watchVer = watch.getVerCur();

                if (watchVer != null && watchVer.length() > 7) {
                    updateDesc = watchJo.getString("description");
                    String url = null;
                    if (watchJo.getString("oldver").equals(watchVer)) {
                        url = watchJo.getString(CloudBridgeUtil.KEY_NAME_DOWNLOAD_URL);

                    }
                    if (url != null) {
                        if (watchUpDateInfo == null) {
                            watchUpDateInfo = new WatchUpDateInfo();
                        }
                        watchUpDateInfo.setFullJsonStr(updateInfo);
                        watchUpDateInfo.setCurVer(watchVer);
                        watchUpDateInfo.setBtMac(myApp.getCurUser().getFocusWatch().getBtMac());
                        watchUpDateInfo.setNewVerName(watchJo.getString("ver"));
                        watchUpDateInfo.setMd5(watchJo.getString("md5"));
                        String name = "update.bin";
                        final File file = new File(ImibabyApp.getSaveDir(), name);
                        watchUpDateInfo.setDownloadFile(file);
                        watchUpDateInfo.setDownLoadUrl(url);
                        getIntent().putExtra(EXTRA, watchUpDateInfo.getBtMac());
                        mWatchForceUpdate = myApp.checkForceResult(watchJo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return updateDesc;
    }


    private void checkAppUpdateStatus(final boolean needInstall) {


        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();

        appUpdateInfo.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("hahahha2", "onSuccess" + appUpdateInfo.availableVersionCode() + "---" + appUpdateInfo.updateAvailability() + " , installStatus=" + appUpdateInfo.installStatus());

                if (!needInstall) {
                    if (appUpdateInfo.availableVersionCode() > AndroidUtil.getVersionCode(SystemUpdateActivity.this)) {
                        mAppHaveNewVerion = true;
                        updateUpgradeInfo(false);
                        setAppUpdateBtnEnabled(getString(R.string.app_update), true);
                    }
                }

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADING) {
                    Log.d("hahahha2", "下载中");
                    setAppUpdateBtnEnabled(getString(R.string.app_downloading), false);
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    Log.d("hahahha2", "下载完成" + needInstall);
                    if (needInstall)
                        appUpdateManager.completeUpdate();
                    else
                        setAppUpdateBtnEnabled(getString(R.string.install), true);

                }

                if (!needInstall) return;


                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    Log.d("hahahha", "Request the update........");
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                SystemUpdateActivity.this,
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @SuppressLint("HandlerLeak")
    class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (mSystemUpdateType == WATCH_UPDATE && getStep() == STEP_EXIT) {
                return;
            }
            // TODO Auto-generated method stub
            switch (msg.what) {
                case Const.UPDATE_DOWN_PROGRESS:
                    long offset = (Long) msg.peekData().get("offset");
                    long total = (Long) msg.peekData().get("total");
                    //refresh it
                    totalPercent = (int) (30 * offset / total);
                    mRoundProgressBar1.setProgress(totalPercent);
                    updateStatusText(getString(R.string.is_download_ing));
                    break;
                case Const.UPDATE_DOWN_OK:
                    if (getStep() != STEP_NEED_DOWNLOAD) {
                        mApp.sdcardLog(TAGSD + "UpdateHandler UPDATE_DOWN_OK cur step is " + step + ", return");
                        return;
                    }
                    boolean checkMd5 = (Boolean) msg.peekData().get("checkMd5");
                    if (checkMd5) {
                        //download ok
                        updateStatusText(getString(R.string.download_success));
                        mApp.sdcardLog(TAGSD + "UpdateHandler UPDATE_DOWN_OK, send e2e");
                        setStep(STEP_NEED_BT_MATCH);
                        e2e();
                    } else {
                        mApp.sdcardLog(TAGSD + "UpdateHandler firmware md5 check failed.");
                        updateStatusText(getString(R.string.device_newversion_md5check_fail));
                        setStep(STEP_FOTA_ERROR);
                    }
                    break;
                case Const.UPDATE_DOWN_ERROR:
                    mApp.sdcardLog(TAGSD + "UpdateHandler UPDATE_DOWN_ERROR - network error.");
                    updateStatusText(getString(R.string.network_error_prompt));
                    setBackgroundColorByUpdateStatus(false);
                    setStep(STEP_FOTA_ERROR);
                    break;
                case FIRMWARE_UPDATE_TIMEOUT_MSG:
                    mApp.sdcardLog(TAGSD + "update firmware timeout");
                    requestWatchVersion();
                    break;
                case Const.UPDATE_DOWN_APP_PROGRESS:
                    long appoffset = (Long) msg.peekData().get("offset");
                    long apptotal = (Long) msg.peekData().get("total");
                    //refresh it
                    totalPercent = (int) (100 * appoffset / apptotal);
                    mRoundProgressBar1.setProgress(totalPercent);
                    updateStatusText(getString(R.string.is_download_app_ing));
                    mAppDownloading = true;
                    break;
                case Const.UPDATE_DOWN_APP_OK:
                    //String appfilepath = (String) msg.peekData().get("filepath");
                    tvState.setVisibility(View.INVISIBLE);
                    btnAppUpdate.setVisibility(View.VISIBLE);
                    mRoundProgressBar1.setProgress(100);
                    setWatchUpdateBtnEnabled(true);

                    mAppDownloading = false;
                    break;
                case Const.UPDATE_DOWN_APP_ERROR:
                    mApp.sdcardLog(TAGSD + "UpdateHandler UPDATE_DOWN_APP_ERROR - new APP download error.");
                    updateStatusText(getString(R.string.network_error_prompt));
                    mRoundProgressBar1.setProgress(100);
                    setBackgroundColorByUpdateStatus(false);
                    setWatchUpdateBtnEnabled(true);
                    mAppDownloading = false;
                    break;
                case FIRMWARE_UPDATE_RECONNECT_MSG:
                    LogUtil.d(TAG + "  " + "reconnect device");
                    reconnectDevice();
                    break;
                case Const.UPDATE_CONNECT_BLUETOOTH_TIMEOUT:
                    if (mTryConnectBLTimes >= 3) {
                        mTryConnectBLTimes = 0;
                        updateStatusText(getString(R.string.bluetooth_content_timeout));
                        setStep(STEP_FOTA_ERROR);
                    } else {
                        if (mBluetoothConnectTask != null) {
                            mBluetoothConnectTask.cancel(true);
                            mBluetoothConnectTask = null;
                        }
                        if (watchUpDateInfo != null) {
                            connectBluetooth(watchUpDateInfo.getBtMac());
                        } else {
                            updateStatusText(getString(R.string.bluetooth_content_timeout));
                            setStep(STEP_FOTA_ERROR);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setBtnWatchUpdateText(final String txt) {
        runOnUiThread(new Runnable() {
            public void run() {
                btnWatchUpdate.setVisibility(View.VISIBLE);
                btnWatchUpdate.setText(txt);
            }
        });
    }

    private void updateStatusText(final String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                tvState.setVisibility(View.VISIBLE);
                tvState.setText(str);
            }
        });
    }

    private void updateStatusText(final int strId) {
        runOnUiThread(new Runnable() {
            public void run() {
                tvState.setVisibility(View.VISIBLE);
                tvState.setText(strId);
            }
        });
    }

    private void startDownloadBin() {
        if (watchUpDateInfo == null) {
            mApp.sdcardLog(TAGSD + "NOTE startDownloadBin watchUpDateInfo is null");
            getWatchUpdateDescAndUpdateWatchInfo();
        }

        if (watchUpDateInfo != null) {
            updateStatusText(getString(R.string.is_download_ing));
            mApp.sdcardLog(TAGSD + "startDownloadBin:" + watchUpDateInfo.getDownLoadUrl());
            myApp.downWatchBin(watchUpDateInfo.getDownLoadUrl(), mMyHandler, watchUpDateInfo.getMd5());
        } else {
            mApp.sdcardLog(TAGSD + "startDownloadBin: watchUpDateInfo is null");
            updateStatusText(getString(R.string.get_bin_info_fail));
            setStep(STEP_FOTA_ERROR);
        }
    }

    private void initNetMsgReceiver() {
        mMsgReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String resp;
                JSONObject respPl;
                if (intent.getAction().equals(Const.ACTION_FIRMWARE_UPDATE_CHECKRESULT)) {
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            //对方回复的
                            int recode = CloudBridgeUtil.getCloudMsgRC(respPl);
                            // 判断当前走到第几步，以免出现和doCallback中已经处理了消息，而这个消息延迟后面才来，导致界面异常的问题出现
                            int curStep = getStep();

                            mApp.sdcardLog(TAGSD + "FIRMWARE_UPDATE_CHECKRESULT recode " + recode + ", curStep " + curStep);

                            if (recode == 1) {//已经下载完成，立即触发升级
                                updateStatusText(R.string.download_succeed_via_bt);
                                setStep(STEP_BT_TRANS_END);
                            } else if (recode == 0) {
                                //ok
                                setStep(STEP_NEED_BT_SCAN);
                                //start trans
                                startBTScan();
                            } else if (recode == -1) {
                                updateStatusText(getString(R.string.device_battery_low));
                                setStep(STEP_FOTA_ERROR);
                            } else if (recode == FOTA_RECODE_MD5_ERROR) {//MD5校验失败
                                updateStatusText(R.string.ud_er);
                                setStep(STEP_FOTA_ERROR);
                            } else {
                                String btResp = (String) respPl.get(CloudBridgeUtil.KEY_NAME_BT_MAC);
                                String verResp = (String) respPl.get(CloudBridgeUtil.KEY_NAME_VERSION_CUR);

                                //bt or ver not match
                                if (!btResp.equals(myApp.getCurUser().getFocusWatch().getBtMac())) {
                                    mApp.sdcardLog(TAGSD + "bt not match-btResp=" + btResp);
                                    //bt not match
                                    watchUpDateInfo.setBtMac(btResp);
                                    myApp.getCurUser().getFocusWatch().setBtMac(btResp);
                                    getIntent().putExtra(EXTRA, watchUpDateInfo.getBtMac());
                                    setStep(STEP_NEED_BT_MATCH);
                                    e2e();
                                } else if (!verResp.equals(watchUpDateInfo.getCurVer())) {
                                    mApp.sdcardLog(TAGSD + "onReceive ver not match-verResp=" + verResp);
                                    //ver not match
                                    watchUpDateInfo.setCurVer(verResp);
                                    myApp.getCurUser().getFocusWatch().setVerCur(verResp);
                                    WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                                    // 上传正确版本到服务器
                                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_VERSION_CUR, verResp);
                                    refreshUserVerInfo();
                                    setStep(STEP_NEED_DOWNLOAD);
                                    myApp.checkUpdateWatch(SystemUpdateActivity.this, false, mApp.getCurUser().getFocusWatch());
                                    //找不到对应的版本
                                    setStep(STEP_FOTA_ERROR);
                                    updateStatusText(getString(R.string.device_vision_not_match));
                                    ToastUtil.showMyToast(SystemUpdateActivity.this, getString(R.string.is_retry_checkupdate_ing), Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_RECEIVE_OTA_RESULT)) {
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        String seid = CloudBridgeUtil.getCloudMsgSEID((JSONObject) JSONValue.parse(resp));
                        assert seid != null;
                        if (!seid.equals(myApp.getCurUser().getFocusWatch().getEid())) {
                            return;
                        }
                        if (respPl != null) {
                            int recode = CloudBridgeUtil.getCloudMsgRC(respPl);
                            mApp.sdcardLog(TAGSD + "receive update result " + recode);

                            if (getStep() != STEP_BT_TRANS_END) {
                                mApp.sdcardLog(TAGSD + "CURRENT STEP is not BT_TRANS_END, ignore this msg.");
                                return;
                            }

                            if (recode == 1) {
                                //reset watch verinfo
                                if (watchUpDateInfo != null) {
                                    mApp.sdcardLog(TAGSD + "ACTION_RECEIVE_OTA_RESULT frameware update success " + watchUpDateInfo.getNewVerName());
                                    myApp.getCurUser().getFocusWatch().setVerCur(watchUpDateInfo.getNewVerName());
                                    WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                                    //send user set
                                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_VERSION_CUR, watchUpDateInfo.getNewVerName());
                                } else {
                                    mApp.sdcardLog(TAGSD + "ACTION_RECEIVE_OTA_RESULT NOTE watchUpDateInfo is null");
                                }
                                //refresh watch data
                                //setBtnWatchUpdateText("完成");
                                setStep(STEP_ALL_END);
                            } else if (recode == 0) {
                                updateStatusText(R.string.ud_er);
                                setStep(STEP_FOTA_ERROR);
                            }
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_APP_UPGRADE_RESULT)) {
                    String type = intent.getExtras().getString("type", "app");
                    if (type.equals("watch")) {
                        watchUpdateChecked = true;
                    } else {
                        appUpdateChecked = true;
                    }
                    updateUpgradeInfo(true);
                } else if (intent.getAction().equals(Const.ACTION_WATCH_VERINFO_RESULT)) {
                    watchVerInfoDesc = getWatchVerInfoByWatch(mCurWatch);
                    if (watchVerInfoDesc == null) {
                        btnWatchVerinfo.setVisibility(View.INVISIBLE);
                    } else {
                        btnWatchVerinfo.setVisibility(View.VISIBLE);
                    }

                } else if (intent.getAction().equals(Const.ACTION_APP_DOWNLOADED)) {
                    Log.d("hahahha2", "首页下载app成功");
                    setAppUpdateBtnEnabled(getString(R.string.install), true);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_FIRMWARE_UPDATE_CHECKRESULT);
        filter.addAction(Const.ACTION_RECEIVE_OTA_RESULT);
        filter.addAction(Const.ACTION_APP_UPGRADE_RESULT);
        filter.addAction(Const.ACTION_WATCH_VERINFO_RESULT);
        filter.addAction(Const.ACTION_APP_DOWNLOADED);

        registerReceiver(mMsgReceiver, filter);
    }

    private void updateUpgradeInfo(boolean showToast) {

        if (myApp.showWatchUpdateResult(SystemUpdateActivity.this, true, false) > 0) {
            mWatchHaveNewVerion = true;
        }

        mApp.sdcardLog(TAGSD + "App Updrage result " + mAppHaveNewVerion + "," + mWatchHaveNewVerion);

        if (showToast && !mAppHaveNewVerion && !mWatchHaveNewVerion) {
            if (appUpdateChecked && watchUpdateChecked) {
                ToastUtil.showMyToast(getApplicationContext(), getText(R.string.no_update).toString(), Toast.LENGTH_SHORT);
            }
        }

        lineWatchUpdate.setVisibility(mWatchHaveNewVerion ? View.VISIBLE : View.GONE);

        btnAppUpdate.setVisibility(mAppHaveNewVerion ? View.VISIBLE : View.INVISIBLE);
        if (mApp.getCurUser().getFocusWatch().isDevice306() || mApp.getCurUser().getFocusWatch().isDevice206()) {
            btnWatchUpdate.setVisibility(View.GONE);
        } else {
            btnWatchUpdate.setVisibility(mWatchHaveNewVerion ? View.VISIBLE : View.INVISIBLE);
        }

        btnAppHaveUpdate.setVisibility(mAppHaveNewVerion ? View.VISIBLE : View.INVISIBLE);
        btnWatchHaveUpdate.setVisibility(mWatchHaveNewVerion ? View.VISIBLE : View.INVISIBLE);


        if (watchLoadingBar.getVisibility() == View.VISIBLE) {
            btnWatchUpdate.setVisibility(View.INVISIBLE);
        }

        if (mWatchHaveNewVerion) {
            setStep(STEP_NEED_DOWNLOAD);
            String newWatchVer = getWatchUpdateNewVersionName();
            if (newWatchVer != null && newWatchVer.length() > 20)
                newWatchVer = newWatchVer.substring(15, 18 + 5);
            StringBuilder watchVerBuff = new StringBuilder();
            watchVerBuff.append(getString(R.string.new_version));
            if (newWatchVer != null) {
                watchVerBuff.append(newWatchVer);
                tvWatchNewVersion.setText(watchVerBuff.toString());
            }
        }

    }

    private void setBackgroundColorByUpdateStatus(boolean status) {
        if (status) {
            setTintColor(getResources().getColor(R.color.bg_color_orange));
            findViewById(R.id.system_update_ui).setBackgroundColor(getResources().getColor(R.color.bg_color_orange));
            findViewById(R.id.title_include).setBackgroundColor(getResources().getColor(R.color.bg_color_orange));
        } else {
            setTintColor(getResources().getColor(R.color.system_update_fail));
            findViewById(R.id.system_update_ui).setBackgroundColor(getResources().getColor(R.color.system_update_fail));
            findViewById(R.id.title_include).setBackgroundColor(getResources().getColor(R.color.system_update_fail));
        }
    }

    private void updatePreference(int newState, int oldState) {
        if (getStep() == STEP_BT_TRANS_END) {

            return;
        }

        // 解决手表升级开机后重新开机连接手机，导致当前界面状态显示异常(当前步骤大于传输完成时不在刷新界面)
        if (getStep() >= STEP_BT_TRANS_END) {
            mApp.sdcardLog(TAGSD + "[updatePreference] currentSetp " + getStep() + ", return");
            return;
        }

        if (getStep() == STEP_NEED_BT_SCAN) {
            if (newState == 4 && oldState == 2) {
                mApp.sdcardLog(TAGSD + "connect fial, STEP_FOTA_ERROR.");
                updateStatusText(R.string.disconnected);
                setStep(STEP_FOTA_ERROR);
                return;
            }
        }
    }

    private Handler mDeviceConnectHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    updatePreference(msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };

    private void reconnectDevice() {
        String address = watchUpDateInfo == null ? null : watchUpDateInfo.getBtMac();
        if (address == null) {
            mApp.sdcardLog(TAGSD + "reconnectDevice - bt address is null.");
            mMyHandler.removeMessages(FIRMWARE_UPDATE_RECONNECT_MSG);
            return;
        }
        return;
    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mMsgReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            unregisterReceiver(mBtPairingReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /// M: auto connect RemoteDevice by Hotknot @{
    public static final String EXTRA = "com.mediatek.hotknot.extra.DATA";

    public void handleIntent(Intent intent) {
        mApp.sdcardLog(TAGSD + "[handleIntent] begin");
        if (intent != null && intent.hasExtra(EXTRA)) {
            String targetAddress = intent.getStringExtra(EXTRA);
            if (targetAddress.equals(watchUpDateInfo.getBtMac())) {
                autoConnectDevice(targetAddress);
            } else {
                mApp.sdcardLog(TAGSD + "[handleIntent] bt addr don't match.");
                updateStatusText(getString(R.string.bluetooth_address_get_fail));
                setStep(STEP_FOTA_ERROR);
            }
        } else {
            mApp.sdcardLog(TAGSD + "[handleIntent] EXTRA is null");
            updateStatusText(getString(R.string.bluetooth_address_get_fail));
            setStep(STEP_FOTA_ERROR);
        }
    }

    private void autoConnectDevice(final String address) {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            mApp.sdcardLog(TAGSD + "[autoConnectDevice] invalid BT address");
            updateStatusText(getString(R.string.bluetooth_content_timeout));
            setStep(STEP_FOTA_ERROR);
            return;
        }
        connectBluetooth(address);
    }

    private AsyncTask<String, Void, Boolean> mBluetoothConnectTask = null;
    private int mTryConnectBLTimes = 0;

    @SuppressLint("StaticFieldLeak")
    private void connectBluetooth(String address) {
        if (mBluetoothConnectTask != null) {
            return;
        }
        if (mBluetoothConnectTask == null) {
            mBluetoothConnectTask = new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(params[0]);
                    try {
                        mSocket = device.createRfcommSocketToServiceRecord(ANDROID_UUID);
                        mBluetoothAdapter.cancelDiscovery();
                        mSocket.connect();
                    } catch (final Exception e) {
                        mApp.sdcardException(TAGSD, e);

                        try {
                            mSocket.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        return false;
                    }
                    return true;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    updateStatusText(R.string.bt_connecting);
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    if (success) {
                        mTryConnectBLTimes = 0;
                        updateStatusText(R.string.blu_connect_succ);
                        if (btnWatchUpdate != null) btnWatchUpdate.setVisibility(View.INVISIBLE);
                        initFota();
                    } else {
                        mTryConnectBLTimes++;
                        mMyHandler.sendMessageDelayed(mMyHandler.obtainMessage(Const.UPDATE_CONNECT_BLUETOOTH_TIMEOUT), 1000);
                    }
                }
            };
            mBluetoothConnectTask.execute(address);

        }
    }


    private boolean startBTScan() {
        mApp.sdcardLog(TAGSD + "startBTScan");
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            scanHelpHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //HotKnot feature
                    handleIntent(getIntent());
                }
            }, 5000);
            return true;
        } else {
            // 判断当前的蓝牙状态，有出现过这个时候蓝牙没有打开的情况，需要更新界面状态
            mApp.sdcardLog(TAGSD + "startBTScan: bluetooth is off");
            updateStatusText(R.string.blu_open_error);
            setStep(STEP_FOTA_ERROR);
        }
        return false;
    }

    private boolean isConnectWithBluetooth() {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State bluetoothstate = connectManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState();
            if (NetworkInfo.State.CONNECTED == bluetoothstate) {
                mApp.sdcardLog(TAGSD + "isConnectWithBluetooth YES");
                return true;
            }
        } catch (Exception e) {
            LogUtil.d("isConnectWithBluetooth - Do not support BLUETOOTH Ethernet");
        }
        return false;
    }

    private boolean isBluetoothUsed() {
        if (isConnectWithBluetooth()) {
            updateStatusText(getString(R.string.is_connect_network_by_bluetooth));
            setStep(STEP_FOTA_ERROR);
            return true;
        }

        int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);          //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
        int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);    //蓝牙头戴式耳机，支持语音输入输出
        int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);      //蓝牙穿戴式设备

        mApp.sdcardLog(TAGSD + "isBluetoothUsed " + a2dp + " " + headset + " " + health);

        if (a2dp == BluetoothProfile.STATE_CONNECTED || headset == BluetoothProfile.STATE_CONNECTED || health == BluetoothProfile.STATE_CONNECTED) {
            updateStatusText(getString(R.string.bluetooth_connect_already));
            setStep(STEP_FOTA_ERROR);
            return true;
        }
        return false;
    }

    private void e2e() {
        e2eFirmwareUpdateStart();
    }

    private void initFota() {
        if (getStep() < STEP_BT_TRANING) {
            scanHelpHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startTransmitTask();
                }
            }, 3000);
            setStep(STEP_BT_TRANING);
        }
    }

    private AsyncTask<Void, Void, Boolean> mTransmitTask = null;

    private void resetTransmitTask() {
        try {
            mTransmitTask.cancel(true);
        } catch (Exception e) {

        }
        mTransmitTask = null;
    }

    @SuppressLint("StaticFieldLeak")
    private void startTransmitTask() {
        if (mTransmitTask != null) {
            mApp.sdcardLog(TAGSD + "startTransmitTask mTransmitTask is not null");
            return;
        }

        if (mTransmitTask == null) {
            mTransmitTask = new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        mOutputStream = mSocket.getOutputStream();
                        mOutputStream.write(OWER_CNF.getBytes());
                        Thread.sleep(100);
                        mOutputStream.write(FOTA.getBytes());
                        Thread.sleep(100);
                        byte[] lengthBuffer = new byte[4];
                        int length = (int) getInputStreamFromFile(watchUpDateInfo.getDownloadFile().getAbsolutePath());
                        lengthBuffer[3] = (byte) (length & 0xff);
                        lengthBuffer[2] = (byte) (length >> 8 & 0xff);
                        lengthBuffer[1] = (byte) (length >> 16 & 0xff);
                        lengthBuffer[0] = (byte) (length >> 24 & 0xff);

                        mOutputStream.write(lengthBuffer);

                        byte[] sendBuffer = new byte[2048];
                        int len = mInputStream.read(sendBuffer);
                        while (len != -1) {
                            mOutputStream.write(sendBuffer, 0, len);
                            Thread.sleep(100);
                            len = mInputStream.read(sendBuffer);
                        }

                        byte[] hashCode = MD5.getFileMD5(watchUpDateInfo.getDownloadFile().getAbsolutePath());
                        mOutputStream.write(hashCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean sucess) {
                    super.onPostExecute(sucess);
                    if (sucess) {
                        updateStatusText(R.string.download_succeed_via_bt);
                        setStep(STEP_BT_TRANS_END);
                    } else {
                        updateStatusText(R.string.ud_er_send);
                        setStep(STEP_FOTA_ERROR);
                    }
                }
            };
            mTransmitTask.execute();
        }
    }

    public long getInputStreamFromFile(String filePath) {
        File f = new File(filePath);
        try {
            mInputStream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return f.length();
    }


    private void sendDeviceSet(String key, Object value) {
        MyMsgData setMsg = new MyMsgData();
        setMsg.setCallback(SystemUpdateActivity.this);

        JSONObject pl = new JSONObject();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
        setMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(setMsg);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mApp.sdcardLog(TAGSD + "[handleMessage] msg.what = " + msg.what);
            switch (msg.what) {
                case FOTA_UPDATE_SEND_PROGRESS:
                    String str = PROGRESS_NULL;
                    if (msg.arg1 > 0) {
                        str = msg.arg1 + " %";
                    }
                    mApp.sdcardLog(TAGSD + "[updateCurrentProgress] str : " + str);
                    updateStatusText(getString(R.string.is_bin_tran_ing));
                    //p.setText(str);
                    break;
                case FOTA_SEND_VIA_BT_SUCCESS:
                    // send update success
                    //p.setText("100 %");
                    updateStatusText(R.string.download_succeed_via_bt);
                    setStep(STEP_BT_TRANS_END);
                    //reset watch verinfo
                    // myApp.getCurUser().getFocusWatch().setVerCur(watchUpDateInfo.getNewVerName());
                    //refresh watch data
                    break;
                case FOTA_UPDATE_VIA_BT_SUCCESS:
                    //reset watch verinfo
                    if (watchUpDateInfo != null) {
                        mApp.sdcardLog(TAGSD + "FOTA_UPDATE_VIA_BT_SUCCESS frameware update success " + watchUpDateInfo.getNewVerName());
                        myApp.getCurUser().getFocusWatch().setVerCur(watchUpDateInfo.getNewVerName());
                        WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                        //send user set
                        sendDeviceSet(CloudBridgeUtil.KEY_NAME_VERSION_CUR, watchUpDateInfo.getNewVerName());
                    } else {
                        mApp.sdcardLog(TAGSD + "FOTA_UPDATE_VIA_BT_SUCCESS NOTE  watchUpDateInfo is null");
                    }
                    setStep(STEP_ALL_END);
                    //refresh watch data
                    //setBtnWatchUpdateText("完成");
                    break;
                case FOTA_UPDATE_VIA_BT_COMMON_ERROR:
                case FOTA_UPDATE_VIA_BT_WRITE_FILE_FAILED:
                case FILE_NOT_FOUND_ERROR:
                case READ_FILE_FAILED:
                    // send update package fail
                    updateStatusText(R.string.ud_er_send);
                    //setBtnWatchUpdateText();
                    setStep(STEP_FOTA_ERROR);
                    break;
                case FOTA_RECODE_MD5_ERROR:
                    updateStatusText(R.string.ud_er);
                    setStep(STEP_FOTA_ERROR);
                    break;
                case FOTA_UPDATE_VIA_BT_DISK_FULL:
                    // package is too large
                    updateStatusText(R.string.dl_er_storage);
                    setStep(STEP_FOTA_ERROR);
                    //setBtnWatchUpdateText();
                    break;
                case FOTA_UPDATE_VIA_BT_DATA_TRANSFER_ERROR:
                case FOTA_UPDATE_VIA_BT_TRIGGER_FAILED:
                    // download fail
                    updateStatusText(R.string.ud_er);
                    setStep(STEP_FOTA_ERROR);
                    //setBtnWatchUpdateText("退出升级");
                    break;
                case FOTA_UPDATE_VIA_BT_FAILED:
                case FOTA_UPDATE_TIMEOUT:
                    // update fail
                    updateStatusText(R.string.ud_er);
                    setStep(STEP_FOTA_ERROR);
                    //setBtnWatchUpdateText("退出升级");
                    break;
                case FOTA_UPDATE_DISCONNECT:
                    updateStatusText(R.string.blu_connect_lost);
                    setStep(STEP_FOTA_ERROR);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 发送e2e给手表，获取定位原始数据
     * watchEid：对应手表的eid
     */
    private void e2eFirmwareUpdateStart() {
        WatchData watch = myApp.getCurUser().getFocusWatch();
        String[] watchEid = new String[1];
        watchEid[0] = watch.getEid();
        String phoneBtAddr = mBluetoothAdapter.getAddress();
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(SystemUpdateActivity.this);
//      e2e.setTimeout(1000);
        e2e.setNeedNetTimeout(false);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FIRMWARE_UPDATE_START);
        if (watch.getVerCur() != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_VERSION_CUR, watch.getVerCur());
        }
        pl.put(CloudBridgeUtil.KEY_NAME_VERSION_TARGET, watchUpDateInfo.getNewVerName());
        pl.put(CloudBridgeUtil.KEY_NAME_MD5, watchUpDateInfo.getMd5());
        pl.put(CloudBridgeUtil.KEY_NAME_BT_MAC, watch.getBtMac());
        pl.put(CloudBridgeUtil.KEY_NAME_PHONE_BT_MAC, phoneBtAddr);
        int SN = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mApp.sdcardLog(TAGSD + "e2eFirmwareUpdateStart: watch bt mac is " + watch.getBtMac());
        if (phoneBtAddr == null) {
            updateStatusText(getString(R.string.bluetooth_address_get_fail));
            setStep(STEP_FOTA_ERROR);
            return;
        }

        pl.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, getFotaSMS(SN, getMyApp().getCurUser().getEid(), watch.getVerCur(), watch.getBtMac(), phoneBtAddr));
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP,
                SN, myApp.getToken(), null, watchEid, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2e);
    }

    private String getFotaSMS(int sN, String eid, String verCur, String btmac, String phoneBtAddr) {
        StringBuilder buff = new StringBuilder();
        buff.append("<");
        buff.append(Integer.valueOf(sN).toString());
        buff.append(",");
        buff.append(getMyApp().getCurUser().getEid());
        buff.append(",");
        buff.append("E300");
        buff.append(",");
        buff.append(verCur);
        buff.append("@");
        if (btmac != null)
            buff.append(btmac.replace(":", ""));
        buff.append("@");
        buff.append(phoneBtAddr.replace(":", ""));
        buff.append(">");
        return buff.toString();
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        if (getStep() == STEP_EXIT) {
            mApp.sdcardLog(TAGSD + "app is exit, ignore callback msg");
            return;
        }
        // TODO Auto-generated method stub
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        JSONObject pl;
        JSONObject rqpl;
        switch (cid) {
            case CloudBridgeUtil.CID_E2E_DOWN:
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                rqpl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (!respMsg.containsKey(CloudBridgeUtil.KEY_NAME_RC)) {
                    int action = (Integer) rqpl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    int recode = CloudBridgeUtil.getCloudMsgRC(pl);
                    switch (action) {
                        case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FIRMWARE_UPDATE_START:
                            int curStep = getStep();

                            mApp.sdcardLog(TAGSD + "doCallBack curStep " + curStep + ", recode " + recode);
                            if (curStep != STEP_NEED_BT_MATCH) {
                                return;
                            }
                            if (recode == 1) {
                                //initFotaNotTrans();
                                updateStatusText(R.string.download_succeed_via_bt);
                                setStep(STEP_BT_TRANS_END);
                            }
                            if (recode == 0) {
                                //ok
                                setStep(STEP_NEED_BT_SCAN);
                                //start trans
                                startBTScan();
                            } else if (recode == -1) {
                                updateStatusText(getString(R.string.device_battery_low));
                                setStep(STEP_FOTA_ERROR);
                            } else {
                                String btResp = (String) pl.get(CloudBridgeUtil.KEY_NAME_BT_MAC);
                                String verResp = (String) pl.get(CloudBridgeUtil.KEY_NAME_VERSION_CUR);

                                //bt or ver not match
                                if (!btResp.equals(myApp.getCurUser().getFocusWatch().getBtMac())) {
                                    mApp.sdcardLog(TAGSD + "doCallBack bt not match-btResp=" + btResp);
                                    //bt not match
                                    watchUpDateInfo.setBtMac(btResp);
                                    myApp.getCurUser().getFocusWatch().setBtMac(btResp);
                                    getIntent().putExtra(EXTRA, watchUpDateInfo.getBtMac());
                                    setStep(STEP_NEED_BT_MATCH);
                                    e2e();
                                } else if (!verResp.equals(watchUpDateInfo.getCurVer())) {
                                    mApp.sdcardLog(TAGSD + "doCallBack ver not match-verResp=" + verResp);
                                    //ver not match
                                    watchUpDateInfo.setCurVer(verResp);
                                    myApp.getCurUser().getFocusWatch().setVerCur(verResp);
                                    WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                                    // 上传正确版本到服务器
                                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_VERSION_CUR, verResp);

                                    refreshUserVerInfo();
                                    setStep(STEP_NEED_DOWNLOAD);
                                    if (watchUpDateInfo.getNewVerName().equals(watchUpDateInfo.getCurVer())) {
                                        setStep(STEP_ALL_END);
                                        return;
                                    }
                                    //需要下载检查新的升级文件
                                    myApp.checkUpdateWatch(SystemUpdateActivity.this, false, mApp.getCurUser().getFocusWatch());
                                    //找不到对应的版本
                                    setStep(STEP_FOTA_ERROR);
                                    updateStatusText(getString(R.string.device_vision_not_match));
                                }
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    int action = (Integer) rqpl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    switch (action) {
                        case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FIRMWARE_UPDATE_START:
                            int curStep = getStep();
                            mApp.sdcardLog(TAGSD + "doCallBack curStep2 " + curStep);

                            if (curStep != STEP_NEED_BT_MATCH) {
                                // 出现状态不对，直接走失败流程，以免在往下走的话，出现流程混乱
                                mApp.sdcardLog(TAGSD + "doCallBack STEP ERROR");
                                setStep(STEP_FOTA_ERROR);
                                updateStatusText(getString(R.string.ud_er));
                                return;
                            }

                            // 加入E2E消息异常情况处理，超过重试次数需提示网络异常消息
                            if (rc == CloudBridgeUtil.RC_TIMEOUT || rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                                mApp.sdcardLog(TAGSD + "cant send e2e msg rc is " + rc);
                                E2E_MSG_RETRY_TIMES++;
                                if (E2E_MSG_RETRY_TIMES <= 2) {
                                    mApp.sdcardLog(TAGSD + "E2E_MSG_RETRY_TIMES is " + E2E_MSG_RETRY_TIMES);
                                    setStep(STEP_NEED_BT_MATCH);
                                    e2eFirmwareUpdateStart();
                                    return;
                                }
                                E2E_MSG_RETRY_TIMES = 0;
                                setStep(STEP_FOTA_ERROR);
                                if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                                    updateStatusText(getString(R.string.device_network_error));
                                } else {
                                    updateStatusText(R.string.net_check_alert);
                                }
                                return;
                            } else if (rc == CloudBridgeUtil.ERROR_CODE_E2G_OFFLINE) {
                                mApp.sdcardLog(TAGSD + "watch is offline. update error.");
                                setStep(STEP_FOTA_ERROR);
                                updateStatusText(getString(R.string.device_is_offline));
                                return;
                            }

                            break;

                        case CloudBridgeUtil.SUB_ACTION_REQUEST_VERSION:
                            int currentStep = getStep();
                            if (currentStep != STEP_BT_TRANS_END) {
                                return;
                            }
                            if (rc == CloudBridgeUtil.RC_SUCCESS) {
                                String watchVersion = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_VERSION);
                                if (watchUpDateInfo != null && watchVersion != null && watchVersion.equals(watchUpDateInfo.getNewVerName())) {
                                    myApp.getCurUser().getFocusWatch().setVerCur(watchUpDateInfo.getNewVerName());
                                    WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_VERSION_CUR, watchUpDateInfo.getNewVerName());
                                    setStep(STEP_ALL_END);
                                } else {
                                    if (getStep() < STEP_ALL_END) {
                                        updateStatusText(getString(R.string.ud_er));
                                        setStep(STEP_TIME_OUT);
                                    }
                                }
                            } else {
                                if (getStep() < STEP_ALL_END) {
                                    updateStatusText(getString(R.string.ud_er));
                                    setStep(STEP_TIME_OUT);
                                }
                            }
                            break;
                    }
                }
                break;
            case CloudBridgeUtil.CID_DEVICE_GET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    //refresh watch
                    WatchData watch = myApp.getCurUser().getFocusWatch();
                    JSONObject devicePl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    myApp.parseDevicePl(watch, devicePl);
                    FamilyData family = myApp.getCurUser().queryFamilyByGid(watch.getFamilyId());
                    if (family != null) {
                        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    }
                    WatchDAO.getInstance(getApplicationContext()).addWatch(watch);
                    refreshUserVerInfo();
                    //同步device后再更新升级信息
                    myApp.checkUpdateWatch(SystemUpdateActivity.this, false, mApp.getCurUser().getFocusWatch());
                    myApp.checkWatchVerInfo(mApp.getCurUser().getFocusWatch());
                    mWatchHaveNewVerion = false;
                    updateUpgradeInfo(false);
                } else {

                }
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
//				loadingdlg.dismiss();
                if (rc > 0) {
                    JSONObject reqPl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (reqPl.get(CloudBridgeUtil.WATCH_AUTO_UPGRADE) != null) {
                        saveAutoUpgrade();
                        setAutoUpgradeView();
                    }
                    String updateOnlyWifi = (String) reqPl.get(CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI);
                    if (updateOnlyWifi != null) {
                        myApp.setValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, Integer.parseInt(updateOnlyWifi));
                        setUpgradeOnlyWifiView();
                    }
                    //refresh ui
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this, getString(R.string.phone_set_timeout), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error), Toast.LENGTH_SHORT);
                }

                break;

            case CloudBridgeUtil.CID_MAPGET_MGET_RESP: {
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc > 0) {
                    String autoUpgrade = (String) pl.get(CloudBridgeUtil.WATCH_AUTO_UPGRADE);
                    if (autoUpgrade != null && !autoUpgrade.equals("")) {
                        mCurWatch.setAutoUpdate(Integer.valueOf(autoUpgrade));
                        myApp.setValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_AUTO_UPGRADE, mCurWatch.getAutoUpdate());
                        tmpAutoUpgrade = mCurWatch.getAutoUpdate();
                        //refresh
                        setAutoUpgradeView();
                    } else {//说明没有这个值，设置为1
                        mapSetMsg(CloudBridgeUtil.WATCH_AUTO_UPGRADE, Integer.toString(tmpAutoUpgrade));
                    }

                    String upgradeOnlyWifi = (String) pl.get(CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI);
                    if (upgradeOnlyWifi != null && !upgradeOnlyWifi.equals("")) {
                        myApp.setValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, Integer.valueOf(upgradeOnlyWifi));
                        setUpgradeOnlyWifiView();
                    }else {
                        if(mCurWatch.isDevice707_H01()||mCurWatch.isDevice709_H01()){
                            mapSetMsg(CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, "0");
                        }
                    }
                }
            }
            default:
                break;
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (this.step == step) {
            LogUtil.d(TAG + "  " + "step is the same " + step);
            return;
        }
        this.step = step;
        switch (step) {
            case STEP_NEED_BT_MATCH:
                totalPercent = 30;
                setBackgroundColorByUpdateStatus(true);
                mRoundProgressBar1.setProgress(totalPercent);
                updateStatusText(getString(R.string.is_md5check_ing));
                btnWatchUpdate.setVisibility(View.GONE);
                break;
            case STEP_NEED_BT_SCAN:
                totalPercent = 50;
                setBackgroundColorByUpdateStatus(true);
                mRoundProgressBar1.setProgress(totalPercent);
                updateStatusText(getString(R.string.is_bluetooth_scaning));
                btnWatchUpdate.setVisibility(View.GONE);
                break;
            case STEP_BT_TRANING:
                totalPercent = 60;
                setBackgroundColorByUpdateStatus(true);
                mRoundProgressBar1.setProgress(totalPercent);
                updateStatusText(getString(R.string.is_bin_tran_ing));
                btnWatchUpdate.setVisibility(View.GONE);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
                mMyHandler.sendEmptyMessageDelayed(FIRMWARE_UPDATE_TIMEOUT_MSG, FIRMWARE_UPDATE_TIMEOUT);
                break;
            case STEP_BT_TRANS_END:
                totalPercent = 80;
                setBackgroundColorByUpdateStatus(true);
                mRoundProgressBar1.setProgress(totalPercent);
                // 手表端已经开始升级了，重置超时定时器
                updateStartTime = System.currentTimeMillis();
                mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
                mMyHandler.sendEmptyMessageDelayed(FIRMWARE_UPDATE_TIMEOUT_MSG, FIRMWARE_UPDATE_TIMEOUT);
                btnWatchUpdate.setVisibility(View.GONE);
                break;
            case STEP_ALL_END:
                totalPercent = 100;
                setBackgroundColorByUpdateStatus(true);
                watchLoadingBar.setVisibility(View.GONE);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_RECONNECT_MSG);
                mRoundProgressBar1.setProgress(totalPercent);
                ivState.setBackgroundResource(R.drawable.up_success);
                btnWatchUpdate.setVisibility(View.GONE);
                btnWatchHaveUpdate.setVisibility(View.GONE);
                lineWatchUpdate.setVisibility(View.GONE);
                updateStatusText(R.string.device_upgrade_success);
                mWatchHaveNewVerion = false;
                watchVerInfoDesc = getWatchUpdateDescAndUpdateWatchInfo();//本地刷新当前版本信息
                refreshUserVerInfo();
                break;
            case STEP_FOTA_ERROR:
                watchLoadingBar.setVisibility(View.GONE);
                setBackgroundColorByUpdateStatus(false);
                mRoundProgressBar1.setProgress(100);
                ivState.setBackgroundResource(R.drawable.up_fail);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_RECONNECT_MSG);
                setBtnWatchUpdateText(getString(R.string.retry));
                if (mBluetoothConnectTask != null) {
                    mBluetoothConnectTask.cancel(true);
                    mBluetoothConnectTask = null;
                }
                resetTransmitTask();
                break;
            case STEP_TIME_OUT:
                watchLoadingBar.setVisibility(View.GONE);
                setBackgroundColorByUpdateStatus(true);
                mRoundProgressBar1.setProgress(100);
                ivState.setBackgroundResource(R.drawable.timeout);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_TIMEOUT_MSG);
                mMyHandler.removeMessages(FIRMWARE_UPDATE_RECONNECT_MSG);
                setBtnWatchUpdateText(getString(R.string.retry));
                if (mBluetoothConnectTask != null) {
                    mBluetoothConnectTask.cancel(true);
                    mBluetoothConnectTask = null;
                }
                resetTransmitTask();
                break;
            default:
                break;
        }
    }

    ////// M : update via bt signals end

    /////////////////////////////////////////
    ///// APP 升级相关
    private int checkNetworkState() {
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //使用蓝牙网络策略同手机网络
        try {
            NetworkInfo.State bluetoothstate = connectManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState();
            if (NetworkInfo.State.CONNECTED == bluetoothstate) {
                return NETWORK_MOBILE_ENABLED;
            }
        } catch (Exception e) {
            LogUtil.e("Do not support TYPE_BLUETOOTH Exp:" + e.getMessage());
        }

        try {
            NetworkInfo.State ethernetstate = connectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
            if (NetworkInfo.State.CONNECTED == ethernetstate) {
                return NETWORK_ETHERNET_ENABLED;
            }
        } catch (Exception e) {
            LogUtil.e("Do not support Ethernet Exp:" + e.getMessage());
        }
        if (mobNetInfo != null && mobNetInfo.isConnected()) {
            return NETWORK_MOBILE_ENABLED;
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
            return NETWORK_WIFI_ENABLED;
        }
        return NETWORK_NOT_AVAILABLE;
    }

    public String getWatchVerInfoByWatch(WatchData watch) {
        String desc = null;
        if (watch.getVerCur() == null) {//判空保护
            return desc;
        }
        String json = myApp.getWatchVerInfoJsonByReq(myApp.getWatchVerinfoReqJsonString(watch));
        if (json != null) {
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            desc = (String) obj.get(CloudBridgeUtil.KEY_NAME_VERSION_DESC);
        }
        return desc;

    }

    public void getUpdateAppInfo() {
        String updateInfo = myApp.getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                mAppUpdateDesc = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_VERSION_DESC);
                mAppForceUpdate = lastJo.getInt("force");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUpdateAppNewVersionName() {
        String verStr = null;
        String updateInfo = myApp.getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                mAppUpdateDesc = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_VERSION_DESC);
                mAppForceUpdate = lastJo.getInt("force");
                verStr = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_VERSION_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return verStr;
    }

    public class BluetoothPairingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                String miuiVersion = getSystemProperty("ro.miui.ui.version.name");
                mApp.sdcardLog(TAGSD + "Receive BT PAIRING_REQUEST miuiVersion " + miuiVersion);

                /// 当前步奏不是STEP_NEED_BT_SCAN时，不弹出提示，以免界面显示有问题
                /// 解决点击升级后，马上退出，然后又马上进入升级界面引发的问题
                if (getStep() != STEP_NEED_BT_SCAN) {
                    mApp.sdcardLog(TAGSD + "Current step is not STEP_NEED_BT_SCAN " + step);
                    return;
                }

                String desc = getString(R.string.please_confirm_bluetooth_pair_request);
                if (miuiVersion != null && miuiVersion.length() > 0) {
                    desc = getString(R.string.please_confirm_bluetooth_pair_request_desc);
                }
                Dialog dlg = DialogUtil.CustomNormalDialog(SystemUpdateActivity.this,
                        getString(R.string.bluetooth_pair_request),
                        desc,
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        },
                        getString(R.string.donothing_text));
                dlg.show();
            }
        }
    }

    private String getSystemProperty(String propName) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            LogUtil.e(TAG + "  " + "Unable to read sysprop " + propName, ex);
            mApp.sdcardLog(TAGSD + "Unable to read sysprop ");
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtil.e(TAG + "  " + "Exception while closing InputStream", e);
                    mApp.sdcardLog(TAGSD + "Exception while closing InputStream");
                }
            }
        }
        return line;
    }

    private void requestWatchVersion() {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(SystemUpdateActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_REQUEST_VERSION);
        String[] teid;
        teid = new String[1];
        teid[0] = getMyApp().getCurUser().getFocusWatch().getEid();
        StringBuffer sms = new StringBuffer();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        sms.append("<" + sn + "," + getMyApp().getCurUser().getEid() + "," +
                "E" + CloudBridgeUtil.SUB_ACTION_REQUEST_VERSION + "," + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, myApp.getToken(), null, teid, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2e);
    }

    private void sendDeviceGet(String eid) {
        MyMsgData msg = new MyMsgData();
        msg.setCallback(SystemUpdateActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        msg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_GET, pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(msg);
        }
    }

    private void mapSetMsg(String key, String value) {
        String eid = myApp.getCurUser().getFocusWatch().getEid();
        String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapSetMsg(eid, familyid, key, value, SystemUpdateActivity.this);
        }
    }

    private void saveAutoUpgrade() {
        mCurWatch.setAutoUpdate(tmpAutoUpgrade);
        myApp.setValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_AUTO_UPGRADE, tmpAutoUpgrade);
    }

    private void mapgetWatchUpgradeState(String eid) {

        String[] keys = new String[2];
        keys[0] = CloudBridgeUtil.WATCH_AUTO_UPGRADE;
        keys[1] = CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI;
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapMGetMsg(eid, keys, SystemUpdateActivity.this);
        }
    }

    private void setAutoUpgradeView() {
        if (btnAutoUpdate.getVisibility() == View.VISIBLE) {
            if (mCurWatch.getAutoUpdate() == 1) {
                btnAutoUpdate.setImageResource(R.drawable.switch_on);
                if (mCurWatch.isDevice701() || mCurWatch.isDevice710() || mCurWatch.isDevice703() || mCurWatch.isDevice705()
                        || mCurWatch.isDevice706_A02() || mCurWatch.isDevice900_A03() || mCurWatch.isDevice709_A03() || mCurWatch.isDevice708_A06()
                        || mCurWatch.isDevice708_A07() || mCurWatch.isDevice709_A05() || mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
                    layoutUpdatebinOnlyWifi.setVisibility(View.VISIBLE);
                    findViewById(R.id.iv_layout_updatebin_wifi).setVisibility(View.VISIBLE);
                }
            } else {
                btnAutoUpdate.setImageResource(R.drawable.switch_off);
                layoutUpdatebinOnlyWifi.setVisibility(View.GONE);
                findViewById(R.id.iv_layout_updatebin_wifi).setVisibility(View.GONE);
            }
        }

        if (mCurWatch.isDevice701() || mCurWatch.isDevice710() || mCurWatch.isDevice703() || mCurWatch.isDevice705()
                || mCurWatch.isDevice709_A03() || mCurWatch.isDevice708_A06() || mCurWatch.isDevice708_A07() || mCurWatch.isDevice709_A05()) {
            tvAutoUpdateDesc.setText(getString(R.string.auto_update_ing_desc_other));
        } else {
            tvAutoUpdateDesc.setText(getString(R.string.auto_update_ing_desc));
        }
    }

    private void setUpgradeOnlyWifiView() {

        int updateOnlyWifi;
        if (mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
            updateOnlyWifi = mApp.getIntValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, 0);
        } else {
            updateOnlyWifi = mApp.getIntValue(mCurWatch.getEid() + CloudBridgeUtil.WATCH_UPGRADE_ONLY_WIFI, 1);
        }
        if (updateOnlyWifi == 1) {
            btnUpdatebinOnlyWifi.setImageResource(R.drawable.switch_on);
        } else {
            btnUpdatebinOnlyWifi.setImageResource(R.drawable.switch_off);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("hahahha", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}
