package com.xiaoxun.mapadapter.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class XunMarker {

    private Context context;
    public XunMarker(Context context) {
        this.context = context;
    }

    /*------------------------------------ params start ----------------------------------*/

    public XunLatLng xunLatLng;
    public XunMarker setXunLatLng(XunLatLng xunLatLng){
        this.xunLatLng = xunLatLng;
        return this;
    }

    public String title;
    public XunMarker setTitle(String title){
        this.title=title;
        return this;
    }

    public Bitmap bitmap;
    public XunMarker setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
        return this;
    }
    public XunMarker setResId(int resId){
        this.bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(resId));
        return this;
    }

    public Bitmap[] bitmapArray;
    public XunMarker setBitmapArray(Bitmap[] bitmapArray){
        this.bitmapArray=bitmapArray;
        return this;
    }
    public XunMarker setResIdArray(int[] resIdArray) {
        this.bitmapArray = new Bitmap[resIdArray.length];
        for (int i = 0; i < resIdArray.length; i++) {
            this.bitmapArray[i] = BitmapFactory.decodeStream(context.getResources().openRawResource(resIdArray[i]));
        }
        return this;
    }

    public boolean isFlat;
    public XunMarker setIsFlat(boolean isFlat){
        this.isFlat=isFlat;
        return this;
    }

    public float zIndex;
    public XunMarker setzIndex(float zIndex){
        this.zIndex=zIndex;
        return this;
    }

    public int period;
    public XunMarker setPeriod(int period) {
        this.period = period;
        return this;
    }

    public float anchorU = 0.5F;
    public float anchorV = 1.0F;
    public XunMarker setAnchor(float anchorU, float anchorV) {
        this.anchorU = anchorU;
        this.anchorV = anchorV;
        return this;
    }

    public float rotateAngle;
    public XunMarker setRotateAngle(float rotateAngle) {
        this.rotateAngle = rotateAngle;
        return this;
    }

    /*------------------------------------ params end ----------------------------------*/

    public Object mapMarker;
    public String mapMarkerId;
    public Object mapCircleMarker;
}
