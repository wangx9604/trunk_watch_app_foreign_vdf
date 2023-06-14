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

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;

public class MonitorReferenceDialog extends Dialog {
    public MonitorReferenceDialog(@NonNull Context context) {
        super(context);
        ImibabyApp mApp = (ImibabyApp) context.getApplicationContext();
        setContentView(R.layout.mid_monitor_reference_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView tv_tip1_content = findViewById(R.id.tv_tip1_content);


        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }
}
