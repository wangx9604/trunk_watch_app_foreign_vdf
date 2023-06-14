package com.xiaoxun.xun.region;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.HelpWebActivity;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

public class BindFailDialog extends Dialog {
    public interface OnBtnClick {
        void onClick();
    }

    public BindFailDialog(@NonNull Context context, OnBtnClick cancelClick, OnBtnClick confirmClick) {
        super(context);
        LogUtil.i("----BindFailDialog  init----");
        setContentView(R.layout.dialog_bind_fail);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_disagree = findViewById(R.id.btn_disagree);
        btn_disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClick.onClick();
                dismiss();
            }
        });
        Button btn_agree = findViewById(R.id.btn_agree);
        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClick.onClick();
                dismiss();
            }
        });
        this.setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        window.setAttributes(layoutParams);
    }

}
