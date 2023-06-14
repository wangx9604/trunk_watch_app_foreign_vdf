package com.xiaoxun.xun.beans;


public class SosWarning {
	private String mEid;     //手表eid
	private LocationData mLocation; //sos发生的地点
	private String mTimestamp;
	private String mTypeKey;  //sos中地点和语音对应的关键字key 
//	private String mKey;	  //c2e的key值
	
	public String getmEid() {
		return mEid;
	}

	public void setmEid(String mEid) {
		this.mEid = mEid;
	}

	public LocationData getmLocation() {
		return mLocation;
	}

	public void setmLocation(LocationData mLocation) {
		this.mLocation = mLocation;
	}

	public String getmTimestamp() {
		return mTimestamp;
	}

	public void setmTimestamp(String mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public String getmTypeKey() {
		return mTypeKey;
	}

	public void setmTypeKey(String mTypeKey) {
		this.mTypeKey = mTypeKey;
	}
	
	public void parse(String param){
		this.mEid = param.substring(0, param.indexOf("+"));
		String n = param.substring(param.indexOf("+")+1);
		this.mTimestamp = n.substring(0,n.indexOf("+"));
		n = n.substring(n.indexOf("+")+1);
		this.mTypeKey = n.substring(0,n.indexOf("+"));
		LocationData location = new LocationData();
		location.parse(n.substring(n.indexOf("+")+1));
		this.mLocation = location;
	}
	
    public String toString() {
    	StringBuilder result = new StringBuilder(mEid+"+"+mTimestamp+"+"+mTypeKey+"+"+mLocation.toString());
        return result.toString();
    }

//	public String getmKey() {
//		return mKey;
//	}
//
//	public void setmKey(String mKey) {
//		this.mKey = mKey;
//	}
}
