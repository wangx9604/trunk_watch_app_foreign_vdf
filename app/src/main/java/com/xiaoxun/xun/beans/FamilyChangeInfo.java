package com.xiaoxun.xun.beans;

public class FamilyChangeInfo {
	private String mEid; //变化者eid
	private String mNickName;//变化者，初次未设置昵称则显示使用username
	private String mTimestamp;
	private String mFamilyId;   //变化的family 
	private int mType; //1 加入， 2 退出  3 合并
	
	public String getmEid() {
		return mEid;
	}

	public void setmEid(String mEid) {
		this.mEid = mEid;
	}

	public String getmNickName() {
		return mNickName;
	}

	public void setmNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public String getmTimestamp() {
		return mTimestamp;
	}

	public void setmTimestamp(String mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public String getmFamilyId() {
		return mFamilyId;
	}

	public void setmFamilyId(String mFamilyId) {
		this.mFamilyId = mFamilyId;
	}

	public int getmType() {
		return mType;
	}

	public void setmType(int mType) {
		this.mType = mType;
	}

	public void parse(String param){
		this.mEid = param.substring(0, param.indexOf("+"));
		String n = param.substring(param.indexOf("+")+1);
		this.mNickName = n.substring(0, n.indexOf("+"));
		n = n.substring(n.indexOf("+")+1);
		this.mTimestamp = n.substring(0, n.indexOf("+"));
		n = n.substring(n.indexOf("+")+1);
		this.mFamilyId = n.substring(0, n.indexOf("+"));
		this.mType = Integer.valueOf(n.substring(n.indexOf("+")+1));
	}
	
    public String toString() {
    	StringBuilder result = new StringBuilder(mEid+"+"+mNickName+"+"+mTimestamp+"+"+mFamilyId+"+"+mType);
        return result.toString();
    }
}
