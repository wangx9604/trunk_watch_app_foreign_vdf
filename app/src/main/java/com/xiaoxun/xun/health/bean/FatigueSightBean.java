package com.xiaoxun.xun.health.bean;

public class FatigueSightBean {
    private String date;
    private FatigueData normal;
    private FatigueData tiny;
    private FatigueData abit;
    private FatigueData obvious;

    public FatigueSightBean(String d){
        date = d;
        normal = new FatigueData();
        tiny = new FatigueData();
        abit = new FatigueData();
        obvious = new FatigueData();
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

    public void setTiny(FatigueData tiny) {
        this.tiny = tiny;
    }

    public FatigueData getTiny() {
        return tiny;
    }

    public void setAbit(FatigueData abit) {
        this.abit = abit;
    }

    public FatigueData getAbit() {
        return abit;
    }

    public void setObvious(FatigueData obvious) {
        this.obvious = obvious;
    }

    public FatigueData getObvious() {
        return obvious;
    }

    public String printData(){
        String ret = "";
        if(date != null && normal != null && tiny != null && abit != null && obvious != null){
            ret = "date : " + date + " | normal = " + normal.getDuration() + " | tiny = " + tiny.getDuration()
                    + " | abit = " + abit.getDuration() + " | obvious = " + obvious.getDuration();
        }
        return ret;
    }
}
