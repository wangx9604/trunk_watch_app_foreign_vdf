package com.xiaoxun.xun.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoxun.xun.Const;

/**
 * Created by xilvkang on 2016/1/11.
 */
public class NotificationRecevier extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Const.ACTION_NOTIFICATION_CLEAR)){
            NotificationManager notifyMng = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra(Const.KEY_NOTIFICATION_ID,0);
            notifyMng.cancel(id);
        }
    }
}
