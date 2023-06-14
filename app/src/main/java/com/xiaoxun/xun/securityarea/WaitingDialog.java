package com.xiaoxun.xun.securityarea;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.R;

public class WaitingDialog extends Dialog {

    private Context mContext;
    private LinearLayout waitingView;
    private ProgressBar loadingBar;
    private ImageView exclamation;
    private TextView waitingText;
    private Boolean enableKeyBack = true;
    private int Mode = 0; //0 normal  1 NFC

    public WaitingDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_nfc_waiting_ly, null);
        waitingView = (LinearLayout) layout.findViewById(R.id.waitingView);
        loadingBar = (ProgressBar) layout.findViewById(R.id.loadingBar);
        exclamation = (ImageView) layout.findViewById(R.id.exclamation);
        waitingText = (TextView) layout.findViewById(R.id.waitingText);

        Window w = this.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        onWindowAttributesChanged(lp);
        setCanceledOnTouchOutside(false);
        setContentView(layout);
    }

    /*
     * modify by xilvkang
     * 添加back键的使能接口
     * ture：可以返回
     * false：不可以返回
     * */
    public void enableKeyBack(boolean enable) {
        enableKeyBack = enable;
    }

    public void show() {
        super.show();
        Utils.errorCode = 0;
    }

    public void normalShow(){
        super.show();
        Utils.errorCode = -100;
    }

    @Override
    public void onBackPressed() {
        Log.e("xxxx","waiting dlg backpress : enableKeyBack = " + enableKeyBack + ",error code = " + Utils.errorCode);
        if (!enableKeyBack || Utils.errorCode >= 0) {
            return;
        }
        super.onBackPressed();

    }

    public void setWaitingText(String s) {
        waitingText.setText(s);
    }
}
