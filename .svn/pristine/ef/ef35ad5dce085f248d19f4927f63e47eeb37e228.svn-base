package com.xiaoxun.xun.health.report.fragments.month;

import static com.xiaoxun.xun.health.bean.FatigueData.DURATION_VALUE;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.health.bean.FatigueBrainBean;
import com.xiaoxun.xun.health.bean.FatigueData;
import com.xiaoxun.xun.health.bean.FatigueSightBean;
import com.xiaoxun.xun.health.report.ReportActivity;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayChartData;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayOxyData;
import com.xiaoxun.xun.health.report.fragments.dataBean.KChartData;
import com.xiaoxun.xun.utils.HttpWorks;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MonthFragmentViewModel extends ViewModel {

    private MutableLiveData<List<DayChartData>> stepsDatas;
    private MutableLiveData<String> totalSteps;
    private MutableLiveData<String> aveSteps;
    private MutableLiveData<String> totalCal;
    private MutableLiveData<String> totalDistance;

    private MutableLiveData<Number> latestHeartRate;
    private MutableLiveData<List<KChartData>> heartRateDatas;
    private MutableLiveData<Number> highHeartRate;
    private MutableLiveData<Number> lowHeartRate;
    private MutableLiveData<Number> aveHeartRate;

    private MutableLiveData<Number> latestOxy;
    private MutableLiveData<List<KChartData>> oxyDatas;
    private MutableLiveData<Number> highOxy;
    private MutableLiveData<Number> lowOxy;
    private MutableLiveData<Number> aveOxy;


    public MonthFragmentViewModel() {
        ArrayList<DayChartData> list = new ArrayList<>();
        stepsDatas = new MutableLiveData<>();
        stepsDatas.setValue(list);
        totalSteps = new MutableLiveData<>();
        aveSteps = new MutableLiveData<>();
        totalCal = new MutableLiveData<>();
        totalDistance = new MutableLiveData<>();

        latestHeartRate = new MutableLiveData<>();
        ArrayList<KChartData> list1 = new ArrayList<>();
        heartRateDatas = new MutableLiveData<>();
        heartRateDatas.setValue(list1);
        highHeartRate = new MutableLiveData<>();
        lowHeartRate = new MutableLiveData<>();
        aveHeartRate = new MutableLiveData<>();

        latestOxy = new MutableLiveData<>();
        ArrayList<KChartData> list2 = new ArrayList<>();
        oxyDatas = new MutableLiveData<>();
        oxyDatas.setValue(list2);
        highOxy = new MutableLiveData<>();
        lowOxy = new MutableLiveData<>();
        aveOxy = new MutableLiveData<>();
    }

    public MutableLiveData<List<DayChartData>> getStepsDatas() {
        return stepsDatas;
    }

    public MutableLiveData<String> getTotalSteps() {
        return totalSteps;
    }

    public MutableLiveData<String> getTotalCal() {
        return totalCal;
    }

    public MutableLiveData<String> getTotalDistance() {
        return totalDistance;
    }

    public MutableLiveData<String> getAveSteps() {
        return aveSteps;
    }

    public MutableLiveData<Number> getLatestHeartRate() {
        return latestHeartRate;
    }

    public MutableLiveData<Number> getHighHeartRate() {
        return highHeartRate;
    }

    public MutableLiveData<Number> getAveHeartRate() {
        return aveHeartRate;
    }

    public MutableLiveData<Number> getLowHeartRate() {
        return lowHeartRate;
    }

    public MutableLiveData<Number> getHighOxy() {
        return highOxy;
    }

    public MutableLiveData<Number> getAveOxy() {
        return aveOxy;
    }

    public MutableLiveData<Number> getLowOxy() {
        return lowOxy;
    }

    public MutableLiveData<Number> getLatestOxy() {
        return latestOxy;
    }

    public MutableLiveData<List<KChartData>> getHeartRateDatas() {
        return heartRateDatas;
    }

    public MutableLiveData<List<KChartData>> getOxyDatas() {
        return oxyDatas;
    }

    public void getMonthData(ImibabyApp mApp, String date,String seid) {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        String[] days = getMonthFirstEndDate(date);
                        if (days != null) {
                            String url = ReportActivity.RECORDS_URL;
                            String eid = seid;
                            JSONObject pl = new JSONObject();
                            pl.put("eid", eid);
                            pl.put("startDate", days[0]);
                            pl.put("endDate", days[1]);
                            HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(url, pl.toJSONString());
                            if (response.isSuccess()) {
                                e.onNext(response.data);
                            } else {
                                e.onError(new Throwable("getDayDatas failed."));
                            }
                            e.onComplete();
                        } else {
                            e.onError(new Throwable("getDayDatas days null."));
                        }
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        paresHealthJson(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e("getDayData complete.");
                    }
                });
    }

    private String[] getMonthFirstEndDate(String date) {
        String[] ret = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int first = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, first);
            String firstDay = format.format(cal.getTime());
            int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, last);
            String lastday = format.format(cal.getTime());
            ret[0] = firstDay;
            ret[1] = lastday;
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void paresHealthJson(String data) {
        JSONObject pl = (JSONObject) JSONValue.parse(data);
        int code = (int) pl.get("code");
        if (code == 1) {
            List<DayChartData> stepsList = new ArrayList<>();
            List<KChartData> heartRateList = new ArrayList<>();

            //step
            JSONObject steps = (JSONObject) pl.get("steps");
            if (steps.size() != 0) {
//            String time = (String) steps.get("time");
                String tSteps = (String) steps.get("totalsteps");
                String tlCal = (String) steps.get("totalkilocalorie");
                String tDis = (String) steps.get("totalkilometer");
                String aSteps = (String) steps.get("avg");
                JSONArray array = (JSONArray) steps.get("data");
                int size = array != null ? array.size() : 0;
                for (int i = 0; i < size; i++) {
                    String item = (String) array.get(i);
                    String[] split = item.split(",");
                    String t = split[0];
                    int v = Integer.parseInt(split[1]);
                    DayChartData itemData = new DayChartData(t, v);
                    stepsList.add(itemData);
                }
                totalSteps.postValue(tSteps);
                totalCal.postValue(tlCal);
                totalDistance.postValue(tDis);
                aveSteps.postValue(aSteps);
                stepsDatas.postValue(stepsList);
            } else {
                totalSteps.postValue(null);
                totalCal.postValue(null);
                totalDistance.postValue(null);
                aveSteps.postValue(null);
                stepsDatas.postValue(null);
            }

            //heartrate
            JSONObject hpl = (JSONObject) pl.get("heartRate");
            if (hpl.size() != 0) {
                Number hmax = (Number) hpl.get("max");
                Number hmin = (Number) hpl.get("min");
                Number have = (Number) hpl.get("avg");
                Number hla = (Number) hpl.get("last");
                JSONArray array1 = (JSONArray) hpl.get("data");
                List<DayChartData> dayHRlist = new ArrayList<>();
                for (int i = 0; i < array1.size(); i++) {
                    JSONObject obj = (JSONObject) array1.get(i);
                    String t = (String) obj.get("dataTime");
                    String v = (String) obj.get("heartRate");
                    DayChartData hdata = new DayChartData(t, Integer.valueOf(v));
                    dayHRlist.add(hdata);
                }
                latestHeartRate.postValue(hla);
                highHeartRate.postValue(hmax);
                lowHeartRate.postValue(hmin);
                aveHeartRate.postValue(have);
                heartRateDatas.postValue(getHeartRateKchartData(dayHRlist));
            } else {
                latestHeartRate.postValue(null);
                highHeartRate.postValue(null);
                lowHeartRate.postValue(null);
                aveHeartRate.postValue(null);
                heartRateDatas.postValue(null);
            }

            //oxy
            JSONObject opl = (JSONObject) pl.get("oxygen");
            if (opl.size() != 0) {
                Number lastOxy = (Number) opl.get("last");
                Number max = (Number) opl.get("max");
                Number min = (Number) opl.get("min");
                Number avg = (Number) opl.get("avg");
                JSONArray array2 = (JSONArray) opl.get("data");
                List<DayOxyData> dayOxylist = new ArrayList<>();
                for (int i = 0; i < array2.size(); i++) {
                    JSONObject obj = (JSONObject) array2.get(i);
                    String t = (String) obj.get("dataTime");
                    String v = (String) obj.get("oxygen");
                    DayOxyData odata = new DayOxyData(t, Integer.parseInt(v));
                    dayOxylist.add(odata);
                }
                latestOxy.postValue(lastOxy);
                highOxy.postValue(max);
                lowOxy.postValue(min);
                aveOxy.postValue(avg);
                oxyDatas.postValue(getOxyKchartData(dayOxylist));
            } else {
                latestOxy.postValue(null);
                highOxy.postValue(null);
                lowOxy.postValue(null);
                aveOxy.postValue(null);
                oxyDatas.postValue(null);
            }

        } else {
            LogUtil.e("HealthDayData code = " + code);
        }
    }

    private List<KChartData> getHeartRateKchartData(List<DayChartData> datas) {
        ArrayList<KChartData> ret = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            int v = datas.get(i).getValue();
            String timeStamp = datas.get(i).getTimeStamp();
            Date d = TimeUtil.unixTimeToDate(timeStamp);
            KChartData kChartData = findTheDateInList(d, ret);
            if (kChartData != null) {
                int high = kChartData.getHigh();
                int low = kChartData.getLow();
                if (v > high) {
                    kChartData.setHigh(v);
                }
                if (v < low) {
                    kChartData.setLow(v);
                }
            } else {
                String t1 = TimeUtil.getDayTimeStrFromDate(d);
                KChartData kChartData1 = new KChartData(t1, v, v);
                ret.add(kChartData1);
            }
        }
        return ret;
    }

    private List<KChartData> getOxyKchartData(List<DayOxyData> datas) {
        ArrayList<KChartData> ret = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            int v = datas.get(i).getValue();
            String timeStamp = datas.get(i).getTimeStamp();
            Date d = TimeUtil.unixTimeToDate(timeStamp);
            KChartData kChartData = findTheDateInList(d, ret);
            if (kChartData != null) {
                int high = kChartData.getHigh();
                int low = kChartData.getLow();
                if (v > high) {
                    kChartData.setHigh(v);
                }
                if (v < low) {
                    kChartData.setLow(v);
                }
            } else {
                String t1 = TimeUtil.getDayTimeStrFromDate(d);
                KChartData kChartData1 = new KChartData(t1, v, v);
                ret.add(kChartData1);
            }
        }
        return ret;
    }

    private KChartData findTheDateInList(Date d, List<KChartData> list) {
        KChartData ret = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        for (int i = 0; i < list.size(); i++) {
            String timeStamp = list.get(i).getTimeStamp();
            try {
                Date d1 = format.parse(timeStamp);
                if (TimeUtil.isTheSameDay(d, d1)) {
                    ret = list.get(i);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
        }
        return ret;
    }

    private List<FatigueSightBean> getSightBeanData(List<DayChartData> datas) {
        ArrayList<FatigueSightBean> ret = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            int v = datas.get(i).getValue();
            String timeStamp = datas.get(i).getTimeStamp();
            Date d = TimeUtil.unixTimeToDate(timeStamp);
            FatigueSightBean item = findSightBean(d, ret);
            if (item != null) {
                if (v == 0) {
                    item.getNormal().addDuration(DURATION_VALUE);
                } else if (v == 1) {
                    item.getTiny().addDuration(DURATION_VALUE);
                } else if (v == 2) {
                    item.getAbit().addDuration(DURATION_VALUE);
                } else {
                    item.getObvious().addDuration(DURATION_VALUE);
                }
            } else {
                FatigueSightBean newItem = new FatigueSightBean(TimeUtil.getDayTimeStrFromDate(d));
                if (v == 0) {
                    newItem.getNormal().addDuration(DURATION_VALUE);
                } else if (v == 1) {
                    newItem.getTiny().addDuration(DURATION_VALUE);
                } else if (v == 2) {
                    newItem.getAbit().addDuration(DURATION_VALUE);
                } else {
                    newItem.getObvious().addDuration(DURATION_VALUE);
                }
                ret.add(newItem);
            }
        }
        return ret;
    }

    private FatigueSightBean findSightBean(Date d, List<FatigueSightBean> list) {
        FatigueSightBean ret = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        for (int i = 0; i < list.size(); i++) {
            String timeStamp = list.get(i).getDate();
            try {
                Date d1 = format.parse(timeStamp);
                if (TimeUtil.isTheSameDay(d, d1)) {
                    ret = list.get(i);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
        }
        return ret;
    }

    private List<FatigueBrainBean> getBrainBeanData(List<DayChartData> datas) {
        ArrayList<FatigueBrainBean> ret = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            int v = datas.get(i).getValue();
            String timeStamp = datas.get(i).getTimeStamp();
            Date d = TimeUtil.unixTimeToDate(timeStamp);
            FatigueBrainBean item = findBrainBean(d, ret);
            if (item != null) {
                if (v == 0) {
                    item.getNormal().addDuration(DURATION_VALUE);
                } else if (v == 1) {
                    item.getLight().addDuration(DURATION_VALUE);
                } else if (v == 2) {
                    item.getMiddle().addDuration(DURATION_VALUE);
                } else {
                    item.getSevere().addDuration(DURATION_VALUE);
                }
            } else {
                FatigueBrainBean newItem = new FatigueBrainBean(TimeUtil.getDayTimeStrFromDate(d));
                if (v == 0) {
                    newItem.getNormal().addDuration(DURATION_VALUE);
                } else if (v == 1) {
                    newItem.getLight().addDuration(DURATION_VALUE);
                } else if (v == 2) {
                    newItem.getMiddle().addDuration(DURATION_VALUE);
                } else {
                    newItem.getSevere().addDuration(DURATION_VALUE);
                }
                ret.add(newItem);
            }
        }
        return ret;
    }

    private FatigueBrainBean findBrainBean(Date d, List<FatigueBrainBean> list) {
        FatigueBrainBean ret = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        for (int i = 0; i < list.size(); i++) {
            String timeStamp = list.get(i).getDate();
            try {
                Date d1 = format.parse(timeStamp);
                if (TimeUtil.isTheSameDay(d, d1)) {
                    ret = list.get(i);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
        }
        return ret;
    }

    public int getOxyStatusLvl(int v) {
        if (v >= 90) {
            return 0;
        } else if (v >= 80) {
            return 1;
        } else if (v >= 70) {
            return 2;
        } else {
            return 3;
        }
    }
}
