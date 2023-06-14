package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.views.CustomSettingView;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class DeviceRepairActivity extends NormalActivity implements View.OnClickListener{

    private CustomSettingView repairSettingCommit;
    private CustomSettingView repairSettingQuery;
    private CustomSettingView repairSettingPolicy;
    private CustomSettingView repairSettingShop;
    private ImibabyApp myApp;
    private WatchData curWatch;

    private String bdate;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_repair);
        myApp = (ImibabyApp)getApplication();
        curWatch = myApp.getCurUser().getFocusWatch();
        initView();
        getUserDate();
    }

    private String getEncryptData(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eid",myApp.getCurUser().getFocusWatch().getEid());
        jsonObject.put("appPackage",this.getPackageName());
        jsonObject.put("type", CloudBridgeUtil.VALUE_TYPE_APP_ANDROID);
        return jsonObject.toJSONString();
    }

    private void getUserDate(){
        new MioAsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String getAesData = getEncryptData();
                    String encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(getAesData, myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY())) + myApp.getToken();
                    return ImibabyApp.PostJsonWithURLConnection(encryptData, FunctionUrl.SEARCH_BIND_STATUS_URL, false, myApp.getAssets().open("dxclient_t.bks"));
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try{
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("PL");
                    String isStatus = String.valueOf(jsonObject1.get("status"));
                    bdate = (String)jsonObject1.get("bdate");
                    deviceName = (String)jsonObject1.get("deviceName");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void initView(){
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.setting_watch_repair);

        repairSettingCommit = findViewById(R.id.layout_track_commit);
        repairSettingQuery = findViewById(R.id.layout_track_query);
        repairSettingPolicy = findViewById(R.id.layout_track_policy);
        repairSettingShop = findViewById(R.id.layout_repair_shop);

        repairSettingQuery.setOnClickListener(this);
        repairSettingCommit.setOnClickListener(this);
        repairSettingPolicy.setOnClickListener(this);
        repairSettingShop.setOnClickListener(this);

        repairSettingShop.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layout_track_commit:
                if(bdate == null || bdate.equals("")){
                    bdate = "";
                }
                if(deviceName == null || deviceName.equals("")){
                    deviceName = "";
                }
                Intent intentCommit = new Intent(this,RepairCommitActivity.class);
                intentCommit.putExtra("uid",myApp.getCurUser().getEid());
                intentCommit.putExtra("imei",curWatch.getImei());
                intentCommit.putExtra("bdate",bdate);
                intentCommit.putExtra("deviceName",deviceName);
                intentCommit.putExtra("phone",myApp.getCurUser().getCellNum());
                startActivity(intentCommit);
                break;
            case R.id.layout_track_query:
                Intent intentQuery = new Intent(this,RepairQueryActivity.class);
                intentQuery.putExtra("uid",myApp.getCurUser().getEid());
                startActivity(intentQuery);
                break;
            case R.id.layout_track_policy:
                Intent intentPolicy = new Intent(this,AdWebViewActivity.class);
                intentPolicy.putExtra("targetUrl", "https://mp.weixin.qq.com/s?__biz=MzIyMDM1OTM0MQ==&mid=100003393&idx=1&sn=f1dc48a831e77bfbce982f046faa5d01&chksm=17cc718320bbf895161ce71410bb80947079ec2aeeee4df11cdccc285fe66a8cc353ec73963c#rd");
                intentPolicy.putExtra("activityType", 0);
                startActivity(intentPolicy);
                break;
            case R.id.layout_repair_shop:
                Intent intentShop = new Intent(this,AdWebViewActivity.class);
                intentShop.putExtra("targetUrl", "https://shop284751852.taobao.com");
                intentShop.putExtra("activityType", 0);
                startActivity(intentShop);
                break;
        }
    }
}
