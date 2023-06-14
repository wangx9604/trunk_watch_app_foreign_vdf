package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

public class FlowStatiticsSettingsActivity extends NormalActivity implements View.OnClickListener,MsgCallback {

    private RelativeLayout mFlowSetting0;
    private RelativeLayout mFlowSetting1;
    private RelativeLayout mFlowSetting2;
    private RelativeLayout mFlowSetting3;
    private RelativeLayout mFlowSetting4;
    private RelativeLayout mFlowSetting5;
    private RelativeLayout mFlowSetting6;
    private ImageView ivFlowSetting0;
    private ImageView ivFlowSetting1;
    private ImageView ivFlowSetting2;
    private ImageView ivFlowSetting3;
    private ImageView ivFlowSetting4;
    private ImageView ivFlowSetting5;
    private ImageView ivFlowSetting6;
    private View btnBack;

    private int mSelectItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_statitics_settings);
        String flowLimit = getIntent().getStringExtra(CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT);
        initView();
        updateCustomUi(Integer.valueOf(flowLimit));
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.flow_statistics_setting_title);
        mFlowSetting0 = findViewById(R.id.flow_setting_0);
        mFlowSetting1 = findViewById(R.id.flow_setting_1);
        mFlowSetting2 = findViewById(R.id.flow_setting_2);
        mFlowSetting3 = findViewById(R.id.flow_setting_3);
        mFlowSetting4 = findViewById(R.id.flow_setting_4);
        mFlowSetting5 = findViewById(R.id.flow_setting_5);
        mFlowSetting6 = findViewById(R.id.flow_setting_6);
        ivFlowSetting0 = findViewById(R.id.iv_setting_0);
        ivFlowSetting1 = findViewById(R.id.iv_setting_1);
        ivFlowSetting2 = findViewById(R.id.iv_setting_2);
        ivFlowSetting3 = findViewById(R.id.iv_setting_3);
        ivFlowSetting4 = findViewById(R.id.iv_setting_4);
        ivFlowSetting5 = findViewById(R.id.iv_setting_5);
        ivFlowSetting6 = findViewById(R.id.iv_setting_6);
        btnBack = findViewById(R.id.iv_title_back);

        mFlowSetting0.setOnClickListener(this);
        mFlowSetting1.setOnClickListener(this);
        mFlowSetting2.setOnClickListener(this);
        mFlowSetting3.setOnClickListener(this);
        mFlowSetting4.setOnClickListener(this);
        mFlowSetting5.setOnClickListener(this);
        mFlowSetting6.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.flow_setting_0:
                mSelectItem = 102400;
                sendMapSetFlowStatiticsData("102400");
                break;
            case R.id.flow_setting_1:
                mSelectItem = 204800;
                sendMapSetFlowStatiticsData("204800");
                break;
            case R.id.flow_setting_2:
                mSelectItem = 512000;
                sendMapSetFlowStatiticsData("512000");
                break;
            case R.id.flow_setting_3:
                mSelectItem = 1048576;
                sendMapSetFlowStatiticsData("1048576");
                break;
            case R.id.flow_setting_4:
                mSelectItem = 2097152;
                sendMapSetFlowStatiticsData("2097152");
                break;
            case R.id.flow_setting_5:
                mSelectItem = 5242880;
                sendMapSetFlowStatiticsData("5242880");
                break;
            case R.id.flow_setting_6:
                mSelectItem = -1;
                sendMapSetFlowStatiticsData("-1");
                break;
        }
        updateCustomUi(mSelectItem);
    }

    private void sendMapSetFlowStatiticsData(String flowLimit){
        if(myApp.getNetService() != null) {
            String watchEid = myApp.getCurUser().getFocusWatch().getEid();
            String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
            myApp.getNetService().sendMapSetMsg(watchEid, familyid, CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,
                    flowLimit, this);
        }
    }

    private void updateCustomUi(int mSelectItem){
        ivFlowSetting0.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting1.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting2.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting3.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting4.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting5.setBackgroundResource(R.drawable.select_2);
        ivFlowSetting6.setBackgroundResource(R.drawable.select_2);

        switch (mSelectItem){
            case 102400:
                ivFlowSetting0.setBackgroundResource(R.drawable.select_0);
                break;
            case 204800:
                ivFlowSetting1.setBackgroundResource(R.drawable.select_0);
                break;
            case 512000:
                ivFlowSetting2.setBackgroundResource(R.drawable.select_0);
                break;
            case 1048576:
                ivFlowSetting3.setBackgroundResource(R.drawable.select_0);
                break;
            case 2097152:
                ivFlowSetting4.setBackgroundResource(R.drawable.select_0);
                break;
            case 5242880:
                ivFlowSetting5.setBackgroundResource(R.drawable.select_0);
                break;
            case -1:
                ivFlowSetting6.setBackgroundResource(R.drawable.select_0);
                break;
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid){
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if(mapRc == 1){
                    myApp.setValue(myApp.getCurUser().getFocusWatch().getEid()+CloudBridgeUtil.KEY_NAME_FLOW_STATITICS_METER_LIMIT,
                            String.valueOf(mSelectItem));
                }
                break;
        }
    }
}
