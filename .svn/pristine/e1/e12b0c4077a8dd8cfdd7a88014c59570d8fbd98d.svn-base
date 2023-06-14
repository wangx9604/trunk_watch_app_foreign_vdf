package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.OnWatchItemClick;
import com.xiaoxun.xun.utils.ImageUtil;

import java.util.Collections;
import java.util.Comparator;

public class AllWatchAdapter extends RecyclerView.Adapter<AllWatchAdapter.WatchItemHolder> {
    private Context context;
    private ImibabyApp mApp;
    private OnWatchItemClick onWatchItemClick;

    public AllWatchAdapter(Activity activity) {
        this.context = activity;
        mApp = (ImibabyApp) activity.getApplication();
        sortWatchList();
    }

    @NonNull
    @Override
    public WatchItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_item, parent, false);
        WatchItemHolder holder = new WatchItemHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final WatchItemHolder watchItemHolder, final int position) {

        final WatchData watch = mApp.getWatchList().get(watchItemHolder.getAdapterPosition());
        showHead(watchItemHolder.headView, watch);
        watchItemHolder.nameText.setText(watch.getNickname());

        if (watch.isSupportPhoneCall()) {
            watchItemHolder.groupCall.setVisibility(View.VISIBLE);
        } else {
            watchItemHolder.groupCall.setVisibility(View.GONE);
        }
        if (watch.isSupportVideoCall()) {
            watchItemHolder.groupVideoCall.setVisibility(View.VISIBLE);
        } else {
            watchItemHolder.groupVideoCall.setVisibility(View.GONE);
        }
        if (watch.isSupportListen()) {
            watchItemHolder.groupListen.setVisibility(View.VISIBLE);
        } else {
            watchItemHolder.groupListen.setVisibility(View.GONE);
        }

        watchItemHolder.callView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWatchItemClick != null) {
                    onWatchItemClick.onCall(watch, watchItemHolder.getAdapterPosition());
                }
            }
        });
        watchItemHolder.videoCallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWatchItemClick != null) {
                    onWatchItemClick.onVideoCall(watch, watchItemHolder.getAdapterPosition());
                }
            }
        });
        watchItemHolder.listenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWatchItemClick != null) {
                    onWatchItemClick.onListen(watch, watchItemHolder.getAdapterPosition());
                }
            }
        });
        watchItemHolder.findWatchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onWatchItemClick != null) {
                    onWatchItemClick.onFindWatch(watch, watchItemHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mApp.getWatchList().size();
    }

    private void showHead(ImageView headView, WatchData watch) {
        String headpath = watch.getHeadPath();
        ImageUtil.setMaskImage(headView, R.drawable.head_1,
                mApp.getHeadDrawableByFile(context.getResources(), headpath, watch.getEid(), R.drawable.default_head));
    }

    public void setOnWatchItemClick(OnWatchItemClick onWatchItemClick) {
        this.onWatchItemClick = onWatchItemClick;
    }

    public void sortWatchList() {
        Collections.sort(mApp.getCurUser().getWatchList(), new Comparator<WatchData>() {
            @Override
            public int compare(WatchData lhs, WatchData rhs) {
                if (lhs == null || lhs.getNickname() == null)
                    return 1;
                if (rhs == null || rhs.getNickname() == null)
                    return -1;
                return lhs.getNickname().compareTo(rhs.getNickname());
            }
        });
    }

    class WatchItemHolder extends RecyclerView.ViewHolder {
        ImageView headView;
        TextView nameText;
        ImageView callView;
        ImageView videoCallView;
        ImageView listenView;
        ImageView findWatchView;

        Group groupCall;
        Group groupVideoCall;
        Group groupListen;
        Group groupFindWatch;

        public WatchItemHolder(View view) {
            super(view);
            headView = (ImageView) view.findViewById(R.id.iv_watch_head);
            nameText = (TextView) view.findViewById(R.id.tv_watch_name);
            callView = (ImageView) view.findViewById(R.id.iv_call);
            videoCallView = (ImageView) view.findViewById(R.id.iv_video_call);
            listenView = (ImageView) view.findViewById(R.id.iv_listen);
            findWatchView = (ImageView) view.findViewById(R.id.iv_find_watch);

            groupCall = (Group) view.findViewById(R.id.group_call);
            groupVideoCall = (Group) view.findViewById(R.id.group_video_call);
            groupListen = (Group) view.findViewById(R.id.group_listen);
            groupFindWatch = (Group) view.findViewById(R.id.group_find_watch);
        }
    }
}
