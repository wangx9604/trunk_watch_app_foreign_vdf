package com.xiaoxun.xun.health.monitor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.MultiBgSeekBar;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

public class StepSettingActivity extends NormalAppCompatActivity {

    private ImibabyApp mApp;
    private WatchData curWatch;

    MultiBgSeekBar sb_seek;

    private int curTarget = 8000;
    private int curProgress = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_setting);
        StatusBarUtil.changeStatusBarColor(this,getResources().getColor(R.color.schedule_no_class));
        mApp = (ImibabyApp)getApplication();
        curTarget = getIntent().getIntExtra("target", 8000);
        curProgress = curTarget;
        String eid = getIntent().getStringExtra("eid");
        curWatch = mApp.getCurUser().queryWatchDataByEid(eid);
        initViews();
    }

    private void initViews() {
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv_step_count = findViewById(R.id.tv_step_count);
        tv_step_count.setText(String.valueOf(curTarget));
        sb_seek = findViewById(R.id.sb_seek);
        TextView tv_light = findViewById(R.id.tv_light);
        TextView tv_middle = findViewById(R.id.tv_middle);
        TextView tv_heavy = findViewById(R.id.tv_heavy);
        TextView tv_cal = findViewById(R.id.tv_cal);
        TextView tv_dura = findViewById(R.id.tv_dura);
        sb_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                curProgress = i*1000;
                sb_seek.setThumb(getThumbDrawable(i*1000));
                tv_step_count.setText(String.valueOf(i*1000));
                getTextColor(curProgress,tv_light,tv_middle,tv_heavy);
                Consume consume = getConsume(i*1000);
                tv_cal.setText(getString(R.string.health_monitor_step_setting_cal_unit, String.valueOf(consume.cal)));
                tv_dura.setText(getString(R.string.health_monitor_step_setting_dura_unit, String.valueOf(consume.dura)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_seek.setThumb(getThumbDrawable(curTarget));
        sb_seek.setProgress(curTarget/1000);
        getTextColor(curProgress,tv_light,tv_middle,tv_heavy);
        Consume consume = getConsume(curTarget);
        tv_cal.setText(getString(R.string.health_monitor_step_setting_cal_unit, String.valueOf(consume.cal)));
        tv_dura.setText(getString(R.string.health_monitor_step_setting_dura_unit, String.valueOf(consume.dura)));

        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsetTarget(curProgress);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void MapsetTarget(int target){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                switch (cid){
                    case CloudBridgeUtil.CID_MAPSET_RESP:
                        if(rc == CloudBridgeUtil.RC_SUCCESS){
                            Intent it = new Intent();
                            it.putExtra("target", target);
                            setResult(41, it);
                            finish();
                        }else{
                            ToastUtil.show(getApplicationContext(),getString(R.string.focustime_setting_send_failed));
                        }
                        break;
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.STEPS_TARGET_LEVEL, String.valueOf(target));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, curWatch.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, curWatch.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    private Drawable getThumbDrawable(int progress){
        Drawable drawable = null;
        if(progress >= 2000 && progress < 8000){
            drawable = getDrawable(R.drawable.thumb_yellow);
        }else if(progress >= 8000 && progress < 14000){
            drawable = getDrawable(R.drawable.thumb_green);
        }else if(progress >= 14000 && progress <= 20000){
            drawable = getDrawable(R.drawable.thumb_red);
        }
        return drawable;
    }

    private void getTextColor(int progress,TextView tv1,TextView tv2,TextView tv3){
        if(progress >= 2000 && progress < 8000){
            tv1.setTextColor(getResources().getColor(R.color.health_report_legend_light_yellow));
            tv2.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
            tv3.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
        }else if(progress >= 8000 && progress < 14000){
            tv1.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
            tv2.setTextColor(getResources().getColor(R.color.health_report_legend_green));
            tv3.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
        }else if(progress >= 14000 && progress <= 20000){
            tv1.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
            tv2.setTextColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
            tv3.setTextColor(getResources().getColor(R.color.health_report_legend_red));
        }
        tv1.invalidate();
        tv2.invalidate();
        tv3.invalidate();
    }

    private Consume getConsume(int steps) {
        if (steps >= 2000 && steps < 3000) {
            return new Consume(36, 4);
        } else if (steps >= 3000 && steps < 4000) {
            return new Consume(54, 6);
        } else if (steps >= 4000 && steps < 5000) {
            return new Consume(72, 9);
        } else if (steps >= 5000 && steps < 6000) {
            return new Consume(91, 11);
        } else if (steps >= 6000 && steps < 7000) {
            return new Consume(109, 13);
        } else if (steps >= 7000 && steps < 8000) {
            return new Consume(127, 15);
        } else if (steps >= 8000 && steps < 9000) {
            return new Consume(145, 18);
        } else if (steps >= 9000 && steps < 10000) {
            return new Consume(164, 20);
        } else if (steps >= 10000 && steps < 11000) {
            return new Consume(182, 22);
        } else if (steps >= 11000 && steps < 12000) {
            return new Consume(200, 25);
        } else if (steps >= 12000 && steps < 13000) {
            return new Consume(218, 27);
        } else if (steps >= 13000 && steps < 14000) {
            return new Consume(237, 29);
        } else if (steps >= 14000 && steps < 15000) {
            return new Consume(255, 31);
        } else if (steps >= 15000 && steps < 16000) {
            return new Consume(273, 34);
        } else if (steps >= 16000 && steps < 17000) {
            return new Consume(291, 36);
        } else if (steps >= 17000 && steps < 18000) {
            return new Consume(310, 38);
        } else if (steps >= 18000 && steps < 19000) {
            return new Consume(328, 41);
        } else if (steps >= 19000 && steps < 20000) {
            return new Consume(346, 43);
        } else if (steps >= 20000) {
            return new Consume(364, 45);
        }else{
            return new Consume(0,0);
        }
    }

    class Consume {
        int cal;
        int dura;

        public Consume(int c, int d) {
            cal = c;
            dura = d;
        }
    }
}