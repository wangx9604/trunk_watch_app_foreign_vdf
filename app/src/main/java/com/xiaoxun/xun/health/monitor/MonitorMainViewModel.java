package com.xiaoxun.xun.health.monitor;

import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.HttpWorks;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class MonitorMainViewModel extends ViewModel {

    private final static String MAINURL = "https://dcenter.xunkids.com/hcxl/snapshotData";
    private final static String FATIGUE_MONITOR_HF_URL = "https://dcenter.xunkids.com/hcxl/setFatigue";
    private final static String KEY_NAME_HAERTRATE_MONITOR = "heart_monitor_onoff";
    private final static String KEY_NAME_FATIGUE_MONITOR = "fatigue_monitor_onoff";
    private final static String KEY_NAME_FATIGUE_MONITOR_LIMIT = "fatigue_monitor_limit";
    //高频疲劳开关
    private final static String KEY_NAME_FATIGUE_MONITOR_HF = "fatigue_monitor_hf_onoff";
    private final static String KEY_NAME_FATIGUE_MONITOR_HF_TIME = "fatigue_mon_hf_time";
    private final static String KEY_NAME_FATIGUE_MONITOR_HF_TIME_START = "start";
    private final static String KEY_NAME_FATIGUE_MONITOR_HF_TIME_END = "end";

    //常规疲劳开关
    private final static String KEY_NAME_FATIGUE_MONITOR_COM = "fatigue_monitor_com_onoff";

    private final static int FATIGUE_MONITOR_DURATION = 120;    //高频监测持续时间 单位：分钟

    private MutableLiveData<Boolean> hasData;   //是否有数据展示
    private MutableLiveData<Integer> curSteps;  //当前步数
    private MutableLiveData<Integer> stepProgress; //步数完成百分比
    private MutableLiveData<Integer> targetSteps; //目标步数
    private MutableLiveData<String> distance;   //运动距离 /公里
    private MutableLiveData<String> calories;   //热量/千卡
    private MutableLiveData<Integer> heartRate; //心率 次/分
    private MutableLiveData<Integer> oxy;   //血氧百分比
    private MutableLiveData<Integer> sightLevel; //视疲劳
    private MutableLiveData<Integer> brainLevel; //脑疲劳

    private MutableLiveData<Boolean> heartMonitorSwitch;
    private MutableLiveData<Boolean> fatigueMonitorComSwitch;
    private MutableLiveData<Boolean> fatigueMonitorHfSwitch;
    private MutableLiveData<List<MonitorTimeBean>> timeList;
    private MutableLiveData<Boolean> fatigueMonitorSwitch;
    private MutableLiveData<Boolean> hasFatigueAuth;    //是否加入探索

    private MutableLiveData<Integer> outsideDura;

    public MonitorMainViewModel(){
        hasData = new MutableLiveData<>();
        curSteps = new MutableLiveData<>();
        targetSteps = new MutableLiveData<>();
        stepProgress = new MutableLiveData<>();
        distance = new MutableLiveData<>();
        calories = new MutableLiveData<>();
        heartRate = new MutableLiveData<>();
        oxy = new MutableLiveData<>();
        sightLevel = new MutableLiveData<>();
        brainLevel = new MutableLiveData<>();
        heartMonitorSwitch = new MutableLiveData<>();
        fatigueMonitorComSwitch = new MutableLiveData<>();
        fatigueMonitorHfSwitch = new MutableLiveData<>();
        timeList = new MutableLiveData<>();
        timeList.setValue(new ArrayList<>());
        outsideDura = new MutableLiveData<>();
        fatigueMonitorSwitch = new MutableLiveData<>();
        hasFatigueAuth = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getHasData() {
        return hasData;
    }

    public MutableLiveData<Integer> getCurSteps() {
        return curSteps;
    }

    public MutableLiveData<Integer> getTargetSteps() {
        return targetSteps;
    }

    public MutableLiveData<Integer> getStepProgress() {
        return stepProgress;
    }

    public MutableLiveData<String> getDistance() {
        return distance;
    }

    public MutableLiveData<Integer> getHeartRate() {
        return heartRate;
    }

    public MutableLiveData<Integer> getOxy() {
        return oxy;
    }

    public MutableLiveData<String> getCalories() {
        return calories;
    }

    public MutableLiveData<Integer> getSightLevel() {
        return sightLevel;
    }

    public MutableLiveData<Integer> getBrainLevel() {
        return brainLevel;
    }

    public MutableLiveData<Boolean> getHeartMonitorSwitch() {
        return heartMonitorSwitch;
    }

    public MutableLiveData<Boolean> getFatigueMonitorComSwitch() {
        return fatigueMonitorComSwitch;
    }

    public MutableLiveData<Boolean> getFatigueMonitorHfSwitch() {
        return fatigueMonitorHfSwitch;
    }

    public MutableLiveData<List<MonitorTimeBean>> getTimeList() {
        return timeList;
    }

    public MutableLiveData<Integer> getOutsideDura() {
        return outsideDura;
    }

    public MutableLiveData<Boolean> getFatigueMonitorSwitch(){
        return fatigueMonitorSwitch;
    }

    public MutableLiveData<Boolean> getHasFatigueAuth(){
        return hasFatigueAuth;
    }

    public void initData(ImibabyApp mApp){
        getOnoffDataFromLocal(mApp);
        getMainDataFromCloud(mApp.getCurUser().getFocusWatch().getEid());
        mapGetSettings(mApp);
    }

    private void getMainDataFromCloud(String eid){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                JSONObject pl = new JSONObject();
                pl.put("eid",eid);
                HttpWorks.HttpWorksResponse resp = HttpWorks.httpPost(MAINURL,pl.toJSONString());
                if(resp.isSuccess()){
                    e.onNext(resp.data);
                }else{
                    e.onError(new Throwable(resp.erroMsg));
                }
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        parseMainData(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void parseMainData(String data){
        JSONObject pl = (JSONObject) JSONValue.parse(data);
        int code = (int)pl.get("code");
        if(code == 1){
            JSONObject stepPl = (JSONObject) pl.get("steps");
            String target = (String) stepPl.get("target");
            String steps = (String) stepPl.get("steps");
            String totalkilocalorie = (String) stepPl.get("totalkilocalorie");
            String totalkilometer = (String) stepPl.get("totalkilometer");
            String totalsportsize = (String) stepPl.get("totalsportsize");
            String totalsporttime = (String) stepPl.get("totalsporttime");
            int heart = (int) pl.get("heart");
            int oxygen = (int) pl.get("oxygen");
            int visualFatigue = (int) pl.get("visualFatigue");
            int brainFatigue = (int) pl.get("brainFatigue");
            //户外活动
            String outdoortime = (String) stepPl.get("outdoortime");

            hasData.postValue(true);
            if(steps != null && !steps.equals("")) {
                curSteps.postValue(Integer.parseInt(steps));
            }
            if(target != null && !target.equals("")) {
                targetSteps.postValue(Integer.parseInt(target));
                if(!target.equals("0")) {
                    stepProgress.postValue(Integer.parseInt(steps) * 100 / Integer.parseInt(target));
                }
            }
            if(totalkilometer != null && !totalkilometer.equals("")) {
                distance.postValue(totalkilometer);
            }
            if(totalkilocalorie != null && !totalkilocalorie.equals("")) {
                calories.postValue(totalkilocalorie);
            }
            heartRate.postValue(heart);
            oxy.postValue(oxygen);
            sightLevel.postValue(visualFatigue);
            brainLevel.postValue(brainFatigue);
            if(outdoortime != null && !outdoortime.equals("")){
                outsideDura.postValue(Integer.parseInt(outdoortime));
            }
        }else{
            hasData.postValue(false);
        }
    }

    private void mapGetSettings(ImibabyApp mApp){
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == CloudBridgeUtil.RC_SUCCESS){
                    JSONObject pl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                    //heart rate onoff
                    String onoff = (String) pl.get(KEY_NAME_HAERTRATE_MONITOR);
                    if(onoff != null && !onoff.equals("")) {
                        heartMonitorSwitch.postValue(onoff.equals("1"));
                    }
                    //疲劳度总开关
                    String fatigueOnoff = (String) pl.get(KEY_NAME_FATIGUE_MONITOR);
                    if(fatigueOnoff != null && !fatigueOnoff.equals("")){
                        fatigueMonitorSwitch.postValue(fatigueOnoff.equals("1"));
                    }
                    //常规疲劳开关
                    String facomonoff = (String)pl.get(KEY_NAME_FATIGUE_MONITOR_COM);
                    if(facomonoff != null && !facomonoff.equals("")){
                        fatigueMonitorComSwitch.postValue(facomonoff.equals("1"));
                    }
                    //高频疲劳开关
                    WatchData watchData = mApp.getCurUser().getFocusWatch();
                        String fahfonoff = (String) pl.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME);
                        if (fahfonoff != null && !fahfonoff.isEmpty()) {
                            JSONObject timeJson = (JSONObject) JSONValue.parse(fahfonoff);
                            String start = (String) timeJson.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME_START);
                            String end = (String) timeJson.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME_END);
                            fatigueMonitorHfSwitch.postValue(isInFatigueHfDuration(start, end));
                            if(facomonoff.equals("1")){
                                fatigueMonitorComSwitch.postValue(false);
                                mapSetFatigueSettingComMonitorStatus(mApp,0,false);
                            }
                        } else {
                            fatigueMonitorHfSwitch.postValue(false);
                            if(facomonoff.equals("0")){
                                fatigueMonitorComSwitch.postValue(true);
                                mapSetFatigueSettingComMonitorStatus(mApp,1,false);
                            }
                        }

                    //疲劳度授权
                    String authstatus = (String) pl.get(KEY_NAME_FATIGUE_MONITOR_LIMIT);
                    if(authstatus != null && !authstatus.equals("")){
                        hasFatigueAuth.postValue(authstatus.equals("1"));
                    }
//                    //fatigue time list
//                    List<MonitorTimeBean> list = new ArrayList<>();
//                    String arraystr = (String) pl.get("fatigue_monitor_times");
//                    if(arraystr != null && !arraystr.equals("")) {
//                        JSONArray array = (JSONArray) JSONValue.parse(arraystr);
//                        if (array != null && array.size() > 0) {
//                            for (int i = 0; i < array.size(); i++) {
//                                JSONObject item = (JSONObject) array.get(i);
//                                String stime = (String) item.get("stime");
//                                String etime = (String) item.get("etime");
//                                String fonoff = (String) item.get("onoff");
//                                String[] start = stime.split(":");
//                                String[] end = etime.split(":");
//                                MonitorTimeBean bean = new MonitorTimeBean("", 0, start[0], start[1], end[0], end[1]
//                                        , "1111111", fonoff, String.valueOf(i));
//                                list.add(bean);
//                            }
//                            timeList.postValue(list);
//                        }
//                    }
                }
            }
        });

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(KEY_NAME_HAERTRATE_MONITOR);
        plKeyList.add(KEY_NAME_FATIGUE_MONITOR);
        plKeyList.add(KEY_NAME_FATIGUE_MONITOR_COM);
        plKeyList.add(KEY_NAME_FATIGUE_MONITOR_HF);
        plKeyList.add(KEY_NAME_FATIGUE_MONITOR_HF_TIME);
        plKeyList.add(KEY_NAME_FATIGUE_MONITOR_LIMIT);
//        plKeyList.add("fatigue_monitor_times");
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(mApp.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (mApp.getNetService() != null && mApp.getNetService().isCloudBridgeClientOk()) {
            mApp.getNetService().sendNetMsg(mapget);
        }
    }

    public void mapSetHeartSettingMonitor(ImibabyApp mApp,int monitorOnOff){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    heartMonitorSwitch.postValue(monitorOnOff==1);
                    saveHeartMonitorOnoff(mApp,monitorOnOff==1);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                    heartMonitorSwitch.postValue(!(monitorOnOff==1));
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_HAERTRATE_MONITOR, String.valueOf(monitorOnOff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    public void mapSetFatigueSettingComMonitor(ImibabyApp mApp,int comOnoff,int hfOnoff){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    saveFatigueMonitorOnoff(mApp,comOnoff == 1);
                    saveFatigueMonitorHfOnoff(mApp,hfOnoff == 1);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                    fatigueMonitorComSwitch.postValue(!(comOnoff==1));
                    fatigueMonitorHfSwitch.postValue(!(hfOnoff==1));
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_FATIGUE_MONITOR_COM,String.valueOf(comOnoff));
        pl.put(KEY_NAME_FATIGUE_MONITOR_HF,String.valueOf(hfOnoff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    public void mapSetFatigueSettingHfMonitor(ImibabyApp mApp,boolean hfOnoff,boolean comOnoff){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    saveFatigueMonitorHfOnoff(mApp,hfOnoff);
                    saveFatigueMonitorOnoff(mApp,comOnoff);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                    fatigueMonitorComSwitch.postValue(!hfOnoff);
                    fatigueMonitorHfSwitch.postValue(!comOnoff);
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_FATIGUE_MONITOR_HF,hfOnoff ? "1" : "0");
        pl.put(KEY_NAME_FATIGUE_MONITOR_COM,comOnoff ? "1" : "0");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }

    /**
     * 设置日常模式开关
     * @param mApp
     * @param onoff 开关
     * @param showToast 是否显示Toast提示
     */
    private void mapSetFatigueSettingComMonitorStatus(ImibabyApp mApp,int onoff,boolean showToast){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    if(showToast) {
                        ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    }
                    saveFatigueMonitorOnoff(mApp,onoff == 1);
                }else{
                    if(showToast) {
                        ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                    }
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_FATIGUE_MONITOR_COM,String.valueOf(onoff));
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    public void SetFatigueSettingHfMonitor(ImibabyApp mApp,int hfOnoff,int comOnoff){
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        JSONObject pl = new JSONObject();
                        pl.put("eid",mApp.getCurUser().getFocusWatch().getEid());
                        pl.put("sid",mApp.getToken());
                        if(hfOnoff == 1) {
                            pl.put(KEY_NAME_FATIGUE_MONITOR_HF_TIME, FATIGUE_MONITOR_DURATION);
                        }else{
                            pl.put(KEY_NAME_FATIGUE_MONITOR_HF_TIME, 0);
                        }
                        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(pl.toJSONString(), mApp.getNetService().AES_KEY, mApp.getNetService().AES_KEY), Base64.NO_WRAP) + mApp.getToken();
                        HttpWorks.HttpWorksResponse resp = HttpWorks.httpPost(FATIGUE_MONITOR_HF_URL,reqBody);
                        if(resp.isSuccess()){
                            e.onNext(resp.data);
                        }else{
                            e.onError(new Throwable(resp.erroMsg));
                        }
                        e.onComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
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
                            JSONObject resp = (JSONObject) JSONValue.parse(decMesString);
                            Integer code = (Integer) resp.get("code");
                            if(code != null && code == 1){
                                JSONObject data = (JSONObject) resp.get("data");
                                if(data != null && !data.equals("")) {
                                    String time = (String) data.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME);
                                    if(time != null && !time.equals("")) {
                                        JSONObject timeJson = (JSONObject) JSONValue.parse(time);
                                        String start = (String) timeJson.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME_START);
                                        String end = (String) timeJson.get(KEY_NAME_FATIGUE_MONITOR_HF_TIME_END);
                                        saveFatigueMonitorHfOnoffTime(mApp, start, end);
                                        mapSetFatigueSettingComMonitorStatus(mApp,comOnoff,true);
                                    }else{
                                        //关闭高频监测
                                        saveFatigueMonitorHfOnoffTime(mApp, "", "");
                                        mapSetFatigueSettingComMonitorStatus(mApp,1,true);
                                    }
                                }else{
                                    //关闭高频监测
                                    saveFatigueMonitorHfOnoffTime(mApp, "", "");
                                    mapSetFatigueSettingComMonitorStatus(mApp,1,true);
                                }
                                ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                            }else{
                                ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                                fatigueMonitorHfSwitch.postValue(!(hfOnoff==1));
                                fatigueMonitorComSwitch.postValue(!(comOnoff==1));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void mapSetFatigueMonitorOnOff(ImibabyApp mApp,boolean status){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    saveFatigueMonitorOnoff(mApp,status);
                    fatigueMonitorComSwitch.postValue(getFatigueMonitorComSwitch().getValue());
                    fatigueMonitorSwitch.postValue(status);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_FATIGUE_MONITOR,status ? "1" : "0");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }

    private void mapSetFatigueSettingMonitorTimes(ImibabyApp mApp,List<MonitorTimeBean> list){
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    timeList.postValue(list);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONArray array = new JSONArray();
        //时段
        boolean isOn = false;
        for(int i=0;i<list.size();i++) {
            MonitorTimeBean bean = list.get(i);
            JSONObject item = new JSONObject();
            item.put("stime",bean.starthour + ":" + bean.startmin);
            item.put("etime",bean.endhour + ":" + bean.endmin);
            item.put("onoff",bean.onoff);
            item.put("def",bean.getType());
            array.add(item);
            if(!isOn) {
                isOn = bean.onoff.equals("1");
            }
        }
        pl.put(KEY_NAME_FATIGUE_MONITOR,isOn ? "1" : "0");
        pl.put("fatigue_monitor_times",array.toJSONString());

        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    public void mapSetFatigueAuth(ImibabyApp mApp,boolean status){
        MyMsgData msgData = new MyMsgData();
        msgData.setCallback(new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if(rc == 1){
                    ToastUtil.show(mApp, mApp.getString(R.string.phone_set_success));
                    saveFatigueMonitorAuth(mApp,status);
                    hasFatigueAuth.postValue(status);
                }else{
                    ToastUtil.show(mApp, mApp.getString(R.string.set_error));
                    hasFatigueAuth.postValue(!status);
                }
            }
        });
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(KEY_NAME_FATIGUE_MONITOR_LIMIT,status ? "1" : "0");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, mApp.getCurUser().getFocusWatch().getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, mApp.getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        msgData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, mApp.getToken(), pl));
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendNetMsg(msgData);
        }
    }

    public void addNewFatigueTime(ImibabyApp mApp,String time,String onoff) throws CloneNotSupportedException {
        List<MonitorTimeBean> list = new ArrayList<>();
        List<MonitorTimeBean> srcList = timeList.getValue();
        for(int i=0;i<srcList.size();i++){
            list.add((MonitorTimeBean) srcList.get(i).clone());
        }
        String[] timeStrs = time.split(":");
        int startH = Integer.parseInt(timeStrs[0]);
        int endH = startH + 1;
        String start = String.valueOf(startH);
        String end = String.valueOf(endH);
        String id = String.valueOf(list.size()+1);
        MonitorTimeBean bean = new MonitorTimeBean("",0,start,"00",end,"00","1111111",onoff,id);
        list.add(bean);
        mapSetFatigueSettingMonitorTimes(mApp,list);
    }

    public void updateFatigueTime(ImibabyApp mApp,String id,String time,String onoff) throws CloneNotSupportedException {
        List<MonitorTimeBean> list = new ArrayList<>();
        List<MonitorTimeBean> srcList = timeList.getValue();
        for(int i=0;i<srcList.size();i++){
            list.add((MonitorTimeBean) srcList.get(i).clone());
        }
        for(int i=0;i<list.size();i++){
            MonitorTimeBean bean = list.get(i);
            if(id.equals(bean.id)){
                String[] timeStrs = time.split(":");
                int startH = Integer.parseInt(timeStrs[0]);
                int endH = startH + 1;
                String start = String.valueOf(startH);
                String end = String.valueOf(endH);
                bean.starthour = start;
                bean.endhour = end;
                bean.onoff = onoff;
                break;
            }
        }
        mapSetFatigueSettingMonitorTimes(mApp,list);
    }

    public void deleteFatigueTime(ImibabyApp mApp,String id) throws CloneNotSupportedException {
        List<MonitorTimeBean> list = new ArrayList<>();
        List<MonitorTimeBean> srcList = timeList.getValue();
        for(int i=0;i<srcList.size();i++){
            list.add((MonitorTimeBean) srcList.get(i).clone());
        }
        for(int i=0;i<list.size();i++){
            MonitorTimeBean bean = list.get(i);
            if(id.equals(bean.id)){
                list.remove(i);
                break;
            }
        }
        mapSetFatigueSettingMonitorTimes(mApp,list);
    }

    /**
     * 实时监测心率、血氧E2E消息
     * @param mApp 上下文
     * @param type 监测类型 heartRate 心率 ； oxygen 血氧
     */
    public void sendMonitorMessage(ImibabyApp mApp,String type){
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,CloudBridgeUtil.SUB_ACTION_HEALTH_MONITOR);
        pl.put("monitor_type",type);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP,TimeUtil.getTimeStampLocal());
        if(mApp.getNetService() != null){
            mApp.getNetService().sendE2EMsg(mApp.getCurUser().getFocusWatch().getEid(), sn
                    , pl, 70 * 1000, true, new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if(rc == 1){
                        JSONObject respPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String type = (String) respPl.get("monitor_type");
                        int value = (int) respPl.get("monitor_value");
                        if(type.equals("heartRate")){
                            heartRate.postValue(value);
                        }else{
                            oxy.postValue(value);
                        }
                    }else{
                        LogUtil.e("sendMonitorMessage callback rc : " + rc);
                        if(type.equals("heartRate")){
                            heartRate.postValue(-1);
                        }else{
                            oxy.postValue(-1);
                        }
                    }
                }
            });
        }
    }

    public void sendStepsReqE2eMsg(ImibabyApp myApp){
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(null);
        JSONObject pl = new JSONObject();
        String [] eid = new String[1];
        eid[0] = myApp.getCurUser().getFocusWatch().getEid();
        String timestamp = TimeUtil.getTimeStampGMT();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GET_STEPS_SUM);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, timestamp);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, myApp.getToken(), null, eid, pl));

        if(myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(e2e);
        }
    }

    public void saveHeartMonitorOnoff(ImibabyApp mApp,boolean value){
        mApp.setValue("heart_monitor_onoff",value);
    }

    /**
     * 存疲劳度日常开关
     * @param mApp
     * @param value
     */
    public void saveFatigueMonitorOnoff(ImibabyApp mApp,boolean value){
        mApp.setValue("fatigue_monitor_onoff",value);
    }

    /**
     * 存疲劳度高频开关
     * @param mApp
     * @param start
     * @param end
     */
    public void saveFatigueMonitorHfOnoffTime(ImibabyApp mApp,String start,String end){
        if(!start.equals("") && !end.equals("")) {
            String builder = start +
                    "_" +
                    end;
            mApp.setValue("fatigue_hf_monitor_time", builder);
        }else{
            mApp.setValue("fatigue_hf_monitor_time", "");
        }
    }

    /**
     * 存疲劳度总开关
     * @param mApp
     * @param value
     */
    public void saveFatigueMonitorAllOnoff(ImibabyApp mApp,boolean value){
        mApp.setValue("heart_monitor_all_onoff",value);
    }

    public void saveFatigueMonitorHfOnoff(ImibabyApp mApp,boolean value){
        mApp.setValue(KEY_NAME_FATIGUE_MONITOR_HF,value);
    }

    public void saveFatigueMonitorAuth(ImibabyApp mApp,boolean value){
        mApp.setValue(KEY_NAME_FATIGUE_MONITOR_LIMIT,value);
    }

    public void getOnoffDataFromLocal(ImibabyApp mApp){
        boolean heartonoff = mApp.getBoolValue("heart_monitor_onoff",false);
        heartMonitorSwitch.postValue(heartonoff);

        WatchData watchData = mApp.getCurUser().getFocusWatch();
            String fatiguehftime = mApp.getStringValue("fatigue_hf_monitor_time", "");
            if (fatiguehftime.equals("")) {
                fatigueMonitorHfSwitch.postValue(false);
                fatigueMonitorComSwitch.postValue(true);
            } else {
                String[] temp = fatiguehftime.split("_");
                if (isInFatigueHfDuration(temp[0], temp[1])) {
                    fatigueMonitorHfSwitch.postValue(true);
                    fatigueMonitorComSwitch.postValue(false);
                } else {
                    fatigueMonitorHfSwitch.postValue(false);
                    fatigueMonitorComSwitch.postValue(true);
                }
            }
        boolean fatigueall = mApp.getBoolValue("heart_monitor_all_onoff",false);
        fatigueMonitorSwitch.postValue(fatigueall);

        boolean fatigueauth = mApp.getBoolValue(KEY_NAME_FATIGUE_MONITOR_LIMIT,false);
        hasFatigueAuth.postValue(fatigueauth);
    }

    private boolean isInFatigueHfDuration(String start,String end){
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        try {
            Date dateStart = format.parse(start);
            Date dateEnd = format.parse(end);
            return now.after(dateStart) && now.before(dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
