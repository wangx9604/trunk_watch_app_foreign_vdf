package com.telecom.websdk;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by xilvkang on 2015/12/26.
 */
public class NormalWebView extends WebView {
    public NormalWebView(Context context) {
        super(context);
    }
    public NormalWebView(Context context, AttributeSet set) {
        super(context, set);
    }

    public NormalWebView(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
    }

    @Override
    public void goBack() {
        if(WebConfig.homepageFlag){
            WebConfig.goBack();
        }else {
            super.goBack();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(WebConfig.homepageFlag)
                WebConfig.goBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(this.getScrollY() <= 0)
                    this.scrollTo(0,1);
                break;
        }
        return super.onTouchEvent(event);
    }
}
