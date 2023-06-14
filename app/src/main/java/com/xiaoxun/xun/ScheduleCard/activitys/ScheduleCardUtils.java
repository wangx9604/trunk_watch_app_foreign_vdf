package com.xiaoxun.xun.ScheduleCard.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.Views.WeekClassView;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardItemBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleClassBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleNoticeBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleTimeBean;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.motion.beans.ScheduleWeekBean;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.SecurityZone;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ScheduleCardUtils {
    //处理学校信息的消息ID
    public static final int GET_GEO_CODE_INFO = 1;
    public static final int GET_DISTRICT_PROVINCE_INFO = 2;
    public static final int GET_DISTRICT_CITY_INFO = 3;
    public static final int GET_DISTRICT_ZONE_INFO = 4;
    public static final int GET_SCHOOL_POI_INFO = 5;
    //处理课表信息的消息ID
    public static final int GET_SCHEDULE_CARD_INFO = 6;
    public static final int UPDATE_SCHEDULE_CARD_INFO = 7;
    //限制课表课程最大数量
    public static final int FIELD_CLASS_LIMIT_NUM = 30;

    public static final String POI_SEARCH_TYPE = "科教文化服务;学校;小学|科教文化服务;学校;中学";//固定查询，不翻译
    public static final int[] BASE_CLASS = {};
//            {R.string.sehedule_course_1, R.string.sehedule_course_2,
//            R.string.sehedule_course_4, R.string.sehedule_course_5, R.string.sehedule_course_6,
//            R.string.sehedule_course_7, R.string.sehedule_course_9};
    public static final int[] BASE_COLOR = {Color.rgb(154,220,100), Color.rgb(184,120,255), Color.rgb(255,175,86),
            Color.rgb(76,224,150), Color.rgb(213, 134, 255), Color.rgb(93, 201, 255),
            Color.rgb(255, 120, 120), Color.rgb(255, 213, 86), Color.rgb(135, 150, 255),
            Color.rgb(255, 169, 221)};
    public static final int[] BASE_CUSTOM_COLOR = {Color.rgb(255, 182, 91),
            Color.rgb(122, 220, 99), Color.rgb(255, 114, 166),
            Color.rgb(255, 123, 246), Color.rgb(247, 150, 101)};

    public final static int WEEKSUM  = 5;//显示到周几
    public final static int EDITSUM  = 6;//编辑列表的行个数

    private final static String TAG = ScheduleCardUtils.class.getSimpleName();

    public static void showLoadingDialog(LoadingDialog loadingDialog, String text) {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.enableCancel(false);
            loadingDialog.changeStatus(1, text);
            loadingDialog.show();
        }
    }
    public static int getWeekClassSumByType(ArrayList<ScheduleWeekBean> mData, int i) {
        int mWeekClassSum = 0;
        for (ScheduleWeekBean mDatum : mData) {
            if(mDatum.getmWeekNum() == i)
                mWeekClassSum++;
        }
        return mWeekClassSum;
    }

    public static void OnGenWeekClassViewBySetInfo(Context mContext, LinearLayout mMorLayoutForWeekClass,
                                                   LinearLayout mAftLayoutForWeekClass,
                                                   ArrayList<ScheduleWeekBean> mWeekArray) {
        ArrayList<ScheduleWeekBean> mWeek6MorList = new ArrayList<>();
        ArrayList<ScheduleWeekBean> mWeek6AftList = new ArrayList<>();
        ArrayList<ScheduleWeekBean> mWeek7MorList = new ArrayList<>();
        ArrayList<ScheduleWeekBean> mWeek7AftList = new ArrayList<>();

        //1:从总表中获取数据
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            if(scheduleWeekBean.getmWeekNum() == 6){
                if(scheduleWeekBean.getmStartTime().compareTo("1200") < 0){
                    mWeek6MorList.add(scheduleWeekBean);
                }else{
                    mWeek6AftList.add(scheduleWeekBean);
                }
            }
            if(scheduleWeekBean.getmWeekNum() == 7){
                if(scheduleWeekBean.getmStartTime().compareTo("1200") < 0){
                    mWeek7MorList.add(scheduleWeekBean);
                }else{
                    mWeek7AftList.add(scheduleWeekBean);
                }
            }
        }

        //2:数据排序
        Collections.sort(mWeek6MorList);
        Collections.sort(mWeek6AftList);
        Collections.sort(mWeek7MorList);
        Collections.sort(mWeek7AftList);

        //3:插入数据到视图中
        mMorLayoutForWeekClass.removeAllViews();
        mAftLayoutForWeekClass.removeAllViews();
        //1:上午课程处理
        if(mWeek6MorList.size() > mWeek7MorList.size()){
            onAddMorOrAftToLayout(mContext, mMorLayoutForWeekClass, mWeek6MorList, mWeek7MorList, mWeekArray);
        }else{
            onAddMorOrAftToLayout(mContext, mMorLayoutForWeekClass, mWeek7MorList,mWeek6MorList, mWeekArray);
        }
        //2：下午课程处理
        if(mWeek6AftList.size() > mWeek7AftList.size()){
            onAddMorOrAftToLayout(mContext, mAftLayoutForWeekClass, mWeek6AftList,mWeek7AftList, mWeekArray);
        }else{
            onAddMorOrAftToLayout(mContext, mAftLayoutForWeekClass, mWeek7AftList,mWeek6AftList, mWeekArray);
        }

        //4:上午&&下午课程不足四节课时，补充四节课
        //1:补充上午的四节课
        if(mWeek6MorList.size() < 4 && mWeek7MorList.size() < 4){
            int mMaxLen = Math.max(mWeek6MorList.size(), mWeek7MorList.size());
            for(int i = mMaxLen; i < 4; i++){
                onAddViewToShowLayout(mContext, mMorLayoutForWeekClass, new ScheduleWeekBean(),-1);
            }
        }
        //2：补充下午的四节课
        if(mWeek6AftList.size() < 4 && mWeek7AftList.size() < 4){
            int mMaxLen = Math.max(mWeek6AftList.size(), mWeek7AftList.size());
            for(int i = mMaxLen; i < 4; i++){
                onAddViewToShowLayout(mContext, mAftLayoutForWeekClass, new ScheduleWeekBean(),-1);
            }
        }
    }

    private static WeekClassView onAddViewToShowLayout(Context mContext, LinearLayout mLayoutForWeekClass,
                                                       ScheduleWeekBean mWeekClassBean,
                                                       int mColor){

        WeekClassView itemClassView = new WeekClassView(mContext);
        try {
            setInfoToCardView(mWeekClassBean, itemClassView, mColor);
        }catch(Exception e){
            e.printStackTrace();
        }
        mLayoutForWeekClass.addView(itemClassView);
        return itemClassView;
    }

    private static int getWeekColorByClassName(ArrayList<ScheduleWeekBean> mWeekArray, String mWeekClassName) {
        for (int i = 0; i < mWeekArray.size(); i++) {
            ScheduleWeekBean mWeekBean = mWeekArray.get(i);
            if(mWeekBean.getmWeekClassName().equals(mWeekClassName)){
                return BASE_CUSTOM_COLOR[i % 5];
            }
        }
        return -1;
    }
    private static void setInfoToCardView(ScheduleWeekBean mWeekClassAnrBean,
                                          WeekClassView itemClassView,
                                          int mColor){

        String mWeekTime = ScheduleCardUtils.ScheduleGetHourFormat(mWeekClassAnrBean.getmStartTime()) + " - " +
                ScheduleCardUtils.ScheduleGetHourFormat(mWeekClassAnrBean.getmEndTime());
        if (mWeekClassAnrBean.getmWeekNum() == 6) {
            itemClassView.setCardView0ForShow(mWeekClassAnrBean.getmWeekClassName(), mWeekTime, mColor);
        } else if(mWeekClassAnrBean.getmWeekNum() == 7){
            itemClassView.setCardView1ForShow(mWeekClassAnrBean.getmWeekClassName(), mWeekTime, mColor);
        }else{
            itemClassView.setCardView1ForShow(mWeekClassAnrBean.getmWeekClassName(), null, -1);
            itemClassView.setCardView0ForShow(mWeekClassAnrBean.getmWeekClassName(), null, -1);
        }
    }

    private static void onAddMorOrAftToLayout(Context mContext,
                                              LinearLayout mLayoutForWeekClass,
                                              ArrayList<ScheduleWeekBean> mWeekStartList,
                                              ArrayList<ScheduleWeekBean> mWeekEndList,
                                              ArrayList<ScheduleWeekBean> mWeekArray){
        for (int i = 0; i < mWeekStartList.size(); i++) {
            ScheduleWeekBean mWeekClassBean = mWeekStartList.get(i);
            int mColor = getWeekColorByClassName(mWeekArray, mWeekClassBean.getmWeekClassName());
            WeekClassView itemClassView = onAddViewToShowLayout(mContext, mLayoutForWeekClass, mWeekClassBean, mColor);
            if(mWeekEndList.size() >  i){
                ScheduleWeekBean mWeekClassAnrBean = mWeekEndList.get(i);
                mColor = getWeekColorByClassName(mWeekArray, mWeekClassAnrBean.getmWeekClassName());
                setInfoToCardView(mWeekClassAnrBean, itemClassView, mColor);
            }
        }
    }


    public static boolean checkWeekClassHasName(ArrayList<ScheduleWeekBean> mWeekArray) {
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            if(scheduleWeekBean.getmType()==1 && (
                    TextUtils.isEmpty(scheduleWeekBean.getmWeekClassName())
                            || "".equals(scheduleWeekBean.getmWeekClassName())))
                return false;
        }

        return true;
    }

    public static void onTranWeekClassBeanToTimeBean(ArrayList<ScheduleWeekBean> mWeekArray, ArrayList<ScheduleTimeBean> mTimeArray, int i) {
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            if(scheduleWeekBean.getmWeekNum() == i){
                mTimeArray.add(new ScheduleTimeBean(-1,"",
                        scheduleWeekBean.getmStartTime(),scheduleWeekBean.getmEndTime(),1));
            }
        }
    }

    public static ArrayList<ScheduleWeekBean> getWeekListByWeekType(ArrayList<ScheduleWeekBean> mWeekArray, int i) {
        ArrayList<ScheduleWeekBean> mNewList = new ArrayList<>();
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            if(scheduleWeekBean.getmWeekNum() == i){
                mNewList.add(scheduleWeekBean);
            }
        }

        return mNewList;
    }

    public static ArrayList<String> transWeekClassArrayToWeekList(ArrayList<ScheduleWeekBean> mWeekArray) {
        ArrayList<String> mWeekList = new ArrayList<>();
        for (ScheduleWeekBean scheduleWeekBean : mWeekArray) {
            StringBuilder mBuilder = new StringBuilder();
            mBuilder.append(scheduleWeekBean.getmWeekNum());
            mBuilder.append(",");
            mBuilder.append(scheduleWeekBean.getmWeekClassName());
            mBuilder.append(",");
            mBuilder.append(scheduleWeekBean.getmStartTime());
            mBuilder.append(",");
            mBuilder.append(scheduleWeekBean.getmEndTime());
            mWeekList.add(mBuilder.toString());
        }

        return mWeekList;
    }

    public static ArrayList<ScheduleWeekBean> getScheduleWeekListByWeekList(ArrayList<String> mWeekList){
        ArrayList<ScheduleWeekBean> mScheduleWeekList = new ArrayList<>();
        if(mWeekList == null)  return mScheduleWeekList;
        try {
            for (String s : mWeekList) {
                String[] mWeekInfo = s.split(",");
                ScheduleWeekBean mCurWeekInfo = new ScheduleWeekBean();
                mCurWeekInfo.setmType(1);
                mCurWeekInfo.setmWeekNum(Integer.valueOf(mWeekInfo[0]));
                mCurWeekInfo.setmWeekClassName(mWeekInfo[1]);
                mCurWeekInfo.setmStartTime(mWeekInfo[2]);
                mCurWeekInfo.setmEndTime(mWeekInfo[3]);
                mScheduleWeekList.add(mCurWeekInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return mScheduleWeekList;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setCanDragButton(Context mContext, ImageView mClassEditImageView, final View.OnClickListener listener) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels;
        final int[] downX = new int[1];
        final int[] downY = new int[1];
        final boolean[] isDrag = {false};

        mClassEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDrag[0]){
                    listener.onClick(v);
                }
            }
        });

        mClassEditImageView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY; // 记录移动的最后的位置

            public boolean onTouch(View v, MotionEvent event) {
                // 获取Action
                int ea = event.getAction();
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX[0] = lastX;
                        downY[0] = lastY;
                        // Toast.makeText(getActivity(), "ACTION_DOWN：" + lastX + ",
                        // " + lastY, 0).show();
                        break;
                    case MotionEvent.ACTION_MOVE: // 移动
                        // 移动中动态设置位置
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        // Toast.makeText(getActivity(), "position：" + left + ", " +
                        // top + ", " + right + ", " + bottom, 0)
                        // .show();
                        // 将当前的位置再次设置
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP: // 抬起
                        if (Math.abs((int) (event.getRawX() - downX[0])) > 5
                                || Math.abs((int) (event.getRawY() - downY[0])) > 5)
                            isDrag[0] = true;
                        else
                            isDrag[0] = false;
                        break;
                }
                return false;
            }
        });
    }

    public static void hideLoading(LoadingDialog loadingDialog) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static void onOpenEditDialog(Context ctxt, String title, String hint,
                                        CustomSelectDialogUtil.CustomDialogListener onOkListener){
        Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(ctxt,
                12,
                0,
                title,
                null, hint, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {

                    }
                },
                ctxt.getText(R.string.cancel).toString(),
                onOkListener,
                ctxt.getText(R.string.confirm).toString());
        dlg.show();
    }

    public static void onLongClickDeleteDialog(Context ctxt, String title,
                                               DialogUtil.OnCustomDialogListener listener){
        Dialog dlg = DialogUtil.CustomNormalDialog(ctxt,
                title, null, new DialogUtil.OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                    }
                }, ctxt.getString(R.string.cancel),
                listener,
                ctxt.getString(R.string.confirm));
        dlg.show();
    }

    public static ScheduleTimeBean onGenerateClassTimeInfo(Context ctxt, ArrayList<ScheduleTimeBean> mTimeArray) {
        ScheduleTimeBean beans;
        ScheduleTimeBean mLastBeans;

        try {
            if(mTimeArray.size() > 0){
                mLastBeans = mTimeArray.get(mTimeArray.size() - 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
                     Date mDate = dateFormat.parse(mLastBeans.getmScheduleEndTime());
                     mDate.setTime(mDate.getTime() + 600000);
                     String startTime = dateFormat.format(mDate);
                     mDate.setTime(mDate.getTime() + 3000000);
                     String endTime = dateFormat.format(mDate);
                     beans = new ScheduleTimeBean(mLastBeans.getmScheduleTimeId()+1,
                             ScheduleGetClassNum(ctxt,mLastBeans.getmScheduleTimeId()+1, false),
                             startTime,endTime,
                            1);

            }else{
                beans = new ScheduleTimeBean(0,ScheduleGetClassNum(ctxt,0,false),
                        "0810","0840",
                        1);
            }

            return beans;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String ScheduleGetHourFormat(String timeZone){
        String mHourZoneType;
        mHourZoneType =timeZone.substring(0,2)+":"+timeZone.substring(2);
        return mHourZoneType;
    }


    public static String ScheduleGetClassNum(Context ctxt, int classNum, boolean isSimple){
        String ClassNum;
        switch(classNum){
            case 0:
                ClassNum = isSimple?"一":ctxt.getString(R.string.class_num_0);
                break;
            case 1:
                ClassNum = isSimple?"二":ctxt.getString(R.string.class_num_1);
                break;
            case 2:
                ClassNum = isSimple?"三":ctxt.getString(R.string.class_num_2);
                break;
            case 3:
                ClassNum = isSimple?"四":ctxt.getString(R.string.class_num_3);
                break;
            case 4:
                ClassNum = isSimple?"五":ctxt.getString(R.string.class_num_4);
                break;
            case 5:
                ClassNum = isSimple?"六":ctxt.getString(R.string.class_num_5);
                break;
            case 6:
                ClassNum = isSimple?"七":ctxt.getString(R.string.class_num_6);
                break;
            case 7:
                ClassNum = isSimple?"八":ctxt.getString(R.string.class_num_7);
                break;
            case 8:
                ClassNum = isSimple?"九":ctxt.getString(R.string.class_num_8);
                break;
            case 9:
                ClassNum = isSimple?"十":ctxt.getString(R.string.class_num_9);
                break;
            case 10:
                ClassNum = isSimple?"十一":ctxt.getString(R.string.class_num_10);
                break;
            default:
                ClassNum = isSimple?"十二":ctxt.getString(R.string.class_num_11);
        }

        return ClassNum;
    }

    public static SecurityZone getDefaultSchoolSecurityZone(ImibabyApp myApp, WatchData curWatch){
        String data = myApp.getStringValue(curWatch.getEid()
                        + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY
                        , "");

        SecurityZone securityZone = new SecurityZone();
        if (data != null && !data.equals("")) {
            JSONObject pl = (JSONObject) JSONValue.parse(data);
            JSONArray arr = (JSONArray) pl.get("list");
            if (arr != null && arr.size() > 0) {
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject item = (JSONObject) arr.get(i);
                    String mSecurityId = (String) item.get("Efid");

                    if ("EFID2".equals(mSecurityId)) {
                        securityZone.sName = (String) item.get("Name");
                        securityZone.sCenter = (String) item.get("Center_amap");
                        securityZone.sRadius = (Integer) item.get("Radius");
                        if (securityZone.sRadius >= Const.MAX_SECURITY_ZONE_RADIA) {
                            securityZone.sRadius = Const.MAX_SECURITY_ZONE_RADIA;
                        }
                        securityZone.onOff = (String) item.get("Onoff");
                        securityZone.keyEFID = (String) item.get("Efid");
                        securityZone.info = (String) item.get("Info");
                        securityZone.preview = (String) item.get("Preview");
                        securityZone.sCenterBD = (String) item.get("Center_bd");
                        securityZone.sCoordinate = (String) item.get("Coodrinate");

                    }
                }
            }
        }
        return securityZone;
    }


    public static void GenNewScheduleCardInfo(ScheduleCardBean cardBean, String mSchoolOnProVince,
                                              String mSchoolOnCity, String mSchoolOnZone,
                                              String mSchoolName, String mSchoolGradeNo,
                                              String mSchoolClassNo, String mSchoolLocation,
                                              String eid, String gid) {
        cardBean.setProvince(mSchoolOnProVince);
        cardBean.setCity(mSchoolOnCity);
        cardBean.setDistrict(mSchoolOnZone);
        cardBean.setSchool(mSchoolName);
        cardBean.setGrade(mSchoolGradeNo);
        cardBean.setMclass(mSchoolClassNo);
        cardBean.setLocation(mSchoolLocation);

        ArrayList<String> mTimeArray = new ArrayList<>();
        mTimeArray.add("0810,0840");
        mTimeArray.add("0850,0930");
        mTimeArray.add("0950,1030");
        mTimeArray.add("1040,1120");
        mTimeArray.add("1430,1510");
        mTimeArray.add("1530,1610");
        cardBean.setTimelist(mTimeArray);

        ArrayList<String> mClassArr = new ArrayList();
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        mClassArr.add("N/A,N/A,N/A,N/A,N/A,N/A");
        cardBean.setWeeklist(mClassArr);

        cardBean.setEID(eid);
        cardBean.setGID(gid);
        cardBean.setOptype(0);
    }

    public static void updateScheduleCardInfo(ImibabyApp myApp, String scheduleInfo, MsgCallback callback) {
        if(myApp.getNetService() != null) {
            MyMsgData mMsgData = new MyMsgData();
            mMsgData.setCallback(callback);
            JSONObject msg = new JSONObject();
            msg.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_SCHEDULE_DATA_UPDATE);
            msg.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
            if (myApp.getToken() != null) {
                msg.put(CloudBridgeUtil.KEY_NAME_SID, myApp.getToken());
            }
            JSONObject pl = (JSONObject) JSONValue.parse(scheduleInfo);
            msg.put(CloudBridgeUtil.KEY_NAME_PL, pl);
            LogUtil.e(TAG + msg.toJSONString());
            mMsgData.setReqMsg(msg);
            myApp.getNetService().sendNetMsg(mMsgData);
        }
    }

    public static void getScheduleCardInfo(ImibabyApp myApp, MsgCallback callback, WatchData watchData) {
        if(myApp.getNetService() != null) {
            MyMsgData mMsgData = new MyMsgData();
            mMsgData.setCallback(callback);
            JSONObject msg = new JSONObject();
            msg.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_SCHEDULE_DATA_GET);
            msg.put(CloudBridgeUtil.KEY_NAME_SN, Long.valueOf(TimeUtil.getTimeStampGMT()).intValue());
            if (myApp.getToken() != null) {
                msg.put(CloudBridgeUtil.KEY_NAME_SID, myApp.getToken());
            }
            JSONObject pl = new JSONObject();
            pl.put(CloudBridgeUtil.E2C_PL_KEY_EID, watchData.getEid());
            msg.put(CloudBridgeUtil.KEY_NAME_PL,pl);
            LogUtil.e(TAG + msg.toJSONString());
            mMsgData.setReqMsg(msg);
            myApp.getNetService().sendNetMsg(mMsgData);
        }
    }

    public static int getColorValueByClassName(Context ctxt, ArrayList<String> mCustomList, String mClassName){
        int index = Color.rgb(255,114,166);
        boolean isHasColor = false;
        for(int i=0; i < BASE_CLASS.length; i++){
            if(mClassName.equals(ctxt.getString(BASE_CLASS[i]))){
                index = BASE_COLOR[i];
                isHasColor = true;
                break;
            }
        }
        if(!isHasColor){
            for(int i = 0; i < mCustomList.size(); i++){
                if(mClassName.equals(mCustomList.get(i))){
                    index = BASE_CUSTOM_COLOR[i % 5];
                    break;
                }
            }
        }

        return index;
    }

    public static void genEditArrayToAdapterBean(Context ctxt, ArrayList<ScheduleCardItemBean> mEditAdapterList) {
        mEditAdapterList.clear();
        for(int i=0; i < BASE_CLASS.length; i++){
            ScheduleCardItemBean itemBean = new ScheduleCardItemBean(3,ctxt.getString(BASE_CLASS[i]),null,
                    false,false,false);
            mEditAdapterList.add(itemBean);
        }
    }

    public static ArrayList<ScheduleClassBean> genClassArrayByCustomList(ArrayList<String> mCustomList){
        ArrayList<ScheduleClassBean> mClassArray = new ArrayList<>();
        for(int i=0; i < mCustomList.size(); i++){
            ScheduleClassBean itemBean = new ScheduleClassBean(mCustomList.get(i),true,1);
            mClassArray.add(itemBean);
        }

        return mClassArray;
    }

    public static ArrayList<ScheduleClassBean> genClassArrayByLocalDefault(Context ctxt){
        ArrayList<ScheduleClassBean> mClassArray = new ArrayList<>();
        for(int i = 0; i < ScheduleCardUtils.BASE_CLASS.length; i++){
            ScheduleClassBean itemBean = new ScheduleClassBean(ctxt.getString(BASE_CLASS[i]),false,1);
            mClassArray.add(itemBean);
        }

        return mClassArray;
    }

    public static void OnEditStateEndByOriginal(ArrayList<ScheduleCardItemBean> mCardAdapterList,
                                                ArrayList<ScheduleCardItemBean> mEditAdapterList) {
        if(mEditAdapterList.size() > 0) {
            for (ScheduleCardItemBean itemBean : mEditAdapterList){
                itemBean.setSelect(false);
            }
        }
        if(mCardAdapterList.size() > 0 ) {
            for (ScheduleCardItemBean itemBean : mCardAdapterList) {
                itemBean.setEditMode(false);
                itemBean.setSelect(false);
                itemBean.setEditOperate(false);
            }
        }
    }

    public static String OnEditStateByOriginal(ArrayList<ScheduleCardItemBean> mCardAdapterList,
                                               ArrayList<ScheduleCardItemBean> mEditAdapterList) {
        String mSelectName="";
        if(mEditAdapterList.size() > 0) {
            for (int i = 0; i < mEditAdapterList.size(); i++) {
                ScheduleCardItemBean itemBean = mEditAdapterList.get(i);
                itemBean.setSelect(false);
            }
        }
        setScheduleCardOperate(mCardAdapterList, mSelectName);

        return mSelectName;
    }

    public static void clearClassOperItem(ArrayList<ScheduleClassBean> mclassArray){
        for(ScheduleClassBean itemBean:mclassArray){
            if(itemBean.getClassType()== 2){
                mclassArray.remove(itemBean);
                break;
            }
        }
    }

    public static void clearTimeOperItem(ArrayList<ScheduleTimeBean> mTimeArray){
        for(ScheduleTimeBean itemBean:mTimeArray){
            if(itemBean.getmHourZoneType()== 2){
                mTimeArray.remove(itemBean);
                break;
            }
        }
        clearScheduleTimeListisSelectState(mTimeArray);
    }

    public static String onUpdateEditSelectOper(ArrayList<ScheduleCardItemBean> mCardAdapterList,
                                                ArrayList<ScheduleCardItemBean> mEditAdapterList,
                                                int position){
        String mEditSelectItem;
        for (ScheduleCardItemBean itemBean : mEditAdapterList){
            itemBean.setSelect(false);
        }
        if(position == -1){
            //擦除模式
            mEditSelectItem = "";
        }else{
            //正常操作
            mEditAdapterList.get(position).setSelect(true);
            mEditSelectItem = mEditAdapterList.get(position).getmScheduleName();
        }
        setScheduleCardOperate(mCardAdapterList, mEditSelectItem);
        return mEditSelectItem;
    }

    public static void setScheduleCardOperate(ArrayList<ScheduleCardItemBean> mCardAdapterList, String mCardOperate){
        if(mCardAdapterList.size() > 0 ) {
            for (ScheduleCardItemBean itemBean : mCardAdapterList) {
                itemBean.setEditMode(true);
                itemBean.setSelect(false);
                if(!mCardOperate.equals(itemBean.getmScheduleName())) {
                    itemBean.setEditOperate(true);
                }else{
                    itemBean.setEditOperate(false);
                }
            }
        }
    }

    public static ScheduleCardBean transCardBeanByScheInfo(String scheduleInfo){
        Gson gson = new Gson();
        return gson.fromJson(scheduleInfo, ScheduleCardBean.class);
    }

    public static ScheduleNoticeBean transNoticeBeanByScheInfo(String scheduleInfo){
        Gson gson = new Gson();
        return gson.fromJson(scheduleInfo, ScheduleNoticeBean.class);
    }

    public static String transScheInfoByCardBean(ScheduleCardBean cardBean){
        Gson gson = new Gson();
        return gson.toJson(cardBean);
    }

    public static void transCardBeanToAdapterBean(Context ctxt,
                                                  ScheduleCardBean cardBean,
                                                  ArrayList<ScheduleCardItemBean> mCardAdapterList
                                            ) {
        mCardAdapterList.clear();
        if(cardBean == null || cardBean.getEID() == null){
            return;
        }
        for(int i = 0;i < cardBean.getTimelist().size(); i++){
            String classTime = cardBean.getTimelist().get(i);
            String[] timeArr = classTime.split(",");
            try {
                String showTime = ScheduleCardUtils.ScheduleGetHourFormat(timeArr[0]) + "-" + ScheduleCardUtils.ScheduleGetHourFormat(timeArr[1]);
                ScheduleCardItemBean itemBean = new ScheduleCardItemBean(1,ScheduleGetClassNum(ctxt,i,false),
                        showTime,false,false,false);
                mCardAdapterList.add(itemBean);
                for(int j = 0; j < WEEKSUM; j++){
                    String[] mClassInfo = cardBean.getWeeklist().get(j).split(",");
                    if(i >= mClassInfo.length){
                        itemBean = new ScheduleCardItemBean(2,"N/A",null,
                                false,false,false);
                        mCardAdapterList.add(itemBean);
                    }else {
                        mClassInfo = cardBean.getWeeklist().get(j).split(",");
                        itemBean = new ScheduleCardItemBean(2, mClassInfo[i], null,
                                false, false, false);
                        mCardAdapterList.add(itemBean);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void transCardBeanToTimeArray(Context ctxt,
                                                ScheduleCardBean cardBean,
                                                ArrayList<ScheduleTimeBean> mTimeArray
                                                ){
        mTimeArray.clear();
        for(int i = 0;i < cardBean.getTimelist().size();i++){
            String itemTime = cardBean.getTimelist().get(i);
            try {
                String[] timeArr = itemTime.split(",");
                mTimeArray.add(new ScheduleTimeBean(i, ScheduleCardUtils.ScheduleGetClassNum(ctxt, i,false),
                        timeArr[0], timeArr[1], 1));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> transTimeArrayToCardBean(ArrayList<ScheduleTimeBean> mTimeArray){
        ArrayList<String> timelist = new ArrayList<>();
        for(ScheduleTimeBean bean:mTimeArray){
            String buildStr = bean.getmScheduleStartTime()+","+bean.getmScheduleEndTime();
            timelist.add(buildStr);
        }
        return timelist;
    }

    public static ArrayList<String> transFixInfoToStandSchedule(ScheduleCardBean cardBean, ArrayList<ScheduleCardItemBean> mCardAdapterList) {
        ArrayList<String> weeklist = new ArrayList<>();
        String[] mWeekList = new String[7];
        for(int i = 0; i < cardBean.getTimelist().size(); i++) {
            for (int j = 0; j < WEEKSUM + 1; j++) {
                ScheduleCardItemBean itemBean = mCardAdapterList.get(i*6+j);
                if (itemBean.getmItemType() == 2) {
                    if(i == 0){
                        mWeekList[j-1] = itemBean.getmScheduleName();
                    }else {
                        mWeekList[j-1] = mWeekList[j-1] + "," + itemBean.getmScheduleName();
                    }
                }
            }
            //补充数据
            for(int k = 5;k<7;k++){
                if(i == 0){
                    mWeekList[k] = "N/A";
                }else {
                    mWeekList[k] = mWeekList[k] + "," + "N/A";
                }
            }
        }
        for(int i = 0;i<7;i++){
            weeklist.add(mWeekList[i]);
        }

        return weeklist;
    }

    public static boolean checkClassNameIsSame(ArrayList<ScheduleClassBean> mClassArray, String text) {
        if("".equals(text)){
            return false;
        }
        for (ScheduleClassBean item:mClassArray) {
           if(item.getmClassName().equals(text)){
               return true;
           }
        }
        return false;
    }

    public static boolean checkClassNameIsSameForCard(ArrayList<ScheduleCardItemBean> mClassArray, String text) {
        if("".equals(text)){
            return false;
        }
        for (ScheduleCardItemBean item:mClassArray) {
            if(item.getmItemType() == 3 && item.getmScheduleName().equals(text)){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> moveJsonArrayToArraylist(JSONArray jsonArray){
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add((String)jsonArray.get(i));
        }
        return list;
    }

    public static ArrayList<ScheduleCardItemBean> transCardBeanToEditList(ScheduleCardBean cardBean) {
        ArrayList<ScheduleCardItemBean> customClassList = new ArrayList<>();
        if(cardBean == null || cardBean.getCustomlist() == null){
            return customClassList;
        }

        for(int i = 0; i<cardBean.getCustomlist().size(); i++){
            String customClass = cardBean.getCustomlist().get(i);
            ScheduleCardItemBean itemBean = new ScheduleCardItemBean(3,customClass,null,
                    false,false,false);
            customClassList.add(itemBean);
        }

        return customClassList;
    }

    public static ArrayList<String> transCustomClassToStandSchedule(ArrayList<ScheduleCardItemBean> mEditAdapterList) {
        ArrayList<String> customList = new ArrayList<>();
//        if(mEditAdapterList.size() > 7){
//            for(int i = 7;i < mEditAdapterList.size(); i++){
//                ScheduleCardItemBean itemBean = mEditAdapterList.get(i);
//                if(itemBean.getmItemType() == 3)
//                    customList.add(itemBean.getmScheduleName());
//            }
//        }
        if(!mEditAdapterList.isEmpty()){
            for(int i = 0;i < mEditAdapterList.size(); i++){
                ScheduleCardItemBean itemBean = mEditAdapterList.get(i);
                if(itemBean.getmItemType() == 3)
                    customList.add(itemBean.getmScheduleName());
            }
        }

        return customList;
    }

    public static void clearScheduleTimeListisSelectState(ArrayList<ScheduleTimeBean> mTimeArray){
        if(mTimeArray == null) return;
        for (ScheduleTimeBean itemBean: mTimeArray) {
            itemBean.setSelect(false);
        }
    }

    public static void updateScheduleTimeListIsSelectState(ArrayList<ScheduleTimeBean> mTimeArray,
                                                           ScheduleTimeBean selectBean) {
        if(mTimeArray == null) return;
        for (ScheduleTimeBean itemBean: mTimeArray) {
            if(itemBean.equals(selectBean)){
                itemBean.setSelect(true);
            }else{
                itemBean.setSelect(false);
            }
        }
    }

    public static boolean onCheckClassArrayForChange(ArrayList<String> mSrcList, ArrayList<String> mDstList) {
        boolean isChange = false;
        if(mSrcList == null || mDstList == null) {
            return isChange;
        }
        ArrayList<String> mSrcArray = new ArrayList<>();
        ArrayList<String> mDstArray = new ArrayList<>();
        mSrcArray.addAll(mSrcList);
        mDstArray.addAll(mDstList);

        Collections.sort(mSrcArray);
        Collections.sort(mDstArray);

        if(!mSrcArray.equals(mDstArray)){
            isChange = true;
        }else{
            isChange = false;
        }

        return isChange;
    }

    public static String getNoticeDate(String curDate){
        String formatDate = null;

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("MM.dd");
        try{
            formatDate = format2.format(format1.parse(curDate));
        }catch (Exception e){
            e.printStackTrace();
        }
        return  formatDate;
    }

    public static String getNoticeWeek(String curDate) {
        String Week = "周";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");//也可将此值当参数传进来
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(curDate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch(c.get(Calendar.DAY_OF_WEEK)){
            case 1:
                Week += "天";
                break;
            case 2:
                Week += "一";
                break;
            case 3:
                Week += "二";
                break;
            case 4:
                Week += "三";
                break;
            case 5:
                Week += "四";
                break;
            case 6:
                Week += "五";
                break;
            case 7:
                Week += "六";
                break;
            default:
                break;
        }
        return Week;
    }

    public static void SaveScheduleInfoToLocal(ImibabyApp myApp, String eid, String mCardInfo) {
        String mScheduleInfo = mCardInfo;
        myApp.setValue(eid + Constants.SHARE_PREF_FIELD_SCHEDULE_CARD_INFO, mScheduleInfo);

        myApp.sdcardLog("scheduleXXXX:"+mScheduleInfo);
    }

    public static String GetScheduleCardInfoByLocal(ImibabyApp myApp, String eid) {
        return myApp.getStringValue(eid+ Constants.SHARE_PREF_FIELD_SCHEDULE_CARD_INFO,"");
    }

    public static boolean checkClassTimeConflict(Context mContext, ArrayList<ScheduleTimeBean> mTimeArray) {
        if(mContext == null) return true;
        //1.1:检查单课程是否时间冲突
        ArrayList<ScheduleTimeBean> hasConflictBeanList = new ArrayList<>();
        for(ScheduleTimeBean beans: mTimeArray){
            if(!CheckClassTimeToPass(
                    beans.getmScheduleStartTime(),beans.getmScheduleEndTime()))
                hasConflictBeanList.add(beans);
        }
        //1.2:弹出对话框提示用户时间冲突
        if(hasConflictBeanList.size() > 0) {
            String mTimeConflictTitle;
            mTimeConflictTitle = mContext.getString(R.string.schedule_class_time_error_0);
            Dialog dlg = DialogUtil.CustomNormalDialog(mContext,
                    mContext.getString(R.string.prompt),
                    mTimeConflictTitle,
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, mContext.getText(R.string.donothing_text).toString());
            dlg.show();
            return true;
        }

        //2：检查多课程时间是否冲突
        for(int i = 0; i < mTimeArray.size() -1; i++){
            ScheduleTimeBean mFirstBeans = mTimeArray.get(i);
            ScheduleTimeBean mTwoBeans = mTimeArray.get(i+1);
            if(!CheckClassTimeToPass(
                    mFirstBeans.getmScheduleEndTime(),mTwoBeans.getmScheduleStartTime())){
                String mTimeConflictTitle;
                mTimeConflictTitle = mContext.getString(R.string.schedule_class_time_error_1);
                Dialog dlg = DialogUtil.CustomNormalDialog(mContext,
                        mContext.getString(R.string.prompt),
                        mTimeConflictTitle,
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, mContext.getText(R.string.donothing_text).toString());
                dlg.show();
                return true;
            }
        }
        //3:多次检查后，没有冲突，设置返回
        return false;
    }

    public static String FormatWeatherInfo(String temp) {
        if(null == temp || temp.equalsIgnoreCase("")) return "";
        int index_char = temp.indexOf("~");
        if(index_char > 2) {
            String tmp_char_0,tmp_char_1;
            tmp_char_0 = temp.substring(0, index_char - 1);
            tmp_char_1 = temp.substring(index_char+1, temp.length() - 1);
            temp = tmp_char_0+"°/"+tmp_char_1+"°";
        }

        return temp;
    }

    public static boolean CheckClassTimeToPass(String mStartTime, String mEndTime) {
        boolean isPass ;
        if(mStartTime.length() < 4 || mEndTime.length() < 4 ) return false;
        int mStartHour = Integer.parseInt(mStartTime.substring(0,2));
        int mStartMin = Integer.parseInt(mStartTime.substring(2));
        int mEndHour = Integer.parseInt(mEndTime.substring(0,2));
        int mSEndMin = Integer.parseInt(mEndTime.substring(2));
        if(mStartHour > mEndHour){
            isPass = false;
        }else isPass = mStartHour != mEndHour || mStartMin < mSEndMin;

        return isPass;
    }

    public static void onTimeSelectForSchedule(Activity context, final RelativeLayout layout,
                                               String mStartHour, String mStartMinute,
                                               String mEndHour, String mEndMinute,
                                               CustomerPickerView.onSelectListener sHourListener,
                                               CustomerPickerView.onSelectListener sMinListener,
                                               CustomerPickerView.onSelectListener eHourListener,
                                               CustomerPickerView.onSelectListener eMinListener){
        CustomerPickerView mStartHourView;
        CustomerPickerView mStartMinView;
        CustomerPickerView mEndHourView;
        CustomerPickerView mEndMinView;

        final View view = View.inflate(context, R.layout.select_time_schedule, null);
        mStartHourView =  view.findViewById(R.id.start_hour_pv);
        mStartHourView.setMarginAlphaValue((float) 3.8, "H");
        mStartMinView = view.findViewById(R.id.start_min_pv);
        mStartMinView.setMarginAlphaValue((float) 3.8, "M");
        mEndHourView =  view.findViewById(R.id.end_hour_pv);
        mEndHourView.setMarginAlphaValue((float) 3.8, "H");
        mEndMinView = view.findViewById(R.id.end_min_pv);
        mEndMinView.setMarginAlphaValue((float) 3.8, "M");

        ImageView close_btn = view.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);
            }
        });

        List<String> StartHours = new ArrayList<>();
        List<String> StartMins = new ArrayList<>();
        List<String> EndHours = new ArrayList<>();
        List<String> EndMins = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hours = i < 10 ? "0" + i : "" + i;
            StartHours.add(hours);
            EndHours.add(hours);
        }
        for (int i = 0; i < 60; i++) {
            String min = i < 10 ? "0" + i : "" + i;
            StartMins.add(min);
            EndMins.add(min);
        }

        mStartHourView.setData(StartHours);
        mStartHourView.setOnSelectListener(sHourListener);
        mStartHourView.setSelected(Integer.valueOf(mStartHour));

        mStartMinView.setData(StartMins);
        mStartMinView.setOnSelectListener(sMinListener);
        mStartMinView.setSelected(Integer.valueOf(mStartMinute));

        mEndHourView.setData(EndHours);
        mEndHourView.setOnSelectListener(eHourListener);
        mEndHourView.setSelected(Integer.valueOf(mEndHour));

        mEndMinView.setData(EndMins);
        mEndMinView.setOnSelectListener(eMinListener);
        mEndMinView.setSelected(Integer.valueOf(mEndMinute));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layout.removeAllViews();
        layout.addView(view, params);
    }
}
