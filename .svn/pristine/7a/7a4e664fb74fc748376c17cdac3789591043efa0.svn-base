package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.view.View;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.EmojiUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.views.HttpTextView;

public class ChatLeftTextHolder extends ChatLeftHolder {

    private HttpTextView mLeftTextContent;

    public ChatLeftTextHolder(View view, Activity activity, int activityType, OnItemClickListener listener) {
        super(view, activity, activityType, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mLeftTextContent = view.findViewById(R.id.tv_left_chat_content);
    }

    @Override
    protected void showCustomView() {
        switch (chat.getmType()) {
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT:
                mLeftTextContent.setUrlText(chat.getmAudioPath());
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI:
                mLeftTextContent.setText(EmojiUtil.getEmojiSequence(activity, chat.getmAudioPath()));
                break;
            default:
                break;
        }

        if (activityType != 2) {
            mLeftTextContent.setOnLongClickListener(this);
        }
    }
}