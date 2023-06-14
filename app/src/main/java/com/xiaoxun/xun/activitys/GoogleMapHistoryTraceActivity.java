package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xiaoxun.xun.calendar.CustomDate;
import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.calendar.calendarView;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.HistoryTraceDAO;
import com.xiaoxun.xun.google.GPSUtil;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.GooglePointInfo;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MapScaleView;
import com.xiaoxun.xun.utils.PointInfo;
import com.xiaoxun.xun.utils.SecurityZone;
import com.xiaoxun.xun.utils.SphericalUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.TracePointInf;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class GoogleMapHistoryTraceActivity extends NormalMapActivity implements OnMapReadyCallback, View.OnClickListener, calendarView.OnItemClickListener, SeekBar.OnSeekBarChangeListener, LoadingDialog.OnConfirmClickListener, MsgCallback {
    private static String TAG = "HistoryTraceActivity";

    ImageButton btnBack;
    ImageButton listbtn;
    LinearLayout btnBackLayout;
    TextView titleView;
    LinearLayout titleLayout;
    ImageView calendarSign;
    RelativeLayout bottomLayout;
    ImageView bottomImg;
    TextView bottomTextErr;
    TextView bottomText;
    TextView bottomTips;
    ImageButton zoomout;
    ImageButton zoomin;
    LinearLayout nomalBottomText;
    Button refreshBtn;
    LinearLayout listbtn_ly;
    ImageButton trace_list;
    TextView onedays;
    TextView threedays;
    TextView fivedays;
    SeekBar seek_bar;
    ImageButton last_point;
    ImageButton next_point;
    MapScaleView mapScaleView;
    private calendarView calendarView_t;
    private LoadingDialog loadingdlg;
    private Drawable littleSign0 = null;

    Comparator<GooglePointInfo> comparator;

    private ImibabyApp mApp;
    private WatchData curWatch;
    private String theSelectDay = "";
    int isDaysHistory = 0;//0 one day;1 three days;2 five days.
    Date selectDate;

    private int cntGps, cntCell, cntWifi, cntMix, cntFail, cntTotal, cntOther, cnGO, cnFilter;
    private int dis1 = 0, dis2 = 0, cell1 = 0, ang1 = 0, cellang = 0, dis3 = 0, speedf = 0;
    private int curDayPointsCounter = 0;
    private boolean mDebugShowAllDots = false;

    GoogleMap mMap;
    private Marker curMarker;
    private ArrayList<GooglePointInfo> mAllPointInfo = new ArrayList<>(); // 服务器回传的点，过滤后的点
    private ArrayList<GooglePointInfo> mAllPointSer = new ArrayList<>(); // 服务器查询所有点
    private ArrayList<SecurityZone> listItem = new ArrayList<>(); //安全区域数组
    private ArrayList<GooglePointInfo> dropList = new ArrayList<>(); //drop点数组
    private ArrayList<GooglePointInfo> filterPointList = new ArrayList<>(); //过滤点数据
    private ArrayList<GooglePointInfo> linePoints = new ArrayList<>();
    private ArrayList<GooglePointInfo> fadePoints = new ArrayList<>();
    private ArrayList<GooglePointInfo> combineList = new ArrayList<>();
    private ArrayList<Marker> allMarkers = new ArrayList<>();
    private ArrayList<Marker> fadeMarkers = new ArrayList<>();
    private ArrayList<LatLng> moveList = new ArrayList<>(); //seekbar 滑动点数组
    private Polyline mainTraceLine;
    private Polyline moveLine = null;

    private int curTrackPosition = 0;
    private int curMarkerPos = 0;

    private static int IGNORE_DISTENCE = 30; // 单位米
    final static int CONFIRM_ANGLEFILTER_TIME = 10;//minute
    final static int DISTANCE_ANGELEFILTER_MULTIPLE = 7;
    final static int DISTANCE_ANGELEFILTER_POINTS = 8;
    final static float SPEED_FILTER_LIMIT = 500f * 1000f;// 米/小时
    final static float CELL_ANGLE_FILTER_DISTANCE = 5000;//米

    enum BottomStatus {
        NO_DATA, GETTING_DATA, NETWORK_ERR, NO_NET
    }

    private static class traceHandler extends Handler {
        WeakReference<GoogleMapHistoryTraceActivity> act;

        private traceHandler(GoogleMapHistoryTraceActivity activity) {
            act = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final GoogleMapHistoryTraceActivity activity = act.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    act.get().zoomout.setVisibility(View.VISIBLE);
                    act.get().zoomin.setVisibility(View.VISIBLE);
                    this.sendEmptyMessageDelayed(1, 300);
                    break;
                case 1:
                    act.get().allMarkersShowInScreen();
                    break;
                case 2:
                    if (act.get().loadingdlg != null && act.get().loadingdlg.isShowing())
                        act.get().loadingdlg.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private traceHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_history_trace);
        setTintColor(getResources().getColor(R.color.bg_color_orange));
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mApp = (ImibabyApp) getApplication();
        curWatch = mApp.getCurUser().getFocusWatch();
        mHandler = new traceHandler(this);

        initViews();

        SharedPreferences prefs = getSharedPreferences(
                DevOptActivity.DEV_OPT_PREF, Context.MODE_PRIVATE);
        String show_all_dots = prefs
                .getString(DevOptActivity.SHOW_ALL_DOTS, "");
        mDebugShowAllDots = show_all_dots.equalsIgnoreCase("true");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMapConfig();
        //默认地图位置
        if (mApp.getCurUser().getFocusWatch().isDevice709_A05() || mApp.getCurUser().getFocusWatch().isDevice708_A07()
                || mApp.getCurUser().getFocusWatch().isDevice708_A06() || mApp.getCurUser().getFocusWatch().isDevice709_A03()) { //越南项目，默认位置河内
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0227788, 105.8194541), 16));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.1754744, 106.8269617), 16));
        }
        initData();
    }

    private void initMapConfig() {

        mMap.getUiSettings().setCompassEnabled(false);  //隐藏指南针
        mMap.getUiSettings().setZoomControlsEnabled(false);  //隐藏地图系统默认的放缩按钮
        mMap.getUiSettings().setRotateGesturesEnabled(false);  //禁止通过手势旋转
        mMap.getUiSettings().setTiltGesturesEnabled(false);  //禁止通过手势倾斜
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);  //隐藏层级选取器
        mMap.getUiSettings().setMapToolbarEnabled(false);  //隐藏地图工具栏
        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                setScaleText();
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                setScaleText();
            }
        });
    }

    private void initData() {
        comparator = new Comparator<GooglePointInfo>() {
            public int compare(GooglePointInfo s1, GooglePointInfo s2) {
                if (!s1.mTimeStamp.equals(s2.mTimeStamp)) {
                    return s1.mTimeStamp.compareTo(s2.mTimeStamp);
                }
                return -100;
            }
        };

        String[] dates = new String[1];
        dates[0] = TimeUtil.getTimeStampLocal().substring(0, 8);
        getHistoryTraceDataByDay(getThreeDaysString(dates[0]));
        selectDate = Calendar.getInstance().getTime();
        theSelectDay = TimeUtil.getTimeStampLocal().substring(0, 8);
    }

    private void initViews() {
        littleSign0 = getResources().getDrawable(R.drawable.location_0);
        titleLayout = findViewById(R.id.tv_title_layout);
        titleLayout.setOnClickListener(this);
        titleView = findViewById(R.id.tv_title);
        titleView.setText("  " + getText(R.string.today) + "  ");
        calendarSign = findViewById(R.id.calendar_sign);
        calendarSign.setImageDrawable(getResources().getDrawable(
                R.drawable.open));

        bottomLayout = findViewById(R.id.buttomtitle);
        nomalBottomText = findViewById(R.id.nomalBottomText);
        bottomImg = findViewById(R.id.iv_buttomimg);
        bottomImg.setImageDrawable(littleSign0);
        bottomText = findViewById(R.id.iv_buttomtitle);
        bottomTips = findViewById(R.id.iv_buttomtips);
        refreshBtn = findViewById(R.id.refreshbutton);
        refreshBtn.setOnClickListener(this);
        bottomTextErr = findViewById(R.id.iv_buttomtitle_err);

        btnBackLayout = findViewById(R.id.iv_title_back_layout);
        btnBackLayout.setOnClickListener(this);
        btnBack = findViewById(R.id.iv_title_back);
        listbtn_ly = findViewById(R.id.listbtn_ly);
        listbtn_ly.setOnClickListener(this);
        listbtn = findViewById(R.id.listbtn);
        listbtn.setOnClickListener(this);
        trace_list = findViewById(R.id.trace_list);
        trace_list.setOnClickListener(this);

        zoomout = findViewById(R.id.zoomout); // 放大地图按钮
        zoomout.setOnClickListener(this);
        zoomin = findViewById(R.id.zoomin); // 缩小地图按钮
        zoomin.setOnClickListener(this);

        mapScaleView = findViewById(R.id.mapScaleView);

        onedays = findViewById(R.id.onedays);
        onedays.setTextColor(getResources().getColor(R.color.color_16));
        onedays.setOnClickListener(this);
        threedays = findViewById(R.id.threedays);
        threedays.setOnClickListener(this);
        fivedays = findViewById(R.id.fivedays);
        fivedays.setOnClickListener(this);

        seek_bar = findViewById(R.id.seek_bar);
        seek_bar.setOnSeekBarChangeListener(this);
        last_point = findViewById(R.id.last_point);
        last_point.setOnClickListener(this);
        next_point = findViewById(R.id.next_point);
        next_point.setOnClickListener(this);

        initcalendar();
    }

    private void initcalendar() {
        calendarView_t = new calendarView(this, GoogleMapHistoryTraceActivity.this);
        calendarView_t.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                calendarSign.setImageDrawable(getResources().getDrawable(
                        R.drawable.open));
            }
        });
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            String time = data.getStringExtra("cur_point");
            int num = 0;
            if (time != null) {
                for (GooglePointInfo p : combineList) {
                    if (p.mTimeStamp.equals(time)) {
                        bottomInvalidate(p.mLatlng);
                        curMarker.setPosition(p.mLatlng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.mLatlng));
                        curMarkerPos = num;
                        if (linePoints.contains(p) && linePoints.size() > 1) {
                            updateLineAndmMarker(p);
                        }
                        break;
                    }
                    num++;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_layout:
                calendarSign.setImageDrawable(getResources().getDrawable(
                        R.drawable.close));

                setCalendarLocation();
                break;
            case R.id.iv_title_back_layout:
                finish();
                break;
            case R.id.zoomout:
                mMap.moveCamera(CameraUpdateFactory.zoomIn());
                setScaleText();
                break;
            case R.id.zoomin:
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                setScaleText();
                break;
            case R.id.refreshbutton:
                getHistoryTraceDataByDay(getThreeDaysString(theSelectDay));
                break;

            case R.id.onedays:
                if (isDaysHistory != 0) {
                    isDaysHistory = 0;
                    onedays.setTextColor(getResources().getColor(R.color.color_16));
                    threedays.setTextColor(getResources().getColor(R.color.color_7));
                    fivedays.setTextColor(getResources().getColor(R.color.color_7));
                    mapReset();
                    getDaysHistoryTrace();
                    if (moveLine != null) {
                        moveLine.remove();
                        moveLine = null;
                    }
                    linePoints.clear();
                }
                break;
            case R.id.threedays:
                if (isDaysHistory != 1) {
                    isDaysHistory = 1;
                    onedays.setTextColor(getResources().getColor(R.color.color_7));
                    threedays.setTextColor(getResources().getColor(R.color.color_16));
                    fivedays.setTextColor(getResources().getColor(R.color.color_7));
                    mapReset();
                    getDaysHistoryTrace();
                    if (moveLine != null) {
                        moveLine.remove();
                        moveLine = null;
                    }
                }
                break;
            case R.id.fivedays:
                if (isDaysHistory != 2) {
                    isDaysHistory = 2;
                    onedays.setTextColor(getResources().getColor(R.color.color_7));
                    threedays.setTextColor(getResources().getColor(R.color.color_7));
                    fivedays.setTextColor(getResources().getColor(R.color.color_16));
                    mapReset();
                    getDaysHistoryTrace();
                    if (moveLine != null) {
                        moveLine.remove();
                        moveLine = null;
                    }
                }
                break;

            case R.id.listbtn:
                if (combineList != null && combineList.size() > 0) {
                    ArrayList<TracePointInf> list = new ArrayList<>();
                    for (GooglePointInfo p : combineList) {
                        TracePointInf item = new TracePointInf();
                        item.mLat = p.mLatlng.latitude;
                        item.mlng = p.mLatlng.longitude;
                        item.mAddressDesc = p.mAddressDesc;
                        item.mTimeStamp = p.mTimeStamp;
                        item.iSosType = p.iSosType;
                        item.iEFIDType = p.iEFIDType;
                        item.radius = p.radius;
                        item.type = p.type;
                        item.efenceName = p.efenceName;
                        item.inteval = p.inteval;
                        item.loctype = p.loctype;
                        item.angle = p.angle;
                        list.add(item);
                    }
                    Intent it = new Intent(GoogleMapHistoryTraceActivity.this, HistoryTraceListActivity.class);
                    it.putParcelableArrayListExtra("list", list);
                    it.putExtra("ptime", combineList.get(curMarkerPos).mTimeStamp);
                    it.putExtra("days", isDaysHistory);
                    it.putExtra("title_time", theSelectDay);
                    startActivityForResult(it, 0);
                }
                break;
            case R.id.last_point:
                move_to_last();
                break;
            case R.id.next_point:
                move_to_next();
                break;
            default:
                break;

        }
    }

    @Override
    public void getItemDate(CustomDate item, int num, int action) {
        mapReset();
        Date date = new Date(item.getYear() - 1900, item.getMonth() - 1,
                item.getDay());
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        Calendar day = Calendar.getInstance();
        if (day.get(Calendar.YEAR) == date.getYear() + 1900
                && day.get(Calendar.MONTH) == date.getMonth()
                && day.get(Calendar.DAY_OF_MONTH) == date.getDate()) {
            titleView.setText(getResources().getString(R.string.today));
        } else {
            titleView.setText("  " + format.format(date) + "  ");
        }
        calendarView_t.showNextFromDown();
        final SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault());
        theSelectDay = formatDateKey.format(date);
        selectDate = date;
        if (titleView.getText().equals(getResources().getString(R.string.today))) {
            bottomOtherStatusInvalidate(BottomStatus.GETTING_DATA);
            getHistoryTraceDataByDay(getThreeDaysString(theSelectDay));
        } else {
            getListFromLocal(theSelectDay);
        }

        isDaysHistory = 0;
        onedays.setTextColor(getResources().getColor(R.color.color_16));
        threedays.setTextColor(getResources().getColor(R.color.color_7));
        fivedays.setTextColor(getResources().getColor(R.color.color_7));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mainTraceLine != null && linePoints.size() > 0) {
                initMoveLine();
                ArrayList<LatLng> list = new ArrayList<>();
                if (curTrackPosition < progress) {
                    for (int i = curTrackPosition + 1; i <= progress; i++) {
                        list.add(linePoints.get(i).mLatlng);
                    }
                    //animList.addAll(list);
                    moveList.addAll(list);
                } else if (curTrackPosition > progress) {
                    for (int i = progress + 1; i <= curTrackPosition && i < linePoints.size(); i++) {
                        list.add(linePoints.get(i).mLatlng);
                    }
                    //animList.removeAll(list);
                    moveList.removeAll(list);
                } else {
                    return;
                }
                //traceLine.setPoints(animList);
                if (moveList.size() > 1) {
                    moveLine.setPoints(moveList);
                } else {
                    moveLine.remove();
                    moveLine = null;
                }

                curMarker.setPosition(linePoints.get(progress).mLatlng);

                curTrackPosition = progress;
                curMarkerPos = curTrackPosition;

                bottomInvalidate(linePoints.get(progress).mLatlng);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(linePoints.get(progress).mLatlng, 16));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void confirmClick() {
        getHistoryTraceDataByDay(getThreeDaysString(theSelectDay));
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        // TODO Auto-generated method stub
        String type;
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        if (CloudBridgeUtil.RC_SUCCESS == rc) {
            switch (cid) {
                case CloudBridgeUtil.CID_TRACE_DATA_BY_DAY_RESP:
                    JSONObject pl = (JSONObject) respMsg
                            .get(CloudBridgeUtil.KEY_NAME_PL);
                    JSONObject list = (JSONObject) pl.get(CloudBridgeUtil.KEY_NAME_LIST);
                    LogUtil.d(TAG + "  " + "getHistoryTraceDataByDay end");
                    list = pl;
                    mAllPointInfo.clear();

                    initStatistics();
                    cntTotal = list.size();
                    LogUtil.d(TAG + "  " + "list.size(): " + cntTotal);
                    for (Map.Entry<String, Object> entry : list.entrySet()) {
                        String timeKey = entry.getKey();
                        String timeID = timeKey.substring(0, 8);

                        // Print the original points info,just for debug.
                        JSONObject obj = (JSONObject) entry.getValue();
                        JSONObject result = (JSONObject) obj.get("result");
                        type = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_TYPE);
                        // LogUtil.d(TAG+"  "+"Original points no sorted: ");
                        if (obj.get("status").equals("1")
                                && type != null) {
                            if (type.equals("0")) {
                                cntFail++;
                                LogUtil.d(TAG + "  " + "Fail time:" + timeKey.substring(0, 12));
                            } else if (type.equals("1")) {
                                cntGps++;
                            } else if (type.equals("2")) {
                                cntWifi++;
                            } else if (type.equals("3")) {
                                cntMix++;
                            } else if (type.equals("4")) {
                                cntCell++;
                            } else if (type.equals("5")) {
                                cntOther++;
                            } else if (type.equals("50")) {
                                cnGO++;
                            }
                        }

                        if (isDaysHistory == 0) {
                            if (isTheDayTrace(timeKey)) {
                                setTraceLocation((JSONObject) entry.getValue(), timeKey); // 是当天的点。
                            }
                        } else if (isDaysHistory == 1) {
                            if (isTheDaysTrace(timeKey, 1)) {
                                setTraceLocation((JSONObject) entry.getValue(), timeKey); // 是3天的点。
                            }
                        } else if (isDaysHistory == 2) {
                            if (isTheDaysTrace(timeKey, 2)) {
                                setTraceLocation((JSONObject) entry.getValue(), timeKey); // 是5天的点。
                            }
                        }
                        //if (theSelectDay.equals(timeID))
                        //setTraceLocation((JSONObject) entry.getValue(), timeKey); // 是当天的点。
                    }
                    LogUtil.d(TAG + "  " + theSelectDay + "  Total:" + cntTotal
                            + ",GPS:" + cntGps
                            + ",Wifi:" + cntWifi
                            + ",Cell:" + cntCell
                            + ",Mix:" + cntMix
                            + ",Other:" + cntOther
                            + ",Fail:" + cntFail);
                    mApp.sdcardLog(theSelectDay + "  Total:" + cntTotal
                            + ",GPS:" + cntGps
                            + ",Wifi:" + cntWifi
                            + ",Cell:" + cntCell
                            + ",Mix:" + cntMix
                            + ",Other:" + cntOther
                            + ",Fail:" + cntFail);

                    //if (mDebugTraceStatistics)
                    //HistoryTraceStatistics.saveDataToFile(theSelectDay, mApp.getCurUser().getFocusWatch().getEid(), mAllPointInfo, mApp.getCurUser().getFocusWatch().getNickname());
                    if (mAllPointInfo.size() > 1) { // 排序
                        Collections.sort(mAllPointInfo, comparator);
                    }
                    JSONObject plq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String[] date = (String[]) plq.get(CloudBridgeUtil.KEY_NAME_DATE);
                    saveTraceToDb(date);
                    initAngle();
                    dropLisetCreat();
                    curDayPointsCounter = mAllPointInfo.size();
                    // Print the sorted original points info,just for debug.
                    LogUtil.d(TAG + "  " + "Original points sorted: ");
                    // Print the sorted original points info,just for debug.

                    // 过滤点，距离小于IGNORE_DISTENCE删除
                    LogUtil.d(TAG + "  " + "Filter before: " + mAllPointInfo.size());
                    if (!mDebugShowAllDots) {
                        mAllPointSer.addAll(mAllPointInfo);
                        int srcsize = mAllPointInfo.size();
                        speedFilter();
                        speedf = srcsize - mAllPointInfo.size();
                        distanceFilter();
                        dis1 = srcsize - speedf - mAllPointInfo.size();
                        LogUtil.e("first dis filter :" + dis1);
                        cellPointFilter();
                        cell1 = srcsize - speedf - dis1 - mAllPointInfo.size();
                        LogUtil.e("cell filter :" + cell1);
                        testAngleFilter();
                        ang1 = srcsize - speedf - dis1 - cell1 - mAllPointInfo.size();
                        LogUtil.e("ang filter :" + ang1);
                        distanceFilter();
                        dis2 = srcsize - speedf - dis1 - cell1 - ang1 - mAllPointInfo.size();
                        LogUtil.e("second dis filter :" + dis2);
                        cellAngleFilter();
                        cellang = srcsize - speedf - dis1 - cell1 - ang1 - dis2 - mAllPointInfo.size();
                        LogUtil.e("cellang filter :" + cellang);
                        distanceFilter();
                        dis3 = srcsize - speedf - dis1 - cell1 - ang1 - dis2 - cellang - mAllPointInfo.size();
                        LogUtil.e("third dis filter :" + dis3);
                        cnFilter = srcsize - mAllPointInfo.size();
                    }
                    lineAndFadeListSeparateFromAllPoints();
                    LogUtil.d(TAG + "  " + "Filter after: " + mAllPointInfo.size());
                    drawTraceItem();
                    LogUtil.d(TAG + "  " + "drawTraceItem end");
                    // 历史点保存到数据库
                    if (mAllPointInfo.size() > 0) {
                        if (refreshBtn.getVisibility() == View.VISIBLE) {
                            //mHandler.sendEmptyMessage(2);
                            refreshBtn.setVisibility(View.GONE);
                        }
                        bottomInvalidate(mAllPointInfo.get(0).mLatlng);
                    } else {
                        bottomOtherStatusInvalidate(BottomStatus.NO_DATA);
                    }
                    if (loadingdlg != null && loadingdlg.isShowing())
                        loadingdlg.dismiss();
                    break;
                default:
                    break;
            }
        } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
            if (cid == CloudBridgeUtil.CID_TRACE_DATA_BY_DAY_RESP) {
                LogUtil.d(TAG + "  " + "getHistoryTraceDataByDay fail: " + rc);
                bottomOtherStatusInvalidate(BottomStatus.NETWORK_ERR);
                initSeekbar();
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(2, 2000);
            }
        } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY
                || rc == CloudBridgeUtil.RC_NOT_LOGIN) {
            if (cid == CloudBridgeUtil.CID_TRACE_DATA_BY_DAY_RESP) {
                LogUtil.d(TAG + "  " + "getHistoryTraceDataByDay fail: " + rc);
                bottomOtherStatusInvalidate(BottomStatus.NO_NET);
                initSeekbar();
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(2, 2000);
            }
        } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
            if (cid == CloudBridgeUtil.CID_TRACE_DATA_BY_DAY_RESP) {
                LogUtil.d(TAG + "  " + "getHistoryTraceDataByDay fail: " + rc);
                bottomOtherStatusInvalidate(BottomStatus.NO_DATA);
                initSeekbar();
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(2, 2000);
            }
        } else if (cid == CloudBridgeUtil.CID_TRACE_COUNTER_DATA_RESP) {
            LogUtil.d(TAG + "  " + "getTraceCounterGroupByDay fail: " + rc);
        }
    }

    private void getDaysHistoryTrace() {
        String[] dates = null;
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectDate);
        if (isDaysHistory == 1) {
            dates = new String[5];
            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
                String timestr = format.format(cal.getTime());
                String temp = TimeUtil.localToCHNTimestamp(timestr);
                dates[i] = temp.substring(0, 8);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else if (isDaysHistory == 2) {
            dates = new String[7];
            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
                String timestr = format.format(cal.getTime());
                String temp = TimeUtil.localToCHNTimestamp(timestr);
                dates[i] = temp.substring(0, 8);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else {
            dates = getThreeDaysString(theSelectDay);
        }
        getHistoryTraceDataByDay(dates);
    }

    private void getHistoryTraceDataByDay(String[] date) {
        LogUtil.d(TAG + "  " + "getHistoryTraceDataByDay begin");
        // theSelectDay = date;
        MyMsgData retrieve = new MyMsgData();
        retrieve.setTimeout(Const.RETRIEVE_TRACE_TIMEOUT);
        retrieve.setCallback(GoogleMapHistoryTraceActivity.this);
        // set msg info
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());//D292B851E89A82E0BE133EB3EA5B4066 curWatch.getEid()
        pl.put(CloudBridgeUtil.KEY_NAME_DATE, date);
        retrieve.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(
                CloudBridgeUtil.CID_TRACE_DATA_BY_DAY,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(retrieve);
            // layerWaiting.setVisibility(View.VISIBLE);
            if (loadingdlg != null && !loadingdlg.isShowing()) {
                loadingdlg.enableCancel(false);
                loadingdlg.changeStatus(1, getResources().getString(R.string.trace_getting_data));
                loadingdlg.show();
            }
        }
    }

    private String[] getThreeDaysString(String stamp) {
        String[] ret = null;
        Calendar cal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        Date d = TimeUtil.getDataFromTimeStamp(stamp + "000000000");
        Date d1 = TimeUtil.getDataFromTimeStamp(stamp + "235959000");
        cal.setTime(d);
        cal1.setTime(d1);
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String da1 = Format.format(cal.getTime());
        String da2 = Format.format(cal1.getTime());
        String dstr = TimeUtil.localToCHNTimestamp(da1).substring(0, 8);
        String dstr1 = TimeUtil.localToCHNTimestamp(da2).substring(0, 8);
        if (dstr.equals(dstr1)) {
            ret = new String[1];
            ret[0] = dstr;
        } else {
            ret = new String[2];
            ret[0] = dstr;
            ret[1] = dstr1;
        }
        return ret;
    }

    private boolean isTheDayTrace(String stamp) {
        boolean ret = false;
        String local = TimeUtil.chnToLocalTimestamp(stamp).substring(0, 8);

        if (theSelectDay.equals(local)) {
            ret = true;
        }
        return ret;
    }

    private void setTraceLocation(JSONObject pl, String timeKey) {
        GooglePointInfo pointInfo = new GooglePointInfo();
        if (pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP) == null) {
            pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timeKey);
        }
        pointInfo.mTimeStamp = ((String) pl
                .get(CloudBridgeUtil.KEY_NAME_TIMESTAMP));
        JSONObject efence = (JSONObject) pl.get("EFence");
        JSONObject result = (JSONObject) pl.get("result");
        if (pl.get("status").equals("1")
                && result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_LOCATION) != null) {
            if (result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_DESC) != null) {
                try {
                    pointInfo.mAddressDesc = (String) result
                            .get(CloudBridgeUtil.KEY_NAME_LOCAITON_DESC);
                } catch (Exception e) {
                    pointInfo.mAddressDesc = getResources().getString(R.string.history_trace_no_location);
                }
            } else {
                pointInfo.mAddressDesc = getResources().getString(R.string.history_trace_no_location);
            }
            if (result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_RADIUS) != null)
                pointInfo.radius = Integer.valueOf((String) result
                        .get(CloudBridgeUtil.KEY_NAME_LOCAITON_RADIUS));
            else
                pointInfo.radius = 10;
            StringBuilder latlng = new StringBuilder(
                    (String) result
                            .get(CloudBridgeUtil.KEY_NAME_LOCAITON_LOCATION));
            int i = latlng.indexOf(",");
            int j = latlng.indexOf("|");

            try {
                double longitude = Double.valueOf(latlng.substring(0, i));
                double latitude;
                if (j > 0) {
                    latitude = Double.valueOf(latlng.substring(i + 1, j));
                    // pointInfo.bGpsPoint = true ;
                } else {
                    latitude = Double.valueOf(latlng.substring(i + 1));
                    // pointInfo.bGpsPoint = false ;
                }
                if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
                    return;
                }

                if (result.containsKey(CloudBridgeUtil.KEY_NAME_REGION)) {
                    pointInfo.region = (Integer) result.get(CloudBridgeUtil.KEY_NAME_REGION);

                } else {
                    pointInfo.region = 460;
                }

                if (result.containsKey(CloudBridgeUtil.KEY_NAME_MAPTYPE)) {
                    pointInfo.map_type = (String) result.get(CloudBridgeUtil.KEY_NAME_MAPTYPE);
                }

                pointInfo.lat = latitude;
                pointInfo.lng = longitude;
                pointInfo.mLatlng = new LatLng(latitude, longitude);
                covertBd09ToWGS84(pointInfo);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                LogUtil.d("get location error,status=" + pl.get("status")
                        + " info=" + pl.get("info"));
                curDayPointsCounter = curDayPointsCounter - 1;
                e.printStackTrace();
                return;
            }

            if (efence != null) {
                pointInfo.iEFIDType = (Integer) efence.get("Type");
                pointInfo.efenceName = (String) efence.get("Name");
                handleEfence(efence, pointInfo.mTimeStamp, pointInfo);
            }

            if (pl.containsKey("in_zone")) {
                pointInfo.in_zone = (String) pl.get("in_zone");
            }
            if (pl.containsKey("sec_zone")) {
                JSONObject zone = (JSONObject) pl.get("sec_zone");
                handleEfence(zone, pointInfo.mTimeStamp, pointInfo);
            }

            if (pl.get(CloudBridgeUtil.KEY_NAME_SOS) != null)
                pointInfo.iSosType = (Integer) pl
                        .get(CloudBridgeUtil.KEY_NAME_SOS);

            if (pl.get(CloudBridgeUtil.KEY_NAME_LOCTYPE) != null) {
                pointInfo.loctype = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_LOCTYPE);
            }

            int type = 0;
            if (result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_TYPE) != null)
                type = Integer.valueOf((String) result
                        .get(CloudBridgeUtil.KEY_NAME_LOCAITON_TYPE));
            switch (type) {
                case 0:
                    pointInfo.type = "N"; // NO
                    break;

                case 1:
                    pointInfo.type = "G"; // GPS
                    break;

                case 2:
                    pointInfo.type = "W"; // WIFI
                    break;

                case 3:
                    pointInfo.type = "H"; // hun he
                    break;

                case 4:
                    pointInfo.type = "C"; // CELL
                    break;

                case 5:
                    pointInfo.type = "O"; // OTHER
                    break;
                case 50:
                    pointInfo.type = "GO"; //amap other
                    break;
                default:
                    pointInfo.type = "N";
                    break;
            }
        } else {
            LogUtil.d("get location error,status=" + pl.get("status")
                    + " info=" + pl.get("info"));
            curDayPointsCounter = curDayPointsCounter - 1;
            // 拿到无用的点
            return;
        }

        // mAllPointLocationData.add(location) ;
        mAllPointInfo.add(pointInfo);
    }

    private void covertBd09ToWGS84(GooglePointInfo info) {
        if (info.region == 466) { //TaiWan
            info.mLatlng = GPSUtil.double_to_latLng(GPSUtil.bd09_To_gps84(info.lat, info.lng));
        } else if (info.region == 460 || info.region == 461 || info.region == 454 || info.region == 455) { //Mainland
            info.mLatlng = GPSUtil.double_to_latLng(GPSUtil.bd09_To_Gcj02(info.lat, info.lng));
        } else {  //oversea

        }
    }

    private void handleEfence(JSONObject efence, String time, GooglePointInfo item) {
        int radius = (Integer) efence.get("Radius");
        String name = (String) efence.get("Name");
        String efid = (String) efence.get("EFID");
        Double lat = (Double) efence.get("Lat");
        Double lng = (Double) efence.get("Lng");
        Double lat_baidu = (Double) efence.get("bdLat");
        Double lng_baidu = (Double) efence.get("bdLng");
        String desc = (String) efence.get("Desc");
        boolean sameEfence = false;
        LatLng item_pos = new LatLng(lat, lng);
        for (int i = 0; i < listItem.size(); i++) {
            LatLng list_pos = new LatLng(listItem.get(i).sCenter_lat, listItem.get(i).sCenter_lng);
            if (list_pos.latitude == item_pos.latitude && list_pos.longitude == item_pos.longitude) {
                sameEfence = true;
                if (listItem.get(i).sRadius < radius) {
                    listItem.get(i).sRadius = radius;
                }
                break;
            }
        }

        SecurityZone securityzone = new SecurityZone();
        securityzone.keyEFID = efid;
        securityzone.sCenter_lat = lat;
        securityzone.sCenter_lng = lng;
        securityzone.sCenter_lat_baidu = lat_baidu;
        securityzone.sCenter_lng_baidu = lng_baidu;
        securityzone.sName = name;
        securityzone.sRadius = radius;
        securityzone.desc = desc;
        securityzone.timestamp = time;
        item.secInf = securityzone;
        if (!sameEfence) {
            listItem.add(securityzone);
        }
    }

    private boolean isTheDaysTrace(String stamp, int l) {
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyyMMddHHmmssSSS");//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        Calendar cal = Calendar.getInstance();
        Date d = TimeUtil.getDataFromTimeStamp(theSelectDay + "000000000");
        cal.setTime(d);
        String[] days = null;
        if (l == 1) {
            days = new String[3];
            for (int i = 0; i < 3; i++) {
                String temp = localFormater.format(cal.getTime());
                days[i] = temp.substring(0, 8);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else {
            days = new String[5];
            for (int i = 0; i < 5; i++) {
                String temp = localFormater.format(cal.getTime());
                days[i] = temp.substring(0, 8);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        String time = TimeUtil.chnToLocalTimestamp(stamp).substring(0, 8);
        for (int i = 0; i < days.length; i++) {
            if (time.equals(days[i])) {
                return true;
            }
        }
        return false;
    }

    private void saveTraceToDb(String[] date) {
        Calendar cal = Calendar.getInstance();
        final SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String now = formatDateKey.format(cal.getTime());
        for (int i = 0; i < date.length; i++) {
            if (now.equals(date[i])) {
                break;
            }
            JSONObject pl = new JSONObject();
            pl.put("time", date[i]);
            net.minidev.json.JSONArray arr = new net.minidev.json.JSONArray();
            pl.put("list", arr);
            for (int j = 0; j < mAllPointInfo.size(); j++) {
                if (mAllPointInfo.get(j).mTimeStamp.substring(0, 8).equals(date[i])) {
                    JSONObject item = new JSONObject();
                    item.put("mLat", mAllPointInfo.get(j).lat);
                    item.put("mLng", mAllPointInfo.get(j).lng);
                    item.put("angle", mAllPointInfo.get(j).angle);
                    item.put("direction", mAllPointInfo.get(j).direction);
                    item.put("efenceName", mAllPointInfo.get(j).efenceName);
                    item.put("iEFIDType", mAllPointInfo.get(j).iEFIDType);
                    item.put("inteval", mAllPointInfo.get(j).inteval);
                    item.put("iSosType", mAllPointInfo.get(j).iSosType);
                    item.put("loctype", mAllPointInfo.get(j).loctype);
                    item.put("mAddressDesc", mAllPointInfo.get(j).mAddressDesc);
                    item.put("mTimeStamp", mAllPointInfo.get(j).mTimeStamp);
                    item.put("radius", mAllPointInfo.get(j).radius);
                    item.put("speed", mAllPointInfo.get(j).speed);
                    item.put("type", mAllPointInfo.get(j).type);
                    item.put("visible", mAllPointInfo.get(j).visible);
                    item.put("drop", mAllPointInfo.get(j).drop);
                    item.put("in_zone", mAllPointInfo.get(j).in_zone);
                    item.put("mapType", mAllPointInfo.get(j).map_type);
                    item.put("region", mAllPointInfo.get(j).region);
                    if (mAllPointInfo.get(j).secInf != null) {
                        JSONObject secinf = new JSONObject();
                        secinf.put("secInf_lat", mAllPointInfo.get(j).secInf.sCenter_lat);
                        secinf.put("secInf_lng", mAllPointInfo.get(j).secInf.sCenter_lng);
                        secinf.put("secInf_lat_baidu", mAllPointInfo.get(j).secInf.sCenter_lat_baidu);
                        secinf.put("secInf_lng_baidu", mAllPointInfo.get(j).secInf.sCenter_lng_baidu);
                        secinf.put("secInf_radius", mAllPointInfo.get(j).secInf.sRadius);
                        secinf.put("secInf_id", mAllPointInfo.get(j).secInf.keyEFID);
                        secinf.put("secInf_name", mAllPointInfo.get(j).secInf.sName);
                        secinf.put("secInf_desc", mAllPointInfo.get(j).secInf.desc);
                        secinf.put("secInf_timestamp", mAllPointInfo.get(j).secInf.timestamp);
                        item.put("secInf", secinf);
                    }
                    arr.add(item);
                }
            }
            HistoryTraceDAO traceDB = HistoryTraceDAO.getInstance(this);
            if (traceDB.getTrace(curWatch.getEid(), date[i]) != null
                    && !traceDB.getTrace(curWatch.getEid(), date[i]).equals("")) {
                traceDB.deleteTrace(curWatch.getEid(), date[i]);
            }
            String str1 = traceDB.getTrace(curWatch.getEid(), date[i]);
            if (str1 != null && !str1.equals("")) {
                LogUtil.e(str1);
            }
            if (traceDB.getCount(curWatch.getEid()) > 6) {
                traceDB.deleteFirstTrace(curWatch.getEid());
            }
            if (arr.size() > 0) {
                HistoryTraceDAO.getInstance(this).addTrace(curWatch.getEid(), date[i], pl.toJSONString());
            }
        }
    }

    private void initAngle() {
        for (int i = 0; i < mAllPointInfo.size() - 1; i++) {
            mAllPointInfo.get(i).angle = getAngle(mAllPointInfo.get(i).mLatlng, mAllPointInfo.get(i + 1).mLatlng);
        }
    }

    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        LogUtil.e("angle = " + angle);
        return angle;
    }

    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;
    }

    private void dropLisetCreat() {
        for (int i = 1; i < mAllPointInfo.size(); i++) {
            if (mAllPointInfo.get(i).drop != -1000) {
                dropList.add(mAllPointInfo.get(i));
                mAllPointInfo.remove(i);
            }
        }
    }

    private void speedFilter() {
        if (mAllPointInfo.size() < 2) {
            LogUtil.e("speedFilter not enough points.");
            return;
        }
        for (int i = 0; i < mAllPointInfo.size() - 1; i++) {
            float dis = (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(i).mLatlng, mAllPointInfo.get(i + 1).mLatlng);
            long time1 = TimeUtil.getDataFromTimeStamp(mAllPointInfo.get(i + 1).mTimeStamp).getTime();
            long time2 = TimeUtil.getDataFromTimeStamp(mAllPointInfo.get(i).mTimeStamp).getTime();
            float hours = (time1 - time2) / (60f * 60f * 1000f);
            float speed = dis / hours;
            if (speed >= SPEED_FILTER_LIMIT) {
                mAllPointInfo.remove(i + 1);
            }
        }
    }

    private void distanceFilter() {
        int num = 0;
        filterPointList.clear();
        if (mAllPointInfo.size() > 1 && !mDebugShowAllDots) {
            for (int i = 1; i < mAllPointInfo.size() - 1; i++) {
                if (mAllPointInfo.get(i).iEFIDType == 1
                        || mAllPointInfo.get(i).iEFIDType == 2
                        || mAllPointInfo.get(i).iSosType == 1) {
                    continue;
                }
                int distance = (int) SphericalUtil.computeDistanceBetween(
                        mAllPointInfo.get(i).mLatlng,
                        mAllPointInfo.get(i - 1).mLatlng);
                if (distance < IGNORE_DISTENCE) {
                    String t1 = mAllPointInfo.get(i).type;
                    if (t1.equals("O") && mAllPointInfo.get(i).radius != 30) {
                        t1 = "S";
                    }
                    String t2 = mAllPointInfo.get(i - 1).type;
                    if (t2.equals("O") && mAllPointInfo.get(i - 1).radius != 30) {
                        t2 = "S";
                    }
                    switch (compareType(t1, t2)) {
                        case 0:
                            if (mAllPointInfo.get(i).radius > mAllPointInfo.get(i - 1).radius) {
                                filterPointList.add(mAllPointInfo.get(i));
                                mAllPointInfo.remove(i);
                                i--;
                                curDayPointsCounter = curDayPointsCounter - 1;
                                num++;
                            } else {
                                if (mAllPointInfo.get(i - 1).iEFIDType != 1 && mAllPointInfo.get(i - 1).iEFIDType != 2
                                        && mAllPointInfo.get(i - 1).iSosType != 1) {
                                    filterPointList.add(mAllPointInfo.get(i - 1));
                                    mAllPointInfo.remove(i - 1);
                                    i--;
                                    curDayPointsCounter = curDayPointsCounter - 1;
                                    num++;
                                }
                            }
                            break;
                        case 1:
                            filterPointList.add(mAllPointInfo.get(i));
                            mAllPointInfo.remove(i);
                            i--;
                            curDayPointsCounter = curDayPointsCounter - 1;
                            num++;
                            break;
                        case 2:
                            if (mAllPointInfo.get(i - 1).iEFIDType != 1 && mAllPointInfo.get(i - 1).iEFIDType != 2
                                    && mAllPointInfo.get(i - 1).iSosType != 1) {
                                filterPointList.add(mAllPointInfo.get(i - 1));
                                mAllPointInfo.remove(i - 1);
                                i--;
                                curDayPointsCounter = curDayPointsCounter - 1;
                                num++;
                            }
                            break;
                        case 3:
                            break;
                    }

                }
            }
        }
        LogUtil.e(TAG + "  " + "distance filtered num: " + num);
    }

    private int compareType(String t1, String t2) {
        int val_t1 = 0;
        int val_t2 = 0;
        if (t1.equals("G") || t1.equals("O")) {
            val_t1 = 4;
        } else if (t1.equals("H")) {
            val_t1 = 3;
        } else if (t1.equals("W")) {
            val_t1 = 2;
        } else if (t1.equals("C")) {
            val_t1 = 1;
        } else if (t1.equals("S")) {
            val_t1 = 0;
        }

        if (t2.equals("G") || t2.equals("O")) {
            val_t2 = 4;
        } else if (t2.equals("H")) {
            val_t2 = 3;
        } else if (t2.equals("W")) {
            val_t2 = 2;
        } else if (t2.equals("C")) {
            val_t2 = 1;
        } else if (t2.equals("S")) {
            val_t2 = 0;
        }

        if (val_t1 == val_t2) {
            if (val_t1 == 4) {
                if (t1.equals(t2)) {
                    return 0;
                } else {
                    return 3;
                }
            } else {
                return 0;
            }
        } else {
            if (val_t1 > val_t2) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    private void cellPointFilter() {
        int num = 0;
        if (mAllPointInfo.size() > 1 && !mDebugShowAllDots) {
            for (int i = 1; i < mAllPointInfo.size() - 1; i++) {
                if (mAllPointInfo.get(i).type.equals("C")) {
                    long tmp = TimeUtil.getMillisByTime(mAllPointInfo.get(i).mTimeStamp) -
                            TimeUtil.getMillisByTime(mAllPointInfo.get(i - 1).mTimeStamp);
                    long tmp1 = TimeUtil.getMillisByTime(mAllPointInfo.get(i + 1).mTimeStamp) -
                            TimeUtil.getMillisByTime(mAllPointInfo.get(i).mTimeStamp);
                    boolean timeReq = tmp <= 5 * 60 * 1000;
                    boolean tiemRReq1 = tmp1 <= 5 * 60 * 1000;
                    if (!mAllPointInfo.get(i - 1).type.equals("C") && !mAllPointInfo.get(i + 1).type.equals("C")
                            && mAllPointInfo.get(i).iEFIDType != 1 && mAllPointInfo.get(i).iEFIDType != 2
                            && mAllPointInfo.get(i).iSosType != 1 && (timeReq || tiemRReq1)) {
                        mAllPointInfo.remove(i);
                        num++;
                    }
                }
            }
            LogUtil.e(TAG + "cellPointFilter num : " + num);
        }
    }

    private void testAngleFilter() {
        int num = 0;
        // filterPointList.clear();
        for (int i = 0; i < mAllPointInfo.size() - 1; i++) {
            mAllPointInfo.get(i).angle =
                    getAngle(mAllPointInfo.get(i).mLatlng, mAllPointInfo.get(i + 1).mLatlng);
        }
        if (mAllPointInfo.size() > 2) {
            Double angle = Math.abs(mAllPointInfo.get(mAllPointInfo.size() - 3).angle - mAllPointInfo.get(mAllPointInfo.size() - 2).angle);
            if (angle > 150 && angle < 210) {//&& !mAllPointInfo.get(mAllPointInfo.size() - 2).type.equals("G")
                num++;
                //filterPointList.add(mAllPointInfo.get(mAllPointInfo.size() - 2));
                mAllPointInfo.remove(mAllPointInfo.size() - 2);
                mAllPointInfo.get(mAllPointInfo.size() - 2).angle =
                        getAngle(mAllPointInfo.get(mAllPointInfo.size() - 2).mLatlng, mAllPointInfo.get(mAllPointInfo.size() - 1).mLatlng);
            }
        }
        for (int j = 0; j < mAllPointInfo.size() - 3; j++) {
            Double angle = Math.abs(mAllPointInfo.get(j).angle - mAllPointInfo.get(j + 1).angle);
            Double angleN = Math.abs(mAllPointInfo.get(j + 1).angle - mAllPointInfo.get(j + 2).angle);
            if (angle > 150 && angle < 210 && mAllPointInfo.get(j + 1).iEFIDType != 1
                    && mAllPointInfo.get(j + 1).iSosType != 1) {//&& !mAllPointInfo.get(j + 1).type.equals("G") && mAllPointInfo.get(j + 1).iEFIDType != 2
                if (angleN > 35 || mAllPointInfo.get(j + 1).type.equals("C")) {
                    if (confirmAngleFilterPoint(mAllPointInfo.get(j + 1), j + 1)) {
                        num++;
                        //filterPointList.add(mAllPointInfo.get(j + 1));
                        boolean needReMoveNext = false;
                        if (mAllPointInfo.get(j + 1).iEFIDType == 2 && mAllPointInfo.get(j + 2).iEFIDType == 1) {
                            needReMoveNext = true;
                        }
                        mAllPointInfo.remove(j + 1);
                        if (needReMoveNext) mAllPointInfo.remove(j + 1);

                        mAllPointInfo.get(j).angle =
                                getAngle(mAllPointInfo.get(j).mLatlng, mAllPointInfo.get(j + 1).mLatlng);
                        j--;
                    }
                } else {
                    int dispiao = (int) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(j).mLatlng, mAllPointInfo.get(j + 1).mLatlng) / 4;
                    if (dispiao > 300) dispiao = 300;
                    int npointnum = 0;
                    for (GooglePointInfo p : mAllPointSer) {
                        int distance = (int) SphericalUtil.computeDistanceBetween(p.mLatlng, mAllPointInfo.get(j + 1).mLatlng);
                        if (distance < dispiao) {
                            npointnum++;
                        }
                    }
                    if (npointnum <= 2) {
                        num++;
                        //filterPointList.add(mAllPointInfo.get(j + 1));
                        mAllPointInfo.remove(j + 1);
                        mAllPointInfo.get(j).angle =
                                getAngle(mAllPointInfo.get(j).mLatlng, mAllPointInfo.get(j + 1).mLatlng);
                        j--;
                    }
                }
            }
        }
    }

    private boolean confirmAngleFilterPoint(GooglePointInfo point, int nump) {
        boolean ret = true;
        int num = 0;
        for (int i = 0; i < filterPointList.size(); i++) {
            long time = Math.abs(TimeUtil.getMillisByTime(point.mTimeStamp) - TimeUtil.getMillisByTime(filterPointList.get(i).mTimeStamp));
            if (SphericalUtil.computeDistanceBetween(point.mLatlng, filterPointList.get(i).mLatlng) <= IGNORE_DISTENCE
                    && time / 1000 / 60 <= CONFIRM_ANGLEFILTER_TIME) {
                num++;
            }
        }
        if (num >= 1) {
            ret = false;
        }
        if (!ret) {
            ret = distanceAngleFilterJudgement(nump);
        }
        return ret;
    }

    private boolean distanceAngleFilterJudgement(int num) {
        boolean ret = false;
        float preDis = 0, lastDis = 0;
        int preSeg = 0, lastSeg = 0;
        if (num - 1 < DISTANCE_ANGELEFILTER_POINTS / 2) {
            for (int i = 0; i < num - 1; i++) {
                preDis = preDis +
                        (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(i).mLatlng, mAllPointInfo.get(i + 1).mLatlng);
            }
            preSeg = num - 1;
        } else {
            for (int i = 0; i < DISTANCE_ANGELEFILTER_POINTS / 2; i++) {
                preDis = preDis +
                        (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(num - 1 - i).mLatlng,
                                mAllPointInfo.get(num - 1 - (i + 1)).mLatlng);
            }
            preSeg = DISTANCE_ANGELEFILTER_POINTS / 2 - 1;
        }
        int size = mAllPointInfo.size() - 1 - (num + 1);
        int lastNum = 0;
        if (size < DISTANCE_ANGELEFILTER_POINTS / 2) {
            lastNum = size;
            lastSeg = size;
        } else {
            lastNum = DISTANCE_ANGELEFILTER_POINTS / 2;
            lastSeg = DISTANCE_ANGELEFILTER_POINTS / 2 - 1;
        }
        for (int i = 0; i < lastNum; i++) {
            lastDis = lastDis +
                    (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(num + 1 + i).mLatlng,
                            mAllPointInfo.get(num + 1 + i + 1).mLatlng);
        }
        float averageDis = (preDis + lastDis) / (preSeg + lastSeg);
        float prePointDis = (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(num).mLatlng,
                mAllPointInfo.get(num - 1).mLatlng);
        float lastPointDis = (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(num).mLatlng,
                mAllPointInfo.get(num + 1).mLatlng);
        if (averageDis * DISTANCE_ANGELEFILTER_MULTIPLE <= prePointDis && averageDis * 7 <= lastPointDis) {
            ret = true;
        }
        return ret;
    }

    private void cellAngleFilter() {
        ArrayList<GooglePointInfo> pendList = new ArrayList<GooglePointInfo>();
        boolean last_limit = false;
        for (int i = 1; i < mAllPointInfo.size() - 2; i++) {
            long t = TimeUtil.getMillisByTime(mAllPointInfo.get(i + 1).mTimeStamp)
                    - TimeUtil.getMillisByTime(mAllPointInfo.get(i).mTimeStamp);
            float dis = (float) SphericalUtil.computeDistanceBetween(mAllPointInfo.get(i).mLatlng, mAllPointInfo.get(i + 1).mLatlng);
            boolean limit = false;
            if (t <= 5 * 60 * 1000 && dis < CELL_ANGLE_FILTER_DISTANCE) {
                limit = true;
                last_limit = limit;
            }
            if (mAllPointInfo.get(i).type.equals("C") && mAllPointInfo.get(i + 1).type.equals("C")
                    && limit) {
                pendList.add(mAllPointInfo.get(i));
                int count = pendList.size();
                if (count < 2) {
                    continue;
                } else if (count == 3) { //pendList里达到3个点的数据，就需要做多基站点角度过滤
                    pendList.add(0, mAllPointInfo.get(i - count));
                    pendList.add(mAllPointInfo.get(i + 1));
                    if (cellAngFilterListHandle(pendList)) {
                        pendList.remove(0);
                        pendList.remove(pendList.size() - 1);
                        mAllPointInfo.removeAll(pendList);
                        i -= count;
                        mAllPointInfo.get(i).angle = getAngle(mAllPointInfo.get(i).mLatlng,
                                mAllPointInfo.get(i + 1).mLatlng);
                    }
                    pendList.clear();
                }
            } else {
                if (mAllPointInfo.get(i).type.equals("C") && limit) {
                    pendList.add(mAllPointInfo.get(i));
                    int count = pendList.size();
                    if (count > 1) {  //不符合条件了，检查pendList里是否有数据，有超过1个点的数据，做多基站点角度过滤
                        pendList.add(0, mAllPointInfo.get(i - count));
                        pendList.add(mAllPointInfo.get(i + 1));
                        if (cellAngFilterListHandle(pendList)) {
                            pendList.remove(0);
                            pendList.remove(pendList.size() - 1);
                            mAllPointInfo.removeAll(pendList);
                            i -= count;
                            mAllPointInfo.get(i).angle = getAngle(mAllPointInfo.get(i).mLatlng,
                                    mAllPointInfo.get(i + 1).mLatlng);
                        }
                    }
                } else {
                    int count = pendList.size();
                    if (count > 1) {  //不符合条件了，检查pendList里是否有数据，有超过1个点的数据，做多基站点角度过滤
                        pendList.add(0, mAllPointInfo.get(i - count - 1));
                        pendList.add(mAllPointInfo.get(i));
                        if (cellAngFilterListHandle(pendList)) {
                            pendList.remove(0);
                            pendList.remove(pendList.size() - 1);
                            mAllPointInfo.removeAll(pendList);
                            i -= count;
                            mAllPointInfo.get(i).angle = getAngle(mAllPointInfo.get(i).mLatlng,
                                    mAllPointInfo.get(i + 1).mLatlng);
                        }
                    }
                }

                pendList.clear();
            }
        }
        if (pendList.size() > 0) {
            int index = mAllPointInfo.size() - 2;
            if (mAllPointInfo.get(index).type.equals("C") && last_limit) {
                pendList.add(mAllPointInfo.get(index));
                int count = pendList.size();
                if (count > 1) {  //不符合条件了，检查pendList里是否有数据，有超过1个点的数据，做多基站点角度过滤
                    pendList.add(0, mAllPointInfo.get(index - count));
                    pendList.add(mAllPointInfo.get(index + 1));
                    if (cellAngFilterListHandle(pendList)) {
                        pendList.remove(0);
                        pendList.remove(pendList.size() - 1);
                        mAllPointInfo.removeAll(pendList);
                        index -= count;
                        mAllPointInfo.get(index).angle = getAngle(mAllPointInfo.get(index).mLatlng,
                                mAllPointInfo.get(index + 1).mLatlng);
                    }
                }
            } else {
                int count = pendList.size();
                if (count > 1) {  //不符合条件了，检查pendList里是否有数据，有超过1个点的数据，做多基站点角度过滤
                    pendList.add(0, mAllPointInfo.get(index - count - 1));
                    pendList.add(mAllPointInfo.get(index));
                    if (cellAngFilterListHandle(pendList)) {
                        pendList.remove(0);
                        pendList.remove(pendList.size() - 1);
                        mAllPointInfo.removeAll(pendList);
                        index -= count;
                        mAllPointInfo.get(index).angle = getAngle(mAllPointInfo.get(index).mLatlng,
                                mAllPointInfo.get(index + 1).mLatlng);
                    }
                }
            }
        }
    }

    private boolean cellAngFilterListHandle(ArrayList<GooglePointInfo> pendList) {
        boolean ret = false;
        double lat = 0, lon = 0;
        for (int i = 1; i < pendList.size() - 1; i++) {
            if (pendList.get(i).iEFIDType == 1 || pendList.get(i).iEFIDType == 2
                    || pendList.get(i).iSosType == 1) {
                return ret;
            }
            lat += pendList.get(i).mLatlng.latitude;
            lon += pendList.get(i).mLatlng.longitude;
        }
        long size = pendList.size() - 2;
        LatLng virtual = new LatLng(lat / size, lon / size);
        GooglePointInfo item = new GooglePointInfo();
        item.mLatlng = virtual;
        item.angle = getAngle(item.mLatlng, pendList.get(pendList.size() - 1).mLatlng);
        double p1Ang = getAngle(pendList.get(0).mLatlng, item.mLatlng);
        double angle = Math.abs(p1Ang - item.angle);
        double angleN = Math.abs(item.angle - pendList.get(pendList.size() - 1).angle);
        if (angle > 150 && angle < 210) {
            if (angleN > 35) {
                ret = true;
            } else {
                int dispiao = (int) SphericalUtil.computeDistanceBetween(pendList.get(pendList.size() - 1).mLatlng
                        , item.mLatlng) / 4;
                if (dispiao > 300) dispiao = 300;
                int npointnum = 0;
                for (GooglePointInfo p : mAllPointSer) {
                    int distance = (int) SphericalUtil.computeDistanceBetween(p.mLatlng, item.mLatlng);
                    if (distance < dispiao) {
                        npointnum++;
                    }
                }
                if (npointnum <= 2) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    private void lineAndFadeListSeparateFromAllPoints() {
        linePoints.clear();
        fadePoints.clear();
        combineList.clear();
        for (int i = 0; i < mAllPointInfo.size() - 1; i++) {
            if (mAllPointInfo.get(i).iEFIDType == 1 || mAllPointInfo.get(i).iEFIDType == 2) {
                linePoints.add(mAllPointInfo.get(i));
                continue;
            }
            if (!mAllPointInfo.get(i).in_zone.equals(" ") && mAllPointInfo.get(i + 1).iEFIDType != 2
                    && mAllPointInfo.get(i + 1).iEFIDType != 1) {
                if (mAllPointInfo.get(i).iEFIDType != 1 && mAllPointInfo.get(i).iEFIDType != 2) {
                    fadePoints.add(mAllPointInfo.get(i));
                } else {
                    linePoints.add(mAllPointInfo.get(i));
                }
            } else {
                linePoints.add(mAllPointInfo.get(i));
            }
        }

        linePoints.add(mAllPointInfo.get(mAllPointInfo.size() - 1));

        combineList.addAll(linePoints);
        combineList.addAll(fadePoints);
    }

    private void bottomInvalidate(LatLng mLatlng) {
        String address;
        String tips = "";
        int iDesPosition = 0;

        bottomTextErr.setVisibility(View.GONE);
        bottomText.setVisibility(View.VISIBLE);
        //trace_list.setVisibility(View.VISIBLE);
        bottomTips.setVisibility(View.VISIBLE);

        for (int i = 0; i < mAllPointInfo.size(); i++) {
            if (mLatlng == mAllPointInfo.get(i).mLatlng) {
                iDesPosition = i;
                break;
            }
        }
        if (iDesPosition < mAllPointInfo.size()) {
            if (mAllPointInfo.get(iDesPosition).iEFIDType == 1) {
                bottomImg.setImageDrawable(littleSign0);
                if (mAllPointInfo.get(iDesPosition).efenceName != null)
                    tips = getResources()
                            .getString(R.string.trace_bottom_tips1)
                            + mAllPointInfo.get(iDesPosition).efenceName;
            }

            if (mAllPointInfo.get(iDesPosition).iEFIDType == 2) {
                bottomImg.setImageDrawable(littleSign0);
                if (mAllPointInfo.get(iDesPosition).efenceName != null)
                    tips = getResources()
                            .getString(R.string.trace_bottom_tips3)
                            + mAllPointInfo.get(iDesPosition).efenceName;
            }

            if (mAllPointInfo.get(iDesPosition).iSosType == 1) {
                bottomImg.setImageDrawable(littleSign0);
                tips = getResources()
                        .getString(R.string.trace_bottom_tips2);
            }
            if (mAllPointInfo.get(iDesPosition).iEFIDType != 1
                    && mAllPointInfo.get(iDesPosition).iEFIDType != 2
                    && mAllPointInfo.get(iDesPosition).iSosType != 1) {
                bottomImg.setImageDrawable(littleSign0);
                if (mAllPointInfo.get(iDesPosition).efenceName != null)
                    tips = getResources()
                            .getString(R.string.trace_bottom_tips0)
                            + mAllPointInfo.get(iDesPosition).efenceName;
            }
            if (mDebugShowAllDots) {
                if (iDesPosition == 0) {
                    address = "  Total:" + cntTotal
                            + ",G:" + cntGps
                            + ",W:" + cntWifi
                            + ",C:" + cntCell
                            + ",H:" + cntMix
                            + ",O:" + cntOther
                            + ",Fail:" + cntFail
                            + ",GO:" + cnGO
                            + ",Filter:" + cnFilter;
                } else {
                    address = mAllPointInfo.get(iDesPosition).mAddressDesc + " | "
                            + mAllPointInfo.get(iDesPosition).type
                            + mAllPointInfo.get(iDesPosition).radius;
                }
            } else {
                address = mAllPointInfo.get(iDesPosition).mAddressDesc;
            }

            if (mApp.getIntValue("filter_show", 0) == 1) {
                address = mAllPointInfo.get(iDesPosition).mAddressDesc
                        + " dis1:" + dis1
                        + " cell:" + cell1
                        + " ang:" + ang1
                        + " dis2:" + dis2
                        + " speedf:" + speedf;
            }

            String str = address;
            int lenth = str.length();
            if (false) {
                //getResources().getDimensionPixelSize(R.dimen.MyTextSize4)
                bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
                bottomTips.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            } else {
                bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.MyTextSize2));
                bottomTips.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.MyTextSize3));
            }
            bottomText.setText(str);

            // bottomText.setText("安慧北里逸园8号楼的阳光新干线小区");
            String timeloc = TimeUtil.chnToLocalTimestamp(mAllPointInfo.get(iDesPosition).mTimeStamp);
            String date = timeStrHandle(timeloc);
            String time = mAllPointInfo.get(iDesPosition).mTimeStamp.substring(
                    8, 10)
                    + ":"
                    + mAllPointInfo.get(iDesPosition).mTimeStamp
                    .substring(10, 12);
            if (tips.equals("")) {
                String intevalStr = "";
                if (mAllPointInfo.get(iDesPosition).inteval != -1) {
                    int hour = mAllPointInfo.get(iDesPosition).inteval / 60;
                    int min = mAllPointInfo.get(iDesPosition).inteval % 60;
                    intevalStr = getResources().getString(R.string.history_stay_time) + hour +
                            getResources().getString(R.string.hour) + min + getResources().getString(R.string.minute);
                }
                bottomTips.setText(date + intevalStr);
            } else
                bottomTips.setText(date + " | " + tips);
        }
    }

    private String timeStrHandle(String timestr) {
        String time = timestr.substring(8, 10) + ":"
                + timestr.substring(10, 12);
        Calendar calstr = Calendar.getInstance();
        calstr.setTime(TimeUtil.getDataFromTimeStamp(timestr));
        Calendar calnow = Calendar.getInstance();
        if (calstr.get(Calendar.MONTH) != calnow.get(Calendar.MONTH)) {
            return TimeUtil.getYear(timestr).replace("   ", " ").substring(5, 17);
        }
        int day = calnow.get(Calendar.DAY_OF_MONTH);
        calnow.add(Calendar.DAY_OF_MONTH, -1);
        int lastday = calnow.get(Calendar.DAY_OF_MONTH);
        calnow.add(Calendar.DAY_OF_MONTH, -1);
        int last2day = calnow.get(Calendar.DAY_OF_MONTH);
        if (calstr.get(Calendar.DAY_OF_MONTH) == day) {
            return getResources().getString(R.string.today) + " " + time;
        } else if (calstr.get(Calendar.DAY_OF_MONTH) == lastday) {
            return getResources().getString(R.string.yesterday) + " " + time;
        } else if (calstr.get(Calendar.DAY_OF_MONTH) == last2day) {
            return getResources().getString(R.string.day_before_yesterday) + " " + time;
        } else {
            return TimeUtil.getYear(timestr).replace("   ", " ").substring(5, 17);
        }

    }

    private void bottomOtherStatusInvalidate(BottomStatus status) {
        bottomTextErr.setVisibility(View.VISIBLE);
        bottomText.setVisibility(View.GONE);
        trace_list.setVisibility(View.GONE);
        bottomTips.setVisibility(View.GONE);
        bottomTips.setText("");
        if (status == BottomStatus.NO_DATA) {
            bottomImg.setImageDrawable(getResources().getDrawable(R.drawable.failure_2));
            bottomTextErr.setText(getResources().getString(R.string.trace_bottom_no_data));
            bottomTips.setText("");
        } else if (status == BottomStatus.GETTING_DATA) {
            bottomTextErr.setText(getResources().getString(R.string.trace_bottom_data_getting));
            bottomTips.setText("");
        } else if (status == BottomStatus.NETWORK_ERR) {
            bottomImg.setImageDrawable(getResources().getDrawable(R.drawable.failure_2));
            bottomTextErr.setText(getResources().getString(R.string.network_error_prompt));
            bottomTips.setText(getResources().getString(R.string.trace_retry_prompt_msg));
            refreshBtn.setVisibility(View.VISIBLE);
        } else if (status == BottomStatus.NO_NET) {
            bottomImg.setImageDrawable(getResources().getDrawable(R.drawable.failure_2));
            bottomTextErr.setText(getResources().getString(R.string.network_error_prompt));
            refreshBtn.setVisibility(View.VISIBLE);
            bottomTips.setText("");
        }
    }

    private void initStatistics() {
        cntGps = 0;
        cntCell = 0;
        cntWifi = 0;
        cntMix = 0;
        cntFail = 0;
        cntOther = 0;
        cntTotal = 0;
        cnFilter = 0;
        dis1 = 0;
        cell1 = 0;
        ang1 = 0;
        dis2 = 0;
        speedf = 0;
    }

    private void devPointsTypeStatistics(String type) {
        if (type.equals("G")) {
            cntGps++;
        } else if (type.equals("W")) {
            cntWifi++;
        } else if (type.equals("H")) {
            cntMix++;
        } else if (type.equals("C")) {
            cntCell++;
        } else if (type.equals("N")) {
            cntFail++;
        } else if (type.equals("O")) {
            cntOther++;
        } else if (type.equals("GO")) {
            cnGO++;
        }
    }

    private void drawTraceItem() {
        if (mAllPointInfo.size() <= 0) {
            LogUtil.e("There is not any trace point.");
            return;
        }
        drawMarkers();
        drawLine();
        drawSecurityZone();

        initSeekbar();
    }

    private void drawMarkers() {
        //line marker
        int lineMarkerSize = linePoints.size();
        if (lineMarkerSize > 0) {
            for (int i = 0; i < linePoints.size() - 1; i++) {
                drawAngleMarker(linePoints.get(i), linePoints.get(i + 1));
            }
            drawEndMarker(linePoints.get(lineMarkerSize - 1));
        }

        //fade marker
        int fadeMarkerSize = fadePoints.size();
        if (fadeMarkerSize > 0) {
            for (int i = 0; i < fadePoints.size(); i++) {
                drawFadeMarker(fadePoints.get(i));
            }
        }

        //current
        if (linePoints.size() > 0) {
            drawCurMarker(linePoints.get(0));
        } else {
            drawCurMarker(mAllPointInfo.get(0));
        }

        //make all Marker
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(1, 200);
        }
    }


    /**
     * draw marker with direction
     *
     * @param point     the infomation of marker
     * @param nextpoint the infomation of next point
     */
    private void drawAngleMarker(GooglePointInfo point, GooglePointInfo nextpoint) {
        MarkerOptions options = new MarkerOptions();
        options.flat(true).anchor(0.5f, 0.5f).zIndex(2f);
        if (point.iEFIDType == 1 || point.iEFIDType == 2) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_2));
        }
        if (point.iSosType == 1) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_3));
        }
        if (point.iEFIDType != 1 && point.iEFIDType != 2 && point.iSosType != 1) {
            if (point.loctype == 1) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.track_point));
            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_1));
            }
        }
        options.position(point.mLatlng);
        options.rotation((float) getAngle(point.mLatlng, nextpoint.mLatlng));
        Marker mk = mMap.addMarker(options);
        mk.setSnippet(point.mTimeStamp);
        allMarkers.add(mk);
    }

    /**
     * draw line end marker
     *
     * @param point the infomation of marker
     */
    private void drawEndMarker(GooglePointInfo point) {
        MarkerOptions options = new MarkerOptions();
        options.flat(true).anchor(0.5f, 0.5f).zIndex(2f);
        if (point.iEFIDType == 1 || point.iEFIDType == 2) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_5));
        }
        if (point.iSosType == 1) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_6));
        }
        if (point.iEFIDType != 1 && point.iEFIDType != 2 && point.iSosType != 1) {
            if (point.loctype == 1) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.track_end_point));
            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_4));
            }
        }
        options.position(point.mLatlng);
        Marker mk = mMap.addMarker(options);
        mk.setSnippet(point.mTimeStamp);
        allMarkers.add(mk);
    }

    /**
     * draw fade point marker
     *
     * @param point the infomation of fade marker
     */
    private void drawFadeMarker(GooglePointInfo point) {
        MarkerOptions options = new MarkerOptions();
        options.flat(true).anchor(0.5f, 0.5f);
        options.position(point.mLatlng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.starting_point_1_fade));
        options.snippet(point.mTimeStamp);
        Marker mk = mMap.addMarker(options);
        fadeMarkers.add(mk);
        allMarkers.add(mk);
    }

    private void drawCurMarker(GooglePointInfo point) {
        MarkerOptions options = new MarkerOptions();
        options.flat(true).anchor(0.5f, 0.5f);
        options.position(point.mLatlng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tumi));
        options.zIndex(3.0f);
        curMarker = mMap.addMarker(options);
    }

    private void drawLine() {
        if (linePoints.size() <= 0) {
            LogUtil.e("There is not any point in line array.");
            return;
        }
        PolylineOptions options = new PolylineOptions();
        options.geodesic(true);
        options.color(0xDB6AE9C8);
        options.width(16);
        options.zIndex(1.0f);
        for (int i = 0; i < linePoints.size(); i++) {
            options.add(linePoints.get(i).mLatlng);
        }
        mainTraceLine = mMap.addPolyline(options);
    }

    private void drawSecurityZone() {
        for (int i = 0; i < listItem.size(); i++) {
            CircleOptions options = new CircleOptions();
            LatLng pos = new LatLng(listItem.get(i).sCenter_lat_baidu, listItem.get(i).sCenter_lng_baidu);
            options.center(pos);
            options.radius(listItem.get(i).sRadius);
            options.fillColor(0x1afcc634);
            options.strokeColor(0xfffcc634);
            options.strokeWidth(2);
            options.zIndex(4.0f);
            mMap.addCircle(options);
        }
    }

    private void allMarkersShowInScreen() {
        if (mAllPointInfo.size() == 0) {
            return;
        }
        if (mAllPointInfo.size() == 1) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mAllPointInfo.get(0).mLatlng, 10));
        }
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (int i = 0; i < mAllPointInfo.size(); i++) {
            bounds.include(mAllPointInfo.get(i).mLatlng);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20));
    }

    private void setScaleText() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        LogUtil.i(TAG + " cameraPosition.zoom = " + cameraPosition.zoom);
        mapScaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    private void getListFromLocal(String date) {
        mAllPointInfo.clear();
        HistoryTraceDAO traceDB = HistoryTraceDAO.getInstance(this);
        String str1 = traceDB.getTrace(curWatch.getEid(), date);
        if (str1 != null && !str1.equals("")) {
            initStatistics();
            JSONObject pl = (JSONObject) JSONValue.parse(str1);
            net.minidev.json.JSONArray list = (net.minidev.json.JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                GooglePointInfo item = new GooglePointInfo();
                JSONObject obj = (JSONObject) list.get(i);
                item.direction = (Double) obj.get("direction");
                item.mTimeStamp = (String) obj.get("mTimeStamp");
                item.iEFIDType = (Integer) obj.get("iEFIDType");
                item.loctype = (Integer) obj.get("loctype");
                item.angle = (Double) obj.get("angle");
                item.efenceName = (String) obj.get("efenceName");
                item.inteval = (Integer) obj.get("inteval");
                item.iSosType = (Integer) obj.get("iSosType");
                item.mAddressDesc = (String) obj.get("mAddressDesc");

                if (obj.containsKey("region")) {
                    item.region = (Integer) obj.get("region");
                } else {
                    item.region = 460;
                }

                if (obj.containsKey("mapType")) {
                    item.map_type = (String) obj.get("mapType");
                } else {
                    item.map_type = "2";
                }

                double latitude = (Double) obj.get("mLat");
                double longitude = (Double) obj.get("mLng");
                item.lat = latitude;
                item.lng = longitude;
                item.mLatlng = new LatLng(latitude, longitude);

                //item.mLatlng = new LatLng((Double) obj.get("mLat"), (Double) obj.get("mLng"));
                item.radius = (Integer) obj.get("radius");
                item.speed = (Double) obj.get("speed");
                item.type = (String) obj.get("type");
                item.visible = (Boolean) obj.get("visible");
                if (obj.containsKey("drop")) {
                    item.drop = (int) obj.get("drop");
                }
                item.in_zone = (String) obj.get("in_zone");
                if (obj.containsKey("secInf")) {
                    JSONObject secinf = (JSONObject) obj.get("secInf");
                    SecurityZone secInf = new SecurityZone();
                    secInf.desc = (String) secinf.get("secInf_desc");
                    secInf.keyEFID = (String) secinf.get("secInf_id");
                    secInf.sCenter_lat = (Double) secinf.get("secInf_lat");
                    secInf.sCenter_lng = (Double) secinf.get("secInf_lng");
                    secInf.sCenter_lat_baidu = (Double) secinf.get("secInf_lat_baidu");
                    secInf.sCenter_lng_baidu = (Double) secinf.get("secInf_lng_baidu");
                    secInf.sName = (String) secinf.get("secInf_name");
                    secInf.sRadius = (Integer) secinf.get("secInf_radius");
                    secInf.timestamp = (String) secinf.get("secInf_timestamp");
                    item.secInf = secInf;
                    handleEfenceFromLocal(secInf);
                }
                mAllPointInfo.add(item);
                devPointsTypeStatistics(item.type);
            }
            if (list.size() == 0) {
                bottomOtherStatusInvalidate(BottomStatus.GETTING_DATA);
                getHistoryTraceDataByDay(getThreeDaysString(date));
                return;
            }
            cntTotal = mAllPointInfo.size();
            initAngle();
            dropLisetCreat();
            curDayPointsCounter = mAllPointInfo.size();
            if (!mDebugShowAllDots) {
                mAllPointSer.addAll(mAllPointInfo);
                int srcsize = mAllPointInfo.size();
                speedFilter();
                speedf = srcsize - mAllPointInfo.size();
                distanceFilter();
                dis1 = srcsize - speedf - mAllPointInfo.size();
                LogUtil.e("first dis filter :" + dis1);
                cellPointFilter();
                cell1 = srcsize - speedf - dis1 - mAllPointInfo.size();
                LogUtil.e("cell filter :" + cell1);
                testAngleFilter();
                ang1 = srcsize - speedf - dis1 - cell1 - mAllPointInfo.size();
                LogUtil.e("ang filter :" + ang1);
                distanceFilter();
                dis2 = srcsize - speedf - dis1 - cell1 - ang1 - mAllPointInfo.size();
                LogUtil.e("second dis filter :" + dis2);
                cellAngleFilter();
                cellang = srcsize - speedf - dis1 - cell1 - ang1 - dis2 - mAllPointInfo.size();
                LogUtil.e("cellang filter :" + cellang);
                distanceFilter();
                dis3 = srcsize - speedf - dis1 - cell1 - ang1 - dis2 - cellang - mAllPointInfo.size();
                LogUtil.e("third dis filter :" + dis3);
                cnFilter = srcsize - mAllPointInfo.size();
            }
            lineAndFadeListSeparateFromAllPoints();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    drawTraceItem();
                    if (mAllPointInfo.size() > 0) {
                        if (refreshBtn.getVisibility() == View.VISIBLE) {
                            //mHandler.sendEmptyMessage(2);
                            refreshBtn.setVisibility(View.GONE);
                        }
                        bottomInvalidate(mAllPointInfo.get(0).mLatlng);
                    } else {
                        bottomOtherStatusInvalidate(BottomStatus.NO_DATA);
                    }
                }
            });
        } else {
            bottomOtherStatusInvalidate(BottomStatus.GETTING_DATA);
            getHistoryTraceDataByDay(getThreeDaysString(date));
        }
    }

    private void handleEfenceFromLocal(SecurityZone scn) {
        LatLng item_pos = new LatLng(scn.sCenter_lat_baidu, scn.sCenter_lng_baidu);
        boolean isfind = false;
        for (int i = 0; i < listItem.size(); i++) {
            LatLng list_pos = new LatLng(listItem.get(i).sCenter_lat_baidu, listItem.get(i).sCenter_lng_baidu);
            if (item_pos.latitude == list_pos.latitude && item_pos.longitude == list_pos.longitude) {
                if (listItem.get(i).sRadius > scn.sRadius) {
                    listItem.get(i).sRadius = scn.sRadius;
                }
                isfind = true;
            }
            break;
        }
        if (!isfind) {
            listItem.add(scn);
        }
    }

    private void mapReset() {
        if (mMap != null)
            mMap.clear();
        curTrackPosition = 0;
        curMarkerPos = 0;
        listItem.clear();
        mAllPointInfo.clear();
        mAllPointSer.clear();
        linePoints.clear();
        dropList.clear();
        fadePoints.clear();
        fadeMarkers.clear();
        combineList.clear();
        allMarkers.clear();
        filterPointList.clear();
        moveList.clear();
        mainTraceLine = null;

    }

    private void setCalendarLocation() {
        calendarView_t.showAtLocation(findViewById(android.R.id.content).getRootView(), Gravity.TOP, 0, 0);
        calendarView_t.showCalendar();
    }

    private void initSeekbar() {
        if (linePoints.size() < 2) {
            seek_bar.setMax(0);
            seek_bar.setProgress(0);
            return;
        }

        moveList.clear();
        curTrackPosition = 0;//linePoints.size() - 1

        seek_bar.setMax(linePoints.size() - 1);
        seek_bar.setProgress(0);//linePoints.size() - 1
    }

    private void initMoveLine() {
        if (linePoints.size() > 1 && moveLine == null) {
            PolylineOptions lop = new PolylineOptions().color(getResources().getColor(R.color.color_14)).width(16);
            moveList.clear();
            moveList.add(linePoints.get(0).mLatlng);
            moveList.add(linePoints.get(1).mLatlng);
            lop.addAll(moveList);
            moveLine = mMap.addPolyline(lop);
            moveLine.setZIndex(2.0f);
        }
    }

    private void move_to_last() {
        if (curTrackPosition > 0 && moveList.size() != 0) {
            curTrackPosition--;
            curMarkerPos = curTrackPosition;
            seek_bar.setProgress(curTrackPosition);
            //animList.remove(animList.size() - 1);
            //traceLine.setPoints(animList);
            moveList.remove(moveList.size() - 1);
            if (moveList.size() > 1) {
                moveLine.setPoints(moveList);
            } else {
                curTrackPosition = 0;
                curMarkerPos = curTrackPosition;
                moveLine.remove();
                moveLine = null;
            }
            curMarker.setPosition(linePoints.get(curTrackPosition).mLatlng);
            bottomInvalidate(linePoints.get(curTrackPosition).mLatlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(linePoints.get(curTrackPosition).mLatlng));
        }
    }

    private void move_to_next() {
        initMoveLine();
        if (curTrackPosition < linePoints.size() - 1) {
            curTrackPosition++;
            curMarkerPos = curTrackPosition;
            seek_bar.setProgress(curTrackPosition);
            if (curTrackPosition > 1) {
                moveList.add(linePoints.get(curTrackPosition).mLatlng);
                moveLine.setPoints(moveList);
            } else {
                if (moveList.size() == 0) {
                    moveList.add(linePoints.get(0).mLatlng);
                    moveList.add(linePoints.get(1).mLatlng);
                    moveLine.setPoints(moveList);
                }
            }
            curMarker.setPosition(linePoints.get(curTrackPosition).mLatlng);
            bottomInvalidate(linePoints.get(curTrackPosition).mLatlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(linePoints.get(curTrackPosition).mLatlng));
        }
    }

    private void updateLineAndmMarker(GooglePointInfo info) {
        initMoveLine();
        int pos = linePoints.indexOf(info);
        ArrayList<LatLng> list = new ArrayList<>();
        if (pos >= curTrackPosition) {
            for (int i = curTrackPosition + 1; i <= pos && i < linePoints.size(); i++) {
                list.add(linePoints.get(i).mLatlng);
            }
            //animList.addAll(list);
            moveList.addAll(list);
        } else {
            for (int i = pos + 1; i <= curTrackPosition && i < linePoints.size(); i++) {
                list.add(linePoints.get(i).mLatlng);
            }
            //animList.removeAll(list);
            moveList.removeAll(list);
        }
        //traceLine.setPoints(animList);
        if (moveList.size() > 1) {
            moveLine.setPoints(moveList);
        } else {
            moveLine.remove();
        }
        curMarker.setPosition(linePoints.get(pos).mLatlng);

        curTrackPosition = pos;
        seek_bar.setProgress(pos);
    }

    public WatchData getCurWatch() {
        return curWatch;
    }
}
