package com.xiaoxun.xun.health.bean;

public class FatigueBrainBean {
    private String date;
    private FatigueData normal;
    private FatigueData light;
    private FatigueData middle;
    private FatigueData severe;

    public FatigueBrainBean(String d){
        date = d;
        normal = new FatigueData();
        light = new FatigueData();
        middle = new FatigueData();
        severe = new FatigueData();
    }

    public String getDate() {
        return date;
    }

    public void setNormal(FatigueData normal) {
        this.normal = normal;
    }
    public FatigueData getNormal() {
        return normal;
    }

    public void setLight(FatigueData light) {
        this.light = light;
    }

    public FatigueData getLight() {
        return light;
    }

    public void setMiddle(FatigueData middle) {
        this.middle = middle;
    }

    public FatigueData getMiddle() {
        return middle;
    }

    public void setSevere(FatigueData severe) {
        this.severe = severe;
    }

    public FatigueData getSevere() {
        return severe;
    }

    public String printData(){
        String ret = "";
        if(date != null && normal != null && light != null && middle != null && severe != null){
            ret = "date : " + date + " | normal = " + normal.getDuration() + " | tiny = " + light.getDuration()
                    + " | abit = " + middle.getDuration() + " | obvious = " + severe.getDuration();
        }
        return ret;
    }
}
