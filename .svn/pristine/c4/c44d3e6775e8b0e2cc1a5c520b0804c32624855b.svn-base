package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairQueryActivity extends NormalActivity {

    private String uid;
    private ListView track_list_lv;
    private SimpleAdapter myAdapter;
    private LoadingDialog mLoadingDlg;
    private LinearLayout ll_no_reply;

    private List<Map<String, Object>> mData;

    private final String getTrackListUrl_1 = "https://xxkj.ewei.com/api/v1/customers/";
    private final String getTrackListUrl_2 = "/tickets.json";
    private final String getTrackListParms = "?_count=100&include_fields=createdAt,updatedAt,subject,id,status,no,user";
    private final String getCustomidUrl = "https://xxkj.ewei.com/api/v1/customers/external_id/";
    private final String appKey = "MTA5NA==";
    private final String appSectet = "3bddgfd215525f24901dff8f1gfdf925";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_query);
        uid = getIntent().getStringExtra("uid");
        mData = new ArrayList<>();
        initView();
        queryDateByImei(uid);
    }

    private void initView(){
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.repair_track_query);

        track_list_lv = findViewById(R.id.show_track_list);
        ll_no_reply = findViewById(R.id.ll_no_reply);

        String[] from = {"name","no","subject","updatedAt","status"};
        int[] to = {R.id.iv_repair_name,R.id.iv_repair_no,R.id.iv_repair_subject,R.id.iv_repair_update,R.id.iv_repair_status};
        myAdapter = new SimpleAdapter(this,mData,R.layout.repair_query_item,from,to);
        track_list_lv.setAdapter(myAdapter);
        track_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> map = mData.get(position);
                String trackId = String.valueOf(map.get("trackid"));
                String trackNo = String.valueOf(map.get("no"));
                Intent intentQuery = new Intent(RepairQueryActivity.this,RepairTrackDetailActivity.class);
                intentQuery.putExtra("id",trackId);
                intentQuery.putExtra("no",trackNo);
                startActivity(intentQuery);
            }
        });

        mLoadingDlg = new LoadingDialog(this, R.style.Theme_DataSheet, null);
    }
    private void queryDateByImei(String uid){
        if (mLoadingDlg != null && !mLoadingDlg.isShowing()) {
            mLoadingDlg.enableCancel(false);
            mLoadingDlg.changeStatus(1, getString(R.string.repair_get_data));
            mLoadingDlg.show();
        }

        syncHttpGet();
    }

    private String getCustomIdSign(String sectet,String timestamp,String parmsUrl){
        String parms = null;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp+parmsUrl;
        return parms;
    }
    private String getTrackSign(String sectet,String timestamp){
        String parms = null;
        parms= "_app_secret="+sectet+"_count=100"+"_timestamp="+timestamp+"include_fields=createdAt,updatedAt,subject,id,status,no,user";
        return parms;
    }

    private void syncHttpGet(){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    Date date = new Date();
                    String timeStamp = String.valueOf(date.getTime());
                    String getCustomIdUrl = getCustomidUrl+uid+".json";//?include_fields=id";
                    String getCustomParms = "include_fields=id";
                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getCustomIdSign(encodeSectet,timeStamp,getCustomParms);

                    String sign = AESUtil.calcTransAllParmsSign("Get",getCustomIdUrl,parms);
                    LogUtil.e("repair query sign:"+sign+":"+
                            timeStamp+":"+encodeSectet+":"+getCustomParms+":"+getCustomIdUrl);

                    String responseData = ImibabyApp.HttpGetJsonData(getCustomIdUrl+"?"+getCustomParms,appKey,timeStamp,sign);
                    LogUtil.e("repair custom id Data:"+responseData);
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(responseData);
                    JSONObject jsonObject1 = (JSONObject)jsonObject.get("result");
                    int customId = (int)jsonObject1.get("id");
                    String getTracksUrl = getTrackListUrl_1+customId+getTrackListUrl_2;
                    String parmsTrack = getTrackSign(encodeSectet,timeStamp);
                    sign = AESUtil.calcTransAllParmsSign("Get",getTracksUrl,parmsTrack);

                    responseData = ImibabyApp.HttpGetJsonData(getTracksUrl+getTrackListParms,appKey,timeStamp,sign);
                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
                        mLoadingDlg.dismiss();
                    }
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
                    JSONArray jsonArray = (JSONArray) jsonObject1.get("tickets");
                    mData.clear();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                        JSONObject jsonObject3 = (JSONObject) jsonObject2.get("user");
                        Map<String, Object> map = new HashMap<>();
                        map.put("subject", jsonObject2.get("subject"));
                        map.put("updatedAt", jsonObject2.get("updatedAt"));
                        map.put("status", convertYiWeiStatus((String)jsonObject2.get("status")));
                        map.put("trackid", jsonObject2.get("id"));
                        map.put("no", jsonObject2.get("no"));
                        map.put("name", jsonObject3.get("name"));
                        mData.add(map);
                    }
                    myAdapter.notifyDataSetChanged();
                    if(jsonArray.size() == 0){
                        ll_no_reply.setVisibility(View.VISIBLE);
                        track_list_lv.setVisibility(View.GONE);
                    }else {
                        ll_no_reply.setVisibility(View.GONE);
                        track_list_lv.setVisibility(View.VISIBLE);
                    }
                }catch(Exception e){
                    ll_no_reply.setVisibility(View.VISIBLE);
                    track_list_lv.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private String convertYiWeiStatus(String status){
        String convertStatus;
        switch (status){
            case "open":
                convertStatus = getString(R.string.repair_state_open);
                break;
            case "pending":
                convertStatus = getString(R.string.repair_state_pending);
                break;
            case "solved":
                convertStatus = getString(R.string.repair_state_solved);
                break;
            default:
                convertStatus = getString(R.string.repair_state_open);
                break;
        }
        return convertStatus;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }
}
