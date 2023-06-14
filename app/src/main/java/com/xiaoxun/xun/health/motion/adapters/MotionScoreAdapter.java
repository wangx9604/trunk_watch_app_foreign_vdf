package com.xiaoxun.xun.health.motion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.networkv2.beans.MotionScoreBean;
import com.xiaoxun.xun.utils.TimeUtil;

import java.util.ArrayList;

public class MotionScoreAdapter extends RecyclerView.Adapter<MotionScoreAdapter.ScoreViewHolder> {

    private Context context;
    private ArrayList<MotionScoreBean> mList;

    public MotionScoreAdapter(Context context, ArrayList<MotionScoreBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ScoreViewHolder(LayoutInflater.from(context).inflate(R.layout.motion_score_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder scoreViewHolder, int i) {
        MotionScoreBean mbeans = mList.get(i);
        if(mbeans.getPoints() > 0){
            scoreViewHolder.mValue0.setText("+"+ mbeans.getPoints());
        }else{
            scoreViewHolder.mValue0.setText(""+ mbeans.getPoints());
        }
        scoreViewHolder.mValue1.setText(TimeUtil.formTimeByTime(mbeans.getDataTime()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder{

        TextView mValue0;
        TextView mValue1;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            mValue0 = itemView.findViewById(R.id.tv_value_0);
            mValue1 = itemView.findViewById(R.id.tv_value_1);
        }
    }
}
