package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.DensityUtil;

import java.io.File;

public class ChatRightRecordHolder extends ChatRightHolder {

    private ImageButton mRightContent;
    private TextView mRightDurationTime;
    private ImageView mRightAnim;


    public ChatRightRecordHolder(View view, Activity activity, int activityType, WatchData watch, OnItemClickListener listener) {
        super(view, activity, activityType, watch, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mRightAnim = view.findViewById(R.id.right_anim);
        mRightContent = view.findViewById(R.id.right_chat_content);
        mRightDurationTime = view.findViewById(R.id.right_duration);
    }

    @Override
    protected void showCustomView() {
        ViewGroup.LayoutParams pl = mRightContent.getLayoutParams();
        pl.width = DensityUtil.dip2px(activity.getApplicationContext(), chat.getmDuration() * 5 + 60);
        mRightContent.setLayoutParams(pl);
        mRightContent.invalidate();
        mRightDurationTime.setText(chat.getmDuration() + "\"");

        Boolean isExist = false;
        if (chat.getmAudioPath() != null) {
            isExist = new File(chat.getmAudioPath()).exists();
        } else {
            isExist = false;
        }

        if (isExist) {
            showAnimation(chat.getmSended());
        } else {
            mRightContent.setBackgroundResource(R.drawable.dialog_box2_1);
            mRightContent.setOnClickListener(null);
            mRightAnim.setVisibility(View.GONE);
            mRightRetry.setVisibility(View.GONE);
            mRightWaiting.setVisibility(View.GONE);
        }

        if (activityType != 2) {
            mRightContent.setOnLongClickListener(this);
        }
    }

    private void showAnimation(int sendState) {
        AnimationDrawable anim;
        switch (sendState) {
            case ChatMsgEntity.CHAT_SEND_STATE_SENDING:
            case ChatMsgEntity.CHAT_SEND_STATE_RETRYING:
                mRightAnim.setVisibility(View.INVISIBLE);
                mRightRetry.setVisibility(View.GONE);
                mRightWaiting.setVisibility(View.VISIBLE);
                break;
            case ChatMsgEntity.CHAT_SEND_STATE_SUCCESS:
                anim = (AnimationDrawable) mRightAnim.getBackground();
                anim.stop();
                anim.selectDrawable(0);
                mRightContent.setOnClickListener(this);
                mRightAnim.setVisibility(View.VISIBLE);
                mRightRetry.setVisibility(View.GONE);
                mRightWaiting.setVisibility(View.GONE);
                break;
            case ChatMsgEntity.CHAT_SEND_STATE_FAIL:
                anim = (AnimationDrawable) mRightAnim.getBackground();
                anim.stop();
                anim.selectDrawable(0);
                mRightContent.setOnClickListener(this);
                mRightRetry.setBackgroundResource(R.drawable.refresh_0);
                mRightAnim.setVisibility(View.VISIBLE);
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

        AnimationDrawable animDra;
        animDra = (AnimationDrawable) mRightAnim.getBackground();
        animDra.selectDrawable(0);
        chat.setmPlayAnimation(animDra);
    }
}