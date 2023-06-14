package com.xiaoxun.xun.activitys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.ChatRecyclerViewAdaper;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.interfaces.OnImageDownload;
import com.xiaoxun.xun.utils.ToastUtil;

import java.io.File;

/**
 * Created by guxiaolong on 2017/9/7.
 */

public class VideoDisplayActivity extends NormalActivity {

    private String mKey;
    private int mType;
    private VideoView mVideoView;
    private RelativeLayout mVideoLayout;
    private String mPreviewFileName;
    private String mFileName;
    private String mWatchEid;
    private MediaController mMediaController;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);
        mType = getIntent().getIntExtra("type", 0);
        initViews();
        if (mType == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO) {
            showLeftVideo();
        } else if (mType == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO + ChatRecyclerViewAdaper.VIEW_TYPE_INTERVAL) {
            showRightVideo();
        }
    }

    private void initViews() {
        mVideoLayout = findViewById(R.id.layout_video_play);
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_title).setVisibility(View.GONE);
        mProgressBar = findViewById(R.id.progressbar);
    }

    private void showLeftVideo() {
        mKey = getIntent().getStringExtra("key");
        mWatchEid = getIntent().getStringExtra("eid");
        mPreviewFileName = mKey.substring(mKey.lastIndexOf(File.separator) + 1);
        String tempFileName = mPreviewFileName.substring(0, mPreviewFileName.lastIndexOf("."));
        if (tempFileName.endsWith("_xxx")) {
            mFileName = mPreviewFileName;
        } else {
            int index = tempFileName.lastIndexOf("_");
            mFileName = tempFileName.substring(0, index) + "." + tempFileName.substring(index + 1);
        }
        File file = new File(ImibabyApp.getChatCacheDir().getPath(), mFileName);
        if (file.exists()) {
            mVideoView.setVideoPath(file.getAbsolutePath());
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            String key = mKey.replace("PREVIEW", "SOURCE").substring(0, mKey.lastIndexOf(File.separator)) + mFileName;
            myApp.downloadNoticeVideo(mWatchEid, key, new OnImageDownload() {
                @Override
                public void onSuccess(String filePath) {
                    mProgressBar.setVisibility(View.GONE);
                    mVideoView.setVideoPath(filePath);
                }

                @Override
                public void onFail() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showRightVideo() {
        String videoPath = getIntent().getStringExtra("video_path");
        File videoFile = new File(videoPath);
        if (videoFile != null && videoFile.exists()) {
            mVideoView.setVideoPath(videoPath);
        } else {
            ToastUtil.showMyToast(this, this.getString(R.string.video_delete), Toast.LENGTH_SHORT);
            finish();
        }
    }
}
