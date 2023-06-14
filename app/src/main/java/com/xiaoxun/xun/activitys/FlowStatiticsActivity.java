package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.ToolUtils;
import com.xiaoxun.xun.views.CustomerPickerView;
import com.xiaoxun.xun.views.MyMarkerView;
import com.xiaoxun.xun.views.RoundProgressBar;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class FlowStatiticsActivity extends NormalActivity implements OnChartGestureListener,
        OnChartValueSelectedListener, View.OnClickListener, MsgCallback {

    private final String TAG = "FlowStatiticsActivity";
    private Typeface mTf;

    private LineChart mChart;  //折线图
    private View btnBack;
    private TextView tvCurValue;
    private TextView tvCurPercent;
    private TextView tvCurUnit;
    private RoundProgressBar roundProgressBar;
    private TextView mFlowSetting;
    private TextView tvSelectDate;

    private ImibabyApp myApp;
    private ArrayList<flowStatiticsPointDate> flowsPointList;
    private BroadcastReceiver receiver;
    private String curDate;
    private String firstDate;

    private String watchEid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_statitics);
        myApp = (ImibabyApp)getApplication();
        flowsPointList = new ArrayList<>();
        curDate = getCurDate();
        firstDate = getFirstDateFormMonth(curDate);
        watchEid = myApp.getCurUser().getFocusWatch().getEid();
        LogUtil.e("statitics:"+curDate+":"+firstDate);

        initView();
        linechart();
        getChartLocalData();
        getChartNetData();
        initBrocastReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFlowStatiticsProcessView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initBrocastReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.ACTION_BROAST_FLOW_STATITICS)) {
                    try{
                        String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
                        if(!eid.equals(myApp.getCurUser().getFocusWatch().getEid()))
                            return;
                        String flowData1 = intent.getStringExtra(Const.SHARE_PREF_FLOW_STATITICS);
                        LogUtil.e("brocast data:"+flowData1);
                        ArrayList<flowStatiticsPointDate> localPointList = new ArrayList<>();
//                        String flowData = "{\"RC\":1,\"Content\":{\"20180601\":501,\"20180605\":201}}";
                        JSONObject jsonObject = (JSONObject)JSONValue.parse(flowData1);
                        JSONObject content = (JSONObject) jsonObject.get("Content");
                        flowsPointList = parseNetFlowStatiticsData(content,firstDate,curDate,localPointList);
                        reFreshFlowStatiticsChartData(flowsPointList,true);
                        updateFlowStatiticsProcessView();
                        if(compareMonthIsCurMon(curDate.substring(0,6),new Date()) == 0){
                            sendGetE2EFlowStatiticsData();
                        }
                    }catch (Exception e){
                        LogUtil.i(e.toString());
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_BROAST_FLOW_STATITICS);
        registerReceiver(receiver, filter);
    }

    private String tranfTimeFormat(String sourceTime){
        String tranFStr = null;
        SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat formatDateKey1 = new SimpleDateFormat("yyyyMM");
        try{
            Date d = formatDateKey1.parse(sourceTime);
            tranFStr = formatDateKey.format(d);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(compareMonthIsCurMon(sourceTime,new Date()) == 0)
            tranFStr = getString(R.string.flow_statistics_cur_month);
        return tranFStr;
    }

    private String tranfTimestamp(String sourceDate){
        String tranFStr = null;
        SimpleDateFormat formatDateKey = new SimpleDateFormat("MM/dd");
        SimpleDateFormat formatDateKey1 = new SimpleDateFormat("yyyyMMdd");
        try{
            Date d = formatDateKey1.parse(sourceDate);
            tranFStr = formatDateKey.format(d);
        }catch(Exception e){
            e.printStackTrace();
        }
        return tranFStr;
    }

    private void reFreshFlowStatiticsChartData(ArrayList<flowStatiticsPointDate> pointList, boolean isAnimal){

        ArrayList<String> xVals = new ArrayList<String>();  //图表x轴的数据显示
        ArrayList<Entry>  yVals = new ArrayList<Entry>();   //图表y轴的数据显示

        for(int i=pointList.size()-1;i>=0;i--){
            flowStatiticsPointDate pointData = pointList.get(i);
            xVals.add(tranfTimestamp(pointData.flowDate));
            yVals.add(new Entry(pointData.flowPoint/1024,pointList.size()-1-i));
        }

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMinValue(0);
//        leftAxis.setLabelCount(6);

        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf(formatStringFromFloat(v));
            }
        });

//        mChart.zoom(1.1f,1.0f,0,0);

        setData(xVals, yVals, false);
        mChart.getMarkerView().findViewById(R.id.tvContent).invalidate();
        mChart.getMarkerView().invalidate();
        mChart.setViewPortOffsets(DensityUtil.dip2px(this, 35), DensityUtil.dip2px(this, 10),
                DensityUtil.dip2px(this, 33), DensityUtil.dip2px(this, 46));
        if(isAnimal) {
            mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        }else{
            mChart.animateX(0, Easing.EasingOption.EaseInOutQuart);
        }
    }

    private void linechart(){
        mChart = findViewById(R.id.chart2);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setHighlightEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setisFormatType("1");

        // set the marker to the chart
        mChart.setMarkerView(mv);

        XAxis xAxis = mChart.getXAxis();
//        xAxis.setEnabled(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.setAxisMaxValue(100);
//        leftAxis.setAxisMinValue(0f);
//        leftAxis.setLabelCount(6);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf(v);
            }
        });

        mChart.getAxisRight().setEnabled(false);
        // add data

        setData(null, null,true);
        mChart.setViewPortOffsets(DensityUtil.dip2px(this, 35), DensityUtil.dip2px(this, 10),
                DensityUtil.dip2px(this, 33), DensityUtil.dip2px(this, 46));
    }

    private void setData(ArrayList<String> xVals, ArrayList<Entry>yVals, boolean isInitFirst) {
        if(isInitFirst){
            xVals = new ArrayList<String>();
            for (int i = 0; i < 7; i++) {
                Date beforeDate = getDateBefore(new Date(), 7-i);
                SimpleDateFormat formatDateKey = new SimpleDateFormat("MM/dd",
                        Locale.CHINA);
                xVals.add(formatDateKey.format(beforeDate));
            }
            yVals = new ArrayList<Entry>();
            for (int i = 0; i < 7; i++) {
                yVals.add(new Entry(0, i));
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, getString(R.string.flow_statistics_item_info));

        set1.setColor(Color.parseColor("#f66d3e"));
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawCircles(true);
        set1.setCircleSize(3f);
        set1.setCircleColor(Color.parseColor("#f66d3e"));
        set1.setCircleColorHole(Color.parseColor("#f66d3e"));
        set1.setFillAlpha(10);
        set1.setFillColor(Color.parseColor("#f66d3e"));
        set1.setDrawFilled(true);
        set1.setDrawValues(false);
        set1.setValueTextSize(9f);
        set1.setValueTextColor(Color.parseColor("#f66d3e"));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setDrawValues(false);
        // set data
        mChart.setData(data);
    }

    public static Date getDateBefore(Date date, int n) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);

        return now.getTime();
    }

    private void getChartLocalData(){
        JSONObject content = new JSONObject();
        flowsPointList = parseNetFlowStatiticsData(content,firstDate,curDate,flowsPointList);
        reFreshFlowStatiticsChartData(flowsPointList,true);
        updateFlowStatiticsProcessView();
    }

    private void getChartNetData(){
        getFlowMeterLimit();
        sendGetHttpsFlowStatiticsData(firstDate,curDate);
    }

    private String getCurDate(){
        SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd");
        String curDate = formatDateKey.format(new Date());
        return curDate;
    }

    private String getFirstDateFormMonth(String curDate){
        String firstDate = null;
        if(curDate.length() >= 6) {
            firstDate = curDate.substring(0, 6) + "01";
        }
        return firstDate;
    }

    private int compareMonthIsCurMon(String month, Date month1){
        SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMM");
        String curDate = formatDateKey.format(month1);

        return month.compareTo(curDate);
    }

    private void sendGetE2EFlowStatiticsData(){
        if(myApp.getNetService() != null) {
            String watchEid = myApp.getCurUser().getFocusWatch().getEid();
            myApp.getNetService().sendE2EMsg(watchEid, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FLOW_STATITICS_NOTIFY,
                    30 * 1000, true, FlowStatiticsActivity.this);

        }
    }

    private void getFlowMeterLimit(){
        if(myApp.getNetService() != null) {
            String watchEid = myApp.getCurUser().getFocusWatch().getEid();
            myApp.getNetService().sendMapGetMsg(watchEid,CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,
                    this);
        }
    }

    private void sendGetHttpsFlowStatiticsData(String startDate,String endDate){
        flowStatiticsDatasUpdateTask(myApp.getToken(),myApp.getCurUser().getFocusWatch().getEid(),startDate,endDate);
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.flow_statistics_item_title);
        tvCurPercent = findViewById(R.id.flow_statistics_cur_percent);
        tvCurValue = findViewById(R.id.flow_statistics_cur_value);
        roundProgressBar = findViewById(R.id.round_progressbar_value);
        mFlowSetting = findViewById(R.id.watch_status);
        btnBack = findViewById(R.id.iv_title_back);
        tvSelectDate = findViewById(R.id.tv_select_date);
        tvCurUnit = findViewById(R.id.tv_flow_unit);

        tvSelectDate.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        mFlowSetting.setOnClickListener(this);
        roundProgressBar.setProgress(0);
        roundProgressBar.setMax(5120);
        tvSelectDate.setText(tranfTimeFormat(curDate.substring(0,6)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_select_date:
                openSelectDateDialog(curDate);
                break;
            case R.id.watch_status:
                Intent _intent = new Intent(this,FlowStatiticsSettingsActivity.class);
                String maxFlow = myApp.getStringValue(watchEid+CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,"-1");
                _intent.putExtra(CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,maxFlow);
                startActivity(_intent);
                break;
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

    private String year ;
    private String month ;
    private void openSelectDateDialog(String curTime){
        year=curTime.substring(0,4);
        month=curTime.substring(4,6);

        final Dialog dlg = new Dialog(FlowStatiticsActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) FlowStatiticsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.flow_statitics_dialog_sel_date, null);
        //init time pickviews
        CustomerPickerView pickYear = layout.findViewById(R.id.year_pv);
        pickYear.setMarginAlphaValue((float) 3.8, "H");
        CustomerPickerView pickMonth = layout.findViewById(R.id.month_pv);
        pickMonth.setMarginAlphaValue((float) 3.8, "H");

        View pickerView = layout.findViewById(R.id.birthday_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        View line_1 = layout.findViewById(R.id.line_1);
        line_1.setTranslationX(pickerView.getX() + width * 5 / 10);
        TextView tvYear = layout.findViewById(R.id.tv_year_pv);
        tvYear.setPadding(width * 1 / 4 + 110 * width / 1080, 0, 0, 0);
        tvYear.setTextColor(0xffdf5600);
        TextView tvMonth = layout.findViewById(R.id.tv_month_pv);
        tvMonth.setPadding(width * 3 / 4 + 55 * width / 1080, 0, 0, 0);
        tvMonth.setTextColor(0xffdf5600);

        List<String> years = new ArrayList<>();
        List<String> months = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year1 = c.get(Calendar.YEAR);
        for (int i = 2010; i < year1 + 1; i++) {
            years.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 1; i < 13; i++) {
            months.add(i < 10 ? "0" + i : "" + i);
        }
        pickYear.setData(years);
        pickYear.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                year = text;
            }
        });
        pickYear.setSelected(Integer.parseInt(year) - 2010);
        pickMonth.setData(months);
        pickMonth.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month = text;
            }
        });
        pickMonth.setSelected(Integer.valueOf(month) - 1);

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempBirthday = year + month;
                LogUtil.e("date:"+tempBirthday);
                if(compareMonthIsCurMon(tempBirthday,new Date()) == 0){
                    curDate = getCurDate();
                    firstDate = getFirstDateFormMonth(curDate);
                    sendGetHttpsFlowStatiticsData(firstDate, curDate);
                }else if(compareMonthIsCurMon(tempBirthday,new Date()) > 0){
                    if(tvSelectDate.getText().equals(getString(R.string.flow_statistics_cur_month))) {
                        curDate = getCurDate();
                    }else{
                        curDate = getCurDate();
                        firstDate = getFirstDateFormMonth(curDate);
                        sendGetHttpsFlowStatiticsData(firstDate, curDate);
                    }
                    tempBirthday = curDate.substring(0, 6);
                    ToastUtil.show(FlowStatiticsActivity.this,getString(R.string.flow_statistics_surpass_month));
                }else{
                    firstDate = tempBirthday+"01";
                    curDate = tempBirthday+"31";
                    sendGetHttpsFlowStatiticsData(firstDate, curDate);
                }
                tvSelectDate.setText(tranfTimeFormat(tempBirthday));
                dlg.dismiss();
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        dlg.show();
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

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid){
            case CloudBridgeUtil.CID_E2E_DOWN:
                try {
                    LogUtil.e(TAG + "FlowStatitics:" + respMsg.toJSONString());
                    ArrayList<flowStatiticsPointDate> curPointList = new ArrayList<>();
//                String retDate="{\"Version\":\"00000000\",\"SN\":718982423,\"TEID\":[\"799F0EF994B36585E8752C286F7294A7\"],\"PL\":{\"sub_action\":411,\"cur_flowmeter\":{\"20180603\":63.449,\"20180601\":56.244,\"20180611\":428.338}},\"CID\":30011,\"SID\":\"18699320A00F46ED9011F7F25091AD2E\"}";
//                JSONObject retJson = (JSONObject)JSONValue.parse(retDate);
                    JSONObject pl = (JSONObject) respMsg.get("PL");
                    JSONObject curFlow = (JSONObject) pl.get("cur_flowmeter");
                    parseCurFlowStatiticsData(curFlow, curPointList);
                    flowsPointList = compareFlowDataToArray(flowsPointList, curPointList);
                    reFreshFlowStatiticsChartData(flowsPointList, true);
                    updateFlowStatiticsProcessView();
                }catch (Exception e){
                    LogUtil.e("e2e message error:"+e.toString());
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String flow_limit = (String) pl.get(CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT);
                    if(flow_limit!=null && !flow_limit.equals("")) {
                        myApp.setValue(watchEid+CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT, flow_limit);
                        updateFlowStatiticsProcessView();
                    }
                }
                break;
        }
    }

    private float formatStringFromFloat(float source){
        return (float)(Math.round(source*100))/100;
    }

    private void updateFlowStatiticsProcessView(){
        float sumFlow = calcFlowStatiticsSum();
        String[] format = ToolUtils.formatFlowStatiticsDataInfo(this, sumFlow);
        tvCurValue.setText(format[0]);
        tvCurUnit.setText(format[1]);

        String maxFlow = myApp.getStringValue(watchEid+CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,"-1");
        if(maxFlow.equals("-1")) {
            roundProgressBar.setMax(5120);
            tvCurPercent.setText("/"+getString(R.string.flow_statistics_setting_6));
        }else {
            roundProgressBar.setMax(Integer.valueOf(maxFlow) / 1024);
            int flowLimitData = Integer.valueOf(maxFlow) / 1024;
            if(flowLimitData <= 500){
                tvCurPercent.setText(getString(R.string.flow_statistics_item_cur_percent,
                        String.valueOf(Integer.valueOf(maxFlow) / 1024))+"M");
            }else{
                tvCurPercent.setText(getString(R.string.flow_statistics_item_cur_percent,
                        String.valueOf(Integer.valueOf(maxFlow) / (1024*1024)))+"G");
            }

        }
        roundProgressBar.setProgress((int)sumFlow/1024);
    }

    private float calcFlowStatiticsSum(){
        float flowSum = 0;
        for(flowStatiticsPointDate pointDate:flowsPointList){
            flowSum+=pointDate.flowPoint;
        }

        return flowSum;
    }

    MioAsyncTask<String, Void, String> getDataTask = null;
    public void flowStatiticsDatasUpdateTask(String sid,final String eid, String startTime, String endTime) {
        if (getDataTask != null) {
            return;
        }
        getDataTask = new MioAsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                // TODO Auto-generated method stub
                try {
                    LogUtil.e(TAG+"ranks json:" + flowDatasUpdateJson(params[0], params[1], params[2], params[3])+":"+ FunctionUrl.APP_FLOW_STATITICS_URL);
//                      LogUtil.e("url:" + Const.APP_RANK_HTTPS_URL);
                    return ImibabyApp.PostJsonWithURLConnection(flowDatasUpdateJson(params[0], params[1], params[2], params[3]), FunctionUrl.APP_FLOW_STATITICS_URL, false, getAssets().open("dxclient_t.bks"));
//                     return myApp.HttpPostJson(flowDatasUpdateJson(params[0], params[1], params[2], params[3]), Const.APP_FLOW_STATITICS_HTTPS_URL, true);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                getDataTask = null;
                LogUtil.i(TAG+"ranks result" + result);

                if (result != null && result.length() > 0) {
                    int rc = 0;
                    try {
                        org.json.JSONObject updateJson = new org.json.JSONObject(result);
                        updateJson.put("TIMESTAMP", TimeUtil.getTimestampCHN());
                        rc = updateJson.getInt("RC");
                        if (rc == -11) {
                            String flowData = "{\"RC\":1,\"Content\":{}}";
                            myApp.setValue(eid + Const.SHARE_PREF_FLOW_STATITICS, updateJson.toString());
                            Intent intent = new Intent(Const.ACTION_BROAST_FLOW_STATITICS);
                            intent.putExtra(Const.KEY_WATCH_ID, eid);
                            intent.putExtra(Const.SHARE_PREF_FLOW_STATITICS, flowData);
                            sendBroadcast(intent);
                        }else if(rc < 0 ){
                            LogUtil.d( TAG+"获取信息失败，失败rc：" + rc);
                        }else {
                            //只写保存一份完全的表。使用时候再解析判断
                            LogUtil.e(TAG+"ranks updatejson:" + updateJson.toString());
                            myApp.setValue(eid + Const.SHARE_PREF_FLOW_STATITICS, updateJson.toString());
                            Intent intent = new Intent(Const.ACTION_BROAST_FLOW_STATITICS);
                            intent.putExtra(Const.KEY_WATCH_ID, eid);
                            intent.putExtra(Const.SHARE_PREF_FLOW_STATITICS, updateJson.toString());
                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {

                    }

                }
            }
        };
        getDataTask.execute(sid, eid, startTime,endTime);
    }

    public String flowDatasUpdateJson(String a1,String a2,String a3,String a4){
        JSONObject params = new JSONObject();
        params.put("RequestId", a1);
        params.put("Eid", a2);
        params.put("BeginTime", a3);
        params.put("EndTime", a4);
        return params.toJSONString();
    }

    private ArrayList<flowStatiticsPointDate> compareFlowDataToArray(ArrayList<flowStatiticsPointDate> sourFlowData,
                                                                     ArrayList<flowStatiticsPointDate> curFlowData){
       for(int i = 0;i<curFlowData.size();i++){
           flowStatiticsPointDate curPoint = curFlowData.get(i);
           for(int j=0;j<sourFlowData.size();j++){
               flowStatiticsPointDate sourPoint = sourFlowData.get(j);
               if(sourPoint.flowDate.equals(curPoint.flowDate)){
                   sourPoint.flowPoint = curPoint.flowPoint;
                   break;
               }
           }
       }

       return sourFlowData;
    }

    private ArrayList<flowStatiticsPointDate> parseCurFlowStatiticsData(JSONObject content,
                                                                        ArrayList<flowStatiticsPointDate> flowsPointList){
        Set<Map.Entry<String,Object>>   entrySet =   content.entrySet();

        for(Map.Entry<String, Object> entry:entrySet){
            flowStatiticsPointDate pointDate = new flowStatiticsPointDate();
            float flowValue = Float.valueOf(entry.getValue().toString());
            pointDate.flowPoint = flowValue;
            pointDate.flowDate = entry.getKey();
            flowsPointList.add(pointDate);
        }

        return flowsPointList;
    }

    private ArrayList<flowStatiticsPointDate> parseNetFlowStatiticsData(JSONObject content,String startTime,String endTime,
                                        ArrayList<flowStatiticsPointDate> flowsPointList){
        SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        try {
            startCal.setTime(formatDateKey.parse(startTime));
            endCal.setTime(formatDateKey.parse(endTime));
            int days=getDaysBetween(startCal, endCal);
            String tranf = endTime;
            for(int i=0;i< days;i++){
                if(compareMonthIsCurMon(startTime.substring(0,6),endCal.getTime()) == 0) {
                    tranf = formatDateKey.format(endCal.getTime());
                    flowStatiticsPointDate pointDate = new flowStatiticsPointDate();
                    if(content.get(tranf) != null) {
                        float flowValue = Float.valueOf(content.get(tranf).toString());
                        pointDate.flowPoint = flowValue;
                    }else{
                        pointDate.flowPoint = 0;
                    }
                    pointDate.flowDate = tranf;
                    flowsPointList.add(pointDate);
                }
                endCal.add(Calendar.DAY_OF_MONTH, -1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flowsPointList;
    }

    public int getDaysBetween (Calendar day1, Calendar day2){
        if (day1.after(day2)){
            Calendar swap = day1;
            day1 = day2;
            day2 = swap;
        }
        int days = day2.get(Calendar.DAY_OF_YEAR) - day1.get(Calendar.DAY_OF_YEAR);
        int y2 = day2.get(Calendar.YEAR);
        if (day1.get(Calendar.YEAR) != y2){
            day1 = (Calendar) day1.clone();
            do{
                days += day1.getActualMaximum(Calendar.DAY_OF_YEAR);//得到当年的实际天数
                day1.add(Calendar.YEAR, 1);
            } while (day1.get(Calendar.YEAR) != y2);
        }
        return days+1;
    }

    private class flowStatiticsPointDate{
        public float flowPoint;
        public String flowDate;

    }
}
