package com.xiaoxun.xun.utils;

/**
 * Created by xilvkang on 2017/12/13.
 */

public class Scale {
    private final String text;
    private final float length;

    Scale(String text, float length) {
        this.text = text;
        this.length = length;
    }

    public String text() {
        return text;
    }

    public float length() {
        return length;
    }
}
