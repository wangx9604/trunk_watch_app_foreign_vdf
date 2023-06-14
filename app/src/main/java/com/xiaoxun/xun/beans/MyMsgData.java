/**
 * Creation Date:2015-2-26
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.interfaces.MsgCallback;

import net.minidev.json.JSONObject;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-2-26
 * 
 */
public class MyMsgData {
    public JSONObject reqMsg;
    public MsgCallback callback;
    public int timeout;
    public int netType;//websocket 0; http1
    public int proiorty;//proiorty 0，最高 1，2，。。
    public int state;// 0   未发送   1 发送中 2 发送完
    public boolean needNetTimeout;
    public int finaltimeout;
    //
    public MyMsgData() {
        timeout = 30000;
        proiorty = 0;//默认 0，后台类msg用1
        netType = 0;//默认 0 webscokt
        state = 0;//默认 0 未发送
        needNetTimeout = false;
        finaltimeout = 30000;
    }
    public JSONObject getReqMsg() {
        return reqMsg;
    }
    public void setReqMsg(JSONObject reqMsg) {
        this.reqMsg = reqMsg;
    }
    public MsgCallback getCallback() {
        return callback;
    }
    /**
     * 
     * @param callback 设置null时，交给netservice处理，调用者不直接处理回调
     */
    public void setCallback(MsgCallback callback) {
        this.callback = callback;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public int getNetType() {
        return netType;
    }
    public void setNetType(int netType) {
        this.netType = netType;
    }
    public int getProiorty() {
        return proiorty;
    }
    public void setProiorty(int proiorty) {
        this.proiorty = proiorty;
    }

    public void setNeedNetTimeout (boolean needtimeout) {
        this.needNetTimeout = needtimeout;
    }

    public boolean getNeedNetTimeout() {
        return needNetTimeout;
    }

    public void setFinalTimeout (int finalTimeout) {
        this.finaltimeout = finalTimeout;
    }

    public void resetTimeout() {
        timeout = finaltimeout;
    }

}
