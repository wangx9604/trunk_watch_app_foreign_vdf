package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.ImageUtil;

public class ChatRightImageHolder extends ChatRightHolder {

    private ImageView mRightImageContent;

    public ChatRightImageHolder(View view, Activity activity, int activityType, WatchData watch, OnItemClickListener listener) {
        super(view, activity, activityType, watch, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mRightImageContent = view.findViewById(R.id.iv_right_image_content);
    }

    @Override
    protected void showCustomView() {
        showImage();
        showImageSendState();
        mRightImageContent.setOnClickListener(this);
        if (activityType != 2) {
            mRightImageContent.setOnLongClickListener(this);
        }
    }

    private void showImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(chat.getmAudioPath());
        if (bitmap != null) {
            mRightImageContent.setImageBitmap(ImageUtil.scale(bitmap, (float)118 * 3 / bitmap.getWidth(), activity));
        } else {
            Bitmap delete = BitmapFactory.decodeResource(activity.getResources(), R.drawable.image_deleted);
            mRightImageContent.setImageBitmap(ImageUtil.scale(delete, (float)118 * 3 / delete.getWidth(), activity));
        }
    }

    private void showImageSendState() {
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