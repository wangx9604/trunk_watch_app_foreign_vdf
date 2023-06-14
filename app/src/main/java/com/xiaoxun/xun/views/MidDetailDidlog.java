package com.xiaoxun.xun.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xiaoxun.xun.R;

public class MidDetailDidlog extends Dialog {
    public interface OnBtnClick{
        void onClick();
    }

    public MidDetailDidlog(@NonNull Context context, String title, String content, String cancelTxt, OnBtnClick cancelClick, String confirmTxt, OnBtnClick confirmClick){
        super(context);
        setContentView(R.layout.mid_dialog_layout);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView tv_content = findViewById(R.id.tv_content);
        tv_content.setText(content);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setText(cancelTxt);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClick.onClick();
                dismiss();
            }
        });
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setText(confirmTxt);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClick.onClick();
                dismiss();
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }
}
