package com.xiaoxun.xun.motion.beans;

public class CalendarScheduleInfoBean {
    private String mClassName;
    private String mLeavteTime;
    private int  mLocInClassTime;
    private boolean isHasClass = false;//是否有课程

    public CalendarScheduleInfoBean(boolean isHasClass) {
        this.isHasClass = isHasClass;
    }

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public String getmLeavteTime() {
        return mLeavteTime;
    }

    public void setmLeavteTime(String mLeavteTime) {
        this.mLeavteTime = mLeavteTime;
    }

    public int getmLocInClassTime() {
        return mLocInClassTime;
    }

    public void setmLocInClassTime(int mLocInClassTime) {
        this.mLocInClassTime = mLocInClassTime;
    }

    public boolean isHasClass() {
        return isHasClass;
    }

    public void setHasClass(boolean hasClass) {
        isHasClass = hasClass;
    }
}
