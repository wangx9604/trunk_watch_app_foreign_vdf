package com.xiaoxun.xun.bgstart.widgets;

import static com.xiaoxun.xun.utils.PermissionUtils.REQUEST_VODE_ALERT_WINDOW;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.bgstart.api.PermissionLisenter;
import com.xiaoxun.xun.bgstart.api.ShowSource;
import com.xiaoxun.xun.utils.AndroidUtil;
import com.xiaoxun.xun.utils.DialogUtil;


/**
 * Copyright (C), 2015-2020
 * FileName: FloatSource
 * Author: zx
 * Date: 2020/4/17 15:17
 * Description:
 */
public class FloatSource implements ShowSource {

    @Override
    public void show(Activity context, final PermissionLisenter permissionListener) {
        Dialog dlg = DialogUtil.CustomNormalDialog(context,
                context.getString(R.string.able_alert_window),
                context.getString(R.string.able_alert_window_desc),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        if(permissionListener!=null){
                            permissionListener.cancel();
                        }
                    }
                },
                context.getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        if(permissionListener!=null){
                            permissionListener.onGranted();
                        }
                    }
                },
                context.getText(R.string.open_state).toString());
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }
}
