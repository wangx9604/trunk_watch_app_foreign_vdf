package com.xiaoxun.xun.health.heartratesetting;

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
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.views.PickerView;

import java.util.ArrayList;

public class limitSettingDlg extends Dialog {

    public interface BtnClick{
        void onConfirm(String time);
        void onCancel();
    }


    private BtnClick btnClick;
    private String value;

    public limitSettingDlg(@NonNull Context context,String title, int min, int max, int interval, String curSetting) {
        super(context);
        setContentView(R.layout.heartrate_limit_setting_ly);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClick.onCancel();
                dismiss();
            }
        });
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClick.onConfirm(value);
                dismiss();
            }
        });

        PickerView start_hour_pv = findViewById(R.id.start_hour_pv);
        ArrayList<String> dataValues = new ArrayList<>();
        for(int i=min;i<=max;i+=interval){
            String v = String.valueOf(i);
            dataValues.add(v);
        }
        start_hour_pv.setSelectTextColor(0xFFFF8846);
        start_hour_pv.setData(dataValues);
        start_hour_pv.setmTextSizeMeasure(6.0f);
        start_hour_pv.setMarginAlphaValue(2.6f);
        start_hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                value = text.replace(" ","");
                LogUtil.e("limitSettingDlg select value = " + value);
            }
        });
        if(curSetting != null && !curSetting.equals("")) {
            start_hour_pv.setSelected(curSetting);
            value = curSetting;
        }

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    public void setBtnClick(BtnClick click){
        btnClick = click;
    }
}
