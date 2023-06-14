/**
 * Creation Date:2015-2-3
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.test.ConvertFile;
import com.xiaoxun.test.DrawPathActivity;
import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class DevOptActivity extends NormalActivity implements OnClickListener,MsgCallback {
	private View btnBack;
	static private String TAG = "DevOptActivity";
	static public String DEV_OPT_PREF = "dev_opt_pref";
	static public String SHOW_ALL_DOTS = "show_all_dots";
	static public String TRACE_STATISTICS = "trace_statistics";
	static public String SECURITY_50 = "security_50";
	private CheckBox mShowAllDot = null;
	private CheckBox mSecurity50 = null;
	private View point_test;
	private View convert;
	private CheckBox trace_statistics;

	private CheckBox drop_point;
	private CheckBox filter_show;

	private SharedPreferences mPrefs;
	private Button btnAppPing;
	private Button btnWatchPing;
	private TextView tvAppPing;
	private TextView tvWatchPing;
	BroadcastReceiver receiver;
	StringBuilder buffAppPing = new StringBuilder();
	private WatchData curWatch;
	private TextView stepTag;
	private TextView stepsCount;
	private Button bt_stepRequest;
	private Button bt_shift_server;
	private TextView basewifi;
	private int testServerFlag = 0;
	private RelativeLayout rela_steps;
    // add by dengxu for loc policy configuration
    private View loc_policy_cfg;

	private View simOp;
	private CheckBox tracke_open;
	private RelativeLayout test_history_trace;

	private View draw_path_ly;
	private TextView tv_versionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_opt_activity);
		curWatch = ((ImibabyApp) getApplication()).getCurUser().getFocusWatch();

        initViews();
		getDataFromCloudBridge();
    }

    private void initViews() {
		basewifi = findViewById(R.id.base_wifi);
		if(myApp!=null && myApp.getCurUser()!=null && myApp.getCurUser().getFocusWatch()!=null){
			basewifi.setText(myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid()+ Const.SHARE_PREF_EFID1_IMPORTENT_KEY,"未检测到"));
		}
    	btnBack = findViewById(R.id.iv_title_back);
		btnBack.setOnClickListener(this);
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.str_dev_opt);		
        mShowAllDot = findViewById(R.id.show_all_dot);
		mShowAllDot.setOnClickListener(this);
		mSecurity50 = findViewById(R.id.security_50);
		mSecurity50.setOnClickListener(this);

		tracke_open = findViewById(R.id.tracke_open);
		tracke_open.setOnClickListener(this);
		if(myApp.getIntValue(Const.SHARE_PREF_TRACE_CAN_VISIBLE,0) == 1){
			tracke_open.setChecked(true);
		}else{
			tracke_open.setChecked(false);
		}

		stepTag = findViewById(R.id.tv_step_tag);
		stepsCount = findViewById(R.id.tv_step_count);
		bt_stepRequest = findViewById(R.id.bt_step_request);
		bt_stepRequest.setOnClickListener(this);

		testServerFlag = myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0);
		Log.i("cui","testServerFlag="+testServerFlag);
		bt_shift_server = findViewById(R.id.bt_shift_server);
		bt_shift_server.setOnClickListener(this);
		if (testServerFlag>0){
			bt_shift_server.setText("正式服务器");
		}

		point_test = findViewById(R.id.point_test);
		point_test.setOnClickListener(this);
		convert = findViewById(R.id.convert);
		convert.setOnClickListener(this);
		trace_statistics = findViewById(R.id.trace_statistics);
		trace_statistics.setOnClickListener(this);
		simOp = findViewById(R.id.simOp);
		simOp.setOnClickListener(this);
		rela_steps = findViewById(R.id.convert2);
		rela_steps.setOnClickListener(this);
        loc_policy_cfg = findViewById(R.id.loc_policy_cfg);
        loc_policy_cfg.setOnClickListener(this);

		mPrefs = getSharedPreferences(DevOptActivity.DEV_OPT_PREF, Context.MODE_PRIVATE);
		String show_all_dots = mPrefs.getString(DevOptActivity.SHOW_ALL_DOTS, "");
		LogUtil.d(TAG + "  " + "show_all_dots: " + show_all_dots);
		if(show_all_dots.equalsIgnoreCase("true")){
			mShowAllDot.setChecked(true);
		}else{
			mShowAllDot.setChecked(false);
		}

		String security_50 = mPrefs.getString(DevOptActivity.SECURITY_50, "");
		if(security_50.equalsIgnoreCase("true")){
			mSecurity50.setChecked(true);
		}else{
			mSecurity50.setChecked(false);
		}

		String trace_stat = mPrefs.getString(DevOptActivity.TRACE_STATISTICS, "");
		LogUtil.d(TAG+"  "+"trace_stat: " + trace_stat);
		if(trace_stat.equalsIgnoreCase("true")){
			trace_statistics.setChecked(true);
		}else{
			trace_statistics.setChecked(false);
		}

		test_history_trace = findViewById(R.id.test_history_trace);
		test_history_trace.setOnClickListener(this);

		drop_point = findViewById(R.id.drop_point);
		drop_point.setOnClickListener(this);
		int drop_display =myApp.getIntValue("drop_display",0);
		if(drop_display == 1){
			drop_point.setChecked(true);
		}else{
			drop_point.setChecked(false);
		}

		filter_show = findViewById(R.id.filter_show);
		filter_show.setOnClickListener(this);
		int filterShow =myApp.getIntValue("filter_show",0);
		if(filterShow == 1){
			filter_show.setChecked(true);
		}else{
			filter_show.setChecked(false);
		}

		draw_path_ly = findViewById(R.id.draw_path_ly);
		draw_path_ly.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(DevOptActivity.this, DrawPathActivity.class));
			}
		});

		findViewById(R.id.layout_mi_oauth).setOnClickListener(this);
		findViewById(R.id.layout_ai_personalize).setOnClickListener(this);
		findViewById(R.id.layout_versionNumber).setOnClickListener(this);
		tv_versionNumber= findViewById(R.id.tv_versionNumber);
		findViewById(R.id.btn_send_e2e_msg).setOnClickListener(this);

		//init test ping
		btnAppPing = findViewById(R.id.buttonPing);
		btnAppPing.setOnClickListener(this);
		tvAppPing = findViewById(R.id.tv_ping);
        btnWatchPing = findViewById(R.id.buttonPing2);
		btnWatchPing.setOnClickListener(this);
		tvWatchPing = findViewById(R.id.tv_ping2);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Const.ACTION_APP_PING_RESULT)){
                 long delta = intent.getLongExtra("ping",0);
					buffAppPing.append("ping:"+delta);
					buffAppPing.append("\r\n");
					tvAppPing.setText(buffAppPing.toString());
				}else if(intent.getAction().equals(Const.ACTION_WATCH_HEALTH_DATA_NOTICE)){
					String sTmp = intent.getStringExtra(Const.KEY_JSON_MSG);
					if (sTmp!=null&&sTmp.length()>0){
						Object obj= JSONValue.parse(sTmp);
						JSONObject jsonObject = (JSONObject) obj;
						String steps = (String) jsonObject.get("steps");
						String timeStamp = (String) jsonObject.get("timestamp");
						if(steps != null && timeStamp != null){
							updateSteps(steps,timeStamp);
						}
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_APP_PING_RESULT);
		filter.addAction(Const.ACTION_WATCH_HEALTH_DATA_NOTICE);
		registerReceiver(receiver, filter);
    }



	//�ӷ�����MAPGET_MGET�ķ�ʽȡ���
	private void getDataFromCloudBridge(){
		MyMsgData queryGroupsMsg = new MyMsgData();
		queryGroupsMsg.setCallback(DevOptActivity.this);
		JSONObject pl = new JSONObject();
		if(curWatch!=null){
			pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
			pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.HEALTH_INFO);
			queryGroupsMsg.setReqMsg(
					CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
							Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
							myApp.getToken(), pl));
			if(myApp.getNetService() != null) {
				myApp.getNetService().sendNetMsg(queryGroupsMsg);
			}
		}
	}
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(receiver!=null){
			unregisterReceiver(receiver);
		}
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub   
        SharedPreferences.Editor editor;
		switch(v.getId()){
			case R.id.iv_title_back:
				finish();
				break;
			case R.id.show_all_dot:
				LogUtil.d(TAG+"  "+"onClick show_all_dot");
				editor = mPrefs.edit();
				if(mShowAllDot.isChecked()){
					mShowAllDot.setChecked(true);
					editor.putString(DevOptActivity.SHOW_ALL_DOTS, "true");
				}else{
					mShowAllDot.setChecked(false);
					editor.putString(DevOptActivity.SHOW_ALL_DOTS, "false");
				}
				editor.commit();							
				break;
			case R.id.security_50:
				LogUtil.d(TAG+"  "+"onClick show_all_dot");
				editor = mPrefs.edit();
				if(mSecurity50.isChecked()){
					mSecurity50.setChecked(true);
					editor.putString(DevOptActivity.SECURITY_50, "true");
				}else{
					mSecurity50.setChecked(false);
					editor.putString(DevOptActivity.SECURITY_50, "false");
				}
				editor.commit();
				break;
			case R.id.tracke_open:
				if(tracke_open.isChecked()){
					tracke_open.setChecked(true);
					myApp.setValue(Const.SHARE_PREF_TRACE_CAN_VISIBLE, 1);
				}else{
					tracke_open.setChecked(false);
					myApp.setValue(Const.SHARE_PREF_TRACE_CAN_VISIBLE, 0);
				}
				break;
			case R.id.trace_statistics:
				editor = mPrefs.edit();
				if(trace_statistics.isChecked()){
					trace_statistics.setChecked(true);
					editor.putString(DevOptActivity.TRACE_STATISTICS, "true");
				}else{
					trace_statistics.setChecked(false);
					editor.putString(DevOptActivity.TRACE_STATISTICS, "false");
				}
				editor.commit();
				break;
			case R.id.drop_point:
				if(drop_point.isChecked()){
					drop_point.setChecked(true);
					myApp.setValue("drop_display", 1);
				}else{
					drop_point.setChecked(false);
					myApp.setValue("drop_display", 0);
				}
				break;
			case R.id.filter_show:
				if(filter_show.isChecked()){
					filter_show.setChecked(true);
					myApp.setValue("filter_show",1);
				}else{
					filter_show.setChecked(false);
					myApp.setValue("filter_show",0);
				}
				break;
			case R.id.buttonPing:
				//
				buffAppPing.delete(0,buffAppPing.length());
				tvAppPing.setText(" ");
				if(null != myApp.getNetService())
				myApp.getNetService().startAutoPing();
				break;

			case R.id.buttonPing2:
				e2ePingStart();
				tvWatchPing.setText(" ");
				break;
			case R.id.convert:
//				ConvertFile.initDir();
//				ConvertFile.convertFiles();
				break;
			case R.id.bt_step_request:
				requestSteps(CloudBridgeUtil.SUB_ACTION_REQUEST_STEPS);
				break;
			case R.id.simOp:
//				startActivity(new Intent(DevOptActivity.this, com.xiaoxun.xun.activitys.SimOpLayoutActivity.class));
				break;

			case R.id.bt_shift_server:
                if (testServerFlag>0){
					testServerFlag = 0;
				}else{
					testServerFlag = 1;
				}
				myApp.setValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, testServerFlag);
				myApp.doLogout("shift server ");
				break;
			case R.id.convert2:
				startActivity(new Intent(DevOptActivity.this,
						StepsActivity.class));
				break;
			case R.id.layout_mi_oauth:
				startActivity(new Intent(DevOptActivity.this, MiOauthActivity.class));
				break;
			case R.id.layout_ai_personalize:
				Intent personalizeIntent = new Intent(DevOptActivity.this, AIPersonalizeActivity.class);
				personalizeIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_AIPERSONALIZE);
				personalizeIntent.putExtra(Const.KEY_HELP_URL, FunctionUrl.APP_AI_PERSONALIZE_URL);
				startActivity(personalizeIntent);
				break;
			case R.id.btn_send_e2e_msg:
				EditText etE2eAction = findViewById(R.id.et_e2e_action);
				try {
					int e2eAction = Integer.valueOf(etE2eAction.getText().toString().trim());
					myApp.getNetService().sendE2EMsg(myApp.getCurUser().getFocusWatch().getEid(), e2eAction, 30, true, this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.layout_versionNumber:
				//TagsReq tagsReq =new TagsReq();
				StringBuilder sb =new StringBuilder();
				sb.append("SDK_MiPush: "+BuildConfig.SDK_MiPush+"\n");//mipush 有混淆，方法未开放
				sb.append("SDK_MiStats: "+BuildConfig.SDK_MiStats+"\n");
				sb.append("SDK_TingPhoneOpen: "+BuildConfig.SDK_TingPhoneOpen+"\n");//CommonRequest#getSdkVersion
				tv_versionNumber.setText(sb.toString());
				break;
		}
    }
	private void requestSteps(int subAction){
		MyMsgData e2e = new MyMsgData();
		e2e.setCallback(DevOptActivity.this);
		JSONObject pl = new JSONObject();
		pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,subAction);
		String [] teid;
		teid = new String[1];
		teid[0] = curWatch.getEid();
		StringBuffer sms = new StringBuffer();
		int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
		sms.append("<" + sn + "," + getMyApp().getCurUser().getEid() + "," +
				"E" + subAction + "," + ">");
		pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
		e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
				CloudBridgeUtil.CID_E2E_UP, sn, myApp.getToken(), null, teid, pl));
		if(myApp.getNetService() != null)
		myApp.getNetService().sendNetMsg(e2e);
	}

	private void updateSteps(String steps,String time){//20151113150306368
		stepTag.setText(time.substring(0, 4) + "." + time.substring(4, 6) + "." + time.substring(6, 8) + ": ");
		stepsCount.setText(steps + " steps");
	}
	private void e2ePingStart( ){
		WatchData watch = myApp.getCurUser().getFocusWatch();
		String [] watchEid = new String[1];
		watchEid[0]= watch.getEid();
		MyMsgData e2e = new MyMsgData();
		e2e.setCallback(DevOptActivity.this);
        e2e.setTimeout(120 * 1000);
		JSONObject pl = new JSONObject();
		pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_TEST_PING);
		int SN = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

		e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(CloudBridgeUtil.CID_E2E_UP,
				SN, myApp.getToken(),null, watchEid,pl));
		if(myApp.getNetService() != null)
		myApp.getNetService().sendNetMsg(e2e);
	}
	@Override
	public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
		int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
		int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
		switch (cid) {
			case CloudBridgeUtil.CID_E2E_DOWN:
				JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
				rc = CloudBridgeUtil.getCloudMsgRC(pl);

				if(pl !=null) {
					String result = (String) pl.get("result");
					if (result != null && result.length() > 0)
						tvWatchPing.setText(result);
				}
				break ;
			case CloudBridgeUtil.CID_MAPGET_RESP:
				int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
				if(mapRc == CloudBridgeUtil.RC_SUCCESS){
					JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
					String sTmp = (String) maggetPl.get(CloudBridgeUtil.HEALTH_INFO) ;
					if (sTmp!=null&&sTmp.length()>0){
						Object obj= JSONValue.parse(sTmp);
						JSONObject jsonObject = (JSONObject) obj;
						String steps = (String) jsonObject.get("steps");
						String timeStamp = (String) jsonObject.get("timestamp");
						if(steps != null && timeStamp != null){
							updateSteps(steps,timeStamp);
						}
					}
				}

			default:
				break;

		}
	}
}
