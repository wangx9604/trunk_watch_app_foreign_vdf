package com.xiaoxun.test;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DrawPathActivity extends NormalActivity implements MsgCallback {

    private final static String FILE_PATH = Environment.getExternalStorageDirectory() + "/imibaby/PDR.txt";

    private ImibabyApp mApp = null;
    private NetService mNetService = null;
    private ServiceConnection conn;

    private int down_x = 0;
    private int down_y = 0;
    private int scroll_x = 0;
    private int scroll_y = 0;

    private DrawPathView view;
    private Button start;
    private Button stop;
    private Button anima;
    private Button clear;

    private boolean isOn = false;
    private Date startTime;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.testpdr")){
                String data = intent.getStringExtra("pdrdata");
                JSONObject respMsg = (JSONObject)JSONValue.parse(data);
                JSONObject pl = (JSONObject)respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                int sub_action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                if(sub_action == 110){
                    String timestr = (String)pl.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                    Date time = TimeUtil.getDataFromTimeStamp(timestr);
                    if(startTime == null){
                        LogUtil.e("app's switch is off.");
                        return;
                    }
                    if(time.after(startTime)){
                        pathInfo item = new pathInfo();
                        item.x = (Integer)pl.get("deltaX");
                        item.y = (Integer)pl.get("deltaY");
                        item.z = (Integer)pl.get("deltaZ");
                        item.step = (Integer)pl.get("stepCount");
                        item.direction = (Integer)pl.get("direction");
                        item.confidencelevel = (Integer)pl.get("confidencelevel");
                        view.updateList(item);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_path);
        view = new DrawPathView(this);
        FrameLayout contain = findViewById(R.id.contain);
        contain.addView(view);

        mApp = (ImibabyApp) getApplication();

        initviews();
        initservices();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.testpdr");
        registerReceiver(mReceiver,filter);
    }

    private void initviews(){
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn = true;
                sendPathOnOff();
                //view.istest = true;

            }
        });
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn = false;
                saveData();
                sendPathOnOff();
                //view.istest = false;

            }
        });
        anima = findViewById(R.id.anima);
        anima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOn) {
                    view.istest = true;
                    view.initPath();
                }else{
                    ToastUtil.show(DrawPathActivity.this,"please stop the PDR TEST.");
                }
            }
        });
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.istest = false;
                view.clearView();
            }
        });
    }

    private void initservices(){

        mApp.sdcardLog("DrawPathActivity initservice ");
        Intent it = new Intent(DrawPathActivity.this, NetService.class);
        bindService(it, conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mApp.sdcardLog("DrawPathActivity onServiceConnected ");
                mNetService = ((NetService.MyBinder) service).getService();
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            down_x = (int) event.getRawX();
            down_y = (int) event.getRawY();
        } else if (action == MotionEvent.ACTION_MOVE) {
            int x2 = (int) event.getRawX();
            int y2 = (int) event.getRawY();
            scroll_x = down_x - x2;
            scroll_y = down_y - y2;
            view.scrollBy(scroll_x, scroll_y);
            down_x = x2;
            down_y = y2;
        }


        return true;
    }

    private void sendPathOnOff(){
        Calendar cal = Calendar.getInstance();
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION,110);
        if(isOn){
            pl.put("Value",1);
            startTime = cal.getTime();
        }else{
            pl.put("Value",0);
            startTime = null;
        }
        pl.put("id", TimeUtil.getTimeStampFromUTC(cal.getTimeInMillis()));
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        mNetService
                .sendE2EMsg(mApp.getCurUser().getFocusWatch().getEid(),sn,pl,20*1000,true,DrawPathActivity.this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_RC);
        if(rc < 1){
            return;
        }
        switch (cid){
            case CloudBridgeUtil.CID_E2E_DOWN:
                JSONObject pl = (JSONObject)respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                int sub_action = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                if(sub_action == 110){
                    if(rc == 1){
                        LogUtil.e("Success PDR Switch.");
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.onViewDestroy();
        unbindService(conn);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onViewResume();
    }

    private void saveData(){
        JSONArray array = new JSONArray();
        for(int i=0;i<view.drawList.size();i++){
            JSONObject obj = new JSONObject();
            obj.put("deltaX",view.drawList.get(i).x);
            obj.put("deltaY",view.drawList.get(i).y);
            obj.put("deltaZ",view.drawList.get(i).z);
            obj.put("stepCount",view.drawList.get(i).step);
            obj.put("direction",view.drawList.get(i).direction);
            obj.put("confidencelevel",view.drawList.get(i).confidencelevel);
            array.add(obj);
        }

        File fp = new File(FILE_PATH);
        if(fp == null){
            try {
                fp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(fp);
                stream.write(array.toString().getBytes());
                stream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(stream != null){
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class pathInfo{
        public int x = 0;
        public int y = 0;
        public int z = 0;
        public int step = 0;
        public int direction = 0;
        public int confidencelevel = 0;
    }
}
