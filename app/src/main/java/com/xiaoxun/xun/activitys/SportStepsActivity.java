package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.OVERSEAURL;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MyHandler;
import com.xiaoxun.xun.utils.Sport2Utils;
import com.xiaoxun.xun.utils.StepsUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.alipayLoginUtil.NetworkRequestUtils;
import com.xiaoxun.xun.views.RoundProgressBar;
import com.xiaoxun.xun.views.view_item_doc;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportStepsActivity extends NormalActivity {

    private BarChart barChart;
    private BarChart mChart;
    private view_item_doc mViewKilo;
    private view_item_doc mViewCalo;
    private TextView mCurSteps;
    private TextView mTargetSteps;
    private TextView mHintDate;
    private RoundProgressBar mCurStepProgress;

    private WatchData mCurWatch;
    private String mStartDate;
    private String mEndDate;

    private String mMonthJsonData;
    private List<Map<String,String>> mSportCurDaysData;  //24小时的计步数据
    private Map<String, String> mSportKiloCalData;

    private MyHandler myHandler;
    private String mdaysDate;
    private String mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_steps);

        initManager();
        initData();
        initView();
        initBarChart();
        getSportStepTempData(mStartDate,mEndDate,0);
        getSportStepTempData(mEndDate,mEndDate,1);
    }

    private void getSportStepTempData(final String startDate, String endDate, final int type) {
        if(type == 1){
            mdaysDate = endDate;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eid", mCurWatch.getEid());
        jsonObject.put("stime", startDate);
        jsonObject.put("etime", endDate);
        jsonObject.put("type", type);
        String data = jsonObject.toJSONString();
        NetworkRequestUtils.getInstance(this).getNetWorkRequest(data,
                myApp.getNetService().getAESKEY(), myApp.getToken(),
                XunKidsDomain.getInstance(myApp).getXunKidsStepsDomain(OVERSEAURL.SPORT_DAY_STEPS), new NetworkRequestUtils.OperationCallback() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("result:"+result);
                        //1:数据解析
                        //2:数据绑定视图
                        if(type == 0) {
                            parseMonthDataByResult(result);
                            myHandler.sendEmptyMessage(MyHandler.CHANGE_VIEW_FLAG);
                        }else{
                            parseDataByDayStepsResult(result,startDate);
                            myHandler.sendEmptyMessage(MyHandler.CHANGE_ITEM_VIEW_FLAG);
                        }

                    }

                    @Override
                    public void onFail(String error) {
                        LogUtil.e("result:"+error);
                    }
                });
    }
    private void updateDetailChartView(){
        int curStep = Sport2Utils.setBarChartLoadData(this,mSportCurDaysData,mChart);
        mCurSteps.setText(curStep+"");
        mTargetSteps.setText(getString(R.string.steps_target_step, mTarget));
        mCurStepProgress.setMax(Integer.valueOf(mTarget));
        if(curStep > 0)
        mCurStepProgress.setProgress(curStep);
        mHintDate.setText(TimeUtil.getMonthDay(mdaysDate));

        mViewKilo.setHint1AndHint2(getString(R.string.ranks_kilo),
                StepsUtil.formatKiloByMeter(StepsUtil.calcMeterBySteps(myApp, String.valueOf(curStep))
                        ,getString(R.string.unit_kilometer),getString(R.string.unit_meter)));
        mViewCalo.setHint1AndHint2(getString(R.string.sport_steps_detail_hint2),
                getString(R.string.unit_kiloCard_with_num,Integer.toString(StepsUtil.calcCalBySteps(myApp,String.valueOf(curStep)))));

        if(null != mSportKiloCalData.get(Constants.SPORT_TOTAL_METER)
            && !"".equals(mSportKiloCalData.get(Constants.SPORT_TOTAL_METER))) {
            String total_meter = mSportKiloCalData.get(Constants.SPORT_TOTAL_METER) + getString(R.string.unit_kilometer);
            mViewKilo.setHint1AndHint2(getString(R.string.ranks_kilo), total_meter);
        }
        if(null != mSportKiloCalData.get(Constants.SPORT_TOTAL_CALOR)
                && !"".equals(mSportKiloCalData.get(Constants.SPORT_TOTAL_CALOR))) {
            String total_calor = mSportKiloCalData.get(Constants.SPORT_TOTAL_CALOR) + getString(R.string.steps_unit_kiloCard);
            mViewCalo.setHint1AndHint2(getString(R.string.sport_steps_detail_hint2), total_calor);
        }
    }

    private void updateViewByResult() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        final String[] array = Sport2Utils.parseXValsPageByLamin(mMonthJsonData, Constants.SPORT_DAYS_DATA_KEY);
        barEntries.addAll(Sport2Utils.parseYValsBarEntryMDataExt(mMonthJsonData));
        Sport2Utils.setBarchartZoomCount(barChart,array.length,10);
        Sport2Utils.setBarChartValue(getString(R.string.sport_step_name),array,barEntries,barChart);
        Sport2Utils.setChartViewMoveClick(barChart, array.length - 1, new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //更新该天的当日24小时数据
                if(entry.getVal() > 0 ) {
                    String date = (String) entry.getData();
                    getSportStepTempData(date, date, 1);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void parseMonthDataByResult(String result) {
        if(Sport2Utils.getIntFromResult(result,"code") == 0){
            mMonthJsonData = Sport2Utils.parseSportMonthListByResult(result);
        }
    }

    private void parseDataByDayStepsResult(String result,String mDate) {
        if(Sport2Utils.getIntFromResult(result,"code") == 0){
            String datajs = Sport2Utils.getStringFromResult(result,"datajs");
            String daysData = Sport2Utils.getStringFromResult(datajs,mDate);
            String dataList = Sport2Utils.getStringFromResult(daysData,"datalist");
            mSportKiloCalData.put(Constants.SPORT_TOTAL_CALOR,Sport2Utils.getStringFromResult(daysData, Constants.SPORT_TOTAL_CALOR));
            mSportKiloCalData.put(Constants.SPORT_TOTAL_METER,Sport2Utils.getStringFromResult(daysData, Constants.SPORT_TOTAL_METER));

            Sport2Utils.parseCurDaysData(mSportCurDaysData, dataList.toString());
        }
    }

    private void initBarChart() {
        Sport2Utils.initChartParms(barChart);
        Sport2Utils.initChartParms(mChart);

        myHandler.sendEmptyMessage(MyHandler.CHANGE_ITEM_VIEW_FLAG);
        myHandler.sendEmptyMessage(MyHandler.CHANGE_VIEW_FLAG);
    }

    private void initManager() {
        String mWatchData = getIntent().getStringExtra(Constants.WATCH_EID_DATA);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchData);
        myHandler = new MyHandler(this, new InterfacesUtil.UpdateView() {
            @Override
            public void UpdateView(int position) {
                if(position == MyHandler.CHANGE_VIEW_FLAG) {
                    updateViewByResult();
                }else{
                    updateDetailChartView();
                }
            }
        });
    }

    private void initData() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mStartDate = TimeUtil.getNormalDateByYMD(year,month,day);
        mEndDate = TimeUtil.getDay();
        mdaysDate = mEndDate;
        mTarget = getIntent().getStringExtra(CloudBridgeUtil.STEPS_TARGET_LEVEL);

        mSportCurDaysData = new ArrayList<>();
        mSportKiloCalData = new HashMap<>();
        mSportCurDaysData.addAll(Sport2Utils.initSportListByDefaultData());
        mMonthJsonData = Sport2Utils.initSportMonthListByDefaultData();
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.sport_step_name);
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        barChart = findViewById(R.id.chart1);
        mChart = findViewById(R.id.chart2);

        mViewKilo = findViewById(R.id.view_sport_kilo);
        mViewCalo = findViewById(R.id.view_sport_calo);

        mCurSteps = findViewById(R.id.sport_cur_step);
        mTargetSteps = findViewById(R.id.sport_target);
        mHintDate = findViewById(R.id.tv_detail_date);

        mViewKilo.setHint1AndHint2(getString(R.string.ranks_kilo),getString(R.string.unit_meter_with_number,"0"));
        mViewCalo.setHint1AndHint2(getString(R.string.sport_steps_detail_hint2),getString(R.string.unit_kiloCard_with_num,"0"));

        mCurStepProgress = findViewById(R.id.round_progressbar_value);
        findViewById(R.id.iv_title_menu).setVisibility(View.GONE);
        findViewById(R.id.iv_title_menu).setBackgroundResource(R.drawable.share);
    }
}
