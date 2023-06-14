/**
 * @time 2015-1-27
 */
package com.xiaoxun.xun.beans;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 *  * Description Of The Class<br>
 *
 * @author huangqilin
 * @time 2015-1-27
 *
 */
public class ChatMsgEntity implements Comparable<ChatMsgEntity> {
    public static int MAX_TRY_SEND_TIME = 3;
    private String mSrcId;          //发送者eid ，或  家庭成员变化eid
    private String mDstId;          //接收者eid
    private String mWatchId;        //mWatchId值的就是手表的eid
    private int mType;             //语音类型 1 普通语音 ; 2 sos; 3 远程录音 ；4 家庭成员变化; 5 sos显示地址; 6 文字消息; 7 图片消息;9 表情 10.短信消息
    private String mFamilyId;
    private String mUserName;       //用户名
    private String mAudioPath;      //语音路径
    private String mDate;           //语音录制时间
    private int mDuration;          //语音时长，单位为'秒'
    private String mUserHeadPath;       //用户图标
    private Boolean mSelectFlag = false;  //是否长按标志
    private Boolean mIsSelected = false;  //item是否选择标志
    private Boolean mPlayed = false;      //是否播放标志
    private Boolean mIsFrom = true;       //收到或者发出标志，true表示内容是接收的，false表示内容是发送的
    private Boolean mIsClick = false;     //语音点击标志
    private int mForceRecordOk;     //远程录音是否成功  1、正在录音(或家庭成员加入)，2、录音成功(或家庭成员退出)； 3、录音失败 ；
    private AnimationDrawable mPlayAnimation = null; //每个语音对应的动画
    public ImageView mLeftRetry = null;
    private int mSended;     //语音标志，0 正在发送 1 发送成功  2 发送失败 3正在重发 4删除
    private int mTryTime = 0;    //重复次数
    private long mSendStartTime = 0;//发送起始时间
    private String mContent; //非语音消息的内容
    private String mSecurityLatLng; //安全警告时设备坐标
    private String mSecurityEfid; //安全警告时安全区域efid
    private String mSecurityDesc; //安全警告时设备位置描述

    public static final int CHAT_MESSAGE_TYPE_VOICE = 1;
    public static final int CHAT_MESSAGE_TYPE_SOS = 2;
    public static final int CHAT_MESSAGE_TYPE_RECORD = 3;
    public static final int CHAT_MESSAGE_SOS_LOCATION = 5;
    public static final int CHAT_MESSAGE_TYPE_TEXT = 6;
    public static final int CHAT_MESSAGE_TYPE_IMAGE = 7;
    public static final int CHAT_MESSAGE_TYPE_VIDEO = 8;
    public static final int CHAT_MESSAGE_TYPE_EMOJI = 9;
    public static final int CHAT_MESSAGE_TYPE_PHOTO = 10;
    public static final int CHAT_MESSAGE_TYPE_VIDEOCALL = 11;

    public static final int CHAT_SEND_STATE_SENDING = 0;
    public static final int CHAT_SEND_STATE_SUCCESS = 1;
    public static final int CHAT_SEND_STATE_FAIL = 2;
    public static final int CHAT_SEND_STATE_RETRYING = 3;
    public static final int CHAT_SEND_STATE_DELETE = 4;

    @Override
    public int compareTo(ChatMsgEntity another) {
        // TODO Auto-generated method stub
        int compareName = this.mDate.compareTo(another.getmDate());
        return compareName;
    }

    public ChatMsgEntity() {

    }

    public ChatMsgEntity(String mUserName, String mDate, int mDuration, String mUserHeadPath, Boolean mIsFrom, String mAudioPath) {
        this.mUserName = mUserName;
        this.mDate = mDate;
        this.mDuration = mDuration;
        this.mUserHeadPath = mUserHeadPath;
        this.mIsFrom = mIsFrom;
        this.mAudioPath = mAudioPath;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserHeadPath() {
        return mUserHeadPath;
    }

    public void setmUserHeadPath(String mUserHeadPath) {
        this.mUserHeadPath = mUserHeadPath;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public Boolean getmIsFrom() {
        return mIsFrom;
    }

    public void setmIsFrom(Boolean mIsFrom) {
        this.mIsFrom = mIsFrom;
    }

    public Boolean getmIsClick() {
        return mIsClick;
    }

    public void setmIsClick(Boolean mIsClick) {
        this.mIsClick = mIsClick;
    }

    public Boolean getmPlayed() {
        return mPlayed;
    }

    public void setmPlayed(Boolean mPlayed) {
        this.mPlayed = mPlayed;
    }

    public String getmAudioPath() {
        return mAudioPath;
    }

    public void setmAudioPath(String mAudioPath) {
        this.mAudioPath = mAudioPath;
    }

    public Boolean getmSelectFlag() {
        return mSelectFlag;
    }

    public void setmSelectFlag(Boolean mSelectFlag) {
        this.mSelectFlag = mSelectFlag;
    }

    public Boolean getmIsSelected() {
        return mIsSelected;
    }

    public void setmIsSelected(Boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public String getmSrcId() {
        return mSrcId;
    }

    public void setmSrcId(String mSrcId) {
        this.mSrcId = mSrcId;
    }

    public String getmDstId() {
        return mDstId;
    }

    public void setmDstId(String mDstId) {
        this.mDstId = mDstId;
    }

    public String getmWatchId() {
        return mWatchId;
    }

    public void setmWatchId(String mWatchId) {
        this.mWatchId = mWatchId;
    }

    public String getmFamilyId() {
        return mFamilyId;
    }

    public void setmFamilyId(String mFamilyId) {
        this.mFamilyId = mFamilyId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder ss = new StringBuilder();
        ss.append("mSrcId=" + getmSrcId() + "/nmDstId=" + getmDstId() + "/nmWatchId=" + getmWatchId() +
                "/nmFamilyId=" + getmFamilyId() + "/nmUserName=" + getmUserName() + "/nmAudioPath=" +
                getmAudioPath() + "/nmDate=" + getmDate() + "/nmDuration=" + getmDuration() + "/nmSelectFlag=" +
                getmSelectFlag() + "/nmIsSelected=" + getmIsSelected() + "/nmPlayed=" + getmPlayed() + "/nmIsFrom=" + getmIsFrom());
        return ss.toString();
    }

    public AnimationDrawable getmPlayAnimation() {
        return mPlayAnimation;
    }

    public void setmPlayAnimation(AnimationDrawable mPlayAnimation) {
        this.mPlayAnimation = mPlayAnimation;
    }

    public ImageView getmLeftRetry() {
        return mLeftRetry;
    }

    public void setmLeftRetry(ImageView mLeftRetry) {
        this.mLeftRetry = mLeftRetry;
    }

    public int getmSended() {
        return mSended;
    }

    public void setmSended(int mSended) {
        this.mSended = mSended;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmForceRecordOk() {
        return mForceRecordOk;
    }

    public void setmForceRecordOk(int mForceRecordOk) {
        this.mForceRecordOk = mForceRecordOk;
    }

    public int getmTryTime() {
        return mTryTime;
    }

    public void setmTryTime(int mTryTime) {
        this.mTryTime = mTryTime;
    }

    public long getmSendStartTime() {
        return mSendStartTime;
    }

    public void setmSendStartTime(long mSendStartTime) {
        this.mSendStartTime = mSendStartTime;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmSecurityLatLng(String mSecurityLatLng) {
        this.mSecurityLatLng = mSecurityLatLng;
    }

    public String getmSecurityLatLng() {
        return mSecurityLatLng;
    }

    public void setmSecurityEfid(String mSecurityEfid) {
        this.mSecurityEfid = mSecurityEfid;
    }

    public String getmSecurityEfid() {
        return mSecurityEfid;
    }

    public void setmSecurityDesc(String mSecurityDesc) {
        this.mSecurityDesc = mSecurityDesc;
    }

    public String getmSecurityDesc() {
        return mSecurityDesc;
    }
}
