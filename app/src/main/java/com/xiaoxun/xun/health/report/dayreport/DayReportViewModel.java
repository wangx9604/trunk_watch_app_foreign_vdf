package com.xiaoxun.xun.health.report.dayreport;

import static com.xiaoxun.xun.health.bean.FatigueData.DURATION_VALUE;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.health.bean.FatigueBrainBean;
import com.xiaoxun.xun.health.bean.FatigueData;
import com.xiaoxun.xun.health.bean.FatigueSightBean;
import com.xiaoxun.xun.health.report.ReportActivity;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayChartData;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayOxyData;
import com.xiaoxun.xun.utils.HttpWorks;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DayReportViewModel extends ViewModel {
    //step
    private MutableLiveData<String> targetSteps;
    private MutableLiveData<String> daySteps;
    private MutableLiveData<List<DayChartData>> stepChartDatas;
    private MutableLiveData<String> dayCal;
    private MutableLiveData<String> dayDistance;
    private MutableLiveData<String> daySedentary;

    //heartrate
    private MutableLiveData<Number> latestHeartRate;
    private MutableLiveData<List<DayChartData>> heartRateChartDatas;
    private MutableLiveData<Number> heartRateMax;
    private MutableLiveData<Number> heartRateAve;
    private MutableLiveData<Number> heartRateMin;

    //oxy
    private MutableLiveData<Number> latestOxy;
    private MutableLiveData<List<DayOxyData>> oxyChartDatas;

    private Comparator<DayChartData> comparator = new Comparator<DayChartData>() {
        @Override
        public int compare(DayChartData dayChartData, DayChartData t1) {
            long s1 = Long.parseLong(dayChartData.getTimeStamp());
            long s2 = Long.parseLong(t1.getTimeStamp());
            if(s1 > s2){
                return 1;
            }else if(s1 < s2){
                return -1;
            }
            return 0;
        }
    };

    public DayReportViewModel(){
        targetSteps = new MutableLiveData<>();
        daySteps = new MutableLiveData<>();
        dayCal = new MutableLiveData<>();
        dayDistance = new MutableLiveData<>();
        daySedentary = new MutableLiveData<>();
        List<DayChartData> steplist = new ArrayList<>();
        stepChartDatas = new MutableLiveData<>();
        stepChartDatas.setValue(steplist);

        latestHeartRate = new MutableLiveData<>();
        heartRateMax = new MutableLiveData<>();
        heartRateAve = new MutableLiveData<>();
        heartRateMin = new MutableLiveData<>();
        List<DayChartData> heartlist = new ArrayList<>();
        heartRateChartDatas = new MutableLiveData<>();
        heartRateChartDatas.setValue(heartlist);

        latestOxy = new MutableLiveData<>();
        List<DayOxyData> oxylist = new ArrayList<>();
        oxyChartDatas = new MutableLiveData<>();
        oxyChartDatas.setValue(oxylist);

    }

    public MutableLiveData<String> getTargetSteps() {
        return targetSteps;
    }

    public MutableLiveData<String> getDaySteps() {
        return daySteps;
    }

    public MutableLiveData<String> getDayCal() {
        return dayCal;
    }

    public MutableLiveData<String> getDayDistance() {
        return dayDistance;
    }

    public MutableLiveData<String> getDaySedentary() {
        return daySedentary;
    }

    public MutableLiveData<List<DayChartData>> getStepChartDatas() {
        return stepChartDatas;
    }

    public MutableLiveData<Number> getLatestHeartRate() {
        return latestHeartRate;
    }

    public MutableLiveData<Number> getHeartRateMin() {
        return heartRateMin;
    }

    public MutableLiveData<Number> getHeartRateAve() {
        return heartRateAve;
    }

    public MutableLiveData<Number> getHeartRateMax() {
        return heartRateMax;
    }

    public MutableLiveData<List<DayChartData>> getHeartRateChartDatas() {
        return heartRateChartDatas;
    }

    public MutableLiveData<Number> getLatestOxy() {
        return latestOxy;
    }

    public MutableLiveData<List<DayOxyData>> getOxyChartDatas() {
        return oxyChartDatas;
    }


    public void getDayReportData(ImibabyApp mApp, String date){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String url = ReportActivity.RECORDS_URL;
                String eid = mApp.getCurUser().getFocusWatch().getEid();
                JSONObject pl = new JSONObject();
                pl.put("eid", eid);
                pl.put("startDate", date);
                pl.put("endDate", date);
                HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(url, pl.toJSONString());
                if (response.isSuccess()) {
                    e.onNext(response.data);
                } else {
                    e.onError(new Throwable("getDayDatas failed."));
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                paresHealthJson(mApp,s,date);
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

    private void paresHealthJson(ImibabyApp mApp,String data,String date){
        JSONObject pl = (JSONObject) JSONValue.parse(data);
        int code = (int)pl.get("code");
        if(code == 1){
            List<DayChartData> steplist = new ArrayList<>();
            List<DayChartData> heartList = new ArrayList<>();
            List<DayOxyData> oxyList = new ArrayList<>();
            List<DayChartData> sightList = new ArrayList<>();
            List<DayChartData> brainList = new ArrayList<>();

            //step
            JSONObject steps = (JSONObject) pl.get("steps");
            if(steps.size()!=0) {
                String time = (String) steps.get("time");
                String totalSteps = (String) steps.get("totalsteps");
                String totalCal = (String) steps.get("totalkilocalorie");
                String totalDis = (String) steps.get("totalkilometer");
                String targetStep = (String) steps.get("steps_target");    //缺目标步数
                String totalsitsize = (String) steps.get("totalsitsize");
                JSONArray array = (JSONArray) steps.get("data");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    int value = (int) obj.get("v");
                    String k = (String) obj.get("k");
                    DayChartData step = new DayChartData(k, value);
                    steplist.add(step);
                }
                daySteps.postValue(totalSteps);
                targetSteps.postValue(targetStep == null ? "0" : targetStep);
                dayCal.postValue(totalCal);
                dayDistance.postValue(totalDis);
                stepChartDatas.postValue(steplist);
                daySedentary.postValue(totalsitsize);
            }

            //heartrate
            JSONObject hpl = (JSONObject) pl.get("heartRate");
            if(hpl.size() != 0) {
                Number hmax = (Number) hpl.get("max");
                Number hmin = (Number) hpl.get("min");
                Number have = (Number) hpl.get("avg");
                Number hla = (Number) hpl.get("last");
                JSONArray array1 = (JSONArray) hpl.get("data");
                for (int i = 0; i < array1.size(); i++) {
                    JSONObject obj = (JSONObject) array1.get(i);
                    String t = (String) obj.get("dataTime");
                    String v = (String) obj.get("heartRate");
                    DayChartData hdata = new DayChartData(t, Integer.valueOf(v));
                    heartList.add(hdata);
                }
                Collections.sort(heartList, comparator);
                latestHeartRate.postValue(hla);
                heartRateMax.postValue(hmax);
                heartRateMin.postValue(hmin);
                heartRateAve.postValue(have);
                heartRateChartDatas.postValue(heartList);
            }

            //oxy
            JSONObject opl = (JSONObject) pl.get("oxygen");
            if(opl.size() != 0) {
                JSONArray array2 = (JSONArray) opl.get("data");
                for (int i = 0; i < array2.size(); i++) {
                    JSONObject obj = (JSONObject) array2.get(i);
                    String t = (String) obj.get("dataTime");
                    String v = (String) obj.get("oxygen");
                    DayOxyData odata = new DayOxyData(t, Integer.parseInt(v));
                    oxyList.add(odata);
                }
                latestOxy.postValue(oxyList.get(oxyList.size() - 1).getValue());
                oxyChartDatas.postValue(oxyList);
            }

        }else{
            LogUtil.e("HealthDayData code = " + code);
        }
    }


    private String getHHmmStr(ImibabyApp mApp,int mins){
        int h = mins / 60;
        int m = mins % 60;
        DecimalFormat format = new DecimalFormat("00");
        String hours = format.format(h);
        String minutes = format.format(m);
        return hours + mApp.getString(R.string.unit_hour) + minutes + mApp.getString(R.string.unit_minute);
    }
}
