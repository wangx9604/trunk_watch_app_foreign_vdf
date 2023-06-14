package com.xiaoxun.xun.health.outside.fragments.week;

import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.health.outside.OutSideMainActivity;
import com.xiaoxun.xun.health.outside.bean.OutSideChartWeekBean;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.HttpWorks;
import com.xiaoxun.xun.utils.LogUtil;

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
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OutsideWeekModel extends ViewModel {

    private String selectDate;

    MutableLiveData<List<OutSideChartWeekBean>> chartDatas;

    MutableLiveData<String> selectOutsideTime;
    MutableLiveData<Integer> selectOutsideDura;
    MutableLiveData<Integer> selectOutsideLvl;
    MutableLiveData<Integer> selectOutsideLvlDura;

    MutableLiveData<Integer> aveDura;
    MutableLiveData<String> roundLvl;

    public OutsideWeekModel(){

        List<OutSideChartWeekBean> list = new ArrayList<>();
        chartDatas = new MutableLiveData<>();
        chartDatas.setValue(list);
        selectOutsideTime = new MutableLiveData<>();
        selectOutsideDura = new MutableLiveData<>();
        selectOutsideLvl = new MutableLiveData<>();
        selectOutsideLvlDura = new MutableLiveData<>();

        aveDura = new MutableLiveData<>();
        roundLvl = new MutableLiveData<>();
    }

    public void setSelectOutsideTime(String time){
        selectOutsideTime.postValue(time);
    }
    public MutableLiveData<String> getSelectOutsideTime() {
        return selectOutsideTime;
    }

    public void setSelectOutsideDura(int dura){
        selectOutsideDura.postValue(dura);
    }

    public MutableLiveData<Integer> getSelectOutsideDura() {
        return selectOutsideDura;
    }

    public void setSelectOutsideLvl(int lvl){
        selectOutsideLvl.postValue(lvl);
    }

    public MutableLiveData<Integer> getSelectOutsideLvl() {
        return selectOutsideLvl;
    }

    public void setSelectOutsideLvlDura(int dura){
        selectOutsideLvlDura.postValue(dura);
    }
    public MutableLiveData<Integer> getSelectOutsideLvlDura() {
        return selectOutsideLvlDura;
    }

    public MutableLiveData<String> getRoundLvl() {
        return roundLvl;
    }

    public MutableLiveData<Integer> getAveDura() {
        return aveDura;
    }

    public MutableLiveData<List<OutSideChartWeekBean>> getChartDatas() {
        return chartDatas;
    }

    public void getWeekData(ImibabyApp mApp, String date,String seid){
        selectDate = date;
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        String url = OutSideMainActivity.OUTSIDE_URL;
                        String[] monSun = getMonAndSunbyReserveDate(date);
                        String eid = seid;
                        String type = "week";
                        String begin = monSun[0];
                        String end = monSun[1];
                        JSONObject pl = new JSONObject();
                        pl.put("beginTime",Long.valueOf(begin));
                        pl.put("endTime",Long.valueOf(end));
                        pl.put("type",type);
                        pl.put("eid",eid);
                        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP)
                                + mApp.getToken();
                        HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(url,reqBody);
                        if(response.isSuccess()){
                            e.onNext(response.data);
                        }else{
                            e.onError(new Throwable("getWeekData fail"));
                        }
                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        byte[] decBase64 = Base64.decode(s, Base64.NO_WRAP);
                        byte[] decMessage = AESUtil.decryptAESCBC(decBase64, mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY);
                        if(decMessage != null && decMessage.length != 0){
                            String decMesString = new String(decMessage);
                            LogUtil.e("getWeekData resp json : " + decMesString);
                            JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                            Integer code = (Integer) resp.get("code");
                            if(code != null && code == 0){
                                JSONObject data = (JSONObject) resp.get("data");
                                if(data != null && !data.isEmpty()){
                                    paresData(mApp,data);
                                }else{
                                    LogUtil.e("getWeekData fail data err");
                                }
                            }else{
                                LogUtil.e("getWeekData fail code err");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("getWeekData onError : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void paresData(ImibabyApp mApp,JSONObject data){
        Integer avgActivityDuration = (Integer) data.get("avgActivityDuration");
        if(avgActivityDuration != null){
            aveDura.postValue(avgActivityDuration);
        }
        String UVIntensityRange = (String) data.get("uvIntensityRange");
        if(UVIntensityRange != null){
            roundLvl.postValue(UVIntensityRange);
        }
        JSONArray array = (JSONArray) data.get("list");
        List<OutSideChartWeekBean> list = new ArrayList<>();
        if(array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = (JSONObject) array.get(i);
                OutSideChartWeekBean bean = new OutSideChartWeekBean();
                Integer dayOfWeek = (Integer) item.get("dayOfWeek");
                bean.setWeekDay(dayOfWeek);
                JSONObject levelAndDuration = (JSONObject) item.get("levelAndDuration");
                if(levelAndDuration.containsKey("1")){
                    Integer lvl1 = (Integer) levelAndDuration.get("1");
                    bean.addLvl1_dura(lvl1);
                }
                if(levelAndDuration.containsKey("2")){
                    Integer lvl2 = (Integer) levelAndDuration.get("2");
                    bean.addLvl2_dura(lvl2);
                }
                if(levelAndDuration.containsKey("3")){
                    Integer lvl3 = (Integer) levelAndDuration.get("3");
                    bean.addLvl3_dura(lvl3);
                }
                if(levelAndDuration.containsKey("4")){
                    Integer lvl4 = (Integer) levelAndDuration.get("4");
                    bean.addLvl4_dura(lvl4);
                }
                if(levelAndDuration.containsKey("5")){
                    Integer lvl5 = (Integer) levelAndDuration.get("5");
                    bean.addLvl5_dura(lvl5);
                }
                if(item.containsKey("activityDuration")){
                    Integer dayOutSideDura = (Integer) item.get("activityDuration");
                    bean.setOutside_week_all_dura(dayOutSideDura);
                }
                list.add(bean);
            }
            setDefaultSelectData(mApp,list);
            if(list.size() < 7) {
                for (int i = 1; i < 8; i++) {
                    boolean hasDay = false;
                    for (int j = 0; j < list.size(); j++) {
                        OutSideChartWeekBean outSideChartBean = list.get(j);
                        if(outSideChartBean.getWeekDay() == i){
                            hasDay = true;
                            break;
                        }
                    }
                    if(!hasDay){
                        OutSideChartWeekBean newBean = new OutSideChartWeekBean();
                        newBean.setWeekDay(i);
                        list.add(i-1,newBean);
                    }
                }
            }
            chartDatas.postValue(list);
        }
    }
    public String[] getMonAndSunbyReserveDate(String time) {
        String[] ret = new String[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date t = null;
        try {
            t = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(t);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        ret[0] = dateFormat.format(cal.getTime());//monday
        cal.add(Calendar.DAY_OF_MONTH, 6);
        ret[1] = dateFormat.format(cal.getTime());//sunday
        return ret;
    }

    public String[] getAllWeekDaysByDate(String time) {
        String[] ret = new String[7];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date t = null;
        try {
            t = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(t);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        for(int i=0;i<7;i++){
            ret[i] = dateFormat.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            LogUtil.e("getAllWeekDaysByDate " + ret[i]);
        }
        return ret;
    }

    public void clearDatas(){
        chartDatas.postValue(null);
        selectOutsideTime.postValue(null);
        selectOutsideDura.postValue(null);
        selectOutsideLvl.postValue(null);
        selectOutsideLvlDura.postValue(null);
        aveDura.postValue(null);
        roundLvl.postValue(null);
    }

    public String getTimeFromChartSelect(ImibabyApp mApp, int index,String curDate){
        String[] allDays = getAllWeekDaysByDate(curDate);
        String curday = allDays[index];
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format.parse(curday);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int mon = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            return mon + mApp.getString(R.string.month) + day + mApp.getString(R.string.day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "--";
    }

    private void setDefaultSelectData(ImibabyApp mApp,List<OutSideChartWeekBean> list){
        if(list != null) {
            int size = list.size();
            OutSideChartWeekBean bean = list.get(size-1);
            if(bean.getLvl5_dura() > 0){
                getSelectOutsideLvl().postValue(5);
                getSelectOutsideLvlDura().postValue(bean.getLvl5_dura());
            }else if(bean.getLvl4_dura() > 0){
                getSelectOutsideLvl().postValue(4);
                getSelectOutsideLvlDura().postValue(bean.getLvl4_dura());
            }else if(bean.getLvl3_dura() > 0){
                getSelectOutsideLvl().postValue(3);
                getSelectOutsideLvlDura().postValue(bean.getLvl3_dura());
            }else if(bean.getLvl2_dura() > 0){
                getSelectOutsideLvl().postValue(2);
                getSelectOutsideLvlDura().postValue(bean.getLvl2_dura());
            }else if(bean.getLvl1_dura() > 0){
                getSelectOutsideLvl().postValue(1);
                getSelectOutsideLvlDura().postValue(bean.getLvl1_dura());
            }
            getSelectOutsideTime().postValue(getTimeFromChartSelect(mApp,bean.getWeekDay(),selectDate));
            getSelectOutsideDura().postValue(bean.getOutside_week_all_dura());
        }
    }

    public void testData(ImibabyApp mApp,String curDate){
        Random ran = new Random();
        List<OutSideChartWeekBean> list = new ArrayList<>();
        for(int i=1;i<8;i++){
            OutSideChartWeekBean bean = new OutSideChartWeekBean();
            bean.setWeekDay(i);
            bean.addLvl1_dura(ran.nextInt(200 * 60));
            bean.addLvl2_dura(ran.nextInt(100 * 60));
            bean.addLvl3_dura(ran.nextInt(40 * 60));
            bean.addLvl4_dura(ran.nextInt(40 * 60));
            bean.addLvl5_dura(ran.nextInt(10 * 60));
            list.add(bean);
        }
        chartDatas.postValue(list);
    }
}
