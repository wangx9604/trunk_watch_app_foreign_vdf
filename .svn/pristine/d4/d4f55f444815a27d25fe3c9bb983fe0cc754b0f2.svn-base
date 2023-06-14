package com.xiaoxun.xun.focustime;

import static com.xiaoxun.xun.focustime.FocustimeViewModel.ACTIVITY_RESULT_CODE_ADD_OR_EDIT;
import static com.xiaoxun.xun.focustime.FocustimeViewModel.ACTIVITY_RESULT_CODE_DEL;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.PickerView;

import java.util.ArrayList;
import java.util.List;

public class FocusTimeSettingActivity extends NormalActivity {

    public FocusTimeBean compareSilenceTime;
    public FocusTimeBean silenceTime;
    public int mode = 0;//0 新增 1 编辑

    private TextView tv_repeat_now;
    private Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_time_setting);
        StatusBarUtil.setStatusBarColor(this,R.color.schedule_no_class);
        mode = getIntent().getIntExtra("mode",0);
        if(mode == 0){
            silenceTime = new FocusTimeBean("",1,"09", "00", "11",
                    "30", "0111110", "1", TimeUtil.getTimeStampLocal());
        }else{
            silenceTime = (FocusTimeBean) getIntent().getSerializableExtra("edit_item");
            compareSilenceTime = silenceTime;
        }
        initViews();
    }

    private void initViews(){
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ImageView iv_delete = findViewById(R.id.iv_delete);
        if(mode == 0 || (mode == 1 && silenceTime.getType() == 0)){
            iv_delete.setVisibility(View.INVISIBLE);
        }
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete dialog
                showDeleteDlg();
            }
        });
        initStartPickView();
        initEndPickView();
        EditText tv_setting_input_name =findViewById(R.id.tv_setting_input_name);
        if(!silenceTime.getName().equals("")){
            tv_setting_input_name.setText(silenceTime.getName());
        }
        if(silenceTime.getType() == 0){
            tv_setting_input_name.setClickable(false);
            tv_setting_input_name.setInputType(InputType.TYPE_NULL);
        }
        tv_setting_input_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_setting_input_name.setCursorVisible(true);
            }
        });
        tv_setting_input_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String name = textView.getText().toString();
                    if(!name.equals("")) {
                        tv_setting_input_name.setCursorVisible(false);
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm.isActive()){
                            imm.hideSoftInputFromWindow(FocusTimeSettingActivity.this.getCurrentFocus().getWindowToken(), 0);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        tv_setting_input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 6){
                    ToastUtil.show(FocusTimeSettingActivity.this,getString(R.string.input_word_limit));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    btn_save.setBackground(getResources().getDrawable(R.drawable.btn_normal_orange_selector));
                    btn_save.setClickable(true);
                }else{
                    btn_save.setBackground(getResources().getDrawable(R.drawable.btn_normal_disable));
                    btn_save.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_setting_input_name.getText().equals("")) { //|| tv_setting_input_name.getHint().equals(getString(R.string.refuse_disturb_name_hint)
                    ToastUtil.show(getApplicationContext(), getString(R.string.focustime_input_name_limit));
                    return;
                }else{
                    silenceTime.setName(tv_setting_input_name.getText().toString());
                }

                Intent it = new Intent();
                it.putExtra("edit_result",silenceTime);
                setResult(ACTIVITY_RESULT_CODE_ADD_OR_EDIT,it);
                finish();
            }
        });
        if(mode == 0){
            btn_save.setBackground(getResources().getDrawable(R.drawable.btn_normal_disable));
            btn_save.setClickable(false);
        }
        tv_repeat_now = findViewById(R.id.tv_repeat_now);
        tv_repeat_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FocusTimeSettingActivity.this,FocusTimeWeekSettingActivity.class);
                it.putExtra("days",silenceTime.days);
                startActivityForResult(it,1);
            }
        });
        updateWeekTextView();

        ImageView iv_arrow = findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FocusTimeSettingActivity.this,FocusTimeWeekSettingActivity.class);
                it.putExtra("days",silenceTime.days);
                startActivityForResult(it,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == 2){
                String days = data.getStringExtra("days");
                silenceTime.days = days;
                updateWeekTextView();
            }
        }
    }

    private void initStartPickView(){
        PickerView start_hour_pv = findViewById(R.id.start_hour_pv);
        PickerView start_min_pv = findViewById(R.id.start_min_pv);
        List<String> hours = new ArrayList<>();
        final List<String> mins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            mins.add(i < 10 ? "0" + i : "" + i);
        }
        start_hour_pv.setData(hours);
        start_hour_pv.setmTextSizeMeasure(6.0f);
        start_hour_pv.setMarginAlphaValue(2.6f);
        start_min_pv.setData(mins);
        start_min_pv.setmTextSizeMeasure(6.0f);
        start_min_pv.setMarginAlphaValue(2.6f);
        start_hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                silenceTime.starthour = text;
            }
        });
        start_min_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                silenceTime.startmin = text;
            }
        });
        start_hour_pv.setSelected(silenceTime.starthour);
        start_min_pv.setSelected(silenceTime.startmin);
    }
    private void initEndPickView(){
        PickerView end_hour_pv = findViewById(R.id.end_hour_pv);
        PickerView end_min_pv = findViewById(R.id.end_min_pv);
        List<String> hours = new ArrayList<>();
        final List<String> mins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            mins.add(i < 10 ? "0" + i : "" + i);
        }
        end_hour_pv.setData(hours);
        end_hour_pv.setmTextSizeMeasure(6.0f);
        end_hour_pv.setMarginAlphaValue(2.6f);
        end_min_pv.setData(mins);
        end_min_pv.setmTextSizeMeasure(6.0f);
        end_min_pv.setMarginAlphaValue(2.6f);
        end_hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                silenceTime.endhour = text;
            }
        });
        end_min_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                silenceTime.endmin = text;
            }
        });
        end_hour_pv.setSelected(silenceTime.endhour);
        end_min_pv.setSelected(silenceTime.endmin);
    }

    private void updateWeekTextView(){
        switch (silenceTime.days) {
            case "1111111":
                tv_repeat_now.setText(getText(R.string.device_alarm_reset_3));
                break;
            case "0111110":
                tv_repeat_now.setText(getText(R.string.focustime_week_work_day));
                break;
            case "1000001":
                tv_repeat_now.setText(getText(R.string.focustime_week_rest_day));
                break;
            default:
                tv_repeat_now.setText((silenceTime.days.substring(0, 1).equals("1") ? getText(R.string.week_0) + "" : "") +
                        (silenceTime.days.substring(1, 2).equals("1") ? getText(R.string.week_1) + " " : "") +
                        (silenceTime.days.substring(2, 3).equals("1") ? getText(R.string.week_2) + " " : "") +
                        (silenceTime.days.substring(3, 4).equals("1") ? getText(R.string.week_3) + " " : "") +
                        (silenceTime.days.substring(4, 5).equals("1") ? getText(R.string.week_4) + " " : "") +
                        (silenceTime.days.substring(5, 6).equals("1") ? getText(R.string.week_5) + " " : "") +
                        (silenceTime.days.substring(6, 7).equals("1") ? getText(R.string.week_6) + " " : ""));
                break;
        }
    }

    private void showDeleteDlg(){
        Dialog dlg = DialogUtil.CustomNormalDialog(FocusTimeSettingActivity.this,
                getText(R.string.device_silence_delete_title).toString(),
                getText(R.string.device_alarm_delete_message).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                },
                getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.putExtra("del_result",silenceTime);
                        setResult(ACTIVITY_RESULT_CODE_DEL,it);
                        finish();
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }
}