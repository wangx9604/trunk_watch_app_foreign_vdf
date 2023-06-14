package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.OnImageDownload;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.ImageUtil;

import java.io.File;

public class ChatLeftVideoHolder extends ChatLeftHolder {

    private ImageView mLeftImageContent;

    public ChatLeftVideoHolder(View view, Activity activity, int activityType, OnItemClickListener listener) {
        super(view, activity, activityType, listener);
    }

    @Override
    protected void initViewHolder(View view) {
        super.initViewHolder(view);
        mLeftImageContent = view.findViewById(R.id.iv_left_image_content);
    }

    @Override
    protected void showCustomView() {
        showPhoto();
        mLeftImageContent.setOnClickListener(this);
        if (activityType != 2) {
            mLeftImageContent.setOnLongClickListener(this);
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