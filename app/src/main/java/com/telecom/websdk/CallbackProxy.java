package com.telecom.websdk;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class CallbackProxy implements Callback {
    private static final String TAG = CallbackProxy.class.getSimpleName();
    private Callback mCallback;

    public CallbackProxy(Callback callback) {
        mCallback = callback;
    }

    @Override
    @JavascriptInterface
    public void closeWindow() {
        if (mCallback != null) {
            mCallback.closeWindow();
        }
    }

    @Override
    public void backClose() {

    }

    /**
     * 该方法为js端调用，弹起选择对话框的按钮
     * @param target 表示为不同的选择框
     */
    @JavascriptInterface
    public void selectImageFile(String target) {
        Log.d(TAG, "selectImageFile");
        WebConfig.startActivityForResult(target);
    }
}
