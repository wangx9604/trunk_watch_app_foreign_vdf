package com.xiaoxun.xun.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import com.xiaoxun.xun.Const;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by guxiaolong on 2017/6/29.
 */

public class SystemUtils {

    public static String getANDROID_ID(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }

    public static String getWifiMAC(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }
    public static String getDeviceBrand(){
        return Build.MANUFACTURER;
    }
    private static boolean checkDeviceHasNavigationBar(Context context) {

        boolean hasMenuKey = ViewConfiguration.get(context)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey & !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        try {
            if(checkDeviceHasNavigationBar(context)) {
                Resources res = context.getResources();
                int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionCode + "_" + pi.versionName.replace("_", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getSystemVersion() {
        return Build.MODEL.replace("_", "") + "_" + Build.VERSION.RELEASE.replace("_", "");
    }

    public static String getSystemModel(){
        return Build.MODEL.replace("_","");
    }

    public static String getEMUI() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            String emui = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
            if (emui == null || emui.length() == 0) {
                return null;
            }
            buildVersion = Build.BRAND + " " + emui;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    public static String getMIUI() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            String miui = (String) getMethod.invoke(classType, new Object[]{"ro.miui.ui.version.name"});
            if (miui == null || miui.length() == 0) {
                return null;
            }
            buildVersion = Build.BRAND + " " + miui + " " +
                    getMethod.invoke(classType, new Object[]{"ro.build.version.incremental"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    public static String getColorOS() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            String colorOS = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.opporom"});
            if (colorOS == null || colorOS.length() == 0) {
                return null;
            }
            buildVersion = Build.BRAND + " " + colorOS + " " + Build.DISPLAY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    public static String getFlyme() {
        if (Build.DISPLAY.contains("Flyme")) {
            return Build.BRAND + Build.DISPLAY;
        } else {
            return null;
        }
    }

    public static String getFuntouch() {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            String funtouch = (String) getMethod.invoke(classType, new Object[]{"ro.vivo.os.build.display.id"});
            if (funtouch == null || funtouch.length() == 0) {
                return null;
            }
            buildVersion = Build.BRAND + " " + funtouch;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    public static String getDeviceInfo(Context context) {
        StringBuilder builder = new StringBuilder();
        builder.append("Android_" + Const.PACKAGE_NAME + "_");
        String androidID = getANDROID_ID(context);
        builder.append(androidID.replace("_", ""));
        builder.append("_" );
        builder.append(getSystemVersion());
        builder.append("_" );

        String uiVersion = getMIUI();
        if (uiVersion == null || uiVersion.length() == 0) {
            uiVersion = getEMUI();
            if (uiVersion == null || uiVersion.length() == 0) {
                uiVersion = getColorOS();
                if (uiVersion == null || uiVersion.length() == 0) {
                    uiVersion = getFlyme();
                    if (uiVersion == null || uiVersion.length() == 0) {
                        uiVersion = getFuntouch();
                    }
                }
            }
        }

        if (uiVersion != null && uiVersion.length() > 0) {
            uiVersion = uiVersion.replace("_", "");
        } else {
            uiVersion = "0";
        }

        builder.append(uiVersion);
        builder.append("_");
        builder.append(getAppVersion(context));
        builder.append("_0");

        return builder.toString().replace(" ", "");
    }

    public static String getDeviceID(Context context) {
        StringBuilder builder = new StringBuilder();
        builder.append("Android_" + Const.PACKAGE_NAME + "_");
        String androidID = getANDROID_ID(context);
        builder.append(androidID.replace("_", ""));
        return builder.toString().replace(" ", "");
    }

    public static void deleteAllFiles(File root) {
        File[] files = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    }

    public static String getTopActivity(Context context) {
        String topActivity = "";
        if (context == null)
            topActivity = "";
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            topActivity = taskInfo.topActivity.getClassName();
        }
        return topActivity;
    }

    public static boolean isAppRunInFront(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(2);
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
