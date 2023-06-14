package com.xiaoxun.xun.motion.beans;

import androidx.annotation.NonNull;

public class CalendarMainBean implements Comparable<CalendarMainBean>{
    String name;
    String mStartTime;
    int mShowIcon;
    boolean isRemindWatch;
    boolean isRemindFamily;
    int mCalendarType; //1:早睡早起  2:上学出发  3:课程提醒   4:放学接娃  5:自定义提醒
    String mCalendarId;//消息id
    String mSortTime;

    public CalendarMainBean(int mType, String name, String mStartTime, int mShowIcon, boolean isRemindWatch, boolean isRemindFamily) {
        this.mCalendarType = mType;
        this.name = name;
        this.mStartTime = mStartTime;
        this.mShowIcon = mShowIcon;
        this.isRemindWatch = isRemindWatch;
        this.isRemindFamily = isRemindFamily;
    }

    public String getmSortTime() {
        return mSortTime;
    }

    public void setmSortTime(String mSortTime) {
        this.mSortTime = mSortTime;
    }

    public int getmCalendarType() {
        return mCalendarType;
    }

    public void setmCalendarType(int mCalendarType) {
        this.mCalendarType = mCalendarType;
    }

    public String getmCalendarId() {
        return mCalendarId;
    }

    public void setmCalendarId(String mCalendarId) {
        this.mCalendarId = mCalendarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public int getmShowIcon() {
        return mShowIcon;
    }

    public void setmShowIcon(int mShowIcon) {
        this.mShowIcon = mShowIcon;
    }

    public boolean isRemindWatch() {
        return isRemindWatch;
    }

    public void setRemindWatch(boolean remindWatch) {
        isRemindWatch = remindWatch;
    }

    public boolean isRemindFamily() {
        return isRemindFamily;
    }

    public void setRemindFamily(boolean remindFamily) {
        isRemindFamily = remindFamily;
    }


    @Override
    public int compareTo(@NonNull CalendarMainBean o) {
        return this.getmSortTime().compareTo(o.getmSortTime());
    }
}
