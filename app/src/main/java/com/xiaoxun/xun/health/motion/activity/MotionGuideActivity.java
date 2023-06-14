package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.motion.utils.MotionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionGuideActivity extends NormalActivity {

    private boolean isHasMotionSchedule = true;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }

    @OnClick(R.id.layout_next_step)
    public void onClickNextStep(){
//        startActivity(new Intent(this, MotionScheduleSettingActivity.class));
        Intent _intent = new Intent(this, MotionHealthBodyActivity.class);
        _intent.putExtra(MotionUtils.MOTIONBODYPARAMSCOMETOSET,true);
        _intent.putExtra(MotionUtils.MOTIONBODYPARAMSTYPE, MotionUtils.MOTION_BODY_HEIGHT);
        startActivity(_intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_guide);

        ButterKnife.bind(this);
        mTvTitle.setText(R.string.motion_title);
    }
}