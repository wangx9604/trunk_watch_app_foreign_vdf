package com.xiaoxun.xun.utils;

import android.content.Context;
import android.widget.ImageView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;

import net.minidev.json.JSONObject;

public class WatchWifiUtils {

    public interface OperationCallback {
        void onSuccess(Object result);

        void onFail(String error);
    }

    // e2e获取手表端扫码的wifi列表
    public static void reqWatchWifilistFromWatch(String eid, NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_DATA_LIST));
                } else {
                    operationCallback.onFail(Integer.toString(rc));
                }
            }
        };

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_DATA;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(eid, sn, pl, 60 * 1000, true, msgCallback);
    }

    // 获取云端存储的wifi列表
    public static void reqWatchWifilistFromServer(String eid, String sid, NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl.get(CloudBridgeUtil.KEY_NAME_LIST));
                } else {
                    operationCallback.onFail(Integer.toString(rc));
                }
            }
        };

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        JSONObject reqJson = new JSONObject();
        reqJson.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_GET_WIFI_LIST);
        reqJson.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        reqJson.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
        reqJson.put(CloudBridgeUtil.KEY_NAME_SID, sid);

        MyMsgData req = new MyMsgData();
        req.setReqMsg(reqJson);
        req.setCallback(msgCallback);
        req.setTimeout(30 * 1000);
        if (mNetService != null)
            mNetService.sendNetMsg(req);
    }

    public static void setFamilyWifi(final Context context, String eid, String ssid, String bssid, NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    operationCallback.onFail(context.getString(R.string.network_err));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    operationCallback.onFail(context.getString(R.string.set_timeout));
                } else {
                    operationCallback.onFail(context.getString(R.string.set_error));
                }
            }
        };
        mNetService.setFamilyWifi(eid, ssid, bssid, msgCallback);
    }

    public static void getFamilyWifi(final Context context, String eid, NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    operationCallback.onFail(context.getString(R.string.network_err));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    operationCallback.onFail(context.getString(R.string.set_timeout));
                } else {
                    operationCallback.onFail(context.getString(R.string.set_error));
                }
            }
        };
        if (mNetService == null) return;
        mNetService.getFamilyWifi(eid, msgCallback);
    }

    public static void showWifiStrength(ImageView imageView, int strength) {

        switch (strength) {
            case 2:
            case 3:
                imageView.setImageResource(R.drawable.watch_wifi_2);
                break;
            case 4:
            case 5:
                imageView.setImageResource(R.drawable.watch_wifi_3);
                break;
            default:
                imageView.setImageResource(R.drawable.watch_wifi_1);
                break;
        }
    }

    public static void mapgetWifiSettingState(final Context context, String eid, NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl);
                }
            }
        };

        String[] keys = new String[1];
        keys[0] = CloudBridgeUtil.KEY_KEEP_WIFI_CONNECT;
        if (mNetService != null)
            mNetService.sendMapMGetMsg(eid, keys, msgCallback);
    }

    public static void mapsetWifiSettingState(final Context context, String eid, String familyid, String field, String isOn,
                                              NetService mNetService, final OperationCallback operationCallback) {

        MsgCallback msgCallback = new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                JSONObject pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    operationCallback.onSuccess(pl);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    operationCallback.onFail(context.getString(R.string.network_err));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    operationCallback.onFail(context.getString(R.string.set_timeout));
                } else {
                    operationCallback.onFail(context.getString(R.string.set_error));
                }
            }
        };

        mNetService.sendMapSetMsg(eid, familyid, field, isOn, msgCallback);
    }
}
