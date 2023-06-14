package com.xiaoxun.xun.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoxun.xun.beans.WatchDownloadBean;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.List;

/**
 * Created by zhangjun5 on 2017/3/4.
 */

public class XimalayaUtil {
    /**
    *user:zhangjun5 time:17:29 date:2017/3/7
    *desc:当前下载的故事是否重名，重名添加尾标
    **/
    public String renameFileBySameTrackId(WatchDownloadBean bean, List<WatchDownloadBean> mDownloadTrackList){
        String fileName = null;
        fileName = bean.getFile();
        for(int i = 0; i < mDownloadTrackList.size();i++) {
            for (WatchDownloadBean tmp : mDownloadTrackList) {
                JSONObject data = (JSONObject) (JSONValue.parse(tmp.getData()));
                String trackName = (String) data.get("file");
                if (trackName.equals(fileName)) {
                    fileName = trackName + "("+(i+1)+")";
                }
            }
        }
        return fileName;
    }

    public static String getMipushRegion(Context  context){
        LogUtil.e("mipush app region:"+ MiPushClient.getAppRegion(context));
        String region = MiPushClient.getAppRegion(context);
        if(region == null) {
            SharedPreferences sp = context.getSharedPreferences("mipush", Context.MODE_MULTI_PROCESS);
            region = sp.getString("appRegion", null);
            LogUtil.e("mipush app region1:"+region);
            if(region == null) {
                if (getAppVersionCode(context, "com.xiaomi.xmsf") >= 109 && isMIUIGlobalVersion()) {
                    region = "global";
                } else {
                    region = "china";
                }
            }
        }
        region = region.toLowerCase();
        LogUtil.e("mipush app region11:"+region);

        return region;
    }

    /**
    *user:zhangjun5 time:10:24 date:2018/1/16
    *desc:获取app的版本号
    **/
    public static int getAppVersionCode(Context mContext, String name){
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo pkgInfo = manager.getPackageInfo(name,
                    PackageManager.GET_SERVICES);
            if (pkgInfo == null) {
                return 0;
            }
            return pkgInfo.versionCode;
        } catch (Throwable ignored) {
            return 0;
        }
    }
    /**
    *user:zhangjun5 time:10:25 date:2018/1/16
    *desc:判断是否是miui全球版本
    **/
    public static boolean isMIUIGlobalVersion() {
        try {
            Class clazz = Class.forName("miui.os.Build");
            return clazz.getField("IS_GLOBAL_BUILD").getBoolean(false);
        } catch (Exception e) {
            Log.e("MIUIGlobalVersion()",e.toString());
            return false;
        }
    }
}
