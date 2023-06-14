package com.xiaoxun.xun.health.outside.fragments.day;

import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.health.outside.OutSideMainActivity;
import com.xiaoxun.xun.health.outside.bean.OutSideBean;
import com.xiaoxun.xun.health.outside.bean.OutSideChartDayBean;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OutsideDayModel extends ViewModel {
    List<OutSideBean> datas;

    MutableLiveData<List<OutSideChartDayBean>> chartDatas;

    MutableLiveData<String> selectOutsideTime;
    MutableLiveData<Integer> selectOutsideDura;
    MutableLiveData<Integer> selectOutsideLvl;
    MutableLiveData<Integer> selectOutsideLvlDura;

    MutableLiveData<Integer> allDura;
    MutableLiveData<Integer> highestLvl;
    MutableLiveData<OutSidePieChartBean> pieChartData;
    MutableLiveData<Integer> lvl1Dura;
    MutableLiveData<Integer> lvl2Dura;
    MutableLiveData<Integer> lvl3Dura;
    MutableLiveData<Integer> lvl4Dura;
    MutableLiveData<Integer> lvl5Dura;

    public OutsideDayModel(){
        datas = new ArrayList<>();

        selectOutsideTime = new MutableLiveData<>();
        selectOutsideDura = new MutableLiveData<>();
        selectOutsideLvl = new MutableLiveData<>();
        selectOutsideLvlDura = new MutableLiveData<>();

        List<OutSideChartDayBean> list = new ArrayList<>();
        chartDatas = new MutableLiveData<>();
        chartDatas.setValue(list);
        allDura = new MutableLiveData<>();
        highestLvl = new MutableLiveData<>();
        pieChartData = new MutableLiveData<>();
        lvl1Dura = new MutableLiveData<>();
        lvl2Dura = new MutableLiveData<>();
        lvl3Dura = new MutableLiveData<>();
        lvl4Dura = new MutableLiveData<>();
        lvl5Dura = new MutableLiveData<>();
    }

    public List<OutSideBean> getDatas() {
        return datas;
    }

    public void setSelectOutsideTime(String time) {
        this.selectOutsideTime.postValue(time);
    }

    public MutableLiveData<String> getSelectOutsideTime() {
        return selectOutsideTime;
    }

    public void setSelectOutsideDura(int dura) {
        this.selectOutsideDura.postValue(dura);
    }

    public MutableLiveData<Integer> getSelectOutsideDura() {
        return selectOutsideDura;
    }

    public void setSelectOutsideLvl(int lvl) {
        this.selectOutsideLvl.postValue(lvl);
    }

    public MutableLiveData<Integer> getSelectOutsideLvl() {
        return selectOutsideLvl;
    }

    public void setSelectOutsideLvlDura(int dura) {
        this.selectOutsideLvlDura.postValue(dura);
    }

    public MutableLiveData<Integer> getSelectOutsideLvlDura() {
        return selectOutsideLvlDura;
    }

    public MutableLiveData<List<OutSideChartDayBean>> getCharDatas() {
        return chartDatas;
    }

    public MutableLiveData<Integer> getAllDura() {
        return allDura;
    }

    public MutableLiveData<Integer> getHighestLvl() {
        return highestLvl;
    }

    public MutableLiveData<OutSidePieChartBean> getPieChartData() {
        return pieChartData;
    }

    public MutableLiveData<Integer> getLvl1Dura() {
        return lvl1Dura;
    }

    public MutableLiveData<Integer> getLvl2Dura() {
        return lvl2Dura;
    }

    public MutableLiveData<Integer> getLvl3Dura() {
        return lvl3Dura;
    }

    public MutableLiveData<Integer> getLvl4Dura() {
        return lvl4Dura;
    }

    public MutableLiveData<Integer> getLvl5Dura() {
        return lvl5Dura;
    }

    public static class OutSidePieChartBean{
        int lvl1;
        int lvl2;
        int lvl3;
        int lvl4;
        int lvl5;
        public OutSidePieChartBean(int l1,int l2,int l3,int l4,int l5){
            lvl1 = l1;
            lvl2 = l2;
            lvl3 = l3;
            lvl4 = l4;
            lvl5 = l5;
        }
    }

    public void sendUploadMsg(ImibabyApp mApp,String today,String seid){
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    getDayData(mApp,today,seid);
                }
            }
        });
        JSONObject pl = new JSONObject();
        String [] eid = new String[1];
        eid[0] = seid;
        String timestamp = TimeUtil.getTimeStampGMT();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,CloudBridgeUtil.SUB_ACTION_HEALTH_OUTSIDE_UPLOAD);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid[0]);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timestamp);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, mApp.getToken(), null, eid, pl));

        if(mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(e2e);
        }
    }

    public void getDayData(ImibabyApp mApp,String date,String seid){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String url = OutSideMainActivity.OUTSIDE_URL;
                String eid = seid;
                String type = "day";
                JSONObject pl = new JSONObject();
                pl.put("beginTime",Long.parseLong(date));
                pl.put("endTime",Long.parseLong(date));
                pl.put("type",type);
                pl.put("eid",eid);
                LogUtil.e("getDayData post json : " + pl.toJSONString());
                String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP)
                        + mApp.getToken();
                HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(url,reqBody);
                if(response.isSuccess()){
                    e.onNext(response.data);
                }else{
                    e.onError(new Throwable("getDayData fail"));
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
                    LogUtil.e("getDayData resp json : " + decMesString);
                    JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                    Integer code = (Integer) resp.get("code");
                    if(code != null && code == 0){
                        JSONObject data = (JSONObject) resp.get("data");
                        if(data != null && !data.isEmpty()){
                            paresData(data);
                        }else{
                            LogUtil.e("getDayData fail data err");
                        }
                    }else{
                        LogUtil.e("getDayData fail code err");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e("getDayData onError : " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void paresData(JSONObject data){
        Integer ActivityDuration = (Integer) data.get("activityDuration");
        if(ActivityDuration != null){
            allDura.postValue(ActivityDuration / 60);
        }
        Integer maximumUVIntensity = (Integer) data.get("maximumUVIntensity");
        if(maximumUVIntensity != null){
            highestLvl.postValue(maximumUVIntensity);
        }
        JSONArray array = (JSONArray) data.get("list");
        if(array != null) {
            ArrayList<OutSideChartDayBean> values = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = (JSONObject) array.get(i);
                LogUtil.e("paresData array item : " + item.toJSONString());
                OutSideChartDayBean chartBean = new OutSideChartDayBean();
                Integer hour = (Integer) item.get("hour");
                chartBean.setHour(hour);
                JSONObject levelAndDuration = (JSONObject) item.get("levelAndDuration");
                if(levelAndDuration.containsKey("1")){
                    Integer lvl1 = (Integer) levelAndDuration.get("1");
                    chartBean.addLvl1_dura(lvl1);
                }
                if(levelAndDuration.containsKey("2")){
                    Integer lvl2 = (Integer) levelAndDuration.get("2");
                    chartBean.addLvl2_dura(lvl2);
                }
                if(levelAndDuration.containsKey("3")){
                    Integer lvl3 = (Integer) levelAndDuration.get("3");
                    chartBean.addLvl3_dura(lvl3);
                }
                if(levelAndDuration.containsKey("4")){
                    Integer lvl4 = (Integer) levelAndDuration.get("4");
                    chartBean.addLvl4_dura(lvl4);
                }
                if(levelAndDuration.containsKey("5")){
                    Integer lvl5 = (Integer) levelAndDuration.get("5");
                    chartBean.addLvl5_dura(lvl5);
                }
                if(item.containsKey("activityDuration")){
                    Integer dayOutSideDura = (Integer) item.get("activityDuration");
                    chartBean.setOutside_day_all_dura(dayOutSideDura);
                }
                values.add(chartBean);
            }
            setDefaultSelectData(values);
            chartDatas.postValue(values);
        }
        JSONObject proportionOfOutdoorActivity = (JSONObject) data.get("proportionOfOutdoorActivity");
        if(proportionOfOutdoorActivity != null && !proportionOfOutdoorActivity.isEmpty()) {
            Integer lvl1p = (Integer) proportionOfOutdoorActivity.get("1");
            Integer lvl2p = (Integer) proportionOfOutdoorActivity.get("2");
            Integer lvl3p = (Integer) proportionOfOutdoorActivity.get("3");
            Integer lvl4p = (Integer) proportionOfOutdoorActivity.get("4");
            Integer lvl5p = (Integer) proportionOfOutdoorActivity.get("5");
            lvl1Dura.postValue(lvl1p != null ? lvl1p : 0);
            lvl2Dura.postValue(lvl2p != null ? lvl2p : 0);
            lvl3Dura.postValue(lvl3p != null ? lvl3p : 0);
            lvl4Dura.postValue(lvl4p != null ? lvl4p : 0);
            lvl5Dura.postValue(lvl5p != null ? lvl5p : 0);

            OutSidePieChartBean pieChartBean = new OutSidePieChartBean(lvl1p != null ? lvl1p : 0,lvl2p != null ? lvl2p : 0,lvl3p != null ? lvl3p : 0,lvl4p != null ? lvl4p : 0,lvl5p != null ? lvl5p : 0);
            pieChartData.postValue(pieChartBean);
        }
    }

    private int getHourFromTimeStamp(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date date = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.HOUR_OF_DAY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private OutSideChartDayBean getSameTimeInChartList(String time,List<OutSideChartDayBean> list){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date date = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            for(int i = 0;i < list.size();i++){
                OutSideChartDayBean bean = list.get(i);
                if(bean.getHour() == hour){
                    return bean;
                }
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearDatas(){
        datas.clear();
        chartDatas.postValue(null);
        selectOutsideTime.postValue(null);
        selectOutsideDura.postValue(null);
        selectOutsideLvl.postValue(null);
        selectOutsideLvlDura.postValue(null);
        allDura.postValue(null);
        highestLvl.postValue(null);
        lvl1Dura.postValue(null);
        lvl2Dura.postValue(null);
        lvl3Dura.postValue(null);
        lvl4Dura.postValue(null);
        lvl5Dura.postValue(null);
    }

    public String getTimeFromChartSelect(int h){
        if(h == 0){
            return "0:00-1:00";
        }else if(h == 1){
            return "1:00-2:00";
        }else if(h == 2){
            return "2:00-3:00";
        }else if(h == 3){
            return "3:00-4:00";
        }else if(h == 4){
            return "4:00-5:00";
        }else if(h == 5){
            return "5:00-6:00";
        }else if(h == 6){
            return "6:00-7:00";
        }else if(h == 7){
            return "7:00-8:00";
        }else if(h == 8){
            return "8:00-9:00";
        }else if(h == 9){
            return "9:00-10:00";
        }else if(h == 10){
            return "10:00-11:00";
        }else if(h == 11){
            return "11:00-12:00";
        }else if(h == 12){
            return "12:00-13:00";
        }else if(h == 13){
            return "13:00-14:00";
        }else if(h == 14){
            return "14:00-15:00";
        }else if(h == 15){
            return "15:00-16:00";
        }else if(h == 16){
            return "16:00-17:00";
        }else if(h == 17){
            return "17:00-18:00";
        }else if(h == 18){
            return "18:00-19:00";
        }else if(h == 19){
            return "19:00-20:00";
        }else if(h == 20){
            return "20:00-21:00";
        }else if(h == 21){
            return "21:00-22:00";
        }else if(h == 22){
            return "22:00-23:00";
        }else if(h == 23){
            return "23:00-24:00";
        }else{
            return "";
        }
    }

    private void setDefaultSelectData(List<OutSideChartDayBean> list){
        if(list != null) {
            int index = -1;
            for(int i=list.size()-1;i>0;i--){
                OutSideChartDayBean temp = list.get(i);
                if(temp.getAllDuration() != 0){
                    index = i;
                    break;
                }
            }
            if(index == -1){
                //均无紫外线数据，
                return;
            }
            OutSideChartDayBean bean = list.get(index);
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
            int hour = bean.getHour();
            getSelectOutsideTime().postValue(getTimeFromChartSelect(hour));
            getSelectOutsideDura().postValue(bean.getOutside_day_all_dura());
        }
    }

    public void testData(){
        chartDatas.postValue(null);
        List<OutSideChartDayBean> list = new ArrayList<>();
        for(int i=7;i<=9;i++){
            OutSideChartDayBean bean = new OutSideChartDayBean();
            bean.setHour(i);
            bean.addLvl1_dura((int)(Math.random() * 1200));
            bean.addLvl2_dura((int)(Math.random() * 1200));
            bean.addLvl3_dura(600);
            bean.addLvl4_dura(300);
            bean.addLvl5_dura(300);
            list.add(bean);
        }
        LogUtil.e("test data : " +list.size());
        chartDatas.postValue(list);
        selectOutsideTime.postValue(getTimeFromChartSelect(list.get(0).getHour()));
        selectOutsideDura.postValue(list.get(0).getAllDuration());
        selectOutsideLvl.postValue(1);
        selectOutsideLvlDura.postValue(list.get(0).getLvl1_dura());

        OutSidePieChartBean pieChartBean = new OutSidePieChartBean(50,20,20,5,5);
        pieChartData.postValue(pieChartBean);
        lvl1Dura.postValue(50);
        lvl2Dura.postValue(20);
        lvl3Dura.postValue(20);
        lvl4Dura.postValue(5);
        lvl5Dura.postValue(5);
    }
}
