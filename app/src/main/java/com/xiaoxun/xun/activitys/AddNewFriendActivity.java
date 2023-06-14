package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.xiaoxun.xun.R.id.btn_next_step;

/**
 * Created by guxiaolong on 2016/9/26.
 */

public class AddNewFriendActivity extends NormalActivity implements View.OnClickListener, MsgCallback {

    private EditText mNumberEdit;
    private TextView mAddFriendBtn;
    private Button mQRCodeBtn;
    private ImageButton mBackBtn;

    private BroadcastReceiver mBroadcastReceiver;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static int SEARCH_WATCH_LIST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);

        initView();

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if (action.equals(Const.ACTION_RECEIVE_ADD_NEW_FRIEND_RESP)) {
                    String resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    LogUtil.i("HYY resp = " + resp);
                    JSONObject  jo = (JSONObject) JSONValue.parse(resp);
                    int rc = CloudBridgeUtil.getCloudMsgRC(jo);
                    if (rc == -156) {
                        ToastUtil.show(AddNewFriendActivity.this, getString(R.string.add_watch_friend_error_deny));
                    } else if (rc == 1) {
                        ToastUtil.show(AddNewFriendActivity.this, getString(R.string.add_watch_friend_success));
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RECEIVE_ADD_NEW_FRIEND_RESP);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.add_watch_friend));
        mBackBtn = findViewById(R.id.iv_title_back);
        mBackBtn.setOnClickListener(this);
        mNumberEdit = findViewById(R.id.edit_number);
        mAddFriendBtn = findViewById(R.id.btn_add_friend);
        mAddFriendBtn.setOnClickListener(this);
        mQRCodeBtn = findViewById(btn_next_step);
        mQRCodeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        } else if (v == mAddFriendBtn) {
            String number = mNumberEdit.getText().toString();
            if (StrUtil.isMobileNumber(number, 2)) {
                Intent intent = new Intent(AddNewFriendActivity.this, SearchWatchListActivity.class);
                intent.putExtra(CloudBridgeUtil.KEY_NAME_SIM_NO, number);
                startActivityForResult(intent, SEARCH_WATCH_LIST_CODE);
                mNumberEdit.setText("");
            } else {
                ToastUtil.show(this, getString(R.string.format_error));
            }
        } else if (v == mQRCodeBtn) {
            if (ActivityCompat.checkSelfPermission(AddNewFriendActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "addFriend");
                intent.setClass(AddNewFriendActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(AddNewFriendActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    private void sendAddFriendMsg(String key, String value) {

        MyMsgData addFriendMsg = new MyMsgData();
        addFriendMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put("Fimei", myApp.getCurUser().getFocusWatch().getImei());
        pl.put(key, value);
        addFriendMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_REQ_ADD_NEW_FIEND, pl));
        LogUtil.e("sendReqJoinWatchGroupMsg :  " + addFriendMsg.getReqMsg().toJSONString());//增加log追踪验证码无效问题
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(addFriendMsg);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_REQ_ADD_NEW_FIEND_RESP:
                if (rc == -171) {
                    AddNewFriendActivity.this.sendBroadcast(new Intent(Const.ACTION_REQUEST_ADDFRIEND_OK));
                    AddNewFriendActivity.this.finish();
                } else if (rc == -160) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_offline));
                } else if (rc == -13) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_phonenum_error));
                } else if (rc == -141) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_phonenum_error));
                } else if (rc == -155) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_not_bind));
                } else if (rc == -181) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_already));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error_timeout));
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.show(this, getString(R.string.network_error_prompt));
                } else if (rc < 0) {
                    ToastUtil.show(this, getString(R.string.add_watch_friend_error));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String sn = bundle.getString("result");
                    sendAddFriendMsg(CloudBridgeUtil.KEY_NAME_QR_STR, sn);
                    ToastUtil.show(this, getString(R.string.add_watch_friend_wait));
                }
                break;
            case SEARCH_WATCH_LIST_CODE:
                if (resultCode == RESULT_OK) {
                    String imei = data.getStringExtra(CloudBridgeUtil.KEY_NAME_IMEI);
                    sendAddFriendMsg(CloudBridgeUtil.KEY_NAME_IMEI, imei);
                    ToastUtil.show(this, getString(R.string.add_watch_friend_wait));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(1 == requestCode && grantResults.length == 1){
            if(grantResults[0] == PERMISSION_GRANTED){
                Intent intent = new Intent();
                intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "addFriend");
                intent.setClass(AddNewFriendActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            } else {
                Toast.makeText(AddNewFriendActivity.this,getString(R.string.camera_premission_tips),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
