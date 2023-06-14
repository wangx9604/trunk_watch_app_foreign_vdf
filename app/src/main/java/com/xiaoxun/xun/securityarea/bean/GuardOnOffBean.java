package com.xiaoxun.xun.securityarea.bean;

public class GuardOnOffBean {
    private int com;//常用
    private int school;//上下学守护
    private int danger;//危险区域
    private int safe;//安全区域
    private int city;//基础城市服务


    public GuardOnOffBean(int com, int school, int danger, int safe, int city) {
        this.com = com;
        this.school = school;
        this.danger = danger;
        this.safe = safe;
        this.city = city;
    }

    public int getCom() {
        return com;
    }

    public void setCom(int com) {
        this.com = com;
    }

    public int getSchool() {
        return school;
    }

    public void setSchool(int school) {
        this.school = school;
    }

    public int getDanger() {
        return danger;
    }

    public void setDanger(int danger) {
        this.danger = danger;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "GuardOnOffBean{" +
                "com=" + com +
                ", school=" + school +
                ", danger=" + danger +
                ", safe=" + safe +
                ", city=" + city +
                '}';
    }
}
