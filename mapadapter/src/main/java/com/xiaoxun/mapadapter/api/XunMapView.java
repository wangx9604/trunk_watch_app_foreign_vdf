package com.xiaoxun.mapadapter.api;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface XunMapView {

    //static void initMapManager(Context context){}

    View createMapView(Context context);

    void createMap(OnMapReadyCallback onMapReadyCallback);

    void onCreate(Context context, Bundle bundle);

    void onResume();

    void onPause();

    void onDestroy();

    void onLowMemory();

    void onSaveInstanceState(Bundle bundle);

    interface OnMapReadyCallback {
        void onMapReady(XunMap xunMap);
    }
}
