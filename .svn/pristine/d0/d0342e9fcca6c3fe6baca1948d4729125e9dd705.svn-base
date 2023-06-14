package com.xiaoxun.mapadapter.googlemapimpl;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.xiaoxun.mapadapter.api.XunMapUtils;
import com.xiaoxun.mapadapter.bean.XunLatLng;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapUtilsImpl implements XunMapUtils {

    @Override
    public float calculateArea(XunLatLng leftTopLatlng, XunLatLng rightBottomLatlng) {

        // TODO: 2019/10/16  
        return 0;
    }

    @Override
    public float calculateArea(List<XunLatLng> xunPoints) {
        
        List<LatLng> points = new ArrayList<>();
        for (XunLatLng mxPoint : xunPoints) {
            points.add(new LatLng(mxPoint.latitudeWGS84, mxPoint.longitudeWGS84));
        }
        return (float) SphericalUtil.computeArea(points);
    }

    @Override
    public float calculateLineDistance(XunLatLng startXunLatlng, XunLatLng endXunLatlng) {

        LatLng startLatlng=new LatLng(startXunLatlng.latitudeWGS84,startXunLatlng.longitudeWGS84);
        LatLng endLatlng=new LatLng(endXunLatlng.latitudeWGS84,endXunLatlng.longitudeWGS84);
        return (float) SphericalUtil.computeDistanceBetween(startLatlng,endLatlng);
    }
}
