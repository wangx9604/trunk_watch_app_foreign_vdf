package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.MidDetailDidlog;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class SuperPowerSavingActivity extends NormalAppCompatActivity {
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private ImibabyApp mApp;
    private WatchData curWatch;

    ToggleButton tb_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_power_saving);
        StatusBarUtil.changeStatusBarColor(SuperPowerSavingActivity.this,getResources().getColor(R.color.schedule_no_class));

        mApp = (ImibabyApp) getApplication();
        curWatch = mApp.getCurUser().getFocusWatch();
        initViews();
        mapGetPowerSavingStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews(){
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mapSetPowerSavingStatusOff();
                finish();
            }
        });
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.super_power_saving));

        tb_switch = findViewById(R.id.tb_switch);
        tb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    if(mApp.getCurUser().isMeAdminByWatch(curWatch)) {
                        if(isChecked){
                            showOnTipsDialog();
                        }else{
                            showPhoneTipsDialog();
                        }
                    }else{
                        ToastUtil.show(SuperPowerSavingActivity.this,getString(R.string.need_admin_auth));
                        tb_switch.setChecked(!isChecked);
                    }
                }
            }
        });
    }

    /**
     * 设置超级省电开启
     */
    private void mapSetPowerSavingStatusOn(){
        MyMsgData mapset = new MyMsgData();
        mapset.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == CloudBridgeUtil.RC_SUCCESS){
                    ToastUtil.show(SuperPowerSavingActivity.this,getString(R.string.phone_set_success));
                    mApp.setValue(curWatch.getEid() + Constants.SHARE_PREF_SUPER_POWER_SAVING,1);
                }else{
                    tb_switch.setChecked(false);
                }
            }
        });
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TEID,curWatch.getEid());
        String tgid = getMyApp().getCurUser().getFocusWatch().getFamilyId();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID,tgid);
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_SUPER_POWER_SAVING,"1");
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET,sn,getMyApp().getToken(),pl));
        if(getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()){
            getMyApp().getNetService().sendNetMsg(mapset);
        }
    }

    private void mapGetPowerSavingStatus(){
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == CloudBridgeUtil.RC_SUCCESS){
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if(pl != null && !pl.isEmpty()){
                        String onoff = (String) pl.get(CloudBridgeUtil.KEY_SUPER_POWER_SAVING);
                        tb_switch.setChecked(onoff.equals("1"));
                    }
                }
            }
        });

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(CloudBridgeUtil.KEY_SUPER_POWER_SAVING);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(getMyApp().obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()) {
            getMyApp().getNetService().sendNetMsg(mapget);
        }
    }

    private void showOnTipsDialog(){
        MidDetailDidlog dlg = new MidDetailDidlog(SuperPowerSavingActivity.this, getString(R.string.super_power_saving_dialog_tips_title),
                getString(R.string.super_power_saving_on_dialog_tips_content), getString(R.string.cancel), new MidDetailDidlog.OnBtnClick() {
            @Override
            public void onClick() {
                tb_switch.setChecked(false);
            }
        }, getString(R.string.confirm), new MidDetailDidlog.OnBtnClick() {
            @Override
            public void onClick() {
                mapSetPowerSavingStatusOn();
            }
        });
        dlg.show();
    }

    private void showPhoneTipsDialog(){
        MidDetailDidlog dlg = new MidDetailDidlog(SuperPowerSavingActivity.this, getString(R.string.super_power_saving_dialog_tips_title),
                getString(R.string.super_power_saving_off_dialog_tips_content), getString(R.string.cancel), new MidDetailDidlog.OnBtnClick() {
            @Override
            public void onClick() {
                tb_switch.setChecked(true);
            }
        }, getString(R.string.make_phonecall), new MidDetailDidlog.OnBtnClick() {
            @Override
            public void onClick() {
                if(checkPhonePermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)) {
                    //make a phone call
                    makePhoneCall(curWatch.getCellNum());
                }
            }
        });
        dlg.show();
    }

    private void makePhoneCall(String phoneNumber){
        Intent Intent =  new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(Intent);
        finish();
    }

    public boolean checkPhonePermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CALL_PERMISSION){
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                ToastUtil.show(SuperPowerSavingActivity.this,getString(R.string.super_power_saving_check_phone_permission));
            }else{
                makePhoneCall(curWatch.getCellNum());
            }
        }
    }

    //Test
    private void mapSetPowerSavingStatusOff(){
        MyMsgData mapset = new MyMsgData();
        mapset.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == CloudBridgeUtil.RC_SUCCESS){
                    ToastUtil.show(SuperPowerSavingActivity.this,getString(R.string.phone_set_success));
                    mApp.setValue(curWatch.getEid() + Constants.SHARE_PREF_SUPER_POWER_SAVING,0);
                }else{
                    tb_switch.setChecked(true);
                }
            }
        });
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TEID,curWatch.getEid());
        String tgid = getMyApp().getCurUser().getFocusWatch().getFamilyId();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID,tgid);
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_SUPER_POWER_SAVING,"0");
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mapset.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET,sn,getMyApp().getToken(),pl));
        if(getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()){
            getMyApp().getNetService().sendNetMsg(mapset);
        }
    }
}