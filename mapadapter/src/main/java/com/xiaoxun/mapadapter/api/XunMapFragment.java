package com.xiaoxun.mapadapter.api;

import android.content.Context;

import androidx.fragment.app.Fragment;

public interface XunMapFragment {

    android.app.Fragment createMapFragment(Context context);

    Fragment createSupportMapFragment(Context context);

    void createMap(OnMapReadyCallback onMapReadyCallback);

    interface OnMapReadyCallback {
        void onMapReady(XunMap xunMap);
    }
}
