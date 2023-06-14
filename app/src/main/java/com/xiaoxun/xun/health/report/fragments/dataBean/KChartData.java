package com.xiaoxun.xun.health.report.fragments.dataBean;

public class KChartData {
    private String timeStamp;
    private int high;
    private int low;

    public KChartData(String t,int h,int l){
        timeStamp = t;
        high = h;
        low = l;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }
}
