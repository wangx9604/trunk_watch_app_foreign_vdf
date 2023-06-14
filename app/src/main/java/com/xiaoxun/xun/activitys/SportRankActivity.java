package com.xiaoxun.xun.activitys;

import android.graphics.PixelFormat;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.PageAdapter;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.fragment.SportRankFragment;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.Sport2Utils;
import com.xiaoxun.xun.utils.StepsUtil;
import com.xiaoxun.xun.views.view_ranks_hints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SportRankActivity extends AppCompatActivity {

    private ViewPager mViewpageRank;
    private PageAdapter mPageAdapter;
    private RelativeLayout mLayoutAllView;

    //页面指示器
    private TextView tv_title;
    private TextView tv_head_hint;
    private ImageView iv_left;
    private ImageView iv_mid;
    private ImageView iv_rig;

    private TextView tv_head_name;
    private ImageView iv_head_icon;

    private view_ranks_hints view_argv_step;
    private view_ranks_hints view_argv_kilo;
    private view_ranks_hints view_argv_calo;

    private ImibabyApp myApp;
    private String mRankData;
    private List<Map<String,String>> mRankList;
    private String mBabyName;
    private WatchData mCurWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_rank);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Sport2Utils.initStateTitle(SportRankActivity.this);
        initAppManage();
        initData();
        initAdapter();
        initView();
        initListener();
    }

    private void initAppManage() {
        myApp  = (ImibabyApp) getApplication();
        String mWatchData = getIntent().getStringExtra(Constants.WATCH_EID_DATA);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchData);
        mRankData = getIntent().getStringExtra(Constants.SPORT_LAST_RANK);
        mBabyName = mCurWatch.getNickname();
    }

    private void initListener() {

        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.iv_title_menu).setVisibility(View.GONE);
        findViewById(R.id.iv_title_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mViewpageRank.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                updatePageChangeView(i);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void updatePageChangeView(int i) {
        //更新指示器
        Sport2Utils.updateIndicatorView(i,iv_left,iv_mid,iv_rig);
        //更新页面下方的详细数据
        updateDetailView(i);
    }

    private void updateDetailView(int index) {
        String seftSteps;
        String averSteps;
        double averWeight;
        double averHeight;

        if(index == 0){
            seftSteps = mRankList.get(onTranFixedPos("1", mRankList)).get(Constants.SPORT_RANK_SELFSTEPS);
            averSteps = mRankList.get(onTranFixedPos("1", mRankList)).get(Constants.SPORT_RANK_AVERAGESTEPS);
            String averTmp = mRankList.get(onTranFixedPos("1", mRankList)).get(Constants.SPORT_RANK_WEIGHT);
            averWeight = Double.parseDouble(averTmp);
            averTmp = mRankList.get(onTranFixedPos("1", mRankList)).get(Constants.SPORT_RANK_HEIGHT);
            averHeight = Double.parseDouble(averTmp);
        }else {
            seftSteps = mRankList.get(onTranFixedPos("2", mRankList)).get(Constants.SPORT_RANK_SELFSTEPS);
            averSteps = mRankList.get(onTranFixedPos("2", mRankList)).get(Constants.SPORT_RANK_AVERAGESTEPS);
            String averTmp = mRankList.get(onTranFixedPos("2", mRankList)).get(Constants.SPORT_RANK_WEIGHT);
            averWeight = Double.parseDouble(averTmp);
            averTmp = mRankList.get(onTranFixedPos("2", mRankList)).get(Constants.SPORT_RANK_HEIGHT);
            averHeight = Double.parseDouble(averTmp);
        }
        view_argv_step.setHint1AndHint2(R.drawable.sport_steps,getString(R.string.unit_steps_with_number,seftSteps),
                getString(R.string.unit_steps_with_number,averSteps));
        view_argv_kilo.setHint1AndHint2(R.drawable.sport_dist, StepsUtil.formatKiloByMeter(StepsUtil.calcMeterBySteps(myApp, String.valueOf(
                seftSteps)),getString(R.string.unit_kilometer),getString(R.string.unit_meter)),
                StepsUtil.formatKiloByMeter(StepsUtil.calcRankMeterBySteps(averHeight, String.valueOf(
                        averSteps)),getString(R.string.unit_kilometer),getString(R.string.unit_meter)));
        view_argv_calo.setHint1AndHint2(R.drawable.sport_cal,
                getString(R.string.unit_kiloCard_with_num,Integer.toString(StepsUtil.calcCalBySteps(myApp,String.valueOf(
                        seftSteps)))),
                getString(R.string.unit_kiloCard_with_num,Integer.toString(
                        StepsUtil.calcRankCalBySteps(averHeight, averWeight,String.valueOf(
                                averSteps)))));


        String parms = Sport2Utils.updateTitleAndHint(this,tv_title, index);
        if(Integer.valueOf(seftSteps) >= Integer.valueOf(averSteps)){
            tv_head_hint.setText(getString(R.string.sport_rank_head_hint0, mBabyName, parms));
        }else{
            tv_head_hint.setText(getString(R.string.sport_rank_head_hint1, mBabyName, parms));
        }


        view_argv_step.invalidate();
        view_argv_kilo.invalidate();
        view_argv_calo.invalidate();
    }

    private int onTranFixedPos(String mSportType,List<Map<String,String>> mRankList){
        int mRetPos = 0;
        for (Map<String, String> stringStringMap : mRankList) {
            if(stringStringMap.get(Constants.SPORT_RANK_TYPE).equals(mSportType)){
                return mRetPos;
            }
            mRetPos++;
        }
        return mRetPos;
    }

    private void initAdapter() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        SportRankFragment fragment = SportRankFragment.newInstance(mRankList.get(
                onTranFixedPos("1", mRankList)
                ).get(Constants.SPORT_RANK_LAMINATION),
                mRankList.get(
                        onTranFixedPos("1", mRankList)
                ).get(Constants.SPORT_RANK_TYPE),
                mRankList.get(
                        onTranFixedPos("1", mRankList)
                ).get(Constants.SPORT_RANK_RANGERANK));
        fragments.add(fragment);
        fragment = SportRankFragment.newInstance(mRankList.get(
                   onTranFixedPos("2", mRankList)
                    ).get(Constants.SPORT_RANK_LAMINATION),
                mRankList.get(
                    onTranFixedPos("2", mRankList)
                ).get(Constants.SPORT_RANK_TYPE),
                mRankList.get(
                    onTranFixedPos("2", mRankList)
                ).get(Constants.SPORT_RANK_RANGERANK));
        fragments.add(fragment);
//        fragment = SportRankFragment.newInstance(mRankList.get(2).get(Constants.SPORT_RANK_LAMINATION),
//                mRankList.get(2).get(Constants.SPORT_RANK_TYPE));
//        fragments.add(fragment);

        mPageAdapter = new PageAdapter(this, getSupportFragmentManager(),fragments);
    }

    private void initView() {
        mLayoutAllView = findViewById(R.id.layout_all_view);
        mViewpageRank = findViewById(R.id.viewpage_rank);
        mViewpageRank.setAdapter(mPageAdapter);
        mViewpageRank.setCurrentItem(0);

        tv_title = findViewById(R.id.tv_title);
        tv_head_hint = findViewById(R.id.tv_rank_hint);
        iv_left = findViewById(R.id.iv_index_left);
        iv_mid = findViewById(R.id.iv_index_mid);
        iv_rig = findViewById(R.id.iv_index_right);

        tv_head_name = findViewById(R.id.tv_detail_hint1);
        iv_head_icon = findViewById(R.id.iv_head_baby);

        view_argv_step = findViewById(R.id.cus_ranks_steps);
        view_argv_kilo = findViewById(R.id.cus_ranks_kilo);
        view_argv_calo = findViewById(R.id.cus_ranks_calo);

        //更新固定的数据视图
        tv_head_name.setText(mBabyName);
        ImageUtil.setMaskImage(iv_head_icon, R.drawable.share_head_portrait_mask, myApp.getHeadDrawableByFile(getResources(),
                mCurWatch.getHeadPath(), mCurWatch.getEid(),
                R.drawable.share_head_portrait));
        tv_title.setText(R.string.sport_step_rank);
        findViewById(R.id.iv_title_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_title_menu).setBackgroundResource(R.drawable.share);
        iv_rig.setVisibility(View.GONE);
    }

    private void initData() {
        mRankList = new ArrayList<>();
        mRankList.addAll(Sport2Utils.parseRankDateByResult(mRankData));
    }

}
