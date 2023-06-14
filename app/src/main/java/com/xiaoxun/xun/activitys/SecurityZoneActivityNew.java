/**
 * Creation Date:2015-2-3
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.xiaoxun.mapadapter.utils.CoordinateConvertUtils;
import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.EFence;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.GoogleMapUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.SecurityZone;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.WatchWifiUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 类名称：SecurityZoneActivity
 * 修改人：zhangjun5
 * 修改时间：2015/11/11 9:52
 * 方法描述：安全区域首页，添加安全区域和设置是否开关。
 * <p>
 * 修改描述：增加空页面处理和安全区域，修改ui。添加地图预览截图。
 * 添加逆地址编码。
 */
public class SecurityZoneActivityNew extends NormalActivity implements OnClickListener, MsgCallback,
        LoadingDialog.OnConfirmClickListener {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x14;
    private static final int REQUEST_CODE_FAMILYWIFI = 0x15;
    public static String lOGTAG = "SecurityZoneActivity";

    private WatchData curWatch;//当前设置的watch
    private ImageButton mBtnBack;
    private View mSecurityAddBtn;
    private LinearLayout layout_save_power_state;
    private TextView watch_state;
    private Button btn_finish;
    private View cover;
    private int callbackType = 0;  //删除分为长按删除和关闭删除：关闭在本地保留数据，服务器端删除

    ArrayList<HashMap<String, Object>> listItem;
    private ListView securityZoneListView;
    private MySimpleAdapter adapter;
    ImibabyApp myApp;
    private HashMap<String, Object> loaderPreViewList = new HashMap<String, Object>();

    private LoadingDialog loadingdlg;
    private boolean isFirstSet = false;
    private NetService mNetService;
    private String familySsid = "", familyBssid = "";

    boolean school_guard_enable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (ImibabyApp) getApplication();
        curWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();
        setContentView(R.layout.security_zone_activity_new);
//        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.schedule_no_class));
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.normal_area);
        String enter = null;
        String eid_firstSet = null;
        enter = getIntent().getStringExtra("enter");
        eid_firstSet = getIntent().getStringExtra(CloudBridgeUtil.KEY_NAME_EID);
        if (enter != null && enter.equals("first_set")) {
            isFirstSet = true;
            if (!eid_firstSet.equals(""))
                curWatch = myApp.getCurUser().queryWatchDataByEid(eid_firstSet);
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.security_zone_guidance);
        }
        //延时对话框
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
        loadingdlg.setCancelable(false);
        //初始化adapter
        initAdapterList();
        initViews();//初始化视图
        if (!isFirstSet) { //如果为第一次设置，去掉本地获取安全列表的选项。
            getListItemFromLocalJason();//从本地获取安全列表
            getListItemFromCloudBridge();//从网络刷新安全列表。
        } else {
            mBtnBack.setVisibility(View.INVISIBLE);//隐藏掉返回按钮。
        }
        if (listItem.size() <= 0) {
            //当没有数据时候的默认值  首次绑定之后的引导界面使用
            initDefaultList();
            saveListItemToLocalJason();
        }
        reqFamilyWifi();
        getGuardListFromCloud();

        //1:隐私权限申请
//        PermissionUtils.checkInitPermission(myApp, this);
    }

    private void reqFamilyWifi() {
        WatchWifiUtils.getFamilyWifi(SecurityZoneActivityNew.this, curWatch.getEid(), mNetService, new WatchWifiUtils.OperationCallback() {
            @Override
            public void onSuccess(Object result) {

                try {
                    JSONObject respMsg = (JSONObject) result;
                    familySsid = (String) ((JSONObject) ((JSONArray) respMsg.get("WIFIS")).get(0)).get("ssid");
                    familyBssid = (String) ((JSONObject) ((JSONArray) respMsg.get("WIFIS")).get(0)).get("bssid");
                    getMyApp().setValue(HomeWiFiSettingActivity.HOMEWIFI_SSID, familySsid);
                    getMyApp().setValue(HomeWiFiSettingActivity.HOMEWIFI_BSSID, familyBssid);
                } catch (Exception e) {

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isFirstSet) {
            super.onBackPressed();
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:13
     * 方法描述：初始化安全区域的adapter，实现adapter内的点击事件。
     * <p>
     * 修改描述：删除时去掉默认引导界面。
     */
    private void initAdapterList() {
        //初始化 adapter , SecurityZonelistView
        listItem = new ArrayList<>();
        adapter = new MySimpleAdapter(this, listItem, R.layout.sample_adapter_item_new,
                new String[]{"logImg", "title", "info", "img"},
                new int[]{R.id.log_img, R.id.title, R.id.info, R.id.button_img});
        securityZoneListView = (ListView) findViewById(R.id.security_zone_list);
        securityZoneListView.setAdapter(adapter);
        securityZoneListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!PermissionUtils.hasPermissions(SecurityZoneActivityNew.this, PermissionUtils.storagePermissions)) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.no_storage_permission_tips),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!myApp.isMeAdmin(curWatch)) {
                    ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                    return;
                }
                SecurityZone securityZone;
                securityZone = (SecurityZone) listItem.get(position).get("securityObject");
                Intent intent = new Intent();
                intent.setClass(SecurityZoneActivityNew.this, SecurityZoneSettingGoogle.class);
                Bundle bundle = new Bundle();
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_NAME, securityZone.sName);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_RADIUS,
                        String.valueOf(securityZone.sRadius));
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER, securityZone.sCenter);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD, securityZone.sCenterBD);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_ONOFF, securityZone.onOff);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_EFID, securityZone.keyEFID);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO, securityZone.info);
                bundle.putString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW, securityZone.preview);
                intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
                intent.putExtra("inzone", bundle);
                startActivityForResult(intent, 1);

                myApp.sdcardLog("securityzone Activity to settings1:" + bundle.toString() + ":" + position + ":");
            }
        });

//        securityZoneListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
//            position, long id) {
//
//                SecurityZone securityZone = (SecurityZone) listItem.get(position).get
//                ("securityObject");
//
//                showDeleteOp(position, securityZone);
//                return true;
//            }
//        });
    }

    private void showDeleteOp(final int position, SecurityZone securityZone) {
        if (!"EFID1".equals(securityZone.keyEFID) && !"EFID2".equals(securityZone.keyEFID)) {
            Dialog dlg = DialogUtil.CustomNormalDialog(SecurityZoneActivityNew.this,
                    getText(R.string.device_szone_delete_title).toString(),
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
                            SecurityZone securityZone = new SecurityZone();
                            securityZone = (SecurityZone) listItem.get(position).get(
                                    "securityObject");
                            if (securityZone.onOff.equals("1")) {
                                if (loadingdlg != null && !loadingdlg.isShowing()) {
                                    loadingdlg.enableCancel(false);
                                    loadingdlg.changeStatus(1,
                                            getResources().getString(R.string.synch_szone_message));
                                    loadingdlg.show();
                                }
                                sendWatchEFenceMsg(securityZone, securityZone.info, "0", 3);
                            } else {
                                deleteAdapterItem(position);
                                saveListItemToLocalJason();
                            }
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        }
    }

    private void addDefaultSecurity(String efid) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        SecurityZone securityZone = new SecurityZone();
        if (efid.equals("EFID1")) {
            securityZone.sName = getResources().getString(R.string.security_zone_home);
            securityZone.sCenter = getResources().getString(R.string.security_zone_default_home);
            securityZone.sCenterBD = getResources().getString(R.string.security_zone_default_home);
        } else if (efid.equals("EFID2")) {
            securityZone.sName = getResources().getString(R.string.security_zone_school);
            securityZone.sCenter = getResources().getString(R.string.security_zone_default_school);
            securityZone.sCenterBD =
                    getResources().getString(R.string.security_zone_default_school);
        }
        securityZone.sRadius = 500;
        securityZone.onOff = "0";
        securityZone.keyEFID = efid;
        securityZone.info = getResources().getString(R.string.security_zone_default_info);
        securityZone.preview = getResources().getString(R.string.security_default);
        setItemAdapterMap(map, securityZone);
        listItem.add(map);
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/16 11:17
     * 方法描述：用于初始化当本地数据和网络数据为0时候的，初始化操作。添加家和学校的默认设置。
     */
    private void initDefaultList() {
        addDefaultSecurity("EFID1");
        addDefaultSecurity("EFID2");
        adapter.notifyDataSetChanged();
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:05
     * 方法描述：返回，安全区域添加按钮和加载页面的初始化。
     */
    private void initViews() {
        mBtnBack = (ImageButton) findViewById(R.id.iv_title_back);
        layout_save_power_state = (LinearLayout) findViewById(R.id.layout_save_power_state);
        watch_state = (TextView) findViewById(R.id.watch_state);
        mBtnBack.setOnClickListener(this);
        mSecurityAddBtn = (View) findViewById(R.id.security_add_btn);
        mSecurityAddBtn.setClickable(true);
        mSecurityAddBtn.setOnClickListener(this);

        btn_finish = (Button) findViewById(R.id.btn_next_step);
        btn_finish.setOnClickListener(this);
        cover = findViewById(R.id.cover);
        if (isFirstSet) {
            btn_finish.setVisibility(View.VISIBLE);
            (findViewById(R.id.security_add_btn)).setVisibility(View.GONE);
            btn_finish.setText(R.string.security_zone_next);
        }
    }

    private int findPositionByEfid(String Efid) {
        int retPosition = -1;
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            if (Efid.equals(securityZone.keyEFID)) {
                retPosition = i;
                break;
            }
        }
        return retPosition;
    }

    private void saveListItemToLocalJason() {
        JSONObject pl = new JSONObject();
        JSONArray arr = new JSONArray();
        pl.put("list", arr);
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            JSONObject item = new JSONObject();
            item.put("Name", securityZone.sName);
            item.put("Center_amap", securityZone.sCenter);
            item.put("Radius", securityZone.sRadius);
            item.put("Onoff", securityZone.onOff);
            item.put("Efid", securityZone.keyEFID);
            item.put("Info", securityZone.info);
            item.put("Preview", securityZone.preview);
            item.put("Center_bd", securityZone.sCenterBD);
            item.put("Coordinate", securityZone.sCoordinate);
            arr.add(item);
        }
        myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY,
                pl.toJSONString());
    }

    /**
     * user:zhangjun5 time:14:23 date:2017/6/21
     * desc:计算开启的安全区域的数量
     **/
    private int getSwitchedOnItemCount() {
        int count = 0;
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = new SecurityZone();
            securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            if (securityZone.onOff.equals("1")) {
                count++;
            }
        }
        LogUtil.d(lOGTAG + "  " + "switched on count: " + count);
        return count;
    }

    private void getListItemFromLocalJason() {
        String data =
                myApp.getStringValue(curWatch.getEid() + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY
                        , "");
        if (data != null && !data.equals("")) {
            JSONObject pl = (JSONObject) JSONValue.parse(data);
            JSONArray arr = (JSONArray) pl.get("list");
            if (arr != null && arr.size() > 0) {
                boolean isEfid1 = false;
                boolean isEfid2 = false;
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject item = (JSONObject) arr.get(i);
                    SecurityZone securityZone = new SecurityZone();
                    securityZone.sName = (String) item.get("Name");
                    securityZone.sCenter = (String) item.get("Center_amap");
                    securityZone.sRadius = (Integer) item.get("Radius");
                    if (securityZone.sRadius >= Const.MAX_SECURITY_ZONE_RADIA) {
                        securityZone.sRadius = Const.MAX_SECURITY_ZONE_RADIA;
                    }
                    securityZone.onOff = (String) item.get("Onoff");
                    securityZone.keyEFID = (String) item.get("Efid");
                    if (securityZone.keyEFID.equals("EFID1")) {
                        isEfid1 = true;
                    }
                    if (securityZone.keyEFID.equals("EFID2")) {
                        isEfid2 = true;
                    }
                    securityZone.info = (String) item.get("Info");
                    securityZone.preview = (String) item.get("Preview");
                    securityZone.sCenterBD = (String) item.get("Center_bd");
                    securityZone.sCoordinate = (String) item.get("Coodrinate");

                    int efInt = Integer.parseInt(securityZone.keyEFID.substring(4, 5));
                    if (efInt > Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
                        continue;
                    }
                    addAdapterItem(securityZone);
                }

                if (!isEfid1) {
                    addDefaultSecurity("EFID1");
                }
                if (!isEfid2) {
                    addDefaultSecurity("EFID2");
                }
            }
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:27
     * 方法描述：从网络端获取到安全区域的同步数据。
     */
    private void getListItemFromCloudBridge() {

        MyMsgData eFenceMsg = new MyMsgData();
        eFenceMsg.setCallback(SecurityZoneActivityNew.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
        eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_GET,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                myApp.getToken(), pl));

        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(eFenceMsg);
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:22
     * 方法描述：把一个安全区域对象转化为adapter的数据对象
     */
    private void setItemAdapterMap(HashMap<String, Object> map, SecurityZone securityZone) {
        if (securityZone.keyEFID.equals("EFID1")) {
            map.put("logImg", R.drawable.icon_home_yellow);
        } else if (securityZone.keyEFID.equals("EFID2")) {
            map.put("logImg", R.drawable.icon_school_yellow);
        } else {
            map.put("logImg", R.drawable.customizel_2);
        }
        if (securityZone.preview.equals(getResources().getString(R.string.security_default))) {
            map.put("title", securityZone.sCenter);
        } else {
            map.put("title",
                    securityZone.sName + " " + getText(R.string.radius) + securityZone.sRadius + getText(R.string.unit_meter));
        }

        map.put("info", securityZone.info);
        if (securityZone.onOff.equals("1"))
            map.put("img", R.drawable.toggle_selector_on);
        else
            map.put("img", R.drawable.toggle_selector_off);
        if ("".equals(securityZone.preview) || securityZone.preview.equals(getResources().getString(R.string.security_default))) {
            if (securityZone.sName.equals(this.getResources().getString(R.string.security_zone_home))) {
                map.put("preview", R.drawable.security_default_home);
            } else if (securityZone.sName.equals(this.getResources().getString(R.string.security_zone_school))) {
                map.put("preview", R.drawable.security_default_school);
            } else {
                map.put("preview", R.drawable.security_default);
            }
        } else {
            map.put("preview", securityZone.preview);
        }
        map.put("securityObject", securityZone);
    }

    private void removeAdapterOnItem() {
        //删除开启的卡片数据
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = new SecurityZone();
            securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            if (securityZone.keyEFID.equals("EFID1") ||
                    securityZone.keyEFID.equals("EFID2")) {
                securityZone.onOff = "0";
                listItem.remove(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                setItemAdapterMap(map, securityZone);
                listItem.add(i, map);
                continue;
            }
            if (securityZone.keyEFID.length() >= 5) {
                int efInt = Integer.parseInt(securityZone.keyEFID.substring(4, 5));
                if (efInt > Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
                    listItem.remove(i);
                    i--;
                    continue;
                }
            }
            if (securityZone.onOff.equals("1")) {
                listItem.remove(i);
                i--;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:24
     * 方法描述：给列表添加安全区域数据信息
     */
    private void addAdapterItem(SecurityZone securityZone) {
        HashMap<String, Object> map = new HashMap<>();
        setItemAdapterMap(map, securityZone);
        listItem.add(map);
        adapter.notifyDataSetChanged();
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/20 11:25
     * 方法描述：删除操作，长按删除listview中的视图。分网络和本地删除：如果该安全区域开启，则使用网络删除
     * 如果未开启，则直接删除
     */
    private void deleteAdapterItem(int itemPosition) {
        if (itemPosition < 0) {
            return;
        }
        int size = listItem.size();
        if (size > itemPosition) {
            listItem.remove(itemPosition);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/2 16:29
     * 方法描述：当安全区域的efid大于MAX_SWTICHED_ON_SECURITY_ZONE_COUNT之后的区域，全部不要。
     */
    private void removeZoneMoreThanFive(ArrayList<SecurityZone> securityList) {
        Iterator itr = securityList.iterator();
        while (itr.hasNext()) {
            SecurityZone zone = (SecurityZone) itr.next();
            if (zone.keyEFID.length() >= 5) {
                int efInt = Integer.parseInt(zone.keyEFID.substring(4, 5));
                if (efInt > Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
                    itr.remove();
                }
            }
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/2 17:06
     * 方法描述：对于网络传送过来的数据中，覆盖掉相同efid的数据
     */
    private void coverSameSecurityZone(ArrayList<SecurityZone> securityList) {
        for (SecurityZone zone : securityList) {
            for (int i = 0; i < listItem.size(); i++) {
                SecurityZone itemZone = (SecurityZone) listItem.get(i).get("securityObject");
                if (itemZone.keyEFID.equals(zone.keyEFID)) {
                    listItem.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/2 19:51
     * 方法描述：对于安全区域进行排序。排序方式按照：efid1，efid2，efid3........
     */
    private void securityZoneListSort() {
        ArrayList<HashMap<String, Object>> sortList = new ArrayList<HashMap<String, Object>>();
        String key = "EFID";
        for (int i = 1; i <= Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT; i++) {
            String tmpKey = key + Integer.toString(i);
            for (int j = 0; j < listItem.size(); j++) {
                SecurityZone sZone = (SecurityZone) listItem.get(j).get("securityObject");
                if (sZone.keyEFID.equals(tmpKey)) {
                    sortList.add(listItem.get(j));
                }
            }
        }
        listItem.clear();
        listItem.addAll(sortList);
    }

    private String getKeyEFID() {
        String key = "EFID";
        for (int i = 3; i <= Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT; i++) {
            boolean hasKey = false;
            String tmpKey = key + Integer.toString(i);
            for (int j = 0; j < listItem.size(); j++) {
                SecurityZone securityTmp = (SecurityZone) listItem.get(j).get("securityObject");
                String comKey = securityTmp.keyEFID;
                if (tmpKey.equals(comKey)) {
                    hasKey = true;
                    break;
                }
            }
            if (!hasKey) {
                key = tmpKey;
                break;
            }
        }
        return key;
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/25 16:32
     * 方法描述：标准化上传文件名。
     */
    private String picIdFormat(String source) {
        String fileid = source;
        fileid = fileid.replace("lat/lng:", "");
        fileid = fileid.replace(" ", "");
        fileid = fileid.replace("(", "");
        fileid = fileid.replace(")", "");
        fileid = fileid.replace(".", "");
        fileid = fileid.replace(",", "");

        return fileid;
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/12/25 16:33
     * 方法描述：兼容ios的lat和lng为小数点后14的文件下载名问题
     */
    private String picIdForMatForLoad(String source) {
        String fileid = source;
        fileid = fileid.replace(" ", "");
        fileid = fileid.replace(".", "");
        fileid = fileid.replace(",", "");

        return fileid;
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/17 14:51
     * 方法描述：操作完成之后的返回事件。resultcode：1:修改返回标志；  2：新建返回标志
     * requestCode：1：修改需求标志； 2：新建需求标志
     * 修改描述：增加对于地图预览功能的添加，和默认地图预览功能的设置。
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myApp.sdcardLog("securityzone Activity  requestCode:" + requestCode + "  resultCode:" + resultCode
                + "  listItem.size():" + listItem.size());
        if (resultCode == 1 || resultCode == 2 || resultCode == 3) {
            Bundle bundle = data.getBundleExtra("outzone");
            SecurityZone securityZone = new SecurityZone();
            securityZone.sRadius =
                    Integer.valueOf(bundle.getString(CloudBridgeUtil.SECURITY_ZONE_RADIUS));
            securityZone.sCenter = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_CENTER);
            securityZone.sName = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_NAME);
            securityZone.info = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO, "default");
            securityZone.preview = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW);
            securityZone.sCenterBD = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD);
            myApp.sdcardLog("securityzone Activity:" + bundle.toString());
            //上传图片信息
            sendPicToServer(securityZone.preview, picIdFormat(securityZone.sCenter));

            if (2 == requestCode) { //添加安全区域，默认开启。
                if (2 == resultCode) {
                    securityZone.onOff = "0";
                    securityZone.keyEFID = getKeyEFID();
                    //添加异常检测
                    if (listItem.size() >= Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
                        ToastUtil.showMyToast(SecurityZoneActivityNew.this,
                                getString(R.string.security_zone_max_count_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        sendWatchEFenceMsg(securityZone, securityZone.info, "1", 1);
                        addAdapterItem(securityZone);
                        saveListItemToLocalJason();
                        securityZoneListView.smoothScrollToPosition(listItem.size() - 1);
                        if (loadingdlg != null && !loadingdlg.isShowing()) {
                            loadingdlg.enableCancel(false);
                            loadingdlg.changeStatus(1,
                                    getResources().getString(R.string.synch_szone_message));
                            loadingdlg.show();
                        }
                    }
                }
            } else if (1 == requestCode) {  //modify the security zone item
                securityZone.keyEFID = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_EFID);
                securityZone.onOff = bundle.getString(CloudBridgeUtil.SECURITY_ZONE_ONOFF);
                int itemPosition = findPositionByEfid(securityZone.keyEFID);
                myApp.sdcardLog("securityzone Activity position:" + securityZone.keyEFID + ":" + itemPosition);
                if (itemPosition == -1) {
                    return;
                }
                if (1 == resultCode) {
                    familySsid = data.getStringExtra("ssid");
                    familyBssid = data.getStringExtra("bssid");
                    if (securityZone.onOff.equals("1")) {//开启的安全区域的修改
                        listItem.remove(itemPosition);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        setItemAdapterMap(map, securityZone);
                        listItem.add(itemPosition, map);
                        adapter.notifyDataSetChanged();
                        securityZone.onOff = "0";
//						securityZone.keyEFID = getKeyEFID();
                        sendWatchEFenceMsg(securityZone, securityZone.info, "1", 1);
                        if (loadingdlg != null && !loadingdlg.isShowing()) {
                            loadingdlg.enableCancel(false);
                            loadingdlg.changeStatus(1,
                                    getResources().getString(R.string.synch_szone_message));
                            loadingdlg.show();
                        }
                    } else if (securityZone.onOff.equals("0")) {//off状态的securityzone被修改
                        setItemAdapterMap(listItem.get(itemPosition), securityZone);
                        adapter.notifyDataSetChanged();
                        saveListItemToLocalJason();
                    }
                } else if (resultCode == 3) {
                    familySsid = data.getStringExtra("ssid");
                    familyBssid = data.getStringExtra("bssid");
                    listItem.remove(itemPosition);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    setItemAdapterMap(map, securityZone);
                    listItem.add(itemPosition, map);
                    adapter.notifyDataSetChanged();
                    setOffToOn(securityZone);
                }
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FAMILYWIFI) {
                familySsid = data.getStringExtra("ssid");
                familyBssid = data.getStringExtra("bssid");
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 1024) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // 授权成功
                    if (PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                        myApp.initFileDirs();
                    } else {
                        if (PermissionUtils.hasRefusedPermissions(this, PermissionUtils.storagePermissions)) {
                            PermissionUtils.showPermissionPromptDialog(myApp, getString(R.string.need_storage_new), new OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, true);
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    PermissionUtils.getNoGrantedPermissions(this, PermissionUtils.storagePermissions), Constants.PERMISSION_RESULT_INIT);
                        }
                    }
                } else {
                    // 授权失败
                    ToastUtil.show(this, "未授权成功");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int opm = myApp.getIntValue(curWatch.getEid() + CloudBridgeUtil.OPERATION_MODE_VALUE,
                Const.DEFAULT_OPERATIONMODE_VALUE);
        if (opm == 4) {
            watch_state.setText(getString(R.string.save_power_remmind));
            layout_save_power_state.setVisibility(View.VISIBLE);
        } else {
            layout_save_power_state.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        String data =
                myApp.getStringValue(curWatch.getEid() + Const.SHARE_PREF_SECURITY_ZONE_JASON_KEY
                        , "");
        if (data != null && !data.equals("")) {
            int flag = 0;
            ArrayList<EFence> ef = new ArrayList<EFence>();
            JSONObject pl = (JSONObject) JSONValue.parse(data);
            JSONArray arr = (JSONArray) pl.get("list");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = (JSONObject) arr.get(i);
                String onoff = (String) item.get("Onoff");
                if (onoff.equals("1")) {
                    EFence securityZone = new EFence();
                    securityZone.name = (String) item.get("Name");
                    String sCenter = (String) item.get("Center_amap");
                    securityZone.lat =
                            Double.parseDouble(sCenter.substring(sCenter.indexOf("(") + 1,
                                    sCenter.indexOf(",")));
                    securityZone.lng =
                            Double.parseDouble(sCenter.substring(sCenter.indexOf(",") + 1,
                                    sCenter.indexOf(")")));
                    securityZone.radius = (Integer) item.get("Radius");
                    securityZone.efid = (String) item.get("Efid");
                    securityZone.eid = curWatch.getEid();
                    securityZone.desc = (String) item.get("Info");
                    ef.add(securityZone);
                } else if (onoff.equals("0")) {
                    flag = 2;
                }
            }
            if (ef.size() > 0 || flag == 2) {
                myApp.getmWatchEFence().put(curWatch.getEid(), ef);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }
    }

    /**
     * user:zhangjun5 time:13:48 date:2017/6/22
     * desc:修改安全区域的开关
     **/
    private void itemButtonClickResult(int position) {
        SecurityZone securityZone = new SecurityZone();
        //添加状态保护，防止因网络延迟造成安全区域内容删除问题。
        if (position < 0) {
            return;
        }
        securityZone = (SecurityZone) listItem.get(position).get("securityObject");

        if (securityZone.onOff.equals("0")) {
            securityZone.onOff = "1";
            listItem.get(position).put("img", R.drawable.toggle_selector_on);
        } else if (securityZone.onOff.equals("1")) {
            securityZone.onOff = "0";
            listItem.get(position).put("img", R.drawable.toggle_selector_off);
        }
        adapter.notifyDataSetChanged();
        saveListItemToLocalJason();
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/16 11:53
     * 方法描述：点击事件操作
     */
    @Override
    public void onClick(View v) {
        if (mBtnBack == v) {
            finish();
        } else if (mSecurityAddBtn == v) {
            if (!PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                Toast.makeText(this, getString(R.string.no_storage_permission_tips), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!myApp.isMeAdmin(curWatch)) {
                ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                return;
            }
            addSecurityZone();
        } else if (btn_finish == v) {
//            myApp.checkSetFunctionList(this, curWatch);
            finish();
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/16 11:52
     * 方法描述：点击添加按钮之后的操作。..
     */
    private void addSecurityZone() {
        if (listItem.size() >= Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
            ToastUtil.showMyToast(SecurityZoneActivityNew.this,
                    getString(R.string.security_zone_max_count_prompt),
                    Toast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent();
        intent.setClass(SecurityZoneActivityNew.this, SecurityZoneSettingGoogle.class);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
        startActivityForResult(intent, 2);

        myApp.sdcardLog("securityzone Activity to settings2:" + ":");
    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/20 14:04
     * 方法描述：发送手表状态信息
     */
    private void sendWatchEFenceMsg(SecurityZone securityZone, String info, String onoff,
                                    int type) {
        cover.setClickable(true);
        MyMsgData eFenceMsg = new MyMsgData();
        eFenceMsg.setCallback(SecurityZoneActivityNew.this);
        JSONObject pl = new JSONObject();

        if (securityZone.keyEFID.equals("EFID1")) {
            if (!securityZone.sName.equals(getResources().getString(R.string.security_zone_home))) {
                securityZone.sName = getResources().getString(R.string.security_zone_home);
            }
        }

        if (onoff.equals("1")) {
            JSONObject efid = new JSONObject();
            double lat = 0.0;
            double lng = 0.0;
            //if(myApp.getIntValue(Const.SHARE_PREF_FIELD_CHANEG_MAP,1) == 2)
            {
                if (securityZone.sCenter != null && securityZone.sCenter.contains(",")) {
                    lat = Double.parseDouble(securityZone.sCenter.substring(securityZone.sCenter.indexOf("(") + 1, securityZone.sCenter.indexOf(",")));
                    lng = Double.parseDouble(securityZone.sCenter.substring(securityZone.sCenter.indexOf(",") + 1, securityZone.sCenter.indexOf(")")));
                }
                efid.put(CloudBridgeUtil.SECURITY_ZONE_COORDINATETYPE, "2");
            }
//			else{
//				lat = Double.parseDouble(securityZone.sCenterBD.substring(securityZone.sCenter
//				.indexOf("(")+1, securityZone.sCenter.indexOf(",")));
//				lng = Double.parseDouble(securityZone.sCenterBD.substring(securityZone.sCenter
//				.indexOf(",")+1, securityZone.sCenter.indexOf(")")));
//				efid.put(CloudBridgeUtil.SECURITY_ZONE_COORDINATETYPE, "1");
//			}
            efid.put(CloudBridgeUtil.KEY_NAME_NAME, securityZone.sName);
            efid.put(CloudBridgeUtil.KEY_NAME_EFID_DESC, info);
            efid.put(CloudBridgeUtil.KEY_NAME_LAT, lat);
            efid.put(CloudBridgeUtil.KEY_NAME_LNG, lng);
            efid.put(CloudBridgeUtil.KEY_NAME_EFID_RADIUS, (securityZone.sRadius));
            pl.put(securityZone.keyEFID, efid);
            pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());

            eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_SET,
                    Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                    myApp.getToken(), pl));
        } else if (onoff.equals("0")) {
            JSONArray keyList = new JSONArray();
            keyList.add(securityZone.keyEFID);
            pl.put(CloudBridgeUtil.KEY_NAME_EFID, keyList);
            pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());

            eFenceMsg.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_EFENCE_DEL,
                    Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                    myApp.getToken(), pl));
        }
        callbackType = type;
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(eFenceMsg);
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/21 14:57
     * 方法描述：设置发送图片之前的操作。
     */
    private void sendPicToServer(String fileName, String picId) {
        File cropTemp = new File(fileName);
        try {
            byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);
            byte[] tmp = AESUtil.getInstance().decrypt(bitmapArray);
            byte[] headBitmapBytes = tmp;
            if (headBitmapBytes != null) {
                headE2cSn = sendPreviewImageE2c(curWatch.getEid(),
                        picId, headBitmapBytes);
            } else {
                LogUtil.i("操作错误" + "  " + "没有数据。");
            }
        } catch (FileNotFoundException e) {
            LogUtil.e("图片数据失败：" + "  " + e.toString());
            myApp.sdcardLog("securityzone Activity1:" + e.toString());
        } catch (Exception e) {
            LogUtil.e("异常操作：" + "  " + e.toString());
            myApp.sdcardLog("securityzone Activity2:" + e.toString());
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/20 20:53
     * 方法描述：发送预览图片到服务器的信息
     */
    private int sendPreviewImageE2c(String eid, String PicId, byte[] mapBytes) {

        MyMsgData e2c = new MyMsgData();
        int sn;
        e2c.setCallback(SecurityZoneActivityNew.this);
        JSONObject pl = new JSONObject();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_SECURITYPREVIEW);
        key.append(PicId);
        JSONObject chat = new JSONObject();
        String baseData = Base64.encodeToString(mapBytes, Base64.NO_WRAP);
        LogUtil.i("securityzone:" + "  " + "" + baseData.length());

        chat.put(CloudBridgeUtil.SECURITY_ZONE_PREVIEW_DATA, Base64.encodeToString(mapBytes,
                Base64.NO_WRAP));

        pl.put(key.toString(), chat);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C_UP,
                sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2c);
        return sn;
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/20 20:54
     * 方法描述：发送请求预览图片到本地的信息。
     */
    private int sendPreviewImageC2E(String eid, String picId) {
        // TODO Auto-generated method stub
        MyMsgData c2e = new MyMsgData();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_SECURITYPREVIEW);
        key.append(picId);

        c2e.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put("Key", key.toString());
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        c2e.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_C2E_GET_MESSAGE,
                sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(c2e);
        return sn;
    }

    private int headE2cSn;

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:50
     * 方法描述：修改安全区域的回调函数
     * <p>
     * 修改描述：添加下载图片和上传图片之后的信息处理。
     * <p>
     * CID_EFENCE_SET_RESP：设置回调标志
     * CID_EFENCE_DEL_RESP：删除回调标志
     * CID_E2C_DOWN       : 上传图片信息
     * CID_C2E_GET_MESSAGE_RESP：下载图片信息
     * CID_EFENCE_GET_RESP： 获取电子围栏信息。
     */
    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        cover.setClickable(false);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_E2C_DOWN:   //上传数据图片
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    if (CloudBridgeUtil.getCloudMsgSN(respMsg) == headE2cSn) {
                        LogUtil.i("数据上传成功" + "  " + "返回数据信息。");
                    }
                }
                break;

            case CloudBridgeUtil.CID_C2E_GET_MESSAGE_RESP:   //下载图片信息
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    int sn = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_SN);
                    String key = String.valueOf(sn);
                    SecurityZone sZone = (SecurityZone) loaderPreViewList.get(key);
                    if (sZone == null) {
                        return;
                    }
//                    if (!PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions))
//                        return;
                    String fileName = ImibabyApp.getIconCacheDir() + "/"
                            + curWatch.getEid() + picIdFormat(sZone.sCenter) + ".jpg";

                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    byte[] bitmapArray;
                    try {
                        String picData;
                        if (pl.get("security_zone_preview_DATA") != null) {
                            picData = (String) pl.get("security_zone_preview_DATA");
                        } else {
                            picData = (String) pl.get(CloudBridgeUtil.SECURITY_ZONE_PREVIEW_DATA);
                        }
                        bitmapArray = Base64.decode(picData, Base64.NO_WRAP);
                        byte[] tmp = AESUtil.getInstance().encrypt(bitmapArray);
                        File headfile = new File(fileName);
                        FileOutputStream out = new FileOutputStream(headfile);
                        out.write(tmp);
                        out.close();
                        LogUtil.i("数据下载成功" + "  " + "查看数据是否正常。");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < listItem.size(); i++) {
                        SecurityZone localZone = (SecurityZone) listItem.get(i).
                                get("securityObject");
                        String sMapCenter = localZone.sCenter;
                        if (sZone.sCenter.equals(sMapCenter)) {
                            localZone.preview = fileName;
                            listItem.get(i).remove("preview");
                            listItem.get(i).put("preview", fileName);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    saveListItemToLocalJason();
                } else if (rc == CloudBridgeUtil.RC_NETERROR) {
                    ToastUtil.show(this, getString(R.string.network_err));
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.show(this, getString(R.string.data_sync_timeout));
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    LogUtil.i("securityzone" + "  " + "图片加载失败！");
                }
                break;
            case CloudBridgeUtil.CID_EFENCE_DEL_RESP:
                if (loadingdlg != null && loadingdlg.isShowing()) {
                    loadingdlg.dismiss();
                }
                JSONObject object = (JSONObject) reqMsg.get("PL");
                JSONArray efArray = (JSONArray) object.get(CloudBridgeUtil.KEY_NAME_EFID);
                String efId = (String) efArray.get(0);
                int position = findPositionByEfid(efId);

                int eRc = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_RC);
                if (eRc == CloudBridgeUtil.RC_SUCCESS) {
                    if (callbackType == 3) {
                        deleteAdapterItem(position);
                        saveListItemToLocalJason();
                    } else if (callbackType == 1) {
                        itemButtonClickResult(position);
                    }
                } else if (eRc == CloudBridgeUtil.RC_NETERROR ||
                        eRc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.show(this, getString(R.string.network_err));
                } else if (eRc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.show(this, getString(R.string.data_sync_timeout));
                } else {
                    LogUtil.e("error rc = " + eRc);
                }
                break;
            case CloudBridgeUtil.CID_EFENCE_SET_RESP:
                if (loadingdlg != null && loadingdlg.isShowing()) {
                    loadingdlg.dismiss();
                }
                JSONObject setObject = (JSONObject) reqMsg.get("PL");
                String setEfid = null;
                for (String key : setObject.keySet()) {
                    if (key.contains("EFID")) {
                        setEfid = key;
                        break;
                    }
                }
                if (setEfid == null) {
                    setEfid = "EFID99";//没有找到EFID之后的保护处理
                }
                int setPosition = findPositionByEfid(setEfid);
                int respRc = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_RC);
                if (respRc == CloudBridgeUtil.RC_SUCCESS) {
                    itemButtonClickResult(setPosition);
                    myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_EFID_IS_HAVE, "1");
                } else if (respRc == CloudBridgeUtil.RC_NETERROR ||
                        respRc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.show(this, getString(R.string.network_err));
                } else if (respRc == CloudBridgeUtil.RC_TIMEOUT) {
                    ToastUtil.show(this, getString(R.string.data_sync_timeout));
                } else {
                    LogUtil.e("error rc = " + respRc);
                }
                break;
            case CloudBridgeUtil.CID_EFENCE_GET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl != null && pl.size() > 0) {
                        loaderPreViewList.clear();
                        ArrayList<SecurityZone> securityList = new ArrayList<SecurityZone>();
                        for (Entry<String, Object> entry : pl.entrySet()) {
                            String key = entry.getKey();
                            //初始化一个安全区域的结构体。
                            JSONObject efid = (JSONObject) entry.getValue();
                            SecurityZone securityZone = new SecurityZone();
                            if (key.equals("EFID1")) {
                                securityZone.sName = getString(R.string.security_zone_home);
                            } else if (key.equals("EFID2")) {
                                securityZone.sName = getString(R.string.security_zone_school);
                            } else {
                                securityZone.sName = (String) efid.get(CloudBridgeUtil.KEY_NAME_NAME);
                            }

                            securityZone.sRadius = (Integer) efid.get(CloudBridgeUtil.KEY_NAME_EFID_RADIUS);
                            int temp = securityZone.sRadius;
                            if (temp >= 500) {
                                securityZone.sRadius = 500;
                            }
                            securityZone.onOff = "1";
                            securityZone.keyEFID = key;
                            securityZone.info = (String) efid.get(CloudBridgeUtil.KEY_NAME_EFID_DESC);
                            if (securityZone.info == null || securityZone.info.equals("")) {
                                break;
                            }
                            Double lat = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LAT);
                            Double lng = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LNG);
                            LatLng latlng = new LatLng(lat, lng);
                            securityZone.sCenter = latlng.toString();

                            Double latbd = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LATBD);
                            Double lngbd = (Double) efid.get(CloudBridgeUtil.KEY_NAME_LNGBD);
                            LatLng latlngbd = new LatLng(latbd, lngbd);
                            securityZone.sCenterBD = "lat/lng:(" + latlngbd.latitude + "," + latlngbd.longitude + ")";
                            securityZone.preview = "";
                            boolean isPreview = false;
                            //检查本地是否存在截图资源
                            for (int i = 0; i < listItem.size(); i++) {
                                SecurityZone localZone = (SecurityZone) listItem.get(i).
                                        get("securityObject");
                                String sMapCenter = localZone.sCenter;
                                String preview = localZone.preview;
                                if (securityZone.sCenter.equals(sMapCenter) &&
                                        (!"".equals(preview) && !"####".equals(preview))
                                        && securityZone.sRadius == (localZone.sRadius)) {
                                    File pathFile = new File((String) listItem.get(i).get("preview"));
                                    if (pathFile.exists()) {
                                        securityZone.preview = (String) listItem.get(i).get("preview");
                                        isPreview = true;
                                    }
                                    break;
                                }
                            }
                            //没有存在本地图片的操作
                            if (!isPreview) {
                                int sn = sendPreviewImageC2E(
                                        curWatch.getEid(),
                                        securityZone.keyEFID);
//										picIdFormat(securityZone.sCenter));
                                loaderPreViewList.put(String.valueOf(sn), securityZone);
                            }

                            securityList.add(securityZone);
                            //	            	addAdapterItem(securityZone);
                        }
                        //去掉efid大于MAX_SWTICHED_ON_SECURITY_ZONE_COUNT之后的安全区域
                        removeZoneMoreThanFive(securityList);
                        //删除offOn为1的安全区域
                        removeAdapterOnItem();
                        //覆盖掉相同efid的安全区域
                        coverSameSecurityZone(securityList);
                        for (int i = 0; i < securityList.size(); i++) {
                            addAdapterItem(securityList.get(i));
                        }
                        //对获取到的数据进行一次排序
                        securityZoneListSort();
                        saveListItemToLocalJason();
                        myApp.setValue(curWatch.getEid() + Const.SHARE_PREF_EFID_IS_HAVE, "1");
                    } else if (pl.size() == 0) {
                        if (listItem.size() > 0) {
                            removeAdapterOnItem();
                        }
                        if (listItem.size() <= 0) {
                            initDefaultList();
                        }
                        saveListItemToLocalJason();
                    }
                }
                break;
            case CloudBridgeUtil.CID_MAPGET_MGET_RESP:
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                if (pl != null && !pl.isEmpty()) {
                    String gl = (String) pl.get(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LIST);
                    if (gl != null && !gl.equals("")) {
                        JSONObject guardlist = (JSONObject) JSONValue.parse(gl);
                        String onoff = (String) guardlist.get(CloudBridgeUtil.ONOFF);
                        school_guard_enable = onoff.equals("1");
                    }
                }
                break;
        }
        callbackType = 0;
    }


    @Override
    public void confirmClick() {

    }

    /**
     * 类名称：SecurityZoneActivity
     * 修改人：zhangjun5
     * 修改时间：2015/11/11 10:31
     * 方法描述：数据适配类，用于匹配数据和视图
     */
    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int mPosition = position;
            convertView = super.getView(position, convertView, parent);

            final ImageButton onOffButton = (ImageButton) convertView.findViewById(R.id.button_img);
            final RelativeLayout layout_home_wifi =
                    (RelativeLayout) convertView.findViewById(R.id.layout_home_wifi);
            final ImageButton delete_img = (ImageButton) convertView.findViewById(R.id.delete_img);
            TextView but_wifi_setting = (TextView) convertView.findViewById(
                    R.id.but_wifi_setting);
            final ImageView backView = (ImageView) convertView.findViewById(
                    R.id.security_zone_image);
            TextView textView = (TextView) convertView.findViewById(R.id.text_security_setting);
            TextView home_wifi_name = (TextView) convertView.findViewById(R.id.home_wifi_name);
            TextView home_wifi_des = (TextView) convertView.findViewById(R.id.home_wifi_des);

            SecurityZone securityZone = new SecurityZone();
            securityZone = (SecurityZone) listItem.get(mPosition).get("securityObject");
            String preview = securityZone.preview;

            if ("EFID1".equals(securityZone.keyEFID)) {
                layout_home_wifi.setVisibility(View.VISIBLE);
                /**
                 * 显示家庭wifi设置信息
                 */
                if (!TextUtils.isEmpty(familySsid)) {
                    home_wifi_name.setText(familySsid);
                    home_wifi_des.setText(getString(R.string.home_wifi_des2));
                } else {
                    home_wifi_name.setText(getString(R.string.home_wifi_name));
                    home_wifi_des.setText(getString(R.string.home_wifi_des));
                }
            } else {
                layout_home_wifi.setVisibility(View.GONE);
            }
            if (!"EFID1".equals(securityZone.keyEFID) && !"EFID2".equals(securityZone.keyEFID)) {
                delete_img.setVisibility(View.VISIBLE);
            } else {
                delete_img.setVisibility(View.GONE);
            }

            if (preview.equals(getResources().getString(R.string.security_default))) {
                backView.setImageResource((Integer) listItem.get(mPosition).get("preview"));
                onOffButton.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);

            } else {


                onOffButton.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                if (!"".equals(preview)) {  //为空保护和检查
                    try {
                        FileInputStream fos = new FileInputStream(preview);
                        byte[] tmp = new byte[fos.available()];
                        fos.read(tmp);
                        byte[] viewbyte = AESUtil.getInstance().decrypt(tmp);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(viewbyte, 0, viewbyte.length);
                        backView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        backView.setImageResource(R.drawable.security_default);
                    }
                } else {
                    backView.setImageResource(R.drawable.security_default);
                }
            }
            delete_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myApp.isMeAdmin(curWatch)) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        return;
                    }
                    SecurityZone securityZone = new SecurityZone();
                    securityZone = (SecurityZone) listItem.get(mPosition).get("securityObject");
                    showDeleteOp(mPosition, securityZone);
                }
            });
            onOffButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!myApp.isMeAdmin(curWatch)) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        return;
                    }
                    SecurityZone securityZone = new SecurityZone();
                    securityZone = (SecurityZone) listItem.get(mPosition).get("securityObject");
                    if (securityZone.onOff.equals("0")) {
                        if (!checkSecurityZoneOverlap(mPosition)) {
                            ToastUtil.showMyToast(SecurityZoneActivityNew.this,
                                    getString(R.string.security_zone_overlay),
                                    Toast.LENGTH_SHORT);
                            return;
                        }
                        setOffToOn(securityZone);

                    } else if (securityZone.onOff.equals("1")) {
                        if (school_guard_enable) {
                            DialogUtil.CustomNormalDialog(SecurityZoneActivityNew.this, getString(R.string.prompt),
                                    getString(R.string.guard_school_efence_onoff_tips), new OnCustomDialogListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }, getString(R.string.confirm)).show();
                            return;
                        }
                        sendWatchEFenceMsg(securityZone, securityZone.info, "0", 1);
                        if (loadingdlg != null && !loadingdlg.isShowing()) {
                            loadingdlg.enableCancel(false);
                            loadingdlg.changeStatus(1,
                                    getResources().getString(R.string.synch_szone_message));
                            loadingdlg.show();
                        }

                    }
                }
            });
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myApp.isMeAdmin(curWatch)) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        return;
                    }
                    SecurityZone securityZone = new SecurityZone();
                    securityZone = (SecurityZone) listItem.get(mPosition).get("securityObject");
                    Intent intent = new Intent();
                    intent.setClass(SecurityZoneActivityNew.this, SecurityZoneSettingGoogle.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_NAME, securityZone.sName);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_RADIUS,
                            String.valueOf(securityZone.sRadius));
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER, securityZone.sCenter);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_CENTER_BD,
                            securityZone.sCenterBD);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_ONOFF, securityZone.onOff);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_EFID, securityZone.keyEFID);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_SEARCH_INFO, securityZone.info);
                    bundle.putString(CloudBridgeUtil.SECURITY_ZONE_PREVIEW, securityZone.preview);
                    intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
                    intent.putExtra("inzone", bundle);
                    startActivityForResult(intent, 1);

                    myApp.sdcardLog("securityzone Activity to settings3:" + bundle.toString()
                            + ":" + mPosition + ":");
                }
            });

            but_wifi_setting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * wifi设置
                     */
                    if (!myApp.isMeAdmin(curWatch)) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        return;
                    }
                    goHomewifiSetting();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
//                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//                                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
//
//                    } else {
//                        goHomewifiSetting();
//
//                    }

                }
            });
            layout_home_wifi.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * wifi设置
                     */
                    if (!myApp.isMeAdmin(curWatch)) {
                        ToastUtil.show(getApplicationContext(), getString(R.string.need_admit_edit));
                        return;
                    }
                    goHomewifiSetting();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
//                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//                                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
//
//                    } else {
//                        goHomewifiSetting();
//
//                    }

                }
            });
            return convertView;
        }

    }

    private void goHomewifiSetting() {
        Intent intent = new Intent(SecurityZoneActivityNew.this,
                HomeWiFiSettingActivity.class);
        intent.putExtra("ssid", familySsid == null ? "" : familySsid);
        intent.putExtra("bssid", familyBssid == null ? "" : familyBssid);
        startActivityForResult(intent, REQUEST_CODE_FAMILYWIFI);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            goHomewifiSetting();
        } else if (requestCode == Constants.PERMISSION_RESULT_INIT) {
            if (PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                myApp.initFileDirs();
            } else {
                PermissionUtils.showPermissionPromptDialog(myApp, getString(R.string.need_storage_new), new OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, true);
            }
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/27 15:42
     * 方法描述：把安全区域从关闭状态进入到开启状态。
     */
    private void setOffToOn(SecurityZone securityZone) {
        int onSecurityZone = getSwitchedOnItemCount();
        if (onSecurityZone < Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT) {
//			securityZone.keyEFID = getKeyEFID();
            sendWatchEFenceMsg(securityZone, securityZone.info, "1", 1);
            if (loadingdlg != null && !loadingdlg.isShowing()) {
                loadingdlg.enableCancel(false);
                loadingdlg.changeStatus(1, getResources().getString(R.string.synch_szone_message));
                loadingdlg.show();
            }
            //上传图片信息
            sendPicToServer(securityZone.preview, picIdFormat(securityZone.sCenter));
        } else {
            ToastUtil.showMyToast(SecurityZoneActivityNew.this,
                    getString(R.string.security_zone_number_limit,
                            Integer.toString(Const.MAX_SWTICHED_ON_SECURITY_ZONE_COUNT)),
                    Toast.LENGTH_SHORT);
        }
    }

    /**
     * 类名称：SecurityZoneActivity
     * 创建人：zhangjun5
     * 创建时间：2015/11/26 15:54
     * 方法描述：检查同步数据是否存在地址重复，发现重复数据后，禁止开启该安全区域。
     */
    private boolean checkSecurityZoneOverlap(int checkZoneNum) {
        SecurityZone checkSecurityZone = (SecurityZone) listItem.get(checkZoneNum).get(
                "securityObject");
        for (int i = 0; i < listItem.size(); i++) {
            SecurityZone securityZone = (SecurityZone) listItem.get(i).get("securityObject");
            if (i == checkZoneNum || securityZone.preview.equals(getResources().getString(R.string.security_default))) {
                continue;
            }
            if (securityZone.onOff.equals("0")) {
                continue;
            }

            String stmpCenter = securityZone.sCenter;
            LatLng startLatLng =
                    new LatLng(Double.parseDouble(stmpCenter.substring(stmpCenter.indexOf("(") + 1, stmpCenter.indexOf(","))),
                            Double.parseDouble(stmpCenter.substring(stmpCenter.indexOf(",") + 1,
                                    stmpCenter.indexOf(")"))));
            stmpCenter = checkSecurityZone.sCenter;
            LatLng endLatLng =
                    new LatLng(Double.parseDouble(stmpCenter.substring(stmpCenter.indexOf("(") + 1, stmpCenter.indexOf(","))),
                            Double.parseDouble(stmpCenter.substring(stmpCenter.indexOf(",") + 1,
                                    stmpCenter.indexOf(")"))));

            int distance = (int) GoogleMapUtil.getGoogleDistance(startLatLng, endLatLng);
            int twoCircleRadius = (securityZone.sRadius) + (checkSecurityZone.sRadius);
            if (distance < twoCircleRadius) {
                return false;
            }
        }
        return true;
    }

    private void getGuardListFromCloud() {
        MyMsgData mapget = new MyMsgData();
        mapget.setCallback(this);

        JSONArray plKeyList = new JSONArray();
        plKeyList.add(CloudBridgeUtil.KEY_NAME_SCHOOL_GUARD_LIST);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, getMyApp().getCurUser().getFocusWatch().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEYS, plKeyList);
        mapget.setReqMsg(getMyApp().obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET_MGET, pl));
        if (getMyApp().getNetService() != null && getMyApp().getNetService().isCloudBridgeClientOk()) {
            getMyApp().getNetService().sendNetMsg(mapget);
        }
    }
}
