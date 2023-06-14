package com.xiaoxun.xun.beans;

/**
 * Created by zhangjun5 on 2017/2/7.
 */

public class RanksStepsEntity implements Comparable<RanksStepsEntity> {
    public String eid;
    public String name;
    public int num;
    public int steps;

    @Override
    public int compareTo(RanksStepsEntity another) {
        return -new Integer(this.steps).compareTo(new Integer(another.steps));
    }
}
