package com.xiaoxun.mapadapter.googlemapimpl;

import android.app.Fragment;
import android.content.Context;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.xiaoxun.mapadapter.api.XunMap;
import com.xiaoxun.mapadapter.api.XunMapFragment;

public class GoogleMapFragmentImpl implements XunMapFragment {

    private MapFragment mapFragment;
    private SupportMapFragment supportMapFragment;

    @Override
    public Fragment createMapFragment(Context context) {

        mapFragment = new MapFragment();
        return mapFragment;
    }

    @Override
    public androidx.fragment.app.Fragment createSupportMapFragment(Context context) {

        supportMapFragment = new SupportMapFragment();
        return supportMapFragment;
    }

    @Override
    public void createMap(final XunMapFragment.OnMapReadyCallback onMapReadyCallback) {

        final XunMap aMapManager = new GoogleMapImpl();
        if (supportMapFragment != null) {
            aMapManager.createMap(supportMapFragment, new XunMap.OnMapReadyCallback() {
                @Override
                public void onMapReady() {
                    onMapReadyCallback.onMapReady(aMapManager);
                }
            });
        } else if (mapFragment != null) {
            aMapManager.createMap(mapFragment, new XunMap.OnMapReadyCallback() {
                @Override
                public void onMapReady() {
                    onMapReadyCallback.onMapReady(aMapManager);
                }
            });
        }
    }
}
