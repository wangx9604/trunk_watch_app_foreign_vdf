package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.EmojiUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.views.HttpTextView;

public class ChatRightTextHolder extends ChatRightHolder {

    private HttpTextView mRightTextContent;

    public ChatRightTextHolder(View view, Activity activity, int activityType, WatchData watch, OnItemClickListener listener) {
        super(view, activity, activityType, watch, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mRightTextContent = view.findViewById(R.id.tv_right_chat_content);
    }

    @Override
    protected void showCustomView() {
        showTextContent();
        showTextSendState();

        if (activityType != 2) {
            mRightTextContent.setOnLongClickListener(this);
        }
    }

    private void showTextContent() {
        switch (chat.getmType()) {
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT:
                mRightTextContent.setUrlText(chat.getmAudioPath());
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI:
                mRightTextContent.setText(EmojiUtil.getEmojiSequence(activity, chat.getmAudioPath()));
                break;
            default:
                break;
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