package com.xiaoxun.xun.securityarea.bean;

import java.io.Serializable;

public class SchoolGuardTimeBean implements Serializable {
    private String id;//时间戳
    private String type;//1上学 2放学
    private String time;//准确上学到校or放学离校时间    08:00
    private String days;//一周哪些天  0011000
    private String fl;//守护时间段  “5,25”

    public SchoolGuardTimeBean() {
    }

    public SchoolGuardTimeBean(String id, String type, String time, String days, String fl) {
        this.id = id;
        this.type = type;
        this.time = time;
        this.days = days;
        this.fl = fl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    @Override
    public String toString() {
        return "SchoolGuardTimeBean{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", days='" + days + '\'' +
                ", fl='" + fl + '\'' +
                '}';
    }
}
