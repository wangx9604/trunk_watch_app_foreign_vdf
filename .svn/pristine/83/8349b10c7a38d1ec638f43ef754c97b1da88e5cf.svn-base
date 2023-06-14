/**
 * Creation Date:2015-1-13
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-13
 * 
 */
public class NoticeMsgData implements Comparable<NoticeMsgData>, Parcelable {
    public static final int MSG_TYPE_ALL = 0;
    public static final int MSG_TYPE_FAMILY_CHANGE = 1;
    public static final int MSG_TYPE_SAFE_AREA = 2;
    public static final int MSG_TYPE_SOS_LOCATION = 3;
    public static final int MSG_TYPE_BATTERY_WARNNING = 4;
    public static final int MSG_TYPE_STAEPS = 5;
    public static final int MSG_TYPE_CHANGE_SIM = 6;
    public static final int MSG_TYPE_SMS = 7;
    public static final int MSG_TYPE_DOWNLOAD = 8;
    public static final int MSG_TYPE_STAEPSRANKS = 9;
    public static final int MSG_TYPE_CLOUD_SPACE = 10;
    public static final int MSG_TYPE_FLOWMETER = 11;
    public static final int MSG_TYPE_OTA_UPGRADE = 12;
    public static final int MSG_TYPE_NAVIGATION = 13;
    public static final int MSG_TYPE_APPMANAGER = 14;
    public static final int MSG_TYPE_OTA_UPGRADE_EX = 15;
    public static final int MSG_TYPE_STORY = 16;
    public static final int MSG_TYPE_SYSTEM = 18;

    public static final int MSG_TYPE_SAFE_DANGER_DRAW = 25;
    public static String PARCE_KEY = "NoticeMsgData.KEY";
    private String mTimeStamp;  //时间
    private String mSrcid;      //发送者id
    private String mDstid;      //接受者id
    private String mContent;    //消息内容
    private String mGroupid;    //组id，目前同familyid
    private String mDeviceid;   //设备id
    private int mType;          //消息类型  1 家庭成员变化，2 进出安全区域，3 sos位置， 4 低电量提醒， 5 计步目标完成提醒, 6 更换sim卡通知, 7 设备短信, 8　手表下载完成通知
    private int mStatus;        //消息状态，1有效，2 无效, 3 拦截的信息
    public static final int MSG_STATUS_SPAM = 3;
    public static final int MSG_STATUS_DELETE = 2;

    public NoticeMsgData(){

    }

    public NoticeMsgData(String mTimeStamp, String mSrcid, String mDstid, String mContent, String mGroupid, String mDeviceid, int mType, int mStatus){
        this.mTimeStamp = mTimeStamp;
        this.mSrcid = mSrcid;
        this.mDeviceid = mDeviceid;
        this.mDstid = mDstid;
        this.mContent = mContent;
        this.mGroupid = mGroupid;
        this.mType = mType;
        this.mStatus = mStatus;
    }


    public int getmStatus() {
        return mStatus;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public String getmDstid() {
        return mDstid;
    }

    public String getmContent() {
        return mContent;
    }

    public String getmSrcid() {
        return mSrcid;
    }

    public String getmGroupid() {
        return mGroupid;
    }

    public String getmDeviceid() {
        return mDeviceid;
    }

    public int getmType() {
        return mType;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public void setmSrcid(String mSrcid) {
        this.mSrcid = mSrcid;
    }

    public void setmDstid(String mDstid) {
        this.mDstid = mDstid;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmGroupid(String mGroupid) {
        this.mGroupid = mGroupid;
    }

    public void setmDeviceid(String mDeviceid) {
        this.mDeviceid = mDeviceid;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int compareTo(NoticeMsgData another) {
        // TODO Auto-generated method stub
        int compareName = this.mTimeStamp.compareTo(another.getmTimeStamp());
        return compareName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTimeStamp);
        dest.writeString(mSrcid);
        dest.writeString(mDeviceid);
        dest.writeString(mContent);
        dest.writeString(mDstid);
        dest.writeString(mGroupid);
        dest.writeInt(mStatus);
        dest.writeInt(mType);
    }
    public static final Parcelable.Creator<NoticeMsgData> CREATOR = new Creator<NoticeMsgData>() {
        @Override
        public NoticeMsgData createFromParcel(Parcel source) {
            NoticeMsgData data = new NoticeMsgData();
            data.mTimeStamp = source.readString();
            data.mSrcid = source.readString();
            data.mDeviceid = source.readString();
            data.mContent = source.readString();
            data.mDstid = source.readString();
            data.mGroupid = source.readString();
            data.mStatus = source.readInt();
            data.mType = source.readInt();
            return data;
        }

        @Override
        public NoticeMsgData[] newArray(int size) {
            return new NoticeMsgData[size];
        }
    };
}
