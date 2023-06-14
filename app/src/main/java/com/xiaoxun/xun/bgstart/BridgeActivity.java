package com.xiaoxun.xun.bgstart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.xiaoxun.xun.bgstart.BridgeBroadcast;
import com.xiaoxun.xun.bgstart.SystemAlertWindow;
import com.xiaoxun.xun.bgstart.utils.Miui;
import com.xiaoxun.xun.bgstart.utils.PermissionUtil;

/**
 * @author LongpingZou
 * @date 2019/3/11
 */
public final class BridgeActivity extends Activity {
    public static final String PERMISSION = "has_permission";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.xiaoxun.xun.bgstart.SystemAlertWindow page = new com.xiaoxun.xun.bgstart.SystemAlertWindow(this);
        page.start(com.xiaoxun.xun.bgstart.SystemAlertWindow.REQUEST_OVERLY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SystemAlertWindow.REQUEST_OVERLY == requestCode) {
            if (PermissionUtil.hasPermissionOnActivityResult(this)
                    || (Miui.isMIUI() && Miui.isAllowed(this))) {
                Intent intent = new Intent(com.xiaoxun.xun.bgstart.BridgeBroadcast.SUC);
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent(BridgeBroadcast.FAIL);
                sendBroadcast(intent);
            }
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}