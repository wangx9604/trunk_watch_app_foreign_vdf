package com.xiaoxun.xun.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.blankj.utilcode.util.ActivityUtils;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchAppBean;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppStoreUtils {

    private static final String TAG = "AppStoreUtils";

    OperationCallback callback;

    public interface OperationCallback {
        void onSuccess(String result);

        void onFail(String error);
    }

    Context context;
    private static AppStoreUtils instance;

    private AppStoreUtils(Context context) {
        this.context = context;
    }

    public static synchronized AppStoreUtils getInstance(Context context) {
        if (instance == null)
            instance = new AppStoreUtils(context);
        return instance;
    }

    //计算出APP端的token,育儿平台使用  zj by:2019.04.17
    public static String getContentToken(ImibabyApp myApp) {
        JSONObject jsonObject = new JSONObject();
        String encryptData = "";
        if (myApp.getCurUser() != null && myApp.isAutoLogin()
                && myApp.getNetService() != null && myApp.getNetService().getAESKEY() != null) {
            String useEid = myApp.getCurUser().getEid();
            if (myApp.getCurUser().getEid() == null || myApp.getCurUser().getEid().length() == 0) {
                String eid = myApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                if (eid != null) {
                    useEid = eid;
                }
            }

            jsonObject.put("appeid", useEid);
            jsonObject.put("timestamp", TimeUtil.getTimeStampLocal());
            LogUtil.e("adwebview json:" + jsonObject.toJSONString());
            encryptData = BASE64Encoder.encode(
                    AESUtil.encryptAESCBC(
                            jsonObject.toJSONString(), myApp.getNetService().getAESKEY(),
                            myApp.getNetService().getAESKEY())
            ) + myApp.getToken();
        }

        return encryptData;
    }

    public void getAppStoreList(String deviceType, String AES_KEY, String sid, final OperationCallback callback) {

        OkHttpClient client = new OkHttpClient();

        String url = FunctionUrl.APP_STORE_LIST_URL;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject reqJson = new JSONObject();
        reqJson.put("device", deviceType);
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(reqJson.toJSONString(), AES_KEY, AES_KEY), Base64.NO_WRAP) + sid;
        RequestBody body = RequestBody.create(JSON, reqBody);

        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG + " IOException = " + e.toString());
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i(TAG + "results = " + results);
                JSONObject resultJson = (JSONObject) JSONValue.parse(results);
                int rc = (int) resultJson.get(CloudBridgeUtil.KEY_NAME_RC);
                if (rc == 1) {
                    JSONArray pl = (JSONArray) resultJson.get(CloudBridgeUtil.KEY_NAME_PL);
                    callback.onSuccess(pl.toJSONString());
                } else {
                    callback.onFail("");
                }
            }
        });
    }

    public void getAppDetail(String appId, String AES_KEY, String sid, final OperationCallback callback) {

        OkHttpClient client = new OkHttpClient();

        String url = FunctionUrl.APP_STORE_DETAIL_URL;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject reqJson = new JSONObject();
        reqJson.put("app_id", appId);
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(reqJson.toJSONString(), AES_KEY, AES_KEY), Base64.NO_WRAP) + sid;
        RequestBody body = RequestBody.create(JSON, reqBody);

        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG + " IOException = " + e.toString());
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i(TAG + " results = " + results);
                JSONObject resultJson = (JSONObject) JSONValue.parse(results);
                int rc = (int) resultJson.get(CloudBridgeUtil.KEY_NAME_RC);
                if (rc == 1) {
                    JSONObject pl = (JSONObject) resultJson.get(CloudBridgeUtil.KEY_NAME_PL);
                    callback.onSuccess(pl.toJSONString());
                } else {
                    callback.onFail("");
                }
            }
        });
    }


    // 获取安装应用列表
    public void getInstalledAppList(String eid, NetService netService, String sid, OperationCallback callBack) {

        this.callback = callBack;
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        JSONObject reqJson = new JSONObject();
        reqJson.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_GET_INSTALL_APP_LIST);
        reqJson.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        reqJson.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
        reqJson.put(CloudBridgeUtil.KEY_NAME_SID, sid);

        MyMsgData req = new MyMsgData();
        req.setReqMsg(reqJson);
        req.setTimeout(30 * 1000);
        req.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    JSONArray array = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_LIST);
                    callback.onSuccess(array.toJSONString());
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    callback.onFail(context.getString(R.string.network_error_prompt));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    callback.onFail(context.getString(R.string.set_timeout));
                } else {
                    callback.onFail(context.getString(R.string.set_error));
                }
            }
        });
        if (netService != null)
            netService.sendNetMsg(req);
    }

    /**
     * 操作手表App
     *
     * @param optType 操作类型。0新增，1修改，2删除。
     */
    public void setWatchAppState(WatchAppBean watchApp, int optType,
                                 String eid, String gid, NetService netService, String sid, OperationCallback callBack) {

        this.callback = callBack;
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_NAME, watchApp.name);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_APPID, watchApp.app_id);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_ICON, watchApp.icon);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_VERSION, watchApp.version);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_VERSIONCODE, watchApp.version_code);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_DOWNLOADURL, watchApp.download_url);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_WIFI, watchApp.wifi);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_SIZE, watchApp.size);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_MD5, watchApp.md5);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_TYPE, watchApp.type);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_STATUS, watchApp.status);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_HIDDEN, watchApp.hidden);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_INSTALLAPP_LIST_CHANGE);
        pl.put(CloudBridgeUtil.KEY_APPSTORE_APP_OPTYPE, optType);

        JSONObject reqJson = new JSONObject();
        reqJson.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_SET_INSTALL_APP_LIST);
        reqJson.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        reqJson.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
        reqJson.put(CloudBridgeUtil.KEY_NAME_SID, sid);

        MyMsgData req = new MyMsgData();
        req.setReqMsg(reqJson);
        req.setTimeout(30 * 1000);
        req.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    callback.onSuccess("");
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    callback.onFail(context.getString(R.string.network_error_prompt));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    callback.onFail(context.getString(R.string.set_timeout));
                } else {
                    callback.onFail(context.getString(R.string.set_error));
                }
            }
        });

        if (netService != null)
            netService.sendNetMsg(req);
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

    public String getPackNameFromList(ImibabyApp mApp, String packName, List<Map<String, String>>
            packInfoList) {
        String appName = "";
        for (int i = 0; i < packInfoList.size(); i++) {
            Map<String, String> appInfo = packInfoList.get(i);
            if (packName.equals(appInfo.get("packagename"))) {
                appName = appInfo.get("name");
                break;
            }
        }
        if (appName == null) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
        }

        return appName;
    }

    public String getIconUrlFromList(ImibabyApp mApp, String packName, List<Map<String, String>>
            packInfoList) {
        String iconUrl = null;
        for (int i = 0; i < packInfoList.size(); i++) {
            Map<String, String> appInfo = packInfoList.get(i);
            if (packName.equals(appInfo.get("packagename"))) {
                iconUrl = appInfo.get("icon");
                break;
            }
        }

        if (iconUrl == null) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
        }

        return iconUrl;
    }

    //判断用户是否有切换语言的动作
    public boolean isLocalLangFromLastSet(ImibabyApp mApp) {
        String localLang = mApp.getStringValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_LANG, "");
        String lang = Locale.getDefault().getLanguage();
        if (localLang.equals(lang)) {
            return true;
        } else {
            return false;
        }
    }

    //获取包名和icon对应表
    public void getPackageAndIconTable(final ImibabyApp mApp) {
        new MioAsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String getAesData = "";
                    JSONObject pl = new JSONObject();
                    pl.put("EID", mApp.getCurUser().getEid());
                    pl.put("lang", Locale.getDefault().getLanguage());
                    getAesData = pl.toJSONString();

                    LogUtil.e(TAG + ":getPackageAndIconTable:" + getAesData);
                    String encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(getAesData
                            .toString(), mApp.getNetService().getAESKEY(), mApp.getNetService()
                            .getAESKEY())) + mApp.getToken();
                    return mApp.PostJsonWithURLConnection(encryptData, FunctionUrl
                            .APP_PACKAGE_AND_ICON_GET_URL, false, mApp.getAssets().open
                            ("dxclient_t.bks"));
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
                    int rc = (int) jsonObject.get("RC");
                    LogUtil.e("getAppNameAndIcon:" + s + ":" + rc);
                    if (rc == 1) {
                        mApp.setValue(Constants.SHARE_PREF_FIELD_PACKAGE_ICON_TABLE, s);
                        mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, true);
                        mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE_TIME,
                                TimeUtil.getTimeStampLocal());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    //获取包名对应icon和应用名  zj 2019.04.17
    public static List<Map<String, String>> getTableFromSourceData(ImibabyApp mApp) {
        String sourceData = mApp.getStringValue(Constants.SHARE_PREF_FIELD_PACKAGE_ICON_TABLE, "");
        List<Map<String, String>> listData = new ArrayList<>();

        if (TextUtils.isEmpty(sourceData)) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
            return listData;
        }

        JSONObject jsonObject = (JSONObject) JSONValue.parse(sourceData);
        JSONArray jsonArray = (JSONArray) jsonObject.get("PL");
        if (jsonArray == null || jsonArray.size() == 0) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
            return listData;
        }

        LogUtil.e("getTableFromSourceData json = " + jsonArray.toJSONString());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("packagename", (String) jsonObject1.get("packagename"));
            map.put("name", (String) jsonObject1.get("name"));
            map.put("icon", (String) jsonObject1.get("icon"));
            listData.add(map);
        }

        LogUtil.e("getTableFromSourceData listData = " + listData);
        if (listData == null || listData.size() == 0) {
            mApp.setValue(Constants.SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE, false);
        }
        return listData;
    }
}
