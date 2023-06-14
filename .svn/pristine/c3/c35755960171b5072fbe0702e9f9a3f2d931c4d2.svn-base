package com.xiaoxun.xun.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.xiaoxun.xun.ImibabyApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

/**
 * @author cuiyufeng
 * @Description: AndroidUtil
 * @date 2018/5/7 11:14
 */
public class AndroidUtil {

    /**
     * 判断是否第一次安装或者是否覆盖安装
     */
    public static boolean isFristInstall(Context context){
        try {
            int currentVersion =getVersionCode(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int lastVersion = prefs.getInt("VERSION_KEY", -1);
            if(lastVersion>0){
                // 大于0 说明安装过
                if(currentVersion != lastVersion){
                    prefs.edit().putInt("VERSION_KEY",currentVersion).commit();
                    return true;
                }else {
                    //Log.i("cui","值一样说明没有更新");
                    return false;
                }
            }else{
                //没启动过就保存值
                prefs.edit().putInt("VERSION_KEY",currentVersion).commit();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    public static boolean isMIUI(ImibabyApp app) {
        //获取缓存状态
        if (app.getIntValue("isMIUI",0)  == 1){// 1表示是小米手机
            return true;
        }else if (app.getIntValue("isMIUI",0)  == -1){// -1表示不是小米手机
            return false;
        }

        boolean isMIUI= (getSystemProperty(KEY_MIUI_VERSION_CODE) != null
                && !"".equalsIgnoreCase(getSystemProperty(KEY_MIUI_VERSION_CODE)))
                || (getSystemProperty(KEY_MIUI_VERSION_NAME) != null
                && !"".equalsIgnoreCase(getSystemProperty(KEY_MIUI_VERSION_NAME)))
                || (getSystemProperty(KEY_MIUI_INTERNAL_STORAGE) != null
                && !"".equalsIgnoreCase(getSystemProperty(KEY_MIUI_INTERNAL_STORAGE)));
        int setMiUiValue = isMIUI ? 1 : -1;
        app.setValue("isMIUI", setMiUiValue);//保存MIUI
        return isMIUI;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static boolean isBelowMiUIV9() {
        try {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            String uiCode = properties.getProperty("ro.miui.ui.version.code", null);
            if (uiCode != null) {
                int code = Integer.parseInt(uiCode);
                return code <7;
            } else {
                return false;
            }

        } catch (final Exception e) {
            return false;
        }
    }


    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取图标 bitmap
     * @param context
     */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }


    public static boolean checkAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq") ||
                        pn.equalsIgnoreCase("com.tencent.tim")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 判断 用户是否安装微星客户端
     */

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

}
