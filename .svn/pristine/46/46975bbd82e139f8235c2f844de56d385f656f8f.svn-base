package com.xiaoxun.xun.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;


import androidx.annotation.Nullable;

import com.xiaoxun.xun.activitys.VideoCallActivity2;
import com.xiaoxun.xun.views.VoiceFloatingView;

import java.util.List;

public class VoiceFloatingService extends Service {

    private VoiceFloatingView mVoiceFloatingView;


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化悬浮View
        mVoiceFloatingView = new VoiceFloatingView(this);
        //mVoiceFloatingView.show();
        mVoiceFloatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voiceActivityIntent = new Intent(VoiceFloatingService.this, cls);
                voiceActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(voiceActivityIntent);
                if (mVoiceFloatingView != null)
                    mVoiceFloatingView.dismiss();

                // 增加一步moveTaskToFront，以防止startActivity不成功
                ActivityManager activityManager = (ActivityManager) VoiceFloatingService.this.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(10);
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    if (taskInfo.topActivity.getPackageName().equals(VoiceFloatingService.this.getPackageName())) {
                        activityManager.moveTaskToFront(taskInfo.id, 0);
                        break;
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public VoiceFloatingService getService() {
            return VoiceFloatingService.this;
        }
    }

    Class<?> cls = VideoCallActivity2.class;
    public void setVideoCallActivity(Class<?> cls) {
        this.cls = cls;
    }

    public void showFloatingView() {
        if (mVoiceFloatingView != null)
            mVoiceFloatingView.show();
    }

    public void dismissFloatingView() {
        if (mVoiceFloatingView != null)
            mVoiceFloatingView.dismiss();
    }

    @Override
    public void onDestroy() {

        if (mVoiceFloatingView != null)
            mVoiceFloatingView.dismiss();
        mVoiceFloatingView = null;
        super.onDestroy();
    }
}
