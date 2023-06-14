package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.SportRunBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangjun5 on 2019/7/27.
 */

public class Sport2Utils {

    public static int getIntFromResult(String result,String key){
        int code = -1;
        try {
            JSONObject obj = new JSONObject(result);
            code = obj.getInt(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }

    public static String getStringFromResult(String result,String key){
        String retStr="";
        try {
            JSONObject obj = new JSONObject(result);
            retStr = obj.getString(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return retStr;
    }

    public static String updateTitleAndHint(Context ctxt,TextView tv_title, int index) {
        String zone ="";
        if(index == 0){
            tv_title.setText(R.string.sport_rank_info_0);
            zone = ctxt.getString(R.string.sport_rank_info_0);
        }else if(index == 1){
            tv_title.setText(R.string.ranks_country);
            zone = ctxt.getString(R.string.ranks_country);
        }else if(index == 2){
            tv_title.setText(R.string.sport_rank_info_1);
            zone = ctxt.getString(R.string.sport_rank_info_1);
        }
        return zone;
    }

    public static void updateIndicatorView(int index, ImageView leftView,
                                     ImageView midView, ImageView rigView) {
        if(index == 0){
            leftView.setBackgroundResource(R.drawable.rectangle_shape_share);
            midView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
            rigView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
        }else if(index == 1){
            leftView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
            midView.setBackgroundResource(R.drawable.rectangle_shape_share);
            rigView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
        }else if(index == 2){
            leftView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
            midView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
            rigView.setBackgroundResource(R.drawable.rectangle_shape_share);
        }else{
            leftView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
            midView.setBackgroundResource(R.drawable.rectangle_shape_share);
            rigView.setBackgroundResource(R.drawable.rectangle_shape_share_grey);
        }
    }

    public static void initStateTitle(Activity activity){
        //设置沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true,activity); //状态栏透明 需要在创建SystemBarTintManager 之前调用。
        }
//        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
//        mTintManager.setStatusBarTintEnabled(true);
//        mTintManager.setTintColor(activity.getResources().getColor(R.color.bg_color_orange));
//        mTintManager.setStatusBarDarkMode(true, activity);
        StatusBarUtil.changeStatusBarColor(activity, activity.getResources().getColor(R.color.bg_color_orange));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 解决部分5.x系统使用状态栏透明属性后状态栏变黑色，不使用这句代码，在6.0设备上又出现半透明状态栏
//            // 需要特殊处理
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
    }

    public static String initSportMonthListByDefaultData(){
        JSONArray jsonArray = new JSONArray();
        try{
            for (int i = 10; i >= 0; i--) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.SPORT_DAYS_DATA_TRUE_KEY, TimeUtil.getDaysByDays(i));
                jsonObject.put(Constants.SPORT_DAYS_DATA_KEY, TimeUtil.getDaysByDays(i));
                jsonObject.put(Constants.SPORT_DAYS_DATA_VALUE, 0);
                jsonArray.put(jsonObject);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public static List<Map<String, String>> initSportListByDefaultData() {
        List<Map<String, String>> list = new ArrayList<>();
        try{
            for(int i = 0; i< 24;i++){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.SPORT_DAYS_DATA_KEY, i+"");
                map.put(Constants.SPORT_DAYS_DATA_VALUE, 0+"");
                list.add(map);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    private static void setTranslucentStatus(boolean on,Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public static List<SportRunBean> parseSportRunningByResult(ImibabyApp myApp, Context ctxt, String result) {
        List<SportRunBean> mRunList = new ArrayList<>();
        try{
            List<String> dataList = new ArrayList();
            JSONObject obj = new JSONObject(result);
            JSONObject datajs = (JSONObject) obj.get("datajs");
            Iterator iterator = datajs.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                dataList.add(key);
            }

            for (int i = 0; i < dataList.size(); i++) {
                JSONObject jsonObject = (JSONObject)datajs.get(dataList.get(i));
                String mRunType = jsonObject.getString(Constants.SPORT_RUNNING_TYPE);
                String distance;
                if(jsonObject.isNull(Constants.SPORT_LAST_RUNNING_DISTANCE)){
                    distance = jsonObject.getString(Constants.SPORT_LAST_RUNNING_STEPS);
                }else{
                    distance = jsonObject.getString(Constants.SPORT_LAST_RUNNING_DISTANCE);
                }
                String count = "0";
                if(!jsonObject.isNull(Constants.SPORT_RUNNING_COUNT)){
                    count = jsonObject.getString(Constants.SPORT_RUNNING_COUNT);
                }

                addSportRunningBeanToList(ctxt,mRunList,1,dataList.get(i),
                        jsonObject.getString(Constants.SPORT_LAST_RUNNING_STIME),
                        jsonObject.getString(Constants.SPORT_LAST_RUNNING_ETIME),
                        distance,mRunType,count);
            }

            //根据日期进行排序
            Collections.sort(mRunList, new Comparator<SportRunBean>() {
                @Override
                public int compare(SportRunBean sportRunBean, SportRunBean t1) {
                    return -sportRunBean.mRunTimeStamp.compareTo(t1.mRunTimeStamp);
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
        return mRunList;
    }

    public static String parseSportMonthListByResult(String data){
        List<Map<String, String>> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        try{
            JSONObject obj = new JSONObject(data);
            JSONObject datajs = (JSONObject)obj.get("datajs");
            List<String> dataList = new ArrayList();
            Iterator iterator = datajs.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                dataList.add(key);
            }

            for (int i = 0; i < dataList.size(); i++) {
                String steps = String.valueOf(datajs.get(dataList.get(i)));
                Map<String, String> map = new HashMap<>();
                map.put(Constants.SPORT_DAYS_DATA_TRUE_KEY, dataList.get(i));
                map.put(Constants.SPORT_DAYS_DATA_KEY, TimeUtil.getMonthDay(dataList.get(i)));
                map.put(Constants.SPORT_DAYS_DATA_VALUE, steps);
                list.add(map);
            }
            Collections.sort(list, new Comparator<Map<String, String>>() {
                @Override
                public int compare(Map<String, String> stringStringMap, Map<String, String> t1) {
                    return stringStringMap.get(Constants.SPORT_DAYS_DATA_KEY).compareTo(t1.get(Constants.SPORT_DAYS_DATA_KEY));
                }
            });
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = list.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.SPORT_DAYS_DATA_TRUE_KEY, map.get(Constants.SPORT_DAYS_DATA_TRUE_KEY));
                jsonObject.put(Constants.SPORT_DAYS_DATA_KEY, map.get(Constants.SPORT_DAYS_DATA_KEY));
                jsonObject.put(Constants.SPORT_DAYS_DATA_VALUE, map.get(Constants.SPORT_DAYS_DATA_VALUE));
                jsonArray.put(jsonObject);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public static List<Map<String, String>> parseSportListByCurDaysData(String result) {
        List<Map<String, String>> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i< jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Map<String, String> map = new HashMap<>();
                map.put(Constants.SPORT_DAYS_DATA_KEY, jsonObject.getString(Constants.SPORT_DAYS_DATA_KEY));
                map.put(Constants.SPORT_DAYS_DATA_VALUE, jsonObject.getString(Constants.SPORT_DAYS_DATA_VALUE));
                list.add(map);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public static Map<String, String> parseSportSnapListByResult(String result) {
        Map<String, String> map = new HashMap<>();
        try{
            JSONObject obj = new JSONObject(result);
            JSONObject jsonObject = (JSONObject) obj.get("datajs");
            JSONArray rankData = jsonObject.getJSONArray("latestrank");
            JSONObject sportData = (JSONObject) jsonObject.get("latestsport");
            JSONObject medalData = (JSONObject) jsonObject.get("latestmedal");
            if(sportData.length() > 0) {
                map.put(Constants.SPORT_LAST_RUNNING_STIME, sportData.getString(Constants.SPORT_LAST_RUNNING_STIME));
                map.put(Constants.SPORT_LAST_RUNNING_ETIME, sportData.getString(Constants.SPORT_LAST_RUNNING_ETIME));
                map.put(Constants.SPORT_LAST_RUNNING_DISTANCE, sportData.getString(Constants.SPORT_LAST_RUNNING_DISTANCE));
                map.put(Constants.SPORT_RUNNING_TYPE, sportData.getString(Constants.SPORT_RUNNING_TYPE));
                if(!sportData.isNull(Constants.SPORT_RUNNING_COUNT))
                    map.put(Constants.SPORT_RUNNING_COUNT, sportData.getString(Constants.SPORT_RUNNING_COUNT));
            }
            if(medalData.length() > 0) {
                map.put(Constants.SPORT_LAST_MEDAL_NAME, medalData.getString(Constants.SPORT_LAST_MEDAL_NAME));
            }
            if(rankData.length() > 0) {
                map.put(Constants.SPORT_LAST_RANK, rankData.toString());

                for (int i = 0; i < rankData.length(); i++) {
                    JSONObject rankObject = (JSONObject) rankData.get(i);
                    int type = rankObject.getInt("type");
                    if (type == 1) {
                        map.put(Constants.SPORT_LAST_RANK_0, rankObject.getString("rank"));
                    } else if (type == 2) {
                        map.put(Constants.SPORT_LAST_RANK_1, rankObject.getString("rank"));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return map;
    }

    public static void parseCurDaysData(List<Map<String,String>> mSportCurDaysData,String dataList){
        mSportCurDaysData.clear();
        mSportCurDaysData.addAll(Sport2Utils.parseSportListByCurDaysData(dataList));
    }

    public static List<Map<String,String>> parseRankDateByResult(String mRankData) {
        List<Map<String, String>> list = new ArrayList<>();
        try{
            JSONArray array = new JSONArray(mRankData);
            for(int i = 0; i< array.length();i++){
                JSONObject jsonObject = (JSONObject) array.get(i);
                Map<String, String> map = new HashMap<>();
                map.put(Constants.SPORT_RANK_SELFSTEPS, jsonObject.getString(Constants.SPORT_RANK_SELFSTEPS));
                map.put(Constants.SPORT_RANK_LAMINATION, jsonObject.getString(Constants.SPORT_RANK_LAMINATION));
                map.put(Constants.SPORT_RANK_AVERAGESTEPS, jsonObject.getString(Constants.SPORT_RANK_AVERAGESTEPS));
                map.put(Constants.SPORT_RANK_RANK, jsonObject.getString(Constants.SPORT_RANK_RANK));
                map.put(Constants.SPORT_RANK_RANGERANK, jsonObject.getString(Constants.SPORT_RANK_RANGERANK));
                map.put(Constants.SPORT_RANK_WEIGHT, jsonObject.getString(Constants.SPORT_RANK_WEIGHT));
                map.put(Constants.SPORT_RANK_TYPE, jsonObject.getString(Constants.SPORT_RANK_TYPE));
                map.put(Constants.SPORT_RANK_HEIGHT, jsonObject.getString(Constants.SPORT_RANK_HEIGHT));

                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static List<Map<String, String>> parseChallListByResult(String result) {
        List<Map<String, String>> list = new ArrayList<>();
        try{
            JSONObject obj = new JSONObject(result);
            JSONArray array = obj.getJSONArray("dataarray");
            for(int i = 0; i< array.length();i++){
                JSONObject jsonObject = (JSONObject) array.get(i);
                Map<String, String> map = new HashMap<>();
                map.put(Constants.SPORT_CHALL_NAME, jsonObject.getString(Constants.SPORT_CHALL_NAME));
                map.put(Constants.SPORT_CHALL_COPYTEXT, jsonObject.getString(Constants.SPORT_CHALL_COPYTEXT));
                map.put(Constants.SPORT_CHALL_BRIGHTICON, jsonObject.getString(Constants.SPORT_CHALL_BRIGHTICON));
                map.put(Constants.SPORT_CHALL_GLOOMYICON, jsonObject.getString(Constants.SPORT_CHALL_GLOOMYICON));
                map.put(Constants.SPORT_CHALL_ISACTIVED, jsonObject.getString(Constants.SPORT_CHALL_ISACTIVED));
                map.put(Constants.SPORT_CHALL_ACTIVETIME, jsonObject.getString(Constants.SPORT_CHALL_ACTIVETIME));
                map.put(Constants.SPORT_CHALL_ACTIVERATIO, jsonObject.getString(Constants.SPORT_CHALL_ACTIVERATIO));
                map.put(Constants.SPORT_CHALL_BIGDETAILSICON, jsonObject.getString(Constants.SPORT_CHALL_BIGDETAILSICON));
                map.put(Constants.SPORT_CHALL_MEDALTYPE, jsonObject.getString(Constants.SPORT_CHALL_MEDALTYPE));
                map.put(Constants.SPORT_CHALL_MEDALVALUE, jsonObject.getString(Constants.SPORT_CHALL_MEDALVALUE));

                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Collections.sort(list, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> stringStringMap, Map<String, String> t1) {
                int retCode;
                if((Integer.valueOf(stringStringMap.get(Constants.SPORT_CHALL_MEDALTYPE))
                        - Integer.valueOf(t1.get(Constants.SPORT_CHALL_MEDALTYPE))) == 0){
                    retCode =  Integer.valueOf(stringStringMap.get(Constants.SPORT_CHALL_MEDALVALUE))
                                - Integer.valueOf(t1.get(Constants.SPORT_CHALL_MEDALVALUE));
                }else{
                    retCode =  Integer.valueOf(stringStringMap.get(Constants.SPORT_CHALL_MEDALTYPE))
                            - Integer.valueOf(t1.get(Constants.SPORT_CHALL_MEDALTYPE));
                }
                return retCode;
            }
        });

        return list;
    }

    public static String[] parseXValsPageByLamin(String sportData,String key) {
        String [] array = new String[24];
        try{
            JSONArray jsonArray = new JSONArray(sportData);
            array = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                array[i] = jsonObject.getString(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return array;
    }

    public static int matchSportArrayAtPos(String[] array,String matchStr){
        int retPos = 0;
        if(array==null || array.length == 0){
            return retPos;
        }
        for (int i = 0; i < array.length; i++) {
            if(matchStr.equalsIgnoreCase(array[i])){
                retPos = i;
            }
        }
        return retPos;
    }

    public static void moveToEndAndHighlight(BarChart barchart1, int moveIndex){
        barchart1.highlightValue(moveIndex, 0);
        barchart1.moveViewToX(moveIndex);
        barchart1.invalidate();
    }

    public static List<BarEntry> parseYValsPageByLamin(String sportData) {
        List<BarEntry> barList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(sportData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                barList.add(new BarEntry(Integer.valueOf(jsonObject.getString(Constants.SPORT_DAYS_DATA_VALUE)), i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return barList;
    }

    public static List<BarEntry> parseYValsBarEntryMDataExt(String sportData){
        List<BarEntry> barList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(sportData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                barList.add(new BarEntry(Integer.valueOf(jsonObject.getString(Constants.SPORT_DAYS_DATA_VALUE))
                        , i, jsonObject.getString(Constants.SPORT_DAYS_DATA_TRUE_KEY)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return barList;
    }

    public static String CalcSportDura(String s, String s1) {
        if(s== null || s1 == null)
            return "00:00:00";

        long mCalcSec = TimeUtil.compareToDiffForTwoTime(s,s1);
        String mCalcSporDura = TimeUtil.formatTimeMs(mCalcSec*1000,true);

        return mCalcSporDura;
    }

    public static String getRunningTypeInfo(Context ctxt,String mRunType){
        String mRunTypeInfo;
        if(mRunType != null) {
            if(mRunType.equals("2"))
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_0);
            else if(mRunType.equals("3"))
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_1);
            else if(mRunType.equals("4"))
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_2);
            else if(mRunType.equals("5"))
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_3);
            else if(mRunType.equals("6"))
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_4);
            else
                mRunTypeInfo = ctxt.getString(R.string.sport_running_type_1);
        }else{
            mRunTypeInfo = ctxt.getString(R.string.sport_running_type_1);
        }
        return mRunTypeInfo;
    }

    public static int getRunningTypeRid(String mRunType){
        int ResId;
        if(mRunType != null) {
            if(mRunType.equals("2"))
                ResId = R.drawable.sport_run_0;
            else if(mRunType.equals("3"))
                ResId = R.drawable.sport_run_1;
            else if(mRunType.equals("4"))
                ResId = R.drawable.sport_run_2;
            else if(mRunType.equals("5"))
                ResId = R.drawable.sport_run_3;
            else if(mRunType.equals("6"))
                ResId = R.drawable.sport_run_4;
            else
                ResId = R.drawable.sport_run_0;
        }else{
            ResId = R.drawable.sport_run_0;
        }

        return ResId;
    }

    /*
     *展示不可操作的固定图标，
     */
    public static void setShowChartFix(BarLineChartBase barChart){
        barChart.setTouchEnabled(false);
        barChart.setEnabled(false);
        barChart.setClickable(false);
        barChart.setSelected(false);
        barChart.setLongClickable(false);
        barChart.setScaleEnabled(false);
    }

    public static void initChartParms(BarLineChartBase barChart){
        barChart.setDescription("");
        barChart.setNoDataText("");
        // 集双指缩放
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(true);//是否显示x坐标的数据
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x坐标数据的位置
        xAxis.setDrawGridLines(false);//是否显示网格线中与x轴垂直的网格线

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setEnabled(true);//设置右侧的y轴是否显示。包括y轴的那一条线和上面的标签都不显示
        rightYAxis.setDrawLabels(false);//设置y轴右侧的标签是否显示。只是控制y轴处的标签。控制不了那根线。
        rightYAxis.setDrawAxisLine(false);//这个方法就是专门控制坐标轴线的

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        leftYAxis.setDrawLabels(true);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftYAxis.setDrawGridLines(false);//只有左右y轴标签都设置不显示水平网格线，图形才不会显示网格线
        leftYAxis.setSpaceBottom(0);//左轴的最小值默认占有10dp的高度，如果左轴最小值为0，一般会去除0的那部分高度
        //Y轴的坐标值标准化
        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                if(v<=1) v = v*10;
                return String.valueOf((int)v);
            }
        });
    }

    public static void setBarchartZoomCount(BarLineChartBase barChart, int xValusSum, int count){
        //设置一页最大显示个数为6，超出部分就滑动
        float ratio = (float) xValusSum/(float) count;
        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
        barChart.zoom(ratio,1f,0,0);
    }

    public static void setBarChartValue(String hintTxt, String[] XVals, ArrayList<BarEntry> barEntries, BarChart barChart){
        if(null == XVals || XVals.length ==0 ){
            return ;
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, hintTxt);
        barDataSet.setColor(Color.parseColor("#f66d3e"));
        BarData barData = new BarData(XVals,
                barDataSet);
        barData.setDrawValues(false);//是否显示柱子的数值
        barChart.setData(barData);
        barChart.invalidate();
    }

    public static void setChartViewMoveClick(BarLineChartBase barLineChart, int index, OnChartValueSelectedListener listener) {
        barLineChart.moveViewToX(index);
        barLineChart.setOnChartValueSelectedListener(listener);
    }

    public static void setChartValue(Context ctxt, ArrayList<String> xVals, ArrayList<Entry> yVals, LineChart mChart){
        LineDataSet set1 = new LineDataSet(yVals, ctxt.getString(R.string.share_to_steps));

        set1.setColor(Color.parseColor("#f66d3e"));
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawCircles(true);
        set1.setCircleSize(3f);
        set1.setCircleColor(Color.parseColor("#f66d3e"));
        set1.setCircleColorHole(Color.parseColor("#f66d3e"));
        set1.setFillAlpha(10);
        set1.setFillColor(Color.parseColor("#f66d3e"));
        set1.setDrawFilled(true);
        set1.setDrawValues(false);
        set1.setValueTextSize(9f);
        set1.setValueTextColor(Color.parseColor("#f66d3e"));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(xVals, dataSets);
        data.setDrawValues(false);
        // set data
        mChart.setData(data);
        mChart.invalidate();
    }


    public static int setBarChartLoadData(Context ctxt,List<Map<String,String>> mSportCurDaysData, BarChart mChart) {
        float stepTotal = 0;
        if(mSportCurDaysData.size() == 0){
            return 0;
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        String[] array = new String[mSportCurDaysData.size()];
        for(int i = 0; i < mSportCurDaysData.size(); i++){
            array[i] = mSportCurDaysData.get(i).get(Constants.SPORT_DAYS_DATA_KEY);
            int stepCount = Integer.valueOf(mSportCurDaysData.get(i).get(Constants.SPORT_DAYS_DATA_VALUE));
            stepTotal += stepCount;
            barEntries.add(new BarEntry(stepCount, i));
        }
        setBarChartValue(ctxt.getString(R.string.share_to_steps),array,barEntries,mChart);
//
//        ArrayList<String> xVals = new ArrayList<>();
//        ArrayList<Entry> yVals = new ArrayList<Entry>();
//        for(int i= 0;i<24;i++){
//            xVals.add(i+"");
//            stepTotal += Float.valueOf(mSportCurDaysData.get(i).get(Constants.SPORT_DAYS_DATA_VALUE));
//            yVals.add(new Entry(stepTotal, i));
//        }
//
//        Sport2Utils.setChartValue(ctxt,xVals,yVals,mChart);

        return (int)stepTotal;
    }

    public static void TranSportRunToAllTypeRun(Context ctxt,List<SportRunBean> mRunDatas, List<SportRunBean> mAllTypeRunDatas) {
        mAllTypeRunDatas.clear();
        Map<String,String> map = new HashMap<>();

        for (SportRunBean bean:mRunDatas) {
            map.put(bean.mRunDate, bean.mRunTimeStamp);
        }

        List<Map.Entry<String, String>> list = soreHashMapData(map);
        for (Map.Entry<String, String> mapping  : list) {
            addSportRunningBeanToList(ctxt,mAllTypeRunDatas,0,mapping.getValue(),"","","","","0");
            for (SportRunBean bean:mRunDatas) {
                if(bean.mRunDate.equals(mapping.getKey()))
                    mAllTypeRunDatas.add(bean);
            }
            addSportRunningBeanToList(ctxt,mAllTypeRunDatas,2,mapping.getValue(),"","","","","0");
        }
    }

    private static List<Map.Entry<String, String>> soreHashMapData(Map<String, String> map) {
        List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        });

        return list;
    }

    public static void addSportRunningBeanToList(Context ctxt, List<SportRunBean> mRunDatas,
                                                 int mLayoutType, String mRunTimeStamp, String mRunST,
                                                 String mRunET, String mRunSteps, String mRunType,
                                                 String count){
        SportRunBean bean = new SportRunBean();
        bean.mLayoutType = mLayoutType;
        bean.mRunType = mRunType;
        bean.mRunDate = TimeUtil.getDay(mRunTimeStamp);
        bean.mRunST = mRunST;
        bean.mRunET = mRunET;
        bean.mRunSteps = mRunSteps;
        bean.mRunTimeStamp = mRunTimeStamp;
        bean.mCount = count;

        if(!mRunSteps.equals("")) {
            bean.mRunKilo = StepsUtil.formatKiloByMeter(Double.valueOf(bean.mRunSteps),
                    " "+ctxt.getString(R.string.unit_kilometer),
                    " "+ctxt.getString(R.string.unit_meter));
        }
        if("6".equals(mRunType)){
            bean.mRunKilo = bean.mCount +" "+ ctxt.getString(R.string.sport_rope_jump_unit);
        }
        bean.mRunDura = Sport2Utils.CalcSportDura(bean.mRunST, bean.mRunET);
        mRunDatas.add(bean);
    }

}
