package com.xiaoxun.xun.networkv2.beans;

public class MotionResoponseInfo {
    int mCode;//0:数据刷新
    String mStateInfo;

    public MotionResoponseInfo(int mCode, String mStateInfo) {
        this.mCode = mCode;
        this.mStateInfo = mStateInfo;
    }

    public int getmCode() {
        return mCode;
    }

    public String getmStateInfo() {
        return mStateInfo;
    }
}
