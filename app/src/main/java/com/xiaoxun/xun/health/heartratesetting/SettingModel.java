package com.xiaoxun.xun.health.heartratesetting;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class SettingModel extends ViewModel {
    private MutableLiveData<Boolean> switch_high_limit;
    private MutableLiveData<Integer> high_limit;
    private MutableLiveData<Boolean> swithc_rest_high_limit;
    private MutableLiveData<Integer> rest_high_limit;
    private MutableLiveData<Boolean> switch_rest_low_limit;
    private MutableLiveData<Integer> rest_low_limit;

    private int hl_status = 0;  //心率上限开关防抖 0 空闲 1 忙碌
    private int rhl_status = 0; //静态心率过高开关防抖 0 空闲 1 忙碌
    private int rll_status = 0; //静态心率过低开关防抖 0 空闲 1 忙碌

    public SettingModel(){
        switch_high_limit = new MutableLiveData<>();
        high_limit = new MutableLiveData<>();
        swithc_rest_high_limit = new MutableLiveData<>();
        rest_high_limit = new MutableLiveData<>();
        switch_rest_low_limit = new MutableLiveData<>();
        rest_low_limit = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getSwitch_high_limit() {
        return switch_high_limit;
    }

    public void setHigh_limit(int high_limit) {
        this.high_limit.postValue(high_limit);
    }

    public MutableLiveData<Integer> getHigh_limit() {
        return high_limit;
    }

    public MutableLiveData<Boolean> getSwithc_rest_high_limit() {
        return swithc_rest_high_limit;
    }

    public void setRest_high_limit(int rest_high_limit) {
        this.rest_high_limit.postValue(rest_high_limit);
    }

    public MutableLiveData<Integer> getRest_high_limit() {
        return rest_high_limit;
    }

    public MutableLiveData<Boolean> getSwitch_rest_low_limit() {
        return switch_rest_low_limit;
    }

    public void setRest_low_limit(int rest_low_limit) {
        this.rest_low_limit.postValue(rest_low_limit);
    }

    public MutableLiveData<Integer> getRest_low_limit() {
        return rest_low_limit;
    }

    public int getHl_status() {
        return hl_status;
    }

    public int getRhl_status() {
        return rhl_status;
    }

    public int getRll_status() {
        return rll_status;
    }

    public void mapGetData(ImibabyApp mApp,String seid){
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    JSONObject pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    String hlonoff = (String)pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_ONOFF);
                    String hlvalue = (String) pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_VALUE);
                    String rhlonoff = (String) pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_ONOFF);
                    String rhlvalue = (String) pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_VALUE);
                    String rllonoff = (String) pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_ONOFF);
                    String rllvalue = (String) pl.get(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_VALUE);

                    if(hlonoff != null && !hlonoff.equals("")) {
                        switch_high_limit.postValue(hlonoff.equals("1"));
                    }
                    if(hlvalue != null && !hlvalue.equals("")) {
                        high_limit.postValue(Integer.parseInt(hlvalue));
                    }
                    if(rhlonoff != null && !rhlonoff.equals("")){
                        swithc_rest_high_limit.postValue(rhlonoff.equals("1"));
                    }
                    if(rhlvalue != null && !rhlvalue.equals("")){
                        rest_high_limit.postValue(Integer.parseInt(rhlvalue));
                    }
                    if(rllonoff != null && !rllonoff.equals("")){
                        switch_rest_low_limit.postValue(rllonoff.equals("1"));
                    }
                    if(rllvalue != null && !rllvalue.equals("")){
                        rest_low_limit.postValue(Integer.parseInt(rllvalue));
                    }
                }else{
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
            }
        });

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_ONOFF);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_VALUE);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_ONOFF);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_VALUE);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_ONOFF);
        plKeyList.add(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_VALUE);


        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, seid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (mApp.getNetService() != null && mApp.getNetService().isCloudBridgeClientOk()) {
            mApp.getNetService().sendNetMsg(mapget);
        }
    }

    public void mapSetHLOnoff(ImibabyApp mApp, int onoff, WatchData watchData){
        hl_status = 1;
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetHLOnoff success");
                }else{
                    switch_high_limit.postValue(!(onoff == 1));
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
                hl_status = 0;
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_ONOFF,String.valueOf(onoff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }
    public void mapSetHLData(ImibabyApp mApp,int value,WatchData watchData){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetHLData success");
                    high_limit.postValue(value);
                }else{
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_WARNING_LIMIT_VALUE,String.valueOf(value));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID,watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }

    public void mapSetRHLOnoff(ImibabyApp mApp,int onoff,WatchData watchData){
        rhl_status = 1;
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetRHLOnoff success");
                }else{
                    swithc_rest_high_limit.postValue(!(onoff == 1));
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
                rhl_status = 0;
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_ONOFF,String.valueOf(onoff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }
    public void mapSetRHLData(ImibabyApp mApp,int value,WatchData watchData){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetRHLData success");
                    rest_high_limit.postValue(value);
                }else{
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_VALUE,String.valueOf(value));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }

    public void mapSetRLLOnoff(ImibabyApp mApp,int onoff,WatchData watchData){
        rll_status = 1;
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetRLLOnoff success");
                }else{
                    switch_rest_low_limit.postValue(!(onoff == 1));
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
                rll_status = 0;
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_ONOFF,String.valueOf(onoff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }
    public void mapSetRLLData(ImibabyApp mApp,int value,WatchData watchData){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    LogUtil.e("mapSetRLLData success");
                    rest_low_limit.postValue(value);
                }else{
                    ToastUtil.show(mApp,mApp.getString(R.string.set_error) + " " + rc);
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_HEART_RATE_REST_LOW_LIMIT_VALUE,String.valueOf(value));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, watchData.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watchData.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }
}
