package com.xiaoxun.xun.health.report.fragments.dataBean;

public class DayOxyData extends DayChartData{
    private int level;

    public DayOxyData(String time,int o){
        super(time,o);
        level = getOxyLevel(o);
    }

    public int getLevel() {
        return level;
    }

    private int getOxyLevel(int value){
        if(value >= 90){
            return 0;
        }else if(value >= 80){
            return 1;
        }else if(value >= 70){
            return 2;
        }else {
            return 3;
        }
    }
}
