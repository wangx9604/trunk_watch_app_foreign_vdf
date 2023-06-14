package com.xiaoxun.xun.focustime;

import com.xiaoxun.xun.beans.SilenceTime;

import java.io.Serializable;

public class FocusTimeBean extends SilenceTime implements Serializable,Cloneable{
    private String name;
    private int type;   //0 默认不可修改名字 1 自定义可修改名字

    public FocusTimeBean(String name,int type,String starthour, String startmin, String endhour, String endmin
            , String days, String onoff, String timeStampId){
        super(starthour,startmin,endhour,endmin,days,onoff,timeStampId,1,1);
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
}
