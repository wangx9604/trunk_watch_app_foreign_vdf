package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

/**
 * Created by huangyouyang on 2017/3/14.
 */

public class FunctionBean {

    public String functionName;
    public String order;    //排序
    public String onoff;  // 0关闭，1开始

    public int resId;
    public int functionNameDesc;

    public FunctionBean(){

    }

    public FunctionBean(String functionName, String order, String onoff, ImibabyApp mApp) {
        this.functionName = functionName;
        this.order = order;
        this.onoff = onoff;
        WatchData watch = mApp.getCurUser().getFocusWatch();

        if (functionName.equals(Const.FUNCTION_NAME_DIALER)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.phone_call;
        } else if (functionName.equals(Const.FUNCTION_NAME_CHATROOM)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_chatroom;
        } else if (functionName.equals(Const.FUNCTION_NAME_SPORT)) {
            resId = R.drawable.health_steps;
            functionNameDesc = R.string.health_steps;
        } else if (functionName.equals(Const.FUNCTION_NAME_AUDIOPLAYER)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.ximalaya_story;
        } else if (functionName.equals(Const.FUNCTION_NAME_PETS)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_pets;
        } else if (functionName.equals(Const.FUNCTION_NAME_SETTING)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.security_zone_default_setting;
        } else if (functionName.equals(Const.FUNCTION_NAME_CAMERA)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_camcera;
        } else if (functionName.equals(Const.FUNCTION_NAME_IMAGEVIEWER)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_camcera;
        } else if (functionName.equals(Const.FUNCTION_NAME_AIVOICE)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_aivoice;
            if (watch.isDevice701() || (watch.isDevice710() && !watch.isDevice730()))
                functionNameDesc = R.string.function_control_aivoice_710;
        } else if (functionName.equals(Const.FUNCTION_NAME_FRIENDS)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_friends;
        } else if (functionName.equals(Const.FUNCTION_NAME_QQ)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.account_type_qq;
        } else if (functionName.equals(Const.FUNCTION_NAME_IMGRECOGNITION)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_imgrecognition;
        } else if (functionName.equals(Const.FUNCTION_NAME_ALIPAY)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.alipay;
        } else if (functionName.equals(Const.FUNCTION_NAME_ENGLISH)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_eng;
        } else if (functionName.equals(Const.FUNCTION_NAME_NAVIGATION)) {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_navigation;
        }else if(functionName.equals(Const.FUNCTION_NAME_STOPWATCH)){
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_watchstop;
        } else {
            resId = R.drawable.ximalaya_story;
            functionNameDesc = R.string.function_control_else;
        }
    }

    public static FunctionBean toBeFunctionBean(FunctionBean function, JSONObject jsonObject, ImibabyApp mApp) {

        String name = (String) jsonObject.get(CloudBridgeUtil.FUNCTION_NAME);
        String order = (String) jsonObject.get(CloudBridgeUtil.FUNCTION_ORDER);
        String onoff = (String) jsonObject.get(CloudBridgeUtil.FUNCTION_ONOFF);
        function = new FunctionBean(name, order, onoff, mApp);
        return function;
    }

    public static JSONObject toJsonObjectFromSleepTimeBean(JSONObject jsonObject,FunctionBean function) {

        if (function.functionName != null)
            jsonObject.put(CloudBridgeUtil.FUNCTION_NAME, function.functionName);
        if (function.order != null)
            jsonObject.put(CloudBridgeUtil.FUNCTION_ORDER, function.order);
        if (function.onoff != null)
            jsonObject.put(CloudBridgeUtil.FUNCTION_ONOFF, function.onoff);
        return jsonObject;
    }

}
