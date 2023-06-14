package com.xiaoxun.xun.services;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by guxiaolong on 2016/9/13.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationCollectorService extends NotificationListenerService {

    /**
     * 在api 18（4.3）前可以通过辅助功能AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED或是反射活取通知栏相关信息。
     * 现在我们可以根据NotificationListenerService类非常容易的活取通知回调相关信息。
     * NotificationListenerService是通过系统调起的服务，
     * 当有应用发起通知的时候，系统会将通知的动作和信息回调给NotificationListenerService。
     */

    private final static String TAG="HYY";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        /**
         * 通知栏中的消息即被封装到了StatusBarNotification中
         * 在里面可以拿到通知栏的应用名称、通知栏标题、通知栏内容
         */
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

}
