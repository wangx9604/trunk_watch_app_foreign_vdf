package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUsageActivity extends NormalActivity implements MsgCallback{
    private ImibabyApp mApp;
    private WatchData curWatch;

    private ListView gv_app_usage;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mData;
    private ImageButton mBackbtn;
    private TextView tv_app_usage;
    private TextView tv_app_update;
    private LinearLayout mLayout;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);
        mApp = (ImibabyApp)getApplication();
        curWatch = mApp.getCurUser().getFocusWatch();
        mData = new ArrayList<Map<String, Object>>();

        initView();
        if(myApp.getNetService() != null) {
            String watchEid = myApp.getCurUser().getFocusWatch().getEid();
            myApp.getNetService().sendE2EMsg(watchEid, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_APP_USAGE,
                    30 * 1000, true, AppUsageActivity.this);
        }

    }

    private void initView(){
        ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.app_uses_info));

        mBackbtn = findViewById(R.id.iv_title_back);
        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLayout = findViewById(R.id.layout_no_use);

        tv_app_usage = findViewById(R.id.tv_useage_time);
        tv_app_update = findViewById(R.id.tv_update_time);
        gv_app_usage = findViewById(R.id.app_usage_view);
        String[] from = {"name","desc","icon"};
        int[] to = {R.id.iv_app_name,R.id.iv_app_desc,R.id.iv_app_icon};
        mAdapter = new SimpleAdapter(this, mData, R.layout.app_usage_item, from, to);
        gv_app_usage.setAdapter(mAdapter);

        loadingDialog = new LoadingDialog(this, R.style.Theme_DataSheet, null);
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.enableCancel(false);
            loadingDialog.changeStatus(1, getString(R.string.ximalaya_story_sync_data));
            loadingDialog.show();
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_E2E_DOWN:
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                try {
                    LogUtil.e( "app usage:" + respMsg.toJSONString());

                    mLayout.setVisibility(View.GONE);
                    tv_app_usage.setVisibility(View.VISIBLE);
                    tv_app_update.setVisibility(View.VISIBLE);
                    gv_app_usage.setVisibility(View.VISIBLE);
                    JSONObject pl = (JSONObject) respMsg.get("PL");
                    JSONObject appUsage = (JSONObject) pl.get("appsUsage");
                    String startTime = (String)appUsage.get("startTime");
                    String endTime = (String)appUsage.get("endTime");
                    tv_app_usage.setText(getString(R.string.app_uses_time));
                    tv_app_update.setText(getString(R.string.steps_update_time, TimeUtil.getAllMsgTime(getApplicationContext(),TimeUtil.getTimeStampLocal())));
                    JSONArray appList = (JSONArray)appUsage.get("apps");
                    jsonArrayToStructureData(appList,mData);
                    mAdapter.notifyDataSetChanged();
                    appUsage.put(Const.KEY_WATCH_STATE_TIMESTAMP, TimeUtil.getTimestampCHN());
                    myApp.setValue(Const.SHARE_PREF_APP_STATITICS_CACHE+curWatch.getEid(), appUsage.toJSONString());
                } catch (Exception e) {
                    String localData = myApp.getStringValue(Const.SHARE_PREF_APP_STATITICS_CACHE+curWatch.getEid(),"");
                    if(localData == null || localData.equals("")){
                        mLayout.setVisibility(View.VISIBLE);
                        tv_app_update.setVisibility(View.GONE);
                        tv_app_usage.setVisibility(View.GONE);
                        gv_app_usage.setVisibility(View.GONE);
                    }else {
                        mLayout.setVisibility(View.GONE);
                        tv_app_update.setVisibility(View.VISIBLE);
                        tv_app_usage.setVisibility(View.VISIBLE);
                        gv_app_usage.setVisibility(View.VISIBLE);
                        JSONObject localUsage = (JSONObject) JSONValue.parse(localData);
                        String startTime = (String)localUsage.get("startTime");
                        String endTime = (String)localUsage.get("endTime");
                        String updateTime = (String)localUsage.get(Const.KEY_WATCH_STATE_TIMESTAMP);
                        tv_app_usage.setText(getString(R.string.app_uses_time));
                        tv_app_update.setText(getString(R.string.steps_update_time, TimeUtil.getAllMsgTime(getApplicationContext(),TimeUtil.chnToLocalTimestamp(updateTime))));
                        JSONArray appList = (JSONArray)localUsage.get("apps");
                        jsonArrayToStructureData(appList,mData);
                        mAdapter.notifyDataSetChanged();
                    }
                    LogUtil.e("e2e message error:" + e.toString());
                }
                break;

        }
    }

    private void jsonArrayToStructureData(JSONArray jsonArray,List<Map<String, Object>> mDataList){
        for(int i = 0;i < jsonArray.size();i++){
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name",jsonObject.get("name"));
            map.put("desc",jsonObject.get("desc"));
            map.put("icon",app_icons[getResIdByPackage((String)jsonObject.get("package"))]);
            mDataList.add(map);
        }
    }

    int[] app_icons = {
            R.drawable.watch_app_baidu,R.drawable.watch_app_botany,R.drawable.watch_app_brightness,
            R.drawable.watch_app_call,R.drawable.watch_app_friends,R.drawable.watch_app_image,
            R.drawable.watch_app_message,R.drawable.watch_app_pet,R.drawable.watch_app_voice
            ,R.drawable.watch_app_photo,R.drawable.watch_app_qq,R.drawable.watch_app_set
            ,R.drawable.watch_app_step,R.drawable.watch_app_story,R.drawable.watch_app_video
            ,R.drawable.watch_app_xiaoai,R.drawable.watch_app_alipay,R.drawable.watch_app_other
            ,R.drawable.watch_app_vedio,R.drawable.watch_app_english,R.drawable.watch_app_xunbaidulbs
    };

    private int getResIdByPackage(String packageName){
        int appResId = 17;
        switch(packageName){
            case "com.android.dialer":
                appResId = 3;
                break;
            case "com.xxun.watch.xunchatroom":
                appResId = 6;
                break;
            case "com.xxun.watch.xunpet":
                appResId = 7;
                break;
            case "com.xxun.watch.stepstart":
                appResId = 12;
                break;
            case "com.xxun.watch.storytall":
                appResId = 13;
                break;
            case "com.tencent.qqlite":
                appResId = 10;
                break;
            case "com.xxun.xungallery":
                appResId = 5;
                break;
            case "com.xxun.xuncamera":
            case "com.xxun.camera":
                appResId = 9;
                break;
            case "com.xxun.duer.dcs":
                appResId = 0;
                break;
            case "com.xxun.watch.xunbrain.x2":
            case "com.xxun.watch.xunbrain.y1":
            case "com.xxun.watch.xunbrain":
                appResId = 15;
                break;
            case "com.xxun.watch.xunsettings":
                appResId = 11;
                break;
            case "com.xxun.screenon":
                appResId = 2;
                break;
            case "com.xxun.xunimgrec":
                appResId = 1;
                break;
            case "com.eg.android.AlipayGphone":
                appResId = 16;
                break;
            case "com.xxun.videocall":
                appResId = 18;
                break;
            case "com.xiaoxun.englishdailystudy":
                appResId = 19;
                break;
            case "com.xxun.xunbaidulbs":
                appResId = 20;
                break;

            default:
                appResId = 17;
                break;
        }

        return appResId;
    }

    private class myAdapter extends SimpleAdapter{

        public myAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView tv_name = view.findViewById(R.id.iv_app_name);
            TextView tv_desc = view.findViewById(R.id.iv_app_desc);
            int str_image_id = (int)mData.get(position).get("image");
            int str_text_id = (int)mData.get(position).get("text");

            return view;
        }
    }
}
