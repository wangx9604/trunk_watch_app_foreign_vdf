package com.xiaoxun.xun.utils;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.beans.WatchData;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RedDotUtils {

    private static final String TAG = "HttpReqUtils ";

    private static RedDotUtils instance;
    ImibabyApp mApp;
    private RedDotUtils(ImibabyApp mApp) {
        this.mApp = mApp;
    }
    public static synchronized RedDotUtils getInstance(ImibabyApp mApp) {
        if (instance == null)
            instance = new RedDotUtils(mApp);
        return instance;
    }

    public void checkNeedGetRedDot(String AES_KEY, String sid) {

        String lastTime = mApp.getStringValue(Const.SHARE_PREF_FUNCTION_REDDOT_TIMESTAMP, TimeUtil.getTimeStampLocal());
        if (TimeUtil.getDataFromTimeStamp(lastTime).before(new Date())) {
            getReddotList(AES_KEY, sid);
        }
    }

    /**
     * 拉取小红点数据
     * 拉取时机：login且获取到watchlist后（判断24h间隔）
     * 拉取后的处理：将各个手表的小红点数据存储到sp中
     */
    public void getReddotList(String AES_KEY, String sid) {

        OkHttpClient client = new OkHttpClient();

        JSONArray deviceArray = new JSONArray();
        for (WatchData watch : mApp.getCurUser().getWatchList()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("EID", watch.getEid());
            JSONArray funArray = new JSONArray();
            JSONObject funXiaoai = new JSONObject();
            funXiaoai.put("name", Const.SHARE_PREF_FUNCTION_XIAOAI);
            funXiaoai.put("time", getReddotUpTime(watch.getEid(), Const.SHARE_PREF_FUNCTION_XIAOAI));
            funArray.add(funXiaoai);
            deviceJson.put("functions", funArray);
            deviceArray.add(deviceJson);
        }

        String url = FunctionUrl.APP_FUNCTION_REDDOT_URL;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject reqJson = new JSONObject();
        reqJson.put("appEID", mApp.getCurUser().getEid());
        reqJson.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        reqJson.put("version", Params.getInstance(mApp).getAppVerName());
        reqJson.put("appPackage", mApp.getPackageName());
        reqJson.put("devices", deviceArray);

        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(reqJson.toJSONString(), AES_KEY, AES_KEY), Base64.NO_WRAP) + sid;
        RequestBody body = RequestBody.create(JSON, reqBody);

        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG + " IOException = " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i(TAG + "results = " + results);
                JSONObject resultJson = (JSONObject) JSONValue.parse(results);
                int rc = (int) resultJson.get(CloudBridgeUtil.KEY_NAME_RC);
                if (rc == 1) {
                    JSONArray pl = (JSONArray) resultJson.get(CloudBridgeUtil.KEY_NAME_PL);
                    for (int i = 0; i < pl.size(); i++) {
                        JSONObject device = (JSONObject) pl.get(i);
                        String eid = (String) device.get("EID");
                        JSONArray funArray = (JSONArray) device.get("functions");
                        mApp.setValue(Const.SHARE_PREF_FUNCTION_REDDOT + eid, funArray.toJSONString());
                    }
                    mApp.sendBroadcast(new Intent(Const.ACTION_FUNCTION_REDDOT_REQ_SUCCESS));
                    // 存储时间戳
                    Calendar canlender = Calendar.getInstance();
                    canlender.add(Calendar.HOUR, 24);
                    canlender.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    mApp.setValue(Const.SHARE_PREF_FUNCTION_REDDOT_TIMESTAMP, dateFormat.format(canlender.getTime()));
                }
            }
        });
    }

    private String getReddotUpTime(String eid,String name) {

        String funArrayStr = mApp.getStringValue(Const.SHARE_PREF_FUNCTION_REDDOT + eid, "");
        if (TextUtils.isEmpty(funArrayStr))
            return "";
        JSONArray funArray = (JSONArray) JSONValue.parse(funArrayStr);
        for (int j = 0; j < funArray.size(); j++) {
            JSONObject funJson = (JSONObject) funArray.get(j);
            if (name.equals(funJson.get("name")))
                return (String) funJson.get("uptime");
        }
        return "";
    }

    public boolean isReddotShow(String eid, String name) {

        String funArrayStr = mApp.getStringValue(Const.SHARE_PREF_FUNCTION_REDDOT + eid, "");
        String lastClickTime = mApp.getStringValue(Const.SHARE_PREF_FUNCTION_REDDOT + eid + name, "");
        if (TextUtils.isEmpty(funArrayStr))
            return false;
        if (TextUtils.isEmpty(lastClickTime))
            return true;
        JSONArray funArray = (JSONArray) JSONValue.parse(funArrayStr);
        for (int j = 0; j < funArray.size(); j++) {
            JSONObject funJson = (JSONObject) funArray.get(j);
            if (name.equals(funJson.get("name")))
                return TimeUtil.getDataFromTimeStamp(lastClickTime).before(TimeUtil.getDataFromTimeStamp((String) funJson.get("uptime")));
        }
        return false;
    }
}
