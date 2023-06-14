package com.xiaoxun.mapadapter.googlemapimpl;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xiaoxun.mapadapter.MapConstant;
import com.xiaoxun.mapadapter.api.XunMap;
import com.xiaoxun.mapadapter.bean.XunLatLng;
import com.xiaoxun.mapadapter.bean.XunMarker;
import com.xiaoxun.mapadapter.bean.XunPolyline;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapImpl implements XunMap {

    private GoogleMap mGoogleMap;

    /**
     * Application onCreate调用
     *
     * @param context 传入ApplicationContext
     */
    public static void initAppMapManager(Context context) {

    }

    /**
     * Application onCreate调用
     *
     * @param context   传入ApplicationContext
     * @param sdcardDir sdcard路径
     */
    public static void initAppMapManager(Context context, String sdcardDir) {

    }

    /**
     * Activity onCreate调用
     *
     * @param context 传入ApplicationContext
     */
    public static void initActivityMapManager(Context context, String sdcardDir) {

    }

    @Override
    public void createMap(View view, final XunMap.OnMapReadyCallback onMapReadyCallback) {

        MapView mapView = (MapView) view;
        mapView.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                onMapReadyCallback.onMapReady();
            }
        });
    }

    @Override
    public void createMap(Fragment fragment, final XunMap.OnMapReadyCallback onMapReadyCallback) {

        MapFragment mapFragment = (MapFragment) fragment;
        mapFragment.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                onMapReadyCallback.onMapReady();
            }
        });
    }

    @Override
    public void createMap(androidx.fragment.app.Fragment fragment, final XunMap.OnMapReadyCallback onMapReadyCallback) {

        SupportMapFragment supportMapFragment = (SupportMapFragment) fragment;
        supportMapFragment.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                onMapReadyCallback.onMapReady();
            }
        });
    }

    @Override
    public void uiSetting() {

        mGoogleMap.getUiSettings().setCompassEnabled(false);  //隐藏指南针
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);  //隐藏地图系统默认的放缩按钮
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);  //禁止通过手势旋转
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);  //禁止通过手势倾斜
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);  //隐藏层级选取器
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);  //隐藏地图工具栏
    }

    @Override
    public void uiSetting(Context context) {

    }

    @Override
    public void setLoadOfflineData(boolean enabled) {

    }

    @Override
    public void zoomIn(int duration) {

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn(), duration, null);
    }

    @Override
    public void zoomOut(int duration) {

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut(), duration, null);
    }

    @Override
    public void animateCamera(XunLatLng xunLatLng, float level, int duration) {

        LatLng latLng = new LatLng(xunLatLng.latitudeWGS84, xunLatLng.longitudeWGS84);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, level), duration, null);
    }

    @Override
    public void animateCamera(List<XunLatLng> xunLatLngList, int duration) {

        LatLngBounds.Builder boundsBuilder=new LatLngBounds.Builder();
        for(XunLatLng xunLatLng : xunLatLngList){
            boundsBuilder.include(new LatLng(xunLatLng.latitudeWGS84, xunLatLng.longitudeWGS84));
        }
        // 设置显示在规定屏幕范围内的地图经纬度范围
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100), duration, null);
    }

    @Override
    public String addMarker(XunMarker xunMarker) {

        LatLng latLng = new LatLng(xunMarker.xunLatLng.latitudeWGS84, xunMarker.xunLatLng.longitudeWGS84);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromBitmap(xunMarker.bitmap))
                .anchor((float) 0.5, (float) 0.65).zIndex(0).flat(true);
        Marker marker = mGoogleMap.addMarker(markerOptions);
        return marker.getId();
    }

    @Override
    public String addMarker(XunMarker xunMarker, boolean isAnimate) {

        return null;
    }

    @Override
    public String addMarker(Context context, XunMarker xunMarker, boolean is3d) {
        return null;
    }

    @Override
    public String addMarker(Context context, XunMarker xunMarker, boolean isAnimate, boolean is3d) {
        return null;
    }

    @Override
    public List<String> addMarkers(Context context, List<XunMarker> xunMarkerList, boolean is3d) {
        return null;
    }

    @Override
    public void updateMarker(Context context, XunMarker xunMarker, boolean is3d) {

    }

    @Override
    public void updateMarker(Context context, XunMarker xunMarker, boolean isAnimate, boolean is3d) {

    }

    @Override
    public List<String> addMarkers(List<XunMarker> xunMarkerList) {

        List<String> markerIdList = new ArrayList<>();
        for (XunMarker xunMarker : xunMarkerList) {
            markerIdList.add(addMarker(xunMarker));
        }
        return markerIdList;
    }

    @Override
    public void addPolyline(List<XunLatLng> xunLatLngList, int colorId, float width) {

        List<LatLng> latLngList = new ArrayList<>();
        for (XunLatLng xunLatLng : xunLatLngList) {
            latLngList.add(new LatLng(xunLatLng.latitudeWGS84, xunLatLng.longitudeWGS84));
        }
        mGoogleMap.addPolyline((new PolylineOptions()).addAll(latLngList).color(colorId).width(width));
    }

    @Override
    public void addPolyline(XunPolyline xunPolyline) {
        List<LatLng> latLngList = new ArrayList<>();
        for (XunLatLng xunLatLng : xunPolyline.xunLatLngList) {
            latLngList.add(new LatLng(xunLatLng.latitudeWGS84, xunLatLng.longitudeWGS84));
        }
        Polyline polyline = mGoogleMap.addPolyline((new PolylineOptions()).addAll(latLngList).color(xunPolyline.colorId).width(xunPolyline.width));
        xunPolyline.mapPolyline = polyline;
    }

    @Override
    public void removePolyline(XunPolyline xunPolyline) {
        if (xunPolyline != null && xunPolyline.mapPolyline != null) {
            Polyline polyline = (Polyline) xunPolyline.mapPolyline;
            polyline.remove();
            xunPolyline.mapPolyline = null;
            xunPolyline = null;
        }
    }

    @Override
    public void clear() {

        mGoogleMap.clear();
    }

    @Override
    public void setMapType(MapConstant.MapType mapType) {

        switch (mapType) {
            case MAP_TYPE_NORMAL:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case MAP_TYPE_SATELLITE:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    @Override
    public void setOnMarkerClickListener(final OnMarkerClickListener markerClickListener) {

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerClickListener.onMarkerClick(marker.getId());
                return true;
            }
        });
    }

    @Override
    public void setOnCameraChangeListener(final OnCameraChangeListener onCameraChangeListener) {

        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
                onCameraChangeListener.onCameraChange(cameraPosition.zoom, cameraPosition.target.latitude, cameraPosition.target.longitude);
            }
        });

        mGoogleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
                onCameraChangeListener.onCameraChange(cameraPosition.zoom, cameraPosition.target.latitude, cameraPosition.target.longitude);
            }
        });
    }
}
