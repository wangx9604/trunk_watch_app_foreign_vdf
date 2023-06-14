package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

import java.io.Serializable;

public class SleepTime implements Serializable{

	public final static long serialVersionUID = 201612161716521L;

	public String starthour;
	public String startmin;
	public String endhour;
	public String endmin;
	public String onoff; // 1 kai  0 guan
	public String type;  //0：飞行模式 1：关机模式
	public String timeStampId;

	public SleepTime(){
		
	}

	public SleepTime(String starthour, String startmin, String endhour, String endmin, String onoff, String type, String timeStampId) {
		this.starthour = starthour;
		this.startmin = startmin;
		this.endhour = endhour;
		this.endmin = endmin;
		this.onoff = onoff;
		this.type = type;
		this.timeStampId = timeStampId;
	}

	@Override
	public String toString() {
		return ("starthour:" + starthour + " " +
				"startmin:" + startmin + " " +
				"endhour:" + endhour + " " +
				"endmin:" + endmin + " " +
				"onoff:" + onoff + " " +
				"type:" + type + " " +
				"timeStampId:" + timeStampId);
	}

	public static JSONObject toJsonObjectFromSleepTimeBean(JSONObject jsonObject, SleepTime sleepTime) {

		jsonObject.put(CloudBridgeUtil.STARTHOUR, sleepTime.starthour);
		jsonObject.put(CloudBridgeUtil.STARTMIN, sleepTime.startmin);
		jsonObject.put(CloudBridgeUtil.ENDHOUR, sleepTime.endhour);
		jsonObject.put(CloudBridgeUtil.ENDMIN, sleepTime.endmin);
		jsonObject.put(CloudBridgeUtil.ONOFF, sleepTime.onoff);
		jsonObject.put(CloudBridgeUtil.TYPE, sleepTime.type);
		jsonObject.put(CloudBridgeUtil.TIMESTAMPID, sleepTime.timeStampId);
		return jsonObject;
	}

	public static SleepTime toBeSleepTimeBean(SleepTime sleepTime, JSONObject jsonObject) {

		sleepTime.starthour = (String) jsonObject.get(CloudBridgeUtil.STARTHOUR);
		if (sleepTime.starthour.length() == 1)
			sleepTime.starthour = "0" + sleepTime.starthour;
		sleepTime.startmin = (String) jsonObject.get(CloudBridgeUtil.STARTMIN);
		if (sleepTime.startmin.length() == 1)
			sleepTime.startmin = "0" + sleepTime.startmin;
		sleepTime.endhour = (String) jsonObject.get(CloudBridgeUtil.ENDHOUR);
		if (sleepTime.endhour.length() == 1)
			sleepTime.endhour = "0" + sleepTime.endhour;
		sleepTime.endmin = (String) jsonObject.get(CloudBridgeUtil.ENDMIN);
		if (sleepTime.endmin.length() == 1)
			sleepTime.endmin = "0" + sleepTime.endmin;
		sleepTime.onoff = (String) jsonObject.get(CloudBridgeUtil.ONOFF);
		sleepTime.timeStampId = (String) jsonObject.get(CloudBridgeUtil.TIMESTAMPID);

		if (jsonObject.containsKey(CloudBridgeUtil.TYPE)) {
			sleepTime.type = (String) jsonObject.get(CloudBridgeUtil.TYPE);
		} else
			sleepTime.type = "0";
		return sleepTime;
	}
}
