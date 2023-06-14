package com.xiaoxun.xun.health.outside.bean;

public class OutSideChartDayBean extends OutSideChartBean{
    int hour;   //日图表使用 范围1-24

    int lvl1_chart_value = 0;   //1级在图表上的Y值
    int lvl2_chart_value = 0;   //2级在图表上的Y值
    int lvl3_chart_value = 0;   //3级在图表上的Y值
    int lvl4_chart_value = 0;   //4级在图表上的Y值
    int lvl5_chart_value = 0;   //5级在图表上的Y值

    int outside_day_all_dura = 0;   //户外活动时长

    public OutSideChartDayBean(){
        super();
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }

    public void setOutside_day_all_dura(int outside_day_all_dura) {
        this.outside_day_all_dura = outside_day_all_dura;
    }

    public int getOutside_day_all_dura() {
        return outside_day_all_dura;
    }

    public int getLvl1_chart_value() {
        return lvl1_chart_value;
    }

    public int getLvl2_chart_value() {
        return lvl2_chart_value;
    }


    public int getLvl3_chart_value() {
        return lvl3_chart_value;
    }

    public int getLvl4_chart_value() {
        return lvl4_chart_value;
    }

    public int getLvl5_chart_value() {
        return lvl5_chart_value;
    }

    /**
     * 初始化图表数据时，必须执行改接口
     */
    public void initChartValueBean(){
        lvl1_chart_value = lvl1_dura;
        lvl2_chart_value = lvl2_dura;
        lvl3_chart_value = lvl3_dura;
        lvl4_chart_value = lvl4_dura;
        lvl5_chart_value = lvl5_dura;
    }

    public String printValues(){
        return "chartValues : hour = " + hour + " lvl1 = " + lvl1_chart_value + " | lvl2 = " + lvl2_chart_value
                + " | lvl3 = " + lvl3_chart_value + " | lvl4 = " + lvl4_chart_value + " | lvl5 = " + lvl5_chart_value;
    }
}
