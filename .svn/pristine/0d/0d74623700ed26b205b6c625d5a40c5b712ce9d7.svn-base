package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.Timer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
* 类名称：SplashAdActivity
* 创建人：zhangjun5
* 创建时间：2016/7/4 13:47
* 方法描述：开屏广告的主要实现主体
*/
public class SplashAdActivity extends NormalActivity {

    private Timer reqTimer = null;
    private int timeCount = 5;
    private RelativeLayout layer_time;
    private TextView tvCloseTimer;
    private ImageView adImagView;
    private ImibabyApp myApp;
    private String adId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        setTintColor(getResources().getColor(R.color.black));
        myApp = (ImibabyApp) getApplication();
        adId = getIntent().getStringExtra("adfilepath");
        myApp.sdcardLog("splashActivity:"+"adId:" + adId +"myApp.getIsMainActivityOpen()"+myApp.getIsMainActivityOpen() );
        if(adId == null || adId.equals("")){
            if(!myApp.getIsMainActivityOpen()){
                startActivity(new Intent(SplashAdActivity.this, NewMainActivity.class));
            }
            finish();
            return ;
        }
        String fileName =ImibabyApp.getIconCacheDir() + "/" + adId+".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        myApp.sdcardLog("splashActivity:"+"bitmap:" + bitmap +"myApp.getIsMainActivityOpen()"+myApp.getIsMainActivityOpen());
        if(bitmap == null){
            if(!myApp.getIsMainActivityOpen()){
                startActivity(new Intent(SplashAdActivity.this, NewMainActivity.class));
            }
            finish();
            return ;
        }
        int interval = getIntent().getIntExtra("adInterval", 0);
        final String targetUrl = getIntent().getStringExtra("targetUrl");
        layer_time = findViewById(R.id.clost_time_layer);
        tvCloseTimer = findViewById(R.id.closeTimer);
        layer_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                if(!myApp.getIsMainActivityOpen()){
                    startActivity(new Intent(SplashAdActivity.this, NewMainActivity.class));
                }
                finish();
            }
        });

        adImagView = findViewById(R.id.splashAd);
        adImagView.setImageBitmap(bitmap);
        adImagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                try {
                    myApp.AdState(2, myApp.getCurUser().getFocusWatch().getEid(), adId);
                    Intent _intent = new Intent(SplashAdActivity.this, AdWebViewActivity.class);
                    if(targetUrl == null || targetUrl.equals("")){
                        _intent.putExtra("targetUrl", FunctionUrl.AD_DEFAULT_URL);
                    }else {
                        _intent.putExtra("targetUrl", targetUrl);
                    }
                    _intent.putExtra("activityType", 0);
                    startActivity(_intent);
                    finish();
                }catch(Exception e){
                    if(!myApp.getIsMainActivityOpen()){
                        startActivity(new Intent(SplashAdActivity.this, NewMainActivity.class));
                    }
                    finish();
                }
            }
        });
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String stamp = dateFormat.format(new Date());
        myApp.setValue(Const.CHECK_AD_INTERVAL_TIME, stamp);
        try {
            myApp.AdState(1, myApp.getCurUser().getFocusWatch().getEid(), adId);

        }catch(Exception e) {
            e.printStackTrace();
        }
        if (interval == 0) {
            startTraceTimeout(5);
        } else {
            tvCloseTimer.setText(getString(R.string.adview_cancle_focus));
            startTraceTimeout(interval / 1000);
        }
    }

    @Override
    public void onBackPressed() {
        LogUtil.i("SplashAdActivity onBackPressed");
    }

    /**
     * 类名称：SplashAdActivity
     * 创建人：zhangjun5
     * 创建时间：2016/6/27 13:38
     * 方法描述：开启定时器开关
     */
    private void startTraceTimeout(int traceTime) {
        stopTimer();
        timeCount = traceTime;
        reqTimer = new Timer(1000, new Runnable() {
            public void run() {
                if (reqTimer != null) {
                    timeCount -= 1;
//                    tvCloseTimer.setText("跳过   "+timeCount+"s");
                    tvCloseTimer.setText(getString(R.string.adview_cancle_focus));
                    tvCloseTimer.invalidate();
                    if (timeCount <= 0) {
                        stopTimer();
                        if(!myApp.getIsMainActivityOpen()){
                            startActivity(new Intent(SplashAdActivity.this, NewMainActivity.class));
                        }
                        finish();
                    } else {
                        reqTimer.restart();
                    }
                }
            }
        });
        reqTimer.start();
    }


    /**
    * 类名称：SplashAdActivity
    * 创建人：zhangjun5
    * 创建时间：2016/6/27 13:37
    * 方法描述：停止定时器
    */
    private void stopTimer() {
        if (reqTimer != null) {
            LogUtil.i("time is stop!");
            reqTimer.stop();
            reqTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
