package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.ToastUtil;

/**
 * Created by guxiaolong on 2016/2/15.
 */

public class SetDeviceNumberActivity extends NormalActivity implements View.OnClickListener {

    private String mCurWatchEid;
    private String mDeviceNumber;
    private EditText mDeviceNumberEdit;
    private Button mNextBtn;
    private ImageButton mBackBtn;
    private ImageButton mDeleteKeyword;
    private BroadcastReceiver mMsgReceiver;
    private WatchData mWatch;
    private String tempNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone_number);

        mCurWatchEid = getIntent().getExtras().getString(Const.KEY_WATCH_ID);
        mWatch = myApp.getCurUser().queryWatchDataByEid(mCurWatchEid);
        tempNumber = mWatch.getCellNum();

        initViews();
        initListener();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewShow();
    }

    private void initViews() {

        mDeviceNumberEdit = findViewById(R.id.et_phone_number);
        mDeleteKeyword = findViewById(R.id.delete_keyword);
        mNextBtn = findViewById(R.id.btn_next_step);
        mBackBtn = findViewById(R.id.iv_title_back);
    }

    private void updateViewShow() {

        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.set_device_number_title));
        mDeviceNumber = mWatch.getCellNum();
        if (mDeviceNumber != null && mDeviceNumber.length() > 0) {
            mDeviceNumber = StrUtil.formatPhoneNumber(mDeviceNumber);
            mDeviceNumberEdit.setText(mDeviceNumber);
            mDeviceNumberEdit.setSelection(mDeviceNumber.length());
        }
    }

    private void initListener() {

        mDeleteKeyword.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mDeviceNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    mDeleteKeyword.setVisibility(View.VISIBLE);
                } else {
                    mDeleteKeyword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == mNextBtn) {
            mDeviceNumber = mDeviceNumberEdit.getText().toString();
            if (!StrUtil.isMobileNumber(mDeviceNumber, 2)) {
                ToastUtil.showMyToast(SetDeviceNumberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
                return;
            }
            if (mDeviceNumber != null && mDeviceNumber.length() > 0) {
                if (!mDeviceNumber.equals(mWatch.getCellNum())) {
                    mDeviceNumber = StrUtil.formatPhoneNumber(mDeviceNumber);
                    tempNumber = mDeviceNumber;
                    myApp.getNetService().sendDevicePhoneChange(mWatch, mDeviceNumber);
                } else {
                    ToastUtil.showMyToast(SetDeviceNumberActivity.this, getString(R.string.modify_success), Toast.LENGTH_SHORT);
                    Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                    it.putExtra("eid", mCurWatchEid);
                    sendBroadcast(it);
                    finish();
                }
            } else {
                ToastUtil.showMyToast(SetDeviceNumberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
            }
        } else if (v == mBackBtn) {
            finish();
        } else if (v == mDeleteKeyword) {
            mDeviceNumberEdit.setText("");
            mDeleteKeyword.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMsgReceiver);
        tempNumber = null;
    }

    private void initReceiver() {

        mMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_RECEIVE_SET_DEVICE_INFO)) {
                    int setResult = intent.getIntExtra(Const.SETTING_RESULt, Const.SETTING_SUCCESS);
                    if (setResult == Const.SETTING_SUCCESS) {
                        mWatch.setCellNum(tempNumber);
                        WatchDAO.getInstance(getApplicationContext()).addWatch(mWatch);
                        Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                        it.putExtra("eid", mCurWatchEid);
                        sendBroadcast(it);
                        SetDeviceNumberActivity.this.finish();
                    } else if (setResult == Const.SETTING_FAIL) {
                        tempNumber = mWatch.getCellNum();
                    }
                    updateViewShow();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RECEIVE_SET_DEVICE_INFO);
        registerReceiver(mMsgReceiver, filter);
    }
}
