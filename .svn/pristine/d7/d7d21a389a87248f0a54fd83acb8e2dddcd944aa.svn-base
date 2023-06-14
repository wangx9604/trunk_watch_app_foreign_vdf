package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.interfaces.OnImageDownload;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.ImageUtil;

import java.io.File;

public class ChatLeftImageHolder extends ChatLeftHolder {

    private ImageView mLeftImageContent;

    public ChatLeftImageHolder(View view, Activity activity, int activityType, OnItemClickListener listener) {
        super(view, activity, activityType, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mLeftImageContent = view.findViewById(R.id.iv_left_image_content);
    }

    @Override
    protected void showCustomView() {
        switch (chat.getmType()) {
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE:
                showImage();
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO:
                showPhoto();
                break;
            default:
                break;
        }
        mLeftImageContent.setOnClickListener(this);
        if (activityType != 2) {
            mLeftImageContent.setOnLongClickListener(this);
        }
    }

    private void showImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(chat.getmAudioPath());
        if (bitmap != null) {
            mLeftImageContent.setImageBitmap(ImageUtil.scale(bitmap, (float)118 * 3 / bitmap.getWidth(), activity));
        } else {
            Bitmap delete = BitmapFactory.decodeResource(activity.getResources(), R.drawable.image_deleted);
            mLeftImageContent.setImageBitmap(ImageUtil.scale(delete, (float)118 * 3 / delete.getWidth(), activity));
        }
    }

    private void showPhoto() {
        File preview = new File(ImibabyApp.getChatCacheDir().getPath(), chat.getmAudioPath().substring(chat.getmAudioPath().lastIndexOf("/")));
        boolean hasPreview = false;
        if (preview.exists()) {
            Bitmap previewBitmap = BitmapFactory.decodeFile(preview.getAbsolutePath());
            if(previewBitmap != null) {
                hasPreview = true;
                mLeftImageContent.setImageBitmap(ImageUtil.scale(previewBitmap, (float) 118 * 3 / previewBitmap.getWidth(), activity));
            }
        }
        if (!hasPreview){
            preview.delete();
            mLeftImageContent.setTag(chat.getmAudioPath().substring(chat.getmAudioPath().lastIndexOf("/")));
            mApp.downloadNoticeVideo(watchEid, chat.getmAudioPath(), new OnImageDownload() {
                @Override
                public void onSuccess(String filePath) {
                    String tag = (String) mLeftImageContent.getTag();
                    if (filePath.endsWith(tag)) {
                        Bitmap previewBitmap = BitmapFactory.decodeFile(filePath);
                        if (previewBitmap != null) {
                            mLeftImageContent.setImageBitmap(ImageUtil.scale(previewBitmap, (float) 118 * 3 / previewBitmap.getWidth(), activity));
                        }
                    }
                }

                @Override
                public void onFail() {

                }
            });
        }
    }
}