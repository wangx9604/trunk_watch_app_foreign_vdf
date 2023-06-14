package com.xiaoxun.xun.motion.beans;

import java.util.ArrayList;

public class CalendarLearSchoolBean {
    private String schedule_time="30";
    private String fix_time="16:00";
    private String remind_time="16:30";
    private int course = 0;
    private String days="0111110";
    private int bad_weather=20;
    private ArrayList<String> appid = new ArrayList<>();

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getBad_weather() {
        return bad_weather;
    }

    public void setBad_weather(int bad_weather) {
        this.bad_weather = bad_weather;
    }

    public ArrayList<String> getAppid() {
        return appid;
    }

    public void setAppid(ArrayList<String> appid) {
        this.appid = appid;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getFix_time() {
        return fix_time;
    }

    public void setFix_time(String fix_time) {
        this.fix_time = fix_time;
    }

    public void cloneDataTo(CalendarLearSchoolBean mLeaveBeanChange) {
        mLeaveBeanChange.setAppid(this.appid);
        mLeaveBeanChange.setBad_weather(this.bad_weather);
        mLeaveBeanChange.setCourse(this.course);
        mLeaveBeanChange.setDays(this.days);
        mLeaveBeanChange.setRemind_time(this.remind_time);
        mLeaveBeanChange.setFix_time(this.fix_time);
        mLeaveBeanChange.setSchedule_time(this.schedule_time);
    }
}
