package com.xiaoxun.xun.health.monitor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MonitorTimeBean implements Serializable,Cloneable{
    public String starthour;//xx
    public String startmin;//xx
    public String endhour;//xx
    public String endmin;//xx
    public String days;//0100100 周日到周六是否选中
    public String onoff;//0,1
    private String name;
    private int type;   //0 默认不可修改名字 1 自定义可修改名字
    @SerializedName("timeid")
    public String id;//区分各个时段，默认时段永远为1

    public MonitorTimeBean(String name, int type, String starthour, String startmin, String endhour, String endmin
            , String days, String onoff, String Id){
        this.starthour = starthour;
        this.startmin = startmin;
        this.endhour = endhour;
        this.endmin = endmin;
        this.days = days;
        this.onoff = onoff;
        this.id = Id;
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
