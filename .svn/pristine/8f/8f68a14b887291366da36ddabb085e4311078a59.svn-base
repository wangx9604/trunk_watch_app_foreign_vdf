package com.xiaoxun.xun.health.monitor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.views.PickerView;

import java.util.ArrayList;
import java.util.List;

public class FatigueTimeSettingDlg extends Dialog {

    public interface BtnClick{
        void onConfirm(String time);
        void onCancel();
    }

    private BtnClick btnClick;
    private String time;

    public FatigueTimeSettingDlg(@NonNull Context context,List<MonitorTimeBean> list) {
        super(context);
        setContentView(R.layout.bottom_time_setting_ly);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                btnClick.onConfirm(time);
                dismiss();
            }
        });

        PickerView start_hour_pv = findViewById(R.id.start_hour_pv);
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hour = String.valueOf(i);
            String min = ":00";
            if(!isListContainTime(hour+min,list)){
                hours.add(hour + min);
            }
        }
        start_hour_pv.setData(hours);
        start_hour_pv.setmTextSizeMeasure(8.0f);
        start_hour_pv.setMarginAlphaValue(2.6f);
        start_hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                time = text;
            }
        });
        start_hour_pv.setSelected("09:00");
        time = "09:00";

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    public void setBtnClick(BtnClick click){
        btnClick = click;
    }

    private boolean isListContainTime(String time,List<MonitorTimeBean> list){
        for(int i=0;i<list.size();i++){
            MonitorTimeBean bean = list.get(i);
            String timeC = bean.starthour + ":" + bean.startmin;
            if(timeC.equals(time)){
                return true;
            }
        }
        return false;
    }
}
