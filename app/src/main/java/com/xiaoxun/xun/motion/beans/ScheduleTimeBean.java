package com.xiaoxun.xun.motion.beans;

public class ScheduleTimeBean implements Comparable<ScheduleTimeBean>{
    private int mScheduleTimeId;
    private String mScheduleTimeNum;
    private String mScheduleStartTime;
    private String mScheduleEndTime;
    private int mHourZoneType;//1:课程表表单   2：操作页面
    private boolean isSelect = false; //是否选择

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getmScheduleTimeId() {
        return mScheduleTimeId;
    }

    public void setmScheduleTimeId(int mScheduleTimeId) {
        this.mScheduleTimeId = mScheduleTimeId;
    }

    public String getmScheduleTimeNum() {
        return mScheduleTimeNum;
    }

    public void setmScheduleTimeNum(String mScheduleTimeNum) {
        this.mScheduleTimeNum = mScheduleTimeNum;
    }

    public String getmScheduleStartTime() {
        return mScheduleStartTime;
    }

    public void setmScheduleStartTime(String mScheduleStartTime) {
        this.mScheduleStartTime = mScheduleStartTime;
    }

    public String getmScheduleEndTime() {
        return mScheduleEndTime;
    }

    public void setmScheduleEndTime(String mScheduleEndTime) {
        this.mScheduleEndTime = mScheduleEndTime;
    }

    public int getmHourZoneType() {
        return mHourZoneType;
    }

    public void setmHourZoneType(int mHourZoneType) {
        this.mHourZoneType = mHourZoneType;
    }

    public ScheduleTimeBean(int mScheduleTimeId, String mScheduleTimeNum, String mScheduleStartTime, String mScheduleEndTime, int mHourZoneType) {
        this.mScheduleTimeId = mScheduleTimeId;
        this.mScheduleTimeNum = mScheduleTimeNum;
        this.mScheduleStartTime = mScheduleStartTime;
        this.mScheduleEndTime = mScheduleEndTime;
        this.mHourZoneType = mHourZoneType;
    }

    @Override
    public String toString() {
        return "ScheduleTimeBean{" +
                "mScheduleTimeId='" + mScheduleTimeId + '\'' +
                ", mScheduleTimeNum='" + mScheduleTimeNum + '\'' +
                ", mScheduleStartTime='" + mScheduleStartTime + '\'' +
                ", mScheduleEndTime='" + mScheduleEndTime + '\'' +
                ", mHourZoneType='" + mHourZoneType + '\'' +
                '}';
    }

    @Override
    public int compareTo(ScheduleTimeBean o) {
        return this.getmScheduleStartTime().compareTo(o.getmScheduleStartTime());
    }
}
