package com.xiaoxun.xun.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangyouyang on 2016/12/6.
 */

public class DeviceSettingAdapter extends RecyclerView.Adapter<DeviceSettingAdapter.DeviceSettingViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, Object>> itemList;
    private int resource = -1;

    public DeviceSettingAdapter(Context context,ArrayList<HashMap<String, Object>> itemList){
        this.context=context;
        this.itemList=itemList;
    }

    public DeviceSettingAdapter(Context context,ArrayList<HashMap<String, Object>> itemList,int resource){
        this.context=context;
        this.itemList=itemList;
        this.resource=resource;
    }

    @Override
    public int getItemCount() {

        if ( itemList!= null) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    @Override
    public DeviceSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (resource > 0) {
            view = View.inflate(context, resource, null);
        } else {
            view = View.inflate(context, R.layout.device_setting_adapter_item, null);
        }
        return new DeviceSettingAdapter.DeviceSettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceSettingViewHolder holder, int position) {

        HashMap<String, Object> map = itemList.get(position);
        holder.time.setText((String) map.get(Const.KEY_MAP_TIME));
        holder.title.setText((String) map.get(Const.KEY_MAP_TITLE));
        holder.info.setText((String) map.get(Const.KEY_MAP_STATUS));
        holder.buttonImg.setImageResource((Integer) map.get(Const.KEY_MAP_IMG));

        if (mOnItemClickLitener != null) {
            holder.settingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(v, position);
                }
            });
        }

        if (mOnItemLongClickLitener != null) {
            holder.settingLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickLitener.onItemLongClick(v, position);
                    return false;
                }
            });
        }

        if (mOnItemOnoffLitener != null) {
            holder.buttonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemOnoffLitener.onItemOnoffClick(v, position);
                }
            });
        }
    }


    public class DeviceSettingViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        TextView verticalLine;
        TextView title;
        TextView info;
        ImageView buttonImg;
        RelativeLayout settingLayout;

        public DeviceSettingViewHolder(View itemView) {
            super(itemView);
            time= itemView.findViewById(R.id.time);
            verticalLine= itemView.findViewById(R.id.vertical_line);
            title= itemView.findViewById(R.id.title);
            info= itemView.findViewById(R.id.status);
            buttonImg= itemView.findViewById(R.id.button_img);
            settingLayout= itemView.findViewById(R.id.device_setting_layout);
        }
    }


    private DeviceSettingAdapter.OnRecyclerViewItemClickListener mOnItemClickLitener;
    public void setOnItemClickLitener(DeviceSettingAdapter.OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private DeviceSettingAdapter.OnRecyclerViewItemLongClickListener mOnItemLongClickLitener;
    public void setOnItemLongClickLitener(DeviceSettingAdapter.OnRecyclerViewItemLongClickListener mOnItemLongClickLitener) {
        this.mOnItemLongClickLitener = mOnItemLongClickLitener;
    }
    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private DeviceSettingAdapter.OnRecyclerViewItemOnoffListener mOnItemOnoffLitener;
    public void setOnItemOnoffLitener(DeviceSettingAdapter.OnRecyclerViewItemOnoffListener mOnItemOnoffLitener) {
        this.mOnItemOnoffLitener = mOnItemOnoffLitener;
    }
    public interface OnRecyclerViewItemOnoffListener {
        void onItemOnoffClick(View view, int position);
    }

}
