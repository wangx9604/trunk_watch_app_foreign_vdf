package com.xiaoxun.xun.beans;

/**
 * Uesr：yaoyonghui on 2020/4/16 16:32
 * Email：yaoyonghui@loogcheer.com
 * Project: trunk_watch_app
 */
public class ChatImage {
    private int mType;
    private String mAudioPath;

    public ChatImage(int type, String audioPath) {
        mType = type;
        mAudioPath = audioPath;
    }

    public int getmType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getmAudioPath() {
        return mAudioPath;
    }

    public void setAudioPath(String audioPath) {
        mAudioPath = audioPath;
    }
}
