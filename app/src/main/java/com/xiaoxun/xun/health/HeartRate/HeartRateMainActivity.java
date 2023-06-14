package com.xiaoxun.xun.health.HeartRate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.calendar.DatePoint;
import com.xiaoxun.xun.health.HeartRate.Fragments.FragmentsManager;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.PersonalAuth;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.views.NoScrollView;

import net.minidev.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class HeartRateMainActivity extends NormalAppCompatActivity implements MsgCallback, OnChartGestureListener, OnChartValueSelectedListener {

    TabLayout time_mode_tab;
    NoScrollView vp_content;

    ImibabyApp mApp;
    FragmentsManager fragmentsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_main);
        mApp = (ImibabyApp)getApplication();
        fragmentsManager = new FragmentsManager();
        initViews();
        //initChart();
        //test();
        //1：清理其他地方调用日期控件残留数据
        DatePoint.clearDatePointLists();

        PersonalAuth.getAuthResultFromCloud(mApp.getCurUser().getFocusWatch().getEid(), Constants.KEY_NAME_AUTHORISE_HEARTRATE, mApp, new PersonalAuth.AuthResultListener() {
            @Override
            public void onResult(int code, String msg) {
                if(!PersonalAuth.isHeartRateAuthorised(mApp,mApp.getCurUser().getFocusWatch().getEid())){
                    if(mApp.isMeAdmin(mApp.getCurUser().getFocusWatch())){
                        PersonalAuth.showHeartRateAuthorisedDialog(HeartRateMainActivity.this, mApp
                                , mApp.getCurUser().getEid(), mApp.getCurUser().getFocusWatch().getEid(), new PersonalAuth.DialogClickListener() {
                            @Override
                            public void onClick(int status) {
                                if(status == 1){
                                    sendUploadMsgToWatch();
                                }else{
                                    finish();
                                }
                            }
                        });
                    }else{
                        PersonalAuth.showHeartRateAuthorisedNotAdminDialog(HeartRateMainActivity.this,mApp);
                    }
                }else{
                    sendUploadMsgToWatch();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出界面清除缓存
        HeartRateModel.getInstance(this).clearLocalData();
    }

    private void initViews() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.heart_rate_title));
        ImageView iv = findViewById(R.id.iv_title_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView iv_title_menu = findViewById(R.id.iv_title_menu);
        iv_title_menu.setBackgroundResource(R.drawable.heart_rate_setting);
        iv_title_menu.setVisibility(View.VISIBLE);
        iv_title_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HeartRateMainActivity.this, HeartRateSettingActivity.class));
            }
        });
        vp_content = findViewById(R.id.vp_content);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager());
        vp_content.setAdapter(vpAdapter);
        vp_content.setOffscreenPageLimit(fragmentsManager.getFragmentsSize());

        time_mode_tab = findViewById(R.id.time_mode_tab);
        for (int i = 0; i < fragmentsManager.getFragmentsSize(); i++) {
            time_mode_tab.addTab(time_mode_tab.newTab());
        }
        time_mode_tab.setSelectedTabIndicatorColor(getResources().getColor(R.color.bg_color_orange));
        time_mode_tab.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.bg_color_orange));
        time_mode_tab.setupWithViewPager(vp_content);
        time_mode_tab.getTabAt(0).setText(getString(R.string.day));
        time_mode_tab.getTabAt(1).setText(getString(R.string.week));
        time_mode_tab.getTabAt(2).setText(getString(R.string.month));
        time_mode_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    vp_content.setCurrentItem(0);
                } else if (tab.getPosition() == 1) {
                    vp_content.setCurrentItem(1);
                } else {
                    vp_content.setCurrentItem(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class VPAdapter extends FragmentPagerAdapter {

        public VPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentsManager.getFragment(i);
        }

        @Override
        public int getCount() {
            return fragmentsManager.getFragmentsSize();
        }

    }

    LineChart chart;

    private void initChart() {
        chart = findViewById(R.id.chart);
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        //chart.setScaleMinima(100f,100f);
        chart.setVisibleYRange(100f, YAxis.AxisDependency.LEFT);
        chart.setPinchZoom(true);
        chart.animateX(1500);

        XAxis xAxis;
        xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        List<String> labs = new ArrayList<>();
//        for(int i=0;i<100;i++){
//            labs.add("No." + i);
//        }
//        xAxis.setValues(labs);

        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        //yAxis.setSpaceBottom(0);
        yAxis.setAxisMaxValue(100f);
        yAxis.setAxisMinValue(0f);

        initData();
        initDayData();
    }

    private void initData() {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        float value;
        for (int i = 0; i < 100; i++) {
            if ((i % 3) == 0) {
                value = 60;
            } else {
                value = (float) (Math.random() * 30 + 40); //0 - 100 随机数
                float high = value + 10;
                float low = value - 10;
            }

            values.add(new Entry(value, i));
            names.add("No." + i);
        }
        LineDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);

            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "test 1");
            set1.setDrawValues(false);
            set1.setColor(getResources().getColor(R.color.transparent));
            set1.setLineWidth(1f);
            set1.setColor(Color.BLACK);

            LineData barData = new LineData(names, set1);
            chart.setData(barData);
//            String[] test = TimeUtil.getFirstDayAndLastDayOfMonthByDate("79789598");
//            LogUtil.e("xxxx firstDay : " + test[0] + " | lastDay : " + test[1]);
        }
    }

    private void initDayData() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                String timestamp = TimeUtil.getDay();
//                DataStruct.DayData result = model.getDayDataByDate(timestamp);
                String[] result = TimeUtil.getMonAndSunbyReserveDate("79789598");
                e.onNext(result[1]);
            }
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e("xxxx onSubscribe");
            }

            @Override
            public void onNext(String s) {
                LogUtil.e("xxxx onNext " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);
    }


    public void sendUploadMsgToWatch() {
        MyMsgData msgData = new MyMsgData();
        msgData.setNeedNetTimeout(true);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_HEART_RATE_UPLOAD_MESSAGE);
        if (getMyApp() != null && getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendE2EMsg(getMyApp().getCurUser().getFocusWatch().getEid(), sn, pl, 20 * 1000, true, this);
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_E2E_DOWN:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    int subAction = (int)pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    if(subAction == CloudBridgeUtil.SUB_ACTION_HEART_RATE_UPLOAD_MESSAGE) {
                        fragmentsManager.refreshDayData();
                    }
                } else {
                    LogUtil.e("HeartRateMainActivity e2e failed.cause : " + rc);
                }
                break;
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }
}