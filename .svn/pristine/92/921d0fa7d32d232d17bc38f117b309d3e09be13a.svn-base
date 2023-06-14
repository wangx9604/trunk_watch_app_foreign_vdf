/**
 * Creation Date:2015-3-12
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.db.UserRelationDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirstSetOtherRelationshipSet extends NormalActivity implements OnClickListener , MsgCallback {

    private View btnBack;
    private String curWatchEid;
    private ImibabyApp mApp;
    private ListView listView;
    private MySimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> listItem;
    private int selRelation = -1;
    private int initSelRelation = -1;
    private Button bt_next;
    private TextView tv_title;
    private String nickname;

    private ArrayList<PhoneNumber> mBindWhiteList = new ArrayList<PhoneNumber>();
    private ArrayList<Integer> attriList = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_set_other_relationship_set);
        mApp = (ImibabyApp) getApplication();
        curWatchEid = getIntent().getExtras().getString(CloudBridgeUtil.KEY_NAME_EID);
        attriList = new ArrayList<Integer>();

        initSelRelation = selRelation = -1;
        getContactListFromLocal();

        int size = mBindWhiteList.size();
        for (int i = 0; i < size; i++) {
            PhoneNumber phoneNumber =  mBindWhiteList.get(i);
            if (phoneNumber.contactType != 2) {
                attriList.add(phoneNumber.attri);
            }
        }

        listItem = new ArrayList<HashMap<String, Object>>();
        adapter = new MySimpleAdapter(this, listItem, R.layout.relationship_list_item,
                new String[]{"imgHead", "relationDetail","selected"},
                new int[]{R.id.relaiton_avatar, R.id.relaiton_name,R.id.iv_selected});
        listView = findViewById(R.id.relation_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (attriList.contains(position+8) && (position+8) != initSelRelation ) {
                    return;
                } else{
                    selRelation = position+8;
                    adapter.notifyDataSetChanged();
                }

            }
        });
        initView();
        initMyReceiver();
    }
    BroadcastReceiver mMyReceiver;
    private void initMyReceiver(){
        mMyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_SET_RELATION_END)) {
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();


        filter.addAction(Const.ACTION_SET_RELATION_END);
        registerReceiver(mMyReceiver, filter);
    }

    private  void getContactListFromLocal(){
        String jsonStr = myApp.getStringValue(curWatchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY,null);
        mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
    }
    private void initView() {
        btnBack = findViewById(R.id.iv_title_back);
        btnBack.setOnClickListener(this);
        bt_next = findViewById(R.id.next_step);
        bt_next.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.first_set_title);

        int[] img_heads = {R.drawable.relation_6, R.drawable.relation_7, R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_6, R.drawable.relation_7,R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_6,R.drawable.relation_7,R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_8,R.drawable.relation_9,R.drawable.relation_8,R.drawable.relation_9};
        String[] details = {
          getString(R.string.relation_8),
                getString(R.string.relation_9),
                getString(R.string.relation_10),
                getString(R.string.relation_11),
                getString(R.string.relation_12),
                getString(R.string.relation_13),
                getString(R.string.relation_14),
                getString(R.string.relation_15),
                getString(R.string.relation_16),
                getString(R.string.relation_17),
                getString(R.string.relation_18),
                getString(R.string.relation_19),
                getString(R.string.relation_20),
                getString(R.string.relation_21),
                getString(R.string.relation_22),
                getString(R.string.relation_23)
        };
        for (int i = 0; i <= 15; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("imgHead", img_heads[i]);
            map.put("relationDetail", details[i]);
            map.put("selected",R.drawable.radio_bt_0);
            listItem.add(map);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (btnBack == v) {
            finish();
        } else if (bt_next == v) {
            if(selRelation != -1){
                {
                    //若为联通通话卡，且选择不为其他，则进入设置白名单手机号，
                    getMyApp().getCurUser().setHeadPath(Integer.valueOf(selRelation).toString());
                    nickname =getText(myApp.getRelationSels().get(selRelation).StrId).toString();
                    getMyApp().getCurUser().setRelation(curWatchEid, nickname);
                    getMyApp().getCurUser().getCustomData().reloadRelation(getMyApp().getCurUser().getWatchList());
                    sendRelationSetMsg();

                    {
//                        Intent intent = new Intent(FirstSetOtherRelationshipSet.this, SetPhonenumActivity.class);
//                        intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);
//                        intent.putExtra("nickname",nickname);
//                        intent.putExtra("index", selRelation);
//                        startActivity(intent);
                    }
                }
            } else {
                ToastUtil.showMyToast(FirstSetOtherRelationshipSet.this, getText(R.string.no_relation).toString(), Toast.LENGTH_SHORT);
            }
        }
    }

    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            if (selRelation == (position+8)) {
                convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_1);
            } else {
                convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_0);
            }
            if(attriList.contains(position+8) && (position+8) != initSelRelation){
                convertView.findViewById(R.id.cover).setVisibility(View.VISIBLE);
            }else{
                convertView.findViewById(R.id.cover).setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    //测试版本修改为alias表示headid的sel
    private void sendRelationSetMsg(){
        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(FirstSetOtherRelationshipSet.this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, getMyApp().getCurUser().getCustomData().toJsonStr());

//        pl.put(CloudBridgeUtil.KEY_NAME_ALIAS, Integer.valueOf(selRelation).toString());
        relationMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET,pl));
        if(myApp.getNetService() != null)
        myApp.getNetService().sendNetMsg(relationMsg);
        //本地先刷新再说
        refreshRelationLocalData();
    }
    private void sendUserSetChangeE2G(String eid,String gid) {

        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(FirstSetOtherRelationshipSet.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_USER_CHANGE_NOTICE);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);

        e2e.setReqMsg(CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), myApp.getToken(), null, gid, pl));
        if(myApp.getNetService() != null)
        myApp.getNetService().sendNetMsg(e2e);
    }
    private int retryCount = 0;

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        // TODO Auto-generated method stub
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        if (cid == CloudBridgeUtil.CID_USER_SET_RESP){
            if (rc == CloudBridgeUtil.RC_SUCCESS
                    ||rc == CloudBridgeUtil.ERROR_CODE_SECURITY_USER_NOT_EXIST){//服务器错误，临时修改大家可以使用
                //save relation save nickname local
                if (nickname!=null&&nickname.length()>0){
//                myApp.getCurUser().setNickname(nickname);
                    //refresh
                    // sendBroadcast(new Intent(Const.ACTION_QUERY_ALL_GROUPS));

                    String curGid = null;
                    for (FamilyData fmy:myApp.getCurUser().getFamilyList()){
                        if (fmy.getWatchlist().get(0).getEid().equals(curWatchEid)){
                            curGid = fmy.getFamilyId();
                        }

                    }
                    if (curGid!=null) {
                        sendUserSetChangeE2G(myApp.getCurUser().getEid(), curGid);
                    }
                }


            }else{
                //retry once
                retryCount++;
                if (retryCount<3){
                    bt_next.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendRelationSetMsg();
                        }
                    },1000);
                }

            }

        }
    }
    private void refreshRelationLocalData(){
        for (FamilyData fmy:myApp.getCurUser().getFamilyList()){
            for (MemberUserData member:fmy.getMemberList()){
                if (member.getEid().equals(myApp.getCurUser().getEid())){
                    member.getCustomData().setFromJsonStr(myApp.getCurUser().getCustomData().toJsonStr());
                    UserRelationDAO.getInstance(getApplicationContext()).addUserRelation(member, myApp.getCurUser().getFocusWatch().getFamilyId(), myApp.getCurUser().getFocusWatch().getWatchId(), myApp.getCurUser().getFocusWatch().getNickname(),member.getCustomData().toJsonStr());
                }
            }
        }
    }
}
