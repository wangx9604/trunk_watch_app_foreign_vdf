package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.activitys.ScheduleCardUtils;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardBean;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.beans.CalendarEventInfo;
import com.xiaoxun.xun.motion.beans.CalendarScheduleInfoBean;
import com.xiaoxun.xun.motion.beans.ScheduleWeekBean;
import com.xiaoxun.xun.views.CustomerPickerView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarTableUtils {
    //早睡早起 sleep，上学提醒 goscl，放学提醒 outscl，自定义 custom
    public static final String CALENDAR_SLEEP = "sleep";
    public static final String CALENDAR_GOSCL = "goscl";
    public static final String CALENDAR_OUTSCL = "outscl";
    public static final String CALENDAR_CUSTOM = "custom";
    public static final String GOODS = "goods";
    public static final String WEATHER = "weather";

    public static void initMinsSelectorView(CustomerPickerView mTimeSelectorView,
                                            int select, int mType,
                                            CustomerPickerView.onSelectListener listener){
        List<String> shutdownTimes = new ArrayList<String>();
        if(mType == 1){
            for (int i = 0; i < 25; i++) {
                shutdownTimes.add(i*5<10?"0"+i*5:i*5+"");
            }
        }else{
            for (int i = 0; i < 60; i++) {
                shutdownTimes.add(i<10?"0"+i:i+"");
            }
        }
        mTimeSelectorView.setmMaxTextSize(60);
        mTimeSelectorView.setSelectTextColor(0x000000);
        mTimeSelectorView.setMarginAlphaValue((float) 3.8, "H");
        mTimeSelectorView.setData(shutdownTimes);
        mTimeSelectorView.setOnSelectListener(listener);
        mTimeSelectorView.setSelected(mType==1?select/5:select);
    }

    public static void initMinsSelectorView(CustomerPickerView mTimeSelectorView,
                                            int select, boolean isHour,
                                            CustomerPickerView.onSelectListener listener){
        List<String> shutdownTimes = new ArrayList<String>();
        if(isHour) {
            for (int i = 0; i < 24; i++) {
                shutdownTimes.add(i < 10 ? "0" + i : i + "");
            }
        }else{
            for (int i = 0; i < 60; i++) {
                shutdownTimes.add(i<10?"0"+i:i+"");
            }
        }
        mTimeSelectorView.setmMaxTextSize(60);
        mTimeSelectorView.setSelectTextColor(0x000000);
        mTimeSelectorView.setMarginAlphaValue((float) 3.8, "H");
        mTimeSelectorView.setData(shutdownTimes);
        mTimeSelectorView.setOnSelectListener(listener);
        mTimeSelectorView.setSelected(select);
    }

    public static void setSwitchViewReverseVisible(int remindFamily, View mFamilyRecyc) {
        if(remindFamily == 0){
            mFamilyRecyc.setVisibility(View.VISIBLE);
        }else{
            mFamilyRecyc.setVisibility(View.GONE);
        }
    }

    public static void setSwitchViewVisible(int remindFamily, View mFamilyRecyc) {
        if(remindFamily == 1){
            mFamilyRecyc.setVisibility(View.VISIBLE);
        }else{
            mFamilyRecyc.setVisibility(View.GONE);
        }
    }



    public static String setStrSort(String text1, String text2){
        return text1+":"+ text2;
    }

    public static void setScheduleToView(ScheduleCardBean cardBean, TextView mTvWeek0, TextView mTvWeek1,
                                         TextView mTvWeek2, TextView mTvWeek3, TextView mTvWeek4,
                                         TextView mTvWeek5, TextView mTvWeek6,
                                         TextView mTvWeekTime0, TextView mTvWeekTime1, TextView mTvWeekTime2,
                                         TextView mTvWeekTime3, TextView mTvWeekTime4, TextView mTvWeekTime5,
                                         TextView mTvWeekTime6,
                                         CardView mCvLayout0,CardView mCvLayout1,CardView mCvLayout2,
                                         CardView mCvLayout3,CardView mCvLayout4,CardView mCvLayout5,
                                         CardView mCvLayout6) {
        if(cardBean == null) return;
        ArrayList<CalendarScheduleInfoBean> mSheduleInfoList = new ArrayList<>();
        //1:周内课程 周内课程，课程和时间分开，先计算课程，再输入时间
        for(int i = 0;i<5;i++){
            String[] mWeekList = cardBean.getWeeklist().get(i).split(",");
            CalendarScheduleInfoBean mBeans = new CalendarScheduleInfoBean(false);
            for (int i1 = 0; i1 < mWeekList.length; i1++) {
                if(!"N/A".equals(mWeekList[i1])){
                    mBeans.setHasClass(true);
                    mBeans.setmClassName(mWeekList[i1]);
                    mBeans.setmLocInClassTime(i1);
                }
            }
            if(mBeans.isHasClass()) {
                String[] mTimeArray = cardBean.getTimelist().get(mBeans.getmLocInClassTime()).split(",");
                mBeans.setmLeavteTime(mTimeArray[1]);
            }
            mSheduleInfoList.add(mBeans);
        }

        //2:周末课程
        ArrayList<ScheduleWeekBean> mWeekArray = ScheduleCardUtils.getScheduleWeekListByWeekList(cardBean.getOthers());
        mSheduleInfoList.add(onGetCalendarScheduleInfoFromWeek(mWeekArray,6));
        mSheduleInfoList.add(onGetCalendarScheduleInfoFromWeek(mWeekArray,7));
        //3:绑定视图数据
        setDataToView(mTvWeek0, mTvWeekTime0, mCvLayout0, mSheduleInfoList.get(6));
        setDataToView(mTvWeek1, mTvWeekTime1, mCvLayout1, mSheduleInfoList.get(0));
        setDataToView(mTvWeek2, mTvWeekTime2, mCvLayout2, mSheduleInfoList.get(1));
        setDataToView(mTvWeek3, mTvWeekTime3, mCvLayout3, mSheduleInfoList.get(2));
        setDataToView(mTvWeek4, mTvWeekTime4, mCvLayout4, mSheduleInfoList.get(3));
        setDataToView(mTvWeek5, mTvWeekTime5, mCvLayout5, mSheduleInfoList.get(4));
        setDataToView(mTvWeek6, mTvWeekTime6, mCvLayout6, mSheduleInfoList.get(5));
    }

    private static void setDataToView(TextView mTvWeek0, TextView mTvWeekTime0, CardView mCvlayout,
                                      CalendarScheduleInfoBean calendarScheduleInfoBean) {
        if(calendarScheduleInfoBean.isHasClass()) {
            mCvlayout.setVisibility(View.VISIBLE);
        }else{
            mCvlayout.setVisibility(View.INVISIBLE);
        }
        mTvWeek0.setText(calendarScheduleInfoBean.getmClassName());
        mTvWeekTime0.setText(ScheduleCardUtils.ScheduleGetHourFormat(calendarScheduleInfoBean.getmLeavteTime()));
    }

    private static CalendarScheduleInfoBean onGetCalendarScheduleInfoFromWeek(ArrayList<ScheduleWeekBean> mWeekArray, int mType) {
        ArrayList<ScheduleWeekBean> mWeek6List = new ArrayList<>();
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            if (scheduleWeekBean.getmWeekNum() == mType) {
                mWeek6List.add(scheduleWeekBean);
            }
        }
        Collections.sort(mWeek6List);
        CalendarScheduleInfoBean mBeans = new CalendarScheduleInfoBean(false);
        if(mWeek6List.size() == 0){
            return mBeans;
        }else{
            ScheduleWeekBean mWeek = mWeek6List.get(mWeek6List.size()-1);
            mBeans.setmLeavteTime(mWeek.getmEndTime());
            mBeans.setHasClass(true);
            mBeans.setmClassName(mWeek.getmWeekClassName());
        }
        return mBeans;
    }

    public static String getMonthShowByType(Context context, int curMonth) {
        switch (curMonth){
            case 1:
            default:
                return context.getString(R.string.month_0);
            case 2:
                return context.getString(R.string.month_1);
            case 3:
                return context.getString(R.string.month_2);
            case 4:
                return context.getString(R.string.month_3);
            case 5:
                return context.getString(R.string.month_4);
            case 6:
                return context.getString(R.string.month_5);
            case 7:
                return context.getString(R.string.month_6);
            case 8:
                return context.getString(R.string.month_7);
            case 9:
                return context.getString(R.string.month_8);
            case 10:
                return context.getString(R.string.month_9);
            case 11:
                return context.getString(R.string.month_10);
            case 12:
                return context.getString(R.string.month_11);
        }
    }

    public static int isHasSelectByList(ArrayList<MemberUserData> userData) {
        int hasSelect = 0;
        for (MemberUserData userDatum : userData) {
            if(userDatum.isSelect()) hasSelect++;
        }

        return hasSelect;
    }

    public static int onHasRemindInfoByType(ArrayList<String> remind_dts, String weather) {
        for (String remind_dt : remind_dts) {
            if(remind_dt.equals(weather))
                return 1;
        }

        return 0;
    }

    public static ArrayList<String> onCreateRemindDts(int mWeatherOnOff, int mThingOnOff) {
        ArrayList<String> mRemindDts = new ArrayList<>();
        if(mWeatherOnOff == 1){
            mRemindDts.add("weather");
        }
        if(mThingOnOff == 1){
            mRemindDts.add("goods");
        }

        return mRemindDts;
    }

    public static void getCalendarAllTnfoToCloud(ImibabyApp myApp, MsgCallback msgCallback){
        JSONObject pl = new JSONObject();
        pl.put("eid", myApp.getCurUser().getFocusWatch().getEid());
        pl.put("type", "all");

        JSONObject reqJson = new JSONObject();
        reqJson.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_GET_ALL_CALENDAR);
        reqJson.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        reqJson.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
        reqJson.put(CloudBridgeUtil.KEY_NAME_SID, myApp.getToken());

        MyMsgData req = new MyMsgData();
        req.setReqMsg(reqJson);
        req.setCallback(msgCallback);
        req.setTimeout(30 * 1000);
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(req);

    }

    public static void sendSetCalendarInfoToCloud(ImibabyApp myApp, String mType, int optype, String mCaId,
                                                  String remind, MsgCallback msgCallback){
        JSONObject remindTran = (JSONObject) JSONValue.parse(remind);
        JSONObject pl = new JSONObject();
        pl.put("eid", myApp.getCurUser().getFocusWatch().getEid());
        pl.put("id", mCaId);
        pl.put("type", mType);
        pl.put("optype", optype);
        pl.put("remind",remindTran);

        JSONObject reqJson = new JSONObject();
        reqJson.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_SET_CALENDAR);
        reqJson.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        reqJson.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
        reqJson.put(CloudBridgeUtil.KEY_NAME_SID, myApp.getToken());

        MyMsgData req = new MyMsgData();
        req.setReqMsg(reqJson);
        req.setCallback(msgCallback);
        req.setTimeout(30 * 1000);
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(req);
    }

    public static ArrayList<String> getFamilyMemInfo(ArrayList<MemberUserData> mMemberList) {
        ArrayList<String> mMemInfo = new ArrayList<>();
        for (MemberUserData memberUserData : mMemberList) {
            if(memberUserData.isSelect()){
                mMemInfo.add(memberUserData.getEid());
            }
        }

        return mMemInfo;
    }

    public static int onHasCalendarInfoByType(String mInfo, String sleep) {
        if("".equals(mInfo)) return 0;
        try {
            JSONObject pl = (JSONObject) JSONValue.parse(mInfo);
            JSONArray list = (JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                String mType = (String) jsonObject.get("type");
                if (mType.equals(sleep)) {
                    return 1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public static String getCalendarInfoByType(String mCalendarInfo, String sleep) {
        try {
            JSONObject pl = (JSONObject) JSONValue.parse(mCalendarInfo);
            JSONArray list = (JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                String mType = (String) jsonObject.get("type");
                if (mType.equals(sleep)) {
                    return (String) jsonObject.get("remind").toString();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static void getPhoneWhiteList(ImibabyApp myApp, String mWatchEid) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单

                    String sTmp = (String) maggetPl.get(CloudBridgeUtil.PHONE_WHITE_LIST);
                    if (sTmp != null && sTmp.length() > 0) {
                        //持久化
                        myApp.setValue(mWatchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY,
                                CloudBridgeUtil.genContactListJsonStr(
                                        CloudBridgeUtil.parseContactListFromJsonStr(sTmp)
                                ));
                    }
                }
            }
        });
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mWatchEid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.PHONE_WHITE_LIST);
        queryGroupsMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }


    public static String getCalendarIdByType(String mCalendarInfo, String sleep) {
        try {
            JSONObject pl = (JSONObject) JSONValue.parse(mCalendarInfo);
            JSONArray list = (JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                String mType = (String) jsonObject.get("type");
                if (mType.equals(sleep)) {
                    return (String) jsonObject.get("id");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static void setEventHandler(CalendarEventInfo mEventInfo,
                                       TextView mTvFlagSleep,
                                       TextView mTvFlagToSleep,
                                       TextView mTvFlagLeaveSchool) {
        switch (mEventInfo.getmEventType()){
            case CALENDAR_SLEEP:
                setSwitchViewVisible(mEventInfo.getmEventOptype()>1?0:1, mTvFlagSleep);
                break;
            case CALENDAR_GOSCL:
                setSwitchViewVisible(mEventInfo.getmEventOptype()>1?0:1, mTvFlagToSleep);
                break;
            case CALENDAR_OUTSCL:
                setSwitchViewVisible(mEventInfo.getmEventOptype()>1?0:1, mTvFlagLeaveSchool);
                break;
        }
    }

    public static String onCalcCalendarDate(int curYear, int curMonth, int curDay) {
        String strYear = String.valueOf(curYear);
        String strMonth = curMonth<10?"0"+curMonth:String.valueOf(curMonth);
        String strDay = curDay<10?"0"+curDay:String.valueOf(curDay);
        return strYear+ strMonth + strDay;
    }

    private static boolean isHasShowByWeekSet(int mCurWeek, String days) {
        String[] mDataVaule = {"0","0","0","0","0","0","0"};
        LogUtil.e("isHasShowByWeekSet:"+mCurWeek+":"+days);
        for (int i = 0; i < mDataVaule.length; i++) {
            mDataVaule[i] = days.substring(i,i+1);
        }

        return mDataVaule[mCurWeek].equals("1");
    }

    private static ArrayList<String> getCalendarCustomListInfoByType(String mCalendarInfo) {
        ArrayList<String> mCustomList = new ArrayList<>();
        try {
            JSONObject pl = (JSONObject) JSONValue.parse(mCalendarInfo);
            JSONArray list = (JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                String mType = (String) jsonObject.get("type");
                if (mType.equals(CALENDAR_CUSTOM)) {
                    mCustomList.add(jsonObject.toString());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return mCustomList;
        }
        return mCustomList;
    }

    public static String getCalendarInfoByType(String mCalendarInfo, String calendarCustom,
                                               String mCalendarId) {
        try {
            JSONObject pl = (JSONObject) JSONValue.parse(mCalendarInfo);
            JSONArray list = (JSONArray) pl.get("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = (JSONObject) list.get(i);
                String mType = (String) jsonObject.get("type");
                String mId = (String) jsonObject.get("id");
                if (mType.equals(calendarCustom) && mId.equals(mCalendarId)) {
                    return (String) jsonObject.get("remind").toString();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static ArrayList<MemberUserData> getMemberUserData(ImibabyApp myApp, String watchEid) {
        ArrayList<MemberUserData> mMemUserData = new ArrayList<>();

        ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(
                myApp.getStringValue(watchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null)
        );
        ArrayList<MemberUserData> tmpMemberList = new ArrayList<>(
                myApp.getCurUser().getUserDataByFamily(
                myApp.getCurUser().getFocusWatch().getFamilyId())
        );
        for (PhoneNumber phoneNumber : mBindWhiteList) {
            MemberUserData data = getWhiteMember(phoneNumber, tmpMemberList);
            if (data != null) {
                mMemUserData.add(data);
                tmpMemberList.remove(data);
            }
        }
        mMemUserData.addAll(tmpMemberList);

        return mMemUserData;
    }

    public static MemberUserData getWhiteMember(PhoneNumber userData, ArrayList<MemberUserData> phoneNumberList) {
        for (MemberUserData tmp : phoneNumberList) {
            if (userData.userEid != null && tmp.getEid() != null && userData.userEid.equals(tmp.getEid())) {
                return tmp;
            }
        }
        return null;
    }
}
