package com.xiaoxun.xun.message;

import android.content.Intent;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;

public class MessageUtils {

    public static void addOfflineMsgCount(ImibabyApp mApp) {

        int count = mApp.getIntValue(Constants.SHARE_PREF_OFFLINE_MESSAGE_COUNT, 0);
        // 点击查看帮助后，直接置count为-1。再次收到离线消息，也不再进行计数
        if (count < 0)
            return;
        count++;
        mApp.setValue(Constants.SHARE_PREF_OFFLINE_MESSAGE_COUNT, count);
        mApp.sendBroadcast(new Intent(Const.ACTION_MESSAGE_CONUNT_CHANGE));
    }

    public static int getOfflineMsgCount(ImibabyApp mApp) {

        return mApp.getIntValue(Constants.SHARE_PREF_OFFLINE_MESSAGE_COUNT, 1);
    }

    public static void clearOfflineMsgCount(ImibabyApp mApp) {

        mApp.setValue(Constants.SHARE_PREF_OFFLINE_MESSAGE_COUNT, -1);
    }
}
