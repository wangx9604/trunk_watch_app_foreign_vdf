package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;

import java.util.HashMap;

public class WatchStateUtils {

    public static int getSignalStateByHundred(String signal_level) {
        if (signal_level == null) {
            return 1;
        }
        int level = Integer.parseInt(signal_level);
        int signalState = 0;
        if (level == 0) {
            signalState = 0;
        } else if (level == 1) {
            signalState = 1;

        } else if (level == 2) {
            signalState = 2;

        } else if (level == 3) {
            signalState = 3;

        } else if (level == 4) {
            signalState = 4;

        } else {
            signalState = 5;
        }
        return signalState;
    }

    public static int getSignalStateByHundred2(String signal_level) {
        if (signal_level == null) {
            return 1;
        }
        int level = Integer.parseInt(signal_level);
        int signalState = 0;
        if(level <= 1){
            signalState = 0;
        } else if (level > 1 && level < 20) {
            signalState = 1;
        } else if (level >= 20 && level < 40) {
            signalState = 2;
        } else if (level >= 40 && level < 60) {
            signalState = 3;
        } else if (level >= 60 && level < 80) {
            signalState = 4;
        } else if (level >= 80) {
            signalState = 5;
        }
        return signalState;
    }

    public static int getSignalStateBySixty(String signal_level) {
        int retSignalRank = 0;
        int level = Integer.parseInt(signal_level);
        if (level < 6) {
            retSignalRank = 1;
        } else if (level >= 6 && level < 12) {
            retSignalRank = 2;
        } else if (level >= 12 && level < 18) {
            retSignalRank = 3;
        } else if (level >= 18 && level < 24) {
            retSignalRank = 4;
        } else if (level >= 24) {
            retSignalRank = 5;
        }
        return retSignalRank;
    }

    public static Bitmap toBatteryBitmap(Context context, WatchData watchData, int chargeS, int battery) {

        Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_bg);
        Bitmap foreground = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_full);
        int lowBattery = Const.BATTERY_MIN_LEVEL_VALUE_501;
        if (watchData.isDevice102() || watchData.isDevice105() || watchData.isDevice106())
            lowBattery = Const.BATTERY_MIN_LEVEL_VALUE;
        if (battery <= lowBattery && chargeS == Const.WATCH_CHARGE_IS_OFF) {
            background = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_low_bg);
            foreground = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_low);
        }
        if (chargeS == Const.WATCH_CHARGE_IS_ON) {
            foreground = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_charging2);
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        if (battery > 0) {
            int newWidth = (bgWidth - 5 - 8) * battery / 100 + 8;   //5 和 8 都是写死的像素值
            Bitmap battery_bit = Bitmap.createBitmap(foreground, bgWidth - newWidth, 0, newWidth, bgHeight);
            cv.drawBitmap(battery_bit, bgWidth - battery_bit.getWidth(), 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        }
        //save all clip
        cv.save();//保存
        //store
        cv.restore();
        return newbmp;
    }

    public static String spliteSignalStr(String signalStr) {

        String signal_level = null;
        if (signalStr == null || signalStr.trim().length() == 0 || signalStr.equals("null"))
            return null;
        String[] array = signalStr.split("_");
        if (array.length == 1) {
            signal_level = array[0];
        } else if (array.length == 2) {
            signal_level = array[1];
        }
        return signal_level;
    }


    public static int getSingalResId(ImibabyApp mApp, int signalBigger) {

        HashMap<String, Integer> watchIsOn = mApp.getmWatchIsOn();

        String eid = mApp.getCurUser().getFocusWatch().getEid();
        if (watchIsOn.get(eid) != null
                && (watchIsOn.get(eid) == Const.WATCH_STATE_POWER_OFF
                || watchIsOn.get(eid) == Const.WATCH_STATE_POWER_OFF_LOW_POWER
                || watchIsOn.get(eid) == Const.WATCH_STATE_FLIGHT
                || watchIsOn.get(eid) == Const.WATCH_STATE_MAYBE_POWER_OFF))
            return R.drawable.signal_0;
        Integer offlineState = mApp.getmWatchOfflineState().get(eid);

        if (null != offlineState && 1 == offlineState) {
            return R.drawable.signal_0;
        }

        if (!mApp.getStringValue(eid + CloudBridgeUtil.SIGNAL_LEVEL_FLAG, "0").equals("0"))
            return R.drawable.signal_1;


        if (signalBigger < 1) {
            return R.drawable.signal_0;

        } else if (signalBigger == 1) {
            return R.drawable.signal_1;

        } else if (signalBigger == 2) {
            return R.drawable.signal_2;

        } else if (signalBigger == 3) {
            return R.drawable.signal_3;

        } else if (signalBigger == 4) {
            return R.drawable.signal_4;

        } else if (signalBigger >= 5) {
            return R.drawable.signal_5;
        }
        return R.drawable.signal_3;
    }
}
