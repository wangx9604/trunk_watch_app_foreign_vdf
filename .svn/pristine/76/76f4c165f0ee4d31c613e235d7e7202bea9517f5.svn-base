package com.xiaoxun.xun.beans;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AppStoreUtils;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;

public class WatchAppBean {

    /**
     * {
     * "icon": "https:\/\/webfile.cdn.bcebos.com\/appstore\/com.alipay\/20181218153019\/icon\/zhifu.png",
     * "version_code": 8,
     * "description": "支付宝 知托付",
     * "type": 1,
     * "version": "1.1.1.1",
     * "size": 2573070,
     * "name": "支付宝",
     * "download_url": "https:\/\/webfile.cdn.bcebos.com\/appstore\/com.alipay\/20181218153019\/apk\/Alipay.apk",
     * "app_id": "com.alipay",
     * "md5": "db9d75fcc51ff53a299dc428d534967d"
     * }
     */
    public String name;
    public String app_id;
    public String icon;
    public String description;
    public String version;
    public int version_code;
    public String download_url;
    public int size;
    public String md5;
    public int type = 1;
    public String dev_version;

    /**
     * {
     * "name":"支付宝",
     * "type":1,  //应用类型，1可卸载，2不可卸载
     * "app_id":"com.xiaoxun.xun",  //应用包名
     * "EID":"BAE73BE9E6B4BEF605CE787B8ACCD6B4",  //设备ID
     * "GID":"F414F6631EB2F64D81B7916210C78660",  //设备所在家庭组ID
     * "optype":0,  //操作类型，0新增1修改2删除
     * "icon":"url//icon",
     * "status":0,  //应用状态，0已安装，1待安装，2待更新，3待卸载
     * "hidden":0,  //是否隐藏，0不隐藏，1隐藏
     * "version":"1.2.8888",
     * "download_url":"xxxx.xunkeys.com/uuu",  //下载链接
     * "wifi": 1,
     * "size":1000,
     * "md5":"45464454646464",
     * "updateTS":"20181220164734110"		//更新时间戳，服务端会自行插入修改
     * }
     */
    //public int optype;
    //public String updateTS;
    public int status = -1;  //应用状态，0已安装，1待安装，2待更新，3待卸载      4有更新
    public int hidden;  //是否隐藏，0不隐藏，1隐藏
    public int wifi;  //0数据流量，1 wifi

    public AppDetail app_deatil;

    public static class AppDetail {
        /**
         * {
         * “name”:“支付宝”,    //显示给用户看的名称
         * “page”:[“xxxxxx”,”xxxxxxx”,…..],    //应用展示的界面图片url，最多5张
         * “description”: “安全好用的支付方式”,  //应用详细介绍
         * “function”: “1、xxxxx;2、xxxxx”   //应用的功能特征
         * }
         */
        public String name;
        public String[] page;
        public String function;
    }

    public static AppDetail toAppDeatilBean(JSONObject appDetailJson) {
        AppDetail appDetail = new AppDetail();
        appDetail.name = (String) appDetailJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_NAME);
        JSONArray pageArray = (JSONArray) appDetailJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_PAGE);
        appDetail.page = new String[pageArray.size()];
        for (int i = 0; i < pageArray.size(); i++) {
            appDetail.page[i] = (String) pageArray.get(i);
        }
        appDetail.function = (String) appDetailJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_FUNCTION);
        return appDetail;
    }

    private static WatchAppBean toWatchAppBean(JSONObject watchAppJson) {
        WatchAppBean watchApp = new WatchAppBean();
//        watchApp.name = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_NAME);
        watchApp.app_id = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_APPID);
        watchApp.name = getAppNameByPackage(watchApp.app_id);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_TYPE))
            watchApp.type = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_TYPE);
        watchApp.icon = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_ICON);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_DESCRIPTION))
            watchApp.description = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_DESCRIPTION);
        watchApp.version = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_VERSION);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_VERSIONCODE))
            watchApp.version_code = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_VERSIONCODE);
        watchApp.download_url = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_DOWNLOADURL);
        watchApp.md5 = (String) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_MD5);
        watchApp.size = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_SIZE);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_STATUS))
            watchApp.status = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_STATUS);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_HIDDEN))
            watchApp.hidden = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_HIDDEN);
        if (watchAppJson.containsKey(CloudBridgeUtil.KEY_APPSTORE_APP_WIFI))
            watchApp.wifi = (int) watchAppJson.get(CloudBridgeUtil.KEY_APPSTORE_APP_WIFI);
        return watchApp;
    }

    public static ArrayList<WatchAppBean> toWatchAppList(JSONArray watchAppArray) {
        ArrayList<WatchAppBean> appList = new ArrayList<>();
        for (int i = 0; i < watchAppArray.size(); i++) {
            appList.add(toWatchAppBean((JSONObject) watchAppArray.get(i)));
        }
        return appList;
    }

    public static void updateWatchAppList(ArrayList<WatchAppBean> appStoreList, ArrayList<WatchAppBean> installedAppList) {

        for (WatchAppBean appStoreBean : appStoreList) {
            for (WatchAppBean installedBean : installedAppList) {
                if (appStoreBean.app_id.equals(installedBean.app_id)) {
                    appStoreBean.status = installedBean.status;
                    appStoreBean.wifi = installedBean.wifi;
                    // 判断已安装的应用有没有更新
                    if (appStoreBean.status == 0) {
                        if (appStoreBean.version_code > installedBean.version_code)
                            appStoreBean.status = 4;
                    }
                }
            }
        }
    }

    public static int[] app_icons = {
            R.drawable.watch_app_baidu, R.drawable.watch_app_botany, R.drawable.watch_app_brightness,
            R.drawable.watch_app_call, R.drawable.watch_app_friends, R.drawable.watch_app_image,
            R.drawable.watch_app_message, R.drawable.watch_app_pet, R.drawable.watch_app_voice,
            R.drawable.watch_app_photo, R.drawable.watch_app_qq, R.drawable.watch_app_set,
            R.drawable.watch_app_step, R.drawable.watch_app_story, R.drawable.watch_app_video,
            R.drawable.watch_app_xiaoai, R.drawable.watch_app_alipay, R.drawable.watch_app_other,
            R.drawable.watch_app_vedio, R.drawable.watch_app_english, R.drawable.watch_app_xunbaidulbs
    };

    public static int getResIdByPackage(String packageName) {
        int appResId;
        switch (packageName) {
            case "com.android.dialer":
                appResId = 3;
                break;
            case "com.xxun.watch.xunchatroom":
                appResId = 6;
                break;
            case "com.xxun.watch.xunpet":
                appResId = 7;
                break;
            case "com.xxun.watch.stepstart":
                appResId = 12;
                break;
            case "com.xxun.watch.storytall":
                appResId = 13;
                break;
            case "com.tencent.qqlite":
                appResId = 10;
                break;
            case "com.xxun.xungallery":
                appResId = 5;
                break;
            case "com.xxun.xuncamera":
            case "com.xxun.camera":
                appResId = 9;
                break;
            case "com.xxun.duer.dcs":
                appResId = 0;
                break;
            case "com.xxun.watch.xunbrain.x2":
            case "com.xxun.watch.xunbrain.y1":
            case "com.xxun.watch.xunbrain":
                appResId = 15;
                break;
            case "com.xxun.watch.xunsettings":
                appResId = 11;
                break;
            case "com.xxun.screenon":
                appResId = 2;
                break;
            case "com.xxun.xunimgrec":
                appResId = 1;
                break;
            case "com.eg.android.AlipayGphone":
                appResId = 16;
                break;
            case "com.xxun.videocall":
                appResId = 18;
                break;
            case "com.xiaoxun.englishdailystudy":
                appResId = 19;
                break;
            case "com.xxun.xunbaidulbs":
                appResId = 20;
                break;
            default:
                appResId = 17;
                break;
        }
        return appResId;
    }

    private static String getAppNameByPackage(String packageName) {
        Context context = ActivityUtils.getTopActivity();
        LogUtil.e("getAppNameByPackage packageName = " + packageName);
        String appName = "";
        switch (packageName) {
            case "com.android.dialer":
            case "com.xiaoxun.dialer":
                appName = context.getString(R.string.app_name_dialer);
                break;
            case "com.xxun.watch.xunbrain.c3":
                appName = context.getString(R.string.app_name_xiaoai);
                break;
            case "com.xxun.watch.xunchatroom":
                appName = context.getString(R.string.app_name_chatroom);
                break;
            case "com.xxun.watch.xunpet":
                appName = context.getString(R.string.app_name_pet);
                break;
            case "com.xxun.watch.stepstart":
                appName = context.getString(R.string.app_name_stepstart);
                break;
            case "com.xxun.watch.storytall":
                appName = context.getString(R.string.app_name_storytall);
                break;
            case "com.tencent.qqlite":
                appName = context.getString(R.string.app_name_qqlite);
                break;
            case "com.xxun.xungallery":
                appName = context.getString(R.string.app_name_xungallery);
                break;
            case "com.xxun.xuncamera":
                appName = context.getString(R.string.app_name_xuncamera);
                break;
            case "com.xxun.camera":
                appName = context.getString(R.string.app_name_camera);
                break;
            case "com.xxun.duer.dcs":
                appName = context.getString(R.string.app_name_dcs);
                break;
            case "com.xxun.watch.xunbrain.x2":
                appName = context.getString(R.string.app_name_xiaoai);
                break;
            case "com.xxun.watch.xunbrain.y1":
                appName = context.getString(R.string.app_name_xiaoai);
                break;
            case "com.xxun.watch.xunbrain":
                appName = context.getString(R.string.app_name_xiaoai);
                break;
            case "com.xxun.watch.xunsettings":
                appName = context.getString(R.string.app_name_xunsettings);
                break;
            case "com.xxun.screenon":
                appName = context.getString(R.string.app_name_screenon);
                break;
            case "com.xxun.xunimgrec":
                appName = context.getString(R.string.app_name_xunimgrec);
                break;
            case "com.eg.android.AlipayGphone":
                appName = context.getString(R.string.app_name_alipaygphone);
                break;
            case "com.xxun.videocall":
                appName = context.getString(R.string.app_name_videocall);
                break;
            case "com.xiaoxun.englishdailystudy":
                appName = context.getString(R.string.app_name_englishdailystudy);
                break;
            case "com.xxun.xunbaidulbs":
                appName = context.getString(R.string.app_name_xunbaidulbs);
                break;
            case "com.xxun.pointsystem":
                appName = context.getString(R.string.app_name_pointsystem);
                break;
            case "ado.install.xiaoxun.com.xiaoxuninstallapk":
                appName = context.getString(R.string.app_name_xiaoxuninstallapk);
                break;
            case "com.xxun.watch.xunfriends":
                appName = context.getString(R.string.app_name_xunfriends);
                break;
            case "com.xxun.watch.xunstopwatch":
                appName = context.getString(R.string.app_name_xunstopwatch);
                break;
            case "com.xxun.system":
                appName = context.getString(R.string.app_name_system);
                break;
            case "com.xxun.watch.xunsports":
                appName = context.getString(R.string.app_name_xunsports);
                break;
            case "com.xxun.watch.xunpalace":
                appName = context.getString(R.string.app_name_xunpalace);
                break;
            case "com.xiaoxun.appstorelocal":
                appName = context.getString(R.string.app_name_appstorelocal);
                break;
            case "com.xxun.schedulecard":
                appName = context.getString(R.string.app_name_schedulecard);
                break;
            case "com.xxun.xunintegral":
                appName = context.getString(R.string.app_name_xunintegral);
                break;
            case "com.xiaomi.children.xiaoai":
                appName = context.getString(R.string.app_name_xiaoai);
                break;
            case "com.xxun.watch.oxygen":
                appName = context.getString(R.string.app_name_oxygen);
                break;
            case "com.xxun.watch.sportslog":
                appName = context.getString(R.string.app_name_sportslog);
                break;
            case "com.xxun.watch.sportsreport":
                appName = context.getString(R.string.app_name_sportsreport);
                break;
            case "com.xxun.watch.heartrate":
                appName = context.getString(R.string.app_name_heartrate);
                break;
            case "com.xxun.watch.sportsplan":
                appName = context.getString(R.string.app_name_sportsplan);
                break;
            case "com.xxun.watch.healthylog":
                appName = context.getString(R.string.app_name_healthylog);
                break;
            case "com.xxun.xundewater":
                appName = context.getString(R.string.app_name_xundewater);
                break;
            case "com.xxun.xuntemperature":
                appName = context.getString(R.string.app_name_xuntemperature);
                break;
            case "com.xxun.xunflashlight":
                appName = context.getString(R.string.app_name_xunflashlight);
                break;
            case "com.xxun.watch.xunheartrate":
                appName = context.getString(R.string.app_name_xunheartrate);
                break;
            case "com.xxun.watch.migration":
                appName = context.getString(R.string.app_name_migration);
                break;
            case "com.miui.tsm.simulator":
                appName = context.getString(R.string.app_name_miui_tsm_simulator);
                break;
            case "com.xxun.watch.xunqrcode":
                appName = context.getString(R.string.app_name_xunqrcode);
                break;
            case "com.xiaoxun.xundial":
                appName = context.getString(R.string.app_name_xundial);
                break;
            case "com.xxun.watch.xunweather":
                appName = context.getString(R.string.app_name_xunweather);
                break;
            case "com.xxun.watch.xunalipay":
                appName = context.getString(R.string.app_name_xunalipay);
                break;
            case "com.android.camera2":
                appName = context.getString(R.string.app_name_camera2);
                break;
            case "com.xxun.xunwordsrec":
                appName = context.getString(R.string.app_name_xunwordsrec);
                break;
            case "com.xxun.watch.xunsunshine":
                appName = context.getString(R.string.app_name_xunsunshine);
                break;
            default:
                appName = context.getString(R.string.app_manager);
                break;
        }
        LogUtil.e("getAppNameByPackage appName = " + appName);
        return appName;
    }
}
