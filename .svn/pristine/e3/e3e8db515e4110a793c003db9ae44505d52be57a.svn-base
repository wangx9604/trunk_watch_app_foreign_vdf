package com.xiaoxun.xun.health.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.gallary.swiplayout.SHSwipeRefreshLayout;
import com.xiaoxun.xun.securityarea.WaitingDialog;
import com.xiaoxun.xun.utils.HttpWorks;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MonitorRecordActivity extends NormalAppCompatActivity {

    public static final String DATE_URL = "https://dcenter.xunkids.com/hcxl/healthyMonth";
    public static final String RECORD_URL = "https://dcenter.xunkids.com/hcxl/healthyRecord";
    public static final int PAGE_SIZE = 4;

    ImibabyApp mApp;
    ConstraintLayout ly_nodata;
    SHSwipeRefreshLayout ly_swipe;
    RecyclerView rv_list;
    WaitingDialog dlg;
    MonitorRecordAdapter adapter;

    List<MonitorRecord> dataList = new ArrayList<>();

    JSONArray dates;
    int currentPos = 0;

    Comparator<Record> comparator = new Comparator<Record>() {
        @Override
        public int compare(Record s, Record t1) {
            if( Long.parseLong(s.getTimeStamp()) >  Long.parseLong(t1.getTimeStamp())){
                return -1;
            }else if( Long.parseLong(s.getTimeStamp()) <  Long.parseLong(t1.getTimeStamp())){
                return 1;
            }
            return 0;
        }
    };
    Comparator<MonitorRecord> comparatorMonitorRecord = new Comparator<MonitorRecord>() {
        @Override
        public int compare(MonitorRecord s, MonitorRecord t1) {
            if( Long.parseLong(s.getDayTime()) >  Long.parseLong(t1.getDayTime())){
                return -1;
            }else if( Long.parseLong(s.getDayTime()) <  Long.parseLong(t1.getDayTime())){
                return 1;
            }
            return 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_record);
        StatusBarUtil.setStatusBarColor(this, R.color.schedule_no_class);
        mApp = (ImibabyApp) getApplication();
        initViews();
        getMonitorRecordDate(mApp.getCurUser().getFocusWatch().getEid());
    }

    private void initViews() {
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dlg = new WaitingDialog(this, R.style.Theme_DataSheet);
        ly_nodata = findViewById(R.id.ly_nodata);
        ly_swipe = findViewById(R.id.ly_swipe);
        rv_list = findViewById(R.id.rv_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(manager);
        adapter = new MonitorRecordAdapter(this, dataList);
        rv_list.setAdapter(adapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.refresh_view, null);
        final TextView textView = (TextView) view.findViewById(R.id.title);
        ly_swipe.setRefreshEnable(false);
        ly_swipe.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoading() {
                getMonitorRecords(mApp.getCurUser().getFocusWatch().getEid());
            }

            @Override
            public void onRefreshPulStateChange(float percent, int state) {

            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        textView.setText(getString(R.string.pull_up_load));
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        textView.setText(getString(R.string.release_load));
                        break;
                    case SHSwipeRefreshLayout.START:
                        textView.setText(getString(R.string.loading));
                        break;
                }
            }
        });
    }

    private void getMonitorRecordDate(String eid) {
        if (dlg != null && !dlg.isShowing()) {
            dlg.show();
        }
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        JSONObject pl = new JSONObject();
                        pl.put("eid", eid);
                        HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(DATE_URL, pl.toJSONString());
                        if (response.isSuccess()) {
                            e.onNext(response.data);
                        } else {
                            e.onError(new Throwable("getMonitorRecordDate error."));
                        }
                        e.onComplete();
                    }
                }).observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (s != null && !s.equals("")) {
                            JSONObject pl = (JSONObject) JSONValue.parse(s);
                            JSONArray array = (JSONArray) pl.get("data");
                            if(array != null) {
                                dates = jsonArraySort(array);
                                getMonitorRecords(eid);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dlg != null && dlg.isShowing()) {
                                    dlg.dismiss();
                                }
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e("getMonitorRecordDate onComplete.");
                    }
                });
    }

    private void getMonitorRecords(String eid) {
        if (dlg != null && !dlg.isShowing()) {
            dlg.show();
        }
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        JSONObject pl = new JSONObject();
                        pl.put("eid", eid);
                        JSONArray days = getDates(dates, currentPos);
                        if(days.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ly_swipe.finishLoadmore();
                                }
                            });
                        }
                        pl.put("month", days);
                        LogUtil.e("getMonitorRecords request : " + pl.toJSONString());
                        HttpWorks.HttpWorksResponse response = HttpWorks.httpPost(RECORD_URL, pl.toJSONString());
                        if (response.isSuccess()) {
                            e.onNext(response.data);
                        } else {
                            e.onError(new Throwable("getMonitorRecords error."));
                        }
                        e.onComplete();
                    }
                }).observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        currentPos += PAGE_SIZE;
                        parseRecordData(s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dataList.size() > 0){
                                    ly_nodata.setVisibility(View.GONE);
                                }else{
                                    ly_nodata.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (dlg != null && dlg.isShowing()) {
                                    dlg.dismiss();
                                }
                                ly_swipe.finishLoadmore();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dlg != null && dlg.isShowing()) {
                                    dlg.dismiss();
                                    ly_swipe.finishLoadmore();
                                }
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e("getMonitorRecordDate onComplete.");
                    }
                });
    }

    private void parseRecordData(String s) {
        JSONObject pl = (JSONObject) JSONValue.parse(s);
        int code = (int) pl.get("code");
        if (code == 1) {
            JSONObject data = (JSONObject) pl.get("data");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String time = entry.getKey();
                List<Record> list = new ArrayList<>();
                JSONArray array = (JSONArray) entry.getValue();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String type = (String) obj.get("type");
                    String timeStamp = (String) obj.get("dataTime");
                    String value = (String) obj.get("value");
                    int t = getRecordType(type);
                    if(isFatigueInvisiable(t)){
                        continue;
                    }
                    Record record = new Record(timeStamp, t, Integer.parseInt(value));
                    list.add(record);
                }
                Collections.sort(list,comparator);
                MonitorRecord monitorRecord = new MonitorRecord(time, list);
                dataList.add(monitorRecord);
                Collections.sort(dataList,comparatorMonitorRecord);
            }
        } else {
            LogUtil.e("parseRecordData code = " + code);
        }
    }

    private int getRecordType(String type) {
        if (type.equals("hearteRate")) {
            return 0;
        } else if (type.equals("oxygen")) {
            return 1;
        } else if (type.equals("visualFatigue")) {
            return 2;
        } else {
            return 3;
        }
    }

    private JSONArray getDates(JSONArray array, int pos) {
        JSONArray ret = new JSONArray();
        int size = PAGE_SIZE;
        if (pos + size > array.size() - 1) {
            size = array.size() - pos;
        }
        for (int i = 0; i < size; i++) {
            ret.add(array.get(pos + i));
        }
        return ret;
    }

    private boolean isFatigueInvisiable(int type) {
        WatchData watchData = mApp.getCurUser().getFocusWatch();
        if ((type == 2 || type == 3)){
            return true;
        } else {
            return false;
        }
    }

    public static JSONArray jsonArraySort(JSONArray jsonArr) {
        JSONArray sortedJsonArray = new JSONArray();
        List<String> jsonValues = new ArrayList<String>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add((String) jsonArr.get(i));
        }
        Collections.sort(jsonValues, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return -a.compareTo(b);
            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
}