package com.xiaoxun.xun.dialBg;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

import com.xiaoxun.xun.activitys.NormalActivity;

import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.DialBgDAO;
import com.xiaoxun.xun.gallary.swiplayout.SHSwipeRefreshLayout;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomFileUtils;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.xiaoxun.xun.dialBg.CropPreviewActivity.GET_IMAGE_FROM_ALBUM;
import static com.xiaoxun.xun.dialBg.CropPreviewActivity.GET_IMAGE_FROM_CAMERA;

/**
 * 相册表盘主界面
 */
public class DialBgActivity extends NormalActivity implements MsgCallback, DialBgAdapter.btnClickListener {
    private final static String TAG = "DialBgActivity";

    public static final int PERMISSION_RESULT_CAMERA = 0xff;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    public static final int RESULT_NO_DATA_BACK = 0x9;

    private WatchData curWatch;

    public static int mode = 0;   //0 normal   1 delete
    private String curWatchFilePath = "";

    TextView tv_title;
    ImageButton iv_title_back;
    ImageButton iv_title_menu;
    RelativeLayout no_data_ly;
    Button add_new;
    SHSwipeRefreshLayout fresh_ly;
    RecyclerView dial_bg_list;
    RelativeLayout list_ly;

    DialBgAdapter adapter;
    private ArrayList<DialBgItem> list = new ArrayList<>();
    private HashMap<Integer, DialBgItem> snList = new HashMap<>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Const.ACTION_DIALBG_DELETE)){
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void initDir() {
        String eid = curWatch.getEid();
        String eidAes = AESUtil.getInstance().encryptDataStr(eid);
        File f = getExternalFilesDir(Const.MY_BASE_DIR + "/DIAL_LOCAL_BG");
        if (!f.exists()) {
            f.mkdirs();
        }
        File fp = new File(f, eidAes);
        if (!fp.exists()) {
            fp.mkdirs();
        }
        curWatchFilePath = fp.getAbsolutePath();
    }

    private void initData() {
        String eid = getMyApp().getCurUser().getFocusWatch().getEid();
        File fp = new File(curWatchFilePath);
        String[] files = fp.list();
        ArrayList<DialBgItem> sqlList = DialBgDAO.getInstance(this).getDialBgList(eid);
        if(sqlList != null) {
            for (DialBgItem sqlItem : sqlList) {
                boolean imgExist = false;
                for (int i = 0; i < files.length; i++) {
                    String fileName = getFileNameFromPath(sqlItem.getImg_path());
                    if (files[i].equals(fileName)) {
                        imgExist = true;
                        list.add(sqlItem);
                        break;
                    }
                }
                if (!imgExist) {
                    DialBgDAO.getInstance(this).deleteDialBgItem(eid, sqlItem);
                }
            }
        }
    }

    private void initViews() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.dial_bg_txt));
        iv_title_back = findViewById(R.id.iv_title_back);
        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 0) {
                    finish();
                } else {
                    mode = 0;
                    iv_title_back.setBackgroundResource(R.drawable.btn_title_back_selector);
                    iv_title_menu.setBackgroundResource(R.drawable.icon_delete);
                    add_new.setText(getResources().getString(R.string.dial_bg_btn_txt));
                    adapter.deleteList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        iv_title_menu = findViewById(R.id.iv_title_menu);
        //need replace image
        iv_title_menu.setBackgroundResource(R.drawable.icon_delete);
        iv_title_menu.setVisibility(View.VISIBLE);
        iv_title_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 0) {
                    if(no_data_ly.getVisibility() == View.VISIBLE){
                        return;
                    }
                    mode = 1;
                    iv_title_menu.setBackgroundResource(R.drawable.confirm_0);
                    iv_title_back.setBackgroundResource(R.drawable.cancel_0);
                    add_new.setText(getResources().getString(R.string.select_all));
                } else {
                    //delete
                    mode = 0;
                    deleteItemsInList();
                    iv_title_back.setBackgroundResource(R.drawable.btn_title_back_selector);
                    iv_title_menu.setBackgroundResource(R.drawable.icon_delete);
                    add_new.setText(getResources().getString(R.string.dial_bg_btn_txt));
                }
                adapter.notifyDataSetChanged();
            }
        });
        add_new = findViewById(R.id.add_new);
        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == 0) {
                    openAvatarEditDialog();
                } else {
                    //select all
                    if (adapter.deleteList.size() < list.size()) {
                        adapter.selectAllToDeleteList();
                    } else {
                        adapter.unselectAllToDeleteList();
                    }
                }
            }
        });

        list_ly = findViewById(R.id.list_ly);
        no_data_ly = findViewById(R.id.no_data_ly);
        if (list.size() == 0) {
            no_data_ly.setVisibility(View.VISIBLE);
            list_ly.setVisibility(View.GONE);
        } else {
            no_data_ly.setVisibility(View.GONE);
            list_ly.setVisibility(View.VISIBLE);
        }

        initSwiplayout();

        dial_bg_list = findViewById(R.id.dial_bg_list);
        adapter = new DialBgAdapter(this, getMyApp(), list);
        adapter.setOnbtnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        dial_bg_list.setLayoutManager(gridLayoutManager);
        dial_bg_list.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_bg);
        curWatch = getMyApp().getCurUser().getFocusWatch();
        if (curWatch == null) {
            LogUtil.e("getFocusWatch null.");
            finish();
            return;
        }
        initDir();
        initData();
        initViews();

        downloadList();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_DIALBG_DELETE);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                if (no_data_ly.getVisibility() == View.VISIBLE) {
                    no_data_ly.setVisibility(View.GONE);
                    list_ly.setVisibility(View.VISIBLE);
                }
                String Name = data.getStringExtra("name");
                String time = data.getStringExtra("time");
                String path = data.getStringExtra("img_path");
                DialBgItem item = new DialBgItem();
                item.setId(time);
                item.setImg_path(path);
                item.setName(Name);
                item.setStatus(DialBgItem.STATUS_LOCAL);
                item.setTime(time);
                list.add(item);
                String eid = getMyApp().getCurUser().getFocusWatch().getEid();
                DialBgDAO.getInstance(this).insertDialBgItem(eid, item);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openAvatarEditDialog() {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getResources().getText(R.string.head_edit_camera).toString());
        itemList.add(getResources().getText(R.string.head_edit_pics).toString());
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(this,
                getResources().getText(R.string.dial_bg_dialog_title).toString(), itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        if (position == 1) {
                            if (ActivityCompat.checkSelfPermission(DialBgActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                                startActivityByAction(GET_IMAGE_FROM_CAMERA);
                            } else {
                                ActivityCompat.requestPermissions(DialBgActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_RESULT_CAMERA);
                            }
                        } else {
                            //gallery ,then go to CropActivity
                            startActivityByAction(GET_IMAGE_FROM_ALBUM);
                        }
                    }
                },
                -1, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                }, getResources().getText(R.string.cancel).toString());
        dlg.show();
    }

    private void startActivityByAction(int action) {
        Intent it = new Intent(DialBgActivity.this, CropPreviewActivity.class);
        it.putExtra("action", String.valueOf(action));
        startActivityForResult(it, REQUEST_CODE_UPDATE_PIC);
    }

//    private List<String> splitName(String name) {
//        List<String> list = new ArrayList<>();
//        String[] s1 = name.split("\\.");
//        String[] s2 = s1[0].split("_");
//        for (int i = 0; i < s2.length; i++) {
//            list.add(s2[i]);
//        }
//        return list;
//    }

    public void deleteItemsInList() {
        if (adapter.deleteList.size() == 0) {
            LogUtil.e("There is no data can delete.");
            return;
        }
        for (int i = 0; i < adapter.deleteList.size(); i++) {
            DialBgItem item = adapter.deleteList.get(i);
            if (list.contains(item)) {
                if (curWatchFilePath != null) {
                    File fp = new File(item.getImg_path());
                    if (fp.exists()) {
                        fp.delete();
                    }
                    list.remove(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initSwiplayout() {
        fresh_ly = findViewById(R.id.fresh_ly);
        fresh_ly.setLoadmoreEnable(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.refresh_view, null);
        final TextView textView = view.findViewById(R.id.title);
        fresh_ly.setFooterView(view);
        fresh_ly.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                //update DialBg from Cloud
                downloadList();
            }

            @Override
            public void onLoading() {

            }

            /**
             * 监听下拉刷新过程中的状态改变
             * @param percent 当前下拉距离的百分比（0-1）
             * @param state 分三种状态{NOT_OVER_TRIGGER_POINT：还未到触发下拉刷新的距离；OVER_TRIGGER_POINT：已经到触发下拉刷新的距离；START：正在下拉刷新}
             */
            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        fresh_ly.setRefreshViewText("下拉刷新");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        fresh_ly.setRefreshViewText("松开刷新");
                        break;
                    case SHSwipeRefreshLayout.START:
                        fresh_ly.setRefreshViewText("正在刷新");
                        break;
                }
            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        textView.setText("上拉加载");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        textView.setText("松开加载");
                        break;
                    case SHSwipeRefreshLayout.START:
                        textView.setText("正在加载...");
                        break;
                }
            }
        });
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_DIALBG_GETLIST_RESP:
                if(rc == 1) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    updaeBgFile(pl);
                    getMyApp().sdcardLog("Get Dialbg list success.");
                    adapter.notifyDataSetChanged();
                }else{
                    getMyApp().sdcardLog("Get Dialbg list failed.");
                }
                if(fresh_ly.isRefreshing()) {
                    fresh_ly.finishRefresh();
                }
                break;
            case CloudBridgeUtil.CID_DIALBG_OPERATE_RESP:
                if(rc == 1) {
                    getMyApp().sdcardLog("upload msg success.");
                }else{
                    int sn = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_SN);
                    JSONObject pl = (JSONObject)reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String eid = (String) pl.get(CloudBridgeUtil.KEY_NAME_EID);
                    for (Map.Entry<Integer, DialBgItem> entry : snList.entrySet()) {
                        if (entry.getKey() == sn) {
                            DialBgItem item = entry.getValue();
                            if(list.contains(item)) {
                                int pos = list.indexOf(item);
                                list.get(pos).setStatus(DialBgItem.STATUS_LOCAL);
                                DialBgDAO.getInstance(this).updateDialBgItem(eid,list.get(pos));
                            }
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    getMyApp().sdcardLog("upload msg failed.sn=" + sn);
                }
                break;
        }
    }

    private String getFileNameFromPath(String key) {
        String[] list = key.split("/");
        int len = list.length;
        return list[len - 1];
    }

    private void downloadList() {
        MyMsgData retrieve = new MyMsgData();
        retrieve.setTimeout(10 * 1000);
        retrieve.setCallback(this);
        String eid = getMyApp().getCurUser().getFocusWatch().getEid();
        JSONObject pl = new JSONObject();
        pl.put("EID", eid);
        retrieve.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(
                CloudBridgeUtil.CID_DIALBG_GETLIST,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(retrieve);
        }
    }

    private void updaeBgFile(JSONObject pl) {
        JSONArray array = (JSONArray) pl.get("List");
        if (array != null && array.size() != 0) {
            if(no_data_ly.getVisibility() == View.VISIBLE){
                no_data_ly.setVisibility(View.GONE);
                list_ly.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                String eid = (String) obj.get("EID");
                String id = (String) obj.get("id");
                String key = (String) obj.get("key");
                String url = (String) obj.get("url");
                int status = (Integer) obj.get("status");
                String name = (String) obj.get("name");
                boolean isExist = false;
                for (int j = 0; j < list.size(); j++) {
                    DialBgItem item = list.get(j);
                    if (id.equals(item.getId())) {
                        item.checked = true;
                        isExist = true;
                        if (status != item.getStatus()) {
                            item.setStatus(status);
                        }
                        break;
                    }
                }
                if (!isExist) {
                    DialBgItem newOne = new DialBgItem();
                    newOne.setStatus(status);
                    newOne.setName(name);
                    newOne.setId(id);
                    newOne.setTime(id);
                    newOne.setUrl(url);
                    list.add(newOne);
                    DialBgDAO.getInstance(this).insertDialBgItem(eid, newOne);
                }
            }
//            for(DialBgItem item : list){
//                if(!item.checked && item.getStatus() != DialBgItem.STATUS_LOCAL){
//                    item.setStatus(DialBgItem.STATUS_LOCAL);
//                    String eid = getMyApp().getCurUser().getFocusWatch().getEid();
//                    DialBgDAO.getInstance(this).updateDialBgItem(eid,item);
//                }
//            }
            //adapter.refreshData(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void uploadDialBg(final DialBgItem item) {
        final String eid = getMyApp().getCurUser().getFocusWatch().getEid();
        final File fp = new File(item.getImg_path());
        if (fp != null && fp.exists()) {
            try {
                final byte[] bitmapArray = StrUtil.getBytesFromFile(fp);
                String key = "EP/" + eid + "/ALBUMDIAL/" + item.getTime() + ".jpg";
                CustomFileUtils.getInstance(getMyApp()).uploadData(bitmapArray, key, new CustomFileUtils.UploadListener() {
                    @Override
                    public void uploadSuccess(String result) {

                        sendUploadMsgToServer(item);

                    }

                    @Override
                    public void uploadFail(String error) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUploadMsgToServer(DialBgItem item) {
        MyMsgData retrieve = new MyMsgData();
        retrieve.setTimeout(10 * 1000);
        retrieve.setCallback(this);
        String eid = getMyApp().getCurUser().getFocusWatch().getEid();
        String key = "EP/" + eid + "/ALBUMDIAL/" + item.getTime() + ".jpg";
        JSONObject pl = new JSONObject();
        pl.put("EID", eid);
        pl.put("id", item.getId());
        pl.put("name", item.getName());
        pl.put("key", key);
        pl.put("optype", 0);
        pl.put("timestamp", item.getTime());
        File fp = new File(item.getImg_path());
        pl.put("size", fp.length());
        pl.put("status", item.getStatus());
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        snList.put(sn, item);
        retrieve.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(
                CloudBridgeUtil.CID_DIALBG_OPERATE,
                sn,
                getMyApp().getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(retrieve);
        }
    }

    @Override
    public void onClick(DialBgItem item) {
        String eid = getMyApp().getCurUser().getFocusWatch().getEid();
        item.setStatus(DialBgItem.STATUS_IN_SERVER);
        DialBgDAO.getInstance(this).updateDialBgItem(eid, item);
        adapter.notifyDataSetChanged();
        uploadDialBg(item);
    }
}
