/**
 * Creation Date:2015-2-10
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

import java.io.Serializable;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-2-10
 * 
 */
public class SilenceTime implements Serializable,Cloneable {

    public final static long serialVersionUID = 201612161601521L;

    public String starthour;//xx
    public String startmin;//xx
    public String endhour;//xx
    public String endmin;//xx
    public String days;//0100100 周日到周六是否选中
    public String onoff;//0,1
    public String timeStampId;//用timeStamp作为id，用以区分
//    public String flag;//标记是上午1、下午2、自定义0
    public int advanceopt; //高级选项开关  0，关闭；1，旧深度防打扰；2，新深度防打扰，表示离线模式开启  默认1
    public int callInOnOff;//能否接电话   这个定义取消，协议中暂保留（20120123）

    public SilenceTime(){

    }

    public SilenceTime(String starthour, String startmin, String endhour, String endmin, String days, String onoff, String timeStampId, int advanceopt, int callInOnOff) {
        this.starthour = starthour;
        this.startmin = startmin;
        this.endhour = endhour;
        this.endmin = endmin;
        this.days = days;
        this.onoff = onoff;
        this.timeStampId = timeStampId;
        this.advanceopt = advanceopt;
        this.callInOnOff = callInOnOff;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static JSONObject toJsonObjectFromSilenceTimeBean(JSONObject jsonObject, SilenceTime silenceTime) {

        jsonObject.put(CloudBridgeUtil.STARTHOUR, silenceTime.starthour);
        jsonObject.put(CloudBridgeUtil.STARTMIN, silenceTime.startmin);
        jsonObject.put(CloudBridgeUtil.ENDHOUR, silenceTime.endhour);
        jsonObject.put(CloudBridgeUtil.ENDMIN, silenceTime.endmin);
        jsonObject.put(CloudBridgeUtil.DAYS, silenceTime.days);
        jsonObject.put(CloudBridgeUtil.ONOFF, silenceTime.onoff);
        jsonObject.put(CloudBridgeUtil.TIMESTAMPID, silenceTime.timeStampId);
        jsonObject.put(CloudBridgeUtil.SILENCETIME_ADVANCEOPT, silenceTime.advanceopt);
        jsonObject.put(CloudBridgeUtil.SILENCETIME_CALL_IN_ONOFF, silenceTime.callInOnOff);
        return jsonObject;
    }

    public static SilenceTime toBeSilenceTimeBean(SilenceTime silenceTime, JSONObject jsonObject) {

        silenceTime.starthour = (String) jsonObject.get(CloudBridgeUtil.STARTHOUR);
        if (silenceTime.starthour.length() == 1)
            silenceTime.starthour = "0" + silenceTime.starthour;
        silenceTime.startmin = (String) jsonObject.get(CloudBridgeUtil.STARTMIN);
        if (silenceTime.startmin.length() == 1)
            silenceTime.startmin = "0" + silenceTime.startmin;
        silenceTime.endhour = (String) jsonObject.get(CloudBridgeUtil.ENDHOUR);
        if (silenceTime.endhour.length() == 1)
            silenceTime.endhour = "0" + silenceTime.endhour;
        silenceTime.endmin = (String) jsonObject.get(CloudBridgeUtil.ENDMIN);
        if (silenceTime.endmin.length() == 1)
            silenceTime.endmin = "0" + silenceTime.endmin;
        silenceTime.days = (String) jsonObject.get(CloudBridgeUtil.DAYS);
        silenceTime.onoff = (String) jsonObject.get(CloudBridgeUtil.ONOFF);
        silenceTime.timeStampId = (String) jsonObject.get(CloudBridgeUtil.TIMESTAMPID);
        if (jsonObject.containsKey(CloudBridgeUtil.SILENCETIME_ADVANCEOPT)) {
            silenceTime.advanceopt = (Integer) jsonObject.get(CloudBridgeUtil.SILENCETIME_ADVANCEOPT);
        } else {
            silenceTime.advanceopt = 0;
        }
        if (jsonObject.containsKey(CloudBridgeUtil.SILENCETIME_CALL_IN_ONOFF)) {
            silenceTime.callInOnOff = (Integer) jsonObject.get(CloudBridgeUtil.SILENCETIME_CALL_IN_ONOFF);
        } else {
            silenceTime.callInOnOff = 0;
        }
        return silenceTime;
    }
}
