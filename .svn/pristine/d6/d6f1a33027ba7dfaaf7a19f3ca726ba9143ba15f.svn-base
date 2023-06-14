/**
 * Creation Date:2015-2-12
 * 
 * Copyright 
 */
package com.xiaoxun.xun.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.R;


public class XiaoMiDialogSelectAdapter extends SimpleAdapter {
	
	Context mContext ;
	private int defaultSelect = -1 ;
	private List<? extends Map<String, ?>> list;
	
    public XiaoMiDialogSelectAdapter(Context context, List<? extends Map<String, ?>> data, int resource,   
            String[] from, int[] to , int select) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mContext = context;
        defaultSelect = select;
        list = data;
    }

    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {   
        // TODO Auto-generated method stub   
        final int mPosition = position;
        //itemButtonClickPosition = mPosition ;
        convertView = super.getView(position, convertView, parent);

        ImageView image = convertView.findViewById(R.id.iv_selectimg1);
    	TextView textView = convertView.findViewById(R.id.iv_selecttext1);

        Map<String, ?> item=list.get(position);
        Boolean select = false;
        if (item.containsKey("select"))
            select = (Boolean) item.get("select");
        if ((select != null && select) || position == defaultSelect) {
            image.setVisibility(View.VISIBLE);
            textView.setTextColor(0xffdf5600);
        } else {
            image.setVisibility(View.INVISIBLE);
            textView.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
        }

        if(defaultSelect == -3 && position == 1){
            textView.setTextColor(mContext.getResources().getColor(R.color.txt_grey)) ;
        }
        return convertView;   
    }

    @Override
    public boolean isEnabled(int position) {
        return defaultSelect != -3 || position != 1;
    }
}
