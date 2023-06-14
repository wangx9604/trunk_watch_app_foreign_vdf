package com.xiaoxun.xun.health.record;

import com.xiaoxun.xun.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Record {
    private String timeStamp;   //unix 时间
    private int type;   //0 心率 1 血氧 2 视疲劳 3 脑疲劳
    private int value;

    public Record(String s,int t,int v){
        timeStamp = s;
        type = t;
        value = v;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public String getFormatUnixDate(){
        if(timeStamp == null || timeStamp.equals("")){
            return null;
        }
        Date d = TimeUtil.unixTimeToDate(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        return format.format(d);
    }
}
