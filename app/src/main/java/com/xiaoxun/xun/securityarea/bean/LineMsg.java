package com.xiaoxun.xun.securityarea.bean;

public class LineMsg {
    private float point1X;
    private float point1Y;
    private float point2X;
    private float point2Y;
    private float minX;
    private float minY;
    private float maxX;
    private float maxY;

    public LineMsg() {
    }

    public LineMsg(float point1X, float point1Y, float point2X, float point2Y, float minX, float minY, float maxX, float maxY) {
        this.point1X = point1X;
        this.point1Y = point1Y;
        this.point2X = point2X;
        this.point2Y = point2Y;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public float getPoint1X() {
        return point1X;
    }

    public void setPoint1X(float point1X) {
        this.point1X = point1X;
    }

    public float getPoint1Y() {
        return point1Y;
    }

    public void setPoint1Y(float point1Y) {
        this.point1Y = point1Y;
    }

    public float getPoint2X() {
        return point2X;
    }

    public void setPoint2X(float point2X) {
        this.point2X = point2X;
    }

    public float getPoint2Y() {
        return point2Y;
    }

    public void setPoint2Y(float point2Y) {
        this.point2Y = point2Y;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    @Override
    public String toString() {
        return "LineMsg{" +
                "point1X=" + point1X +
                ", point1Y=" + point1Y +
                ", point2X=" + point2X +
                ", point2Y=" + point2Y +
                ", minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }
}
