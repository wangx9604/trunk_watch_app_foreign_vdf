package com.xiaoxun.xun.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.interfaces.ChatMsgViewHolder;
import com.xiaoxun.xun.utils.TimeUtil;

public class ChatLeftSosLocationHolder extends RecyclerView.ViewHolder implements ChatMsgViewHolder {

    private Activity activity;
    private ImibabyApp mApp;
    private int activityType;
    private ChatMsgEntity chat;
    private TextView mNotifyLocation;
    private RelativeLayout mSendTimeLayout;
    private TextView mSendTime;

    public ChatLeftSosLocationHolder(View view, Activity activity, int activityType) {
        super(view);
        this.activity = activity;
        mApp = (ImibabyApp) activity.getApplication();
        this.activityType = activityType;
        initViewHolder(view);
    }

    @Override
    public void bindTo(ChatMsgEntity chat, String lastTime) {
        this.chat = chat;
        setSendTime(lastTime);
        showSosLocationView();
    }

    private void initViewHolder(View view) {
        mSendTimeLayout = view.findViewById(R.id.send_time);
        mSendTime = view.findViewById(R.id.chat_send_time);
        mNotifyLocation = view.findViewById(R.id.notify_location);
    }

    private void setSendTime(String lastTime) {
        if (!lastTime.equals(Const.DEFAULT_NEXT_KEY)) {
            long t1 = TimeUtil.getMillisByTime(chat.getmDate());
            long t2 = TimeUtil.getMillisByTime(lastTime);
            long interval = Math.abs(t1 - t2) / 1000;
            if (interval > Const.CHAT_INTERVAL_TIME || (activityType == 2 && getLayoutPosition() == 1)) {
                mSendTimeLayout.setVisibility(View.VISIBLE);
                mSendTime.setVisibility(View.VISIBLE);
                mSendTime.setText(TimeUtil.getTime(activity, TimeUtil.chnToLocalTimestamp(chat.getmDate())));
            } else {
                mSendTimeLayout.setVisibility(View.GONE);
                mSendTime.setVisibility(View.GONE);
            }
        } else {
            mSendTimeLayout.setVisibility(View.VISIBLE);
            mSendTime.setVisibility(View.VISIBLE);
            mSendTime.setText(TimeUtil.getTime(activity, TimeUtil.chnToLocalTimestamp(chat.getmDate())));
        }
    }

    private void showSosLocationView() {
        mNotifyLocation.setText(chat.getmUserName());
    }
}