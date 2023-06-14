package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.interfaces.ChatMsgViewHolder;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.TimeUtil;

public abstract class ChatLeftHolder extends RecyclerView.ViewHolder implements ChatMsgViewHolder, View.OnClickListener, View.OnLongClickListener {

    protected Activity activity;
    protected ImibabyApp mApp;
    protected int activityType;

    private RelativeLayout mSendTimeLayout;
    private TextView mSendTime;
    private ImageView mLeftHead;
    private TextView mNickname;

    protected ChatMsgEntity chat;
    protected String watchEid;
    private OnItemClickListener onItemClickListener;

    public ChatLeftHolder(View view, Activity activity, int activityType, OnItemClickListener listener) {
        super(view);
        this.activity = activity;
        mApp = (ImibabyApp) activity.getApplication();
        this.activityType = activityType;
        this.onItemClickListener = listener;
        initViewHolder(view);
    }

    @Override
    public void bindTo(ChatMsgEntity chat, String lastTime) {
        this.chat = chat;
        setSendTime(lastTime);
        showContentView();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, getLayoutPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemLongClick(v, getLayoutPosition());
        }
        return false;
    }

    protected void initViewHolder(View view) {
        mSendTimeLayout = view.findViewById(R.id.send_time);
        mSendTime = view.findViewById(R.id.chat_send_time);

        mNickname = view.findViewById(R.id.chat_nickname);
        mLeftHead = view.findViewById(R.id.left_head);
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

    private void showNickName() {
        mApp.sdcardLog("chatLeftHolder:"+watchEid+":"+chat.getmSrcId()+":"+chat.getmDate());
        if (chat.getmSrcId().equals(watchEid)) {
            mNickname.setText(mApp.getCurUser().queryNicknameByEid(chat.getmSrcId()));
        } else {
            PhoneNumber phoneNumber = mApp.getPhoneNumberByEid(chat.getmSrcId(), watchEid);
            if (phoneNumber != null && phoneNumber.nickname != null && phoneNumber.nickname.length() > 0) {
                mNickname.setText(phoneNumber.nickname);
            } else {
                mNickname.setText(R.string.default_relation_text);
            }
        }
    }

    private void showHeadImage() {
        if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {
            String headpath = mApp.getCurUser().getHeadPathByEid(chat.getmSrcId());
            ImageUtil.setMaskImage(mLeftHead, R.drawable.head_1,
                    mApp.getHeadDrawableByFile(activity.getResources(), headpath, chat.getmSrcId(), R.drawable.default_head));
        } else {
            int attri = mApp.getUserAttriByEid(watchEid, chat.getmSrcId());
            String avatatr = mApp.getUserAvatarByEid(watchEid, chat.getmSrcId());
            final ImageView ivAvatar = mLeftHead;
            if (avatatr == null) {
                ImageUtil.setMaskImage(mLeftHead, R.drawable.head_1,
                        mApp.getHeadDrawableByFile(activity.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
            } else {
                Bitmap headBitmap = new ImageDownloadHelper(activity).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                    @Override
                    public void onImageDownload(String url, Bitmap bitmap) {
                        Drawable headDrawable = new BitmapDrawable(activity.getResources(), bitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                    }
                });
                if (headBitmap != null) {
                    Drawable headDrawable = new BitmapDrawable(activity.getResources(), headBitmap);
                    ImageUtil.setMaskImage(mLeftHead, R.drawable.head_2, headDrawable);
                }
            }
        }
        mLeftHead.setTag(chat);
    }

    private void showContentView() {
        watchEid = mApp.getWatchEid(chat);
        if (TextUtils.isEmpty(watchEid)) {
            return;
        }

        showNickName();
        showHeadImage();
        showCustomView();
    }

    protected abstract void showCustomView();
}
