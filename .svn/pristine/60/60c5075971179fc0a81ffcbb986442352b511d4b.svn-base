package com.xiaoxun.xun.beans;

public class BattaryPower {
	private String mEid;  //手表eid
	private int mLevel;   //电量值
	private String mTimestamp;
	
	
	public String getmEid() {
		return mEid;
	}

	public void setmEid(String mEid) {
		this.mEid = mEid;
	}

	public int getmLevel() {
		return mLevel;
	}

	public void setmLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public String getmTimestamp() {
		return mTimestamp;
	}

	public void setmTimestamp(String mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public void parse(String param){
		this.mEid = param.substring(0, param.indexOf("+"));
		String n = param.substring(param.indexOf("+")+1);
		this.mLevel = Integer.valueOf(n.substring(0, n.indexOf("+")));
		this.mTimestamp = n.substring(n.indexOf("+")+1);
	}
	
    public String toString() {
    	StringBuilder result = new StringBuilder(mEid+"+"+mLevel+"+"+mTimestamp);
        return result.toString();
    }
}
