/**
 * Creation Date:2015-1-8
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.PushConfiguration;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.AddCallMemberActivity;
import com.xiaoxun.xun.activitys.BindResultActivity;
import com.xiaoxun.xun.activitys.GroupMessageActivity;
import com.xiaoxun.xun.activitys.NewLoginActivity;
import com.xiaoxun.xun.activitys.NewMainActivity;
import com.xiaoxun.xun.activitys.NoticeTypeActivity;
import com.xiaoxun.xun.activitys.PrivateMessageActivity;
import com.xiaoxun.xun.activitys.SetDeviceNumberActivity;
import com.xiaoxun.xun.activitys.SosStartActivity;
import com.xiaoxun.xun.activitys.VideoCallActivity;
import com.xiaoxun.xun.activitys.VideoCallActivity2;
import com.xiaoxun.xun.activitys.VideoCallActivity3;
import com.xiaoxun.xun.activitys.WatchFirstSetActivity;
import com.xiaoxun.xun.adapter.AllMessageAdapter;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.LocationData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.SilenceTime;
import com.xiaoxun.xun.beans.SosWarning;
import com.xiaoxun.xun.beans.UserData;
import com.xiaoxun.xun.beans.WarningInfo;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.beans.WatchDownloadBean;
import com.xiaoxun.xun.db.ChatHisDao;
import com.xiaoxun.xun.db.DialBgDAO;
import com.xiaoxun.xun.db.LocationDAO;
import com.xiaoxun.xun.db.NoticeMsgHisDAO;
import com.xiaoxun.xun.db.UserRelationDAO;
import com.xiaoxun.xun.db.WarningInfoDao;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.dialBg.DialBgActivity;
import com.xiaoxun.xun.gallary.downloadUtils.DownloadListener;
import com.xiaoxun.xun.gallary.downloadUtils.ListDownLoader;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.message.MessageUtils;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.FrontServiceUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.NotificationHelper;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.RSACoder;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.SystemUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.XimalayaUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import dx.client.api.DefaultEndpointListener;
import dx.client.api.EndpointFactory;
import dx.client.api.IEndpoint;
import dx.client.impl.ssl.SslClientContextFactory;

import static com.blankj.utilcode.util.ProcessUtils.isMainProcess;
import static com.xiaoxun.xun.Const.ACTION_RECEIVE_REQ_ADD_NEW_FRIEND;
import static com.xiaoxun.xun.utils.CloudBridgeUtil.KEY_NAME_EID;
import static com.xiaoxun.xun.utils.CloudBridgeUtil.KEY_NAME_SID;


/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-8
 */
public class NetService extends Service implements MsgCallback, SensorEventListener {
    static private String TAG = "NetService";
    private static String RSA_PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHlQ1eMFOWFHAF0d278lqmQvskvIjnOgk9QpMoeddV0ZsEyEe/8EjNpp+xzLa6ScftZLBJy1KIPUku1gqacAv1Cr91vS5GPrPGSEowH34ErGHCmJ6v+TV0CX+GA5l+cXsIB6qjsqeDwsuL9qy69v4bgDxwwb4BTqj4yrtC6iIhIwIDAQAB";
    public String AES_KEY = null;
    private static int MAX_TIMEOUT_COUNTER = 2;
    private Handler mainActivityHandle = null;

    private Handler myHandler = null;
    private List<MyMsgData> mMsgQueue = new ArrayList<MyMsgData>();
    private BroadcastReceiver mNetSeriveReceiver;

    private static final int NETWORK_ERROR_MESSAGE_TIMEOUT = 30 * 1000;
    private static final int OPEN_WEB_SOCKET_TIMEOUT = 35 * 1000;

    public static final String CHANNEL_ID_STRING = "xiaoxun_netservice";

    private boolean isPermissionDedied = false;

    private int autoPingCount = 0;
    private long pingtime;
    private BroadcastReceiver mBgMsgReceiver;
    private MsgCallback mBgMsgNetCallback;
    private HashMap<String, String> locationSnMap = new HashMap<String, String>();

    SensorManager mSensorManager;
    String phoneStepsPref;

    //系统计步累加值
    Sensor mStepCount;
    private static final int sensorTypeC = Sensor.TYPE_STEP_COUNTER;


    private void tryPing() {
        try {
            pingtime = System.currentTimeMillis();
            websocketEndpoint.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startAutoPing() {
        autoPingCount = 10;
        tryPing();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == sensorTypeC) {
                int totalSteps = (int) event.values[0];
                if (phoneStepsPref.equals("0")) {
                    String saveSteps = TimeUtil.getTimeStampLocal() + "_" + totalSteps;
                    LogUtil.e("pref=0 showSteps:" + saveSteps);
                    mApp.sdcardLog("pref=0 showSteps:" + saveSteps);
                    mApp.setValue(Const.SHARE_PREF_PHONT_STEPS_NEW, saveSteps);
                    phoneStepsPref = saveSteps;
                } else {
                    String[] phoneSteps = phoneStepsPref.split("_");
                    if (TimeUtil.compareTodayToLastInfo(phoneSteps[0]) && phoneSteps.length >= 2) {

                    } else {
                        String saveSteps = TimeUtil.getTimeStampLocal() + "_" + totalSteps;
                        LogUtil.e("notToday showSteps:" + saveSteps);
                        mApp.sdcardLog("notToday showSteps:" + saveSteps);
                        mApp.setValue(Const.SHARE_PREF_PHONT_STEPS_NEW, saveSteps);
                        phoneStepsPref = saveSteps;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sendSetLangInfo() {
        MyMsgData langSet = new MyMsgData();
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        final Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        pl.put(CloudBridgeUtil.KEY_SET_LANG, locale.getLanguage());
        langSet.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_SET_LANG, sn, mApp.getToken(), pl));
        langSet.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                LogUtil.e("lang info:" + reqMsg.toJSONString() + ":" + respMsg.toJSONString());
                mApp.setValue("langrage", locale.getLanguage());
            }
        });
        sendNetMsg(langSet);
    }

    public class MyBinder extends Binder {

        public NetService getService() {
            return NetService.this;
        }
    }

    private MyBinder myBinder = new MyBinder();
    private ImibabyApp mApp;

    private String WEBSOCKET_IP_ADDR;
    private String HTTP_IP_ADDR;
    private String WEBSOCKET_SSL_IP_ADDR;

    public interface DnsParseCallback {
        void doCallBack();
    }

    private void sendStaticToCloudserver(JSONArray pl) {
        MyMsgData Msg = new MyMsgData();
        Msg.setCallback(mBgMsgNetCallback);
        Msg.setTimeout(60 * 1000);
        Msg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_STATE_MSG_REQ, pl));
        sendNetMsg(Msg);
    }


    private String getWebSocketUrl() {
        if (WEBSOCKET_IP_ADDR != null && WEBSOCKET_IP_ADDR.length() > 8) {
            return "ws://" + WEBSOCKET_IP_ADDR + Const.WEBSOCKET_DIR;
        } else {
            return null;
        }

    }

//    private String getWebSSLSocketUrl() {
//        LogUtil.e("WEBSOCKET_IP_ADDR:" + WEBSOCKET_IP_ADDR);
//        if (WEBSOCKET_IP_ADDR != null && WEBSOCKET_IP_ADDR.length() > 8) {
//            return "wss://" + WEBSOCKET_IP_ADDR + Const.WEBSOCKET_SSL_DIR;
//        } else {
//            return null;
//        }
//
//    }

    private String getWebSSLSocketUrl() {
        LogUtil.e("WEBSOCKET_IP_ADDR:" + WEBSOCKET_IP_ADDR);
        if (WEBSOCKET_IP_ADDR != null && WEBSOCKET_IP_ADDR.length() > 8) {
            return "wss://" + XunKidsDomain.getInstance(this).getXunKidsDomain() + Const.WEBSOCKET_SSL_DIR;
        } else {
            return null;
        }
    }



    private String getTestWebSSLSocketUrl() {
        LogUtil.e("WEBSOCKET_TEST_IP_ADDR:" + WEBSOCKET_IP_ADDR);
        if (WEBSOCKET_IP_ADDR != null && WEBSOCKET_IP_ADDR.length() > 8) {
            return "wss://" + WEBSOCKET_IP_ADDR + Const.WEBSOCKET_SSL_TEST_DIR;
        } else {
            return null;
        }

    }

    private void saveWebSocketIpAddr(String newIp) {
        WEBSOCKET_IP_ADDR = newIp;
        if (mApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) <= 0) {
            mApp.setValue(Const.SHARE_PREF_FIELD_WEBSOCKET_IP_ADDR, newIp);
        }
    }

    private void saveHttpIpAddr(String newIp) {
        HTTP_IP_ADDR = newIp;
        if (mApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) <= 0) {
            mApp.setValue(Const.SHARE_PREF_FIELD_HTTP_IP_ADDR, newIp);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (ImibabyApp) getApplication();
        myHandler = new MyHandler();
        mApp.sdcardLog("NetService---------->>onCreate");

        phoneStepsPref = mApp.getStringValue(Const.SHARE_PREF_PHONT_STEPS_NEW, "0");
        AES_KEY = mApp.getStringValue(Const.SHARE_PREF_AES_KEY, "");
        initNetClient();
        initSensor();
        initReceivers();
        mApp.setNetService(NetService.this);

        initBgMsgReceiver();
        initBgMsgNetCallback();

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationChannel channel = null;
//            channel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
//            startForeground(1, notification);
//            myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stopForeground(true);
//                }
//            }, 10000);
//        }

        FrontServiceUtils.startFrontServiceAndroidO(this);

    }


    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(sensorTypeC);
        if (mStepCount != null) {
            mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    //网络状态变化监测
    private boolean lastNetState = false;

    private void initReceivers() {
        // TODO Auto-generated method stub
        mNetSeriveReceiver = new BroadcastReceiver() {
            //wifi状态广播接收，连接变化，扫描结果等
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                String action = intent.getAction();
                if (action.equals(Const.ACTION_QUERY_ALL_GROUPS)) {
                    int flag = 0;
                    if (intent.getExtras() != null) {
                        flag = intent.getExtras().getInt("get_offline_chat", 0);
                    }
                    sendQueryAllGroups(flag);
                } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    boolean nextNetState = false;

                    NetworkInfo activeNetInfo = mConnectivityManager.getActiveNetworkInfo();
                    nextNetState = activeNetInfo != null && activeNetInfo.isConnected();

                    Intent it = new Intent(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE);
                    sendBroadcast(it);
                    mApp.sdcardLog("action CONNECTIVITY_ACTION nextNetState: " + nextNetState + " lastNetState: " + lastNetState);
                    if (nextNetState == true) {//状态改变
                        //切换
                        if (!isWebSocketOK) {
                            //需要重置
                            restartCloudBridgeClient();
                        }

                    }
                    lastNetState = nextNetState;

                    if (nextNetState && mApp.shouldInit() && !mApp.getMiPushRegister()) {

//                        PushConfiguration pushConfiguration = new PushConfiguration();
//                        pushConfiguration.setOpenHmsPush(true);
//                        pushConfiguration.setOpenCOSPush(true);
//                        pushConfiguration.setOpenFTOSPush(true);
//                        MiPushClient.registerPush(mApp.getApplicationContext(), Const.MI_STATE_ID, Const.MI_STATE_KEY, pushConfiguration);
                    }
                    if (isMainProcess()) {
                        //You must call setRegion to set the region before calling registerPush
                        MiPushClient.setRegion(Region.Global);
                        MiPushClient.registerPush(mApp.getApplicationContext(), Const.MI_STATE_ID, Const.MI_STATE_KEY);
                    }
                    if (nextNetState && mApp.getMiPushRegister()) {
                        if (mApp.getCurUser() != null && mApp.getCurUser().getEid() != null && mApp.getCurUser().getEid().length() > 0) {
                            mApp.setMiPushAlias();
                        }
                    }

                } else if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    String serviceString = Context.DOWNLOAD_SERVICE;

                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                    if (id == mApp.getUpdownLoadId()) {

                        DownloadManager downloadManager;
                        downloadManager = (DownloadManager) getSystemService(serviceString);
                        Uri uri = downloadManager.getUriForDownloadedFile(id);

                        //1. 安装下载的应用
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installIntent.setDataAndType(uri,
                                "application/vnd.android.package-archive");
                        startActivity(installIntent);
                    }
                } else if (action.equals(Const.ACTION_RECREATE_APP)) {
                    mApp = (ImibabyApp) getApplication();
                    mApp.setNetService(NetService.this);
                    mApp.sdcardLog("NetService---------->>ACTION_RECREATE_APP");
                } else if (action.equals(Const.ACTION_LOOP_ALARM)) {
                    doQueueLoop();
                    startQueueLoopTimer();
                } else if (intent.getAction().equals(Const.ACTION_HEART_BEAT_ALARM)) {
                    if (isHeartBeatNeed() && isWebSocketOK) {//增加判断websocket状态，没有ok时候不需要检查心跳
                        mApp.sdcardLog("doQueueLoop,heartbeatCount=" + heartbeatCount);
                        //long time on p
                        if (isPongOffline()) {
                            doNoPongTimeout();
                        } else {
                            sendHeartbeat();
                        }
                    }
                    mHeartBeatCheckTime = 20 * 1000;
                    startHeartbeatTimer();
                } else if (intent.getAction().equals(Const.ACTION_ADD_WATCH_CONTACT)) {
                    String eid = intent.getStringExtra("eid");
                    addWatchContact(eid);
                } else if (action.equals(Const.ACTION_CHECK_WEBSOCKET_STATE)) {
                    if (isNetworkOK() && !isWebSocketOK) {
                        //需要重置
                        restartCloudBridgeClient();
                    }
                } else if (action.equals(Constants.ACTION_DOMAIN_CHANGE)) {
                    closeWebSocketLogout();
                    try {
                        startCloudBridgeClient();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (action.equals(Constants.ACTION_PRIVACY_CHANGE)) {
                    initNetClient();
                }
            }
        };

        IntentFilter baseFilter = new IntentFilter();
        baseFilter.addAction(Const.ACTION_QUERY_ALL_GROUPS);
        //增加网络变化监测
        baseFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        baseFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        baseFilter.addAction(Const.ACTION_RECREATE_APP);
        baseFilter.addAction(Const.ACTION_LOOP_ALARM);
        baseFilter.addAction(Const.ACTION_HEART_BEAT_ALARM);
        baseFilter.addAction(Const.ACTION_ADD_WATCH_CONTACT);
        baseFilter.addAction(Const.ACTION_CHECK_WEBSOCKET_STATE);
        baseFilter.addAction(Constants.ACTION_DOMAIN_CHANGE);
        baseFilter.addAction(Constants.ACTION_PRIVACY_CHANGE);

        registerReceiver(mNetSeriveReceiver, baseFilter);

    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mNetSeriveReceiver);
            unregisterReceiver(mBgMsgReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void sendQueryAllGroups(int flagValue) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(NetService.this);
        JSONObject param = new JSONObject();
        //set msg body
        if (flagValue == 1) {
            param.put("get_offline_chat", flagValue);
        }
        queryGroupsMsg.setReqMsg(mApp.obtainCloudMsgContentWithParam(CloudBridgeUtil.CID_QUERY_MYGROUPS, null, param));

        sendLoginMsg(queryGroupsMsg);
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Const.MSG_RESPONSE_WATCH_LOCATION_OK:
                    if (mainActivityHandle != null) {
                        Message newMsg = mainActivityHandle.obtainMessage(Const.MSG_RESPONSE_WATCH_LOCATION_OK);
                        newMsg.getData().putAll(msg.peekData());
                        newMsg.sendToTarget();
                    }
                    break;
                case Const.MSG_RESPONSE_WEBSOCKET_RECV:
                    JSONObject jo = (JSONObject) JSONValue.parse(msg.peekData().getString(Const.KEY_VALUE_JSONOBJECT));

                    checkMsgQueueResp(jo);

                    break;
                case Const.MSG_RESPONSE_HTTP_RECV:
                    JSONObject joHTTP = (JSONObject) JSONValue.parse(msg.peekData().getString(Const.KEY_VALUE_JSONOBJECT));
                    checkMsgQueueResp(joHTTP);
                    break;
                case Const.MSG_RESPONSE_WEBSOCKET_ERROR:
                    networkErrorMsgQueueResp();
                    break;
                case Const.MSG_RESPONSE_NETWORK_OR_WEBSOCKET_ERROR:
                    JSONObject joError = (JSONObject) JSONValue.parse(msg.peekData().getString(Const.KEY_VALUE_JSONOBJECT));
                    Integer reqSN = (Integer) joError.get("SN");
                    boolean isFindSN = false;
                    synchronized (mMsgQueue) {
                        if (mMsgQueue.size() > 0) {
                            for (MyMsgData myMsgData : mMsgQueue) {
                                Integer msgSN = (Integer) myMsgData.reqMsg.get("SN");
                                if (reqSN != null && msgSN != null && reqSN.compareTo(msgSN) == 0) {
                                    isFindSN = true;
                                }
                            }
                        }
                    }
                    if (isFindSN) {
                        checkMsgQueueResp(joError);
                    }
                    break;
                case Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT:
                    if (!isWebSocketOK && !isPermissionDedied) {
                        dnsReparseCountAdd();
                        closeWebSocket();
                    }

                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        mApp.sdcardLog("NetService---------->>onBind:" + intent.toString());
        return myBinder;
    }

    @Override
    public void onDestroy() {
        mApp.sdcardLog("NetService---------->>onDestroy");
        super.onDestroy();
        unInitNetClient();
        clearReceivers();
        if (mStepCount != null) {
            mSensorManager.unregisterListener(this, mStepCount);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        mApp.sdcardLog("NetService---------->>onRebind:" + intent.toString());
        super.onRebind(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d("NetService---------->>onUnbind");
        return false;
    }

    /**
     * 如果登陆前调用，会返还未登陆
     *
     * @param
     * @return false error , true try send
     */
    public boolean sendNetMsg(MyMsgData msg) {
        return sendNetMsg(msg, true);
    }

    //login专用
    public boolean sendLoginMsg(MyMsgData msg) {
        return sendNetMsg(msg, false);
    }

    /**
     * @param
     * @return false error , true try send
     */
    private boolean sendNetMsg(MyMsgData msg, boolean isAfterLogin) {
        //如果是e2c,c2e使用http方式不使用websocket，
        boolean isAllStateReady = true;
        int ERROR_CODE = CloudBridgeUtil.RC_NETERROR;
        if (!isNetworkOK()) {
            ERROR_CODE = CloudBridgeUtil.RC_NETERROR;
            isAllStateReady = false;
        } else if (!isCloudBridgeClientOk()) {
            isAllStateReady = false;
            ERROR_CODE = CloudBridgeUtil.RC_SOCKET_NOTREADY;
        } else if (isAfterLogin && !isNetServiceLoginOK()) {
            ERROR_CODE = CloudBridgeUtil.RC_NOT_LOGIN;
            isAllStateReady = false;
        }
        //统一添加版本号"Version": "00020000"
        if (null == msg.getReqMsg().get(CloudBridgeUtil.KEY_NAME_VERSION)) {//没有设置，则默认
            msg.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, CloudBridgeUtil.SW_PROTOCOL_NUM);
        } else if (CloudBridgeUtil.PROTOCOL_FOBIDDEN.equals(msg.getReqMsg().get(CloudBridgeUtil.KEY_NAME_VERSION))) {
            msg.getReqMsg().remove(CloudBridgeUtil.KEY_NAME_VERSION);
        }

        if (msg.getReqMsg().get(CloudBridgeUtil.KEY_NAME_SID) != null) {
            msg.getReqMsg().put(CloudBridgeUtil.KEY_NAME_SID, mApp.getToken());
        }

        if (isAllStateReady) {
            addMsgQueue(msg);
            LogUtil.e("sendmsg = " + msg.reqMsg.toJSONString());
            mApp.sdcardLog("sdmsg cid=" + msg.reqMsg.get("CID")
                    + ",sn=" + msg.reqMsg.get("SN") + " ,msglength" + msg.reqMsg.toJSONString().length());
            return sendWebsocketEndpointMsg(msg.reqMsg.toJSONString());
        } else {
            addMsgQueue(msg);


            JSONObject respMsg = new JSONObject();
            respMsg.put("SN", msg.reqMsg.get("SN"));
            respMsg.put("CID", (Integer) msg.reqMsg.get("CID") + 1);
            // 上行和下行CID匹配？
            //如有特殊CID不符合需要匹配
            respMsg.put("RC", ERROR_CODE);
            //异步报错
            if (msg.getNeedNetTimeout()) {
                Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_NETWORK_OR_WEBSOCKET_ERROR);
                Bundle bd = newmsg.getData();
                bd.putString(Const.KEY_VALUE_JSONOBJECT, respMsg.toJSONString());
                myHandler.sendMessageDelayed(newmsg, NETWORK_ERROR_MESSAGE_TIMEOUT);
            } else {
                Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_WEBSOCKET_RECV);
                Bundle bd = newmsg.getData();
                bd.putString(Const.KEY_VALUE_JSONOBJECT, respMsg.toJSONString());
                newmsg.sendToTarget();
            }
            LogUtil.e("NetService sendNetMsg error_code=" + ERROR_CODE + "   cid=" + msg.reqMsg.get("CID"));
            mApp.sdcardLog("NetService sendNetMsg error_code=" + ERROR_CODE + "   cid=" + msg.reqMsg.get("CID"));
            return false;
        }
    }

    public boolean isNetworkOK() {
        boolean nextNetState = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                nextNetState = true;
            }
        } catch (Exception e) {
            LogUtil.e("FlashMemoryApp.checkConectionToUpdate() Exp:" + e.getMessage());
        }
        lastNetState = nextNetState;
        return lastNetState;
    }

    private void closeWebSocketLogout() {
        mApp.sdcardLog("manual close the closeWebSocketLogout");
        try {
            if (websocketEndpoint != null) {
                websocketEndpoint.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (websocketEndpoint != null) {
            websocketEndpoint.registerEndpointListener(new DefaultEndpointListener() {

                @Override
                public void onPing() {
                    // TODO Auto-generated method stub
                    super.onPing();
                }

                @Override
                public void onOpen(int status, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onOpen is error");
                }

                @Override
                public void onReceive(String message, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onReceive is error");
                }

                @Override
                public void onClose(int status, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onClose is error, time=" + TimeUtil.getTimeStampLocal() + " port= " + port);
                }

                @Override
                public void onError(int errorCode, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this OnError is error, time=" + TimeUtil.getTimeStampLocal() + " port= " + port);

                }
            });
        }
        websocketEndpoint = null;
        isWebSocketOK = false;
        mApp.lastWebsocketPort = 0;
        resetWebsocketDoingCount();
    }


    private void closeWebSocket() {
        mApp.sdcardLog("manual close the websocket: websocketEndpoint = " + websocketEndpoint);
        try {
            if (websocketEndpoint != null) {
                websocketEndpoint.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (websocketEndpoint != null) {
            websocketEndpoint.registerEndpointListener(new DefaultEndpointListener() {

                @Override
                public void onPing() {
                    // TODO Auto-generated method stub
                    super.onPing();
                }

                @Override
                public void onOpen(int status, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onOpen is error");
                }

                @Override
                public void onReceive(String message, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onReceive is error");
                }

                @Override
                public void onClose(int status, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this onClose is error, time=" + TimeUtil.getTimeStampLocal() + " port= " + port);
                }

                @Override
                public void onError(int errorCode, String reason, int port) {
                    mApp.sdcardLog("websocketEndpoint had null,this OnError is error, time=" + TimeUtil.getTimeStampLocal() + " port= " + port);

                }
            });
        }
        websocketEndpoint = null;
        isWebSocketOK = false;
        mApp.lastWebsocketPort = 0;
        resetWebsocketDoingCount();
        myHandler.sendMessage(myHandler.obtainMessage(Const.MSG_RESPONSE_WEBSOCKET_ERROR));
        Intent it = new Intent(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE);
        sendBroadcast(it);
        if (isNetworkOK()) {
            restartCloudBridgeClient();
        }
    }

    private boolean sendWebsocketEndpointMsg(String jsonString) {
        try {
            //加密处理
            JSONObject json = (JSONObject) JSONValue.parse(jsonString);
            if (CloudBridgeUtil.getCloudMsgCID(json) == CloudBridgeUtil.CID_THIRD_REG) {
                if (AES_KEY == null || AES_KEY.equals("")) {
                    byte[] random_key = new byte[16];
                    Random r = new Random();
                    r.nextBytes(random_key);
                    AES_KEY = BASE64Encoder.encode(random_key).substring(0, 16);
                    mApp.setValue(Const.SHARE_PREF_AES_KEY, AES_KEY);
                }
                JSONObject reg = new JSONObject();
                reg.put("PT1", BASE64Encoder.encode(RSACoder.encryptByPublicKey(AES_KEY.getBytes(), RSA_PUBLICKEY)));
                reg.put("PT2", BASE64Encoder.encode(AESUtil.encryptAESCBC(jsonString, AES_KEY, AES_KEY)));
                websocketEndpoint.send(reg.toJSONString());
            } else if (CloudBridgeUtil.getCloudMsgCID(json) == CloudBridgeUtil.CID_CHECK_SESSION) {
                if (AES_KEY == null || AES_KEY.equals("")) {
                    byte[] random_key = new byte[16];
                    Random r = new Random();
                    r.nextBytes(random_key);
                    AES_KEY = BASE64Encoder.encode(random_key).substring(0, 16);
                    mApp.setValue(Const.SHARE_PREF_AES_KEY, AES_KEY);
                }
                JSONObject sessionping = new JSONObject();
                sessionping.put("PT1", BASE64Encoder.encode(RSACoder.encryptByPublicKey(AES_KEY.getBytes(), RSA_PUBLICKEY)));
                sessionping.put("PT2", BASE64Encoder.encode(AESUtil.encryptAESCBC(jsonString, AES_KEY, AES_KEY)));
                websocketEndpoint.send(sessionping.toJSONString());
            } else if (CloudBridgeUtil.getCloudMsgCID(json) == CloudBridgeUtil.CID_USER_LOGIN) {
                byte[] random_key = new byte[16];
                Random r = new Random();
                r.nextBytes(random_key);
                AES_KEY = BASE64Encoder.encode(random_key).substring(0, 16);
                mApp.setValue(Const.SHARE_PREF_AES_KEY, AES_KEY);
                JSONObject login = new JSONObject();
                login.put("PT1", BASE64Encoder.encode(RSACoder.encryptByPublicKey(AES_KEY.getBytes(), RSA_PUBLICKEY)));
                login.put("PT2", BASE64Encoder.encode(AESUtil.encryptAESCBC(jsonString, AES_KEY, AES_KEY)));
                websocketEndpoint.send(login.toJSONString());
            } else if (AES_KEY.length() == 16) {
                websocketEndpoint.send(AESUtil.encryptAESCBC(jsonString, AES_KEY, AES_KEY));
            } else {
                mApp.sdcardLog("AES_KEY length =" + AES_KEY.length() + ",it is not happen");
            }
//            websocketEndpoint.send(jsonString);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mApp.sdcardLog("sendWebsocketEndpointMsg error:" + e.getMessage());
            closeWebSocket();
            return false;
        }
    }

    private void initNetClient() {
//         startQueueThread();
        startQueueLoopTimer();//改为alarm方式loop
        startHeartbeatTimer();
        closeWebSocketLogout();//初始化一下
        SslClientContextFactory.initInstance(getResources());
        if (mApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0) {
            parseDomain(Const.WEBSOCKET_TEST_SERVICE, new DnsParseCallback() {

                @Override
                public void doCallBack() {
                    // TODO Auto-generated method stub
                    try {
                        startCloudBridgeClient();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } else if (getWebSocketUrl() == null) {
            parseDomain(Const.WEBSOCKET_DOMAIN, new DnsParseCallback() {

                @Override
                public void doCallBack() {
                    // TODO Auto-generated method stub
                    try {
                        startCloudBridgeClient();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                startCloudBridgeClient();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void unInitNetClient() {
        stopCloudBridgeClient();
    }

    private IEndpoint websocketEndpoint = null;
    private boolean isWebSocketOK = false;

    public boolean isCloudBridgeClientOk() {
        if (websocketEndpoint != null) {
            return isWebSocketOK;
        }
        return false;
    }

    private boolean isLoginOK = false;

    public boolean isNetServiceLoginOK() {
        return isLoginOK;
    }

    public void setNetServiceLoginOK(boolean state) {
        isLoginOK = state;
    }

    private int restartWebsocketDoingCount = 0;

    public void restartWebsocketDoingCountAdd() {
        restartWebsocketDoingCount++;
    }

    private void resetWebsocketDoingCount() {
        restartWebsocketDoingCount = 0;
    }

    //重试websocket的次数累积到3次，强制closewebsocket
    public boolean checkWebsocketDoingCountOut() {
        if (restartWebsocketDoingCount >= 3) {
            restartWebsocketDoingCount = 0;
            closeWebSocket();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 先采用主动触发的机制完成netclient 错误后的重置，更优秀的方案后续优化时候综合考虑
     */
    public void restartCloudBridgeClient() {
        if (isKickdownFlag()) {
            return;
        }
        if (websocketEndpoint != null) {
            restartWebsocketDoingCountAdd();
            if (checkWebsocketDoingCountOut()) {
                //累积三次强制重置
            } else {
                return;
            }
        }

        try {
            if (mApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0) {
                parseDomain(Const.WEBSOCKET_TEST_SERVICE, new DnsParseCallback() {

                    @Override
                    public void doCallBack() {
                        // TODO Auto-generated method stub
                        try {
                            startCloudBridgeClient();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else if (getWebSocketUrl() == null) {
                parseDomain(Const.WEBSOCKET_DOMAIN, new DnsParseCallback() {

                    @Override
                    public void doCallBack() {
                        // TODO Auto-generated method stub
                        try {
                            startCloudBridgeClient();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                startCloudBridgeClient();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean sendHeartbeat() {
        if (isNetServiceLoginOK()) {

            new MioAsyncTask<String, Void, Boolean>() {
                protected Boolean doInBackground(String... params) {
                    try {
                        mApp.sdcardLog("NetService websocketEndpoint sendHeartbeat,time=" + TimeUtil.getTimeStampLocal());
                        websocketEndpoint.ping();

                        //上传统计消息到云桥服务器
                        String privKey = mApp.getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), Const.DEFAULT_NEXT_KEY);

                        if (!privKey.equals(Const.DEFAULT_NEXT_KEY)) {
                            JSONArray statArray;
                            statArray = (JSONArray) JSONValue.parse(privKey);
                            for (int i = 0; i < statArray.size(); i++) {
                                JSONObject obj = (JSONObject) statArray.get(i);
                                obj.put("version", "1");
                            }
                            for (int i = 0; i < statArray.size(); i++) {
                                JSONObject obj = (JSONObject) statArray.get(i);
                                String callLog = (String) obj.get("call_log");
                                if (callLog == null) {
                                    obj.put("call_log", "0,0,0,0,0");
                                }
                            }
                            if (statArray != null) {
//                                LogUtil.i(statArray.toString());
                                sendStaticToCloudserver(statArray);
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                    }
                    return true;

                }

                protected void onCancelled() {
                    super.onCancelled();
                }

                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    try {

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }.execute();

        }
        return false;
    }

    private static int timeoutCounter = 0;

    public void timeoutInc() {
        timeoutCounter++;
    }

    public void timeoutZero() {
        timeoutCounter = 0;
    }

    private boolean isPongOffline() {
        if (heartbeatCount > 1) {
            timeoutCounter = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean isHeartBeatNeed() {
        if ((System.currentTimeMillis() - lastPingTime > HEART_BEAT_TIME && lastPingTime > 0)) {
            heartbeatCount++;
            return true;
        } else {
            return false;
        }
    }

    private void resetHeartbeatCount() {
        lastPingTime = System.currentTimeMillis();
        heartbeatCount = 0;
        mHeartBeatCheckTime = 270 * 1000;
        cancelHeartbeatTimer();
        startHeartbeatTimer();
    }

    private boolean kickdownFlag = false;

    public boolean isKickdownFlag() {
        return kickdownFlag;
    }

    public void setKickdownFlag(boolean kickdownFlag) {
        this.kickdownFlag = kickdownFlag;
    }


    private synchronized void startCloudBridgeClient() throws Exception {
        if (websocketEndpoint != null) {
            return;
        }
        // websocketEndpoint = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_WEBSOCKET);
        websocketEndpoint = EndpointFactory.INSTANCE.getEndpoint(IEndpoint.SERVICE_PROTOCOL_WEBSOCKET_SSL);//ssl服务endpoint
        mApp.sdcardLog("NetService websocketEndpoint startCloudBridgeClient:" + websocketEndpoint);

        websocketEndpoint.registerEndpointListener(new DefaultEndpointListener() {

            @Override
            public void onPing() {
                // TODO Auto-generated method stub
                super.onPing();
                LogUtil.d("Rx:" + "pong");
                mApp.sdcardLog("NetService websocketEndpoint receiveHeartbeat,time=" + TimeUtil.getTimeStampLocal());
                isManualPingSuccess = true;
                if (autoPingCount > 0) {
                    autoPingCount--;
                    long delta = System.currentTimeMillis() - pingtime;
                    Intent pingIt = new Intent(Const.ACTION_APP_PING_RESULT);
                    pingIt.putExtra("ping", delta);
                    sendBroadcast(pingIt);
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tryPing();
                        }
                    }, 2000);
                } else {
                    resetHeartbeatCount();
                }
            }

            @Override
            public void onOpen(int status, String reason, int port) {
                mApp.sdcardLog("NetService websocketEndpoint open status=" + status + ", reason=" + reason + ",port=" + port);
                LogUtil.d("The client example opened!");
                mApp.lastWebsocketPort = port;
                myHandler.removeMessages(Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT);

                isWebSocketOK = true;
                isPermissionDedied = false;
                Intent it = new Intent(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE);
                sendBroadcast(it);
                resetHeartbeatCount();
                if (mApp.needAutoLogin() || mApp.getBindAutoLogin()) {
                    if (mApp.getToken() == null || mApp.getToken().length() == 0) {
                        mApp.sdcardLog("NetService websocketEndpoint auto login");
                        sendAutoLogin();
                    } else {
                        mApp.sdcardLog("NetService websocketEndpoint sendSessionPing");
                        sendSessionPing();
                    }
                } else {
                    mApp.sdcardLog("NetService websocketEndpoint wait  user login");
                }
            }

            @Override
            public void onReceive(byte[] data) {
                LogUtil.e("Rx onReceive(byte[] data)");
                //if the message is a json text, you can parse it and get the "JSONObject" object. See below.
                byte[] decMessage = AESUtil.decryptAESCBC(data, AES_KEY, AES_KEY);
                String decMesString = new String(decMessage);
                JSONObject json = (JSONObject) JSONValue.parse(decMesString);
                LogUtil.e("Rx string:" + decMesString);
                mApp.sdcardLog("Rx onReceive(byte[] data):" + decMesString);
                if (json != null) {
                    parseWSSReceiveJson(json);
                } else {
                    mApp.sdcardLog("onReceive, Received data is error! it isn't happen !");
                }
            }

            @Override
            public void onReceive(String message, int port) {
                LogUtil.e("Rx port:" + port);
                mApp.sdcardLog("Rx: length:" + message.length() + ",port" + port + ",lastport" + mApp.lastWebsocketPort);
                // when the a text message arrives
                if (port != mApp.lastWebsocketPort) {//不是最新创建的websocket，不处理
                    return;
                }
                //if the message is a json text, you can parse it and get the "JSONObject" object. See below.
                //两个小时没有rx会造成session失效
                JSONObject jo;
                try {
                    jo = (JSONObject) JSONValue.parseWithException(message);
                } catch (Exception e) {
                    jo = null;
                }
                if (jo == null) {
                    byte[] decBase64 = Base64.decode(message, Base64.NO_WRAP);
                    byte[] decMessage = AESUtil.decryptAESCBC(decBase64, AES_KEY, AES_KEY);
                    String decMesString = new String(decMessage);
                    JSONObject json = (JSONObject) JSONValue.parse(decMesString);
                    LogUtil.e("Rx string:" + decMesString);
                    mApp.sdcardLog("Rx string:" + decMesString);
                    if (json != null) {
                        parseWSSReceiveJson(json);
                    } else {
                        mApp.sdcardLog("onReceive, Received data is error! it isn't happen !");
                    }
                } else {
                    LogUtil.e("Rx:" + message);
                    mApp.sdcardLog("Rx: length:" + message.length() + ",content:" + message);
                    parseWSSReceiveJson(jo);
                }
            }

            @Override
            public void onClose(int status, String reason, int port) {
                mApp.sdcardLog("NetService websocketEndpoint close status=" + status + ", reason=" + reason + ", port = " + port + " mApp.lastPort: " + mApp.lastWebsocketPort);
                LogUtil.d("connection closed status=" + status + ", reason=" + reason);

                myHandler.removeMessages(Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT);
                if (reason.contains("Permission denied")) { //应用网络权限被禁用
                    isPermissionDedied = true;
                    closeWebSocketLogout();
                    return;
                }
                if (mApp.lastWebsocketPort != 0 && port != mApp.lastWebsocketPort) {
                    return;
                }
                // Add your code here to do consequence jobs
                // when the app has disconnected from the cloud server
                if (status == -1) { //创建失败close
                    //异常关闭，
                    dnsReparseCountAdd();
                } else if (status >= 1000) {//1000 以上是socket close
                    if (status == 1006) {
                        // setNetServiceLoginOK(false);
                    }
                }
                isWebSocketOK = false;
                closeWebSocket();
            }

            @Override
            public void onError(int errorCode, String reason, int port) {
                mApp.sdcardLog("NetService websocketEndpoint onError errorcode=" + errorCode + ", reason=" + reason + ", port = " + port + "mApp.lastPort: " + mApp.lastWebsocketPort);

                LogUtil.d("errorcode=" + errorCode + ", reason=" + reason);
                if (errorCode == -1 && reason == null) {
                    return;
                }
                // Add your code here to handle connectivity errors
                isWebSocketOK = false;
                websocketEndpoint = null;
            }
        });
        //使用asynctask异步打开
        //Send a json message for ping.
        Intent it = new Intent(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE);
        sendBroadcast(it);
        if (mApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0) {
            openWebSocketENdpoint(websocketEndpoint, getTestWebSSLSocketUrl());//ssl服务器地址
        } else {
            openWebSocketENdpoint(websocketEndpoint, getWebSSLSocketUrl());//ssl服务器地址
        }
    }

    private void parseWSSReceiveJson(JSONObject jo) {
        //不是所有接受到的数据都有RC字段
        if (null != jo.get("RC")) {
            LogUtil.d(jo.get("RC").toString());
            if (CloudBridgeUtil.getCloudMsgRC(jo) == CloudBridgeUtil.ERROR_CODE_COMMOM_SESSION_ILLEGAL) {
                //session失效
                setNetServiceLoginOK(false);
            }
        }

        int cid = CloudBridgeUtil.getCloudMsgCID(jo);

        mApp.sdcardLog("rxmsg cid=" + jo.get("CID")
                + ",sn=" + jo.get("SN"));
        LogUtil.e("rxmsg cid=" + jo.get("CID")
                + ",sn=" + jo.get("SN"));
        if (cid == CloudBridgeUtil.CID_KICK_DOWN) {
            mApp.sdcardLog("NetService websocketEndpoint CID_KICK_DOWN");
        } else if (cid == CloudBridgeUtil.CID_MY_PING + 1) {
            LogUtil.d("Rx:" + "pong");
            mApp.sdcardLog("NetService websocketEndpoint receiveHeartbeat,time=" + TimeUtil.getTimeStampLocal());
            if (autoPingCount > 0) {
                autoPingCount--;
                long delta = System.currentTimeMillis() - pingtime;
                Intent pingIt = new Intent(Const.ACTION_APP_PING_RESULT);
                pingIt.putExtra("ping", delta);
                sendBroadcast(pingIt);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryPing();
                    }
                }, 2000);
            } else {
                resetHeartbeatCount();
            }
        }

        if (isSNExist(jo)) {
            resetHeartbeatCount();
        }

        Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_WEBSOCKET_RECV);
        Bundle bd = newmsg.getData();
        bd.putString(Const.KEY_VALUE_JSONOBJECT, jo.toString());
        newmsg.sendToTarget();
    }

    synchronized private boolean isSNExist(JSONObject message) {
        boolean isExist = false;
        JSONObject recvMsg = message;
        Integer recvSN = null;
        int recvCID = 0;
        try {
            recvSN = (Integer) recvMsg.get("SN");
            recvCID = (Integer) recvMsg.get("CID");
            if (mMsgQueue.size() > 0) {
                for (MyMsgData msg : mMsgQueue) {
                    Integer reqSN = (Integer) msg.reqMsg.get("SN");
                    int reqCID = (Integer) msg.reqMsg.get("CID");
                    if (reqSN != null && recvSN != null) {
                        if (recvSN.compareTo(reqSN) == 0 && (recvCID == reqCID + 1)) {
                            isExist = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExist;
    }

    public void sendSessionPing() {
        int sn;
        MyMsgData askJoinMsg = new MyMsgData();
        askJoinMsg.setCallback(null);
        //set msg body
        MiPushClient.setRegion(Region.Global);
        String region = XimalayaUtil.getMipushRegion(this);
        LogUtil.e("mipush app region11:" + region);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_ADS, SystemUtils.getDeviceInfo(getApplicationContext()));
        pl.put(CloudBridgeUtil.KEY_NAME_REGION, region);
        pl.put(Const.SHARE_PREF_FIELD_PUSH_TOKEN,mApp.getStringValue(Const.KEY_FCM_TOKEN,""));
        askJoinMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_CHECK_SESSION, sn, mApp.getToken(), pl));
        sendLoginMsg(askJoinMsg);
    }

    public void sendMyPing() {
        int sn;
        MyMsgData askJoinMsg = new MyMsgData();
        askJoinMsg.setCallback(null);
        //set msg body
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        askJoinMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MY_PING, sn, mApp.getToken(), null));
        sendNetMsg(askJoinMsg);
    }

    private void openWebSocketENdpoint(final IEndpoint websocketEndpoint2,
                                       final String websocketUrl) {
        //加锁改成同步加锁，
//       isWebSocketConnecting = true;
        new MioAsyncTask<String, Void, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    myHandler.removeMessages(Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT);
                    Message msg = myHandler.obtainMessage(Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT);
                    myHandler.sendMessageDelayed(msg, OPEN_WEB_SOCKET_TIMEOUT);

                    mApp.sdcardLog("NetService websocketEndpoint try open");
                    LogUtil.e("NetService websocketEndpoint try open");
                    LogUtil.e("NetService websocketEndpoint = " + websocketEndpoint);
                    LogUtil.e("NetService websocketUrl = " + websocketUrl);
                    if (websocketEndpoint != null && websocketUrl != null) {
                        websocketEndpoint.open(websocketUrl);
                    }

                    //Thread.sleep(OPEN_WEB_SOCKET_TIMEOUT);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
//                    isWebSocketConnecting = false;
                    mApp.sdcardLog("NetService websocketEndpoint try open error:" + e.getMessage());
                    e.printStackTrace();
                    myHandler.removeMessages(Const.MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT);
                    closeWebSocket();
                }
                return true;

            }

            protected void onCancelled() {
                super.onCancelled();
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
            }
        }.execute();
    }

    private void stopCloudBridgeClient() {
        closeWebSocketLogout();
    }

    private void doNoPongTimeout() {
        mApp.sdcardLog("NetService websocketEndpoint doNoPongTimeout,time=" + TimeUtil.getTimeStampLocal());
        closeWebSocket();
        Intent intent = new Intent(Const.ACTION_NO_PONG_OFFLINE);
        sendBroadcast(intent);
    }

    private int heartbeatCount = 0;
    public static final int HEART_BEAT_TIME = 240 * 1000;//心跳最低间隔4分钟
    private int mHeartBeatCheckTime = 270 * 1000;//心跳检查修改成4分半钟
    public static long lastPingTime = 0;
    public static final int MSG_QUEUE_INTERVAL = 20000;//millsecond

    private void doQueueLoop() {
        try {
            try {
                checkMsgQueueTimeout(MSG_QUEUE_INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //检查断网重连
            if (isWebSocketOK == false) {
                mApp.sdcardLog("isNetworkOK=" + isNetworkOK());
            }
            if (isNetworkOK() && (!isWebSocketOK || (timeoutCounter > MAX_TIMEOUT_COUNTER))) {//增加一下断网重连的间隔，防止太频繁操作
                mApp.sdcardLog("restartCloudBridgeClient");
                restartCloudBridgeClient();
            }
        } catch (Exception e) {
            mApp.sdcardLog("NetService--->doQueueLoop . error:" + e.getMessage());
        } finally {

        }
    }

    private void sendHello(String watcheid) {
        String[] Teids = new String[1];
        MyMsgData askJoinMsg_1 = new MyMsgData();
        askJoinMsg_1.setCallback(NetService.this);
        Teids[0] = watcheid;
        //测试环境，直接加入group，跳过请求应答e2e
        JSONObject newPl_1 = new JSONObject();
        newPl_1.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_HELLO);
        newPl_1.put(CloudBridgeUtil.KEY_NAME_EID, watcheid);
        int SN = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

        askJoinMsg_1.setReqMsg(
                CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, SN, mApp.getToken(), null, Teids, newPl_1));
        sendNetMsg(askJoinMsg_1);
//        ToastUtil.showMyToast(BindResultActivity.this, "发送入组请求",  Toast.LENGTH_SHORT);
    }

    synchronized private void addMsgQueue(MyMsgData msg) {
        mMsgQueue.add(msg);
    }

    synchronized private void networkErrorMsgQueueResp() {

        if (mMsgQueue.size() > 0) {
            for (MyMsgData msg : mMsgQueue) {

                JSONObject respMsg = new JSONObject();
                respMsg.put("SN", msg.reqMsg.get("SN"));
                respMsg.put("CID", (Integer) msg.reqMsg.get("CID") + 1);
                // 上行和下行CID匹配？
                //如有特殊CID不符合需要匹配
                respMsg.put("RC", CloudBridgeUtil.RC_NETERROR);
                if (msg.getNeedNetTimeout()) {
                    Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_NETWORK_OR_WEBSOCKET_ERROR);
                    Bundle bd = newmsg.getData();
                    bd.putString(Const.KEY_VALUE_JSONOBJECT, respMsg.toJSONString());
                    myHandler.sendMessageDelayed(newmsg, NETWORK_ERROR_MESSAGE_TIMEOUT);
                } else {
                    Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_WEBSOCKET_RECV);
                    Bundle bd = newmsg.getData();
                    bd.putString(Const.KEY_VALUE_JSONOBJECT, respMsg.toJSONString());
                    newmsg.sendToTarget();
                }
            }
        }
    }

    synchronized private void checkMsgQueueResp(JSONObject recvMsg) {
        List<MyMsgData> removeList = new ArrayList<MyMsgData>();
        Integer recvSN = null;
        int recvCID = 0;
        try {
            recvSN = (Integer) recvMsg.get("SN");
            recvCID = (Integer) recvMsg.get("CID");
        } catch (Exception e) {
            // TODO: handle exception
            JSONObject respMsg = (JSONObject) recvMsg.clone();
            NetService.this.doCallBack(null, respMsg); //找不到req，直接处理resp
        }

        boolean findReq = false;
        if (mMsgQueue.size() > 0) {
            for (MyMsgData msg : mMsgQueue) {
                Integer reqSN = (Integer) msg.reqMsg.get("SN");
                int reqCID = (Integer) msg.reqMsg.get("CID");
                if (reqSN != null && recvSN != null) {
                    if (recvSN.compareTo(reqSN) == 0 && (recvCID == reqCID + 1)) {//找到匹配的req
                        JSONObject respMsg = (JSONObject) recvMsg.clone();
                        checkWatchOffline(respMsg);
                        checkWatchLowSigal(respMsg);
                        LogUtil.e("msg.reqMsg = " + msg.reqMsg);
                        LogUtil.e("msg.callback = " + msg.callback);
                        if (msg.callback != null) {
                            msg.callback.doCallBack(msg.reqMsg, respMsg);
                        } else {
                            NetService.this.doCallBack(msg.reqMsg, respMsg);
                        }
                        HandlerMapSetRespWatchOfflineState(msg.reqMsg, respMsg);
                        removeList.add(msg);
                        findReq = true;
                        break;
                    } else {

                    }
                }
            }
            if (findReq == false) {
                JSONObject respMsg = (JSONObject) recvMsg.clone();
                checkWatchOffline(respMsg);
                checkWatchLowSigal(respMsg);
                NetService.this.doCallBack(null, respMsg); //找不到req，直接处理resp
            }
            mMsgQueue.removeAll(removeList);
        } else {
            if (findReq == false) {
                JSONObject respMsg = (JSONObject) recvMsg.clone();
                checkWatchOffline(respMsg);
                checkWatchLowSigal(respMsg);
                NetService.this.doCallBack(null, respMsg); //找不到req，直接处理resp
            }
        }
    }

    synchronized private void checkMsgQueueTimeout(int interval) {
        //List<MyMsgData> removeList = new ArrayList<MyMsgData>();
        if (mMsgQueue.size() > 0) {
            for (MyMsgData msg : mMsgQueue) {
                if (msg.timeout < interval) {

                    JSONObject respMsg = new JSONObject();
                    respMsg.put("SN", msg.reqMsg.get("SN"));
                    respMsg.put("CID", (Integer) msg.reqMsg.get("CID") + 1);
                    // 上行和下行CID匹配？
                    //如有特殊CID不符合需要匹配

                    respMsg.put("RC", CloudBridgeUtil.RC_TIMEOUT);
                    //异步报错
                    Message newmsg = myHandler.obtainMessage(Const.MSG_RESPONSE_WEBSOCKET_RECV);
                    Bundle bd = newmsg.getData();
                    bd.putString(Const.KEY_VALUE_JSONOBJECT, respMsg.toJSONString());
                    newmsg.sendToTarget();
                } else {
                    msg.timeout = msg.timeout - interval;
                }
            }
        }
    }

    /**
     * 类名称：NetService
     * 创建人：zhangjun5
     * 创建时间：2016/4/28 15:10
     * 方法描述：根据手表是否离线来设定信号是否为弱信号,以及弱信号的恢复
     * 方法修改：20170303 by hyy  1.是否为弱信号通过0或1判断、弱信号恢复时置信号为中等信号
     */
    private void checkWatchLowSigal(JSONObject respMsg) {

        if (respMsg == null) {
            return;
        }
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        String seid = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_SEID);
        if (cid != CloudBridgeUtil.CID_E2E_DOWN && cid != CloudBridgeUtil.CID_C2E_NEW_LOCATION_NOTIFY
                && cid != CloudBridgeUtil.CID_C2E_TRACK_LOCATION_NOTIFY && cid != CloudBridgeUtil.CID_E2G_DOWN) {
            return;
        }
        //CID_E2G_DOWN消息，只对语音进行处理 非语音消息直接跳出
        if (cid == CloudBridgeUtil.CID_E2G_DOWN) {
            JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
            if (null == pl) {
                return;
            }
            if (pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == null) {
                return;
            }
            int action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
            if (action != CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE &&
                    action != CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE_OLD) {
                return;
            }
        }
        //CID_E2G_DOWN消息，只对语音进行处理 非语音消息直接跳出
        if (cid == CloudBridgeUtil.CID_E2E_DOWN) {
            JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
            if (null == pl) {
                return;
            }
            if (pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == null) {
                return;
            }
            int action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
            if (action != CloudBridgeUtil.SUB_ACTION_SERVER_TO_ENDPOINT_NOTICE) {
                return;
            }
        }
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        //对所有160信息处理，直接修改设备状态
        if (rc == -160) {
            seid = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_OFFLINE);
            mApp.setValue(seid + CloudBridgeUtil.SIGNAL_LEVEL_FLAG, "1"); //1表示弱信号
            Intent intent = new Intent(Const.ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE);
            intent.putExtra(Const.KEY_WATCH_ID, seid);
            sendBroadcast(intent);
            if (null != seid) {
                mApp.setmWatchOfflineState(seid, 1);
                Intent intent1 = new Intent(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
                sendBroadcast(intent1);
            }
            return;
        }

        if (null != seid) {
            mApp.setmWatchOfflineState(seid, 0);
            Intent intent1 = new Intent(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
            sendBroadcast(intent1);
        }

        if (!mApp.getStringValue(seid + CloudBridgeUtil.SIGNAL_LEVEL_FLAG, "0").equals("0")) {
            String sl = TimeUtil.getTimeStampLocal() + "_" + "50";
            mApp.setValue(seid + CloudBridgeUtil.SIGNAL_LEVEL, sl);//本地保存低信号信息
            mApp.setValue(seid + CloudBridgeUtil.SIGNAL_LEVEL_FLAG, "0"); //0表示正常
            Intent intent = new Intent(Const.ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE);
            intent.putExtra(Const.KEY_WATCH_ID, seid);
            intent.putExtra(CloudBridgeUtil.SIGNAL_LEVEL, sl);
            sendBroadcast(intent);
        }
    }

    private void HandlerMapSetRespWatchOfflineState(JSONObject reqMsg, JSONObject respMsg) {
        if (respMsg == null) {
            return;
        }
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        if (cid != CloudBridgeUtil.CID_MAPSET_RESP) {
            return;
        }
        JSONObject pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_TEID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        if (rc == 1) {
            //获取当前手表是否在线的状态信息
            mApp.getNetService().getDeviceOfflineState(eid);
            mApp.isDeviceOfflineMapSet = true;
        }
    }

    //检查手表是否离线
    private boolean checkWatchOffline(JSONObject respMsg) {
        if (respMsg == null) {
            return false;
        }
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        if (cid != CloudBridgeUtil.CID_E2E_DOWN) {
            return false;
        }
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        if (rc == -160) {
            String offlineEid = (String) respMsg.get("Offline");
            if (null != offlineEid) {
                mApp.setmWatchOfflineState(offlineEid, 1);
                Intent intent1 = new Intent(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
                sendBroadcast(intent1);
            }
        }

        return false;
    }

    private ArrayList<FamilyData> mFamilyList;

    //处理异步响应的msg，在这里预处理，后台处理后分发
    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        String seid;
        int action;
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = 0;
        int sn = CloudBridgeUtil.getCloudMsgSN(respMsg);
        String rn;
        if (respMsg.containsKey(CloudBridgeUtil.KEY_NAME_RC)) {
            rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        }
        if (rc < 0) {
            rn = CloudBridgeUtil.getCloudMsgRN(respMsg);
        }

        if (cid == 2) {
            //heartbeat
            return;
        }
        JSONObject pl;
        if (cid < 1000) {
            LogUtil.e("msg cid:" + cid);
            pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
            pl.get("ss");
            return;
        }
        //pl 可能为null，即该消息没有内容部分，只有头，或者错误吗

        //reqmsg可能为null，就是收到下发的msg
        switch (cid) {
            case CloudBridgeUtil.CID_GET_PRIVATE_CHAT_MULTI_PKT_RESP:
                if (rc != CloudBridgeUtil.RC_SUCCESS) {
                    return;
                }
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                JSONArray list = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_LIST);
                if (list.size() == 0) {
                    String markKey = (String) pl.get(CloudBridgeUtil.KEY_NAME_MARK_KEY);
                    if (markKey != null && markKey.length() > 0) {
                        String key = markKey.substring(markKey.lastIndexOf("/") + 1);
                        mApp.setNextPrivateChatKey(mApp.getCurUser().getEid(), TimeUtil.timeInc(TimeUtil.getOrderTime(key)));
                    }
                    return;
                }
                ChatMsgEntity entity = null;
                for (Object obj : list) {
                    entity = handlePrivateChat((JSONObject) obj);
                }
                MessageUtils.addOfflineMsgCount(mApp);
                Intent intt = new Intent();
                intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
                sendBroadcast(intt);
                sendBroadcast(new Intent(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY));
                break;
            case CloudBridgeUtil.CID_GET_GROUP_ALL_CHAT_RESP:
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    handleGroupAllMessage(pl);
                } else {
                    LogUtil.e("error rc = " + rc);
                }
                break;
            case CloudBridgeUtil.CID_REQ_JOIN_WATCH_GROUP_RESP: {
                String sdt = mApp.getBindRequsetSN().get(respMsg.get(CloudBridgeUtil.KEY_NAME_SN) + "");
                if (mApp.getRunningActivityName().equals(BindResultActivity.class.getName()) && sdt != null && sdt.length() > 0 && sdt.equals("0")) {
                    Intent it2 = new Intent(Const.ACTION_RECEIVE_REQ_JOIN_WATCH_GROUP_RESP);
                    it2.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                    sendBroadcast(it2);
                } else {
                    rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (rc == -156) {
                        DialogUtil.ShowCustomSingleTopSystemDialog(getApplicationContext(),
                                getString(R.string.prompt),
                                getString(R.string.admin_denied_bind_request),
                                null,
                                null,
                                new DialogUtil.OnCustomDialogListener() {

                                    @Override
                                    public void onClick(View v) {

                                    }
                                },
                                getText(R.string.confirm).toString());

                    } else if (rc == 1) {
                        JSONObject repl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String curWatchEid = (String) repl.get("EID");
                        int sdt1 = 1;
                        if (repl.get("sdt") != null) {
                            sdt1 = (Integer) repl.get("sdt");
                        }
                        sendGetContactReq(curWatchEid);
                        sendQueryAllGroups(curWatchEid, sdt1);
                    } else {
                        Intent it2 = new Intent(Const.ACTION_RECEIVE_REQ_JOIN_WATCH_GROUP_RESP);
                        it2.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                        sendBroadcast(it2);
                    }
                }
            }
            break;
            case CloudBridgeUtil.CID_REQ_ADD_NEW_FIEND_RESP:
                Intent newfriend = new Intent(Const.ACTION_RECEIVE_ADD_NEW_FRIEND_RESP);
                newfriend.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                sendBroadcast(newfriend);
                break;
            case CloudBridgeUtil.CID_C2E_NEW_LOCATION_NOTIFY:
            case CloudBridgeUtil.CID_C2E_TRACK_LOCATION_NOTIFY://定位策略的调试功能
                mApp.sdcardLog(respMsg.toString());
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (1 == rc) {
                    int sostype = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SOS);
                    if (1 == sostype) {
                        SosWarning sosWarning = new SosWarning();
                        sosWarning.setmTimestamp((String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP));
                        sosWarning.setmEid((String) pl.get(CloudBridgeUtil.KEY_NAME_EID));
                        refreshSosInfo(mApp.getCurUser().queryWatchDataByEid((String) pl.get(CloudBridgeUtil.KEY_NAME_EID)),
                                sosWarning, pl);
                    }
                }
                Intent it2 = new Intent(Const.ACTION_RECEIVE_NEW_LOCATION_NOTIFY);
                it2.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                sendBroadcast(it2);
                break;
            case CloudBridgeUtil.CID_E2G_DOWN:
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                seid = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_SEID);
                Integer srcType = null;
                if (pl != null && pl.get("sourceType") != null) {
                    srcType = (Integer) pl.get("sourceType");//兼容智能家庭接口， 从其他来源的消息
                }

                if (seid != null) {
                    if (srcType == null
                            && (mApp.getCurUser() == null || mApp.getCurUser().getEid() == null || mApp.getCurUser().getEid().equals(seid))) {
                        break;//正常来源的e2g，过滤掉自己发送的
                    }

                    if (pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == null) {
                        //云桥的通知
                        mApp.sdcardLog("e2g server notice respMsg content= " + respMsg.toString());
                    } else {
                        action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                        switch (action) {
                            //sim卡更换时的通知消息，更新手表的手机信息
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_UPDATE_SIM_CARD_INFO:
                                String iccid = (String) pl.get(CloudBridgeUtil.KEY_NAME_ICCID);
                                String phoneNum = (String) pl.get(CloudBridgeUtil.KEY_NAME_SIM_NO);
                                String Eid = (String) pl.get(CloudBridgeUtil.PL_KEY_EID);
                                if (Eid == null) {
                                    Eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                }
                                for (int i = 0; i < mApp.getWatchList().size(); i++) {
                                    final WatchData watchtmp = mApp.getWatchList().get(i);
                                    if (watchtmp.getEid().equals(Eid)) {
                                        //号码格式化
                                        if (phoneNum != null) {
                                            phoneNum = StrUtil.formatPhoneNumber(phoneNum);
                                        }
                                        //增加是否变化的过滤
                                        if (watchtmp.getCellNum() != null && phoneNum != null && watchtmp.getCellNum().equals(phoneNum)
                                                && iccid != null && watchtmp.getIccid() != null && watchtmp.getIccid().equals(iccid)) {
                                            //没有变化，不提示

                                        } else {
                                            NoticeMsgData msg = new NoticeMsgData();
                                            msg.setmSrcid(watchtmp.getEid());
                                            msg.setmGroupid(watchtmp.getFamilyId());
                                            String key = (String) pl.get("Key");
                                            TimeUtil.getOrderTime(key);
                                            msg.setmTimeStamp(TimeUtil.getOrderTime(key));
                                            msg.setmType(NoticeMsgData.MSG_TYPE_CHANGE_SIM);
                                            JSONObject content = new JSONObject();
                                            content.put("Iccid", iccid);
                                            content.put("SimNo", phoneNum);
                                            msg.setmContent(content.toString());
                                            NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(msg.getmGroupid(), mApp.getCurUser().getEid(), msg);
                                            Intent intent = new Intent(Const.ACTION_RECEIVE_NOTICE_MSG);
                                            intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_ALL);
                                            sendBroadcast(intent);
                                            watchtmp.setIccid(iccid);
                                            watchtmp.setCellNum(phoneNum);
                                            WatchDAO.getInstance(getApplicationContext()).addWatch(watchtmp);
                                            mApp.setValue(watchtmp.getEid() + Const.SHARE_PREF_SMS_NUMBER, "101");

                                            String textStr = getString(R.string.watch_number_change_null_tips, "<font color=\"#12a7e5\">" + "\"" + watchtmp.getNickname() + "\"" + "</font>");
                                            if (watchtmp.getCellNum() != null && watchtmp.getCellNum().length() > 0) {
                                                textStr = getString(R.string.watch_number_change_tips, "<font color=\"#12a7e5\">" + "\"" + watchtmp.getNickname() + "\"" + "</font>", "<font color=\"#000000\">" + watchtmp.getCellNum() + "</font>");
                                            }

                                            DialogUtil.ShowCustomSpanSystemDialog(getApplicationContext(),
                                                    getString(R.string.watch_number_change_title),
                                                    Html.fromHtml(textStr),
                                                    null,
                                                    null,
                                                    new OnCustomDialogListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            if (watchtmp.getCellNum() != null && watchtmp.getCellNum().length() > 0) {
                                                                Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                                                                it.putExtra("eid", watchtmp.getEid());
                                                                sendBroadcast(it);
                                                            }
                                                            if (mApp.getCurUser().isMeAdminByWatch(watchtmp)) {
                                                                Intent it = new Intent(getApplicationContext(), SetDeviceNumberActivity.class);
                                                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                it.putExtra(Const.KEY_WATCH_ID, watchtmp.getEid());
                                                                startActivity(it);
                                                            }
                                                        }
                                                    },
                                                    getText(R.string.confirm).toString());
                                        }
                                    }
                                }

                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_BATTERY_LEVEL:
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_STATE_CHANGE:
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_STATE_CHANGE_NOTICE:
                                String watcheid = (String) pl.get(CloudBridgeUtil.PL_KEY_EID);
                                if (watcheid == null)
                                    watcheid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                int state;
                                mApp.sdcardLog("WatchState msg= " + respMsg.toString());
                                if (action == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_BATTERY_LEVEL) {
                                    String value = (String) pl.get(CloudBridgeUtil.BATTERY_LEVEL);
                                    handleMapMGetBattery(watcheid, value);
                                }

                                if (action != CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_BATTERY_LEVEL) {
                                    String time = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_STATE_TIMESTAMP);
                                    String str = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_STATE);
                                    if (str.isEmpty()) {
                                        int index = time.indexOf("_");
                                        if (index == -1) {// not found
                                            return;
                                        } else {
                                            state = Integer.valueOf(time.substring(index + 1));
                                        }
                                    } else {
                                        state = Integer.valueOf(str);
                                    }
                                    LogUtil.d("WatchState" + "  " + "Recv subaction: " + action);
                                    Intent it = new Intent(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
                                    it.putExtra(Const.KEY_WATCH_ID, watcheid);
                                    it.putExtra(Const.KEY_WATCH_STATE, state);
                                    it.putExtra(Const.KEY_WATCH_STATE_TIMESTAMP, time);
                                    sendBroadcast(it);
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE_OLD:
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE: {
                                LogUtil.e("sub_action 102 "+ action  +" respMsg = "+respMsg.toJSONString());
                                Intent it = new Intent(Const.ACTION_RECEIVE_CHAT_MSG);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(it);
                            }
                            break;

                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP: {
                                Intent it = new Intent(Const.ACTION_RECEIVE_REQ_JOIN_GROUP);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE: {
//                        //加入group 应答
                                String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
                                String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);

                                String type = (String) pl.get(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE);
                                if (eid.equals(mApp.getCurUser().getEid())
                                        && type.equals("2")) {
                                    mFamilyList = mApp.getCurUser().getFamilyList();
                                    //自己被移除
                                    for (FamilyData family : mFamilyList) {
                                        if (gid.equals(family.getFamilyId())) {
                                            //每个家庭的手表是唯一的，get(0)即可
                                            WatchData curWatch = family.getWatchlist().get(0);
                                            //先发e2c，保存下teids 然后再删除本地，
//                                    sendGroupChangeE2C(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP, eid, gid, getMyApp().getCurUser().getNickname());
                                            mFamilyList.remove(family);
                                            //clear old group del db
                                            UserRelationDAO.getInstance(getApplicationContext()).cleanUsersByGid(gid);
                                            //remove  watch
                                            mApp.getCurUser().getWatchList().remove(curWatch);
                                            //reset focus
                                            if (mApp.getCurUser().getWatchList().size() > 0) {
                                                if (curWatch.equals(mApp.getCurUser().getFocusWatch())) {
                                                    mApp.getCurUser().setFocusWatch(mApp.getCurUser().getWatchList().get(0));
                                                    mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH));
                                                } else {
                                                    Intent it = new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH);
                                                    it.putExtra(Const.KEY_FAMILY_ID, curWatch.getFamilyId());
                                                    mApp.getLocalBroadcastManager().sendBroadcast(it);
                                                }
                                            } else {
                                                //?移除成员
                                                //mApp.doLogout("CID_E2G_DOWN be remove out");
                                                Intent it = new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH);
                                                mApp.getLocalBroadcastManager().sendBroadcast(it);
                                            }

                                            if ((srcType != null && srcType == 1) && seid.equals(mApp.getCurUser().getEid())) {//srctype是1 ，智能家庭发起的
                                                ToastUtil.showMyToast(this, getString(R.string.quit_another, curWatch.getNickname()), Toast.LENGTH_SHORT);
                                            } else {
                                                ToastUtil.showMyToast(this, getString(R.string.remove_by_admin, curWatch.getNickname()), Toast.LENGTH_SHORT);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    //refresh group
                                    sendBroadcast(new Intent(Const.ACTION_QUERY_ALL_GROUPS));
                                    //send broadcast
                                    Intent it1 = new Intent(Const.ACTION_RECEIVE_GROUP_CHANGE_MSG);
                                    it1.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                    sendBroadcast(it1);
                                }
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE: {
                                String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
                                String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                sendDeviceGet(eid);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_USER_CHANGE_NOTICE:
                                sendBroadcast(new Intent(Const.ACTION_QUERY_ALL_GROUPS));
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_LOCATION: {
                                Intent it = new Intent(Const.ACTION_LOCATION_RESP);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SOS: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SOS);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_ACROSS_CITY_REMIDD:
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_SAFEAREA: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SAFEAREA);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_SAFEDANGERDRAW: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SAFEDANGERDRAW);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_LOWPOWER: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_POWER);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;


                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_REMOVE_OUT_PRE_NOTICE:
                                String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
                                if (gid != null && gid.length() > 0) {
                                    WatchData watch = null;

                                    mFamilyList = mApp.getCurUser().getFamilyList();
                                    //remove local
                                    for (FamilyData family : mFamilyList) {
                                        if (gid.equals(family.getFamilyId())) {
                                            watch = family.getWatchlist().get(0);
                                            mFamilyList.remove(family);
                                            //clear old group del db
                                            UserRelationDAO.getInstance(getApplicationContext()).cleanUsersByGid(gid);
                                            //remove  watch
                                            mApp.getCurUser().getWatchList().remove(watch);
                                            //reset focus

                                            if (mApp.getCurUser().getWatchList().size() > 0) {
                                                if (watch.equals(mApp.getCurUser().getFocusWatch())) {
                                                    mApp.getCurUser().setFocusWatch(mApp.getCurUser().getWatchList().get(0));
                                                    mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH));
                                                } else {
                                                    mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_OTHER_WATCH));
                                                }
                                            } else {
                                                //?
                                                //mApp.doLogout("CID_E2G_DOWN  group be remove  by admin");
                                                mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_OTHER_WATCH));
                                            }

                                            ToastUtil.showMyToast(this, getString(R.string.dismiss_by_admin, watch.getNickname()), Toast.LENGTH_SHORT);
                                            break;
                                        }
                                    }

                                    //删除自己从gid中

                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_OTA_RESULT: {
                                Intent it = new Intent(Const.ACTION_RECEIVE_OTA_RESULT);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_START:
                                if (seid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviStart(pl.toString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_UPDATE_ROUTE_PLAN:
                                if (seid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviStart(pl.toString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_CURRENT_POINT:
                                if (seid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviCurrentPoint(pl.toString());
                                }
                                break;
                            default:
                                break;

                        }
                    }
                } else {

                }
                break;
            //C2E处理
            case CloudBridgeUtil.CID_E2E_DOWN:

                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (pl == null) {//容错处理，
                    break;
                }
                if (pl.containsKey(CloudBridgeUtil.KEY_NAME_EFID)) { //电子围栏告警
                    Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SAFEAREA);
                    it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                    sendBroadcast(it);
                } else if (pl.containsKey(CloudBridgeUtil.KEY_AREA_EVENT)) { //安全危险区域
                    Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SAFEDANGERDRAW);
                    it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                    sendBroadcast(it);
                }  else if (respMsg.containsKey(CloudBridgeUtil.KEY_NAME_RC)) {//e2e回复下行
                    if (rc == CloudBridgeUtil.RC_SUCCESS) {//对方回复的e2e

                        seid = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_SEID);
                        action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                        if (pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == null) {
                            //服务器的下行，没有subaction，服务器需要跟踪一下
                            mApp.sdcardLog("e2g server notice respMsg content= " + respMsg.toString());
                        } else {
                            switch (action) {
                                case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP:

                                    break;
                                case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ANSWER_JOIN_GROUP:

                                    break;
                                case 301:
                                    Intent it = new Intent("action.testpoint.watchdata");
                                    it.putExtra("watchdata", respMsg.toJSONString());

                                    sendBroadcast(it);
                                    break;
                                case 110:
                                    Intent it1 = new Intent("action.testpdr");
                                    it1.putExtra("pdrdata", respMsg.toJSONString());
                                    sendBroadcast(it1);
                                    break;
                                default:
                                    break;
                            }
                        }

                    } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {

                    } else {

                    }
                } else {//接受到e2e下行
                    if (pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == null) {
                        //服务器的下行，没有subaction，服务器需要跟踪一下
                        mApp.sdcardLog("e2g server notice respMsg content= " + respMsg.toString());
                    } else {
                        action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                        switch (action) {
                            case CloudBridgeUtil.SUB_ACTION_AD_ON_OFF://接收到紧急关闭广告的通知
//                                String ADSplashOnOff = Const.KEY_NAME_SPLASH_ON_OFF+getPackageName();
//                                String ADMainOnOff = Const.KEY_NAME_MAIN_AD_ON_OFF+getPackageName();
                                if (pl.get(Const.KEY_NAME_SPLASH_ON_OFF) != null) {
                                    Integer splashOnOff = Integer.parseInt((String) pl.get(Const.KEY_NAME_SPLASH_ON_OFF));
                                    if (splashOnOff == 0) {
                                        mApp.setAdSplashOnOff(false);
                                        mApp.setValue(Const.SHARE_PREF_AD_SPLASH_ONOFF, 0);
                                    } else if (splashOnOff == 1) {
                                        mApp.setAdSplashOnOff(true);
                                        mApp.setValue(Const.SHARE_PREF_AD_SPLASH_ONOFF, 1);
                                    }
                                }
                                if (pl.get(Const.KEY_NAME_MAIN_AD_ON_OFF) != null) {
                                    Integer mainADOnOff = Integer.parseInt((String) pl.get(Const.KEY_NAME_MAIN_AD_ON_OFF));
                                    if (mainADOnOff == 0) {
                                        mApp.setAdMainAdOnOff(false);
                                        mApp.setValue(Const.SHARE_PREF_AD_MAINPAGE_ONOFF, 0);
                                    } else if (mainADOnOff == 1) {
                                        mApp.setAdMainAdOnOff(true);
                                        mApp.setValue(Const.SHARE_PREF_AD_MAINPAGE_ONOFF, 1);
                                    }
                                }
                                if (pl.get(CloudBridgeUtil.KEY_NAME_STORY_SWITCH) != null) {
                                    Integer visible = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_STORY_SWITCH);
                                    mApp.setValue(Const.SHARE_PREF_FIELD_STORY_VISIBLE, visible);
                                    Intent it = new Intent(Const.ACTION_STORY_VISIBLE_CHANGE);
                                    sendBroadcast(it);
                                }
                                break;
                            case 515:
                                //拉取群组消息
                                sendQueryAllGroups(0);
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SERVER_NOTIFY:
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_REQ_ADD_NEW_FRIEND:
                                Intent friednIntent = new Intent(ACTION_RECEIVE_REQ_ADD_NEW_FRIEND);
                                friednIntent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(friednIntent);
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP: {
                                if (pl.containsKey(CloudBridgeUtil.KEY_NAME_RC)) {

                                    Intent it = new Intent(Const.ACTION_RECEIVE_RESPONSE_JOIN_GROUP);
                                    it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                    sendBroadcast(it);
                                } else {
                                    Intent it = new Intent(Const.ACTION_RECEIVE_REQ_JOIN_GROUP);
                                    it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                    sendBroadcast(it);
                                }
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE: {
//                          //加入group 应答
                                String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
                                String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                //refresh group
                                sendBroadcast(new Intent(Const.ACTION_QUERY_ALL_GROUPS));
                                //send broadcast
                                Intent it1 = new Intent(Const.ACTION_RECEIVE_GROUP_CHANGE_MSG);
                                it1.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(it1);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE_OLD:
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE: {
                                Intent it = new Intent(Const.ACTION_RECEIVE_CHAT_MSG);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_LOCATION: {
                                Intent it = new Intent(Const.ACTION_LOCATION_RESP);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SOS: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SOS);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_ACROSS_CITY_REMIDD:
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_SAFEAREA:
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_NOTICE_LOWPOWER: {
                                Intent it = new Intent(Const.ACTION_WARNNING_TYPE_POWER);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                            }
                            break;

                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_REMOVE_OUT_PRE_NOTICE:
                                String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
                                String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);


                                if ((eid == null || eid.length() == 0)
                                        || eid.equalsIgnoreCase(mApp.getCurUser().getEid())) {
                                    WatchData watch = null;

                                    mFamilyList = mApp.getCurUser().getFamilyList();
                                    //remove local
                                    for (FamilyData family : mFamilyList) {
                                        if (gid.equals(family.getFamilyId())) {
                                            watch = family.getWatchlist().get(0);
                                            mFamilyList.remove(family);
                                            //clear old group del db
                                            UserRelationDAO.getInstance(getApplicationContext()).cleanUsersByGid(gid);
                                            //remove  watch
                                            mApp.getCurUser().getWatchList().remove(watch);
                                            //reset focus

                                            if (mApp.getCurUser().getWatchList().size() > 0) {
                                                if (watch.equals(mApp.getCurUser().getFocusWatch())) {
                                                    mApp.getCurUser().setFocusWatch(mApp.getCurUser().getWatchList().get(0));
                                                    mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH));
                                                } else {
                                                    mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_OTHER_WATCH));
                                                }

                                            } else {
                                                //?
                                                //mApp.doLogout("e2e be remove by admin");
                                                mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_OTHER_WATCH));
                                            }
                                            break;
                                        }
                                    }

                                    ToastUtil.showMyToast(this, getString(R.string.remove_by_admin, watch.getNickname()), Toast.LENGTH_SHORT);
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FIRMWARE_UPDATE_START:
                                Intent it = new Intent(Const.ACTION_FIRMWARE_UPDATE_CHECKRESULT);
                                it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(it);
                                break;

                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_SHUTDOWN_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());

                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_ALARM_TIME_RANGE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_ALARM_SETTING_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DELETE_ALARM_TIME_RANGE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_ALARM_DELETE_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SET_SILENCE_TIME_RANGE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_SLIENCE_TIME_SET_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_MODIFY_SILENCE_TIME_RANGE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_SLIENCE_TIME_CHANGED_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DELETE_SILENCE_TIME_RANGE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_SLIENCE_TIME_DELETE_CHECKRESULT);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE: {
                                Intent intent = new Intent(Const.ACTION_CONTACT_CHANGE);
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DOWNLOAD_CHANGE_NOTICE: {
                                Intent intent = new Intent(Const.ACTION_WATCH_DOWNLOAD_CHANGE);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_STORY_LIST: {
                                Intent intent = new Intent(Const.ACTION_WATCH_GET_STORY_LIST_7XX);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DEL_CHOOSE_STORY: {
                                Intent intent = new Intent(Const.ACTION_WATCH_DEL_STORY_CHOOSE_7XX);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            //追踪模式下的e2g数据
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_TRACE_STATES: {
                                Intent intent = new Intent(Const.ACTION_SELECT_TARCE_TO_MODE);
                                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                                sendBroadcast(intent);
                            }
                            break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL:
                                if (!respMsg.containsKey(CloudBridgeUtil.KEY_NAME_RC)) {
                                    JSONObject videoPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                                    LogUtil.e("NetSetvice -- SUB_ACTION_VALUE_NAME_VIDEO_CALL  = " + videoPl.toJSONString());
                                    receiveVideoCallReq(videoPl.toJSONString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL_END:
                                JSONObject videoPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                                LogUtil.e("NetSetvice -- SUB_ACTION_VALUE_NAME_VIDEO_CALL_END  = " + videoPl.toJSONString());
                                receiveVideoCallReq(videoPl.toJSONString());
                                break;

                            case CloudBridgeUtil.SUB_ACTION_SERVER_TO_ENDPOINT_NOTICE: {
                                LogUtil.e("NetSetvice -- SUB_ACTION_SERVER_TO_ENDPOINT_NOTICE  = " + pl.toJSONString());
                                eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                String ws = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_STATE);
                                String bl = (String) pl.get(CloudBridgeUtil.BATTERY_LEVEL);
                                String cs = (String) pl.get(CloudBridgeUtil.KEY_NAME_CHARGE_STATUS);
                                String sl = (String) pl.get(CloudBridgeUtil.SIGNAL_LEVEL);
                                String ns = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE);
                                String stl = (String) pl.get(CloudBridgeUtil.STEPS_LEVEL);
                                String step_noti = (String) pl.get(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
                                String super_power_saving = (String) pl.get(CloudBridgeUtil.KEY_SUPER_POWER_SAVING);

                                if (ws != null) {
                                    String[] array = ws.split("_");
                                    int watchStatus = Integer.valueOf(array[1]);
                                    mApp.setmWatchState(eid, watchStatus);
                                    Intent intent = new Intent(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    intent.putExtra(Const.KEY_WATCH_STATE, watchStatus);
                                    sendBroadcast(intent);
                                }

                                if (cs != null && cs.length() == 1) {
                                    int chargestatus = Integer.valueOf(cs);
                                    mApp.setmChargeState(eid, chargestatus);
                                    // 收到手表发来的充电中状态时，存储下时间（30min内没有再次收到充电中消息，认为已经不再充电）
                                    if (chargestatus == Const.WATCH_CHARGE_IS_ON)
                                        mApp.settimeOfChargeRecentBattery(eid, System.currentTimeMillis());
                                }

                                if (bl != null) {
                                    handleMapMGetBattery(eid, bl);   //虽然这里不是mapget获取的电量，但为统一处理逻辑，也调用了该方法
                                    Intent intent = new Intent(Const.ACTION_REFRESH_WATCH_TITLE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    intent.putExtra(CloudBridgeUtil.BATTERY_LEVEL, bl);
                                    sendBroadcast(intent);
                                }
                                //存储手表信号并发送广播
                                if (sl != null) {
                                    mApp.setValue(eid + CloudBridgeUtil.SIGNAL_LEVEL, sl);
                                    Intent intent = new Intent(Const.ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    sendBroadcast(intent);
                                }
                                // 手表网络状态变化
                                if (ns != null) {
                                    handleMapMGETWatchNetState(eid, ns);
                                    Intent intent = new Intent(Const.ACTION_WATCH_NET_STATE_CHANGE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    sendBroadcast(intent);
                                }

                                String sTmp = (String) pl.get(CloudBridgeUtil.HEALTH_INFO);
                                if (sTmp != null && sTmp.length() > 0) {
                                    Intent intent = new Intent(Const.ACTION_WATCH_HEALTH_DATA_NOTICE);
                                    intent.putExtra(Const.KEY_JSON_MSG, sTmp);
                                    sendBroadcast(intent);
                                }

                                //存储当前步数，并发送广播。
                                if (stl != null) {
                                    LogUtil.i("发送广播！" + stl);
                                    mApp.setValue(eid + CloudBridgeUtil.STEPS_LEVEL, stl);
                                    Intent intent = new Intent(Const.ACTION_CLOUD_BRIDGE_STEPS_CHANGE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    intent.putExtra(CloudBridgeUtil.STEPS_LEVEL, stl);
                                    sendBroadcast(intent);
                                }

                                //收到亲情号码设置更新本地数据
                                String phoneWihteList = (String) pl.get(CloudBridgeUtil.PHONE_WHITE_LIST);
                                if (phoneWihteList != null && phoneWihteList.length() > 0) {
                                    mApp.setValue(eid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, phoneWihteList);//
                                    Intent intent = new Intent(Const.ACTION_CONTACT_CHANGE);
                                    sendBroadcast(intent);
                                }
                                //收到自动升级修改
                                String autoUpgrade = (String) pl.get(CloudBridgeUtil.WATCH_AUTO_UPGRADE);
                                if (autoUpgrade != null) {
                                    WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);
                                    if (watch != null) {
                                        watch.setAutoUpdate(Integer.valueOf(autoUpgrade));
                                        mApp.setValue(watch.getEid() + CloudBridgeUtil.WATCH_AUTO_UPGRADE, watch.getAutoUpdate());
                                    }
                                }

                                if (pl.containsKey(CloudBridgeUtil.SILENCE_LIST)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.SILENCE_LIST);
                                    updateSilenceTimeData(eid, jstr);
                                }

                                if (pl.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
                                    updateSleepListData(eid, jstr);
                                }

                                if (pl.containsKey(CloudBridgeUtil.OFFLINE_MODE_VALUE)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.OFFLINE_MODE_VALUE);
                                    updateOfflineMode(eid, jstr);
                                }

                                if (pl.containsKey(CloudBridgeUtil.KEY_DEVICE_WLAN_STATE)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.KEY_DEVICE_WLAN_STATE);
                                    updateWlanStateData(eid, jstr);
                                }

                                //开机时间
                                if (pl.containsKey(CloudBridgeUtil.DEVICE_POWER_ON_TIME)) {
                                    String powerOnTime = (String) pl.get(CloudBridgeUtil.DEVICE_POWER_ON_TIME);
                                    mApp.setValue(eid + CloudBridgeUtil.DEVICE_POWER_ON_TIME, powerOnTime);
                                    // 发广播通知首页刷新title
                                    Intent intent = new Intent(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
                                    intent.putExtra(Const.KEY_WATCH_ID, eid);
                                    intent.putExtra(Const.KEY_WATCH_STATE, Const.WATCH_STATE_POWER_OFF);
                                    sendBroadcast(intent);
                                }

                                if (step_noti != null) {
                                    if (step_noti.equals("0")) {
                                        mApp.setValue(eid +
                                                CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "0");
                                    } else {
                                        mApp.setValue(eid +
                                                CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "1");
                                    }
                                }

                                //超级省电
                                if (super_power_saving != null) {
                                    mApp.setValue(eid + Constants.SHARE_PREF_SUPER_POWER_SAVING, Integer.parseInt(super_power_saving));
                                }

                                Intent intent = new Intent(Const.ACTION_REFRESH_WATCH_TITLE);
                                intent.putExtra(Const.KEY_WATCH_ID, eid);
                                sendBroadcast(intent);
                            }
                            break;

                            case CloudBridgeUtil.SUB_ACTION_DIABG_OPT:
                                int optype = (Integer) pl.get("optype");
                                if (optype == 1) {//修改
                                    //do nothing
                                } else if (optype == 2) {//删除
                                    String id = (String) pl.get(CloudBridgeUtil.KEY_NAME_CONTACT_ID);
                                    String eid_op = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                    String path = mApp.getExternalFilesDir(Const.MY_BASE_DIR + "/DIAL_LOCAL_BG").getAbsolutePath()
                                            + "/" + AESUtil.getInstance().encryptDataStr(eid_op) + "/" + id + ".jpg";
                                    File fp = new File(path);
                                    if (fp != null && fp.exists()) {
                                        boolean ret = fp.delete();
                                        if (ret) {
                                            DialBgDAO.getInstance(getApplicationContext()).deleteDialBgItemById(eid_op, id);
                                            Intent dialIntent = new Intent(Const.ACTION_DIALBG_DELETE);
                                            sendBroadcast(dialIntent);
                                        } else {
                                            LogUtil.e("delete DialBg file failed.");
                                        }
                                    }
                                } else {
                                    //do nothing
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_START:
                                eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                if (eid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviStart(pl.toString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_UPDATE_ROUTE_PLAN:
                                eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                if (eid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviStart(pl.toString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_CURRENT_POINT:
                                eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                                if (eid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                                    handleWatchNaviCurrentPoint(pl.toString());
                                }
                                break;
                            case CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_NOTICE:
                                handleWatchNaviNotice(pl);
                                break;

                            case CloudBridgeUtil.SUB_ACTION_VALUE_NAME_INSTALLAPP_LIST_CHANGE:
                                sendBroadcast(new Intent(Const.ACTION_INSTALL_APPLIST_CHANGE));
                                break;
                            default:
                                break;
                        }
                    }
                }

                break;

            case CloudBridgeUtil.CID_E2C_DOWN:
                if (CloudBridgeUtil.getCloudMsgSN(respMsg) == sendHeadImageE2cSn) {
                    if (rc == CloudBridgeUtil.RC_SUCCESS) {
                        if (sendHeadImageE2cType == 1) {
                            // TODO: 2016/12/1 watchdata实例
                            sendDeviceHeadPath(mApp.getCurUser().getFocusWatch());
                        } else if (sendHeadImageE2cType == 2) {
                            sendUserHeadPath(mApp.getCurUser());
                        }
                    } else {
                        if (rc == CloudBridgeUtil.RC_NETERROR
                                || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                            ToastUtil.showMyToast(this,
                                    getString(R.string.network_error_prompt),
                                    Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                        }
                        sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO).putExtra(Const.SETTING_RESULt, Const.SETTING_FAIL));//set fail
                    }
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SEND_IMAGE_DATA));
                }
                break;

            case CloudBridgeUtil.CID_DEVICE_GET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject devicePl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    String eid = (String) devicePl.get(KEY_NAME_EID);
                    WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);
                    mApp.parseDevicePl(watch, devicePl);
                    FamilyData family = mApp.getCurUser().queryFamilyByGid(watch.getFamilyId());
                    if (family != null) {
                        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    }
                    WatchDAO.getInstance(getApplicationContext()).addWatch(watch);
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_GET_DEVICE_INFO));    //send broadcast to reflush device detail
                    //刷新通讯录
                    Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                    it.putExtra("eid", eid);
                    sendBroadcast(it);
                }
                break;

            case CloudBridgeUtil.CID_DEVICE_SIM_CHANGE_RESP:

                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    ToastUtil.showMyToast(this, getString(R.string.modify_success), Toast.LENGTH_SHORT);
                    JSONObject devicePl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                    String eid = (String) devicePl.get(KEY_NAME_EID);
                    WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);
                    FamilyData family = mApp.getCurUser().queryFamilyByGid(watch.getFamilyId());
                    if (family != null) {
                        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    }
                    sendUserOrDeviceSetChangeE2G(eid, watch.getFamilyId(), CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE);
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO).putExtra(Const.SETTING_RESULt, Const.SETTING_SUCCESS));//set success
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO).putExtra(Const.SETTING_RESULt, Const.SETTING_FAIL));//set fail
                }
                break;

            case CloudBridgeUtil.CID_DEVICE_SET_RESP:
                mApp.sdcardLog("startPhotoZoom CID_DEVICE_SET_RESP rc:" + rc);
                JSONObject reqPl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                String deviceseEid = (String) reqPl.get(KEY_NAME_EID);
                WatchData watch = mApp.getCurUser().queryWatchDataByEid(deviceseEid);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    ToastUtil.showMyToast(this, getString(R.string.modify_success), Toast.LENGTH_SHORT);
                    FamilyData family = mApp.getCurUser().queryFamilyByGid(watch.getFamilyId());
                    if (family != null) {
                        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    }
                    WatchDAO.getInstance(getApplicationContext()).addWatch(watch);
                    //send editnotice
                    sendUserOrDeviceSetChangeE2G(deviceseEid, watch.getFamilyId(), CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE);
                    Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                    it.putExtra("eid", deviceseEid);
                    sendBroadcast(it);
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO).putExtra(Const.SETTING_RESULt, Const.SETTING_SUCCESS));//set success
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                    WatchDAO.getInstance(getApplicationContext()).readWatch(watch);
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO).putExtra(Const.SETTING_RESULt, Const.SETTING_FAIL));//set fail
                }
                break;

            case CloudBridgeUtil.CID_USER_SET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    ToastUtil.showMyToast(this, getString(R.string.modify_success), Toast.LENGTH_SHORT);
                    //refresh list
                    for (FamilyData family : mApp.getCurUser().getFamilyList()) {
                        for (MemberUserData member : family.getMemberList()) {
                            if (member.getEid().equals(mApp.getCurUser().getEid())) {
                                member.setNickname(mApp.getCurUser().getNickname());
                                WatchData mWatch = family.getWatchlist().get(0);
                                UserRelationDAO.getInstance(getApplicationContext()).addUserRelation(member, mWatch.getFamilyId(), mWatch.getWatchId(), mWatch.getNickname(), member.getCustomData().toJsonStr());
                            }
                        }
                    }
                    if (mApp.getCurUser() != null && mApp.getCurUser().getFocusWatch() != null) {
                        sendUserOrDeviceSetChangeE2G(mApp.getCurUser().getEid(), mApp.getCurUser().getFocusWatch().getFamilyId(), CloudBridgeUtil.SUB_ACTION_VALUE_NAME_USER_CHANGE_NOTICE);
                    }
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                    for (FamilyData family : mApp.getCurUser().getFamilyList()) {
                        for (MemberUserData member : family.getMemberList()) {
                            if (member.getEid().equals(mApp.getCurUser().getEid())) {
                                mApp.getCurUser().setNickname(member.getNickname());
                                break;
                            }
                        }
                    }
                }
                break;

            case CloudBridgeUtil.CID_QUERY_MYGROUPS_RESP:

                if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_SESSION_ILLEGAL) {
                    //如果需要登录则打开登录窗口

                } else if (rc == CloudBridgeUtil.RC_SUCCESS) {

                    //刷新groups
                    int recode = mApp.parseJSONObjectGroups(CloudBridgeUtil.getCloudMsgPLArray(respMsg));
                    mApp.setValue(Const.SHARE_PREF_CURRENT_USER_REFLECT_ID, mApp.getCurUser().getUid());
                    //检查watchlist，如果watchlist列表为空直接logout
                    if (mApp.getCurUser().isValidFamilys() == 0 || mApp.getCurUser().getWatchList() == null || mApp.getCurUser().getWatchList().size() == 0) {
                        if (mApp.getIsMainActivityOpen()) {//mainactivity打开后才弹出此提示
                            //kick down
                           /*
                            cui 检测是否在家庭组中
                           DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                                    getString(R.string.prompt),
                                    getString(R.string.no_group_quit_tips),
                                    null,
                                    null,
                                    new OnCustomDialogListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            mApp.doLogout("CID_QUERY_MYGROUPS_RESP no group be out");
                                        }
                                    },
                                    getText(R.string.confirm).toString());*/
                        } else {
                            mApp.setIsNeedInvalidFamilyDialog(true);
                        }
                    } else {
                        final WatchData findWatch = mApp.getReActiveWatch();
                        mApp.setIsNeedInvalidFamilyDialog(false);
                        if (findWatch != null) {
                            if (!mApp.isControledByVersion(findWatch, false, "T22")) {
                                DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                                        getString(R.string.prompt),
                                        getString(R.string.str_sim_reactive_restart_alert/*, findWatch.getNickname()*/),
                                        null,
                                        null,
                                        new OnCustomDialogListener() {

                                            @Override
                                            public void onClick(View v) {

                                            }
                                        },
                                        getText(R.string.confirm).toString());
                            } else {
                                DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                                        getString(R.string.prompt),
                                        getString(R.string.str_sim_reactive_sms, findWatch.getNickname()),
                                        new OnCustomDialogListener() {

                                            @Override
                                            public void onClick(View v) {

                                            }
                                        },
                                        getText(R.string.cancel).toString(),
                                        new OnCustomDialogListener() {

                                            @Override
                                            public void onClick(View v) {
                                                mApp.sendReActiveSMS(findWatch.getCellNum(), findWatch.getIccid());
                                            }
                                        },
                                        getText(R.string.sendSms).toString());
                            }
                        }

                        if (recode == -1) {//刷新家庭时候，focuswatch被移出，则需要重置界面
                            mApp.getLocalBroadcastManager().sendBroadcast(new Intent(Const.ACTION_UNBIND_RESET_FOCUS_WATCH));
                        } else {
                            //刷新login的数据库,当登陆账户切换时，需要必要的初始化
                            // mApp.saveLoginOKResult(Const.LOGIN_STATE_LOGIN);
                            // mApp.getNetService().setNetServiceLoginOK(true);
                            sendBroadcast(new Intent(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW));
                            //发送刷新界面的广播给上面.
                            //重新登录时获取一下离线消息
                        }
                        sendBroadcast(new Intent(Const.ACTION_GET_OFFLINE_CHAT_MSG));
                        getNoticeSetting(this);
                    }
//                    mApp.initMapType();
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT
                        || rc == CloudBridgeUtil.RC_NETERROR) {
//                    //获取群组信息
//                    ToastUtil.showMyToast(getApplicationContext(), getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);

                } else {
                    LogUtil.e("resp rc error:" + rc);
                    LogUtil.e("resp error rn" + CloudBridgeUtil.getCloudMsgRN(respMsg));
                }
                break;
            case CloudBridgeUtil.CID_USER_LOGIN_RESP:
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    String sid = (String) respMsg.get(KEY_NAME_SID);
                    mApp.setToken(sid);
                    //save token
                    JSONObject jo = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    String eid = (String) jo.get(CloudBridgeUtil.KEY_NAME_EID);

                    mApp.getCurUser().setEid(eid);
                    mApp.setValue(Const.SHARE_PREF_FIELD_LOGIN_EID, eid);
                    Intent it = new Intent(Const.ACTION_QUERY_ALL_GROUPS);
                    it.putExtra("get_offline_chat", 1);
                    sendBroadcast(it);

                    mApp.saveLoginOKResult(Const.LOGIN_STATE_LOGIN); //在loginresp置登陆状态。不在getallgroups时候置状态
                    setNetServiceLoginOK(true);
                    retryLoginCount = 0;
                    if (mApp.getMiPushRegister()) {
                        mApp.setMiPushAlias();
                    }
                    sendBroadcast(new Intent(Const.ACTION_GET_OFFLINE_CHAT_MSG));
                    //设置语言信息
                    mApp.getNetService().sendSetLangInfo();

                } else if (rc == CloudBridgeUtil.RC_TIMEOUT
                        || rc == CloudBridgeUtil.RC_NETERROR
                        || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    closeWebSocket();
                    //       ToastUtil.showMyToast(getApplicationContext(), getText(R.string.net_check_alert).toString(), Toast.LENGTH_SHORT);
                } else {
                    mApp.doLogout("login resp fail");
                }
                break;
            case CloudBridgeUtil.CID_GETTIME_RESP:
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (1 == rc) {
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String serviceTime = TimeUtil.parseTimeStampGMT2CHN((String) pl.get(CloudBridgeUtil.KEY_NAME_GMT));

                    for (FamilyData family : mApp.getCurUser().getFamilyList()) {
                        if (mApp.getStringValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + family.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                            mApp.setNextContentKey(family, serviceTime);
                        }
                        if (mApp.getStringValue(Const.SHARE_PREF_FIELD_NEXT_FAMILY_CHANGE_KEY + family.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                            mApp.setNextFamilyChangeNotifyKey(family, serviceTime);
                        }
                        if (mApp.getStringValue(Const.SHARE_PREF_FIELD_NEXT_WARNING_KEY + family.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                            mApp.setNextWarningKey(family, serviceTime);
                        }
                    }
                    if (mApp.getStringValue(mApp.getCurUser().getEid() + Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY, Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                        mApp.setNextPrivateChatKey(mApp.getCurUser().getEid(), serviceTime);
                    }
                } else if (-12 == rc) {
                    LogUtil.e("error rc = " + rc);
                } else if (-200 == rc) {
                    LogUtil.e("error rc = " + rc);
                } else {

                }
                break;

            case CloudBridgeUtil.CID_KICK_DOWN:
                //kick down
                String cp = (String) respMsg.get(CloudBridgeUtil.KEY_NAME_CP);
                if (cp != null && cp.startsWith(SystemUtils.getDeviceID(getApplicationContext()))) {
                    setNetServiceLoginOK(false);
                    closeWebSocket();
                    mApp.setToken(null);
                    mApp.setValue(Const.SHARE_PREF_FIELD_LOGIN_TOKEN, null);
                    return;
                }
                String content = getString(R.string.other_location_login_tips);
                if (cp != null && cp.length() > 0) {
                    String[] deviceInfo = cp.split("_");
                    String deviceName = deviceInfo[3];
                    if (deviceInfo[0].equals("iOS")) {
                        deviceName = deviceInfo[deviceInfo.length - 1];
                    }
                    String account = getString(R.string.account_type_xiaomi);
                    content = getString(R.string.kickdown_tips, getString(R.string.app_name), TimeUtil.getTime(NetService.this, TimeUtil.getTimeStampLocal()),
                            deviceName, account, account, account);
                }
                setKickdownFlag(true);
                mApp.doLogoutNoQuitActivity(content);
                break;

            case CloudBridgeUtil.CID_CHECK_SESSION_DOWN:
                if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_SESSION_ILLEGAL) {
                    //session timeout,重置登陆状态
                    setNetServiceLoginOK(false);
                    String info = getString(R.string.other_location_login_tips_1, getString(R.string.app_name));
                    mApp.setToken(null);
                    mApp.setValue(Const.SHARE_PREF_FIELD_LOGIN_TOKEN, null);

                    if (mApp.getRunningActivityName().equals(NewLoginActivity.class.getName())) {
                        mApp.doLogoutNoQuit(info);
                        DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                                getString(R.string.prompt), info,
                                null,
                                null,
                                new DialogUtil.OnCustomDialogListener() {

                                    @Override
                                    public void onClick(View v) {

                                    }
                                },
                                getText(R.string.confirm).toString());
                    } else {
                        setKickdownFlag(true);
                        mApp.doLogoutNoQuitActivity(info);
                    }
                } else if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    mApp.saveLoginOKResult(Const.LOGIN_STATE_LOGIN);
                    if (mApp.getCurUser().getEid() == null || mApp.getCurUser().getEid().length() == 0) {
                        String eid = mApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                        if (eid != null) {
                            mApp.getCurUser().setEid(eid);
                        }
                    }
                    setNetServiceLoginOK(true);
                    if (mApp.getMiPushRegister()) {
                        mApp.setMiPushAlias();
                    }
                    resendAllMessage();
                    Intent it = new Intent(Const.ACTION_QUERY_ALL_GROUPS);
                    sendBroadcast(it);
                }

                break;

            case CloudBridgeUtil.CID_GET_CONTACT_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单
                    JSONArray obj = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_CONTACT_SYNC_ARRAY);
                    //持久化
                    if (reqMsg == null) {
                        break;
                    }
                    pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl == null) {
                        break;
                    }
                    String aEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                    mApp.setValue(aEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, obj.toString());

                    Intent intent = new Intent(Const.ACTION_GET_CONTACT_SUCCESS);
                    intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, aEid);
                    sendBroadcast(intent);
                } else if (rc == -13) {
                    if (reqMsg == null) {
                        break;
                    }
                    pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl == null) {
                        break;
                    }
                    String aEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                    Intent intent = new Intent(Const.ACTION_GET_CONTACT_SUCCESS);
                    intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, aEid);
                    sendBroadcast(intent);
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_RESP:
                int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单
                    if (reqMsg == null) {
                        break;
                    }
                    JSONObject requestPL = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (requestPL == null) {
                        break;
                    }
                    String aEid = (String) requestPL.get(CloudBridgeUtil.KEY_NAME_EID);
                    String sTmp = (String) maggetPl.get(CloudBridgeUtil.PHONE_WHITE_LIST);
                    if (sTmp != null && sTmp.length() > 0) {
                        ArrayList<PhoneNumber> bindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                        mApp.setValue(aEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(bindWhiteList));
                    }
                }
                break;
            case CloudBridgeUtil.CID_GET_NOTICE_SETTING_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    JSONArray devices = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_DEVICES);
                    for (int i = 0; i < devices.size(); i++) {
                        JSONObject setting = (JSONObject) devices.get(i);
                        String watchEid = (String) setting.get(CloudBridgeUtil.KEY_NAME_EID);
                        mApp.setNoticeSetting(watchEid, setting);
                    }
                }
                break;
            case CloudBridgeUtil.CID_GET_WATCH_NAVI_STATE_RESP:
                if (reqMsg != null) {
                    JSONObject requestPL = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                    String requestEid = (String) requestPL.get(CloudBridgeUtil.KEY_NAME_EID);
                    if (mApp.getCurUser().getFocusWatch() != null && requestEid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
                        String naviState = "";
                        if (rc == CloudBridgeUtil.RC_SUCCESS && respMsg.containsKey(CloudBridgeUtil.KEY_NAME_PL)) {
                            pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                            naviState = pl.toString();
                        }
                        handleWatchNaviStart(naviState);
                    }
                }
                break;
            default:
                break;
        }


    }

    private int dnsReparseCount = 0;

    private void dnsReparseCountAdd() {
        dnsReparseCount++;
        if (dnsReparseCount > 2) {
            dnsReparseCount = 0;
            saveWebSocketIpAddr(null);//重置ipaddr为null，下次连接时候需要parse
        }
    }

    public void parseDomain(final String url, final DnsParseCallback cb) {
        mApp.sdcardLog("parseDomain start at:" + System.currentTimeMillis());
        new MioAsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                String ip = null;
                InetAddress addr;
                try {
                    LogUtil.e("parseDomain url = " + url);
                    addr = InetAddress.getByName(url);
                    InetAddress[] inetAddress = InetAddress.getAllByName(url);
                    for (InetAddress inet : inetAddress) {
                        mApp.sdcardLog("parseDomain ip: " + inet.getHostAddress());
                    }

                    ip = addr.getHostAddress();
                    LogUtil.e("parseDomain ip = " + ip);
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    mApp.sdcardLog("parseDomain error:" + e.getMessage());
                    e.printStackTrace();
                }
                if (ip != null) {
                    saveHttpIpAddr(ip);
                    saveWebSocketIpAddr(ip);
                    cb.doCallBack();
                }
                mApp.sdcardLog("parseDomain end ip:" + ip);
                return ip;
            }

            protected void onCancelled() {
                super.onCancelled();
            }

            protected void onPostExecute(String message) {
                super.onPostExecute(message);

            }
        }.execute();

    }

    private int retryLoginCount = 0;

    private void sendAutoLogin() {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        //set msg body
        LogUtil.e("mipush app region:" + MiPushClient.getAppRegion(this));
        String region = XimalayaUtil.getMipushRegion(this);
        LogUtil.e("mipush app region11:" + region);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_NAME, mApp.getLoginId());
        pl.put(CloudBridgeUtil.KEY_NAME_PASSWORD, mApp.getLastppssww());
        pl.put(CloudBridgeUtil.KEY_NAME_UNIONID, mApp.getLastUnionId());
        retryLoginCount++;

        pl.put(CloudBridgeUtil.KEY_NAME_TYPE, CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        pl.put(CloudBridgeUtil.KEY_NAME_ADS, SystemUtils.getDeviceInfo(getApplicationContext()));
        pl.put(CloudBridgeUtil.KEY_NAME_REGION, region);
        queryGroupsMsg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_LOGIN, pl));

        sendLoginMsg(queryGroupsMsg);
    }

    public static boolean isKitKatOrLater() {

        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2;

    }

    private void startQueueLoopTimer() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Const.ACTION_LOOP_ALARM);
        PendingIntent sendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (isKitKatOrLater() && hasAlarmExactPermission()) {
            am.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + MSG_QUEUE_INTERVAL, sendIntent);
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + MSG_QUEUE_INTERVAL, sendIntent);
        }
    }

    private boolean hasAlarmExactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        } else {
            return true;
        }
    }

    private void startHeartbeatTimer() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Const.ACTION_HEART_BEAT_ALARM);
        PendingIntent sendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (isKitKatOrLater()) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + mHeartBeatCheckTime, sendIntent);
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + mHeartBeatCheckTime, sendIntent);
        }
    }

    private void cancelHeartbeatTimer() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Const.ACTION_HEART_BEAT_ALARM);
        PendingIntent sendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        am.cancel(sendIntent);
    }

//    服务器新增接口，支持APP与设备都拉取语音，APP只需要拉取私聊语音信息，设备不区分私聊拉取全部语音。接口参数传：EID，KeyBegin，KeyEnd，Size，服务器针对设备还是APP的请求，分小包下发，针对设备的小包，包数据不超过30K，针对APP的小包，包数据不超过64K。Size服务器最大限制200条
//
// 上行:{"CID":40191, "SN":123456,"PL":{"EID":"XXXX","Size":10, " KeyBegin ":" GP/XXX/XXX/78999898989898998",
// "KeyEnd":"GP/XXX/XXX/79838772846744776"}}
// 下行:{"CID":40192, "SN":123456,"PL":{"List":[{"EID":"XXX","Type":"voice","Content":"XXX","Duration":5,
// "Key":"79838772869362558"}]}    //多小包同一SN

    private void getPrivateChat(String eid) {
        StringBuilder endkey = new StringBuilder();
        StringBuilder startkey = new StringBuilder();
        //查询语音
        String nextKey = mApp.getStringValue(eid + Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY, Const.DEFAULT_NEXT_KEY);
        if (nextKey != null && !nextKey.equals(Const.DEFAULT_NEXT_KEY)) {
            endkey.append(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
            endkey.append(eid + CloudBridgeUtil.E2C_SPLIT_MEG);
            endkey.append(TimeUtil.getReversedOrderTime(nextKey));
            startkey.append(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
            startkey.append(eid + CloudBridgeUtil.E2C_SPLIT_MEG);
            startkey.append(TimeUtil.getReversedOrderTime(Const.FUTURE_TIME_KEY));
            MyMsgData queryMsg = new MyMsgData();
            queryMsg.setCallback(this);
            JSONObject pl = new JSONObject();
            pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
            pl.put(CloudBridgeUtil.KEY_NAME_C2E_BEGIN, startkey.toString());
            pl.put(CloudBridgeUtil.KEY_NAME_C2E_END, endkey.toString());
            pl.put(CloudBridgeUtil.KEY_NAME_SIZE, 200);
            queryMsg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_PRIVATE_CHAT_MULTI_PKT, pl));
            sendNetMsg(queryMsg);
        }
    }

    private void initBgMsgReceiver() {
        mBgMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stuba
                String resp;
                JSONObject respPl;
                if (intent.getAction().equals(Const.ACTION_RECEIVE_CHAT_MSG)) {//需要移走
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        LogUtil.e("sub_action 102 resp != null");
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        JSONObject content = (JSONObject) respPl.get(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY);
                        String key = (String) respPl.get("Key");
                        LogUtil.e("sub_action 102 content = "+content +"Key = "+key);
                        if (content == null) {
                            c2eGetMessage(key);
                        } else {
                            getVoiceMsg(key, content);
                        }
                        try {
                            recvStatChat(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else if (intent.getAction().equals(Const.ACTION_WARNNING_TYPE_SOS)) {//sos
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            setSosInfo(respPl);
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_RECEIVE_GROUP_CHANGE_MSG)) {//家庭成员变化  //需要移走
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            setFamilyChangeInfo(respPl);
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_WARNNING_TYPE_SAFEAREA)) {  //需要移走
                    //安全区域提醒
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        mApp.sdcardLog("safearea=" + resp + ";\ntoken=" + mApp.getToken());
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            int type = (Integer) respPl.get(CloudBridgeUtil.KEY_NAME_TYPE);
                            String eid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_EID);
                            String efid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_EFID);

                            if (mApp.getCurUser().queryWatchDataByEid(eid) != null) {//出现了一次空指针报错，增加保护
                                //增加未认证手表的过滤
                                WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);

                                String watchName = mApp.getCurUser().queryWatchDataByEid(eid).getNickname();


                                JSONObject efence = (JSONObject) respPl.get(CloudBridgeUtil.KEY_NAME_EFENCE);
                                String efenceName = (String) efence.get(CloudBridgeUtil.KEY_NAME_NAME);
                                if (TextUtils.isEmpty(efenceName)) {//出现了一次空指针报错，增加保护mApp.getmWatchEFence().get(eid)
                                    efenceName = getString(R.string.security_zone);
                                }
                                String noticeContent = "";
                                if (type == 1) {
                                    noticeContent = getString(R.string.watch_already_arrive_safe_area, watchName, efenceName);
                                } else if (type == 2) {
                                    noticeContent = getString(R.string.watch_already_leave_safe_area, watchName, efenceName);
                                }

                                //保存安全区域消息
                                String gid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_GID);
                                String time = TimeUtil.getOrderTime((String) respPl.get(CloudBridgeUtil.KEY_NAME_KEY));
                                boolean flag = NoticeMsgHisDAO.getInstance(getApplicationContext()).isMsgExist(gid, mApp.getCurUser().getEid(), time);

                                if (flag == false) {
                                    NoticeMsgData msg = new NoticeMsgData();
                                    msg.setmTimeStamp(time);
                                    msg.setmType(NoticeMsgData.MSG_TYPE_SAFE_AREA);
                                    msg.setmSrcid((String) respPl.get(CloudBridgeUtil.E2C_PL_KEY_EID));
                                    msg.setmGroupid(gid);
                                    JSONObject content = new JSONObject();
                                    content.put("EFence", respPl.get("EFence"));
                                    content.put("Location", respPl.get("Location"));
                                    msg.setmContent(content.toString());
                                    NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(msg.getmGroupid(), mApp.getCurUser().getEid(), msg);
                                    mApp.setNextContentKey(mApp.getCurUser().queryFamilyByGid(msg.getmGroupid()), TimeUtil.timeInc(msg.getmTimeStamp()));
                                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_NOTICE_MSG));
                                    intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_LOCATION);
                                    mApp.setHasNewNoticeMsg(msg.getmGroupid(), AllMessageAdapter.MESSAGE_TYPE_LOCATION, true);
                                    JSONObject noticeSetting = mApp.getNoticeSetting(eid);
                                    if (noticeSetting != null) {
                                        String isNeedNotice = (String) noticeSetting.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE);
                                        if ("0".equals(isNeedNotice)) {
                                            return;
                                        }
                                    }
                                    mApp.recvMsgNotify(eid, getString(R.string.location_message, watch.getNickname()),
                                            noticeContent, Const.TITLE_BAR_NEW_NOTICE_MESSAGE + AllMessageAdapter.MESSAGE_TYPE_LOCATION, gid, NoticeTypeActivity.class);
                                }
                            } else {
                                mApp.sdcardLog("safearea not find eid:" + eid);
                            }
                        }
                    }
                } else if (intent.getAction().equals(Const.ACTION_WARNNING_TYPE_SAFEDANGERDRAW)) {
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (resp != null) {
                        respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
                        if (respPl != null) {
                            String eid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_EID);
                            String type = (String) respPl.get(CloudBridgeUtil.KEY_NAME_TYPE);//通知类型 danger 危险区域，safe 安全区域，city 城市
                            String event = (String) respPl.get(CloudBridgeUtil.KEY_AREA_EVENT);//事件，in 进入，out 离开
                            String gid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_GID);
                            String time = TimeUtil.getOrderTime((String) respPl.get(CloudBridgeUtil.KEY_NAME_KEY));
                            JSONObject efence = (JSONObject) respPl.get(CloudBridgeUtil.KEY_NAME_EFENCE);
                            JSONObject location = (JSONObject) respPl.get(CloudBridgeUtil.KEY_NAME_LOCATION);
                            if (mApp.getCurUser().queryWatchDataByEid(eid) != null) {
                                WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);
                                if (!mApp.isSimCertiStatusEnable(watch.getSimCertiStatus())) {
                                    //过滤掉未认证的手表的信息
                                    return;
                                }
                                String watchName = mApp.getCurUser().queryWatchDataByEid(eid).getNickname();

                                String noticeContent = "";
                                if ("in".equals(event)) {
                                    if ("danger".equals(type)) {
                                        noticeContent = getString(R.string.watch_in_danger_area, watchName);
                                    } else if ("safe".equals(type)) {
                                        noticeContent = getString(R.string.watch_in_safe_area, watchName);
                                    } else if ("city".equals(type)) {
                                        noticeContent = getString(R.string.watch_in_city, watchName);
                                    }
                                } else if ("out".equals(event)) {
                                    if ("danger".equals(type)) {
                                        noticeContent = getString(R.string.watch_out_danger_area, watchName);
                                    } else if ("safe".equals(type)) {
                                        noticeContent = getString(R.string.watch_out_safe_area, watchName);
                                    } else if ("city".equals(type)) {
                                        noticeContent = getString(R.string.watch_out_city, watchName);
                                    }
                                }
                                boolean flag = NoticeMsgHisDAO.getInstance(getApplicationContext()).isMsgExist(gid, mApp.getCurUser().getEid(), time);
                                if (!flag) {
                                    NoticeMsgData msg = new NoticeMsgData();
                                    msg.setmTimeStamp(time);
                                    msg.setmType(NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW);
                                    msg.setmSrcid((String) respPl.get(CloudBridgeUtil.E2C_PL_KEY_EID));
                                    msg.setmGroupid(gid);
                                    JSONObject content = new JSONObject();
                                    content.put("EFence", respPl.get("EFence"));
                                    content.put("Location", respPl.get("Location"));
                                    content.put("event", respPl.get("event"));
                                    msg.setmContent(content.toString());
                                    NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(msg.getmGroupid(), mApp.getCurUser().getEid(), msg);
                                    mApp.setNextContentKey(mApp.getCurUser().queryFamilyByGid(msg.getmGroupid()), TimeUtil.timeInc(msg.getmTimeStamp()));

                                    intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_LOCATION);
                                    mApp.setHasNewNoticeMsg(msg.getmGroupid(), AllMessageAdapter.MESSAGE_TYPE_LOCATION, true);
                                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_NOTICE_MSG));
                                    JSONObject noticeSetting = mApp.getNoticeSetting(eid);
                                    if (noticeSetting != null) {
                                        String isNeedNotice = (String) noticeSetting.get(CloudBridgeUtil.KEY_GUARD_EFFENCE);
                                        if ("0".equals(isNeedNotice)) {
                                            return;
                                        }
                                    }
                                    mApp.recvMsgNotify(eid, getString(R.string.location_message, watch.getNickname()),
                                            noticeContent, Const.TITLE_BAR_NEW_NOTICE_MESSAGE + AllMessageAdapter.MESSAGE_TYPE_ALL, gid, NoticeTypeActivity.class);
                                }
                            } else {
                                mApp.sdcardLog("safe or danger area not find eid:" + eid);
                            }

                        }
                    }}else if (intent.getAction().equals(Const.ACTION_RECEIVE_REQ_JOIN_GROUP)) {//需要移走
                    //弹出pop
                    LogUtil.e(mApp.getRunningActivityName());
                    LogUtil.e(NewLoginActivity.class.getName());
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (mApp.getRunningActivityName().equals(NewLoginActivity.class.getName())) {
                        mApp.setBindRequest(resp);
                        return;
                    }
                    handleBindRequest(resp);
                } else if (intent.getAction().equals(Const.ACTION_GET_OFFLINE_CHAT_MSG)) {
                    if (mApp.getCurUser().getWatchList() != null) {
                        for (int i = 0; i < mApp.getCurUser().getWatchList().size(); i++) {
                            WatchData watch = mApp.getCurUser().getWatchList().get(i);
                            if (!watch.isDevice102()) {
                                if (watch.getEid() != null) {
                                    sendGetContactReq(watch.getEid());
                                }
                            } else {
                                if (watch.getEid() != null) {
                                    getPhoneWhiteListByMapget(watch.getEid());
                                }
                            }
                        }
                    }
                    ArrayList<FamilyData> famliylist = mApp.getCurUser().getFamilyList();
                    for (FamilyData family : famliylist) {
                        StringBuilder endkey = new StringBuilder();
                        StringBuilder startkey = new StringBuilder();
                        //查询语音
                        if (!mApp.getStringValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + family.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                            endkey.append(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
                            endkey.append(family.getFamilyId() + CloudBridgeUtil.E2C_SPLIT_MEG);
                            if (family.getNextContentKey() == null) {
                                String TempCotentKey = mApp.getStringValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + family.getFamilyId(), Const.DEFAULT_NEXT_KEY);
                                if (!TempCotentKey.equals(Const.DEFAULT_NEXT_KEY)) {
                                    family.setNextContentKey(TempCotentKey);
                                } else {
                                    return;
                                }
                            }
                            endkey.append(TimeUtil.getReversedOrderTime(family.getNextContentKey()));
                            //endkey.append(TimeUtil.getReversedOrderTime("20180321010101001"));
                            startkey.append(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
                            startkey.append(family.getFamilyId() + CloudBridgeUtil.E2C_SPLIT_MEG);
                            startkey.append(TimeUtil.getReversedOrderTime(Const.FUTURE_TIME_KEY));

                            getGroupAllMessage(family.getFamilyId(), startkey.toString(), endkey.toString());
                            if (mApp.getmEndContentKey() == null) {
                                mApp.setmEndContentKey(new HashMap<String, String>());
                            }
                            mApp.sdcardLog("huangqilin:find chat startkey:" + startkey.toString() + "endkey:" + endkey.toString());
                            mApp.getmEndContentKey().put(family.getFamilyId(), TimeUtil.getReversedOrderTime(family.getNextContentKey()));
                        }
                    }

                    //获取离线私聊语音
                    getPrivateChat(mApp.getCurUser().getEid());

                    if (mApp.isCurrentRunningForeground) {
                        // 断网重连时拉一下手表状态信息
                        if (mApp.getCurUser().getFocusWatch() != null) {
                            String[] statekeys = new String[7];
                            statekeys[0] = CloudBridgeUtil.BATTERY_LEVEL;
                            statekeys[1] = CloudBridgeUtil.WATCH_ONOFF_FLAG;
                            statekeys[2] = CloudBridgeUtil.OPERATION_MODE_VALUE;
                            statekeys[3] = CloudBridgeUtil.SILENCE_LIST;
                            statekeys[4] = CloudBridgeUtil.DEVICE_POWER_ON_TIME;
                            statekeys[5] = CloudBridgeUtil.SLEEP_LIST;
                            statekeys[6] = CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE;
                            mapMGet(mApp.getCurUser().getFocusWatch().getEid(), statekeys);
                        }
                    }

                } else if (intent.getAction().equals(Const.ACTION_RECEIVE_REQ_ADD_NEW_FRIEND)) {
                    //弹出pop
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    if (mApp.getRunningActivityName().equals(NewLoginActivity.class.getName())) {
                        mApp.setFriendRequest(resp);
                        return;
                    }
                    handleNewFriendRequest(resp);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RECEIVE_CHAT_MSG);
        filter.addAction(Const.ACTION_WARNNING_TYPE_SAFEAREA);
        filter.addAction(Const.ACTION_WARNNING_TYPE_SOS);
        filter.addAction(Const.ACTION_RECEIVE_GROUP_CHANGE_MSG);
        filter.addAction(Const.ACTION_RECEIVE_REQ_JOIN_GROUP);
        filter.addAction(Const.ACTION_GET_OFFLINE_CHAT_MSG);
        filter.addAction(Const.ACTION_RECEIVE_REQ_ADD_NEW_FRIEND);
        registerReceiver(mBgMsgReceiver, filter);
    }

    private String lastAskJoinTag = null;

    private void initBgMsgNetCallback() {
        mBgMsgNetCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc;
                JSONObject pl, plReq;
                String eid;
                Intent intt;
                int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
                Date d = new Date();
                switch (cid) {
                    case CloudBridgeUtil.CID_DEVICE_OFFLINE_STATE_RESP:
                        rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        if (rc == CloudBridgeUtil.RC_SUCCESS) {
                            pl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                            eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                            pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                            int offline_state = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_DEVICE_OFFLINE_STATE);
                            if (offline_state == 1 && mApp.isDeviceOfflineMapSet) {
                                mApp.isDeviceOfflineMapSet = false;
                                String deviceName = mApp.getCurUser().queryNicknameByEid(eid);
                                String toastTitle = getString(R.string.watch_offline_set_state, deviceName);
                                ToastUtil.show(mApp, toastTitle);
                            }
                            mApp.setmWatchOfflineState(eid, offline_state);
                            Intent intent = new Intent(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
                            sendBroadcast(intent);
                        }
                        break;
                    case CloudBridgeUtil.CID_STATE_MSG_RESP:
                        rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        if (rc == CloudBridgeUtil.RC_SUCCESS) {
//                            String currentStamp = TimeUtil.getTimeStampLocal();
                            mApp.setValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), Const.DEFAULT_NEXT_KEY);
//                            mApp.setValue(Const.SHARE_TIME_CALL_OUTING_COUNT, currentStamp);
                        }
                        break;

                    case CloudBridgeUtil.CID_C2E_GET_MESSAGE_RESP:
                        rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        if (1 == rc) {
                            pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                            ChatMsgEntity entity = new ChatMsgEntity();
                            JSONObject content;
                            Boolean privateflag = true;
                            String key = (String) pl.get("Key");
                            String sendtime;
                            //组消息处理
                            if (key.substring(0, 3).equals(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE)) {
                                key = key.substring(3);
                                sendtime = key.substring(key.indexOf('/') + 5); //5表示‘/MSG/’的字符数
                                key = key.substring(0, key.indexOf('/'));
                                for (FamilyData family : mApp.getCurUser().getFamilyList()) {
                                    if (key.equals(family.getFamilyId())) {//找到对应的familyid
                                        privateflag = false;
                                        content = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                                        String type = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_TYPE);
                                        String keyEid = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_EID);
                                        if (!keyEid.equals(mApp.getCurUser().getEid())) { //获取的是语音消息
                                            if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS)
                                                    || type.equals(CloudBridgeUtil.OFFlINE_MSG_TYPE_SECURITYAREA)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_APPSTORE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM)
                                            ) {
                                                String msgContent;
                                                String time;
                                                if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)) {
                                                    byte[] bytes = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                                    msgContent = StrUtil.Byte2Unicode(bytes, 0, bytes.length);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)) {
                                                    msgContent = getString(R.string.device_upgrade_success);
                                                    int downloadCode = Integer.parseInt((String) content.get("Content"));
                                                    switch (downloadCode) {
                                                        case 1:
                                                            msgContent = getString(R.string.device_upgrade_success);
                                                            break;
                                                        case 0:
                                                            msgContent = getString(R.string.device_upgrade_start);
                                                            break;
                                                        case -1:
                                                            msgContent = getString(R.string.device_upgrade_ongoing);
                                                            break;
                                                        case -2:
                                                            msgContent = getString(R.string.device_upgrade_battery_low);
                                                            break;
                                                        case -3:
                                                            msgContent = getString(R.string.device_upgrade_info_error);
                                                            break;
                                                        case -4:
                                                            msgContent = getString(R.string.device_upgrade_unnecessary);
                                                            break;
                                                        case -5:
                                                            msgContent = getString(R.string.device_upgrade_info_error);
                                                            break;
                                                    }
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)) {
                                                    JSONObject content_1 = (JSONObject) content.get("Content");
                                                    msgContent = content_1.get(CloudBridgeUtil.KEY_NAME_NICKNAME) + "_" + content_1.get(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE);
                                                    keyEid = (String) content_1.get(CloudBridgeUtil.E2C_PL_KEY_EID);
                                                } else {
                                                    msgContent = content.get("Content").toString();
                                                }

                                                if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)) {
                                                    JSONObject content_1 = (JSONObject) content.get("Content");
                                                    time = (String) content_1.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                                                } else {
                                                    time = TimeUtil.getOrderTime(sendtime);
                                                }

                                                handleNoticeMsg(family, time, keyEid, type, msgContent, false);
                                                mApp.setNextContentKey(family, TimeUtil.timeInc(time));
                                            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VOICE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)
                                                    || type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)
                                                    || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {

                                                if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS);
                                                    entity.setmForceRecordOk(2);
                                                    mApp.sdcardLog("sos receive record time=" + TimeUtil.getTimeStampLocal() + "line=" + LogUtil.getLineNumber());

                                                    mApp.setForceRecordState(keyEid, false);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD);
                                                    entity.setmForceRecordOk(2);
                                                    mApp.sdcardLog("handleC2eResp receive force record " + LogUtil.getLineNumber());

                                                    mApp.setForceRecordState(keyEid, false);
                                                } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE);
                                                    mApp.setForceTakePhotoState(keyEid, false);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                                                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO);
                                                }
                                                entity.setmDate(TimeUtil.getOrderTime(sendtime));
                                                if (content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                                                    entity.setmDuration((Integer) content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
                                                }
                                                entity.setmSrcId(keyEid);
                                                entity.setmIsFrom(true);
                                                entity.setmPlayed(false);

                                                entity.setmFamilyId(family.getFamilyId());
                                                byte[] bitmapArray;
                                                //去除重复消息标志
                                                boolean flag = ChatHisDao.getInstance(getApplicationContext()).isMsgExist(family.getFamilyId(),
                                                        entity.getmDate());
                                                if (flag == false) {
                                                    try {
                                                        if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                                            entity.setmPlayed(true);
                                                            entity.setmAudioPath((String) content.get("Content"));
                                                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)) {
                                                            entity.setmPlayed(true);
                                                            bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                                            File image = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getImageFileName());
                                                            FileOutputStream out = new FileOutputStream(image);
                                                            out.write(bitmapArray);
                                                            out.close();
                                                            entity.setmAudioPath(image.getPath());
                                                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                                                            entity.setmPlayed(true);
                                                            entity.setmAudioPath((String) content.get("Content"));
                                                            mApp.downloadNoticeVideo(keyEid, entity.getmAudioPath(), null);
                                                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                                            entity.setmPlayed(true);
                                                            entity.setmAudioPath((String) content.get("Content"));
                                                        } else {
                                                            bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                                            File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                                                            FileOutputStream out = new FileOutputStream(voice);
                                                            byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                                                            out.write(tmp);
                                                            out.close();
                                                            entity.setmAudioPath(voice.getPath());
                                                        }
                                                        //修改sos消息存放列表和存放规则
                                                        if (mApp.getmSosStartFlag() == true) {
                                                            for (String time : mApp.getmSosCollectList().keySet()) {
                                                                HashMap<String, ArrayList<ChatMsgEntity>> curchatlist = mApp.getmSosCollectList()
                                                                        .get(time);
                                                                if (curchatlist.get(family.getFamilyId()) != null) {
                                                                    mApp.sdcardLog("sos receive record add list. time=" + TimeUtil.getTimeStampLocal() + "line=" + LogUtil.getLineNumber());
                                                                    curchatlist.get(family.getFamilyId()).add(entity);
                                                                }
                                                            }
                                                        }
                                                        ChatHisDao.getInstance(getApplicationContext()).addChatMsg(family.getFamilyId(), entity);
                                                        mApp.setNextContentKey(family, TimeUtil.timeInc(TimeUtil.getOrderTime(sendtime)));
                                                        mApp.setHasNewGroupMsg(family.getFamilyId(), true);
                                                        intt = new Intent();
                                                        intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
                                                        sendBroadcast(intt);
                                                        sendBroadcast(new Intent(Constants.ACTION_RECEIVE_GROUP_MESSAGE_NOTIFY));

                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        } else {

                                        }
                                        break;

                                    }
                                }

                                if (privateflag) {
                                    content = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                                    String type = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_TYPE);
                                    if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI) || type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                        if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)) {
                                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
                                        } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
                                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
                                        }
                                        entity.setmDate(TimeUtil.getOrderTime(sendtime));
                                        if (content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                                            entity.setmDuration((Integer) content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
                                        }
                                        entity.setmSrcId((String) content.get(CloudBridgeUtil.E2C_PL_KEY_EID));
                                        if (!entity.getmSrcId().equals(mApp.getCurUser().getEid())) {
                                            entity.setmIsFrom(true);
                                        } else {
                                            entity.setmIsFrom(false);
                                            entity.setmSended(1);
                                        }
                                        entity.setmPlayed(false);
                                        entity.setmFamilyId(key);
                                        byte[] bitmapArray;
                                        //去除重复消息标志
                                        boolean flag = ChatHisDao.getInstance(getApplicationContext()).isMsgExist(key,
                                                entity.getmDate());
                                        if (flag == false) {
                                            try {
                                                if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                                    entity.setmPlayed(true);
                                                    entity.setmAudioPath((String) content.get("Content"));
                                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                                    entity.setmPlayed(true);
                                                    entity.setmAudioPath((String) content.get("Content"));
                                                } else {
                                                    bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                                    File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                                                    FileOutputStream out = new FileOutputStream(voice);

                                                    byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                                                    out.write(tmp);
                                                    out.close();
                                                    entity.setmAudioPath(voice.getPath());
                                                }

                                                ChatHisDao.getInstance(getApplicationContext()).addChatMsg(key, entity);
                                                mApp.setHasNewPrivateMsg(key, true);
                                                //通知接受到语音消息
                                                intt = new Intent();
                                                intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
                                                sendBroadcast(intt);
                                                sendBroadcast(new Intent(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY));

                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } else if (key.substring(0, 3).equals(CloudBridgeUtil.PREFIX_EP_E2C_MESSAGE)) {
                                //单个eid消息处理

                            }
                        } else if (-12 == rc) {
                            LogUtil.e("error rc = " + rc);
                        } else if (-200 == rc) {
                            LogUtil.e("error rc = " + rc);
                        } else {

                        }
                        break;

                    case CloudBridgeUtil.CID_MAPGET_MGET_RESP:

                        rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        if (CloudBridgeUtil.RC_SUCCESS == rc) {
                            if (plReq != null && pl != null) {
                                eid = (String) plReq.get(CloudBridgeUtil.KEY_NAME_EID);
                                String value = (String) pl.get(CloudBridgeUtil.BATTERY_LEVEL);
                                handleMapMGetBattery(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.DEVICE_POWER_ON_TIME);
                                handleMapMGetPowerOnTime(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.WATCH_ONOFF_FLAG);
                                handleMapMGetWatchOnOff(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.OPERATION_MODE_VALUE);
                                handleMapMGetWatchMode(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.SIGNAL_LEVEL);
                                handleMapMGETWatchSignal(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE);
                                handleMapMGETWatchNetState(eid, value);

                                value = (String) pl.get(CloudBridgeUtil.KEY_NAME_CHARGE_STATUS);
                                if (value != null && value.length() == 1) {
                                    mApp.setmChargeState(eid, Integer.valueOf(value));
                                }
                                if (pl.containsKey(CloudBridgeUtil.SILENCE_LIST)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.SILENCE_LIST);
                                    updateSilenceTimeData(eid, jstr);
                                }
                                if (pl.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
                                    updateSleepListData(eid, jstr);
                                }
                                if (pl.containsKey(CloudBridgeUtil.OFFLINE_MODE_VALUE)) {
                                    String jstr = (String) pl.get(CloudBridgeUtil.OFFLINE_MODE_VALUE);
                                    updateOfflineMode(eid, jstr);
                                }

                                Intent intent = new Intent(Const.ACTION_REFRESH_WATCH_TITLE);
                                intent.putExtra(Const.KEY_WATCH_ID, eid);
                                sendBroadcast(intent);

                                try {

                                    WatchData focusWatch = mApp.getCurUser().getFocusWatch();
                                    if (pl.containsKey(CloudBridgeUtil.KEY_STORY_SWITCH + focusWatch.getDeviceType())) {
                                        JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                                        String ximalayaOnOff = (String) maggetPl.get(CloudBridgeUtil.KEY_STORY_SWITCH + focusWatch.getDeviceType());
                                        if (ximalayaOnOff != null && ximalayaOnOff.length() > 0) {
                                            int visible = Integer.parseInt(ximalayaOnOff);
                                            mApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_FIELD_STORY_VISIBLE, visible);
                                            sendBroadcast(new Intent(Const.ACTION_STORY_VISIBLE_CHANGE));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            LogUtil.e("mapmget error rc = " + rc);
                        }
                        break;

                    case CloudBridgeUtil.CID_E2E_DOWN: {
                        rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        int sub_action = (int) plReq.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                        if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_UPDATE == sub_action) {
                            String updateInfo = getString(R.string.device_upgrade_start);
                            if (pl != null) {
                                int state = Integer.valueOf((String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_UPDATE_STATE));
                                switch (state) {
                                    case 1:
                                        updateInfo = getString(R.string.device_upgrade_success);
                                        break;
                                    case 0:
                                        updateInfo = getString(R.string.device_upgrade_start);
                                        break;
                                    case -1:
                                        updateInfo = getString(R.string.device_upgrade_ongoing);
                                        break;
                                    case -2:
                                        updateInfo = getString(R.string.device_upgrade_battery_low);
                                        break;
                                    case -3:
                                        updateInfo = getString(R.string.device_upgrade_info_error);
                                        break;
                                    case -4:
                                        updateInfo = getString(R.string.device_upgrade_unnecessary);
                                        break;
                                    case -5:
                                        updateInfo = getString(R.string.device_upgrade_info_error);
                                        break;
                                }
                            } else {
                                if (rc == CloudBridgeUtil.ERROR_CODE_E2E_OFFLINE) {
                                    updateInfo = getString(R.string.watch_offline);
                                } else if (rc == CloudBridgeUtil.RC_NETERROR) {
                                    updateInfo = getString(R.string.network_error_prompt);
                                } else {
                                    updateInfo = getString(R.string.watch_offline);
                                }
                            }
                            ToastUtil.show(NetService.this, updateInfo);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        };
    }

    public void mapMGet(String eid, String[] keys) {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(mBgMsgNetCallback);

        JSONArray plKeyList = new JSONArray();
        for (int i = 0; i < keys.length; i++) {
            plKeyList.add(keys[i]);
        }

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (isCloudBridgeClientOk()) {
            sendNetMsg(mapget);
        }
    }

    public void getDeviceOfflineState(String eid) {
        MyMsgData deviceOfflineState = new MyMsgData();
        deviceOfflineState.setCallback(mBgMsgNetCallback);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        deviceOfflineState.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_OFFLINE_STATE, pl));
        if (isCloudBridgeClientOk()) {
            sendNetMsg(deviceOfflineState);
        }
    }

    // mapset battery format:20150804115841993_35
    private long getBatteryTimeFromMapset(String value) {
        int index = value.indexOf("_");
        long time;
        if (index == -1) {// not found
            time = 0;
        } else {
            time = TimeUtil.getMillisByTime(value.substring(0, index));
        }
        return time;
    }

    // mapset battery format:201508090234234_90
    private String getBatteryValueFromMapset(String value) {
        int index = value.indexOf("_");
        if (index == -1) {// not found
            return value;
        } else {
            return value.substring(index + 1);
        }
    }

    public void handleMapMGetBattery(String eid, String battery) {

        if (eid == null || battery == null
                || eid.length() == 0 || battery.length() == 0)
            return;
        LogUtil.d("hep" + "  " + "eid: " + eid + ",battery: " + battery);

        long curtime = getBatteryTimeFromMapset(battery);
        if (mApp.gettimeOfRecentBattery() != null) {
            mApp.settimeOfRecentBattery(eid, curtime);
        }
        String[] array = battery.split("_");
        mApp.setValue(eid + CloudBridgeUtil.BATTERY_TIMESTAMP, array[0]);
        mApp.sdcardLog("map get battery: battery=" + battery + "  eid:" + eid);

        battery = getBatteryValueFromMapset(battery);
        for (WatchData watch : mApp.getCurUser().getWatchList()) {
            if (eid.equals(watch.getEid())) {
                mApp.setWatchBatteryLevel(watch, Integer.valueOf(battery));
            }
        }
    }

    public void handleMapMGETWatchSignal(String eid, String value) {
        if (eid == null || value == null
                || eid.length() == 0 || value.length() == 0)
            return;
        LogUtil.e( "handleMapMGETWatchSignal  value = " + value);
        mApp.setValue(eid + CloudBridgeUtil.SIGNAL_LEVEL, value);//本地保存信号信息
    }

    public void handleMapMGETWatchNetState(String eid, String value) {
        if (eid == null || value == null
                || eid.length() == 0 || value.length() == 0)
            return;
        mApp.setValue(eid + CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE, value);//本地保存信号信息
    }

    public void handleMapMGetWatchMode(String eid, String mode) {
        if (eid == null || mode == null
                || eid.length() == 0 || mode.length() == 0)
            return;

        LogUtil.d("hep" + "  " + "eid: " + eid + ",mode: " + mode);
        int tmpSelect = Integer.parseInt(mode);
        for (WatchData watch : mApp.getCurUser().getWatchList()) {
            if (eid.equals(watch.getEid())) {
                watch.setOperationMode(tmpSelect);
                break;
            }
        }
    }

    public void handleMapMGetPowerOnTime(String eid, String powerOnTime) {
        if (eid == null || powerOnTime == null || eid.length() == 0 || powerOnTime.length() == 0)
            return;
        mApp.setValue(eid + CloudBridgeUtil.DEVICE_POWER_ON_TIME, powerOnTime);
    }

    public void handleMapMGetWatchOnOff(String eid, String flag) {

        String strFlag;
        int state;

        if (eid == null || flag == null || eid.length() == 0 || flag.length() == 0)
            return;

        LogUtil.d("hep" + "  " + "eid: " + eid + ",Stateflag: " + flag);
        int index = flag.indexOf("_");
        if (index == -1) {
            state = Integer.valueOf(flag);
        } else {
            strFlag = flag.substring(index + 1);
            state = Integer.valueOf(strFlag);
        }
        mApp.setmWatchState(eid, state);
    }

    private ChatMsgEntity handlePrivateChat(JSONObject chat) {
        ChatMsgEntity entity = new ChatMsgEntity();
        String type = (String) chat.get(CloudBridgeUtil.E2C_PL_KEY_TYPE);
        if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE) ||
                type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_EMOJI) ||
                type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT) ||
                type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO) ||
                type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
            if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)) {
                entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
            } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO);
            }
            if (chat.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                entity.setmDuration((Integer) chat.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
            }
            entity.setmSrcId((String) chat.get(CloudBridgeUtil.E2C_PL_KEY_EID));
            if (entity.getmSrcId() != null && !entity.getmSrcId().equals(mApp.getCurUser().getEid())) {
                entity.setmIsFrom(true);
            } else {
                entity.setmIsFrom(false);
                entity.setmSended(1);
            }
            entity.setmPlayed(false);
            String key = (String) chat.get(CloudBridgeUtil.KEY_NAME_KEY);
            String fimilyid = key.substring(3, key.indexOf(CloudBridgeUtil.E2C_SPLIT_MEG));
            String sendtime = key.substring(key.indexOf(CloudBridgeUtil.E2C_SPLIT_MEG) + 5);
            entity.setmDate(TimeUtil.getOrderTime(sendtime));

            entity.setmFamilyId(fimilyid);
            byte[] bitmapArray;
            //去除重复消息标志
            boolean flag = ChatHisDao.getInstance(mApp.getApplicationContext()).isMsgExist(fimilyid, entity.getmDate());
            if (flag == false) {
                try {
                    if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                        entity.setmPlayed(true);
                        entity.setmAudioPath((String) chat.get("Content"));
                    } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                        entity.setmPlayed(true);
                        entity.setmAudioPath((String) chat.get("Content"));
                    } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                        entity.setmPlayed(true);
                        entity.setmAudioPath((String) chat.get("Content"));
                        mApp.downloadNoticeVideo(entity.getmSrcId(), entity.getmAudioPath(), null);
                    } else {
                        bitmapArray = Base64.decode((String) chat.get("Content"), Base64.NO_WRAP);
                        File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                        FileOutputStream out = new FileOutputStream(voice);

                        byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                        out.write(tmp);
                        out.close();
                        entity.setmAudioPath(voice.getPath());
                    }
                    ChatHisDao.getInstance(getApplicationContext()).addChatMsg(fimilyid, entity);
                    mApp.setNextPrivateChatKey(mApp.getCurUser().getEid(), TimeUtil.timeInc(TimeUtil.getOrderTime(sendtime)));
                    mApp.setHasNewPrivateMsg(fimilyid, true);
                    return entity;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void handleGroupAllMessage(JSONObject pl) {
        if (pl.containsKey(CloudBridgeUtil.KEY_NAME_MARK_KEY)) {
            String markKey = (String) pl.get(CloudBridgeUtil.KEY_NAME_MARK_KEY);
            String[] splits = markKey.split("/");
            String gid = splits[1];
            FamilyData family = mApp.getCurUser().queryFamilyByGid(gid);
            mApp.setNextContentKey(family, TimeUtil.timeInc(TimeUtil.getOrderTime(splits[splits.length - 1])));
        } else {
            JSONArray list = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_LIST);
            for (int i = 0; i < list.size(); i++) {
                JSONObject content = (JSONObject) list.get(i);
                handleGroupMessageContent(content);
            }
            MessageUtils.addOfflineMsgCount(mApp);
        }
    }

    private void handleGroupMessageContent(JSONObject content) {
        String eid = (String) content.get(KEY_NAME_EID);
        String key = (String) content.get(CloudBridgeUtil.KEY_NAME_KEY);
        String[] splits = key.split("/");
        String gid = splits[1];
        String timeKey = splits[splits.length - 1];
        FamilyData family = mApp.getCurUser().queryFamilyByGid(gid);
        String type = (String) content.get(CloudBridgeUtil.KEY_NAME_TYPE);
        switch (type) {
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS:
            case CloudBridgeUtil.OFFlINE_MSG_TYPE_SECURITYAREA:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_APPSTORE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM:
                handleGroupNoticeMessage(family, eid, timeKey, type, content);
                break;

            case CloudBridgeUtil.OFFLINE_MSG_TYPE_VOICE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS:
            case CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO:
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI:
                handleGroupVoiceMessage(family, eid, timeKey, type, content);
                break;
            default:
                break;

        }
        mApp.setNextContentKey(family, TimeUtil.timeInc(TimeUtil.getOrderTime(timeKey)));
    }

    private void handleGroupNoticeMessage(FamilyData family, String seid, String timeKey, String type, JSONObject content) {
        String time;
        String msgContent;

        if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)) {
            byte[] bytes = Base64.decode((String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT), Base64.NO_WRAP);
            msgContent = StrUtil.Byte2Unicode(bytes, 0, bytes.length);
        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)) {
            msgContent = getString(R.string.device_upgrade_success);
            int downloadCode = Integer.parseInt((String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT));
            switch (downloadCode) {
                case 1:
                    msgContent = getString(R.string.device_upgrade_success);
                    break;
                case 0:
                    msgContent = getString(R.string.device_upgrade_start);
                    break;
                case -1:
                    msgContent = getString(R.string.device_upgrade_ongoing);
                    break;
                case -2:
                    msgContent = getString(R.string.device_upgrade_battery_low);
                    break;
                case -3:
                    msgContent = getString(R.string.device_upgrade_info_error);
                    break;
                case -4:
                    msgContent = getString(R.string.device_upgrade_unnecessary);
                    break;
                case -5:
                    msgContent = getString(R.string.device_upgrade_info_error);
                    break;
            }
        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)) {
            JSONObject content_1 = (JSONObject) content.get(CloudBridgeUtil.KEY_NAME_CONTENT);
            msgContent = content_1.get(CloudBridgeUtil.KEY_NAME_NICKNAME) + "_" + content_1.get(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE);
            seid = (String) content_1.get(CloudBridgeUtil.E2C_PL_KEY_EID);
        } else {
            //content不能为null
            if (content.get(CloudBridgeUtil.KEY_NAME_CONTENT) == null) {
                return;
            }
            msgContent = content.get(CloudBridgeUtil.KEY_NAME_CONTENT).toString();
        }

        if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY)) {
            JSONObject content_1 = (JSONObject) content.get(CloudBridgeUtil.KEY_NAME_CONTENT);
            time = (String) content_1.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
        } else {
            time = TimeUtil.getOrderTime(timeKey);
        }
        handleNoticeMsg(family, time, seid, type, msgContent, false);
    }

    private void handleGroupVoiceMessage(FamilyData family, String seid, String timeKey, String type, JSONObject content) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setmDate(TimeUtil.getOrderTime(timeKey));
        boolean flag = ChatHisDao.getInstance(getApplicationContext()).isMsgExist(family.getFamilyId(), entity.getmDate());
        if (!flag) {
            entity.setmType(getMessageTypeByOfflineType(type));
            entity.setmSrcId(seid);
            entity.setmIsFrom(true);
            entity.setmFamilyId(family.getFamilyId());
            if (content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                entity.setmDuration((Integer) content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
            }

            if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)) {
                entity.setmForceRecordOk(2);
                mApp.sdcardLog("handleC2eResp receive force record " + LogUtil.getLineNumber());
                mApp.setForceRecordState(seid, false);
                entity.setmPlayed(false);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD)) {
                entity.setmForceRecordOk(2);
                mApp.sdcardLog("handleC2eResp receive force record " + LogUtil.getLineNumber());
                mApp.setForceRecordState(seid, false);
                entity.setmPlayed(false);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)) {
                mApp.setForceTakePhotoState(seid, false);
                entity.setmPlayed(true);
                byte[] bitmapArray;
                String tempContent = (String) content.get("Content");
                bitmapArray = Base64.decode(tempContent, Base64.NO_WRAP);
                File image = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getImageFileName());
                try {
                    FileOutputStream out = new FileOutputStream(image);
                    out.write(bitmapArray);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                entity.setmAudioPath(image.getPath());
            } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                entity.setmPlayed(true);
                entity.setmAudioPath((String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT));
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                entity.setmPlayed(true);
                entity.setmAudioPath((String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT));
                mApp.downloadNoticeVideo(seid, entity.getmAudioPath(), null);
            } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                entity.setmPlayed(true);
                entity.setmAudioPath((String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT));
            } else {
                entity.setmPlayed(false);
                byte[] bitmapArray;
                String tempContent = (String) content.get(CloudBridgeUtil.KEY_NAME_CONTENT);
                bitmapArray = Base64.decode(tempContent, Base64.NO_WRAP);
                File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                try {
                    FileOutputStream out = new FileOutputStream(voice);
                    byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                    out.write(tmp);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                entity.setmAudioPath(voice.getPath());
            }
            if (mApp.getmSosStartFlag() == true && family.getFamilyId().equals(mApp.getmSosFamily())) {
                mApp.getmSosChatList().add(entity);
            }
            ChatHisDao.getInstance(getApplicationContext()).addChatMsg(family.getFamilyId(), entity);
            mApp.setHasNewGroupMsg(family.getFamilyId(), true);
            Intent intt = new Intent();
            intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
            sendBroadcast(intt);
            sendBroadcast(new Intent(Constants.ACTION_RECEIVE_GROUP_MESSAGE_NOTIFY));

        }
    }

    private int getMessageTypeByOfflineType(String type) {
        int messageType = 0;
        switch (type) {
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_VOICE:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD;
                break;
            case CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO;
                break;
            case CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI:
                messageType = ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI;
                break;
        }

        return messageType;
    }

    /**
     * 类名称：NetService
     * 创建人：zhangjun5
     * 创建时间：2015/12/31 14:52
     * 方法描述：当定位信息为-12时，存在sos请求，则把最后一次手表定位信息最为标志，弹出sos界面。
     */
    private void setupSosInfo(WatchData watch, SosWarning sos, LocationData location) {
        if (watch == null || location == null)
            return;

        String familyid = watch.getFamilyId();
        watch.setCurLocation(location);
        LocationDAO.getInstance(mApp).updateLocation(watch.getEid(), location);
        sos.setmLocation(location);
        WarningInfo warning = new WarningInfo();
        warning.setmWarningType(2);
        warning.setmTimestamp(sos.getmTimestamp());
        warning.setmSos(sos);
        if (mApp.getmWarningMsg().get(familyid) == null) {
            mApp.getmWarningMsg().put(familyid, new ArrayList<WarningInfo>());
        }
        mApp.getmWarningMsg().get(familyid).add(warning);
        Collections.sort(mApp.getmWarningMsg().get(familyid));
        WarningInfoDao.getInstance(getApplication()).addWarningMsg(familyid, warning);

        Intent it = new Intent(NetService.this, SosStartActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("sos", sos.getmTimestamp());
        it.putExtra("familyid", familyid);
        startActivity(it);
        mApp.sdcardLog("sos startActivity time=" + TimeUtil.getTimeStampLocal() + ",line=" + LogUtil.getLineNumber()
                + ":setupSosInfo");
    }

    private void refreshSosInfo(WatchData watch, SosWarning sos, JSONObject pl) {

        if (watch == null)
            return;

        String familyid = watch.getFamilyId();
        LocationData location = LocationData.parseLocation(this, pl, watch.getCurLocation());
        JSONObject result = (JSONObject) pl.get("result");
        if (location.getStatus() == 1 && location.getType() != 0) {
            watch.setCurLocation(location);
            LocationDAO.getInstance(mApp).updateLocation(watch.getEid(), location);
            sos.setmLocation(location);
            WarningInfo warning = new WarningInfo();
            warning.setmWarningType(2);
            warning.setmTimestamp(sos.getmTimestamp());
            warning.setmSos(sos);
            if (mApp.getmWarningMsg().get(familyid) == null) {
                mApp.getmWarningMsg().put(familyid, new ArrayList<WarningInfo>());
            }
            mApp.getmWarningMsg().get(familyid).add(warning);
            Collections.sort(mApp.getmWarningMsg().get(familyid));
            WarningInfoDao.getInstance(getApplication()).addWarningMsg(familyid, warning);

            //更新sos动态
            boolean flag = NoticeMsgHisDAO.getInstance(getApplicationContext()).isMsgExist(familyid, mApp.getCurUser().getEid(), location.getTimestamp());
            if (!flag) {
                NoticeMsgData msg = new NoticeMsgData();
                msg.setmTimeStamp(location.getTimestamp());
                msg.setmType(NoticeMsgData.MSG_TYPE_SOS_LOCATION);
                msg.setmSrcid(watch.getEid());
                msg.setmGroupid(familyid);
                JSONObject content = new JSONObject();
                content.put("Location", result);
                content.put("timestamp", msg.getmTimeStamp());
                msg.setmContent(content.toString());
                NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(familyid, mApp.getCurUser().getEid(), msg);
                mApp.setHasNewNoticeMsg(familyid, AllMessageAdapter.MESSAGE_TYPE_LOCATION, true);
                Intent intent = new Intent(Const.ACTION_RECEIVE_NOTICE_MSG);
                intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_LOCATION);
                sendBroadcast(intent);
            }
            //收到sos告警，弹出windows pop
            LogUtil.i("netservice" + "  " + "接收到sos信息！");
            Intent it = new Intent(NetService.this, SosStartActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("sos", sos.getmTimestamp());
            it.putExtra("familyid", familyid);
            startActivity(it);
            mApp.sdcardLog("sos startActivity time=" + TimeUtil.getTimeStampLocal() + ",line=" + LogUtil.getLineNumber());
        }
    }

    private void setFamilyChangeInfo(JSONObject pl) {

        String gid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
        String time = (String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
        boolean flag = NoticeMsgHisDAO.getInstance(getApplicationContext()).isMsgExist(gid, mApp.getCurUser().getEid(), time);

        if (flag == false) {
            NoticeMsgData msg = new NoticeMsgData();
            msg.setmTimeStamp(time);
            msg.setmType(NoticeMsgData.MSG_TYPE_FAMILY_CHANGE);
            msg.setmSrcid((String) pl.get(CloudBridgeUtil.E2C_PL_KEY_EID));
            msg.setmGroupid((String) pl.get(CloudBridgeUtil.KEY_NAME_GID));
            //1、家庭成员加入，2、家庭成员退出
            msg.setmContent(pl.get(CloudBridgeUtil.KEY_NAME_NICKNAME) + "_" + pl.get(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE));
            NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(msg.getmGroupid(), mApp.getCurUser().getEid(), msg);
            mApp.setHasNewNoticeMsg(gid, AllMessageAdapter.MESSAGE_TYPE_FAMILY_MEMBER, true);
            Intent intent = new Intent(Const.ACTION_RECEIVE_NOTICE_MSG);
            intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_ALL);
            sendBroadcast(intent);

            if (msg != null) {
                if (mApp.getCurUser().queryFamilyByGid(msg.getmGroupid()) != null) {
                    String text = "";
                    int type = Integer.valueOf((String) pl.get(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE));
                    String name = (String) pl.get(CloudBridgeUtil.KEY_NAME_NICKNAME);
                    FamilyData family = mApp.getCurUser().queryFamilyByGid(msg.getmGroupid());
                    switch (type) {
                        case 1:
                            text = getString(R.string.join_family, name);
                            break;
                        case 2:
                            text = getString(R.string.quit_from_family, name);
                            break;
                        case 4:
                            text = getString(R.string.become_family_admin, name);
                            break;
                        default:
                            break;
                    }
                    WatchData watch = mApp.getCurUser().queryFamilyByGid(msg.getmGroupid()).getWatchlist().get(0);
                    JSONObject noticeSetting = mApp.getNoticeSetting(watch.getEid());
                    if (noticeSetting != null) {
                        String isNeedNotice = (String) noticeSetting.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY);
                        if ("0".equals(isNeedNotice)) {
                            return;
                        }
                    }
                    mApp.recvMsgNotify(watch.getEid(), getString(R.string.family_member_message, watch.getNickname()),
                            text, Const.TITLE_BAR_NEW_NOTICE_MESSAGE + AllMessageAdapter.MESSAGE_TYPE_ALL, msg.getmGroupid(), NoticeTypeActivity.class);
                }
            }
        }
    }

    private void setSosInfo(JSONObject pl) {
        SosWarning sos = new SosWarning();
        sos.setmEid((String) pl.get(CloudBridgeUtil.E2C_PL_KEY_EID));
        sos.setmTypeKey((String) pl.get(CloudBridgeUtil.KEY_NAME_SOS_TYPEKEY));
        sos.setmTimestamp((String) pl.get("Timestamp"));

        JSONObject location = (JSONObject) pl.get(CloudBridgeUtil.KEY_NAME_SOS_LOCATION);
        location.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, sos.getmTimestamp());
        location.put(CloudBridgeUtil.KEY_NAME_EID, pl.get(CloudBridgeUtil.E2C_PL_KEY_EID));

        String eid = (String) pl.get(CloudBridgeUtil.E2C_PL_KEY_EID);
        String familyid = mApp.getCurUser().queryWatchDataByEid(sos.getmEid()).getFamilyId();
        mApp.getmSosWarning().put(sos.getmTimestamp(), sos);
        mApp.setNextWarningKey(mApp.getCurUser().queryFamilyByGid(familyid), sos.getmTimestamp());
    }

    private void recvStatChat(String key) {
        JSONArray statArray;
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHH");
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Boolean statFlag = false;
        key = key.substring(3);
        String familyid = key.substring(0, key.indexOf("/"));
        if (mApp.getCurUser().queryFamilyByGid(familyid) == null) {
            return;
        }
        String eid = mApp.getCurUser().queryFamilyByGid(familyid).getWatchlist().get(0).getEid();
        String sendtime = key.substring(key.indexOf("/") + 5);
        long getMillis = TimeUtil.getMillisByTime(TimeUtil.getOrderTime(sendtime));
        getMillis = System.currentTimeMillis() - getMillis;
        String tempkey = mApp.getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + mApp.getCurUser().getEid(), null);
        if (tempkey == null) {
            statArray = new JSONArray();
        } else {
            statArray = (JSONArray) JSONValue.parse(tempkey);
            JSONObject checkobj = (JSONObject) statArray.get(0);
            String checkTime = (String) checkobj.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
            checkTime = checkTime.substring(0, 8);
            if (!checkTime.equals(format1.format(today))) {
                String privKey = mApp.getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), Const.DEFAULT_NEXT_KEY);
                if (privKey.equals(Const.DEFAULT_NEXT_KEY)) {
                    mApp.setValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), tempkey);
                    statArray.clear();
                }
            }
        }
        for (Object object : statArray) {
            JSONObject json = (JSONObject) object;
            String tempeid = (String) json.get(CloudBridgeUtil.KEY_NAME_EID);
            if (tempeid.equals(eid)) {
                String tempTime = (String) json.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                if (tempTime.equals(format.format(today))) {
                    String statLocation = (String) json.get("voice_recv");
                    int all = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + 1;
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    int all_time;
                    if (statLocation.indexOf(",") == -1) {
                        all_time = Integer.valueOf(statLocation);
                    } else {
                        all_time = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    }
                    if (getMillis > 0) {
                        all_time = (int) (all_time + getMillis / 1000);
                    }
                    json.put("voice_recv", all + "," + all_time);
                    statFlag = true;
                }
            }
        }
        if (!statFlag) {
            JSONObject locationObject = new JSONObject();
            locationObject.put(CloudBridgeUtil.KEY_NAME_EID, eid);
            locationObject.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, format.format(today));
            locationObject.put("voice_recv", "1," + (int) (getMillis / 1000));
            locationObject.put("location", "0,0,0,0,0,0,0,0,0,0,0,0,0");
            locationObject.put("voice_send", "0,0,0,0,0,0,0,0,0,0,0,0,0");
            statArray.add(locationObject);
        }
        mApp.setValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + mApp.getCurUser().getEid(), statArray.toString());
    }

    private void sendAskJoinResult(int rc, int ask_sn, String ask_eid, JSONObject reqJo) {
        String[] addGroupTeids = new String[1];
        addGroupTeids[0] = ask_eid;
        String reqVersion = (String) reqJo.get(CloudBridgeUtil.KEY_NAME_VERSION);
        MyMsgData askJoinMsg_1 = new MyMsgData();
        askJoinMsg_1.setCallback(mBgMsgNetCallback);
        JSONObject reqPl = CloudBridgeUtil.getCloudMsgPL(reqJo);

        JSONObject newPl_1 = new JSONObject(reqPl);
        newPl_1.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP);
        newPl_1.put(CloudBridgeUtil.KEY_NAME_RC, rc);
        askJoinMsg_1.setReqMsg(
                CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, ask_sn, mApp.getToken(), null, addGroupTeids, newPl_1));
        if (reqVersion != null) {
            askJoinMsg_1.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, reqVersion);
        }
        sendNetMsg(askJoinMsg_1);

    }

    private void sendAskJoinResultNoPl(int rc, int ask_sn, String ask_eid, JSONObject reqJo) {
        String[] addGroupTeids = new String[1];
        addGroupTeids[0] = ask_eid;
        String reqVersion = (String) reqJo.get(CloudBridgeUtil.KEY_NAME_VERSION);
        MyMsgData askJoinMsg_1 = new MyMsgData();
        askJoinMsg_1.setCallback(mBgMsgNetCallback);
        JSONObject reqPl = CloudBridgeUtil.getCloudMsgPL(reqJo);

        JSONObject newPl_1 = new JSONObject();
        newPl_1.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP);
        newPl_1.put(CloudBridgeUtil.KEY_NAME_RC, rc);
        askJoinMsg_1.setReqMsg(
                CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, ask_sn, mApp.getToken(), null, addGroupTeids, newPl_1));
        if (reqVersion != null) {
            askJoinMsg_1.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, reqVersion);
        }
        sendNetMsg(askJoinMsg_1);

    }

    private void getGroupAllMessage(String gid, String startKey, String endKey) {
        MyMsgData groupAllMessage = new MyMsgData();
        groupAllMessage.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        pl.put(CloudBridgeUtil.KEY_NAME_TYPE, "all");
        pl.put(CloudBridgeUtil.KEY_NAME_KEY_BEGIN, startKey);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY_END, endKey);
        pl.put(CloudBridgeUtil.KEY_NAME_SIZE, 200);
        groupAllMessage.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_GROUP_ALL_CHAT, pl));
        sendNetMsg(groupAllMessage);
    }

    /**
     * 类名称：NetService
     * 修改人：zhangjun5
     * 修改时间：2015/11/9 11:31
     * 方法描述：合成语音信息，同时进行信息归类
     * <p>
     * 修改描述：接收语音信息，分类为当前时间戳为标示的聊天列表里。
     */

    private void handleNoticeMsg(FamilyData family, String time, String eid, String type, String content, boolean needNotification) {
        boolean flag = NoticeMsgHisDAO.getInstance(getApplicationContext()).isMsgExist(family.getFamilyId(), mApp.getCurUser().getEid(), time);
        if (!flag) {
            NoticeMsgData msg = new NoticeMsgData();
            msg.setmTimeStamp(time);
            msg.setmSrcid(eid);
            msg.setmGroupid(family.getFamilyId());
            msg.setmContent(content);
            WatchData watch = family.getWatchlist().get(0);
            int noticeMsgType = NoticeMsgData.MSG_TYPE_SMS;
            int allMsgType = AllMessageAdapter.MESSAGE_TYPE_ALL;

            String notificationTitle = "";
            String notificationContent = "";
            switch (type) {
                case CloudBridgeUtil.OFFlINE_MSG_TYPE_SECURITYAREA:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_SAFE_AREA;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_LOCATION;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_SOS_LOCATION;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_LOCATION;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_BATTERY_WARNNING;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_BATTERY;
                    notificationTitle = getString(R.string.battery_message, watch.getNickname());
                    if (watch.isWatch()) {
                        notificationContent = getString(R.string.battery_notification, watch.getNickname(), content + "%");
                    } else {
                        notificationContent = getString(R.string.battery_notification_device, watch.getNickname(), content + "%");
                    }
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_STAEPS;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_STEPS;
                    notificationTitle = getString(R.string.steps_message, watch.getNickname());
                    String cut_step = msg.getmContent().substring(0, msg.getmContent().indexOf("_"));
                    int curSteps = Integer.valueOf(cut_step);
                    int targetSteps = Integer.valueOf(msg.getmContent().substring(msg.getmContent().indexOf("_") + 1));
                    if (curSteps > targetSteps / 2 && curSteps < targetSteps) {
                        notificationContent = getString(R.string.steps_notice_half_end, watch.getNickname(), curSteps);
                    } else {
                        notificationContent = getString(R.string.steps_notice_finish_end, watch.getNickname(), curSteps);
                    }
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_STAEPSRANKS;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_STEPS;
                    notificationTitle = getString(R.string.ranks_notice_msg);
                    notificationContent = getString(R.string.ranks_notice_content);
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_FAMILY_CHANGE;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_FAMILY_MEMBER;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_SMS;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SMS;
                    notificationTitle = getString(R.string.sms_message, watch.getNickname());
                    notificationContent = getString(R.string.sms_notification, content.substring(0, content.indexOf("\n")), watch.getNickname());
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_CHANGE_SIM;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SMS;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_FLOWMETER;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SMS;
                    notificationTitle = getString(R.string.sms_message, watch.getNickname());
                    String[] flowmeters = msg.getmContent().split("_");
                    double total = Double.valueOf(flowmeters[0]);
                    double current = Double.valueOf(flowmeters[1]);
                    if (current < total) {
                        notificationContent = getString(R.string.flowmeter_tips, watch.getNickname(), StrUtil.flowmeterChange(current, true), StrUtil.flowmeterChange(total, false));
                    } else {
                        notificationContent = getString(R.string.flowmeter_tips_over, watch.getNickname(), StrUtil.flowmeterChange(total, false), StrUtil.flowmeterChange(current, true));
                    }
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_OTA_UPGRADE;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SMS;
                    notificationTitle = getString(R.string.sms_message, watch.getNickname());
                    notificationContent = content;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SMS;
                    notificationTitle = getString(R.string.sms_message, watch.getNickname());
                    JSONObject otaContent = (JSONObject) JSONValue.parse(content);
                    String subType = (String) otaContent.get(CloudBridgeUtil.KEY_NAME_SUBTYPE);
                    if (subType.equals("1")) {
                        notificationContent = (String) otaContent.get(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT);
                    } else {
                        notificationContent = content;
                    }
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_APPSTORE:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_APPMANAGER;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD;
                    notificationTitle = getString(R.string.download_message, watch.getNickname());
                    notificationContent = content;
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_DOWNLOAD;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD;
                    notificationTitle = getString(R.string.download_message, watch.getNickname());
                    JSONObject contentPL = (JSONObject) JSONValue.parse(content);
                    int status = (Integer) contentPL.get("status");
                    String filename = (String) contentPL.get("file");
                    String result;
                    if (status == WatchDownloadBean.DOWNLOAD_STATUS_FAIL) {
                        notificationContent = getString(R.string.watch_download_fail, watch.getNickname(), filename);
                    } else {
                        notificationContent = getString(R.string.watch_download_success, watch.getNickname(), filename);
                    }
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_STORY;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD;
                    notificationTitle = getString(R.string.download_message, watch.getNickname());
                    JSONObject storyContent = (JSONObject) JSONValue.parse(content);
                    notificationContent = (String) storyContent.get(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT);
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM:
                    LogUtils.e(TAG, "OFFLINE_MSG_TYPE_SYSTEM  content = "+content);
                    noticeMsgType = NoticeMsgData.MSG_TYPE_SYSTEM;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_SYSTEM;
                    JSONObject systemJson = (JSONObject) JSONValue.parse(content);
                    notificationTitle = (String) systemJson.get(CloudBridgeUtil.KEY_NAME_TITLE);
                    notificationContent = (String) systemJson.get(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT);
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_CLOUD_SPACE;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD;
                    notificationTitle = getString(R.string.download_message, watch.getNickname());
                    notificationContent = getString(R.string.cloud_space_tips, msg.getmContent());
                    break;
                case CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION:
                    noticeMsgType = NoticeMsgData.MSG_TYPE_NAVIGATION;
                    allMsgType = AllMessageAdapter.MESSAGE_TYPE_LOCATION;
                    notificationTitle = getString(R.string.navigation_title, watch.getNickname());
                    notificationContent = getString(R.string.navigation_notification, watch.getNickname());

                    JSONObject contentJson = (JSONObject) JSONValue.parse(content);
                    String key = (String) contentJson.get(CloudBridgeUtil.KEY_NAME_KEY);
                    Intent naviEndIntent = new Intent(Const.ACTION_WATCH_NAVI_END);
                    naviEndIntent.putExtra(CloudBridgeUtil.KEY_NAME_KEY, key);
                    naviEndIntent.putExtra(Const.KEY_WATCH_ID, eid);
                    sendBroadcast(naviEndIntent);
                    break;
                default:
                    break;

            }

            msg.setmType(noticeMsgType);

            NoticeMsgHisDAO.getInstance(getApplicationContext()).addNoticeMsg(family.getFamilyId(), mApp.getCurUser().getEid(), msg);

            mApp.setHasNewNoticeMsg(family.getFamilyId(), allMsgType, true);
            allMsgType = AllMessageAdapter.MESSAGE_TYPE_ALL;
            Intent intent = new Intent(Const.ACTION_RECEIVE_NOTICE_MSG);
            intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, allMsgType);
            sendBroadcast(intent);

            if (needNotification) {
                JSONObject noticeSetting = mApp.getNoticeSetting(watch.getEid());
                if (noticeSetting != null) {
                    String isNeedNotice;
                    if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOSLOC)) {
                        isNeedNotice = (String) noticeSetting.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS);
                    } else if (type.equals(CloudBridgeUtil.OFFlINE_MSG_TYPE_SECURITYAREA)) {
                        isNeedNotice = (String) noticeSetting.get(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE);
                    } else {
                        isNeedNotice = (String) noticeSetting.get(type);
                    }
                    if ("0".equals(isNeedNotice)) {
                        return;
                    }
                }
                mApp.recvMsgNotify(watch.getEid(), notificationTitle, notificationContent,
                        Const.TITLE_BAR_NEW_NOTICE_MESSAGE + allMsgType, msg.getmGroupid(), NoticeTypeActivity.class);

            }
        }
    }

    private void getVoiceMsg(String key, JSONObject content) {
        Boolean privateflag = true;
        key = key.substring(3);
        String sendtime = key.substring(key.indexOf('/') + 5); //5表示‘/MSG/’的字符数
        key = key.substring(0, key.indexOf('/'));
        for (FamilyData family : mApp.getCurUser().getFamilyList()) {
            if (key.equals(family.getFamilyId())) {//找到对应的familyid
                privateflag = false;
                String eid = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_EID);
                if (!eid.equals(mApp.getCurUser().getEid())) {
                    String type = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_TYPE);
                    if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_APPSTORE)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM)) {
                        String noticeContent;
                        if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS)) {
                            byte[] bytes = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                            noticeContent = StrUtil.Byte2Unicode(bytes, 0, bytes.length);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)) {
                            noticeContent = getString(R.string.device_upgrade_success);
                            int downloadCode = Integer.parseInt((String) content.get("Content"));
                            switch (downloadCode) {
                                case 1:
                                    noticeContent = getString(R.string.device_upgrade_success);
                                    break;
                                case 0:
                                    noticeContent = getString(R.string.device_upgrade_start);
                                    break;
                                case -1:
                                    noticeContent = getString(R.string.device_upgrade_ongoing);
                                    break;
                                case -2:
                                    noticeContent = getString(R.string.device_upgrade_battery_low);
                                    break;
                                case -3:
                                    noticeContent = getString(R.string.device_upgrade_info_error);
                                    break;
                                case -4:
                                    noticeContent = getString(R.string.device_upgrade_unnecessary);
                                    break;
                                case -5:
                                    noticeContent = getString(R.string.device_upgrade_info_error);
                                    break;
                            }
                        } else {
                            noticeContent = content.get("Content").toString();
                        }

                        handleNoticeMsg(family, TimeUtil.getOrderTime(sendtime), eid, type, noticeContent, true);
                        mApp.setNextContentKey(family, TimeUtil.timeInc(TimeUtil.getOrderTime(sendtime)));

                    } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD)
                            || type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)
                            || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                        ChatMsgEntity entity = new ChatMsgEntity();
                        if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS);
                            entity.setmForceRecordOk(2);
                            mApp.sdcardLog("sos receive record time=" + TimeUtil.getTimeStampLocal() + "line=" + LogUtil.getLineNumber());
                            mApp.setForceRecordState(eid, false);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_RECORD)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD);
                            entity.setmForceRecordOk(2);
                            mApp.sdcardLog("handleC2eResp receive force record " + LogUtil.getLineNumber());
                            mApp.setForceRecordState(eid, false);
                        } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE);
                            mApp.setForceTakePhotoState(eid, false);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO);
                        }
                        entity.setmDate(TimeUtil.getOrderTime(sendtime));
                        if (content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                            entity.setmDuration((Integer) content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
                        }
                        entity.setmSrcId(eid);
                        entity.setmIsFrom(true);
                        entity.setmPlayed(false);
                        entity.setmFamilyId(family.getFamilyId());
                        byte[] bitmapArray;
                        //去除重复消息标志
                        boolean flag = ChatHisDao.getInstance(getApplicationContext()).isMsgExist(family.getFamilyId(), entity.getmDate());

                        if (flag == false) {
                            try {
                                if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                    entity.setmPlayed(true);
                                    entity.setmAudioPath((String) content.get("Content"));
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE)) {
                                    entity.setmPlayed(true);
                                    bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                    File image = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getImageFileName());
                                    FileOutputStream out = new FileOutputStream(image);
                                    out.write(bitmapArray);
                                    out.close();
                                    entity.setmAudioPath(image.getPath());
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                                    entity.setmPlayed(true);
                                    entity.setmAudioPath((String) content.get("Content"));
                                    mApp.downloadNoticeVideo(eid, entity.getmAudioPath(), null);
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                    entity.setmPlayed(true);
                                    entity.setmAudioPath((String) content.get("Content"));
                                } else {
                                    bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                                    File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                                    FileOutputStream out = new FileOutputStream(voice);
                                    byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                                    out.write(tmp);
                                    out.close();
                                    entity.setmAudioPath(voice.getPath());
                                }
                                //修改sos消息存放列表和存放规则
                                if (mApp.getmSosStartFlag() == true) {
                                    for (String time : mApp.getmSosCollectList().keySet()) {
                                        HashMap<String, ArrayList<ChatMsgEntity>> curchatlist = mApp.getmSosCollectList()
                                                .get(time);
                                        if (curchatlist.get(family.getFamilyId()) != null) {
                                            mApp.sdcardLog("sos receive record add list. time=" + TimeUtil.getTimeStampLocal() + "line=" + LogUtil.getLineNumber());
                                            curchatlist.get(family.getFamilyId()).add(entity);
                                        }
                                    }
                                }
                                ChatHisDao.getInstance(getApplicationContext()).addChatMsg(family.getFamilyId(), entity);
                                mApp.setNextContentKey(family, TimeUtil.timeInc(TimeUtil.getOrderTime(sendtime)));
                                mApp.setHasNewGroupMsg(family.getFamilyId(), true);
                                //通知接受到语音消息
                                Intent intt = new Intent();
                                intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
                                sendBroadcast(intt);
                                sendBroadcast(new Intent(Constants.ACTION_RECEIVE_GROUP_MESSAGE_NOTIFY));

                                String watchName = family.getWatchlist().get(0).getNickname();
                                String notiContent = watchName + ":" + getString(R.string.voice);
                                if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                                    notiContent = watchName + ":" + entity.getmAudioPath();
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_IMAGE) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                                    notiContent = watchName + ":" + getString(R.string.image);
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                                    notiContent = watchName + ":" + getString(R.string.video);
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                                    notiContent = watchName + ":" + getString(R.string.emoji);
                                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE)) {
                                    notiContent = watchName + ":" + getString(R.string.ota_upgrade);
                                }
                                mApp.recvMsgNotify(family.getWatchlist().get(0).getEid(), getString(R.string.group_message, watchName),
                                        notiContent,
                                        Const.TITLE_BAR_NEW_GROUP_MESSAGE, family.getFamilyId(), GroupMessageActivity.class);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                mApp.sdcardException("getvoice", e);
                            }
                        }
                    }
                }
            }
        }
        if (privateflag) {
            ChatMsgEntity entity = new ChatMsgEntity();
            String type = (String) content.get(CloudBridgeUtil.E2C_PL_KEY_TYPE);
            if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE) ||
                    type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI) ||
                    type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT) ||
                    type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) ||
                    type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {

                if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE)) {
                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
                } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO);
                } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                    entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO);
                }
                entity.setmDate(TimeUtil.getOrderTime(sendtime));
                if (content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION) != null) {
                    entity.setmDuration((Integer) content.get(CloudBridgeUtil.E2C_PL_KEY_DURATION));
                }
                entity.setmSrcId((String) content.get(CloudBridgeUtil.E2C_PL_KEY_EID));
                if (entity.getmSrcId() != null && !entity.getmSrcId().equals(mApp.getCurUser().getEid())) {
                    entity.setmIsFrom(true);
                } else {
                    entity.setmIsFrom(false);
                    entity.setmSended(1);
                }
                entity.setmPlayed(false);
                entity.setmFamilyId(key);
                byte[] bitmapArray;
                //去除重复消息标志
                boolean flag = ChatHisDao.getInstance(mApp.getApplicationContext()).isMsgExist(key, entity.getmDate());
                if (flag == false) {
                    try {
                        if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                            entity.setmPlayed(true);
                            entity.setmAudioPath((String) content.get("Content"));
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                            entity.setmPlayed(true);
                            entity.setmAudioPath((String) content.get("Content"));
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO) || type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                            entity.setmPlayed(true);
                            entity.setmAudioPath((String) content.get("Content"));
                            mApp.downloadNoticeVideo(entity.getmSrcId(), entity.getmAudioPath(), null);
                        } else {
                            bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
                            File voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                            FileOutputStream out = new FileOutputStream(voice);

                            byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                            out.write(tmp);
                            out.close();
                            entity.setmAudioPath(voice.getPath());
                        }

                        ChatHisDao.getInstance(getApplicationContext()).addChatMsg(key, entity);
                        mApp.setNextPrivateChatKey(mApp.getCurUser().getEid(), TimeUtil.timeInc(TimeUtil.getOrderTime(sendtime)));
                        mApp.setHasNewPrivateMsg(key, true);
                        //通知接受到语音消息
                        Intent intt = new Intent();
                        intt.setAction(Const.ACTION_PROCESSED_NOTIFY_OK);
                        sendBroadcast(intt);
                        sendBroadcast(new Intent(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY));

                        WatchData watch = mApp.getCurUser().queryWatchDataByEid(entity.getmSrcId());
                        String notiContent = watch.getNickname() + ":" + getString(R.string.voice);
                        if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI)) {
                            notiContent = watch.getNickname() + ":" + getString(R.string.emoji);
                        } else if (type.equals(CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT)) {
                            notiContent = watch.getNickname() + ":" + entity.getmAudioPath();
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
                            notiContent = watch.getNickname() + ":" + getString(R.string.image);
                        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
                            notiContent = watch.getNickname() + ":" + getString(R.string.video);
                        }
                        mApp.recvMsgNotify(watch.getEid(), watch.getNickname(),
                                notiContent,
                                Const.TITLE_BAR_NEW_PRIVATE_MESSAGE, key, PrivateMessageActivity.class);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void c2eGetMessage(String key) {
        MyMsgData c2e = new MyMsgData();
        c2e.setTimeout(60 * 1000);
        c2e.setCallback(mBgMsgNetCallback);
        JSONObject pl = new JSONObject();
        pl.put("Key", key);
        c2e.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_C2E_GET_MESSAGE, pl));
        sendNetMsg(c2e);
    }

    private boolean isManualPingSuccess = false;
    private boolean isManualPinging = false;
    private static final int MANUAL_PING_TIMEOUT = 10000;

    public void manualPing() {
        if (isManualPinging) {
            return;
        }
        new MioAsyncTask<String, Void, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    isManualPingSuccess = false;
                    isManualPinging = true;
                    mApp.sdcardLog("NetService websocketEndpoint manualPing,time=" + TimeUtil.getTimeStampLocal());
                    websocketEndpoint.ping();
                    Thread.sleep(MANUAL_PING_TIMEOUT);
                    return true;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;

            }

            protected void onCancelled() {
                super.onCancelled();
                isManualPinging = false;
            }

            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                try {
                    if (!isManualPingSuccess) {
                        closeWebSocket();
                        //startCloudBridgeClient();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                isManualPinging = false;
            }
        }.execute();

    }

    private void resendAllMessage() {
        if (mMsgQueue.size() > 0) {
            for (MyMsgData msg : mMsgQueue) {
                if (msg.getNeedNetTimeout()) {
                    myHandler.removeMessages(Const.MSG_RESPONSE_NETWORK_OR_WEBSOCKET_ERROR);
                    msg.resetTimeout();
                    msg.setNeedNetTimeout(false);
                    if (msg.getReqMsg().get(CloudBridgeUtil.KEY_NAME_SID) != null) {
                        msg.getReqMsg().put(CloudBridgeUtil.KEY_NAME_SID, mApp.getToken());
                    }
                    mApp.sdcardLog("Resend message: " + msg.reqMsg.toString());
                    LogUtil.e("Resendmsg = " + msg.reqMsg.toJSONString());
                    sendWebsocketEndpointMsg(msg.reqMsg.toJSONString());
                }
            }
        }
    }

    private long getContactIdByNumber(String number) {
        if (number != null && number.trim().length() > 0) {
            Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            long contactId = 0;
            while (cursor.moveToNext()) {
                String num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (num != null && num.length() > 0 && num.replaceAll("-", "").replaceAll(" ", "").endsWith(number)) {
                    try {
                        contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (contactId > 0) {
                        break;
                    }
                }
            }
            cursor.close();
            return contactId;
        } else {
            return 0;
        }
    }

    private void addContactNumber(long contactId, String number) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void addContactName(long contactId, String name) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void addContactPhoto(long contactId, Bitmap photo) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytePhoto = outputStream.toByteArray();
            outputStream.close();

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bytePhoto);
            values.put(ContactsContract.Data.IS_PRIMARY, 1);
            this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContactNote(long contactId, String note) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
        this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void addNewContact(Bitmap photo, String name, String number, String note) {
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(this.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values));
        addContactPhoto(contactId, photo);
        addContactName(contactId, name);
        addContactNumber(contactId, number);
        addContactNote(contactId, this.getResources().getString(R.string.app_name) + note);
    }

    private void addContact(String number, String name, Bitmap photo, String note) {
        if (number == null || number.length() == 0) return;
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        if (!PermissionUtils.hasPermissions(this, permissions)) {
            return;
        }
        long contactId = getContactIdByNumber(number);
        if (contactId > 0) {
            return;
        }

        String md5String = MD5.md5_string(name + number);
        Boolean isAdded = mApp.getBoolValue(md5String, false);
        if (isAdded)
            return;
        else {
            mApp.setValue(md5String, true);
            addNewContact(photo, name, number, note);
        }
    }

    private Bitmap getHeadIcon(WatchData watchData) {
        Drawable drawable = mApp.getHeadDrawableByFile(this.getResources(), watchData.getHeadPath(), watchData.getEid(), R.drawable.default_head);
        Bitmap headIcon = ((BitmapDrawable) drawable).getBitmap();
        return headIcon;
    }

    private void addWatchContact(String eid) {
        new MioAsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                String watchEid = params[0];
                if (watchEid != null && watchEid.length() > 0) {
                    for (WatchData watchData : mApp.getCurUser().getWatchList()) {
                        if (watchData.getEid().equals(watchEid)) {
                            {
                                if (watchData.getCellNum() != null && watchData.getCellNum().length() > 0) {
                                    addContact(watchData.getCellNum(), watchData.getNickname(), getHeadIcon(watchData), "");
                                }
                            }
                        }
                    }
                } else {
                    for (WatchData watchData : mApp.getCurUser().getWatchList()) {
                        {
                            if (watchData.getCellNum() != null && watchData.getCellNum().length() > 0) {
                                addContact(watchData.getCellNum(), watchData.getNickname(), getHeadIcon(watchData), "");
                            }
                        }
                    }
                }
                return null;
            }
        }.execute(eid);

    }

    public boolean isPermissionDedied() {
        return isPermissionDedied;
    }

    //更新存储防打扰时段数据
    List<SilenceTime> silencelist = new ArrayList<SilenceTime>();
    SilenceTime silenceTimeMor;
    SilenceTime silenceTimeAft;

    public void updateSilenceTimeData(String eid, String jstr) {
        try {
            if (jstr != null && jstr.length() > 0) {
                silencelist.clear();
                JSONArray array = (JSONArray) JSONValue.parse(jstr);
                dealWithLocalData(eid, array.size());
                dealWithMapGetData(eid, array);
                saveListToLocalNew(eid, silencelist);

                Intent intent = new Intent(Const.ACTION_RECEIVE_SILENCETIME_UPDATE);
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealWithLocalData(String eid, int size) throws Exception {
        String silenceDataStr = mApp.getStringValue(eid + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, "");
        if (!silenceDataStr.equals("") && !silenceDataStr.equals("[]")) {
            JSONArray array = (JSONArray) JSONValue.parse(silenceDataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                SilenceTime tmpSilenceTime = new SilenceTime();
                String timeId = (String) jsonObj.get(CloudBridgeUtil.TIMESTAMPID);
                if (Const.SILENCE_TIME_MORNING_TIMEID.equals(timeId) && "0".equals(jsonObj.get(CloudBridgeUtil.ONOFF))) {
                    silenceTimeMor = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                    size--;
                } else if (Const.SILENCE_TIME_AFTERNOON_TIMEID.equals(timeId) && "0".equals(jsonObj.get(CloudBridgeUtil.ONOFF))) {
                    silenceTimeAft = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                    size--;
                } else {
                    tmpSilenceTime = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                    if (tmpSilenceTime.onoff.equals("0"))
                        silencelist.add(tmpSilenceTime);
                }
            }
        }

    }

    private void dealWithMapGetData(String eid, JSONArray arary) throws Exception {
        for (int i = 0; i < arary.size(); i++) {
            JSONObject jsonObj = (JSONObject) arary.get(i);
            SilenceTime tmpSilenceTime = new SilenceTime();
            String timeId = (String) jsonObj.get(CloudBridgeUtil.TIMESTAMPID);
            if (Const.SILENCE_TIME_MORNING_TIMEID.equals(timeId)) {
                silenceTimeMor = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
            } else if (Const.SILENCE_TIME_AFTERNOON_TIMEID.equals(timeId)) {
                silenceTimeAft = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
            } else {
                tmpSilenceTime = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                silencelist.add(tmpSilenceTime);
            }
        }
    }

    private void saveListToLocalNew(String eid, List<SilenceTime> list) {

        JSONArray plA = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject plO = new JSONObject();
            SilenceTime silenceTime = list.get(i);
            plO = SilenceTime.toJsonObjectFromSilenceTimeBean(plO, silenceTime);
            plA.add(plO);
        }
        if (silenceTimeMor != null)
            plA.add(SilenceTime.toJsonObjectFromSilenceTimeBean(new JSONObject(), silenceTimeMor));
        if (silenceTimeAft != null)
            plA.add(SilenceTime.toJsonObjectFromSilenceTimeBean(new JSONObject(), silenceTimeAft));
        mApp.setValue(eid + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, plA.toJSONString());
        silenceTimeMor = null;
        silenceTimeAft = null;
        list.clear();
    }

    //更新存储休眠时段数据
    public void updateSleepListData(String eid, String jstr) {
        try {
            if (jstr != null && jstr.length() > 0 && !jstr.equals("[]")) {
                Object obj = JSONValue.parse(jstr);
                JSONObject jsonObj = (JSONObject) obj;
                if (!ifFlushLocalSleepListData(eid, jsonObj))
                    return;
                JSONArray plA = new JSONArray();
                plA.add(jsonObj);
                mApp.setValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, plA.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ifFlushLocalSleepListData(String eid, JSONObject serverData) throws Exception {
        String serverOnoff = (String) serverData.get(CloudBridgeUtil.ONOFF);
        String localData = mApp.getStringValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
        if (localData.equals("") || localData.equals("[]"))
            return true;
        JSONArray array = (JSONArray) JSONValue.parse(localData);
        JSONObject jsonObj = (JSONObject) array.get(0);
        String localOnoff = (String) jsonObj.get(CloudBridgeUtil.ONOFF);
        return "1".equals(serverOnoff) || "1".equals(localOnoff);
    }

    //更新设备wifi连接状态
    private void updateWlanStateData(String eid, String jstr) {

        if (jstr != null && jstr.length() > 0 && !jstr.equals("[]")) {
            JSONArray arrayWlan = (JSONArray) JSONValue.parse(jstr);
            JSONObject jsWLan = (JSONObject) arrayWlan.get(0);
            String wlanStatus = "";
            if (jsWLan != null && jsWLan.containsKey(CloudBridgeUtil.KEY_DEVICE_WLAN_STATUS))
                wlanStatus = (String) jsWLan.get(CloudBridgeUtil.KEY_DEVICE_WLAN_STATUS);
            String ssid = (String) jsWLan.get(CloudBridgeUtil.KEY_DEVICE_WLAN_WIFI_SSID);

            Intent it = new Intent(Const.ACTION_RECEIVE_DEVICE_WLAN_STATE);
            it.putExtra("eid", eid);
            it.putExtra(CloudBridgeUtil.KEY_DEVICE_WLAN_WIFI_SSID, ssid);
            it.putExtra(CloudBridgeUtil.KEY_DEVICE_WLAN_STATUS, wlanStatus);
            sendBroadcast(it);
        }
    }

    // 更新移动数据设置状态
    public void updateOfflineMode(String eid, String jstr) {
        if (jstr != null && jstr.length() > 0) {
            JSONObject offlineModeJson = (JSONObject) JSONValue.parse(jstr);
            int mode = Integer.parseInt((String) offlineModeJson.get(CloudBridgeUtil.MODE_VALUE));
            mApp.setValue(eid + CloudBridgeUtil.OFFLINE_MODE_VALUE, mode);
        }
    }

    public void sendDeviceGet(String eid) {
        MyMsgData msg = new MyMsgData();
        msg.setCallback(this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        msg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_GET, pl));
        sendNetMsg(msg);
    }

    public void sendDeviceGet(String eid, MsgCallback callback) {
        MyMsgData msg = new MyMsgData();
        msg.setCallback(callback);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        msg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_GET, pl));
        sendNetMsg(msg);
    }

    /**
     * 类名称：StepsActivity
     * 创建人：zhangjun5
     * 创建时间：2016/2/25 20:06
     * 方法描述：发送e2e消息，通知手表上传计步数据mapset到服务器
     */
    public void sendStepsReqE2eMsg() {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(null);
        JSONObject pl = new JSONObject();
        String[] eid = new String[1];
        eid[0] = mApp.getCurUser().getFocusWatch().getEid();
        String timestamp = TimeUtil.getTimestampCHN();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_STEPS_SUM);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timestamp);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, eid, pl));

        sendNetMsg(e2e);
    }

    public void sendDeviceSet(String eid, String key, Object value) {
        MyMsgData setMsg = new MyMsgData();
        setMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        setMsg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));
        sendNetMsg(setMsg);
    }

    private void sendDeviceHeadPath(WatchData watch) {
        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, watch.getCustomData().toJsonStr());
        relationMsg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));
        sendNetMsg(relationMsg);
    }

    public int sendDevicePhoneChange(WatchData mWatch, String mDeviceNumber) {
        int sn;
        MyMsgData setMsg = new MyMsgData();
        setMsg.setCallback(this);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mWatch.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_UPDATE_SIM_CARD_INFO);
        pl.put(CloudBridgeUtil.KEY_NAME_SIM_NO, mDeviceNumber);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mWatch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_REMOVEOLD, 0);//app 设置号码不传iccid，只设置号码
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        setMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SIM_CHANGE,
                sn, mApp.getToken(), pl));
        sendNetMsg(setMsg);
        return sn;
    }

    private void sendUserHeadPath(UserData user) {
        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, user.getCustomData().toJsonStr());
        relationMsg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));
        sendNetMsg(relationMsg);
    }

    private int sendHeadImageE2cType;
    private int sendHeadImageE2cSn;

    // type 1,设备头像上传  2，用户头像上传
    public int sendHeadImageE2c(int type, String eid, byte[] mapBytes) {
        MyMsgData e2c = new MyMsgData();
        int sn;
        e2c.setCallback(this);
        e2c.setTimeout(1000 * 60);
        JSONObject pl = new JSONObject();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_HEADIMG);
        JSONObject chat = new JSONObject();
        chat.put(CloudBridgeUtil.HEAD_IMAGE_DATA, Base64.encodeToString(mapBytes, Base64.NO_WRAP));
        pl.put(key.toString(), chat);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C_UP,
                sn, mApp.getToken(), pl));
        sendNetMsg(e2c);
        mApp.sdcardLog("startPhotoZoom sendHeadImageE2c mapBytes.size:" + mapBytes.length);
        sendHeadImageE2cType = type;
        sendHeadImageE2cSn = sn;
        return sn;
    }

    public void sendUserOrDeviceSetChangeE2G(String eid, String gid, int subaction) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subaction);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        e2e.setReqMsg(CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), mApp.getToken(), null, gid, pl));
        sendNetMsg(e2e);
    }

    private String certiSimNo;
    private int certiAddType;

    public void setCertiSimNo(String no) {
        certiSimNo = no;
    }

    public void setCertiAddType(int addType) {
        certiAddType = addType;
    }

    public int sendCertiStatMsg(int action) {
        MyMsgData mapset = new MyMsgData();
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put("Simno", certiSimNo);
        pl.put("Xiaomiid", Integer.valueOf(mApp.getCurUser().getXiaomiId()).toString());
        pl.put("Addtype", certiAddType);
        pl.put("Action", action);
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_CERTI_SIM_STAT, sn, mApp.getToken(), pl));
        sendNetMsg(mapset);
        return sn;
    }

    public int sendMapSetMsg(String eid, String gid, String key, String value, MsgCallback callback) {
        MyMsgData mapset = new MyMsgData();
        mapset.setCallback(callback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, eid);
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, gid);
        StringBuffer sms = new StringBuffer();
        sms.append("<" + sn + "," + mApp.getCurUser().getEid() + "," + "E501" + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        sendNetMsg(mapset);
        return sn;
    }

    public int sendMapSetMsg(String eid, String gid, int sn, String key, String value, MsgCallback callback) {
        MyMsgData mapset = new MyMsgData();
        mapset.setCallback(callback);
        JSONObject pl = new JSONObject();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, eid);
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, gid);
        StringBuffer sms = new StringBuffer();
        sms.append("<" + sn + "," + mApp.getCurUser().getEid() + "," + "E501" + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        sendNetMsg(mapset);
        return sn;
    }

    public int sendMapGetMsg(String eid, String key, MsgCallback callback) {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(callback);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mapget.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET, sn, mApp.getToken(), pl));
        sendNetMsg(mapget);
        return sn;
    }

    public void sendMapMGetMsg(String eid, String[] keys, MsgCallback callback) {
        MyMsgData mapmget = new MyMsgData();
        mapmget.setCallback(callback);

        JSONArray plKeyList = new JSONArray();
        for (String key : keys) {
            if ("".equals(key) || key == null) {
            } else {
                plKeyList.add(key);
            }
        }
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapmget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        sendNetMsg(mapmget);
    }

    public int sendE2EMsg(String eid, int actionValue, int timeout, boolean netTimeout, MsgCallback callback) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(callback);
        //set msg info
        e2e.setTimeout(timeout);
        e2e.setFinalTimeout(timeout);
        e2e.setNeedNetTimeout(netTimeout);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, actionValue);

        String[] teid;
        teid = new String[1];
        teid[0] = eid;

        StringBuffer sms = new StringBuffer();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        sms.append("<" + sn + "," + mApp.getCurUser().getEid() + "," + "E" + actionValue + "," + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        LogUtil.d("Sms = " + sms.toString());

        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, teid, pl));
        sendNetMsg(e2e);
        return sn;
    }

    // 该方法重载主要是因为有些e2e消息PL里面需要封装内容
    public int sendE2EMsg(String eid, int sn, JSONObject pl, int timeout, boolean netTimeout, MsgCallback callback) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(callback);
        e2e.setTimeout(timeout);
        e2e.setFinalTimeout(timeout);
        e2e.setNeedNetTimeout(netTimeout);
        String[] teid;
        teid = new String[1];
        teid[0] = eid;
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, teid, pl));
        sendNetMsg(e2e);
        return sn;
    }

    public int sendE2GMsg(String familyid, int sn, JSONObject pl, int timeout, boolean netTimeout, MsgCallback callback) {
        MyMsgData e2g = new MyMsgData();
        e2g.setCallback(callback);
        e2g.setTimeout(timeout);
        e2g.setFinalTimeout(timeout);
        e2g.setNeedNetTimeout(netTimeout);
        e2g.setReqMsg(
                CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP, sn, mApp.getToken(), null, familyid, pl));
        sendNetMsg(e2g);
        return sn;
    }

    public void sendMMapMGetMsg(String[] eids, String[] keys, MsgCallback callback) {
        MyMsgData mapmget = new MyMsgData();
        mapmget.setCallback(callback);

        JSONArray plKeyList = new JSONArray();
        for (String key : keys) {
            if ("".equals(key) || key == null) {
            } else {
                plKeyList.add(key);
            }
        }

        JSONArray eidList = new JSONArray();
        for (String eid : eids) {
            if ("".equals(eid) || eid == null) {
            } else {
                eidList.add(eid);
            }
        }
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EIDS, eidList);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapmget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        sendNetMsg(mapmget);
    }

    /**
     * 发送e2e消息给4G手表进行升级。app端只是触发升级操作。
     */
    public void sendWatchUpdateE2eMsg(String eid) {
        sendE2EMsg(eid, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_UPDATE, 30, true, mBgMsgNetCallback);
    }

    /**
     * user:zhangjun5 time:15:55 date:2017/5/2
     * desc:批量删除讲故事接口
     **/
    public void sendMultSelectOperationToWatch(int status, int operation, JSONArray snList, WatchData watch, MsgCallback msgCallback) {
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_GID, watch.getFamilyId());
        pl.put("status", status);
        pl.put("optype", operation);
        pl.put("fileList", snList.toString());
        msgData.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_WATCH_MULT_SELECT, sn, mApp.getToken(), pl));
        sendNetMsg(msgData);
    }

    /**
     * 设置定位方式
     * 仅管理员、部分手表项目生效，由服务器端控制
     * type:0为高德 1为百度
     */
    public void setWatchLocationType(int type, MsgCallback msgCallback) {
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_LOCATION_MAPTYPE, Integer.toString(type));
        msgData.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_SET_LOCATION_TYPE, sn, mApp.getToken(), pl));
        sendNetMsg(msgData);
    }

    public void getWatchDownloadList(String eid, String gid, MsgCallback msgCallback) {
        if (mApp.getCurUser() != null && mApp.getCurUser().getFocusWatch() != null
                && (mApp.getCurUser().getFocusWatch().isDevice502() || mApp.getCurUser().getFocusWatch().isDevice701()
                || mApp.getCurUser().getFocusWatch().isDevice305()
                || mApp.getCurUser().getFocusWatch().isDevice710())
                || mApp.getCurUser().getFocusWatch().isDevice705()) {
            return;
        }
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        msgData.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_WATCH_DOWNLOAD_LIST, sn, mApp.getToken(), pl));
        sendNetMsg(msgData);
    }

    public int getHighPowerApplist(String eid, MsgCallback callback) {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(callback);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mapget.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_FUNCTION_HIGHPOWER_STATE, sn, mApp.getToken(), pl));
        sendNetMsg(mapget);
        return sn;
    }

    public void sendTakePhotoMsg(final String eid, MsgCallback callback) {
        MyMsgData e2e = new MyMsgData();
        e2e.setTimeout(55 * 1000);
        e2e.setCallback(callback);
        String[] teid = new String[1];
        teid[0] = eid;
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FORCE_TAKE_PHOTO);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        StringBuffer sms = new StringBuffer();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        sms.append("<" + sn + "," + mApp.getCurUser().getEid() + "," + "E" + CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FORCE_TAKE_PHOTO + "," + "0>");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        LogUtil.d("Sms = " + sms.toString());
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, teid, pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(e2e);
        }
    }

    public void sendDeviceListenMsg(String eid, String num, int type, MsgCallback callback) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(callback);
        e2e.setTimeout(60000);
        JSONObject pl = new JSONObject();
        String[] eids = new String[1];
        eids[0] = eid;
        String timestamp = TimeUtil.getTimeStampGMT();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DEVICE_LISTEN);
        pl.put(CloudBridgeUtil.KEY_DEVICE_LISTEN_PHONENUM, num);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eids[0]);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timestamp);
        pl.put(CloudBridgeUtil.KEY_DEVICE_LISTEN_TYPE, type);  //1  显示回拨  2静默回拨
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, eids, pl));
        sendNetMsg(e2e);
    }

    public void sendGetContactReq(String eid) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_CONTACT_REQ,
                sn, mApp.getToken(), pl));
        sendNetMsg(req);
    }

    public void getPhoneWhiteListByMapget(String eid) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.PHONE_WHITE_LIST);
        queryGroupsMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        mApp.getToken(), pl));
        sendNetMsg(queryGroupsMsg);
    }

    private void sendQueryAllGroups(final String curWatchEid, final int sdt) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    mApp.parseJSONObjectGroups(CloudBridgeUtil.getCloudMsgPLArray(respMsg));
                    sendBroadcast(new Intent(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW));
                    mApp.setIsNeedInvalidFamilyDialog(false);
                    mApp.setValue(Const.SHARE_PREF_CURRENT_USER_REFLECT_ID, mApp.getCurUser().getUid());

                    mApp.setAdminBindFlag(false);
                    WatchData watch = mApp.getCurUser().queryWatchDataByEid(curWatchEid);
                    mApp.setFocusWatch(watch);
                    mApp.initMapType();
                    DialogUtil.ShowCustomSingleTopSystemDialog(getApplicationContext(),
                            getString(R.string.set_member_info),
                            getString(R.string.set_member_detail, watch.getNickname()),
                            null,
                            null,
                            new DialogUtil.OnCustomDialogListener() {

                                @Override
                                public void onClick(View v) {
                                    if (sdt == 0) {
                                        Intent intent2 = new Intent(getApplicationContext(), WatchFirstSetActivity.class);
                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent2.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);
                                        startActivity(intent2);
                                        mApp.setAdminBindFlag(true);
                                    } else {

                                        String memberEid = mApp.getCurUser().getEid();
                                        if (memberEid == null) {
                                            memberEid = mApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                                            if (memberEid != null)
                                                mApp.getCurUser().setEid(memberEid);
                                        }
                                        Intent intent = new Intent(getApplicationContext(), AddCallMemberActivity.class);
                                        intent.putExtra(Const.KEY_WATCH_ID, curWatchEid);
                                        intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 0);
                                        intent.putExtra("eid", memberEid);
                                        intent.putExtra(Const.SET_CONTACT_ISBIND, true);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        mApp.setAdminBindFlag(false);
                                    }
                                }
                            },
                            getText(R.string.confirm).toString());

                }
            }
        });
        JSONObject param = new JSONObject();
        queryGroupsMsg.setReqMsg(mApp.obtainCloudMsgContentWithParam(CloudBridgeUtil.CID_QUERY_MYGROUPS, null, param));

        sendNetMsg(queryGroupsMsg);
    }

    public void handleBindRequest(String request) {
        if (request != null) {
            final JSONObject jo = (JSONObject) JSONValue.parse(request);
            JSONObject respPl = CloudBridgeUtil.getCloudMsgPL(jo);
            final int sn = CloudBridgeUtil.getCloudMsgSN(jo);
            final String nick = (String) respPl.get(CloudBridgeUtil.KEY_NAME_NICKNAME);
            final String eid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_EID);
            final String timestamp = (String) respPl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
            final String gid = (String) respPl.get(CloudBridgeUtil.KEY_NAME_GID);

            FamilyData family = mApp.getCurUser().queryFamilyByGid(gid);
            if (family == null) {
                return;
            }

            final long lastReqTime = System.currentTimeMillis();
            if (lastAskJoinTag != null && lastAskJoinTag.equals(eid + gid + timestamp)) {//防止重复提示
                return;
            } else {
                lastAskJoinTag = eid + gid + timestamp;
            }
            //增加ack
            sendAskJoinResultNoPl(CloudBridgeUtil.RC_ACK, sn, eid, jo);//
            //add dialog
            String joinRequest = getString(R.string.join_family_request, nick, family.getFamilyName(), TimeUtil.getReqTime(timestamp));
            final WatchData watch = family.getWatchlist().get(0);
            DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                    getString(R.string.new_member_join_request),
                    joinRequest,

                    new OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            if (watch.isDevice102()) {
                                if (System.currentTimeMillis() - lastReqTime < 50000) {
                                    sendAskJoinResult(0, sn, eid, jo);
                                } else {
                                    ToastUtil.showMyToast(mApp, getString(R.string.request_timeout),
                                            Toast.LENGTH_LONG);
                                }
                            } else {
                                sendAskJoinResult(0, sn, eid, jo);
                            }
                            lastAskJoinTag = null;
                        }
                    },
                    getText(R.string.bind_ask_ignore).toString(),
                    new OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            if (watch.isDevice102()) {
                                if (System.currentTimeMillis() - lastReqTime < 50000) {
                                    sendAskJoinResult(1, sn, eid, jo);
                                } else {
                                    ToastUtil.showMyToast(mApp, getString(R.string.request_timeout),
                                            Toast.LENGTH_LONG);
                                }
                            } else {
                                sendAskJoinResult(1, sn, eid, jo);
                            }
                            lastAskJoinTag = null;
                        }
                    },
                    getText(R.string.bind_ask_accept).toString());
            mApp.recvMsgNotify(getString(R.string.new_member_join_request), joinRequest, Const.TITLE_BAR_ADD_FAMILY, NewMainActivity.class, true);
        }
    }

    public void handleNewFriendRequest(String request) {

        final JSONObject jo = (JSONObject) JSONValue.parse(request);
        if (request != null) {
            final JSONObject respPL = CloudBridgeUtil.getCloudMsgPL(jo);
            final int sn = CloudBridgeUtil.getCloudMsgSN(jo);
            final String eid = CloudBridgeUtil.getCloudMsgSEID(jo);
            final String fAdminName = (String) respPL.get("FadminName");
            final String fAdminEid = (String) respPL.get("FadminEid");
            final String fImei = (String) respPL.get("Imei");
            final String fDeviceName = (String) respPL.get("FdeviceName");
            final String fDeviceEid = (String) respPL.get("FdeviceEid");
            final String deviceEid = (String) respPL.get("deviceEid");
            final String deviceName = (String) respPL.get("deviceName");

            String builder = getString(R.string.watch_add_friend, fDeviceName, deviceName);
            DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                    getString(R.string.new_friends_join_request),
                    builder,
                    new OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                            MyMsgData responseMsg = new MyMsgData();
                            responseMsg.setCallback(mBgMsgNetCallback);
                            JSONObject newPl = new JSONObject();
                            String[] addGroupTeids = new String[1];
                            addGroupTeids[0] = fAdminEid;
                            //newPl.put(CloudBridgeUtil.KEY_NAME_RC, 1);
                            respPL.put(CloudBridgeUtil.KEY_NAME_RC, -156);
                            newPl.put(CloudBridgeUtil.KEY_NAME_PL, respPL);
                            newPl.put(CloudBridgeUtil.KEY_NAME_TEID, addGroupTeids);
                            newPl.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_E2E_UP);
                            newPl.put(CloudBridgeUtil.KEY_NAME_SID, mApp.getToken());
                            newPl.put(CloudBridgeUtil.KEY_NAME_SN, sn);
                            responseMsg.setReqMsg(newPl);
                            sendNetMsg(responseMsg);
                        }
                    },
                    getText(R.string.bind_ask_ignore).toString(),
                    new OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                            MyMsgData responseMsg = new MyMsgData();
                            responseMsg.setCallback(mBgMsgNetCallback);
                            JSONObject newPl = new JSONObject();
                            String[] addGroupTeids = new String[1];
                            addGroupTeids[0] = fAdminEid;
                            //newPl.put(CloudBridgeUtil.KEY_NAME_RC, 1);
                            respPL.put(CloudBridgeUtil.KEY_NAME_RC, 1);
                            newPl.put(CloudBridgeUtil.KEY_NAME_PL, respPL);
                            newPl.put(CloudBridgeUtil.KEY_NAME_TEID, addGroupTeids);
                            newPl.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_E2E_UP);
                            newPl.put(CloudBridgeUtil.KEY_NAME_SID, mApp.getToken());
                            newPl.put(CloudBridgeUtil.KEY_NAME_SN, sn);
                            responseMsg.setReqMsg(newPl);
                            sendNetMsg(responseMsg);
                        }
                    },
                    getText(R.string.bind_ask_accept).toString());
        }
    }

    public String getAESKEY() {
        if (AES_KEY == null || AES_KEY.length() == 0) {
            AES_KEY = mApp.getStringValue(Const.SHARE_PREF_AES_KEY, "");
        }
        return AES_KEY;
    }

    private void setNoticeOnOff(final String openId, final String name, final String devId, final int onoff) {
        JSONObject obj = new JSONObject();
        obj.put("DeviceId", devId);
        obj.put("OpenId", openId);
        obj.put("DeviceNickName", name);
        obj.put("OuthCode", onoff);
        ListDownLoader downLoader = new ListDownLoader(new DownloadListener() {
            @Override
            public void onStartDownload() {

            }

            @Override
            public void onFinished(String result) {
                if (result == null || result.equals("") || result.length() < 4) {
                    Log.e("xxxx", "no result!");
                    return;
                }
                JSONObject obj = (JSONObject) JSONValue.parse(result);
                int rc = (Integer) obj.get("RC");
                Log.e("xxxx", "SUB_ACTION_WECHAT_NOTICE_BIND rc = " + rc);
                if (rc == 1) {

                } else {
                    ToastUtil.showMyToast(getApplicationContext(), getString(R.string.set_error), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(String cause) {
                ToastUtil.showMyToast(getApplicationContext(), cause, Toast.LENGTH_LONG);
            }
        });
        downLoader.HttpsDownloadList(URLSETStringLink(), obj.toJSONString());
    }

    private String URLSETStringLink() {
        long mills = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyymmddHHssSSS");
        Date d = new Date(mills);
        String updatetime = format.format(d);
        String sn = RandomStringCreat(14);
        String sign = StrUtil.md5("94CECB85AE17BB85C56FFA91FE33F6E0" + sn + updatetime);

        return FunctionUrl.WECHAT_INFORM_URL + "updateTime=" + updatetime + "&sn=" + sn + "&sign=" + sign;
    }

    private String RandomStringCreat(int lenth) {
        String ku = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder newStr = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < lenth; i++) {
            int r2 = r.nextInt(ku.length());
            newStr.append(ku.charAt(r2));
        }
        return newStr.toString();
    }

    public void getNoticeSetting(MsgCallback callback) {
        MyMsgData noticeSetting = new MyMsgData();
        if (callback != null) {
            noticeSetting.setCallback(callback);
        } else {
            noticeSetting.setCallback(this);
        }
        JSONArray eids = new JSONArray();
        for (WatchData watch : mApp.getCurUser().getWatchList()) {
            eids.add(watch.getEid());
        }
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_DEVICES, eids);

        noticeSetting.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_NOTICE_SETTING,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        mApp.getToken(), pl));
        sendNetMsg(noticeSetting);
    }

    public void setNoticeSetting(String eid, JSONObject noticeSetting, MsgCallback callback) {
        MyMsgData noticeSettingMsg = new MyMsgData();
        if (callback != null) {
            noticeSettingMsg.setCallback(callback);
        } else {
            noticeSettingMsg.setCallback(this);
        }
        noticeSetting.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        JSONArray devices = new JSONArray();
        devices.add(noticeSetting);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_DEVICES, devices);

        noticeSettingMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_SET_NOTICE_SETTING,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        mApp.getToken(), pl));
        sendNetMsg(noticeSettingMsg);
    }

    /*----------------------------- video call start ---------------------------------------------*/

    // receive request
    private void receiveVideoCallReq(String reqMsg) {

        JSONObject pl = (JSONObject) JSONValue.parse(reqMsg);
        if (pl != null && pl.containsKey(CloudBridgeUtil.KEY_NAME_SUB_ACTION)) {
            int subAction = (int) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
            if (subAction == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL) {
                int receiveSn = (int) pl.get(CloudBridgeUtil.KEY_NAME_SN);
                String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_SEID);
//                mApp.setFocusWatch(mApp.getCurUser().queryWatchDataByEid(eid));

                if (mApp.callState == Const.MESSAGE_CALL_INIT_STATE) {
                    if (mApp.videoCallSn == receiveSn)
                        return;
                    else
                        mApp.videoCallSn = receiveSn;
                    mApp.videoCallEid = eid;
                    LogUtil.e("NetSetvice -- receiveVideoCallReq  startVideoCallActivity  eid = " + eid + ", pl = " + pl.toJSONString());
                    startVideoCallActivity(eid, pl.toJSONString());
                } else if (!eid.equals(mApp.videoCallEid)) {
                    LogUtil.e("NetSetvice -- receiveVideoCallReq  sendVideoCallResp  eid = " + eid + ", receiveSn = " + receiveSn);
                    sendJuphoonVideoCallResp(eid, receiveSn, CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INVIDEOCALL,null,null);
                }
            } else if (subAction == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL_END) {
                int receiveSn = (int) pl.get(CloudBridgeUtil.KEY_NAME_SN);
                if (mApp.videoCallSn != receiveSn)
                    return;
                NetService.this.sendBroadcast(new Intent(Const.ACTION_VIDEOCALL_ENDCALL));
            }
        }
    }

    public void sendVideoCallResp(String teid, int sn, int result) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(null);  //resp不关心回执
        e2e.setNeedNetTimeout(false);
        String[] teidArray = new String[1];
        teidArray[0] = teid;
        // PL
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESULT, result);
        pl.put(CloudBridgeUtil.KEY_NAME_SN, sn);
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgResp(CloudBridgeUtil.CID_E2E_UP, sn, teidArray, mApp.getToken(), pl));
        sendNetMsg(e2e);
    }

    public void sendJuphoonVideoCallResp(String teid, int sn, int result, String appUrl, String deviceUrl) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(null);  //resp不关心回执
        e2e.setNeedNetTimeout(false);
        String[] teidArray = new String[1];
        teidArray[0] = teid;
        // PL
        JSONObject pl = new JSONObject();
        if (!TextUtils.isEmpty(appUrl)) {
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APP_URL, appUrl);
        }
        if (!TextUtils.isEmpty(deviceUrl)) {
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_DEVICE_URL, deviceUrl);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESULT, result);
        pl.put(CloudBridgeUtil.KEY_NAME_SN, sn);
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgResp(CloudBridgeUtil.CID_E2E_UP, sn, teidArray, mApp.getToken(), pl));
        sendNetMsg(e2e);
    }


    public void startVideoCallActivity(String eid, String videocallParams) {

        WatchData watch = mApp.getCurUser().queryWatchDataByEid(eid);
        final Intent intent = new Intent(this, VideoCallActivity.class);
        int videoCallVersion = WatchData.getVideoCallVersion(watch);
        if (videoCallVersion == 2) {
            intent.setClass(this, VideoCallActivity2.class);
        } else if (videoCallVersion == 3) {
            intent.setClass(this, VideoCallActivity3.class);
        }
        intent.putExtra(Const.VIDEOCALL_TYPE, 1);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, eid);
        intent.putExtra(Constants.VIDEOCALL_PARAMS, videocallParams);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        // 判断栈顶Activity是不是WelcomeActivity、SplashAdActivity，是的话延迟3s
        if (SystemUtils.getTopActivity(this).contains("WelcomeActivity")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NetService.this.startActivity(intent);
                }
            }, 3 * 1000);
        } else if (SystemUtils.getTopActivity(this).contains("SplashAdActivity")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NetService.this.startActivity(intent);
                }
            }, 5 * 1000);
        } else {
            startActivity(intent);
            if (videoCallVersion == 2) {
                LogUtil.e("startVideoCallActivity videlCallActivity2");
            } else if (videoCallVersion == 3) {
                LogUtil.e("startVideoCallActivity VideoCallActivity3");
            }

        }
    }

    /*----------------------------- video call end ---------------------------------------------*/


    private void handleWatchNaviStart(String naviData) {
        Intent naviStart = new Intent(Const.ACTION_WATCH_NAVI_START);
        naviStart.putExtra("data", naviData);
        sendBroadcast(naviStart);
    }

    private void handleWatchNaviCurrentPoint(String currentPoint) {
        Intent curPoint = new Intent(Const.ACTION_WATCH_NAVI_CURRENT_POINT);
        curPoint.putExtra("data", currentPoint);
        sendBroadcast(curPoint);
    }

    public void getWatchNaviState(String watchEid, MsgCallback callback) {
        MyMsgData naviState = new MyMsgData();
        naviState.setCallback(this);
        if (callback != null) {
            naviState.setCallback(callback);
        } else {
            naviState.setCallback(this);
        }

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watchEid);

        naviState.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_WATCH_NAVI_STATE,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        mApp.getToken(), pl));
        sendNetMsg(naviState);
    }

    private void handleWatchNaviNotice(JSONObject naviNoticePL) {
        String key = (String) naviNoticePL.get(CloudBridgeUtil.KEY_NAME_KEY);
        JSONObject value = (JSONObject) naviNoticePL.get(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY);
        getVoiceMsg(key, value);
    }

    public void uploadNotice(int sn, String eid, String gid, String type, String content, MsgCallback callBack) {
        MyMsgData uploadNoticeMessage = new MyMsgData();
        uploadNoticeMessage.setCallback(callBack);

        JSONObject value = new JSONObject();
        value.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        value.put(CloudBridgeUtil.KEY_NAME_TYPE, type);
        value.put(CloudBridgeUtil.KEY_NAME_CONTENT, content);
        value.put(CloudBridgeUtil.E2C_PL_KEY_DURATION, 100);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY, value);
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, gid);
        String key = CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE + gid + CloudBridgeUtil.E2C_SPLIT_MEG + CloudBridgeUtil.E2C_SERVER_SET_TIME;
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key);

        uploadNoticeMessage.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(
                CloudBridgeUtil.CID_UPLOAD_NOTICE,
                sn,
                mApp.getToken(),
                pl
        ));
        sendNetMsg(uploadNoticeMessage);
    }

    public void getLostRecord(final ChatMsgEntity chat) {
        MyMsgData c2e = new MyMsgData();
        c2e.setTimeout(60 * 1000);
        c2e.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    try {
                        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String content = (String) pl.get("Content");
                        byte[] bitmapArray = Base64.decode(content, Base64.NO_WRAP);
                        File voice;
                        if (chat.getmAudioPath() == null) {
                            voice = new File(ImibabyApp.getChatCacheDir().getPath(), ImibabyApp.getChatFileName());
                        } else {
                            voice = new File(chat.getmAudioPath());
                        }

                        FileOutputStream out = new FileOutputStream(voice);
                        byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                        out.write(tmp);
                        out.close();
                        chat.setmAudioPath(voice.getPath());
                        ChatHisDao.getInstance(getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                        Intent intent = new Intent(Const.ACTION_PROCESSED_NOTIFY_OK);
                        sendBroadcast(intent);
                        sendBroadcast(new Intent(Constants.ACTION_RECEIVE_GROUP_MESSAGE_NOTIFY));
                        sendBroadcast(new Intent(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY));
                    } catch (Exception e) {
                        mApp.sdcardException("getLostRecord", e);
                    }
                }
            }
        });
        JSONObject pl = new JSONObject();
        pl.put("Key", "GP/" + chat.getmFamilyId() + "/MSG/" + TimeUtil.getReversedOrderTime(chat.getmDate()));
        c2e.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_C2E_GET_MESSAGE, pl));
        sendNetMsg(c2e);
    }

    public void setFamilyWifi(String eid, String ssid, String bssid, MsgCallback msgCallback) {
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        JSONObject wifi = new JSONObject();
        wifi.put(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID, ssid);
        wifi.put(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID, bssid);
        JSONArray wifis = new JSONArray();
        wifis.add(wifi);
        pl.put(CloudBridgeUtil.KEY_FAMILY_WIFIS, wifis);
        msgData.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_SET_FAMILY_WIFI, sn, mApp.getToken(), pl));
        sendNetMsg(msgData);
    }

    public void getFamilyWifi(String eid, MsgCallback msgCallback) {
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        msgData.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_FAMILY_WIFI, sn, mApp.getToken(), pl));
        sendNetMsg(msgData);
    }

    public void doLogout() {
        mApp.setToken(null);
        mApp.setLastppssww(null);
        mApp.setLastUnionId(null);
        mApp.setLastLoginState(Const.LOGIN_STATE_NEED_LOG);
        setNetServiceLoginOK(false);
        closeWebSocket();
    }

}
