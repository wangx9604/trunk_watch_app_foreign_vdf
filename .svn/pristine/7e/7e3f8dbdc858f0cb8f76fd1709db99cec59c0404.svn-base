package com.xiaoxun.xun.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.utils.AppStoreUtils;

public class JavaScriptSubObject extends Object {
    private Context mContxt;
    private ImibabyApp myApp;
    private String mOrientation;

    public JavaScriptSubObject(Context mContxt, ImibabyApp mApp) {
        this.mContxt = mContxt;
        this.myApp = mApp;
    }

    @JavascriptInterface //sdk17版本以上加上注解
    public String getToken() {
        return AppStoreUtils.getContentToken(myApp);
    }

    @JavascriptInterface
    public void HtmlcallJava2(final String param) {
        mOrientation = param;
    }

    public String getOrientation(){
        return mOrientation;
    }
}
