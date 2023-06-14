package com.xiaoxun.xun.motion.beans;

import java.util.ArrayList;

public class CalendarSleepBean {
    int holiday_onoff = 0;
    int isup = 0;
    int remind_dvs = 1;
    int remind_app =0;
    String up_time = "06:30";
    String sleep_time ="22:00";
    ArrayList<String> appid;

    public ArrayList<String> getAppid() {
        return appid;
    }

    public void setAppid(ArrayList<String> appid) {
        this.appid = appid;
    }

    public int getHoliday_onoff() {
        return holiday_onoff;
    }

    public void setHoliday_onoff(int holiday_onoff) {
        this.holiday_onoff = holiday_onoff;
    }

    public int getIsup() {
        return isup;
    }

    public void setIsup(int isup) {
        this.isup = isup;
    }

    public int getRemind_dvs() {
        return remind_dvs;
    }

    public void setRemind_dvs(int remind_dvs) {
        this.remind_dvs = remind_dvs;
    }

    public int getRemind_app() {
        return remind_app;
    }

    public void setRemind_app(int remind_app) {
        this.remind_app = remind_app;
    }

    public String getUp_time() {
        return up_time;
    }

    public void setUp_time(String up_time) {
        this.up_time = up_time;
    }

    public String getSleep_time() {
        return sleep_time;
    }

    public void setSleep_time(String sleep_time) {
        this.sleep_time = sleep_time;
    }

    public void cloneDataTo(CalendarSleepBean mSleepBeansChanges) {
        mSleepBeansChanges.setSleep_time(this.sleep_time);
        mSleepBeansChanges.setHoliday_onoff(this.holiday_onoff);
        mSleepBeansChanges.setIsup(this.isup);
        mSleepBeansChanges.setRemind_app(this.remind_app);
        mSleepBeansChanges.setRemind_dvs(this.remind_dvs);
        mSleepBeansChanges.setUp_time(this.up_time);
        mSleepBeansChanges.setAppid(this.appid);
    }
}
