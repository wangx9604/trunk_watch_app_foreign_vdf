package com.xiaoxun.xun.motion.beans;

import java.util.ArrayList;

public class CalendarSchoolBean {

    private String remind_time ="07:30";
    private int bad_weather = 20;
    private int remind_dvs = 1;
    private int remind_app = 0;
    private ArrayList<String> appid;
    private ArrayList<String> remind_dts = new ArrayList<>();
    private ArrayList<String> goods = new ArrayList<>();
    private ArrayList<String> custom_goods = new ArrayList<>();

    public CalendarSchoolBean() {
        remind_dts.add("weather");
        goods.add("口罩");
    }

    public ArrayList<String> getCustom_goods() {
        return custom_goods;
    }

    public void setCustom_goods(ArrayList<String> custom_goods) {
        this.custom_goods = custom_goods;
    }

    public ArrayList<String> getAppid() {
        return appid;
    }

    public void setAppid(ArrayList<String> appid) {
        this.appid = appid;
    }

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public int getBad_weather() {
        return bad_weather;
    }

    public void setBad_weather(int bad_weather) {
        this.bad_weather = bad_weather;
    }

    public ArrayList<String> getRemind_dts() {
        return remind_dts;
    }

    public void setRemind_dts(ArrayList<String> remind_dts) {
        this.remind_dts = remind_dts;
    }

    public ArrayList<String> getGoods() {
        return goods;
    }

    public void setGoods(ArrayList<String> goods) {
        this.goods = goods;
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

    public void cloneDataTo(CalendarSchoolBean mCalendarSchoolChanges) {
        mCalendarSchoolChanges.setBad_weather(this.bad_weather);
        mCalendarSchoolChanges.setRemind_app(this.remind_app);
        mCalendarSchoolChanges.setRemind_dvs(this.remind_dvs);
        mCalendarSchoolChanges.setRemind_time(this.remind_time);
        mCalendarSchoolChanges.setAppid(this.appid);
        mCalendarSchoolChanges.setGoods(this.goods);
        mCalendarSchoolChanges.setRemind_dts(this.remind_dts);
        mCalendarSchoolChanges.setCustom_goods(this.custom_goods);
    }
}
