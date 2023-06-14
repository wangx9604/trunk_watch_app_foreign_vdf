package com.xiaoxun.xun.securityarea.bean;

import java.io.Serializable;
import java.util.List;

public class SchoolGuardBean implements Serializable {
    private String onoff;
    private String holiday_onoff;
    private List<SchoolGuardTimeBean> times;
    private String last_time;
    private String last_time_fl;

    public SchoolGuardBean() {
    }

    public SchoolGuardBean(String onoff, String holiday_onoff, List<SchoolGuardTimeBean> times, String last_time, String last_time_fl) {
        this.onoff = onoff;
        this.holiday_onoff = holiday_onoff;
        this.times = times;
        this.last_time = last_time;
        this.last_time_fl = last_time_fl;
    }

    public String getOnoff() {
        return onoff;
    }

    public void setOnoff(String onoff) {
        this.onoff = onoff;
    }

    public String getHoliday_onoff() {
        return holiday_onoff;
    }

    public void setHoliday_onoff(String holiday_onoff) {
        this.holiday_onoff = holiday_onoff;
    }

    public List<SchoolGuardTimeBean> getTimes() {
        return times;
    }

    public void setTimes(List<SchoolGuardTimeBean> times) {
        this.times = times;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getLast_time_fl() {
        return last_time_fl;
    }

    public void setLast_time_fl(String last_time_fl) {
        this.last_time_fl = last_time_fl;
    }

    @Override
    public String toString() {
        return "SchoolGuardBean{" +
                "onoff='" + onoff + '\'' +
                ", holiday_onoff='" + holiday_onoff + '\'' +
                ", times=" + times +
                ", last_time='" + last_time + '\'' +
                ", last_time_fl='" + last_time_fl + '\'' +
                '}';
    }
}
