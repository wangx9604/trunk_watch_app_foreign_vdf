package com.xiaoxun.xun.securityarea.service;

import com.google.android.gms.maps.model.LatLng;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SecurityService {
    private final ImibabyApp mApp;
    private static SecurityService mService;
    public static SecurityService getInstance(ImibabyApp mApp){
        synchronized (SecurityService.class){
            if(mService == null){
                mService = new SecurityService(mApp);
            }
        }
        return mService;
    }
    private SecurityService(ImibabyApp mApp){
        this.mApp = mApp;
    }

    //安全危险区域查询
    public void sendAreaGetMsg(String eid, MsgCallback callback) {
        MyMsgData data = new MyMsgData();
        data.setCallback(callback);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        data.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_AREA_GET, pl));
        mApp.getNetService().sendNetMsg(data);
    }
    //安全危险区域修改设置
    public void sendAreaSetMsg(String eid, String efid, String etype, String efname, List<LatLng> points, String desc, MsgCallback callback) {
        MyMsgData data = new MyMsgData();
        data.setCallback(callback);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_EFID_AREA, efid);
        pl.put(CloudBridgeUtil.KEY_AREA_TYPE, etype);
        pl.put(CloudBridgeUtil.KEY_AREA_NAME, efname);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String timeStr = format.format(date);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timeStr);
        String[] point = new String[points.size()];
        for (int i = 0; i < points.size(); i++) {
            String str = points.get(i).longitude + "," + points.get(i).latitude;
            point[i] = str;
        }
        pl.put(CloudBridgeUtil.KEY_AREA_POINTS, point);
        pl.put(CloudBridgeUtil.KEY_AREA_DESC, desc);
        data.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_AREA_SET, pl));
        mApp.getNetService().sendNetMsg(data);
    }
    //安全危险区域删除
    public void sendAreaDeleteMsg(String eid, String efid, MsgCallback callback) {
        MyMsgData data = new MyMsgData();
        data.setCallback(callback);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_EFID_AREA, efid);
        data.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_AREA_DELETE, pl));
        mApp.getNetService().sendNetMsg(data);
    }

    public void sendNormalAreaGetMsg(String eid,MsgCallback callback){
        MyMsgData data = new MyMsgData();
        data.setCallback(callback);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        data.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_GET,Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                mApp.getToken(), pl));
        mApp.getNetService().sendNetMsg(data);
    }
}
