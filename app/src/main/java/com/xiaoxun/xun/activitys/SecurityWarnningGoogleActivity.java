package com.xiaoxun.xun.activitys;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.google.GPSUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LocationUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.SphericalUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guxiaolong on 2016/1/19.
 */
public class SecurityWarnningGoogleActivity extends BaseAppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback {

    private WatchData mCurWatch;
    private ImibabyApp mApp;
    private NoticeMsgData mNoticeMsg;
    private JSONObject mContent;
    private JSONObject mEfance;
    private JSONObject mLocation;

    private MarkerOptions mMarker = null;
    private CircleOptions circleOptions;

    private GoogleMap mGoogleMap = null;

    private TextView mWarnningTitle;
    private TextView mWarnningAdress;
    private TextView mWarnningTime;
    private ImageButton mBackBtn;

    private static final int MESSAGE_UPDATE = 0;
    private static final int MESSAGE_MAP_UPDATE = 1;

    private MessageHandler mHandler;

    private LatLng mWatchLatLng;
    private LatLng mSecurityZoneLatLng;
    private String mMapType;

    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_MAP_UPDATE:
                    displayMap();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_warnning);
        mApp = (ImibabyApp) getApplication();
        mNoticeMsg = getIntent().getParcelableExtra(NoticeMsgData.PARCE_KEY);
        if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_AREA) {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.notice_safe_area_title);
            mContent = (JSONObject) JSONValue.parse(mNoticeMsg.getmContent());
            mEfance = (JSONObject) mContent.get("EFence");
            mLocation = (JSONObject) mContent.get("Location");
        }else if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW) {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.notice_safe_area_title);
            mContent = (JSONObject) JSONValue.parse(mNoticeMsg.getmContent());
            mEfance = (JSONObject) mContent.get("EFence");
            mLocation = (JSONObject) mContent.get("Location");
        }  else if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SOS_LOCATION) {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.notice_sos_title);
            mContent = (JSONObject) JSONValue.parse(mNoticeMsg.getmContent());
            mLocation = (JSONObject) mContent.get("Location");
        } else {
            finish();
        }

        mHandler = new MessageHandler();
        mCurWatch = mApp.getCurUser().queryWatchDataByEid(mNoticeMsg.getmSrcid());

        setUpMap();
        initViews();
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap)).getMapAsync(this);
    }

    protected GoogleMap getMap() {
        return mGoogleMap;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap != null) {
            return;
        }
        mGoogleMap = googleMap;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_MAP_UPDATE), 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.zoomout:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn(), 250, null);
                break;
            case R.id.zoomin:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut(), 250, null);
                break;
            default:
                break;
        }
    }

    private void initViews() {
        ToolUtils.setWindowStatusColor(this);

        mBackBtn = findViewById(R.id.iv_title_back);
        mBackBtn.setOnClickListener(this);

        ImageButton zoomout = findViewById(R.id.zoomout);  //放大地图按钮
        zoomout.setOnClickListener(this);
        ImageButton zoomin = findViewById(R.id.zoomin);    //缩小地图按钮
        zoomin.setOnClickListener(this);

        mWarnningAdress = findViewById(R.id.tv_bottom_address);
        mWarnningTime = findViewById(R.id.tv_bottom_time);
        mWarnningTitle = findViewById(R.id.tv_bottom_title);
        if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_AREA) {
            if ((Integer) mEfance.get("Type") == 1) {
                mWarnningTitle.setText(getString(R.string.watch_already_arrive_safe_area, mApp.getCurUser().queryNicknameByEid(mNoticeMsg.getmSrcid()), mEfance.get("Name")));
            } else if ((Integer) mEfance.get("Type") == 2) {
                mWarnningTitle.setText(getString(R.string.watch_already_leave_safe_area, mApp.getCurUser().queryNicknameByEid(mNoticeMsg.getmSrcid()), mEfance.get("Name")));
            }
            mWarnningAdress.setText((String) mLocation.get("desc"));
            String locTimestamp = (String) mLocation.get("timestamp");
            if (locTimestamp == null) {
                mWarnningTime.setText(TimeUtil.getTime(SecurityWarnningGoogleActivity.this, TimeUtil.chnToLocalTimestamp(mNoticeMsg.getmTimeStamp())));
            } else {
                mWarnningTime.setText(TimeUtil.getTime(SecurityWarnningGoogleActivity.this, TimeUtil.chnToLocalTimestamp(locTimestamp)));
            }
        } else if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW) {
            mWarnningAdress.setText((String) mLocation.get("desc"));
            String locTimestamp = (String) mLocation.get("timestamp");
            if (locTimestamp == null) {
                mWarnningTime.setText(TimeUtil.chnToLocalTimestamp(mNoticeMsg.getmTimeStamp()));
            } else {
                mWarnningTime.setText(TimeUtil.chnToLocalTimestamp(locTimestamp));
            }
            String type = (String) mEfance.get("etype");
            String tip = "";
            if ("danger".equals(type)) {
                tip = "危险区域";
            } else if ("safe".equals(type)) {
                tip = "安全区域";
            } else if (!StrUtil.isNotBlank(type)) {
                tip = "城市";
            }
            if ("in".equals(mContent.get("event"))) {
                mWarnningTitle.setText(getString(R.string.in_area, mApp.getCurUser().queryNicknameByEid(mNoticeMsg.getmSrcid()), tip));
            } else if ("out".equals(mContent.get("event"))) {
                mWarnningTitle.setText(getString(R.string.out_area, mApp.getCurUser().queryNicknameByEid(mNoticeMsg.getmSrcid()), tip));
            }
        }else if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SOS_LOCATION) {
            mWarnningTitle.setText(getString(R.string.ask_for_help, mApp.getCurUser().queryNicknameByEid(mNoticeMsg.getmSrcid())));
            mWarnningAdress.setText((String) mLocation.get("desc"));
            mWarnningTime.setText(TimeUtil.getTime(SecurityWarnningGoogleActivity.this, TimeUtil.chnToLocalTimestamp(mNoticeMsg.getmTimeStamp())));
        }
    }

    private void displayMap() {
        initWatchLocation();
        if (mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_AREA) {
            initSecurityZone();
            initBounds();
        } else if(mNoticeMsg.getmType() == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW){
//            //绘制定位
            List<LatLng> mapLatLng = new ArrayList<>();//绘制区域地图坐标集合
            Type type = new TypeToken<String[]>() {}.getType();
            String[] points = new Gson().fromJson(String.valueOf(mEfance.get("points")), type);

            String[] defaultPoint = points[0].split(",");
            double lngMax = Double.parseDouble(defaultPoint[0]);
            double lngMin = Double.parseDouble(defaultPoint[0]);
            double latMax = Double.parseDouble(defaultPoint[1]);
            double latMin = Double.parseDouble(defaultPoint[1]);
            for (int i = 0; i < points.length; i++) {
                String str = points[i];
                String[] latLngArray = str.split(",");
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
            PolygonOptions mLineOptions = new PolygonOptions().addAll(mapLatLng);
            LatLng llText = new LatLng((latMax + latMin) / 2, (lngMax + lngMin) / 2);

//            MarkerOptions mTextOptions = new MarkerOptions().title().fontSize(24).position(llText);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(llText);
            if ("danger".equals(mEfance.get("etype"))) {
                mLineOptions.fillColor(Color.parseColor("#10FF0000"));
                mLineOptions.strokeColor(Color.parseColor("#FF0000"));
                mLineOptions.strokeWidth(9f);
                markerOptions.title("危险区域");
//                mTextOptions.text("危险区域").fontColor(Color.parseColor("#FF0000"));
            } else if("safe".equals(mEfance.get("etype"))){
                mLineOptions.fillColor(Color.parseColor("#1000BC81"));
                mLineOptions.strokeColor(Color.parseColor("#00BC81"));
                mLineOptions.strokeWidth(8f);
                markerOptions.title("安全区域");
//                mLineOptions.stroke(new Stroke(8, Color.parseColor("#00BC81")));
//                mTextOptions.text("安全区域").fontColor(Color.parseColor("#00BC81"));
            }
            mGoogleMap.addPolygon(mLineOptions);
//            mBaiduMap.addOverlay(mTextOptions);
        }else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mWatchLatLng, 16.0f));
        }
    }

    private void initWatchLocation() {
        String location = (String) mLocation.get("location");
        mMapType = (String) mLocation.get(CloudBridgeUtil.KEY_NAME_LOCATION_MAPTYPE);
        double latitude = Double.valueOf(location.substring(location.indexOf(",") + 1));
        double longitude = Double.valueOf(location.substring(0, location.indexOf(",")));
        int region = 460;  //默认460
            region = (Integer) mLocation.get(CloudBridgeUtil.KEY_NAME_REGION);
        LogUtil.e("initWatchLocation  region = "+mLocation.get(CloudBridgeUtil.KEY_NAME_REGION));
        if (region == 460 || region == 461 || region == 454 || region == 455) {  //大陆
            switch (mMapType) {
                case "0":
                case "2":
                    mWatchLatLng = new LatLng(latitude,longitude);
                    break;
                case "1":
                    mWatchLatLng = GPSUtil.double_to_latLng(GPSUtil.bd09_To_Gcj02(latitude, longitude));
                    break;
            }
        } else if (region == 466) {  //台湾
            switch (mMapType) {
                case "0":
                case "2":
                    mWatchLatLng = new LatLng(latitude, longitude);
                    break;
                case "1":
                    mWatchLatLng = GPSUtil.double_to_latLng(GPSUtil.bd09_To_gps84(latitude, longitude));
                    break;
            }

        } else {  //海外
            mWatchLatLng = new LatLng(latitude, longitude);
        }

        mMarker = new MarkerOptions().position(mWatchLatLng).anchor(0.5f, 0.95f).flat(true);
        mMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coordinate_0)));
        mGoogleMap.addMarker(mMarker);
    }

    private void initSecurityZone() {
        mSecurityZoneLatLng = new LatLng((Double) mEfance.get("Lat"), (Double) mEfance.get("Lng"));
        mMarker = new MarkerOptions().position(mSecurityZoneLatLng);
        if (mWarnningTitle.getText().toString().contains(getString(R.string.leave))) {
            mMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red_point_0)));
        } else {
            mMarker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.green_point_1)));
        }
        mMarker.anchor(0.5f, 0.5f);
        mGoogleMap.addMarker(mMarker);

        circleOptions = new CircleOptions();
        circleOptions.center(mSecurityZoneLatLng);
        circleOptions.radius(Double.valueOf((Integer) mEfance.get("Radius")));

        if (mWarnningTitle.getText().toString().contains(getString(R.string.leave))) {
            circleOptions.fillColor(getResources().getColor(R.color.color_red_10));
            circleOptions.strokeColor(getResources().getColor(R.color.color_red_50));
        } else {
            circleOptions.fillColor(0x1a12a7e5);
            circleOptions.strokeColor(0x8012a7e5);
        }
        circleOptions.strokeWidth(2);
        circleOptions.visible(true);
        circleOptions.zIndex(3);
        mGoogleMap.addCircle(circleOptions);

    }

    private void initBounds() {
        LatLng mRightPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 0);
        LatLng mLeftPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 180);
        LatLng mTopPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 90);
        LatLng mBottomPoint = SphericalUtil.computeOffset(mSecurityZoneLatLng, circleOptions.getRadius(), 270);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(mRightPoint);
        builder.include(mLeftPoint);
        builder.include(mTopPoint);
        builder.include(mBottomPoint);
        builder.include(mWatchLatLng);
        LatLngBounds bounds = builder.build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
