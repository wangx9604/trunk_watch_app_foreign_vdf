package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class ChatRightVideoCallHolder extends ChatRightHolder {

    private TextView mRightTextContent;

    public ChatRightVideoCallHolder(View view, Activity activity, int activityType, WatchData watch, OnItemClickListener listener) {
        super(view, activity, activityType, watch, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mRightTextContent = (TextView) view.findViewById(R.id.tv_right_chat_content);
    }

    @Override
    protected void showCustomView() {
        showTextContent();
        showTextSendState();

        if (activityType != 2) {
            mRightTextContent.setOnLongClickListener(this);
            mRightTextContent.setOnClickListener(this);
        }
    }

    private void showTextContent() {
        String content = chat.getmAudioPath();
        JSONObject json = (JSONObject) JSONValue.parse(content);
        int callType = (Integer) json.get("callType");
        if (callType == 1) {
            mRightTextContent.setText(R.string.video_call_not_accept);
        } else if (callType == 2) {
            mRightTextContent.setText(R.string.video_call_reject);
        } else if (callType == 3) {
            mRightTextContent.setText(R.string.video_call_cancel);
        } else if (callType == 5) {
            mRightTextContent.setText(R.string.video_call_calling);
        } else if (callType == 4) {
            int duration = (Integer) json.get("Duration");
            mRightTextContent.setText(activity.getString(R.string.video_call_duration, TimeUtil.formatTimeMs(duration * 1000, false)));
        }
    }

    private void showTextSendState() {
        switch (chat.getmSended()) {
            case ChatMsgEntity.CHAT_SEND_STATE_SENDING:
            case ChatMsgEntity.CHAT_SEND_STATE_RETRYING:
                mRightRetry.setVisibility(View.GONE);
                mRightWaiting.setVisibility(View.VISIBLE);
                break;
            case ChatMsgEntity.CHAT_SEND_STATE_SUCCESS:
                mRightRetry.setVisibility(View.GONE);
                mRightWaiting.setVisibility(View.GONE);
                break;
            case ChatMsgEntity.CHAT_SEND_STATE_FAIL:
                mRightRetry.setBackgroundResource(R.drawable.refresh_0);
                mRightRetry.setVisibility(View.VISIBLE);
                mRightWaiting.setVisibility(View.GONE);
                mRightRetry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(Const.ACTION_RESEND_CHAT);
                        it.putExtra("position", getLayoutPosition());
                        activity.sendBroadcast(it);
                    }
                });
                break;
            default:
                break;
        }
    }
}
