package com.xiaoxun.xun.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.WatchManagerActivity;
import com.xiaoxun.xun.beans.LocationData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.services.NetService;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.List;
import java.util.Set;

/**
 * Created by huangyouyang on 2017/7/17.
 */

public class FrontServiceUtils {

    public static boolean isStartFrontService(ImibabyApp mApp) {
//        if (SystemUtils.getMIUI() == null)
//            return mApp.getBoolValue(Const.SHARE_PREF_FIELD_APP_NOTIFY_ONOFF, true);
//        else
//            return mApp.getBoolValue(Const.SHARE_PREF_FIELD_APP_NOTIFY_ONOFF, false);
        return false;
    }


    public static void stopFrontService(NetService service) {
        if (service != null)
            service.stopForeground(true);  //If true, the notification previously providedto will be removed.  Otherwise it will remain until a later call removes it (or the service is destroyed).
    }

    public static void startFrontServiceAndroidO(final NetService service) {

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Intent intent = new Intent(Const.ACTION_MOVE_TASK_TO_FRONT);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(service, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                Notification.Builder builder = new Notification.Builder(service, NotificationHelper.APP_RUNNING_CHANNEL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(service.getString(R.string.app_name))
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(service.getString(R.string.app_intelligent_guard, service.getString(R.string.app_name)))
                        .setContentText(service.getString(R.string.xun_purpose))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(false);
                Notification notification = builder.build();
                service.startForeground(1, notification);
                // 先不10s后关闭通知栏
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        service.stopForeground(true);
//                    }
//                }, 10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
