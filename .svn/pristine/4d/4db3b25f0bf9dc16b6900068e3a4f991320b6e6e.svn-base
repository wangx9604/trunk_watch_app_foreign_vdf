package com.xiaoxun.xun.receivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.WatchData;

/**
 * Created by guxiaolong on 2015/9/29.
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是拨打电话
        if(!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            //如果是来电
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_IDLE:
                    String incoming_number = intent.getStringExtra("incoming_number");
                    if(incoming_number == null){
                        break;
                    }
                    int number_length = incoming_number.length();
                    ImibabyApp mApp = (ImibabyApp)context.getApplicationContext();
                    if(mApp == null || mApp.getCurUser() == null || mApp.getCurUser().getWatchList() == null){
                        break;
                    }
					if(number_length <= 10)
						break;
                    for(WatchData watch: mApp.getCurUser().getWatchList()){
                        if(watch.getCellNum() != null &&  watch.getCellNum().length() > 0
                                && incoming_number.substring(number_length-10).equals(watch.getCellNum().substring(1))){
                            mApp.setValue(watch.getEid() + Const.SHARE_PREF_CALL_BACK_STATE, 0);
                            mApp.setValue(watch.getEid() +  Const.SHARE_PREF_LISTEN_STATE, 0);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
