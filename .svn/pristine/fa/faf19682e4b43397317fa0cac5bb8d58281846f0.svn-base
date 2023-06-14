/**
 * Creation Date:2015-3-12
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description Of The Class<br>gggss
 *
 * @author fushiqing
 * @version 1.000, 2015-10-22
 */
public class WhitePhoneListOtherRelationshipSet extends NormalActivity implements OnClickListener {

    private View btnBack;
    private WatchData curWatch;
    private ImibabyApp mApp;
    private ListView listView;
    private MySimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> listItem;
    private int selRelation = -1;
    private int initSelRelation = -1;
    private ArrayList<Integer> attriList;
    private TextView tv_title;
    private String number;
    private String sub_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_white_list_other_relationship_set);
        mApp = (ImibabyApp) getApplication();
        curWatch = ((ImibabyApp) getApplication()).getCurUser().getFocusWatch();
        attriList = new ArrayList<Integer>();
        final Intent intent = this.getIntent();
        initSelRelation = selRelation = intent.getIntExtra("attri",-1);
        number = intent.getStringExtra("number");
        sub_number = intent.getStringExtra("sub_number");
        attriList = new ArrayList<Integer>();
        String jsonStr = myApp.getStringValue(curWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY,null);
        ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
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
                    Intent data = new Intent();
                    data.putExtra("attri",selRelation);
                    setResult(RESULT_OK,data);
                    finish();
                    //adapter.notifyDataSetChanged();
                }

            }
        });
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.iv_title_back);
        btnBack.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getText(R.string.group_nickname));

        int[] img_heads = {R.drawable.relation_6, R.drawable.relation_7, R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_6, R.drawable.relation_7,R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_6,R.drawable.relation_7,R.drawable.relation_6,R.drawable.relation_7,
                R.drawable.relation_8,R.drawable.relation_9,R.drawable.relation_8,R.drawable.relation_9};
        String[] details = {getResources().getString(R.string.relation_8),getResources().getString(R.string.relation_9), getResources().getString(R.string.relation_10),
                getResources().getString(R.string.relation_11),getResources().getString(R.string.relation_12),getResources().getString(R.string.relation_13),
                getResources().getString(R.string.relation_14),getResources().getString(R.string.relation_15),getResources().getString(R.string.relation_16),
                getResources().getString(R.string.relation_17),getResources().getString(R.string.relation_18),getResources().getString(R.string.relation_19),
                getResources().getString(R.string.relation_20),getResources().getString(R.string.relation_21),getResources().getString(R.string.relation_22),
                getResources().getString(R.string.relation_23)};
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if(requestCode ==1 && resultCode == 1){
            data.putExtra("attri",selRelation);
            setResult(2,data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
