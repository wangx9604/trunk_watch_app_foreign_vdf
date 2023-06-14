package com.xiaoxun.xun.health.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.HCircleProgress;
import com.xiaoxun.xun.health.customview.MidDetailDidlog;
import com.xiaoxun.xun.health.customview.MidDialog;
import com.xiaoxun.xun.health.customview.MidHelpDialog;
import com.xiaoxun.xun.health.customview.MidTipHfDialog;
import com.xiaoxun.xun.health.heartratesetting.SettingActivity;
import com.xiaoxun.xun.health.outside.OutSideMainActivity;
import com.xiaoxun.xun.health.report.ReportActivity;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.text.DecimalFormat;


public class MonitorMainActivity extends NormalAppCompatActivity {

    private ImibabyApp mApp;
    private WatchData curWatch;
    private MonitorMainViewModel monitorMainViewModel;

    private LinearLayout layout_handmove_content;
    private TextView tv_heart_count;
    private TextView tv_heartrate_unit;
    private TextView tv_heart_monitoring;
    private TextView tv_oxy_count;
    private TextView tv_oxy_unit;
    private TextView tv_oxy_monitoring;
    private HCircleProgress pb_step;
    private ConstraintLayout ly_fatigue_enable;
    private ImageView iv_monitor_switch;
    private CardView cv_outside;
    private TextView tv_outside_dura;
    private ProgressBar pb_outside;

    private LinearLayout ly_heart_monitor;
    private ImageView iv_heart_monitor;
    private TextView tv_heart_monitor_str;
    private LinearLayout ly_oxy_monitor;
    private ImageView iv_oxy_monitor;
    private TextView tv_oxy_monitor_str;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_CLOUD_BRIDGE_STEPS_CHANGE)) {
                String stepsSum = intent.getStringExtra(CloudBridgeUtil.STEPS_LEVEL);
                String[] array = stepsSum.split("_");
                if (TimeUtil.compareTodayToLastInfo(array[0])) {
                    String curSteps = array[1];
                    monitorMainViewModel.getCurSteps().postValue(Integer.valueOf(curSteps));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_main);
        StatusBarUtil.setStatusBarColor(this, R.color.health_monitor_statusbar_color);
        mApp = (ImibabyApp) getApplication();
        String eid = getIntent().getStringExtra("eid");
        if (eid != null) {
            //从H5跳转
            curWatch = mApp.getCurUser().queryWatchDataByEid(eid);
        } else {
            //native
            curWatch = mApp.getCurUser().getFocusWatch();
        }

        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        monitorMainViewModel = factory.create(MonitorMainViewModel.class);//new ViewModelProvider(this, factory).get(MonitorMainViewModel.class);

        initViews();
        initRecevier();
        monitorMainViewModel.sendStepsReqE2eMsg(mApp);
    }

    private void initRecevier() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_CLOUD_BRIDGE_STEPS_CHANGE);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 41) {
            LogUtil.e("onActivityResult data = " + data.getStringExtra("target"));
            if (requestCode == 40) {
                int target = data.getIntExtra("target", 0);
                pb_step.setTarget(String.valueOf(target));
                if (monitorMainViewModel.getCurSteps().getValue() != null) {
                    float pec = (float) (monitorMainViewModel.getCurSteps().getValue()) / (float) target;
                    pb_step.setProgress(pec * 100);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        monitorMainViewModel.initData(mApp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initViews() {
        ImageView iv_help = findViewById(R.id.iv_help);
        boolean isAdmin = mApp.getCurUser().isMeAdminByWatch(curWatch);
        iv_help.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);
        iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonitorMainActivity.this, SettingActivity.class));
            }
        });
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        ImageView iv_help = findViewById(R.id.iv_help);
//        iv_help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MidHelpDialog dlg = new MidHelpDialog(MonitorMainActivity.this);
//                dlg.show();
//            }
//        });
        pb_step = findViewById(R.id.pb_step);
        pb_step.setTargetSettingVisiable(true);
        pb_step.setProgress(0);
        pb_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent it = new Intent(MonitorMainActivity.this, StepSettingActivity.class);
                    it.putExtra("target", pb_step.getTarget());
                    it.putExtra("eid", curWatch.getEid());
                    startActivityForResult(it, 40);
            }
        });
        monitorMainViewModel.getCurSteps().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    pb_step.setNumber(String.valueOf(integer));
                }
            }
        });
        monitorMainViewModel.getTargetSteps().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    pb_step.setTarget(String.valueOf(integer));
                }
            }
        });
        monitorMainViewModel.getStepProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    int progress = integer > 100 ? 100 : integer;
                    pb_step.setProgress(progress, true);
                }
            }
        });

        TextView tv_distance = findViewById(R.id.tv_distance);
        monitorMainViewModel.getDistance().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    double meters = Double.parseDouble(s);
//                    double kilometers = meters  / 1000d;
                    LogUtil.i("----getDistance----" + meters);
                    tv_distance.setText(formatKilometers(meters));
                }
            }
        });
        TextView tv_cal = findViewById(R.id.tv_cal);
        monitorMainViewModel.getCalories().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    double cal = Double.parseDouble(s);
                    tv_cal.setText(formatCaloris(cal));
                }
            }
        });

        tv_heartrate_unit = findViewById(R.id.tv_heartrate_unit);
        tv_heart_monitoring = findViewById(R.id.tv_heart_monitoring);
        tv_heart_count = findViewById(R.id.tv_heart_count);
        monitorMainViewModel.getHeartRate().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    updateHeartRateMonitorStatus(1);
                    if (integer != -1) {
                        tv_heart_count.setText(String.valueOf(integer));
                    } else {
                        ToastUtil.show(getApplicationContext(), getString(R.string.health_monitor_monitor_failed));
                    }
                }
            }
        });
        tv_oxy_unit = findViewById(R.id.tv_oxy_unit);
        tv_oxy_monitoring = findViewById(R.id.tv_oxy_monitoring);
        tv_oxy_count = findViewById(R.id.tv_oxy_count);
        monitorMainViewModel.getOxy().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    updateOxygenMonitorStatus(1);
                    if (integer != -1) {
                        tv_oxy_count.setText(String.valueOf(integer));
                    } else {
                        ToastUtil.show(getApplicationContext(), getString(R.string.health_monitor_monitor_failed));
                    }
                }
            }
        });

        iv_monitor_switch = findViewById(R.id.iv_monitor_switch);
        iv_monitor_switch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MidHelpDialog dlg = new MidHelpDialog(MonitorMainActivity.this);
                dlg.show();
            }
        });

        ly_heart_monitor = findViewById(R.id.ly_heart_monitor);
        iv_heart_monitor = findViewById(R.id.iv_heart_monitor);
        tv_heart_monitor_str = findViewById(R.id.tv_heart_monitor_str);
        ly_heart_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_heart_monitoring.getVisibility() == View.VISIBLE) {
                    return;
                }
                //发起心率一次监测
                monitorMainViewModel.sendMonitorMessage(mApp, "heartRate");
                updateHeartRateMonitorStatus(0);
            }
        });
        iv_oxy_monitor = findViewById(R.id.iv_oxy_monitor);
        ly_oxy_monitor = findViewById(R.id.ly_oxy_monitor);
        tv_oxy_monitor_str = findViewById(R.id.tv_oxy_monitor_str);
        ly_oxy_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_oxy_monitoring.getVisibility() == View.VISIBLE) {
                    return;
                }
                //发起一次血氧监测
                monitorMainViewModel.sendMonitorMessage(mApp, "oxygen");
                updateOxygenMonitorStatus(0);
            }
        });
            ly_heart_monitor.setVisibility(View.VISIBLE);
            ly_oxy_monitor.setVisibility(View.VISIBLE);

        ToggleButton tb_heart_monitor = findViewById(R.id.tb_heart_monitor);
        tb_heart_monitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (!mApp.getCurUser().isMeAdminByWatch(curWatch)) {
                        ToastUtil.show(MonitorMainActivity.this, getString(R.string.need_admin_auth));
                        tb_heart_monitor.setChecked(!b);
                        return;
                    }
                    if (!b) {
                        monitorMainViewModel.mapSetHeartSettingMonitor(mApp, 0);
                    } else {
                        MidDialog dlg = new MidDialog(MonitorMainActivity.this, getString(R.string.health_monitor_power_tips_title)
                                , getString(R.string.health_monitor_power_tips_content)
                                , new MidDialog.OnBtnClick() {
                            @Override
                            public void onClick() {
                                tb_heart_monitor.setChecked(false);
                            }
                        }, new MidDialog.OnBtnClick() {
                            @Override
                            public void onClick() {
                                monitorMainViewModel.mapSetHeartSettingMonitor(mApp, 1);
                            }
                        });
                        dlg.show();
                    }
                }
            }
        });

        ConstraintLayout cl_fatigue_monitor_auth = findViewById(R.id.cl_fatigue_monitor_auth);
        monitorMainViewModel.getHasFatigueAuth().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    cl_fatigue_monitor_auth.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                }
            }
        });
        monitorMainViewModel.getHasFatigueAuth().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    cl_fatigue_monitor_auth.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                } else {
                    cl_fatigue_monitor_auth.setVisibility(View.GONE);
                }
            }
        });
        Button btn_fatigue_monitor_auth = findViewById(R.id.btn_fatigue_monitor_auth);
        btn_fatigue_monitor_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传拒绝加入探索
                MidDetailDidlog dlg = new MidDetailDidlog(MonitorMainActivity.this, getString(R.string.health_monitor_fatigue_auth_exit_dlg_title),
                        getString(R.string.health_monitor_fatigue_auth_exit_dlg_content), getString(R.string.cancel), new MidDetailDidlog.OnBtnClick() {
                    @Override
                    public void onClick() {

                    }
                }, getString(R.string.confirm), new MidDetailDidlog.OnBtnClick() {
                    @Override
                    public void onClick() {
                        monitorMainViewModel.mapSetFatigueAuth(mApp, false);
                    }
                });
                dlg.show();
            }
        });

        ToggleButton tb_fatigue_monitor = findViewById(R.id.tb_fatigue_monitor);
        ly_fatigue_enable = findViewById(R.id.ly_fatigue_enable);
        ly_fatigue_enable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        tb_fatigue_monitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (!mApp.getCurUser().isMeAdminByWatch(curWatch)) {
                        ToastUtil.show(MonitorMainActivity.this, getString(R.string.need_admin_auth));
                        tb_fatigue_monitor.setChecked(!isChecked);
                        return;
                    }
                    boolean fatiAuth = monitorMainViewModel.getHasFatigueAuth().getValue();
                    if (!fatiAuth) {
                        //显示加入探索弹窗
                        MidDetailDidlog dlg = new MidDetailDidlog(MonitorMainActivity.this, getString(R.string.health_monitor_fatigue_auth_dlg_title)
                                , getString(R.string.health_monitor_fatigue_auth_dlg_content)
                                , getString(R.string.health_monitor_fatigue_auth_dlg_refuse), new MidDetailDidlog.OnBtnClick() {
                            @Override
                            public void onClick() {
                                //上传授权结果
                                monitorMainViewModel.mapSetFatigueAuth(mApp, false);
                                monitorMainViewModel.mapSetFatigueMonitorOnOff(mApp, false);
                            }
                        }, getString(R.string.health_monitor_fatigue_auth_dlg_agree), new MidDetailDidlog.OnBtnClick() {
                            @Override
                            public void onClick() {
                                //上传授权结果
                                monitorMainViewModel.mapSetFatigueAuth(mApp, true);
                                //上传开关状态
                                monitorMainViewModel.mapSetFatigueMonitorOnOff(mApp, isChecked);
                            }
                        });
                        dlg.show();
                    } else {
                        //上传开关状态
                        monitorMainViewModel.mapSetFatigueMonitorOnOff(mApp, isChecked);
                    }
                }
            }
        });
        monitorMainViewModel.getFatigueMonitorSwitch().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        ly_fatigue_enable.setVisibility(View.GONE);
                    } else {
                        ly_fatigue_enable.setVisibility(View.VISIBLE);
                    }
                    tb_fatigue_monitor.setChecked(aBoolean);
                }
            }
        });

        monitorMainViewModel.getHeartMonitorSwitch().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    tb_heart_monitor.setChecked(aBoolean);
                }
            }
        });
        ToggleButton tb_fatigue_monitor_com = findViewById(R.id.tb_fatigue_monitor_daily);
        ToggleButton tb_fatigue_monitor_hf = findViewById(R.id.tb_fatigue_monitor_highfrequency);
        tb_fatigue_monitor_com.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (!mApp.getCurUser().isMeAdminByWatch(curWatch)) {
                        ToastUtil.show(MonitorMainActivity.this, getString(R.string.need_admin_auth));
                        tb_fatigue_monitor_com.setChecked(!b);
                        return;
                    }
                    if (!b) {
                        tb_fatigue_monitor_com.setChecked(true);
                        return;
                    }
                    tb_fatigue_monitor_hf.setChecked(!b);
                        monitorMainViewModel.SetFatigueSettingHfMonitor(mApp, !b ? 1 : 0, b ? 1 : 0);
                }
            }
        });
        monitorMainViewModel.getFatigueMonitorComSwitch().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    tb_fatigue_monitor_com.setChecked(aBoolean);
                }
            }
        });
        TextView tv_heart_monitor_schedule_content_daily = findViewById(R.id.tv_heart_monitor_schedule_content_daily);
            tv_heart_monitor_schedule_content_daily.setText(getString(R.string.health_monitor_help_daily_content1));

        tb_fatigue_monitor_hf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (!mApp.getCurUser().isMeAdminByWatch(curWatch)) {
                        ToastUtil.show(MonitorMainActivity.this, getString(R.string.need_admin_auth));
                        tb_fatigue_monitor_hf.setChecked(!b);
                        return;
                    }
                    if (!b) {
                        tb_fatigue_monitor_hf.setChecked(true);
                        return;
                    }
                    tb_fatigue_monitor_com.setChecked(!b);
                        if (!b) {
                            monitorMainViewModel.SetFatigueSettingHfMonitor(mApp, 0, 1);
                        } else {
                            MidTipHfDialog dlg = new MidTipHfDialog(MonitorMainActivity.this, new MidTipHfDialog.OnBtnClick() {
                                @Override
                                public void onClick() {
                                    tb_fatigue_monitor_com.setChecked(true);
                                    tb_fatigue_monitor_hf.setChecked(false);
                                }
                            }, new MidTipHfDialog.OnBtnClick() {
                                @Override
                                public void onClick() {
                                    monitorMainViewModel.SetFatigueSettingHfMonitor(mApp, 1, 0);
                                }
                            }, true);
                            dlg.show();
                        }
                }
            }
        });
        monitorMainViewModel.getFatigueMonitorHfSwitch().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    tb_fatigue_monitor_hf.setChecked(aBoolean);
                }
            }
        });

        TextView tv_heart_monitor_schedule_content = findViewById(R.id.tv_heart_monitor_schedule_content);
            tv_heart_monitor_schedule_content.setText(getString(R.string.health_monitor_heart_monitor_content_hf1));


//        layout_handmove_content = findViewById(R.id.layout_handmove_content);
        WatchData watchData = mApp.getCurUser().getFocusWatch();
//        if(mApp.getConfigFormDeviceType(watchData.getDeviceType(),watchData.getVerCur(),watchData.getMachSn()).getSwitch_health_monitor_time()){
//            layout_handmove_content.setVisibility(View.VISIBLE);
//            tb_fatigue_monitor_com.setVisibility(View.INVISIBLE);
//        }else{
//            layout_handmove_content.setVisibility(View.GONE);
//            tb_fatigue_monitor_com.setVisibility(View.VISIBLE);
//        }
//        RelativeLayout layout_add_new_time = findViewById(R.id.layout_add_new_time);
//        layout_add_new_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mApp.getCurUser().isMeAdminByWatch(curWatch)) {
//                    ToastUtil.show(MonitorMainActivity.this,getString(R.string.need_admin_auth));
//                    return;
//                }
//                if(monitorMainViewModel.getTimeList().getValue() != null){
//                    if(monitorMainViewModel.getTimeList().getValue().size() >= 3){
//                        ToastUtil.show(getApplicationContext(),getString(R.string.health_monitor_add_time_limit));
//                        return;
//                    }
//                    showFatigueTimeSelectDialog();
//                }
//            }
//        });
//        monitorMainViewModel.getTimeList().observe(this, new Observer<List<MonitorTimeBean>>() {
//            @Override
//            public void onChanged(@Nullable List<MonitorTimeBean> monitorTimeBeans) {
//                updateListView(monitorTimeBeans);
//            }
//        });

        CardView card_heart = findViewById(R.id.card_heart);
        CardView card_oxy = findViewById(R.id.card_oxy);
        ConstraintLayout cl_heart_monitor_schedule = findViewById(R.id.cl_heart_monitor_schedule);
            cl_heart_monitor_schedule.setVisibility(View.VISIBLE);
        card_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MonitorMainActivity.this, ReportActivity.class);
                int value = pb_step.getTarget();
                it.putExtra("target_step", value);
                it.putExtra("sign", "heart");
                it.putExtra("eid", curWatch.getEid());
                startActivity(it);
            }
        });
        card_oxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MonitorMainActivity.this, ReportActivity.class);
                int value = pb_step.getTarget();
                it.putExtra("target_step", value);
                it.putExtra("sign", "oxy");
                it.putExtra("eid", curWatch.getEid());
                startActivity(it);
            }
        });


        cv_outside = findViewById(R.id.cv_outside);
        tv_outside_dura = findViewById(R.id.tv_outside_dura);
        pb_outside = findViewById(R.id.pb_outside);
        TextView tv_outside_unit = findViewById(R.id.tv_outside_unit);
        TextView tv_no_outside_data = findViewById(R.id.tv_no_outside_data);
            cv_outside.setVisibility(View.VISIBLE);
        cv_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonitorMainActivity.this, OutSideMainActivity.class));
            }
        });
        monitorMainViewModel.getOutsideDura().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    tv_outside_dura.setVisibility(View.VISIBLE);
                    tv_outside_unit.setVisibility(View.VISIBLE);
                    tv_no_outside_data.setVisibility(View.GONE);
                    tv_outside_dura.setText(String.valueOf(integer));
                    if (integer >= 60) {
                        pb_outside.setProgress(60);
                    } else {
                        pb_outside.setProgress(integer);
                    }
                } else {
                    tv_outside_dura.setVisibility(View.GONE);
                    tv_outside_unit.setVisibility(View.GONE);
                    tv_no_outside_data.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * @param status 0 监测中 1 监测数据显示
     */
    public void updateHeartRateMonitorStatus(int status) {
        if (status == 0) {
            ly_heart_monitor.setBackground(getDrawable(R.drawable.btn_jiance_disabled));
            iv_heart_monitor.setImageResource(R.drawable.icon_jiance_disabled);
            tv_heart_monitor_str.setTextColor(getResources().getColor(R.color.health_monitor_str_dis_color));
            tv_heart_monitoring.setVisibility(View.VISIBLE);
            tv_heart_count.setVisibility(View.INVISIBLE);
            tv_heartrate_unit.setVisibility(View.INVISIBLE);
        } else {
            ly_heart_monitor.setBackground(getDrawable(R.drawable.btn_jiance_nor));
            iv_heart_monitor.setImageResource(R.drawable.icon_jiance_nor);
            tv_heart_monitor_str.setTextColor(getResources().getColor(R.color.health_monitor_str_color));
            tv_heart_monitoring.setVisibility(View.INVISIBLE);
            tv_heart_count.setVisibility(View.VISIBLE);
            tv_heartrate_unit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param status 0 监测中 1 监测数据显示
     */
    public void updateOxygenMonitorStatus(int status) {
        if (status == 0) {
            ly_oxy_monitor.setBackground(getDrawable(R.drawable.btn_jiance_disabled));
            iv_oxy_monitor.setImageResource(R.drawable.icon_jiance_disabled);
            tv_oxy_monitor_str.setTextColor(getResources().getColor(R.color.health_monitor_str_dis_color));
            tv_oxy_monitoring.setVisibility(View.VISIBLE);
            tv_oxy_count.setVisibility(View.INVISIBLE);
            tv_oxy_unit.setVisibility(View.INVISIBLE);
        } else {
            ly_oxy_monitor.setBackground(getDrawable(R.drawable.btn_jiance_nor));
            iv_oxy_monitor.setImageResource(R.drawable.icon_jiance_nor);
            tv_oxy_monitor_str.setTextColor(getResources().getColor(R.color.health_monitor_str_color));
            tv_oxy_monitoring.setVisibility(View.INVISIBLE);
            tv_oxy_count.setVisibility(View.VISIBLE);
            tv_oxy_unit.setVisibility(View.VISIBLE);
        }
    }

    private String formatKilometers(double s) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(s);
    }

    private String formatCaloris(double s) {
        DecimalFormat format = new DecimalFormat("0");
        return format.format(s);
    }
}
