package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.motion.views.TriangleIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionHealthEvaluActivity extends NormalActivity {

    @BindView(R.id.tv_title_2)
    public TextView mTvBmiValue;
    @BindView(R.id.tv_title_3)
    public TextView mTvBmiName;
    @BindView(R.id.triangleIndicator)
    public TriangleIndicatorView mIndicator;
    @BindView(R.id.tv_next)
    public TextView mTvNext;

    private boolean isComeToSetting;
    private String mHeightSetting;
    private String mWeightSetting;
    private ImibabyApp myApp;
    private WatchData mCurData;
    private int mBmiType;

    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }

    @OnClick(R.id.tv_next)
    public void onNextAction(){
        if(isComeToSetting){
            //设置手表的身高体重，进入下一步
            Intent _intent = new Intent(this, MotionTransferActivity.class);
            _intent.putExtra(Constants.BMI_SUGGEST, mBmiType);
            startActivity(_intent);
            finish();
        }else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_health_evalu);
        ButterKnife.bind(this);

        initManageData();
        initView();
    }

    private void initView() {
        float mHeightValue = Float.valueOf(mHeightSetting);
        float mWeightValue = Float.valueOf(mWeightSetting);
        float mBmiValue = mWeightValue/(float)Math.pow(mHeightValue/100,2);

        mTvBmiValue.setText(String.format("%.1f", mBmiValue));
        mTvBmiName.setText(MotionUtils.getBmiNameByValue(this, mBmiValue));
        mIndicator.setmBmiValue(mBmiValue);
        mBmiType = MotionUtils.getBmiTypeByValue(mBmiValue);

        if(isComeToSetting) {
            mTvNext.setText(R.string.health_evalu_hint_0);
        }else{
            mTvNext.setText(R.string.security_finish_next);
        }
    }

    private void initManageData() {
        myApp = getMyApp();
        mCurData = myApp.getCurUser().getFocusWatch();
        isComeToSetting = getIntent().getBooleanExtra(MotionUtils.MOTIONBODYPARAMSCOMETOSET,false);
        mHeightSetting = getIntent().getStringExtra(MotionUtils.MOTION_BODY_HEIGHT);
        mWeightSetting = getIntent().getStringExtra(MotionUtils.MOTION_BODY_WEIGHT);
    }
}