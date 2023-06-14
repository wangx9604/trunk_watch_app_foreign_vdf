package com.telecom.websdk;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class CookieConfig {

    /**
     * 清除app的cookie, 建议在退出账号时使用
     * @param context @NotNull
     */
    public static void clearCookie(Context context) {
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
}
