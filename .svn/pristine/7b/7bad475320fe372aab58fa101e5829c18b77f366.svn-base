package com.xiaoxun.xun.beans;

public class WarningInfo implements Comparable<WarningInfo>{
	private int mWarningType;   //1 低电量  2 sos 3 家庭成员变化
	private String mTimestamp;
	private BattaryPower mPower;
	private SosWarning mSos;
	private FamilyChangeInfo mFamilyChange;
	
	public int getmWarningType() {
		return mWarningType;
	}
	public void setmWarningType(int mWarningType) {
		this.mWarningType = mWarningType;
	}
	public String getmTimestamp() {
		return mTimestamp;
	}
	public void setmTimestamp(String mTimestamp) {
		this.mTimestamp = mTimestamp;
	}
	public BattaryPower getmPower() {
		return mPower;
	}
	public void setmPower(BattaryPower mPower) {
		this.mPower = mPower;
	}
	public SosWarning getmSos() {
		return mSos;
	}
	public void setmSos(SosWarning mSos) {
		this.mSos = mSos;
	}
	public FamilyChangeInfo getmFamilyChange() {
		return mFamilyChange;
	}
	public void setmFamilyChange(FamilyChangeInfo mFamilyChange) {
		this.mFamilyChange = mFamilyChange;
	}
	@Override
	public int compareTo(WarningInfo another) {
		// TODO Auto-generated method stub
		int compareName = this.mTimestamp.compareTo(another.getmTimestamp());
		if(compareName > 0){
			compareName = -1;
		}else if(compareName < 0){
			compareName = 1;
		}
		return compareName;
	}
}
