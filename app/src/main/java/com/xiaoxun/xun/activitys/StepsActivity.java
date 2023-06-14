package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.xiaoxun.xun.calendar.CustomDate;
import com.xiaoxun.xun.calendar.DatePointForSteps;
import com.xiaoxun.xun.calendar.calendarViewForSteps;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.Timer;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.MyMarkerView;
import com.xiaoxun.xun.views.RoundProgressBar;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepsActivity extends FragmentActivity implements OnChartGestureListener,
        OnChartValueSelectedListener, View.OnClickListener, MsgCallback,
        calendarViewForSteps.OnItemClickListener{

    private LineChart mChart;  //折线图
    private ImageView tv_head_back;
    private ImageButton ib_steps_settings;
    private BroadcastReceiver mMsgReceiver = null;
    private TextView tv_steps_value;
    private TextView tv_steps_target_value;
    private TextView tv_steps_update_time;
    private TextView tv_seven_average;
    private Typeface mTf;
    private TextView titleView;
    private LinearLayout titleLayout;
    private ImageView calendarSign;
    private LinearLayout btnBackLayout;
    private RelativeLayout rl_watch_offon;

    private calendarViewForSteps calendarView_t;

    private String watchEid;
    private String theSelectDay = "";
    private ImibabyApp myApp;
    //滑动计步功能  该功能在计步2.0之后开始进行
    private TextView  tv_watch_status;
    private TextView  tv_steps_percent;
    private TextView  tv_steps_Count;
    private TextView  tv_steps_calories;
    private RoundProgressBar roundProgressBar;
    private Date      curSelectDate = null;
    private ImageView iv_synchrodata_view;

    private ImageView iv_button_update;

    private Timer reqTimer = null;
    private int timeCount = 30;
    private boolean isUpdateing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //状态栏透明 需要在创建SystemBarTintManager 之前调用。
            setTranslucentStatus(true);
        }
//        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
//        mTintManager = new SystemBarTintManager(this);
//        mTintManager.setStatusBarTintEnabled(true);
//        mTintManager.setTintColor(getResources().getColor(R.color.bg_color_orange));
//        mTintManager.setStatusBarDarkMode(true, this);
        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.bg_color_orange));
        myApp = (ImibabyApp)getApplication();
        initView();
        linechart();
//        InitViewPager();
        if(myApp.getCurUser() == null || myApp.getCurUser().getFocusWatch() == null){
            finish();
        }
        watchEid = myApp.getCurUser().getFocusWatch().getEid();

        //测试分享
        List<ResolveInfo> mApps = getShareApps(this);
        getChartStepsFromLocal();
        getChartStepsFromCloudBridge();


        mMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.ACTION_CLOUD_BRIDGE_STEPS_CHANGE)) {
                    String stepsSum = intent.getStringExtra(CloudBridgeUtil.STEPS_LEVEL);
                    String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
                    if(!myApp.getCurUser().getFocusWatch().getEid().equals(eid)){
                        LogUtil.i("eid is not same!");
                        return ;
                    }
                    String[] array = stepsSum.split("_");
                    if(compareTodayToLastInfo(array[0]) && isTodayBySelectDate(curSelectDate)){
                        tv_steps_value.setText(array[1]);
                        tv_steps_value.invalidate();
                        tv_steps_update_time.setText(getString(R.string.steps_update_time,formatUpdateTime(TimeUtil.chnToLocalTimestamp(array[0]))));
                        tv_steps_update_time.invalidate();
                        tv_steps_update_time.setVisibility(View.VISIBLE);
                        iv_synchrodata_view.setVisibility(View.GONE);
                        if(360*Integer.parseInt(array[1]) / roundProgressBar.getMax() < 1
                                && Integer.parseInt(array[1]) != 0){
                            roundProgressBar.setProgress(roundProgressBar.getMax()/360+1);
                        }else {
                            roundProgressBar.setProgress(Integer.parseInt(array[1]));
                        }
                        calcKiloBySteps(array[1]);
                        if (array.length<3){
                            tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                        }else {
//                            tv_steps_calories.setText(String.format("%s%s%s", "消耗", array[2], "千卡"));
//                            102和105的卡路里计算统一，但保留102的计算方式，如果需要保持102的计算方式，则进行回退
                            tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                        }
                        tv_steps_calories.invalidate();
                        int sevenAverage =  sevenAverageChartData(new Date(), false);
                        tv_seven_average.setText(getString(R.string.steps_seven_average,sevenAverage+""));
                        tv_seven_average.invalidate();
                        if(isUpdateing) {
                            ToastUtil.show(StepsActivity.this, getString(R.string.steps_update_date_success));
                        }
                    }else if(!isTodayBySelectDate(curSelectDate) && isUpdateing){
                        tv_steps_update_time.setVisibility(View.GONE);
                        iv_synchrodata_view.setVisibility(View.GONE);
                        ToastUtil.show(StepsActivity.this, getString(R.string.steps_update_date_success));
                    }
                    if(reqTimer != null){
                        LogUtil.i("time is stop!");
                        reqTimer.stop();
                        reqTimer = null;
                        isUpdateing = false;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_CLOUD_BRIDGE_STEPS_CHANGE);

        registerReceiver(mMsgReceiver, filter);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/4/5 19:21
    * 方法描述：判断是否超过20s时间，超过之后，显示更新失败
    */
    private void startTimeOutForNoSteps(){
        timeCount = 20;
        reqTimer = new Timer(1000, new Runnable() {
            public void run() {
                if (reqTimer != null) {
                    timeCount -= 1;
                    if (timeCount <= 0) {
                        if(isTodayBySelectDate(curSelectDate)) {
                            iv_synchrodata_view.setVisibility(View.GONE);
                            iv_synchrodata_view.invalidate();
                            getLocalDataToCurSteps(1, true);
                            isUpdateing = false;
                        }else{
                            ToastUtil.show(StepsActivity.this, getString(R.string.ximalaya_story_data_delay));
                            iv_synchrodata_view.setVisibility(View.GONE);
                            iv_synchrodata_view.invalidate();
                            isUpdateing = false;
                        }
                    } else {
                        LogUtil.i("time:"+timeCount);
                        reqTimer.restart();
                    }
                }
            }
        });
        reqTimer.start();
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/25 11:01
    * 方法描述：可使用的分享代码
    */
    public static List<ResolveInfo> getShareApps(Context context) {
        List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("image/*");
        PackageManager pManager = context.getPackageManager();
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    private String formatUpdateTime(String updateTime){
        Date time = TimeUtil.getDataFromTimeStamp(updateTime);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5   
    * 创建时间：2016/6/1 17:12      
    * 方法描述：发送设备基本配置请求
    */ 
    private void sendDeviceGet(String eid) {

        MyMsgData msg = new MyMsgData();
        msg.setCallback(this);
        //set msg body
        JSONObject pl = new JSONObject();
        // pl.put(CloudBridgeUtil.KEY_NAME_ALIAS, getMyApp().getCurUser().genRelationStr());
        pl.put(CloudBridgeUtil.KEY_NAME_EID,eid);
        msg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_GET, pl));
        if(myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(msg);


    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/16 19:21
    * 方法描述：根据给出的日期，来判断是否是今天的日期
    */
    private boolean isTodayBySelectDate(Date selectDate){
        if(selectDate == null){
            return true;
        }
        Calendar day = Calendar.getInstance();
        //获取到该天的数据，同步显示到组件上
        return day.get(Calendar.YEAR) == selectDate.getYear() + 1900
                && day.get(Calendar.MONTH) == selectDate.getMonth()
                && day.get(Calendar.DAY_OF_MONTH) == selectDate.getDate();
    }

    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/29 16:34
    * 方法描述：初始化折线图，用来显示七天的步数统计
    */
    private void linechart(){
        mChart = findViewById(R.id.chart2);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription(getString(R.string.steps_no_steps_info));

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setHighlightEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

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
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(10);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setLabelCount(3);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf((int)v);
            }
        });

        mChart.getAxisRight().setEnabled(false);
        // add data

        setData(null, null,true);
        mChart.setViewPortOffsets(DensityUtil.dip2px(this, 35), DensityUtil.dip2px(this, 10),
                DensityUtil.dip2px(this, 33), DensityUtil.dip2px(this, 46));
//        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/29 16:35
    * 方法描述：折线图的数据初始化函数
    */
    private void setData(ArrayList<String> xVals, ArrayList<Entry>yVals,boolean isInitFirst) {
        if(isInitFirst){
            xVals = new ArrayList<String>();
            Date beforeDate = getDateBefore(new Date(), 1);
            SimpleDateFormat formatDateKey = new SimpleDateFormat("MM/dd");
            for (int i = 0; i < 7; i++) {
                xVals.add(formatDateKey.format(beforeDate));
            }
            yVals = new ArrayList<Entry>();
            for (int i = 0; i < 7; i++) {
                yVals.add(new Entry(0, i));
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, getString(R.string.share_to_steps));

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

    private void initView(){
        AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(getResources().getDrawable(R.drawable.update_steps1), 250);
        anim.addFrame(getResources().getDrawable(R.drawable.update_steps2), 250);
        anim.addFrame(getResources().getDrawable(R.drawable.update_steps3), 250);

        iv_synchrodata_view = findViewById(R.id.synchrodata_view);
        iv_synchrodata_view.setBackground(anim);
        anim.setOneShot(false);
        anim.start();

        iv_button_update = findViewById(R.id.button_update);
        iv_button_update.setOnClickListener(this);

        tv_steps_Count = findViewById(R.id.steps_Count);
        tv_steps_calories = findViewById(R.id.steps_calories);
        tv_watch_status = findViewById(R.id.watch_status);
        rl_watch_offon = findViewById(R.id.watch_offon_rela);
        tv_watch_status.setOnClickListener(this);
        rl_watch_offon.setOnClickListener(this);
        tv_steps_percent = findViewById(R.id.steps_percent);
        tv_steps_target_value = findViewById(R.id.steps_tag);
        ib_steps_settings = findViewById(R.id.setting_button);
        ib_steps_settings.setOnClickListener(this);
        tv_head_back = findViewById(R.id.iv_title_back);
        tv_head_back.setOnClickListener(this);
        tv_steps_value = findViewById(R.id.steps_value);
        tv_steps_update_time = findViewById(R.id.steps_update_time);
        tv_seven_average = findViewById(R.id.seven_average_info);
        btnBackLayout = findViewById(R.id.iv_title_back_layout);
        btnBackLayout.setOnClickListener(this);
        roundProgressBar = findViewById(R.id.round_progressbar_value);
        roundProgressBar.setOnClickListener(this);

        //操作日历相关
        titleLayout = findViewById(R.id.tv_title_layout);
        titleLayout.setOnClickListener(this);
        titleView = findViewById(R.id.tv_title);
        titleView.setText(getText(R.string.today));
        calendarSign = findViewById(R.id.calendar_sign);
        calendarSign.setImageDrawable(getResources().getDrawable(R.drawable.open));

        initcalendar();

        //管理员操作相关
        if (!isAdmin()){
            ib_steps_settings.setVisibility(View.GONE);
        }
    }
    private boolean isAdmin(){
        return myApp.getCurUser().isMeAdminByWatch(myApp.getCurUser().getFocusWatch());
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/2 9:35
    * 方法描述：和轨迹界面相同的日期显示方式，初始化日期显示界面
    */
    private void initcalendar() {
        calendarView_t = new calendarViewForSteps(this, this);
        calendarView_t.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                calendarSign.setImageDrawable(getResources().getDrawable(
                        R.drawable.open));
            }
        });
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/24 11:27
    * 方法描述：通过本地保存的数据来初始化实时步数显示，和直方图中的七日数据内容。
    */
    private void getChartStepsFromLocal(){
        //获取到本地保存的目标步数
        getLocalDataToTargetLevel();
        //实时步数本地化显示
        getLocalDataToCurSteps(1, false);
        //70天步数本地化显示
        String steps_recv_pl = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_LIST, "0");
        if(steps_recv_pl != null && !steps_recv_pl.equals("0")) {
            JSONObject steps_pl = (net.minidev.json.JSONObject) JSONValue.parse(steps_recv_pl);
            JSONArray steps = (JSONArray) steps_pl.get(CloudBridgeUtil.STEPS_LIST);//(net.minidev.json.JSONArray) JSONValue.parse(steps_all_data);
            DatePointForSteps.stepsPointList.clear();
            updateChartAndCalendarView(steps);
            calendarView_t.updateAllCalendarViewToSteps();
        }
        //获取到本地计步功能是否开启
        updateDataToOnOffLevel();
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/7 10:05
    * 方法描述：根据日期判断是否需要进行拉取最近70天的数据
    */
    private boolean IsHasDataForCloudBridget(){
        String steps_recv_pl = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_LIST, "0");
        if(steps_recv_pl != null && !steps_recv_pl.equals("0")) {
            JSONObject steps_pl = (net.minidev.json.JSONObject) JSONValue.parse(steps_recv_pl);
            Date beforeDate = getDateBefore(new Date(), 1);
            @SuppressLint("SimpleDateFormat")
            DateFormat format=new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String beforeDateStr = format.format(beforeDate).substring(0,8);
            JSONArray steps = (JSONArray) steps_pl.get(CloudBridgeUtil.STEPS_LIST);//(net.minidev.json.JSONArray) JSONValue.parse(steps_all_data);
            for(int i = 0;i<steps.size();i++){
                JSONObject stepObject = (JSONObject)steps.get(i);
                String keyOnLine = TimeUtil.getReversedTimeByTime(stepObject.get("Key").toString());
                if(keyOnLine.equals(beforeDateStr)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/4 16:48
    * 方法描述：实时步数本地化显示
     * 修改描述：添加标志位，来确定是否需要显示时间提示和是否显示时间超时
    */
    private void getLocalDataToCurSteps(int timeFlag, boolean isShowTimeOut){
        String steps_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_LEVEL, "0");
        if(steps_level == null || steps_level.equals("0")){

        }else {
            String[] array = steps_level.split("_");
            if (compareTodayToLastInfo(array[0])) {
                tv_steps_value.setText(array[1]);
                tv_steps_value.invalidate();
                if(timeFlag == 1) {
                    tv_steps_update_time.setText(getString(R.string.steps_update_time, formatUpdateTime(TimeUtil.chnToLocalTimestamp(array[0]))));
                    tv_steps_update_time.invalidate();
                    tv_steps_update_time.setVisibility(View.VISIBLE);
                }
                if(360*Integer.parseInt(array[1]) / roundProgressBar.getMax() < 1
                        && Integer.parseInt(array[1]) != 0){
                    roundProgressBar.setProgress(roundProgressBar.getMax()/360+1);
                }else {
                    roundProgressBar.setProgress(Integer.parseInt(array[1]));
                }
                roundProgressBar.invalidate();
                calcKiloBySteps(array[1]);
                if (array.length<3){
                    tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                }else {
//                    tv_steps_calories.setText(String.format("%s%s%s", "消耗", array[2], "千卡"));
//                            102和105的卡路里计算统一，但保留102的计算方式，如果需要保持102的计算方式，则进行回退
                    tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                }
                tv_steps_calories.invalidate();
                return ;
            }
        }
        if(isShowTimeOut){
            ToastUtil.show(this,getString(R.string.ximalaya_story_data_delay));
            return ;
        }
        tv_steps_value.setText("0");
        tv_steps_value.invalidate();
//        tv_steps_update_time.setVisibility(View.GONE);
        roundProgressBar.setProgress(0);
        tv_steps_Count.setText("");
        tv_steps_Count.invalidate();
        tv_steps_calories.setText("");
        tv_steps_calories.invalidate();

    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/7/22 16:29
    * 方法描述：通过步数，体重，距离，计算消耗的卡路里
    */
    private int calcCalBySteps(String steps){
        double weight;
        if(myApp.getCurUser().getFocusWatch().getWeight() < 18 ){
            weight = 18;
        }else{
            weight = myApp.getCurUser().getFocusWatch().getWeight();
        }
        double kilo = calcKiloBySteps(steps);
        double calo = kilo * 1.036 * weight + 0.5;//四舍五入之后取整数

        return (int)calo;
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/10 13:47
    * 方法描述：通过步数，身高，计算行走距离
    */
    private double calcKiloBySteps(String steps){
        int stepCount = Integer.parseInt(steps);
        double heightScale;
        if(myApp.getCurUser().getFocusWatch().getHeight() < 60 ){
            heightScale = 110;
        }else{
            heightScale = myApp.getCurUser().getFocusWatch().getHeight();
        }
        double meter = stepCount*heightScale*0.45*0.01;
        if(meter > 1000){
            meter /= 1000;
            tv_steps_Count.setText(getString(R.string.steps_calc_sport_data_kilo,Integer.toString((int)meter)));
        }else{
            tv_steps_Count.setText(getString(R.string.steps_calc_sport_data_meter,Integer.toString((int)meter)));
            meter /= 1000;
        }
        tv_steps_Count.invalidate();
        return meter;
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/8 15:46
    * 方法描述：根据本地获取到的计步器开关，来设置开关按钮
    */
    private void updateDataToOnOffLevel(){
        String steps_onoff_level;
        if(myApp.getCurUser().getFocusWatch().isDevice102()){
            steps_onoff_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "0");
        }else {
           steps_onoff_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "1");
        }
        if(steps_onoff_level == null || steps_onoff_level.equals("0")){
            tv_watch_status.setVisibility(View.VISIBLE);
            rl_watch_offon.setVisibility(View.VISIBLE);
            tv_steps_percent.setVisibility(View.INVISIBLE);
            tv_steps_value.setVisibility(View.INVISIBLE);
            tv_steps_update_time.setVisibility(View.INVISIBLE);
            iv_synchrodata_view.setVisibility(View.INVISIBLE);
        }else if(steps_onoff_level.equals("1")){
            if(iv_synchrodata_view.getVisibility() == View.VISIBLE){
                tv_steps_update_time.setVisibility(View.INVISIBLE);
            }else {
                if(!isTodayBySelectDate(curSelectDate)){
                    tv_steps_update_time.setVisibility(View.GONE);
                }else {
                    tv_steps_update_time.setVisibility(View.VISIBLE);
                }
            }
            tv_watch_status.setVisibility(View.GONE);
            rl_watch_offon.setVisibility(View.GONE);
            tv_steps_percent.setVisibility(View.VISIBLE);
            tv_steps_value.setVisibility(View.VISIBLE);
        }
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/4 15:23
    * 方法描述：获取到本地保存的目标步数
    */
    private void getLocalDataToTargetLevel(){
        String steps_target_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_TARGET_LEVEL, "0");
        if(steps_target_level == null || steps_target_level.equals("0")){
            tv_steps_target_value.setText(getString(R.string.steps_target_step,"8000"));
            roundProgressBar.setMax(8000);
        }else {
            tv_steps_target_value.setText(getString(R.string.steps_target_step,steps_target_level));
            roundProgressBar.setMax(Integer.valueOf(steps_target_level));
        }
        tv_steps_target_value.invalidate();
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/26 9:47
    * 方法描述：比较给出的时间是否是今天的时间
    */
    private boolean compareTodayToLastInfo(String oldData){
        boolean isToday = false;
        String curTime = TimeUtil.getTimeStampLocal();
        String curDate = curTime.substring(0, 8);
        String oldDate = oldData.substring(0, 8);
        if(curDate.equals(oldDate)) {
            isToday = true;
        }

        return isToday;
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/24 11:33
    * 方法描述：从网络刷新步数和近七日数据列表
    */
    private void getChartStepsFromCloudBridge(){
        sendDeviceGet(myApp.getCurUser().getFocusWatch().getEid());
        //c2e获取最近70日内每日的步数
        if(!IsHasDataForCloudBridget()) {
            String keyOnLine = TimeUtil.getReversedTimeByTime(TimeUtil.getTimeStampLocal().substring(0, 8));
            String c2eKeyValue = "EP/" + watchEid + "/STEPS/" + keyOnLine;
            sendStepsDateReqC2eMsg(Const.STEPS_CALENDER_DATE_COUNT, c2eKeyValue);
        }
        //MapMget获取到目标步数,实时步数，计步开关
        String[] keys = new String[4];
        keys[0] = CloudBridgeUtil.STEPS_TARGET_LEVEL;
        keys[1] = CloudBridgeUtil.STEPS_LEVEL;
        keys[2] = CloudBridgeUtil.STEPS_ONOFF_SETTING;
        keys[3] = CloudBridgeUtil.STEPS_NOTIFICATION_SETTING;
        mapMGet(myApp.getCurUser().getFocusWatch().getEid(), keys);
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/26 10:37
    * 方法描述：发送c2e数据请求，服务器日期步数（第一个参数请求日期，第二个参数，从该日期开始前的几天）
    */
    private void sendStepsDateReqC2eMsg(int num, String key){
        MyMsgData c2e = new MyMsgData();
        c2e.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put("Key", key);
        pl.put("Size", num);
        c2e.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_C2E, pl));

        if(myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(c2e);
        }
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/2/25 20:06
    * 方法描述：发送e2e消息，通知手表上传计步数据mapset到服务器
    */
    private void sendStepsReqE2eMsg(){
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(null);
        JSONObject pl = new JSONObject();
        String [] eid = new String[1];
        eid[0] = myApp.getCurUser().getFocusWatch().getEid();
        String timestamp = TimeUtil.getTimeStampGMT();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_STEPS_SUM);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timestamp);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, myApp.getToken(), null, eid, pl));

        if(myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(e2e);
        }
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/8 15:10
    * 方法描述：获取定位豆的计步相关初始化数据，具体为：开关，目标步数和当前实时步数
    */
    private void mapMGet(String eid, String[] keys) {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(this);

        JSONArray plKeyList = new JSONArray();
        for (int i = 0; i < keys.length; i++) {
            plKeyList.add(keys[i]);
        }

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));

        if(myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(mapget);
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

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_DEVICE_GET_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS){
                    //refresh watch
                    JSONObject devicePl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    myApp.parseDevicePl(myApp.getCurUser().getFocusWatch(), devicePl);

                    WatchDAO.getInstance(getApplicationContext()).addWatch(myApp.getCurUser().getFocusWatch());
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String steps = (String) pl.get(CloudBridgeUtil.STEPS_LEVEL);
                    String target = (String) pl.get(CloudBridgeUtil.STEPS_TARGET_LEVEL);
                    if(steps != null && !steps.equals("0")){
                        String[] array = steps.split("_");
                        if(compareTodayToLastInfo(array[0])){
                            tv_steps_value.setText(array[1]);
                            tv_steps_value.invalidate();
                            if(360*Integer.parseInt(array[1]) / roundProgressBar.getMax() < 1
                                    && Integer.parseInt(array[1]) != 0){
                                roundProgressBar.setProgress(roundProgressBar.getMax()/360+1);
                            }else {
                                roundProgressBar.setProgress(Integer.parseInt(array[1]));
                            }
                        }
                    }
                    if(target != null && !target.equals("0")){
                        tv_steps_target_value.setText(getString(R.string.steps_target_step,target));
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + CloudBridgeUtil.STEPS_TARGET_LEVEL,
                                target);
                        tv_steps_target_value.invalidate();
                        roundProgressBar.setMax(Integer.parseInt(target));
                    }
                    LogUtil.i("docallback:"+steps+":"+target);
                }
                break;
            case CloudBridgeUtil.CID_C2E_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    JSONArray steps = (JSONArray) pl.get(CloudBridgeUtil.STEPS_LIST);
                    String TimeKeyValue = TimeUtil.getTimeStampLocal().substring(0, 8);
                    pl.put(Const.SETPS_CUR_TIMEKEY, TimeKeyValue);
                    myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + CloudBridgeUtil.STEPS_LIST,
                            pl.toString());
                    updateChartAndCalendarView(steps);
                    calendarView_t.updateAllCalendarViewToSteps();
//                    InitViewPager();
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String steps = (String) pl.get(CloudBridgeUtil.STEPS_LEVEL);
                    String target = (String) pl.get(CloudBridgeUtil.STEPS_TARGET_LEVEL);
                    if (target != null && !"".equals(target) && !target.equals("0")) {
                        tv_steps_target_value.setText(getString(R.string.steps_target_step,target));
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + CloudBridgeUtil.STEPS_TARGET_LEVEL,
                                target);
                        tv_steps_target_value.invalidate();
                        roundProgressBar.setMax(Integer.parseInt(target));
                    }
                    String offOnValue = (String) pl.get(CloudBridgeUtil.STEPS_ONOFF_SETTING);
                    boolean isOnByWatchType = false;
                    if(myApp.getCurUser().getFocusWatch().isDevice102()){
                        if(offOnValue == null || "".equals(offOnValue) || offOnValue.equals("0")){
                        }else if(offOnValue.equals("1")){
                            isOnByWatchType = true;
                        }
                    }else {
                        if(offOnValue == null || "".equals(offOnValue) || offOnValue.equals("1")){
                            isOnByWatchType = true;
                        }
                    }
                    if (isOnByWatchType) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() +
                                    CloudBridgeUtil.STEPS_ONOFF_SETTING, "1");
                            updateDataToOnOffLevel();
                            if (steps != null && !"".equals(steps) && !steps.equals("0")) {
                                myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() +
                                    CloudBridgeUtil.STEPS_LEVEL, steps);
                                String[] array = steps.split("_");
                                if (compareTodayToLastInfo(array[0])&& isTodayBySelectDate(curSelectDate)) {
                                    tv_steps_value.setText(array[1]);
                                    tv_steps_value.invalidate();
                                    if(360*Integer.parseInt(array[1]) / roundProgressBar.getMax() < 1
                                            && Integer.parseInt(array[1]) != 0){
                                        roundProgressBar.setProgress(roundProgressBar.getMax()/360+1);
                                    }else {
                                        roundProgressBar.setProgress(Integer.parseInt(array[1]));
                                    }
                                    calcKiloBySteps(array[1]);
                                    if (array.length<3){
                                        tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                                    }else {
//                                        tv_steps_calories.setText(String.format("%s%s%s", "消耗", array[2], "千卡"));
//                            102和105的卡路里计算统一，但保留102的计算方式，如果需要保持102的计算方式，则进行回退
                                        tv_steps_calories.setText(getString(R.string.steps_expend_energy,calcCalBySteps(array[1])+""));
                                    }
                                    tv_steps_calories.invalidate();
                                    int sevenAverage =  sevenAverageChartData(new Date(), false);
                                    tv_seven_average.setText(getString(R.string.steps_seven_average, String.valueOf(
                                            sevenAverage)));
                                    tv_seven_average.invalidate();
                                }
                            }
                            iv_synchrodata_view.setVisibility(View.VISIBLE);
                            tv_steps_update_time.setVisibility(View.INVISIBLE);
                            //根据开关拉取e2e实时步数数据
                            sendStepsReqE2eMsg();
                            //定时器，判断是否获取到数据
                            startTimeOutForNoSteps();
//                        }
                    } else {
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() +
                                CloudBridgeUtil.STEPS_ONOFF_SETTING, "0");
                        updateDataToOnOffLevel();
                    }
                    LogUtil.i("docallback:" + pl.toString());

                    String offOnNotication = (String) pl.get(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
                    if(offOnNotication != null) {
                        if (offOnNotication.equals("0")) {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() +
                                    CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "0");
                        } else {
                            myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() +
                                    CloudBridgeUtil.STEPS_NOTIFICATION_SETTING, "1");
                        }
                    }
                }
                break;
        }
    }
    private void updateChartAndCalendarView(JSONArray steps){
        if(steps == null){
            return ;
        }
        DatePointForSteps.setDateToCalenderPointListForSteps(steps);

        LogUtil.i("docallback:"+steps.toString());
        Date date = new Date();
        int sevenAverage =  sevenAverageChartData(date, true);
        tv_seven_average.setText(getString(R.string.steps_seven_average, String.valueOf(
                sevenAverage)));
        tv_seven_average.invalidate();
    }

    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/4 15:58
    * 方法描述：计算7日平均数据和初始化七日的图表信息
     * 修改时间：2016.03.25 16:36
     * 修改内容：显示今天的特殊处理（不加动画）
    */
    private int sevenAverageChartData(Date selectData, boolean isAnimal){
        int sevenAverage = 0;

        ArrayList<String> xVals = new ArrayList<String>();  //图表x轴的数据显示
        ArrayList<Entry>  yVals = new ArrayList<Entry>();   //图表y轴的数据显示
        float stepsSum = 0;                                 //七天的总数据
        float stepsCount = 0;
        float stepsBiggest = 0;                             //七天数据中的最大数据
        for(int i = 6;i >= 0; i--){
            Date beforeDate = getDateBefore(selectData,i);
            LogUtil.i("beforDate:"+beforeDate);
            boolean isHasSteps = false;
            SimpleDateFormat formatDateKey = new SimpleDateFormat("MM/dd");
            String beforeDayStr = formatDateKey.format(beforeDate);
            if(isTodayBySelectDate(beforeDate)){
                String steps_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_LEVEL, "0");
                if(steps_level == null || steps_level.equals("0")){
                    isHasSteps = false;
                }else {
                    String[] array = steps_level.split("_");
                    if (compareTodayToLastInfo(array[0])) {
                        isHasSteps = true;
                        xVals.add(beforeDayStr);
                        float stepsData = Float.parseFloat(array[1]);
                        yVals.add(new Entry(stepsData, 6 - i));
                        stepsSum += stepsData;
                        if (stepsData >= stepsBiggest) {
                            stepsBiggest = stepsData;
                        }
                        stepsCount++;
                    }
                }
            }else {
                for (int j = 0; j < DatePointForSteps.stepsPointList.size(); j++) {
                    DatePointForSteps pointTmp = DatePointForSteps.stepsPointList.get(j);
                    if (beforeDate.getDate() == pointTmp.getDate().getDate()
                            && beforeDate.getMonth() == pointTmp.getDate().getMonth()
                            && beforeDate.getYear() == pointTmp.getDate().getYear()) {
                        isHasSteps = true;
                        xVals.add(beforeDayStr);
                        float stepsData = Float.parseFloat(String.valueOf(pointTmp.getPointNum()));
                        yVals.add(new Entry(stepsData, 6 - i));
                        stepsSum += stepsData;
                        if (stepsData >= stepsBiggest) {
                            stepsBiggest = stepsData;
                        }
                        stepsCount++;
                        break;
                    }
                }
            }
            if(!isHasSteps) {
                xVals.add(beforeDayStr);
                yVals.add(new Entry(0, 6 - i));
            }
        }
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        if(stepsCount == 0){
            stepsCount = 1;
        }
        float averageSteps = (int)stepsSum/stepsCount;
        sevenAverage = (int)stepsSum/7;
        if(stepsBiggest + averageSteps == 0){
            stepsBiggest= 10;
        }
        leftAxis.setAxisMaxValue(stepsBiggest * 5 / 4 + averageSteps);
        leftAxis.setAxisMinValue(0);
        leftAxis.setLabelCount(3);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf((int)v);
            }
        });
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
        return sevenAverage;
    }
    /**
    * 类名称：StepsActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/1 13:36
    * 方法描述：计算date之前n天的日期
    */
    public static Date getDateBefore(Date date, int n) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);

        return now.getTime();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_update: {
                String steps_onoff_level ;
                if(myApp.getCurUser().getFocusWatch().isDevice102()){
                    steps_onoff_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "0");
                }else {
                    steps_onoff_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_ONOFF_SETTING, "1");
                }
                if (steps_onoff_level == null || steps_onoff_level.equals("0")) {
                    ToastUtil.show(this, getString(R.string.steps_updates_limit));
                } else if(!isUpdateing){
                    isUpdateing = true;
                    iv_synchrodata_view.setVisibility(View.VISIBLE);
                    tv_steps_update_time.setVisibility(View.INVISIBLE);
                    //根据开关拉取e2e实时步数数据
                    sendStepsReqE2eMsg();
                    //定时器，判断是否获取到数据
                    startTimeOutForNoSteps();
                }
            }
                break;

            case R.id.watch_status:
            case R.id.watch_offon_rela:
                if(isAdmin()) {
                    startActivity(new Intent(this, StepsSettingActivity.class));
                }else{
                    ToastUtil.show(this,getString(R.string.steps_settings_open_limit));
                }
                break;
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.setting_button:
                startActivity(new Intent(this, StepsSettingActivity.class));
                break;
            case R.id.tv_title_layout:
                calendarSign.setImageDrawable(getResources().getDrawable(
                        R.drawable.close));
                setCalendarLocation();
                break;
        }
    }
    private void setCalendarLocation() {
        calendarView_t.showAtLocation(findViewById(android.R.id.content).getRootView(), Gravity.TOP, 0, 0);
        calendarView_t.showCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocalDataToTargetLevel();
        updateDataToOnOffLevel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatePointForSteps.curPageIndex = 1;
        DatePointForSteps.curSelectItem.setDay(1);
        DatePointForSteps.curSelectItem.setMonth(1);
        DatePointForSteps.curSelectItem.setYear(1900);
        if(reqTimer != null){
            reqTimer.stop();
            reqTimer = null;
            isUpdateing = false;
        }
        try {
            unregisterReceiver(mMsgReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void getItemDate(CustomDate item, int num) {
        Date date = new Date(item.getYear() - 1900, item.getMonth() - 1,
                item.getDay());
        curSelectDate = date;
        final SimpleDateFormat format = new SimpleDateFormat(getResources().getString(R.string.format_time));
        Calendar day = Calendar.getInstance();
        //获取到该天的数据，同步显示到组件上
        if (day.get(Calendar.YEAR) == date.getYear() + 1900
                && day.get(Calendar.MONTH) == date.getMonth()
                && day.get(Calendar.DAY_OF_MONTH) == date.getDate()) {
            titleView.setText(getString(R.string.today));
            getLocalDataToCurSteps(1, false);
            updateDataToOnOffLevel();
            iv_button_update.setOnClickListener(this);
            iv_button_update.setBackgroundResource(R.drawable.btn_steps_update);
        } else {
            iv_button_update.setOnClickListener(null);
            iv_button_update.setBackgroundResource(R.drawable.steps_update_on);

            titleView.setText(format.format(date));
            int i;
            for(i = 0;i < DatePointForSteps.stepsPointList.size();i++){
                DatePointForSteps pointTmp = DatePointForSteps.stepsPointList.get(i);
                if(date.getDate() == pointTmp.getDate().getDate()
                        && date.getMonth() == pointTmp.getDate().getMonth()
                        && date.getYear() == pointTmp.getDate().getYear()){
                    tv_steps_value.setText(String.valueOf(pointTmp.getPointNum()));
                    tv_steps_value.invalidate();
                    tv_steps_update_time.setVisibility(View.GONE);
                    iv_synchrodata_view.setVisibility(View.GONE);
                    calcKiloBySteps(String.valueOf(pointTmp.getPointNum()));
                    if(pointTmp.getCalories() == 0 && pointTmp.getPointNum() != 0) {
                        tv_steps_calories.setText(getString(R.string.steps_expend_energy,
                                calcCalBySteps(String.valueOf(pointTmp.getPointNum()))+""));
                    }else{
//                        tv_steps_calories.setText(String.format("%s%s%s", "消耗",
//                                String.valueOf(pointTmp.getCalories()), "千卡"));
//                            102和105的卡路里计算统一，但保留102的计算方式，如果需要保持102的计算方式，则进行回退
                        tv_steps_calories.setText(getString(R.string.steps_expend_energy,
                                calcCalBySteps(String.valueOf(pointTmp.getPointNum()))+""));
                    }
                    tv_steps_calories.invalidate();
                    if(360 * pointTmp.getPointNum() / roundProgressBar.getMax() < 1
                            && pointTmp.getPointNum() != 0){
                        roundProgressBar.setProgress(roundProgressBar.getMax()/360+1);
                    }else {
                        roundProgressBar.setProgress(pointTmp.getPointNum());
                    }
                    break;
                }
            }
            if(i == DatePointForSteps.stepsPointList.size()){
                tv_steps_value.setText("0");
                tv_steps_value.invalidate();
                tv_steps_update_time.setVisibility(View.GONE);
                iv_synchrodata_view.setVisibility(View.GONE);
                roundProgressBar.setProgress(0);
                tv_steps_Count.setText("");
                tv_steps_Count.invalidate();
                tv_steps_calories.setText(getString(R.string.steps_expend_energy,"0"));
                tv_steps_calories.invalidate();
            }
        }
        int sevenAverage = sevenAverageChartData(date, true);
        tv_seven_average.setText(getString(R.string.steps_seven_average,sevenAverage+""));
        tv_seven_average.invalidate();
        calendarView_t.showNextFromDown();

        final SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd");
        theSelectDay = formatDateKey.format(date);

    }
}
