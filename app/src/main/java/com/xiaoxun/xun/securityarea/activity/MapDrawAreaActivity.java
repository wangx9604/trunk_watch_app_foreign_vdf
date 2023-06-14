package com.xiaoxun.xun.securityarea.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.LocationData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.LocationDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.listener.SensorMarkerHelper;
import com.xiaoxun.xun.securityarea.bean.EfencesAreaBean;
import com.xiaoxun.xun.securityarea.bean.EventXY;
import com.xiaoxun.xun.securityarea.bean.LineMsg;
import com.xiaoxun.xun.securityarea.service.SecurityService;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomFileUtils;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LocationUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastFastUtils;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新加绘制区域：以手表定位为准居中显示
 * 修改绘制区域：显示以绘制区域所在矩形中心居中显示              地图显示的尺寸大小
 */
public class MapDrawAreaActivity extends NormalActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.SnapshotReadyCallback, LoadingDialog.OnConfirmClickListener {
    private ImibabyApp myApp;
    private ImageButton mBtnBack;
    private ImageView mIvDelete;
    private TextView mTitle;
    private TextView mTitleRest;
    private Button btSave;
    private String enterType;
    private String etype;
    private EfencesAreaBean mBean;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private int btnStatus = 0;//btn状态 0-->开始画出安全/危险区域 ; 1-->保存;

    private WatchData curWatch;//当前设置的watch
    //    private Api.Client mLocationClient;
//    private MyLocationListener mLocationListener = new MyLocationListener();
    private LatLng mLatLng;//初始化时加载的地理位置

    private ImageView ivAreaDraw;
    Canvas canvas;
    Paint paint;
    Bitmap bitmap;
    float downX, downY, upX, upY;//手指按下位置
    List<EventXY> pointList = new ArrayList<>();//绘制点集合
    private Boolean isReDraw = false;
    List<LineMsg> lineList = new ArrayList<>();//绘制的点采集样点组成线段集合
    private int count = 0;//交点数量
    private int focus1, focus2;//交点位置
    List<LatLng> mapLatLng = new ArrayList<>();//绘制区域地图坐标集合

    private LocationManager locationManager;
    private SensorMarkerHelper mSensorHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_draw_area);
        myApp = (ImibabyApp) getApplication();
//        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.schedule_no_class));
        enterType = getIntent().getStringExtra("drawType");
        if (getIntent().getSerializableExtra("efenceData") != null) {
            mBean = (EfencesAreaBean) getIntent().getSerializableExtra("efenceData");
        }
        if (enterType == null) {
            finish();
        }
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
        initViews();
        initData();
        initMapLocal(savedInstanceState);
        initListener();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap != null) {
            LogUtil.e("onMapReady failed");
//            ToastUtil.show(this,"地图初始化失败，请重试");
//            mGoogleMap.clear();
            return;
        }
        mGoogleMap = googleMap;

        if (mBean != null) {
            loadDefaultArea();
        } else if (curWatch.getCurLocation() != null && curWatch.getCurLocation().getLatLng() != null) {//加载手表定位
            mLatLng = curWatch.getCurLocation().getLatLng();
//            mGoogleMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11));
//            mGoogleMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mLatLng));
        } else {
            startPhoneLocal();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
        }
//        addWatchMaker();
    }

    private void initMapLocal(Bundle savedInstanceState) {
        curWatch = myApp.getCurUser().getFocusWatch();
        //加载地图初始状态
        //1.未设置区域-->加载手表位置
        //2.已设置区域-->latLng集合中心区域(手表->手机)
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private void loadDefaultArea() {
        String[] points = mBean.getPoints();
        String strLatLng1 = points[0];
        String[] latLngArray1 = strLatLng1.split(",");
        double lngMax = Double.parseDouble(latLngArray1[0]);
        double lngMin = Double.parseDouble(latLngArray1[0]);
        double latMax = Double.parseDouble(latLngArray1[1]);
        double latMin = Double.parseDouble(latLngArray1[1]);
        for (int i = 0; i < points.length; i++) {
            String strLatLng = points[i];
            String[] latLngArray = strLatLng.split(",");
            double lng = Double.parseDouble(latLngArray[0]);
            double lat = Double.parseDouble(latLngArray[1]);
            LatLng latLng = new LatLng(lat, lng);
            mapLatLng.add(latLng);
            if (lat > latMax) {
                latMax = lat;
            }
            if (lat < latMin) {
                latMin = lat;
            }
            if (lng > lngMax) {
                lngMax = lng;
            }
            if (lng < lngMin) {
                lngMin = lng;
            }
        }
        mLineOptions = new PolygonOptions().addAll(mapLatLng);
        LatLng llText = new LatLng((latMax + latMin) / 2, (lngMax + lngMin) / 2);
//        mGoogleMap.setMapStatus(MapStatusUpdateFactory.newLatLng(llText));
//        mTextOptions = new TextOptions().fontSize(24).position(llText);
        if (enterType.equals("dangerAdd") || enterType.equals("danger")) {
            mLineOptions.fillColor(Color.parseColor("#10FF0000"));
            mLineOptions.strokeWidth(5);
            mLineOptions.strokeColor(Color.parseColor("#FF0000"));
//            mTextOptions.text("危险区域").fontColor(Color.parseColor("#FF0000"));
        } else {
            mLineOptions.fillColor(Color.parseColor("#1000BC81"));
            mLineOptions.strokeWidth(5);
            mLineOptions.strokeColor(Color.parseColor("#00BC81"));
//            mTextOptions.text("安全区域").fontColor(Color.parseColor("#00BC81"));
        }
        mGoogleMap.addPolygon(mLineOptions);
//        mBaiduMap.addOverlay(mTextOptions);
        //将区域显示在中心
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < mapLatLng.size(); i++) {

                    builder.include(mapLatLng.get(i));
                }
                LatLngBounds bounds = builder.build();
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 250);
                mGoogleMap.animateCamera(update);
            }
        });
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // 当位置发生变化的时候会自动调用该方法，参数location记录了最新的位置信息。
            LogUtil.i("location lat:" + location.getLatitude() + ",lon:" + location.getLongitude());
            LocationUtils.handlePhoneLocation(myApp, new LatLng(location.getLatitude(), location.getLongitude()));
            setPhoneLastLocation(location);
//            if (curWatch.getCurLocation() != null) {
//                setWatchMarker(focusWatch.getCurLocation(), focusWatch.getEid(), 2);
//                if (mSensorHelper != null) {
//                    mSensorHelper.setCurrentGoogleMarker(mPhoneMarker);
//                }
//            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    void setPhoneLastLocation(Location location) {
        if (myApp.getCurUser().getLocation() == null) {
            myApp.getCurUser().setLocation(new LocationData());
        }
        if (location.hasAccuracy()) {
            myApp.getCurUser().getLocation().setAccuracy(location.getAccuracy());
        }
        myApp.getCurUser().getLocation().setLatitude(location.getLatitude());
        myApp.getCurUser().getLocation().setLongitude(location.getLongitude());
        myApp.getCurUser().getLocation().setMapType("2");
        myApp.getCurUser().getLocation().setWgs84Latlng(new LatLng(location.getLatitude(), location.getLongitude()));
        LogUtil.e("setPhoneLastLocation: setWgs84Latlng");
        LocationDAO.getInstance(myApp).updateLocation(myApp.getCurUser().getEid(), myApp.getCurUser().getLocation());
    }

    private void startPhoneLocal() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        stopPhoneLocation();
        /*
         * 参数1：定位模式 GPS_PROVIDER（GPS定位）、NETWORK_PROVIDER（网络定位）、PASSIVE_PROVIDER（）
         * 参数2：连续定位的间隔时间，单位是毫秒
         * 参数3：连续定位的间隔距离，单位是米
         * 参数4：定位结果回调
         */
        assert locationManager != null;
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (gpsProvider != null) {
            locationManager.requestLocationUpdates(gpsProvider.getName(), Const.PHONE_LOCATION_TIME,
                    Const.LOCATION_AGGREGATION_DISTANCE, locationListener);
        } else {
            LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            if (networkProvider != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Const.PHONE_LOCATION_TIME,
                        Const.LOCATION_AGGREGATION_DISTANCE, locationListener);
            }
        }

        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentGoogleMarker(null);
            mSensorHelper = null;
        }
        mSensorHelper = new SensorMarkerHelper(this);
        mSensorHelper.registerSensorListener();
    }

    // 停止定位
    private void stopPhoneLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null && locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentGoogleMarker(null);
            mSensorHelper = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    private void initViews() {
        mTitle = findViewById(R.id.tv_title);
        mTitleRest = findViewById(R.id.iv_title_reset);
        mBtnBack = findViewById(R.id.iv_title_back);
        mIvDelete = findViewById(R.id.iv_title_delete);
        btSave = findViewById(R.id.btn_save);
        ivAreaDraw = findViewById(R.id.iv_draw);
        mMapView = findViewById(R.id.cl_map);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
    }

    private void initData() {
        ViewTreeObserver vto = ivAreaDraw.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bitmap = Bitmap.createBitmap(ivAreaDraw.getWidth(), ivAreaDraw.getHeight(), Bitmap.Config.ARGB_8888);
                ivAreaDraw.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                canvas = new Canvas(bitmap);
                ivAreaDraw.setImageBitmap(bitmap);
            }
        });
        switch (enterType) {
            case "dangerAdd":
                etype = "danger";
                mTitle.setText(getString(R.string.draw_denger_area));
                btSave.setText(getString(R.string.start_draw_danger_area));
                mTitleRest.setVisibility(View.INVISIBLE);
                mIvDelete.setVisibility(View.INVISIBLE);
                btnStatus = 0;
                paint.setColor(Color.parseColor("#FF0000"));
                break;
            case "safeAdd":
                etype = "safe";
                mTitle.setText(getString(R.string.draw_safe_area));
                btSave.setText(getString(R.string.start_draw_safe_area));
                mTitleRest.setVisibility(View.INVISIBLE);
                mIvDelete.setVisibility(View.INVISIBLE);
                btnStatus = 0;
                paint.setColor(Color.parseColor("#00BC81"));
                break;
            case "danger":
                etype = "danger";
                mTitle.setText(getString(R.string.draw_denger_area));
                btSave.setText(getString(R.string.redraw_denger_area));
                mTitleRest.setVisibility(View.INVISIBLE);
                mIvDelete.setVisibility(View.VISIBLE);
                btnStatus = 0;
                paint.setColor(Color.parseColor("#FF0000"));
                break;
            case "safe":
                etype = "safe";
                mTitle.setText(getString(R.string.draw_safe_area));
                btSave.setText(getString(R.string.redraw_safe_area));
                mTitleRest.setVisibility(View.INVISIBLE);
                mIvDelete.setVisibility(View.VISIBLE);
                btnStatus = 0;
                paint.setColor(Color.parseColor("#00BC81"));
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mBtnBack.setOnClickListener(this);
        btSave.setOnClickListener(this);
        mTitleRest.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        ivAreaDraw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pointList.clear();
                        lineList.clear();
                        isReDraw = false;
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        count = 0;
                        downX = event.getX();
                        downY = event.getY();
                        EventXY eventXY = new EventXY();
                        eventXY.setX(downX);
                        eventXY.setY(downY);
                        pointList.add(eventXY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        EventXY eventXY1 = new EventXY();
                        upX = event.getX();
                        upY = event.getY();
                        canvas.drawLine(downX, downY, upX, upY, paint);
                        ivAreaDraw.invalidate();
                        //判断有效点，
                        if (Math.abs(pointList.get(pointList.size() - 1).getX() - downX) > 2 || Math.abs(pointList.get(pointList.size() - 1).getY() - downY) > 2) {
                            eventXY1.setX(upX);
                            eventXY1.setY(upY);
                            pointList.add(eventXY1);
                        }
                        downY = upY;
                        downX = upX;
                        break;
                    case MotionEvent.ACTION_UP:
                        //判断方法
                        doPointToLine();
                        if (!isReDraw) {
                            doCut();
                        }
                        break;
                }
                return true;
            }
        });
    }

    //判断裁切位置，去除多余的线段
    private void doCut() {
        if (count == 1) {
            //如果数量为1，则符合要求；将交点处前后清除并首尾相连
            Path path = new Path();
            for (int i = pointList.size() - 1; i > focus2; i--) {
                pointList.remove(i);
            }
            for (int i = focus1; i >= 0; i--) {
                pointList.remove(i);
            }
            path.moveTo(pointList.get(0).getX(), pointList.get(0).getY());
            for (int i = 1; i < pointList.size(); i++) {
                path.lineTo(pointList.get(i).getX(), pointList.get(i).getY());
            }
            path.close();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawPath(path, paint);
            ivAreaDraw.invalidate();
            drawAreaOnMap();
        } else if (count == 0) {
            pointList.add(pointList.get(0));
            doPointToLine();
            if (count <= 1) {//首尾相连后只有一个交点，也符合要求
                Path path = new Path();
                path.moveTo(pointList.get(0).getX(), pointList.get(0).getY());
                for (int i = 1; i < pointList.size(); i++) {
                    path.lineTo(pointList.get(i).getX(), pointList.get(i).getY());
                }
                path.close();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawPath(path, paint);
                ivAreaDraw.invalidate();
                drawAreaOnMap();
            } else {
                drawAreaFail();
                drawReset();
            }
        } else {
            drawAreaFail();
            drawReset();
        }

    }

    //绘制不符合要求，弹窗。
    private void drawAreaFail() {
        ToastFastUtils.FastSimpleDialogBuilder builder = new ToastFastUtils.FastSimpleDialogBuilder(this);
        builder.setGravity(Gravity.CENTER, 0, 0)
                .setText(getString(R.string.unrecognizable_area))
                .build();
    }

    private void drawReset() {
        btSave.setEnabled(true);
        pointList.clear();
        lineList.clear();
        count = 0;
        mapLatLng.clear();
        ivAreaDraw.setImageBitmap(null);
        ivAreaDraw.setVisibility(View.GONE);
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        isReDraw = false;
        initData();
    }

    PolylineOptions mTextOptions;
    PolygonOptions mLineOptions;

    private void drawAreaOnMap() {
        btSave.setEnabled(true);
        ivAreaDraw.setVisibility(View.GONE);
        float maxX = pointList.get(0).getX();
        float maxY = pointList.get(0).getY();
        float minX = pointList.get(0).getX();
        float minY = pointList.get(0).getY();
        //添加区域
        Point point = new Point();
        pointList.add(pointList.get(0));
        for (int i = 0; i < pointList.size() - 1; i++) {
            if (pointList.get(i).getX() > maxX) {
                maxX = pointList.get(i).getX();
            }
            if (pointList.get(i).getY() > maxY) {
                maxY = pointList.get(i).getY();
            }
            if (pointList.get(i).getX() < minX) {
                minX = pointList.get(i).getX();
            }
            if (pointList.get(i).getY() < minY) {
                minY = pointList.get(i).getY();
            }
            point.x = (int) pointList.get(i).getX();
            point.y = (int) pointList.get(i).getY();
            mapLatLng.add(mGoogleMap.getProjection().fromScreenLocation(point));
        }
        mLineOptions = new PolygonOptions().addAll(mapLatLng);

        //添加文字
        Point pointText = new Point();
        pointText.x = (int) ((maxX + minX) / 2);
        pointText.y = (int) ((minY + maxY) / 2);
        LatLng llText = mGoogleMap.getProjection().fromScreenLocation(pointText);
//        mTextOptions = new PolygonOptions().ss(24).position(llText);

        if (enterType.equals("dangerAdd") || enterType.equals("danger")) {
            mLineOptions.fillColor(Color.parseColor("#10FF0000"));
            mLineOptions.strokeWidth(5);
            mLineOptions.strokeColor(Color.parseColor("#FF0000"));
//            mTextOptions.text("危险区域").fontColor(Color.parseColor("#FF0000"));
        } else {
            mLineOptions.fillColor(Color.parseColor("#1000BC81"));
            mLineOptions.strokeWidth(5);
            mLineOptions.strokeColor(Color.parseColor("#00BC81"));
//            mTextOptions.text("安全区域").fontColor(Color.parseColor("#00BC81"));
        }
        mGoogleMap.addPolygon(mLineOptions);
//        mBaiduMap.addOverlay(mTextOptions);
    }

    //点转线段，判断交点数量--以此为根据-->判断绘制是否合理
    private void doPointToLine() {
        //pointList判断，长度判断
        if (pointList.size() <= 3) {
            isReDraw = true;
        } else {
            //pointList ---转化-->lineList
            for (int i = 0; i < pointList.size() - 1; i++) {
                LineMsg lineMsg = new LineMsg();
                lineMsg.setPoint1X(pointList.get(i).getX());
                lineMsg.setPoint1Y(pointList.get(i).getY());
                lineMsg.setPoint2X(pointList.get(i + 1).getX());
                lineMsg.setPoint2Y(pointList.get(i + 1).getY());
                lineMsg.setMinX(Math.min(pointList.get(i).getX(), pointList.get(i + 1).getX()));
                lineMsg.setMinY(Math.min(pointList.get(i).getY(), pointList.get(i + 1).getY()));
                lineMsg.setMaxX(Math.max(pointList.get(i).getX(), pointList.get(i + 1).getX()));
                lineMsg.setMaxY(Math.max(pointList.get(i).getY(), pointList.get(i + 1).getY()));
                lineList.add(lineMsg);
            }
            //0跟2345···比，1跟3456···比>>>>>>判断有无交叉点
            for (int i = 0; i < lineList.size() - 2; i++) {
                for (int j = i + 2; j <= lineList.size() - 1; j++) {
                    doHasFocus(lineList.get(i), lineList.get(j), i, j);
                }
            }
        }

    }

    //判断两条线段有无交点
    private void doHasFocus(LineMsg lineMsg1, LineMsg lineMsg2, int i, int j) {
        if (lineMsg1.getMinX() > lineMsg2.getMaxX() || lineMsg1.getMinY() > lineMsg2.getMaxY() || lineMsg1.getMaxX() < lineMsg2.getMinX() || lineMsg1.getMaxY() < lineMsg2.getMinY()) {
            //筛选出不可能相交的线段
        } else {
            //是否相交，，
            //设前一个line的两端点为AB，后一个端点为CD
            //AB代表向量AB ABx代表向量AB的x值   vectorABAC代表向量AB与AC的叉乘
            //AB。AC的叉乘与AB。AD的叉乘 相乘为负数代表CD在直线AB的两侧
            float ABx = lineMsg1.getPoint2X() - lineMsg1.getPoint1X();
            float ABy = lineMsg1.getPoint2Y() - lineMsg1.getPoint1Y();
            float ACx = lineMsg2.getPoint1X() - lineMsg1.getPoint1X();
            float ACy = lineMsg2.getPoint1Y() - lineMsg1.getPoint1Y();
            float ADx = lineMsg2.getPoint2X() - lineMsg1.getPoint1X();
            float ADy = lineMsg2.getPoint2Y() - lineMsg1.getPoint1Y();
            float vectorABAC = ABx * ACy - ABy * ACx;
            float vectorABAD = ABx * ADy - ABy * ADx;

            float CDx = lineMsg2.getPoint2X() - lineMsg2.getPoint1X();
            float CDy = lineMsg2.getPoint2Y() - lineMsg2.getPoint1Y();
            float CAx = lineMsg1.getPoint1X() - lineMsg2.getPoint1X();
            float CAy = lineMsg1.getPoint1Y() - lineMsg2.getPoint1Y();
            float CBx = lineMsg1.getPoint2X() - lineMsg2.getPoint1X();
            float CBy = lineMsg1.getPoint2Y() - lineMsg2.getPoint1Y();
            float vectorCDCA = CDx * CAy - CDy * CAx;
            float vectorCDCB = CDx * CBy - CDy * CBx;

            if (vectorABAC * vectorABAD < 0 && vectorCDCA * vectorCDCB < 0) {
                //有交点
                count++;
                focus1 = i;
                focus2 = j;
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnBack) {
            finish();
        } else if (v == btSave) {
            if (btnStatus == 0) {
                startDrawArea();
                btnStatus = 1;
                mIvDelete.setVisibility(View.INVISIBLE);
                mTitleRest.setVisibility(View.VISIBLE);

            } else if (btnStatus == 1) {
                //保存结果
                saveResult();
            }
        } else if (v == mTitleRest) {
            drawReset();
        } else if (v == mIvDelete) {
            //删除弹窗
            Dialog dlg = DialogUtil.CustomNormalDialog(MapDrawAreaActivity.this, getString(R.string.prompt), getString(R.string.security_delete),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, getString(R.string.cancel),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAreaToCloud();
                        }
                    }, getString(R.string.confirm));
            dlg.setCancelable(false);
            dlg.show();

        }
    }

    private void deleteAreaToCloud() {
        if (getMyApp().getNetService() != null) {
            SecurityService.getInstance(myApp).sendAreaDeleteMsg(curWatch.getEid(), mBean.getEfid(), new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (rc == 1) {
                        finish();
                    }
                }
            });
        }
    }


    private void startDrawArea() {
        drawReset();
        ivAreaDraw.setVisibility(View.VISIBLE);
        btSave.setText(getString(R.string.save_edit));
        btSave.setEnabled(false);
        if (enterType.equals("dangerAdd") || enterType.equals("danger")) {
            ivAreaDraw.setBackgroundResource(R.drawable.draw_area_danger_tip);
        } else {
            ivAreaDraw.setBackgroundResource(R.drawable.draw_area_safe_tip);
        }
    }

    //保存结果上传
    private void saveResult() {
        //调整大小，截图保存，上传退出。
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mapLatLng.size(); i++) {

            builder.include(mapLatLng.get(i));
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 250);
        mGoogleMap.animateCamera(update);
        if (loadingdlg != null && !loadingdlg.isShowing()) {
            loadingdlg.enableCancel(false);
            loadingdlg.changeStatus(1, getResources().getString(R.string.save_szone_message));
            loadingdlg.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.snapshot(MapDrawAreaActivity.this);
            }
        }, 1000);
    }

//    private void initBounds() {
//        LatLng mRightPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 0);
//        LatLng mLeftPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 180);
//        LatLng mTopPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 90);
//        LatLng mBottomPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 270);
//        LatLngBounds.Builder builder = LatLngBounds.builder();
//        builder.include(mRightPoint);
//        builder.include(mLeftPoint);
//        builder.include(mTopPoint);
//        builder.include(mBottomPoint);
//        builder.include(curWatch.getCurLocation().getLatLng());
//        LatLngBounds bounds = builder.build();
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//    }

    private LoadingDialog loadingdlg;

    @Override
    public void onSnapshotReady(Bitmap bitmap) {

        String efid = "";
        if (mBean != null) {
            efid = mBean.getEfid();
        } else {
            efid = String.valueOf(System.currentTimeMillis() / 1000);
        }

        String fileName = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + curWatch.getEid() + efid + ".jpg";

        String key = "EP/" + curWatch.getEid() + "/EFENCE/" + efid + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getMyApp().getNetService() != null) {
            SecurityService.getInstance(myApp).sendAreaSetMsg(curWatch.getEid(), efid, etype, "", mapLatLng, "", new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (rc == 1) {
                        sendPicData(key, fileName);
                    }
                }
            });
        }
    }

    private void sendPicData(String key, String fileName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    FileInputStream fileInputStream = new FileInputStream(fileName);
                    LogUtil.e("sendPicData  fileName = " + fileName);
                    int length = fileInputStream.available();
                    byte[] input = new byte[length];
                    fileInputStream.read(input);
                    fileInputStream.close();
                    if (CustomFileUtils.getInstance(myApp).uploadData(key, input)) {
                        AESUtil.encryptFile(new File(fileName));
                        finish();
                        if (loadingdlg != null && loadingdlg.isShowing()) {
                            loadingdlg.dismiss();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void confirmClick() {

    }

    public class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (mMapView == null) {
                return;
            }
            if (location != null) {
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                ToastUtil.showMyToast(MapDrawAreaActivity.this, getResources().getString(R.string.security_location_failed), 1500);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LocationListener.super.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != locationManager) {
            stopPhoneLocation();
        }
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }
    }


}