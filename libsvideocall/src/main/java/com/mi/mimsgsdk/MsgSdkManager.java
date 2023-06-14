package com.mi.mimsgsdk;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.mi.mimsgsdk.api.MiEngineCallback;
import com.mi.mimsgsdk.api.MsgSdkManagerApi;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class MsgSdkManager implements MsgSdkManagerApi {

    public final static String RTC_PROBVIDER_JUPHOON = "juphoon";
    public final static String RTC_PROBVIDER_AGORA = "agora";
    public final static String RTC_PROBVIDER_MIMSG = "mimsg";

    public final static int RTC_CALLTYPE_CALL = 0;
    public final static int RTC_CALLTYPE_RECEIVECALL = 1;

    private static final String TAG = "MsgSdkManager";

    private static MsgSdkManager mMsgSdkManager;
    private MiEngineCallback mMiEngineCallback;

    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.i(TAG, " onJoinChannelSuccess" + " channel=" + channel + " uid=" + uid + " elapsed=" + elapsed);
            mMiEngineCallback.onJoinRes();
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i(TAG, " onUserJoined" + " uid=" + uid + " elapsed=" + elapsed);
            mMiEngineCallback.onUserJoin(uid);
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            Log.i(TAG, " onFirstRemoteVideoDecoded" + " uid=" + uid + " elapsed=" + elapsed);
            mMiEngineCallback.onReceiveFirstRemoteVideo(uid, width, height);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.i(TAG, " onUserOffline" + " uid=" + uid + " reason=" + reason);
            mMiEngineCallback.onUserLeave(uid);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            Log.i(TAG, " onLeaveChannel totalDuration=" + stats.totalDuration);
            mMiEngineCallback.onLeaveRes();
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            Log.i(TAG, " onUserMuteVideo" + " uid=" + uid + " muted=" + muted);
            mMiEngineCallback.onUserMuteVideo(uid, muted);
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            Log.i(TAG, " onUserMuteAudio" + " uid=" + uid + " muted=" + muted);
            mMiEngineCallback.onUserMuteAudio(uid, muted);
        }

        @Override
        public void onConnectionLost() {
            Log.i(TAG, " onConnectionLost");
            mMiEngineCallback.onConnectionLost();
        }

        @Override
        public void onError(int err) {
            Log.i(TAG, " onError" + " error=" + err);
            mMiEngineCallback.onError(err);
        }

        @Override
        public void onApiCallExecuted(int error, String api, String result) {
            //Log.i(TAG, " onApiCallExecuted" + " error=" + error+" api="+api+" result="+result);
            super.onApiCallExecuted(error, api, result);
        }
    };
    /**
     * 获取视频通话引擎实例
     *
     * @param provider juphoon agora mimsg
     * @return 视频通话引擎实例
     */
    public static MsgSdkApi getInstance(String provider) {

//        if (RTC_PROBVIDER_JUPHOON.equals(provider)) {
            return MsgSdkJuphoon.getInStance();
//        } else {
//            return MsgSdkAgora.getInStance();
//        }
    }
    public static synchronized MsgSdkManager getInStance() {

        if (mMsgSdkManager == null)
            mMsgSdkManager = new MsgSdkManager();
        return mMsgSdkManager;
    }

    @Override
    public void voipMsgSdkInit(MiEngineCallback miEngineCallback, Context context, String appId) {
        try {
            mRtcEngine = RtcEngine.create(context, appId, mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            this.mMiEngineCallback = miEngineCallback;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enterConference(String token, String conferenceid, int uid) {
        Log.i(TAG, " enterConference");
        mRtcEngine.joinChannel(token, conferenceid, "MIMsg", uid);
    }

    @Override
    public void exitConference() {
        Log.i(TAG, " exitConference");
        mRtcEngine.leaveChannel();
    }

    @Override
    public void startAudio() {
        mRtcEngine.enableAudio();
    }

    @Override
    public void stopAudio() {
        mRtcEngine.disableAudio();
    }

    @Override
    public void openMic() {
        mRtcEngine.muteLocalAudioStream(false);
    }

    @Override
    public void closeMic() {
        mRtcEngine.muteLocalAudioStream(true);
    }

    @Override
    public void audioMuteUser(int uid, boolean muted) {
        mRtcEngine.muteRemoteAudioStream(uid, muted);
    }

    @Override
    public void setAudioProfile(int profile, int scenario) {
        mRtcEngine.setAudioProfile(profile, scenario);
    }

    @Override
    public void setEnableSpeakerphone(boolean enabled) {
        mRtcEngine.setEnableSpeakerphone(enabled);
    }

    @Override
    public void enableAudioVolumeIndication() {
        mRtcEngine.enableAudioVolumeIndication(0, 0, false);
    }

    @Override
    public void startVideo() {
        mRtcEngine.enableVideo();
    }

    @Override
    public void stopVideo() {
        mRtcEngine.disableVideo();
    }

    @Override
    public void setupRemoteVideo(SurfaceView surfaceView, int uid) {
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }

    @Override
    public void setupLocalVideo(SurfaceView surfaceView, int uid) {
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }

    @Override
    public SurfaceView createRendererView(Context context) {
        return RtcEngine.CreateRendererView(context);
    }

    @Override
    public void openCamera() {
        mRtcEngine.muteLocalVideoStream(false);
    }

    @Override
    public void closeCamera() {
        mRtcEngine.muteLocalVideoStream(true);
    }

    @Override
    public void switchCamera() {
        mRtcEngine.switchCamera();
    }

    @Override
    public void videoMuteUser(int uid, boolean muted) {
        mRtcEngine.muteRemoteVideoStream(uid, muted);
    }

    @Override
    public void setVideoProfile(int width, int height, int frameRate, int bitrate) {
        mRtcEngine.setVideoProfile(width, height, frameRate, bitrate);
    }

    @Override
    public void setParameters(String parameters) {
        mRtcEngine.setParameters(parameters);
    }

    @Override
    public void destroy() {
        RtcEngine.destroy();
    }

    @Override
    public void setLogFile(String path){
        mRtcEngine.setLogFile(path);
    }
}
