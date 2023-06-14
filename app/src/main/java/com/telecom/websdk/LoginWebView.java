package com.telecom.websdk;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

public class LoginWebView extends WebView {
    private static final String TAG = LoginWebView.class.getSimpleName();

    public LoginWebView(Context context) {
        super(context);
    }

    public LoginWebView(Context context, AttributeSet set) {
        super(context, set);
    }

    public LoginWebView(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
    }

    @Override
    public void goBack() {
        Log.i(TAG, "goback");
            WebConfig.goBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            WebConfig.goBack();
        }
        return super.onKeyDown(keyCode, event);
    }
}
