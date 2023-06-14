package com.xiaoxun.xun.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.NoticeMsgData;

import java.util.ArrayList;

/**
 * Created by huangqilin on 2016/7/25.
 */
public class MsgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NoticeMsgData> mDatas;
    private Activity mActivity;
    public MsgListAdapter(Activity activity, ArrayList<NoticeMsgData> msgDatas){
        this.mDatas = msgDatas;
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case NoticeMsgData.MSG_TYPE_SAFE_AREA:
            case NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sefe_area_notice_item, parent, false);
                return new SafeAreaItemHolder(view,mActivity);
            case NoticeMsgData.MSG_TYPE_SOS_LOCATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sosloc_notice_item, parent, false);
                return new SosLocationItemHolder(view,mActivity);
            case NoticeMsgData.MSG_TYPE_STAEPSRANKS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stepsranks_notice_item, parent, false);
                return new StepsRanksItemHolder(view, mActivity);
            case 1:
            case 4:
            case 5:
            case 10:
            case 12:
            case 14:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_notice_item, parent, false);
                return new NormalNoticeItemHolder(view,mActivity);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == 0) {
            ((RecyclerItemHolder) holder).bindTo(mDatas.get(position), Const.DEFAULT_NEXT_KEY);
        }else{
            ((RecyclerItemHolder) holder).bindTo(mDatas.get(position),mDatas.get(position-1).getmTimeStamp());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getmType();
    }
}
