package com.xiaoxun.xun.securityarea;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */

public class Utils {

    public static int errorCode = 0;//-1 login failed   -2 e2eSender error -3 switch off -4 se error

    public static void showSoftInputMethod(Context context, View view, boolean show) {
        if (view == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isIP(String addr) {
        if (addr == null || addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        if (!ipAddress) {
            return false;
        }
        String ips[] = addr.split("\\.");
        if (ips.length == 4) {
            try {
                for (String ip : ips) {
                    if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static String object2String(Parcelable obj){
        Parcel p = Parcel.obtain();
        obj.writeToParcel(p,0);
        byte[] bytes = p.marshall();
        p.recycle();
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        return str;
    }

    public static Parcel unmarshall(byte[] bytes){
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static <T> T unmarshall(String str, Parcelable.Creator<T> creator){
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }

    public static String formatTimeStr(String timestr){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String ret = "";
        try {
            Date date = format1.parse(timestr);
            ret = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static String formatBJCardNo(String no){
        int len = no.length();
        String cardno = no.substring(0,len - 4);
        String check = no.substring(len - 4 ,len);
        String ret = cardno + "-" + check;
        return ret;
    }

    public static boolean isValidDate(String strDate, SimpleDateFormat format) {
        try {
            format.setLenient(false);
            String[] sArray = strDate.split("-");
            for (String s : sArray) {
                boolean isNum = s.matches("[0-9]+");
                if (!isNum) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
