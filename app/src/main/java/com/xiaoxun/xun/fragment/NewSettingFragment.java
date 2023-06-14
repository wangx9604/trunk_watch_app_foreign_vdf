package com.xiaoxun.xun.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.activitys.ScheduleCardActivity;
import com.xiaoxun.xun.activitys.AddNewFriendActivity;
import com.xiaoxun.xun.activitys.AppAboutActivity;
import com.xiaoxun.xun.activitys.AppManagerActivity;
import com.xiaoxun.xun.activitys.AppStoreActivity;
import com.xiaoxun.xun.activitys.BindNewActivity;
import com.xiaoxun.xun.activitys.CallLogActivity;
import com.xiaoxun.xun.activitys.DeviceDetailActivity;
import com.xiaoxun.xun.activitys.DeviceQrActivity;
import com.xiaoxun.xun.activitys.DeviceRepairActivity;
import com.xiaoxun.xun.activitys.FamilyMemberActivity;
import com.xiaoxun.xun.activitys.HelpWebActivity;
import com.xiaoxun.xun.activitys.NewMainActivity;
import com.xiaoxun.xun.activitys.PowersaveSettingActivity;
import com.xiaoxun.xun.activitys.SecurityZoneActivity;
import com.xiaoxun.xun.activitys.SleepTimeActivity;
import com.xiaoxun.xun.activitys.SportActivity;
import com.xiaoxun.xun.activitys.StepsActivity;
import com.xiaoxun.xun.activitys.SuperPowerSavingActivity;
import com.xiaoxun.xun.activitys.SystemUpdateActivity;
import com.xiaoxun.xun.activitys.WatchManagerActivity;
import com.xiaoxun.xun.beans.SettingBean;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.dialBg.DialBgActivity;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.activity.MotionSportRecordActivity;
import com.xiaoxun.xun.networkv2.XiaoXunCloudInfoClient;
import com.xiaoxun.xun.networkv2.apis.OnResponCallBack;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.securityarea.activity.SecurityMapMainActivity;
import com.xiaoxun.xun.securityarea.activity.SecurityWelcomeActivity;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.RedDotUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.WebViewUtil;
import com.xiaoxun.xun.views.CustomSettingNewView;
import com.xiaoxun.xun.views.JGridView;
import com.xiaoxun.xun.views.WatchListPopUpWindow;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import okhttp3.ResponseBody;

/**
 * @author cuiyufeng
 * @Description: NewSettingFragment
 * @date 2018/12/13 11:21
 */

public class NewSettingFragment extends Fragment implements View.OnClickListener, LoadingDialog.OnConfirmClickListener, MsgCallback {
    private ImibabyApp mApp;
    private Context context;
    private NewMainActivity mHostActivity;
    private SettingReceiver mSettingReceiver;

    private View viewRoot;
    private TextView mTitleName;
    private ImageButton mBtnMenu;
    private ImageButton mBtnAdd;
    private RelativeLayout mWatchSelectView;
    private ImageButton mListBtn;
    private View mLyTitleShadow;
    private WatchListPopUpWindow mMenuWindow;
    private LoadingDialog loadingdlg;
    private WatchData focusWatch;
    private JGridView jgridview1, jgridview2, jgridview3;
    private List<SettingBean> settingList1 = new ArrayList<>();
    private List<SettingBean> settingList2 = new ArrayList<>();
    private List<SettingBean> settingList3 = new ArrayList<>();

    boolean isXiaoaiReddot;
    private String settingManger;

    boolean isShowDialShop = false;
    boolean isShowDialShopRedDot = true;
    static EventChannel.EventSink eventSink = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mApp = (ImibabyApp) getActivity().getApplication();
        focusWatch = mApp.getCurUser().getFocusWatch();
        mHostActivity = (NewMainActivity) getActivity();
        context = mHostActivity;
        viewRoot = inflater.inflate(R.layout.fragment_device_newsetting, container, false);
        initViews(viewRoot);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSettingReceiver = new SettingReceiver();
        mSettingReceiver.registerReceiver(getActivity());
        mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
        updateViewShow();
        if (jgridview1 != null) {
            jgridview1.setAdapter(baseAdapter1);
        }
        if (jgridview2 != null) {
            jgridview2.setAdapter(baseAdapter2);
        }
        if (jgridview3 != null) {
            jgridview3.setAdapter(baseAdapter3);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
        updateViewShow();
        for (int i = 0; i < settingList1.size(); i++) {
            if (settingList1.get(i).getName().equals(getString(R.string.power_saving_setting))) {
                settingList1.remove(i);
                settingList1.add(i, new SettingBean(getString(R.string.power_saving_setting), R.drawable.icon_power, false));
                jgridview1.refresh();
            }
        }

        if (haveUpdate(getActivity())) {
            settingList3.remove(0);
            settingList3.add(0, new SettingBean(getString(R.string.setting_check_update), R.drawable.icon_updata, haveUpdate(getActivity())));
            jgridview3.refresh();
        }

        if (jgridview1 != null) {
            jgridview1.refresh();
        }
        if (jgridview2 != null) {
            jgridview2.refresh();
        }
        if (jgridview3 != null) {
            jgridview3.refresh();
        }
        mHostActivity.updateSettingRedPoint();
        mHostActivity.updateSettingRedPoint();
    }

    private boolean before24h(String lastTime) {
        LogUtil.e("before24h  lastTime = " + lastTime);
        //创建SimpleDateFormat对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String nowString = TimeUtils.getNowString(sdf);
        //转换字符串为Date对象
        Date date = null;
        Date nowDate = null;
        try {
            date = sdf.parse(lastTime);
            nowDate = sdf.parse(nowString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //获取毫秒数
        long millis = date.getTime();
        //获取当前时间戳
        long now = nowDate.getTime();
        //计算两者之间的毫秒数
        long diff = now - millis;
        //计算两者之间的天数
        double days = diff / (1000 * 60 * 60 * 24.0);
        //判断是否大于1
        LogUtil.e("before24h = " + days);
        return days >= 1;
    }

    private void getDialShopShowStatus() {
        isShowDialShop = mApp.getBoolValue(Const.DIAL_SHOP_SHOW + mApp.getCurUser().getFocusWatch().getImei(), false);
//        isShowDialShop = true;
        isShowDialShopRedDot = mApp.getBoolValue(Const.DIAL_SHOP_REDDOTVER_SHOW_TAG + mApp.getCurUser().getFocusWatch().getImei(), true);
        //大于24H请求表盘商店入口权限
        String lastTime = mApp.getStringValue(Const.DIAL_SHOP_SHOW_LONGTIME + mApp.getCurUser().getFocusWatch().getImei(), TimeUtils.getStringByNow(-1,new SimpleDateFormat("yyyyMMddHHmmssSSS"), TimeConstants.DAY));
        if (before24h(lastTime)) {
            JSONObject dialShap = new JSONObject();
            JSONObject dialShapImei = new JSONObject();
            dialShap.put("deviceType", mApp.getCurUser().getFocusWatch().getDeviceType());
            dialShapImei.put("imei", mApp.getCurUser().getFocusWatch().getImei());
            String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(dialShapImei.toString(),
                            mApp.getNetService().getAESKEY(), mApp.getNetService().getAESKEY()),
                    Base64.NO_WRAP);
            dialShap.put("enVal", encryptInfo);
            dialShap.put("sid", mApp.getToken());
            LogUtil.e("getXunDialShop --- getDialShopShowStatus ");
            new XiaoXunCloudInfoClient.Builder().build().onGetXunDialShopVisible(dialShap.toJSONString(), new OnResponCallBack<ResponseBody>() {
                @Override
                public void onCallBack(ResponseBody mResponseBody) {
                    try {
                        String mResponseStr = mResponseBody.string();
                        LogUtil.e("onGetXunDialShopVisible: " + mResponseStr);
                        JSONObject mObject = (JSONObject) JSONValue.parse(mResponseStr);
                        String rc = (String) mObject.get("code");
                        if (Objects.equals(rc, "0000")) {
                            // 存储时间戳
                            mApp.setValue(Const.DIAL_SHOP_SHOW_LONGTIME + mApp.getCurUser().getFocusWatch().getImei(), TimeUtils.getNowString(new SimpleDateFormat("yyyyMMddHHmmssSSS")));

                            String data = mObject.get("data").toString();
                            JSONObject dataObject = (JSONObject) JSONValue.parse(data);
                            int show = (int) dataObject.get("show");
                            int redDotShow = (int) dataObject.get("reddotVer");
                            LogUtil.e("onGetXunDialShopVisible:DIAL_SHOP_SHOW " + (show != 0));
                            LogUtil.e("onGetXunDialShopVisible:DIAL_SHOP_REDDOTVER_SHOW " + (redDotShow != 0));
                            mApp.setValue(Const.DIAL_SHOP_SHOW + mApp.getCurUser().getFocusWatch().getImei(), show != 0);
                            if (redDotShow == 0) {
                                mApp.setValue(Const.DIAL_SHOP_REDDOTVER_SHOW_TAG + mApp.getCurUser().getFocusWatch().getImei(), false);
                            } else if (mApp.getIntValue(Const.DIAL_SHOP_REDDOTVER_SHOW + mApp.getCurUser().getFocusWatch().getImei(), 0) != (redDotShow)) {
                                mApp.setValue(Const.DIAL_SHOP_REDDOTVER_SHOW_TAG + mApp.getCurUser().getFocusWatch().getImei(), true);
                                mApp.setValue(Const.DIAL_SHOP_REDDOTVER_SHOW + mApp.getCurUser().getFocusWatch().getImei(), redDotShow);
                            } else {
                                mApp.setValue(Const.DIAL_SHOP_REDDOTVER_SHOW_TAG + mApp.getCurUser().getFocusWatch().getImei(), false);
                            }
                            isShowDialShop = show != 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void initDialMark() {
        FlutterEngine flutterEngine = new FlutterEngine(context);
        flutterEngine.getNavigationChannel().setInitialRoute("myApp");
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        EventChannel eventChannel = new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "hometest");
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                String imei = "{imei:" + mApp.getCurUser().getFocusWatch().getImei() + "}";
                String deviceType = mApp.getCurUser().getFocusWatch().getDeviceType();
                String enVal = Base64.encodeToString(
                        AESUtil.encryptAESCBC(
                                imei,
                                mApp.getNetService().AES_KEY,
                                mApp.getNetService().AES_KEY
                        ), Base64.NO_WRAP
                );
                String ismanager = (mApp.getCurUser().getEid().equals
                        (mApp.getCurUser().getAdminEidByWatch(mApp.getCurUser().getFocusWatch())) ? "1" : "0");
                Log.d("Android", "mEid is   " + mApp.getCurUser().getEid() +
                        "  mManager is   " + mApp.getCurUser().getAdminEidByWatch(mApp.getCurUser().getFocusWatch()));
                Log.d("ismanager", ismanager);
                String url = XunKidsDomain.getInstance(context).getXunKidsShopDomain() + "/";
                Map<String, String> map = new HashMap<>();
                map.put("deviceType", deviceType);
                map.put("sid", mApp.getToken());
                map.put("enVal", enVal);
                map.put("type", "home");
                map.put("url", url);
                map.put("ismanager", ismanager);
                eventSink = events;
                Log.d("Android", "EventChannel onListen called");
                eventSink.success(map);
            }

            @Override
            public void onCancel(Object arguments) {

            }
        });
        MethodChannel mMethodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "channelShop");
        mMethodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                switch (call.method) {
                    case "passGoodsId":
                        JSONObject passObject = new JSONObject();
                        passObject.put("imei", mApp.getCurUser().getFocusWatch().getImei());
                        passObject.put("goodsId", call.arguments.toString());
                        String enVal = Base64.encodeToString(
                                AESUtil.encryptAESCBC(
                                        passObject.toString(),
                                        mApp.getNetService().AES_KEY,
                                        mApp.getNetService().AES_KEY
                                ), Base64.NO_WRAP
                        );
                        Map returnMap = new HashMap();
                        returnMap.put("enVal", enVal);
                        returnMap.put("sid", mApp.getToken());
                        result.success(returnMap);
                        break;
                    case "maidian":
                        String index = call.arguments.toString().substring(call.arguments.toString().length() - 3);
//                        BaiDuStatCollect.onBaiDuStatHandlerById(context, Integer.parseInt(index));
                        result.success(index);
                        break;
                    case "setNotice":
                        LogUtil.e("call.arguments.toString()   " + call.arguments.toString());
                        MyMsgData queryGroupsMsg = new MyMsgData();
                        queryGroupsMsg.setCallback((reqMsg, respMsg) -> {
                            int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                            int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
                            if (cid == CloudBridgeUtil.CID_MAPSET_RESP) {
                                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                                    ToastUtil.show(getActivity(),
                                            getString(R.string.phone_set_success));
                                    String pushOnoff;
                                    if (mApp.getStringValue(
                                            Const.DIAL_STORE_PUSH_ONOFF + focusWatch.getEid(),
                                            "0"
                                    ).equals("0")) {
                                        pushOnoff = "1";
                                    } else {
                                        pushOnoff = "0";
                                    }
                                    mApp.setValue(
                                            Const.DIAL_STORE_PUSH_ONOFF + focusWatch.getEid(),
                                            pushOnoff
                                    );
                                    LogUtil.e("push on off callback = " + pushOnoff);
                                } else {
                                    ToastUtil.show(getActivity(),
                                            getString(R.string.set_error));
                                }
                            }
                        });
                        JSONObject pl = new JSONObject();
                        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
                        pl.put(CloudBridgeUtil.KEY_DIAL_STORE_PUSH_ONOFF, mApp.getStringValue(
                                Const.DIAL_STORE_PUSH_ONOFF + focusWatch.getEid(),
                                "0"
                        ));
                        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
                        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
                        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
                        queryGroupsMsg.setReqMsg(
                                CloudBridgeUtil.CloudMapSetContent(
                                        CloudBridgeUtil.CID_MAPSET,
                                        sn,
                                        mApp.getToken(),
                                        pl
                                )
                        );
                        if (mApp.getNetService() != null) {
                            mApp.getNetService().sendNetMsg(queryGroupsMsg);
                        }
                        break;
                    case "getNotice":
                        String pushTag = mApp.getStringValue(
                                Const.DIAL_STORE_PUSH_ONOFF + focusWatch.getEid(), "0"
                        );
                        Map pushMap = new HashMap();
                        pushMap.put("flag", pushTag);
                        LogUtil.e("getNotice .pushTag()   " + pushTag);
                        result.success(pushMap);
                        break;
                    case "pay":
//                        LogUtil.e("call.arguments.toString()   " + call.arguments.toString());
//                        Intent it = new Intent(context, MultFunWebViewActivity.class);
//                        it.putExtra("targetUrl", call.arguments.toString());
//                        it.putExtra("channerShop", 1);
//                        it.putExtra(Constants.TYPE_FUN, Constants.FINAL_CHANNEL_SUB_FIND);
//                        it.putExtra("show_title_menu", false);
//                        startActivity(it);
                        break;
                }
            }

        });
        FlutterEngineCache
                .getInstance()
                .put("dialshop", flutterEngine);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    public static void onRefresh() {
//        if (eventSink != null) {
//            LogUtil.e("onRefresh() ");
//            Map map = new HashMap();
//            map.put("refresh", "refresh");
//            eventSink.success(map);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettingReceiver.unregisterReceiver(this.getActivity());
    }

    private void initViews(View view) {
        if (focusWatch.isWatch()) {
            settingManger = getString(R.string.setting_watch_manager);
        } else {
            settingManger = getString(R.string.setting_device_manager);
        }
        mTitleName = view.findViewById(R.id.tv_title);
        mBtnAdd = view.findViewById(R.id.iv_title_back);
        mBtnAdd.setOnClickListener(this);
        mBtnMenu = view.findViewById(R.id.iv_title_menu);
        mBtnMenu.setOnClickListener(this);
        mWatchSelectView = view.findViewById(R.id.ly_title);
        mLyTitleShadow = view.findViewById(R.id.ly_title_shadow);
        mListBtn = view.findViewById(R.id.title_spinner);
        loadingdlg = new LoadingDialog(context, R.style.Theme_DataSheet, this);

        jgridview1 = view.findViewById(R.id.jgridview1);
        jgridview2 = view.findViewById(R.id.jgridview2);
        jgridview3 = view.findViewById(R.id.jgridview3);
    }

    private void updateViewShow() {
        mBtnMenu.setVisibility(View.VISIBLE);
        mBtnMenu.setBackgroundResource(R.drawable.menu_add_selector);
        mBtnAdd.setVisibility(View.GONE);
        mBtnAdd.setBackgroundResource(R.drawable.menu_add_selector);
        mTitleName.setText(mHostActivity.getString(R.string.setting));
        mLyTitleShadow.setVisibility(View.GONE);
        if (mApp.getCurUser().getWatchList().size() > 1) {
            mListBtn.setVisibility(View.GONE);
            mWatchSelectView.setOnClickListener(null);
            mLyTitleShadow.setOnClickListener(null);
        } else {
            mListBtn.setVisibility(View.GONE);
            mWatchSelectView.setOnClickListener(null);
            mLyTitleShadow.setOnClickListener(null);
        }
        loadingdlg.hideReloadView();
        if ((focusWatch.isDevice709_H01() && !focusWatch.isDevice607()) || focusWatch.isDevice707_H01()) {
            getDialShopShowStatus();
        }

        if (isShowDialShop) {
            initDialMark();
        }

        settingList1.clear();
        settingList1.add(new SettingBean(getString(R.string.setting_babyinfo), R.drawable.icon_me));
        settingList1.add(new SettingBean(getString(R.string.two_dimension_code), R.drawable.icon_qr_code));

        settingList1.add(new SettingBean(getString(R.string.power_saving_setting), R.drawable.icon_power, false));
        settingList1.add(new SettingBean(settingManger, R.drawable.icon_watch));
        settingList1.add(new SettingBean(getString(R.string.setting_home), R.drawable.icon_home));
        settingList1.add(new SettingBean(getString(R.string.security_zone), R.drawable.icon_safe));
        if (focusWatch != null && !focusWatch.isDevice102() && !focusWatch.isDevice206_A02() && !focusWatch.isDevice203_A03()) {//通话记录
            settingList1.add(new SettingBean(getString(R.string.setting_calllog), R.drawable.icon_record));
        }

        settingList3.clear();
        settingList3.add(new SettingBean(getString(R.string.setting_check_update), R.drawable.icon_updata, haveUpdate(getActivity())));
//        if(!focusWatch.isDevice707_A05()) {
//            settingList3.add(new SettingBean(getString(R.string.setting_faq), R.drawable.icon_help));
//        }
        //售后维修入口 --- 海外版入口隐藏
//        String repair_onoff = mApp.getStringValue(Const.SHARE_PREF_FIELD_REPAIR_ONOFF, "0");
//        if(controlRepairOnOff(repair_onoff)){
//            settingList3.add(new SettingBean(getString(R.string.setting_watch_repair),R.drawable.icon_repair));
//        }

//        settingList3.add(new SettingBean(getString(R.string.health_monitor_monitor_name), R.drawable.icon_health));
//
//        settingList3.add(new SettingBean(getString(R.string.health_report_title), R.drawable.icon_health_record));
//
//        settingList3.add(new SettingBean(getString(R.string.sport_plan), R.drawable.icon_sport_plan));


        settingList3.add(new SettingBean(getString(R.string.setting_watch_about), R.drawable.icon_about));

        settingList2.clear();
        //还未开放
        //settingList2.add(new SettingBean(getString(R.string.app_store),R.drawable.icon_store));
        if (focusWatch != null && (focusWatch.isDevice705() || focusWatch.isDevice708_A06() || focusWatch.isDevice709_A03()
                || focusWatch.isDevice708_A07() || focusWatch.isDevice709_A05() || focusWatch.isDevice607())) {
            settingList2.add(new SettingBean(getString(R.string.app_manager), R.drawable.icon_manage));
        }

        //运动计步
        if (focusWatch.isDevice706_A02() || focusWatch.isDevice900_A03() || focusWatch.isDevice206_A02() || focusWatch.isDevice707() || focusWatch.isDevice709()
                || focusWatch.isDevice707_H01() || (focusWatch.isDevice709_H01() && !focusWatch.isDevice607()) || focusWatch.isDevice708() || focusWatch.isDevice709_A03() || focusWatch.isDevice708_A06() || focusWatch.isDevice709_A05() || focusWatch.isDevice708_A07())
            settingList2.add(new SettingBean(getString(R.string.health_steps), R.drawable.icon_exercise));

        if (focusWatch.isDevice707_H01() || (focusWatch.isDevice709_H01() && !focusWatch.isDevice607())) {
            settingList2.add(new SettingBean(getString(R.string.sport_record_title), R.drawable.icon_sport_record));
        }
        //运动周报
        if (focusWatch != null && focusWatch.isDevice607()) {
            settingList2.add(new SettingBean(getString(R.string.sport_weekly_report), R.drawable.icon_sport_weekly));
        }
        //相册表盘
        if (false && focusWatch != null && focusWatch.isDevice705()) {
            settingList2.add(new SettingBean(getString(R.string.dial_bg_txt), R.drawable.icon_photo));
        }
        if (isShowDialShop) {
            settingList1.add(new SettingBean(getString(R.string.xiaoxun_dial_market), R.drawable.icon_setting_channel_shop, isShowDialShopRedDot));
        }
        settingList1.add(new SettingBean(getString(R.string.setting_watch_sleep), R.drawable.icon_watch_sleep));
//        if (focusWatch.isDevice707_H01() || focusWatch.isDevice709_H01()) {
//            settingList1.add(new SettingBean(getString(R.string.super_power_saving), R.drawable.icon_super_power_saving));
//        }
        //小爱百度同学  海外版本隐藏
//        if (focusWatch != null && focusWatch.isDevice501() || focusWatch.isDevice502() || focusWatch.isDevice730() || focusWatch.isDevice705()
//                || focusWatch.isDevice703() ||focusWatch.isDevice306() ||focusWatch.isDevice307()) {
//            isXiaoaiReddot = RedDotUtils.getInstance(mApp).isReddotShow(focusWatch.getEid(), Const.SHARE_PREF_FUNCTION_XIAOAI);
//            settingList2.add(new SettingBean(getString(R.string.function_control_aivoice), R.drawable.icon_xiaoai, isXiaoaiReddot));
//        } else if (focusWatch != null && focusWatch.isDevice710() || (focusWatch.isDevice701() && mApp.isControledByVersion(focusWatch, false, "T41"))) {
//            settingList2.add(new SettingBean(getString(R.string.function_control_aivoice_710),R.drawable.icon_ai));
//        }

        //识单词
        if (focusWatch != null && focusWatch.isDevice705()) {
            settingList2.add(new SettingBean(getString(R.string.function_control_eng), R.drawable.icon_learn));
        }
        if ((focusWatch != null && focusWatch.isDevice708_A06()) || focusWatch.isDevice708_A07() || focusWatch.isDevice707_H01() || focusWatch.isDevice709_H01() ) {
            settingList2.add(new SettingBean(getString(R.string.schedule_card_title), R.drawable.schedule_card));
        }

    }

    private boolean controlRepairOnOff(String isStatus) {
        boolean isOpenView = false;
        if (Params.PACKAGE_NAME_XUN.equals(BuildConfig.APPLICATION_ID)) {
            isOpenView = isStatus.equals("1");
        } else {
            isOpenView = false;
        }
        return isOpenView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_menu:
            case R.id.iv_title_back:
                //添加新手表
                if (mApp.getCurUser().getWatchList() != null && mApp.getCurUser().getWatchList().size() >= Const.DEVICE_MAX_LIMIT) {
                    ToastUtil.showMyToast(context, getString(R.string.max_watch_num_prompt_msg), Toast.LENGTH_SHORT);
                } else {
                    startActivity(new Intent(context, BindNewActivity.class));
//                    Intent intent= new Intent(getActivity(), MipcaActivityCapture.class);
//                    intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "bind");
//                    startActivity(intent);
                }
                break;

            case R.id.ly_title:
            case R.id.ly_title_shadow:
                mWatchSelectView.setClickable(false);
                mMenuWindow = new WatchListPopUpWindow(getActivity(), null);
                mMenuWindow.showAtLocation(getActivity().findViewById(android.R.id.content).getRootView(), Gravity.TOP, 0, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWatchSelectView.setClickable(true);
                    }
                }, 500);
                break;

            case R.id.layout_add_friend:
                startActivity(new Intent(context, AddNewFriendActivity.class));
                break;
        }
    }

    @Override
    public void confirmClick() {

    }

    public class SettingReceiver extends BroadcastReceiver {

        // 注册监听
        public void registerReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_PROCESSED_NOTIFY_OK);
            filter.addAction(CloudBridgeUtil.ACTION_SIM_OP_NOTICE);
            filter.addAction(Const.ACTION_STORY_VISIBLE_CHANGE);
            filter.addAction(Const.ACTION_CHANGE_WATCH);
            filter.addAction(Const.ACTION_DOWNLOAD_HEADIMG_OK);
            filter.addAction(Const.ACTION_RECEIVE_GET_DEVICE_INFO);
            filter.addAction(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE);
            filter.addAction(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW);
            filter.addAction(Const.ACTION_BIND_RESULT_END);
            context.registerReceiver(this, filter);
        }

        // 关闭监听
        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        // 监听广播
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(CloudBridgeUtil.ACTION_SIM_OP_NOTICE)) {

            } else if (intent.getAction().equals(Const.ACTION_CHANGE_WATCH) || intent.getAction().equals(Const.ACTION_BIND_RESULT_END)
                    || intent.getAction().equals(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW)) {
                focusWatch = mApp.getCurUser().getFocusWatch();
                mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
                updateViewShow();
                if (jgridview1 != null) {
                    jgridview1.refresh();
                }
                if (jgridview2 != null) {
                    jgridview2.refresh();
                }
                if (jgridview3 != null) {
                    jgridview3.refresh();
                }
                mHostActivity.updateSettingRedPoint();
            } else if (intent.getAction().equals(Const.ACTION_DOWNLOAD_HEADIMG_OK)) {
                //ImageUtil.setMaskImage(mImageHead, R.drawable.head_0, mApp.getHeadDrawableByFile(getResources(), focusWatch.getHeadPath(), focusWatch.getEid(), R.drawable.small_default_head));
            } else if (intent.getAction().equals(Const.ACTION_RECEIVE_GET_DEVICE_INFO) || intent.getAction().equals(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE)) {
                mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
            }
        }
    }

    private boolean haveUpdate(Activity activity) {
        if (mApp == null) {
            mApp = (ImibabyApp) activity.getApplication();
        }
        if (mApp != null && mApp.showUpdateResult(activity, true, false, false) > 0) {
            return true;
        }
        return mApp != null && mApp.showWatchUpdateResult(activity, false, false) > 0;
    }

    public boolean IsShowAllRedPoint(Activity activity) {
        boolean flag = false;
        try {
            //boolean flag1 = !alipayIsShow(activity);
            boolean flag4 = haveUpdate(activity);
            //boolean flag5 = haveMapUpdate(activity);
            boolean flag6 = RedDotUtils.getInstance(mApp).isReddotShow(focusWatch.getEid(), Const.SHARE_PREF_FUNCTION_XIAOAI);
            flag = flag4 || flag6;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int sn = CloudBridgeUtil.getCloudMsgSN(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPGET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (reqMsg == null) {
                        break;
                    }
                    if (maggetPl.containsKey(CloudBridgeUtil.KEY_NAME_PAY_PWD)) {
                        String pay_pwd = (String) maggetPl.get(CloudBridgeUtil.KEY_NAME_PAY_PWD);
                        if (!TextUtils.isEmpty(pay_pwd)) {
                            mApp.setValue(focusWatch.getEid() + CloudBridgeUtil.SHARE_PAY_PWD, pay_pwd);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private BaseAdapter baseAdapter1 = new BaseAdapter() {

        @Override
        public int getCount() {
            return settingList1.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup view) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_setting_item, view, false);
            CustomSettingNewView user_info = convertView.findViewById(R.id.layout_user_settinginfo);
            SettingBean bean = settingList1.get(position);
            String name = bean.getName();
            user_info.setTitle(name);
            user_info.setIvIcon(bean.getPicture());
            user_info.setRedPointVisible(bean.isShowRed());

            if (name.equals(getString(R.string.setting_babyinfo))) {
                //用户信息
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, DeviceDetailActivity.class));
                    }
                });
            } else if (name.equals(getString(R.string.two_dimension_code))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentQr = new Intent(context, DeviceQrActivity.class);
                        intentQr.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
                        startActivity(intentQr);
                    }
                });
            } else if (name.equals(getString(R.string.power_saving_setting))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, PowersaveSettingActivity.class));
                    }
                });
            } else if (name.equals(settingManger)) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WatchManagerActivity.class);
                        intent.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
                        startActivity(intent);
                        //startActivityForResult(intent,201);//requesCode
                    }
                });
            } else if (name.equals(getString(R.string.xiaoxun_dial_market))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.e("FlutterEngineCache  " + FlutterEngineCache
                                .getInstance()
                                .get("dialshop"));
                        if (FlutterEngineCache
                                .getInstance()
                                .get("dialshop") == null) {
                            initDialMark();
                        }
                        startActivity(
                                FlutterActivity
                                        .withCachedEngine("dialshop")
                                        .build(context));
                        if (isShowDialShopRedDot) {
                            isShowDialShopRedDot = false;
                            mApp.setValue(Const.DIAL_SHOP_REDDOTVER_SHOW_TAG + mApp.getCurUser().getFocusWatch().getImei(), isShowDialShopRedDot);
                            settingList1.remove(position);
                            settingList1.add(new SettingBean(getString(R.string.xiaoxun_dial_market), R.drawable.icon_setting_channel_shop, isShowDialShopRedDot));
                            jgridview1.refresh();
                        }
                    }
                });
            } else if (name.equals(getString(R.string.setting_watch_sleep))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (focusWatch == null)
                            return;
                        final Intent intent = new Intent(context, SleepTimeActivity.class);
                        startActivity(intent);
                    }
                });
            } else if (name.equals(getString(R.string.setting_home))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentFamily = new Intent(context, FamilyMemberActivity.class);
                        intentFamily.putExtra(Const.KEY_FAMILY_ID, focusWatch.getFamilyId());
                        startActivity(intentFamily);
                    }
                });
            } else if (name.equals(getString(R.string.security_zone))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mApp.getCurUser().getFocusWatch().isDevice707_H01() || mApp.getCurUser().getFocusWatch().isDevice707_H01()) {
                            boolean isFirstTime = mApp.getBoolValue(focusWatch.getEid() + Constants.SHARE_PREF_IS_FIRST_TIME_TO_SECURITY, true);
                            if (!mApp.isMeAdmin(mApp.getCurUser().getFocusWatch())) {
                                startActivity(new Intent(context, SecurityMapMainActivity.class));
                            } else {
                                LogUtil.e("SecurityWelcomeActivity isFirstTime  = " + isFirstTime);
                                if (isFirstTime) {
                                    startActivity(new Intent(context, SecurityWelcomeActivity.class));
                                } else {
                                    startActivity(new Intent(context, SecurityMapMainActivity.class));
                                }
                            }
                        } else {
                            startActivity(new Intent(context, SecurityZoneActivity.class));
                        }
                    }
                });
            } else if (name.equals(getString(R.string.setting_calllog))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callLogIntent = new Intent(context, CallLogActivity.class);
                        callLogIntent.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
                        startActivity(callLogIntent);
                    }
                });
            } else if (name.equals(getString(R.string.super_power_saving))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, SuperPowerSavingActivity.class));
                    }
                });
            }
            return convertView;
        }
    };

    private BaseAdapter baseAdapter2 = new BaseAdapter() {

        @Override
        public int getCount() {
            return settingList2.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup view) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_setting_item, view, false);
            CustomSettingNewView user_info = convertView.findViewById(R.id.layout_user_settinginfo);
            SettingBean bean = settingList2.get(position);
            String name = bean.getName();
            if (name.equals(getString(R.string.app_store))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent storeIntent = new Intent(context, AppStoreActivity.class);
                        startActivity(storeIntent);
                    }
                });
            } else if (name.equals(getString(R.string.app_manager))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent storeIntent = new Intent(context, AppManagerActivity.class);
                        startActivity(storeIntent);
                    }
                });
            } else if (name.equals(getString(R.string.health_steps)) || name.equals(getString(R.string.new_sport_steps))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (focusWatch.isDevice102() && !mApp.isControledByVersion(focusWatch, false, "T24")) {
                            ToastUtil.show(mApp, getString(R.string.not_support_steps));
                        } else {
                            if (focusWatch.isDevice706_A02() || focusWatch.isDevice900_A03() || focusWatch.isDevice709()
                                    || focusWatch.isDevice708() || focusWatch.isDevice708_A06()
                                    || focusWatch.isDevice709_A03() || focusWatch.isDevice709_A05() || focusWatch.isDevice708_A07() || focusWatch.isDevice709_H01() || focusWatch.isDevice707_H01()) {
                                Intent _intent = new Intent(context, SportActivity.class);
                                _intent.putExtra(Constants.WATCH_EID_DATA, focusWatch.getEid());
                                startActivity(_intent);
                            } else {
                                startActivity(new Intent(context, StepsActivity.class));
                            }
                        }

                    }
                });
            } else if (name.equals(getString(R.string.sport_weekly_report))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int mQrindex = focusWatch.getQrStr().indexOf("sn=");
                        String mMotionWeekly = XunKidsDomain.getInstance(context).getXunKidsDcenterDomain() + "/sportsWeekly/index.html?"
                                + focusWatch.getQrStr().substring(mQrindex);
                        WebViewUtil.onNextPage(getActivity(), mMotionWeekly, false);
                    }
                });
            } else if (name.equals(getString(R.string.sport_record_title))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), MotionSportRecordActivity.class));
                    }
                });
            } else if (name.equals(getString(R.string.dial_bg_txt))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent dbIntent = new Intent(context, DialBgActivity.class);
                        startActivity(dbIntent);
                    }
                });
            } else if (name.equals(getString(R.string.function_control_aivoice)) || name.equals(getString(R.string.function_control_aivoice_710))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent aiIntent = new Intent(context, HelpWebActivity.class);
                        aiIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_AIVOICE);
                        aiIntent.putExtra(Const.KEY_HELP_URL, FunctionUrl.APP_AI_VOICE_DISPLAY_URL);
                        aiIntent.putExtra(Const.KEY_PARAMS, mApp.getAiVoiceParams());
                        if (focusWatch.isDevice710() && !focusWatch.isDevice730() || focusWatch.isDevice701()) {
                            aiIntent.putExtra(Const.KEY_AITYPE, 2);
                        } else {
                            aiIntent.putExtra(Const.KEY_AITYPE, 1);
                            mApp.setValue(Const.SHARE_PREF_FUNCTION_REDDOT + focusWatch.getEid() + Const.SHARE_PREF_FUNCTION_XIAOAI, TimeUtil.getTimeStampLocal());
                            if (isXiaoaiReddot) {
                                isXiaoaiReddot = false;
                                for (int i = 0; i < settingList2.size(); i++) {
                                    if (settingList2.get(i).getName().equals(getString(R.string.function_control_aivoice))) {
                                        settingList2.remove(i);
                                        settingList2.add(i, new SettingBean(getString(R.string.function_control_aivoice), R.drawable.icon_xiaoai, false));
                                    }
                                }
                                jgridview2.refresh();
                            }
                        }
                        startActivity(aiIntent);
                    }
                });
            } else if (name.equals(getString(R.string.function_control_eng))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (focusWatch.isDevice705() || focusWatch.isDevice730() && mApp.isControledByVersion(focusWatch, false, "T28")) {
                            Intent engIntent = new Intent(mHostActivity, HelpWebActivity.class);
                            engIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_ENGSTATISTICS);
                            engIntent.putExtra(Const.KEY_HELP_URL, mApp.getEngStatisticsUrl(focusWatch.getEid()));
                            startActivity(engIntent);
                        } else {
                            ToastUtil.showNewVerToast(context, focusWatch, "T28");
                        }
                    }
                });
            } else if (name.equals(getString(R.string.schedule_card_title))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _intent = new Intent(context, ScheduleCardActivity.class);
                        _intent.putExtra(Constants.SCHEDULE_SETTING_FIRST, false);
                        if (focusWatch != null)
                            _intent.putExtra(Constants.WATCH_EID_DATA, focusWatch.getEid());
                        startActivity(_intent);
                    }
                });
            }

            user_info.setTitle("" + bean.getName());
            user_info.setIvIcon(bean.getPicture());
            user_info.setRedPointVisible(bean.isShowRed());
            return convertView;
        }
    };

    private BaseAdapter baseAdapter3 = new BaseAdapter() {

        @Override
        public int getCount() {
            return settingList3.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup view) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_setting_item, view, false);
            CustomSettingNewView user_info = convertView.findViewById(R.id.layout_user_settinginfo);
            SettingBean bean = settingList3.get(position);
            String name = bean.getName();
            user_info.setTitle(name);
            user_info.setIvIcon(bean.getPicture());
            user_info.setRedPointVisible(bean.isShowRed());
            if (name.equals(getString(R.string.setting_check_update))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentUpdate = new Intent(getActivity(), SystemUpdateActivity.class);
                        intentUpdate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentUpdate.putExtra("SystemUpdateType", -1);
                        intentUpdate.putExtra("CheckUpdate", 1);
                        startActivity(intentUpdate);
                    }
                });
            } else if (name.equals(getString(R.string.setting_faq))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(mApp.getHelpCenterIntent(mHostActivity, "main"));
                    }
                });
            } else if (name.equals(getString(R.string.setting_watch_repair))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentRepair = new Intent(getActivity(), DeviceRepairActivity.class);
                        startActivity(intentRepair);
                    }
                });

            } else if (name.equals(getString(R.string.setting_watch_about))) {
                user_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AppAboutActivity.class));
                    }
                });
            }
            return convertView;
        }
    };
}

