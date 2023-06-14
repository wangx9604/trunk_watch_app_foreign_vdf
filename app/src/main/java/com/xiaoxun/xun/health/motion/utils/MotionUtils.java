package com.xiaoxun.xun.health.motion.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.beans.RecordDetailBean;
import com.xiaoxun.xun.motion.views.LayoutStateView;
import com.xiaoxun.xun.motion.views.SelectStateView;
import com.xiaoxun.xun.networkv2.beans.MotionReportBean;
import com.xiaoxun.xun.networkv2.beans.MotionSettingBean;
import com.xiaoxun.xun.networkv2.beans.MotionSportRecordBeans;
import com.xiaoxun.xun.networkv2.beans.SportRecordBean;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.views.CustomSelectorDialog;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class MotionUtils {

    public static final int Motion_Sport_Type_5 = 5;//户外跑
    public static final int Motion_Sport_Type_6 = 6;//跳绳
    public static final int Motion_Sport_Type_7 = 7;//游泳
    public static final int Motion_Sport_Type_8 = 8;//骑行
    public static final int Motion_Sport_Type_9 = 9;//健走
    public static final int Motion_Sport_Type_10 = 10;//登山
    public static final int Motion_Sport_Type_11 = 11;//仰卧起坐
    public static final int Motion_Sport_Type_101 = 101;//羽毛球
    public static final int Motion_Sport_Type_102 = 102;//篮球
    public static final int Motion_Sport_Type_103 = 103;//足球
    public static final int Motion_Sport_Type_104 = 104;//舞蹈
    public static final int Motion_Sport_Type_105 = 105;//高尔夫
    public static final int Motion_Sport_Type_106 = 106;//马术
    public static final int Motion_Sport_Type_107 = 107;
    public static final int Motion_Sport_Type_108 = 108;
    public static final int Motion_Sport_Type_109 = 109;
    public static final int Motion_Sport_Type_110 = 110;
    public static final int Motion_Sport_Type_111 = 111;
    public static final int Motion_Sport_Type_112 = 112;
    public static final int Motion_Sport_Type_113 = 113;
    public static final int Motion_Sport_Type_114 = 114;
    public static final int Motion_Sport_Type_115 = 115;
    public static final int Motion_Sport_Type_116 = 116;
    public static final int Motion_Sport_Type_117 = 117;
    public static final int Motion_Sport_Type_118 = 118;
    public static final int Motion_Sport_Type_119 = 119;
    public static final int Motion_Sport_Type_120 = 120;
    public static final int Motion_Sport_Type_121 = 121;
    public static final int Motion_Sport_Type_122 = 122;
    public static final int Motion_Sport_Type_123 = 123;

    public static final String MOTION_BODY_HEIGHT = "body_height";
    public static final String MOTION_BODY_WEIGHT = "body_weight";

    public static final String MOTIONBODYPARAMSTYPE = "body_type";
    public static final String MOTIONBODYPARAMSCOMETOSET = "body_come_to_set";



    public static int hasTargetSelectSum(SelectStateView mSSVTarget0, SelectStateView mSSVTarget1, SelectStateView mSSVTarget2) {
        int mSelectSum = 0;
        if(mSSVTarget0.isSelect()) mSelectSum++;
        if(mSSVTarget1.isSelect()) mSelectSum++;
        if(mSSVTarget2.isSelect()) mSelectSum++;
        return mSelectSum;

        
    }

    public static void setSelectStateView(Context mContext, SelectStateView mSSVTarget0, 
                                          SelectStateView mSSVTarget1, 
                                          SelectStateView mSSVTarget2, 
                                          SelectStateView mSSVCurTarget) {
        if(mSSVCurTarget.isSelect()){
            mSSVCurTarget.setSelect(false);
        }else{
            mSSVCurTarget.setSelect(true);
        }
    }

    public static void ShowSelectMinsDialog(Context mContext, String mTitle, String mDefaultValue,
                                            InterfacesUtil.UpdateViewData updateViewData) {
        new CustomSelectorDialog.Builder(mContext)
                .setmDailogType(2)
                .setTitle(mTitle)
                .setmMinsType(2)
                .setmDefValue(mDefaultValue)
                .setTopConfirmLayout(true)
                .setmSmallBtnRightListener(updateViewData).build().show();
    }

    public static MotionReportBean.MotionSportBean getReportByType(
            ArrayList<MotionReportBean.MotionSportBean> sportList, int i) {
        if(sportList == null || sportList.size() == 0) return new MotionReportBean.MotionSportBean();
        for (MotionReportBean.MotionSportBean motionSportBean : sportList) {
            if(motionSportBean.getType() == i){
                return motionSportBean;
            }
        }
        return new MotionReportBean.MotionSportBean();
    }

    public static MotionReportBean.MotionStepBean getReportByName(
            ArrayList<MotionReportBean.MotionStepBean> stepsList, String mType) {
        if(stepsList == null || stepsList.size() == 0) return new MotionReportBean.MotionStepBean();
        for (MotionReportBean.MotionStepBean motionStepBean : stepsList) {
            if(mType.equals(motionStepBean.getTag())){
                return motionStepBean;
            }
        }

        return new MotionReportBean.MotionStepBean();
    }

    public static void getMotonScoreInfo(ImibabyApp myApp, String eid,
                                         MsgCallback msgCallback) {
        String[] keys = new String[2];
        keys[0] = CloudBridgeUtil.MOTION_TOTAL_SCORE;
        keys[1] = CloudBridgeUtil.MOTION_DAILY_SCORE;
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapMGetMsg(eid,keys, msgCallback);
        }
    }

    public static void getMotonScheduleInfo(ImibabyApp myApp, String mWatchEid,
            MsgCallback msgCallback) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(msgCallback);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mWatchEid);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.MOTION_SCHEDULE_LIST);
        queryGroupsMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
                        Long.valueOf(TimeUtil.getTimeStampLocal()).intValue(),
                        myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    public static String getScheduleStateByType(Context mContext,
                                                ArrayList<String> sport_strength,
                                                String sType) {
        String mStateInfo = mContext.getString(R.string.motion_no_setting);

        if(sport_strength == null) return mStateInfo;
        for (String s1 : sport_strength) {
            String[] mSportTemp = s1.split(",");
            if(sType.equals(mSportTemp[0])){
                mStateInfo = mSportTemp[1]+"";
                break;
            }
        }

        return mStateInfo;
    }

    public static void setMainExeMotionStateView(Context mContext,
                                                 ArrayList<String> sport_test,
                                                 SelectStateView mSSView0,
                                                 SelectStateView mSSView1,
                                                 SelectStateView mSSView2) {
        if(sport_test == null) return;
        ArrayList<String> mOpenExeMotionList = new ArrayList<>();

        for (String s : sport_test) {
            String[] mTemp = s.split(",");
            if(mTemp[1].equals("1")){
                mOpenExeMotionList.add(getExeMotionByTyoe(mContext, mTemp[0]));
            }
        }

        switch (mOpenExeMotionList.size()){
            default:
                mSSView0.setVisibility(View.GONE);
                mSSView1.setVisibility(View.GONE);
                mSSView2.setVisibility(View.GONE);
                break;
            case 1:
                mSSView0.setText(mOpenExeMotionList.get(0));
                mSSView0.setVisibility(View.VISIBLE);
                mSSView1.setVisibility(View.GONE);
                mSSView2.setVisibility(View.GONE);
                break;
            case 2:
                mSSView0.setText(mOpenExeMotionList.get(0));
                mSSView1.setText(mOpenExeMotionList.get(1));
                mSSView0.setVisibility(View.VISIBLE);
                mSSView1.setVisibility(View.VISIBLE);
                mSSView2.setVisibility(View.GONE);
                break;
            case 3:
                mSSView0.setText(mOpenExeMotionList.get(0));
                mSSView1.setText(mOpenExeMotionList.get(1));
                mSSView2.setText(mOpenExeMotionList.get(2));
                mSSView0.setVisibility(View.VISIBLE);
                mSSView1.setVisibility(View.VISIBLE);
                mSSView2.setVisibility(View.VISIBLE);
                break;
        }
    }

    private static String getExeMotionByTyoe(Context mContext, String s) {
        String mMotionTitle = mContext.getString(R.string.motion_target_0);
        switch (s){
            case "5":
                mMotionTitle = mContext.getString(R.string.motion_target_0);
                break;
            case "6":
                mMotionTitle = mContext.getString(R.string.motion_target_1);
                break;
            case "11":
                mMotionTitle = mContext.getString(R.string.motion_target_2);
                break;
        }
        return mMotionTitle;
    }

    public static MotionSettingBean getMotionDataByLocal(ImibabyApp myApp, String eid) {
        String mSportList = myApp.getStringValue(CloudBridgeUtil.MOTION_SCHEDULE_LIST+eid,"");
        return new Gson().fromJson(mSportList, MotionSettingBean.class);
    }

    public static void setShowMotionStrengerView(TextView mTvSetValue,
                                                 SelectStateView mSSVMotion0,
                                                 SelectStateView mSSVMotion1,
                                                 SelectStateView mSSVMotion2,
                                                 int i) {
        switch (i){
            case 20:
                mTvSetValue.setText("20");
                mSSVMotion0.setSelect(true);
                mSSVMotion1.setSelect(false);
                mSSVMotion2.setSelect(false);
                break;
            case 30:
                mTvSetValue.setText("30");
                mSSVMotion0.setSelect(false);
                mSSVMotion1.setSelect(true);
                mSSVMotion2.setSelect(false);
                break;
            case 40:
                mTvSetValue.setText("40");
                mSSVMotion0.setSelect(false);
                mSSVMotion1.setSelect(false);
                mSSVMotion2.setSelect(true);
                break;
        }
    }

    public static boolean getScheduleSelectByType(ArrayList<String> sport_test, String s) {
        for (String s1 : sport_test) {
            String[] mTemp = s1.split(",");
            if(mTemp[0].equals(s) && "1".equals(mTemp[1])){
                return true;
            }
        }

        return false;
    }

    public static ArrayList<String> saveToTestList(SelectStateView mSSVTarget0,
                                                   SelectStateView mSSVTarget1,
                                                   SelectStateView mSSVTarget2) {
        ArrayList<String> mTemp = new ArrayList<>();
        if(mSSVTarget0.isSelect()){
            mTemp.add("5,1");
        }else{
            mTemp.add("5,0");
        }
        if(mSSVTarget1.isSelect()){
            mTemp.add("6,1");
        }else{
            mTemp.add("6,0");
        }
        if(mSSVTarget2.isSelect()){
            mTemp.add("11,1");
        }else{
            mTemp.add("11,0");
        }

        return mTemp;
    }

    public static ArrayList<String> saveToScheduleList(Context mContext,
                                                       LayoutStateView mLayoutState1,
                                                       LayoutStateView mLayoutState2,
                                                       LayoutStateView mLayoutState3,
                                                       LayoutStateView mLayoutState4,
                                                       LayoutStateView mLayoutState5) {
        ArrayList<String> mTemp = new ArrayList<>();
        if(!mLayoutState1.getTvState().equals(mContext.getString(R.string.motion_no_setting))){
            mTemp.add("5,"+mLayoutState1.getTvState());
        }
        if(!mLayoutState2.getTvState().equals(mContext.getString(R.string.motion_no_setting))){
            mTemp.add("6,"+mLayoutState2.getTvState());
        }
        if(!mLayoutState3.getTvState().equals(mContext.getString(R.string.motion_no_setting))){
            mTemp.add("11,"+mLayoutState3.getTvState());
        }
        if(!mLayoutState4.getTvState().equals(mContext.getString(R.string.motion_no_setting))){
            mTemp.add("8,"+mLayoutState4.getTvState());
        }
        if(!mLayoutState5.getTvState().equals(mContext.getString(R.string.motion_no_setting))){
            mTemp.add("9,"+mLayoutState5.getTvState());
        }
        return mTemp;
    }

    public static String getSchdeleSum(Context mContext, ArrayList<String> sport_strength) {
        int mSumValue = 0;
        for (String s : sport_strength) {
            String[] mTemp = s.split(",");
            mSumValue+=Integer.valueOf(mTemp[1]);
        }
        return mContext.getString(R.string.motion_main_title_3, mSumValue);
    }

    public static String getStatValue(Context mContext,String scheduleStateByType) {
        if(scheduleStateByType.equals(mContext.getString(R.string.motion_no_setting))){
            return "10";
        }else{
            return scheduleStateByType;
        }
    }

    public static ArrayList<String> resetScheduleStateByType(ArrayList<String> sport_strength, String s, String value) {
        boolean isHasState = false;
        ArrayList<String> mNewList = new ArrayList<>();
        for (String s1 : sport_strength) {
           String[] mTemp = s1.split(",");
           if(s.equals(mTemp[0])){
               isHasState = true;
               s1 = mTemp[0]+","+value;
           }
           mNewList.add(s1);
        }
        if(!isHasState){
            mNewList.add(s+","+value);
        }

        return mNewList;
    }

    public static ArrayList<String> getRequestMonthFromList(ImibabyApp myApp,
                                                            String mMotionValue,
                                                            String eid,
                                                            String getmStateInfo) {
        ArrayList<String> mMonthList = new ArrayList();
        String mTemp = myApp.getStringValue(mMotionValue+eid, "[]");
        JSONArray mOldArray = (JSONArray) JSONValue.parse(mTemp);
        JSONArray mNewArray = (JSONArray) JSONValue.parse(getmStateInfo);

        //1:获取没有获取的月份
        for (int i = 0; i < mNewArray.size(); i++) {
            boolean isHasUpdate = false;
            String mNewItem = (String)mNewArray.get(i);
            for (int i1 = 0; i1 < mOldArray.size(); i1++) {
                String mOldItem = (String)mOldArray.get(i1);
                if(mOldItem.equals(mNewItem)){
                    isHasUpdate = true;
                    break;
                }
            }
            if(!isHasUpdate){
                mMonthList.add(mNewItem);
            }
        }
        //2：处理当月特殊的请求
        boolean isHasCurMonth = false;
        String mCurMonth =TimeUtil.getMonth().substring(0,6);
        for (String s : mMonthList) {
            if(s.equals(mCurMonth)){
                isHasCurMonth = true;
                break;
            }
        }
        if(!isHasCurMonth){
            for (int i = 0; i < mNewArray.size(); i++) {
                String mNewItem = (String) mNewArray.get(i);
                if(mNewItem.equals(mCurMonth)){
                   mMonthList.add(mCurMonth);
                   break;
                }
            }
        }
        myApp.setValue(mMotionValue+eid, getmStateInfo);
        return mMonthList;
    }

    public static ArrayList<SportRecordBean> onSportRecordToAdapterItemData(Context context, List<MotionSportRecordBeans> mList
                                                      ) {
        ArrayList<SportRecordBean> mAdapterList = new ArrayList<>();
        int mSubKal = 0;
        int mSubTime = 0;
        for (int i = 0; i < mList.size(); i++) {
            MotionSportRecordBeans mMotionBean = mList.get(i);
            if(i == 0) {
                //1:月数据头
                SportRecordBean mSportItemBean = new SportRecordBean();
                mSportItemBean.setmRecordType(0);
                mSportItemBean.setmDate(mMotionBean.getRecordmonth());
                mAdapterList.add(mSportItemBean);
                //2:汇总数据
                mSportItemBean = new SportRecordBean();
                mSportItemBean.setmRecordType(1);
                mSportItemBean.setmDate(mMotionBean.getRecordmonth());
                mAdapterList.add(mSportItemBean);
            }
            if(i == mList.size() - 1){
                //3:最后一条数据
                SportRecordBean mSportItemBean = new SportRecordBean();
                mSportItemBean.setmRecordType(3);
                mSportItemBean.setmSubType(mMotionBean.getType());
                mSportItemBean.setmSubHeadIcon(getHeadIconByType(mMotionBean.getType(), mMotionBean.getIstest()));
                mSportItemBean.setmSubValue(getValueByType(mMotionBean, context,false,true));
                mSportItemBean.setmSubValueUnit(getValueUnitByType(mMotionBean, context, true));
                mSportItemBean.setmSubDurTime(TextUtils.isEmpty(mMotionBean.getDuration())?
                        (int)TimeUtil.compareToDiffForTwoTime(mMotionBean.getStime(), mMotionBean.getEtime())*1000:
                        Integer.valueOf(mMotionBean.getDuration())*1000);
                mSportItemBean.setmSubKal(Integer.valueOf(mMotionBean.getCalorie()));
                mSportItemBean.setmSubDate(mMotionBean.getTime());
                mSportItemBean.setmRecordId(mMotionBean.getId());
                mSportItemBean.setSportType(mMotionBean.getType());
                mSportItemBean.setIsTest(mMotionBean.getIstest());
                mSportItemBean.setTestScore(mMotionBean.getTestscore());
                mAdapterList.add(mSportItemBean);
            }else{
                //4：中间数据
                SportRecordBean mSportItemBean = new SportRecordBean();
                mSportItemBean.setmRecordType(2);
                mSportItemBean.setmSubType(mMotionBean.getType());
                mSportItemBean.setmSubHeadIcon(getHeadIconByType(mMotionBean.getType(), mMotionBean.getIstest()));
                mSportItemBean.setmSubValue(getValueByType(mMotionBean, context,false,true));
                mSportItemBean.setmSubValueUnit(getValueUnitByType(mMotionBean, context, true));
                mSportItemBean.setmSubDurTime(TextUtils.isEmpty(mMotionBean.getDuration())?
                        (int)TimeUtil.compareToDiffForTwoTime(mMotionBean.getStime(), mMotionBean.getEtime())*1000:
                        Integer.valueOf(mMotionBean.getDuration())*1000);
                mSportItemBean.setmSubKal(Integer.valueOf(mMotionBean.getCalorie()));
                mSportItemBean.setmSubDate(mMotionBean.getTime());
                mSportItemBean.setmRecordId(mMotionBean.getId());
                mSportItemBean.setSportType(mMotionBean.getType());
                mSportItemBean.setIsTest(mMotionBean.getIstest());
                mSportItemBean.setTestScore(mMotionBean.getTestscore());
                mAdapterList.add(mSportItemBean);
            }
            mSubKal += Integer.valueOf(mMotionBean.getCalorie());
            mSubTime += TextUtils.isEmpty(mMotionBean.getDuration())?
                    (int)TimeUtil.compareToDiffForTwoTime(mMotionBean.getStime(), mMotionBean.getEtime()):Integer.valueOf(mMotionBean.getDuration());
        }
        if(mAdapterList.size() > 2){
            SportRecordBean mSportItemBean = mAdapterList.get(1);
            mSportItemBean.setmSumKal(mSubKal);
            mSportItemBean.setmSumMin(mSubTime);
            mSportItemBean.setmSumNum(mAdapterList.size()-2);
        }
        return mAdapterList;
    }

    public static String getValueUnitByType(MotionSportRecordBeans mMotionBean, Context context,
                                            boolean isTestName) {
        if("1".equals(mMotionBean.getIstest())){
            if(isTestName) {
                return "";
            }else{
                return context.getString(R.string.fraction);
            }
        }
        switch (mMotionBean.getType()){
            case Motion_Sport_Type_5:
            case Motion_Sport_Type_8:
            case Motion_Sport_Type_9:
            case Motion_Sport_Type_10:
                return context.getString(R.string.unit_kilometer);
            case Motion_Sport_Type_7:
                return context.getString(R.string.unit_meter);
            case Motion_Sport_Type_6:
            case Motion_Sport_Type_11:
                return context.getString(R.string.sport_rope_jump_unit);
            case Motion_Sport_Type_101:
            case Motion_Sport_Type_102:
            case Motion_Sport_Type_103:
            case Motion_Sport_Type_104:
            case Motion_Sport_Type_105:
            case Motion_Sport_Type_106:
            case Motion_Sport_Type_107:
            case Motion_Sport_Type_108:
            case Motion_Sport_Type_109:
            case Motion_Sport_Type_110:
            case Motion_Sport_Type_111:
            case Motion_Sport_Type_112:
            case Motion_Sport_Type_113:
            case Motion_Sport_Type_114:
            case Motion_Sport_Type_115:
            case Motion_Sport_Type_116:
            case Motion_Sport_Type_117:
            case Motion_Sport_Type_118:
            case Motion_Sport_Type_119:
            case Motion_Sport_Type_120:
            case Motion_Sport_Type_121:
            case Motion_Sport_Type_122:
            case Motion_Sport_Type_123:
            default:
                return context.getString(R.string.steps_unit_kiloCard);
        }
    }

    public static String getValueByType(MotionSportRecordBeans mMotionBean,
                                        Context context, boolean isName,
                                        boolean isTestName) {
        if("1".equals(mMotionBean.getIstest()) ){
            switch (mMotionBean.getType()){
                case Motion_Sport_Type_5:
                default:
                    if(isTestName) {
                        return context.getString(R.string.motion_target_0);
                    }else{
                        return mMotionBean.getTestscore();
                    }
                case Motion_Sport_Type_6:
                    if(isTestName) {
                        return context.getString(R.string.motion_target_1);
                    }else{
                        return mMotionBean.getTestscore();
                    }
                case Motion_Sport_Type_11:
                    if(isTestName) {
                        return context.getString(R.string.motion_target_2);
                    }else{
                        return mMotionBean.getTestscore();
                    }
            }
        }
        switch (mMotionBean.getType()){
            case Motion_Sport_Type_5:
                if(isName){
                    return context.getString(R.string.motion_func_1);
                }else {
                    String mDisStr = mMotionBean.getDistance();
                    return formatFloat2String(mDisStr);
                }
            case Motion_Sport_Type_6:
                if(isName){
                    return context.getString(R.string.motion_func_2);
                }else {
                    return mMotionBean.getCount();
                }
            case Motion_Sport_Type_7:
                if(isName){
                    return context.getString(R.string.motion_fun_6);
                }else {
                    return mMotionBean.getDistance();
                }
            case Motion_Sport_Type_11:
                if(isName){
                    return context.getString(R.string.motion_func_3);
                }else {
                    return mMotionBean.getCount();
                }
            case Motion_Sport_Type_8:
                if(isName){
                    return context.getString(R.string.motion_func_4);
                }else {
                    return mMotionBean.getDistance();
                }
            case Motion_Sport_Type_9:
                if(isName){
                    return context.getString(R.string.motion_func_5);
                }else {
                    return mMotionBean.getDistance();
                }
            case Motion_Sport_Type_10:
                if(isName){
                    return context.getString(R.string.motion_fun_7);
                }else {
                    return mMotionBean.getDistance();
                }
            default:
                if(isName){
                    switch (mMotionBean.getType()) {
                        case Motion_Sport_Type_101:
                            return context.getString(R.string.motion_fun_8);
                        case Motion_Sport_Type_102:
                            return context.getString(R.string.motion_fun_9);
                        case Motion_Sport_Type_103:
                            return context.getString(R.string.motion_fun_10);
                        case Motion_Sport_Type_104:
                            return context.getString(R.string.motion_fun_11);
                        case Motion_Sport_Type_105:
                            return context.getString(R.string.motion_fun_12);
                        case Motion_Sport_Type_106:
                            return context.getString(R.string.motion_fun_13);
                        case Motion_Sport_Type_107:
                            return context.getString(R.string.motion_fun_new_07);
                        case Motion_Sport_Type_108:
                            return context.getString(R.string.motion_fun_new_08);
                        case Motion_Sport_Type_109:
                            return context.getString(R.string.motion_fun_new_09);
                        case Motion_Sport_Type_110:
                            return context.getString(R.string.motion_fun_new_10);
                        case Motion_Sport_Type_111:
                            return context.getString(R.string.motion_fun_new_11);
                        case Motion_Sport_Type_112:
                            return context.getString(R.string.motion_fun_new_12);
                        case Motion_Sport_Type_113:
                            return context.getString(R.string.motion_fun_new_13);
                        case Motion_Sport_Type_114:
                            return context.getString(R.string.motion_fun_new_14);
                        case Motion_Sport_Type_115:
                            return context.getString(R.string.motion_fun_new_15);
                        case Motion_Sport_Type_116:
                            return context.getString(R.string.motion_fun_new_16);
                        case Motion_Sport_Type_117:
                            return context.getString(R.string.motion_fun_new_17);
                        case Motion_Sport_Type_118:
                            return context.getString(R.string.motion_fun_new_18);
                        case Motion_Sport_Type_119:
                            return context.getString(R.string.motion_fun_new_19);
                        case Motion_Sport_Type_120:
                            return context.getString(R.string.motion_fun_new_20);
                        case Motion_Sport_Type_121:
                            return context.getString(R.string.motion_fun_new_21);
                        case Motion_Sport_Type_122:
                            return context.getString(R.string.motion_fun_new_22);
                        case Motion_Sport_Type_123:
                            return context.getString(R.string.motion_fun_new_23);
                        default:
                            return context.getString(R.string.motion_func_1);
                    }
                }else {
                    return mMotionBean.getCalorie();
                }
        }
    }

    private static int getHeadIconByType(int type, String isTest) {
        if("1".equals(isTest)) return R.drawable.motion_run_5;
        switch (type){
            case Motion_Sport_Type_5:
                return R.drawable.motion_run_0;
            case Motion_Sport_Type_6:
                return R.drawable.motion_run_4;
            case Motion_Sport_Type_7:
                return R.drawable.motion_run_2;
            case Motion_Sport_Type_8:
                return R.drawable.motion_run_3;
            case Motion_Sport_Type_9:
                return R.drawable.motion_run_1;
            case Motion_Sport_Type_10:
                return R.drawable.motion_run_6;
            case Motion_Sport_Type_11:
                return R.drawable.motion_run_7;
            case Motion_Sport_Type_101:
                return R.drawable.motion_run_8;
            case Motion_Sport_Type_102:
                return R.drawable.motion_run_9;
            case Motion_Sport_Type_103:
                return R.drawable.motion_run_10;
            case Motion_Sport_Type_104:
                return R.drawable.motion_run_11;
            case Motion_Sport_Type_105:
                return R.drawable.motion_run_12;
            case Motion_Sport_Type_106:
                return R.drawable.motion_run_13;
            case Motion_Sport_Type_107:
                return R.drawable.motion_run_new_107;
            case Motion_Sport_Type_108:
                return R.drawable.motion_run_new_108;
            case Motion_Sport_Type_109:
                return R.drawable.motion_run_new_109;
            case Motion_Sport_Type_110:
                return R.drawable.motion_run_new_110;
            case Motion_Sport_Type_111:
                return R.drawable.motion_run_new_111;
            case Motion_Sport_Type_112:
                return R.drawable.motion_run_new_112;
            case Motion_Sport_Type_113:
                return R.drawable.motion_run_new_113;
            case Motion_Sport_Type_114:
                return R.drawable.motion_run_new_114;
            case Motion_Sport_Type_115:
                return R.drawable.motion_run_new_115;
            case Motion_Sport_Type_116:
                return R.drawable.motion_run_new_116;
            case Motion_Sport_Type_117:
                return R.drawable.motion_run_new_117;
            case Motion_Sport_Type_118:
                return R.drawable.motion_run_new_118;
            case Motion_Sport_Type_119:
                return R.drawable.motion_run_new_119;
            case Motion_Sport_Type_120:
                return R.drawable.motion_run_new_120;
            case Motion_Sport_Type_121:
                return R.drawable.motion_run_new_121;
            case Motion_Sport_Type_122:
                return R.drawable.motion_run_new_122;
            case Motion_Sport_Type_123:
                return R.drawable.motion_run_new_123;
            default:
                return R.drawable.motion_run_0;
        }
    }

    public static ArrayList<RecordDetailBean> onSportRecordToDetailList(Context context, MotionSportRecordBeans mBeans) {
        ArrayList<RecordDetailBean> mTempList = new ArrayList<>();
        RecordDetailBean metailBean;
        switch (mBeans.getType()){
            case Motion_Sport_Type_5:
            case Motion_Sport_Type_9:
                //跑步
                metailBean = new RecordDetailBean(R.drawable.record_detail_0,
                        context.getString(R.string.record_title_0),
                        TimeUtil.formatTimeMs(!TextUtils.isEmpty(mBeans.getDuration())?Integer.valueOf(mBeans.getDuration())*1000:
                                TimeUtil.compareToDiffForTwoTime(mBeans.getStime(), mBeans.getEtime())*1000,true),
                        "");
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_1,
                        context.getString(R.string.record_title_1),
                        mBeans.getCalorie(),
                        context.getString(R.string.steps_unit_kiloCard));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_2,
                        context.getString(R.string.record_title_2),
                        TimeUtil.formatTime(Integer.valueOf(mBeans.getVelocity())),
                        context.getString(R.string.motion_unit_kilometer));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_3,
                        context.getString(R.string.record_title_3),
                        formatFloat2String(mBeans.getAvgspeed()),
                        context.getString(R.string.motion_unit_speed));
                mTempList.add(metailBean);
                break;
            case Motion_Sport_Type_6:
                //跳绳
                metailBean = new RecordDetailBean(R.drawable.record_detail_0,
                        context.getString(R.string.record_title_0),
                        TimeUtil.formatTimeMs(!TextUtils.isEmpty(mBeans.getDuration())?Integer.valueOf(mBeans.getDuration())*1000:
                                TimeUtil.compareToDiffForTwoTime(mBeans.getStime(), mBeans.getEtime())*1000,true),
                        "");
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_1,
                        context.getString(R.string.record_title_1),
                        mBeans.getCalorie(),
                        context.getString(R.string.steps_unit_kiloCard));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_3,
                        context.getString(R.string.record_title_3),
                        formatFloat2String(mBeans.getAvgspeed()),
                        context.getString(R.string.sport_title_unit_1));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_4,
                        context.getString(R.string.record_title_4),
                        mBeans.getContinuous(),
                        context.getString(R.string.sport_rope_jump_unit));
                mTempList.add(metailBean);
                break;
            case Motion_Sport_Type_11:
                //仰卧起坐
                metailBean = new RecordDetailBean(R.drawable.record_detail_0,
                        context.getString(R.string.record_title_0),
                        TimeUtil.formatTimeMs(!TextUtils.isEmpty(mBeans.getDuration())?Integer.valueOf(mBeans.getDuration())*1000:
                                TimeUtil.compareToDiffForTwoTime(mBeans.getStime(), mBeans.getEtime())*1000,true),
                        "");
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_1,
                        context.getString(R.string.record_title_1),
                        mBeans.getCalorie(),
                        context.getString(R.string.steps_unit_kiloCard));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_3,
                        context.getString(R.string.record_title_3),
                        formatFloat2String(mBeans.getAvgspeed()),
                        context.getString(R.string.sport_title_unit_1));
                mTempList.add(metailBean);
                break;
            case Motion_Sport_Type_10:
            case Motion_Sport_Type_8:
                //骑行
                metailBean = new RecordDetailBean(R.drawable.record_detail_0,
                        context.getString(R.string.record_title_0),
                        TimeUtil.formatTimeMs(!TextUtils.isEmpty(mBeans.getDuration())?Integer.valueOf(mBeans.getDuration()):
                                TimeUtil.compareToDiffForTwoTime(mBeans.getStime(), mBeans.getEtime())*1000,true),
                        "");
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_1,
                        context.getString(R.string.record_title_1),
                        mBeans.getCalorie(),
                        context.getString(R.string.steps_unit_kiloCard));
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_3,
                        context.getString(R.string.record_title_3),
                        formatFloat2String(mBeans.getAvgspeed()),
                        context.getString(R.string.sport_title_unit_0));
                mTempList.add(metailBean);
                break;
            default:
                metailBean = new RecordDetailBean(R.drawable.record_detail_0,
                        context.getString(R.string.record_title_0),
                        TimeUtil.formatTimeMs(!TextUtils.isEmpty(mBeans.getDuration())?Integer.valueOf(mBeans.getDuration())*1000:
                                TimeUtil.compareToDiffForTwoTime(mBeans.getStime(), mBeans.getEtime())*1000,true),
                        "");
                mTempList.add(metailBean);
                metailBean = new RecordDetailBean(R.drawable.record_detail_1,
                        context.getString(R.string.record_title_1),
                        mBeans.getCalorie(),
                        context.getString(R.string.steps_unit_kiloCard));
                mTempList.add(metailBean);
        }
        return mTempList;
    }

    private static String formatFloat2String(String avgspeed) {
        float mSpeed = Float.parseFloat(avgspeed);
        return String.format("%.2f",mSpeed);
    }

    public static int getBmiTypeByValue(float mBmiValue) {
        int  mBmiType;
        if(mBmiValue < 18.5){
            mBmiType = 0;
        }else if(mBmiValue < 24.9){
            mBmiType = 1;
        }else if(mBmiValue <29.9){
            mBmiType = 2;
        }else{
            mBmiType = 3;
        }
        return mBmiType;
    }

    public static String getBmiNameByValue(Context context,float mBmiValue) {
        String  mBmiName;
        if(mBmiValue < 18.5){
            mBmiName = context.getString(R.string.bmi_title_1);
        }else if(mBmiValue < 24.9){
            mBmiName = context.getString(R.string.bmi_title_2);
        }else if(mBmiValue <29.9){
            mBmiName = context.getString(R.string.bmi_title_3);
        }else{
            mBmiName = context.getString(R.string.bmi_title_4);
        }
        return mBmiName;
    }
    //com日常,hot热身,fat燃脂,aer有氧,atp无氧,lim极限
    private static String getTypeByPos(int pos) {
        String mTypePos = "com";
        switch (pos){
            case 1:
                mTypePos = "com";
                break;
            case 2:
                mTypePos = "hot";
                break;
            case 3:
                mTypePos = "fat";
                break;
            case 4:
                mTypePos = "aer";
                break;
            case 5:
                mTypePos = "atp";
                break;
            case 6:
                mTypePos = "lim";
                break;
        }
        return mTypePos;
    }

    public static int getHeartIntvlZoneByPos(List<String> mIntvlInfo, int i) {
        int pos = i/2+i%2;
        String mCalcType = getTypeByPos(pos);
        for (String s : mIntvlInfo) {
            if(s.split(",")[0].equals(mCalcType))
                return Integer.valueOf(s.split(",")[2]);
        }
        return 0;
    }

    public static String getHeartIntvlTimeByPos(List<String> mIntvlInfo, int i) {
        int pos = i/2+i%2;
        String mCalcType = getTypeByPos(pos);
        for (String s : mIntvlInfo) {
            if(s.split(",")[0].equals(mCalcType))
                return s.split(",")[1];
        }
        return "0";
    }

    public static MotionSportRecordBeans convertRecordFromJson(String toJSONString) {
        JSONObject mJsonInfo = (JSONObject) JSONValue.parse(toJSONString);
        MotionSportRecordBeans mBean = new MotionSportRecordBeans();
        mBean.setLoc(mJsonInfo.get("loc")==null?"":mJsonInfo.get("loc").toString());
        mBean.setDistance((String) mJsonInfo.get("distance"));
        mBean.setHeartRate(mJsonInfo.get("heartRate")==null?"":mJsonInfo.get("heartRate").toString());
        mBean.setEtime((String) mJsonInfo.get("etime"));
        mBean.setHeartIntvl(mJsonInfo.get("heartIntvl")==null?"":mJsonInfo.get("heartIntvl").toString());
        mBean.setAvgspeed((String) mJsonInfo.get("avgspeed"));
        mBean.setCalorie((String) mJsonInfo.get("calorie"));
        mBean.setStime((String) mJsonInfo.get("stime"));
        mBean.setVelocity((String) mJsonInfo.get("velocity"));
        mBean.setTestscore((String) mJsonInfo.get("testscore"));
        mBean.setType((int) mJsonInfo.get("type"));
        mBean.setIstest((String) mJsonInfo.get("istest"));
        mBean.setCount((String) mJsonInfo.get("count"));
        mBean.setContinuous((String) mJsonInfo.get("continuous"));
        mBean.setUphigh((String) mJsonInfo.get("uphigh"));
        mBean.setDownhigh((String) mJsonInfo.get("downhigh"));
        mBean.setDuration((String) mJsonInfo.get("duration"));

        return mBean;
    }

    public static void showStateView(String mDefStr,String scheduleStateByType, LayoutStateView mStateView1) {
        if(scheduleStateByType.equals(mDefStr)){
            mStateView1.setVisibility(View.GONE);
        }else {
            mStateView1.setVisibility(View.VISIBLE);
            mStateView1.setTvState(scheduleStateByType);
        }
    }

    public static int getBackGroundRid(String testScore) {
        int mColorId;
        try{
            int mScore = Integer.valueOf(testScore);
            if(mScore >= 90){
                mColorId = R.color.motion_record_bg_0;
            }else if(mScore >= 80){
                mColorId = R.color.motion_record_bg_1;
            }else if(mScore >= 60){
                mColorId = R.color.motion_record_bg_2;
            }else{
                mColorId = R.color.motion_record_bg_3;
            }
        }catch(Exception e){
            mColorId =  R.color.motion_record_bg_2;
        }
        return mColorId;
    }

    public static int getTvScore(Context mContext, String testScore) {
        int mHintId;
        try{
            int mScore = Integer.valueOf(testScore);
            if(mScore >= 90){
                mHintId = R.string.motion_record_tx_0;
            }else if(mScore >= 80){
                mHintId = R.string.motion_record_tx_1;
            }else if(mScore >= 60){
                mHintId = R.string.motion_record_tx_2;
            }else{
                mHintId = R.string.motion_record_tx_3;
            }
        }catch(Exception e){
            mHintId =  R.string.motion_record_tx_2;
        }
        return mHintId;
    }

    public static boolean isShowKal(int sportType) {
        boolean isShow;
        switch (sportType){
            case Motion_Sport_Type_5:
            case Motion_Sport_Type_6:
            case Motion_Sport_Type_7:
            case Motion_Sport_Type_8:
            case Motion_Sport_Type_9:
            case Motion_Sport_Type_10:
            case Motion_Sport_Type_11:
                 isShow = true;
                 break;
            case Motion_Sport_Type_101:
            case Motion_Sport_Type_102:
            case Motion_Sport_Type_103:
            case Motion_Sport_Type_104:
            case Motion_Sport_Type_105:
            case Motion_Sport_Type_106:
            case Motion_Sport_Type_107:
            case Motion_Sport_Type_108:
            case Motion_Sport_Type_109:
            case Motion_Sport_Type_110:
            case Motion_Sport_Type_111:
            case Motion_Sport_Type_112:
            case Motion_Sport_Type_113:
            case Motion_Sport_Type_114:
            case Motion_Sport_Type_115:
            case Motion_Sport_Type_116:
            case Motion_Sport_Type_117:
            case Motion_Sport_Type_118:
            case Motion_Sport_Type_119:
            case Motion_Sport_Type_120:
            case Motion_Sport_Type_121:
            case Motion_Sport_Type_122:
            case Motion_Sport_Type_123:
            default:
                isShow = false;
                break;
        }
        return isShow;
    }
}
