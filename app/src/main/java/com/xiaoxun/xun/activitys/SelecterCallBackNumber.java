package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelecterCallBackNumber extends NormalActivity implements MsgCallback {

    private ImibabyApp mApp;
    private ListView whitePhoneListView;
    private MySimpleAdapter adapter;
    private ImageButton mTitleBack;
    ArrayList<HashMap<String, Object>> listItem;
    private String mWatchEid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecter_call_back_number);
        mApp = (ImibabyApp)getApplication();
        mWatchEid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.call_back_member_num));
        mTitleBack = findViewById(R.id.iv_title_back);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listItem = new ArrayList<>();
        getDataFromLocalAndShow();
        String callbacknum = mApp.getStringValue(mWatchEid + Const.SHARE_PREF_CALL_BACK_NUMBER,Const.DEFAULT_NEXT_KEY);
        adapter = new MySimpleAdapter(this, listItem, R.layout.phone_white_list_item,
                new String[]{"imgHead", "memberName", "phoneNumber"},
                new int[]{R.id.iv_member_head, R.id.tv_member_name, R.id.tv_info},callbacknum);
        whitePhoneListView = findViewById(R.id.phone_white_listview);
        whitePhoneListView.setAdapter(adapter);
        whitePhoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhoneNumber formalPhoneNum = (PhoneNumber) listItem.get(position).get("numberObject");
                if (formalPhoneNum.subNumber != null) {
                    mApp.setValue(mWatchEid + Const.SHARE_PREF_CALL_BACK_NUMBER, formalPhoneNum.subNumber);
                } else {
                    mApp.setValue(mWatchEid + Const.SHARE_PREF_CALL_BACK_NUMBER, formalPhoneNum.number);
                }
                mApp.setValue(mWatchEid + Const.SHARE_PREF_CALL_BACK_ATTRI, myApp.getRelation(formalPhoneNum));
                finish();
            }
        });
    }
    private void getDataFromLocalAndShow(){
        String jsonStr = myApp.getStringValue(mWatchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        ArrayList<PhoneNumber> mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
        int size = mBindWhiteList.size();

        for (int i = 0; i < size; i++) {
            PhoneNumber phoneNumberS =  mBindWhiteList.get(i);

            if (phoneNumberS.contactType==2){
                //contactType为2表示手表好友，不显示手表好友
                continue;
            }
            if (!isNumberExist(phoneNumberS.number)) {
                if (phoneNumberS.number != null) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.number = phoneNumberS.number;
                    phoneNumber.attri = phoneNumberS.attri;
                    phoneNumber.timeStampId = phoneNumberS.timeStampId;
                    phoneNumber.nickname = phoneNumberS.nickname;
                    phoneNumber.avatar = phoneNumberS.avatar;
                    addAdapterItem(phoneNumber);
                }
            }
            if (!isNumberExist(phoneNumberS.subNumber)) {
                if (phoneNumberS.subNumber != null && phoneNumberS.subNumber.length() != 0) {
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.subNumber = phoneNumberS.subNumber;
                    phoneNumber.attri = phoneNumberS.attri;
                    phoneNumber.timeStampId = phoneNumberS.timeStampId;
                    phoneNumber.nickname = phoneNumberS.nickname;
                    phoneNumber.avatar = phoneNumberS.avatar;
                    addAdapterItem(phoneNumber);
                }
            }
        }

    }
    private void addAdapterItem(PhoneNumber phoneNumber) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        setItemAdapterMap(map, phoneNumber);
        listItem.add(map);
    }

    private void setItemAdapterMap(HashMap<String, Object> map, PhoneNumber phoneNumber) {
        if (phoneNumber.attri == 0) {
            map.put("memberName", getText(R.string.relation_0));
            map.put("imgHead", R.drawable.relation_0);
        } else if(phoneNumber.attri == 1){
            map.put("memberName", getText(R.string.relation_1));
            map.put("imgHead", R.drawable.relation_1);
        }else if(phoneNumber.attri == 2){
            map.put("memberName", getText(R.string.relation_2));
            map.put("imgHead", R.drawable.relation_2);
        }else if(phoneNumber.attri == 3){
            map.put("memberName", getText(R.string.relation_3));
            map.put("imgHead", R.drawable.relation_3);
        }else if(phoneNumber.attri == 4){
            map.put("memberName", getText(R.string.relation_4));
            map.put("imgHead", R.drawable.relation_4);
        }else if(phoneNumber.attri == 5){
            map.put("memberName", getText(R.string.relation_5));
            map.put("imgHead", R.drawable.relation_5);
        } else if(phoneNumber.attri == 6){
            map.put("memberName", getText(R.string.relation_6));
            map.put("imgHead", R.drawable.relation_4);
        }else if(phoneNumber.attri == 7){
            map.put("memberName", getText(R.string.relation_7));
            map.put("imgHead", R.drawable.relation_5);
        }else if(phoneNumber.attri == 8){
            map.put("memberName", getText(R.string.relation_8));
            map.put("imgHead", R.drawable.relation_6);
        }else if(phoneNumber.attri == 9){
            map.put("memberName", getText(R.string.relation_9));
            map.put("imgHead", R.drawable.relation_7);
        }else if(phoneNumber.attri == 10){
            map.put("memberName", getText(R.string.relation_10));
            map.put("imgHead", R.drawable.relation_6);
        } else if(phoneNumber.attri == 11){
            map.put("memberName", getText(R.string.relation_11));
            map.put("imgHead", R.drawable.relation_7);
        }else if(phoneNumber.attri == 12){
            map.put("memberName", getText(R.string.relation_12));
            map.put("imgHead", R.drawable.relation_6);
        }else if(phoneNumber.attri == 13){
            map.put("memberName", getText(R.string.relation_13));
            map.put("imgHead", R.drawable.relation_7);
        }else if(phoneNumber.attri == 14){
            map.put("memberName", getText(R.string.relation_14));
            map.put("imgHead", R.drawable.relation_6);
        }else if(phoneNumber.attri == 15){
            map.put("memberName", getText(R.string.relation_15));
            map.put("imgHead", R.drawable.relation_7);
        } else if(phoneNumber.attri == 16){
            map.put("memberName", getText(R.string.relation_16));
            map.put("imgHead", R.drawable.relation_6);
        }else if(phoneNumber.attri == 17){
            map.put("memberName", getText(R.string.relation_17));
            map.put("imgHead", R.drawable.relation_7);
        }else if(phoneNumber.attri == 18){
            map.put("memberName", getText(R.string.relation_18));
            map.put("imgHead", R.drawable.relation_6);
        }else if(phoneNumber.attri == 19){
            map.put("memberName", getText(R.string.relation_19));
            map.put("imgHead", R.drawable.relation_7);
        }else if(phoneNumber.attri == 20){
            map.put("memberName", getText(R.string.relation_20));
            map.put("imgHead", R.drawable.relation_8);
        } else if(phoneNumber.attri == 21){
            map.put("memberName", getText(R.string.relation_21));
            map.put("imgHead", R.drawable.relation_9);
        }else if(phoneNumber.attri == 22){
            map.put("memberName", getText(R.string.relation_22));
            map.put("imgHead", R.drawable.relation_8);
        }else if(phoneNumber.attri == 23){
            map.put("memberName", getText(R.string.relation_23));
            map.put("imgHead", R.drawable.relation_9);
        }else{
            if (phoneNumber.nickname!=null){
                map.put("memberName",phoneNumber.nickname);
            }else {
                map.put("memberName", getText(R.string.device_lesson_custom));
            }
            map.put("imgHead", R.drawable.relation_custom);
        }

        if(phoneNumber.subNumber!=null){
            map.put("phoneNumber",getText(R.string.sub_num_tag)+phoneNumber.subNumber);
        }else{
            map.put("phoneNumber", getText(R.string.main_num_tag)+phoneNumber.number);
        }

        map.put("numberObject", phoneNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

    }

    private class MySimpleAdapter extends SimpleAdapter {
        private String mCallBackNum;
        private Context context;
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to, String callbacknum) {
            super(context, data, resource, from, to);
            mCallBackNum = callbacknum;
            this.context = context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            PhoneNumber formalPhoneNum = (PhoneNumber) listItem.get(position).get("numberObject");
            String tel_num;
            if(formalPhoneNum.subNumber != null){
                tel_num = formalPhoneNum.subNumber;
            }else {
                tel_num = formalPhoneNum.number;
             }
            if(tel_num.equals(mCallBackNum)) {
                convertView.findViewById(R.id.iv_member_next).setBackgroundResource(R.drawable.radio_bt_1);
            }else{
                convertView.findViewById(R.id.iv_member_next).setBackgroundResource(R.drawable.radio_bt_0);
            }

            final ImageView ivAvatar= convertView.findViewById(R.id.iv_member_head);
            if (formalPhoneNum.avatar != null) {
                Bitmap headBitmap = new ImageDownloadHelper(context).downloadImage(formalPhoneNum.avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                    @Override
                    public void onImageDownload(String url, Bitmap bitmap) {
                        Drawable headDrawable = new BitmapDrawable(context.getResources(), bitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                    }
                });
                if (headBitmap != null) {
                    Drawable headDrawable = new BitmapDrawable(context.getResources(), headBitmap);
                    ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                }
            }
            return convertView;
        }
    }

    private boolean isNumberExist(String number) {
        for (int i = 0; i < listItem.size(); i++) {
            PhoneNumber phoneNumberItem = (PhoneNumber) listItem.get(i).get("numberObject");
            if (number != null && number.length() > 0) {
                if (phoneNumberItem.number != null && phoneNumberItem.number.length() > 0 && phoneNumberItem.number.equals(number)) {
                    return true;
                }
                if (phoneNumberItem.subNumber != null && phoneNumberItem.subNumber.length() > 0 && phoneNumberItem.subNumber.equals(number)) {
                    return true;
                }
            }
        }
        return false;
    }
}
