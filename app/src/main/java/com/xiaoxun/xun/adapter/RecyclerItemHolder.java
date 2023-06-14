package com.xiaoxun.xun.adapter;

import com.xiaoxun.xun.beans.NoticeMsgData;

/**
 * Created by huangqilin on 2016/7/25.
 */
public interface RecyclerItemHolder {
    void bindTo(NoticeMsgData msg, String time);
}
