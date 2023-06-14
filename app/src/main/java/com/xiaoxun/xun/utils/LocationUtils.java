package com.xiaoxun.xun.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.android.gms.maps.model.LatLng;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.EFence;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;

import net.minidev.json.JSONObject;

import java.util.ArrayList;

public class LocationUtils {

    public static String getLocationtype(int locationType) {

        String locationtype = "";
        switch (locationType) {
            case 0:
                locationtype = "N";
                break;
            case 1:
                locationtype = "G";
                break;
            case 2:
                locationtype = "W";
                break;
            case 3:
                locationtype = "H";
                break;
            case 4:
                locationtype = "C";
                break;
            case 5:
                locationtype = "O";
                break;
            case 50:
                locationtype = "GO";
                break;
        }
        return locationtype;
    }

    public static void handlePhoneLocation(ImibabyApp mApp,LatLng loc) {

        ConnectivityManager conMag = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = conMag.getActiveNetworkInfo();
        WifiManager wifimag = (WifiManager) mApp.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wifimag.getConnectionInfo();
        if (activeInfo != null && (activeInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            String[] base = new String[1];
            base[0] = winfo.getBSSID();
            ArrayList<EFence> tempEfence;
            for (WatchData watch : mApp.getWatchList()) {
                tempEfence = mApp.getmWatchEFence().get(watch.getEid());
                if (tempEfence != null) {
                    for (EFence fence : tempEfence) {
                        if (fence.efid.equals("EFID1")) {
                            double distance = SphericalUtil.computeDistanceBetween(loc, new LatLng(fence.lat, fence.lng));
                            String importentkey = mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                            String tempkey = mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, Const.DEFAULT_NEXT_KEY);
                            String[] other = new String[10];
                            if (distance < fence.radius) {
                                if (importentkey.equals(Const.DEFAULT_NEXT_KEY) && tempkey.equals(Const.DEFAULT_NEXT_KEY)) {
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, base[0]);
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                } else if (importentkey.equals(base[0])) {
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                                } else if (tempkey.equals(base[0])) {
                                    //记录关键热点到服务器
                                    String time = mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, Const.DEFAULT_NEXT_KEY);
                                    if (!time.equals(Const.DEFAULT_NEXT_KEY) && (System.currentTimeMillis() - Long.valueOf(time)) > Const.PERMANENT_TIME) {
                                        mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, base[0]);
                                        mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                        SetPermanent(mApp,watch.getEid(), fence, base, other);
                                    }
                                } else {
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, base[0]);
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                }
                            } else if (importentkey.equals(base[0])) {
                                if (mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, base[0]);
                                    mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                } else {
                                    //判断关键热点是否失效
                                    String time1 = mApp.getStringValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, Const.DEFAULT_NEXT_KEY);
                                    if (!time1.equals(Const.DEFAULT_NEXT_KEY) && (System.currentTimeMillis() - Long.valueOf(time1)) > Const.PERMANENT_TIME) {
                                        mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                                        mApp.setValue(watch.getEid() + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                        DelPermanent(mApp,watch.getEid(),fence);
                                    }
                                }
                            }
                        }else{ //非家安全区域学习
                            double distance = SphericalUtil.computeDistanceBetween(loc, new LatLng(fence.lat, fence.lng));
                            String importentkey = mApp.getStringValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                            String tempkey = mApp.getStringValue(watch.getEid() +fence.efid+ Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, Const.DEFAULT_NEXT_KEY);
                            String[] other = new String[10];
                            if (distance < fence.radius) {
                                if (importentkey.equals(Const.DEFAULT_NEXT_KEY) && tempkey.equals(Const.DEFAULT_NEXT_KEY)) {
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, base[0]);
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                } else if (base[0].equals(importentkey)) {
                                    mApp.setValue(watch.getEid()+fence.efid  + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                                } else if (base[0].equals(tempkey)) {
                                    //记录关键热点到服务器
                                    String time = mApp.getStringValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, Const.DEFAULT_NEXT_KEY);
                                    if (!time.equals(Const.DEFAULT_NEXT_KEY) && (System.currentTimeMillis() - Long.valueOf(time)) > Const.PERMANENT_TIME) {
                                        mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, base[0]);
                                        mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                        SetPermanent(mApp,watch.getEid(), fence, base, other);
                                    }
                                } else {
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP, base[0]);
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                }
                            } else if (importentkey.equals(base[0])) {
                                if (mApp.getStringValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY).equals(Const.DEFAULT_NEXT_KEY)) {
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_UN_IMPORTENT_KEY, base[0]);
                                    mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                } else {
                                    //判断关键热点是否失效
                                    String time1 = mApp.getStringValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, Const.DEFAULT_NEXT_KEY);
                                    if (!time1.equals(Const.DEFAULT_NEXT_KEY) && (System.currentTimeMillis() - Long.valueOf(time1)) > Const.PERMANENT_TIME) {
                                        mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_IMPORTENT_KEY, Const.DEFAULT_NEXT_KEY);
                                        mApp.setValue(watch.getEid()+fence.efid + Const.SHARE_PREF_EFID1_FIND_TIME, String.valueOf(System.currentTimeMillis()));
                                        DelPermanent(mApp,watch.getEid(),fence);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void SetPermanent(final ImibabyApp mApp, String eid, EFence fence, String[] base, String[] other) {

        MyMsgData e2clist = new MyMsgData();
        e2clist.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                mApp.sdcardLog(reqMsg.toString());
                mApp.sdcardLog(respMsg.toString());
            }
        });
        JSONObject pl = new JSONObject();
        JSONObject per = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        //  per.put(CloudBridgeUtil.KEY_NAME_EFID, fence.efid);
        per.put(CloudBridgeUtil.KEY_NAME_LNG, fence.lng);
        per.put(CloudBridgeUtil.KEY_NAME_LAT, fence.lat);
        per.put("Base", base);
        per.put("Other", other);
        if(fence.efid.equals("EFID1")) { //家
            pl.put("PERMANENTID1", per);
        }else{
            pl.put("PERMANENTID"+fence.efid, per);
        }
        e2clist.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_SET_PERMANENT_REQ, pl));
        if(mApp.getNetService()!=null) {
            mApp.getNetService().sendNetMsg(e2clist);
        }
    }
    private static void DelPermanent(ImibabyApp mApp,String eid, EFence fence) {

        MyMsgData e2clist = new MyMsgData();
        String []per = new String[1];
        e2clist.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

            }
        });
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        if(fence.efid.equals("EFID1")) { //家
            per[0] = "PERMANENTID1";
            pl.put("PERMANENTID", per);
        }else{
            per[0] = "PERMANENTID" + fence.efid;
            pl.put("PERMANENTID", per);
        }
        e2clist.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEL_PERMANENT_REQ, pl));
        if(mApp.getNetService()!=null) {
            mApp.getNetService().sendNetMsg(e2clist);
        }
    }
}
