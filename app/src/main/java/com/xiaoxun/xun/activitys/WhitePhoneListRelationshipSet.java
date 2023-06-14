/**
 * Creation Date:2015-3-12
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description Of The Class<br>gggss
 *
 * @author fushiqing
 * @version 1.000, 2015-10-22
 */
public class WhitePhoneListRelationshipSet extends NormalActivity implements OnClickListener {

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
    private String nickname;
    private  int trueRelation = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_white_list_relationship_set);
        mApp = (ImibabyApp) getApplication();
        curWatch = ((ImibabyApp) getApplication()).getCurUser().getFocusWatch();
        attriList = new ArrayList<Integer>();
        Intent intent = this.getIntent();
        trueRelation = initSelRelation = selRelation = intent.getIntExtra("attri",-1);
        nickname = intent.getStringExtra("nickname");
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
                if (attriList.contains(position) && position != initSelRelation && position != 8&& position != 9) {
                    return;
                } else if(position == 8){
                    Intent intent = new Intent(WhitePhoneListRelationshipSet.this, WhitePhoneListOtherRelationshipSet.class);
                    intent.putExtra("attri",initSelRelation);
                    startActivityForResult(intent, 1);
                }else{
                    if (position == 9){
                        if (curWatch.isDevice102()){//102 要判断版本号
                            if (!myApp.isControledByVersion(curWatch,false,"T26")){
                                ToastUtil.show(mApp, getResources().getString(R.string.not_support_steps));
                                return;
                            }
                        }
                    }
                    selRelation = position;
                    if (selRelation == 9){
                        if (initSelRelation>=1000){//已经是自定义的关系，换昵称，关系不需要换

                        }else {
                            trueRelation = getNextCustomAttri();
                        }
                        selRelation = trueRelation;
                        String oldNickname = null;
                        if (nickname!=null&&nickname.length()>0&&nickname.length()<=Const.CALL_NICKNAME_MAXLEN) {
                            oldNickname = nickname;
                        }
                        //打开编辑昵称
                        String hint;
                        int maxLength;
                        if (!curWatch.isNeedTTS()) {
                            hint = getText(R.string.wrong_nickname).toString();
                            maxLength = Const.NICKNAME_MAXLEN;
                            oldNickname = nickname;
                        } else {
                            hint = getText(R.string.wrong_nickname).toString();
                            maxLength = Const.NICKNAME_MAXLEN;
                        }
                        if (initSelRelation < 1000) {
                            oldNickname = "";
                        }
                        Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(WhitePhoneListRelationshipSet.this,
                                maxLength,
                                0,
                                getText(R.string.group_nickname).toString(),
                                oldNickname, hint, new CustomSelectDialogUtil.CustomDialogListener() {
                                    @Override
                                    public void onClick(View v, String text) {
                                        // TODO Auto-generated method stub
                                    }
                                },
                                getText(R.string.cancel).toString(),
                                new CustomSelectDialogUtil.CustomDialogListener() {
                                    @Override
                                    public void onClick(View v, String text) {
                                        // TODO Auto-generated method stub

                                        if (text.length() < 1) {
                                            ToastUtil.showMyToast(WhitePhoneListRelationshipSet.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
                                        }else {
                                                nickname = text;
                                                //完成编辑
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("nickname", nickname);
                                                resultIntent.putExtra("attri", trueRelation);
                                                resultIntent.putExtra("timeStampId", TimeUtil.getTimeStampLocal() + "-00000000");
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                        }
                                    }
                                },
                                getText(R.string.confirm).toString());
                        dlg.show();
                    }else {
                        trueRelation = selRelation;
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("attri", trueRelation);
                        resultIntent.putExtra("timeStampId", TimeUtil.getTimeStampLocal() + "-00000000");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        initView();
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */
    public boolean checkNameChinese(String name) {
        boolean temp = false;
        Pattern p= Pattern.compile("[\u4e00-\u9fa5]*");
        Matcher m=p.matcher(name);
        if(p.matcher(name).matches()){
            temp =  true;
        }
        return temp;
    }
    private void initView() {
        btnBack = findViewById(R.id.iv_title_back);
        btnBack.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getText(R.string.group_nickname));

        int[] img_heads = {R.drawable.relation_0, R.drawable.relation_1, R.drawable.relation_2,
                R.drawable.relation_3, R.drawable.relation_4, R.drawable.relation_5,R.drawable.relation_4,
                R.drawable.relation_5,R.drawable.relation_other,R.drawable.relation_custom};
        String[] details = {getText(R.string.relation_0).toString(), getText(R.string.relation_1).toString(),
                getText(R.string.relation_2).toString(),getText(R.string.relation_3).toString(),
                getText(R.string.relation_4).toString(), getText(R.string.relation_5).toString(),getText(R.string.relation_6).toString(),
                getText(R.string.relation_7).toString(),getText(R.string.relation_other).toString(),getText(R.string.device_lesson_custom).toString()};
        for (int i = 0; i <= 9; i++) {
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
    private  int getNextCustomAttri(){
        int  attri = 1000;
        while (true) {
            if (!attriList.contains(attri)) {
                break;
            }else {
                attri++;
            }
        }
        return  attri;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if(requestCode ==1 && resultCode == 1) { //其他关系中选择的结果
            data.putExtra("timeStampId", TimeUtil.getTimeStampLocal() + "-00000000");
        }
        setResult(RESULT_OK,data);
        finish();
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
            if (position == 9){
                if (selRelation>=1000){
                    convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_1);
                }else{
                    convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_0);
                }
            }else {
                if (selRelation == position) {
                    convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_1);
                } else {
                    convertView.findViewById(R.id.iv_selected).setBackgroundResource(R.drawable.radio_bt_0);
                }
            }
            if(attriList.contains(position) && position!=initSelRelation && position != 8&& position != 9){
                convertView.findViewById(R.id.cover).setVisibility(View.VISIBLE);
            }else{
                convertView.findViewById(R.id.cover).setVisibility(View.INVISIBLE);
            }
            if(position == 8){
                convertView.findViewById(R.id.iv_selected).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.iv_member_next).setVisibility(View.VISIBLE);
            }else{
                convertView.findViewById(R.id.iv_selected).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.iv_member_next).setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }
}
