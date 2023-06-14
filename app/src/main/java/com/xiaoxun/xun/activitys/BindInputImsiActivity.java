package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

public class BindInputImsiActivity extends NormalActivity implements View.OnClickListener {

    private String imsi;
    private EditText editPhonenum;
    private Button btnNext;
    private ImageButton btn_back;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_input_imsi);
        initviews();
        initReceivers();
    }

    private void initviews() {
        editPhonenum = findViewById(R.id.edit_imsi);
        btnNext = findViewById(R.id.btn_next_step);
        btnNext.setOnClickListener(this);
        btn_back = findViewById(R.id.iv_title_back);
        btn_back.setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.bind_watch);
    }

    private void initReceivers() {
        // TODO Auto-generated method stub
        mReceiver = new BroadcastReceiver() {
            //wifi状态广播接收，连接变化，扫描结果等
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_BIND_RESULT_END)) {
                    finish();
                } else {

                }

            }

        };

        IntentFilter baseFilter = new IntentFilter();


        baseFilter.addAction(Const.ACTION_BIND_RESULT_END);

        registerReceiver(mReceiver, baseFilter);

    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReceivers();
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            imsi = editPhonenum.getText().toString();
            if (imsi == null || imsi.length() != 12 && imsi.length() != 6) {
                ToastUtil.showMyToast(this, getText(R.string.bind_verifycode_length_error).toString(), Toast.LENGTH_SHORT);
            } else {
                if (getMyApp().getCurUser().isWatchImsiBinded("460" + imsi)) {
                    Intent intent = new Intent();
                    intent.setClass(BindInputImsiActivity.this, BindResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intent.putExtra(Const.KEY_RESULT_CODE, 0);
                    intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_binded));
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(BindInputImsiActivity.this, BindResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Const.KEY_RESULT_CODE, 1);
                    if (imsi.length() == 12) {
                        intent.putExtra(CloudBridgeUtil.KEY_NAME_IMSI, "460" + imsi);
                    } else if (imsi.length() == 6) {
                        intent.putExtra(CloudBridgeUtil.KEY_NAME_IMSI, imsi); //传递随机验证码
                    }
                    intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_req_send));
                    startActivity(intent);
                    finish();
                }

            }
        } else if (v == btn_back) {
            finish();
        }
    }
}
