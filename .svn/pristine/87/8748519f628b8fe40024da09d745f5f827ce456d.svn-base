/**
 * Creation Date:2015-2-3
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.interfaces.ResponseOnTouch;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.AdapterItemClickListener;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.CustomDialogListener;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomConfigSeekbar;

import net.minidev.json.JSONObject;

import java.util.ArrayList;

public class VolumeActivity extends NormalActivity implements OnClickListener , MsgCallback{

	private ImageButton btnBack;
	private TextView mTvTitle;
	private CustomConfigSeekbar volumeBar;
	private CustomConfigSeekbar lightBar;
	private View watchNoticeView ;
	private TextView volumeResultText ;
	private View mLayoutLight;
	
	private WatchData curWatch;
	private NetService mNetService;
	private int lightLevel = Const.DEFAULT_LIGHT_LEVEL;	//亮度
	private int volumeLevel = Const.DEFAULT_VOLUME_LEVEL; //音量
	private int noticeLevel = Const.DEFAULT_REMIND_TYPE; //提示方式
	private int defaultVolumeLevel;
	private ArrayList<String> lightSections = new ArrayList<>();
	private ArrayList<String> volumeSections = new ArrayList<>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);

		curWatch = myApp.getCurUser().getFocusWatch();
		mNetService = myApp.getNetService();

		initViews();
		initListener();
		getWatchParameterFromLocal();
		sendMsgForGetWatchParameter();
    }

	@Override
	protected void onResume() {
		super.onResume();
		updateViewShow();
		updateWatchParameterShow();
	}

	private void initViews() {

		mTvTitle= findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.iv_title_back);
		mLayoutLight=findViewById(R.id.light_title);
		
		volumeBar = findViewById(R.id.myCustomSeekBar);
		volumeSections.add(getString(R.string.watch_volume_silence));
		volumeSections.add(getString(R.string.watch_volume_low));
		volumeSections.add(getString(R.string.watch_volume_middle));
		volumeSections.add(getString(R.string.watch_volume_high));
		volumeBar.initData(volumeSections);
		
		lightBar = findViewById(R.id.myCustomConfigSeekBar);
		lightSections.add(getString(R.string.watch_light_dark));
		lightSections.add(getString(R.string.watch_volume_middle));
		lightSections.add(getString(R.string.watch_light_bright));
		lightBar.initData(lightSections);

        watchNoticeView = findViewById(R.id.watch_notice_view);
        volumeResultText = findViewById(R.id.volume_result_text);
	}

	private void updateViewShow() {

		if ((myApp.isControledByVersion(curWatch, false, "T26") && curWatch.isDevice102()) || curWatch.isDevice105()) {
			mTvTitle.setText(R.string.volume_vibrate_led);
		} else {
			mTvTitle.setText(R.string.volume_vibrate);
		}

		if((myApp.isControledByVersion(curWatch, false, "T26") && curWatch.isDevice102()) || curWatch.isDevice105()) {
			mLayoutLight.setVisibility(View.VISIBLE);
		}else{
			mLayoutLight.setVisibility(View.GONE);
		}
	}

	private void updateWatchParameterShow() {

		setLightSeekBarProgress(lightLevel);
		setVolumeNotice(volumeLevel);
		setNoticeLevelSelect(noticeLevel);
	}

	private void initListener() {

		btnBack.setOnClickListener(this);

		volumeBar.setResponseOnTouch(new ResponseOnTouch() {
			@Override
			public void onTouchResponse(int volume) {
				int newVolumeLevel = volume * 2;
				if (curWatch.isDevice501() && volume == 3)
					newVolumeLevel = 5;
				mapSetData(newVolumeLevel, CloudBridgeUtil.NOTICE_VOLUME_LEVEL);
			}
		});

		lightBar.setResponseOnTouch(new ResponseOnTouch() {
			@Override
			public void onTouchResponse(int level) {
				int newLightLevel ;
				if(level == 0){
					newLightLevel = 1;
				}else if(level == 1){
					newLightLevel = 4;
				}else{
					newLightLevel = 10;
				}
				mapSetData(newLightLevel,CloudBridgeUtil.NOTICE_LED_LEVEL);
			}
		});

		View.OnClickListener noticeListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ArrayList<String> itemList = new ArrayList<>();
				itemList.add(getText(R.string.only_volume).toString());
				itemList.add(getText(R.string.only_vibrate).toString());
				itemList.add(getText(R.string.volume_and_vibrate).toString());
				Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(VolumeActivity.this, getText(R.string.watch_notice_method).toString(),itemList ,
						new AdapterItemClickListener() {
							@Override
							public void onClick(View v , int position) {
								if(noticeLevel != position){
									int newNoticeLevel = position ;
									mapSetData(newNoticeLevel,CloudBridgeUtil.VOL_VIB);
								}
							}
						},
						noticeLevel, new CustomDialogListener() {
							@Override
							public void onClick(View v, String text) {
							}
						}, getText(R.string.cancel).toString());
				dlg.show();
			}
		};
		watchNoticeView.setOnClickListener(noticeListener);
	}

	private void getWatchParameterFromLocal() {

		lightLevel = myApp.getIntValue(curWatch.getEid() + CloudBridgeUtil.NOTICE_LED_LEVEL, Const.DEFAULT_LIGHT_LEVEL);
		defaultVolumeLevel = Const.DEFAULT_VOLUME_LEVEL;
		if (curWatch.isDevice501())
			defaultVolumeLevel = Const.DEFAULT_VOLUME_LEVEL_501;
		volumeLevel = myApp.getIntValue(curWatch.getEid() + CloudBridgeUtil.NOTICE_VOLUME_LEVEL, defaultVolumeLevel);
		noticeLevel = myApp.getIntValue(curWatch.getEid() + CloudBridgeUtil.VOL_VIB, Const.DEFAULT_REMIND_TYPE);
	}

    private void sendMsgForGetWatchParameter(){

		String[] keys = new String[3];
		String eid = curWatch.getEid();
		keys[0] = CloudBridgeUtil.NOTICE_VOLUME_LEVEL;
		keys[1] = CloudBridgeUtil.VOL_VIB;
		keys[2] = CloudBridgeUtil.NOTICE_LED_LEVEL;
		if (mNetService != null) {
			mNetService.sendMapMGetMsg(eid, keys, VolumeActivity.this);
		}
    }

    private void mapSetData(int value, String key){

		String eid = myApp.getCurUser().getFocusWatch().getEid();
		String familyid = myApp.getCurUser().getFocusWatch().getFamilyId();
        if (mNetService != null) {
            mNetService.sendMapSetMsg(eid, familyid, key, Integer.toString(value), VolumeActivity.this);
        }
    }

	@Override
	public void onClick(View v) {
		if (btnBack == v) {
			finish();
		}
	}

	@Override
	public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
		switch (cid) {
			case CloudBridgeUtil.CID_MAPSET_RESP:
				int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
				JSONObject pl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
				String light_lvl = (String)pl.get(CloudBridgeUtil.NOTICE_LED_LEVEL);
				String volume_lvl = (String)pl.get(CloudBridgeUtil.NOTICE_VOLUME_LEVEL);
				String volumviber = (String)pl.get(CloudBridgeUtil.VOL_VIB);
				if (rc > 0) {
					if(volume_lvl != null && volume_lvl.length() > 0) {
						setVolumeNotice(Integer.parseInt(volume_lvl));
						setValueToLocal(CloudBridgeUtil.NOTICE_VOLUME_LEVEL,Integer.parseInt(volume_lvl));
					}
					if(volumviber != null && volumviber.length() > 0){
						setNoticeLevelSelect(Integer.parseInt(volumviber));
						setValueToLocal(CloudBridgeUtil.VOL_VIB,Integer.parseInt(volumviber));
					}
					if (light_lvl != null && light_lvl.length() > 0) {
						setLightSeekBarProgress(Integer.parseInt(light_lvl));
						setValueToLocal(CloudBridgeUtil.NOTICE_LED_LEVEL,Integer.parseInt(light_lvl));
					}
				} else if(rc == CloudBridgeUtil.RC_TIMEOUT){
					ToastUtil.showMyToast(this,
							getString(R.string.phone_set_timeout),
							Toast.LENGTH_SHORT);
					if(volume_lvl != null && volume_lvl.length() > 0) {
						setVolumeNotice(volumeLevel);
					}
					if (light_lvl != null && light_lvl.length() > 0) {
						setLightSeekBarProgress(lightLevel);
					}
				}else if(rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY){
					ToastUtil.showMyToast(this, getString(R.string.network_error_prompt),
							Toast.LENGTH_SHORT);
					if(volume_lvl != null && volume_lvl.length() > 0) {
						setVolumeNotice(volumeLevel);
					}
					if (light_lvl != null && light_lvl.length() > 0) {
						setLightSeekBarProgress(lightLevel);
					}
				}else if(rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
					ToastUtil.showMyToast(this, getString(R.string.set_error),
							Toast.LENGTH_SHORT);
					if(volume_lvl != null && volume_lvl.length() > 0) {
						setVolumeNotice(volumeLevel);
					}
					if (light_lvl != null && light_lvl.length() > 0) {
						setLightSeekBarProgress(lightLevel);
					}
				}
				break;

        case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
            int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);       
            if (mapRc == CloudBridgeUtil.RC_SUCCESS){
                JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                String sTmp = (String) maggetPl.get(CloudBridgeUtil.NOTICE_VOLUME_LEVEL) ;
                if (sTmp!=null&&sTmp.length()>0&&!sTmp.equals("")&&!sTmp.equals("null")){
					volumeLevel = Integer.parseInt(sTmp);
					setVolumeNotice(volumeLevel);
					setValueToLocal(CloudBridgeUtil.NOTICE_VOLUME_LEVEL,volumeLevel);
                } else {
					if (haveLocalValue(CloudBridgeUtil.NOTICE_VOLUME_LEVEL)){
						deleteLocalValue(CloudBridgeUtil.NOTICE_VOLUME_LEVEL);
						setVolumeNotice(defaultVolumeLevel);
					}
				}

                sTmp = (String) maggetPl.get(CloudBridgeUtil.VOL_VIB) ;
                if (sTmp!=null&&sTmp.length()>0&&!sTmp.equals("")&&!sTmp.equals("null")){
                	noticeLevel = Integer.parseInt(sTmp);
					if(noticeLevel == 0){
						noticeLevel = Const.DEFAULT_REMIND_TYPE;
					}
                	setNoticeLevelSelect(noticeLevel);
					setValueToLocal(CloudBridgeUtil.VOL_VIB,noticeLevel);
                } else {
					if (haveLocalValue(CloudBridgeUtil.VOL_VIB)){
						deleteLocalValue(CloudBridgeUtil.VOL_VIB);
					}
				}

				sTmp = (String) maggetPl.get(CloudBridgeUtil.NOTICE_LED_LEVEL);
				if(sTmp != null&&sTmp.length()>0&&!sTmp.equals("")&&!sTmp.equals("null")){
					lightLevel = Integer.parseInt(sTmp);
					if(lightLevel == 0){
						lightLevel = Const.DEFAULT_LIGHT_LEVEL;
					}
					setLightSeekBarProgress(lightLevel);
					setValueToLocal(CloudBridgeUtil.NOTICE_LED_LEVEL,lightLevel);
				} else {
					if (haveLocalValue(CloudBridgeUtil.NOTICE_LED_LEVEL)){
						deleteLocalValue(CloudBridgeUtil.NOTICE_LED_LEVEL);
					}
				}
            }
            break;
		default:
			break;
		}
	}

	private void setNoticeLevelSelect(int noticeValue){

		noticeLevel=noticeValue;
    	if(noticeValue == 1)
        	volumeResultText.setText(R.string.only_volume);
    	else if(noticeValue == 2)
        	volumeResultText.setText(R.string.only_vibrate);
    	else if(noticeValue == 3)
        	volumeResultText.setText(R.string.volume_and_vibrate);
	}
	
    private void setVolumeNotice(int volumeValue) {

		volumeLevel = volumeValue;
		int tmpVolumeLevel = Const.DEFAULT_VOLUME_LEVEL;
		if (volumeValue == 5 || volumeValue == 6) {
			tmpVolumeLevel = 3;
		} else if (volumeValue == 3 || volumeValue == 4) {
			tmpVolumeLevel = 2;
		} else if (volumeValue == 1 || volumeValue == 2) {
			tmpVolumeLevel = 1;
		} else if (volumeValue == 0) {
			tmpVolumeLevel = 0;
		}
		volumeBar.setProgress(tmpVolumeLevel);
	}

	private void setLightSeekBarProgress(int lightValue){

		int tmpLightLvl = Const.DEFAULT_LIGHT_LEVEL;	//亮度显示值
		lightLevel=lightValue;
		if (lightValue == 10) {
			tmpLightLvl = 2;
		} else if (lightValue == 4) {
			tmpLightLvl = 1;
		} else if (lightValue == 1) {
			tmpLightLvl = 0;
		}
		lightBar.setProgress(tmpLightLvl);
	}

	private void setValueToLocal(String key,int value){
		myApp.setValue(curWatch.getEid() + key, value);
	}

	private boolean haveLocalValue(String key) {
		return myApp.hasValue(curWatch.getEid() + key);
	}

	private void deleteLocalValue(String key) {
		// 这里处理其实是不友好的，但考虑到兼容旧app版本，暂且这样处理
		myApp.deletValue(curWatch.getEid() + key);
	}
}
