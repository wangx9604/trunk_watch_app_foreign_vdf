/**
 * Creation Date:2015-1-9
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.beans;

import android.text.TextUtils;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import java.util.ArrayList;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-9
 */
public class WatchData {

    private String watchId;//序列号
    private String eid;              //手表的EID
    private String familyId; //gid

    private LocationData curLocation;

    private String nickname;
    private int sex = 0; //1 ,male 0 .female
    private String birthday = "20101212";
    private int headId;
    private double weight = 18.0;//体重
    private double height = 110.0;//身高
    private CustomData customData = new CustomData();//自定义数据
    //    private String headPath; //索引值 不是真正的path，通过索引值得到文件路径
    private String verOrg;
    private String expireTime;
    private String verCur;
    private Boolean offLine = false; //false手表在线，true手表离线
    private int battery;
    private String btMac;
    private String iccid;
    private String cellNum;//手机号
    private String imei;
    private String imsi;
    private String deviceType = "";//手表的类型 a或者c
    private String machSn;//手表整机sn

    private int simCertiStatus = 0;//默认值是已经认证  // //0表示审核通过，1表示审核失败，10表示审核中 - 1 未提交
    private int simActiveStatus = 10;//默认值是已经激活//// 帐号状态：0未激活，未开户, 10已激活，开机活跃状态,20已停机-认证不通过,25已半停机，欠费
    // 30已销户,11开户请求中，等待结果,21从激活状态发起的停机请求等待结果，22从停机状态发起的开机请求中，等待结果
    // 23从半停机状态发起的停机请求中，等待结果，26从激活状态发起的半停机请求中，等待结果
    // 27从半停机状态发起的开机请求中，等待结果，31从开机状态发起的销户请求中,等待结果
    // 32从停机状态发起的销户请求中,等待结果，33从半停机机状态发起的销户请求中,等待结果提交业务变更请求
    // 12 2/3G转4G业务在途 400已被回收
    private String iccidQRCode;
    private String imsiQRCode;
    private String imeiQRCode;
    private String qrStr;
    private String brandType;
    private int autoUpdate = 1;//手表自主升级 0不自主 1 自动

    public WatchData() {
        super();
        curLocation = new LocationData();
    }

    public int getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(int autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getMachSn() {
        return machSn;
    }

    public void setMachSn(String machSn) {
        this.machSn = machSn;
    }

    public String getBrandType() {
        return brandType;
    }

    public void setBrandType(String brandtype) {
        this.brandType = brandtype;
    }

    public String getDeviceType() {
        return deviceType;
//        return "SW203_A03";
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getSimActiveStatus() {
        return simActiveStatus;
    }

    public void setSimActiveStatus(int simActiveStatus) {
        this.simActiveStatus = simActiveStatus;
    }

    public int getSimCertiStatus() {
        if (isDevice102()) {
            return simCertiStatus;
        } else {
            return 0;
        }
    }

    public void setSimCertiStatus(int simCertiStatus) {
        this.simCertiStatus = simCertiStatus;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCellNum() {
        return cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    private ArrayList<WatchGroupMemberData> watchGroupMembers;
    private boolean isNewWatch = false;
    private int operationMode = Const.DEFAULT_OPERATIONMODE_VALUE;

    public int getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(int operationMode) {
        this.operationMode = operationMode;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    //使用custom字段保存headpath
    public String getHeadPath() {
        // return headPath;
        return customData.getHeadkey();
    }

    public void setHeadPath(String headpath) {
        //   this.headPath = headpath;
        customData.setHeadkey(headpath);
    }


    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public LocationData getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(LocationData curLocation) {
        this.curLocation = curLocation;
    }

    public String getNickname() {
        if (nickname == null || nickname.length() == 0)
            nickname = "Honey";
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEid() {
        if (TextUtils.isEmpty(eid)) {
            return "";
        } else {
            return eid;
        }
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getVerOrg() {
        return verOrg;
    }

    public void setVerOrg(String verOrg) {
        this.verOrg = verOrg;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getVerCur() {
        return verCur;
    }

    public void setVerCur(String verCur) {
        this.verCur = verCur;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public Boolean getOffLine() {
        return false;// offLine;   It is always online when using SMS
    }

    public void setOffLine(Boolean offLine) {
        this.offLine = offLine;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

    public ArrayList<WatchGroupMemberData> getWatchGroupMembers() {
        return watchGroupMembers;
    }

    public void setWatchGroupMembers(ArrayList<WatchGroupMemberData> watchGroupMembers) {
        this.watchGroupMembers = watchGroupMembers;
    }

    public boolean isNewWatch() {
        return isNewWatch;
    }

    public void setNewWatch(boolean isNewWatch) {
        this.isNewWatch = isNewWatch;
    }

    public String getBtMac() {
        return btMac;
    }

    public void setBtMac(String btMac) {
        this.btMac = btMac;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getIccid() {
        return iccid;
    }

    public String getIccidQRCode() {
        return iccidQRCode;
    }

    public void setIccidQRCode(String iccidQRCode) {
        this.iccidQRCode = iccidQRCode;
    }

    //这个判断不是严谨的判断，现在只是为了能继续使用102项目的物联卡表，各种操作不需要根据这个判断区别，
    public boolean isWuLianCard() {
        if (iccid != null && iccid.length() > 8 && iccid.substring(0, 7).equals(Const.MOBILE_WULIAN_CARD)) {
            return true;
        } else return iccid == null;
    }

    public void setImsiQRCode(String imsiQRCode) {
        this.imsiQRCode = imsiQRCode;
    }

    public String getImsiQRCode() {
        return imsiQRCode;
    }

    public void setImeiQRCode(String imeiQRCode) {
        this.imeiQRCode = imeiQRCode;
    }

    public String getImeiQRCode() {
        return imeiQRCode;
    }

    public String getQrStr() {
        return qrStr;
    }

    public void setQrStr(String qrStr) {
        this.qrStr = qrStr;
    }

    public boolean isSimCertiStatusEnable() {
        boolean result = true;
        if (simCertiStatus == -1 || simCertiStatus == 1) {
            result = false;
        }
        return result;
    }

    public boolean isDevice102() {//是否102的手表，判断方法统一在这里修改
        return getDeviceType() == null || getDeviceType().length() == 0 || getDeviceType().toUpperCase().equals("A") || getDeviceType().toUpperCase().equals("C");
    }

    public boolean isDevice105() {
        return getDeviceType() != null && (getDeviceType().equals("SW105") || getDeviceType().equals("SW106"));
    }

    public boolean isDevice106() {
        return getDeviceType() != null && getDeviceType().equals("SW106");
    }

    public boolean isDevice206() {
        return getDeviceType() != null && getDeviceType().equals("SW206_A02");
    }

    public boolean isDevice302() {
        return getDeviceType() != null && getDeviceType().equals("SW302");
    }

    public boolean isDevice306() {
        return getDeviceType() != null && (getDeviceType().equals("SW306") || getDeviceType().equals("SW306_A02"));
    }

    public boolean isDevice306_A03() {
        return getDeviceType() != null && (getDeviceType().equals("SW306_A03"));
    }

    public boolean isDevice206_A02() {
        return getDeviceType() != null && (getDeviceType().equals("SW206_A02"));
    }

    public boolean isDevice307() {
        return getDeviceType() != null && (getDeviceType().equals("SW307"));
    }

    public boolean isDevice303() {
        return getDeviceType() != null && (getDeviceType().equals("SW303"));
    }

    public boolean isDevice303_A02() {
        return getDeviceType() != null && getDeviceType().equals("SW303_A02");
    }

    public boolean isDevice706_A02() {
        return getDeviceType() != null && getDeviceType().equals("SW706_A02");
    }

    public boolean isDevice900_A03() {
        return getDeviceType() != null && getDeviceType().equals("SW900_A03");
    }

    public boolean isDevice305() {
        return getDeviceType() != null && getDeviceType().equals("SW305");
    }

    public boolean isDevice501() {
        return getDeviceType() != null && (getDeviceType().equals("SW501") || getDeviceType().equals("SW505"));
    }

    public boolean isDevice502() {
        return getDeviceType() != null && (getDeviceType().equals("SW502") || getDeviceType().equals("SW502B_A02") || getDeviceType().equals("SW502_A03"));
    }

    public boolean isDevice502B_A02() {
        return getDeviceType() != null && getDeviceType().equals("SW502B_A02");
    }

    public boolean isDevice502_A03() {
        return getDeviceType() != null && getDeviceType().equals("SW502_A03");
    }

    public boolean isDevice505() {
        return getDeviceType() != null && getDeviceType().equals("SW505");
    }

    public boolean isDevice701() {
        return getDeviceType() != null && getDeviceType().equals("SW701");
    }

    public boolean isDevice705() {
        return getDeviceType() != null && getDeviceType().equals("SW705");
    }

    public boolean isDevice703() {
        return getDeviceType() != null && (getDeviceType().equals("SW703") || getDeviceType().equals("SW760"));
    }

    public boolean isDevice710() {
        return getDeviceType() != null && (getDeviceType().equals("SW710")
                || getDeviceType().equals("SW730") || getDeviceType().equals("SW710_A03"));
    }

    public boolean isDevice730() {
        return getDeviceType() != null && getDeviceType().equals("SW730");
    }

    public boolean isDevice760() {
        return getDeviceType() != null && getDeviceType().equals("SW760");
    }

    public boolean isDevice710_A03() {
        return getDeviceType() != null && getDeviceType().equals("SW710_A03");
    }

    public boolean isDevice707() {
        return getDeviceType() != null && getDeviceType().equals("SW707")
                || getDeviceType().equals("SW712_H01");
    }

    public boolean isDevice707_H01() {
        return getDeviceType() != null && getDeviceType().equals("SW707_H01");
    }

    public boolean isDevice607() {
        return getDeviceType() != null && getDeviceType().equals("SW609")
                || getDeviceType().equals("SW607")
                || getDeviceType().equals("SW607_H01");
    }

    public boolean isDevice607_1() {
        return getDeviceType() != null && (getDeviceType().equals("SW607") || getDeviceType().equals("SW607_H01"));
    }

    public boolean isDevice709_H01() {
        return getDeviceType() != null && getDeviceType().equals("SW709_H01")
                || getDeviceType().equals("SW607")
                || getDeviceType().equals("SW609")
                || getDeviceType().equals("SW607_H01");
    }

    public boolean isDevice708() {
        return getDeviceType() != null && (getDeviceType().equals("SW708")
        );
    }

    public boolean isDevice900() {
        return getDeviceType() != null && getDeviceType().equals("SW900");
    }

    public boolean isDevice203_A03() {
        return getDeviceType() != null && getDeviceType().equals("SW203_A03");
    }

    public boolean isDevice709() {
        return getDeviceType() != null && getDeviceType().equals("SW709");
    }

    public boolean isDevice709_A03() {
        return getDeviceType() != null && getDeviceType().equals("SW709_A03");
    }

    public boolean isDevice709_A05() {
        return getDeviceType() != null && getDeviceType().equals("SW709_A05");
    }

    public boolean isDevice708_A06() {
        return getDeviceType() != null && (getDeviceType().equals("SW708_A06")
                || getDeviceType().equals("SW707_A03")
                || getDeviceType().equals("SW707_A05")
        );
    }

    public boolean isDevice708_A07() {
        return getDeviceType() != null && getDeviceType().equals("SW708_A07");
    }

    public boolean isDevice707_A05() {
        return getDeviceType() != null && (
                getDeviceType().equals("SW707_A05") || getDeviceType().equals("SW707_A03")
        );
    }

    public String getDeviceProtocolVersion() {
        if (isDevice102()) {
            return CloudBridgeUtil.PROTOCOL_FOBIDDEN;
        } else {
            return CloudBridgeUtil.SW_PROTOCOL_NUM;
        }
    }

    //手表wifi相关属性
    private String deviceWifiName;

    public String getDeviceWifiName() {
        return deviceWifiName;
    }

    public void setDeviceWifiName(String deviceWifiName) {
        this.deviceWifiName = deviceWifiName;
    }

    private boolean isWifiConnect;

    public boolean getIsWifiConnect() {
        return isWifiConnect;
    }

    public void setIsWifiConnect(boolean isWifiConnect) {
        this.isWifiConnect = isWifiConnect;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        WatchData target = (WatchData) o;
        return (target.getWatchId().equals(getWatchId()));
    }

    public boolean isSupportDownloadNotice() {
        return false;
    }

    public boolean isSupportStepNotice() {
        return !isDevice306_A03();
    }

    public boolean isSupportGroupMessage() {
        return !isDevice203_A03() && !isDevice206_A02();
    }

    public boolean isSupportPrivateMessage() {
        return !isDevice203_A03() && !isDevice206_A02();
    }

    public boolean isSupportPhoneCall() {
        return !isDevice203_A03() && !isDevice206_A02();
    }

    public boolean isSupportVideoCall() {
        return isDevice706_A02() || isDevice900_A03() || isDevice707() || isDevice708() || isDevice708_A06()
                || isDevice709() || isDevice709_A03() || isDevice900() || isDevice708_A07() || isDevice709_A05() || isDevice707_H01() || isDevice709_H01() || isDevice607();
    }

    public boolean isSupportListen() {
        return isDevice708() || isDevice708_A06() || isDevice709() || isDevice709_A03() || isDevice708_A07() || isDevice709_A05();
    }

    public boolean isSupportCallFeeSearch() {
        return getDeviceType() != null && !isDevice102() && !isDevice710_A03();
    }

    public boolean isSupportSpamSms() {
        return getDeviceType() != null && !isDevice102() && !isDevice710_A03();
    }

    public boolean isSupportStory(int visible, int storyGlobalOnOff) {
        if (visible == 1 && (isDevice302() || isDevice303() || isDevice501()
                || isDevice502() || isDevice303_A02() || isDevice305()
                || isDevice701() || (isDevice710() && !isDevice710_A03()) || isDevice703()
                || isDevice705() || isDevice307())) {
            //在单个项目的前提下，增加紧急开关的判断
            return storyGlobalOnOff == 1;
        }
        return false;
    }

    public boolean isNeedTTS() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106() || isDevice203_A03()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBindSetMode() {
        if (getDeviceType() != null && (isDevice701() || isDevice710() || isDevice705()
                || isDevice306() || isDevice307() || isDevice505()
                || isDevice703() || isDevice306_A03() || isDevice206_A02()) || isDevice709_A03()
                || isDevice708_A06() || isDevice709_A05() || isDevice708_A07() || isDevice707_H01() || isDevice709_H01()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportWatchFriend() {
        if (getDeviceType() != null && (isDevice302() || isDevice303() || isDevice303_A02() || isDevice305()
                || isDevice306() || isDevice307() //306包括306/306_A02、307包括307_A01/307_A02
                || isDevice501() || isDevice502() //502包括502/502B_A02/502_A03
                || isDevice701() || isDevice710() //710包括710/730
                || isDevice705() || isDevice703() || isDevice306_A03() || isDevice206_A02() || isDevice709_H01() || isDevice707_H01())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needSetAlarmBell() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportTraceLocation() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportSmsFilter() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106() || isDevice203_A03() || isDevice206_A02()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportFunctionControl() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106() || isDevice203_A03() || isDevice206_A02() ||
                isDevice708_A06() || isDevice709_A03() || isDevice709_A05() || isDevice708_A07()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportSlienceTime() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106() || isDevice203_A03() || isDevice206_A02()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportWifiSetting() {
        if (getDeviceType() == null || isDevice102() || isDevice105() || isDevice106() || isDevice203_A03() || isDevice206_A02()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupportFlowStatitics() {
        return false;
    }

    public boolean isSupportDeviceWifi() {
        return !isDevice203_A03() && !isDevice206_A02() && !isDevice709_A05() && !isDevice707_A05();
    }

    public boolean isSupportAlarmClock() {
        if (isDevice710_A03() || isDevice206_A02() || isDevice709_A03() || isDevice708_A06() || isDevice709_A05() || isDevice708_A07()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isWatch() {
        return !isDevice203_A03() && !isDevice206_A02();
    }

    public static int getVideoCallVersion(WatchData focusWatch) {

        if (focusWatch.isDevice710_A03())
            return 1;

        if (focusWatch.isDevice706_A02() || focusWatch.isDevice707()
                || focusWatch.isDevice708() || focusWatch.isDevice708_A06()
                || focusWatch.isDevice709() || focusWatch.isDevice709_A03()
                || focusWatch.isDevice900() || focusWatch.isDevice900_A03()
                || focusWatch.isDevice708_A07() || focusWatch.isDevice709_A05())
            return 2;
        if (focusWatch.isDevice707_H01() || focusWatch.isDevice709_H01())
            return 3;
        return 0;
    }
}
