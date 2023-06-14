package com.xiaoxun.xun.health.motion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionHealthBodyActivity extends NormalActivity {

    private boolean isComeToSetting = false;
    private String mBodyParams = MotionUtils.MOTION_BODY_HEIGHT;
    private WatchData mCurData;
    private ImibabyApp myApp;
    private String mParams;
    private String mParams1;

    @BindView(R.id.tv_title_0)
    public TextView mTvTitle0;
    @BindView(R.id.tv_title_1)
    public TextView mTvTitle1;
    @BindView(R.id.tv_title)
    public TextView mTvTitle;
    @BindView(R.id.tv_unit_cm)
    public TextView mTvUnitCm;
    @BindView(R.id.tv_unit_point)
    public TextView mTvUnitPoint;
    @BindView(R.id.tv_unit_kg)
    public TextView mTvUnitKG;
    @BindView(R.id.moring_hour_pv)
    public CustomerPickerView mPickerView0;
    @BindView(R.id.moring_mins_pv)
    public CustomerPickerView mPickerView1;
    private String mSetHeight;

    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }
    @OnClick(R.id.tv_next)
    public void onCkeckToNext(){
        //1:设置身高、体重
        if(mBodyParams.equals(MotionUtils.MOTION_BODY_HEIGHT)){
            LogUtil.e("motionHealthBody0"+":"+mParams);
        }else{
            LogUtil.e("motionHealthBody1"+":"+mParams+":"+mParams1);
        }

        //2:进入到下一步
        Intent _intent;
        if(mBodyParams.equals(MotionUtils.MOTION_BODY_HEIGHT)){
            _intent = new Intent(this, MotionHealthBodyActivity.class);
            _intent.putExtra(MotionUtils.MOTIONBODYPARAMSCOMETOSET,isComeToSetting);
            _intent.putExtra(MotionUtils.MOTIONBODYPARAMSTYPE, MotionUtils.MOTION_BODY_WEIGHT);
            _intent.putExtra(MotionUtils.MOTION_BODY_HEIGHT, mParams);
        }else{
            _intent = new Intent(this, MotionHealthEvaluActivity.class);
            _intent.putExtra(MotionUtils.MOTIONBODYPARAMSCOMETOSET,isComeToSetting);
            _intent.putExtra(MotionUtils.MOTION_BODY_HEIGHT, mSetHeight);
            _intent.putExtra(MotionUtils.MOTION_BODY_WEIGHT, mParams+"."+mParams1);
        }
        startActivity(_intent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_health_body);
        //1:页面基本信息
        ButterKnife.bind(this);
        initManageData();
        initView();
        //2：检查授权信息
//        if(!PersonalAuth.isPersonalInfoAuthorised(myApp,myApp.getCurUser().getFocusWatch().getEid())) {
//            PersonalAuth.showAuthorisedRequestDialog(this, myApp, myApp.getCurUser().getEid(),
//                    myApp.getCurUser().getFocusWatch().getEid(), Constants.KEY_NAME_AUTHORISE_PRIVACY,
//                    getString(R.string.perspnal_authorise_info_title),
//                    getString(R.string.personal_authorise_info_content_next, getString(R.string.app_name)),
//                    new PersonalAuth.DialogClickListener() {
//                        @Override
//                        public void onClick(int status) {
//                            if (status == -1) return;
//                            if (status == 0) {
//                                finish();
//                            }
//                        }
//                    });
//        }

    }

    private void initManageData() {
        myApp = getMyApp();
        mCurData = myApp.getCurUser().getFocusWatch();
        isComeToSetting = getIntent().getBooleanExtra(MotionUtils.MOTIONBODYPARAMSCOMETOSET,false);
        mBodyParams = getIntent().getStringExtra(MotionUtils.MOTIONBODYPARAMSTYPE);
        if(mBodyParams.equals(MotionUtils.MOTION_BODY_HEIGHT)){
            mParams = String.valueOf(mCurData.getHeight()<60?110:mCurData.getHeight());
        }else{
            mSetHeight = getIntent().getStringExtra(MotionUtils.MOTION_BODY_HEIGHT);
            double weight = mCurData.getWeight()<8?18:mCurData.getWeight();
            mParams = String.valueOf((int)weight*10/10);
            mParams1 = String.valueOf((int)weight*10%10);
        }
    }

    private void initView() {
        mPickerView0.setmMaxTextSize(80);
        mPickerView1.setmMaxTextSize(80);

        if(mBodyParams.equals(MotionUtils.MOTION_BODY_HEIGHT)){
            mTvTitle0.setText(R.string.height);
            mTvTitle1.setText(R.string.health_body_title_0);
            mTvUnitCm.setVisibility(View.VISIBLE);
            mTvUnitPoint.setVisibility(View.GONE);
            mTvUnitKG.setVisibility(View.GONE);
            mPickerView0.setVisibility(View.VISIBLE);
            mPickerView1.setVisibility(View.GONE);

            // select height
            List<String> heights = new ArrayList<String>();
            for (int i = 60; i < 180; i++) {
                heights.add(i < 60 ? "0" + i : "" + i);
            }
            mPickerView0.setData(heights);
            mPickerView0.setOnSelectListener(new CustomerPickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    mParams = text;
                }
            });
            mPickerView0.setSelected(Double.valueOf(mParams).intValue() - 60);
        }else{
            mTvTitle0.setText(R.string.weight);
            mTvTitle1.setText(R.string.health_body_title_1);
            mTvUnitCm.setVisibility(View.GONE);
            mTvUnitPoint.setVisibility(View.VISIBLE);
            mTvUnitKG.setVisibility(View.VISIBLE);
            mPickerView0.setVisibility(View.VISIBLE);
            mPickerView1.setVisibility(View.VISIBLE);

            List<String> weights = new ArrayList<String>();
            for (int i = 8; i < 80; i++) {
                weights.add("" + i);
            }
            mPickerView0.setData(weights);
            mPickerView0.setOnSelectListener(new CustomerPickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    mParams = text;
                }
            });
            mPickerView0.setSelected(Integer.valueOf(mParams) - 8);


            List<String> weights1 = new ArrayList<String>();
            for (int i = 0; i < 10; i++) {
                weights1.add("" + i);
            }
            mPickerView1.setData(weights1);
            mPickerView1.setOnSelectListener(new CustomerPickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    mParams1 = text;
                }
            });
            mPickerView1.setSelected(Integer.valueOf(mParams1));
        }

    }
}