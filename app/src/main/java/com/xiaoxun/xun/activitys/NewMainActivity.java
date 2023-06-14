package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.HorizontalListViewAdapter;
import com.xiaoxun.xun.adapter.SimpleFragmentPageAdapter;
import com.xiaoxun.xun.beans.ADShowData;
import com.xiaoxun.xun.beans.EFence;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.fragment.MeFragment;
import com.xiaoxun.xun.fragment.NewSettingFragment;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.AndroidUtil;
import com.xiaoxun.xun.utils.AppStoreUtils;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.NotificationUtils;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.RomUtils;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.StepsUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.views.AllMessageFragment;
import com.xiaoxun.xun.views.GoogleMapFragment;
import com.xiaoxun.xun.views.HorizontalListView;
import com.xiaoxun.xun.views.NoScrollView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.json.JSONException;

import com.xiaoxun.xun.bgstart.api.PermissionLisenter;
import com.xiaoxun.xun.bgstart.impl.BgStart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.xiaoxun.xun.utils.PermissionUtils.REQUEST_VODE_ALERT_WINDOW;

public class NewMainActivity extends BaseAppCompatActivity implements MsgCallback {
    private static final String XUN_WATCH_DOWNLOAD_URL = "http://apps.imiwear.com/";
    private static final String SHARE_PREF_BRAND_TYPE_TIPS = "SHARE_PREF_BRAND_TYPE_TIPS";
    private static final String BRAND_XIAOXUN = "XIAOXUN";
    private static final String BRAND_MITU = "MITU";
    private static final String BRAND_XTR = "XTR";
    private static final int MY_REQUEST_CODE = 0x14;
    private ImibabyApp mApp;
    private NoScrollView mPage;
    private HorizontalListView lv_tab;
    private HorizontalListViewAdapter listadapter;
    private ArrayList<PageData> mPageData = new ArrayList<PageData>();
    private SimpleFragmentPageAdapter mPageAdapter;
    private String mMiPushMessage = null;
    private NetService mNetService = null;
    private MainReceiver mMainReceiver;
    private BroadcastReceiver mBroadcastReceiver;
    private int mMapType = 3; //1表示高德地图，2表示百度地图, 3表示谷歌地图
    public static final int GET_MONITOR_NUMBER_REQUEST = 2;
    public static final int GET_CALLBACK_NUMBER_REQUEST = 1;
    private boolean isFcState = false;
    //    private SystemBarTintManager mTintManager;
    private int selectIndex;
    public static final String isShowBack = "isshowback";//登录页是否显示返回按钮
    public static final String jumpWhere = "jumpWhere";//点击绑定设备到登录成功之后是去man还是bind
    public static final int PERMISSION_RESULT_CALL_PHONE = 2;
    private AppUpdateManager appUpdateManager;
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mApp = (ImibabyApp) getApplication();

//        checkExternalStorgePermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        //设置沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true); //状态栏透明 需要在创建SystemBarTintManager 之前调用。
        }

        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.bg_color_orange));
        if (savedInstanceState != null) {
            mApp.sdcardLog("NewMainActivity is fc!");
            isFcState = true;
            mApp.isCurrentRunningForeground = true;
        }
        String lastTime = mApp.getStringValue(Const.USER_NOT_UPDATETIME, "");

        if (TextUtils.isEmpty(lastTime) || System.currentTimeMillis() - Long.parseLong(lastTime) >= 3 * 24 * 3600 * 1000) { //用户拒绝后三天内不提示

            checkAppUpdateStatus();
        }

        mMiPushMessage = getIntent().getStringExtra(Const.KEY_JSON_MSG);
        mPage = findViewById(R.id.main_page);
        refreshListTab("Normal");//初始化list
        mPageAdapter = new SimpleFragmentPageAdapter(getSupportFragmentManager(), mPageData, mApp);
        mPage.setAdapter(mPageAdapter);
        if (isAutoLoginState() && isHaveWatchList()) {
            selectIndex = 1;
        } else {
            if (getPackageName().equals("com.imibaby.client")) {
                selectIndex = mPageData.size() - 1;
            } else {
                selectIndex = 0;
            }
        }
        mPage.setCurrentItem(selectIndex);
        initView();
        lv_tab = findViewById(R.id.lv_tab);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px
        listadapter = new HorizontalListViewAdapter(this, screenWidth, mPageData);
        lv_tab.setAdapter(listadapter);
        listadapter.setSelectIndex(selectIndex);//初始化 tab首次选中的位置

        lv_tab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectIndex = position;
                mPage.setCurrentItem(selectIndex);
                for (int i = 0; i < mPageData.size(); i++) {
                    if (position == i) {
                        ImageView imageView = adapterView.getChildAt(i).findViewById(R.id.item_main_icon);
                        String title = mPageData.get(i).mTitle;
                        imageView.setBackgroundResource(mPageData.get(i).mSeResourceId);
                    } else {
                        ImageView imageView = adapterView.getChildAt(i).findViewById(R.id.item_main_icon);
                        String title = mPageData.get(i).mTitle;
                        imageView.setBackgroundResource(mPageData.get(i).mUnseResourceId);
                    }
                }

            }
        });
        //updateNewMsgDisplay();
        initservice();
        uploadPhoneStepsData();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.BROADCAST_ACTION_QUIT_APP)) {
                    finish();
                } else if (intent.getAction().equals(Const.ACTION_UNBIND_RESET_FOCUS_WATCH)) {
                    finish();
                } else if (intent.getAction().equals(Const.ACTION_UNBIND_OTHER_WATCH)) {
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_ACTION_QUIT_APP);
        intentFilter.addAction(Const.ACTION_UNBIND_RESET_FOCUS_WATCH);
        intentFilter.addAction(Const.ACTION_UNBIND_OTHER_WATCH);
        mApp.getLocalBroadcastManager().registerReceiver(mBroadcastReceiver, intentFilter);

        mMainReceiver = new MainReceiver();
        mMainReceiver.registerReceiver(this);
        startCheckUpdate();
        LogUtil.d("huangqilin NewMainActivity oncreate");
        checkNotificationAbled();
        checkIsAlwaysFinish();
        requestNotificationPermission(this);
        //获取发现小红点
        mApp.getDiscoveryWarnUpdateData();
        //售后维修
        getUserDate();
        //App端拉取数据
        getIconAndPackTable();

        if (!RomUtils.instance.isBackgroundStartAllowed(this)) {
            BgStart.getInstance().requestStartPermisstion(this, new PermissionLisenter() {
                @Override
                public void onGranted() {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + AndroidUtil.getPackageName(context)));
//                context.startActivityForResult(intent, REQUEST_VODE_ALERT_WINDOW);
                    LogUtil.e("onGranted");
                }

                @Override
                public void cancel() {
//                sendBroadcast(new Intent(Const.ACTION_REQUEST_ALERT_WINDOW_CANCEL));
                    LogUtil.e("cancel");
                }

                @Override
                public void onDenied() {
                    LogUtil.e("onDenied");
                }
            }, "oppo", "huawei", "vivo", "meizu", "xiaomi");
        }else{
            PermissionUtils.needGotoAlertWindowActivity(this, mApp, false);
        }
    }

    private void checkNotificationAbled() {

        if (NotificationUtils.checkNotificationAbled(this))
            return;
        if (!NotificationUtils.checkShowNotificationDialog(mApp))
            return;

        Dialog dlg = DialogUtil.CustomNormalDialog(NewMainActivity.this,
                getString(R.string.able_read_notification),
                getString(R.string.able_read_notification_desc),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                },
                getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationUtils.startNotificationSetActivity(NewMainActivity.this);
                    }
                },
                getText(R.string.open_state).toString());
        dlg.show();
    }

    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(activity, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, POST_NOTIFICATIONS)) {
                    enableNotification(activity);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{POST_NOTIFICATIONS}, 100);
                }
            }
        } else {
            boolean enabled = NotificationManagerCompat.from(activity).areNotificationsEnabled();
            if (!enabled) {
                enableNotification(activity);
            }
        }
    }

    public static void enableNotification(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    private void checkAppUpdateStatus() {

        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);

        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();

        appUpdateInfo.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("hahahha", "onSuccess" + appUpdateInfo.availableVersionCode() + "---" + appUpdateInfo.updateAvailability() + " , installStatus=" + appUpdateInfo.installStatus());
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Request the update.
                    Log.d("hahahha", "Request the update........");
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                NewMainActivity.this,
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showInstallDialog();
                }
            }
        });
    }

    private void showInstallDialog() {
        Dialog dlg = DialogUtil.CustomALertDialog(NewMainActivity.this,
                getString(R.string.prompt),
                getString(R.string.update_downloaded),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setValue(Const.USER_NOT_UPDATETIME, String.valueOf(System.currentTimeMillis()));
                    }
                },
                getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        appUpdateManager.completeUpdate();
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showInstallDialog();
                Intent it = new Intent(Const.ACTION_APP_DOWNLOADED);
                sendBroadcast(it);
            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mMapType = mApp.getIntValue(Const.SHARE_PREF_FIELD_CHANEG_MAP, 1);
        Log.i("cui", "onNewIntent");
        if (isAutoLoginState()) {
            //已登录
            if (mApp.getWatchList() == null || mApp.getWatchList().size() == 0) {
                //没有设备的情况下刷新三tab
                if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("adwebBack"))) {
                    //从发现页面到主页不刷新
                } else {
                    refreshListTab("refreshTab");
                }
            } else {
                if (mApp.refreshTab) {
                    //有登录，并且有设备 刷新
                    refreshListTab("refreshTab");
                    //售后维修
                    getUserDate();
                }
            }
        } else {
            //没有登录的情况下 不刷新
            refreshListTab("Normal");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1 == requestCode && grantResults.length == 1) {
            if (grantResults[0] != PERMISSION_GRANTED) {
                finish();
            }
        } else if (2 == requestCode && grantResults.length == 1) {
            if (permissions[0].equals(Manifest.permission.CALL_PHONE) && grantResults[0] == PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mApp.getCurUser().getFocusWatch().getCellNum()));
                startActivity(callIntent);
            } else {
                Toast.makeText(NewMainActivity.this, getString(R.string.call_phone_premission_tips), Toast.LENGTH_SHORT).show();
            }
        } else if (100 == requestCode && grantResults.length == 1) {
            if (Build.VERSION.SDK_INT >= 33) {
                Toast.makeText(NewMainActivity.this, POST_NOTIFICATIONS, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPhoneStepsData() {
        //增加判空，解绑瞬间会产生
        if (mApp.getWatchList() == null || mApp.getWatchList().size() == 0) {
            return;
        }
        StepsUtil.initSensor(this, "0");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("huangqilin NewMainActivity onPause");
        mApp.setIsMsgPage(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //修改广告拉取和图片下载的入口
        checkAdLoadLint();


        //广告逻辑检查：1：是否在前台，2：是否开启开屏广告，3：activity是否回收
        if (!mApp.isCurrentRunningForeground && mApp.isAdSplashOnOff() && !isFcState
                && Constants.CONTROL_IS_OPEN_AD) {
            LogUtil.d(">>>>>>>>>>>>>>>>>>>切到前台 new main activity process");
            mApp.sdcardLog(">>>>>>>>>>>>>>>>>>>切到前台 new main activity process");
            String splshId = mApp.startSplashPage();
            if (splshId.equals("#########")) {
            } else {
                ADShowData taskData = new ADShowData();
                int i;
                for (i = 0; i < mApp.getAdShowList().size(); i++) {
                    taskData = mApp.getAdShowList().get(i);
                    if (taskData.adId.equals(splshId)) {
                        break;
                    }
                }
                if (i == mApp.getAdShowList().size()) {
                } else {
                    processByBack(taskData.adId, String.valueOf(taskData.adShowTime), taskData.adTarUrl);
                }
            }
        }
        mApp.isCurrentRunningForeground = true;

        if (mMiPushMessage != null && mMiPushMessage.length() > 0) {
            JSONObject resp = (JSONObject) JSONValue.parse(mMiPushMessage);
            JSONObject pl = (JSONObject) resp.get(CloudBridgeUtil.KEY_NAME_PL);
            if (pl != null) {
                if (!pl.containsKey(CloudBridgeUtil.KEY_NAME_SUB_ACTION)) {
                    Integer type = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_TYPE);
                    mMiPushMessage = null;
                } else {
                    int action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    if (action == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SOS) {
                        Intent it = new Intent(Const.ACTION_WARNNING_TYPE_SOS);
                        it.putExtra(Const.KEY_JSON_MSG, mMiPushMessage);
                        sendBroadcast(it);
                        mMiPushMessage = null;
                    }
                }
            }
        }
        LogUtil.d("huangqilin NewMainActivity onResume");
        if (mPage.getCurrentItem() == 1) {
            mApp.setIsMsgPage(true);
        }
        updateNewMsgDisplay();
        mApp.getDiscoveryWarnUpdateData();
        sendBroadcast(new Intent(Const.ACTION_CHECK_WEBSOCKET_STATE));
        checkUserGid();
        mApp.setMainActivityOpen(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateSettingRedPoint();
            }
        }, 1000);
        // mApp.destoryActivity("NewWelcomeActivity");
        //返回app上传当前手机语言
        Locale local;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            local = LocaleList.getDefault().get(0);
        } else {
            local = Locale.getDefault();
        }
        String lang = mApp.getStringValue("langrage", local.getLanguage());
        if (!lang.equals(local.getLanguage())) {
            mApp.getNetService().sendSetLangInfo();
        }
        refreshListTab("normal");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApp.isCurrentRunningForeground = mApp.isRunningForeground();
        if (!mApp.isCurrentRunningForeground) {
            LogUtil.d(">>>>>>>>>>>>>>>>>>>切到后台 new main activity process");
            mApp.sdcardLog(">>>>>>>>>>>>>>>>>>>切到后台 new main activity process");
        }
        LogUtil.d("huangqilin NewMainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(listener);
        }

        if (mMainReceiver != null) {
            mMainReceiver.unregisterReceiver(this);
        }
        if (mBroadcastReceiver != null) {
            mApp.getLocalBroadcastManager().unregisterReceiver(mBroadcastReceiver);
        }
        LogUtil.d("huangqilin NewMainActivity onDestroy");
        mApp.setMainActivityOpen(false);
        if (conn != null) {
            unbindService(conn);
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * user:zhangjun5 time:15:06 date:2017/1/19
     * desc:首页进入前台时，吊起广告页面修改为线程吊起，减少主页中resume中的操作
     **/
    private void processByBack(String adId, String adShowTime, String adTarUrl) {
        new MioAsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                String retcode = "0";
                LogUtil.i("adfilepath+adInterval+targetUrl:" + params[0] + ":" + params[1] + ":" + params[2]);
                Intent _intent = new Intent(NewMainActivity.this, SplashAdActivity.class);
                _intent.putExtra("adfilepath", params[0]);
                _intent.putExtra("adInterval", Integer.valueOf(params[1]));
                _intent.putExtra("targetUrl", params[2]);
                if (mApp.callState == Const.MESSAGE_CALL_INIT_STATE)  //如果是通话页面，就不再起广告
                    startActivity(_intent);
                return retcode;
            }

            protected void onCancelled() {
                super.onCancelled();
            }

            protected void onPostExecute(String message) {
                super.onPostExecute(message);
            }
        }.execute(adId, adShowTime, adTarUrl);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //   super.onBackPressed();
    }

    //adWebViewFragment adwebViewFragment;
    //StoreFragment storeFragment;
    MeFragment meFragment;
    GoogleMapFragment mapFragment;
    AllMessageFragment allMessageFragment;
    NewSettingFragment settingFragment;

    public void refreshListTab(String tag) {
        mPageData.clear();
        //有登录并有设备
        if (isAutoLoginState() && isHaveWatchList()) { //已经登录并且有设绑定备
            mApp.refreshTab = false;
            //位置
            mPageData.add(0, new PageData());
            mPageData.get(0).mTitle = getString(R.string.location);
            mPageData.get(0).mSeResourceId = R.drawable.tab_location_sel;
            mPageData.get(0).mUnseResourceId = R.drawable.tab_location;
            if (mapFragment == null) {
                mapFragment = new GoogleMapFragment();
            }
            mPageData.get(0).mFrag = mapFragment;

            //联系
            mPageData.add(1, new PageData());
            mPageData.get(1).mTitle = getString(R.string.chat_string);
            mPageData.get(1).mSeResourceId = R.drawable.tab_messages_sel;
            mPageData.get(1).mUnseResourceId = R.drawable.tab_messages;
            if (allMessageFragment == null) {
                allMessageFragment = new AllMessageFragment();
            }
            mPageData.get(1).mFrag = allMessageFragment;

            //我们
            mPageData.add(2, new PageData());
            mPageData.get(2).mTitle = getString(R.string.setting);
            mPageData.get(2).mSeResourceId = R.drawable.tab_me_sel;
            mPageData.get(2).mUnseResourceId = R.drawable.tab_me;
            if (settingFragment == null) {
                settingFragment = new NewSettingFragment();
            }
            mPageData.get(2).mFrag = settingFragment;

        } else {
            mApp.refreshTab = true;
            //我们
            mPageData.add(0, new PageData());
            mPageData.get(0).mTitle = getString(R.string.setting);
            mPageData.get(0).mSeResourceId = R.drawable.tab_me_sel;
            mPageData.get(0).mUnseResourceId = R.drawable.tab_me;
            if (meFragment == null) {
                meFragment = new MeFragment();
            }
            mPageData.get(0).mFrag = meFragment;
        }

        //mPage.setOffscreenPageLimit(mPageData.size());
        //从我的页面过来 刷新mPageAdapter
        if (!TextUtils.isEmpty(tag) && tag.equals("refreshTab")) {
            mPageAdapter.setNewFragments(mPageData);
            listadapter.notifyDataSetChanged();

            if (isAutoLoginState() && isHaveWatchList()) {
                selectIndex = 1;
            } else {
                selectIndex = mPageData.size() - 1;
            }
            mPage.setCurrentItem(selectIndex);
            listadapter.setSelectIndex(selectIndex);
            //刷新小红点
            updateNewMsgDisplay();
        }
    }

    private void initView() {
    }

    private ServiceConnection conn;

    private void initservice() {
        mApp.sdcardLog("NewMainActivity initservice ");
        Intent it = new Intent(NewMainActivity.this, NetService.class);
        bindService(it, conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mApp.sdcardLog("NewMainActivity onServiceConnected ");
                mNetService = ((NetService.MyBinder) service).getService();
                if (mNetService != null) {
                    if (mNetService.isCloudBridgeClientOk()) {
                        //获取一次手表定位信息
                        String[] eid = new String[1];
                        if (mApp.getWatchList() != null) {
                            for (WatchData watch : mApp.getWatchList()) {
                                eid[0] = watch.getEid();
                                String time = mApp.getStringValue(eid[0] + Const.SHARE_PREF_EFID_GET_LAST_TIME, "0");
                                if (mApp.getStringValue(eid[0] + Const.SHARE_PREF_EFID_IS_HAVE, "0").equals("0")
                                        || TimeUtil.compareToDiffForTwoTime(time, TimeUtil.getTimeStampLocal()) >= 24 * 60 * 60) {
                                    mApp.setValue(eid[0] + Const.SHARE_PREF_EFID_GET_LAST_TIME, TimeUtil.getTimeStampLocal());
                                    getEFence(eid[0]);
                                }
//                            String ADSplashOnOff = Const.KEY_NAME_SPLASH_ON_OFF+getPackageName();
//                            String ADMainOnOff = Const.KEY_NAME_MAIN_AD_ON_OFF+getPackageName();
//                            LogUtil.e("AD/"+ADSplashOnOff+"/"+ADMainOnOff);
                                String[] keys = new String[14];
                                keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
                                keys[1] = CloudBridgeUtil.WATCH_ONOFF_FLAG;
                                keys[2] = CloudBridgeUtil.OPERATION_MODE_VALUE;
                                keys[3] = CloudBridgeUtil.SIGNAL_LEVEL;
                                keys[4] = CloudBridgeUtil.DEVICE_POWER_ON_TIME;
                                keys[5] = CloudBridgeUtil.SLEEP_LIST;
                                keys[6] = CloudBridgeUtil.KEY_NAME_CHARGE_STATUS;
                                keys[7] = CloudBridgeUtil.STEPS_NOTIFICATION_SETTING;
//                            keys[8] = ADSplashOnOff;
//                            keys[9] = ADMainOnOff;
                                keys[8] = Const.KEY_NAME_SPLASH_ON_OFF;
                                keys[9] = Const.KEY_NAME_MAIN_AD_ON_OFF;
                                keys[10] = CloudBridgeUtil.KEY_STORY_SWITCH + watch.getDeviceType();
                                keys[11] = CloudBridgeUtil.OFFLINE_MODE_VALUE;
                                keys[12] = CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE;
                                keys[13] = CloudBridgeUtil.KEY_SUPER_POWER_SAVING;
                                mapMGet(eid[0], keys, NewMainActivity.this);
                                if (mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_LAST_GET_TIME, Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)
                                        || (System.currentTimeMillis() - Long.valueOf(mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_LAST_GET_TIME, Const.DEFAULT_NEXT_KEY)) > Const.PERMANENT_TIME)) {
                                    GetPermanent(watch.getEid());
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_LAST_GET_TIME, String.valueOf(System.currentTimeMillis()));
                                }

                                if (watch.isSupportTraceLocation())
                                    getTraceStatue(watch.getEid(), watch.getFamilyId());
                            }
                        }

                        sendBroadcast(new Intent(Const.ACTION_BAND_NETSERVICE_IS_OK));

                        String bindRequest = mApp.getBindRequest();
                        if (bindRequest != null && bindRequest.length() > 0) {
                            if (mNetService != null) {
                                mNetService.handleBindRequest(bindRequest);
                                mApp.setBindRequest(null);
                            }
                        }

                        String friendRequest = mApp.getFriendRequest();
                        if (friendRequest != null && friendRequest.length() > 0) {
                            if (mNetService != null) {
                                mNetService.handleNewFriendRequest(friendRequest);
                                mApp.setFriendRequest(null);
                            }
                        }
                    }
                    //功能小红点
//                    if (mNetService != null)
//                        RedDotUtils.getInstance(mApp).checkNeedGetRedDot(mNetService.AES_KEY, mApp.getToken());
                }
            }
        }, Context.BIND_AUTO_CREATE);

    }

    public NetService getNetservice() {
        return mNetService;
    }

    private void getEFence(String eid) {
        MyMsgData req = new MyMsgData();
        req.setCallback(NewMainActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        req.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_GET, pl));
        mNetService.sendNetMsg(req);
    }

    private void GetPermanent(String eid) {
        MyMsgData e2clist = new MyMsgData();
        e2clist.setCallback(NewMainActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        e2clist.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_PERMANENT_REQ, pl));
        mNetService.sendNetMsg(e2clist);
    }

    public void mapMGet(String eid, String[] keys, MsgCallback callback) {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(callback);

        JSONArray plKeyList = new JSONArray();
        for (int i = 0; i < keys.length; i++) {
            plKeyList.add(keys[i]);
        }

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (mNetService != null && mNetService.isCloudBridgeClientOk()) {
            mNetService.sendNetMsg(mapget);
        }
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private boolean isAutoLoginState() {
        boolean flag = mApp.needAutoLogin();
        return mApp.needAutoLogin();
    }

    private boolean isHaveWatchList() {
        return mApp.getWatchList() != null && mApp.getWatchList().size() != 0;
    }

    public class PageData {
        public int mSeResourceId;
        public int mUnseResourceId;
        public String mTitle;
        public Fragment mFrag;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("hahahha", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                mApp.setValue(Const.USER_NOT_UPDATETIME, String.valueOf(System.currentTimeMillis()));
            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            fm.getFragments();
            if (fm.getFragments().size() > 0) {
                List<Fragment> frags = fm.getFragments();
                for (int i = 0; i < frags.size(); i++) {
                    Fragment f = frags.get(i);
                    if (f != null) f.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int rc;
        JSONObject pl, plReq;
        String eid;
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_EFENCE_GET_RESP:
                pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (1 == rc) {
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl == null)//增加判空
                        return;
                    ArrayList<EFence> efence = new ArrayList<EFence>();
                    for (Map.Entry<String, Object> entry : pl.entrySet()) {
                        EFence fence = new EFence();
                        fence.efid = entry.getKey();
                        JSONObject json = (JSONObject) entry.getValue();
                        fence.eid = eid;
                        fence.desc = (String) json.get(CloudBridgeUtil.KEY_NAME_EFID_DESC);
                        if (fence.desc == null || fence.desc.equals("")) {
                            break;
                        }
                        fence.lat = (Double) json.get(CloudBridgeUtil.KEY_NAME_LAT);
                        fence.lng = (Double) json.get(CloudBridgeUtil.KEY_NAME_LNG);
                        fence.radius = (Integer) json.get(CloudBridgeUtil.KEY_NAME_EFID_RADIUS);
                        fence.name = (String) json.get(CloudBridgeUtil.KEY_NAME_NAME);
                        efence.add(fence);
                        mApp.setValue(eid + Const.SHARE_PREF_EFID_IS_HAVE, "1");
                    }
                    mApp.getmWatchEFence().put(eid, efence);
                }
                break;

            case CloudBridgeUtil.CID_GET_PERMANENT_RESP:
                mApp.sdcardLog(reqMsg.toString());
                mApp.sdcardLog(respMsg.toString());
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                if (1 == rc && pl != null) {
                    eid = (String) ((JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL)).get(CloudBridgeUtil.KEY_NAME_EID);
                    ArrayList<EFence> tempEfence = mApp.getmWatchEFence().get(eid);
                    if (tempEfence != null) {
                        for (EFence fence : tempEfence) {
                            if (fence.efid.equals("EFID1")) {
                                JSONObject pre = (JSONObject) pl.get("PERMANENTID1");
                                if (pre != null) {
                                    JSONArray base = (JSONArray) pre.get("Base");
                                    mApp.setValue(eid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, base.get(0).toString());
                                }
                            } else {
                                JSONObject pre = (JSONObject) pl.get("PERMANENTID" + fence.efid);
                                if (pre != null) {
                                    JSONArray base = (JSONArray) pre.get("Base");
                                    mApp.setValue(eid + fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, base.get(0).toString());
                                }
                            }
                        }
                    }
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (CloudBridgeUtil.RC_SUCCESS == rc) {
                    if (plReq != null && pl != null && mNetService != null) {
                        eid = (String) plReq.get(CloudBridgeUtil.KEY_NAME_EID);
                        String value = (String) pl.get(CloudBridgeUtil.BATTERY_LEVEL);
                        mNetService.handleMapMGetBattery(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.DEVICE_POWER_ON_TIME);
                        mNetService.handleMapMGetPowerOnTime(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.WATCH_ONOFF_FLAG);
                        mNetService.handleMapMGetWatchOnOff(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE);
                        mNetService.handleMapMGETWatchNetState(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.OPERATION_MODE_VALUE);
                        mNetService.handleMapMGetWatchMode(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.SIGNAL_LEVEL);
                        mNetService.handleMapMGETWatchSignal(eid, value);

                        value = (String) pl.get(CloudBridgeUtil.KEY_NAME_CHARGE_STATUS);
                        if (value != null && value.length() == 1) {
                            mApp.setmChargeState(eid, Integer.valueOf(value));
                        }
                        if (pl.containsKey(CloudBridgeUtil.SILENCE_LIST)) {
                            String jstr = (String) pl.get(CloudBridgeUtil.SILENCE_LIST);
                            mNetService.updateSilenceTimeData(eid, jstr);
                        }
                        if (pl.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                            String jstr = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
                            mNetService.updateSleepListData(eid, jstr);
                        }

                        if (pl.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                            String jstr = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
                            mNetService.updateSleepListData(eid, jstr);
                        }

                        if (pl.containsKey(CloudBridgeUtil.OFFLINE_MODE_VALUE)) {
                            String jstr = (String) pl.get(CloudBridgeUtil.OFFLINE_MODE_VALUE);
                            mNetService.updateOfflineMode(eid, jstr);
                        }

                        Intent intent = new Intent(Const.ACTION_REFRESH_WATCH_TITLE);
                        intent.putExtra(Const.KEY_WATCH_ID, eid);
                        sendBroadcast(intent);

                        // 讲故事
                        WatchData focusWatch = mApp.getCurUser().queryWatchDataByEid(eid);
                        if (focusWatch != null && pl.containsKey(CloudBridgeUtil.KEY_STORY_SWITCH + focusWatch.getDeviceType())) {
                            String ximalayaOnOff = (String) plReq.get(CloudBridgeUtil.KEY_STORY_SWITCH + focusWatch.getDeviceType());
                            if (ximalayaOnOff != null && ximalayaOnOff.length() > 0) {
                                int visible = Integer.parseInt(ximalayaOnOff);
                                mApp.setValue(eid + Const.SHARE_PREF_FIELD_STORY_VISIBLE, visible);
                                sendBroadcast(new Intent(Const.ACTION_STORY_VISIBLE_CHANGE));
                            }
                        }

                        String sps = (String) pl.get(CloudBridgeUtil.KEY_SUPER_POWER_SAVING);
                        if(sps != null && sps.length() > 0){
                            mApp.setValue(eid + Constants.SHARE_PREF_SUPER_POWER_SAVING,Integer.parseInt(sps));
                        }

                        //广告相关的业务逻辑
//                        String ADSplashOnOff = Const.KEY_NAME_SPLASH_ON_OFF+getPackageName();
//                        String ADMainOnOff = Const.KEY_NAME_MAIN_AD_ON_OFF+getPackageName();
//
//                        if(pl.containsKey(ADSplashOnOff)) {
//                            if(pl.get(ADSplashOnOff) == null){
                        if (pl.containsKey(Const.KEY_NAME_SPLASH_ON_OFF)) {
                            if (pl.get(Const.KEY_NAME_SPLASH_ON_OFF) == null) {
                                return;
                            }
                            try {
                                Integer splashOnOff = Integer.parseInt((String) pl.get(Const.KEY_NAME_SPLASH_ON_OFF));
                                if (splashOnOff == null || splashOnOff == 0) {
                                    mApp.setAdSplashOnOff(false);
                                    mApp.setValue(Const.SHARE_PREF_AD_SPLASH_ONOFF, 0);
                                } else {
                                    mApp.setAdSplashOnOff(true);
                                    mApp.setValue(Const.SHARE_PREF_AD_SPLASH_ONOFF, 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        if(pl.containsKey(ADMainOnOff)) {
//                            if(pl.get(ADMainOnOff) == null){
                        if (pl.containsKey(Const.KEY_NAME_MAIN_AD_ON_OFF)) {
                            if (pl.get(Const.KEY_NAME_MAIN_AD_ON_OFF) == null) {
                                return;
                            }
                            try {
                                Integer mainADOnOff = Integer.parseInt((String) pl.get(Const.KEY_NAME_MAIN_AD_ON_OFF));
                                if (mainADOnOff == null || mainADOnOff == 0) {
                                    mApp.setAdMainAdOnOff(false);
                                    mApp.setValue(Const.SHARE_PREF_AD_MAINPAGE_ONOFF, 0);
                                } else {
                                    mApp.setAdMainAdOnOff(true);
                                    mApp.setValue(Const.SHARE_PREF_AD_MAINPAGE_ONOFF, 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        if(pl.containsKey(ADSplashOnOff) ||
//                                pl.containsKey(ADMainOnOff)){
                        if (pl.containsKey(Const.KEY_NAME_MAIN_AD_ON_OFF) ||
                                pl.containsKey(Const.KEY_NAME_SPLASH_ON_OFF)) {
                            if (mApp.isAdMainAdOnOff() || mApp.isAdSplashOnOff()) {
                                mApp.checkNeedGetAdUpdate();
                            }
                        }
                        value = (String) pl.get(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
                        if (value != null) {
                            if (value.equals("0")) {
                                mApp.setValue(eid + CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "0");
                            } else {
                                mApp.setValue(eid + CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "1");
                            }
                        }
                    }
                } else {
                    LogUtil.e("mapmget error rc = " + rc);
                }
                break;

            case CloudBridgeUtil.CID_TRACE_TO_GET_STATUE_RESP:
                Intent intent = new Intent(Const.ACTION_SELECT_TARCE_TO_MODE);
                intent.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                sendBroadcast(intent);
                break;

            case CloudBridgeUtil.CID_GET_CONTACT_RESP:
                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
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
                    final String aEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                    mApp.setValue(aEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, obj.toString());

                    if (aEid != null && aEid.length() > 0) {
                        WatchData watch = mApp.getCurUser().queryWatchDataByEid(aEid);
                        if (watch != null && !watch.isDevice102()) {
                            String userNumber = mApp.getUserNumberByEid(aEid);
                            if (userNumber == null || userNumber.length() == 0) {
                                DialogUtil.ShowCustomSingleTopSystemDialog(getApplicationContext(),
                                        getString(R.string.set_member_info),
                                        getString(R.string.set_member_detail, watch.getNickname()),
                                        null,
                                        null,
                                        new DialogUtil.OnCustomDialogListener() {

                                            @Override
                                            public void onClick(View v) {
//                                                Intent intent2 = new Intent(NewMainActivity.this, FirstSetActivity.class);
//                                                intent2.putExtra(CloudBridgeUtil.KEY_NAME_EID, aEid);
//                                                startActivity(intent2);
                                                String memberEid = mApp.getCurUser().getEid();
                                                if (memberEid == null) {
                                                    memberEid = mApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                                                    if (memberEid != null)
                                                        mApp.getCurUser().setEid(memberEid);
                                                }
                                                Intent intent2 = new Intent(NewMainActivity.this, AddCallMemberActivity.class);
                                                intent2.putExtra(Const.KEY_WATCH_ID, aEid);
                                                intent2.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 0);
                                                intent2.putExtra("eid", memberEid);
                                                intent2.putExtra(Const.SET_CONTACT_ISBIND, true);
                                                startActivity(intent2);
                                                mApp.setAdminBindFlag(false);
                                            }
                                        },
                                        getText(R.string.confirm).toString());
                            }
                        }
                    }
                    updateNewMsgDisplay();
                } else if (rc == -13) {
                    if (reqMsg == null) {
                        break;
                    }
                    pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl == null) {
                        break;
                    }

                    final String aEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                    if (aEid != null && aEid.length() > 0) {
                        WatchData watch = mApp.getCurUser().queryWatchDataByEid(aEid);
                        if (watch != null && !watch.isDevice102()) {
                            String userNumber = mApp.getUserNumberByEid(aEid);
                            if (userNumber == null || userNumber.length() == 0) {
                                DialogUtil.ShowCustomSingleTopSystemDialog(getApplicationContext(),
                                        getString(R.string.set_member_info),
                                        getString(R.string.set_member_detail, watch.getNickname()),
                                        null,
                                        null,
                                        new DialogUtil.OnCustomDialogListener() {

                                            @Override
                                            public void onClick(View v) {

                                                String memberEid = mApp.getCurUser().getEid();
                                                if (memberEid == null) {
                                                    memberEid = mApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                                                    if (memberEid != null)
                                                        mApp.getCurUser().setEid(memberEid);
                                                }
                                                Intent intent2 = new Intent(NewMainActivity.this, AddCallMemberActivity.class);
                                                intent2.putExtra(Const.KEY_WATCH_ID, aEid);
                                                intent2.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 0);
                                                intent2.putExtra("eid", memberEid);
                                                intent2.putExtra(Const.SET_CONTACT_ISBIND, true);
                                                startActivity(intent2);
                                                mApp.setAdminBindFlag(false);
                                            }
                                        },
                                        getText(R.string.confirm).toString());

                            }
                        }
                    }
                    updateNewMsgDisplay();
                }
                break;
            default:
                break;
        }
    }

    //获取应用包名和图标对应表
    private void getIconAndPackTable() {
        String updateTime = mApp.getStringValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE_TIME, TimeUtil.getTimeStampLocal());
        long duration = TimeUtil.compareToDiffForTwoTime(updateTime, TimeUtil.getTimeStampLocal());
        long days = duration / (3600 * 24);

        boolean isUpdate = mApp.getBoolValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
        if (!isUpdate || days >= 7 || !AppStoreUtils.getInstance(this).isLocalLangFromLastSet(mApp)) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_LANG, Locale.getDefault().getLanguage());
            AppStoreUtils.getInstance(this).getPackageAndIconTable(mApp);
        }
    }

    /**
     * user:zhangjun5 time:11:02 date:2017/6/19
     * desc:根据广告开关，判断是否拉取网络连接和下载开屏广告图片信息
     **/
    private void checkAdLoadLint() {
        if (mApp.isAdMainAdOnOff() || mApp.isAdSplashOnOff()) {
            mApp.checkNeedGetAdUpdate();
            //下载图片
            int length = mApp.getAdShowList().size();
            for (int i = 0; i < length; i++) {
                ADShowData taskData = mApp.getAdShowList().get(i);
                if (taskData.adType == 0) {
                    String expira = taskData.adExpirationTime;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    long dataCompare = TimeUtil.compareToDiffForTwoTime(expira, dateFormat.format(new Date()));
                    if (taskData.adIsShow == 1 && dataCompare < 0) {
                        mApp.sdcardLog("ADDOWNLOAD updateFlag:2");
                        mApp.downAdImgRes(taskData.adImgUrl, taskData.adId + ".jpg");
                    }
                }
            }
        }
    }

    private int getAllNewMsgCount() {
        if (mApp.getCurUser() == null || mApp.getCurUser().getFamilyList() == null) {
            return 0;
        }
        int allMsgCount = 0;
        for (int j = 0; j < mApp.getCurUser().getFamilyList().size(); j++) {
            String familyId = mApp.getCurUser().getFamilyList().get(j).getFamilyId();

            WatchData watch = mApp.getCurUser().getFamilyList().get(j).getWatchlist().get(0);
            if (watch.isSupportGroupMessage()) {
                allMsgCount += mApp.getNewGroupMsgCount(familyId);
            }
            if (watch.isSupportPrivateMessage()) {
                allMsgCount += mApp.getNewPrivateMsgCount(mApp.getWatchPrivateGid(watch.getEid()));
            }
            allMsgCount += mApp.getNewNoticeMsgCount(familyId);
        }
        return allMsgCount;
    }

    //消息页面小红点
    private void updateNewMsgDisplay() {
        int newMsgCount = getAllNewMsgCount();
        listadapter.setSelectIndex(selectIndex);
        listadapter.SetRedDotConcact(1, newMsgCount, isAutoLoginState());
    }

    //设置页面小红点
    public void updateSettingRedPoint() {
        if (settingFragment != null) {
            listadapter.setSelectIndex(selectIndex);
            boolean flag = settingFragment.IsShowAllRedPoint(NewMainActivity.this);
            listadapter.settingRedPoint(2, flag, isAutoLoginState());
        }
    }

    public void getTraceStatue(String eid, String gid) {
        MyMsgData req = new MyMsgData();
        req.setCallback(NewMainActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        req.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_TRACE_TO_GET_STATUS, pl));
        if (mNetService != null)
            mNetService.sendNetMsg(req);
    }

    // 启动app时检测更新
    private void startCheckUpdate() {
        if (mApp.showUpdateResult(NewMainActivity.this, true, false, true) > 0) {
            // update app first
        } else {
            mApp.checkNeedUpdate(NewMainActivity.this, 0, true);
            if (mApp.getCurUser().getFocusWatch() != null && !mApp.getCurUser().getFocusWatch().isDevice306_A03())
                mApp.checkNeedUpdateWatch(NewMainActivity.this, mApp.getCurUser().getFocusWatch());
        }
    }

    private class MainReceiver extends BroadcastReceiver {
        // 注册监听
        public void registerReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_PROCESSED_NOTIFY_OK);
            filter.addAction(Const.ACTION_RECEIVE_NOTICE_MSG);
            filter.addAction(Const.ACTION_GET_OFFLINE_CHAT_MSG);
            filter.addAction(Const.ACTION_WATCH_UPGRADE_RESULT);
            filter.addAction(Const.ACTION_BROAST_SENSOR_STEPS);
            filter.addAction(Const.ACTION_BROAST_DISCOVERY_WARN);
            filter.addAction(Const.ACTION_BROAST_DISCOVERY_FIND);
            context.registerReceiver(this, filter);
        }

        // 关闭监听
        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Const.ACTION_PROCESSED_NOTIFY_OK)
                    || intent.getAction().equals(Const.ACTION_RECEIVE_NOTICE_MSG)) {
                updateNewMsgDisplay();
            } else if (intent.getAction().equals(Const.ACTION_BROAST_DISCOVERY_FIND)) {
                mApp.getDiscoveryWarnUpdateData();
            } else if (intent.getAction().equals(Const.ACTION_BROAST_DISCOVERY_WARN)) {
                String warnInfo = intent.getStringExtra(Const.DISCOVERY_WARN_INFO);
                if (warnInfo == null || warnInfo.equals("")) {
                    return;
                }
                try {
                    org.json.JSONObject reddotJson = new org.json.JSONObject(warnInfo);
                    String updataTime = reddotJson.getString("updateTime");
                    int reddotCount = reddotJson.getInt("size");
                    LogUtil.e("updateTime+size:" + updataTime + ":" + reddotCount);
                    //updateDiscoveryReddotDisplay(reddotCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(Const.ACTION_GET_OFFLINE_CHAT_MSG)) {
                for (WatchData watch : mApp.getWatchList()) {
                    //查询实时追踪的状态
                    if (watch.isSupportTraceLocation())
                        getTraceStatue(watch.getEid(), watch.getFamilyId());
                }
            } else if (intent.getAction().equals(Const.ACTION_WATCH_UPGRADE_RESULT)) {
                if (mApp.getCurUser().getFocusWatch() != null &&
                        !(mApp.getCurUser().getFocusWatch().isDevice102()
                                && mApp.getWatchVerCode(mApp.getCurUser().getFocusWatch().getVerCur()).compareTo("24") < 0)) {
                    if (!mApp.getIsSystemUpdateActivityOpen()) {
                        mApp.showWatchUpdateResult(NewMainActivity.this, false, true);
                    }
                }
            } else if (intent.getAction().equals(Const.ACTION_BROAST_SENSOR_STEPS)) {
                String sensorSteps = intent.getStringExtra("sensor_steps");
                String sensorType = intent.getStringExtra("sensor_type");
                if (!sensorType.equals("0")) {
                    return;
                }
                String curSteps = StepsUtil.getPhoneStepsByFirstSteps(mApp, sensorSteps);
                LogUtil.e("phoneSteps:" + curSteps);
                for (WatchData watchData : mApp.getWatchList()) {
                    StepsUtil.getRanksDataFromCloud(mApp, "0", watchData.getEid(), curSteps);
                }
            }
        }
    }

    private void checkUserGid() {
        if (mApp.getCurUser().getWatchList() != null) {
            for (int i = 0; i < mApp.getCurUser().getWatchList().size(); i++) {
                WatchData watch = mApp.getCurUser().getWatchList().get(i);
                if (!watch.isDevice102()) {
                    String userGid = mApp.getWatchPrivateGid(watch.getEid());
                    if ((userGid == null || userGid.length() == 0) && watch.getEid() != null) {
                        sendGetContactReq(watch.getEid());
                    }
                }
            }
        }
    }

    private void sendGetContactReq(String eid) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_CONTACT_REQ,
                sn, mApp.getToken(), pl));
        if (mNetService != null) {
            mNetService.sendNetMsg(req);
        }
    }

    private void checkIsAlwaysFinish() {
        int alwaysFinish = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
        if (alwaysFinish == 1) {
            DialogUtil.ShowCustomSystemDialog(NewMainActivity.this,
                    mApp.getString(R.string.prompt), getString(R.string.isalwaysfinish_prompt),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, mApp.getString(R.string.quit),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            startActivity(intent);
                        }
                    }, mApp.getString(R.string.goto_permission_set));
        }
    }

    private int fouceWatchIndex = 0;

    public void initWatchScroll(View view, int watchSize) {
        final HorizontalScrollView moreWatch = view.findViewById(R.id.watch_scroll);
        moreWatch.setVerticalScrollBarEnabled(false);//垂直方向的水平滚动条是否显示
        moreWatch.setHorizontalScrollBarEnabled(false);//水平方向的水平滚动条是否显示
        LinearLayout moreWatchLayout = view.findViewById(R.id.more_watch);
        LinearLayout twoWatch = view.findViewById(R.id.two_watch);
        LinearLayout threeWatch = view.findViewById(R.id.three_watch);
        Collections.sort(mApp.getCurUser().getWatchList(), new Comparator<WatchData>() {
            @Override
            public int compare(WatchData lhs, WatchData rhs) {
                if (lhs == null || lhs.getNickname() == null)
                    return 1;
                if (rhs == null || rhs.getNickname() == null)
                    return -1;
                return lhs.getNickname().compareTo(rhs.getNickname());
            }
        });
        if (watchSize == 1) {
            moreWatch.setVisibility(View.GONE);
            twoWatch.setVisibility(View.GONE);
            threeWatch.setVisibility(View.GONE);
        } else if (watchSize == 2) {
            moreWatch.setVisibility(View.VISIBLE);
            twoWatch.setVisibility(View.VISIBLE);
            threeWatch.setVisibility(View.GONE);
            RelativeLayout two_watch_1 = view.findViewById(R.id.two_watch_1);
            RelativeLayout two_watch_1_image = view.findViewById(R.id.two_watch_1_image);
            ImageView two_watch_1_head = view.findViewById(R.id.two_watch_1_head);
            ImageView two_watch_1_mask = view.findViewById(R.id.two_watch_1_mask);
            TextView two_watch_1_name = view.findViewById(R.id.two_watch_1_name);
            two_watch_1_name.setText(mApp.getCurUser().getWatchList().get(0).getNickname());
            ImageView two_watch_1_line = view.findViewById(R.id.two_watch_1_line);
            String watchEid = mApp.getCurUser().getWatchList().get(0).getEid();
            final WatchData watch0 = mApp.getCurUser().getWatchList().get(0);
            ImageUtil.setMaskImage(two_watch_1_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                    mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
            if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                two_watch_1_line.setVisibility(View.VISIBLE);
                two_watch_1_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                two_watch_1_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                two_watch_1_name.setTextSize(16);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(two_watch_1_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, two_watch_1_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                two_watch_1_name.setLayoutParams(ly);
                two_watch_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                two_watch_1_line.setVisibility(View.INVISIBLE);
                two_watch_1_head.setBackgroundResource(R.drawable.peopletab_picbg);
                two_watch_1_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                two_watch_1_name.setTextSize(14);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(two_watch_1_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, two_watch_1_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                two_watch_1_name.setLayoutParams(ly);
                two_watch_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setFocusWatch(watch0);
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        sendBroadcast(it);
                    }
                });
            }

            RelativeLayout two_watch_2 = view.findViewById(R.id.two_watch_2);
            RelativeLayout two_watch_2_image = view.findViewById(R.id.two_watch_2_image);
            ImageView two_watch_2_head = view.findViewById(R.id.two_watch_2_head);
            ImageView two_watch_2_mask = view.findViewById(R.id.two_watch_2_mask);
            TextView two_watch_2_name = view.findViewById(R.id.two_watch_2_name);
            two_watch_2_name.setText(mApp.getCurUser().getWatchList().get(1).getNickname());
            ImageView two_watch_2_line = view.findViewById(R.id.two_watch_2_line);
            watchEid = mApp.getCurUser().getWatchList().get(1).getEid();
            final WatchData watch1 = mApp.getCurUser().getWatchList().get(1);
            ImageUtil.setMaskImage(two_watch_2_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                    mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
            if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                two_watch_2_line.setVisibility(View.VISIBLE);
                two_watch_2_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                two_watch_2_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                two_watch_2_name.setTextSize(16);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(two_watch_2_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, two_watch_2_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                two_watch_2_name.setLayoutParams(ly);
                two_watch_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                two_watch_2_line.setVisibility(View.INVISIBLE);
                two_watch_2_head.setBackgroundResource(R.drawable.peopletab_picbg);
                two_watch_2_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                two_watch_2_name.setTextSize(14);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(two_watch_2_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, two_watch_2_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                two_watch_2_name.setLayoutParams(ly);
                two_watch_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setFocusWatch(watch1);
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        sendBroadcast(it);
                    }
                });
            }
        } else if (watchSize == 3) {
            moreWatch.setVisibility(View.VISIBLE);
            twoWatch.setVisibility(View.GONE);
            threeWatch.setVisibility(View.VISIBLE);
            RelativeLayout three_watch_1 = view.findViewById(R.id.three_watch_1);
            RelativeLayout three_watch_1_image = view.findViewById(R.id.three_watch_1_image);
            ImageView three_watch_1_head = view.findViewById(R.id.three_watch_1_head);
            ImageView three_watch_1_mask = view.findViewById(R.id.three_watch_1_mask);
            TextView three_watch_1_name = view.findViewById(R.id.three_watch_1_name);
            three_watch_1_name.setText(mApp.getCurUser().getWatchList().get(0).getNickname());
            ImageView three_watch_1_line = view.findViewById(R.id.three_watch_1_line);
            String watchEid = mApp.getCurUser().getWatchList().get(0).getEid();
            final WatchData watch0 = mApp.getCurUser().getWatchList().get(0);
            ImageUtil.setMaskImage(three_watch_1_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                    mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
            if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                three_watch_1_line.setVisibility(View.VISIBLE);
                three_watch_1_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                three_watch_1_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                three_watch_1_name.setTextSize(16);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_1_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_1_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                three_watch_1_name.setLayoutParams(ly);
                three_watch_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                three_watch_1_line.setVisibility(View.INVISIBLE);
                three_watch_1_head.setBackgroundResource(R.drawable.peopletab_picbg);
                three_watch_1_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                three_watch_1_name.setTextSize(14);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_1_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_1_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                three_watch_1_name.setLayoutParams(ly);
                three_watch_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setFocusWatch(watch0);
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        sendBroadcast(it);
                    }
                });
            }

            RelativeLayout three_watch_2 = view.findViewById(R.id.three_watch_2);
            RelativeLayout three_watch_2_image = view.findViewById(R.id.three_watch_2_image);
            ImageView three_watch_2_head = view.findViewById(R.id.three_watch_2_head);
            ImageView three_watch_2_mask = view.findViewById(R.id.three_watch_2_mask);
            TextView three_watch_2_name = view.findViewById(R.id.three_watch_2_name);
            three_watch_2_name.setText(mApp.getCurUser().getWatchList().get(1).getNickname());
            ImageView three_watch_2_line = view.findViewById(R.id.three_watch_2_line);
            watchEid = mApp.getCurUser().getWatchList().get(1).getEid();
            final WatchData watch1 = mApp.getCurUser().getWatchList().get(1);
            ImageUtil.setMaskImage(three_watch_2_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                    mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
            if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                three_watch_2_line.setVisibility(View.VISIBLE);
                three_watch_2_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                three_watch_2_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                three_watch_2_name.setTextSize(16);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_2_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_2_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                three_watch_2_name.setLayoutParams(ly);
                three_watch_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                three_watch_2_line.setVisibility(View.INVISIBLE);
                three_watch_2_head.setBackgroundResource(R.drawable.peopletab_picbg);
                three_watch_2_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                three_watch_2_name.setTextSize(14);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_2_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_2_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                three_watch_2_name.setLayoutParams(ly);
                three_watch_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setFocusWatch(watch1);
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        sendBroadcast(it);
                    }
                });
            }

            RelativeLayout three_watch_3 = view.findViewById(R.id.three_watch_3);
            RelativeLayout three_watch_3_image = view.findViewById(R.id.three_watch_3_image);
            ImageView three_watch_3_head = view.findViewById(R.id.three_watch_3_head);
            ImageView three_watch_3_mask = view.findViewById(R.id.three_watch_3_mask);
            TextView three_watch_3_name = view.findViewById(R.id.three_watch_3_name);
            three_watch_3_name.setText(mApp.getCurUser().getWatchList().get(2).getNickname());
            ImageView three_watch_3_line = view.findViewById(R.id.three_watch_3_line);
            watchEid = mApp.getCurUser().getWatchList().get(2).getEid();
            final WatchData watch2 = mApp.getCurUser().getWatchList().get(2);
            ImageUtil.setMaskImage(three_watch_3_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                    mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
            if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                three_watch_3_line.setVisibility(View.VISIBLE);
                three_watch_3_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                three_watch_3_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                three_watch_3_name.setTextSize(16);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_3_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_3_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                three_watch_3_name.setLayoutParams(ly);
                three_watch_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                three_watch_3_line.setVisibility(View.INVISIBLE);
                three_watch_3_head.setBackgroundResource(R.drawable.peopletab_picbg);
                three_watch_3_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                three_watch_3_name.setTextSize(14);
                RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(three_watch_3_name.getLayoutParams());
                ly.addRule(RelativeLayout.RIGHT_OF, three_watch_3_image.getId());
                ly.addRule(RelativeLayout.CENTER_VERTICAL);
                ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                three_watch_3_name.setLayoutParams(ly);
                three_watch_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.setFocusWatch(watch2);
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        sendBroadcast(it);
                    }
                });
            }
        } else if (watchSize > 3) {
            moreWatch.setVisibility(View.VISIBLE);
            twoWatch.setVisibility(View.GONE);
            threeWatch.setVisibility(View.GONE);
            moreWatchLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < watchSize; i++) {
                View item = inflater.inflate(R.layout.new_watch_scroll_item, null);
                RelativeLayout more_watch_image = item.findViewById(R.id.more_watch_image);
                ImageView more_watch_head = item.findViewById(R.id.more_watch_head);
                ImageView more_watch_mask = item.findViewById(R.id.more_watch_mask);
                TextView more_watch_name = item.findViewById(R.id.more_watch_name);
                more_watch_name.setText(mApp.getWatchList().get(i).getNickname());
                ImageView more_watch_line = item.findViewById(R.id.more_watch_line);
                String watchEid = mApp.getWatchList().get(i).getEid();
                final WatchData watch = mApp.getWatchList().get(i);
                ImageUtil.setMaskImage(more_watch_mask, R.drawable.peopletab_mask, mApp.getHeadDrawableByFile(getResources(),
                        mApp.getCurUser().getHeadPathByEid(watchEid), watchEid, R.drawable.small_default_head));
                if (mApp.getCurUser().getFocusWatch().getEid().equals(watchEid)) {
                    more_watch_line.setVisibility(View.VISIBLE);
                    more_watch_head.setBackgroundResource(R.drawable.peopletab_picbg_sel);
                    more_watch_name.setTextColor(getResources().getColor(R.color.FF_F66D3E));
                    more_watch_name.setTextSize(16);
                    RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(more_watch_name.getLayoutParams());
                    ly.addRule(RelativeLayout.RIGHT_OF, more_watch_image.getId());
                    ly.addRule(RelativeLayout.CENTER_VERTICAL);
                    ly.setMargins(DensityUtil.dip2px(this, 12), 0, 0, 0);
                    more_watch_name.setLayoutParams(ly);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    fouceWatchIndex = i;
                } else {
                    more_watch_line.setVisibility(View.INVISIBLE);
                    more_watch_head.setBackgroundResource(R.drawable.peopletab_picbg);
                    more_watch_name.setTextColor(getResources().getColor(R.color.black_d9_000000));
                    more_watch_name.setTextSize(14);
                    RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(more_watch_name.getLayoutParams());
                    ly.addRule(RelativeLayout.RIGHT_OF, more_watch_image.getId());
                    ly.addRule(RelativeLayout.CENTER_VERTICAL);
                    ly.setMargins(DensityUtil.dip2px(this, 6), 0, 0, 0);
                    more_watch_name.setLayoutParams(ly);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApp.setFocusWatch(watch);
                            Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                            sendBroadcast(it);
                        }
                    });
                }
                moreWatchLayout.addView(item);
            }
            Runnable runnable = new Runnable() {

                public void run() {
                    moreWatch.scrollTo((fouceWatchIndex - 1) * DensityUtil.dip2px(NewMainActivity.this, 120), 0);
                }

            };

            Handler handler = new Handler();
            handler.post(runnable);
        }
    }

    //售后维修
    private void getUserDate() {
        new MioAsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String getAesData = getEncryptData();
                    String encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(getAesData, mApp.getNetService().getAESKEY(), mApp.getNetService().getAESKEY())) + mApp.getToken();
                    return ImibabyApp.PostJsonWithURLConnection(encryptData, FunctionUrl.SEARCH_BIND_STATUS_URL, false, mApp.getAssets().open("dxclient_t.bks"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("PL");
                    String isStatus = String.valueOf(jsonObject1.get("status"));
                    mApp.setValue(Const.SHARE_PREF_FIELD_REPAIR_ONOFF, isStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private String getEncryptData() {
        JSONObject jsonObject = new JSONObject();
        if (mApp.getCurUser() != null && mApp.getCurUser().getFocusWatch() != null) {
            jsonObject.put("eid", mApp.getCurUser().getFocusWatch().getEid());
        }
        jsonObject.put("appPackage", this.getPackageName());
        jsonObject.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        return jsonObject.toJSONString();
    }

    private void checkExternalStorgePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// android 11 外部存储权限
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                PermissionUtils.reqExternStoragePermission(NewMainActivity.this, 1024);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
