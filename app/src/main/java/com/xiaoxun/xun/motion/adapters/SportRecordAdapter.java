package com.xiaoxun.xun.motion.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.networkv2.beans.SportRecordBean;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SportRecordAdapter extends RecyclerView.Adapter {

    private ArrayList<SportRecordBean> mSportList;
    private Context mContext;
    private InterfacesUtil.OnRecyclerViewItemClickListener listener;

    public InterfacesUtil.OnRecyclerViewItemClickListener getListener() {
        return listener;
    }

    public void setListener(InterfacesUtil.OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public SportRecordAdapter(Context mContext, ArrayList<SportRecordBean> mSportList) {
        this.mSportList = mSportList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        switch (i){
            case 0:
                viewHolder = new RecordTitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.motion_record_title, viewGroup, false));
                break;
            case 1:
            default:
                viewHolder = new RecordStartViewHolder(LayoutInflater.from(mContext).inflate(R.layout.motion_record_start, viewGroup, false));
                break;
            case 2:
                viewHolder = new RecordCenterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.motion_record_center, viewGroup, false));
                break;
            case 3:
                viewHolder = new RecordEndViewHolder(LayoutInflater.from(mContext).inflate(R.layout.motion_record_end, viewGroup, false));
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MotionItemHolder)viewHolder).bindTo(mSportList.get(i),"");
    }

    @Override
    public int getItemCount() {
        return mSportList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mSportList.get(position).getmRecordType();
    }

    private class RecordTitleViewHolder extends RecyclerView.ViewHolder implements MotionItemHolder {

        TextView mTvDate;

        public RecordTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvDate = itemView.findViewById(R.id.tv_date);
        }

        @Override
        public void bindTo(SportRecordBean msg, String time) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.CHINA);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
            try {
                String mformat = sdf1.format(sdf.parse(msg.getmDate()));
                mTvDate.setText(mformat);
            }catch (Exception e){
                mTvDate.setText(
                        msg.getmDate());
            }
        }
    }

    private class RecordStartViewHolder extends RecyclerView.ViewHolder implements MotionItemHolder{

        TextView mTvValue0;
        TextView mTvValue1;
        TextView mTvValue2;

        public RecordStartViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvValue0 = itemView.findViewById(R.id.tv_vaule_0);
            mTvValue1 = itemView.findViewById(R.id.tv_vaule_1);
            mTvValue2 = itemView.findViewById(R.id.tv_vaule_2);
        }

        @Override
        public void bindTo(SportRecordBean msg, String time) {
            mTvValue0.setText(String.valueOf(msg.getmSumKal()));
            mTvValue1.setText(TimeUtil.formatTimeMs(msg.getmSumMin()*1000, true));
            mTvValue2.setText(String.valueOf(msg.getmSumNum()));
        }
    }

    private class RecordCenterViewHolder extends RecyclerView.ViewHolder implements MotionItemHolder{

        ImageView mHeadIcon;
        TextView mValue;
        TextView mValueUnit;
        TextView mSubValue0;
        TextView mSubValue1;
        TextView mSubValue2;
        Group mGroupKal;
        CardView mCardViewScore;
        ConstraintLayout mLayoutBack;
        TextView mTvScore;

        public RecordCenterViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeadIcon = itemView.findViewById(R.id.iv_icon);
            mValue = itemView.findViewById(R.id.tv_motion_value);
            mSubValue0 = itemView.findViewById(R.id.tv_motion_time_value);
            mSubValue1 = itemView.findViewById(R.id.tv_motion_kal_value);
            mSubValue2 = itemView.findViewById(R.id.tv_motion_date);
            mValueUnit = itemView.findViewById(R.id.tv_motion_hint);
            mGroupKal = itemView.findViewById(R.id.group_kal);
            mCardViewScore = itemView.findViewById(R.id.cardview_score);
            mLayoutBack = itemView.findViewById(R.id.layout_background);
            mTvScore = itemView.findViewById(R.id.tv_show_score);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void bindTo(SportRecordBean msg, String time) {
            mHeadIcon.setBackgroundResource(msg.getmSubHeadIcon());
            mValue.setText(String.valueOf(msg.getmSubValue()));
            mSubValue0.setText(TimeUtil.formatTimeMs(Long.valueOf(msg.getmSubDurTime()), true));
            mSubValue1.setText(String.valueOf(msg.getmSubKal())+mContext.getString(R.string.steps_unit_kiloCard));
            mSubValue2.setText(TimeUtil.getMonthDayByymdhmss(msg.getmSubDate()));
            mValueUnit.setText(msg.getmSubValueUnit());

            if(!TextUtils.isEmpty(msg.getIsTest()) && "1".equals(msg.getIsTest())){
                mCardViewScore.setVisibility(View.VISIBLE);
                mLayoutBack.setBackgroundResource(MotionUtils.getBackGroundRid(msg.getTestScore()));
                mTvScore.setText(MotionUtils.getTvScore(mContext,msg.getTestScore()));
                mGroupKal.setVisibility(View.GONE);
            }else{
                mCardViewScore.setVisibility(View.GONE);
                if(MotionUtils.isShowKal(msg.getSportType())){
                    mGroupKal.setVisibility(View.VISIBLE);
                }else{
                    mGroupKal.setVisibility(View.GONE);
                }
            }

        }
    }

    private class RecordEndViewHolder extends RecyclerView.ViewHolder implements MotionItemHolder{

        ImageView mHeadIcon;
        TextView mValue;
        TextView mSubValue0;
        TextView mSubValue1;
        TextView mSubValue2;
        TextView mValueUnit;
        Group mGroupKal;
        CardView mCardViewScore;
        ConstraintLayout mLayoutBack;
        TextView mTvScore;

        public RecordEndViewHolder(@NonNull View itemView) {
            super(itemView);

            mHeadIcon = itemView.findViewById(R.id.iv_icon);
            mValue = itemView.findViewById(R.id.tv_motion_value);
            mSubValue0 = itemView.findViewById(R.id.tv_motion_time_value);
            mSubValue1 = itemView.findViewById(R.id.tv_motion_kal_value);
            mSubValue2 = itemView.findViewById(R.id.tv_motion_date);
            mValueUnit = itemView.findViewById(R.id.tv_motion_hint);
            mGroupKal = itemView.findViewById(R.id.group_kal);
            mCardViewScore = itemView.findViewById(R.id.cardview_score);
            mLayoutBack = itemView.findViewById(R.id.layout_background);
            mTvScore = itemView.findViewById(R.id.tv_show_score);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void bindTo(SportRecordBean msg, String time) {
            mHeadIcon.setBackgroundResource(msg.getmSubHeadIcon());
            mValue.setText(String.valueOf(msg.getmSubValue()));
            mSubValue0.setText(TimeUtil.formatTimeMs(Long.valueOf(msg.getmSubDurTime()), true));
            mSubValue1.setText(String.valueOf(msg.getmSubKal())+mContext.getString(R.string.steps_unit_kiloCard));
            mSubValue2.setText(TimeUtil.getMonthDayByymdhmss(msg.getmSubDate()));
            mValueUnit.setText(msg.getmSubValueUnit());

            if(!TextUtils.isEmpty(msg.getIsTest()) && "1".equals(msg.getIsTest())){
                mCardViewScore.setVisibility(View.VISIBLE);
                mLayoutBack.setBackgroundResource(MotionUtils.getBackGroundRid(msg.getTestScore()));
                mTvScore.setText(MotionUtils.getTvScore(mContext,msg.getTestScore()));
                mGroupKal.setVisibility(View.GONE);
            }else{
                mCardViewScore.setVisibility(View.GONE);
                if(MotionUtils.isShowKal(msg.getSportType())){
                    mGroupKal.setVisibility(View.VISIBLE);
                }else{
                    mGroupKal.setVisibility(View.GONE);
                }
            }
        }
    }
}
