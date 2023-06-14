package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CommonUtil;

import net.minidev.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WRemoteLossActivity extends NormalActivity implements MsgCallback {

    private boolean isRemoteLossOpen = false;
    private WatchData curWatch;
    private NetService mNetService;
    private boolean isShaking = false;

    @BindView(R.id.tv_title)
    TextView mHeadTitle;

    @BindView(R.id.tv_loss_info_4)
    TextView mGoToSmartStaff;

    @BindView(R.id.ib_remote_loss_onoff)
    ImageButton mRemoteLossOnOff;

    @OnClick(R.id.ib_remote_loss_onoff)  //点击远程挂失开关
    public void onUpdateRemoteLossState(){
        //1:防抖处理
        if(isShaking) return;
        isShaking = true;
        StartShakeTimer(); //开启抖动定时器

        //2:信息设置
        String mSetMapValue;
        if(isRemoteLossOpen) {
            isRemoteLossOpen = false;
            mSetMapValue = "0";
        }else {
            isRemoteLossOpen = true;
            mSetMapValue = "1";
        }
        //mapSet到服务器
        if (mNetService != null)
            mNetService.sendMapSetMsg(curWatch.getEid(), curWatch.getFamilyId(),
                    CloudBridgeUtil.REMOTE_LOSS, mSetMapValue, this);

    }

    private void StartShakeTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isShaking = false;
            }
        },1500);
    }

    @OnClick(R.id.iv_title_back)//返回按钮操作
    public void onBackViewClick(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_remote_loss);

        ButterKnife.bind(this);
        initBaseDate();

        initView();
        getLossStateByLoacl();
        getLossStateFromCloud();
    }

    private void initBaseDate() {
        curWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();
    }

    private void getLossStateFromCloud() {
        if (mNetService != null)
            mNetService.sendMapGetMsg(curWatch.getEid(), CloudBridgeUtil.REMOTE_LOSS, this);
    }

    private void getLossStateByLoacl() {
        //智能节假日本地保存开关状态
        String mSmartState = myApp.getStringValue(curWatch.getEid() + Constants.SHARE_PREF_REMOTE_LOSS_KEY, "");
        isRemoteLossOpen = CommonUtil.setFunctionStateByStrAndDefClose(mSmartState);
        onRefreshRemoteLossState();
    }

    private void onRefreshRemoteLossState() {
        if(isRemoteLossOpen){
            mRemoteLossOnOff.setBackgroundResource(R.drawable.switch_on);
        } else {
            mRemoteLossOnOff.setBackgroundResource(R.drawable.switch_off);
        }
    }

    private void initView() {
        mHeadTitle.setText(R.string.setting_watch_loss);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        switch(cid){
            case CloudBridgeUtil.CID_MAPSET_RESP:
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    onRefreshRemoteLossState();
                    myApp.setValue(curWatch.getEid() + Constants.SHARE_PREF_REMOTE_LOSS_KEY,
                            CommonUtil.setFunctionStateByBool(isRemoteLossOpen));
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_RESP:
                int rcMapGet = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
                    String mRemoteLoss = (String) pl.get(CloudBridgeUtil.REMOTE_LOSS);
                    isRemoteLossOpen = CommonUtil.setFunctionStateByStrAndDefClose(mRemoteLoss);
                    onRefreshRemoteLossState();
                    myApp.setValue(curWatch.getEid() + Constants.SHARE_PREF_REMOTE_LOSS_KEY,
                            CommonUtil.setFunctionStateByBool(isRemoteLossOpen));
                }
                break;
        }
    }
}
