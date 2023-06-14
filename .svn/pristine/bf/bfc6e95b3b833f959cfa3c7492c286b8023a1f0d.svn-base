package com.xiaoxun.xun.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Uesr：yaoyonghui on 2019/10/28 14:08
 * Email：yaoyonghui@loogcheer.com
 * Project: trunk_watch_app_foreign_vdf
 */

public class MyLocation {
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean passive_enabled = false;

    @SuppressLint("MissingPermission")
    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //是否可用
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            passive_enabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {
        }

        //如果gps和网络还有wifi不行就不开监听
        if (!gps_enabled && !network_enabled&& !passive_enabled)
            return false;


        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        if(passive_enabled)
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListenerpassive);
        if (timer1 == null){

            timer1=new Timer();
        }
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    //关闭定位
    public void cancelTimer() {
        timer1.cancel();
        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);
        lm.removeUpdates(locationListenerpassive);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {

            locationResult.gotLocation(location);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {

            locationResult.gotLocation(location);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerpassive = new LocationListener() {
        public void onLocationChanged(Location location) {

            locationResult.gotLocation(location);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            lm.removeUpdates(locationListenerpassive);

            Location net_loc=null, gps_loc=null,passive_loc=null;

            if(gps_enabled)
                gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(network_enabled)
                net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(passive_enabled)
                passive_loc=lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            //使用最近的一条
            if(gps_loc!=null && net_loc!=null&&passive_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime()&&gps_loc.getTime()>passive_loc.getTime()){
                    locationResult.gotLocation(gps_loc);
                } else if (net_loc.getTime()>gps_loc.getTime()&&net_loc.getTime()>passive_loc.getTime()){
                    locationResult.gotLocation(net_loc);
                }else {
                    locationResult.gotLocation(passive_loc);
                }
                return;
            }

            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            if(passive_loc!=null){
                locationResult.gotLocation(passive_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
