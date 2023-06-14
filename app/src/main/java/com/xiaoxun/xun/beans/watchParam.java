package com.xiaoxun.xun.beans;

/**
 * Created by liutianxiang on 2015/9/30.
 */
public class watchParam {
    public long[] updatetime = {0, 0, 0};

    public void updateParamTime(int pr, long time) {
        updatetime[pr] = time;
    }
}
