package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

import java.io.Serializable;

public class AlarmTime implements Serializable,Cloneable{

	public final static long serialVersionUID = 201612171144521L;

	public String hour;
	public String min;
	public String days;
	public String onoff;
	public String timeStampId;
	public String select;
	public String bell;

	public AlarmTime(){

	}

	public AlarmTime(String hour, String min, String days, String onoff, String timeStampId, String select, String bell) {
		this.hour = hour;
		this.min = min;
		this.days = days;
		this.onoff = onoff;
		this.timeStampId = timeStampId;
		this.select = select;
		this.bell = bell;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return ("hour:" + hour + " " +
				"min:" + min + " " +
				"days:" + days + " " +
				"onoff:" + onoff + " " +
				"timeStampId:" + timeStampId + " " +
				"select:" + select + " " +
				"bell:" + bell + " ");
	}

	public static JSONObject toJsonObjectFromAlarmTimeBean(JSONObject jsonObject, AlarmTime alarmTime) {

		jsonObject.put(CloudBridgeUtil.ALARM_HOUR, alarmTime.hour);
		jsonObject.put(CloudBridgeUtil.ALARM_MIN, alarmTime.min);
		jsonObject.put(CloudBridgeUtil.ALARM_DAYS, alarmTime.days);
		jsonObject.put(CloudBridgeUtil.ALARM_ONOFF, alarmTime.onoff);
		jsonObject.put(CloudBridgeUtil.ALARM_TIMESTAMPID, alarmTime.timeStampId);
		jsonObject.put(CloudBridgeUtil.ALARM_BELL, alarmTime.bell);
		return jsonObject;
	}

	public static AlarmTime toBeAlarmTimeBean(AlarmTime alarmTime, JSONObject jsonObj) {

		alarmTime.hour = (String) jsonObj.get(CloudBridgeUtil.ALARM_HOUR);
		alarmTime.min = (String) jsonObj.get(CloudBridgeUtil.ALARM_MIN);
		alarmTime.days = (String) jsonObj.get(CloudBridgeUtil.ALARM_DAYS);
		alarmTime.timeStampId = (String) jsonObj.get(CloudBridgeUtil.ALARM_TIMESTAMPID);
		alarmTime.bell = (String) jsonObj.get(CloudBridgeUtil.ALARM_BELL);
		alarmTime.onoff = (String) jsonObj.get(CloudBridgeUtil.ALARM_ONOFF);
		if (alarmTime.hour.length() == 1)
			alarmTime.hour = "0" + alarmTime.hour;
		if (alarmTime.min.length() == 1)
			alarmTime.min = "0" + alarmTime.min;
		if (alarmTime.days.substring(0, 1).equals("0"))
			alarmTime.select = "1";
		else if (alarmTime.days.equals("1,1,1,1,1,1,0,0"))
			alarmTime.select = "2";
		else if (alarmTime.days.equals("1,1,1,1,1,1,1,1"))
			alarmTime.select = "3";
		else
			alarmTime.select = "4";

		return alarmTime;
	}
}
