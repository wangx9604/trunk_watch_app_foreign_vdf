package com.xiaoxun.xun.networkv2.utils;

import com.google.gson.Gson;

public class ToolUtil {

    public static <T> String tranJsonFromBeanByGson(T t){
        return new Gson().toJson(t);
    }
}
