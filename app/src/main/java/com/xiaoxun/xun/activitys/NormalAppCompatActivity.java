/**
 * Creation Date:2015-2-5
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.HttpNetUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;

/**
 * 普通activty 如果需要处理通用的事件，可以继承
 *
 * 
 */
public class NormalAppCompatActivity extends AppCompatActivity {

    ImibabyApp myApp;
    BroadcastReceiver baseRevReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        myApp = (ImibabyApp)getApplication();

        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.bg_color_orange));

        baseRevReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.BROADCAST_ACTION_QUIT_APP)) {
                    finish();
                } else if (action.equals(Const.ACTION_UNBIND_RESET_FOCUS_WATCH)){
                    unbindWatch();
                } else if (action.equals(Const.ACTION_UNBIND_OTHER_WATCH)){
                    unbindWatch();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.BROADCAST_ACTION_QUIT_APP);
        filter.addAction(Const.ACTION_UNBIND_RESET_FOCUS_WATCH);
        filter.addAction(Const.ACTION_UNBIND_OTHER_WATCH);
        myApp.getLocalBroadcastManager().registerReceiver(baseRevReceiver, filter);
    }

    protected ImibabyApp getMyApp(){
        return myApp;
    }
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!myApp.isCurrentRunningForeground  || myApp.isFirstSendTuibida()){
//            LogUtil.d(">>>>>>>>>>>>>>>>>>>切到前台NormalAppCompat 发送推必达唤醒消息");
//            sendTuibidaWakeMsg();
//        }
        myApp.isCurrentRunningForeground = true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        myApp.isCurrentRunningForeground = myApp.isRunningForeground();
        if (!myApp.isCurrentRunningForeground) {
            LogUtil.d(">>>>>>>>>>>>>>>>>>>切到后台 NormalAppCompat activity process");
            myApp.sdcardLog(">>>>>>>>>>>>>>>>>>>切到后台 NormalAppCompat activity process");
        }
    }

    protected  void unbindWatch(){
        finish();
    }
}
