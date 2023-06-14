package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;

public class MotionTransferActivity extends NormalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_transfer);
        int mBmiType = getIntent().getIntExtra(Constants.BMI_SUGGEST, 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent _intent = new Intent(MotionTransferActivity.this,
                        MotionScheduleSettingActivity.class);
                _intent.putExtra(Constants.BMI_SUGGEST, mBmiType);
                startActivity(_intent);
                finish();
            }
        },3000);
        
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}