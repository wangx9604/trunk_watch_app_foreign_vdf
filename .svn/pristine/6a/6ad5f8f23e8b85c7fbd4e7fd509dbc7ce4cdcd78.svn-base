/**
 * Creation Date:2015-2-3
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.AlarmTime;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.AdapterItemClickListener;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.CustomDialogListener;
import com.xiaoxun.xun.utils.SelectTimeUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class AlarmClockAddActivity extends NormalActivity implements OnClickListener {

    private ImageButton btnCancel;
    private ImageButton btnConfirm;
    private TextView reSetDetail;
    private TextView bellDetail;
    private View itemAlarmSet;
    private View itemAlarmBells;

    private AlarmTime outTime;
    private AlarmTime inTime;
    private int iDayItemSelect = 2;
    private int iBellItemSelect = 1;
    private String sCustomerWeekSelect = "";
    private int isModifyOrNewAdd = 0;//1:modify 2:new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmclock_add);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.add_alarm_clock);

        initData();
        initViews();
        initListener();
        refreshResetDetailView(iDayItemSelect);
        setBellDetail(iBellItemSelect);
    }

    private void initViews() {

        btnCancel = findViewById(R.id.iv_title_back);
        btnCancel.setBackground(this.getResources().getDrawable(R.drawable.btn_cancel_selector));
        btnConfirm = findViewById(R.id.iv_title_menu);
        btnConfirm.setVisibility(View.VISIBLE);
        btnConfirm.setBackground(this.getResources().getDrawable(R.drawable.btn_confirm_selector));
        itemAlarmSet = findViewById(R.id.alarm_reset);
        itemAlarmBells = findViewById(R.id.alarm_bells);
        reSetDetail = findViewById(R.id.tv_alarm_reset_detail);
        bellDetail = findViewById(R.id.tv_alarm_bells_detail);

        if (!myApp.getCurUser().getFocusWatch().needSetAlarmBell()){
            itemAlarmBells.setVisibility(View.GONE);
            findViewById(R.id.alarm_bells_divider).setVisibility(View.GONE);
        }
    }

    private void initData() {

        outTime = new AlarmTime();
        inTime = new AlarmTime();

        Intent intent = this.getIntent();
        if (intent.getSerializableExtra(Const.KEY_MAP_ALARMOBJECT) != null) {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.modify_alarm_clock);
            inTime= (AlarmTime) intent.getSerializableExtra(Const.KEY_MAP_ALARMOBJECT);
            isModifyOrNewAdd = 1;
        } else {
            inTime = new AlarmTime("07", "20", "1,1,1,1,1,1,0,0", "1", TimeUtil.getTimeStampLocal(), "2", "1");
            isModifyOrNewAdd = 2;
        }
        try {
            outTime= (AlarmTime) inTime.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (outTime.bell == null) {
            iBellItemSelect = 1;
        } else {
            iBellItemSelect = Integer.parseInt(outTime.bell);
        }
        iDayItemSelect = Integer.parseInt(outTime.select);
        sCustomerWeekSelect = outTime.days.substring(2, 15);
    }

    private void initListener() {

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        itemAlarmBells.setOnClickListener(this);
        itemAlarmSet.setOnClickListener(this);

        SelectTimeUtils.openSelectTimeView(this, outTime.hour, outTime.min, 0,
                new CustomerPickerView.onSelectListener() {
                    @Override
                    public void onSelect(String text) {
                        outTime.hour = text;
                    }
                },
                new CustomerPickerView.onSelectListener() {
                    @Override
                    public void onSelect(String text) {
                        outTime.min = text;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (btnCancel == v) {

            finish();
        } else if (btnConfirm == v) {

            if (iDayItemSelect == 4 && (sCustomerWeekSelect.equals("0,0,0,0,0,0,0") || sCustomerWeekSelect.equals(""))) {
                ToastUtil.showMyToast(AlarmClockAddActivity.this,
                        getString(R.string.set_silence_time_error1),
                        Toast.LENGTH_SHORT);
                return;
            }
            outTime.days = "";
            initOutTime();
            Intent intent = new Intent();
            intent.putExtra(Const.KEY_MAP_ALARMOBJECT,outTime);
            if (isModifyOrNewAdd == 2) {
                setResult(2, intent);
            } else if (isModifyOrNewAdd == 1) {
                if (checkModifyAlarmTimeIsReallyChange()) {
                    setResult(1, intent);
                }
            }
            finish();
        } else if (itemAlarmSet == v) {

            ArrayList<String> itemList = new ArrayList<>();
            itemList.add(getText(R.string.device_alarm_reset_1).toString());
            itemList.add(getText(R.string.device_alarm_reset_2).toString());
            itemList.add(getText(R.string.device_alarm_reset_3).toString());
            itemList.add(getText(R.string.device_lesson_custom).toString());
            Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialog(AlarmClockAddActivity.this, itemList,
                    new AdapterItemClickListener() {
                        @Override
                        public void onClick(View v, int position) {
                            sCustomerWeekSelectSetting(position);
                            if (position != 4) {
                                iDayItemSelect = position;
                                selectClick(iDayItemSelect);
                            } else {
                                selectClick(4);
                            }

                        }
                    },
                    iDayItemSelect);
            dlg.show();
        } else if (itemAlarmBells == v) {

            Intent intent = new Intent(AlarmClockAddActivity.this, AlarmClockBellActivity.class);
            intent.putExtra(Const.KEY_EXTRA_BELLSELECT, iBellItemSelect);
            startActivityForResult(intent, 3);
        }
    }

    private void selectClick(int select) {

        if (select == 1 || select == 2 || select == 3) {
            refreshResetDetailView(select);
        } else if (select == 4) {
            ArrayList<String> itemList = new ArrayList<>();
            itemList.add(getText(R.string.week_1).toString());
            itemList.add(getText(R.string.week_2).toString());
            itemList.add(getText(R.string.week_3).toString());
            itemList.add(getText(R.string.week_4).toString());
            itemList.add(getText(R.string.week_5).toString());
            itemList.add(getText(R.string.week_6).toString());
            itemList.add(getText(R.string.week_0).toString());
            Dialog dlg = CustomSelectDialogUtil.CustomItemMultSelectDialog(AlarmClockAddActivity.this, itemList,
                    getText(R.string.device_alarm_reset).toString(),
                    new CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {

                        }
                    },
                    getText(R.string.cancel).toString(),
                    new CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            if (!text.equals("0,0,0,0,0,0,0")) {
                                sCustomerWeekSelect = text;
                                refreshResetDetailView(4);
                                iDayItemSelect = 4;
                            } else {
                                ToastUtil.show(AlarmClockAddActivity.this, getResources().getString(R.string.invalid_setting));
                            }

                        }
                    },
                    getText(R.string.confirm).toString(), sCustomerWeekSelect.replace(",", ""));
            dlg.show();
        }
    }

    private void initOutTime() {

        if (iDayItemSelect == 1) {
            outTime.days = "0,0,0,0,0,0,0,0";
        } else if (iDayItemSelect == 2) {
            outTime.days = "1,1,1,1,1,1,0,0";
        } else if (iDayItemSelect == 3) {
            outTime.days = "1,1,1,1,1,1,1,1";
        } else if (iDayItemSelect == 4) {
            if ("1,1,1,1,1,0,0".equals(sCustomerWeekSelect))
                iDayItemSelect = 2;
            else if ("1,1,1,1,1,1,1".equals(sCustomerWeekSelect))
                iDayItemSelect = 3;
            outTime.days = "1" + "," + sCustomerWeekSelect;
        }
        outTime.select = Integer.toString(iDayItemSelect);
        outTime.bell = Integer.toString(iBellItemSelect);
        outTime.timeStampId = TimeUtil.getTimeStampLocal();
    }

    private void refreshResetDetailView(int id) {

        if (id < 0) {
            ToastUtil.showMyToast(AlarmClockAddActivity.this,
                    "No Radio Button is selected",
                    Toast.LENGTH_SHORT);
            return;
        }
        iDayItemSelect = id;
        if (id == 1) {
            reSetDetail.setText(R.string.device_alarm_reset_1);
        } else if (id == 2) {
            reSetDetail.setText(R.string.device_alarm_reset_2);
        } else if (id == 3) {
            reSetDetail.setText(R.string.device_alarm_reset_3);
        } else if (id == 4) {
            if ("1,1,1,1,1,0,0".equals(sCustomerWeekSelect))
                reSetDetail.setText(R.string.device_alarm_reset_2);
            else if ("1,1,1,1,1,1,1".equals(sCustomerWeekSelect))
                reSetDetail.setText(R.string.device_alarm_reset_3);
            else
                reSetDetail.setText(getResources().getString(R.string.device_lesson_custom) + ":"
                        + (sCustomerWeekSelect.substring(0, 1).equals("1") ? " " + getText(R.string.week_1) : "")
                        + (sCustomerWeekSelect.substring(2, 3).equals("1") ? " " + getText(R.string.week_2) : "")
                        + (sCustomerWeekSelect.substring(4, 5).equals("1") ? " " + getText(R.string.week_3) : "")
                        + (sCustomerWeekSelect.substring(6, 7).equals("1") ? " " + getText(R.string.week_4) : "")
                        + (sCustomerWeekSelect.substring(8, 9).equals("1") ? " " + getText(R.string.week_5) : "")
                        + (sCustomerWeekSelect.substring(10, 11).equals("1") ? " " + getText(R.string.week_6) : "")
                        + (sCustomerWeekSelect.substring(12, 13).equals("1") ? " " + getText(R.string.week_0) : ""));
        }
    }

    private void setBellDetail(int id) {
        if (id < 0) {
            ToastUtil.showMyToast(AlarmClockAddActivity.this,
                    "No bell selected",
                    Toast.LENGTH_SHORT);
            return;
        }
        iBellItemSelect = id;
        if (id == 1) {
            bellDetail.setText(R.string.alarm_bell_1);
        } else if (id == 2) {
            bellDetail.setText(R.string.alarm_bell_2);
        } else if (id == 3) {
            bellDetail.setText(R.string.alarm_bell_3);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 3 && 3 == requestCode) {
            iBellItemSelect = data.getIntExtra(Const.KEY_EXTRA_BELLSELECT,1);
            setBellDetail(iBellItemSelect);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sCustomerWeekSelectSetting(int select) {

        if (select == 1) {
            sCustomerWeekSelect = "0,0,0,0,0,0,0";
        } else if (select == 2) {
            sCustomerWeekSelect = "1,1,1,1,1,0,0";
        } else if (select == 3) {
            sCustomerWeekSelect = "1,1,1,1,1,1,1";
        }
    }

    private boolean checkModifyAlarmTimeIsReallyChange() {

        if (isModifyOrNewAdd == 1) {
            return !outTime.hour.equals(inTime.hour) || !outTime.min.equals(inTime.min)
                    || !outTime.days.equals(inTime.days) || !outTime.select.equals(inTime.select)
                    || !outTime.bell.equals(inTime.bell);
        }
        return true;
    }
}



