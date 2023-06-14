package com.xiaoxun.xun.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.R;

public class AuthAdminDialog extends Dialog {
    public interface OnDialogListener {
        void onClick(View v,AuthAdminDialog dialog);
    }

    ProgressBar pb_wait;

    public AuthAdminDialog(Context context, String title, String content, final OnDialogListener left, final OnDialogListener right) {
        super(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.auth_dialog_1, null);
        TextView title_view = (TextView) layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = (TextView) layout.findViewById(R.id.content);
        content_view.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        pb_wait = layout.findViewById(R.id.pb_wait);
        if (content != null) {
            content_view.setVisibility(View.VISIBLE);
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }

        TextView left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setText(context.getString(R.string.wechat_notice_bind_dialog_refuse));
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb_wait.setVisibility(View.VISIBLE);
                left.onClick(view,AuthAdminDialog.this);
            }
        });
        TextView right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setText(context.getString(R.string.agree));
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb_wait.setVisibility(View.VISIBLE);
                right.onClick(view,AuthAdminDialog.this);
            }
        });

        Window w = this.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        onWindowAttributesChanged(lp);
        setCancelable(false);
        setContentView(layout);
    }

    public void setProgressBarVisibility(int visibility){
        pb_wait.setVisibility(visibility);
    }

    public void setDismiss(){
        dismiss();
    }
}
