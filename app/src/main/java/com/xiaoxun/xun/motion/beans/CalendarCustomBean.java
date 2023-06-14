package com.xiaoxun.xun.motion.beans;

import java.util.ArrayList;

public class CalendarCustomBean {
    private String content;
    private String remind_time ="08:00";
    private String days ="0111110";
    private int remind_dvs =1;
    private int remind_app =0;
    private ArrayList<String> appid;

    public ArrayList<String> getAppid() {
        return appid;
    }

    public void setAppid(ArrayList<String> appid) {
        this.appid = appid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
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

    public void cloneDataTo(CalendarCustomBean mCustomBeanChange) {
        mCustomBeanChange.setContent(this.content);
        mCustomBeanChange.setRemind_app(this.remind_app);
        mCustomBeanChange.setRemind_time(this.remind_time);
        mCustomBeanChange.setRemind_dvs(this.remind_dvs);
        mCustomBeanChange.setDays(this.days);
        mCustomBeanChange.setAppid(this.appid);
    }
}
