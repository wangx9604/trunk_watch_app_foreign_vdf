package com.xiaoxun.xun.motion.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xiaoxun.mapadapter.MapConstant;
import com.xiaoxun.mapadapter.XunMapManager;
import com.xiaoxun.mapadapter.api.XunMap;
import com.xiaoxun.mapadapter.api.XunMapFragment;
import com.xiaoxun.mapadapter.bean.XunLatLng;
import com.xiaoxun.mapadapter.bean.XunMarker;
import com.xiaoxun.mapadapter.bean.XunPolyline;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;

public class RecordMapFragment extends Fragment implements OnMapReadyCallback {
    private Context mContext;
    private ImibabyApp mApp;

    GoogleMap mMap;
    PolylineOptions mPlanRouteLine;

    public RecordMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        initData();
        initMap();
        return view;
    }

    private void initData() {
        mContext = getActivity();
        mApp = (ImibabyApp) ((Activity) mContext).getApplication();
    }

    private void initMap() {

    }

    public void setPolyLineInfo(PolylineOptions options) {
        mPlanRouteLine = options;
        if (mMap != null) {
            if (mPlanRouteLine != null) {
                mMap.addPolyline(mPlanRouteLine);

                int length = mPlanRouteLine.getPoints().size();
                LatLng startPoint = mPlanRouteLine.getPoints().get(0);
                LatLng endPoint = mPlanRouteLine.getPoints().get(length - 1);

                MarkerOptions mStartMarker = new MarkerOptions();
                mStartMarker.position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)).anchor(0.5f, 0.89f);
                mMap.addMarker(mStartMarker);


                MarkerOptions mEndMarker = new MarkerOptions();
                mEndMarker.position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)).anchor(0.5f, 0.89f);
                mMap.addMarker(mEndMarker);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < mPlanRouteLine.getPoints().size(); i++) {

                    builder.include(mPlanRouteLine.getPoints().get(i));
                }

                LatLngBounds bounds = builder.build();
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                mMap.animateCamera(update);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMap != null)
            mMap.clear();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LogUtil.e(" onMapReady");
        if (mMap != null) {
            if (mPlanRouteLine != null) {
                mMap.getUiSettings().setCompassEnabled(false);  //隐藏指南针
                mMap.getUiSettings().setZoomControlsEnabled(false);  //隐藏地图系统默认的放缩按钮
                mMap.getUiSettings().setRotateGesturesEnabled(false);  //禁止通过手势旋转
                mMap.getUiSettings().setTiltGesturesEnabled(false);  //禁止通过手势倾斜
                mMap.getUiSettings().setIndoorLevelPickerEnabled(false);  //隐藏层级选取器
                mMap.getUiSettings().setMapToolbarEnabled(false);  //隐藏地图工具栏
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.addPolyline(mPlanRouteLine);

                int length = mPlanRouteLine.getPoints().size();
                LatLng startPoint = mPlanRouteLine.getPoints().get(0);
                LatLng endPoint = mPlanRouteLine.getPoints().get(length - 1);

                MarkerOptions mStartMarker = new MarkerOptions();
                mStartMarker.position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)).anchor(0.5f, 0.89f);
                mMap.addMarker(mStartMarker);


                MarkerOptions mEndMarker = new MarkerOptions();
                mEndMarker.position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)).anchor(0.5f, 0.89f);
                mMap.addMarker(mEndMarker);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < mPlanRouteLine.getPoints().size(); i++) {
                    builder.include(mPlanRouteLine.getPoints().get(i));
                }

                LatLngBounds bounds = builder.build();
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 500,500,100);
                mMap.animateCamera(update);
            }
        }
    }
}