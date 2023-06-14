package com.xiaoxun.xun.health.motion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.motion.beans.RecordDetailBean;

import java.util.ArrayList;

public class RecordDetailAdapter extends RecyclerView.Adapter<RecordDetailAdapter.RecordDetailViewHolder> {

    private ArrayList<RecordDetailBean> mList;
    private Context mContext;

    public RecordDetailAdapter(Context context, ArrayList<RecordDetailBean> mDetailList) {
        this.mContext = context;
        this.mList = mDetailList;
    }

    @NonNull
    @Override
    public RecordDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecordDetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.motion_record_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordDetailViewHolder recordDetailViewHolder, int i) {
        RecordDetailBean mBean = mList.get(i);
        recordDetailViewHolder.mIvIcon.setBackgroundResource(mBean.getmHeadIconId());
        recordDetailViewHolder.mValue0.setText(mBean.getmTitle());
        recordDetailViewHolder.mValue1.setText(mBean.getmValue());
        recordDetailViewHolder.mValue2.setText(mBean.getmUnit());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class RecordDetailViewHolder extends RecyclerView.ViewHolder{

        ImageView mIvIcon;
        TextView mValue0;
        TextView mValue1;
        TextView mValue2;

        public RecordDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mValue0 = itemView.findViewById(R.id.tv_value_0);
            mValue1 = itemView.findViewById(R.id.tv_value_1);
            mValue2 = itemView.findViewById(R.id.tv_value_2);
        }
    }
}
