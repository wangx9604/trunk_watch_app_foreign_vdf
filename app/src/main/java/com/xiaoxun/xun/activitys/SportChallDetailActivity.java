package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;

public class SportChallDetailActivity extends NormalActivity {

    private ConstraintLayout mLayoutAllView;

    private TextView tv_share;
    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_desc;

    private String mName;
    private String mCopyText;
    private String mBIcon;
    private String mGIcon;
    private String mIsAct;
    private String mActRa;
    private String mBigIcon;

    private String mChallDesc;
    private String mBabyName;
    private WatchData mCurWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_chall_detail);

        initAppManage();
        initChallData();
        initView();
        initListener();
        updateChallView();
    }

    private void initAppManage() {
        String mWatchData = getIntent().getStringExtra(Constants.WATCH_EID_DATA);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchData);
        mBabyName = mCurWatch.getNickname();
    }

    private void updateChallView() {
        String iconUrl = mBigIcon;
        Glide.with(this)
                .load(iconUrl)
                .into(iv_icon);

        tv_share.setVisibility(View.GONE);
        if(mIsAct.equals("1")) {
            mChallDesc = getString(R.string.sport_chall_detail_desc0, mBabyName, mName, mActRa);
        }else{
            mChallDesc = getString(R.string.sport_chall_detail_desc1, mActRa);
        }

        tv_name.setText(mName);
        tv_desc.setText(mChallDesc);
    }

    private void initChallData() {
        mName = getIntent().getStringExtra(Constants.SPORT_CHALL_NAME);
        mCopyText = getIntent().getStringExtra(Constants.SPORT_CHALL_COPYTEXT);
        mBIcon = getIntent().getStringExtra(Constants.SPORT_CHALL_BRIGHTICON);
        mGIcon = getIntent().getStringExtra(Constants.SPORT_CHALL_GLOOMYICON);
        mIsAct = getIntent().getStringExtra(Constants.SPORT_CHALL_ISACTIVED);
        mActRa = getIntent().getStringExtra(Constants.SPORT_CHALL_ACTIVERATIO);
        mBigIcon = getIntent().getStringExtra(Constants.SPORT_CHALL_BIGDETAILSICON);

    }

    private void initListener() {
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.sport_chall_title);
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void initView() {
        mLayoutAllView = findViewById(R.id.layout_all_view);
        tv_share = findViewById(R.id.tv_chall_shared);
        iv_icon = findViewById(R.id.iv_chall_icon);
        tv_name = findViewById(R.id.tv_chall_name);
        tv_desc = findViewById(R.id.tv_chall_desc);
    }

}
