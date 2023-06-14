package com.xiaoxun.xun.health.heartratesetting;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.StatusBarUtil;

public class SettingActivity extends AppCompatActivity {

    ImibabyApp mApp;
    SettingModel settingModel;
    WatchData curWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarUtil.changeStatusBarColor(this,getResources().getColor(R.color.schedule_no_class));
        mApp = (ImibabyApp) getApplication();
        String eid = getIntent().getStringExtra("eid");
        if(eid != null){
            curWatch = mApp.getCurUser().queryWatchDataByEid(eid);
        }else{
            curWatch = mApp.getCurUser().getFocusWatch();
        }
        settingModel = new ViewModelProvider(this).get(SettingModel.class);

        initViews();
        initData();
    }

    private void initViews(){
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tv_high_limit_intro = findViewById(R.id.tv_high_limit_intro);
        ToggleButton tb_high_limit = findViewById(R.id.tb_high_limit);
        TextView tv_high_limit_num = findViewById(R.id.tv_high_limit_num);
        ToggleButton tb_rest_limit = findViewById(R.id.tb_rest_limit);
        TextView tv_rest_limit_num = findViewById(R.id.tv_rest_limit_num);
        ToggleButton tb_rest_low_limit = findViewById(R.id.tb_rest_low_limit);
        TextView tv_rest_low_limit_num = findViewById(R.id.tv_rest_low_limit_num);
        Group group_high_limit = findViewById(R.id.group_high_limit);
        Group group_rest_limit = findViewById(R.id.group_rest_limit);
        Group group_rest_low_limit = findViewById(R.id.group_rest_low_limit);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int[] hlIds = group_high_limit.getReferencedIds();
        for(int id : hlIds){
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tb_high_limit.isChecked()) {
                        showHighLimitDlg();
                    }
                }
            });
        }

        int[] rhlIds = group_rest_limit.getReferencedIds();
        for(int id : rhlIds){
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tb_rest_limit.isChecked()) {
                        showRestLimitDlg();
                    }
                }
            });
        }

        int[] rllIds = group_rest_low_limit.getReferencedIds();
        for(int id : rllIds){
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tb_rest_low_limit.isChecked()) {
                        showRestLowLimitDlg();
                    }
                }
            });
        }

        tb_high_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingModel.getHl_status() == 0){
                    return;
                }
            }
        });
        tb_high_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    settingModel.mapSetHLOnoff(mApp,isChecked?1:0,curWatch);
                }
            }
        });
        tb_rest_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingModel.getRhl_status() == 0){
                    return;
                }
            }
        });
        tb_rest_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    settingModel.mapSetRHLOnoff(mApp,isChecked?1:0,curWatch);
                }
            }
        });
        tb_rest_low_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingModel.getRll_status() == 0){
                    return;
                }
            }
        });
        tb_rest_low_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    settingModel.mapSetRLLOnoff(mApp,isChecked?1:0,curWatch);
                }
            }
        });

        settingModel.getHigh_limit().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_high_limit_intro.setText(getString(R.string.health_heartrate_setting_high_limit_intro,integer));
                    tv_high_limit_num.setText(getString(R.string.heart_rate_warning_value_format,String.valueOf(integer)));
                }
            }
        });
        settingModel.getSwitch_high_limit().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null){
                    tb_high_limit.setChecked(aBoolean);
                }
            }
        });

        settingModel.getRest_high_limit().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_rest_limit_num.setText(getString(R.string.heart_rate_warning_value_format,String.valueOf(integer)));
                }
            }
        });
        settingModel.getSwithc_rest_high_limit().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null){
                    tb_rest_limit.setChecked(aBoolean);
                }
            }
        });
        settingModel.getRest_low_limit().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_rest_low_limit_num.setText(getString(R.string.heart_rate_warning_value_format,String.valueOf(integer)));
                }
            }
        });
        settingModel.getSwitch_rest_low_limit().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null){
                    tb_rest_low_limit.setChecked(aBoolean);
                }
            }
        });
    }

    private void showHighLimitDlg(){
        int cur = 160;
        if(settingModel.getHigh_limit().getValue() != null) {
            cur = settingModel.getHigh_limit().getValue();
        }
        limitSettingDlg dlg = new limitSettingDlg(SettingActivity.this, getString(R.string.heart_rate_warning_limit), 155,195,1,String.valueOf(cur));
        dlg.setBtnClick(new limitSettingDlg.BtnClick() {
            @Override
            public void onConfirm(String time) {
                settingModel.mapSetHLData(mApp,Integer.parseInt(time),curWatch);
            }

            @Override
            public void onCancel() {

            }
        });
        dlg.show();
    }

    private void showRestLimitDlg(){
        int cur = 120;
        if(settingModel.getRest_high_limit().getValue() != null) {
            cur = settingModel.getRest_high_limit().getValue();
        }
        limitSettingDlg dlg = new limitSettingDlg(SettingActivity.this,getString(R.string.health_heartrate_setting_too_high),100,150,10,String.valueOf(cur));
        dlg.setBtnClick(new limitSettingDlg.BtnClick() {
            @Override
            public void onConfirm(String time) {
                settingModel.mapSetRHLData(mApp,Integer.parseInt(time),curWatch);
            }

            @Override
            public void onCancel() {

            }
        });
        dlg.show();
    }

    private void showRestLowLimitDlg(){
        int cur = 60;
        if(settingModel.getRest_low_limit().getValue() != null) {
            cur = settingModel.getRest_low_limit().getValue();
        }
        limitSettingDlg dlg = new limitSettingDlg(SettingActivity.this,getString(R.string.health_heartrate_setting_too_low),50,70,10,String.valueOf(cur));
        dlg.setBtnClick(new limitSettingDlg.BtnClick() {
            @Override
            public void onConfirm(String time) {
                settingModel.mapSetRLLData(mApp,Integer.parseInt(time),curWatch);
            }

            @Override
            public void onCancel() {

            }
        });
        dlg.show();
    }

    private void initData(){
        settingModel.setHigh_limit(160);
        settingModel.setRest_high_limit(120);
        settingModel.setRest_low_limit(60);
        settingModel.mapGetData(mApp,curWatch.getEid());
    }
}