package com.xiaoxun.xun.motion.beans;

import androidx.annotation.NonNull;

public class ScheduleWeekBean implements Comparable<ScheduleWeekBean>{
    int mWeekNum;//6:周六   7：周日
    String mWeekClassName;
    String mStartTime;
    String mEndTime;
    int mType;     //设置页面显示视图使用默认为1

    public ScheduleWeekBean() {
    }

    public ScheduleWeekBean(int mWeekNum, String mWeekClassName, String mStartTime, String mEndTime, int mType) {
        this.mWeekNum = mWeekNum;
        this.mWeekClassName = mWeekClassName;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mType = mType;
    }

    public boolean onIsSameForSilenceList(ScheduleWeekBean mNextBean){
        return this.mWeekNum == mNextBean.mWeekNum
                && this.mWeekClassName.equals(mNextBean.mWeekClassName)
                && this.mStartTime.equals(mNextBean.mStartTime)
                && this.mEndTime.equals(mNextBean.mEndTime)
                && this.mType == mNextBean.mType;
    }

    public int getmWeekNum() {
        return mWeekNum;
    }

    public void setmWeekNum(int mWeekNum) {
        this.mWeekNum = mWeekNum;
    }

    public String getmWeekClassName() {
        return mWeekClassName;
    }

    public void setmWeekClassName(String mWeekClassName) {
        this.mWeekClassName = mWeekClassName;
    }

    public String getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    @Override
    public int compareTo(@NonNull ScheduleWeekBean o) {
        return this.getmStartTime().compareTo(o.getmStartTime());
    }
}
