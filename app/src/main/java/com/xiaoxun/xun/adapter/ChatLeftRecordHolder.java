package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.DensityUtil;

import java.io.File;

public class ChatLeftRecordHolder extends ChatLeftHolder {

    private TextView mLeftDurationTime;
    private ImageButton mLeftContent;
    private ImageView mLeftAnim;

    private ImageView mLeftRetry;


    public ChatLeftRecordHolder(View view, Activity activity, int activityType, OnItemClickListener listener) {
        super(view, activity, activityType, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);

        mLeftContent = view.findViewById(R.id.left_chat_content);
        mLeftDurationTime = view.findViewById(R.id.left_duration);
        mLeftAnim = view.findViewById(R.id.left_anim);
        mLeftRetry = view.findViewById(R.id.left_retry);
    }

    @Override
    protected void showCustomView() {
        ViewGroup.LayoutParams pl = mLeftContent.getLayoutParams();
        pl.width = DensityUtil.dip2px(activity.getApplicationContext(), chat.getmDuration() * 5 + 60);
        mLeftContent.setLayoutParams(pl);
        mLeftContent.invalidate();
        mLeftDurationTime.setText(chat.getmDuration() + "\"");
        if (!chat.getmPlayed()) {
            mLeftRetry.setVisibility(View.VISIBLE);
        } else {
            mLeftRetry.setVisibility(View.GONE);
        }
        showRecordBackgroud();
        boolean isExist = true;

        if (chat.getmAudioPath() != null) {
            isExist = new File(chat.getmAudioPath()).exists();
        } else {
            isExist = false;
        }

        if (isExist) {
            mLeftAnim.setVisibility(View.VISIBLE);
            mLeftContent.setOnClickListener(this);
            setAnimation(chat.getmType());
        } else if (chat.getmSended() != ChatMsgEntity.CHAT_SEND_STATE_DELETE) {
            if (mApp.getNetService() != null) {
                mApp.getNetService().getLostRecord(chat);
            }
        } else {
            mLeftContent.setBackgroundResource(R.drawable.dialog_box_1);
            mLeftContent.setOnClickListener(null);
            mLeftAnim.setVisibility(View.GONE);
            mLeftRetry.setVisibility(View.GONE);
        }
        chat.setmLeftRetry(mLeftRetry);

        if (activityType != 2) {
            mLeftContent.setOnLongClickListener(this);
        }
    }

    private void showRecordBackgroud() {
        if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS) {
            mLeftContent.setBackgroundResource(R.drawable.dialog_box_sos_0);
        } else {
            mLeftContent.setBackgroundResource(R.drawable.dialog_box_0);
        }
    }

    private void setAnimation(int type) {
        AnimationDrawable anim;
        switch (type) {
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE:
                mLeftAnim.setBackgroundResource(R.drawable.animation_chat_left);
                anim = (AnimationDrawable) mLeftAnim.getBackground();
                anim.stop();
                anim.selectDrawable(0);
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD:
                mLeftAnim.setBackgroundResource(R.drawable.animation_force_record);
                anim = (AnimationDrawable) mLeftAnim.getBackground();
                anim.stop();
                anim.selectDrawable(0);
                break;
            default:
                break;
        }

        AnimationDrawable animDra;
        animDra = (AnimationDrawable) mLeftAnim.getBackground();
        animDra.selectDrawable(0);
        chat.setmPlayAnimation(animDra);
    }

}
