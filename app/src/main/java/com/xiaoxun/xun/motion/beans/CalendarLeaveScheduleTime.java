package com.xiaoxun.xun.motion.beans;

public class CalendarLeaveScheduleTime {
    int mWeatherRemind = 20;
    int mCustomRemind = 30;
    String mLeaveClass;
    String mGroupList;

    public int getmWeatherRemind() {
        return mWeatherRemind;
    }

    public void setmWeatherRemind(int mWeatherRemind) {
        this.mWeatherRemind = mWeatherRemind;
    }

    public int getmCustomRemind() {
        return mCustomRemind;
    }

    public void setmCustomRemind(int mCustomRemind) {
        this.mCustomRemind = mCustomRemind;
    }

    public String getmLeaveClass() {
        return mLeaveClass;
    }

    public void setmLeaveClass(String mLeaveClass) {
        this.mLeaveClass = mLeaveClass;
    }

    public String getmGroupList() {
        return mGroupList;
    }

    public void setmGroupList(String mGroupList) {
        this.mGroupList = mGroupList;
    }

    public void cloneTo(CalendarLeaveScheduleTime mLeaveSchduelBeanChange) {
        mLeaveSchduelBeanChange.setmCustomRemind(this.mCustomRemind);
        mLeaveSchduelBeanChange.setmWeatherRemind(this.mWeatherRemind);
        mLeaveSchduelBeanChange.setmGroupList(this.mGroupList);
        mLeaveSchduelBeanChange.setmLeaveClass(this.mLeaveClass);
    }
}
