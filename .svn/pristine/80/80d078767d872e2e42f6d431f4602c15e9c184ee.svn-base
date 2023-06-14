package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.DeviceWifiBean;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangyouyang on 2016/9/12.
 */
public class WatchWifiActivity extends NormalActivity implements View.OnClickListener, MsgCallback {

    private ImageButton mBtnBack;
    private ImageButton mBtnWifiSetting;
    private TextView mTitle;
    private LinearLayout llCurentWifilist;
    private LinearLayout llConnectWifi;
    private TextView tvConnectWifiName;
    private TextView tvConnectWifiState;
    private ListView lvCurrentWifiList;
    private LinearLayout llSavedWifilist;
    private ListView lvSavedWifiList;
    private Button ivRefreshWifiList;
    private Button ivSavedWifiList;
    private ProgressBar animWifilist;
    private ProgressBar animConnectWifi;
    private View layoutBottomControl;

    private ArrayList<DeviceWifiBean> savedWifiList;
    private ArrayList<HashMap<String, Object>> currentWifiList;
    private SavedWifiAdapter savedListAdapter;
    private WifiListAdapter currentListAdapter;

    private int itemClickPosition;
    private int itemLongClickPosition;
    private HashMap<String, Object> connectingWifi;
    private DeviceWifiBean connectWifiBean;

    private static final String OBJECT_WIFIBEAN = "object_wifibean";
    private static final int TYPE_REFRESH_WIFI_LIST = 1;
    private static final int TYPE_SAVED_WIFI_LIST = 2;
    private static final int IS_WIFI_CONNECTING = 101;   //正在连接wifi
    private static final int IS_WIFI_CONNECTED = 102; //连接成功
    private static final int IS_WIFI_CONNECT_FAIL = 103; //连接失败
    private static final int NO_WIFI_CONNECT = 104; //无连接
    private static final int IS_WIFI_DISCONNECTING = 105;   //正在断开连接wifi
    private static final int GET_WIFI_DATA_FAIL = 106; //连接失败

    private Context context;
    private WatchData curWatch;
    private NetService mNetService;
    private BroadcastReceiver mReceiver;
    private LoadingDialog loadingDialog;
    private int enterType = 0;  //进入该设置界面的通道
    private Bundle bundle = new Bundle();

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudBridgeUtil.KEY_DEVICE_WIFI_CMD_QUERY:
                    updateSavedWifiListView((JSONObject) msg.obj);
                    break;
                case CloudBridgeUtil.KEY_DEVICE_WIFI_CMD_DEL:
                    ToastUtil.showMyToast(context, getString(R.string.delete_success), Toast.LENGTH_SHORT);
//                    loadingDialog.dismiss();
                    if (itemLongClickPosition >= 0 && itemLongClickPosition <= savedWifiList.size() - 1)
                        savedWifiList.remove(itemLongClickPosition);
                    itemLongClickPosition = -1;
                    savedListAdapter.notifyDataSetChanged();
                    break;
                case IS_WIFI_CONNECTED:
                    updatellConnectWifiView(true, getString(R.string.connected), curWatch.getDeviceWifiName());
                    animConnectWifi.setVisibility(View.GONE);
//                    connectingWifi = null;
//                    connectWifiBean = null;
                    break;
                case IS_WIFI_CONNECTING:
                    updatellConnectWifiView(true, getString(R.string.wifi_is_connecting), msg.obj.toString());
                    animConnectWifi.setVisibility(View.VISIBLE);
                    if (itemClickPosition == -1 || itemClickPosition >= currentWifiList.size())
                        return;
                    connectingWifi = currentWifiList.get(itemClickPosition);
                    currentWifiList.remove(itemClickPosition);
                    itemClickPosition = -1;
                    currentListAdapter.notifyDataSetChanged();
                    break;
                case IS_WIFI_CONNECT_FAIL:
                    updatellConnectWifiView(false, getString(R.string.connect_wifi_fail), null);
                    if (connectingWifi != null)
                        currentWifiList.add(0, connectingWifi);
                    connectingWifi = null;
                    currentListAdapter.notifyDataSetChanged();
                    connectWifiBean = null;
                    break;
                case NO_WIFI_CONNECT:
                    updatellConnectWifiView(false, "", null);
                    break;
                case IS_WIFI_DISCONNECTING:
                    updatellConnectWifiView(true, getString(R.string.wifi_is_disconnecting), curWatch.getDeviceWifiName());
                    animConnectWifi.setVisibility(View.VISIBLE);
                    break;
                case GET_WIFI_DATA_FAIL:
                    ivRefreshWifiList.setText(getString(R.string.refresh_wifi_list));
                    ivRefreshWifiList.setClickable(true);
                    animWifilist.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_wifi);

        context = this;
        curWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();
        String type = getIntent().getStringExtra("setType");
        if (type != null && type.equals("ximalaya")) {
            enterType = 1;
            bundle = getIntent().getBundleExtra("storyData");
        }
        initViews();
        initData();
        initListener();
        initReceiver();

        e2eGetWifiConnectState();
        e2eGetCurrentWifiList();
//        e2eGetSavedWifiList();

        checkWifiConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        updateAutoConnectState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        mBtnBack = findViewById(R.id.iv_title_back);
        mBtnWifiSetting = findViewById(R.id.iv_title_menu);
        mTitle = findViewById(R.id.tv_title);
        llCurentWifilist = findViewById(R.id.ll_device_current_wifilist);
        llConnectWifi = findViewById(R.id.ll_device_connect_wifi);
        tvConnectWifiName = findViewById(R.id.tv_device_connect_wifi_name);
        tvConnectWifiState = findViewById(R.id.iv_device_connect_wifi_state);
        lvCurrentWifiList = findViewById(R.id.lv_device_current_wifi_list);
        llSavedWifilist = findViewById(R.id.ll_device_saved_wifilist);
        lvSavedWifiList = findViewById(R.id.lv_device_saved_wifi_list);
        ivRefreshWifiList = findViewById(R.id.iv_refresh_wifi_list);
        layoutBottomControl = findViewById(R.id.ll_bottom_control);
        ivSavedWifiList = findViewById(R.id.iv_saved_wifi_list);
        animWifilist = findViewById(R.id.anim_refresh_wifi);
        animConnectWifi = findViewById(R.id.anim_connect_wifi);

        animWifilist.setVisibility(View.VISIBLE);
        ivRefreshWifiList.setText(getString(R.string.refresh_wifi_list_ing));
        ivRefreshWifiList.setClickable(false);

        if (curWatch.isSupportWifiSetting())
            mBtnWifiSetting.setVisibility(View.VISIBLE);
        else
            mBtnWifiSetting.setVisibility(View.GONE);
        mBtnWifiSetting.setBackgroundResource(R.drawable.btn_steps_setting_selector);
    }

    private void initData() {

        mTitle.setText(getText(R.string.setting_watch_wifi));
        currentWifiList = new ArrayList<>();
        addManualItem();
        currentListAdapter = new WifiListAdapter(this, currentWifiList, R.layout.item_device_wifi,
                new String[]{"wifiname", "isfree", "strength"},
                new int[]{R.id.tv_device_wifi_name, R.id.iv_device_wifi_free, R.id.iv_device_wifi_strength});
        lvCurrentWifiList.setAdapter(currentListAdapter);

        savedWifiList = new ArrayList<>();
        savedListAdapter = new SavedWifiAdapter();
        lvSavedWifiList.setAdapter(savedListAdapter);

        loadingDialog = new LoadingDialog(this, R.style.Theme_DataSheet, new LoadingDialog.OnConfirmClickListener() {
            @Override
            public void confirmClick() {
            }
        });
        loadingDialog.hideReloadView();
    }

    private void initReceiver() {

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (Const.ACTION_RECEIVE_DEVICE_WLAN_STATE.equals(intent.getAction())) {
                    if (curWatch.getEid().equals(intent.getStringExtra("eid")) ) {

                        Integer wifiState = Integer.parseInt(intent.getStringExtra(CloudBridgeUtil.KEY_DEVICE_WLAN_STATUS));
                        if ((wifiState & 0x04) == 0x04) {    //if(status&0x04==0x04) connected
                            curWatch.setDeviceWifiName(intent.getStringExtra(CloudBridgeUtil.KEY_DEVICE_WLAN_WIFI_SSID));
                            curWatch.setIsWifiConnect(true);
                            myHandler.sendEmptyMessage(IS_WIFI_CONNECTED);
                        } else {
                            curWatch.setDeviceWifiName("");
                            curWatch.setIsWifiConnect(false);
                            myHandler.sendEmptyMessage(NO_WIFI_CONNECT);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RECEIVE_DEVICE_WLAN_STATE);
        registerReceiver(mReceiver, filter);
    }

    private void initListener() {

        mBtnBack.setOnClickListener(this);
        ivRefreshWifiList.setOnClickListener(this);
        ivSavedWifiList.setOnClickListener(this);
        mBtnWifiSetting.setOnClickListener(this);

        llConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!curWatch.getIsWifiConnect())
                    return ;

                DialogUtil.CustomNormalDialog(context, getString(R.string.prompt), getString(R.string.really_disconnect_wifi), new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, getText(R.string.cancel).toString(), new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        e2eDisconnectWifi();
                        Message msg = Message.obtain();
                        msg.what = IS_WIFI_DISCONNECTING;
                        myHandler.sendMessage(msg);
                    }
                }, getText(R.string.confirm).toString()).show();
            }
        });

        lvCurrentWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (position == (currentWifiList.size() - 1)) {
                    startActivityForResult(new Intent(context, WatchWifiAddActivity.class), 1);
                    return;
                }

                final DeviceWifiBean deviceWifiBean = (DeviceWifiBean) currentWifiList.get(position).get(OBJECT_WIFIBEAN);
                connectWifiBean = deviceWifiBean;

                if (deviceWifiBean.isSaved == 1) {
                    e2eSendWifiMessageToDevice(1, deviceWifiBean.ssid, deviceWifiBean.bssid, "",
                            deviceWifiBean.wifiType, deviceWifiBean.profId);
                    itemClickPosition = position;
                    Message msg = Message.obtain();
                    msg.what = IS_WIFI_CONNECTING;
                    msg.obj = deviceWifiBean.ssid;
                    myHandler.sendMessage(msg);
                    return;
                }

                if (deviceWifiBean.isFree) {
                    //int cmdId,String name, String pwd,int profId
                    e2eSendWifiMessageToDevice(0, deviceWifiBean.ssid, deviceWifiBean.bssid, "", deviceWifiBean.wifiType, 0);
                    itemClickPosition = position;
                    Message msg = Message.obtain();
                    msg.what = IS_WIFI_CONNECTING;
                    msg.obj = deviceWifiBean.ssid;
                    myHandler.sendMessage(msg);
                } else {
                    openInputPwdDialog(deviceWifiBean, position);
                }
            }
        });

        lvSavedWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectWifiBean = savedWifiList.get(position);
                e2eSendWifiMessageToDevice(1, savedWifiList.get(position).ssid, savedWifiList.get(position).bssid,
                        "", savedWifiList.get(position).wifiType,savedWifiList.get(position).profId);
                itemClickPosition = -1;
                switchWifiList(TYPE_REFRESH_WIFI_LIST);

                Message msg = Message.obtain();
                msg.what = IS_WIFI_CONNECTING;
                msg.obj = savedWifiList.get(position).ssid;
                myHandler.sendMessage(msg);
            }
        });
    }

    private void openInputPwdDialog(final DeviceWifiBean deviceWifiBean, final int position) {

        if (deviceWifiBean == null)
            return;

        Dialog dlg = CustomSelectDialogUtil.CustomInputPwdDialog(context,
                32, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
                deviceWifiBean.ssid,
                null, getString(R.string.edit_password_alert), new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
//                        Message msg = Message.obtain();
//                        msg.what = IS_WIFI_CONNECT_FAIL;
//                        msg.obj = deviceWifiBean.ssid;
//                        myHandler.sendMessage(msg);
                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text0) {

                        if (TextUtils.isEmpty(text0)) {
                            ToastUtil.show(context, getString(R.string.edit_password_alert));
                            return;
                        }
                        e2eSendWifiMessageToDevice(0, deviceWifiBean.ssid, deviceWifiBean.bssid, text0, deviceWifiBean.wifiType, 0);
                        itemClickPosition = position;
                        connectWifiBean = deviceWifiBean;
                        Message msg = Message.obtain();
                        msg.what = IS_WIFI_CONNECTING;
                        msg.obj = deviceWifiBean.ssid;
                        myHandler.sendMessage(msg);
                    }
                },
                getText(R.string.connect).toString());
        dlg.show();
    }

    private void checkWifiConnect() {

        if (curWatch.getIsWifiConnect()) {
            Message msg = Message.obtain();
            msg.what = IS_WIFI_CONNECTED;
            myHandler.sendMessage(msg);
        } else {
            Message msg = Message.obtain();
            msg.what = NO_WIFI_CONNECT;
            myHandler.sendMessage(msg);
        }
    }

    private void e2eGetWifiConnectState() {

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    private void e2eGetCurrentWifiList() {

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_DATA;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    private void e2eSendWifiMessageToDevice(int cmdId, String ssid, String bssid, String pwd, int auth_type, long profId) {

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_SEND_WIFI_NAME_AND_PWD;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD, cmdId);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID, ssid);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID, bssid);
        pl.put(CloudBridgeUtil.KEY_WIFI_PWD, pwd);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_TYPE, auth_type);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF, profId);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    private void e2eDisconnectWifi() {

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_DISCONNECT_DEVICE_WIFI;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    private void e2eGetSavedWifiList() {

        JSONObject pl = new JSONObject();
        int subAction = CloudBridgeUtil.SUB_ACTION_SAVE_DEVICE_WIFI_DATA;
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD, 0);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    private void e2eDeleteSavedWifiList(DeviceWifiBean deviceWifiBean) {

        int subAction = CloudBridgeUtil.SUB_ACTION_SAVE_DEVICE_WIFI_DATA;
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, subAction);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD, 2);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF, deviceWifiBean.profId);
        pl.put(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID, deviceWifiBean.bssid);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mNetService != null)
            mNetService.sendE2EMsg(curWatch.getEid(), sn, pl, 60 * 1000, true, WatchWifiActivity.this);
    }

    /**
     * @param isConnect true已连接或正在连接 , else false
     * @param wifiState 要显示的图片id
     * @param wifiName  已连接或正在连接的wifi名
     */
    private void updatellConnectWifiView(boolean isConnect, String wifiState, String wifiName) {

        if (isConnect) {
            llConnectWifi.setVisibility(View.VISIBLE);
            tvConnectWifiState.setText(wifiState);
            tvConnectWifiName.setText(wifiName);
        } else {
            llConnectWifi.setVisibility(View.GONE);
        }
    }

    private void updateCurrentWifiListView(JSONObject pl) {

        //要先清空集合
        currentWifiList.clear();
        ivRefreshWifiList.setText(getString(R.string.refresh_wifi_list));
        ivRefreshWifiList.setClickable(true);
        animWifilist.setVisibility(View.GONE);

        setCurrentWifiList(pl);
        delCurrentConnectWifi();
        addManualItem();
        currentListAdapter.notifyDataSetChanged();
    }

    private void updateSavedWifiListView(JSONObject pl) {

        //要先清空集合
        savedWifiList.clear();

        JSONArray jsonArray = (JSONArray) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_DATA_LIST);
        int length = jsonArray.size();
        DeviceWifiBean wifiBean = null;
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            wifiBean = new DeviceWifiBean();
            wifiBean.ssid = (String) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
            wifiBean.bssid = (String) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID);
            wifiBean.profId = Long.valueOf(jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF) + "");
            savedWifiList.add(wifiBean);
        }
        savedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_title_back:
                if (wifiListType == TYPE_REFRESH_WIFI_LIST) {
                    if(enterType == 1) {
                        Intent _intent = new Intent();
                        _intent.putExtra("bundle",bundle);
                        setResult(0, _intent);
                    }
                    finish();
                }else if (wifiListType == TYPE_SAVED_WIFI_LIST)
                    switchWifiList(TYPE_REFRESH_WIFI_LIST);
                break;

            case R.id.iv_title_menu:
                Intent settingIntent = new Intent(WatchWifiActivity.this, WatchWifiSettingActivity.class);
                settingIntent.putExtra("eid", curWatch.getEid());
                startActivity(settingIntent);
                break;

            case R.id.iv_refresh_wifi_list:
                e2eGetCurrentWifiList();
                switchWifiList(TYPE_REFRESH_WIFI_LIST);
                ivRefreshWifiList.setText(getString(R.string.refresh_wifi_list_ing));
                ivRefreshWifiList.setClickable(false);
                animWifilist.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_saved_wifi_list:
                e2eGetSavedWifiList();
                switchWifiList(TYPE_SAVED_WIFI_LIST);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (wifiListType == TYPE_REFRESH_WIFI_LIST) {
            if(enterType == 1) {
                Intent _intent = new Intent();
                _intent.putExtra("bundle",bundle);
                setResult(0, _intent);
            }
            finish();
        } else if (wifiListType == TYPE_SAVED_WIFI_LIST) {
            switchWifiList(TYPE_REFRESH_WIFI_LIST);
            return;
        }
        super.onBackPressed();
    }

    private int wifiListType = TYPE_REFRESH_WIFI_LIST;

    /**
     * @param type 1附近wifi列表  2保存wifi列表
     */
    private void switchWifiList(int type) {
        wifiListType = type;
        switch (type) {
            case TYPE_REFRESH_WIFI_LIST:
                mTitle.setText(getText(R.string.setting_watch_wifi));
                llCurentWifilist.setVisibility(View.VISIBLE);
                llSavedWifilist.setVisibility(View.GONE);
                layoutBottomControl.setVisibility(View.VISIBLE);
                break;

            case TYPE_SAVED_WIFI_LIST:
                mTitle.setText(getText(R.string.title_saved_wifilist));
                llCurentWifilist.setVisibility(View.GONE);
                llSavedWifilist.setVisibility(View.VISIBLE);
                layoutBottomControl.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_E2E_DOWN:

                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                JSONObject reqPl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                LogUtil.i("HYY " + " WatchWifiActivity respMsg = " + respMsg);
                if (pl == null) {
                    //处理异常弹框提示
                    int tmpRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (tmpRc == CloudBridgeUtil.RC_NETERROR) { //网络连接异常
                        ToastUtil.show(this, getText(R.string.network_error_prompt).toString());
                    } else if (tmpRc == CloudBridgeUtil.ERROR_CODE_E2E_OFFLINE) { //手表不在线
                        ToastUtil.show(this, getText(R.string.watch_offline).toString());
                    } else if (tmpRc == CloudBridgeUtil.RC_TIMEOUT) {  //设置超时
                        ToastUtil.showMyToast(this, getText(R.string.set_timeout).toString(), Toast.LENGTH_LONG);
                    } else if (tmpRc < 0) { //网络不好,有时候手表关机服务器没有返回值,所以也存在手表不在线的可能
                        ToastUtil.show(this, getText(R.string.watch_offline).toString());
                    }
                    //处理界面显示
                    int reqValue = (Integer) reqPl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    if (reqValue == CloudBridgeUtil.SUB_ACTION_SEND_WIFI_NAME_AND_PWD) {
                        Message msg = Message.obtain();
                        msg.what = IS_WIFI_CONNECT_FAIL;
                        myHandler.sendMessage(msg);
                    } else if (reqValue == CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_DATA) {
                        Message msg = Message.obtain();
                        msg.what = GET_WIFI_DATA_FAIL;
                        myHandler.sendMessage(msg);
                    } else if (reqValue == CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE){
                        curWatch.setIsWifiConnect(false);
                        curWatch.setDeviceWifiName("");
                        myHandler.sendEmptyMessage(NO_WIFI_CONNECT);
                    }
                    break;
                }

                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    int value = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                    if (value == CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_STATE) {
                        setDeviceWifiState(pl);
                    } else if (value == CloudBridgeUtil.SUB_ACTION_REQUEST_DEVICE_WIFI_DATA) {
                        updateCurrentWifiListView(pl);
                    } else if (value == CloudBridgeUtil.SUB_ACTION_SEND_WIFI_NAME_AND_PWD) {
                        JSONObject reqJsonObject = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String ssid = (String) reqJsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
                        dealWifiConnectResult(ssid, pl);
                    } else if (value == CloudBridgeUtil.SUB_ACTION_DISCONNECT_DEVICE_WIFI) {
                        dealWifiDisConnectResult(pl);
                    } else if (value == CloudBridgeUtil.SUB_ACTION_SAVE_DEVICE_WIFI_DATA) {
                        int cmdId = (Integer) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_CMD);
                        if (cmdId == CloudBridgeUtil.KEY_DEVICE_WIFI_CMD_QUERY) {   //query
                            updateSavedWifiListView(pl);
                        } else if (cmdId == CloudBridgeUtil.KEY_DEVICE_WIFI_CMD_DEL) {    //del
                            Message msg = Message.obtain();
                            msg.what = CloudBridgeUtil.KEY_DEVICE_WIFI_CMD_DEL;
                            myHandler.handleMessage(msg);
                        }
                    }
                } else if (rc < 0) {
                    //根据调试，存在PL不为null但rc<0的情况
                    ToastUtil.showMyToast(this, getText(R.string.set_error).toString(), Toast.LENGTH_SHORT);
                }
                break;

            default:
                break;
        }
    }

    private void dealWifiConnectResult(String wifiname, JSONObject pl) {

        int result = (Integer) pl.get("result");
        if (result == 0) {  //为0表连接成功，但可能存在设备没有登录成功，因此不以这个为准。以收到设备上报的mapset消息为准。

            //将结果设置给WatchData
            curWatch.setDeviceWifiName(wifiname);
            curWatch.setIsWifiConnect(true);
            myHandler.sendEmptyMessage(IS_WIFI_CONNECTED);
        } else if (result == 1) {

            int cause = (Integer) pl.get("cause");
            switch (cause) {
                case 0:
                    ToastUtil.showMyToast(this, getString(R.string.connect_wifi_error), Toast.LENGTH_SHORT);
                    break;
                case 4:
                    ToastUtil.showMyToast(this, getString(R.string.connect_wifi_error_pwd), Toast.LENGTH_SHORT);
                    openInputPwdDialog(connectWifiBean,itemClickPosition);
                    //这种情况下直接return
                    return;
                case 8:
                    ToastUtil.showMyToast(this, getString(R.string.connect_wifi_error_signal), Toast.LENGTH_SHORT);
                    break;
                default:
                    ToastUtil.showMyToast(this, getString(R.string.connect_wifi_error_unkown), Toast.LENGTH_SHORT);
                    break;
            }
            Message msg = Message.obtain();
            msg.what = IS_WIFI_CONNECT_FAIL;
            msg.obj = wifiname;
            myHandler.sendMessage(msg);
        } else {
            ToastUtil.showMyToast(this, getString(R.string.connect_wifi_error_unkown), Toast.LENGTH_SHORT);
        }
    }

    private void dealWifiDisConnectResult(JSONObject pl) {

        int result = (Integer) pl.get("result");
        if (result == 0) {
            ToastUtil.showMyToast(this, getString(R.string.disconnect_wifi_success), Toast.LENGTH_SHORT);
            //llConnectDeviceWifi gone
            myHandler.sendEmptyMessage(NO_WIFI_CONNECT);
            //将结果设置给WatchData
            curWatch.setDeviceWifiName("");
            curWatch.setIsWifiConnect(false);
        } else {
            ToastUtil.showMyToast(this, getString(R.string.disconnect_wifi_fail), Toast.LENGTH_SHORT);
        }
    }

    private void setCurrentWifiList(JSONObject pl) {

        JSONArray jsonArray = (JSONArray) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_DATA_LIST);
        int length = jsonArray.size();
        HashMap<String, Object> map;
        DeviceWifiBean wifiBean;
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            wifiBean = new DeviceWifiBean();
            wifiBean.ssid = (String) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
            wifiBean.bssid = (String) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID);
            wifiBean.isFree = !(Boolean) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_ISFREE);
            if (jsonObject.containsKey(CloudBridgeUtil.KEY_DEVICE_WIFI_IS_SAVED))
                wifiBean.isSaved = (int) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_IS_SAVED);
            if (jsonObject.containsKey(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF))
                wifiBean.profId = Long.valueOf(jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF) + "");
            Integer strength = (Integer) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_STRENGTH);
            //设置wifi强度
            int beanStrength;
            if (strength > -40) {
                beanStrength = 5;
            } else if (strength > -60) {
                beanStrength = 4;
            } else if (strength > -70) {
                beanStrength = 3;
            } else if (strength > -80) {
                beanStrength = 2;
            } else {
                beanStrength = 1;
            }
            wifiBean.strength = beanStrength;
            wifiBean.wifiType = (Integer) jsonObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_TYPE);
            //设置wifi是否显示  只显示某些类型的wifi
            switch (wifiBean.wifiType) {
                case 0:
                case 1:
                case 4:
                case 9:
                case 10:
                    wifiBean.isWifiShow = true;
                    break;
                default:
                    wifiBean.isWifiShow = false;
                    break;
            }

            //只将允许显示的wifi添加到集合中
            if (wifiBean.isWifiShow) {
                map = new HashMap<>();
                map.put(OBJECT_WIFIBEAN, wifiBean);
                //"wifiname", "isfree", "strength"
                map.put("wifiname", wifiBean.ssid);
                map.put("strength", wifiBean.strength);
                map.put("isfree", wifiBean.isFree);
                currentWifiList.add(map);
            }
        }
    }

    private void delCurrentConnectWifi() {

        if (!curWatch.getIsWifiConnect())
            return;

        int position = 0;
        for (int i = 0; i < currentWifiList.size(); i++) {
            DeviceWifiBean wifiBean = (DeviceWifiBean) currentWifiList.get(i).get(OBJECT_WIFIBEAN);
            if (wifiBean.ssid.equals(curWatch.getDeviceWifiName())) {
                position = i;
                break;
            }
        }
        if (currentWifiList.size() > position)
            currentWifiList.remove(position);
    }

    private void addManualItem() {

        //添加wifi
        HashMap<String, Object> map = new HashMap<>();
        map.put(OBJECT_WIFIBEAN, new DeviceWifiBean());
        map.put("wifiname", getString(R.string.manual_add_wifi));
        map.put("strength", -1);
        map.put("isfree", true);
        currentWifiList.add(map);
    }

    private void setDeviceWifiState(JSONObject pl) {

        Integer wifiState = (Integer) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_STATE);
        if ((wifiState & 0x04) == 0x04) {    //if(status&0x04==0x04) connected
            curWatch.setIsWifiConnect(true);
            curWatch.setDeviceWifiName((String) pl.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID));
            myHandler.sendEmptyMessage(IS_WIFI_CONNECTED);
        } else {
            curWatch.setIsWifiConnect(false);
            curWatch.setDeviceWifiName("");
            myHandler.sendEmptyMessage(NO_WIFI_CONNECT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String wifiName = data.getStringExtra("wifiname");
            String wifiPwd = data.getStringExtra("wifipwd");
            e2eSendWifiMessageToDevice(0, wifiName, "", wifiPwd, 9, 0);//auth_type默认值填9
            itemClickPosition = -1;
            Message msg = Message.obtain();
            msg.what = IS_WIFI_CONNECTING;
            msg.obj = wifiName;
            myHandler.sendMessage(msg);
            llConnectWifi.setVisibility(View.VISIBLE);
        }
    }

    private class SavedWifiAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return savedWifiList.size();
        }

        @Override
        public Object getItem(int position) {
            return savedWifiList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            SaveWifiViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_device_saved_wifi, null);
                vh = new SaveWifiViewHolder();
                vh.tvWifiName = convertView.findViewById(R.id.tv_device_wifi_name);
                vh.ivEditWifi = convertView.findViewById(R.id.iv_edit_wifi);
                vh.ivDeleteWifi = convertView.findViewById(R.id.iv_delete_wifi);
                convertView.setTag(vh);
            } else {
                vh = (SaveWifiViewHolder) convertView.getTag();
            }
            //wifiname
            String wifiName = savedWifiList.get(position).ssid;
            vh.tvWifiName.setText(wifiName);
            //edit wifi
            vh.ivEditWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position >= savedWifiList.size())
                        return;
                    Intent intent = new Intent(WatchWifiActivity.this, WatchWifiAddActivity.class);
                    intent.putExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_PROF, savedWifiList.get(position).profId);
                    intent.putExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID, savedWifiList.get(position).ssid);
                    intent.putExtra(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID, savedWifiList.get(position).bssid);
                    startActivity(intent);
                }
            });
            //delete wifi
            vh.ivDeleteWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position >= savedWifiList.size())
                        return;
                    DialogUtil.CustomNormalDialog(context, getString(R.string.delete_saved_wifi), getString(R.string.really_delete) + savedWifiList.get(position).ssid + "？", new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, getText(R.string.cancel).toString(), new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            // 添加数据长度保护。因为删除wifi是e2e消息，回复较慢，可能会导致数据不同步问题
                            if (position >= savedWifiList.size())
                                return;
                            e2eDeleteSavedWifiList(savedWifiList.get(position));
                            itemLongClickPosition = position;
                            ToastUtil.show(WatchWifiActivity.this,getString(R.string.deleting));
//                            if (!loadingDialog.isShowing()) {
//                                loadingDialog.changeStatus(1, getString(R.string.delete_saved_wifi_ing));
//                                loadingDialog.show();
//                            }
                        }
                    }, getText(R.string.confirm).toString()).show();
                }
            });
            return convertView;
        }
    }

    class SaveWifiViewHolder {

        TextView tvWifiName;
        ImageView ivEditWifi;
        ImageView ivDeleteWifi;
    }

    private class WifiListAdapter extends SimpleAdapter {

        WifiListAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                        String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DeviceWifiViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_device_wifi, null);
                vh = new DeviceWifiViewHolder();
                vh.tvWifiName = convertView.findViewById(R.id.tv_device_wifi_name);
                vh.ivWifiFree = convertView.findViewById(R.id.iv_device_wifi_free);
                vh.ivWifiStrength = convertView.findViewById(R.id.iv_device_wifi_strength);
                convertView.setTag(vh);
            } else {
                vh = (DeviceWifiViewHolder) convertView.getTag();
            }

            //wifiname
            String wifiName = (String) currentWifiList.get(position).get("wifiname");
            vh.tvWifiName.setText(wifiName);
            //isFree
            boolean isFree = (boolean) currentWifiList.get(position).get("isfree");
            showImageViewByIsFree(vh.ivWifiFree, isFree);
            //strength
            int strength = (int) currentWifiList.get(position).get("strength");
            showImageViewByStrength(vh.ivWifiStrength, strength);
            return convertView;
        }
    }

    class DeviceWifiViewHolder {

        TextView tvWifiName;
        ImageView ivWifiFree;
        ImageView ivWifiStrength;
    }

    private void showImageViewByIsFree(ImageView imageView, boolean isFree) {
        if (isFree) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void showImageViewByStrength(ImageView imageView, int strength) {
        switch (strength) {
            case -1:
                imageView.setImageResource(R.drawable.btn_next_selector);
                break;
            case 1:
                imageView.setImageResource(R.drawable.watch_wifi_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.watch_wifi_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.watch_wifi_2);
                break;
            case 4:
                imageView.setImageResource(R.drawable.watch_wifi_3);
                break;
            case 5:
                imageView.setImageResource(R.drawable.watch_wifi_3);
                break;
            default:
                imageView.setImageResource(R.drawable.watch_wifi_1);
                break;
        }
    }
}
