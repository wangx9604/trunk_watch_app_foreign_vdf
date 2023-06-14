package com.xiaoxun.mapadapter.api;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.xiaoxun.mapadapter.MapConstant;
import com.xiaoxun.mapadapter.bean.XunLatLng;
import com.xiaoxun.mapadapter.bean.XunMarker;
import com.xiaoxun.mapadapter.bean.XunPolyline;

import java.util.List;

public interface XunMap {

    void createMap(View view, XunMap.OnMapReadyCallback onMapReadyCallback);

    void createMap(android.app.Fragment fragment, XunMap.OnMapReadyCallback onMapReadyCallback);

    void createMap(Fragment fragment, OnMapReadyCallback onMapReadyCallback);

    void uiSetting();

    void uiSetting(Context context);

    void setLoadOfflineData(boolean enabled);

    void zoomIn(int duration);

    void zoomOut(int duration);

    void animateCamera(XunLatLng xunLatLng, float level, int duration);

    void animateCamera(List<XunLatLng> xunLatLngList, int duration);

    String addMarker(XunMarker xunMarker);

    String addMarker(XunMarker xunMarker, boolean isAnimate);

    String addMarker(Context context, XunMarker xunMarker, boolean is3d);

    String addMarker(Context context, XunMarker xunMarker, boolean isAnimate, boolean is3d);

    List<String> addMarkers(Context context, List<XunMarker> xunMarkerList, boolean is3d);

    void updateMarker(Context context, XunMarker xunMarker, boolean is3d);

    void updateMarker(Context context, XunMarker xunMarker, boolean isAnimate, boolean is3d);

    List<String> addMarkers(List<XunMarker> xunMarkerList);

    void addPolyline(List<XunLatLng> xunLatLngList, int colorId, float width);

    void addPolyline(XunPolyline xunPolyline);

    void removePolyline(XunPolyline xunPolyline);

    void clear();

    void setMapType(MapConstant.MapType mapType);

    void setOnMarkerClickListener(OnMarkerClickListener markerClickListener);

    void setOnCameraChangeListener(OnCameraChangeListener onCameraChangeListener);

    interface OnMapReadyCallback {
        void onMapReady();
    }

    interface OnMarkerClickListener {
        boolean onMarkerClick(String markerId);
    }

    interface OnCameraChangeListener {
        void onCameraChange(float zoom, double latitude, double longitude);

        void onCameraChangeFinish(float zoom, double latitude, double longitude);
    }
}
