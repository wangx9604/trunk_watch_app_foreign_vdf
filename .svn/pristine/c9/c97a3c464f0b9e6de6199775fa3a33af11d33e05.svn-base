package com.xiaoxun.xun.adapter;

import android.content.Context;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.R;


/**
 * Created by huangyouyang on 2016/12/23.
 */

public class DeviceModeAdapter extends RecyclerView.Adapter<DeviceModeAdapter.DeviceModeViewHolder> {

    private Context context;
    private ArrayMap<Integer, String[]> modeMap;
    private int selectPosition;

    public DeviceModeAdapter(Context context, ArrayMap<Integer, String[]> modeMap) {
        this.context = context;
        this.modeMap = modeMap;
    }

    public void selectItem(int position) {
        this.selectPosition = position;
    }

    @Override
    public int getItemCount() {
        if (modeMap != null) {
            return modeMap.size();
        } else {
            return 0;
        }
    }

    @Override
    public DeviceModeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_watch_select_mode, null);
        return new DeviceModeAdapter.DeviceModeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceModeViewHolder holder, int position) {

        String[] array = modeMap.get(position);
        holder.modeTitle.setText(array[0]);
        holder.modeDetail.setText(array[1]);

        if (position == selectPosition)
            holder.ivSelect.setImageResource(R.drawable.select_0);
        else
            holder.ivSelect.setImageResource(R.drawable.select_2);

        if (mOnItemClickLitener != null) {
            holder.modeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    modeMap.get(position);
                    mOnItemClickLitener.onItemClick(v, position);
                }
            });
        }
    }


    class DeviceModeViewHolder extends RecyclerView.ViewHolder {

        View modeView;
        TextView modeTitle;
        TextView modeDetail;
        ImageView ivSelect;

        public DeviceModeViewHolder(View itemView) {
            super(itemView);

            modeView=itemView.findViewById(R.id.watch_mode_view);
            modeTitle = itemView.findViewById(R.id.watch_mode_title);
            modeDetail = itemView.findViewById(R.id.watch_mode_detail);
            ivSelect = itemView.findViewById(R.id.iv_select_mode);
        }
    }

    private DeviceModeAdapter.OnRecyclerViewItemClickListener mOnItemClickLitener;
    public void setOnItemClickLitener(DeviceModeAdapter.OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
