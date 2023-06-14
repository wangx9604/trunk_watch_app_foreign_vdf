package com.xiaoxun.xun.health.HeartRate;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.health.HeartRate.Data.DataStruct;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.HttpNetUtils;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HeartRateModel {
    private volatile static HeartRateModel instance;

    public static final String HEART_RATE_CLOUD_PATH = "EP/EID/HEART/";
    public static final String HEART_RATE_CLOUD_URL = "https://nfdsfile.xunkids.com/dateGet";
    public static final int ONE_DAY_ALL_MINUTES = 1440;
    public static final int ONE_DAY_INTEVAL_LIMIT = 30;

    Context mContext;
    ImibabyApp mApp;

    public ArrayList<DataStruct.DayData> dayDataList;
    public ArrayList<DataStruct.WeekData> weekDataList;
    public ArrayList<DataStruct.MonthData> monthDataList;

    public DataStruct.DayData todayData;

    public HeartRateModel(Context t) {
        mContext = t;
        mApp = (ImibabyApp) t.getApplicationContext();
        dayDataList = new ArrayList<>();
        weekDataList = new ArrayList<>();
        monthDataList = new ArrayList<>();
    }

    public static HeartRateModel getInstance(Context t) {
        if (instance == null) {
            synchronized (HeartRateModel.class) {
                if (instance == null) {
                    instance = new HeartRateModel(t);
                }
            }
        }
        return instance;
    }

    public void clearLocalData() {
        if(dayDataList != null) {
            dayDataList.clear();
        }
        if(weekDataList != null) {
            weekDataList.clear();
        }
        if(monthDataList != null) {
            monthDataList.clear();
        }
    }

    /**
     * 拉取一天的数据，如果有缓存取缓存，无缓存Http拉取；
     *
     * @param date 日期 格式化后的日期字符串
     * @return 当天数据
     */
    public DataStruct.DayData getDayDataByDate(String date) {
        LogUtil.e("HeartRateModel getDayDataByDate");
        for (DataStruct.DayData day : dayDataList) {
            if (date.equals(day.getDate())) {
                return day;
            }
        }
        DataStruct.DayData ret;
        JSONObject pl = new JSONObject();
        String eid = mApp.getCurUser().getFocusWatch().getEid();
        StringBuilder path = new StringBuilder();
        path.append("EP/");
        path.append(eid);
        path.append("/HEART/");
        pl.put("path", path.toString());
        pl.put("start", date);
        pl.put("end", date);
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP) + mApp.getToken();
        String responseJson = HttpNetUtils.httpPostJson(reqBody, HEART_RATE_CLOUD_URL, true);
        if (!TextUtils.isEmpty(responseJson)) {
            byte[] decBase64 = Base64.decode(responseJson, Base64.NO_WRAP);
            byte[] decMessage = AESUtil.decryptAESCBC(decBase64, mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY);
            if (decMessage != null && decMessage.length != 0) {
                String decMesString = new String(decMessage);
                LogUtil.e("getDayDataByDate resp : " + decMesString);
                JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                Integer code = (Integer) resp.get("code");
                if (code != 0) {
                    return null;
                }
                JSONArray array = (JSONArray) resp.get("data");
                if (array != null && array.size() != 0) {
                    JSONObject data = (JSONObject) array.get(0);
                    String value = (String) data.get(date);
                    //String decryptValue = decryptData(value);
                    ret = analyseDayValue(date, value);
                    dayDataList.add(ret);
                    return ret;
                } else {
                    return null;
                }
            }else{
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取日期所在周的周数据，先找缓存，无缓存去云端取
     *
     * @param date 日期
     * @return 周数据
     */
    public DataStruct.WeekData getWeekDataByDate(String date) {
        LogUtil.e("HeartRateModel getWeekDataByDate");
        int weekOfMonth = -1;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date day = format.parse(TimeUtil.getReversedTimeByTime(date));
            cal.setTime(day);
            weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (DataStruct.WeekData week : weekDataList) {
            if (week.getWeekOfMonth() == weekOfMonth && week.getWeekDates().get(0).equals(date)) {  //相同周并且相同周一的日期
                return week;
            }
        }

        ArrayList<DataStruct.DayData> values = new ArrayList<>();
        String[] dates = TimeUtil.getMonAndSunbyReserveDate(date);
        JSONObject pl = new JSONObject();
        String eid = mApp.getCurUser().getFocusWatch().getEid();
        StringBuilder path = new StringBuilder();
        path.append("EP/");
        path.append(eid);
        path.append("/HEART/");
        pl.put("path", path.toString());
        pl.put("start", dates[0]);
        pl.put("end", dates[1]);
        LogUtil.e("getWeekDataByDate reqBody : " + pl.toJSONString());
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP) + mApp.getToken();
        String responseJson = HttpNetUtils.httpPostJson(reqBody, HEART_RATE_CLOUD_URL, true);
        if (!TextUtils.isEmpty(responseJson)) {
            byte[] decBase64 = Base64.decode(responseJson, Base64.NO_WRAP);
            byte[] decMessage = AESUtil.decryptAESCBC(decBase64, mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY);
            if(decMessage != null && decMessage.length != 0) {
                String decMesString = new String(decMessage);
                LogUtil.e("getWeekDataByDate resp : " + decMesString);
                JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                Integer code = (Integer) resp.get("code");
                if (code != 0) {
                    return null;
                }
                JSONArray array = (JSONArray) resp.get("data");
                if (array != null && array.size() != 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject dayValue = (JSONObject) array.get(i);
                        String timeValue = null, dataValue = null;
                        for (Map.Entry<String, Object> entry : dayValue.entrySet()) {
                            timeValue = entry.getKey();
                            dataValue = (String) entry.getValue();
                        }
                        //String decryptValue = decryptData(dataValue);
                        DataStruct.DayData dayData = analyseDayValue(timeValue, dataValue);
                        values.add(0,dayData);
                    }
                    DataStruct.WeekData weekData = new DataStruct.WeekData(values);
                    weekData.setWeekOfMonth(weekOfMonth);
                    weekData.setWeekDates(getWeekDayDateByMonday(dates[0]));
                    weekDataList.add(weekData);
                    return weekData;
                } else {
                    return null;
                }
            }else {
                return null;
            }
        } else {
            return null;
        }
    }

    public DataStruct.MonthData getMonthDataByDate(String date) {
        LogUtil.e("HeartRateModel getMonthDataByDate");
        int month = -1;
        int year = -1;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date day = format.parse(TimeUtil.getReversedTimeByTime(date));
            cal.setTime(day);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (DataStruct.MonthData monthData : monthDataList) {
            if (monthData.getMonth() == month && monthData.getYear() == year) {
                return monthData;
            }
        }

        ArrayList<DataStruct.DayData> values = new ArrayList<>();
        String[] dates = TimeUtil.getFirstDayAndLastDayOfMonthByDate(date);
        JSONObject pl = new JSONObject();
        String eid = mApp.getCurUser().getFocusWatch().getEid();
        StringBuilder path = new StringBuilder();
        path.append("EP/");
        path.append(eid);
        path.append("/HEART/");
        pl.put("path", path.toString());
        pl.put("start", dates[0]);
        pl.put("end", dates[1]);
        LogUtil.e("getMonthDataByDate reqbody : " + pl.toJSONString());
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP) + mApp.getToken();
        String responseJson = HttpNetUtils.httpPostJson(reqBody, HEART_RATE_CLOUD_URL, true);
        if (!TextUtils.isEmpty(responseJson)) {
            byte[] decBase64 = Base64.decode(responseJson, Base64.NO_WRAP);
            byte[] decMessage = AESUtil.decryptAESCBC(decBase64, mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY);
            if(decMessage != null && decMessage.length != 0) {
                String decMesString = new String(decMessage);
                LogUtil.e("getMonthDataByDate resp : " + decMesString);
                JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                Integer code = (Integer) resp.get("code");
                if (code != 0) {
                    return null;
                }
                JSONArray array = (JSONArray) resp.get("data");
                if (array != null && array.size() != 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject dayValue = (JSONObject) array.get(i);
                        String timeValue = null, dataValue = null;
                        for (Map.Entry<String, Object> entry : dayValue.entrySet()) {
                            timeValue = entry.getKey();
                            dataValue = (String) entry.getValue();
                        }
                        //String decryptValue = decryptData(dataValue);
                        DataStruct.DayData dayData = analyseDayValue(timeValue, dataValue);
                        values.add(0,dayData);
                    }
                    DataStruct.MonthData monthData = new DataStruct.MonthData(year, month, values);
                    monthDataList.add(monthData);
                    return monthData;
                } else {
                    return null;
                }
            }else{
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 解析一天的心率数据  以逗号分割，最后一组数据为当天的统计数据
     *
     * @param date  日期
     * @param value 待数据字符串   “743|0|92,744|0|78,745|0|79 ... ，high|low|average|resting”
     * @return
     */
    public static DataStruct.DayData analyseDayValue(String date, String value) {
        if (date == null || value == null) {
            LogUtil.e("analyseDayValue date or value null.");
            return null;
        }
        ArrayList<DataStruct.BaseData> minValues = new ArrayList<>();
        String[] datas = value.split(",");
        for (int i = 0; i < datas.length - 1; i++) {
            String[] min_data = datas[i].split("\\|");
            Integer min = Integer.parseInt(min_data[0]);
            Integer rate = Integer.parseInt(min_data[2]);
            DataStruct.BaseData baseData = new DataStruct.BaseData(min, rate);
            minValues.add(baseData);
        }
        String dayStatistics = datas[datas.length - 1];
        String[] statisticStr = dayStatistics.split("\\|");
        DataStruct.DayData ret = new DataStruct.DayData(date, minValues);
        ret.setHighRate(Integer.parseInt(statisticStr[0]));
        ret.setLowRate(Integer.parseInt(statisticStr[1]));
        ret.setAverageRate(Integer.parseInt(statisticStr[2]));
        ret.setRestingRate(Integer.parseInt(statisticStr[3]));

        return ret;
    }

    /**
     * 一天的数据可能不连续，需要分成多条连续数据用来提供给chart进行绘制
     *
     * @param dayMap 一天的所有数据
     * @return 多条曲线的数据
     */
    public ArrayList<ArrayList<DataStruct.BaseData>> getDayLineData(ArrayList<DataStruct.BaseData> dayMap) {
        ArrayList<ArrayList<DataStruct.BaseData>> ret = new ArrayList<>();
        int curLineIndex = 0;
        ArrayList<DataStruct.BaseData> headLine = new ArrayList<>();
        ret.add(headLine);
        for(int i = 0;i < dayMap.size()-1;i++){
            ret.get(curLineIndex).add(dayMap.get(i));
            int inteval = dayMap.get(i + 1).getMin() - dayMap.get(i).getMin();
            if(inteval >ONE_DAY_INTEVAL_LIMIT){
                ArrayList<DataStruct.BaseData> line = new ArrayList<>();
                ret.add(line);
                curLineIndex++;
            }
        }
        ret.get(curLineIndex).add(dayMap.get(dayMap.size()-1));
        LogUtil.e("getDayLineData day lines = " + ret.size());
        return ret;
    }

    private int getOrderDataIndexEndFromList(int start, ArrayList<DataStruct.BaseData> list) {
        int end = start;
        for (int i = start; i < list.size(); i++) {
            DataStruct.BaseData item = list.get(i);
            if (item.getRate() > 0) {
                end = i;
            } else {
                break;
            }
        }
        return end;
    }

    private int getOrderVirDataIndexEndFromList(int start, ArrayList<DataStruct.BaseData> list) {
        int end = start;
        for (int i = start; i < list.size(); i++) {
            DataStruct.BaseData item = list.get(i);
            if (item.getRate() == -1) {
                end = i;
            } else {
                break;
            }
        }
        return end;
    }

    private ArrayList<String> getWeekDayDateByMonday(String monday) {
        ArrayList<String> ret = new ArrayList<>();
        Date d = TimeUtil.getDateFromOrderTimeStr(monday);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        ret.add(monday);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            String orderTime = TimeUtil.getDayTimeStrFromDate(cal.getTime());
            String reverseTime = TimeUtil.getReversedTimeByTime(orderTime);
            ret.add(reverseTime);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return ret;
    }

    private String decryptData(String data){
        byte[] decBase64 = Base64.decode(data, Base64.NO_WRAP);
        String key = mApp.getNetService().AES_KEY;//mApp.getCurUser().getFocusWatch().getEid().substring(0,16)
        byte[] bytes = AESUtil.decryptAESCBC(decBase64,key,key);
        return new String(bytes);
    }

    public DataStruct.DayData simulateData(String date) {
        ArrayList<DataStruct.BaseData> minValues = new ArrayList<>();
        for (int i = 0; i < ONE_DAY_ALL_MINUTES; i++) {
            if (i > 400 && i < 900) {
                int minData = 65;
                DataStruct.BaseData baseData = new DataStruct.BaseData(i, minData);
                minValues.add(baseData);
                continue;
            }

            if (i < 1200 && i > 200) {
                continue;
            }

            int minData = (int) (Math.random() * 5) + 60;
            DataStruct.BaseData baseData = new DataStruct.BaseData(i, minData);
            minValues.add(baseData);
        }
        DataStruct.DayData ret = new DataStruct.DayData(date, minValues);
        ret.setHighRate(70);
        ret.setLowRate(40);
        ret.setAverageRate(55);
        ret.setRestingRate(60);

        return ret;
    }

    public DataStruct.WeekData simulateWeekData(String date) {
        ArrayList<DataStruct.DayData> weekDatas = new ArrayList<>();
        String orderTime = TimeUtil.getReversedTimeByTime(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMDD", Locale.getDefault());
        try {
            Date d = format.parse(orderTime);
            Calendar calpre = Calendar.getInstance();
            calpre.setTime(d);
            while (calpre.get(Calendar.DAY_OF_WEEK) >= Calendar.SUNDAY) {
                Date d1 = calpre.getTime();
                String str = TimeUtil.getDayTimeStrFromDate(d1);
                String strReverse = TimeUtil.getReversedTimeByTime(str);
                DataStruct.DayData dayData = simulateData(strReverse);
                weekDatas.add(0, dayData);
                calpre.add(Calendar.DAY_OF_YEAR, -1);
            }
            Calendar calnext = Calendar.getInstance();
            calnext.setTime(d);
            while (calnext.get(Calendar.DAY_OF_WEEK) <= Calendar.SATURDAY) {
                Date d2 = calnext.getTime();
                String str2 = TimeUtil.getDayTimeStrFromDate(d2);
                String strRecerse2 = TimeUtil.getReversedTimeByTime(str2);
                DataStruct.DayData dayData2 = simulateData(strRecerse2);
                weekDatas.add(dayData2);
                calnext.add(Calendar.DAY_OF_YEAR, 1);
            }
            DataStruct.DayData temp = weekDatas.get(0);
            weekDatas.add(temp);
            weekDatas.remove(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DataStruct.WeekData weekData = new DataStruct.WeekData(weekDatas);
        return weekData;
    }

    public DataStruct.WeekData simulateWeekData() {
        String[] startDateStr = new String[]{"20210426", "20210427", "20210428", "20210430", "20210501", "20210502"};//"20210429",
        ArrayList<DataStruct.DayData> weekDatas = new ArrayList<>();
        for (int i = 0; i < startDateStr.length; i++) {
            String reverseTime = TimeUtil.getReversedTimeByTime(startDateStr[i]);
            DataStruct.DayData dayData = simulateData(reverseTime);
            weekDatas.add(dayData);
        }
        ArrayList<String> dates = getWeekDayDateByMonday(TimeUtil.getReversedTimeByTime(startDateStr[0]));
        DataStruct.WeekData weekData = new DataStruct.WeekData(weekDatas);
        LogUtil.e("simulateWeekData weekDatas size = " + weekDatas.size());
        weekData.setWeekDates(dates);
        return weekData;
    }

    public DataStruct.MonthData simulateMonthData(String date) {
        ArrayList<DataStruct.DayData> monthDatas = new ArrayList<>();
        String[] dates = TimeUtil.getFirstDayAndLastDayOfMonthByDate(date);
        Date startD = TimeUtil.getDateFromOrderTimeStr(dates[0]);
        Date endD = TimeUtil.getDateFromOrderTimeStr(dates[1]);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startD);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endD);
        while (startCal.get(Calendar.DAY_OF_YEAR) != endCal.get(Calendar.DAY_OF_YEAR)) {
            Date temp = startCal.getTime();
            String dateStr = TimeUtil.getDayTimeStrFromDate(temp);
            String dateReverseStr = TimeUtil.getReversedTimeByTime(dateStr);
            DataStruct.DayData dayData = simulateData(dateReverseStr);
            monthDatas.add(dayData);
            startCal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date temp = endCal.getTime();
        String dateStr = TimeUtil.getDayTimeStrFromDate(temp);
        String dateReverseStr = TimeUtil.getReversedTimeByTime(dateStr);
        DataStruct.DayData dayData = simulateData(dateReverseStr);
        monthDatas.add(dayData);

        int year = endCal.get(Calendar.YEAR);
        int month = endCal.get(Calendar.MONTH);
        DataStruct.MonthData monthData = new DataStruct.MonthData(year, month, monthDatas);
        return monthData;
    }
}
