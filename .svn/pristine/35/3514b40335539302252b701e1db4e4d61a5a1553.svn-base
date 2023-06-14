package com.xiaoxun.xun.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.xiaoxun.xun.interfaces.InterfacesUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zhangjun5 on 2019/8/5.
 */

public class MyHandler extends Handler {
    public static final int CHANGE_VIEW_FLAG = 1;
    public static final int CHANGE_ITEM_VIEW_FLAG = 2;

    private InterfacesUtil.UpdateView onListener;
    private WeakReference<Context> context;

    public MyHandler(Context ctxt, InterfacesUtil.UpdateView updateListener){
        context = new WeakReference(ctxt);
        onListener = updateListener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case CHANGE_VIEW_FLAG:
                onListener.UpdateView(CHANGE_VIEW_FLAG);
                break;
            case CHANGE_ITEM_VIEW_FLAG:
                onListener.UpdateView(CHANGE_ITEM_VIEW_FLAG);
                break;
        }
    }

}
