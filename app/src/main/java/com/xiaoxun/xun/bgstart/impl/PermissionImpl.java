package com.xiaoxun.xun.bgstart.impl;

import android.app.Activity;
import android.os.Build;

import com.xiaoxun.xun.bgstart.api.PermissionServer;

import com.xiaoxun.xun.bgstart.api.PermissionLisenter;
import com.xiaoxun.xun.bgstart.api.ShowSource;
import com.xiaoxun.xun.bgstart.utils.Miui;
import com.xiaoxun.xun.bgstart.widgets.FloatSource;
import com.xiaoxun.xun.bgstart.widgets.MiuiSource;

/**
 * @ProjectName: BGStart
 * @Package: com.xiaoxun.xun.bgstart.impl
 * @ClassName: PermissionImpl
 * @Description:java类作用描述
 * @Author: zhouxue
 * @CreateDate: 2020/7/16 10:02
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/16 10:02
 * @UpdateRemark: 更新说明
 * @Version:1.0
 */
public class PermissionImpl implements PermissionServer {
    private ShowSource mSource;
    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    public PermissionImpl(ShowSource mSource) {
        this.mSource = mSource;
    }

    @Override
    public void checkPermisstion(final Activity activity, final PermissionLisenter lisenter, String... params) {
        if (mSource == null)
            if (Miui.isMIUI()) {
                mSource = new MiuiSource();
            } else {
                mSource = new FloatSource();
            }
        boolean isShowNotice = false;
        if ("oppo".equals(MARK)) {
            isShowNotice = true;
        }
        if (params != null && !isShowNotice) {
            for (String str : params) {
                if (MARK.equals(str)) {
                    isShowNotice = true;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || isShowNotice) {
            mSource.show(activity, lisenter);
        }
    }
}
