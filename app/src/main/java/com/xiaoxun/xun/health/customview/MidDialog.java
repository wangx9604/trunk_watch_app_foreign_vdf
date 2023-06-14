package com.xiaoxun.xun.health.customview;

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

public class MidDialog extends Dialog {
    public interface OnBtnClick{
        void onClick();
    }

    public MidDialog(@NonNull Context context, String title, String content, OnBtnClick cancelClick, OnBtnClick confirmClick) {
        super(context);
        setContentView(R.layout.mid_monitor_dialog_tip_delay);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelClick.onClick();
                dismiss();
            }
        });
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmClick.onClick();
                dismiss();
            }
        });
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView tv_content = findViewById(R.id.tv_content);
        tv_content.setText(content);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }
}
