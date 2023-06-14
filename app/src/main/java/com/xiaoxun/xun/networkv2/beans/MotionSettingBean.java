package com.xiaoxun.xun.networkv2.beans;

import java.util.ArrayList;

public class MotionSettingBean {
    int total_strength = 1;
    ArrayList<String> sport_strength;
    ArrayList<String> sport_test;

    public MotionSettingBean() {
        sport_strength = new ArrayList<>();
        sport_test = new ArrayList<>();
    }

    public int getTotal_strength() {
        return total_strength;
    }

    public ArrayList<String> getSport_strength() {
        return sport_strength;
    }

    public ArrayList<String> getSport_test() {
        return sport_test;
    }

    public void setTotal_strength(int total_strength) {
        this.total_strength = total_strength;
    }

    public void setSport_strength(ArrayList<String> sport_strength) {
        this.sport_strength = sport_strength;
    }

    public void setSport_test(ArrayList<String> sport_test) {
        this.sport_test = sport_test;
    }

    public void clone(MotionSettingBean oldBean){
        if(oldBean == null) return;
        this.total_strength = oldBean.total_strength;
        this.sport_strength.addAll(oldBean.getSport_strength());
        this.sport_test.addAll(oldBean.getSport_test());
    }

    public boolean isDiff(MotionSettingBean motionSettingBean) {
        if(this.total_strength != motionSettingBean.getTotal_strength()) return true;
        for (String s : motionSettingBean.sport_strength) {
            int i;
            for (i = 0; i < this.sport_strength.size(); i++) {
                if(this.sport_strength.get(i).equals(s)) break;
            }
            if(i == this.sport_strength.size()) return true;
        }
        if(motionSettingBean.sport_strength.size() != this.sport_strength.size()) return true;
        for (String s : motionSettingBean.sport_test) {
            int i;
            for (i = 0; i < this.sport_test.size(); i++) {
                if(this.sport_test.get(i).equals(s)) break;
            }
            if(i == this.sport_test.size()) return true;
        }
        if(motionSettingBean.sport_test.size() != this.sport_test.size()) return true;
        return false;
    }
}
