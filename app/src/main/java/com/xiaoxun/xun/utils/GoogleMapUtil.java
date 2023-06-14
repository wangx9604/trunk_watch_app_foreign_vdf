package com.xiaoxun.xun.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * <pre>
 *     author : wangxin
 *     e-mail : wangxin7@longcheer.com
 *     time   : 2022/11/14
 * </pre>
 */
public class GoogleMapUtil {
    /**
     * <p>获取Google 地图上，2点之间的距离</p>
     *
     * @param start
     * @param end
     * @return
     */
    public static double getGoogleDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;
        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

        double R = 6371;//地球半径
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2)
                * Math.cos(lon2 - lon1))
                * R;
        return d * 1000;
    }
}
