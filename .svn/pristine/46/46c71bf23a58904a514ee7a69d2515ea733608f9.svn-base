package com.xiaoxun.mapadapter.bean;

import android.content.Context;

import java.util.List;

public class XunPolyline {

    private Context context;
    public XunPolyline(Context context) {
        this.context = context;
    }

    /*------------------------------------ params start ----------------------------------*/

    public List<XunLatLng> xunLatLngList;
    public XunPolyline setXunLatLngList(List<XunLatLng> xunLatLngList) {
        this.xunLatLngList = xunLatLngList;
        return this;
    }

    public int colorId;
    public XunPolyline setColorId(int colorId) {
        this.colorId = colorId;
        return this;
    }

    public float width;  //兼容高德、百度地图时，以高德为准
    public XunPolyline setWidth(float width) {
        this.width = width;
        return this;
    }

    /*------------------------------------ params end ----------------------------------*/

    public Object mapPolyline;
}
