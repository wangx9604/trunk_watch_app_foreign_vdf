package com.xiaoxun.xun.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CallBack;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;

import java.util.List;

/**
 * @author cuiyufeng
 * @Description: MigrationDialog
 * @date 2018/8/29 13:53
 */
public class MigrationDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private Button btn_nomigrate;
    private Button btn_datamigrate;
    private TextView tv_migration_warning;
    private GridView gridView;
    private List<WatchData> watchDataList;
    private String curWatchEid;
    private String oldWatchEid;
    private CallBack.ReturnCallback<String> returnCallback;
    ImibabyApp myApp;

    public MigrationDialog(ImibabyApp myApp,Context context,List<WatchData> watchDataList,String curWatchEid, CallBack.ReturnCallback<String> returnCallback) {
        super(context, R.style.Theme_DataSheet);
        this.context=context;
        this.watchDataList=watchDataList;
        this.curWatchEid=curWatchEid;
        this.returnCallback=returnCallback;
        this.myApp=myApp;
    }

    public MigrationDialog(Context context){
        super(context, R.style.Theme_DataSheet);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migration_data);
        btn_nomigrate= findViewById(R.id.btn_nomigrate);
        btn_datamigrate= findViewById(R.id.btn_datamigrate);
        tv_migration_warning= findViewById(R.id.tv_migration_warning);
        btn_nomigrate.setOnClickListener(this);
        btn_datamigrate.setOnClickListener(this);
        gridView= findViewById(R.id.gridView_migration);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉默认点击背景
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id){
                //Toast.makeText(context, "pic" + position, Toast.LENGTH_SHORT).show();
                //Log.i("cui","EID="+watchDataList.get(position).getEid());
                oldWatchEid=watchDataList.get(position).getEid();

                for (int i = 0; i < watchDataList.size(); i++) {
                    if(position==i){
                        ImageView imageView = adapterView.getChildAt(i).findViewById(R.id.iv_select_item);
                        imageView.setBackgroundResource(R.drawable.radio_bt_1);
                    }else{
                        ImageView imageView = adapterView.getChildAt(i).findViewById(R.id.iv_select_item);
                        imageView.setBackgroundResource(R.drawable.radio_bt_0);
                    }
                }
            }
        });
    }

    BaseAdapter adapter =new BaseAdapter() {
        @Override
        public int getCount() {
            if(watchDataList!=null && watchDataList.size()>0){
                return watchDataList.size();
            }else{
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return watchDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.grid_datamigration_item, null);
                viewHolder = new ViewHolder();

                viewHolder.tv_model_item = convertView.findViewById(R.id.tv_model_item);
                viewHolder.image_head_item = convertView.findViewById(R.id.image_head_item);
                viewHolder.iv_select_item = convertView.findViewById(R.id.iv_select_item);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
           WatchData watchData=watchDataList.get(position);
            viewHolder.tv_model_item.setText(""+watchData.getNickname());
            ImageUtil.setMaskImage(viewHolder.image_head_item, R.drawable.peopletab_mask, myApp.getHeadDrawableByFile(context.getResources(),
                    myApp.getCurUser().getHeadPathByEid(watchData.getEid()), watchData.getEid(), R.drawable.small_default_head));
            return convertView;
        }

        class ViewHolder{
            public TextView tv_model_item;
            public ImageView image_head_item;
            public ImageView iv_select_item;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_nomigrate:
                returnCallback.back("false");
                //Toast.makeText(context, "BU迁移" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_datamigrate:
                if(TextUtils.isEmpty(oldWatchEid)){
                    Toast.makeText(context, context.getResources().getString(R.string.migration_dlg_txt) , Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(context, "数据迁移" , Toast.LENGTH_SHORT).show();
                    returnCallback.back(oldWatchEid);
                }
                break;
        }
    }
}
