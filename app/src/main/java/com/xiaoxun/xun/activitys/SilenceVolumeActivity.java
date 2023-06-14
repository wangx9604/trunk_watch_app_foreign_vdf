/**
 * Creation Date:2015-2-3
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.DeviceSettingAdapter;
import com.xiaoxun.xun.beans.SilenceTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.FullyLinearLayoutManager;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;

public class SilenceVolumeActivity extends NormalActivity implements OnClickListener, MsgCallback {

    private ImageButton btnBack;
    private TextView messageDetail;
    private View silenceTimeAdd;

    private View itemMorning;
    private ImageButton btSwitchMorining;
    private TextView tvTimeMorning;
    private TextView tvWeekMoring;
    private TextView tvStatusMorning;
    private View itemAfternoon;
    private ImageButton btSwitchAfternoon;
    private TextView tvTimeAfternoon;
    private TextView tvWeekAfternoon;
    private TextView tvStatusAfternoon;
    private ScrollView scrollView;
    private View selfSetView;
    private View selfSetView_divide;
    private View cover;
    private RecyclerView silenceTimeRecyclerView;
    private View divideLine;
    private ArrayList<HashMap<String, Object>> listItem;
    private LinearLayoutManager mLinearLayoutManager;
    private DeviceSettingAdapter mSilenceAdapter;

    private SilenceTime silenceTimeMorning;
    private SilenceTime silenceTimeAfternoon;
    private int morAftFlag = 0;//标志是上午课时 -2上午，-1下午
    private HashMap<Integer, Integer> snPosition; //通过sn来获取操作的item的position
    private int itemClickPosition = 0;
    private int itemButtonClickPosition = 0;
    private int itemLongClickPosition = 0;
    private int itemButtonClickAddPosition = -1;    //记录添加的位置
    private int handleType = 0; // 1:开关  2:删除  3:添加、修改

    private WatchData curWatch;
    private NetService mNetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silence_volume);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.setting_watch_silence);
        curWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();

        initViews();
        initData();
        initMorAft();
        initListener();
        getListItemFromLocal();
        getListItemFromCloudBridge();
    }

    private void initViews() {

        btnBack = findViewById(R.id.iv_title_back);
        silenceTimeAdd = findViewById(R.id.iv_silence_add);
        messageDetail= findViewById(R.id.message_detail);

        itemMorning = findViewById(R.id.morning_class_time);
        btSwitchMorining = findViewById(R.id.switch_morninig);
        itemAfternoon = findViewById(R.id.afternoon_class_time);
        btSwitchAfternoon = findViewById(R.id.switch_afternoon);
        tvTimeMorning = findViewById(R.id.start_end_time_morning);
        tvTimeAfternoon = findViewById(R.id.start_end_time_afternoon);
        tvWeekMoring = findViewById(R.id.week1);
        tvWeekAfternoon = findViewById(R.id.week2);
        tvStatusMorning = findViewById(R.id.status1);
        tvStatusAfternoon = findViewById(R.id.status2);

        silenceTimeRecyclerView = findViewById(R.id.silence_time_recyclerview);
        divideLine=findViewById(R.id.self_set_divide_bottom);
        selfSetView = findViewById(R.id.self_set);
        selfSetView_divide = findViewById(R.id.self_set_divide);
        scrollView = findViewById(R.id.scrollview);
        cover = findViewById(R.id.cover);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 20170216修改：不再做类型判断，统一隐去深度防打扰。设置的时候，advanceop字段强制置为1
        // 20170301修改：考虑到102和105用户、302出货可能不支持调整功能，需要对文案描述加版本判断
        boolean isOldSilence = curWatch.isDevice102() || (curWatch.isDevice105() && !myApp.isControledByVersion(curWatch, false, "T13"))
                || (curWatch.isDevice302() && !myApp.isControledByVersion(curWatch, false, "T16"));
        if(isOldSilence)
            messageDetail.setText(getText(R.string.device_silence_message_detail_old));
        else
            messageDetail.setText(getText(R.string.device_silence_message_detail_new));

        if (!curWatch.isWatch()) {
            messageDetail.setText(getText(R.string.device_silence_message_detail_203));
        }
    }

    private void initData() {

        listItem = new ArrayList<>();
        snPosition = new HashMap<>();
        silenceTimeMorning = new SilenceTime();
        silenceTimeAfternoon = new SilenceTime();

        mLinearLayoutManager = new FullyLinearLayoutManager(this);
        silenceTimeRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSilenceAdapter = new DeviceSettingAdapter(this, listItem);
        silenceTimeRecyclerView.setAdapter(mSilenceAdapter);
//        silenceTimeRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST) {
//        });
    }

    private void initListener() {

        btnBack.setOnClickListener(this);
        silenceTimeAdd.setOnClickListener(this);
        itemMorning.setOnClickListener(this);
        btSwitchMorining.setOnClickListener(this);
        itemAfternoon.setOnClickListener(this);
        btSwitchAfternoon.setOnClickListener(this);

        mSilenceAdapter.setOnItemLongClickLitener(new DeviceSettingAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                itemLongClickPosition = position;
                Dialog dlg = DialogUtil.CustomNormalDialog(SilenceVolumeActivity.this,
                        getText(R.string.device_silence_delete_title).toString(),
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
                                SilenceTime oldSilenceTime = (SilenceTime) listItem.get(itemLongClickPosition).get("silenceObject");
                                if (oldSilenceTime.onoff.equals("1")) {
                                    listItem.get(itemLongClickPosition).put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
                                    listItem.get(itemLongClickPosition).put(Const.KEY_MAP_STATUS, getText(R.string.deleting));
                                    mSilenceAdapter.notifyDataSetChanged();
                                    mapSetSilenceListMsg(listItem, itemLongClickPosition, 2);
                                    handleType = 2;
                                } else {
                                    deleteAdapterItem(itemLongClickPosition);
                                    saveListToLocal(null);
                                }
                            }
                        },
                        getText(R.string.confirm).toString());
                dlg.show();
            }
        });

        mSilenceAdapter.setOnItemClickLitener(new DeviceSettingAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemClickPosition = position;
                Intent intent = new Intent(SilenceVolumeActivity.this, SilenceModeActivity.class);
                SilenceTime silenceTime = (SilenceTime) listItem.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
                intent.putExtra(Const.KEY_MAP_SILENCEOBJECT,silenceTime);
                startActivityForResult(intent, 1);//requestCode 自定义数据的修改
            }
        });

        mSilenceAdapter.setOnItemOnoffLitener(new DeviceSettingAdapter.OnRecyclerViewItemOnoffListener() {
            @Override
            public void onItemOnoffClick(View view, int position) {

                SilenceTime silencetime = (SilenceTime) listItem.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
                if (silencetime.onoff.equals("0")) {
                    if (getSwitchedOnItemCount() >= 3) {
                        ToastUtil.showMyToast(SilenceVolumeActivity.this,
                                getString(R.string.silencetime_max_count_prompt),
                                Toast.LENGTH_SHORT);
                        return;
                    }
                }
                itemButtonClickPosition = position;
                mapSetSilenceListMsg(listItem, itemButtonClickPosition, 1);
                handleType = 1;
                itemButtonClick(itemButtonClickPosition);
            }
        });
    }

    private void getListItemFromLocal() {

        String silenceDataStr = myApp.getStringValue(curWatch.getEid() + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, "");
        if (!silenceDataStr.equals("") && !silenceDataStr.equals("[]")) {
            JSONArray array = (JSONArray) JSONValue.parse(silenceDataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                if ((jsonObj.get(CloudBridgeUtil.TIMESTAMPID)).equals(Const.SILENCE_TIME_MORNING_TIMEID)) {
                    silenceTimeMorning = SilenceTime.toBeSilenceTimeBean(silenceTimeMorning, jsonObj);
                    updateMorAftTime();
                } else if ((jsonObj.get(CloudBridgeUtil.TIMESTAMPID)).equals(Const.SILENCE_TIME_AFTERNOON_TIMEID)) {
                    silenceTimeAfternoon = SilenceTime.toBeSilenceTimeBean(silenceTimeAfternoon, jsonObj);
                    updateMorAftTime();
                } else {
                    setSelfSetVisibility(true);
                    SilenceTime tmpSilenceTime = new SilenceTime();
                    tmpSilenceTime = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                    addAdapterItem(tmpSilenceTime, listItem.size());
                }
            }
        } else {
            initSaveJsonString();
        }
    }

    private void getListItemFromCloudBridge() {

        if (mNetService != null)
            mNetService.sendMapGetMsg(curWatch.getEid(), CloudBridgeUtil.SILENCE_LIST, SilenceVolumeActivity.this);
    }

    private void initSaveJsonString() {

        initMorAft();
        JSONArray plA = new JSONArray();
        JSONObject plMor = new JSONObject();
        plMor = SilenceTime.toJsonObjectFromSilenceTimeBean(plMor, silenceTimeMorning);
        plA.add(plMor);
        JSONObject plAft = new JSONObject();
        plAft = SilenceTime.toJsonObjectFromSilenceTimeBean(plAft, silenceTimeAfternoon);
        plA.add(plAft);
        updateMorAftTime();
        saveListToLocal(plA.toJSONString());
    }

    private void initMorAft() {

        silenceTimeMorning = new SilenceTime("09", "00", "11", "30", "1111100", "0", Const.SILENCE_TIME_MORNING_TIMEID, 1, 0);
        silenceTimeAfternoon = new SilenceTime("13", "00", "16", "00", "1111100", "0", Const.SILENCE_TIME_AFTERNOON_TIMEID, 1, 0);
    }

    private void saveListToLocal(String list) {

        if (list != null) {
            myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, list);
        } else {
            JSONArray plA = new JSONArray();
            JSONObject plMor = new JSONObject();
            plMor = SilenceTime.toJsonObjectFromSilenceTimeBean(plMor, silenceTimeMorning);
            plA.add(plMor);
            JSONObject plAft = new JSONObject();
            plAft = SilenceTime.toJsonObjectFromSilenceTimeBean(plAft, silenceTimeAfternoon);
            plA.add(plAft);
            for (int i = 0; i < listItem.size(); i++) {
                JSONObject plO = new JSONObject();
                SilenceTime silenceTime = (SilenceTime) listItem.get(i).get(Const.KEY_MAP_SILENCEOBJECT);
                plO = SilenceTime.toJsonObjectFromSilenceTimeBean(plO, silenceTime);
                plA.add(plO);
            }
            myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_SILENCE_NEW_MODE_KEY, plA.toJSONString());
        }
    }

    private void updateMorAftTime() {

        //上午课程时间的更新
        tvTimeMorning.setText(silenceTimeMorning.starthour + ":" + silenceTimeMorning.startmin + "  " + "-" + "  " +
                silenceTimeMorning.endhour + ":" + silenceTimeMorning.endmin);
        if (silenceTimeMorning.onoff.equals("0")) {
            tvStatusMorning.setText(getText(R.string.close));
            btSwitchMorining.setBackgroundResource(R.drawable.switch_off);
        } else if (silenceTimeMorning.onoff.equals("1")) {
            tvStatusMorning.setText(getText(R.string.open));
            btSwitchMorining.setBackgroundResource(R.drawable.switch_on);
        }
        if (silenceTimeMorning.days.equals("1111100")) {
            tvWeekMoring.setText(getText(R.string.device_alarm_reset_2));
        } else if (silenceTimeMorning.days.equals("1111111")) {
            tvWeekMoring.setText(getText(R.string.device_alarm_reset_3));
        } else {
            tvWeekMoring.setText(getWeeks(silenceTimeMorning));
        }
        //下午课程时间的更新
        tvTimeAfternoon.setText(silenceTimeAfternoon.starthour + ":" + silenceTimeAfternoon.startmin + "  " + "-" + "  " +
                silenceTimeAfternoon.endhour + ":" + silenceTimeAfternoon.endmin);

        if (silenceTimeAfternoon.onoff.equals("0")) {
            tvStatusAfternoon.setText(getText(R.string.close));
            btSwitchAfternoon.setBackgroundResource(R.drawable.switch_off);

        } else if (silenceTimeAfternoon.onoff.equals("1")) {
            tvStatusAfternoon.setText(getText(R.string.open));
            btSwitchAfternoon.setBackgroundResource(R.drawable.switch_on);
        }
        if (silenceTimeAfternoon.days.equals("1111100")) {
            tvWeekAfternoon.setText(getText(R.string.device_alarm_reset_2));
        } else if (silenceTimeAfternoon.days.equals("1111111")) {
            tvWeekAfternoon.setText(getText(R.string.device_alarm_reset_3));
        } else {
            tvWeekAfternoon.setText(getWeeks(silenceTimeAfternoon));
        }
    }

    private void addAdapterItem(SilenceTime addNewSilenceTime, int index) {

        HashMap<String, Object> map = new HashMap<>();
        setItemAdapterMap(map, addNewSilenceTime);
        listItem.add(index, map);
        mSilenceAdapter.notifyDataSetChanged();
    }

    private void setItemAdapterMap(HashMap<String, Object> map, SilenceTime addNewSilenceTime) {

        if ((Integer.parseInt(addNewSilenceTime.starthour) > Integer.parseInt(addNewSilenceTime.endhour))
                || ((Integer.parseInt(addNewSilenceTime.starthour) == Integer.parseInt(addNewSilenceTime.endhour)) &&
                (Integer.parseInt(addNewSilenceTime.startmin) > Integer.parseInt(addNewSilenceTime.endmin)))) {
            map.put(Const.KEY_MAP_TIME, addNewSilenceTime.starthour + ":" + addNewSilenceTime.startmin + "  " + "-" + "  "
                    + addNewSilenceTime.endhour + ":" + addNewSilenceTime.endmin);
        } else {
            map.put(Const.KEY_MAP_TIME, addNewSilenceTime.starthour + ":" + addNewSilenceTime.startmin + "  " + "-" + "  "
                    + addNewSilenceTime.endhour + ":" + addNewSilenceTime.endmin);
        }
        if (addNewSilenceTime.days.equals("1111100")) {
            map.put(Const.KEY_MAP_TITLE, getResources().getString(R.string.device_alarm_reset_2));
        } else if (addNewSilenceTime.days.equals("1111111")) {
            map.put(Const.KEY_MAP_TITLE, getResources().getString(R.string.device_alarm_reset_3));
        } else {
            map.put(Const.KEY_MAP_TITLE, getWeeks(addNewSilenceTime));
        }
        if (addNewSilenceTime.onoff.equals("1")) {
            map.put(Const.KEY_MAP_IMG, R.drawable.switch_on);
            map.put(Const.KEY_MAP_STATUS, getText(R.string.open));
        } else if (addNewSilenceTime.onoff.equals("0")) {
            map.put(Const.KEY_MAP_IMG, R.drawable.switch_off);
            map.put(Const.KEY_MAP_STATUS, getText(R.string.close));
        }
        map.put(Const.KEY_MAP_SILENCEOBJECT, addNewSilenceTime);
    }

    private void modifyAdapterItem(SilenceTime addNewSilenceTime) {

        setItemAdapterMap(listItem.get(itemClickPosition), addNewSilenceTime);
        mSilenceAdapter.notifyDataSetChanged();
    }

    private void deleteAdapterItem(int position) {

        int size = listItem.size();
        if (size > position) {
            listItem.remove(position);
            mSilenceAdapter.notifyDataSetChanged();
        }
        if (listItem.size() == 0) {
            setSelfSetVisibility(false);
        }
    }

    // get switched on item count,max:Const.MAX_SWTICHED_ON_ALARMCLOCK_COUNT
    private int getSwitchedOnItemCount() {
        int count = 0;
        for (int i = 0; i < listItem.size(); i++) {
            SilenceTime silenceTime = (SilenceTime) listItem.get(i).get(Const.KEY_MAP_SILENCEOBJECT);
            if (silenceTime.onoff.equals("1")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {

        if (btnBack == v) {
            finish();
        } else if (silenceTimeAdd == v) {
            if (getSwitchedOnItemCount() >= 3) {
                ToastUtil.show(SilenceVolumeActivity.this, getString(R.string.silencetime_max_count_prompt));
                return;
            }
            Intent intent = new Intent(SilenceVolumeActivity.this, SilenceModeActivity.class);
            startActivityForResult(intent, 2);//requestCode 2 自定义数据的添加
        } else if (itemMorning == v) {
            Intent intent = new Intent(SilenceVolumeActivity.this, SilenceModeActivity.class);
            intent.putExtra(Const.KEY_MAP_SILENCEOBJECT,silenceTimeMorning);
            startActivityForResult(intent, 3);//requestcode 3 为上午数据的修改
        } else if (itemAfternoon == v) {
            Intent intent = new Intent(SilenceVolumeActivity.this, SilenceModeActivity.class);
            intent.putExtra(Const.KEY_MAP_SILENCEOBJECT,silenceTimeAfternoon);
            startActivityForResult(intent, 4);//requestcode 4 为下午数据的修改
        } else if (btSwitchMorining == v) {
            morAftFlag = -2;
            morAftSwitchClick(1);
        } else if (btSwitchAfternoon == v) {
            morAftFlag = -1;
            morAftSwitchClick(1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 || resultCode == 2) {
            SilenceTime silenceTime = (SilenceTime) data.getSerializableExtra(Const.KEY_MAP_SILENCEOBJECT);
            if (2 == requestCode) { //add new silencetime item
                setSelfSetVisibility(true);
                if (2 == resultCode) {
                    addAdapterItem(silenceTime, listItem.size());
                    itemButtonClickAddPosition = itemButtonClickPosition = listItem.size() - 1;
                    mapSetSilenceListMsg(listItem, itemButtonClickPosition, 3);
                    handleType = 3;
                    ((SilenceTime) listItem.get(itemButtonClickPosition).get(Const.KEY_MAP_SILENCEOBJECT)).onoff = "0";
                    itemButtonClick(itemButtonClickPosition);
                }
            } else if (1 == requestCode) {  //modify the silencetime item
                if (1 == resultCode) {
                    if (silenceTime.onoff.equals("1")) {//on状态的silencetime被修改
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        setItemAdapterMap(map, silenceTime);
                        listItem.remove(itemClickPosition);
                        listItem.add(itemClickPosition, map);
                        mapSetSilenceListMsg(listItem, itemClickPosition, 3);
                        handleType = 3;
                        ((SilenceTime) listItem.get(itemClickPosition).get(Const.KEY_MAP_SILENCEOBJECT)).onoff = "0";
                        itemButtonClick(itemClickPosition);
                    } else if (silenceTime.onoff.equals("0")) {//off状态的silencetime被修改。
                        modifyAdapterItem(silenceTime);
                        saveListToLocal(null);
                    }
                }
            } else if (requestCode == 3) {
                silenceTimeMorning = silenceTime;
                if (silenceTimeMorning.onoff.equals("0")) {
                    morAftFlag = -2;
                    updateMorAftTime();
                    saveListToLocal(null);
                } else if (silenceTimeMorning.onoff.equals("1")) {
                    morAftFlag = -2;
                    silenceTimeMorning.onoff = "0";
                    morAftSwitchClick(3);
                }
            } else if (requestCode == 4) {
                silenceTimeAfternoon = silenceTime;
                if (silenceTimeAfternoon.onoff.equals("0")) {
                    morAftFlag = -1;
                    updateMorAftTime();
                    saveListToLocal(null);
                } else if (silenceTimeAfternoon.onoff.equals("1")) {
                    morAftFlag = -1;
                    silenceTimeAfternoon.onoff = "0";
                    morAftSwitchClick(3);
                }
            }
        }
    }

    /*
   * @param listData:数据源
   * @param position:操作的item的position(0,1,2), 上午 -2，下午 -1
   * @param handleType:操作的类型1(开关),2(删除),3(增加、修改)
   * */
    private void mapSetSilenceListMsg(ArrayList<HashMap<String, Object>> listData, int position, int type) {

        cover.setClickable(true);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        snPosition.put(sn, position);
        JSONArray plA = prepareJsonArray(listData, position, type);
        if (mNetService != null)
            mNetService.sendMapSetMsg(curWatch.getEid(), curWatch.getFamilyId(), sn,
                    CloudBridgeUtil.SILENCE_LIST, plA.toString(), SilenceVolumeActivity.this);
    }

    //待发送的防打扰数据
    private JSONArray prepareJsonArray(ArrayList<HashMap<String, Object>> listData, int position, int type) {

        JSONArray plA = new JSONArray();
        SilenceTime silenceTime;
        if (listData.size() > 0) {
            for (int i = 0; i < listData.size(); i++) {
                if (i != position) {
                    JSONObject plO = new JSONObject();
                    silenceTime = (SilenceTime) listData.get(i).get(Const.KEY_MAP_SILENCEOBJECT);
                    if (silenceTime.onoff.equals("1")) {
                        plO = SilenceTime.toJsonObjectFromSilenceTimeBean(plO, silenceTime);
                        plA.add(plO);
                    }
                }
            }
        }
        if (type == 1 && position >= 0) {
            silenceTime = (SilenceTime) listData.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
            if (silenceTime.onoff.equals("1")) {
                //从开到关的数据不用添加
            } else if (silenceTime.onoff.equals("0")) {//从关到开的数据需要添加
                JSONObject plO = new JSONObject();
                plO = SilenceTime.toJsonObjectFromSilenceTimeBean(plO, silenceTime);
                plO.put(CloudBridgeUtil.ONOFF, "1");
                plA.add(plO);
            }
        } else if (type == 2 && position >= 0) {
            //删除的数据不用添加发送
        } else if (type == 3 && position >= 0) {//修改的话需要将对应的item的数据添加进来
            plA.clear();
            if (listData.size() != 0) {
                for (int i = 0; i < listData.size(); i++) {
                    JSONObject plO = new JSONObject();
                    silenceTime = (SilenceTime) listData.get(i).get(Const.KEY_MAP_SILENCEOBJECT);
                    if (silenceTime.onoff.equals("1")) {
                        plO = SilenceTime.toJsonObjectFromSilenceTimeBean(plO, silenceTime);
                        plA.add(plO);
                    }
                }
            }
        }
        /*
        * 上下午数据加入发送的情况：
        * 1.从关到开（开关位置尚为0）
        * 2.修改开的数据（在onAcitivtyResult中会将开关位置为0）
        * 3.未修改的开的数据
        * */
        if ((position == -2 && silenceTimeMorning.onoff.equals("0")) ||
                (position != -2 && silenceTimeMorning.onoff.equals("1"))) {
            JSONObject plMor = new JSONObject();
            plMor = SilenceTime.toJsonObjectFromSilenceTimeBean(plMor, silenceTimeMorning);
            plMor.put(CloudBridgeUtil.ONOFF, "1");
            plA.add(plMor);
        }
        if ((position == -1 && silenceTimeAfternoon.onoff.equals("0")) ||
                (position != -1 && silenceTimeAfternoon.onoff.equals("1"))) {
            JSONObject plAft = new JSONObject();
            plAft = SilenceTime.toJsonObjectFromSilenceTimeBean(plAft, silenceTimeAfternoon);
            plAft.put(CloudBridgeUtil.ONOFF, "1");
            plA.add(plAft);
        }
        return plA;
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        cover.setClickable(false);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        int sn = (Integer) reqMsg.get(CloudBridgeUtil.KEY_NAME_SN);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                int position = snPosition.get(sn);
                snPosition.remove(sn);
                if (rc > 0) {
                    if (handleType == 1) {
                        if (position >= 0) {
                            itemButtonClickResult(position);
                        } else {
                            morAftSwitchClickResult(position);
                        }
                    } else if (handleType == 2) {
                        deleteAdapterItem(position);
                        saveListToLocal(null);
                    } else if (handleType == 3) {
                        if (position >= 0) {
                            itemButtonClickResult(position);
                            itemButtonClickAddPosition = -1;
                        } else {
                            morAftSwitchClickResult(position);
                        }
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
                if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
                    String sTmp = (String) pl.get(CloudBridgeUtil.SILENCE_LIST);
                    int num = 0;
                    if (sTmp != null && sTmp.length() > 0) {
                        Object obj = JSONValue.parse(sTmp);
                        JSONArray array = (JSONArray) obj;
                        if (array != null) {
                            removeOnSilenceInList();
                            silenceTimeMorning.onoff = "0";
                            silenceTimeAfternoon.onoff = "0";
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject jsonObj = (JSONObject) array.get(i);
                                if ((jsonObj.get(CloudBridgeUtil.TIMESTAMPID)).equals(Const.SILENCE_TIME_MORNING_TIMEID)) {
                                    silenceTimeMorning = SilenceTime.toBeSilenceTimeBean(silenceTimeMorning, jsonObj);
                                } else if ((jsonObj.get(CloudBridgeUtil.TIMESTAMPID)).equals(Const.SILENCE_TIME_AFTERNOON_TIMEID)) {
                                    silenceTimeAfternoon = SilenceTime.toBeSilenceTimeBean(silenceTimeAfternoon, jsonObj);
                                } else {
                                    SilenceTime tmpSilenceTime = new SilenceTime();
                                    tmpSilenceTime = SilenceTime.toBeSilenceTimeBean(tmpSilenceTime, jsonObj);
                                    addAdapterItem(tmpSilenceTime,num);
                                    num++;
                                }
                            }
                        }
                        updateMorAftTime();
                        mSilenceAdapter.notifyDataSetChanged();
                        saveListToLocal(null);
                    }
                } else if (rcMapGet == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    initSaveJsonString();
                } else if (rcMapGet == CloudBridgeUtil.RC_SOCKET_NOTREADY)
                    ToastUtil.showMyToast(this, getString(R.string.set_error8),
                            Toast.LENGTH_SHORT);
            default:
                break;
        }
    }

    private void removeOnSilenceInList() {

        for (int i = 0; i < listItem.size(); i++) {
            SilenceTime ret = (SilenceTime) listItem.get(i).get(Const.KEY_MAP_SILENCEOBJECT);
            if (ret.onoff.equals("1")) {
                listItem.remove(i);
                i = i - 1;
            }
        }
    }

    //设置失败置状态为关闭
    private void backToPreviousState(int position) {

        if (position >= 0) {
            if (position == itemButtonClickAddPosition) { //对于新添加的，如果添加失败，则删除
                deleteAdapterItem(position);
                mSilenceAdapter.notifyDataSetChanged();
                itemButtonClickAddPosition = -1;
                return;
            }
            SilenceTime silenceTime = (SilenceTime) listItem.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
            if (silenceTime.onoff.equals("0")) {
                listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off);
                listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.close));
            } else if (silenceTime.onoff.equals("1")) {
                listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on);
                listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.open));
            }
            mSilenceAdapter.notifyDataSetChanged();
        } else if (position == -2) {
            if (silenceTimeMorning.onoff.equals("0")) {
                tvStatusMorning.setText(getText(R.string.close));
                btSwitchMorining.setBackgroundResource(R.drawable.switch_off);
            } else if (silenceTimeMorning.onoff.equals("1")) {
                tvStatusMorning.setText(getText(R.string.open));
                btSwitchMorining.setBackgroundResource(R.drawable.switch_on);
            }
        } else if (position == -1) {
            if (silenceTimeAfternoon.onoff.equals("0")) {
                tvStatusAfternoon.setText(getText(R.string.close));
                btSwitchAfternoon.setBackgroundResource(R.drawable.switch_off);
            } else if (silenceTimeAfternoon.onoff.equals("1")) {
                tvStatusAfternoon.setText(getText(R.string.open));
                btSwitchAfternoon.setBackgroundResource(R.drawable.switch_on);
            }
        }
    }

    private void itemButtonClickResult(int position) {
        if (listItem.size() == 0)
            return;
        SilenceTime silenceTime = (SilenceTime) listItem.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
        if (silenceTime.onoff.equals("0")) {
            silenceTime.onoff = "1";
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on);
            listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.open));
        } else if (silenceTime.onoff.equals("1")) {
            silenceTime.onoff = "0";
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off);
            listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.close));
        } else if (silenceTime.onoff.equals("-1")) {
            silenceTime.onoff = "0";
        }
        mSilenceAdapter.notifyDataSetChanged();
        saveListToLocal(null);
    }

    private void itemButtonClick(int position) {

        SilenceTime silenceTime = (SilenceTime) listItem.get(position).get(Const.KEY_MAP_SILENCEOBJECT);
        if (silenceTime.onoff.equals("0")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_on_wait);
            listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.opening));
        } else if (silenceTime.onoff.equals("1")) {
            listItem.get(position).put(Const.KEY_MAP_IMG, R.drawable.switch_off_wait);
            listItem.get(position).put(Const.KEY_MAP_STATUS, getText(R.string.closing));
        }
        mSilenceAdapter.notifyDataSetChanged();
    }

    private void morAftSwitchClick(int type) {

        if (morAftFlag == -2) {
            if (silenceTimeMorning.onoff.equals("0")) {
                tvStatusMorning.setText(getText(R.string.opening));
                btSwitchMorining.setBackgroundResource(R.drawable.switch_on_wait);
                mapSetSilenceListMsg(listItem, morAftFlag, type);
            } else if (silenceTimeMorning.onoff.equals("1")) {
                tvStatusMorning.setText(getText(R.string.closing));
                btSwitchMorining.setBackgroundResource(R.drawable.switch_off_wait);
                mapSetSilenceListMsg(listItem, morAftFlag, type);
            }
            if (silenceTimeMorning.days.equals("1111100")) {
                tvWeekMoring.setText(getText(R.string.device_alarm_reset_2));
            } else if (silenceTimeMorning.days.equals("1111111")) {
                tvWeekMoring.setText(getText(R.string.device_alarm_reset_3));
            } else {
                tvWeekMoring.setText(getWeeks(silenceTimeMorning));
            }
        } else if (morAftFlag == -1) {
            if (silenceTimeAfternoon.onoff.equals("0")) {
                tvStatusAfternoon.setText(getText(R.string.opening));
                btSwitchAfternoon.setBackgroundResource(R.drawable.switch_on_wait);
                mapSetSilenceListMsg(listItem, morAftFlag, type);
            } else if (silenceTimeAfternoon.onoff.equals("1")) {
                tvStatusAfternoon.setText(getText(R.string.closing));
                btSwitchAfternoon.setBackgroundResource(R.drawable.switch_off_wait);
                mapSetSilenceListMsg(listItem, morAftFlag, type);
            }
            if (silenceTimeAfternoon.days.equals("1111100")) {
                tvWeekAfternoon.setText(getText(R.string.device_alarm_reset_2));
            } else if (silenceTimeAfternoon.days.equals("1111111")) {
                tvWeekAfternoon.setText(getText(R.string.device_alarm_reset_3));
            } else {
                tvWeekAfternoon.setText(getWeeks(silenceTimeAfternoon));
            }
        }
        handleType = type;
    }

    private void morAftSwitchClickResult(int position) {

        if (position == -2) {
            if (silenceTimeMorning.onoff.equals("0")) {
                silenceTimeMorning.onoff = "1";
            } else if (silenceTimeMorning.onoff.equals("1")) {
                silenceTimeMorning.onoff = "0";
            }
        } else if (position == -1) {
            if (silenceTimeAfternoon.onoff.equals("0")) {
                silenceTimeAfternoon.onoff = "1";
            } else if (silenceTimeAfternoon.onoff.equals("1")) {
                silenceTimeAfternoon.onoff = "0";

            }
        }
        updateMorAftTime();
        saveListToLocal(null);
    }

    private void setSelfSetVisibility(boolean visible) {
        if (visible) {
            silenceTimeRecyclerView.setVisibility(View.VISIBLE);
            divideLine.setVisibility(View.INVISIBLE);
            selfSetView.setVisibility(View.VISIBLE);
            selfSetView_divide.setVisibility(View.VISIBLE);
        } else {
            silenceTimeRecyclerView.setVisibility(View.INVISIBLE);
            divideLine.setVisibility(View.INVISIBLE);
            selfSetView.setVisibility(View.INVISIBLE);
            selfSetView_divide.setVisibility(View.INVISIBLE);
        }
    }

    private String getWeeks(SilenceTime silenceTime) {
        String week = ((silenceTime.days.substring(0, 1).equals("1") ? getText(R.string.week_1) + "" : "") +
                (silenceTime.days.substring(1, 2).equals("1") ? getText(R.string.week_2) + " " : "") +
                (silenceTime.days.substring(2, 3).equals("1") ? getText(R.string.week_3) + " " : "") +
                (silenceTime.days.substring(3, 4).equals("1") ? getText(R.string.week_4) + " " : "") +
                (silenceTime.days.substring(4, 5).equals("1") ? getText(R.string.week_5) + " " : "") +
                (silenceTime.days.substring(5, 6).equals("1") ? getText(R.string.week_6) + " " : "") +
                (silenceTime.days.substring(6, 7).equals("1") ? getText(R.string.week_0) + " " : ""));
        return week;
    }
}
