package com.xiaoxun.xun.health.bean;

public class FatigueData {
    public static final int DURATION_VALUE = 6;

    private int duration = 0; // 持续时长

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
    public void addDuration(int dur){
        duration += dur;
    }
}
