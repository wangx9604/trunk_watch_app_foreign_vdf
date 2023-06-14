package com.xiaoxun.xun.ScheduleCard.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleClassBean;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class ScheduleClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ScheduleClassBean> mData;
    private InterfacesUtil.OnRecyclerViewItemLongClickListener mLongClickListener;
    private OnItemClickListener mDelectListener;
    private OnItemClickListener mAddListener;
    private Boolean isEditMode = false;

    public void setEditMode(Boolean editMode) {
        isEditMode = editMode;
    }

    public ScheduleClassAdapter(Context context, ArrayList<ScheduleClassBean> datas){
        mContext = context;
        mData = datas;
    }

    public void setmDelectListener(OnItemClickListener listener){
        mDelectListener = listener;
    }

    public void setmAddListener(OnItemClickListener listener){
        mAddListener = listener;
    }

    public void setOnLongClickListener(InterfacesUtil.OnRecyclerViewItemLongClickListener listener){
        this.mLongClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_schedule_class, viewGroup, false);
            return new ScheduleClassViewHolder(view);
        }else if(i == 2){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_schedule_class_add, viewGroup, false);
            return new ScheduleClassAddViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_schedule_class, viewGroup, false);
            return new ScheduleClassViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ScheduleClassBean mScheduleClassInfo = mData.get(i);
        if(mScheduleClassInfo.getClassType() == 1) {
            String mScheduleClassName = mScheduleClassInfo.getmClassName();
            if (!TextUtils.isEmpty(mScheduleClassName))
                ((ScheduleClassViewHolder) viewHolder).tv_show.setText(mScheduleClassName);
            if (isEditMode && mScheduleClassInfo.isCanDelete()) {
                ((ScheduleClassViewHolder) viewHolder).tv_delect.setVisibility(View.VISIBLE);
            } else {
                ((ScheduleClassViewHolder) viewHolder).tv_delect.setVisibility(View.GONE);
            }
            ((ScheduleClassViewHolder) viewHolder).tv_delect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDelectListener != null)
                        mDelectListener.onItemClick(v, i);
                }
            });
        }else{

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getClassType();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ScheduleClassViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_show;
        TextView tv_delect;

        public ScheduleClassViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_show = itemView.findViewById(R.id.tv_class_name);
            tv_delect = itemView.findViewById(R.id.tv_delete);
        }
    }

    class ScheduleClassAddViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_add;

        public ScheduleClassAddViewHolder(@NonNull View itemView) {
            super(itemView);
            final int position = getLayoutPosition();
            iv_add = itemView.findViewById(R.id.tv_class_add);
            iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mAddListener != null){
                        mAddListener.onItemClick(v, position);
                    }
                }
            });
        }
    }
}
