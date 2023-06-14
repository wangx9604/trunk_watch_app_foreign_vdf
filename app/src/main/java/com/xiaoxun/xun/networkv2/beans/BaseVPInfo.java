package com.xiaoxun.xun.networkv2.beans;

import android.content.Context;

public class BaseVPInfo {
    Context mContext;
    String mInfo1;
    String mInfo2;
    String mInfo3;
    String mInfo4;

    public BaseVPInfo() {
    }

    public BaseVPInfo(Context mContext, String mInfo1) {
        this.mContext = mContext;
        this.mInfo1 = mInfo1;
    }

    public BaseVPInfo(Context mContext, String mInfo1, String mInfo2) {
        this.mContext = mContext;
        this.mInfo1 = mInfo1;
        this.mInfo2 = mInfo2;
    }

    public BaseVPInfo(Context mContext, String mInfo1, String mInfo2, String mInfo3) {
        this.mContext = mContext;
        this.mInfo1 = mInfo1;
        this.mInfo2 = mInfo2;
        this.mInfo3 = mInfo3;
    }

    public BaseVPInfo(Context mContext, String mInfo1, String mInfo2, String mInfo3, String mInfo4) {
        this.mContext = mContext;
        this.mInfo1 = mInfo1;
        this.mInfo2 = mInfo2;
        this.mInfo3 = mInfo3;
        this.mInfo4 = mInfo4;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getmInfo1() {
        return mInfo1;
    }

    public void setmInfo1(String mInfo1) {
        this.mInfo1 = mInfo1;
    }

    public String getmInfo2() {
        return mInfo2;
    }

    public void setmInfo2(String mInfo2) {
        this.mInfo2 = mInfo2;
    }

    public String getmInfo3() {
        return mInfo3;
    }

    public void setmInfo3(String mInfo3) {
        this.mInfo3 = mInfo3;
    }

    public String getmInfo4() {
        return mInfo4;
    }

    public void setmInfo4(String mInfo4) {
        this.mInfo4 = mInfo4;
    }
}
