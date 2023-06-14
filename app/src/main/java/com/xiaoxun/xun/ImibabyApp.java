/**
 * Creation Date:2015-1-7
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDexApplication;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.PushConfiguration;
import com.xiaomi.stat.MiStat;
import com.xiaoxun.xun.activitys.ChewbaccaUncaughtExceptionHandler;
import com.xiaoxun.xun.activitys.HelpWebActivity;
import com.xiaoxun.xun.activitys.NewLoginActivity;
import com.xiaoxun.xun.activitys.NewMainActivity;
import com.xiaoxun.xun.activitys.SystemUpdateActivity;
import com.xiaoxun.xun.adapter.AllMessageAdapter;
import com.xiaoxun.xun.beans.ADShowData;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.DialogSet;
import com.xiaoxun.xun.beans.EFence;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.MyUserData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.RelationSel;
import com.xiaoxun.xun.beans.SilenceTime;
import com.xiaoxun.xun.beans.SleepTime;
import com.xiaoxun.xun.beans.SosWarning;
import com.xiaoxun.xun.beans.WarningInfo;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.beans.WatchGroupMemberData;
import com.xiaoxun.xun.beans.WatchUpDateInfo;
import com.xiaoxun.xun.db.LocationDAO;
import com.xiaoxun.xun.db.UserRelationDAO;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.interfaces.OnImageDownload;
import com.xiaoxun.xun.receivers.MiPushMessageReceiver.MiPushHandler;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.ActivityLifecycleUtils;
import com.xiaoxun.xun.utils.AsyncImageLoader;
import com.xiaoxun.xun.utils.AsyncImageLoader.ImageCallback;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DataCleanManager;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.DownloadHelper;
import com.xiaoxun.xun.utils.DownloadHelper.DownloadListener;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.NotificationHelper;
import com.xiaoxun.xun.utils.SmsUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.SystemUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.XimalayaUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.json.JSONArray;
import org.json.JSONException;

import com.xiaoxun.xun.bgstart.BgManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-7
 */
public class ImibabyApp extends MultiDexApplication implements ImageCallback {
    static private String TAG = "ImibabyApp";
    //MIIT  OAID
    public String miit_oaid = "";
    private HashMap<String, ArrayList<WarningInfo>> mWarningMsg = new HashMap<String, ArrayList<WarningInfo>>(); //保存用户的告警类消息
    private HashMap<String, SosWarning> mSosWarning = new HashMap<String, SosWarning>(); //保存用户的临时sos
    //sos的汇总列表。为每一个sos建立一个聊天列表，当sos消失之后，删除该sos中的列表信息。key为每个sos的时间戳。
    private HashMap<String, HashMap<String, ArrayList<ChatMsgEntity>>> mSosCollectList
            = new HashMap<String, HashMap<String, ArrayList<ChatMsgEntity>>>();

    private MyUserData curUser;      //
    private String loginId;
    private String loginXiaomiId;
    private int lastloginState;//上次登陆结果
    private String token;
    //   private String loginEid;
    private Boolean mSosStartFlag = false;
    private String mSosFamily;

    private HashMap<String, String> mEndContentKey = new HashMap<String, String>();

    private ArrayList<ChatMsgEntity> mSosChatList;

    private HashMap<String, Integer> mWatchIsOn;

    private HashMap<String, Integer> mWatchOfflineStatus;
    //alex charge status
    private HashMap<String, Integer> mChargeState;
    //最新一次从服务器获取的电量的时间戳
    public HashMap<String, Long> timeOfRecentBattery = null;
    //最新一次充电状态的时间戳
    public HashMap<String, Long> timeOfChargeRecentBattery = null;

    private HashMap<String, ArrayList<EFence>> mWatchEFence = new HashMap<String, ArrayList<EFence>>();
    private int mBackHomeFlag;
    private String lastppssww;
    private String lastUnionId;
    public boolean isDeviceOfflineMapSet = false;

    //收到手表的关机回执时的时间戳
    public HashMap<String, Long> timeWatchShutDown = null;

    private LocalBroadcastManager mLocalBroadcastManager;

    public int callState = Const.MESSAGE_CALL_INIT_STATE;   // 视频通话状态 , 初始值 0
    public int videoCallSn;  // 视频通话的sn
    public String videoCallEid;  // 正在通话中的eid

    public boolean refreshTab = true;
    private static File mapHereDir;

    //广告业务代码逻辑变量
    public boolean isAdSplashOnOff() {
        return adSplashOnOff;
    }

    public void setAdSplashOnOff(boolean adSplashOnOff) {
        this.adSplashOnOff = adSplashOnOff;
    }

    public boolean isAdMainAdOnOff() {
        return adMainAdOnOff;
    }

    public void setAdMainAdOnOff(boolean adMainAdOnOff) {
        this.adMainAdOnOff = adMainAdOnOff;
    }

    //广告相关变量
    private boolean adSplashOnOff = false;  //true 开启 false 关闭
    private boolean adMainAdOnOff = false;  //true 开启 false 关闭
    private ArrayList<ADShowData> adShowList = new ArrayList<ADShowData>();
    private int adUpdateFlag = 0;             //广告的更新标志
    private String adUpdateTime;              //广告的更新时间
    private int adInterval = 30;              //开屏广告检查间隔

    public ArrayList<ADShowData> getAdShowList() {
        return adShowList;
    }

    public void setAdUpdateTime(String adUpdateTime) {
        this.adUpdateTime = adUpdateTime;
    }

    public void setAdUpdateFlag(int adUpdateFlag) {
        this.adUpdateFlag = adUpdateFlag;
    }

    public int getAdInterval() {
        return adInterval;
    }

    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }

    private HashMap<Integer, DialogSet> sysDialogSets = new HashMap<Integer, DialogSet>();

    public HashMap<String, String> getmEndContentKey() {
        return mEndContentKey;
    }

    public String mAudioPath = null;

    private HashMap<String, Boolean> mCallsInquiryTag = new HashMap<String, Boolean>();

    public void setmEndContentKey(HashMap<String, String> mEndContentKey) {
        this.mEndContentKey = mEndContentKey;
    }

    public int getLastLoginState() {
        return lastloginState;
    }

    public void setLastLoginState(int loginState) {
        this.lastloginState = loginState;
    }

    public void setFocusWatch(WatchData selWatch) {
        if (selWatch != null) {
            setValue(Const.SHARE_PREF_FIELD_LAST_WATCH, selWatch.getEid());
            //focusWatch = selWatch;
            getCurUser().setFocusWatch(selWatch);
        } else {
            setValue(Const.SHARE_PREF_FIELD_LAST_WATCH, "");
            getCurUser().setFocusWatch(null);
        }
    }

    private static ImibabyApp mInstance;
    public String isLoginToStore;//判断从商城跳转到登录页

    /**
     * 获取context
     *
     * @return
     */
    public static Context getInstance() {
        if (mInstance == null) {
            mInstance = new ImibabyApp();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        closeAndroidPDialog();
        initMiStat();
        boolean hasClean = getBoolValue(Const.SHARE_PREF_CLEAN_CACHE_DATA_FOR_UPDATE, false);
        String key = getStringValueNoDecrypt(Const.SHARE_PREF_TIME_STAP, "");

        int sdkVersion = getIntValue(Const.SHARE_PREF_BUILD_VERSION_SDK_INT, 0);
        boolean isUpdateToNougat = (sdkVersion != 0 && sdkVersion < 24 && Build.VERSION.SDK_INT >= 24);
        if (isUpdateToNougat) {
            DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                    getString(R.string.clean_data_notice),
                    getString(R.string.clean_data_tips),
                    null,
                    null,
                    new OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    },
                    getText(R.string.confirm).toString());
        }
        if (!hasClean || (key == null || key.length() == 0) || isUpdateToNougat) {
            DataCleanManager.cleanApplicationData(getApplicationContext());
            setValue(Const.SHARE_PREF_CLEAN_CACHE_DATA_FOR_UPDATE, true);
            key = MD5.md5_string("Imibaby" + TimeUtil.getTimestampCHN());
            setStringValueNoEncrypt(Const.SHARE_PREF_TIME_STAP, key);
        }

        boolean hasCleanLogin = getBoolValue(Const.SHARE_PREF_CLEAN_LOGIN_DATA_FOR_UPDATE, true);
        if (hasCleanLogin) {
            setValue(Const.SHARE_PREF_FIELD_LAST_PPSSWW, null);
            setValue(Const.SHARE_PREF_FIELD_LAST_UID, null);
            setValue(Const.SHARE_PREF_FIELD_LOGIN_TOKEN, null);
            setValue(Const.SHARE_PREF_CLEAN_LOGIN_DATA_FOR_UPDATE, false);
        }
        setValue(Const.SHARE_PREF_BUILD_VERSION_SDK_INT, Build.VERSION.SDK_INT);
        AESUtil.getInstance().init(key);
        initContext();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        //register catch all exception
        //Thread.getDefaultUncaughtExceptionHandler();
        Intent it = new Intent(this, NetService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LogUtil.i(" isAppRunInFront=" + SystemUtils.isAppRunInFront(this));
            if (SystemUtils.isAppRunInFront(this))
//                startService(it);
                startForegroundService(it);
        } else {
            startService(it);
        }

//        startService(it);
        Thread.setDefaultUncaughtExceptionHandler(new ChewbaccaUncaughtExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler()));
        sdcardLog("imibabayApp---------->>onCreate");

        //判断手机系统是否支持google service
        getSupportGoogleService();

        //初始话广告数据
        initAdMainPageAndSplashOnOff();
        initAppAdDataListBySharePref();


//        if (shouldInit()) {
//            Log.e("ImibabyApp", XimalayaUtil.getAppVersionCode(this, "com.xiaomi.xmsf")
//                    + ":" + XimalayaUtil.isMIUIGlobalVersion());
//
//            PushConfiguration pushConfiguration = new PushConfiguration();
//            pushConfiguration.setOpenHmsPush(true);
//            pushConfiguration.setOpenCOSPush(true);
//            pushConfiguration.setOpenFTOSPush(true);
//            MiPushClient.registerPush(this.getApplicationContext(), Const.MI_STATE_ID,
//                    Const.MI_STATE_KEY, pushConfiguration);
//        }
        initMiPushLogger();
        if (mMiPushHandler == null) {
            mMiPushHandler = new MiPushHandler(getApplicationContext(), this);
        }
        ActivityLifecycleUtils.observeActivityLifeCycle(this);
        initNotificationChannel();
        if (isMainProcess()) {
            //You must call setRegion to set the region before calling registerPush
            MiPushClient.setRegion(Region.Global);
            MiPushClient.registerPush(this, Const.MI_STATE_ID, Const.MI_STATE_KEY);
        }
        BgManager.getInstance().init(this);
    }

    private void initMiStat() {
        // regular stats.
//        MiStatInterface.initialize(this.getApplicationContext(), Const.MI_STATE_ID, Const.MI_STATE_KEY,
//                "default channel");
//        MiStatInterface.setUploadPolicy(
//                MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
//        MiStatInterface.enableLog();
//
//        // enable exception catcher.
//        MiStatInterface.enableExceptionCatcher(true);
//            //小米统计sdk3.0初始化
//            MiStat.initialize(this.getApplicationContext(), Const.MI_STATE_ID, MI_STATE_KEY,
//                    true, "default channel");
//            MiStat.setCustomPrivacyState(true);
//            MiStat.setInternationalRegion(true,"RU");
//            MiStat.setUploadNetworkType(MiStat.NetworkType.TYPE_NONE);
//            LogUtil.e("MiStat device:"+MiStat.getDeviceId());
    }

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getApplicationInfo().processName;
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    // 如没有保存过当前使用地图类型flag，且所有设备都是305/710/730/106/502B_A02 ，则保存flag为baidu
    public void initMapType() {

        if (hasValue(Const.SHARE_PREF_FIELD_CHANEG_MAP))
            return;
        if (getWatchList().size() == 0) {
            return;
        }
        for (WatchData watch : getWatchList()) {
            if (!(watch.isDevice305() || watch.isDevice710() || watch.isDevice106() || watch.isDevice502B_A02()
                    || watch.isDevice705() || watch.isDevice502_A03()
                    || watch.isDevice505() || watch.isDevice307() || watch.isDevice703()))
                return;
        }
        setValue(Const.SHARE_PREF_FIELD_CHANEG_MAP, 2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LogUtil.d(TAG + "  onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        LogUtil.d(TAG + "  onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        LogUtil.d(TAG + "  onTrimMemory");
        super.onTrimMemory(level);
    }

    private void initContext() {

        if (null == mSosChatList) {
            mSosChatList = new ArrayList<ChatMsgEntity>();
        }

        if (null == sysDialogSets) {
            sysDialogSets = new HashMap<Integer, DialogSet>();
        }
        if (null == mWatchIsOn) {
            mWatchIsOn = new HashMap<String, Integer>();
        }

        //alex charge status
        if (null == mChargeState) {
            mChargeState = new HashMap<String, Integer>();
        }

        //最近一次电量更新时间
        if (null == timeOfRecentBattery) {
            timeOfRecentBattery = new HashMap<String, Long>();
        }

        //最新一次充电状态更新时间
        if (null == timeOfChargeRecentBattery) {
            timeOfChargeRecentBattery = new HashMap<String, Long>();
        }

        if (null == mWatchOfflineStatus) {
            mWatchOfflineStatus = new HashMap<String, Integer>();
        }

        if (timeWatchShutDown == null) {
            timeWatchShutDown = new HashMap<String, Long>();
        }

        if (mCallsInquiryTag == null) {
            mCallsInquiryTag = new HashMap<String, Boolean>();
        }

        setmBackHomeFlag(0);
        setmUseCall(getBoolValue(Const.SHARE_PREE_FIELD_USE_CALL_MODE, true));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            initFileDirs();
            initSettings();
            initUserData();
            initWatchData();
            initLocationEfence();  //初始化本地安全区域：
        }
        initRelationSels();
        initImgLoader();
    }

    private void initSettings() {
        lastloginState = getIntValue(Const.SHARE_PREF_FIELD_LOGIN_STATE, Const.LOGIN_STATE_REG_NEW);
        setLoginId(getStringValue(Const.SHARE_PREF_FIELD_LAST_UID, null));
        setLoginXiaomiId(getStringValue(Const.SHARE_PREF_FIELD_LAST_XIAOMIID, null));
        setLastppssww(getStringValue(Const.SHARE_PREF_FIELD_LAST_PPSSWW, null));
        setLastUnionId(getStringValue(Const.SHARE_PREF_FIELD_LAST_UNIONID, null));
        setToken(getStringValue(Const.SHARE_PREF_FIELD_LOGIN_TOKEN, null));
        if (lastloginState != Const.LOGIN_STATE_LOGIN) {
            lastloginState = Const.LOGIN_STATE_CHECK_REG;
        }

    }

    private void saveIsValidFamily(int value) {
        setValue(Const.SHARE_PREF_FIELD_IS_VALID_FAMILY, value);
    }

    private int readIsValidFamily() {
        return getIntValue(Const.SHARE_PREF_FIELD_IS_VALID_FAMILY, 0);
    }

    private void initWatchData() {
        if (curUser.getWatchList() != null && curUser.getWatchList().size() > 0) {
            String eid = getStringValue(Const.SHARE_PREF_FIELD_LAST_WATCH, curUser.getWatchList().get(0).getEid());
            setValue(Const.SHARE_PREF_FIELD_LAST_WATCH, eid);
            WatchData focusWatch = null;
            focusWatch = curUser.queryWatchDataByEid(eid);
            if (focusWatch == null) {
                focusWatch = curUser.getWatchList().get(0);
                setValue(Const.SHARE_PREF_FIELD_LAST_WATCH, curUser.getWatchList().get(0).getEid());
            }

            focusWatch.setCurLocation(LocationDAO.getInstance(getApplicationContext()).readLocation(focusWatch.getEid()));
            setFocusWatch(focusWatch);
            for (WatchData watch : curUser.getWatchList()) {
                watch.setCurLocation(LocationDAO.getInstance(getApplicationContext()).readLocation(watch.getEid()));
                mWatchIsOn.put(watch.getEid(), getIntValue(Const.SHARE_PREF_FIELD_WATCH_IS_ON + watch.getEid(), Const.WATCH_STATE_POWER_ON));
                //alex charge status
                mChargeState.put(watch.getEid(), Const.WATCH_CHARGE_IS_OFF);
            }
        }
    }

    private void initUserData() {
        MyUserData cur = new MyUserData();
        String uid = getStringValue(Const.SHARE_PREF_CURRENT_USER_REFLECT_ID, getLoginId());
        cur.setUid(uid);
        cur.setNickname(getLoginId());

        setCurUser(cur);

        if (readIsValidFamily() == 0) {//置为空列表，但是不为null
            cur.setWatchList(new ArrayList<WatchData>());
            cur.setFamilyList(new ArrayList<FamilyData>());
            return;
        }
        cur.setWatchList(UserRelationDAO.getInstance(getApplicationContext()).readBindWathcs(uid));
        //update watch detail
        fillWatchListDetail(cur.getWatchList());
        cur.setFamilyList(UserRelationDAO.getInstance(getApplicationContext()).readFamilys(uid));
        //set familyList data
        if (cur.getFamilyList() != null && cur.getFamilyList().size() > 0) {
            for (FamilyData family : cur.getFamilyList()) {
                if (readFamilyAdminEid(family.getFamilyId()) != null) {
                    family.setAdminEId(readFamilyAdminEid(family.getFamilyId()));
                } else {
                    family.setAdminEId(family.getFamilyId());
                }
                family.setUserList(UserRelationDAO.getInstance(getApplicationContext()).readFamilyUsers(family.getFamilyId()));
                family.setWatchlist(UserRelationDAO.getInstance(getApplicationContext()).readFamilyWatchs(family.getFamilyId()));
                fillWatchListDetail(family.getWatchlist());
                family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                family.setDescription(StrUtil.genFamilyDesc(family, getApplicationContext()));
            }

            FamilyData firstfamily = cur.getFamilyList().get(0);

            if (firstfamily.getMemberList() != null && firstfamily.getMemberList().size() > 0) {
                for (MemberUserData member : firstfamily.getMemberList()) {
                    if (member.getHeadPath() == null) {
                        member.setHeadPath(Integer.valueOf(relationSels.size() - 1).toString());
                    }
                    if (member.getUid().equals(getCurUser().getUid())) {
                        getCurUser().setNickname(member.getNickname());
                        getCurUser().setHeadPath(member.getHeadPath());
                        getCurUser().setCellNum(member.getCellNum());
                        getCurUser().setXiaomiId(member.getXiaomiId());
                        getCurUser().setEid(member.getEid());
                        getCurUser().getCustomData().setFromJsonStr(member.getCustomData().toJsonStr());
                        break;
                    }
                }

            }

        }

    }

    public void setWatchBatteryLevel(WatchData watch, int level) {
        setValue(Const.SHARE_PREF_FIELD_BATTERY_LEVEL + watch.getEid(), level);
        if (-1 == level) {
            watch.setBattery(100);
        } else {
            watch.setBattery(level);
        }
    }

    @SuppressWarnings("key要以Local时间正序传入")
    public synchronized void setNextContentKey(FamilyData family, String value) {
        setValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + family.getFamilyId(), value);
        family.setNextContentKey(value);
    }

    @SuppressWarnings("key要以Local时间正序传入")
    public synchronized void setNextFamilyChangeNotifyKey(FamilyData family, String value) {
        setValue(Const.SHARE_PREF_FIELD_NEXT_FAMILY_CHANGE_KEY + family.getFamilyId(), value);
        family.setNextFamilyChangeNotifyKey(value);
    }

    @SuppressWarnings("key要以Local时间正序传入")
    public synchronized void setNextWarningKey(FamilyData family, String value) {
        setValue(Const.SHARE_PREF_FIELD_NEXT_WARNING_KEY + family.getFamilyId(), value);
        family.setNextWarningKey(value);
    }

    @SuppressWarnings("key要以Local时间正序传入")
    public synchronized void setNextPrivateChatKey(String eid, String value) {
        setValue(eid + Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY, value);
    }

    //
    public synchronized void saveFamilyAdminEid(FamilyData family, String adminEid) {
        setValue(Const.SHARE_PREF_FIELD_FAMILY_ADMINEID_KEY + family.getFamilyId(), adminEid);
    }

    public String readFamilyAdminEid(String familyId) {
        return getStringValue(Const.SHARE_PREF_FIELD_FAMILY_ADMINEID_KEY + familyId, null);
    }

    private void fillWatchListDetail(List<WatchData> watchList2) {
        if (watchList2 != null && watchList2.size() > 0) {
            for (int i = 0; i < watchList2.size(); i++) {
                WatchDAO.getInstance(getApplicationContext()).readWatch(watchList2.get(i));
                int battery = getIntValue(Const.SHARE_PREF_FIELD_BATTERY_LEVEL + watchList2.get(i).getEid(), -1);
                if (-1 == battery) {
                    watchList2.get(i).setNewWatch(true);
                    setWatchBatteryLevel(watchList2.get(i), battery);
                } else {
                    setWatchBatteryLevel(watchList2.get(i), battery);
                }
                watchList2.get(i).setWatchGroupMembers(readWatchGroupsStringValue(Const.SHARE_PREF_FIELD_WATCH_GROUP_MEMBERS + watchList2.get(i).getEid()));
            }
        }
    }


    static File baseDir;
    static File mapCacheDir;
    static File mapOfflineDir;
    static File mapBaiduOfflineDir;
    static File myChat;

    static File chatCacheDir;
    static File iconCacheDir;
    static File alarmRecordDir;
    static File logCacheDir;


    public static File getMyChat() {
        if (!myChat.isDirectory()) {
            myChat.delete();
            myChat.mkdirs();
        }
        return myChat;
    }

    public static File getIconCacheDir() {
        if (iconCacheDir != null && !iconCacheDir.isDirectory()) {
            iconCacheDir.delete();
            iconCacheDir.mkdirs();
        }
        return iconCacheDir;
    }

    public static File getSaveDir() {
        if (!baseDir.isDirectory()) {
            baseDir.delete();
            baseDir.mkdirs();
        }
        return baseDir;
    }

    public static File getMapOfflineDir() {
        if (!mapOfflineDir.isDirectory()) {
            mapOfflineDir.delete();
            mapOfflineDir.mkdirs();
        }
        return mapOfflineDir;
    }

    public static File getMapBaiduOfflineDir() {
        if (!mapBaiduOfflineDir.isDirectory()) {
            mapBaiduOfflineDir.delete();
            mapBaiduOfflineDir.mkdirs();
        }
        return mapBaiduOfflineDir;
    }

    public static File getChatCacheDir() {
        if (chatCacheDir == null) {
            chatCacheDir = new File(baseDir, Const.CHAT_CACHE_DIR);
            if (chatCacheDir.exists() && !chatCacheDir.isDirectory()) {
                chatCacheDir.delete();
            }

            if (!chatCacheDir.exists()) {
                chatCacheDir.mkdir();
            }
        } else {
            if (!chatCacheDir.isDirectory()) {
                chatCacheDir.delete();
                chatCacheDir.mkdirs();
            }
        }
        return chatCacheDir;
    }

    public static String getChatFileName() {
        return TimeUtil.getTimestampCHN() + Const.VOICE_FILE_SUFFIX;
    }

    public static String getImageFileName() {
        return TimeUtil.getTimestampCHN() + Const.IMAGE_FILE_SUFFIX;
    }

    public static String getVideoFileName() {
        return TimeUtil.getTimestampCHN() + Const.VIDEO_FILE_SUFFIX;
    }

    public static File getAlarmRecordDir() {
        if (!alarmRecordDir.isDirectory()) {
            alarmRecordDir.delete();
            alarmRecordDir.mkdirs();
        }
        return alarmRecordDir;
    }

    public void initFileDirs() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            if (baseDir == null) {
                baseDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), Const.MY_BASE_DIR);
                LogUtil.e("initFileDirs baseDir  = " + baseDir.getAbsolutePath());
            } else {
                baseDir = new File(baseDir.getPath());
                LogUtil.e("initFileDirs baseDir  = " + baseDir.getAbsolutePath());
            }
            if (baseDir.exists() && !baseDir.isDirectory()) {
                baseDir.delete();
            }
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            myChat = new File(baseDir, Const.MY_CHAT_DIR);
            if (myChat.exists() && !myChat.isDirectory()) {
                myChat.delete();
            }

            if (!myChat.exists()) {
                myChat.mkdir();
            }

            mapCacheDir = new File(baseDir, Const.MAP_CACHE_DIR);
            if (mapCacheDir.exists() && !mapCacheDir.isDirectory()) {
                mapCacheDir.delete();
            }

            if (!mapCacheDir.exists()) {
                mapCacheDir.mkdir();
            }

            mapOfflineDir = new File(mapCacheDir, Const.Map_OFFLINE_DIR);
            if (mapOfflineDir.exists() && !mapOfflineDir.isDirectory()) {
                mapOfflineDir.delete();
            }

            if (!mapOfflineDir.exists()) {
                mapOfflineDir.mkdir();
            }

            mapBaiduOfflineDir = new File(mapCacheDir, Const.Map_OFFLINE_BIADU_DIR);
            if (mapBaiduOfflineDir.exists() && !mapBaiduOfflineDir.isDirectory()) {
                mapBaiduOfflineDir.delete();
            }

            if (!mapBaiduOfflineDir.exists()) {
                mapBaiduOfflineDir.mkdir();
            }

            chatCacheDir = new File(baseDir, Const.CHAT_CACHE_DIR);
            if (chatCacheDir.exists() && !chatCacheDir.isDirectory()) {
                chatCacheDir.delete();
            }

            if (!chatCacheDir.exists()) {
                chatCacheDir.mkdir();
            }

//            chatCacheDataDir = new File(this.getFilesDir(), Const.CHAT_CACHE_DIR);
//            if (chatCacheDataDir.exists() && !chatCacheDataDir.isDirectory()) {
//                chatCacheDataDir.delete();
//            }
//
//            if (!chatCacheDataDir.exists()) {
//                chatCacheDataDir.mkdir();
//            }

            alarmRecordDir = new File(baseDir, Const.ALARM_RECORD_DIR);
            if (alarmRecordDir.exists() && !alarmRecordDir.isDirectory()) {
                alarmRecordDir.delete();
            }

            if (!alarmRecordDir.exists()) {
                alarmRecordDir.mkdir();
            }

            iconCacheDir = new File(baseDir, Const.ICON_CACHE_DIR);
            if (iconCacheDir.exists() && !iconCacheDir.isDirectory()) {
                iconCacheDir.delete();
            }
            if (!iconCacheDir.exists()) {
                iconCacheDir.mkdir();
            }

            logCacheDir = new File(baseDir, Const.LOG_CACHE_DIR);
            if (logCacheDir.exists() && !logCacheDir.isDirectory()) {
                logCacheDir.delete();
            }
            if (!logCacheDir.exists()) {
                logCacheDir.mkdir();
            }
        }

    }

    public File getCurLogFile() {
        File file = null;
        File baseDir;
        File dir;
        baseDir = getExternalFilesDir(Const.MY_BASE_DIR);
        dir = new File(baseDir, Const.LOG_CACHE_DIR);
        Date nowtime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currDay = dateFormat.format(nowtime);
        StringBuilder fileNameBuff = new StringBuilder();
        fileNameBuff.append(currDay);//调整一下log文件命名方式，方便查找
        fileNameBuff.append("_");
        fileNameBuff.append("all");//修改sdcardlog,不区分eid，方便分析
        fileNameBuff.append(".log");

        file = new File(dir, fileNameBuff.toString());

        return file;
    }

    public static File getMapHereDir() {
        if (!mapHereDir.isDirectory()) {
            mapHereDir.delete();
            mapHereDir.mkdirs();
        }
        return mapHereDir;
    }

    public boolean isInOfflineMode(String eid) {
        return getIntValue(eid + CloudBridgeUtil.OPERATION_MODE_VALUE,
                Const.DEFAULT_OPERATIONMODE_VALUE) == 1;
    }

    class locationStat {
        public int all;
        public int success;
        public int timeout;
        public int time_5;
        public int time_avg_5;
        public int time_10;
        public int time_avg_10;
        public int time_20;
        public int time_avg_20;
        public int time_40;
        public int time_avg_40;
        public int time_60;
        public int time_avg_60;
    }

    public void locationStatistic(long time, String eid) {
        net.minidev.json.JSONArray statArray;
        Boolean statFlag = false;
        locationStat mLocationStat = new locationStat();
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHH");
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        String type = "Location_" + (today.getHours() + 1);
        String key;
        mLocationStat.all = 1;
        if (time > 0 && time <= 5) {
            key = "ok_5";
            mLocationStat.success = 1;
            mLocationStat.time_5 = 1;
            mLocationStat.time_avg_5 = Integer.valueOf(String.valueOf(time));
        } else if (time > 5 && time <= 10) {
            key = "ok_10";
            mLocationStat.success = 1;
            mLocationStat.time_10 = 1;
            mLocationStat.time_avg_10 = Integer.valueOf(String.valueOf(time));
        } else if (time > 10 && time <= 20) {
            key = "ok_20";
            mLocationStat.success = 1;
            mLocationStat.time_20 = 1;
            mLocationStat.time_avg_20 = Integer.valueOf(String.valueOf(time));
        } else if (time > 20 && time <= 30) {
            key = "ok_30";
            mLocationStat.success = 1;
            mLocationStat.time_40 = 1;
            mLocationStat.time_avg_40 = Integer.valueOf(String.valueOf(time));
        } else if (time > 30 && time <= 40) {
            key = "ok_40";
            mLocationStat.success = 1;
            mLocationStat.time_40 = 1;
            mLocationStat.time_avg_40 = Integer.valueOf(String.valueOf(time));
        } else if (time > 40 && time <= 50) {
            key = "ok_50";
            mLocationStat.success = 1;
            mLocationStat.time_60 = 1;
            mLocationStat.time_avg_60 = Integer.valueOf(String.valueOf(time));
        } else if (time > 50 && time < 60) {
            key = "ok_60";
            mLocationStat.success = 1;
            mLocationStat.time_60 = 1;
            mLocationStat.time_avg_60 = Integer.valueOf(String.valueOf(time));
        } else {
            key = "fail";
            time = 0;
            mLocationStat.timeout = 1;
        }

        String tempkey = getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + getCurUser().getEid(), null);
        if (tempkey == null) {
            statArray = new net.minidev.json.JSONArray();
        } else {
            statArray = (net.minidev.json.JSONArray) JSONValue.parse(tempkey);
            JSONObject checkobj = (JSONObject) statArray.get(0);
            String checkTime = (String) checkobj.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
            checkTime = checkTime.substring(0, 8);
            if (!checkTime.equals(format1.format(today))) {
                String privKey = getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + getCurUser().getEid(), Const.DEFAULT_NEXT_KEY);
                if (privKey.equals(Const.DEFAULT_NEXT_KEY)) {
                    setValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + getCurUser().getEid(), tempkey);
                }
                statArray.clear();
            }
        }
        for (Object object : statArray) {
            JSONObject json = (JSONObject) object;
            String tempeid = (String) json.get(CloudBridgeUtil.KEY_NAME_EID);
            if (tempeid.equals(eid)) {
                String tempTime = (String) json.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                if (tempTime.equals(format.format(today))) {
                    String statLocation = (String) json.get(CloudBridgeUtil.KEY_NAME_LOCAITON_LOCATION);
                    mLocationStat.all += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.success += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.timeout += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_5 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_avg_5 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mLocationStat.time_avg_5;
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_10 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_avg_10 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mLocationStat.time_avg_10;
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_20 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_avg_20 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mLocationStat.time_avg_20;
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_40 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_avg_40 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mLocationStat.time_avg_40;
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_60 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                    statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                    mLocationStat.time_avg_60 = Integer.valueOf(statLocation) + mLocationStat.time_avg_60;
                    json.put(CloudBridgeUtil.KEY_NAME_LOCAITON_LOCATION, mLocationStat.all + "," + mLocationStat.success + ","
                            + mLocationStat.timeout + "," + mLocationStat.time_5 + "," + mLocationStat.time_avg_5 + "," + mLocationStat.time_10 + ","
                            + mLocationStat.time_avg_10 + "," + mLocationStat.time_20 + "," + mLocationStat.time_avg_20 + "," + mLocationStat.time_40
                            + "," + mLocationStat.time_avg_40 + "," + mLocationStat.time_60 + "," + mLocationStat.time_avg_60);
                    statFlag = true;
                }
            }
        }
        if (!statFlag) {
            JSONObject locationObject = new JSONObject();
            locationObject.put(CloudBridgeUtil.KEY_NAME_EID, eid);
            locationObject.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, format.format(today));
            locationObject.put("voice_recv", "0,0");
            locationObject.put("voice_send", "0,0,0,0,0,0,0,0,0,0,0,0,0");
            locationObject.put(CloudBridgeUtil.KEY_NAME_LOCAITON_LOCATION, mLocationStat.all + "," + mLocationStat.success + ","
                    + mLocationStat.timeout + "," + mLocationStat.time_5 + "," + mLocationStat.time_avg_5 + "," + mLocationStat.time_10 + ","
                    + mLocationStat.time_avg_10 + "," + mLocationStat.time_20 + "," + mLocationStat.time_avg_20 + "," + mLocationStat.time_40
                    + "," + mLocationStat.time_avg_40 + "," + mLocationStat.time_60 + "," + mLocationStat.time_avg_60);
            statArray.add(locationObject);
        }
        setValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + getCurUser().getEid(), statArray.toString());
    }

    public void sdcardLog(String sMsg) {
        if (!BuildConfig.IS_PRINT_LOG)
            return;
//        Date nowtime = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        String currTime = dateFormat.format(nowtime);
//
//        String logText = currTime + " " + sMsg + "\n";
//        try {
//            FileOutputStream fos = new FileOutputStream(getCurLogFile(), true);
//            if (BuildConfig.ISDEBUG) {
//                fos.write(logText.getBytes());
//            } else {
//
//                String key = "1234567890abcdef";
//                if (getCurUser().getEid() != null && getCurUser().getEid().length() >= 16) {
//                    key = getCurUser().getEid().substring(0, 16);
//                } else if (SystemUtils.getANDROID_ID(this) != null && SystemUtils.getANDROID_ID(this).length() >= 16) {
//                    key = SystemUtils.getANDROID_ID(this).substring(0, 16);
//                }
//
//                String out = BASE64Encoder.encode(AESUtil.encryptBytes(logText.getBytes(), key, key)) + "\n";
//                fos.write(out.getBytes());
//            }
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public MyUserData getCurUser() {
        return curUser;
    }

    public void setCurUser(MyUserData loginUser) {
        this.curUser = loginUser;
    }

    public boolean getBoolValue(String key, boolean defValue) {
        return getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(key, defValue);
    }

    public int getIntValue(String key, int defValue) {
        return getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE)
                .getInt(key, defValue);
    }

    public String getStringValue(String key, String defValue) {
        String str = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE)
                .getString(key, defValue);
        if (str == null || str.equals(defValue)) {
            return str;
        } else {
            try {
                return AESUtil.getInstance().decryptDataStr(str);
            } catch (Exception e) {
                e.printStackTrace();
                return defValue;
            }
        }
    }

    public boolean hasValue(String key) {
        SharedPreferences prefs = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(key);
    }

    public void deletValue(String key) {
        final SharedPreferences prefs = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void setValue(String key, boolean value) {
        final SharedPreferences preferences = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setValue(String key, int value) {
        final SharedPreferences preferences = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setValue(String key, String value) {
        final SharedPreferences preferences = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString(key, AESUtil.getInstance().encryptDataStr(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public String getStringValueNoDecrypt(String key, String defValue) {
        return getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE)
                .getString(key, defValue);
    }

    public void setStringValueNoEncrypt(String key, String value) {
        final SharedPreferences preferences = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginXiaomiId() {
        return loginXiaomiId;
    }

    public void setLoginXiaomiId(String loginXiaomiId) {
        this.loginXiaomiId = loginXiaomiId;
    }

    public ArrayList<WatchData> getWatchList() {
        if (getCurUser() != null) {
            return getCurUser().getWatchList();
        } else {
            return new ArrayList<WatchData>();
        }
    }

    public String getToken() {
        return token;
    }

    /**
     * @param token 设置登陆成功的session
     */
    public void setToken(String token) {
        this.token = token;
    }

    private NetService mNetService = null;

    public NetService getNetService() {
        return mNetService;
    }

    public void setNetService(NetService mNetService) {
        sdcardLog("setNetService---------->>" + mNetService);

        this.mNetService = mNetService;
    }

    public JSONObject obtainCloudMsgContent(int cid, Object pl) {
        return CloudBridgeUtil.obtainCloudMsgContent(cid, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), getToken(), pl);
    }

    public JSONObject obtainCloudMsgContentWithParam(int cid, Object pl, Object param) {
        return CloudBridgeUtil.obtainCloudMsgContentWithParam(cid, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), getToken(), pl, param);
    }

    public void quitApp() {
        Intent it = new Intent(Const.BROADCAST_ACTION_QUIT_APP);
        //it.putExtra("effect", effect);
        getLocalBroadcastManager().sendBroadcast(it);
    }

    public void resetCurUser() {
        initFileDirs();
        initSettings();
        initUserData();
        initWatchData();
        initLocationEfence();
    }

    public void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    // 正常退出
    public void doLogout(String reason) {
        // TODO Auto-generated method stub
        cleanTransNotice();
        removeCookie(getApplicationContext());
        try {
//            Intent intent = new Intent(this, NetService.class);
//            stopService(intent);
            getNetService().doLogout();
        } catch (Exception e) {
            // TODO: handle exception
        }
        File path = getDatabasePath("webview.db");
        if (path.exists()) {
            for (File fl : path.getParentFile().listFiles()) {
                if (fl.getName().contains("webview")) {
                    fl.delete();
                }
            }
        }
        //reset userdatas
        //resetCurUser();
        saveIsValidFamily(0);//设置标志为家庭无效，
        setLastLoginState(Const.LOGIN_STATE_NEED_LOG);
        setValue(Const.SHARE_PREF_FIELD_LOGIN_STATE, getLastLoginState());
        LogUtil.d("doLogout reason: " + reason);
        sdcardLog("doLogout reason: " + reason);
        quitApp();
        setBindAutoLogin(false);
        setStringValueNoEncrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, ""); //清楚SharedPreferences保存的nikname
        setIsLoginToStore("");//清空主页tab的商城标志位
        /*Intent it = new Intent(this, NewMainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(it);*/
    }

    // session 失效，被挤下线（被别人登录） (退出到login)
    public void doLogoutNoQuitActivity(String reason) {
        // TODO Auto-generated method stub
        cleanTransNotice();
        removeCookie(getApplicationContext());
        try {
//            Intent intent = new Intent(this, NetService.class);
//            stopService(intent);
            getNetService().doLogout();
        } catch (Exception e) {
            // TODO: handle exception
        }
        File path = getDatabasePath("webview.db");
        if (path.exists()) {
            for (File fl : path.getParentFile().listFiles()) {
                if (fl.getName().contains("webview")) {
                    fl.delete();
                }
            }
        }
        //reset userdatas
        //resetCurUser();
        saveIsValidFamily(0);//设置标志为家庭无效，
        setLastLoginState(Const.LOGIN_STATE_NEED_LOG);
        setValue(Const.SHARE_PREF_FIELD_LOGIN_STATE, getLastLoginState());
        LogUtil.d("doLogout reason: " + reason);
        sdcardLog("doLogout reason: " + reason);
        setBindAutoLogin(false);
        quitApp();
        setStringValueNoEncrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME, ""); //清楚SharedPreferences保存的nikname
        Intent it = new Intent(this, NewLoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra("flag", "kickoff");
        it.putExtra("cp", reason);
        startActivity(it);
    }

    //被挤下线 在登录页面不刷新
    public void doLogoutNoQuit(String reason) {
        // TODO Auto-generated method stub
        cleanTransNotice();
        removeCookie(getApplicationContext());
        try {
//            Intent intent = new Intent(this, NetService.class);
//            stopService(intent);
            getNetService().doLogout();
        } catch (Exception e) {
            // TODO: handle exception
        }
        File path = getDatabasePath("webview.db");
        if (path.exists()) {
            for (File fl : path.getParentFile().listFiles()) {
                if (fl.getName().contains("webview")) {
                    fl.delete();
                }
            }
        }
        //reset userdatas
        //resetCurUser();
        saveIsValidFamily(0);//设置标志为家庭无效，
        setLastLoginState(Const.LOGIN_STATE_NEED_LOG);
        setValue(Const.SHARE_PREF_FIELD_LOGIN_STATE, getLastLoginState());
        LogUtil.d("doLogout reason: " + reason);
        sdcardLog("doLogout reason: " + reason);
        setBindAutoLogin(false);
        if (getCurUser().getFamilyList() != null) {
            getCurUser().getFamilyList().clear();
        }
        if (getCurUser().getWatchList() != null) {
            getCurUser().getWatchList().clear();
        }
    }

    public String getAccesskey() {
        // TODO Auto-generated method stub
        return CloudBridgeUtil.ACCESS_KEY;
    }

    public String getWatchVerCode(String ver) {
        if (ver != null && ver.length() > 20) {
            return ver.substring(16, 16 + 3);
        } else {
            return "22";
        }
    }

    public String getAppUpdateReqJsonString() {
        JSONObject params = new JSONObject();

        params.put(Const.CHECK_UPDATE_PROJECT, BuildConfig.project);
        params.put(Const.CHECK_UPDATE_VERSIONCODE, BuildConfig.VERSION_CODE);
        params.put(Const.CHECK_UPDATE_PACKAGENAME, BuildConfig.APPLICATION_ID);
        params.put(Const.CHECK_UPDATE_UNIKEY, getCurUser().getEid());
        return params.toJSONString();
    }

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 11:00
     * 方法描述：获取广告请求数据格式
     */
    public String getAppAdReqJsonString(String infoType, String updateTime) {
        if (getCurUser().getFocusWatch() == null) {
            return null;
        }
        net.minidev.json.JSONArray devices = new net.minidev.json.JSONArray();
        for (WatchData watch : getCurUser().getWatchList()) {
            JSONObject watchJson = new JSONObject();
            watchJson.put("EID", watch.getEid());
            watchJson.put("deviceVer", watch.getVerCur());
            devices.add(watchJson);
        }
        JSONObject params = new JSONObject();
//        params.put("updateTime", adUpdateTime);
        params.put("updateTime", updateTime);
        params.put("appEID", getCurUser().getEid());
        params.put("appVersion", Params.getInstance(this).getAppVerName());
        params.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        if (infoType.equals("0")) {
            params.put("appProjectNum", BuildConfig.project);
        }
        params.put("appPackage", this.getPackageName());
        params.put("devices", devices);
        return params.toJSONString();
    }

    public String getAiVoiceParams() {
        JSONObject params = new JSONObject();
        params.put("appPackage", this.getPackageName());
        params.put("deviceType", getCurUser().getFocusWatch().getDeviceType());
        params.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        return params.toJSONString();
    }

    public String getHelpAgreementParams() {
        JSONObject params = new JSONObject();
        params.put("appVersion", Params.getInstance(this).getAppVerName());
        params.put("appPackage", this.getPackageName());
        if (getCurUser().getFocusWatch() != null) {
            params.put("deviceType", getCurUser().getFocusWatch().getDeviceType());
        }
        params.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        return params.toJSONString();
    }

    public String getHelpCenterParams(String helpType) {
        JSONObject params = new JSONObject();
        params.put("osType", "android");
        params.put("appPackage", this.getPackageName());
        params.put("appVersion", Params.getInstance(getApplicationContext()).getAppVerName());
        if (getCurUser() != null && getCurUser().getFocusWatch() != null)
            params.put("deviceType", getCurUser().getFocusWatch().getDeviceType());
        params.put("helpType", helpType);
        params.put("ads", SystemUtils.getDeviceInfo(getApplicationContext()));
        params.put("lang", Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
        return params.toJSONString();
    }

    public Intent getHelpCenterIntent(Context context, String helpType) {
        String params = getHelpCenterParams(helpType);
        Intent helpIntent = new Intent(context, HelpWebActivity.class);
        helpIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_HELP);
        helpIntent.putExtra(Const.KEY_HELP_URL, FunctionUrl.APP_HELP_CENTER_URL);
        helpIntent.putExtra(Const.KEY_PARAMS, params);
        return helpIntent;
    }

    public Intent getHelpMsg(Context context, String helpType) {
        String params = getHelpCenterParams(helpType);
        Intent helpIntent = new Intent(context, HelpWebActivity.class);
        helpIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_HELP);
        helpIntent.putExtra(Const.KEY_HELP_URL, Url.URL_NO_MSG);
        helpIntent.putExtra(Const.KEY_PARAMS, params);
        return helpIntent;
    }

    public String getEngStatisticsUrl(String eid) {
        StringBuilder statisticsUrl = new StringBuilder(FunctionUrl.APP_ENGLISH_STUDY_STATISTICS_URL);
        JSONObject eidJson = new JSONObject();
        eidJson.put("eid", eid);
        eidJson.put("sn", TimeUtil.getTimeStampGMT());
        String encryptedEid = Base64.encodeToString(AESUtil.encryptAESCBC(eidJson.toJSONString(), getNetService().AES_KEY, getNetService().AES_KEY), Base64.NO_WRAP);
        statisticsUrl.append("?s=").append(encryptedEid).append(getToken());
        return statisticsUrl.toString();
    }

    public String getWatchUpdateReqJsonString(WatchData watch) {
        JSONObject params = new JSONObject();

        params.put(Const.CHECK_UPDATE_PROJECT, watch.getVerCur().substring(0, 7));
        params.put(Const.CHECK_UPDATE_VERSIONNAME, watch.getVerCur());
        params.put(Const.CHECK_UPDATE_UNIKEY, watch.getImei());
        params.put("lang", Locale.getDefault().getLanguage());
        return params.toJSONString();
    }

    public String getWatchVerinfoReqJsonString(WatchData watch) {
        JSONObject params = new JSONObject();
        params.put(Const.CHECK_UPDATE_PROJECT, watch.getVerCur().substring(0, 7));
        params.put(Const.CHECK_UPDATE_VERSIONNAME, watch.getVerCur());
        return params.toJSONString();
    }

    private HashMap<String, String> mWatchVerinfoJson = new HashMap<String, String>();

    public String getWatchVerInfoJsonByReq(String req) {
        return mWatchVerinfoJson.get(req);
    }

    public void putWatchVerInfoJsonByReq(String req, String json) {
        mWatchVerinfoJson.put(req, json);
        //持久化
        JSONObject result = new JSONObject();
        result.put(req, json);
        //    setValue(Const.KEY_WATCH_VERSION_INFO, result.toString());
    }

    public void removeWatchVerInfoJsonByReq(String req) {
        mWatchVerinfoJson.remove(req);
    }

    private HashMap<String, String> mWatchUpdateJson = new HashMap<String, String>();

    public String getWatchUpdateJsonByReq(String req) {
        Log.i("cui", "mWatchUpdateJson====" + mWatchUpdateJson.size());
        return mWatchUpdateJson.get(req);
    }

    public void putWatchUpdateJsonByReq(String req, String json) {
        mWatchUpdateJson.put(req, json);
    }

    public void removeWatchUpdateJsonByReq(String req) {
        mWatchUpdateJson.remove(req);
    }

    public String watchUpdateMaxVersion(WatchData mWatch) {
        String maxVer = null;
        if (mWatch == null || mWatch.getVerCur() == null || mWatch.getVerCur().length() < 7) {
            return null;
        }

        maxVer = mWatch.getVerCur();
        String req = getWatchUpdateReqJsonString(mWatch);
        String updateInfo = getWatchUpdateJsonByReq(req);
        if (updateInfo != null) {
            try {
                org.json.JSONObject watchJo = new org.json.JSONObject(updateInfo);
                String url = null;
                if (watchJo.getString("oldver").equals(maxVer)) {
                    url = watchJo.getString(CloudBridgeUtil.KEY_NAME_DOWNLOAD_URL);
                }
                if (url != null) {
                    maxVer = watchJo.getString("ver");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return maxVer;
    }

    public int showWatchUpdateResult(final Activity activity, boolean isOnlyCheck, final boolean checkWhenStart) {
        final WatchData watch = getCurUser().getFocusWatch();
        int recode = 0;
        if (watch == null || watch.getVerCur() == null || watch.getVerCur().length() < 7) {//无效的watch
            return recode;
        }
        String req = getWatchUpdateReqJsonString(watch);
        String updateInfo = getWatchUpdateJsonByReq(req);

        //测试end
        if (updateInfo != null) {
            // parse it
            try {
                org.json.JSONObject watchJo = new org.json.JSONObject(updateInfo);
                String watchVer = watch.getVerCur();
                //String watchVer = "LGA0013150709T12";
                if (watchVer != null && watchVer.length() > 7) {
                    String watchPrj = watchVer.substring(0, 7);

                    final org.json.JSONObject joUpdate = watchJo;
                    if (joUpdate != null && checkWhitesResult(joUpdate) == 1) {
                        String matchVer = null;
                        //检测vers详情
                        String desc = joUpdate.getString("description");
                        String url = null;
                        if (joUpdate.getString("oldver").equals(watchVer)) {
                            url = joUpdate.getString(CloudBridgeUtil.KEY_NAME_DOWNLOAD_URL);

                        }
                        if (url != null) {
                            final WatchUpDateInfo watchUpdateInfo = new WatchUpDateInfo();
                            watchUpdateInfo.setFullJsonStr(updateInfo);
                            watchUpdateInfo.setCurVer(watchVer);
                            watchUpdateInfo.setBtMac(getCurUser().getFocusWatch().getBtMac());
                            watchUpdateInfo.setNewVerName(joUpdate.getString("ver"));
                            watchUpdateInfo.setMd5(joUpdate.getString("md5"));

                            String name = "update.bin";
                            final File file = new File(getSaveDir(), name);
                            watchUpdateInfo.setDownloadFile(file);

                            watchUpdateInfo.setDownLoadUrl(url);

                            // 需要升级
                            recode = 1;
                            Dialog dlg = DialogUtil.CustomNormalDialog(activity,
                                    getString(R.string.check_bin_new_version),
                                    desc,
                                    new OnCustomDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    },
                                    getText(R.string.update_later).toString(),
                                    new OnCustomDialogListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (watch.isDevice710() || watch.isDevice705() || watch.isDevice703()) {
                                                //send e2e
                                                getNetService().sendWatchUpdateE2eMsg(watch.getEid());
                                                return;
                                            }
                                            //check power
//                                            if (watch.getBattery() <= 60) {
//                                                //toast
//                                                ToastUtil.showMyToast(getApplicationContext(),
//                                                        getText(R.string.device_battery_low).toString(),
//                                                        Toast.LENGTH_SHORT);
//                                            } else
                                            {
                                                startSystemUpdateActivity(activity, watchUpdateInfo, 0);
                                            }
                                        }
                                    },
                                    getText(R.string.update_now).toString());
                            if (watch.isDevice701()) {
                                dlg = DialogUtil.CustomNormalDialog(activity,
                                        getText(R.string.check_bin_new_version).toString(),
                                        desc,
                                        new OnCustomDialogListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        }, getText(R.string.donothing_text).toString());
                            }
                            if (checkWhenStart /*&& isUpdateHintTimeout(watch.getEid() + Const.KEY_NEXT_WATCH_UPDATE_HINT)*/) {
//                                setNextUpdateTimeout(watch.getEid() + Const.KEY_NEXT_WATCH_UPDATE_HINT);
                                if (!(activity == null || activity.isFinishing() || activity.isDestroyed())) {
                                    dlg.show();
                                }
                            } else {
                                LogUtil.d(TAG + "  " + "Watch update hint not timeout");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.i("cui", "recode=====" + recode);
        return recode;
    }

    private void startSystemUpdateActivity(final Context context, final WatchUpDateInfo watchUpdateInfo, int systemUpdateType) {
        // APP升级时，如果app已经下载成功了，直接启动app安装器，不在进入SystemUpdateActivity中启动安装
        if (systemUpdateType == 1 && checkAppDownloadState(false)) {
            LogUtil.d(TAG + "  " + "startSystemUpdateActivity: new App is downloaded, start it.");
            return;
        }

        Intent it = new Intent(context, SystemUpdateActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(WatchUpDateInfo.WATCH_UPDATE_INFO, watchUpdateInfo);
        it.putExtra("SystemUpdateType", systemUpdateType); // 0:手表升级 1:APP升级
        it.putExtra("CheckUpdate", -1);
        startActivity(it);
    }

    public String getAppDownloadURL() {
        String downloadURL = null;
        String updateInfo = getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                if (lastJo != null) {
                    downloadURL = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_DOWNLOAD_URL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return downloadURL;
    }

    public String getUpdateAppMD5() {
        String md5 = null;
        String updateInfo = getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                if (lastJo != null) {
                    md5 = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_MD5);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    public boolean checkUpdateFirmwareMD5(String filepath, String rightMD5) {
        String md5 = rightMD5;

        if (md5 == null) {
            return false;
        }

        String curFileMd5 = null;
        try {
            curFileMd5 = MD5.md5_file(filepath);
        } catch (Exception e) {
            LogUtil.e(TAG + "  " + "check firmware md5 fail " + e);
        }

        LogUtil.d(TAG + "  " + "DL firmware md5 " + curFileMd5 + ", Svr md5 " + md5);

        return md5.equalsIgnoreCase(curFileMd5);
    }

    public String getUpdateAppSize() {
        String size = null;
        String updateInfo = getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                if (lastJo != null) {
                    size = (String) lastJo.get(CloudBridgeUtil.KEY_NAME_UPDATE_SIZE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    public boolean checkAppDownloadState(boolean forCheck) {
        String serverUrl = getAppDownloadURL();
        //String name = getDownloadFilePathByUrl(serverUrl);
        String name = "Imibaby.apk";
        final File file = new File(getSaveDir(), name);

        if (file.exists()) {
            boolean checkMd5;
            String md5 = getUpdateAppMD5();
            String curFileMd5 = null;
            try {
                curFileMd5 = MD5.md5_file(file.getPath());
            } catch (Exception e) {
                LogUtil.e(TAG + "  " + "check app md5 fail " + e);
            }

            LogUtil.d(TAG + "  " + "DL app " + curFileMd5 + ", Svr md5 " + md5);

            if (md5 != null && md5.equalsIgnoreCase(curFileMd5)) {
                checkMd5 = true;
            } else {
                checkMd5 = false;
                file.delete();
            }

            if (forCheck) {
                return checkMd5;
            }

            if (checkMd5) {
                if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
                    Uri packageURI = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                } else {  //Android7.0之后获取uri要用contentProvider
                    Uri apkUri = FileProvider.getUriForFile(getBaseContext(), getPackageName() + ".xun.fileprovider", file);
                    installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivity(installIntent);
                return true;
            }
        }
        return false;
    }

    public boolean isWifiEnabled() {
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetInfo != null && wifiNetInfo.isConnected();
    }

    //增加强制升级和白名单
    private int checkWhitesResult(org.json.JSONObject curJo) {
        int recode = 0;
        JSONArray whites;
        try {
            whites = curJo.getJSONArray("whites");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            whites = null;
        }

        if (whites == null) {
            recode = 1;
        } else {
            if (whites.length() > 0) {

                for (int i = 0; i < whites.length(); i++) {
                    String tmp = null;
                    try {
                        tmp = whites.getString(i);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (tmp != null && tmp.equalsIgnoreCase("ALL")) {
                        recode = 1;
                        break;
                    } else if (tmp != null && tmp.equalsIgnoreCase(getCurUser().getEid())) {
                        recode = 1;
                        break;
                    }
                }
            } else {
                recode = 0;
            }
        }
        return recode;
    }

    public int checkForceResult(org.json.JSONObject curJo) {
        int force = 0;
        try {
            force = curJo.getInt("force");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return force;
    }

    public int showUpdateResult(final Context context, boolean isSkip, boolean isOnlyCheck, final boolean checkWhenStart) {

        Params param = Params.getInstance(context);
        int recode = 0;
        int checkVersion = param.getAppVersion();
        String updateInfo = getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
        if (updateInfo != null) {
            // parse it
            try {
                org.json.JSONObject lastJo = new org.json.JSONObject(updateInfo);

                final org.json.JSONObject joUpdate = lastJo;
                if (checkWhitesResult(joUpdate) == 1) {
                    int releaseVerCode = joUpdate.getInt(CloudBridgeUtil.KEY_NAME_VERSION_CODE);
                    if (releaseVerCode > checkVersion) {
                        String desc = (String) joUpdate.get(CloudBridgeUtil.KEY_NAME_VERSION_DESC);

                        // APP启动时检测到有更新，当前为WIFI时自动下载更新
                        if (checkWhenStart && isWifiEnabled()) {
                            if (!checkAppDownloadState(true)) {
                                String uri = getAppDownloadURL();
                                LogUtil.d(TAG + "  " + "DL new APP " + uri);
                                downNewVersion(uri, null);
                                return 0;
                            } else {
                                LogUtil.d(TAG + "  " + "New APP DL completed");
                            }
                        }
                        //add dialog
                        if (checkForceResult(joUpdate) == 1) {
                            recode = 2;   //强制升级，则弹出不可取消的dialog
                            if (checkWhenStart) {
                                DialogUtil.ShowCustomSystemDialog(context,
                                        getText(R.string.app_update_force).toString(),
                                        desc,
                                        null,
                                        null,
                                        new OnCustomDialogListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startSystemUpdateActivity(context, null, 1);

                                            }
                                        },
                                        getText(R.string.update_now).toString());
                            }
                        } else {
                            recode = 1;
                            if (checkWhenStart && isUpdateHintTimeout(Const.KEY_NEXT_APP_UPDATE_HINT)) {
                                setNextUpdateTimeout(Const.KEY_NEXT_APP_UPDATE_HINT);
                                Dialog dlg = DialogUtil.CustomNormalDialog(context,
                                        getText(R.string.newversion_content).toString(),
                                        desc,
                                        new OnCustomDialogListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        },
                                        getText(R.string.update_later).toString(),
                                        new OnCustomDialogListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startSystemUpdateActivity(context, null, 1);
                                            }
                                        },
                                        getText(R.string.update_now).toString());
                                dlg.show();
                            } else {
                                LogUtil.d(TAG + "  " + "APP update hint not timeout");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return recode;
    }

    private MioAsyncTask<String, Void, Boolean> downloadBinTask = null;

    public void downWatchBin(String url, final Handler mMyHandler, final String rightMD5) {

        final String serverUrl = url;

        String name = "update.bin";
        final File file = new File(getSaveDir(), name);
        final File tmpFile = new File(getSaveDir(), name + Const.SUFFIX_TMP_FILE);
        if (tmpFile.exists())
            tmpFile.delete();
        if (file.exists()) {
            //report download end
            //report ok
            if (mMyHandler != null) {
                boolean checkMd5 = checkUpdateFirmwareMD5(file.getAbsolutePath(), rightMD5);
                if (!checkMd5) {
                    if (file.exists()) file.delete();
                    if (tmpFile.exists()) tmpFile.delete();
                }
                Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_OK);
                Bundle bd = newmsg.getData();
                bd.putBoolean("checkMd5", checkMd5);
                newmsg.sendToTarget();
            }
        } else {
            if (downloadBinTask != null)
                return;

            LogUtil.e("downNewVersion start name:" + name);
            sdcardLog("ADDOWNLOAD download Url1:" + serverUrl + ":" + tmpFile.getName());
            downloadBinTask = new MioAsyncTask<String, Void, Boolean>() {
                protected Boolean doInBackground(String... params) {
                    return DownloadHelper.downloadFile(serverUrl, tmpFile, false, true,
                            new DownloadListener() {
                                public void onRedirectURL(String redirectURL) {
                                }

                                public void onDownloadHelperOffset(long offset, long total) {
                                    // 广播通知进度
                                    long cur = System.currentTimeMillis();
                                    long delta = cur - lastTime;

                                    if (offset > 0 && delta != 0) {
                                        if (mMyHandler != null) {
                                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_PROGRESS);
                                            Bundle bd = newmsg.getData();
                                            bd.putLong("offset", offset);
                                            bd.putLong("total", total);
                                            newmsg.sendToTarget();
                                        }
                                        lastTime = cur;
                                    }
                                }
                            });
                }

                protected void onCancelled() {
                    super.onCancelled();
                    downloadBinTask = null;
                    // 广播通知进度
                    if (mMyHandler != null) {
                        Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_ERROR);
                        newmsg.sendToTarget();
                    }
                    LogUtil.e("downNextRecv onCancelled name:");
                }

                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    downloadBinTask = null;
                    if (result == false) {// 广播通知进度
                        if (mMyHandler != null) {
                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_ERROR);
                            newmsg.sendToTarget();
                        }
                    } else {
                        // 广播通知进度
                        if (file.exists())
                            file.delete();
                        tmpFile.renameTo(file);
                        //report ok
                        if (mMyHandler != null) {
                            boolean checkMd5 = checkUpdateFirmwareMD5(file.getAbsolutePath(), rightMD5);
                            if (!checkMd5) {
                                if (file.exists()) file.delete();
                                if (tmpFile.exists()) tmpFile.delete();
                            }
                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_OK);
                            Bundle bd = newmsg.getData();
                            bd.putBoolean("checkMd5", checkMd5);
                            newmsg.sendToTarget();
                        }
                        // 记录消耗流量大小
                        updateDownFileSize(Const.SHARE_PREF_DOWNLOAD_NEWVERSION_SIZE, file.length());
                    }
                }

                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }
            };
            downloadBinTask.execute();
        }

    }

    private long lastTime;
    private MioAsyncTask<String, Void, Boolean> downloadTask = null;

    public void cancelDownNewVersion() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
            downloadTask = null;
        }
    }

    public void downAdImgRes(String url, String fileName) {
        sdcardLog("ADDOWNLOAD enter downAdImgRes" + url);
        final String serverUrl = url;
        String name = fileName;
        final File file = new File(getIconCacheDir(), name);
        final File tmpFile = new File(getIconCacheDir(), name + Const.SUFFIX_TMP_FILE);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        if (file.exists()) {
            sdcardLog("ADDOWNLOAD enter downAdImgRes" + file.getName());
        } else {
            sdcardLog("ADDOWNLOAD enter downAdImgRes" + file.getName() + ":" + serverUrl + ":" + tmpFile.getName());
            MioAsyncTask<String, Void, Boolean> downloadAdTask;
            LogUtil.d(TAG + "  " + "begin download " + serverUrl);
            downloadAdTask = new MioAsyncTask<String, Void, Boolean>() {
                protected Boolean doInBackground(String... params) {
                    return DownloadHelper.downloadFile(serverUrl, tmpFile, false, true,
                            new DownloadListener() {
                                public void onRedirectURL(String redirectURL) {
                                }

                                public void onDownloadHelperOffset(long offset, long total) {

                                }
                            });
                }

                protected void onCancelled() {
                    super.onCancelled();

                    //if (tmpFile.exists()) tmpFile.delete();
                    LogUtil.e("downNewVersion onCancelled");
                }

                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    LogUtil.d(TAG + "  " + "downNewVersion result " + result);
                    sdcardLog("ADDOWNLOAD download result:" + result);
                    if (result == false) {// 广播通知进度

                    } else {
                        // 广播通知进度
                        sdcardLog("ADDOWNLOAD file exists:" + file.exists());
                        if (file.exists())
                            file.delete();
                        tmpFile.renameTo(file);
                    }
                }

                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }
            };
            downloadAdTask.execute();
        }
    }

    public void downNewVersion(String url, final Handler mMyHandler) {
        final String serverUrl = url;

        String name = "Imibaby.apk";
        final File file = new File(getSaveDir(), name);
        final File tmpFile = new File(getSaveDir(), name + Const.SUFFIX_TMP_FILE);
        if (tmpFile.exists())
            tmpFile.delete();
        if (file.exists()) {
            if (mMyHandler != null) {
                //1. 安装下载的应用
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                startActivity(installIntent);

                Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_APP_OK);
                Bundle bd = newmsg.getData();
                bd.putString("filepath", file.getAbsolutePath());
                newmsg.sendToTarget();
            }
        } else {
            if (downloadTask != null) {
                downloadTask.cancel(true);
                downloadTask = null;
                LogUtil.d(TAG + "  " + "downNewVersion reset download task");
            }

            LogUtil.d(TAG + "  " + "begin download " + serverUrl);
            sdcardLog("ADDOWNLOAD download Url2:" + serverUrl + ":" + tmpFile.getName());
            downloadTask = new MioAsyncTask<String, Void, Boolean>() {
                protected Boolean doInBackground(String... params) {
                    return DownloadHelper.downloadFile(serverUrl, tmpFile, false, true,
                            new DownloadListener() {
                                public void onRedirectURL(String redirectURL) {
                                }

                                public void onDownloadHelperOffset(long offset, long total) {
                                    // 广播通知进度
                                    long cur = System.currentTimeMillis();
                                    long delta = cur - lastTime;

                                    if (offset > 0 && delta != 0) {
                                        if (mMyHandler != null) {
                                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_APP_PROGRESS);
                                            Bundle bd = newmsg.getData();
                                            bd.putLong("offset", offset);
                                            bd.putLong("total", total);
                                            newmsg.sendToTarget();
                                        }
                                        lastTime = cur;
                                    }
                                }
                            });
                }

                protected void onCancelled() {
                    super.onCancelled();
                    //downloadTask = null;
                    // 广播通知进度
                    if (mMyHandler != null) {
                        Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_APP_ERROR);
                        newmsg.sendToTarget();
                    }
                    //if (tmpFile.exists()) tmpFile.delete();
                    LogUtil.e("downNewVersion onCancelled");
                }

                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    LogUtil.d(TAG + "  " + "downNewVersion result " + result);
                    downloadTask = null;
                    if (result == false) {// 广播通知进度
                        if (mMyHandler != null) {
                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_APP_ERROR);
                            newmsg.sendToTarget();
                        }
                    } else {
                        // 广播通知进度
                        if (file.exists())
                            file.delete();
                        tmpFile.renameTo(file);
                        //report ok
                        if (mMyHandler != null) {
                            Message newmsg = mMyHandler.obtainMessage(Const.UPDATE_DOWN_APP_OK);
                            Bundle bd = newmsg.getData();
                            bd.putString("filepath", file.getAbsolutePath());
                            newmsg.sendToTarget();
                        }
                        // 记录消耗流量大小
                        updateDownFileSize(Const.SHARE_PREF_DOWNLOAD_NEWVERSION_SIZE, file.length());
                    }
                }

                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }
            };
            downloadTask.execute();
        }
    }

    private MioAsyncTask<String, Void, String> checkADInfoTask = null;

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 11:15
     * 方法描述：获取到广告的下载数据
     */
    public void getAdUpdateData() {
        LogUtil.i("getAdUpdateData" + checkADInfoTask);
        if (checkADInfoTask != null) {
            return;
        }
        //每天拉取一次的广告数据的线程
        checkADInfoTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    setNextUpdateTimeout(Const.SHARE_PREF_AD_UPDATE_TIME);
                    return PostJsonWithURLConnection(getAppAdReqJsonString("0", null), FunctionUrl.AD_PULL_DATA_URL, false, getAssets().open("dxclient_t.bks"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                checkADInfoTask = null;
                LogUtil.i("adView result" + result);

                if (result != null && result.length() > 0) {
                    int rc = 0;
                    int updateFlag = 0;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        rc = updateJson.getInt("rc");
                        org.json.JSONObject pl = updateJson.getJSONObject("pl");
                        updateFlag = pl.getInt("updateFlag");
                    } catch (Exception e) {

                    }
                    if (rc < 0) {
                        LogUtil.d(TAG + "获取广告信息失败，失败rc：" + rc);
                    } else {
                        if (updateFlag == 1) {
                            //只写保存一份完全的表。使用时候再解析判断
                            setValue(Const.SHARE_PREF_AD_UPDATE_DATA, result);
                            initAppAdDataListBySharePref();
                            //下载图片
                            int length = getAdShowList().size();
                            for (int i = 0; i < length; i++) {
                                ADShowData taskData = getAdShowList().get(i);
                                if (taskData.adType == 0) {
                                    String expira = taskData.adExpirationTime;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                                    long dataCompare = TimeUtil.compareToDiffForTwoTime(expira, dateFormat.format(new Date()));
                                    if (taskData.adIsShow == 1 && dataCompare < 0) {
                                        sdcardLog("ADDOWNLOAD updateFlag:1");
                                        downAdImgRes(taskData.adImgUrl, taskData.adId + ".jpg");
                                    }
                                }
                            }
                        } else {
                            setAdUpdateFlag(updateFlag);
                        }
                    }
                }
            }
        };
        checkADInfoTask.execute();
    }


    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 10:16
     * 方法描述：检查是否需要更新广告信息
     */
    public boolean checkNeedGetAdUpdate() {
        boolean isGetData = false;
        //每天拉取一次的领取保险数据的线程
        if (isUpdateHintTimeout(Const.SHARE_PREF_AD_UPDATE_TIME)) {
            getAdUpdateData();
            isGetData = true;
        }
        return isGetData;
    }

    private long downloadId = 0;

    public long getUpdownLoadId() {
        return downloadId;
    }

    private boolean isUpdateHintTimeout(String key) {
        String time = getStringValue(key, null);
        if (time == null) {
            return true;
        }
        return TimeUtil.getDataFromTimeStamp(time).before(new Date());
    }

    private void setNextUpdateTimeout(String key) {
        Calendar canlender = Calendar.getInstance();
        canlender.add(Calendar.HOUR, 24);
        canlender.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String stamp = dateFormat.format(canlender.getTime());
        setValue(key, stamp);
    }

    private MioAsyncTask<String, Void, String> checkWatchVerInfoTask = null;

    static public String HttpPostJsonData(String postData, String postUrl, String appKey, String timeStamp, String sign) {
        StringBuilder sData = new StringBuilder();
        HttpURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign", sign);
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            LogUtil.e("post json string length:" + ll + "bytes length" + lngth + "  sData:" + sData);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            out.write(sData.toString());
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                LogUtil.e("responseJson = " + responseJson);
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    static public String HttpGetFileData(String getUrl, File file, String appKey, String timeStamp, String sign) {
        HttpURLConnection conn = null;
        String result = null;
        LogUtil.e("http url:" + getUrl);
        try {
            //发送get请求
            URL url = new URL(getUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign", sign);
            conn.connect();
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));

                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    dos.write(buf, 0, readLen);
                }   //回复结果
                result = "1";

                if (dos != null) {
                    dos.close();
                }
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            result = "0";
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    static public String HttpGetJsonData(String getUrl, String appKey, String timeStamp, String sign) {
        HttpURLConnection conn = null;
        String result = null;
        LogUtil.e("http url:" + getUrl);
        try {
            //发送get请求
            URL url = new URL(getUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign", sign);
            conn.connect();
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                LogUtil.e("responseJson = " + responseJson);
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public void checkWatchVerInfo(final WatchData watch) {

        if (watch == null) {
            return;
        }
        if (watch.getVerCur() == null || watch.getVerCur().length() < 7) {//无效的watch
            return;
        }
        if (checkWatchVerInfoTask != null) {
            return;
        }

        checkWatchVerInfoTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    return PostJsonWithURLConnection(getWatchVerinfoReqJsonString(watch), XunKidsDomain.getInstance(getInstance()).getXunKidsOtaDomain(OVERSEAURL.WATCH_VERSION_INFO_URL), true, getAssets().open("dxclient_t.bks"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                String req = getWatchVerinfoReqJsonString(watch);
                checkWatchVerInfoTask = null;
                if (result != null && result.length() > 0) {
                    int rc = 0;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        rc = updateJson.getInt("rc");
                    } catch (Exception e) {

                    }
                    if (rc < 0) {
                        removeWatchVerInfoJsonByReq(req);
                        LogUtil.d(TAG + "  " + "no upgrade info");
                    } else {
                        putWatchVerInfoJsonByReq(req, result);
                        LogUtil.d(TAG + "  " + "get new upgrade info");
                    }
                }
                //send broadcast
                Intent itupdate = new Intent(Const.ACTION_WATCH_VERINFO_RESULT);
                sendBroadcast(itupdate);
            }
        };
        checkWatchVerInfoTask.execute();
    }

    private MioAsyncTask<String, Void, String> checkUpdateWatctTask = null;

    public void checkNeedUpdateWatch(final Context context, WatchData watch) {
        if (isUpdateHintTimeout(watch.getEid() + Const.KEY_NEXT_WATCH_UPDATE)) {
            checkUpdateWatch(context, true, watch);
        }
    }

    public void checkUpdateWatch(final Context context, final boolean checkWhenStart, final WatchData watch) {

        if (watch == null) {
            return;
        }
        if (watch.getVerCur() == null || watch.getVerCur().length() < 7) {//无效的watch
            return;
        }
        if (checkUpdateWatctTask != null) {
            return;
        }

        checkUpdateWatctTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    return PostJsonWithURLConnection(getWatchUpdateReqJsonString(watch), XunKidsDomain.getInstance(getInstance()).getXunKidsOtaDomain(OVERSEAURL.WATCH_UPGRADE_URL), true, getAssets().open("dxclient_t.bks"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                String req = getWatchUpdateReqJsonString(watch);
                checkUpdateWatctTask = null;
                if (result != null && result.length() > 0) {
                    int rc = 0;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        rc = updateJson.getInt("rc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (rc < 0) {
                        LogUtil.d(TAG + "  " + "no upgrade info");
                        removeWatchUpdateJsonByReq(req);
                    } else {
                        setNextUpdateTimeout(watch.getEid() + Const.KEY_NEXT_WATCH_UPDATE);
                        putWatchUpdateJsonByReq(req, result);
                    }
                    LogUtil.d(TAG + "  " + "get new upgrade info");

                    // for update [KEY_UPGRADE_INFO] when start the app
                    if (checkWhenStart) {
                        //send broadcast
                        Intent itupdate = new Intent(Const.ACTION_WATCH_UPGRADE_RESULT);
                        sendBroadcast(itupdate);
                        return;
                    }
                }
                //send broadcast
                Intent itupdate = new Intent(Const.ACTION_APP_UPGRADE_RESULT);
                itupdate.putExtra("type", "watch");
                sendBroadcast(itupdate);
            }
        };
        checkUpdateWatctTask.execute();
    }

    public void checkNeedUpdate(final Context context, final int CheckFlag, boolean checkWhenStart) {
        if (isUpdateHintTimeout(Const.KEY_NEXT_CHECK_UPDATE)) {
            //checkUpdate(context, CheckFlag, checkWhenStart);
        }
    }


    private MioAsyncTask<String, Void, String> checkDiscoveryWarnInfoTask = null;

    /**
     * user:zhangjun5 time:17:19 date:2018/7/12
     * desc:获取活动页面的小红点
     **/
    public void getDiscoveryWarnUpdateData() {
        LogUtil.i("getDiscoveryWarnUpdateData" + checkDiscoveryWarnInfoTask);
        if (checkDiscoveryWarnInfoTask != null) {
            return;
        }
        checkDiscoveryWarnInfoTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    LogUtil.e("DISCOVERY URL:" + FunctionUrl.AD_RED_DOT_URL);
                    return PostJsonWithURLConnection(getAppAdReqJsonString("1", TimeUtil.getTimestampCHN()), FunctionUrl.AD_RED_DOT_URL, false, getAssets().open("dxclient_t.bks"));
                } catch (Exception e) {
                    LogUtil.i(e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                checkDiscoveryWarnInfoTask = null;
                LogUtil.i("adView result" + result);

                if (result != null && result.length() > 0) {
                    int rc = 0;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        rc = updateJson.getInt("rc");
                        org.json.JSONObject pl = updateJson.getJSONObject("pl");
                        if (rc <= 0) {
                            LogUtil.d(TAG + "获取发现红点信息失败，失败rc：" + rc);
                        } else {
                            Intent intent = new Intent(Const.ACTION_BROAST_DISCOVERY_WARN);
                            intent.putExtra(Const.DISCOVERY_WARN_INFO, pl.toString());
                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        LogUtil.e(e.toString());
                    }
                }
            }
        };
        checkDiscoveryWarnInfoTask.execute();
    }

    // 获取登录页面
    public Class<?> getLoginClass() {

        int type = getIntValue(Constants.SHARE_PREF_FIELD_LOGIN_TYPE, Constants.SHARE_PREF_FIELD_LOGIN_TYPE_THRID);
        // 编译打开自有账号、且服务器配置为自有账号

        return NewLoginActivity.class;
    }

    /**
     * user:zhangjun5 time:16:17 date:2017/2/6
     * desc:计步排名数据结构体
     **/
    public String ranksDatasUpdateJson(String steps, String upload, String eid) {
        JSONObject params = new JSONObject();
        params.put("AppSteps", Integer.valueOf(steps));
        params.put("UID", getCurUser().getEid());
        params.put("UpdateTime", TimeUtil.getTimestampCHN());
        params.put("EID", eid);
        params.put("UploadType", Integer.valueOf(upload));
        return params.toJSONString();
    }

    private MioAsyncTask<String, Void, String> stepsRanksDataTask = null;

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 11:15
     * 方法描述：获取到广告的下载数据
     */
    public void ranksDatasUpdateTask(String steps, String uploadType, String eid) {
        if (stepsRanksDataTask != null) {
            return;
        }
        stepsRanksDataTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    LogUtil.i("ranks json:" + ranksDatasUpdateJson(params[0], params[1], params[2]));
                    if (getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0) {
                        LogUtil.e("url:" + Const.APP_RANK_HTTPS_TEST_URL);
                        return PostJsonWithURLConnection(ranksDatasUpdateJson(params[0], params[1], params[2]), Const.APP_RANK_HTTPS_TEST_URL, false, getAssets().open("dxclient_t.bks"));
                    } else {
                        LogUtil.e("url:" + XunKidsDomain.getInstance(getInstance()).getXunKidsStepsDomain(OVERSEAURL.STEPS_RANK_URL));
                        return PostJsonWithURLConnection(ranksDatasUpdateJson(params[0], params[1], params[2]), XunKidsDomain.getInstance(getInstance()).getXunKidsStepsDomain(OVERSEAURL.STEPS_RANK_URL), false, getAssets().open("dxclient_t.bks"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                stepsRanksDataTask = null;
                LogUtil.i("ranks result" + result);

                if (result != null && result.length() > 0) {
                    int rc = 0;
                    String eid;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        updateJson.put("TIMESTAMP", TimeUtil.getTimestampCHN());
                        rc = updateJson.getInt("RC");
                        eid = updateJson.getString("EID");
                        if (updateJson.isNull("PL")) {
                            LogUtil.d(TAG + "pl为空：" + rc);
                            return;
                        }
                        if (rc < 0) {
                            LogUtil.d(TAG + "获取排名信息失败，失败rc：" + rc);
                        } else {
                            //只写保存一份完全的表。使用时候再解析判断
                            LogUtil.i("ranks updatejson:" + updateJson.toString());
                            setValue(eid + Const.SHARE_PREF_RANKS_DATA_JSON, updateJson.toString());
                            Intent intent = new Intent(Const.ACTION_CLOUD_BRIDGE_RANKS_DATA);
                            intent.putExtra(Const.KEY_WATCH_ID, eid);
                            intent.putExtra(Const.SHARE_PREF_RANKS_DATA_JSON, updateJson.toString());
                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {

                    }

                }
            }
        };
        stepsRanksDataTask.execute(steps, uploadType, eid);
    }

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/7/5 14:36
     * 方法描述：初始化首页和开屏
     */
    private void initAdMainPageAndSplashOnOff() {
        int mainPage = getIntValue(Const.SHARE_PREF_AD_MAINPAGE_ONOFF, 0);
        if (mainPage == 0) {
            adMainAdOnOff = false;
        } else if (mainPage == 1) {
            adMainAdOnOff = true;
        }
        int splash = getIntValue(Const.SHARE_PREF_AD_SPLASH_ONOFF, 0);
        if (splash == 0) {
            adSplashOnOff = false;
        } else if (splash == 1) {
            adSplashOnOff = true;
        }
    }

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 15:23
     * 方法描述：初始化广告列表从本地保存的数据中
     */
    private void initAppAdDataListBySharePref() {
        String info = getStringValue(Const.SHARE_PREF_AD_UPDATE_DATA, null);
        LogUtil.i("initAppAdDataListBySharePref:" + info);
        if (info == null) {
            return;
        }
        try {
            org.json.JSONObject updateADJson = new org.json.JSONObject(info);

            if (updateADJson != null) {
                LogUtil.d("adShow:" + updateADJson.toString());
                org.json.JSONObject pl = updateADJson.getJSONObject("pl");
                setAdUpdateFlag(pl.getInt("updateFlag"));
                setAdUpdateTime(pl.getString("updateTime"));
                setAdInterval(pl.getInt("interval"));
                getAdShowList().clear();
                org.json.JSONObject ads = pl.getJSONObject("ads");
                org.json.JSONObject mainScreen = ads.getJSONObject("mainscreenad");
                parseJsonToAdListItem(mainScreen, 1);
                org.json.JSONArray splashScreen = ads.getJSONArray("splashad");
                for (int i = 0; i < splashScreen.length(); i++) {
                    org.json.JSONObject adDataType = splashScreen.getJSONObject(i);
                    parseJsonToAdListItem(adDataType, 0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 类名称：ImibabyApp
     * 创建人：zhangjun5
     * 创建时间：2016/6/30 15:24
     * 方法描述：根据给出的json数据，整合数据到全局的广告列表单项中
     */
    private void parseJsonToAdListItem(org.json.JSONObject json, int jsonType) {
        if (json == null) {
            return;
        }
        ADShowData showItem = new ADShowData();
        try {
            int adType = jsonType;
            String adId = null;
            String adImgUrl = null;
            String adTargUrl = json.getString("targUrl");
            String adExpirationTime = json.getString("expirationTime");
            int adIsShow = json.getInt("isShow");
            int adUrlPars = 0;

            if (adType == 0) {
                adId = json.getString("id");
                adImgUrl = json.getString("imgUrl");
                int adShowNum = json.getInt("showNum");
                int adShowTime = json.getInt("showTime");
                showItem.adShowNum = adShowNum;
                showItem.adShowTime = adShowTime;
            } else if (adType == 1) {
                adId = TimeUtil.getTimestampCHN();
            }
            showItem.adType = adType;
            showItem.adId = adId;
            showItem.adImgUrl = adImgUrl;
            showItem.adTarUrl = adTargUrl;
            showItem.adExpirationTime = adExpirationTime;
            showItem.adIsShow = adIsShow;
            showItem.adUrlPars = adUrlPars;

            adShowList.add(showItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MioAsyncTask<String, Void, String> checkUpdateTask = null;

//    public void checkUpdate(final Context context, final int CheckFlag, final boolean checkWhenStart) {
//
//        if (checkUpdateTask != null) {
//            return;
//        }
//
//        checkUpdateTask = new MioAsyncTask<String, Void, String>() {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                try {
//                    return PostJsonWithURLConnection(getAppUpdateReqJsonString(), FunctionUrl.APP_UPGRADE_URL, true, getAssets().open("dxclient_t.bks"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                checkUpdateTask = null;
//                String info = null;
//                if (result != null && result.length() > 0) {
//                    int rc = 0;
//                    try {
//                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
//                        rc = updateJson.getInt("rc");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (rc < 0) {
//                        LogUtil.d(TAG + "  " + "no app  upgrade info");
//                        setValue(Const.KEY_APP_UPGRADE_INFO, null);
//                    } else {
//                        setNextUpdateTimeout(Const.KEY_NEXT_CHECK_UPDATE);   //重置next check time
//                        //只写保存一份完全的表。使用时候再解析判断
//                        setValue(Const.KEY_APP_UPGRADE_INFO, result);
//                        info = result;
//                        LogUtil.d(TAG + "  " + "get new upgrade info");
//                    }
//                } else {
//                    info = getStringValue(Const.KEY_APP_UPGRADE_INFO, null);
//                    LogUtil.d(TAG + "  " + "use old upgrade info");
//                }
//
//                // for update [KEY_UPGRADE_INFO] when start the app
//                if (checkWhenStart) {
//                    return;
//                }
//
//                //send broadcast
//                Intent itupdate = new Intent(Const.ACTION_APP_UPGRADE_RESULT);
//                itupdate.putExtra(Const.KEY_JSON_MSG, info);
//                sendBroadcast(itupdate);
//            }
//        };
//        checkUpdateTask.execute();
//    }

    //type 1表示显示，2表示点击
    public void AdState(int type, String eid, String adid) {
        if (eid == null || adid == null) {
            return;
        }
        net.minidev.json.JSONArray statArray;
        Boolean statFlag = false;
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHH");
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        String tempkey = getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + getCurUser().getEid(), null);
        if (tempkey == null) {
            statArray = new net.minidev.json.JSONArray();
        } else {
            statArray = (net.minidev.json.JSONArray) JSONValue.parse(tempkey);
            JSONObject checkobj = (JSONObject) statArray.get(0);
            String checkTime = (String) checkobj.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
            checkTime = checkTime.substring(0, 8);
            if (!checkTime.equals(format1.format(today))) {
                String privKey = getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + eid, Const.DEFAULT_NEXT_KEY);
                if (privKey.equals(Const.DEFAULT_NEXT_KEY)) {
                    setValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + eid, tempkey);
                }
                statArray.clear();
            }
        }
        if (type == 1) {
            for (Object object : statArray) {
                JSONObject json = (JSONObject) object;
                String tempeid = (String) json.get(CloudBridgeUtil.KEY_NAME_EID);
                if (tempeid.equals(eid)) {
                    String tempTime = (String) json.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                    if (tempTime.equals(format.format(today))) {
                        json.put("version", "2");
                        JSONObject adlog = (JSONObject) json.get("adClick_log");
                        if (adlog != null) {
                            String id = (String) adlog.get("adid_" + adid);
                            if (id != null) {
                                int show = Integer.valueOf(id.substring(0, id.indexOf(","))) + 1;
                                int click = Integer.valueOf(id.substring(id.indexOf(",") + 1));
                                adlog.put("adid_" + adid, show + "," + click);
                            } else {
                                adlog.put("adid_" + adid, "1,0");
                            }
                        } else {
                            adlog = new JSONObject();
                            adlog.put("adid_" + adid, "1,0");
                        }
                        json.put("adClick_log", adlog);
                        statFlag = true;
                    }
                }
            }
            if (!statFlag) {
                JSONObject locationObject = new JSONObject();
                locationObject.put(CloudBridgeUtil.KEY_NAME_EID, eid);
                locationObject.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, format.format(today));
                locationObject.put("version", "2");
                locationObject.put("voice_recv", "0,0");
                locationObject.put("location", "0,0,0,0,0,0,0,0,0,0,0,0,0");
                locationObject.put("voice_send", "0,0,0,0,0,0,0,0,0,0,0,0,0");
                JSONObject adlog = new JSONObject();
                adlog.put("adid_" + adid, "1,0");
                locationObject.put("adClick_log", adlog);
                statArray.add(locationObject);
            }
        } else if (type == 2) {
            for (Object object : statArray) {
                JSONObject json = (JSONObject) object;
                String tempeid = (String) json.get(CloudBridgeUtil.KEY_NAME_EID);
                if (tempeid.equals(eid)) {
                    String tempTime = (String) json.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                    if (tempTime.equals(format.format(today))) {
                        json.put("version", "2");
                        JSONObject adlog = (JSONObject) json.get("adClick_log");
                        if (adlog != null) {
                            String id = (String) adlog.get("adid_" + adid);
                            if (id != null) {
                                int show = Integer.valueOf(id.substring(0, id.indexOf(",")));
                                int click = Integer.valueOf(id.substring(id.indexOf(",") + 1)) + 1;
                                adlog.put("adid_" + adid, show + "," + click);
                            } else {
                                adlog.put("adid_" + adid, "0,1");
                            }
                        } else {
                            adlog = new JSONObject();
                            adlog.put("adid_" + adid, "0,1");
                        }
                        json.put("adClick_log", adlog);
                        statFlag = true;
                    }
                }
            }
            if (!statFlag) {
                JSONObject locationObject = new JSONObject();
                locationObject.put(CloudBridgeUtil.KEY_NAME_EID, eid);
                locationObject.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, format.format(today));
                locationObject.put("version", "2");
                locationObject.put("voice_recv", "0,0");
                locationObject.put("location", "0,0,0,0,0,0,0,0,0,0,0,0,0");
                locationObject.put("voice_send", "0,0,0,0,0,0,0,0,0,0,0,0,0");
                JSONObject adlog = new JSONObject();
                adlog.put("adid_" + adid, "0,1");
                locationObject.put("adClick_log", adlog);
                statArray.add(locationObject);
            }
        }

        setValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + getCurUser().getEid(), statArray.toString());

    }

    static public String PostJsonWithURLConnection(String postData, String postUrl, boolean checkForUpdate, InputStream certificate) {

        StringBuilder sData = new StringBuilder();
        HttpsURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpsURLConnection) url.openConnection();

            // check for new app/watch version, set timeout - 3s
            if (checkForUpdate) {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            LogUtil.e("post json string length:" + ll + "bytes length" + lngth + "  sData:" + sData + " url:" + postUrl);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), Charset.forName("UTF-8"));
            out.write(sData.toString());
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.d("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                LogUtil.e("responseJson = " + responseJson);
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    /**
     * user:zhangjun5 time:15:22 date:2017/1/19
     * desc:轮询开屏广告的广告id，如果没有广告，返回########，有广告返回广告id
     **/
    public String startSplashPage() {
        String defaultString = "#########";
        String time = getStringValue(Const.CHECK_AD_INTERVAL_TIME, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        if (time == null) {// 第一次
            Date today = new Date();
            Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000L - 60 * 60 * 1000L);
            String stamp = dateFormat.format(yesterday);
            setValue(Const.CHECK_AD_INTERVAL_TIME, stamp);
            time = stamp;
        }
        if (TimeUtil.compareToDiffForTwoTime(time, dateFormat.format(new Date()))
                > getAdInterval() * 60) {// 超过时间了
            //检查是否没有开屏广告信息
            int index = 0;
            for (index = 0; index < getAdShowList().size(); index++) {
                ADShowData tmpData = getAdShowList().get(index);
                if (tmpData.adType == 0) {
                    break;
                }
            }
            if (index == getAdShowList().size()) {
                LogUtil.i("ad splash index:" + index);
                return defaultString;
            }
            String curIdShowNum = getStringValue(Const.CHECK_AD_CURID_SHOWNUM, null);
            ADShowData taskData = new ADShowData();
            if (curIdShowNum != null) {
                String[] userData = curIdShowNum.split("_");
                int i;
                for (i = 0; i < getAdShowList().size(); i++) {
                    taskData = getAdShowList().get(i);
                    if (taskData.adType == 0 && taskData.adId.equals(userData[0])) {
                        break;
                    }
                }
                if (i < getAdShowList().size()) {
                    String expira = taskData.adExpirationTime;
                    long dataCompare = TimeUtil.compareToDiffForTwoTime(expira,
                            dateFormat.format(new Date()));
                    File file = new File(getIconCacheDir(), taskData.adId + ".jpg");
                    if (taskData.adShowNum > Integer.parseInt(userData[1]) &&
                            taskData.adIsShow == 1 && dataCompare < 0 && file.exists()) {
                        setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                                (Integer.parseInt(userData[1]) + 1));
                        return taskData.adId;
                    } else {
                        setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                                0);
                        i++;
                        for (; i < getAdShowList().size(); i++) {
                            taskData = getAdShowList().get(i);
                            if (taskData.adType == 0) {
                                break;
                            }
                        }
                        expira = taskData.adExpirationTime;
                        dataCompare = TimeUtil.compareToDiffForTwoTime(expira,
                                dateFormat.format(new Date()));
                        file = new File(getIconCacheDir(), taskData.adId + ".jpg");
                        if (i < getAdShowList().size() &&
                                taskData.adIsShow == 1 && dataCompare < 0 && file.exists()) {
                            setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                                    1);
                            return taskData.adId;
                        } else {
                            for (i = 0; i < getAdShowList().size(); i++) {
                                taskData = getAdShowList().get(i);
                                if (taskData.adType == 0) {
                                    break;
                                }
                            }
                            setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                                    1);
                        }
                    }
                } else {
                    for (i = 0; i < getAdShowList().size(); i++) {
                        taskData = getAdShowList().get(i);
                        if (taskData.adType == 0) {
                            break;
                        }
                    }
                    setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                            1);
                }
            } else {
                for (int i = 0; i < getAdShowList().size(); i++) {
                    taskData = getAdShowList().get(i);
                    if (taskData.adType == 0) {
                        break;
                    }
                }
                setValue(Const.CHECK_AD_CURID_SHOWNUM, taskData.adId + "_" +
                        1);
            }
            String expira = taskData.adExpirationTime;
            long dataCompare = TimeUtil.compareToDiffForTwoTime(expira,
                    dateFormat.format(new Date()));
            File file = new File(getIconCacheDir(), taskData.adId + ".jpg");
            if (taskData.adIsShow == 0 || dataCompare > 0 || !file.exists()) {
                return defaultString;
            }
            return taskData.adId;
        } else {
            return defaultString;
        }
    }

    public void saveLoginOKResult(int newState) {
        String lastLoginId = getStringValue(Const.SHARE_PREF_FIELD_LAST_UID, null);
        if (lastLoginId != null && lastLoginId.equals(getLoginId())) {

        } else {
            resetDataAfterUserChange();
        }
        // TODO Auto-generated method stub
        lastloginState = newState;
        //setLoginEid(getCurUser().getEid());
        setValue(Const.SHARE_PREF_FIELD_LAST_PPSSWW, getLastppssww());
        setValue(Const.SHARE_PREF_FIELD_LAST_UNIONID, getLastUnionId());
        setValue(Const.SHARE_PREF_FIELD_LAST_UID, getLoginId());
        setValue(Const.SHARE_PREF_FIELD_LAST_XIAOMIID, getLoginXiaomiId());
        setValue(Const.SHARE_PREF_FIELD_LOGIN_STATE, getLastLoginState());
        setValue(Const.SHARE_PREF_FIELD_LOGIN_TOKEN, getToken());
    }

    public void resetDataAfterUserChange() {
        //reset focus watch
        if (curUser.getWatchList() != null && curUser.getWatchList().size() > 0) {

            setFocusWatch(getCurUser().getWatchList().get(0));
        } else {
            setFocusWatch(null);
        }
        //其他reset 补充在后面
    }

    private WatchData reActiveWatch = null;

    public WatchData getReActiveWatch() {
        return reActiveWatch;
    }

    public void resetReActiveWatch() {
        reActiveWatch = null;
    }

    public WatchData checkReActiveWatch(ArrayList<WatchData> newWatchList) {
        WatchData findWatch = null;
        if (newWatchList != null && newWatchList.size() > 0) {
            for (WatchData watch : newWatchList) {
                WatchData oldWatch = getCurUser().queryWatchDataByEid(watch.getEid());
                if (oldWatch != null) {
                    if (oldWatch.getSimActiveStatus() != watch.getSimActiveStatus()
                            && watch.getSimActiveStatus() == 10) {
//                  if (watch.getSimActiveStatus() == 10){
                        //复机状态
                        findWatch = watch;
                        break;
                    }
                }
            }
        }
        return findWatch;
    }

    /**
     * recode： -1,当前focucwatch被移出
     */
    public int parseJSONObjectGroups(net.minidev.json.JSONArray joArray) {
        // TODO Auto-generated method stub
        resetReActiveWatch();
        int recode = 0;
        ArrayList<FamilyData> familyList = new ArrayList<FamilyData>();
        int groupNum;
        if (joArray != null) {
            groupNum = joArray.size();
            for (int i = 0; i < groupNum; i++) {

                JSONObject groupJO = (JSONObject) joArray.get(i);
                FamilyData family = new FamilyData();
                String gid = (String) groupJO.get(CloudBridgeUtil.KEY_NAME_GID);
                family.setFamilyId(gid);
                String adminEid = (String) groupJO.get(CloudBridgeUtil.KEY_NAME_ADMIN_EID);
                family.setAdminEId(adminEid);
                saveFamilyAdminEid(family, adminEid);
                ArrayList<WatchData> watchlist = new ArrayList<WatchData>();
                ArrayList<MemberUserData> memberList = new ArrayList<MemberUserData>();
                net.minidev.json.JSONArray endpoints = (net.minidev.json.JSONArray) groupJO.get(CloudBridgeUtil.KEY_NAME_ENDPOINTS);
                int endpointsSize = endpoints.size();
                for (int j = 0; j < endpointsSize; j++) {
                    JSONObject memberJO = (JSONObject) endpoints.get(j);
                    int type = (Integer) memberJO.get(CloudBridgeUtil.KEY_NAME_TYPE);
                    if (type == CloudBridgeUtil.VALUE_TYPE_ENDPOINT_USER) {
                        MemberUserData member = new MemberUserData();
                        member.setEid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EID));
                        member.setFamilyId(gid);
                        member.setNickname((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
                        member.setUid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NAME));
                        member.setCellNum((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CELLPHONE));
                        Object xiaomiIdStr = memberJO.get(CloudBridgeUtil.KEY_NAME_XIAOMIID);
                        if (xiaomiIdStr != null) {
                            member.setXiaomiId((String) xiaomiIdStr);
                        }
//使用custom 字段
                        member.getCustomData().setFromJsonStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM));
                        //                       member.parseRelationStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_ALIAS));
                        //set my curuser
                        if (member.getHeadPath() == null) {
                            member.setHeadPath(Integer.valueOf(relationSels.size() - 1).toString());
                        }
                        if (member.getEid().equals(getCurUser().getEid())) {
                            getCurUser().setNickname(member.getNickname());
                            getCurUser().setHeadPath(member.getHeadPath());
                            getCurUser().setUid(member.getUid());
                            getCurUser().setCellNum(member.getCellNum());
                            getCurUser().setXiaomiId(member.getXiaomiId());
                            getCurUser().getCustomData().setFromJsonStr(member.getCustomData().toJsonStr());

                        }
                        memberList.add(member);
                    } else {
                        WatchData watch = new WatchData();
                        watch.setWatchId((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NAME));
                        watch.setEid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EID));
                        watch.setFamilyId(gid);

                        watch.setBtMac((String) memberJO.get(CloudBridgeUtil.KEY_NAME_BT_MAC));
                        watch.setIccid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_ICCID));
                        watch.setCellNum((String) memberJO.get(CloudBridgeUtil.KEY_NAME_SIM_NO));
                        watch.setImei((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMEI));
                        watch.setImsi((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMSI));
                        watch.setNickname((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
                        watch.setVerCur((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_CUR));
                        watch.setVerOrg((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_ORG));
                        watch.setExpireTime((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EXPIRE_TIME));
                        watch.setIccidQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_ICCID_ENCRYPT));
                        watch.setImsiQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMSI_ENCRYPT));
                        watch.setImeiQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMEI_ENCRYPT));
                        watch.setQrStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_QR_STR));

                        Object weightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT);
                        Object heightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT);
                        Object birthStr = memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH);
                        Object sexStr = memberJO.get(CloudBridgeUtil.KEY_NAME_SEX);
                        Object simCertiObj = memberJO.get(CloudBridgeUtil.KEY_SIM_SEARCH_IDENTITY_STATUS);
                        Object simActiveObj = memberJO.get(CloudBridgeUtil.KEY_SIM_SEARCH_ACCOUNT_STATUS);
                        Object brandStr = memberJO.get(CloudBridgeUtil.KEY_NAME_XMPL);

                        Object deviceType = memberJO.get(CloudBridgeUtil.KEY_DEVICE_TYPE);
                        Object machSN = memberJO.get(CloudBridgeUtil.KEY_NAME_MACH_SN);
                        //使用custom 字段
                        watch.getCustomData().setFromJsonStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM));
                        try {
                            if (brandStr != null) {
                                watch.setBrandType((String) brandStr);
                            }
                            if (birthStr != null)
                                watch.setBirthday((String) memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH));
                            if (weightStr != null)
                                watch.setWeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT));
                            if (heightStr != null)
                                watch.setHeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT));
                            if (sexStr != null)
                                watch.setSex((Integer) memberJO.get(CloudBridgeUtil.KEY_NAME_SEX));
                            if (simCertiObj != null)
                                watch.setSimCertiStatus((Integer) memberJO.get(CloudBridgeUtil.KEY_SIM_SEARCH_IDENTITY_STATUS));
                            if (simActiveObj != null)
                                watch.setSimActiveStatus((Integer) memberJO.get(CloudBridgeUtil.KEY_SIM_SEARCH_ACCOUNT_STATUS));
                            if (deviceType != null)
                                watch.setDeviceType((String) deviceType);
                            if (machSN != null)
                                watch.setMachSn((String) machSN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        watch.setCurLocation(LocationDAO.getInstance(getApplicationContext()).readLocation(watch.getEid()));
                        watchlist.add(watch);
                    }

                }
                if (watchlist.size() > 0 && memberList.size() > 0) {
                    family.setUserList(memberList);
                    family.setWatchlist(watchlist);
                    family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    family.setDescription(StrUtil.genFamilyDesc(family, getApplicationContext()));
                    familyList.add(family);
                }
            }
            if (familyList.size() > 0) {
                ArrayList<WatchData> curWatchList = new ArrayList<WatchData>();
                boolean keyflag = false;
                for (FamilyData curFamily : familyList) {
                    curWatchList.addAll(curFamily.getWatchlist());
                    if (getStringValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)
                            || getStringValue(Const.SHARE_PREF_FIELD_NEXT_FAMILY_CHANGE_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)
                            || getStringValue(Const.SHARE_PREF_FIELD_NEXT_WARNING_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                        keyflag = true;
                    }
                    setNextContentKey(curFamily, getStringValue(Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY));
                    setNextFamilyChangeNotifyKey(curFamily, getStringValue(Const.SHARE_PREF_FIELD_NEXT_FAMILY_CHANGE_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY));
                    setNextWarningKey(curFamily, getStringValue(Const.SHARE_PREF_FIELD_NEXT_WARNING_KEY + curFamily.getFamilyId(), Const.DEFAULT_NEXT_KEY));
                }
                if (getStringValue(getCurUser().getEid() + Const.SHARE_PREF_FIELD_NEXT_CONTENT_KEY, Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                    keyflag = true;
                }
                if (keyflag == true) {
                    GetServiceTime(mNetService);
                }
                //setcuruser
                //过滤一下是否有reactivewatch
                reActiveWatch = checkReActiveWatch(curWatchList);
                getCurUser().setWatchList(curWatchList);

                //refresh curuser
                getCurUser().setFamilyList(familyList);

                //save db
                saveAllFamilyToDB();

                fillWatchListDetail(getCurUser().getWatchList());
                for (FamilyData family : getCurUser().getFamilyList()) {
                    fillWatchListDetail(family.getWatchlist());
                }

                if (getCurUser().getFocusWatch() == null) {
                    setFocusWatch(curWatchList.get(0));
                } else {
                    boolean find = false;
                    for (WatchData curWatch : curWatchList) {
                        if (curWatch.equals(getCurUser().getFocusWatch())) {
                            setFocusWatch(curWatch);
                            find = true;
                            break;
                        }
                    }
                    if (find == false) {
                        setFocusWatch(curWatchList.get(0));
                        recode = -1;//当前focucwatch被移出
                    }
                }
                if (getLastLoginState() == Const.LOGIN_STATE_LOGIN) {
                    getCurUser().setIsValidFamilys(1);
                    saveIsValidFamily(1);
                }
            } else {
                //空family也应该覆盖旧的数据
                getCurUser().setIsValidFamilys(0);
                saveIsValidFamily(0);
                //如果列表时null，设置成空列表
                if (getCurUser().getFamilyList() == null) {
                    getCurUser().setFamilyList(new ArrayList<FamilyData>());
                }
                if (getCurUser().getWatchList() == null) {
                    getCurUser().setWatchList(new ArrayList<WatchData>());
                }
            }
        }
        return recode;
    }

    public void GetServiceTime(MsgCallback callback) {
        MyMsgData queryMsg = new MyMsgData();
        queryMsg.setCallback(callback);
        queryMsg.setReqMsg(obtainCloudMsgContent(CloudBridgeUtil.CID_GETTIME, null));
        mNetService.sendNetMsg(queryMsg);
    }


    public ArrayList<WatchGroupMemberData> readWatchGroupsStringValue(String key) {
        SharedPreferences prefs = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        try {
            ArrayList<WatchGroupMemberData> memberList = new ArrayList<WatchGroupMemberData>();
            JSONArray jsonArray = new JSONArray(prefs.getString(key, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {

                //JSONObject  memberJO  = (JSONObject) jsonArray.get(i);  
                org.json.JSONObject memberJO = jsonArray.getJSONObject(i);
                WatchGroupMemberData member = new WatchGroupMemberData();
                member.setEid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EID));
                member.setGid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_GID));
                member.setNick((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
                member.setHeadpath((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM_HEADKEY));

                memberList.add(member);

            }
            return memberList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FamilyData parseGroupEndpoints(net.minidev.json.JSONArray members, String gid) {
        // TODO Auto-generated method stub
        FamilyData family = new FamilyData();
        family.setFamilyId(gid);
        ArrayList<WatchData> watchlist = new ArrayList<WatchData>();
        ArrayList<MemberUserData> memberList = new ArrayList<MemberUserData>();
        int count = 0;
        int size = members.size();
        while (count < size) {
            JSONObject memberJO = (JSONObject) members.get(count);
            int type = (Integer) memberJO.get(CloudBridgeUtil.KEY_NAME_TYPE);
            if (type == CloudBridgeUtil.VALUE_TYPE_ENDPOINT_WATCH) {
                WatchData watch = new WatchData();
                watch.setWatchId((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NAME));
                watch.setEid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EID));
                watch.setFamilyId(gid);

                watch.setBtMac((String) memberJO.get(CloudBridgeUtil.KEY_NAME_BT_MAC));
                watch.setIccid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_ICCID));
                watch.setCellNum((String) memberJO.get(CloudBridgeUtil.KEY_NAME_SIM_NO));
                watch.setImei((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMEI));
                watch.setImsi((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMSI));
                watch.setNickname((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
                watch.setVerCur((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_CUR));
                watch.setVerOrg((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_ORG));
                watch.setExpireTime((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EXPIRE_TIME));

                watch.setIccidQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_ICCID_ENCRYPT));
                watch.setImsiQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMSI_ENCRYPT));
                watch.setImeiQRCode((String) memberJO.get(CloudBridgeUtil.KEY_NAME_WATCH_IMEI_ENCRYPT));
                watch.setQrStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_QR_STR));
                Object weightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT);
                Object heightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT);
                Object birthStr = memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH);
                Object sexStr = memberJO.get(CloudBridgeUtil.KEY_NAME_SEX);
                //使用custom 字段
                watch.getCustomData().setFromJsonStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM));
                try {
                    if (birthStr != null)
                        watch.setBirthday((String) memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH));
                    if (weightStr != null)
                        watch.setWeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT));
                    if (heightStr != null)
                        watch.setHeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT));
                    if (sexStr != null)
                        watch.setSex((Integer) memberJO.get(CloudBridgeUtil.KEY_NAME_SEX));
                } catch (Exception e) {
                    // TODO: handle exception
                }

                watch.setCurLocation(LocationDAO.getInstance(getApplicationContext()).readLocation(watch.getEid()));
                watchlist.add(watch);
            } else if (type == CloudBridgeUtil.VALUE_TYPE_ENDPOINT_USER) {
                MemberUserData member = new MemberUserData();
                member.setEid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EID));
                member.setFamilyId(gid);
                member.setNickname((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
                member.setUid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NAME));
                member.setCellNum((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CELLPHONE));
                Object xiaomiIdStr = memberJO.get(CloudBridgeUtil.KEY_NAME_XIAOMIID);
                if (xiaomiIdStr != null) {
                    member.setXiaomiId((String) xiaomiIdStr);
                }
//使用custom 字段
                member.getCustomData().setFromJsonStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM));
                //                       member.parseRelationStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_ALIAS));
                //set my curuser
                if (member.getHeadPath() == null) {
                    member.setHeadPath(Integer.valueOf(relationSels.size() - 1).toString());
                }
                if (member.getEid().equals(getCurUser().getEid())) {
                    member.setNickname(getCurUser().getNickname());
                    getCurUser().setHeadPath(member.getHeadPath());
                    getCurUser().setUid(member.getUid());
                    getCurUser().getCustomData().setFromJsonStr(member.getCustomData().toJsonStr());

                }
                memberList.add(member);
            }
            count++;
        }
        family.setUserList(memberList);
        family.setWatchlist(watchlist);
        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
        family.setDescription(StrUtil.genFamilyDesc(family, getApplicationContext()));

        return family;
    }

    public void parseDevicePl(WatchData watch, JSONObject memberJO) {
        if (watch != null && memberJO != null) {
            watch.setBtMac((String) memberJO.get(CloudBridgeUtil.KEY_NAME_BT_MAC));
//            watch.setIccid((String) memberJO.get(CloudBridgeUtil.KEY_NAME_ICCID));

            watch.setNickname((String) memberJO.get(CloudBridgeUtil.KEY_NAME_NICKNAME));
            watch.setVerCur((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_CUR));
            watch.setVerOrg((String) memberJO.get(CloudBridgeUtil.KEY_NAME_VERSION_ORG));
            watch.setExpireTime((String) memberJO.get(CloudBridgeUtil.KEY_NAME_EXPIRE_TIME));
            Object weightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT);
            Object heightStr = memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT);
            Object birthStr = memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH);
            Object sexStr = memberJO.get(CloudBridgeUtil.KEY_NAME_SEX);
            Object simNo = memberJO.get(CloudBridgeUtil.KEY_NAME_SIM_NO);
            //使用custom 字段
            watch.getCustomData().setFromJsonStr((String) memberJO.get(CloudBridgeUtil.KEY_NAME_CUSTOM));
            try {
                if (birthStr != null)
                    watch.setBirthday((String) memberJO.get(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH));
                if (weightStr != null)
                    watch.setWeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_WEIGHT));
                if (heightStr != null)
                    watch.setHeight((Double) memberJO.get(CloudBridgeUtil.KEY_NAME_HEIGHT));
                if (sexStr != null)
                    watch.setSex((Integer) memberJO.get(CloudBridgeUtil.KEY_NAME_SEX));
                if (simNo != null)
                    watch.setCellNum((String) memberJO.get(CloudBridgeUtil.KEY_NAME_SIM_NO));
            } catch (Exception e) {
                // TODO: handle exception
            }

            watch.setCurLocation(LocationDAO.getInstance(getApplicationContext()).readLocation(watch.getEid()));
        }
    }

    public void addAllFamilysToDB(List<FamilyData> familyList) {
        // TODO Auto-generated method stub
        int groupNum = familyList.size();
        for (int i = 0; i < groupNum; i++) {

            //save admin to prefrence

            FamilyData family = familyList.get(i);
            String gid = family.getFamilyId();
            ArrayList<MemberUserData> memberList = family.getMemberList();
            ArrayList<WatchData> watchlist = family.getWatchlist();

            int memberSize = memberList.size();
            int watchSize = watchlist.size();

            for (int j = 0; j < watchSize; j++) {
                WatchData watch = watchlist.get(j);
                for (int k = 0; k < memberSize; k++) {
                    MemberUserData member = memberList.get(k);
                    UserRelationDAO.getInstance(getApplicationContext()).addUserRelation(member, gid, watch.getWatchId(), watch.getNickname(), member.getCustomData().toJsonStr());
                }
                WatchDAO.getInstance(getApplicationContext()).addWatch(watch);

                Date date = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddd");
                String today = df.format(date);
                String lastDay = getStringValue(Const.SHARE_PREF_UPDATE_DEVICE_CONTACT_TIME_KEY + watch.getEid(), Const.MASK_TIME_KEY);
                if (!today.equals(lastDay)) {
                    Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                    it.putExtra("eid", watch.getEid());
                    sendBroadcast(it);
                    setValue(Const.SHARE_PREF_UPDATE_DEVICE_CONTACT_TIME_KEY + watch.getEid(), today);
                }
            }
        }
    }


    boolean isNeedInvalidFamilyDialog = false;

    public void setIsNeedInvalidFamilyDialog(boolean isNeedInvalidFamilyDialog) {
        this.isNeedInvalidFamilyDialog = isNeedInvalidFamilyDialog;
    }

    boolean isMainActivityOpen = false;

    public void setMainActivityOpen(boolean flag) {
        isMainActivityOpen = flag;
    }

    public boolean getIsMainActivityOpen() {
        return isMainActivityOpen;
    }

    boolean isSystemUpdateActivityOpen = false;

    public void setSystemUpdateActivityOpen(boolean flag) {
        isSystemUpdateActivityOpen = flag;
    }

    public boolean getIsSystemUpdateActivityOpen() {
        return isSystemUpdateActivityOpen;
    }

    public HashMap<String, ArrayList<WarningInfo>> getmWarningMsg() {
        return mWarningMsg;
    }

    public String getLastppssww() {
        return lastppssww;
    }

    public void setLastppssww(String lastppssww) {
        this.lastppssww = lastppssww;
    }

    public String getLastUnionId() {
        return lastUnionId;
    }

    public void setLastUnionId(String lastUnionId) {
        this.lastUnionId = lastUnionId;
    }

    public void saveAllFamilyToDB() {
        UserRelationDAO.getInstance(getApplicationContext()).cleanAll();
        WatchDAO.getInstance(getApplicationContext()).cleanAll();
        //add
        addAllFamilysToDB(getCurUser().getFamilyList());
    }

    public boolean isCurrentRunningForeground = true;

    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        if (appProcessInfos == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    LogUtil.d("EntryActivity isRunningForeGround : " + this.getApplicationInfo().processName);
                    return true;
                }
            }
        }
        LogUtil.d("EntryActivity isRunningBackGround");
        return false;
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationHelper.getInstance(this).init();
        }
    }

    public void cleanTransNotice() {
        NotificationManager notifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMng.cancelAll();
    }

    public void cleanNotice(String tag, int id) {
        NotificationManager notifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMng.cancel(tag, id);
    }

    public String getWatchPrivateGid(String mTargetEid) {
        String contactkey = getStringValue(mTargetEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, Const.DEFAULT_NEXT_KEY);
        String mUserChatGid = null;
        if (!contactkey.equals(Const.DEFAULT_NEXT_KEY)) {
            ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(contactkey);
            for (PhoneNumber phoneNumber : mBindWhiteList) {
                if (phoneNumber.userEid != null && phoneNumber.userEid.equals(getCurUser().getEid())) {
                    mUserChatGid = phoneNumber.userGid;
                    break;
                }
            }
        }
        return mUserChatGid;
    }

    public String getUserNumberByEid(String mTargetEid) {
        String contactkey = getStringValue(mTargetEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, Const.DEFAULT_NEXT_KEY);
        String number = null;
        if (!contactkey.equals(Const.DEFAULT_NEXT_KEY)) {
            ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(contactkey);
            for (PhoneNumber phoneNumber : mBindWhiteList) {
                if (phoneNumber.userEid != null && phoneNumber.userEid.equals(getCurUser().getEid())) {
                    number = phoneNumber.number;
                    break;
                }
            }
        }
        return number;
    }

    public String getUserAvatarByEid(String watchEid, String userEid) {
        String contactkey = getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, Const.DEFAULT_NEXT_KEY);
        String avatar = null;
        if (!contactkey.equals(Const.DEFAULT_NEXT_KEY)) {
            ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(contactkey);
            for (PhoneNumber phoneNumber : mBindWhiteList) {
                if (phoneNumber.userEid != null && phoneNumber.userEid.equals(userEid)) {
                    avatar = phoneNumber.avatar;
                    break;
                }
            }
        }
        return avatar;
    }

    public int getUserAttriByEid(String watchEid, String userEid) {
        String contactkey = getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, Const.DEFAULT_NEXT_KEY);
        int attri = 1000;
        if (!contactkey.equals(Const.DEFAULT_NEXT_KEY)) {
            ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(contactkey);
            for (PhoneNumber phoneNumber : mBindWhiteList) {
                if (phoneNumber.userEid != null && phoneNumber.userEid.equals(userEid)) {
                    attri = phoneNumber.attri;
                    break;
                }
            }
        }
        return attri;
    }

    private String mGroupMsgOpenGid = null;

    public void setGroupMsgOpenGid(String gid) {
        mGroupMsgOpenGid = gid;
    }

    public String getGroupMsgOpenGid() {
        return mGroupMsgOpenGid;
    }

    private String mPrivateMsgOpenEid = null;

    public void setPrivateMsgOpenEid(String eid) {
        mPrivateMsgOpenEid = eid;
    }

    public String getPrivateMsgOpenEid() {
        return mPrivateMsgOpenEid;
    }

    private String mNoticeMsgOpenGid = null;

    public void setNoticeMsgOpenGid(String gid, int type) {
        mNoticeMsgOpenGid = gid + type;
    }

    public String getNoticeMsgOpenGid() {
        return mNoticeMsgOpenGid;
    }

    private boolean isMsgPage = false;

    public void setIsMsgPage(boolean isPage) {
        isMsgPage = isPage;
    }

    public boolean getIsMsgPage() {
        return isMsgPage;
    }

    public void recvMsgNotify(String strType, String strContent, int id, Class<?> cls, boolean isRootActivity) {
        recvMsgNotify(null, strType, strContent, id, null, cls, isRootActivity);
    }

    public void recvMsgNotify(String Eid, String strType, String strContent, int id, String familyIdTag, Class<?> cls) {
        recvMsgNotify(Eid, strType, strContent, id, familyIdTag, cls, true);
    }

    public void recvMsgNotify(String Eid, String strType, String strContent, int id, String familyIdTag, Class<?> cls, boolean isRootActivity) {
        int allMsgCount = 0;//其他类型通知每个id只算一条
        if (id == Const.TITLE_BAR_NEW_GROUP_MESSAGE) {
            if (familyIdTag != null && getGroupMsgOpenGid() != null && getGroupMsgOpenGid().equals(familyIdTag)) {
                return;
            }
            if (familyIdTag != null && getIsMsgPage()) {
                return;
            }
            allMsgCount = getNewGroupMsgCount(familyIdTag);
        } else if (id == Const.TITLE_BAR_NEW_PRIVATE_MESSAGE) {
            if (familyIdTag != null && getPrivateMsgOpenEid() != null && getWatchPrivateGid(getPrivateMsgOpenEid()).equals(familyIdTag)) {
                return;
            }
            if (familyIdTag != null && getIsMsgPage()) {
                return;
            }
            allMsgCount = getNewPrivateMsgCount(familyIdTag);
        } else if (id >= Const.TITLE_BAR_NEW_NOTICE_MESSAGE) {
            int noticeType = id - Const.TITLE_BAR_NEW_NOTICE_MESSAGE;
            if (familyIdTag != null && getNoticeMsgOpenGid() != null && getNoticeMsgOpenGid().equals(familyIdTag + noticeType)) {
                return;
            }
            if (familyIdTag != null && getIsMsgPage()) {
                return;
            }
            allMsgCount = getNewNoticeMsgCount(familyIdTag);
        }
        NotificationManager notifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT < 26) {
            builder = new Notification.Builder(getApplicationContext());
        } else {
            builder = new Notification.Builder(getApplicationContext(), NotificationHelper.IMIBEAN_CHANNEL);
        }
        Notification notification;
        if (cls != null) {
            Intent[] intents = new Intent[2];
            intents[0] = new Intent(this, NewMainActivity.class);
            intents[1] = new Intent(this, cls);
            if (Eid != null) {
                intents[1].putExtra(Const.KEY_WATCH_ID, Eid);
            }
            if (id == Const.TITLE_BAR_NEW_PRIVATE_MESSAGE) {
                intents[1].putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, familyIdTag);
            } else if (id >= Const.TITLE_BAR_NEW_NOTICE_MESSAGE) {
                int noticeType = id - Const.TITLE_BAR_NEW_NOTICE_MESSAGE;
                intents[1].putExtra(AllMessageAdapter.MESSAGE_TYPE, noticeType);
            }
            PendingIntent pendIntent = PendingIntent.getActivities(this, (int) System.currentTimeMillis(), intents, PendingIntent.FLAG_IMMUTABLE);

            notification = builder
                    .setTicker(strType).setSmallIcon(R.drawable.ic_launcher).setContentTitle(strType)
                    .setContentText(strContent).setContentIntent(pendIntent).setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true).build();

        } else {
            Intent it = new Intent(Const.ACTION_NOTIFICATION_CLEAR);
            it.putExtra(Const.KEY_NOTIFICATION_ID, id);
            PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0, it, 0);
            notification = builder.setTicker(strType)
                    .setSmallIcon(R.drawable.ic_launcher).setContentTitle(strType).setPriority(Notification.PRIORITY_HIGH)
                    .setContentText(strContent).setAutoCancel(true).setContentIntent(pendIntent).build();
        }
        try {

            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, allMsgCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.icon = R.drawable.ic_launcher;
        notification.defaults = /*Notification.DEFAULT_LIGHTS |*/ Notification.DEFAULT_VIBRATE;
        if (getPackageName().equals("com.imibaby.client")) {
            notification.sound = Uri.parse("android.resource://com.imibaby.client/" + R.raw.beep);
        } else if (getPackageName().equals("com.xiaoxun.xunoversea")) {
            notification.sound = Uri.parse("android.resource://com.xiaoxun.xunoversea/" + R.raw.beep);
        } else if (getPackageName().equals("com.xiaotongren.watch")) {
            notification.sound = Uri.parse("android.resource://com.xiaotongren.watch/" + R.raw.beep);
        }
        notification.ledARGB = /*0x0000ff00*/Color.GREEN;
        notification.ledOnMS = 1 * 1000;
        notification.ledOffMS = 1 * 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        if (familyIdTag != null) {
            notifyMng.cancel(familyIdTag, id);
            notifyMng.notify(familyIdTag, id, notification);
        } else {
            notifyMng.cancel(id);
            notifyMng.notify(id, notification);
        }
    }

    //头像相关操作 start
    public static List<RelationSel> relationSels = new ArrayList<RelationSel>(5);

    private void initRelationSels() {
        relationSels.clear();
        relationSels.add(new RelationSel(R.drawable.relation_0, R.string.relation_0));
        relationSels.add(new RelationSel(R.drawable.relation_1, R.string.relation_1));
        relationSels.add(new RelationSel(R.drawable.relation_2, R.string.relation_2));
        relationSels.add(new RelationSel(R.drawable.relation_3, R.string.relation_3));
        relationSels.add(new RelationSel(R.drawable.relation_4, R.string.relation_4));
        relationSels.add(new RelationSel(R.drawable.relation_5, R.string.relation_5));

        relationSels.add(new RelationSel(R.drawable.relation_4, R.string.relation_6));
        relationSels.add(new RelationSel(R.drawable.relation_5, R.string.relation_7));
        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_8));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_9));
        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_10));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_11));

        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_12));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_13));
        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_14));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_15));
        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_16));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_17));

        relationSels.add(new RelationSel(R.drawable.relation_6, R.string.relation_18));
        relationSels.add(new RelationSel(R.drawable.relation_7, R.string.relation_19));
        relationSels.add(new RelationSel(R.drawable.relation_8, R.string.relation_20));
        relationSels.add(new RelationSel(R.drawable.relation_9, R.string.relation_21));
        relationSels.add(new RelationSel(R.drawable.relation_8, R.string.relation_22));
        relationSels.add(new RelationSel(R.drawable.relation_9, R.string.relation_23));

        relationSels.add(new RelationSel(R.drawable.relation_custom, R.string.relation_other));
    }

    public List<RelationSel> getRelationSels() {
        return relationSels;
    }

    private AsyncImageLoader imgLoader;

    public void initImgLoader() {
        if (imgLoader == null) {
            imgLoader = new AsyncImageLoader(this);
        }

    }

    private void clearLocalData(boolean force) {
        int needclear = getIntValue("need_clear", 0);
        if ((needclear == 0 || force) && curUser.getFocusWatch() != null) {
            setValue(curUser.getFocusWatch().getEid() + Const.SHARE_PREF_SECURITY_ZONE_KEY, null);
            setValue("need_clear", 1);
        }

    }

    public void initLocationEfence() {

        if (curUser.getWatchList() != null && curUser.getWatchList().size() > 0) {
            clearLocalData(false);
            for (WatchData watch : curUser.getWatchList()) {
                String data = getStringValue(watch.getEid() + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY, "");

                if (data != null && !data.equals("")) {
                    int flag = 0;
                    ArrayList<EFence> efence = new ArrayList<EFence>();
                    JSONObject pl = (JSONObject) JSONValue.parse(data);
                    net.minidev.json.JSONArray arr = (net.minidev.json.JSONArray) pl.get("list");
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject item = (JSONObject) arr.get(i);
                        try {
                            EFence fence = new EFence();
                            String sCenter = (String) item.get("Center_amap");
                            fence.efid = (String) item.get("Efid");
                            fence.eid = watch.getEid();
                            fence.desc = (String) item.get("Info");
                            fence.lat = Double.parseDouble(sCenter.substring(
                                    sCenter.indexOf("(") + 1, sCenter.indexOf(",")));
                            fence.lng = Double.parseDouble(sCenter.substring(
                                    sCenter.indexOf(",") + 1, sCenter.indexOf(")")));
                            fence.radius = (Integer) item.get("Radius");
                            fence.name = (String) item.get("Name");
                            efence.add(fence);
                        } catch (Exception e) {
                            LogUtil.i("exception:" + e);
                        }
                        if (efence.size() > 0 || flag == 2) {
                            getmWatchEFence().put(watch.getEid(), efence);
                        }
                    }
                }
            }
        }
    }

    public Drawable getHeadDrawableByFile(Resources res, String filepath, String eid, int defaultId) {
        return getHeadDrawableByFile(res, filepath, eid, defaultId, true);
    }

    //兼容 head id的方式 head id目前
    public Drawable getHeadDrawableByFile(Resources res, String filepath, String eid, int defaultId, boolean autoDown) {

        Drawable drawable = null;
        if (filepath != null) {
            //head id 方式
            if (filepath.length() < 32) {
                int resId = 0;
                //if ()
                if (filepath.equals("0")) {
                    resId = relationSels.get(0).ImageId;
                } else if (filepath.equals("1")) {
                    resId = relationSels.get(1).ImageId;
                } else if (filepath.equals("2")) {
                    resId = relationSels.get(2).ImageId;
                } else if (filepath.equals("3")) {
                    resId = relationSels.get(3).ImageId;
                } else if (filepath.equals("4")) {
                    resId = relationSels.get(4).ImageId;
                } else if (filepath.equals("5")) {
                    resId = relationSels.get(5).ImageId;
                } else if (filepath.equals("6")) {
                    resId = relationSels.get(6).ImageId;
                } else if (filepath.equals("7")) {
                    resId = relationSels.get(7).ImageId;
                } else if (filepath.equals("8")) {
                    resId = relationSels.get(8).ImageId;
                } else if (filepath.equals("9")) {
                    resId = relationSels.get(9).ImageId;
                } else if (filepath.equals("10")) {
                    resId = relationSels.get(10).ImageId;
                } else if (filepath.equals("11")) {
                    resId = relationSels.get(11).ImageId;
                } else if (filepath.equals("12")) {
                    resId = relationSels.get(12).ImageId;
                } else if (filepath.equals("13")) {
                    resId = relationSels.get(13).ImageId;
                } else if (filepath.equals("14")) {
                    resId = relationSels.get(14).ImageId;
                } else if (filepath.equals("15")) {
                    resId = relationSels.get(15).ImageId;
                } else if (filepath.equals("16")) {
                    resId = relationSels.get(16).ImageId;
                } else if (filepath.equals("17")) {
                    resId = relationSels.get(17).ImageId;
                } else if (filepath.equals("18")) {
                    resId = relationSels.get(18).ImageId;
                } else if (filepath.equals("19")) {
                    resId = relationSels.get(19).ImageId;
                } else if (filepath.equals("20")) {
                    resId = relationSels.get(20).ImageId;
                } else if (filepath.equals("21")) {
                    resId = relationSels.get(21).ImageId;
                } else if (filepath.equals("22")) {
                    resId = relationSels.get(22).ImageId;
                } else if (filepath.equals("23")) {
                    resId = relationSels.get(23).ImageId;
                } else if (filepath.equals("1000")) {
                    resId = R.drawable.relation_custom;
                } else {
                    resId = R.drawable.relation_custom;//relationSels.get(24).ImageId;
                }
                drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(res, resId));
            } else {
                if (getNetService() != null) {
                    drawable = imgLoader.load(defaultId, this, filepath, eid, this, autoDown);
                }
            }
        }
        if (drawable == null) {
            drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(res, defaultId));
        }
        return drawable;
    }

    //end
    public HashMap<String, HashMap<String, ArrayList<ChatMsgEntity>>> getmSosCollectList() {
        return mSosCollectList;
    }

    public HashMap<String, SosWarning> getmSosWarning() {
        return mSosWarning;
    }

    @Override
    public void imageLoaded(Object context, Drawable imageDrawable,
                            String imageUrl) {

    }

    public boolean needAutoLogin() {
        return (this.getLastLoginState() == Const.LOGIN_STATE_LOGIN
                && this.getLoginId() != null
                && this.getLoginId().length() > 0
                && this.getLastppssww() != null
                && this.getLastppssww().length() > 0
                /**
                 * 去掉设备判断
                 && (this.getCurUser().getWatchList() != null && this.getCurUser().getWatchList().size() > 0)*/);
    }

    private boolean bindAutoLogin = false;

    //绑定时候已经登录设为true
    public void setBindAutoLogin(boolean state) {
        bindAutoLogin = state;
    }

    public boolean getBindAutoLogin() {
        return bindAutoLogin;
    }

    public ArrayList<ChatMsgEntity> getmSosChatList() {
        return mSosChatList;
    }

    private HashMap<String, Boolean> forceRecordMap = new HashMap<>();

    public boolean getForceRecordState(String eid) {
        if (!forceRecordMap.containsKey(eid))
            return false;
        return forceRecordMap.get(eid);
    }

    public void setForceRecordState(String eid, boolean state) {
        forceRecordMap.put(eid, state);
    }

    private HashMap<String, Boolean> forceTakePhotoMap = new HashMap<>();

    public boolean getForceTakePhotoState(String eid) {
        if (!forceTakePhotoMap.containsKey(eid))
            return false;
        return forceTakePhotoMap.get(eid);
    }

    public void setForceTakePhotoState(String eid, boolean state) {
        forceTakePhotoMap.put(eid, state);
    }

    private HashMap<String, Long> forceTakePhotoEndTimeMap = new HashMap<>();

    public long getForceTakePhotoEndTime(String eid) {
        if (!forceTakePhotoEndTimeMap.containsKey(eid))
            return 0l;
        return forceTakePhotoEndTimeMap.get(eid);
    }

    public void setForceTakePhotoEndTime(String eid, Long time) {
        forceTakePhotoEndTimeMap.put(eid, time);
    }

    /**
     * 云相册是否开启
     *
     * @return 开启，返回true。默认false。
     */
    public boolean getCloudPhotosOnoff(String eid) {
        return getStringValue(eid + Const.SHARE_PREF_FIELD_CLOUD_PHOTOS, "0").equals("1");
    }

    public HashMap<Integer, DialogSet> getSysDialogSets() {
        return sysDialogSets;
    }

    public int getmBackHomeFlag() {
        return mBackHomeFlag;
    }

    public void setmBackHomeFlag(int mBackHomeFlag) {
        this.mBackHomeFlag = mBackHomeFlag;
    }

    public HashMap<String, Integer> getmWatchIsOn() {
        return mWatchIsOn;
    }

    public HashMap<String, Integer> getmWatchOfflineState() {
        return mWatchOfflineStatus;
    }

    public void setmWatchOfflineState(String eid, Integer type) {
        mWatchOfflineStatus.put(eid, type);
    }

    // Const.WATCH_STATE_POWER_ON
    public void setmWatchState(String eid, int type) {
        setValue(Const.SHARE_PREF_FIELD_WATCH_IS_ON + eid, type);
        mWatchIsOn.put(eid, type);
    }

    public void updatemWatchState(String eid) {

        // 收到关机指令后的20秒内再收到手表的消息，不认为手表已经再次开机
        if (timeWatchShutDown != null && timeWatchShutDown.get(eid) != null) {
            long timeShutDown = timeWatchShutDown.get(eid);
            if (System.currentTimeMillis() - timeShutDown <= 1000 * 20) {
                return;
            }
        }
        // 获取不到WatchData实例直接return
        if (getCurUser().queryWatchDataByEid(eid) == null)
            return;

        Long time = gettimeOfRecentBattery().get(eid);
        long locationTime = 0;
        if (getCurUser().queryWatchDataByEid(eid).getCurLocation() != null
                && getCurUser().queryWatchDataByEid(eid).getCurLocation().getTimestamp() != null)
            locationTime = TimeUtil.getMillisByTime(getCurUser().queryWatchDataByEid(eid).getCurLocation().getTimestamp());
        if (time != null && locationTime > time)
            time = locationTime;
        Integer state = getmWatchIsOn().get(eid);
        if (time != null && time != 0) {
            long timediff = System.currentTimeMillis() - time;
            Integer battery = getCurUser().queryWatchDataByEid(eid).getBattery();
            if (timediff >= Const.ONE_DAY_MILLISECOND) {
                setmWatchState(eid, Const.WATCH_STATE_MAYBE_POWER_OFF);
            } else if (battery <= 10 && timediff > Const.MAX_LOW_BATTERY_POWEROFF_TIME
                    && state != null && state == Const.WATCH_STATE_POWER_ON) {
                setmWatchState(eid, Const.WATCH_STATE_POWER_OFF_LOW_POWER);
            }
        }
    }

    public HashMap<String, ArrayList<EFence>> getmWatchEFence() {
        return mWatchEFence;
    }

    //alex charge status
    public void setmChargeState(String eid, int type) {
        mChargeState.put(eid, type);
    }

    public HashMap<String, Integer> getmChargeState() {
        return mChargeState;
    }

    public void updatemWatchChargeState(String eid) {

        Integer chargeS = getmChargeState().get(eid);
        if (chargeS == null)
            setmChargeState(eid, Const.WATCH_CHARGE_IS_OFF);
        Integer state = getmWatchIsOn().get(eid);
        if (state == null || state == Const.WATCH_STATE_POWER_OFF || state == Const.WATCH_STATE_FLIGHT
                || state == Const.WATCH_STATE_MAYBE_POWER_OFF) {
            setmChargeState(eid, Const.WATCH_CHARGE_IS_OFF);
            return;
        }
        if (state == Const.WATCH_CHARGE_IS_ON && timeOfChargeRecentBattery != null && timeOfChargeRecentBattery.get(eid) != null
                && (System.currentTimeMillis() - timeOfChargeRecentBattery.get(eid)) > Const.HALF_HOUR_MILLISECOND) {
            setmChargeState(eid, Const.WATCH_CHARGE_IS_OFF);
            return;
        } else if (state == Const.WATCH_CHARGE_IS_ON && timeOfChargeRecentBattery != null && timeOfChargeRecentBattery.get(eid) == null) {
            timeOfChargeRecentBattery.put(eid, System.currentTimeMillis());
        }
        if (getCurUser().queryWatchDataByEid(eid) != null && getCurUser().queryWatchDataByEid(eid).getBattery() == 100) {
            setmChargeState(eid, Const.WATCH_CHARGE_IS_OFF);
        }
    }

    //最近一次电量更新
    public void settimeOfRecentBattery(String eid, long time) {
        if (timeOfRecentBattery.get(eid) == null || time > timeOfRecentBattery.get(eid))
            timeOfRecentBattery.put(eid, time);
    }

    public HashMap<String, Long> gettimeOfRecentBattery() {
        return timeOfRecentBattery;
    }

    //最新一次充电状态更新时间
    public void settimeOfChargeRecentBattery(String eid, Long time) {
        timeOfChargeRecentBattery.put(eid, time);
    }

    public Boolean getmSosStartFlag() {
        return mSosStartFlag;
    }

    public void setmSosStartFlag(Boolean mSosStartFlag) {
        this.mSosStartFlag = mSosStartFlag;
    }

    public String getmSosFamily() {
        return mSosFamily;
    }

    public void setmSosFamily(String mSosFamily) {
        this.mSosFamily = mSosFamily;
    }

    public Boolean getmUseCall() {
        return getBoolValue(Const.SHARE_PREE_FIELD_USE_CALL_MODE, true);
    }

    public void setmUseCall(Boolean mUseCall) {
        setValue(Const.SHARE_PREE_FIELD_USE_CALL_MODE, mUseCall);
    }

    public boolean isDeviceFlightModeTime(String eid) {

        String sleepTimeDataStr = getStringValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
        float begintime = 0;
        float endtime = 7;
        if (!sleepTimeDataStr.equals("") && !sleepTimeDataStr.equals("[]")) {
            try {
                net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) JSONValue.parse(sleepTimeDataStr);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObj = (JSONObject) array.get(i);
                    SleepTime sleepTime = new SleepTime();
                    sleepTime = SleepTime.toBeSleepTimeBean(sleepTime, jsonObj);
                    if (!sleepTime.onoff.equals("0")) {
                        begintime = Float.valueOf(sleepTime.starthour) + Float.valueOf(sleepTime.startmin) / 60;
                        endtime = Float.valueOf(sleepTime.endhour) + Float.valueOf(sleepTime.endmin) / 60;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                // hyy：修改了本地存储格式，取旧数据会出现异常，这里进行捕获，并将本地数据置空
                setValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
            }
        }
        float timeNow;
        String time = TimeUtil.getTimeStampLocal();//"yyyyMMddHHmmssSSS"
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        timeNow = Float.valueOf(hour) + Float.valueOf(minute) / 60;
        if (begintime < endtime) {
            return timeNow >= begintime && timeNow < endtime;
        } else if (begintime > endtime) {
            return timeNow >= begintime || timeNow < endtime;
        } else {
            return false;
        }
    }

    public String getAES_KEY() {
        if (mNetService != null) {
            return mNetService.getAESKEY();
        } else {
            return initAES_KEY();
        }
    }

    public String initAES_KEY() {
        byte[] random_key = new byte[16];
        Random r = new Random();
        r.nextBytes(random_key);
        String key = Base64.encodeToString(random_key, Base64.NO_WRAP).substring(0, 16);
        setValue(Const.SHARE_PREF_AES_KEY, key);
        return key;
    }

    public boolean isFlightModeTime() {
        if (getCurUser() == null || getCurUser().getFocusWatch() == null || getCurUser().getFocusWatch().getEid() == null) {
            return false;
        }
        String sleepTimeDataStr = getStringValue(getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
        float begintime = 0;
        float endtime = 7;
        if (!sleepTimeDataStr.equals("") && !sleepTimeDataStr.equals("[]")) {
            try {
                net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) JSONValue.parse(sleepTimeDataStr);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObj = (JSONObject) array.get(i);
                    SleepTime sleepTime = new SleepTime();
                    sleepTime = SleepTime.toBeSleepTimeBean(sleepTime, jsonObj);
                    if (!sleepTime.onoff.equals("0")) {
                        begintime = Float.valueOf(sleepTime.starthour) + Float.valueOf(sleepTime.startmin) / 60;
                        endtime = Float.valueOf(sleepTime.endhour) + Float.valueOf(sleepTime.endmin) / 60;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                // hyy：修改了本地存储格式，取旧数据会出现异常，这里进行捕获，并将本地数据置空
                setValue(getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
            }
        }

        float timeNow;
        try {
            String time = TimeUtil.getTimeStampLocal();//"yyyyMMddHHmmssSSS"
            String hour = time.substring(8, 10);
            String minute = time.substring(10, 12);
            timeNow = Float.valueOf(hour) + Float.valueOf(minute) / 60;
        } catch (Exception e) {
            return false;
        }
        if (begintime < endtime) {
            return timeNow >= begintime && timeNow < endtime;
        } else if (begintime > endtime) {
            return timeNow >= begintime || timeNow < endtime;
        } else {
            return false;
        }
    }

    //高效的文件拷贝方式
    public void fileChannelCopy(File s, File t) {

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        File target = new File(t, s.getName());
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(target);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public HashMap<String, Boolean> getmWatchBackhomeLocationFlag() {
        return mWatchBackhomeLocationFlag;
    }

    private HashMap<String, Boolean> mWatchBackhomeLocationFlag = new HashMap<String, Boolean>();

    public void setHasNewGroupMsg(String gid, boolean state) {
        if (state) {
            int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE + gid + getCurUser().getEid(), 0);
            count++;
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE + gid + getCurUser().getEid(), count);
        } else {
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE + gid + getCurUser().getEid(), 0);
        }
    }

    public boolean getHasNewGroupMsg(String gid) {
        int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE + gid + getCurUser().getEid(), 0);
        return count != 0;
    }

    public void setHasNewPrivateMsg(String gid, boolean state) {
        if (state) {
            int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE + gid + getCurUser().getEid(), 0);
            count++;
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE + gid + getCurUser().getEid(), count);
        } else {
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE + gid + getCurUser().getEid(), 0);
        }
    }

    public boolean getHasNewPrivateMsg(String gid) {
        int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE + gid + getCurUser().getEid(), 0);
        return count != 0;
    }

    public boolean getHasNewNoticeMsg(String gid) {
        int count = getNewNoticeMsgCount(gid);
        return count != 0;
    }

    public void clearHasNewNoticeMsg(String gid) {
        for (int i = AllMessageAdapter.MESSAGE_TYPE_LOCATION; i <= AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD; i++) {
            setHasNewNoticeMsg(gid, i, false);
        }
    }

    public void setHasNewNoticeMsg(String gid, int type, boolean state) {
        if (type == AllMessageAdapter.MESSAGE_TYPE_ALL) {
            clearHasNewNoticeMsg(gid);
            return;
        }
        if (state) {
            int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE + type + gid + getCurUser().getEid(), 0);
            count++;
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE + type + gid + getCurUser().getEid(), count);
        } else {
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE + type + gid + getCurUser().getEid(), 0);
        }
    }

    public boolean getHasNewNoticeMsg(String gid, int type) {
        int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE + type + gid + getCurUser().getEid(), 0);
        return count != 0;
    }

    public void setHasNewSpamSms(String gid, boolean state) {
        if (state) {
            int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_SPAM_SMS + gid + getCurUser().getEid(), 0);
            count++;
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_SPAM_SMS + gid + getCurUser().getEid(), count);
        } else {
            setValue(Const.SHARE_PREF_FIELD_HAS_NEW_SPAM_SMS + gid + getCurUser().getEid(), 0);
        }
    }

    public boolean getHasNewSpamSms(String gid) {
        int count = getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_SPAM_SMS + gid + getCurUser().getEid(), 0);
        return count != 0;
    }

    public int getNewGroupMsgCount(String gid) {
        return getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE + gid + getCurUser().getEid(), 0);
    }

    public int getNewPrivateMsgCount(String gid) {
        return getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE + gid + getCurUser().getEid(), 0);
    }

    public int getNewNoticeMsgCount(String gid) {
        int result = 0;
        for (int i = AllMessageAdapter.MESSAGE_TYPE_LOCATION; i <= AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD; i++) {
            result += getNewNoticeMsgCount(gid, i);
        }
        return result;
    }

    public int getNewNoticeMsgCount(String gid, int type) {
        return getIntValue(Const.SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE + type + gid + getCurUser().getEid(), 0);
    }

    public HashMap<String, Long> getmAppToWatchLocationTime() {
        return mAppToWatchLocationTime;
    }

    private HashMap<String, Long> mAppToWatchLocationTime = new HashMap<String, Long>();


    private static MiPushHandler mMiPushHandler = null;

    public static MiPushHandler getMiPushHandler() {
        return mMiPushHandler;
    }

    public boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (processInfos == null) {
            return false;
        }
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void initMiPushLogger() {
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                LogUtil.d(TAG + "  " + content, t);
            }

            @Override
            public void log(String content) {
                LogUtil.d(TAG + "  " + content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean isMiPushRegister = false;
    private boolean isMiPushSetAlias = false;

    public void setMiPushRegister(boolean register) {
        isMiPushRegister = register;
    }

    public boolean getMiPushRegister() {
        return isMiPushRegister;
    }

    public void setMiPushSetAlias(boolean alias) {
        isMiPushSetAlias = alias;
    }

    public void setMiPushAlias() {
        List<String> tempAlias = MiPushClient.getAllAlias(getApplicationContext());
        for (String alias : tempAlias) {
            if (!alias.equals(getCurUser().getEid())) {
                MiPushClient.unsetAlias(getApplicationContext(), alias, null);
            }
        }
        LogUtil.e("setMiPushAlias  getCurUser().getEid() = " + getCurUser().getEid());
        MiPushClient.setAlias(getApplicationContext(), getCurUser().getEid(), null);
    }

    private boolean adminBindFlag = false;

    public void setAdminBindFlag(boolean flag) {
        adminBindFlag = flag;
    }

    public boolean getAdminBindFlag() {
        return adminBindFlag;
    }

    public int lastWebsocketPort = 0;

    public void sendReActiveSMS(String tellphonenum, String iccid) {
        if (tellphonenum != null && tellphonenum.length() > 0 && iccid != null) {
            String message = MD5.md5_string("PS_ACTIVE:" + iccid);
            SmsUtil.sendMsgToWatch(this, tellphonenum, message);
        }
    }

    private SilenceTime effectingAdvanceSilenceTime = new SilenceTime();

    public SilenceTime getEffectingAdvanceSilenceTime() {
        return effectingAdvanceSilenceTime;
    }

    /**
     * @return 0, 非防打扰；1，防打扰；2，深度防打扰（2G系列）；3，新深度防打扰（4G系列）
     */
    public int isInSilenceTime(String eid) {
        int ret = 0;
        int retTemp = 0;
        String silenceDataStr = getStringValue(eid + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, "");
        if (!silenceDataStr.equals("")) {
            net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) JSONValue.parse(silenceDataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                SilenceTime tmpSilenceTime = new SilenceTime();
                tmpSilenceTime.starthour = (String) jsonObj.get(CloudBridgeUtil.STARTHOUR);
                if (tmpSilenceTime.starthour.length() == 1)
                    tmpSilenceTime.starthour = "0" + tmpSilenceTime.starthour;
                tmpSilenceTime.startmin = (String) jsonObj.get(CloudBridgeUtil.STARTMIN);
                if (tmpSilenceTime.startmin.length() == 1)
                    tmpSilenceTime.startmin = "0" + tmpSilenceTime.startmin;
                tmpSilenceTime.endhour = (String) jsonObj.get(CloudBridgeUtil.ENDHOUR);
                if (tmpSilenceTime.endhour.length() == 1)
                    tmpSilenceTime.endhour = "0" + tmpSilenceTime.endhour;
                tmpSilenceTime.endmin = (String) jsonObj.get(CloudBridgeUtil.ENDMIN);
                if (tmpSilenceTime.endmin.length() == 1)
                    tmpSilenceTime.endmin = "0" + tmpSilenceTime.endmin;
                tmpSilenceTime.days = (String) jsonObj.get(CloudBridgeUtil.DAYS);
                tmpSilenceTime.onoff = (String) jsonObj.get(CloudBridgeUtil.ONOFF);
                tmpSilenceTime.timeStampId = (String) jsonObj.get(CloudBridgeUtil.TIMESTAMPID);
                tmpSilenceTime.advanceopt = (Integer) jsonObj.get(CloudBridgeUtil.SILENCETIME_ADVANCEOPT);

                Calendar cal = Calendar.getInstance();
                int week = cal.get(Calendar.DAY_OF_WEEK);
                int silencemilstart = Integer.valueOf(tmpSilenceTime.starthour) * 60 + Integer.valueOf(tmpSilenceTime.startmin);
                int silencemilend = Integer.valueOf(tmpSilenceTime.endhour) * 60 + Integer.valueOf(tmpSilenceTime.endmin);
                int nowmil = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
                switch (week) {
                    case Calendar.MONDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 0, 1, 6, 7);
                        break;
                    case Calendar.TUESDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 1, 2, 0, 1);
                        break;
                    case Calendar.WEDNESDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 2, 3, 1, 2);
                        break;
                    case Calendar.THURSDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 3, 4, 2, 2);
                        break;
                    case Calendar.FRIDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 4, 5, 3, 4);
                        break;
                    case Calendar.SATURDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 5, 6, 4, 5);
                        break;
                    case Calendar.SUNDAY:
                        retTemp = getSilenceStateByDay(tmpSilenceTime, silencemilstart, silencemilend, nowmil, 6, 7, 5, 6);
                        break;
                }
                if (ret <= retTemp)
                    ret = retTemp;
                if (ret == 2 || ret == 1) {
                    effectingAdvanceSilenceTime.advanceopt = tmpSilenceTime.advanceopt;
                    effectingAdvanceSilenceTime.endmin = tmpSilenceTime.endmin;
                    effectingAdvanceSilenceTime.endhour = tmpSilenceTime.endhour;
                    effectingAdvanceSilenceTime.startmin = tmpSilenceTime.startmin;
                    effectingAdvanceSilenceTime.starthour = tmpSilenceTime.starthour;
                    effectingAdvanceSilenceTime.days = tmpSilenceTime.days;
                    effectingAdvanceSilenceTime.onoff = tmpSilenceTime.onoff;
                    effectingAdvanceSilenceTime.timeStampId = tmpSilenceTime.timeStampId;
                    break;
                }
            }
        } else {
            ret = 0;
        }
        return ret;
    }

    /**
     * 处于防打扰的几种情况：
     * 1.silencemilstart<=silencemilend  当天区间[silencemilstart,silencemilend]
     * 2.silencemilstart>silencemilend  当天区间[silencemilstart,24] + 第二天区间[0,silencemilend]
     * <p>
     * 判断此次时刻是否处于防打扰：（为简化代码判断，这样分类）
     * 1.当天防打扰开启  分为两种情况  防打扰区间
     * 2.silencemilstart>silencemilend  "前"一天防打扰开启  防打扰区间
     */
    private int getSilenceStateByDay(SilenceTime silenceTime, int silencemilstart,
                                     int silencemilend, int nowmil, int startDay, int endDay, int startDayOther, int endDayOther) {
        int ret = 0;
        if (silenceTime.days.substring(startDay, endDay).equals("1") && silenceTime.onoff.equals("1")) {
            if (silencemilstart <= silencemilend) {
                if (nowmil >= silencemilstart && nowmil <= silencemilend) {
                    ret = 1;
                    if (silenceTime.advanceopt == 1) {
                        ret = 2;
                    } else if (silenceTime.advanceopt == 2) {
                        ret = 3;
                    }
                }
            } else {
                if (nowmil >= silencemilstart) {
                    ret = 1;
                    if (silenceTime.advanceopt == 1) {
                        ret = 2;
                    } else if (silenceTime.advanceopt == 2) {
                        ret = 3;
                    }
                }
            }
        }

        if (silencemilstart > silencemilend) {
            if (silenceTime.days.substring(startDayOther, endDayOther).equals("1") && silenceTime.onoff.equals("1")) {
                if (nowmil < silencemilend) {
                    ret = 1;
                    if (silenceTime.advanceopt == 1) {
                        ret = 2;
                    } else if (silenceTime.advanceopt == 2) {
                        ret = 3;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 是否开启防打扰  默认关闭
     *
     * @param eid watch eid
     */
    public boolean isOpenSilenceTime(String eid) {
        String silenceDataStr = getStringValue(eid + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, "");
        if (!silenceDataStr.equals("") && !silenceDataStr.equals("[]")) {
            net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) JSONValue.parse(silenceDataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                String onoff = (String) jsonObj.get(CloudBridgeUtil.ONOFF);
                if ("1".equals(onoff)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否开启休眠  默认开启
     *
     * @param eid watch eid
     */
    public boolean isOpenSleepTime(String eid) {
        String sleepTimeDataStr = getStringValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
        if (!sleepTimeDataStr.equals("") && !sleepTimeDataStr.equals("[]")) {
            try {
                net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) JSONValue.parse(sleepTimeDataStr);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObj = (JSONObject) array.get(i);
                    String onoff = (String) jsonObj.get(CloudBridgeUtil.ONOFF);
                    if ("0".equals(onoff)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                // hyy：修改了本地存储格式，取旧数据会出现异常，这里进行捕获，并将本地数据置空
                setValue(eid + Const.SHARE_PREF_SLEEP_MODE_KEY, "");
            }
        }
        return true;
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            Log.e("xxxx", "isFastClick");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void sdcardException(final String tag, final Exception e) {
        if (!BuildConfig.IS_PRINT_LOG)
            return;
        Date nowtime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final String currTime = dateFormat.format(nowtime);

        try {
            new PrintWriter(new BufferedWriter(new FileWriter(
                    getCurLogFile().getAbsoluteFile(), true)), true).println(new Object() {
                public String toString() {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter);
                    writer.print(currTime + " " + tag + " ");
                    e.printStackTrace(writer);
                    StringBuffer buffer = stringWriter.getBuffer();
                    return buffer.toString();
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 根据版本控制，显示View
     *
     * @param watch          WatchData实例
     * @param needDebugJudge 是否需要debug判断，如果true，则版本判断只对release版本有效；如果false，对release、debug版本都有效
     * @param controlVer     版本控制的版本号 格式以T开头
     * @return if effective,return true
     */
    public boolean isControledByVersion(WatchData watch, boolean needDebugJudge, String controlVer) {
        //LGA0018.2.0.1.3T25G1011_M023
        String verCur = watch.getVerCur();
        if (verCur == null)
            return false;
        int indexDot = verCur.lastIndexOf(".");
        int indexT = -1;
        if (indexDot != -1)
            indexT = verCur.substring(indexDot).indexOf("T");
        String ver;
        if (needDebugJudge && BuildConfig.ISDEBUG) {
            ver = "T99";
        } else if (indexDot == -1 || indexT == -1 || verCur.length() < indexDot + indexT + 3) {
            ver = "T00";    //如果版本信息为错误格式，以T00处理
        } else {
            ver = verCur.substring(indexDot).substring(indexT, indexT + 3);
        }
        return ver.compareTo(controlVer) >= 0;
    }

    /**
     * 判断最大版本，是否低于支持该功能的版本
     *
     * @param watch          WatchData实例，主要用于获取
     * @param supportVersion 支持该功能的版本
     * @return if lowerSupportVersion, return true. default true
     */
    public boolean isLowerSupportVersion(WatchData watch, String supportVersion) {
        String maxVer = watchUpdateMaxVersion(watch);
        if (maxVer == null)
            return true;
        int indexDot = maxVer.lastIndexOf(".");
        int indexT = -1;
        if (indexDot != -1)
            indexT = maxVer.substring(indexDot).indexOf("T");
        String ver;
        if (indexDot == -1 || indexT == -1 || maxVer.length() < indexDot + indexT + 3) {
            ver = "T00";    //如果版本信息为错误格式，以T00处理
        } else {
            ver = maxVer.substring(indexDot).substring(indexT, indexT + 3);
        }
        return ver.compareTo(supportVersion) < 0;
    }

    public String getPhoneNumberFamilyNickname(PhoneNumber phoneNumber) {
        String nickname = null;
        if (phoneNumber.attri == 0) {
            nickname = getText(R.string.relation_0).toString();
        } else if (phoneNumber.attri == 1) {
            nickname = getText(R.string.relation_1).toString();
        } else if (phoneNumber.attri == 2) {
            nickname = getText(R.string.relation_2).toString();
        } else if (phoneNumber.attri == 3) {
            nickname = getText(R.string.relation_3).toString();
        } else if (phoneNumber.attri == 4) {
            nickname = getText(R.string.relation_4).toString();
        } else if (phoneNumber.attri == 5) {
            nickname = getText(R.string.relation_5).toString();
        } else if (phoneNumber.attri == 6) {
            nickname = getText(R.string.relation_6).toString();
        } else if (phoneNumber.attri == 7) {
            nickname = getText(R.string.relation_7).toString();
        } else if (phoneNumber.attri == 8) {
            nickname = getText(R.string.relation_8).toString();
        } else if (phoneNumber.attri == 9) {
            nickname = getText(R.string.relation_9).toString();
        } else if (phoneNumber.attri == 10) {
            nickname = getText(R.string.relation_10).toString();
        } else if (phoneNumber.attri == 11) {
            nickname = getText(R.string.relation_11).toString();
        } else if (phoneNumber.attri == 12) {
            nickname = getText(R.string.relation_12).toString();
        } else if (phoneNumber.attri == 13) {
            nickname = getText(R.string.relation_13).toString();
        } else if (phoneNumber.attri == 14) {
            nickname = getText(R.string.relation_14).toString();
        } else if (phoneNumber.attri == 15) {
            nickname = getText(R.string.relation_15).toString();
        } else if (phoneNumber.attri == 16) {
            nickname = getText(R.string.relation_16).toString();
        } else if (phoneNumber.attri == 17) {
            nickname = getText(R.string.relation_17).toString();
        } else if (phoneNumber.attri == 18) {
            nickname = getText(R.string.relation_18).toString();
        } else if (phoneNumber.attri == 19) {
            nickname = getText(R.string.relation_19).toString();
        } else if (phoneNumber.attri == 20) {
            nickname = getText(R.string.relation_20).toString();
        } else if (phoneNumber.attri == 21) {
            nickname = getText(R.string.relation_21).toString();
        } else if (phoneNumber.attri == 22) {
            nickname = getText(R.string.relation_22).toString();
        } else if (phoneNumber.attri == 23) {
            nickname = getText(R.string.relation_23).toString();
        } else {
            if (phoneNumber.nickname != null) {
                nickname = phoneNumber.nickname;
            }
        }
        return nickname;
    }

    ///判断是不是管理员
    public boolean isMeAdmin(WatchData watchData) {
        String myEid = getCurUser().getEid();
        String adminEid = "";
        for (FamilyData cur : getCurUser().getFamilyList()) {
            if (cur.getFamilyId().equals(watchData.getFamilyId())) {
                adminEid = cur.getAdminEId();
                break;
            }
        }
        boolean isMeAdmin = false;
        if (myEid.equals(adminEid)) {
            isMeAdmin = true;
        }
        return isMeAdmin;
    }

    public String getAdminPhonenumber(String eid) {

        String adminEid = "";
        for (FamilyData cur : getCurUser().getFamilyList()) {
            if (cur.getFamilyId().equals(getCurUser().getFocusWatch().getFamilyId())) {
                adminEid = cur.getAdminEId();
                break;
            }
        }

        String adminNumber = "";
        String jsonStr = getStringValue(eid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
        int size = mBindWhiteList.size();
        for (int i = 0; i < size; i++) {
            PhoneNumber phoneNumberS = mBindWhiteList.get(i);
            if (adminEid.equals(phoneNumberS.userEid)) {
                adminNumber = phoneNumberS.number;
                break;
            }
        }

        return adminNumber;
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public PhoneNumber getPhoneWhiteDataByNumber(String number, ArrayList<PhoneNumber> phoneWhiteList) {
        for (PhoneNumber data : phoneWhiteList) {
            if ((!TextUtils.isEmpty(data.number) && data.number.equals(number)) ||
                    (!TextUtils.isEmpty(data.subNumber) && data.subNumber.equals(number))) {
                return data;
            }
        }

        if (!TextUtils.isEmpty(number)) {
            if (number.length() >= 11) {
                for (PhoneNumber data : phoneWhiteList) {
                    if ((!TextUtils.isEmpty(data.number) && data.number.endsWith(number.substring(number.length() - 11))) ||
                            (!TextUtils.isEmpty(data.subNumber) && data.subNumber.endsWith(number.substring(number.length() - 11)))) {
                        return data;
                    }
                }
            } else if (number.length() >= 7) {
                for (PhoneNumber data : phoneWhiteList) {
                    if ((!TextUtils.isEmpty(data.number) && data.number.endsWith(number.substring(number.length() - 7))) ||
                            (!TextUtils.isEmpty(data.subNumber) && data.subNumber.endsWith(number.substring(number.length() - 7)))) {
                        return data;
                    }
                }
            }
        }
        return null;
    }

    public PhoneNumber getPhoneWhiteDataByUserEid(String userEid, ArrayList<PhoneNumber> phoneWhiteList) {
        PhoneNumber phoneNumber = null;
        for (PhoneNumber data : phoneWhiteList) {
            if (!TextUtils.isEmpty(data.userEid) && data.userEid.equals(userEid)) {
                phoneNumber = data;
                break;
            }
        }
        return phoneNumber;
    }

    public PhoneNumber getPhoneWhiteDataByNumber(String watchEid, String number) {
        PhoneNumber phoneNumber = null;
        String jsonStr = getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        ArrayList<PhoneNumber> phoneWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
        phoneNumber = getPhoneWhiteDataByNumber(number, phoneWhiteList);
        return phoneNumber;
    }

    public PhoneNumber getPhoneWhiteDataByUserEid(String watchEid, String userEid) {
        PhoneNumber phoneNumber = null;
        String jsonStr = getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        ArrayList<PhoneNumber> phoneWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
        phoneNumber = getPhoneWhiteDataByUserEid(userEid, phoneWhiteList);
        return phoneNumber;
    }

    public PhoneNumber getPhoneNumberByEid(String userEid, String watchEid) {
        String jsonStr = getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        ArrayList<PhoneNumber> phoneWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
        for (PhoneNumber phoneNumber : phoneWhiteList) {
            if (phoneNumber.userEid != null && phoneNumber.userEid.length() > 0 && phoneNumber.userEid.equals(userEid)) {
                return phoneNumber;
            }
        }
        return null;
    }

    public String getRelation(PhoneNumber phoneNumber) {
        if (phoneNumber.attri == 0) {
            return getText(R.string.relation_0).toString();
        } else if (phoneNumber.attri == 1) {
            return getText(R.string.relation_1).toString();
        } else if (phoneNumber.attri == 2) {
            return getText(R.string.relation_2).toString();
        } else if (phoneNumber.attri == 3) {
            return getText(R.string.relation_3).toString();
        } else if (phoneNumber.attri == 4) {
            return getText(R.string.relation_4).toString();
        } else if (phoneNumber.attri == 5) {
            return getText(R.string.relation_5).toString();
        } else if (phoneNumber.attri == 6) {
            return getText(R.string.relation_6).toString();
        } else if (phoneNumber.attri == 7) {
            return getText(R.string.relation_7).toString();
        } else if (phoneNumber.attri == 8) {
            return getText(R.string.relation_8).toString();
        } else if (phoneNumber.attri == 9) {
            return getText(R.string.relation_9).toString();
        } else if (phoneNumber.attri == 10) {
            return getText(R.string.relation_10).toString();
        } else if (phoneNumber.attri == 11) {
            return getText(R.string.relation_11).toString();
        } else if (phoneNumber.attri == 12) {
            return getText(R.string.relation_12).toString();
        } else if (phoneNumber.attri == 13) {
            return getText(R.string.relation_13).toString();
        } else if (phoneNumber.attri == 14) {
            return getText(R.string.relation_14).toString();
        } else if (phoneNumber.attri == 15) {
            return getText(R.string.relation_15).toString();
        } else if (phoneNumber.attri == 16) {
            return getText(R.string.relation_16).toString();
        } else if (phoneNumber.attri == 17) {
            return getText(R.string.relation_17).toString();
        } else if (phoneNumber.attri == 18) {
            return getText(R.string.relation_18).toString();
        } else if (phoneNumber.attri == 19) {
            return getText(R.string.relation_19).toString();
        } else if (phoneNumber.attri == 20) {
            return getText(R.string.relation_20).toString();
        } else if (phoneNumber.attri == 21) {
            return getText(R.string.relation_21).toString();
        } else if (phoneNumber.attri == 22) {
            return getText(R.string.relation_22).toString();
        } else if (phoneNumber.attri == 23) {
            return getText(R.string.relation_23).toString();
        } else {
            if (phoneNumber.nickname != null) {
                return phoneNumber.nickname;
            } else {
                return getText(R.string.device_lesson_custom).toString();
            }
        }
    }

    public String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager.getRunningTasks(1) != null && !activityManager.getRunningTasks(1).isEmpty()) {
            String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            return runningActivity;
        }else {
            return "";
        }
    }

    private String mBindRequest = null;

    public String getBindRequest() {
        return mBindRequest;
    }

    public void setBindRequest(String request) {
        mBindRequest = request;
    }

    private String mFriendRequest = null;

    public String getFriendRequest() {
        return mFriendRequest;
    }

    public void setFriendRequest(String request) {
        mFriendRequest = request;
    }

    private HashMap<String, String> mBindRequsetSN = new HashMap<String, String>();

    public HashMap<String, String> getBindRequsetSN() {
        return mBindRequsetSN;
    }

    public void downloadNoticeVideo(final String eid, final String key, final OnImageDownload listener) {
        final JSONObject obj = new JSONObject();
        obj.put("prefix", key);
        obj.put("sid", getToken());
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(obj.toString(), mNetService.getAESKEY(), mNetService.getAESKEY()));
                    String result = PostJsonWithURLConnection(encryptData + getToken(), XunKidsDomain.getInstance(getApplicationContext()).getXunKidsFilesDomain(OVERSEAURL.FDSFILE_FILE_LIST_URL), false, getAssets().open("dxclient_t.bks"));
                    if (result != null && result.length() > 0) {
                        String out = new String(AESUtil.decryptAESCBC(org.java_websocket.util.Base64.decode(result), mNetService.getAESKEY(), mNetService.getAESKEY()));
                        JSONObject json = (JSONObject) JSONValue.parse(out);
                        net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) json.get("files");
                        Object code = json.get("code");
                        if ((code != null && ((Integer) code) != 0) || array == null) {
                            return null;
                        }
                        String url = null;
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject file = (JSONObject) array.get(i);
                            String tempKey = (String) file.get("key");
                            if (tempKey.equals(key)) {
                                url = (String) file.get("url");
                                break;
                            }
                        }
                        if (url != null && url.length() > 0) {
                            HttpURLConnection conn = null;
                            try {
                                URL target = new URL(url);
                                conn = (HttpURLConnection) target.openConnection();
                                conn.setConnectTimeout(10 * 1000);
                                conn.setReadTimeout(30 * 1000);
                                conn.setDoInput(true);

                                if (conn.getResponseCode() == 200) {
                                    InputStream is = conn.getInputStream();
                                    File fp = new File(getChatCacheDir().getPath(), key.substring(key.lastIndexOf("/")));
                                    OutputStream fout = new FileOutputStream(fp);
                                    int len = 0;
                                    byte[] buf = new byte[1024];
                                    while ((len = is.read(buf)) != -1) {
                                        fout.write(buf, 0, len);
                                    }
                                    is.close();
                                    fout.close();
                                    AESUtil.decryptFile(fp.getAbsolutePath(), eid.substring(0, 16), eid.substring(0, 16));
                                    return fp.getAbsolutePath();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (listener != null) {
                    if (s != null && s.length() > 0) {
                        listener.onSuccess(s);
                    } else {
                        listener.onFail();
                    }
                }
            }
        }.execute();
    }

    public LocalBroadcastManager getLocalBroadcastManager() {
        if (mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        }
        return mLocalBroadcastManager;
    }

    public void updateDownFileSize(String type, long size) {
        String oldSize = getStringValue(type, "");
        JSONObject sizeJo = new JSONObject();
        if (!TextUtils.isEmpty(oldSize)) {
            sizeJo = (JSONObject) JSONValue.parse(oldSize);
        }
        if (sizeJo != null) {
            sizeJo.put(TimeUtil.getTimestampCHN(), size / 1024 + "kb");
            setValue(type, sizeJo.toJSONString());
        }
    }

    private HashMap<String, JSONObject> mNoticeSettingMap = new HashMap<String, JSONObject>();

    public JSONObject initNoticeSetting(String eid) {
        JSONObject json = new JSONObject();

        json.put(CloudBridgeUtil.KEY_NAME_EID, eid);

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_EFENCE, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SOS, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_NAVIGATION, "1");

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_BATTERY, "1");

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPS, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STEPSRANKS, "1");

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_GROUPBODY, "1");

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SMS, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SIMCHANGE, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_FLOWMETER, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_OTA_UPGRADE_EX, "1");

        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_SYSTEM, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_DOWNLOAD, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_STORY, "1");
        json.put(CloudBridgeUtil.OFFLINE_MSG_TYPE_CLOUD_SPACE, "1");
        return json;
    }

    public JSONObject getNoticeSetting(String watchEid) {
        JSONObject json = mNoticeSettingMap.get(watchEid);
        if (json == null || TextUtils.isEmpty(json.toString())) {
            json = initNoticeSetting(watchEid);
        }
        return json;
    }

    public void setNoticeSetting(String watchEid, JSONObject noticeSetting) {
        mNoticeSettingMap.put(watchEid, noticeSetting);
    }

    public void setIsLoginToStore(String isLoginToStore) {
        this.isLoginToStore = isLoginToStore;
    }

    public HashMap<String, Long> clickDelayedTime = new HashMap<>();

    public void putDelayedTime(String key) {
        clickDelayedTime.put(key, System.currentTimeMillis() + 30 * 1000);
    }

    public boolean isInDelayedTime(String key) {
        if (clickDelayedTime.containsKey(key)) {
            Long delayTime = clickDelayedTime.get(key);
            Long nowTime = System.currentTimeMillis();
            return delayTime > nowTime;
        }
        return false;
    }

    public String getWatchEid(ChatMsgEntity chat) {
        String watchEid = null;
        if (getCurUser().getIsWatchByEid(chat.getmSrcId())) {
            watchEid = chat.getmSrcId();
        } else {
            if (getCurUser().queryFamilyByGid(chat.getmFamilyId()) != null) {
                watchEid = getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0).getEid();
            }
        }
        return watchEid;
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAutoLogin() {
        return (this.getLastLoginState() == Const.LOGIN_STATE_LOGIN
                && this.getLoginId() != null
                && this.getLoginId().length() > 0
                && this.getLastppssww() != null
                && this.getLastppssww().length() > 0
                /**
                 * 去掉设备判断
                 && (this.getCurUser().getWatchList() != null && this.getCurUser().getWatchList().size() > 0)*/);
    }

    public boolean isSupportGoogleService = false;

    private void getSupportGoogleService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            isSupportGoogleService = false;
        } else {
            isSupportGoogleService = true;
        }
    }

    public File getAgorasdkLogFile() {
        File file = null;
        File baseDir;
        File dir;
        baseDir = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        dir = new File(baseDir, Const.LOG_CACHE_DIR);
        file = new File(dir, "agorasdk.log");
        return file;
    }

    public File getVideoRecordFile() {
        File file = null;
        File baseDir;
        File dir;
        baseDir = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        dir = new File(baseDir, Const.LOG_CACHE_DIR);
        file = new File(dir, TimeUtil.getTimeStampLocal() + ".mp4");
        return file;
    }

    public boolean isSimCertiStatusEnable(int state) {
        boolean result = true;
        if (state == -1 || state == 1) {
            result = false;
        }
        return result;
    }


}
