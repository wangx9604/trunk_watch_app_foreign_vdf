/**
 * Creation Date:2015-2-3
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.DeviceSettingAdapter;
import com.xiaoxun.xun.beans.AlarmTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AlarmClockActivity extends NormalActivity implements OnClickListener, MsgCallback {

    public static String TAG = "AlarmClockActivity";
    private ImageButton btnBack;
    private TextView alarmAddBtn;
    private RecyclerView alarmTimeRecyclerView;
    private LinearLayout noclock;
    private LinearLayoutManager mLinearLayoutManager;
    private DeviceSettingAdapter mAlarmAdapter;

    private ArrayList<HashMap<String, Object>> listItem;
    private String SHARE_PREF_ALARM_CLOCK_MODE_KEY = "alarm_clock_mode";
    private int itemButtonClickPosition = 0;
    private int itemLongClickPosition = 0;
    private int itemClickPosition = 0;
    private int itemButtonClickAddPosition = -1;    //记录添加的位置
    private View cover;
    private int handleType = 0; // 1:开关  2:删除  3:添加、修改
    private SparseArray<Integer> snPosition; //通过sn来获取操作的item的position
    private View divide;
    private boolean needMapSet = false;

    private WatchData curWatch;
    private NetService mNetService;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    noclock.setVisibility(View.GONE);
                    divide.setVisibility(View.INVISIBLE);
                    alarmTimeRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    noclock.setVisibility(View.VISIBLE);
                    divide.setVisibility(View.INVISIBLE);
                    alarmTimeRecyclerView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmclock);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.alarm_clock);
        curWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();

        initViews();
        initData();
        initListener();
        getListItemFromLocal();
        getListItemFromCloudBridge();
    }

    private void initViews() {

        btnBack = findViewById(R.id.iv_title_back);
        alarmTimeRecyclerView = findViewById(R.id.alarm_time_recyclerview);
        alarmAddBtn = findViewById(R.id.iv_alarm_add_btn);
        noclock = findViewById(R.id.noclock);
        divide = findViewById(R.id.divide_under_listview);
        cover = findViewById(R.id.cover);
    }

    private void initData() {

        listItem = new ArrayList<>();
        snPosition = new SparseArray<>();

        mLinearLayoutManager = new LinearLayoutManager(this);
        alarmTimeRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAlarmAdapter = new DeviceSettingAdapter(this, listItem);
        alarmTimeRecyclerView.setAdapter(mAlarmAdapter);
//        alarmTimeRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST) {
//        });
    }

    private void initListener() {

        btnBack.setOnClickListener(this);
        alarmAddBtn.setOnClickListener(this);

        mAlarmAdapter.setOnItemLongClickLitener(new DeviceSettingAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                itemLongClickPosition = position;
                Dialog dlg = DialogUtil.CustomNormalDialog(AlarmClockActivity.this,
                        getText(R.string.device_alarm_delete_title).toString(),
                        getText(R.string.device_alarm_delete_message).toString(),
                        new OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        },
                        getText(R.string.cancel).toString(),
                        new OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAdapterItemItem();
                                if (listItem.size() == 0) {
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        },
                        getText(R.string.confirm).toString());
                dlg.show();
            }
        });

        mAlarmAdapter.setOnItemClickLitener(new DeviceSettingAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemClickPosition = position;
                Intent intent = new Intent(AlarmClockActivity.this, AlarmClockAddActivity.class);
                AlarmTime alarmTime = (AlarmTime) listItem.get(itemClickPosition).get(Const.KEY_MAP_ALARMOBJECT);
                intent.putExtra(Const.KEY_MAP_ALARMOBJECT,alarmTime);
                startActivityForResult(intent, 2);
            }
        });

        mAlarmAdapter.setOnItemOnoffLitener(new DeviceSettingAdapter.OnRecyclerViewItemOnoffListener() {
            @Override
            public void onItemOnoffClick(View view, int position) {
                int count = getSwitchedOnItemCount();
                AlarmTime alarmTime = (AlarmTime) listItem.get(position).get(Const.KEY_MAP_ALARMOBJECT);
                if (count >= Const.MAX_SWTICHED_ON_ALARMCLOCK_COUNT && alarmTime.onoff.equals("0")) {
                    ToastUtil.showMyToast(AlarmClockActivity.this,
                            getString(R.string.alarmclock_max_count_prompt),
                            Toast.LENGTH_SHORT);
                    return;
                }
                itemButtonClickPosition = position;
                alarmTime.timeStampId = TimeUtil.getTimeStampLocal();
                mapSetAlarmListMsg(listItem, itemButtonClickPosition, 1);
                itemButtonClick(itemButtonClickPosition);
            }
        });
    }

    private void getListItemFromLocal() {

        String alarmTimeDataStr = myApp.getStringValue(curWatch.getEid() + SHARE_PREF_ALARM_CLOCK_MODE_KEY, "");
        try {
            JSONArray array = (JSONArray) JSONValue.parse(alarmTimeDataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                AlarmTime alarmTime = new AlarmTime();
                alarmTime = AlarmTime.toBeAlarmTimeBean(alarmTime, jsonObj);
                if (!isAlarmClockOverdue(alarmTime)) {
                    addAdapterOnItemToList(alarmTime,listItem.size());
                }
            }
        } catch (Exception e) {
            // hyy：修改了本地存储格式，取旧数据会出现异常，这里进行捕获，并将本地数据置空
            myApp.setValue(curWatch.getEid() + SHARE_PREF_ALARM_CLOCK_MODE_KEY, "");
        }
        if (listItem.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void getListItemFromCloudBridge() {

        if (mNetService != null)
            mNetService.sendMapGetMsg(curWatch.getEid(), CloudBridgeUtil.ALARM_CLOCK_LIST, AlarmClockActivity.this);
    }
    
    private void addAdapterOnItemToList(AlarmTime addAlarmTime, int num) {
        HashMap<String, Object> map = new HashMap<>();
        setItemAdapterMap(map, addAlarmTime);
        listItem.add(num, map);
        mAlarmAdapter.notifyDataSetChanged();
    }

    private void setItemAdapterMap(HashMap<String, Object> map, AlarmTime addNewAlarmTime) {

        map.put(Const.KEY_MAP_TIME, addNewAlarmTime.hour + ":" + addNewAlarmTime.min);
        if (addNewAlarmTime.select.length() > 0) {
            if (addNewAlarmTime.select.equals("1"))
                map.put(Const.KEY_MAP_TITLE, getResources().getString(R.string.device_alarm_reset_1));
            else if (addNewAlarmTime.select.equals("2"))
                map.put(Const.KEY_MAP_TITLE, getResources().getString(R.string.device_alarm_reset_2));
            else if (addNewAlarmTime.select.equals("3"))
                map.put(Const.KEY_MAP_TITLE, getResources().getString(R.string.device_alarm_reset_3));
            else if (addNewAlarmTime.select.equals("4")) {
                String days = addNewAlarmTime.days;
                String title = (days.substring(2, 3).equals("1") ? getText(R.string.week_1) : "")
                        + (days.substring(4, 5).equals("1") ? " " + getText(R.string.week_2) : "")
                        + (days.substring(6, 7).equals("1") ? " " + getText(R.string.week_3) : "")
                        + (days.substring(8, 9).equals("1") ? " " + getText(R.string.week_4) : "")
                        + (days.substring(10, 11).equals("1") ? " " + getText(R.string.week_5) : "")
                        + (days.substring(12, 13).equals("1") ? " " + getText(R.string.week_6) : "")
                        + (days.substring(14, 15).equals("1") ? " " + getText(R.string.week_0) : "");
                map.put(Const.KEY_MAP_TITLE, title);
            }
        }
        if (addNewAlarmTime.onoff.equals("1")) {
            String sTmp = getResources().getString(R.string.open);
            map.put(Const.KEY_MAP_STATUS, sTmp);
            map.put(Const.KEY_MAP_IMG, R.drawable.switch_on);
        } else if (addNewAlarmTime.onoff.equals("0")) {
            String sTmp = getResources().getString(R.string.close);
            map.put(Const.KEY_MAP_STATUS, sTmp);
            map.put(Const.KEY_MAP_IMG, R.drawable.switch_off);
        } else if (addNewAlarmTime.onoff.equals("-1")) {
            String sTmp = getResources().getString(R.string.opening);
            map.put(Const.KEY_MAP_STATUS, sTmp);
            map.put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
        }
        map.put(Const.KEY_MAP_ALARMOBJECT, addNewAlarmTime);
    }

    private void saveListItemToLocal() {
        JSONArray plA = new JSONArray();
        for (int i = 0; i < listItem.size(); i++) {
            JSONObject plO = new JSONObject();
            AlarmTime alarmTime = (AlarmTime) listItem.get(i).get(Const.KEY_MAP_ALARMOBJECT);
            plO = AlarmTime.toJsonObjectFromAlarmTimeBean(plO, alarmTime);
            plA.add(plO);
        }
        myApp.setValue(curWatch.getEid() + SHARE_PREF_ALARM_CLOCK_MODE_KEY, plA.toJSONString());
    }

    private void removeOnAlarmInList() {
        for (int i = 0; i < listItem.size(); i++) {
            AlarmTime ret = (AlarmTime) listItem.get(i).get(Const.KEY_MAP_ALARMOBJECT);
            if (ret.onoff.equals("1")) {
                listItem.remove(i);
                i = i - 1;
            }
        }
    }

    private void deleteAdapterItemItem() {
        int size = listItem.size();
        if (size > itemLongClickPosition) {
            AlarmTime alarmTime = (AlarmTime) listItem.get(itemLongClickPosition).get(Const.KEY_MAP_ALARMOBJECT);
            if (alarmTime.onoff.equals("0")) {
                listItem.remove(itemLongClickPosition);
                mAlarmAdapter.notifyDataSetChanged();
                saveListItemToLocal();
            } else if (alarmTime.onoff.equals("1")) {
                mapSetAlarmListMsg(listItem, itemLongClickPosition, 2);
                listItem.get(itemLongClickPosition).put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
                String sTmp = getResources().getString(R.string.deleting);
                listItem.get(itemLongClickPosition).put(Const.KEY_MAP_STATUS, sTmp);
                mAlarmAdapter.notifyDataSetChanged();
            }
        }
    }

    // get switched on item count,max:Const.MAX_SWTICHED_ON_ALARMCLOCK_COUNT
    private int getSwitchedOnItemCount() {
        int count = 0;
        for (int i = 0; i < listItem.size(); i++) {
            AlarmTime alarmTime = (AlarmTime) listItem.get(i).get(Const.KEY_MAP_ALARMOBJECT);
            if (alarmTime.onoff.equals("1")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        if (btnBack == v) {
            finish();
        } else if (alarmAddBtn == v) {
            int count = getSwitchedOnItemCount();
            if (count >= Const.MAX_SWTICHED_ON_ALARMCLOCK_COUNT) {
                ToastUtil.showMyToast(AlarmClockActivity.this,
                        getString(R.string.alarmclock_max_count_prompt),
                        Toast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(AlarmClockActivity.this, AlarmClockAddActivity.class);
            startActivityForResult(intent, 2);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 2 && 2 == requestCode) {
            AlarmTime alarmTime =  (AlarmTime) data.getSerializableExtra(Const.KEY_MAP_ALARMOBJECT);
            addAdapterOnItemToList(alarmTime,listItem.size());
            itemButtonClickAddPosition = itemButtonClickPosition = listItem.size() - 1;
            mapSetAlarmListMsg(listItem, itemButtonClickPosition, 3);
            ((AlarmTime) listItem.get(itemButtonClickPosition).get(Const.KEY_MAP_ALARMOBJECT)).onoff = "0";
            itemButtonClick(itemButtonClickPosition);
        } else if (resultCode == 1 && 2 == requestCode) {
            AlarmTime alarmTime =  (AlarmTime) data.getSerializableExtra(Const.KEY_MAP_ALARMOBJECT);
            if (alarmTime.onoff.equals("0")) {
                HashMap<String, Object> map = new HashMap<>();
                setItemAdapterMap(map, alarmTime);
                listItem.remove(itemClickPosition);
                listItem.add(itemClickPosition, map);
                mAlarmAdapter.notifyDataSetChanged();
                saveListItemToLocal();//保存
            } else if (alarmTime.onoff.equals("1")) {
                HashMap<String, Object> map = new HashMap<>();
                setItemAdapterMap(map, alarmTime);
                String sTmp = getResources().getString(R.string.opening);
                map.put(Const.KEY_MAP_STATUS, sTmp);
                map.put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
                listItem.remove(itemClickPosition);
                listItem.add(itemClickPosition, map);
                mAlarmAdapter.notifyDataSetChanged();
                mapSetAlarmListMsg(listItem, itemClickPosition, 3);
                ((AlarmTime) listItem.get(itemClickPosition).get(Const.KEY_MAP_ALARMOBJECT)).onoff = "0";
                itemButtonClick(itemClickPosition);
            }
        }
        if (listItem.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * @param listData:数据源
    * @param position:操作的item的position
    * @param handleType:操作的类型1(开关),2(删除),3(增加、修改)
    * */
    private void mapSetAlarmListMsg(ArrayList<HashMap<String, Object>> listData, int position, int type) {

        handleType = type;
        cover.setClickable(true);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        snPosition.put(sn, position);
        JSONArray plA = new JSONArray();
        AlarmTime alarmTime;
        if (listData.size() != 0) {
            for (int i = 0; i < listData.size(); i++) {
                if (i != position) {
                    JSONObject plO = new JSONObject();
                    alarmTime = (AlarmTime) listData.get(i).get(Const.KEY_MAP_ALARMOBJECT);
                    if (alarmTime.onoff.equals("1")) {
                        plO=AlarmTime.toJsonObjectFromAlarmTimeBean(plO,alarmTime);
                        plA.add(plO);
                    }
                }
            }
        }

        if (handleType == 1) {
            alarmTime = (AlarmTime) listData.get(position).get(Const.KEY_MAP_ALARMOBJECT);
            if (alarmTime.onoff.equals("1")) {
                //从开到关的数据不用添加发送
            } else if (alarmTime.onoff.equals("0")) {
                JSONObject plO = new JSONObject();
                plO=AlarmTime.toJsonObjectFromAlarmTimeBean(plO,alarmTime);
                plO.put(CloudBridgeUtil.ALARM_ONOFF, "1");
                plA.add(plO);

            }
        } else if (handleType == 2) {
            //删除的数据不用添加发送
        } else if (handleType == 3) {
            plA.clear();
            if (listData.size() != 0) {
                for (int i = 0; i < listData.size(); i++) {
                    JSONObject plO = new JSONObject();
                    alarmTime = (AlarmTime) listData.get(i).get(Const.KEY_MAP_ALARMOBJECT);
                    if (alarmTime.onoff.equals("1")) {
                        plO=AlarmTime.toJsonObjectFromAlarmTimeBean(plO,alarmTime);
                        plA.add(plO);
                    }
                }
            }
        }

        if (mNetService != null)
            mNetService.sendMapSetMsg(curWatch.getEid(), curWatch.getFamilyId(), sn,
                    CloudBridgeUtil.ALARM_CLOCK_LIST, plA.toString(), AlarmClockActivity.this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        cover.setClickable(false);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int sn = (Integer) reqMsg.get(CloudBridgeUtil.KEY_NAME_SN);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                int position = snPosition.get(sn);
                snPosition.remove(sn);
                if (rc > 0) {
                    if (handleType == 1) {
                        itemButtonClickResult(position);
                    } else if (handleType == 2) {
                        listItem.remove(position);
                        mAlarmAdapter.notifyDataSetChanged();
                        saveListItemToLocal();
                        if (listItem.size() == 0) {
                            mHandler.sendEmptyMessage(1);
                        }
                    } else if (handleType == 3) {
                        if (!needMapSet)
                            itemButtonClickResult(position);
                        itemButtonClickAddPosition = -1;
                    }
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.showMyToast(this,
                            getString(R.string.phone_set_timeout),
                            Toast.LENGTH_SHORT);
                    backToPreviousState(position);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt),
                            Toast.LENGTH_SHORT);
                    backToPreviousState(position);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    ToastUtil.showMyToast(this, getString(R.string.set_error),
                            Toast.LENGTH_SHORT);
                    backToPreviousState(position);
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_RESP:
                int rcMapGet = CloudBridgeUtil.getCloudMsgRC(respMsg);
                int num = 0;
                if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
                    String sTmp = (String) pl.get(CloudBridgeUtil.ALARM_CLOCK_LIST);
                    if (sTmp != null && sTmp.length() > 0) {
                        Object obj = JSONValue.parse(sTmp);
                        JSONArray array = (JSONArray) obj;
                        if (array != null) {
                            removeOnAlarmInList();
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject jsonObj = (JSONObject) array.get(i);
                                AlarmTime alarmTime = new AlarmTime();
                                alarmTime = AlarmTime.toBeAlarmTimeBean(alarmTime, jsonObj);
                                boolean isOverdue = isAlarmClockOverdue(alarmTime);
                                if (!isOverdue) {
                                    addAdapterOnItemToList(alarmTime, num);
                                    num++;
                                } else
                                    needMapSet = true;
                            }
                            mAlarmAdapter.notifyDataSetChanged();
                            saveListItemToLocal();
                            if (needMapSet) {
                                mapSetAlarmListMsg(listItem, 0, 3);
                            }
                        }
                        if (listItem.size() > 0) {
                            mHandler.sendEmptyMessage(0);
                        }
                    } else {
                        //服务器数据为0,苹果app传过来的为"",安卓为"[]",0为服务器闹钟个数
                        listItem.clear();
                        mAlarmAdapter.notifyDataSetChanged();
                        saveListItemToLocal();
                    }
                }

            default:
                break;
        }
    }

    private void itemButtonClickResult(int position) {
        AlarmTime alarmTime = new AlarmTime();
        if (listItem.size() != 0)
            alarmTime = (AlarmTime) listItem.get(position).get(Const.KEY_MAP_ALARMOBJECT);
        if (alarmTime.onoff.equals("0")) {
            alarmTime.onoff = "1";
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on);
            String sTmp = getResources().getString(R.string.open);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        } else if (alarmTime.onoff.equals("1")) {
            alarmTime.onoff = "0";
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off);
            String sTmp = getResources().getString(R.string.close);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        }
        mAlarmAdapter.notifyDataSetChanged();
        saveListItemToLocal();//保存
    }

    private void itemButtonClick(int position) {
        AlarmTime alarmTime = (AlarmTime) listItem.get(position).get(Const.KEY_MAP_ALARMOBJECT);
        if (alarmTime.onoff.equals("0")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
            String sTmp = getResources().getString(R.string.opening);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        } else if (alarmTime.onoff.equals("1")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off_wait);
            String sTmp = getResources().getString(R.string.closing);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        }
        mAlarmAdapter.notifyDataSetChanged();
    }

    private void backToPreviousState(int position) {
        if (position == itemButtonClickAddPosition) { //对于新添加的，如果添加失败，则删除
            listItem.remove(itemButtonClickAddPosition);
            mAlarmAdapter.notifyDataSetChanged();
            itemButtonClickAddPosition = -1;
            return;
        }
        AlarmTime alarmTime = (AlarmTime) listItem.get(position).get(Const.KEY_MAP_ALARMOBJECT);
        if (alarmTime.onoff.equals("0")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off);
            String sTmp = getResources().getString(R.string.close);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        } else if (alarmTime.onoff.equals("1")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on);
            String sTmp = getResources().getString(R.string.open);
            listItem.get(position).put(Const.KEY_MAP_STATUS, sTmp);
        }
        mAlarmAdapter.notifyDataSetChanged();
    }

    private boolean isAlarmClockOverdue(AlarmTime clock) {

        boolean ret = false;
        if (clock.days.equals("0,0,0,0,0,0,0,0")) {
            Calendar cal = Calendar.getInstance();
            Date setDate = TimeUtil.getDataFromTimeStamp(clock.timeStampId);
            Calendar setCal = Calendar.getInstance();
            setCal.setTime(setDate);
            int minofcontent = Integer.valueOf(clock.hour) * 60 + Integer.valueOf(clock.min);
            int minofset = setCal.get(Calendar.HOUR_OF_DAY) * 60 + setCal.get(Calendar.MINUTE);
            if (minofset >= minofcontent) {
                Calendar contentCal = Calendar.getInstance();
                contentCal.setTime(setDate);
                contentCal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(clock.hour));
                contentCal.set(Calendar.MINUTE, Integer.valueOf(clock.min));
                contentCal.add(Calendar.DATE, 1);
                ret = contentCal.compareTo(cal) <= 0;
            } else {
                Calendar contentCal = Calendar.getInstance();
                contentCal.setTime(setDate);
                contentCal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(clock.hour));
                contentCal.set(Calendar.MINUTE, Integer.valueOf(clock.min));
                ret = contentCal.compareTo(cal) <= 0;
            }
        }
        if (ret) {
            String time = clock.hour + ":" + clock.min;
            myApp.sdcardLog("isAlarmClockOverdue timeStampId=" + clock.timeStampId + " content=" + time);
        }
        return ret;
    }
}
