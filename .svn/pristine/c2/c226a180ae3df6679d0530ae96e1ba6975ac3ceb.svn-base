package com.xiaoxun.mapadapter;

import android.content.Context;

import com.xiaoxun.mapadapter.api.XunMapFragment;
import com.xiaoxun.mapadapter.api.XunMapUtils;
import com.xiaoxun.mapadapter.api.XunMapView;
import com.xiaoxun.mapadapter.googlemapimpl.GoogleMapFragmentImpl;
import com.xiaoxun.mapadapter.googlemapimpl.GoogleMapImpl;
import com.xiaoxun.mapadapter.googlemapimpl.GoogleMapUtilsImpl;
import com.xiaoxun.mapadapter.googlemapimpl.GoogleMapViewImpl;

public class XunMapManager {

    private static XunMapManager instance;

    private XunMapManager() {
    }

    public synchronized static XunMapManager getInstance() {

        if (instance == null) {
            instance = new XunMapManager();
        }
        return instance;
    }

    /**
     * 根据地图类型初始化地图
     * @param context  context信息，传入ApplicationContext
     * @param mapProvider  地图类型，默认AMap
     */
    public void initAppMapManager(Context context, MapConstant.MapProvider mapProvider){

        switch (mapProvider){
            case GOOGLEMAP:
                GoogleMapImpl.initAppMapManager(context);
                break;
            default:
                GoogleMapImpl.initAppMapManager(context);
                break;
        }
    }

    /**
     * 根据地图类型初始化地图
     * @param context  context信息，传入Activity context
     * @param sdcardDir 路径
     * @param mapProvider  地图类型，默认AMap
     */
    public void initActivityMapManager(Context context, String sdcardDir, MapConstant.MapProvider mapProvider){

        switch (mapProvider){
            case GOOGLEMAP:
                GoogleMapImpl.initActivityMapManager(context,sdcardDir);
                break;
            default:
                GoogleMapImpl.initActivityMapManager(context,sdcardDir);
                break;
        }
    }

    /**
     * 根据地图类型获取地图View控制器
     * @param mapProvider  地图类型，默认AMap
     * @return 地图View控制器
     */
    public XunMapView getMXMapView(MapConstant.MapProvider mapProvider) {

        XunMapView mXunMapView;
        switch (mapProvider) {
            case GOOGLEMAP:
                mXunMapView = new GoogleMapViewImpl();
                break;
            default:
                mXunMapView = new GoogleMapViewImpl();
                break;
        }
        return mXunMapView;
    }

    /**
     * 根据地图类型获取地图Fragment管理器
     * @param mapProvider  地图类型，默认AMap
     * @return 地图View控制器
     */
    public XunMapFragment getMXMapFragment(MapConstant.MapProvider mapProvider) {

        XunMapFragment mMXMapView;
        switch (mapProvider) {
            case GOOGLEMAP:
                mMXMapView = new GoogleMapFragmentImpl();
                break;
            default:
                mMXMapView = new GoogleMapFragmentImpl();
                break;
        }
        return mMXMapView;
    }

    /**
     * 根据地图类型获取地图工具类
     * @param mapProvider  地图类型，默认AMap
     * @return 地图工具类
     */
    public XunMapUtils getMXMapUtils(MapConstant.MapProvider mapProvider) {

        XunMapUtils mXunMapUtils;
        switch (mapProvider) {
            case GOOGLEMAP:
                mXunMapUtils = new GoogleMapUtilsImpl();
                break;
            default:
                mXunMapUtils = new GoogleMapUtilsImpl();
                break;
        }
        return mXunMapUtils;
    }
}
