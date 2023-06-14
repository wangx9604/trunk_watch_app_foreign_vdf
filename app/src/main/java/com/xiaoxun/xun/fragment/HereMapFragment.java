//package com.xiaoxun.xun.fragment;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.PointF;
//import android.graphics.drawable.BitmapDrawable;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import androidx.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.model.LatLng;
//import com.here.android.mpa.common.CopyrightLogoPosition;
//import com.here.android.mpa.common.GeoBoundingBox;
//import com.here.android.mpa.common.GeoCoordinate;
//import com.here.android.mpa.common.Image;
//import com.here.android.mpa.common.OnEngineInitListener;
//import com.here.android.mpa.common.PositioningManager;
//import com.here.android.mpa.common.ViewObject;
//import com.here.android.mpa.mapping.Map;
//import com.here.android.mpa.mapping.MapGesture;
//import com.here.android.mpa.mapping.MapLocalModel;
//import com.here.android.mpa.mapping.MapMarker;
//import com.here.android.mpa.mapping.MapOverlay;
//import com.here.android.mpa.mapping.MapState;
//import com.here.android.mpa.mapping.SupportMapFragment;
//import com.xiaoxun.xun.Const;
//import com.xiaoxun.xun.ImibabyApp;
//import com.xiaoxun.xun.R;
//import com.xiaoxun.xun.activitys.DevOptActivity;
//import com.xiaoxun.xun.activitys.ErrorPromptActivity;
//import com.xiaoxun.xun.activitys.GoogleMapHistoryTraceActivity;
//import com.xiaoxun.xun.activitys.HereMapHistoryActivity;
//import com.xiaoxun.xun.activitys.NewMainActivity;
//import com.xiaoxun.xun.activitys.SelecterCallBackNumber;
//import com.xiaoxun.xun.activitys.SetDeviceNumberActivity;
//import com.xiaoxun.xun.activitys.VideoCallActivity;
//import com.xiaoxun.xun.beans.LocationData;
//import com.xiaoxun.xun.beans.MyMsgData;
//import com.xiaoxun.xun.beans.PhoneNumber;
//import com.xiaoxun.xun.beans.WatchData;
//import com.xiaoxun.xun.db.LocationDAO;
//import com.xiaoxun.xun.interfaces.MsgCallback;
//import com.xiaoxun.xun.listener.SensorMarkerHelper;
//import com.xiaoxun.xun.utils.CloudBridgeUtil;
//import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
//import com.xiaoxun.xun.utils.DialogUtil;
//import com.xiaoxun.xun.utils.HereUtils;
//import com.xiaoxun.xun.utils.ImageUtil;
//import com.xiaoxun.xun.utils.LocationUtils;
//import com.xiaoxun.xun.utils.LogUtil;
//import com.xiaoxun.xun.utils.MapScaleView;
//import com.xiaoxun.xun.utils.MioAsyncTask;
//import com.xiaoxun.xun.utils.SmsUtil;
//import com.xiaoxun.xun.utils.TimeUtil;
//import com.xiaoxun.xun.utils.Timer;
//import com.xiaoxun.xun.utils.ToastUtil;
//import com.xiaoxun.xun.utils.ToolUtils;
//import com.xiaoxun.xun.utils.WatchStateUtils;
//import com.xiaoxun.xun.views.CustomerPickerView;
//import com.xiaoxun.xun.views.ItemListPopUpWindow;
//
//import net.minidev.json.JSONObject;
//import net.minidev.json.JSONValue;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//import static android.content.pm.PackageManager.PERMISSION_GRANTED;
//
///**
// * Created by huangqilin on 2016/11/24.
// */
//
//public class HereMapFragment extends Fragment implements View.OnClickListener, MsgCallback {
//
//    private static final String TAG = "HereMapFragment";
//
//    private ImibabyApp mApp;
//    private WatchData focusWatch;
//    private NewMainActivity mHostActivity;
//    private MainMapReceiver mMainMapReceiver;
//
//    private int mMyLocationIsVisibale = -1;
//    private int mMyLocationChangeTips = 0;   //0 提示， 1 不提示.0.1版本
//
////    private MarkerOptions mMarkerOptions;
////    private Marker mPhoneMarker = null;
////    private MarkerOptions mMarkerPhone;
////    private ArrayList<MarkerOptions> mMarkerList;
//
//    private View viewRoot;
//    private ImageButton mChangeMyLoc;
//    private LinearLayout mHistoryLocs;
//    private TextView mLocText;
//    private TextView mLocOtherText;
//    private ImageButton mMoreFunc;
//    private View layoutWatchState;
//    private TextView mWatchStateText;
//    private MapScaleView mScaleLine;
//    private TextView mNetworkText;
//    private View mNetworkLayout;
//    private ImageView mHead;
//    private ImageView mSignal;
//    private ImageView mBattery;
//    private TextView mBatteryText;
//    private RelativeLayout mTitle;
//    private ImageButton mZoomOut;
//    private ImageButton mZoomIn;
//    private ImageButton mLocation;
//    private ImageButton mTraceLocation;
//    private ImageView guide1;
//    private RelativeLayout guidely;
//    private Button mBtnWakeUp;
//
//    //    private ArrayList<LatLng> mWatchRouteShapePoints = new ArrayList<>();
////    private Polyline mPlanRouteLine;
//    private boolean isNavigating = false;
//    private String mNavigationKey;
//    private String mNavigationDescription = "";
//
//    private Boolean isSatellite = false;
//    private boolean isFirstIn = true;
//    private HashMap<String, Long> mWatchBatteryTime = new HashMap<>();
//    private HashMap<String, Boolean> mWatchClickFlag = new HashMap<>();
//    private final HashMap<String, MioAsyncTask<String, String, String>> mWatchClickTask = new
//            HashMap<>();
//    private HashMap<String, Long> mLocationUseTime = new HashMap<>();
//    private HashMap<String, MioAsyncTask<String, Long, String>> mWatchTask = new HashMap<>();
//    private MioAsyncTask<String, String, String> mLocationAnimTask = null; //地图动画
//    private boolean isOnTraceToIsActive;  //标志位，开启追踪命令后，收到回执前
//    private boolean isOnTraceTo;  //标志位，正在追踪
//    private int mTrackToTime;  //追踪时长
//    private long timeCount;
//    private boolean isResume;
//    private Timer traceTimer;  //追踪模式定时器
//    private SensorMarkerHelper mSensorHelper = null;
//    MapMarker marker_phone;
//    ArrayList<GeoCoordinate> list = new ArrayList<>();
//    private LocationData testLocationData = new LocationData();
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case ItemListPopUpWindow.MESSAGE_MORE_FUCTION_CHANGE_MAP:
//                    if (!isSatellite) {
//                        isSatellite = true;
//                        mApp.setValue(Const.SHARE_PREF_FIELD_IS_SHOW_SATELLITE, 1);
//
//                        if (mAMap != null) {
//                            mAMap.setMapScheme(Map.Scheme.HYBRID_DAY);
//                        }
//                    } else {
//                        isSatellite = false;
//                        mApp.setValue(Const.SHARE_PREF_FIELD_IS_SHOW_SATELLITE, 0);
//                        if (mAMap != null) {
//                            mAMap.setMapScheme(Map.Scheme.NORMAL_DAY);
//                        }
//                    }
//                    break;
//                case 1:
//                    // 定位纠偏
//                    break;
//                case ItemListPopUpWindow.MESSAGE_MORE_FUCTION_CALL:
//                    checkPhoneNum();
//                    break;
//                case 5:
//                    startMonitorDialog(NewMainActivity.GET_MONITOR_NUMBER_REQUEST);
//                    break;
//                case 6:
//                    openTakePhotoDialog();
//                    break;
//                case 7: {
//                    Intent intent = new Intent(mHostActivity, VideoCallActivity.class);
//                    intent.putExtra(Const.VIDEOCALL_TYPE, 0);
//                    intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, focusWatch.getEid());
//                    mHostActivity.startActivity(intent);
//                }
//                break;
//            }
//        }
//    };
//    private SupportMapFragment mMapView;
//    private Map mAMap;
//    private MapOverlay overlay;
//    private boolean mShowAllMarkerFlag = true;
//
//    private int curGolbleCount = 0;  //记录一次取消主动定位控制
//    private boolean testLocationFlag = false;
//    private ImageView imageView;
////    private boolean mLocationAnimIsGoing = false;
//    private PositioningManager mPositioningManager;
//    private MapLocalModel currentPositionMapLocalModel;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        isResume = true;
//        Log.e("xxxx", "MapFragment mMapView.onResume():" + System.currentTimeMillis());
//        curGolbleCount = 0;
//
//        mMyLocationIsVisibale = mApp.getIntValue(Const
//                .SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 0);
//        if (mChangeMyLoc != null && 0 == mMyLocationIsVisibale) {
//            mChangeMyLoc.setBackgroundResource(R.drawable.btn_add_my_location_selector);
//        } else if (mChangeMyLoc != null && 1 == mMyLocationIsVisibale) {
//            mChangeMyLoc.setBackgroundResource(R.drawable.phone_position_1);
//            startPhoneLocation(false);
//        }
//        updateTitleNetworkState();
//        changeWatchRefresh();
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && mApp != null) {
//            Log.e("xxxx", "MapFragment mMapView.setUserVisibleHint:" + System.currentTimeMillis());
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        isResume = false;
////        mLocationAnimIsGoing = false;
//        if (mLocationAnimTask != null) {
//            mLocationAnimTask.cancel(true);
//            mLocationAnimTask = null;
//        }
//
//        stopPhoneLocation();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//            savedInstanceState) {
//
//        Log.i("cui", "MapFragment==================================onCreateView");
//        mApp = (ImibabyApp) getActivity().getApplication();
//        focusWatch = mApp.getCurUser().getFocusWatch();
//        mHostActivity = (NewMainActivity) getActivity();
//        viewRoot = inflater.inflate(R.layout.fragment_here_map, container, false);
//        mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
//
//        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.heremap);
//
//        getloactionManager();
//
//
//        initMaps();
//
//        initView(viewRoot);
//
//        Log.e("xxxx", "MapFragment mMapView.onCreateView:" + System.currentTimeMillis());
//        return viewRoot;
//    }
//
//    //获取地理位置
//    LocationManager locationManager;
//    String provider = "";
//
//    private void getloactionManager() {
//        if (locationManager == null) {
//            locationManager = (LocationManager) mHostActivity.getSystemService(Context
//                    .LOCATION_SERVICE);
//        }
//        List<String> providerList = locationManager.getProviders(true);
//        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//            provider = LocationManager.GPS_PROVIDER;
//        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
//            provider = LocationManager.NETWORK_PROVIDER;
//        } else {
//            Toast.makeText(mHostActivity, "No location provider to use", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void initMaps() {
//        boolean success = HereUtils.setIsolatedDiskCacheRootPath();
//        if (!success) {
//            Toast.makeText(mHostActivity, "Unable to set isolated disk cache path.", Toast
//                    .LENGTH_LONG);
//        } else {
//            mMapView.init(new OnEngineInitListener() {
//                @Override
//                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
//                    if (error == Error.NONE) {
//                        mAMap = mMapView.getMap();
//                        mAMap.setZoomLevel(16);
//                        //定位
//                        mPositioningManager = PositioningManager.getInstance();
//                        //手势
//                        mMapView.getMapGesture().addOnGestureListener(new MyOnGestureListener(),
//                                0, true);
//
//                        MapSetting();
//                    } else {
//                        Log.e("cui", error.name());
//                        Log.e("cui", error.getDetails());
//                        Log.e("cui", error.getStackTrace());
//                    }
//                }
//            });
//        }
//    }
//
//    private void MapSetting() {
//        mMapView.getMapGesture().setRotateEnabled(false);       //禁止旋转
//        mMapView.setCopyrightLogoPosition(CopyrightLogoPosition.BOTTOM_LEFT);//设置图标位置
//
//        //是否开启卫星地图
//        int mapSatellite = mApp.getIntValue(Const.SHARE_PREF_FIELD_IS_SHOW_SATELLITE, 0);
//        if (mapSatellite == 0) {
//            isSatellite = false;
//            mAMap.setMapScheme(Map.Scheme.NORMAL_DAY);
//        } else {
//            isSatellite = true;
//            mAMap.setMapScheme(Map.Scheme.HYBRID_DAY);
//
//        }
//
//
//        //地图变化监听，比如 手势，
//        mAMap.addTransformListener(new com.here.android.mpa.mapping.Map.OnTransformListener() {
//            @Override
//            public void onMapTransformStart() {
//                //Log.i("cui","手势开始");
//            }
//
//            @Override
//            public void onMapTransformEnd(MapState mapState) {
//                double zoomlvl = mapState.getZoomLevel();
//                double latitude = mapState.getCenter().getLatitude();
//                mScaleLine.update((float) zoomlvl, latitude);
//            }
//        });
//
//        //查找手表位置
//        if (mApp.getCurUser().getFocusWatch() != null && mApp.getCurUser().getFocusWatch()
//                .getCurLocation() != null) {
//            double latude = mApp.getCurUser().getFocusWatch().getCurLocation().getLatitude();
//            double longitude = mApp.getCurUser().getFocusWatch().getCurLocation().getLongitude();
//            mAMap.setCenter(new GeoCoordinate(latude, longitude), com.here.android.mpa.mapping
//                    .Map.Animation.LINEAR);
//            setMarkerIcon(mApp.getCurUser().getFocusWatch().getEid());
//            Log.i("cui", "setMarkerIcon--------MapSetting");
//        } else {
//            GeoCoordinate pos = new GeoCoordinate(28.6329539686, 77.2194457054);
//            mAMap.setCenter(pos, com.here.android.mpa.mapping.Map.Animation.LINEAR);
//        }
//
//        mMyLocationIsVisibale = mApp.getIntValue(Const
//                .SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 0);
//        if (mMyLocationIsVisibale == 1) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startPhoneLocation(false);
//                }
//            }, 2000);
//        }
//    }
//
//    private void setMarkerIcon(String eid) {
//        ImageView imageView = null;
//        GeoCoordinate geoCoordinate = null;
//        //Log.i("cui","setMarkerIcon");
//
//        if (mAMap != null && overlay != null) {
//            mAMap.removeMapOverlay(overlay);
//        }
//        if (mApp != null && mApp.getCurUser().getFocusWatch().getCurLocation() != null) {
//            double latude = mApp.getCurUser().getFocusWatch().getCurLocation().getLatitude();
//            double longitude = mApp.getCurUser().getFocusWatch().getCurLocation().getLongitude();
//            geoCoordinate = new GeoCoordinate(latude, longitude);
//        } else {
//            Log.e("cui", "mApp.getCurUser().getFocusWatch().getCurLocation()   is   null");
//        }
//
//        if (overlay == null) {
//            imageView = new ImageView(mHostActivity);
//            if (geoCoordinate != null) {
//                overlay = new MapOverlay(imageView, geoCoordinate);
//            }
//        } else {
//            imageView = (ImageView) overlay.getView();
//        }
//        if (mAMap != null && overlay != null) {
//            mAMap.addMapOverlay(overlay);
//        }
//
//        BitmapDrawable icon = null;
//        for (WatchData watch : mApp.getCurUser().getWatchList()) {
//            if (watch.getEid().equals(eid)) {
//                icon = (BitmapDrawable) (mApp.getHeadDrawableByFile(getResources(), watch
//                        .getHeadPath(), watch.getEid(), R.drawable.coordinate_0));
//                break;
//            }
//        }
//        if (icon == null) {
//            icon = (BitmapDrawable) getResources().getDrawable(R.drawable.coordinate_0);
//        }
//
//        imageView.setImageBitmap(icon.getBitmap());
//
//        //设置偏移量 效果不是很好 先注释
//        /*PointF pointF =   marker_sw.getAnchorPoint();
//        pointF.offset(0,25);
//        marker_sw.setAnchorPoint(pointF);*/
//    }
//
//
//    private Bitmap toConformBitmap(Bitmap background, Bitmap foreground, int widthPercent, int
//            heightPercent) {
//        if (background == null) {
//            return null;
//        }
//
//        int bgWidth = background.getWidth();
//        int bgHeight = background.getHeight();
//        int fgWidth = foreground.getWidth();
//        int fgHeight = foreground.getHeight();
//        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
//        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
//        Canvas cv = new Canvas(newbmp);
//        //draw bg into
//        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
//        //draw fg into
//        cv.drawBitmap(foreground, (bgWidth - fgWidth) / widthPercent, (bgHeight - fgHeight) /
//                heightPercent, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
//        //save all clip
//        cv.save();//保存cv.save(Canvas.ALL_SAVE_FLAG);
//        //store
//        cv.restore();//存储
//        return newbmp;
//    }
//
//    // Map gesture listener
//    private class MyOnGestureListener implements MapGesture.OnGestureListener {
//
//        @Override
//        public void onPanStart() {
//        }
//
//        @Override
//        public void onPanEnd() {
//            //Log.i("cui","onPanEnd");
//            // TODO 清除覆盖物
//            if (mAMap != null && overlay != null) {
//
//                mAMap.removeMapOverlay(overlay);
//            }
//            setMarkerIcon(mApp.getCurUser().getFocusWatch().getEid());
//            Log.i("cui", "setMarkerIcon-----onPanEnd");
//            //setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(), mApp.getCurUser
//            // ().getFocusWatch().getEid(), 2);
//        }
//
//        @Override
//        public void onMultiFingerManipulationStart() {
//        }
//
//        @Override
//        public void onMultiFingerManipulationEnd() {
//        }
//
//        @Override
//        public boolean onMapObjectsSelected(List<ViewObject> objects) {
//            return false;
//        }
//
//        @Override
//        public boolean onTapEvent(PointF p) {
//            return false;
//        }
//
//        @Override
//        public boolean onDoubleTapEvent(PointF p) {
//            return false;
//        }
//
//        @Override
//        public void onPinchLocked() {
//        }
//
//        @Override
//        public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
//            //double zoom=mAMap.getZoomLevel();
//            //Log.i("cui"," zoom = "+zoom);
//            return false;
//        }
//
//        @Override
//        public void onRotateLocked() {
//        }
//
//        @Override
//        public boolean onRotateEvent(float rotateAngle) {
//            return false;
//        }
//
//        @Override
//        public boolean onTiltEvent(float angle) {
//            return false;
//        }
//
//        @Override
//        public boolean onLongPressEvent(PointF p) {
//            return false;
//        }
//
//        @Override
//        public void onLongPressRelease() {
//        }
//
//        @Override
//        public boolean onTwoFingerTapEvent(PointF p) {
//            return false;
//        }
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.i("cui", "MapFragment==================================onViewCreated");
//        initparameter();
//        mMainMapReceiver = new MainMapReceiver();
//        mMainMapReceiver.registerReceiver(getActivity());
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        stopPhoneLocation();
//        if (mLocationAnimTask != null) {
//            mLocationAnimTask.cancel(true);
//            mLocationAnimTask = null;
//        }
//        handler.removeCallbacksAndMessages(null);
//        mMainMapReceiver.unregisterReceiver(this.getActivity());
//
//    }
//
//
//    @Override
//    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
//
//        int rc;
//        JSONObject pl, plReq;
//        String eid;
//        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
//
//        switch (cid) {
//            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
//                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
//                plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                if (CloudBridgeUtil.RC_SUCCESS == rc && mHostActivity.getNetservice() != null) {
//                    if (plReq != null && pl != null) {
//                        eid = (String) plReq.get(CloudBridgeUtil.KEY_NAME_EID);
//                        String value = (String) pl.get(CloudBridgeUtil.BATTERY_LEVEL);
//                        mHostActivity.getNetservice().handleMapMGetBattery(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.DEVICE_POWER_ON_TIME);
//                        mHostActivity.getNetservice().handleMapMGetPowerOnTime(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.WATCH_ONOFF_FLAG);
//                        mHostActivity.getNetservice().handleMapMGetWatchOnOff(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.OPERATION_MODE_VALUE);
//                        mHostActivity.getNetservice().handleMapMGetWatchMode(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE);
//                        mHostActivity.getNetservice().handleMapMGETWatchNetState(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.SIGNAL_LEVEL);
//                        mHostActivity.getNetservice().handleMapMGETWatchSignal(eid, value);
//
//                        value = (String) pl.get(CloudBridgeUtil.KEY_NAME_CHARGE_STATUS);
//                        if (value != null && value.length() == 1)
//                            mApp.setmChargeState(eid, Integer.valueOf(value));
//
//                        if (pl.containsKey(CloudBridgeUtil.SILENCE_LIST)) {
//                            String jstr = (String) pl.get(CloudBridgeUtil.SILENCE_LIST);
//                            mHostActivity.getNetservice().updateSilenceTimeData(eid, jstr);
//                        }
//
//                        if (pl.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
//                            String jstr = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
//                            mHostActivity.getNetservice().updateSleepListData(eid, jstr);
//                        }
//
//                        if (pl.containsKey(CloudBridgeUtil.OFFLINE_MODE_VALUE)) {
//                            String jstr = (String) pl.get(CloudBridgeUtil.OFFLINE_MODE_VALUE);
//                            mHostActivity.getNetservice().updateOfflineMode(eid, jstr);
//                        }
//
//                        updateTitle(eid);
//                        updateWatchCurrentState(eid);
//
//                        value = (String) pl.get(CloudBridgeUtil.STEPS_NOTIFICATION_SETTING);
//                        if (value != null) {
//                            if (value.equals("0")) {
//                                mApp.setValue(eid + CloudBridgeUtil.STEPS_NOTIFICATION_SETTING,
//                                        "0");
//                            } else {
//                                mApp.setValue(eid + CloudBridgeUtil.STEPS_NOTIFICATION_SETTING,
//                                        "1");
//                            }
//                        }
//                    }
//                } else {
//                    LogUtil.e("mapmget error rc = " + rc);
//                }
//                break;
//
//            case CloudBridgeUtil.CID_RETRIEVE_TRACE_DATA_RESP:
//                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
//                if (1 == rc && getHost() != null) {
//                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    if (pl != null) {
//                        JSONObject list = (JSONObject) pl.get(CloudBridgeUtil.KEY_NAME_LIST);
//                        if (list != null) {
//                            for (java.util.Map.Entry<String, Object> entry : list.entrySet()) {
//                                JSONObject value = (JSONObject) entry.getValue();
//                                value.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, entry.getKey());
//                                eid = (String) value.get(CloudBridgeUtil.KEY_NAME_EID);
//                                if (mApp.getCurUser().queryWatchDataByEid(eid) != null) {
//                                    mApp.sdcardLog("huangqilin retrieve response:" + TimeUtil
//                                            .getTimeStampLocal());
//                                    setWatchLocation(value, 1);
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (focusWatch != null && focusWatch.getCurLocation() != null) {
//                        setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                    }
//                }
//                break;
//
//            case CloudBridgeUtil.CID_E2E_DOWN:
//                pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                int value = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
//                String[] watchid = (String[]) reqMsg.get(CloudBridgeUtil.KEY_NAME_TEID);
//                eid = watchid[0];
//                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
//                if (value == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_LOCATION) {
//                    if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil
//                            .RC_SOCKET_NOTREADY
//                            || rc == CloudBridgeUtil.ERROR_CODE_E2G_OFFLINE || rc ==
//                            CloudBridgeUtil.RC_HALF_SUCCESS) {
//                        mApp.getmWatchBackhomeLocationFlag().put(eid, false);
//                        mApp.sdcardLog("huangqilin 4 Backhome eid:" + eid + " flag:" + mApp
//                                .getmWatchBackhomeLocationFlag().get(eid));
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                String[] watcheid = new String[1];
//                                if (focusWatch != null) {
//                                    watcheid[0] = focusWatch.getEid();
//                                    if (mWatchClickTask.get(watcheid[0]) != null) {
//                                        mWatchClickFlag.put(watcheid[0], false);
//                                        mWatchClickTask.get(watcheid[0]).cancel(true);
//                                        Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                                        it.putExtra("fouce_eid", watcheid[0]);
//                                        mHostActivity.sendBroadcast(it);
//                                    }
//                                }
//                            }
//                        }, 500);
//                        if (isAdded() && HereMapFragment.this.getUserVisibleHint()) {
//                            if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil
//                                    .RC_SOCKET_NOTREADY)
//                                ToastUtil.show(mApp, getString(R.string.network_error_prompt));
//                            else
//                                ToastUtil.show(mApp, getString(R.string.watch_offline));
//                        }
//                    } else if (rc == CloudBridgeUtil.RC_NOT_LOGIN || rc == CloudBridgeUtil
//                            .RC_TIMEOUT) {
//                        mApp.getmWatchBackhomeLocationFlag().put(eid, false);
//                        mApp.sdcardLog("huangqilin 7 Backhome eid:" + eid + " flag:" + mApp
//                                .getmWatchBackhomeLocationFlag().get(eid));
//                    } else {
//                        pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                        if (pl != null) {
//                            pl.remove(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
//                            Integer recvSN = null;
//                            try {
//                                recvSN = (Integer) reqMsg.get("SN");
//                            } catch (Exception e) {
//                            }
//                            sendLocationToService(pl, String.valueOf(recvSN));
//                            Integer state = mApp.getmWatchIsOn().get(focusWatch.getEid());
//                            if (state != null && (state == Const.WATCH_STATE_POWER_OFF || state
//                                    == Const.WATCH_STATE_POWER_OFF_LOW_POWER)) {
//                                mWatchBatteryTime.put(focusWatch.getEid(), System
//                                        .currentTimeMillis());
//                                String[] keys = new String[1];
//                                keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
//                                mHostActivity.mapMGet(focusWatch.getEid(), keys,
//                                        HereMapFragment.this);
//                            }
//                        } else {
//                            mApp.getmWatchBackhomeLocationFlag().put(eid, false);
//                            mApp.sdcardLog("huangqilin 5 Backhome eid:" + eid + " flag:" + mApp
//                                    .getmWatchBackhomeLocationFlag().get(eid));
//                        }
//                    }
//                } else if (value == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_DEVICE_LISTEN) {
//                    JSONObject reqPL = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    JSONObject respPL = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    int type = (Integer) reqPL.get(CloudBridgeUtil.KEY_DEVICE_LISTEN_TYPE);
//                    if (type == 2) {
//                        if (respPL != null) {
//                            int plRC = (Integer) respPL.get(CloudBridgeUtil.KEY_NAME_RC);
//                            if (plRC == 1) {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .listen_success));
//                            } else if (plRC == -1) {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .listen_reject));
//                            } else {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .listen_timeout));
//                            }
//                        } else {
//                            if (rc == -160) {
//                                if (isAdded()) {
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .watch_offline));
//                                }
//                            } else if (CloudBridgeUtil.RC_TIMEOUT == rc) {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .listen_timeout));
//                            } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil
//                                    .RC_SOCKET_NOTREADY) {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .network_error_prompt));
//                            } else {
//                                if (isAdded())
//                                    ToastUtil.show(mHostActivity, getString(R.string.listen_fail));
//                            }
//                        }
//                        mApp.setValue(eid + Const.SHARE_PREF_LISTEN_STATE, 0);
//                    }
//                } else if (value == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FORCE_TAKE_PHOTO) {
//                    if (!isAdded()) {
//                        return;
//                    }
//                    JSONObject reqPL = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    String photoEid = (String) reqPL.get(CloudBridgeUtil.KEY_NAME_EID);
//                    JSONObject respPL = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    if (respPL != null) {
//                        int plRC = (Integer) respPL.get(CloudBridgeUtil.KEY_NAME_RC);
//                        if (plRC == 1) {
//                            ToastUtil.show(mHostActivity, getString(R.string
//                                    .send_takephoto_success));
//                        } else {
//                            ToastUtil.show(mHostActivity, getString(R.string.takephoto_timeout));
//                            mApp.setForceTakePhotoState(photoEid, false);
//                            handler.removeMessages(100);
//                        }
//                    } else {
//                        if (rc == -160) {
//                            ToastUtil.show(mHostActivity, getString(R.string.watch_offline));
//                        } else if (CloudBridgeUtil.RC_TIMEOUT == rc) {
//                            ToastUtil.show(mHostActivity, getString(R.string.send_takephoto_error));
//                        } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil
//                                .RC_SOCKET_NOTREADY) {
//                            ToastUtil.show(mHostActivity, getString(R.string.network_error_prompt));
//                        } else {
//                            ToastUtil.show(mHostActivity, getString(R.string.send_takephoto_error));
//                        }
//                        mApp.setForceTakePhotoState(photoEid, false);
//                        handler.removeMessages(100);
//                    }
//                } else if (value == CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_NAVI_STATE) {
//                    if (rc == 1) {
//                        isNavigating = true;
//                        showNaviMap();
//                    } else {
//                        isNavigating = false;
//                    }
//                }
//                break;
//            case CloudBridgeUtil.CID_LOCATION_RESP:
//                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
//                pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                if (testLocationFlag) {
//                    if (mApp.getCurUser().getEid().equals(pl.get("EID")) && respMsg.get
//                            (CloudBridgeUtil.KEY_NAME_PL) != null) {
//                        setTestLocationMarker((JSONObject) respMsg.get(CloudBridgeUtil
//                                .KEY_NAME_PL));
//                        break;
//                    }
//                }
//                mApp.getmWatchBackhomeLocationFlag().put((String) pl.get(CloudBridgeUtil
//                        .KEY_NAME_EID), false);
//                mApp.sdcardLog("huangqilin 6 Backhome eid:" + pl.get(CloudBridgeUtil
//                        .KEY_NAME_EID) + " flag:" + mApp.getmWatchBackhomeLocationFlag().get(pl.get
//                        (CloudBridgeUtil.KEY_NAME_EID)));
//                if (mWatchClickTask.get(pl.get(CloudBridgeUtil.KEY_NAME_EID)) != null) {
//                    mWatchClickTask.get(pl.get(CloudBridgeUtil.KEY_NAME_EID)).cancel(true);
//                }
//                if (1 == rc) {
//                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                    if (pl != null) {
//                        pl.put("sn", respMsg.get("SN"));
//                        setWatchLocation(pl, 0);
//                        mApp.sdcardLog("get location=" + pl.toString());
//                        mApp.sdcardLog("huangqilin watch location response:" + TimeUtil
//                                .getTimeStampLocal());
//                    }
//                } else {
//                    if (mApp.getCurUser().getFocusWatch().getCurLocation() != null) {
//                        setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(), mApp
//                                .getCurUser().getFocusWatch().getEid(), 2);
//                    }
//
//                    mWatchClickFlag.put((String) pl.get(CloudBridgeUtil.KEY_NAME_EID), false);
//                    Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                    it.putExtra("fouce_eid", (String) pl.get(CloudBridgeUtil.KEY_NAME_EID));
//                    mHostActivity.sendBroadcast(it);
//                    LogUtil.e("error rc = " + rc);
//                }
//                break;
//
//            case CloudBridgeUtil.CID_TRACE_TO_SET_RESP:
//                plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
//                eid = (String) plReq.get(CloudBridgeUtil.KEY_NAME_EID);
//                rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
//                isOnTraceToIsActive = false;
//                if (1 == rc) {
//                    pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
//                    int valueTrace = (Integer) pl.get(CloudBridgeUtil.KEY_TRACE_TO_VALUE);
//                    if (valueTrace <= 0) {
//                        closeTraceMode(true);
//                    } else {
//                        LogUtil.i("53022返回后开启定时器");
//                        String endtime = (String) pl.get(CloudBridgeUtil.KEY_TRACE_TO_END_TIME);
//                        String timestamp = (String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
//                        long diffSec = TimeUtil.compareToDiffForTwoTime(timestamp, endtime);
//                        String localTime = TimeUtil.convertToLocalTime(diffSec);
//                        mApp.setValue(focusWatch.getEid() + Const
//                                .SHARE_PREF_TRACE_TO_LOCAL_ENDTIME, localTime);
//                        updateWatchCurrentState(eid);
//                    }
//                } else {
//                    if (isAdded())
//                        ToastUtil.show(mHostActivity, getResources().getString(R.string
//                                .map_fragment_net_error));
//                    if (isOnTraceTo) {
//                        mTraceLocation.setBackgroundResource(R.drawable.btn_track_close_selector);
//                    } else {
//                        mTraceLocation.setBackgroundResource(R.drawable.btn_track_open_selector);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void setTestLocationMarker(JSONObject pl) {
//        JSONObject result = (JSONObject) pl.get("result");
//        /*0：没有得到定位结果；1： gps 定位结果；2：wifi 定位结果；3：混合定位结果；
//        4：基站定位结果；5：其他-->*/
//        if (result != null) {
//            testLocationData.setTimestamp((String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP));
//            if (pl.get("status").equals("1") && !result.get("type").equals("0")) {
//                testLocationData.setType(Integer.valueOf((String) result.get("type")));
//                String city = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_CITY);
//                if (city.equals("")) {
//                    city = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_PROVINCE);
//                }
//                testLocationData.setCity(city);
//                testLocationData.setDescription((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_DESC));
//                testLocationData.setPoi((String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_POI));
//                testLocationData.setRadius(Integer.valueOf((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_RADIUS)));
//                testLocationData.setAccuracy(Integer.valueOf((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_RADIUS)));
//                testLocationData.setRoad((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_ROAD));
//                StringBuilder latlng = new StringBuilder((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_LOCATION));
//                int i = latlng.indexOf(",");
//                double longitude = Double.valueOf(latlng.substring(0, i));
//                double latitude = Double.valueOf(latlng.substring(i + 1));
//                LatLng temp = new LatLng(latitude, longitude);
//                testLocationData.setLatitude(latitude);
//                testLocationData.setLongitude(longitude);
//                setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(), mApp
//                        .getCurUser().getFocusWatch().getEid(), 2);
//            } else {
//                ToastUtil.show(mHostActivity, getString(R.string.error_addr));
//            }
//        } else {
//            ToastUtil.show(mHostActivity, getString(R.string.error_addr));
//        }
//    }
//
//    private void handleRoutePlan(JSONObject routePlanPL) {
////        mNavigationKey = (String) routePlanPL.get(CloudBridgeUtil.KEY_NAME_KEY);
////        JSONObject routePlan = (JSONObject) routePlanPL.get("route_plan");
////        JSONArray routePoints = (JSONArray) routePlan.get("points");
////        mWatchRouteShapePoints.clear();
////        for (int i = 0; i < routePoints.size(); i++) {
////            JSONObject pointJson = (JSONObject) routePoints.get(i);
////            double latitude = (Double) pointJson.get("lat");
////            double longitude = (Double) pointJson.get("lng");
////            LatLng latLng=new LatLng(latitude,longitude);
////            mWatchRouteShapePoints.add(latLng);
////        }
////        JSONObject efence = (JSONObject) routePlan.get(CloudBridgeUtil.KEY_NAME_NAVI_EFENCE);
////        String efenceName = (String)efence.get(CloudBridgeUtil.KEY_NAME_NAME);
////        mNavigationDescription = getString(R.string.navtigation_description, mApp.getCurUser()
//// .getFocusWatch().getNickname(), efenceName);
//    }
//
//    private void showPlanRouteAmapLine() {
////        if (mPlanRouteLine != null) {
////            mPlanRouteLine.remove();
////        }
////
////        PolylineOptions polygonOptions=new PolylineOptions()
////                .width(24f)
////                .addAll(mWatchRouteShapePoints)
////                .color(getResources().getColor(R.color.navi_road));
////        mPlanRouteLine=mAMap.addPolyline(polygonOptions);
//    }
//
//    //    private Marker mStartMarkerAmap;
////    private Marker mEndMarkerAmap;
//    private void showStartEndAmapMarker() {
////        int length = mWatchRouteShapePoints.size();
////        LatLng startPoint = mWatchRouteShapePoints.get(0);
////        LatLng endPoint =  mWatchRouteShapePoints.get(length - 1);
////
////        MarkerOptions startMarkerOptions = new MarkerOptions().position(startPoint).
////                icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)).anchor(
//// (float) 0.5, (float) 0.89);
////        if (mStartMarkerAmap != null) {
////            mStartMarkerAmap.remove();
////        }
////        mStartMarkerAmap = mGoogleMap.addMarker(startMarkerOptions);
////
////        MarkerOptions endMarkerOptions = new MarkerOptions().position(endPoint).
////                icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)).anchor((float)
//// 0.5, (float) 0.89);
////        if (mEndMarkerAmap != null) {
////            mEndMarkerAmap.remove();
////        }
////        mEndMarkerAmap = mGoogleMap.addMarker(endMarkerOptions);
//    }
//
//    private void updateMapStatus() {
////        LatLngBounds.Builder builder = LatLngBounds.builder();
////        for (LatLng point : mWatchRouteShapePoints) {
////            builder.include(point);
////        }
////        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
//    }
//
////    private LatLng mWatchNaviLocation;
////    private Marker mWatchNaviMarker;
//
//    private void updateWatchNaviLocation(String locationData) {
//        handleWatchNaviLocation(locationData);
//        showWatchNaviMarker();
//    }
//
//    private void handleWatchNaviLocation(String locationData) {
////        JSONObject pl = (JSONObject) JSONValue.parse(locationData);
////        JSONObject curPoint = (JSONObject) pl.get("cur_point");
////        double latitude = (Double) curPoint.get("lat");
////        double longitude = (Double) curPoint.get("lng");
////        mWatchNaviLocation = new LatLng(latitude, longitude);
//    }
//
//    private void showWatchNaviMarker() {
//        if (!isResume) {
//            return;
//        }
//        showWatchNaviAmapMarker();
//    }
//
//    private void showWatchNaviAmapMarker() {
////        if (mWatchNaviLocation == null) {
////            mWatchNaviLocation = mWatchRouteShapePoints.get(0);
////        }
////        if (mWatchNaviMarker != null) {
////            mWatchNaviMarker.remove();
////        }
////        MarkerOptions markerOptions = new MarkerOptions().position(mWatchNaviLocation).
////                icon(BitmapDescriptorFactory.fromResource(R.drawable.coordinate_0)).anchor(
//// (float) 0.5, (float) 0.89);
////        mWatchNaviMarker = mGoogleMap.addMarker(markerOptions);
//    }
//
//    private void showNaviMap() {
////        if (!isResume) return;
////        if (mLocationAnimTask != null) {
////            mLocationAnimTask.cancel(true);
////            mLocationAnimTask = null;
////        }
////
////        mGoogleMap.clear();
////        showPlanRouteAmapLine();
////        showStartEndAmapMarker();
////        showWatchNaviAmapMarker();
////        updateMapStatus();
////
////        mLocText.setText(mNavigationDescription);
////        mLocOtherText.setText("");
////        if (mWatchTask.get(focusWatch.getEid()) != null) {
////            mWatchTask.get(focusWatch.getEid()).cancel(true);
////        }
//    }
//
//    private void clearNaviMap() {
////        isNavigating = false;
////        if (mGoogleMap != null)
////            mGoogleMap.clear();
//    }
//
//    private void endNavigation(String eid, String key) {
//
//        if (isNavigating && focusWatch.getEid().equals(eid) && key.equals(mNavigationKey)) {
//            isNavigating = false;
//            mNavigationKey = "";
//            clearNaviMap();
//            mLocation.callOnClick();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        String[] watcheid = new String[1];
//        switch (v.getId()) {
//            case R.id.title_all_dev:
//
//                break;
//
//            case R.id.change_my_location:
//                if (0 == mMyLocationIsVisibale) {
//
//                    if (ActivityCompat.checkSelfPermission(mHostActivity, Manifest.permission
//                            .ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(mHostActivity, new String[]{Manifest
//                                .permission.ACCESS_FINE_LOCATION}, 2);
//                        break;
//                    }
//
//                    if (0 == mMyLocationChangeTips) {
//                        if (!isWifiOpen() && !isGpsOpen() && isAdded()) {
//                            DialogUtil.CustomSelectorDialog(mHostActivity, getString(R.string
//                                            .open_mylocation_title), getString(R.string
//                                            .open_mylocation_content_gps_wifi),
//                                    new DialogUtil.OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                        }
//                                    }, getString(R.string.cancel), new DialogUtil
//                                            .OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            mMyLocationIsVisibale = 1;
//                                            mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE,
//                                                     1);
//                                            mChangeMyLoc.setBackgroundResource(R.drawable
//                                                    .phone_position_1);
//                                            stopPhoneLocation();
//                                            startPhoneLocation(false);
//                                        }
//                                    }, getString(R.string.devicelist_confirm_open), new
//                                            DialogUtil.OnSelectorDialogLister() {
//                                                @Override
//                                                public Boolean onClick(View v) {
//                                                    if (0 == mMyLocationChangeTips) {
//                                                        mMyLocationChangeTips = 1;
//                                                        mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return true;
//                                                    } else {
//                                                        mMyLocationChangeTips = 0;
//                                                        mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return false;
//                                                    }
//                                                }
//                                            }).show();
//                        } else if (isWifiOpen() && !isGpsOpen() && isAdded()) {
//                            DialogUtil.CustomSelectorDialog(mHostActivity, getString(R.string
//                                            .open_mylocation_title), getString(R.string
//                                            .open_mylocation_content_gps),
//                                    new DialogUtil.OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                        }
//                                    }, getString(R.string.cancel), new DialogUtil
//                                            .OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            mMyLocationIsVisibale = 1;
//                                            mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE,
//                                                     1);
//                                            mChangeMyLoc.setBackgroundResource(R.drawable
//                                                    .phone_position_1);
//                                            stopPhoneLocation();
//                                            startPhoneLocation(false);
//                                        }
//                                    }, getString(R.string.devicelist_confirm_open), new
//                                            DialogUtil.OnSelectorDialogLister() {
//                                                @Override
//                                                public Boolean onClick(View v) {
//                                                    if (0 == mMyLocationChangeTips) {
//                                                        mMyLocationChangeTips = 1;
//                                                        mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return true;
//                                                    } else {
//                                                        mMyLocationChangeTips = 0;
//                                                        mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return false;
//                                                    }
//                                                }
//                                            }).show();
//                        } else if (!isWifiOpen() && isGpsOpen() && isAdded()) {
//                            DialogUtil.CustomSelectorDialog(mHostActivity, getString(R.string
//                                            .open_mylocation_title), getString(R.string
//                                            .open_mylocation_content_wifi),
//                                    new DialogUtil.OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                        }
//                                    }, getString(R.string.cancel), new DialogUtil
//                                            .OnCustomDialogListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            mMyLocationIsVisibale = 1;
//                                            mApp.setValue(Const
//                                                    .SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE,
//                                                     1);
//                                            mChangeMyLoc.setBackgroundResource(R.drawable
//                                                    .phone_position_1);
//                                            stopPhoneLocation();
//                                            startPhoneLocation(false);
//                                        }
//                                    }, getString(R.string.devicelist_confirm_open), new
//                                            DialogUtil.OnSelectorDialogLister() {
//                                                @Override
//                                                public Boolean onClick(View v) {
//                                                    if (0 == mMyLocationChangeTips) {
//                                                        mMyLocationChangeTips = 1;
//                                                        mApp.setValue(Const
//                                                                        .SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return true;
//                                                    } else {
//                                                        mMyLocationChangeTips = 0;
//                                                        mApp.setValue(Const
//                                                                        .SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS,
//                                                                mMyLocationChangeTips);
//                                                        return false;
//                                                    }
//                                                }
//                                            }).show();
//                        } else {
//                            mMyLocationIsVisibale = 1;
//                            mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 1);
//                            mChangeMyLoc.setBackgroundResource(R.drawable.phone_position_1);
//                            stopPhoneLocation();
//                            startPhoneLocation(false);
//                        }
//                    } else {
//                        mMyLocationIsVisibale = 1;
//                        mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 1);
//                        mChangeMyLoc.setBackgroundResource(R.drawable.phone_position_1);
//                        stopPhoneLocation();
//                        startPhoneLocation(false);
//                    }
//                } else if (1 == mMyLocationIsVisibale) {
//                    mMyLocationIsVisibale = 0;
//                    mApp.setValue(Const.SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 0);
//                    mChangeMyLoc.setBackgroundResource(R.drawable.btn_add_my_location_selector);
//                    stopPhoneLocation();
//                    setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(),
//                            mApp.getCurUser().getFocusWatch().getEid(), 2);
//                }
//                break;
//
//            case R.id.switch_traceing_button:
//                mApp.sdcardLog("onClick e2etraceTo");
//                Integer state = mApp.getmWatchIsOn().get(focusWatch.getEid());
//                if (!isAdded()) {
//                    return;
//                }
//                if (state == null || state == Const.WATCH_STATE_MAYBE_POWER_OFF) {
//                    ToastUtil.show(mHostActivity, getString(R.string.watch_state_unknown) +
//                            getString(R.string.trace_failure));
//                    return;
//                } else if (state == Const.WATCH_STATE_POWER_OFF) {
//                    ToastUtil.show(mHostActivity, getString(R.string.watch_poweroff_prompt) +
//                            getString(R.string.trace_failure));
//                    return;
//                } else if (state == Const.WATCH_STATE_FLIGHT && mApp.isFlightModeTime()) {
//                    ToastUtil.show(mHostActivity, getString(R.string.watch_state_flight) +
//                            getString(R.string.trace_failure));
//                    return;
//                } else if (state == Const.WATCH_STATE_POWER_OFF_LOW_POWER) {
//                    ToastUtil.show(mHostActivity, getString(R.string.watch_state_low_power_off) +
//                            getString(R.string.trace_failure));
//                    return;
//                }
//                if (isOnTraceToIsActive) {//标志位，如果已经发送了一条信息，在该信息返回之前，不进行任何操作
//                    return;
//                }
//                openTraceLocationDialog();
//                break;
//
//            case R.id.zoomout:
//                if (mAMap != null) {
//                    mAMap.setZoomLevel(mAMap.getZoomLevel() + 2, Map.Animation.LINEAR);
//                }
//                break;
//
//            case R.id.zoomin:
//                if (mAMap != null) {
//                    mAMap.setZoomLevel(mAMap.getZoomLevel() - 2, Map.Animation.LINEAR);
//                }
//                break;
//
//            case R.id.location:
//                final String eid = mApp.getCurUser().getFocusWatch().getEid();
//                watcheid[0] = eid;
//                if ((mWatchClickFlag.get(eid) == null || !mWatchClickFlag.get(eid))) {
//                    long intervaiTime = Const.LOCATION_AFTER_SUCCESS_CLOCK_VALUE + 1;
//                    if (focusWatch.getCurLocation() != null) {
//                        intervaiTime = TimeUtil.getChatTime(focusWatch.getCurLocation()
//                                .getTimestamp());
//                    }
//                    if (focusWatch.getCurLocation() != null &&
//                            intervaiTime < Const.LOCATION_AFTER_SUCCESS_CLOCK_VALUE &&
//                            intervaiTime > 0) {
//                        mWatchClickFlag.put(eid, false);
//                        setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 0);
//                    } else {
//                        mWatchClickFlag.put(eid, true);
//                        if (mWatchClickTask.get(eid) != null) {
//                            mWatchClickTask.get(eid).cancel(true);
//                        }
//                        mLocation.setBackgroundResource(R.drawable.mylocation_1);
//                        mApp.sdcardLog("huangqilin location onclick:" + TimeUtil
//                                .getTimeStampLocal());
//                        StringBuilder recordLocation = new StringBuilder(getResources().getString
//                                (R.string.map_fragment_active_location));
//                        if (mApp.getCurUser().getFocusWatch().isDevice102()) {
//                            recordLocation.append("_102");
//                        } else {
//                            recordLocation.append("_105");
//                        }
//                        mApp.sdcardLog("e2eGetWatchLocation 2");
//                        e2eGetWatchLocation(watcheid);
//                        retrieveTraceData(eid, TimeUtil.getReversedOrderTime(Const
//                                .FUTURE_TIME_KEY), 1);
//                        setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                        mWatchClickTask.put(eid, getLocationTask(eid));
//                        mWatchClickTask.get(watcheid[0]).execute(eid);
//                    }
//                }
//                break;
//
//            case R.id.history_trace:
//                startActivity(new Intent(mHostActivity, GoogleMapHistoryTraceActivity.class));
//                //startActivity(new Intent(mHostActivity, HereMapHistoryActivity.class));
//                break;
//
//            case R.id.btn_more_func:
//                ItemListPopUpWindow mMenuWindow = new ItemListPopUpWindow(mHostActivity, null,
//                        mMoreFunc, handler);
//                if (mApp.getCurUser().getWatchList().size() == 1)
//                    mMenuWindow.showAsDropDown(mTitle);
//                else
//                    mMenuWindow.showAsDropDown(viewRoot.findViewById(R.id.watch_scroll));
//                mMoreFunc.setBackgroundResource(R.drawable.img_more_func_sel);
//                break;
//
//            case R.id.guide_control:
//                if (isFirstIn) {
//                    guide1.setVisibility(View.GONE);
//                    guidely.setVisibility(View.GONE);
//                    isFirstIn = false;
//                    mApp.setValue(Const.SHARE_PREF_IS_FIRST_MAINMAP, false);
//                }
//                break;
//
//            case R.id.wake_up:
//                Dialog dlg = DialogUtil.CustomNormalDialog(mHostActivity,
//                        getString(R.string.wake_up_device_rightnow), getString(R.string
//                                .wake_up_device_rightnow_desc),
//                        new DialogUtil.OnCustomDialogListener() {
//                            @Override
//                            public void onClick(View v) {
//                            }
//                        }, getString(R.string.cancel),
//                        new DialogUtil.OnCustomDialogListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String simNo = focusWatch.getCellNum();
//                                if (simNo == null || "".equals(simNo)) {
//                                    ToastUtil.show(mHostActivity, getString(R.string
//                                            .device_has_no_simnumber));
//                                    return;
//                                }
//                                SmsUtil.sendWakeUpMsgToDevice(mHostActivity, focusWatch.getEid(),
//                                        simNo);
//                            }
//                        }, getString(R.string.confirm));
//                dlg.show();
//                break;
//
//            case R.id.layout_network_error: {
//                Intent intent = new Intent(mHostActivity, ErrorPromptActivity.class);
//                intent.putExtra("type", "neterror");
//                mHostActivity.startActivity(intent);
//                break;
//            }
//        }
//    }
//
//    private MioAsyncTask<String, String, String> getLocationTask(final String eid) {
//
//        return new MioAsyncTask<String, String, String>() {
//
//            @Override
//            protected String doInBackground(String... params) {
//                String eid = params[0];
//                mApp.sdcardLog("huangqilin mWatchClickTask doInBackground");
//                try {
//                    Thread.sleep(Const.LOCATION_CLICK_CLOCK_VALUE * 1000);
//                } catch (InterruptedException  e) {
//                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                }
//                return eid;
//            }
//
//            protected void onCancelled() {
//                mApp.sdcardLog("huangqilin mWatchClickTask onCancelled");
//                if (mApp.getCurUser().getFocusWatch().getEid().equals(eid)) {
//                    mLocation.setBackgroundResource(R.drawable.btn_location_selector);
//                }
//            }
//
//            protected void onPostExecute(String result) {
//                if (!isAdded()) return;
//                mApp.sdcardLog("huangqilin mWatchClickTask onPostExecute");
//                try {
//                    if (!isAdded()) {
//                        return;
//                    }
//                    String toastText;
//                    toastText = getString(R.string.text5, mApp.getCurUser().queryWatchDataByEid
//                            (result).getNickname());
//                    mWatchClickFlag.put(result, false);
//                    if (isAdded() && HereMapFragment.this.getUserVisibleHint())
//                        ToastUtil.show(mApp, toastText);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (mApp.getCurUser().getFocusWatch() != null
//                        && mApp.getCurUser().getFocusWatch().getEid().equals(result)) {
//                    mLocation.setBackgroundResource(R.drawable.btn_location_selector);
//                    Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                    it.putExtra("fouce_eid", eid);
//                    mHostActivity.sendBroadcast(it);
//                }
//            }
//        };
//    }
//
//    private void checkPhoneNum() {
//
//        final String number = focusWatch.getCellNum();
//        if (number != null && number.length() > 0 && number.length() <= 18) {
//            if (!isAdded()) {
//                return;
//            }
//            if (mApp.getStringValue(focusWatch.getEid() + Const
//                    .SHARE_PREF_FIELD_AUTO_RECEIVE_NOTIFY, "1").equals("1")) {
//                Dialog dlg = DialogUtil.CustomNormalDialog(mHostActivity, getText(R.string
//                                .call_baby_notify).toString(),
//                        getString(R.string.call_baby_notify_content),
//                        new DialogUtil.OnCustomDialogListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                mApp.setValue(focusWatch.getEid() + Const
//                                        .SHARE_PREF_FIELD_AUTO_RECEIVE_NOTIFY, "0");
//                                makeACall(number);
//                            }
//                        },
//                        getText(R.string.donothing_text).toString());
//                dlg.show();
//            } else {
//                makeACall(number);
//            }
//        } else {
//            if (!isAdded()) {
//                return;
//            }
//            if (mApp.getCurUser().isMeAdminByWatch(focusWatch)) {
//                ToastUtil.show(mApp, getString(R.string.set_watch_number));
//                Intent intent = new Intent(mHostActivity, SetDeviceNumberActivity.class);
//                intent.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
//                startActivity(intent);
//            } else {
//                ToastUtil.show(mApp, getString(R.string.admin_set_watch_number));
//            }
//        }
//    }
//
//    private void makeACall(String number) {
//        //直接拨打出去
//        mApp.sdcardLog("TelephoneCall: App made a phone call to watch(childNumber)");
//        if (ActivityCompat.checkSelfPermission(mHostActivity, Manifest.permission.CALL_PHONE) ==
//                PERMISSION_GRANTED) {
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + number));
//            startActivity(intent);
//        } else {
//            ActivityCompat.requestPermissions(mHostActivity, new String[]{Manifest.permission
//                    .CALL_PHONE}, 2);
//        }
//    }
//
//    private void startMonitorDialog(final int type) {
//        if (!isAdded()) {
//            return;
//        }
//        String callBackNumber = mApp.getStringValue(focusWatch.getEid() + Const
//                .SHARE_PREF_CALL_BACK_NUMBER, Const.DEFAULT_NEXT_KEY);
//        String callBackAttri = mApp.getStringValue(focusWatch.getEid() + Const
//                .SHARE_PREF_CALL_BACK_ATTRI, Const.DEFAULT_NEXT_KEY);
//        String textStr1 = "<font color=\"#a3a3a3\">" + getString(R.string
//                .send_monitor_number_tips) + "</font>";
//        String title = getString(R.string.listen_tel_title);
//        if (type == NewMainActivity.GET_MONITOR_NUMBER_REQUEST) {
//            textStr1 = "<font color=\"#a3a3a3\">" + getString(R.string.send_monitor_number_tips)
//                    + "</font>";
//            title = getString(R.string.listen_tel_title);
//        } else if (type == NewMainActivity.GET_CALLBACK_NUMBER_REQUEST) {
//            textStr1 = "<font color=\"#a3a3a3\">" + getString(R.string.send_callback_number_tips)
//                    + "</font>";
//            title = getString(R.string.call_back_tel_title);
//        }
//
//        boolean isFamilyMember = false;
//        String jsonStr = mApp.getStringValue(focusWatch.getEid() + Const
//                .SHARE_PREF_DEVICE_CONTACT_KEY, null);
//        ArrayList<PhoneNumber> phoneWhiteList = CloudBridgeUtil.parseContactListFromJsonStr
//                (jsonStr);
//        if (callBackNumber.equals(Const.DEFAULT_NEXT_KEY)) {
//            for (PhoneNumber data : phoneWhiteList) {
//                if (data.userEid != null && data.userEid.equals(mApp.getCurUser().getEid()) &&
//                        data.number != null) {
//                    callBackNumber = data.number;
//                    callBackAttri = mApp.getRelation(data);
//                    isFamilyMember = true;
//                    mApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_CALL_BACK_NUMBER,
//                            callBackNumber);
//                    mApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_CALL_BACK_ATTRI,
//                            callBackAttri);
//                    break;
//                }
//            }
//        } else {
//            for (PhoneNumber data : phoneWhiteList) {
//                if ((data.number != null && data.number.length() > 0 && data.number.endsWith
//                        (callBackNumber)) ||
//                        (data.subNumber != null && data.subNumber.length() > 0 && data.subNumber
//                                .endsWith(callBackNumber))) {
//                    isFamilyMember = true;
//                    break;
//                }
//            }
//        }
//
//        String text;
//        if (callBackNumber.length() > 0 && !callBackNumber.equals(Const.DEFAULT_NEXT_KEY) &&
//                isFamilyMember) {
//            text = callBackAttri + " " + callBackNumber;
//        } else {
//            if (!isAdded()) {
//                return;
//            }
//            text = getString(R.string.choose_number_hint);
//        }
//
//        final String finalCallNumber = callBackNumber;
//        Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithSelect(mHostActivity,
//                title,
//                Html.fromHtml(textStr1),
//                new CustomSelectDialogUtil.CustomDialogListener() {
//                    @Override
//                    public void onClick(View v, String text) {
//                        Intent it = new Intent(mHostActivity, SelecterCallBackNumber.class);
//                        it.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
//                        mHostActivity.startActivityForResult(it, type);
//                    }
//                }, text,
//                new CustomSelectDialogUtil.CustomDialogListener() {
//
//                    @Override
//                    public void onClick(View v, String text) {
//                    }
//                },
//                getText(R.string.cancel).toString(),
//                new CustomSelectDialogUtil.CustomDialogListener() {
//
//                    @Override
//                    public void onClick(View v, String text) {
//                        if (!isAdded()) {
//                            return;
//                        }
//                        if (text != null && text.length() > 0 && !text.equals(getString(R.string
//                                .choose_number_hint))) {
//                            sendDeviceListenMsg(finalCallNumber, type, focusWatch.getEid());
//
//                        } else {
//                            if (!isAdded()) {
//                                return;
//                            }
//                            ToastUtil.show(mHostActivity, getString(R.string.number_not_null));
//                        }
//                    }
//                },
//                getText(R.string.confirm).toString(), getResources().getDrawable(R.drawable
//                        .btn_pick_contact_selector),
//                new CustomSelectDialogUtil.CustomDialogListener() {
//                    @Override
//                    public void onClick(View v, String text) {
//                        Intent it = new Intent(mHostActivity, SelecterCallBackNumber.class);
//                        it.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
//                        mHostActivity.startActivityForResult(it, type);
//                    }
//                });
//        dlg.show();
//    }
//
//    private void sendDeviceListenMsg(String number, int type, String eid) {
//        if (null != mHostActivity.getNetservice()) {
//            mHostActivity.getNetservice().sendDeviceListenMsg(eid, number, type, this);
//            if (type == 1) {
//                mApp.setValue(eid + Const.SHARE_PREF_CALL_BACK_STATE, 1);
//            } else {
//                mApp.setValue(eid + Const.SHARE_PREF_LISTEN_STATE, 1);
//            }
//        }
//    }
//
//    private void openTakePhotoDialog() {
//
//        Dialog takePhotoDlg = DialogUtil.CustomNormalDialog(getActivity(),
//                getText(R.string.head_edit_camera).toString(),
//                getText(R.string.takephoto_confirm_msg).toString(),
//                new DialogUtil.OnCustomDialogListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                },
//                getText(R.string.cancel).toString(),
//                new DialogUtil.OnCustomDialogListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (mHostActivity.getNetservice() != null) {
//                            mHostActivity.getNetservice().sendTakePhotoMsg(focusWatch.getEid(),
//                                    HereMapFragment.this);
//                        }
//                    }
//                },
//                getText(R.string.confirm).toString());
//        takePhotoDlg.show();
//    }
//
//    void setPhoneLastLocation(Location location) {
//        if (mApp.getCurUser().getLocation() == null) {
//            mApp.getCurUser().setLocation(new LocationData());
//        }
//        if (location.hasAccuracy()) {
//            mApp.getCurUser().getLocation().setAccuracy(location.getAccuracy());
//        }
//        mApp.getCurUser().getLocation().setLatitude(location.getLatitude());
//        mApp.getCurUser().getLocation().setLongitude(location.getLongitude());
//        mApp.getCurUser().getLocation().setMapType("2");
//        mApp.getCurUser().getLocation().setWgs84Latlng(new LatLng(location.getLatitude(),
//                location.getLongitude()));
//        LocationDAO.getInstance(mApp).updateLocation(mApp.getCurUser().getEid(), mApp
//                .getCurUser().getLocation());
//    }
//
//
//    LocationListener locationListener = new LocationListener() {
//
//        @Override
//        public void onLocationChanged(Location location) {
//            // 当位置发生变化的时候会自动调用该方法，参数location记录了最新的位置信息。
//            LogUtil.i(TAG + "手写location lat:" + location.getLatitude() + ",lon:" + location
//                    .getLongitude());
//            LocationUtils.handlePhoneLocation(mApp, new LatLng(location.getLatitude(), location
//                    .getLongitude()));
//            setPhoneLastLocation(location);
//
//            GeoCoordinate geoCoordinate_phone = new GeoCoordinate(location.getLatitude(), location.getLongitude());
//
//
//
//            startP(geoCoordinate_phone);
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//    private void startP(GeoCoordinate geoCoordinate_phone) {
//        mMyLocationIsVisibale = mApp.getIntValue(Const
//                .SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 0);
//        if (mMyLocationIsVisibale == 0 || mApp.getCurUser().getFocusWatch().getCurLocation() ==
//                null) {
//            return;
//        }
//        if (mApp.getCurUser().getFocusWatch() != null && mApp.getCurUser().getFocusWatch()
//                .getCurLocation() != null) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mine);
//            double dis = HereUtils.computeDistanceBetween(geoCoordinate_phone, new GeoCoordinate
//                    (mApp.getCurUser().getFocusWatch().getCurLocation().getLatitude(), mApp
//                    .getCurUser()
//                            .getFocusWatch().getCurLocation().getLongitude()));
//            bitmap = HereUtils.rotateBimap(mHostActivity, (float) dis, bitmap);
//            Image marker_img = new Image();
//            try {
////                marker_img.setImageResource(R.drawable.mine);
//                marker_img.setBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            if (marker_phone == null) {
//                marker_phone = new MapMarker();
//                if (geoCoordinate_phone.isValid())
//                    marker_phone.setCoordinate(geoCoordinate_phone);
//                else
//                    return;
//            }
//
//
//            marker_phone.setIcon(marker_img);
//
//            mAMap.addMapObject(marker_phone);
////            mAMap.addMapObject(currentPositionMapLocalModel);
//
//            if (mSensorHelper != null) {
//                mSensorHelper.setCurrentMarker(currentPositionMapLocalModel);
//            }
//            if (list.size() > 0) {
//                list.clear();
//            }
//            list.add(new GeoCoordinate(mApp.getCurUser().getFocusWatch().getCurLocation()
//                    .getLatitude(), mApp.getCurUser().getFocusWatch().getCurLocation()
//                    .getLongitude()));
//            list.add(geoCoordinate_phone);
//            GeoBoundingBox bounds = GeoBoundingBox.getBoundingBoxContainingGeoCoordinates(list);
//            mAMap.zoomTo(bounds, Map.Animation.LINEAR, 0f);
//            mAMap.setCenter(bounds.getCenter(), Map.Animation.LINEAR);
//            Log.i("cui", "startP----GeoBoundingBox-----");
//        }
//    }
//
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case NewMainActivity.GET_MONITOR_NUMBER_REQUEST:
//                startMonitorDialog(NewMainActivity.GET_MONITOR_NUMBER_REQUEST);
//                break;
//            case NewMainActivity.GET_CALLBACK_NUMBER_REQUEST:
//                startMonitorDialog(NewMainActivity.GET_CALLBACK_NUMBER_REQUEST);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void initView(View view) {
//
//        if (focusWatch == null) {
//            return;
//        }
//        layoutWatchState = view.findViewById(R.id.layout_watch_state);
//        mWatchStateText = view.findViewById(R.id.watch_state);
//        mScaleLine = view.findViewById(R.id.scale_line);
//        mNetworkText = view.findViewById(R.id.title_network_state);
//        mNetworkLayout = view.findViewById(R.id.layout_network_error);
//        mNetworkLayout.setOnClickListener(this);
//        mHead = view.findViewById(R.id.head);
//        ImageUtil.setMaskImage(mHead, R.drawable.head_1, mApp.getHeadDrawableByFile(mApp
//                        .getResources(),
//                mApp.getCurUser().getHeadPathByEid(focusWatch.getEid()),
//                focusWatch.getEid(), R.drawable.small_default_head));
//        mSignal = view.findViewById(R.id.signal);
//        mBattery = view.findViewById(R.id.battery);
//        mBatteryText = view.findViewById(R.id.text_battery);
//        mTitle = view.findViewById(R.id.title);
//        mZoomOut = view.findViewById(R.id.zoomout);    //放大地图按钮
//        mZoomOut.setOnClickListener(this);
//        mZoomIn = view.findViewById(R.id.zoomin);        //缩小地图按钮
//        mZoomIn.setOnClickListener(this);
//        mLocation = view.findViewById(R.id.location);    //手表定位按钮
//        mLocation.setOnClickListener(this);
//        mTraceLocation = view.findViewById(R.id.switch_traceing_button);
//        mTraceLocation.setOnClickListener(this);
//        mChangeMyLoc = view.findViewById(R.id.change_my_location); //切换本机位置显示状态
//        mChangeMyLoc.setOnClickListener(this);
//        mHistoryLocs = view.findViewById(R.id.history_trace);    //轨迹按钮
//        mHistoryLocs.setOnClickListener(this);
//        mLocText = view.findViewById(R.id.location_text);  //定位显示信息
//        mLocOtherText = view.findViewById(R.id.other_text);  //定位精度\时间
//        mMoreFunc = view.findViewById(R.id.btn_more_func);
//        mMoreFunc.setOnClickListener(this);
//
//        guide1 = view.findViewById(R.id.guide1);
//        guidely = view.findViewById(R.id.guide_control);
//        guidely.setOnClickListener(this);
//        isFirstIn = mApp.getBoolValue(Const.SHARE_PREF_IS_FIRST_MAINMAP, true);
//        if (!isFirstIn) {
//            guide1.setVisibility(View.GONE);
//            guidely.setVisibility(View.GONE);
//        }
//
//        mBtnWakeUp = view.findViewById(R.id.wake_up);
//        mBtnWakeUp.setOnClickListener(this);
//
//        RelativeLayout debug = view.findViewById(R.id.debug);
//        TextView debug_txt = view.findViewById(R.id.debug_txt);
//        ToolUtils.showAppVerInfo(mHostActivity, debug_txt, debug);
//
//
//    }
//
//
//    private void initparameter() {
//
//        mMyLocationChangeTips = mApp.getIntValue(Const.SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS, 0);
//        for (WatchData watch : mApp.getCurUser().getWatchList()) {
//            mWatchClickFlag.put(watch.getEid(), false);
//            mApp.setValue(watch.getEid() + Const.SHARE_PREF_CALL_BACK_STATE, 0);
//            mApp.setValue(watch.getEid() + Const.SHARE_PREF_LISTEN_STATE, 0);
//        }
//
//    }
//
//
//    private void changeWatchRefresh() {
//
//        clearNaviMap();
//        focusWatch = mApp.getCurUser().getFocusWatch();
//        String focus = focusWatch.getEid();
//        for (WatchData watch : mApp.getWatchList()) {
//            if (!watch.getEid().equals(focus) && (mWatchTask.get(watch.getEid()) != null)) {
//                mWatchTask.get(watch.getEid()).cancel(true);
//            }
//            if (watch.getCurLocation() == null) {
//                watch.setCurLocation(LocationDAO.getInstance(mApp).readLocation(watch.getEid()));
//            }
//        }
//        if (mWatchBatteryTime.get(focus) == null
//                || mWatchBatteryTime.get(focus) < (System.currentTimeMillis() - Const
//                .GET_WATCH_BATTERY_TIME)) {
//            mWatchBatteryTime.put(focus, System.currentTimeMillis());
//
//            String[] keys = new String[12];
//            keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
//            keys[1] = CloudBridgeUtil.WATCH_ONOFF_FLAG;
//            keys[2] = CloudBridgeUtil.OPERATION_MODE_VALUE;
//            keys[3] = CloudBridgeUtil.SILENCE_LIST;
//            keys[4] = CloudBridgeUtil.SLEEP_LIST;
//            keys[5] = CloudBridgeUtil.SIGNAL_LEVEL;
//            keys[6] = CloudBridgeUtil.DEVICE_POWER_ON_TIME;
//            keys[7] = CloudBridgeUtil.KEY_NAME_CHARGE_STATUS;
//            keys[8] = CloudBridgeUtil.STEPS_NOTIFICATION_SETTING;
//            keys[9] = CloudBridgeUtil.KEY_STORY_SWITCH + mApp.getCurUser().getFocusWatch()
//                    .getDeviceType();
//            keys[10] = CloudBridgeUtil.OFFLINE_MODE_VALUE;
//            keys[11] = CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE;
//            mHostActivity.mapMGet(focus, keys, HereMapFragment.this);
//        }
//
//        updateTitle(focus);
//        setSafeAreaChange(focus);
//
//        //更新锁定定位图标
//        Boolean tempFlag = mWatchClickFlag.get(focus);
//        if (tempFlag != null) {
//            if (!tempFlag) {
//                mLocation.setBackgroundResource(R.drawable.btn_location_selector);
//            } else {
//                mLocation.setBackgroundResource(R.drawable.mylocation_1);
//            }
//        } else {
//            mWatchClickFlag.put(focus, false);
//            mLocation.setBackgroundResource(R.drawable.btn_location_selector);
//        }
//        startWatchLocation();
//        updateWatchCurrentState(focus);
//        // TODO: 2019/8/9 追踪配置
//        mTraceLocation.setVisibility(View.VISIBLE);
//        if (isAdded()) {
//            ImageUtil.setMaskImage(mHead, R.drawable.mask, mApp.getHeadDrawableByFile
//                    (getResources(),
//                            mApp.getCurUser().getHeadPathByEid(focus), focus, R.drawable
//                                    .small_default_head));
//        }
//        mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
//    }
//
//    private void startWatchLocation() {
//
//        if (mHostActivity.getNetservice() != null && mHostActivity.getNetservice()
//                .isCloudBridgeClientOk()) {
//            String[] eid = new String[1];
//            eid[0] = focusWatch.getEid();
//            retrieveTraceData(eid[0], TimeUtil.getReversedOrderTime(Const.FUTURE_TIME_KEY), 1);
//            Integer stateWatch = mApp.getmWatchIsOn().get(eid[0]);
//            if ((focusWatch.getCurLocation() == null ||
//                    TimeUtil.getChatTime(focusWatch.getCurLocation().getTimestamp()) > Const
//                            .WATCH_BACKHOME_LOCATION_TIMEOUT
//                    || (TimeUtil.getChatTime(focusWatch.getCurLocation().getTimestamp()) + Const
//                    .WATCH_BACKHOME_LOCATION_TIMEOUT) < 0)
//                    && (mApp.getmWatchBackhomeLocationFlag().get(eid[0]) == null || !mApp
//                    .getmWatchBackhomeLocationFlag().get(eid[0]))
//                    && (!mApp.isFlightModeTime())
//                    && (stateWatch == null || (stateWatch != Const.WATCH_STATE_POWER_OFF &&
//                    stateWatch != Const.WATCH_STATE_MAYBE_POWER_OFF))) {
//                mApp.getmWatchBackhomeLocationFlag().put(eid[0], true);
//                if (mWatchClickTask.get(eid[0]) != null) {
//                    mWatchClickTask.get(eid[0]).cancel(true);
//                }
//                mWatchClickTask.put(eid[0], getLocationTask(eid[0]));
//                mWatchClickTask.get(eid[0]).execute(eid);
//                mApp.sdcardLog("huangqilin 3 Backhome eid:" + eid[0] + " flag:" + mApp
//                        .getmWatchBackhomeLocationFlag().get(eid[0]));
//                if ((mWatchClickFlag.get(eid[0]) == null || !mWatchClickFlag.get(eid[0]))) {
//
//                    long intervaiTime = Const.LOCATION_AFTER_SUCCESS_CLOCK_VALUE + 1;
//                    if (focusWatch.getCurLocation() != null) {
//                        intervaiTime = TimeUtil.getChatTime(focusWatch.getCurLocation()
//                                .getTimestamp());
//                    }
//                    if (!(mApp.getCurUser().getFocusWatch().getCurLocation() != null &&
//                            intervaiTime < Const.LOCATION_AFTER_SUCCESS_CLOCK_VALUE &&
//                            intervaiTime > 0)) {
//                        mWatchClickFlag.put(eid[0], true);
//                        mLocation.setBackgroundResource(R.drawable.mylocation_1);
//                        setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                    }
//                }
//                mApp.sdcardLog("e2eGetWatchLocation 1");
//                retrieveTraceData(eid[0], TimeUtil.getReversedOrderTime(Const.FUTURE_TIME_KEY), 1);
//                e2eGetWatchLocation(eid);
//            }
//            //获取当前手表是否在线的状态信息
//            mApp.getNetService().getDeviceOfflineState(eid[0]);
//            if (focusWatch.isDevice705()) {
//                mHostActivity.getNetservice().getWatchNaviState(eid[0], null);
//            }
//        }
//    }
//
//    private void retrieveTraceData(String eid, String key, int num) {
//
//        MyMsgData retrieve = new MyMsgData();
//        retrieve.setCallback(HereMapFragment.this);
//        JSONObject pl = new JSONObject();
//        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
//        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key);
//        pl.put(CloudBridgeUtil.KEY_NAME_SIZE, num);
//        retrieve.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_RETRIEVE_TRACE_DATA, pl));
//        if (mHostActivity.getNetservice() != null) {
//            mHostActivity.getNetservice().sendNetMsg(retrieve);
//        }
//    }
//
//    private void setSafeAreaChange(String eid) {
//        if (eid.equals(focusWatch.getEid())) {
//            if (focusWatch.getCurLocation() != null) {
//                setWatchMarker(focusWatch.getCurLocation(), eid, 2);
//            } else {
//                mLocText.setText(R.string.null_location);
//                mLocOtherText.setText(R.string.null_location_other);
//                if (mAMap != null) {
//                }
//            }
//        }
//    }
//
//    private void startPhoneLocation(boolean isOnceLocation) {
//
//
//        mMyLocationIsVisibale = mApp.getIntValue(Const
//                .SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE, 0);
//        if (mMyLocationIsVisibale == 0) {
//            return;
//        }
//        if (ActivityCompat.checkSelfPermission(mHostActivity, Manifest.permission
//                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
//                .checkSelfPermission(mHostActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        if (mAMap == null) {
//            return;
//        }
//        Log.i(TAG, "startPhoneLocation----");
//
//        if(mPositioningManager != null){
//            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
//            Location  location_phone= mPositioningManager.getDataSource().getLastKnownLocation();
//            if(location_phone !=null){
//                GeoCoordinate geoCoordinate_phone = new GeoCoordinate(location_phone.getLatitude(), location_phone.getLongitude());
//
//                locationManager.requestLocationUpdates(provider, 50000,15,locationListener);
//                startP(geoCoordinate_phone);
//            }
//        }
//
//        if (!isOnceLocation) {
//            if (mSensorHelper != null) {
//                mSensorHelper.unRegisterSensorListener();
//                mSensorHelper.setCurrentMarker(null);
//                mSensorHelper = null;
//            }
//            mSensorHelper = new SensorMarkerHelper(mHostActivity);
//            if (mSensorHelper != null) {
//                mSensorHelper.registerSensorListener();
//            }
//        }
//    }
//
//    private void setLocation(Location location) throws IOException {
//        if (location!=null) {
//            String mfeatureName = null;
//            Geocoder geocoder = new Geocoder(getContext());
//            List<Address> addList = null;// 解析经纬度
//            try {
//                addList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (addList != null && addList.size() > 0) {
//                for (int i = 0; i < addList.size(); i++) {
//                    Address add = addList.get(i);
//                    mfeatureName = add.getFeatureName();
//                    Log.d(TAG, "setLocation:获取到手机定位啦 "+mfeatureName);
//                    ToastUtil.show(getContext(),"util获取到手机定位啦"+mfeatureName);
//                }
//            }
//        }
//    }
//
//
//    // 停止定位
//    private void stopPhoneLocation() {
//
//        // 停止定位
//        if (mAMap == null) {
//            return;
//        }
//
//        if (locationManager != null){
//            locationManager.removeUpdates(locationListener);
//        }
//
//        if(mPositioningManager !=null){
//            mPositioningManager.stop();
//        }
//
//        mAMap.removeMapObject(marker_phone);
//        if (mApp.getCurUser().getFocusWatch() != null && mApp.getCurUser().getFocusWatch()
//                .getCurLocation() != null) {
//            mAMap.setCenter(new GeoCoordinate(mApp.getCurUser().getFocusWatch().getCurLocation()
//                            .getLatitude(), mApp.getCurUser().getFocusWatch().getCurLocation()
//                            .getLongitude()),
//                    Map.Animation.NONE);
//        }
//        mAMap.setZoomLevel(16);
//        if (mSensorHelper != null) {
//            mSensorHelper.unRegisterSensorListener();
//            mSensorHelper.setCurrentMarker(null);
//            mSensorHelper = null;
//        }
//    }
//
//    private Boolean isGpsOpen() {
//        LocationManager lm = (LocationManager) mHostActivity.getSystemService(Context
//                .LOCATION_SERVICE);
//        assert lm != null;
//        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        //获得手机是不是设置了GPS开启状态true：gps开启，false：GPS未开启
//    }
//
//    @NonNull
//    private Boolean isWifiOpen() {
//        WifiManager wm = (WifiManager) mHostActivity.getApplicationContext().getSystemService
//                (Context.WIFI_SERVICE);
//        assert wm != null;
//        return wm.isWifiEnabled();
//    }
//
////    private void setScaleText() {
////
////        if(!isAdded()){
////            return;
////        }
////        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
////        LogUtil.i(TAG + " cameraPosition.zoom = " + cameraPosition.zoom);
////        mScaleLine.update(cameraPosition.zoom, cameraPosition.target.latitude);
////    }
//
//    private void getRestLocationByWifi() {
//
//        ConnectivityManager conMag = (ConnectivityManager) mHostActivity.getSystemService(Context
//                .CONNECTIVITY_SERVICE);
//        NetworkInfo activeInfo = conMag.getActiveNetworkInfo();
//        if (activeInfo != null && (activeInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
//            startPhoneLocation(true);
//        }
//    }
//
//    /**
//     * 发送e2e给手表，获取定位原始数据
//     * watchEid：对应手表的eid
//     */
//    private void e2eGetWatchLocation(final String[] watchEid) {
//
//        MyMsgData e2e = new MyMsgData();
//        e2e.setCallback(HereMapFragment.this);
//        e2e.setTimeout(Const.LOCATION_CLICK_CLOCK_VALUE * 1000);
//        e2e.setFinalTimeout(Const.LOCATION_CLICK_CLOCK_VALUE * 1000);
//        e2e.setNeedNetTimeout(true);
//        JSONObject pl = new JSONObject();
//        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil
//                .SUB_ACTION_VALUE_NAME_GET_LOCATION);
//        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
//        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP, sn, mApp
//                .getToken(), null, watchEid, pl));
//        mApp.getmAppToWatchLocationTime().put(String.valueOf(sn), System.currentTimeMillis());
//        mLocationUseTime.put(watchEid[0], System.currentTimeMillis());
//        new MioAsyncTask<String, Long, String>() {
//            protected String doInBackground(String... params) {
//                String sn = params[0];
//                try {
//                    Thread.sleep(Const.WATCH_BACKHOME_LOCATION_TIMEOUT * 1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return sn;
//            }
//
//            protected void onCancelled() {
//                super.onCancelled();
//            }
//
//            protected void onPostExecute(String message) {
//                super.onPostExecute(message);
//                mLocationUseTime.remove(message);
//                if (mApp.getmAppToWatchLocationTime().get(message) != null) {
//                    mApp.locationStatistic(Const.WATCH_BACKHOME_LOCATION_TIMEOUT, watchEid[0]);
//                    mApp.getmAppToWatchLocationTime().remove(message);
//                }
//            }
//        }.execute(String.valueOf(sn));
//        if (mHostActivity.getNetservice() == null) {
//            return;
//        }
//        mHostActivity.getNetservice().sendNetMsg(e2e);
//    }
//
//    //根据服务器返回的location地址设置手表的位置， type=0表示Location获取，type=1表示retrieve
//    private void setWatchLocation(JSONObject pl, int type) {
//        LocationData location = new LocationData();
//        location.setTimestamp((String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP));
//        try {
//            if (pl.get("sn") != null && mLocationUseTime.get(String.valueOf(pl.get("sn"))) !=
//                    null) {
//                long test = (System.currentTimeMillis() - mLocationUseTime.get(String.valueOf(pl
//                        .get("sn")))) / 1000;
//                location.setUse_time(Long.valueOf(test).intValue());
//            }
//        } catch (Exception e) {
//
//        }
//        JSONObject result = (JSONObject) pl.get("result");
//        String stringTest;
//        /*0：没有得到定位结果；1： gps 定位结果；2：wifi 定位结果；3：混合定位结果；
//        4：基站定位结果；5：其他-->*/
//        switch (type) {
//            case 0:
//                LogUtil.d("huangqilin service location:" + TimeUtil.getTimeStampLocal());
//                break;
//            case 1:
//                LogUtil.d("huangqilin retrieve:" + TimeUtil.getTimeStampLocal());
//                break;
//            default:
//                break;
//        }
//        if (result != null) {
//            if (pl.get("status").equals("1") && !result.get("type").equals("0")) {
//                location.setType(Integer.valueOf((String) result.get("type")));
//                String city = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_CITY);
//                String province = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_PROVINCE);
//                if (city != null) {
//                    if (city.equals("") && province != null) {
//                        city = province;
//                    }
//                } else {
//                    city = "";
//                }
//                location.setCity(city);
//                location.setDescription((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_DESC));
//                location.setPoi((String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_POI));
//                location.setRadius(Integer.valueOf((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_RADIUS)));
//                location.setAccuracy(Integer.valueOf((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_RADIUS)));
//                location.setRoad((String) result.get(CloudBridgeUtil.KEY_NAME_LOCAITON_ROAD));
//                String maptype = (String) result.get(CloudBridgeUtil.KEY_NAME_LOCATION_MAPTYPE);
//                location.setMapType((maptype == null) ? "0" : maptype);
//                StringBuilder latlng = new StringBuilder((String) result.get(CloudBridgeUtil
//                        .KEY_NAME_LOCAITON_LOCATION));
//                int i = latlng.indexOf(",");
//                double longitude = Double.valueOf(latlng.substring(0, i));
//                double latitude = Double.valueOf(latlng.substring(i + 1));
//                LatLng temp = new LatLng(latitude, longitude);
////                location.setGoogleLatLng(temp);
//                location.setLatitude(latitude);
//                location.setLongitude(longitude);
//                setWatchMarker(location, (String) pl.get(CloudBridgeUtil.KEY_NAME_EID), type);
//                //xxxxxxxxxxxxxxxxxxxxxxxx
//            } else {
//                if (mApp.getCurUser().getFocusWatch().getCurLocation() != null) {
//                    setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(), mApp
//                            .getCurUser().getFocusWatch().getEid(), 2);
//                }
//            }
//        } else {
//            LogUtil.d("get location error,status=" + pl.get("status") + " info=" + pl.get("info"));
//            if (mApp.getCurUser().getFocusWatch().getCurLocation() != null) {
//                setWatchMarker(mApp.getCurUser().getFocusWatch().getCurLocation(), mApp
//                        .getCurUser().getFocusWatch().getEid(), 2);
//            }
//        }
//    }
//
//
//    private void watchNormalAnim() {
//
//        ImageView imageView = null;
//        GeoCoordinate geoCoordinate = null;
//        //Log.i("cui","setMarkerIcon");
//
//        if (mLocationAnimTask != null) {
//            mLocationAnimTask.cancel(true);
//            mLocationAnimTask = null;
//            Log.d(TAG, "watchNormalAnim: ");
//        }
//
//
//        if (mAMap != null && overlay != null) {
//            mAMap.removeMapOverlay(overlay);
//        }
//        if (mApp != null && mApp.getCurUser().getFocusWatch().getCurLocation() != null) {
//            double latude = mApp.getCurUser().getFocusWatch().getCurLocation().getLatitude();
//            double longitude = mApp.getCurUser().getFocusWatch().getCurLocation().getLongitude();
//            geoCoordinate = new GeoCoordinate(latude, longitude);
//        } else {
//            Log.e("cui", "mApp.getCurUser().getFocusWatch().getCurLocation()   is   null");
//        }
//
//        if (overlay == null) {
//            imageView = new ImageView(mHostActivity);
//            if (geoCoordinate != null) {
//                overlay = new MapOverlay(imageView, geoCoordinate);
//            }
//        } else {
//            imageView = (ImageView) overlay.getView();
//        }
//        if (mAMap != null && overlay != null) {
//            mAMap.addMapOverlay(overlay);
//        }
//
//        BitmapDrawable icon = null;
//        for (WatchData watch : mApp.getCurUser().getWatchList()) {
//            if (watch.getEid().equals(focusWatch.getEid())) {
//                icon = (BitmapDrawable) (mApp.getHeadDrawableByFile(getResources(), watch
//                        .getHeadPath(), watch.getEid(), R.drawable.coordinate_0));
//                break;
//            }
//        }
//        if (icon == null) {
//            icon = (BitmapDrawable) getResources().getDrawable(R.drawable.coordinate_0);
//        }
//
//        imageView.setImageBitmap(icon.getBitmap());
//
//        //设置偏移量 效果不是很好 先注释
//        /*PointF pointF =   marker_sw.getAnchorPoint();
//        pointF.offset(0,25);
//        marker_sw.setAnchorPoint(pointF);*/
//    }
//
//
//    /**
//     * 设置手表定位信息,type=0表示Location获取，type=1表示retrieve, type = 2表示本地记录位置 ，type = 3 逆地理编码
//     */
//    private void setWatchMarker(LocationData location, String eid, int type) {
//
//        //增加判空，解绑瞬间会产生
//        if (mApp.getWatchList() == null || mApp.getWatchList().size() == 0) {
//            return;
//        }
//        if (location != null && location.getLatLng() != null && location.getDescription() != null
//                && location.getTimestamp() != null) {
//            if (type != 2) {
//                mShowAllMarkerFlag = true;
//                for (WatchData watch : mApp.getWatchList()) {
//                    if (eid.equals(watch.getEid())) {
//                        watch.setCurLocation(location);
//                        LocationDAO.getInstance(mApp).updateLocation(eid, location);
//                    }
//                }
//            }
//            if (eid.equals(mApp.getCurUser().getFocusWatch().getEid()) &&
//                    mApp.getCurUser().getFocusWatch().getCurLocation() != null &&
//                    mApp.getCurUser().getFocusWatch().getCurLocation().getLatLng() != null) {
//
//                StringBuilder des = new StringBuilder();
//                if (mApp.getCurUser().getFocusWatch().getCurLocation().getPoi() != null
//                        && !mApp.getCurUser().getFocusWatch().getCurLocation().getPoi().equals
//                        ("null")
//                        && !mApp.getCurUser().getFocusWatch().getCurLocation().getPoi().equals
//                        ("")) {
//                    String road = mApp.getCurUser().getFocusWatch().getCurLocation().getRoad();
//                    if (road != null && !road.equals("null") && !road.equals("")) {
//                        des.append(road);
//                    }
//                    des.append(mApp.getCurUser().getFocusWatch().getCurLocation().getPoi());
//
//                } else {
//                    des.append(mApp.getCurUser().getFocusWatch().getCurLocation().getDescription());
//                }
//                mLocText.setText(des.toString());
//                final StringBuffer dimen = new StringBuffer();
//
//                int accuracy = (int) mApp.getCurUser().getFocusWatch().getCurLocation()
//                        .getAccuracy();
//                SharedPreferences prefs = mHostActivity.getSharedPreferences(
//                        DevOptActivity.DEV_OPT_PREF, Context.MODE_PRIVATE);
//                String show_type_radius = prefs.getString(
//                        DevOptActivity.SHOW_ALL_DOTS, "");
//                String locationtype = "";
//                if (show_type_radius.equalsIgnoreCase("true")) {
//                    if (location.getType() == 0) {
//                        locationtype = "N";
//                    } else if (location.getType() == 1) {
//                        locationtype = "G";
//                    } else if (location.getType() == 2) {
//                        locationtype = "W";
//                    } else if (location.getType() == 3) {
//                        locationtype = "H";
//                    } else if (location.getType() == 4) {
//                        locationtype = "C";
//                    } else if (location.getType() == 5) {
//                        locationtype = "O";
//                    } else if (location.getType() == 50) {
//                        locationtype = "GO";
//                    }
//                    dimen.append(getString(R.string.accuracy_of_positioning) + locationtype +
//                            accuracy + getString(R.string.mi) + " | ");
//                }
//
//
//                if (mWatchTask.get(eid) != null) {
//                    mWatchTask.get(eid).cancel(true);
//                }
//                if (show_type_radius.equalsIgnoreCase("true") && location.getUse_time() != 0) {
//                    dimen.append(getString(R.string.text13, String.valueOf(location.getUse_time()
//                    )));
//                    mLocOtherText.setText(dimen.toString());
//                } else {
//                    if (focusWatch.getCurLocation().getTimestamp() != null) {
//                        dimen.append(getString(R.string.steps_update_time, ""));
//                        String timestamp = focusWatch.getCurLocation().getTimestamp();
//                        final String localTimeStamp = TimeUtil.chnToLocalTimestamp(timestamp);
//                        if ((TimeUtil.getChatTime(localTimeStamp) >= 60) || (TimeUtil.getChatTime
//                                (localTimeStamp) < 0)) {
//                            dimen.append(TimeUtil.getLocationTime(localTimeStamp));
//                            mLocOtherText.setText(dimen.toString());
//                        } else {
//                            MioAsyncTask<String, Long, String> task = new MioAsyncTask<String,
//                                    Long, String>() {
//                                protected String doInBackground(String... params) {
//                                    String timestamp = params[0];
//                                    long time = TimeUtil.getChatTime(timestamp);
//                                    try {
//                                        while (time < 60) {
//                                            publishProgress(time);
//                                            Thread.sleep(500);
//                                            time = TimeUtil.getChatTime(timestamp);
//                                        }
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Thread.currentThread().interrupt();
//                                    }
//                                    return timestamp;
//                                }
//
//                                protected void onCancelled() {
//                                    super.onCancelled();
//                                }
//
//                                protected void onPostExecute(String message) {
//                                    super.onPostExecute(message);
//                                    dimen.append(TimeUtil.getLocationTime(message));
//                                    mLocOtherText.setText(dimen.toString());
//                                }
//
//                                @SuppressLint("SetTextI18n")
//                                protected void onProgressUpdate(Long... values) {
//                                    if (getActivity() != null) {
//                                        if (values[0] > 50) {
//                                            mLocOtherText.setText(dimen.toString() + getResources
//                                                    ().getString(R.string.text15));
//                                        } else if (values[0] > 0) {
//                                            mLocOtherText.setText(dimen.toString() + (values[0] /
//                                                    10 + 1) * 10 + getResources().getString(R.string
//                                                    .text16));
//                                        } else {
//                                            mLocOtherText.setText(dimen.toString() + TimeUtil
//                                                    .getLocationTime(localTimeStamp));
//                                        }
//                                    }
//                                }
//                            };
//                            task.execute(localTimeStamp);
//                            mWatchTask.put(eid, task);
//                        }
//                    }
//                }
//
//                //主动定位信息 type:0 主动定位信息； type：3，没有使用。
//                if (type == 0 || type == 3) {
//                    if (mWatchClickFlag.get(mApp.getCurUser().getFocusWatch().getEid()) != null
//                            && mWatchClickFlag.get(mApp.getCurUser().getFocusWatch().getEid())) {
//                        mWatchClickFlag.put(mApp.getCurUser().getFocusWatch().getEid(), false);
//                        watchLocationFinish();
//                    } else {
//                        if (isOnTraceTo) {
//                            watchLocationTrace();
//                        } else {
//                            watchLocationFinish();
//                        }
//                    }
//                } else if (mWatchClickFlag.get(mApp.getCurUser().getFocusWatch().getEid()) ==
//                        null || mWatchClickFlag.get(mApp.getCurUser().getFocusWatch().getEid())
//                        == false) {
//                    Log.d("hui", "定位完成 ->type=" + type);
//                    watchNormalAnim();
//
//                } else if (mWatchClickFlag.get(mApp.getCurUser().getFocusWatch().getEid())) {
//                    if (mApp.isInOfflineMode(mApp.getCurUser().getFocusWatch().getEid())) {
//                        watchNormalAnim();  //离线模式下不作定位动画
//                    } else {
//                        Log.d("hui", "定位动画 ->type=" + type);
//                        watchWalk(mApp.getCurUser().getFocusWatch().getEid());
//                    }
//                }
//
//
//            } else if (eid.equals(mApp.getCurUser().getFocusWatch().getEid())) {
//                mLocText.setText(R.string.null_location);
//                mLocOtherText.setText(R.string.null_location_other);
//                if (mLocationAnimTask != null) {
//                    mLocationAnimTask.cancel(true);
//                    mLocationAnimTask = null;
//                }
//                if (mAMap != null) {
//
//                }
//            } else if (mWatchClickFlag.get(eid) != null && mWatchClickFlag.get(eid)) {
//                mWatchClickFlag.put(eid, false);
//            }
//        }
//    }
//
//
//
//
//    private void watchWalk(String eid) {
//        if (mAMap == null || mApp.getCurUser().getFocusWatch().getCurLocation() == null) {
//            return;
//        }
//
//        if (!isAdded()) {
//            return;
//        }
//
////        if (mLocationAnimIsGoing) {
////            return;
////        }
//
//        try {
//            if (mLocationAnimTask != null) {
//                mLocationAnimTask.cancel(true);
//                mLocationAnimTask = null;
//                Log.d(TAG, "watchWalk: ");
//            }
//
//            try {
//                mLocationAnimTask = new MioAsyncTask<String, String, String>() {
//                    @Override
//                    protected String doInBackground(String... params) {
//                        String location_png_type = "location1";
//                        try {
//                            while (true) {
//                                if (location_png_type.equals("location1")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location2";
//                                } else if (location_png_type.equals("location2")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location3";
//                                } else if (location_png_type.equals("location3")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location4";
//                                } else if (location_png_type.equals("location4")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location5";
//                                } else if (location_png_type.equals("location5")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location6";
//                                } else if (location_png_type.equals("location6")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location7";
//                                } else if (location_png_type.equals("location7")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location8";
//                                } else if (location_png_type.equals("location8")) {
//                                    Thread.sleep(150);
//                                    location_png_type = "location1";
//                                }
//                                publishProgress(location_png_type);
//                            }
//                        } catch (Exception e) {
//                        }
//                        return null;
//                    }
//
//                    protected void onCancelled() {
//                        super.onCancelled();
//                    }
//
//                    protected void onProgressUpdate(String... values) {
//                        if (!isAdded()) {
//                            return;
//                        }
//                        if (getHost() != null) {
//                            Bitmap bmp;
//                            if (overlay != null) {
//                                if (imageView == null) {
//                                    imageView = (ImageView) overlay.getView();
//                                }
//                            }
//                            if (values[0].equals("location1")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location1);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location2")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location2);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location3")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location3);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location4")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location4);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location5")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location5);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location6")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location6);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location7")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location7);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            } else if (values[0].equals("location8")) {
//                                if (overlay != null) {
//                                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                            .location8);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            }
//
//                            if (mApp.getCurUser().getFocusWatch().getCurLocation() != null &&
//                                    overlay != null) {
//                                double latude = mApp.getCurUser().getFocusWatch().getCurLocation
//                                        ().getLatitude();
//                                double longitude = mApp.getCurUser().getFocusWatch()
//                                        .getCurLocation().getLongitude();
//                                GeoCoordinate geoCoordinate = new GeoCoordinate(latude, longitude);
//                                overlay.setCoordinate(geoCoordinate);
//
//                                mAMap.setCenter(geoCoordinate, Map.Animation.LINEAR);
//                            }
//                        }
//                    }
//
//                    protected void onPostExecute(String result) {
//
//                        Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                        it.putExtra("fouce_eid", result);
//                        mHostActivity.sendBroadcast(it);
//                    }
//                };
//                Log.d("hui", "定位动画开始执行");
//                mLocationAnimTask.execute(eid);
////                mLocationAnimIsGoing = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (NullPointerException e) {
//            Log.e("cui", "e:2----" + e.getMessage());
//        }
//    }
//
//    /**
//     * 类名称：MainHereActivity
//     * 创建人：zhangjun5
//     * 创建时间：2016/1/28 14:06
//     * 方法描述：跟踪模式下的动画效果
//     */
//    private void watchLocationTrace() {
//        if (mAMap == null) {
//            return;
//        }
//        if (mAMap != null && overlay != null) {
//            mAMap.removeMapOverlay(overlay);
//        }
//        setMarkerIcon(mApp.getCurUser().getFocusWatch().getEid());
//        Log.i("cui", "setMarkerIcon--------watchLocationTrace");
//        showCurUserLocation();
//        mLocationAnimTask = new MioAsyncTask<String, String, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                String location_png_type;
//                try {
//                    Thread.sleep(200);
//                    location_png_type = "vehicle";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle_floor";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle_floor";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle_floor";
//                    publishProgress(location_png_type);
//                    Thread.sleep(200);
//                    location_png_type = "vehicle";
//                    publishProgress(location_png_type);
//                } catch (Exception e) {
//                }
//                return params[0];
//            }
//
//            protected void onCancelled() {
//                super.onCancelled();
//            }
//
//            protected void onProgressUpdate(String... values) {
//                Bitmap bmp;
////                if (values[0].equals("vehicle")) {
////                    Bitmap tmp = getMaskBitmap(mApp.getCurUser().getFocusWatch().getEid());
////                    //marker.setIcon(BitmapDescriptorFactory.fromBitmap(tmp));
////                } else if (values[0].equals("vehicle_floor")) {
////                    bmp = BitmapFactory.decodeResource(getResources(), R.mipmap
////                            .small_coordinate_icon);
////                    // marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
////                }
//            }
//
//            protected void onPostExecute(String result) {
//                Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                it.putExtra("fouce_eid", result);
//                mHostActivity.sendBroadcast(it);
//            }
//        };
//        mLocationAnimTask.execute(mApp.getCurUser().getFocusWatch().getEid());
//    }
//
//
//    /**
//     * 类名称：MainHereActivity
//     * 创建人：zhangjun5
//     * 创建时间：2016/1/28 14:07
//     * 方法描述：主动定位后，返回信息后的动画效果。
//     */
//
//    private void watchLocationFinish() {
//        //Log.i("cui","watchLocationFinish");
//        if (mAMap == null || mApp.getCurUser().getFocusWatch().getCurLocation() == null) {
//            return;
//        }
//        showCurUserLocation();
//        if (mLocationAnimTask != null) {
//            mLocationAnimTask.cancel(true);
//            mLocationAnimTask = null;
//            Log.d(TAG, "watchLocationFinish: ");
//        }
//        try {
//            mLocationAnimTask = new MioAsyncTask<String, String, String>() {
//                @Override
//                protected String doInBackground(String... params) {
//                    String location_png_type = "coordinate_120";
//                    try {
//                        for (int i = 0; i < 10; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_110";
//
//                        for (int i = 0; i < 3; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_100";
//                        for (int i = 0; i < 3; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_90";
//                        for (int i = 0; i < 4; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_80";
//
//                        for (int i = 0; i < 3; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_90";
//                        for (int i = 0; i < 3; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_100";
//                        for (int i = 0; i < 3; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_110";
//
//                        for (int i = 0; i < 5; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_100";
//                        for (int i = 0; i < 5; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_90";
//
//                        for (int i = 0; i < 5; i++) {
//                            publishProgress(location_png_type);
//                            Thread.sleep(15);
//                        }
//                        location_png_type = "coordinate_100";
//                        publishProgress(location_png_type);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return params[0];
//                }
//
//                protected void onCancelled() {
//                    super.onCancelled();
//                }
//
//                protected void onProgressUpdate(String... values) {
//                    try {
//                        if (!isAdded()) {
//                            return;
//                        }
//                        Bitmap bmp;
//                        if (values[0].equals("coordinate_120")) {
//
//                            if (overlay != null) {
//                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                        .coordinate_120);
//                                ImageView imageView = (ImageView) overlay.getView();
//                                imageView.setImageBitmap(bmp);
//
//                            }
//
//                        } else if (values[0].equals("coordinate_110")) {
//                            if (overlay != null) {
//                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                        .coordinate_110);
//                                ImageView imageView = (ImageView) overlay.getView();
//                                imageView.setImageBitmap(bmp);
//
//                            }
//                        } else if (values[0].equals("coordinate_100")) {
//                            if (overlay != null) {
//                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                        .coordinate_100);
//                                ImageView imageView = (ImageView) overlay.getView();
//                                imageView.setImageBitmap(bmp);
//
//                            }
//                        } else if (values[0].equals("coordinate_90")) {
//                            if (overlay != null) {
//                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                        .coordinate_90);
//                                ImageView imageView = (ImageView) overlay.getView();
//                                imageView.setImageBitmap(bmp);
//
//                            }
//                        } else if (values[0].equals("coordinate_80")) {
//                            if (overlay != null) {
//                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable
//                                        .coordinate_80);
//                                ImageView imageView = (ImageView) overlay.getView();
//                                imageView.setImageBitmap(bmp);
//
//                            }
//                        }
//
//                        if (mApp.getCurUser().getFocusWatch().getCurLocation() != null && overlay
//                                != null) {
//                            double latude = mApp.getCurUser().getFocusWatch().getCurLocation()
//                                    .getLatitude();
//                            double longitude = mApp.getCurUser().getFocusWatch().getCurLocation()
//                                    .getLongitude();
//                            GeoCoordinate geoCoordinate = new GeoCoordinate(latude, longitude);
//                            overlay.setCoordinate(geoCoordinate);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                protected void onPostExecute(String result) {
//                    Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                    it.putExtra("fouce_eid", result);
//                    mHostActivity.sendBroadcast(it);
//                }
//            };
//            mLocationAnimTask.execute(focusWatch.getEid());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 类名称：MainHereActivity
//     * 创建人：zhangjun5
//     * 创建时间：2016/5/23 19:43
//     * 方法描述：显示设用户的位置信息
//     */
//    private void showCurUserLocation() {
//        if (mAMap == null) {
//            return;
//        }
//        curGolbleCount++;
//        if (curGolbleCount <= 4) {
//            try {
//                if (mApp.getCurUser().getFocusWatch().getCurLocation() != null && 0 ==
//                        mMyLocationIsVisibale) {
//                    mAMap.setCenter(new GeoCoordinate(mApp.getCurUser().getFocusWatch()
//                            .getCurLocation().getLatitude(), mApp.getCurUser().getFocusWatch()
//                            .getCurLocation().getLongitude()), Map.Animation.NONE);
//                }
//            } catch (Exception e) {
//                mApp.sdcardLog("showCurUserLocation,error=" + e.getMessage());
//            }
//        }
//    }
//
//    private long getChatTime(String mDateS) {
//        long result = 0;
//        if (mDateS == null) {
//            return 0;
//        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        Date other = null;
//        try {
//            other = format.parse(mDateS);
//            Date today = new Date();
//            result = (today.getTime() - other.getTime()) / 1000;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    private String getLocationTime(String mDates) {
//        String result = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        Date other = null;
//        try {
//            other = format.parse(mDates);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Date today = new Date();
//        int temp = Integer.valueOf(sdf.format(today)) - Integer.valueOf(sdf.format(other));
//        if (!sdf1.format(other).equals(sdf1.format(today))) {
//            temp = 3;
//        }
//        switch (temp) {
//            case 0:
//                result = getHourAndMin(other); //今天
//                break;
//            case 3:
//                result = getYear(other);
//                break;
//            default:
//                result = getTime(other);
//                break;
//        }
//        return result;
//    }
//
//    public static String getHourAndMin(Date mDate) {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//        return format.format(mDate);
//    }
//
//    public static String getTime(Date mDate) {
//        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
//        return format.format(mDate);
//    }
//
//    public static String getYear(Date mDate) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        return format.format(mDate);
//    }
//
//    private class MainMapReceiver extends BroadcastReceiver {
//
//        // 注册监听
//        public void registerReceiver(Context context) {
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//            filter.addAction(Const.ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE);
//            filter.addAction(Const.ACTION_LOCATION_RESP);
//            filter.addAction(Const.ACTION_CHANGE_WATCH);
//            filter.addAction(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
//            filter.addAction(Const.ACTION_RECEIVE_GET_DEVICE_INFO);
//            filter.addAction(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE);
//            filter.addAction(Const.ACTION_LOCATION_ANIM_CHANGE);
//            filter.addAction(Const.ACTION_LOCATION_OK_ANIM_CHANGE);
//            filter.addAction(Const.ACTION_REFRESH_WATCH_MARKER);
//            filter.addAction(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW);
//            filter.addAction(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE);
//            filter.addAction(Const.ACTION_REFRESH_WATCH_TITLE);
//            filter.addAction(Const.ACTION_QUERY_ALL_GROUPS);
//            filter.addAction(Const.ACTION_RECEIVE_NEW_LOCATION_NOTIFY);
//            filter.addAction(Const.ACTION_RECEIVE_SILENCETIME_UPDATE);
//            filter.addAction(Const.ACTION_GET_OFFLINE_CHAT_MSG);
//            filter.addAction(Const.ACTION_DOWNLOAD_HEADIMG_OK);
//            filter.addAction(Const.ACTION_SELECT_TARCE_TO_MODE);
//            filter.addAction(Const.ACTION_PROCESSED_NOTIFY_OK);
//            filter.addAction(Const.ACTION_RECEIVE_NOTICE_MSG);
//            filter.addAction(Const.ACTION_BAND_NETSERVICE_IS_OK);
//            filter.addAction(Const.ACTION_BIND_RESULT_END);
//            filter.addAction(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
//            filter.addAction(Const.ACTION_WATCH_NAVI_START);
//            filter.addAction(Const.ACTION_WATCH_NAVI_CURRENT_POINT);
//            filter.addAction(Const.ACTION_WATCH_NAVI_END);
//            context.registerReceiver(this, filter);
//        }
//
//        // 关闭监听
//        public void unregisterReceiver(Context context) {
//            context.unregisterReceiver(this);
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String resp;
//            JSONObject respPl;
//            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                if (mApp.getmWatchEFence() != null) {
//                    getRestLocationByWifi();
//                }
//            } else if (intent.getAction().equals(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE))
//            {//device offline更新广播
//                Log.e("offlinestate", Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
//                String eid = focusWatch.getEid();
//                updateWatchCurrentState(eid);
//            } else if (intent.getAction().equals(Const.ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE))
//            {//信号量更新广播
//                String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
//                updateTitle(eid);
//            } else if (intent.getAction().equals(Const.ACTION_LOCATION_RESP)) { //手表第二次返回的定位信息
//                resp = intent.getStringExtra(Const.KEY_JSON_MSG);
//                if (resp != null) {
//                    respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
//                    if (respPl != null) {
//                        respPl.remove(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
//                        Integer state = mApp.getmWatchIsOn().get(focusWatch.getEid());
//                        if (state != null &&
//                                (state == Const.WATCH_STATE_POWER_OFF || state == Const
//                                        .WATCH_STATE_POWER_OFF_LOW_POWER)) {
//                            mWatchBatteryTime.put(focusWatch.getEid(), System.currentTimeMillis());
//                            String[] keys = new String[1];
//                            keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
//                            mHostActivity.mapMGet(focusWatch.getEid(), keys,
//                                    HereMapFragment.this);
//                        }
//                    }
//                }
//            } else if (intent.getAction().equals(Const.ACTION_LOCATION_RESP)) { //设备第二次返回的定位信息
//                resp = intent.getStringExtra(Const.KEY_JSON_MSG);
//                if (resp != null) {
//                    respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
//                    String recvSn = String.valueOf(CloudBridgeUtil.getCloudMsgSN((JSONObject)
//                            JSONValue.parse(resp)));
//                    if (recvSn.equals("0")) {
//                        recvSn = null;
//                    }
//                    if (respPl != null) {
//                        respPl.remove(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
//                        sendLocationToService(respPl, recvSn);
//                        Integer state = mApp.getmWatchIsOn().get(mApp.getCurUser().getFocusWatch
//                                ().getEid());
//                        if (state != null &&
//                                (state == Const.WATCH_STATE_POWER_OFF
//                                        || state == Const.WATCH_STATE_POWER_OFF_LOW_POWER)) {
//                            mWatchBatteryTime.put(mApp.getCurUser().getFocusWatch().getEid(),
//                                    System.currentTimeMillis());
//
//                            String[] keys = new String[1];
//                            keys[0] = CloudBridgeUtil.BATTERY_LEVEL;
//                            mApp.getNetService().sendMapMGetMsg(mApp.getCurUser().getFocusWatch()
//                                    .getEid(), keys, mHostActivity);
//                        }
//                    }
//                }
//            } else if (intent.getAction().equals(Const.ACTION_SELECT_TARCE_TO_MODE)) {  //实时追踪下的信息处理
//                resp = intent.getStringExtra(Const.KEY_JSON_MSG);
//                if (resp != null) {
//                    respPl = CloudBridgeUtil.getCloudMsgPL((JSONObject) JSONValue.parse(resp));
//                    if (respPl != null) {
//                        doTrackToModeInfo(respPl);
//                    }
//                }
//            } else if (intent.getAction().equals(Const.ACTION_CHANGE_WATCH) || intent.getAction()
//                    .equals(Const.ACTION_BIND_RESULT_END)) {
//                changeWatchRefresh();
//                if (!focusWatch.isDevice306_A03()) {
//                    mApp.checkNeedUpdateWatch(mHostActivity, focusWatch);
//                }
//                mHostActivity.updateSettingRedPoint();
//            } else if (intent.getAction().equals(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE)) {
//                String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
//                int newstate = intent.getIntExtra(Const.KEY_WATCH_STATE, Const
//                        .WATCH_STATE_POWER_ON);
//                String time = intent.getStringExtra(Const.KEY_WATCH_STATE_TIMESTAMP);
//                if (time != null) {
//                    long ret = TimeUtil.getMillisByTime(time);//Long.valueOf(time);
//                    if (mApp.gettimeOfRecentBattery() != null) {
//                        mApp.settimeOfRecentBattery(eid, ret);
//                    }
//                }
//                updateTitle(eid);
//                updateWatchCurrentState(eid);
//                if (newstate == Const.WATCH_STATE_POWER_OFF || newstate == Const
//                        .WATCH_STATE_POWER_ON_BY_CHECK) {
//                    String[] keys = new String[2];
//                    keys[0] = CloudBridgeUtil.DEVICE_POWER_ON_TIME;
//                    keys[1] = CloudBridgeUtil.KEY_NAME_WATCH_STATE;
//                    mHostActivity.mapMGet(eid, keys, HereMapFragment.this);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_RECEIVE_GET_DEVICE_INFO) || intent
//                    .getAction().equals(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE)) {
//                mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
//            } else if (intent.getAction().equals(Const.ACTION_LOCATION_ANIM_CHANGE)) {
////                mLocationAnimIsGoing = false;
//
//                String fouceeid = intent.getStringExtra("fouce_eid");
//                if (fouceeid.equals(focusWatch.getEid())) {
//                    setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_LOCATION_OK_ANIM_CHANGE)) {
//                String fouceeid = intent.getStringExtra("fouce_eid");
//                if (fouceeid.equals(focusWatch.getEid())) {
//                    setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 0);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_REFRESH_WATCH_MARKER)) {
//
//                String familyId = intent.getStringExtra("family_id");
//                if (familyId.equals(focusWatch.getFamilyId())) {
//                    setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW))
//            {//刷新title
//                mHostActivity.initWatchScroll(viewRoot, mApp.getWatchList().size());
//            } else if (intent.getAction().equals(Const.ACTION_CLOUD_BRIDGE_STATE_CHANGE)) {
//                updateTitleNetworkState();
//            } else if (intent.getAction().equals(Const.ACTION_REFRESH_WATCH_TITLE)) {
//                String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
//                updateTitle(eid);
//                updateWatchCurrentState(eid);
//            } else if (intent.getAction().equals(Const.ACTION_QUERY_ALL_GROUPS)) {
//                mApp.sdcardLog("MainActivity ACTION_QUERY_ALL_GROUPS");
//                if (mApp.getmBackHomeFlag() != 1) {
//                    return;
//                }
//                if (focusWatch != null && focusWatch.getCurLocation() == null && !mApp
//                        .isFlightModeTime() &&
//                        mHostActivity.getNetservice() != null && mHostActivity.getNetservice()
//                        .isCloudBridgeClientOk()) {
//                    mApp.sdcardLog("MainActivity getCurLocation");
//                    String[] eid = new String[1];
//                    eid[0] = focusWatch.getEid();
//                    mApp.sdcardLog("e2eGetWatchLocation 4");
//                    e2eGetWatchLocation(eid);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_RECEIVE_NEW_LOCATION_NOTIFY)) {
//                resp = intent.getStringExtra(Const.KEY_JSON_MSG);
//                JSONObject respObj = (JSONObject) JSONValue.parse(resp);
//                int rc = CloudBridgeUtil.getCloudMsgRC(respObj);
//                JSONObject pl = (JSONObject) respObj.get(CloudBridgeUtil.KEY_NAME_PL);
//                mApp.getmWatchBackhomeLocationFlag().put((String) pl.get(CloudBridgeUtil
//                        .KEY_NAME_EID), false);
//                mApp.sdcardLog("huangqilin 20 Backhome eid:" + pl.get(CloudBridgeUtil
//                        .KEY_NAME_EID) + " flag:" + mApp.getmWatchBackhomeLocationFlag().get(pl.get
//                        (CloudBridgeUtil.KEY_NAME_EID)));
//                String watchEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
//                if (mWatchClickTask.get(watchEid) != null) {
//                    mWatchClickTask.get(watchEid).cancel(true);
//                }
//                if (1 == rc) {
//                    int sostype = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SOS);
//                    if (1 == sostype) {
//                        mWatchClickFlag.put((String) pl.get(CloudBridgeUtil.KEY_NAME_EID), false);
//                    } else {
//                        mWatchClickFlag.put((String) pl.get(CloudBridgeUtil.KEY_NAME_EID), false);
//                        pl.put("sn", respObj.get("SN"));
//                        setWatchLocation(pl, 0);
//                    }
//                } else {
//                    mWatchClickFlag.put((String) pl.get(CloudBridgeUtil.KEY_NAME_EID), false);
//                    if (focusWatch.getCurLocation() != null) {
//                        setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                    }
//                    Intent it = new Intent(Const.ACTION_LOCATION_ANIM_CHANGE);
//                    it.putExtra("fouce_eid", (String) pl.get(CloudBridgeUtil.KEY_NAME_EID));
//                    mHostActivity.sendBroadcast(it);
//                    LogUtil.e("error rc = " + rc);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_RECEIVE_SILENCETIME_UPDATE)) {
//                updateWatchCurrentState(focusWatch.getEid());
//            } else if (intent.getAction().equals(Const.ACTION_GET_OFFLINE_CHAT_MSG)) {
//                if (mApp.getmBackHomeFlag() != 1)
//                    return;
//                if (mApp.getCurUser() == null || focusWatch == null)
//                    return;
//
//                retrieveTraceData(focusWatch.getEid(), TimeUtil.getReversedOrderTime(Const
//                        .FUTURE_TIME_KEY), 1);
//                String[] eids = new String[1];
//                eids[0] = focusWatch.getEid();
//                mApp.sdcardLog("e2eGetWatchLocation 5");
//                e2eGetWatchLocation(eids);
//                if (focusWatch.isDevice705()) {
//                    mHostActivity.getNetservice().getWatchNaviState(eids[0], null);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_DOWNLOAD_HEADIMG_OK)) {
//                ImageUtil.setMaskImage(mHead, R.drawable.head_1, mApp.getHeadDrawableByFile(mApp
//                                .getResources(),
//                        mApp.getCurUser().getHeadPathByEid(focusWatch.getEid()),
//                        focusWatch.getEid(), R.drawable.small_default_head));
//            } else if (intent.getAction().equals(Const.ACTION_PROCESSED_NOTIFY_OK) ||
//                    intent.getAction().equals(Const.ACTION_RECEIVE_NOTICE_MSG)) {
//                setSafeAreaChange(focusWatch.getEid());
//            } else if (intent.getAction().equals(Const.ACTION_BAND_NETSERVICE_IS_OK)) {
//                if (focusWatch != null) {
//                    startWatchLocation();
//                    updateTitleNetworkState();
//                }
//            } else if (intent.getAction().equals(Const.ACTION_WATCH_NAVI_CURRENT_POINT)) {
//                if (isNavigating) {
//                    String locationData = intent.getStringExtra("data");
//                    updateWatchNaviLocation(locationData);
//                } else {
//                    mHostActivity.getNetservice().getWatchNaviState(focusWatch.getEid(), null);
//                }
//            } else if (intent.getAction().equals(Const.ACTION_WATCH_NAVI_START)) {
//                String naviData = intent.getStringExtra("data");
//                if (!TextUtils.isEmpty(naviData)) {
//                    JSONObject pl = (JSONObject) JSONValue.parse(naviData);
//                    if (!pl.isEmpty()) {
//                        mHostActivity.getNetservice().sendE2EMsg(focusWatch.getEid(),
//                                CloudBridgeUtil.SUB_ACTION_WATCH_NAVI_NAVI_STATE, 30 * 1000, false,
//                                HereMapFragment.this);
//                        handleRoutePlan(pl);
//                    }
//                } else {
//                    isNavigating = false;
//                }
//            } else if (intent.getAction().equals(Const.ACTION_WATCH_NAVI_END)) {
//                String eid = intent.getStringExtra(Const.KEY_WATCH_ID);
//                String key = intent.getStringExtra(CloudBridgeUtil.KEY_NAME_KEY);
//                endNavigation(eid, key);
//            }
//        }
//    }
//
//    /**
//     * 请求获取设备地址, sn为null表示不需要做定位统计
//     */
//    private void sendLocationToService(JSONObject pl, String sn) {
//        MyMsgData Msg = new MyMsgData();
//        Msg.setNeedNetTimeout(true);
//        Msg.setCallback(mHostActivity);
//        try {//增加保护
//            pl.put("GID", mApp.getCurUser().getFocusWatch().getFamilyId());
//            mApp.sdcardLog("sendLocationToService,pl=" + pl.toString());
//            Msg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_LOCATION, pl));
//            mApp.getNetService().sendNetMsg(Msg);
//            if (sn != null && mApp.getmAppToWatchLocationTime().get(sn) != null) {
//                mApp.locationStatistic((System.currentTimeMillis() - mApp
//                        .getmAppToWatchLocationTime().get(sn)) / 1000, (String) pl.get
//                        (CloudBridgeUtil
//                        .KEY_NAME_EID));
//                mApp.getmAppToWatchLocationTime().remove(sn);
//                long time = mLocationUseTime.get(sn);
//                mLocationUseTime.remove(sn);
//                mLocationUseTime.put(String.valueOf(Msg.getReqMsg().get("SN")), time);
//            }
//        } catch (Exception e) {
//            mApp.sdcardLog("sendLocationToService,error=" + e.getMessage());
//        }
//
//    }
//
//    // 参数：eid,gid,上报频率，打点类型，追踪结束时间，和组id
//    private void sendTraceToWatch(String eid, String gid, int time, int freq, int mode, int
//            traceStatus) {
//
//        MyMsgData msg = new MyMsgData();
//        msg.setCallback(HereMapFragment.this);
//        JSONObject pl = new JSONObject();
//        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
//        pl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
//        pl.put(CloudBridgeUtil.KEY_TRACE_TO_STATUS, traceStatus);
//        pl.put(CloudBridgeUtil.KEY_TRACE_TO_MODE, mode);
//        pl.put(CloudBridgeUtil.KEY_TRACE_TO_FREQ, freq);
//        pl.put(CloudBridgeUtil.KEY_TRACE_TO_TIME, time);
//        msg.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_TRACE_TO_SET, pl));
//        mApp.getNetService().sendNetMsg(msg);
//    }
//
//    private void startTraceTimer(long traceTime) {
//
//        stopTraceTimer();
//        timeCount = traceTime;
//        traceTimer = new Timer(1000, new Runnable() {
//            public void run() {
//                if (traceTimer != null && isAdded()) {
//                    long hour = timeCount / (60 * 60);
//                    long min = (timeCount / 60) % 60;
//                    long sec = timeCount % 60;
//                    String traceTimeInfo;
//                    if (hour >= 10) {
//                        traceTimeInfo = getString(R.string.text17) + hour + ":" + (min >= 10 ?
//                                min : "0" + min) + ":" + (sec >= 10 ? sec : "0" + sec);
//                    } else if (hour >= 1) {
//                        traceTimeInfo = getString(R.string.text17) + "0" + hour + ":" + (min >=
//                                10 ? min : "0" + min) + ":" + (sec >= 10 ? sec : "0" + sec);
//                    } else {
//                        traceTimeInfo = getString(R.string.text17) + (min >= 10 ? min : "0" +
//                                min) + ":" + (sec >= 10 ? sec : "0" + sec);
//                    }
//
//                    mWatchStateText.setText(traceTimeInfo);
//                    timeCount -= 1;
//                    if (timeCount <= 0) {
//                        //查询实时追踪的状态
//                        mHostActivity.getTraceStatue(focusWatch.getEid(), focusWatch.getFamilyId());
//                    } else {
//                        traceTimer.restart();
//                    }
//                }
//            }
//        });
//        traceTimer.start();
//    }
//
//    private void stopTraceTimer() {
//        if (traceTimer != null) {
//            LogUtil.i("time is stop!");
//            traceTimer.stop();
//            traceTimer = null;
//        }
//    }
//
//    /**
//     * 类名称：MainActivity
//     * 创建人：zhangjun5
//     * 创建时间：2016/4/6 20:05
//     * 方法描述： 处理实时追踪的开启和关闭操作
//     * 添加修改:1.根据gid来判断该信息是否来自于同一个家庭组内
//     * 2.区分不同类型的报文   type，0 ：代表查询报文  1：代表e2g通知
//     * 3:添加获取到状态时，定时器是否超时
//     */
//    private void doTrackToModeInfo(JSONObject pl) {
//
//        int value = (Integer) pl.get(CloudBridgeUtil.KEY_TRACE_TO_VALUE);
//        String watchEid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
//        String watchGid = (String) pl.get(CloudBridgeUtil.KEY_NAME_GID);
//        Integer state = mApp.getmWatchIsOn().get(watchEid);
//        if (state == null || state == Const.WATCH_STATE_POWER_OFF)
//            return;
//        if (watchGid != null && watchGid.equals(focusWatch.getFamilyId())) {
//            if (value > 0) {
//                String endtime = (String) pl.get(CloudBridgeUtil.KEY_TRACE_TO_END_TIME);
//                String timestamp = (String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
//                long diffSec = TimeUtil.compareToDiffForTwoTime(timestamp, endtime);
//                LogUtil.i("MainActivity diffsec:" + diffSec);
//                String localTime = TimeUtil.convertToLocalTime(diffSec);
//                LogUtil.i("LocalTime:" + localTime);
//                mApp.setValue(focusWatch.getEid() + Const.SHARE_PREF_TRACE_TO_LOCAL_ENDTIME,
//                        localTime);
//                updateWatchCurrentState(watchEid);
//            } else {
//                closeTraceMode(true);
//            }
//        } else if (watchGid != null) {
//            if (value > 0) {
//                String endtime = (String) pl.get(CloudBridgeUtil.KEY_TRACE_TO_END_TIME);
//                String timestamp = (String) pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
//                long diffSec = TimeUtil.compareToDiffForTwoTime(timestamp, endtime);
//                String localTime = TimeUtil.convertToLocalTime(diffSec);
//                LogUtil.i("other watch LocalTime:" + localTime);
//                mApp.setValue(watchEid + Const.SHARE_PREF_TRACE_TO_LOCAL_ENDTIME, localTime);
//            } else {
//                mApp.setValue(watchEid + Const.SHARE_PREF_TRACE_TO_LOCAL_ENDTIME, TimeUtil
//                        .getTimeStampLocal());
//            }
//        }
//    }
//
//    private void openTraceLocationDialog() {
//        String dialog_title;
//        if (isOnTraceTo) {
//            dialog_title = getText(R.string.trace_to_title_close).toString();
//            Dialog trace_dlg = DialogUtil.CustomNormalDialog(mHostActivity,
//                    dialog_title,
//                    getText(R.string.trace_to_close_msg).toString(),
//                    new DialogUtil.OnCustomDialogListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                        }
//                    },
//                    getText(R.string.cancel).toString(),
//                    new DialogUtil.OnCustomDialogListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            if (!isOnTraceTo) {
//                                return;
//                            }
//                            isOnTraceToIsActive = true;
//                            String geteid = focusWatch.getEid();
//                            String gid = focusWatch.getFamilyId();
//                            sendTraceToWatch(geteid, gid, 0, 0, 1, 0);
//                        }
//                    },
//                    getText(R.string.confirm).toString());
//            trace_dlg.show();
//        } else {
//            openCustomTraceTimeDialog();
//        }
//    }
//
//    private CustomerPickerView traceTimeHourView;
//    private CustomerPickerView traceTimeMinuteView;
//    String traceTimeHour = "00";//需要赋初值
//    String traceTimeMinute = "10";//需要赋初值
//
//    private void openCustomTraceTimeDialog() {
//
//        final Dialog dlg = new Dialog(mHostActivity, R.style.Theme_DataSheet);
//        LayoutInflater inflater = (LayoutInflater) mHostActivity.getSystemService(Context
//                .LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.xiaomi_dialog_tracetime, null);
//        traceTimeHourView = layout.findViewById(R.id.pick_tracetime_hour);
//        traceTimeHourView.setMarginAlphaValue((float) 3.8, "H");
//        traceTimeMinuteView = layout.findViewById(R.id.pick_tracetime_minute);
//        traceTimeMinuteView.setMarginAlphaValue((float) 3.8, "H");
//
//        TextView tvWarnInfo = layout.findViewById(R.id.warning_info);
//        int battery = focusWatch.getBattery();
//        if (battery <= 15) {
//            tvWarnInfo.setVisibility(View.VISIBLE);
//        }
//
//        Button left_btn = layout.findViewById(R.id.cancel_btn);
//        left_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlg.dismiss();
//            }
//        });
//        Button right_btn = layout.findViewById(R.id.confirm_btn);
//        right_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int hour = Integer.valueOf(traceTimeHour);
//                int minute = Integer.valueOf(traceTimeMinute);
//                mTrackToTime = hour * 60 + minute;
//                traceTimeHour = "00";
//                traceTimeMinute = "10";
//                isOnTraceToIsActive = true;
//                String eid = focusWatch.getEid();
//                String gid = focusWatch.getFamilyId();
//                sendTraceToWatch(eid, gid, mTrackToTime, 20, 1, 1);
//                mTraceLocation.setBackgroundResource(R.drawable.track_plus_1);
//                dlg.dismiss();
//            }
//        });
//
//        List<String> hours = new ArrayList<>();
//        final List<String> mins = new ArrayList<>();
//        for (int i = 0; i < 24; i++) {
//            hours.add(i < 10 ? "0" + i : "" + i);
//        }
//        for (int i = 0; i < 60; i++) {
//            mins.add(i < 10 ? "0" + i : "" + i);
//        }
//
//        traceTimeHourView.setData(hours);
//        traceTimeHourView.setSelected(0);
//        traceTimeHourView.setOnSelectListener(new CustomerPickerView.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                traceTimeHour = text;
//                if (traceTimeHour.equals("00") && traceTimeMinute.equals("00")) {
//                    traceTimeMinute = "01";
//                    traceTimeMinuteView.setSelected(31);
//                }
//            }
//        });
//
//        traceTimeMinuteView.setData(mins);
//        traceTimeMinuteView.setSelected(10);
//        traceTimeMinuteView.setOnSelectListener(new CustomerPickerView.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                traceTimeMinute = text;
//                if (traceTimeHour.equals("00") && traceTimeMinute.equals("00")) {
//                    traceTimeMinute = "01";
//                    traceTimeMinuteView.setSelected(31);
//                }
//            }
//        });
//
//        DisplayMetrics metric = new DisplayMetrics();
//        mHostActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;     // 屏幕宽度
//        TextView tvHour = layout.findViewById(R.id.start_hour_pv_hour);
//        //因为计算尺寸是按照1080宽度，所以这里计算padding值需要按比例求出
//        tvHour.setPadding((width - 17 * width / 1080) / 4 + 48 * width / 1080, 0, 0, 0);
//        tvHour.setTextColor(0xffdf5600);
//        TextView tvMinute = layout.findViewById(R.id.start_min_pv_min);
//        tvMinute.setPadding(width - (width - 17 * width / 1080) / 4 + 28 * width / 1080, 0, 0, 0);
//        tvMinute.setTextColor(0xffdf5600);
//
//        // set a large value put it in bottom
//        Window w = dlg.getWindow();
//        w.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = w.getAttributes();
//        lp.x = 0;
//        final int cMakeBottom = -1000;
//        lp.y = cMakeBottom;
//        lp.gravity = Gravity.BOTTOM;
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        dlg.onWindowAttributesChanged(lp);
//        dlg.setContentView(layout);
//        dlg.show();
//    }
//
//    private void closeTraceMode(boolean isCloseTrace) {
//        stopTraceTimer();
//        mTraceLocation.setBackgroundResource(R.drawable.btn_track_open_selector);
//        isOnTraceTo = false;
//        if (isCloseTrace) {
//            mApp.setValue(focusWatch.getEid() +
//                    Const.SHARE_PREF_TRACE_TO_LOCAL_ENDTIME, TimeUtil.getTimeStampLocal());
//            updateWatchCurrentState(focusWatch.getEid());
//        }
//    }
//
//    private void checkIsOnTrace() {
//
//        boolean hasTrace = true;  // TODO: 2019/8/9
//        if (hasTrace) {
//            mTraceLocation.setVisibility(View.VISIBLE);
//            // 计算手表有没有处于追踪模式
//            String traceEndtime = mApp.getStringValue(focusWatch.getEid() + Const
//                    .SHARE_PREF_TRACE_TO_LOCAL_ENDTIME, TimeUtil.getTimeStampLocal());
//            long diffSec = TimeUtil.compareToDiffForTwoTime(TimeUtil.getTimeStampLocal(),
//                    traceEndtime);
//            isOnTraceTo = diffSec > 0;
//            if (isOnTraceTo) {
//                mTraceLocation.setBackgroundResource(R.drawable.btn_track_close_selector);
//                startTraceTimer(diffSec);
//            } else {
//                mTraceLocation.setBackgroundResource(R.drawable.btn_track_open_selector);
//                stopTraceTimer();
//            }
//        } else {
//            isOnTraceTo = false;
//            mTraceLocation.setVisibility(View.INVISIBLE);
//            stopTraceTimer();
//        }
//    }
//
//    /**
//     * 原先是更新title，包括电量、信号、手表状态等，后在501中修改为仅更新电量（包括充电状态）、信号
//     * 何时调用？onResume、刷新手表、电量和信号发生变化
//     */
//    private void updateTitle(String eid) {
//
//        String focusEid = focusWatch.getEid();
//        if (focusEid != null && !focusEid.equals(eid)) {
//            return;
//        }
//        if (!isAdded()) {
//            return;
//        }
//        String signal = WatchStateUtils.spliteSignalStr(mApp.getStringValue(eid + CloudBridgeUtil
//                .SIGNAL_LEVEL, "0"));
//        if (signal == null) {
//            signal = "0";
//        }
//        int signalBigger;
//        if (focusWatch.isDevice102()) {
//            signalBigger = WatchStateUtils.getSignalStateBySixty(signal);
//        } else {
//            signalBigger = WatchStateUtils.getSignalStateByHundred(signal);
//        }
//        mApp.updatemWatchState(eid);
//        mApp.updatemWatchChargeState(eid);
//        mSignal.setBackgroundResource(WatchStateUtils.getSingalResId(mApp, signalBigger));
//        Bitmap mBatteryBitmap = WatchStateUtils.toBatteryBitmap(mHostActivity, focusWatch,
//                mApp.getmChargeState().get(focusWatch.getEid()), focusWatch.getBattery());
//        mBattery.setBackground(new BitmapDrawable(getResources(), mBatteryBitmap));
//        mBatteryText.setText(focusWatch.getBattery() + "%");
//    }
//
//    /**
//     * 显示关机、休眠、防打扰状态，优先级：关机、休眠、追踪、防打扰
//     * 何时调用？OnResume、电量信息发生变化时、开启或停止追踪、防打扰状态变化、刷新设备
//     */
//    private void updateWatchCurrentState(String eid) {
//
//        String focusEid = focusWatch.getEid();
//        if (focusEid != null && !focusEid.equals(eid)) {
//            return;
//        }
////        mApp.updatemWatchState(eid);
//// 涉及到电量变化时需要执行，但电量发生变化时必然执行updateTitle，也即必然触发updatemWatchState，所以这里不必调用了
//        closeTraceMode(false);    //先关闭掉追踪模式的相关显示
//        mBtnWakeUp.setVisibility(View.GONE);  //先隐掉唤醒按钮
//        Integer state = mApp.getmWatchIsOn().get(eid);
//
//        if (state != null && state == Const.WATCH_STATE_POWER_OFF) {
//            String string = getString(R.string.watch_poweroff_prompt) + "\n" + getString(R.string
//                    .low_battery_notice);
//            String powerOn = mApp.getStringValue(eid + CloudBridgeUtil.DEVICE_POWER_ON_TIME, "0");
//            if (powerOn != null && powerOn.length() > 0 && TimeUtil.compareToDiffForTwoTime
//                    (TimeUtil.getTimeStampLocal(), powerOn) > 0) {
//                string = getString(R.string.watch_poweroff_already) + TimeUtil.getDayTime(powerOn)
//                        + getString(R.string.watch_boot) + "\n" + getString(R.string
//                        .low_battery_notice);
//            }
//            mWatchStateText.setText(string);
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg);
//            if (isOnTraceTo) {
//                stopTraceTimer();
//                mTraceLocation.setBackgroundResource(R.drawable.btn_track_open_selector);
//                isOnTraceTo = false;
//            }
//            return;
//        }
//
//        if (/*state != null && state == Const.WATCH_STATE_FLIGHT && */mApp.isFlightModeTime()) {
//            mWatchStateText.setText(getText(R.string.watch_state_flight_already));
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg);
//            return;
//        }
//
//        // 深度上课防打扰中
//        if (mApp.isInSilenceTime(focusWatch.getEid()) == 3) {
//            mWatchStateText.setText(getText(R.string.watch_state_silence_advanceopt));
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg_single);
//            return;
//        }
//        // 移动数据
//        boolean isHaveOffline = (focusWatch.isDevice710() && !focusWatch.isDevice730() && mApp
//                .isControledByVersion(focusWatch, false, "T31"))
//                || (focusWatch.isDevice730() && mApp.isControledByVersion(focusWatch, false,
//                "T34"));
//        if (isHaveOffline) {
//            int offlineMode = mApp.getIntValue(focusWatch.getEid() + CloudBridgeUtil
//                    .OFFLINE_MODE_VALUE, Const.DEFAULT_OFFLINEMODE_VALUE);
//            if (offlineMode == 2 || (offlineMode == 1 && focusWatch.getBattery() <= Const
//                    .DEFAULT_OFFLINEMODE_BATTERY_LIMIT)) {
//                mWatchStateText.setText(getText(R.string.offline_mode_state));
//                mWatchStateText.setVisibility(View.VISIBLE);
//                layoutWatchState.setVisibility(View.VISIBLE);
//                layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg_single);
//                mBtnWakeUp.setVisibility(View.VISIBLE);
//                // 弹框提示
//                if (offlineMode == 1 && focusWatch.getBattery() <= Const
//                        .DEFAULT_OFFLINEMODE_BATTERY_LIMIT) {
//                    if (mApp.getIntValue(Const.SHARE_PREF_OFFLINEMODE_PROMPT, 0) == 0) {
//                        mApp.setValue(Const.SHARE_PREF_OFFLINEMODE_PROMPT, 1);
//                        Dialog promptDlg = DialogUtil.CustomNormalDialog(mHostActivity, getString
//                                        (R.string.offline_mode_state_batterylow), getString(R.string
//                                        .offline_mode_setting_desc),
//                                new DialogUtil.OnCustomDialogListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                }, getString(R.string.donothing_text));
//                        promptDlg.show();
//                    }
//                }
//                return;
//            }
//        }
//
//        //根据deviceOfflineState来判断手表是否在线
//        Integer offlineState = mApp.getmWatchOfflineState().get(eid);
//        Log.e("offlinestate", offlineState + ":" + eid);
//        if (null != offlineState && 1 == offlineState) {
//            mWatchStateText.setText(getString(R.string.watch_offline_state));
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg_single);
//            return;
//        }
//
//        // 计算手表有没有处于追踪模式
//        checkIsOnTrace();
//        if (isOnTraceTo) {
//            mWatchStateText.setText(getString(R.string.trace_to_running));
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg_single);
//            return;
//        }
//
//        if (mApp.isInSilenceTime(eid) > 0) {
//            mWatchStateText.setText(getText(R.string.watch_state_silence));
//            mWatchStateText.setVisibility(View.VISIBLE);
//            layoutWatchState.setVisibility(View.VISIBLE);
//            layoutWatchState.setBackgroundResource(R.drawable.watch_state_bg_single);
//            return;
//        }
//
//        mWatchStateText.setText("");
//        mWatchStateText.setVisibility(View.GONE);
//        layoutWatchState.setVisibility(View.GONE);
//    }
//
//    private void updateTitleNetworkState() {
//        if (mNetworkText == null || !isAdded()) return;
//        if (mHostActivity.getNetservice() != null && mHostActivity.getNetservice()
//                .isCloudBridgeClientOk()
//                && mHostActivity.getNetservice().isNetworkOK()) {
//            mNetworkLayout.setVisibility(View.GONE);
//            mNetworkText.setVisibility(View.GONE);
//        } else if (mHostActivity.getNetservice() != null && mHostActivity.getNetservice()
//                .isNetworkOK()) {
//            mNetworkLayout.setVisibility(View.GONE);
//            mNetworkText.setVisibility(View.VISIBLE);
//            if (mHostActivity.getNetservice().isPermissionDedied()) {
//                mNetworkText.setText(getResources().getText(R.string.permission_denied_toast));
//            } else {
//                mNetworkText.setText(getResources().getText(R.string.wifi_is_connecting));
//            }
//        } else {
//            mNetworkLayout.setVisibility(View.VISIBLE);
//            mNetworkText.setVisibility(View.GONE);
//        }
//    }
//
//    private boolean indoorViewIsVisiable(int locMap, int appMap) {
//        boolean ret = false;
//        if ((locMap == 0 && appMap == 1) || (locMap == 1 && appMap == 2)) {
//            ret = true;
//        }
//        return ret;
//    }
//}
