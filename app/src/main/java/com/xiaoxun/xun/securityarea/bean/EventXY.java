package com.xiaoxun.xun.securityarea.bean;

public class EventXY {
    private float x;
    private float y;

    public EventXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public EventXY() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "EventXY{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
