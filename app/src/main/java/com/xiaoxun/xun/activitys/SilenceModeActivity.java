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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.SilenceTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.CustomDialogListener;
import com.xiaoxun.xun.utils.SelectTimeUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import java.util.ArrayList;


@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class SilenceModeActivity extends NormalActivity implements OnClickListener {

    private View itemWeekSelect;
    private View silenceStartTimeLayout;
    private View silenceEndTimeLayout;
    private ImageButton btnOk;
    private ImageButton btnCancel;
    private TextView silenceWeekDetail;
    private TextView silenceStartimeDetail;
    private TextView silenceEndtimeDetail;
    private ImageView imgSelectedStart;
    private ImageView imgSelectedEnd;
    private TextView tvStartTimeTag;
    private TextView tvEndTimeTag;
    private View silenceAdvanceOp;
    private ImageView optSelect;

    private SilenceTime outTime;
    private SilenceTime inTime;
    private String weeks = "";
    private int isModifyOrNewAdd = 0;  // 1 Modify the silence time ,  2 new add the silence time

    private WatchData focusWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silence_mode);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.setting_watch_silence);
        focusWatch = myApp.getCurUser().getFocusWatch();

        initData();
        initViews();
        initListener();
        refreshSettingState();
    }

    private void initData() {

        inTime = new SilenceTime();
        outTime = new SilenceTime();

        Intent intent = this.getIntent();
        if (intent.getSerializableExtra(Const.KEY_MAP_SILENCEOBJECT) != null) {
            inTime = (SilenceTime) intent.getSerializableExtra(Const.KEY_MAP_SILENCEOBJECT);
            isModifyOrNewAdd = 1;
        } else {
            inTime = new SilenceTime("08", "20", "16", "30", "0000000", "1", TimeUtil.getTimeStampLocal(), 1, 0);
            isModifyOrNewAdd = 2;
        }
        try {
            outTime = (SilenceTime) inTime.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        btnCancel = findViewById(R.id.iv_title_back);
        btnCancel.setBackground(this.getResources().getDrawable(R.drawable.btn_cancel_selector));
        btnOk = findViewById(R.id.iv_title_menu);
        btnOk.setBackground(this.getResources().getDrawable(R.drawable.btn_confirm_selector));
        btnOk.setVisibility(View.VISIBLE);

        itemWeekSelect = findViewById(R.id.silence_week_view);
        silenceStartTimeLayout = findViewById(R.id.silence_start_time_view);
        silenceEndTimeLayout = findViewById(R.id.silence_end_time_view);
        silenceWeekDetail = findViewById(R.id.silence_week_detail);
        silenceStartimeDetail = findViewById(R.id.silence_startime_title_detail);
        silenceEndtimeDetail = findViewById(R.id.silence_endtime_title_detail);
        imgSelectedStart = findViewById(R.id.iv_select_start);
        imgSelectedEnd = findViewById(R.id.iv_select_end);
        tvStartTimeTag = findViewById(R.id.silence_startime_title);
        tvEndTimeTag = findViewById(R.id.silence_endtime_title);
        silenceAdvanceOp = findViewById(R.id.silence_advance_op);
        optSelect = findViewById(R.id.opt_select);
    }

    private void initListener() {

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        itemWeekSelect.setOnClickListener(this);
        silenceStartTimeLayout.setOnClickListener(this);
        silenceEndTimeLayout.setOnClickListener(this);
        silenceAdvanceOp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 20170216修改：不再做类型判断，统一隐去深度防打扰。设置的时候，advanceop字段强制置为1
        // 20180714修改：710/730添加深度防打扰（离线模式）功能。advanceop字段为2表示离线模式深度防打扰
        boolean isHaveAdvance = (focusWatch.isDevice710() && !focusWatch.isDevice730() && myApp.isControledByVersion(focusWatch, false, "T31"))
                || (focusWatch.isDevice701() && myApp.isControledByVersion(focusWatch, false, "T42"))
                || (focusWatch.isDevice730() && myApp.isControledByVersion(focusWatch, false, "T34"));
        silenceAdvanceOp.setVisibility(isHaveAdvance?View.VISIBLE:View.GONE);
        findViewById(R.id.silence_advance_op_divider).setVisibility(isHaveAdvance?View.VISIBLE:View.GONE);
    }

    @Override
    public void onClick(View v) {

        if (btnCancel == v) {
            finish();
        } else if (btnOk == v) {
            if (checkOutputSilenceTime()) {
                ToastUtil.showMyToast(SilenceModeActivity.this,
                        getString(R.string.set_silence_time_error2),
                        Toast.LENGTH_SHORT);
                return;
            }
            if (isModifyOrNewAdd == 2 || isModifyOrNewAdd == 1) {
                if (outTime.days.equals("") || outTime.days.equals("0000000")) {
                    ToastUtil.show(SilenceModeActivity.this, getString(R.string.set_silence_time_error1));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Const.KEY_MAP_SILENCEOBJECT, outTime);
                if (isModifyOrNewAdd == 2) {  // add new silence time
                    setResult(2, intent);
                } else if (isModifyOrNewAdd == 1) {  //modify the silence time
                    if (!checkModifySilenceTimeIsReallyChange()) {  //检查有没有更改
                        finish();
                        return;
                    }
                    setResult(1, intent);
                }
            }
            finish();
        } else if (v == silenceAdvanceOp) {
            if (focusWatch.isDevice102() && !myApp.isControledByVersion(focusWatch, false, "T24")) {
                ToastUtil.show(myApp, getString(R.string.not_support_steps));
            } else {
                if (outTime.advanceopt == 0) {
                    outTime.advanceopt = 2;
                    optSelect.setBackground(getResources().getDrawable(R.drawable.radio_bt_1));
                } else {
                    outTime.advanceopt = 0;
                    optSelect.setBackground(getResources().getDrawable(R.drawable.radio_bt_0));
                }
            }
        } else if (itemWeekSelect == v) {

            openSelectWeekDialog();
        } else if (silenceStartTimeLayout == v) {

            selectTimetype(1);
            SelectTimeUtils.openSelectTimeView(this, outTime.starthour, outTime.startmin, 1,
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.starthour = text;
                            silenceStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
                        }
                    },
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.startmin = text;
                            silenceStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
                        }
                    });
        } else if (silenceEndTimeLayout == v) {

            selectTimetype(2);
            SelectTimeUtils.openSelectTimeView(this, outTime.endhour, outTime.endmin, 2,
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.endhour = text;
                            silenceEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);
                        }
                    },
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.endmin = text;
                            silenceEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);
                        }
                    });
        }
    }

    private void refreshSettingState() {

        silenceStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
        silenceEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);

        if (isModifyOrNewAdd == 1) {
            switch (outTime.days) {
                case "1111111":
                    silenceWeekDetail.setText(getText(R.string.device_alarm_reset_3));
                    break;
                case "1111100":
                    silenceWeekDetail.setText(getText(R.string.device_alarm_reset_2));
                    break;
                default:
                    silenceWeekDetail.setText((inTime.days.substring(0, 1).equals("1") ? getText(R.string.week_1) + "" : "") +
                            (inTime.days.substring(1, 2).equals("1") ? getText(R.string.week_2) + " " : "") +
                            (inTime.days.substring(2, 3).equals("1") ? getText(R.string.week_3) + " " : "") +
                            (inTime.days.substring(3, 4).equals("1") ? getText(R.string.week_4) + " " : "") +
                            (inTime.days.substring(4, 5).equals("1") ? getText(R.string.week_5) + " " : "") +
                            (inTime.days.substring(5, 6).equals("1") ? getText(R.string.week_6) + " " : "") +
                            (inTime.days.substring(6, 7).equals("1") ? getText(R.string.week_0) + " " : ""));
                    break;
            }
        }

        if (outTime.advanceopt == 2) {
            optSelect.setBackground(getResources().getDrawable(R.drawable.radio_bt_1));
        } else {
            optSelect.setBackground(getResources().getDrawable(R.drawable.radio_bt_0));
        }
    }

    private void selectTimetype(int selected) {
        if (selected == 1) {
            imgSelectedStart.setVisibility(View.VISIBLE);
            tvStartTimeTag.setTextColor(getResources().getColor(R.color.bg_color_orange));
            imgSelectedEnd.setVisibility(View.INVISIBLE);
            tvEndTimeTag.setTextColor(getResources().getColor(R.color.black));
        } else if (selected == 2) {
            imgSelectedStart.setVisibility(View.INVISIBLE);
            tvStartTimeTag.setTextColor(getResources().getColor(R.color.black));
            imgSelectedEnd.setVisibility(View.VISIBLE);
            tvEndTimeTag.setTextColor(getResources().getColor(R.color.bg_color_orange));
        }
    }

    private void openSelectWeekDialog() {

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(getText(R.string.week_1).toString());
        itemList.add(getText(R.string.week_2).toString());
        itemList.add(getText(R.string.week_3).toString());
        itemList.add(getText(R.string.week_4).toString());
        itemList.add(getText(R.string.week_5).toString());
        itemList.add(getText(R.string.week_6).toString());
        itemList.add(getText(R.string.week_0).toString());
        if (weeks.equals(""))
            weeks = outTime.days;
        Dialog dlg = CustomSelectDialogUtil.CustomItemMultSelectDialogSilence(SilenceModeActivity.this, itemList, getText(R.string.device_alarm_reset).toString(),
                weeks,
                new CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                },
                getText(R.string.cancel).toString(),
                new CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                        outTime.days = text.replace(",","");
                        switch (outTime.days) {
                            case "1111111":
                                silenceWeekDetail.setText(getText(R.string.device_alarm_reset_3));
                                break;
                            case "1111100":
                                silenceWeekDetail.setText(getText(R.string.device_alarm_reset_2));
                                break;
                            case "0000000":
                                silenceWeekDetail.setText(getText(R.string.silence_week_click_select));
                                break;
                            default:
                                silenceWeekDetail.setText((outTime.days.substring(0, 1).equals("1") ? " " + getText(R.string.week_1) : "")
                                        + (outTime.days.substring(1, 2).equals("1") ? " " + getText(R.string.week_2) : "")
                                        + (outTime.days.substring(2, 3).equals("1") ? " " + getText(R.string.week_3) : "")
                                        + (outTime.days.substring(3, 4).equals("1") ? " " + getText(R.string.week_4) : "")
                                        + (outTime.days.substring(4, 5).equals("1") ? " " + getText(R.string.week_5) : "")
                                        + (outTime.days.substring(5, 6).equals("1") ? " " + getText(R.string.week_6) : "")
                                        + (outTime.days.substring(6, 7).equals("1") ? " " + getText(R.string.week_0) : ""));
                                break;
                        }
                        weeks = outTime.days;
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private boolean checkOutputSilenceTime() {
        //结束时间不能等于开始时间
        int startTime = Integer.valueOf(outTime.starthour) * 60 + Integer.valueOf(outTime.startmin);
        int endTime = Integer.valueOf(outTime.endhour) * 60 + Integer.valueOf(outTime.endmin);
        return startTime == endTime;
    }

    private boolean checkModifySilenceTimeIsReallyChange() {
        if (isModifyOrNewAdd == 1) {
            return !outTime.starthour.equals(inTime.starthour) || !outTime.startmin.equals(inTime.startmin)
                    || !outTime.endhour.equals(inTime.endhour) || !outTime.endmin.equals(inTime.endmin)
                    || !outTime.days.equals(inTime.days) || outTime.advanceopt != inTime.advanceopt;
        }
        return true;
    }
}
