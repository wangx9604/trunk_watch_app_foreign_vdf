package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiaoxun.xun.R;

public class SZUtil {

    public static MarkerOptions createMarkerIconByKeyLat(Context ctxt, String sKey, LatLng lat) {
        MarkerOptions mMarker;
        if (sKey == null) {
            mMarker = new MarkerOptions().position(lat)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.yellow_point_0)));
        } else {
            if (sKey.equals("EFID1")) {
                mMarker = new MarkerOptions().position(lat)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.green_point_0)));
            } else if (sKey.equals("EFID2")) {
                mMarker = new MarkerOptions().position(lat)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.blue_point_0)));
            } else {
                mMarker = new MarkerOptions().position(lat)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.yellow_point_0)));
            }
        }

        return mMarker;
    }
}
