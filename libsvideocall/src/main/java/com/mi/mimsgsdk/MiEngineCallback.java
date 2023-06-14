//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mi.mimsgsdk;

import android.os.Message;

public interface MiEngineCallback {

    // 加入房间的回调
    void onJoinRes();

    // 用户加入房间
    void onUserJoin(int uid);

    // 收到第一帧画面
    void onReceiveFirstRemoteVideo(final int uid, int width, int height);

    // 用户离开房间
    void onUserLeave(int uid);

    // 离开房间的回调
    void onLeaveRes();

    // mute音频的回调
    void onUserMuteAudio(int uid, boolean muted);

    // mute音频的回调
    void onUserMuteVideo(int uid, boolean muted);

    // 网络不好的回调
    void onConnectionLost();

    // 错误回调
    void onError(int err);

    // 打印日志
    void onWriteLog(Message msg);
}
