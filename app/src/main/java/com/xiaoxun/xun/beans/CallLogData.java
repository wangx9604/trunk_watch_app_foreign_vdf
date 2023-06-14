package com.xiaoxun.xun.beans;

/**
 * Created by guxiaolong on 2016/6/21.
 */
public class CallLogData {
    String watchEid;
    String name;
    int type;
    String localNumber;
    String number;
    String timestamp;
    int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWatchEid() {
        return watchEid;
    }

    public void setWatchEid(String watchEid) {
        this.watchEid = watchEid;
    }
}
