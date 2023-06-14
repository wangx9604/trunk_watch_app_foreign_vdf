package com.xiaoxun.xun.networkv2.beans;

import java.io.Serializable;

public class BaseResponseInfo implements Serializable {
    private static final long serialVersionUID = -1927509501111500001L;
    int code;

    public BaseResponseInfo(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
