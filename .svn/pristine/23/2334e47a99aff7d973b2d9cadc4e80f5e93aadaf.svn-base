/**
 * Creation Date:2015-3-12
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.SleepTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.SelectTimeUtils;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Description Of The Class<br>
 *
 * @author fushiqing
 * @version 1.000, 2015-8-12
 */
public class SleepTimeActivity extends NormalActivity implements OnClickListener, MsgCallback {

    private TextView tvSleeptimeDesc;
    private WatchData curWatch;
    private NetService mNetService;
    private ImageButton button_img_sleep;
    private TextView time_sleep_setting;

    //休眠时段
    private ImageButton btnOk;
    private boolean isbtnok;
    private ImageButton btnCancel;
    private View itemStartTimeSelect;
    private View itemEndTimeSelect;
    private TextView sleepStartimeDetail;
    private TextView sleepEndtimeDetail;
    private ImageView imgSelectedStart;
    private ImageView imgSelectedEnd;
    private TextView tvStartTimeTag;
    private TextView tvEndTimeTag;
    private RelativeLayout layoutIfPowerOff;
    private ImageButton btnIfPowerOff;
    private SleepTime outTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_setting);
        ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.setting_watch_sleep));
        curWatch = ((ImibabyApp) getApplication()).getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();
        Intent intent = this.getIntent();
        if (intent.getSerializableExtra(Const.KEY_MAP_SLEEPOBJECT) != null) {
            outTime = (SleepTime) intent.getSerializableExtra(Const.KEY_MAP_SLEEPOBJECT);
        }
        initView();
        if (outTime != null) {
            initData();
        } else {
            getListItemFromCloudBridge();
        }
        initListener();
    }

    private void initView() {
        tvSleeptimeDesc = findViewById(R.id.tv_sleeptime_desc);
        time_sleep_setting = findViewById(R.id.time_sleep_setting);
        button_img_sleep = findViewById(R.id.button_img_sleep);

        //休眠时段
        btnCancel = findViewById(R.id.iv_title_back);
        btnCancel.setBackground(this.getResources().getDrawable(R.drawable.btn_cancel_selector));
        btnCancel.setOnClickListener(this);
        btnOk = findViewById(R.id.iv_title_menu);
        btnOk.setBackground(this.getResources().getDrawable(R.drawable.btn_confirm_selector));
        btnOk.setVisibility(View.VISIBLE);

        itemStartTimeSelect = findViewById(R.id.sleep_start_time_view);
        itemEndTimeSelect = findViewById(R.id.sleep_end_time_view);
        sleepStartimeDetail = findViewById(R.id.sleep_startime_title_detail);
        sleepEndtimeDetail = findViewById(R.id.sleep_endtime_title_detail);
        imgSelectedStart = findViewById(R.id.iv_select_start);
        imgSelectedEnd = findViewById(R.id.iv_select_end);
        tvStartTimeTag = findViewById(R.id.sleep_startime_title);
        tvEndTimeTag = findViewById(R.id.sleep_endtime_title);

        layoutIfPowerOff = findViewById(R.id.layout_if_poweroff);
        btnIfPowerOff = findViewById(R.id.btn_if_poweroff);

        WatchData focusWatch = myApp.getCurUser().getFocusWatch();
        if (focusWatch.isDevice710() || focusWatch.isDevice703()
                || focusWatch.isDevice705() || focusWatch.isDevice706_A02() || focusWatch.isDevice900_A03() || focusWatch.isDevice306_A03()) {
            layoutIfPowerOff.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_watch_white_list_selector).setVisibility(View.VISIBLE);
        } else {
            layoutIfPowerOff.setVisibility(View.GONE);
            findViewById(R.id.iv_watch_white_list_selector).setVisibility(View.GONE);
        }
    }

    private void initData() {

        if (outTime.onoff.equals("1")) {
            button_img_sleep.setBackgroundResource(R.drawable.switch_on);
        } else if (outTime.onoff.equals("0")) {
            button_img_sleep.setBackgroundResource(R.drawable.switch_off);
        }

        sleepStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
        sleepEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);
        if (outTime.type.equals("1")) {
            btnIfPowerOff.setBackgroundResource(R.drawable.switch_on);
        } else if (outTime.type.equals("0")) {
            btnIfPowerOff.setBackgroundResource(R.drawable.switch_off);
        }

        if (curWatch.isWatch()) {
            tvSleeptimeDesc.setText(R.string.device_sleep_message_detail);
        } else {
            tvSleeptimeDesc.setText(R.string.device_sleep_message_detail_203);
        }
    }

    private void initListener() {
        //休眠时段
        itemStartTimeSelect.setOnClickListener(this);
        itemEndTimeSelect.setOnClickListener(this);
        btnIfPowerOff.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        button_img_sleep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outTime.onoff.equals("1")) {
                    outTime.onoff = "0";
                    button_img_sleep.setBackgroundResource(R.drawable.switch_off);
                } else {
                    outTime.onoff = "1";
                    button_img_sleep.setBackgroundResource(R.drawable.switch_on);
                }
            }
        });
    }

    private void getListItemFromCloudBridge() {
        if (mNetService != null)
            mNetService.sendMapGetMsg(curWatch.getEid(), CloudBridgeUtil.SLEEP_LIST,
                    SleepTimeActivity.this);////CID_MAPGET_RESP
    }

    //mapset存入服务器
    private void mapSetSleepListMsg(SleepTime outTime) {
        JSONObject plO = new JSONObject();
        plO = SleepTime.toJsonObjectFromSleepTimeBean(plO, outTime);
        plO.put(CloudBridgeUtil.ONOFF, outTime.onoff);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendMapSetMsg(curWatch.getEid(), curWatch.getFamilyId(), sn,
                    CloudBridgeUtil.SLEEP_LIST, plO.toString(), SleepTimeActivity.this);
        //CID_MAPSET_RESP
    }

    @Override
    public void onClick(View v) {
        if (btnCancel == v) {
            finish();
        } else if (btnOk == v) {
            isbtnok = true;
            if (outTime != null) {
                mapSetSleepListMsg(outTime);
            }
        } else if (itemStartTimeSelect == v) {
            selectTimetype(1);
            SelectTimeUtils.openSelectTimeViewTwo(this, outTime.starthour, outTime.startmin, 1,
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.starthour = text;
                            sleepStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
                        }
                    },
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.startmin = text;
                            sleepStartimeDetail.setText(outTime.starthour + ":" + outTime.startmin);
                        }
                    });
        } else if (itemEndTimeSelect == v) {
            selectTimetype(2);
            SelectTimeUtils.openSelectTimeViewTwo(this, outTime.endhour, outTime.endmin, 2,
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.endhour = text;
                            sleepEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);
                        }
                    },
                    new CustomerPickerView.onSelectListener() {
                        @Override
                        public void onSelect(String text) {
                            outTime.endmin = text;
                            sleepEndtimeDetail.setText(outTime.endhour + ":" + outTime.endmin);
                        }
                    });
        } else if (btnIfPowerOff == v) {
            if (outTime != null && !TextUtils.isEmpty(outTime.type)) {
                if (outTime.type.equals("1")) {
                    outTime.type = "0";
                    btnIfPowerOff.setBackgroundResource(R.drawable.switch_off);
                } else if (outTime.type.equals("0")) {
                    outTime.type = "1";
                    btnIfPowerOff.setBackgroundResource(R.drawable.switch_on);
                }
            }
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc > 0) {
                    if (isbtnok) {
                        finish();
                    }
                    //存入成功
                    initData();
                    saveListItemToLocal();
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this, getString(R.string.phone_set_timeout),
                            Toast.LENGTH_SHORT);
                    button_img_sleep.setBackgroundResource(R.drawable.switch_off);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt),
                            Toast.LENGTH_SHORT);
                    button_img_sleep.setBackgroundResource(R.drawable.switch_off);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error), Toast.LENGTH_SHORT);
                    button_img_sleep.setBackgroundResource(R.drawable.switch_off);
                } else {
                    button_img_sleep.setBackgroundResource(R.drawable.switch_off);
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_RESP:
                int rcMapGet = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
                    String sTmp = (String) pl.get(CloudBridgeUtil.SLEEP_LIST);
                    if (sTmp != null && !sTmp.equals("[]")) {
                        Object obj = JSONValue.parse(sTmp);
                        JSONObject jsonObj = (JSONObject) obj;
                        outTime = new SleepTime();
                        assert jsonObj != null;
                        outTime = SleepTime.toBeSleepTimeBean(outTime, jsonObj);
                        initData();
                        saveListItemToLocal();
                    } else {
                        saveListItemToLocalFirstTime();
                    }
                } else if (rcMapGet == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    saveListItemToLocalFirstTime();
                } else if (rcMapGet == CloudBridgeUtil.RC_SOCKET_NOTREADY || rcMapGet == CloudBridgeUtil.RC_NETERROR) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error8), Toast.LENGTH_SHORT);
                    saveListItemToLocalFirstTime();
                } else {
                    saveListItemToLocalFirstTime();
                }
                break;
            default:
                break;
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

    private void saveListItemToLocalFirstTime() {
        String starthour = "00";
        String endhour = "07";
        String type = "1";
        if (curWatch.isDevice102() || curWatch.isDevice105()) {
            starthour = "00";
        } else if (curWatch.isDevice302() || curWatch.isDevice501() || curWatch.isDevice709_A05() || curWatch.isDevice708_A07()|| curWatch.isDevice707_H01() || curWatch.isDevice709_H01()) {
            starthour = "22";
        } else if (curWatch.isDevice502() || curWatch.isDevice303() || curWatch.isDevice303_A02()
                || curWatch.isDevice305() || curWatch.isDevice701() || curWatch.isDevice710()
                || curWatch.isDevice705() || curWatch.isDevice307() || curWatch.isDevice703()
                || curWatch.isDevice706_A02() || curWatch.isDevice900_A03()) {
            starthour = "21";
        }
        if (curWatch.isDevice701()) {
            outTime = new SleepTime(starthour, "00", endhour, "00", "1", "0",
                    TimeUtil.getTimeStampLocal());
        } else {
            outTime = new SleepTime(starthour, "00", endhour, "00", "1", type,
                    TimeUtil.getTimeStampLocal());
        }
        initData();
        saveListItemToLocal();
    }

    private void saveListItemToLocal() {
        JSONArray plA = new JSONArray();
        JSONObject plO = new JSONObject();
        plO = SleepTime.toJsonObjectFromSleepTimeBean(plO, outTime);
        plA.add(plO);
        myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_SLEEP_MODE_KEY, plA.toJSONString());
    }
}
