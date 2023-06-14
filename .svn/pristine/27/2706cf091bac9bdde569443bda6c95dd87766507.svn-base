package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mi.mimsgsdk.MsgSdkApi;
import com.mi.mimsgsdk.MsgSdkManager;
import com.mi.mimsgsdk.api.MiEngineCallback;
import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.ChatHisDao;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.presenter.VideoCallLocationPresenter;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.services.VoiceFloatingService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.MyMediaPlayerUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.PromptUtils;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.ToolUtils;
import com.xiaoxun.xun.utils.VibratorUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by huangyouyang on 2018/8/22.
 */

public class VideoCallActivity3 extends NormalActivity2 implements View.OnClickListener, MsgCallback {

    private static final String TAG = "VideoCallActivity3";

    private View layoutCallWait;
    private ImageView ivWatchHead;
    private TextView tvCallState;

    private View layoutCallIng;
    private RelativeLayout layoutVideoView;
    private FrameLayout localVideoView;
    private FrameLayout remoteVideoView;
    private LinearLayout layoutAudioMute;
    private LinearLayout layoutSwitchLocalCamera;
    private LinearLayout layoutSwitchWatchCamera;
    private LinearLayout layoutVideoRecord;

    private LinearLayout layoutEndCall;
    private LinearLayout layoutAnswer;
    private TextView mTimeshow;
    private TextView tvVideoRecord;

    private WatchData focusWatch;
    private MsgSdkApi mMsgSdkManager;
    private String appId;
    private String appSecret;
    private int uidSelf;
    private String tokenSelf;
    private String authTokenSelf = "auth";  //默认值，以处理服务器的一个异常
    private int uidOther;
    private String tokenOther;
    private String authTokenOther;
    private String channelName;
    private String rtcProvider;
    private String appUrl;
    private String deviceUrl;
    private String rtcVersion;
    private int callType = 0;  //0,call;1,reviceCall

    private int timeOut = 30;
    private int countSecond = -1;  //初始值-1
    private boolean haveSendPrivatemsg = false;
    private boolean isInVideoRecord = false;
    private NetService mNetService;
    private VideocallHandler mHandler;
    private BroadcastReceiver mReceiver;
    private AudioManager mAudioManager;

    private VoiceFloatingService mVoiceFloatingService;
    private ServiceConnection serviceConnection;

    private MiEngineCallback miEngineCallback = new MiEngineCallback() {

        @Override
        public void onJoinRes() {
            LogUtil.i(TAG + " onJoinChannelSuccess");
            mHandler.removeMessages(VideocallHandler.JOIN_CHANNEL_SUCCESS);
            mHandler.sendEmptyMessage(VideocallHandler.JOIN_CHANNEL_SUCCESS);
        }

        @Override
        public void onUserJoin(int uid) {
            LogUtil.i(TAG + " onUserJoined" + " uid=" + uid);
        }

        @Override
        public void onReceiveFirstRemoteVideo(final int uid, int width, int height) {
            LogUtil.i(TAG + " onFirstRemoteVideoDecoded" + " uid=" + uid);
            uidOther = uid;
            mHandler.removeMessages(VideocallHandler.RECEIVE_REMOTE_DATA);
            mHandler.sendEmptyMessage(VideocallHandler.RECEIVE_REMOTE_DATA);
        }

        @Override
        public void onUserLeave(int uid) {
            LogUtil.i(TAG + " onUserOffline" + " uid=" + uid);
            if (myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE) {  //只在通话过程中才发endcall，防止误提示
                mHandler.removeMessages(VideocallHandler.RECEIVE_END_CALL);
                mHandler.sendEmptyMessage(VideocallHandler.RECEIVE_END_CALL);
            }
        }

        @Override
        public void onLeaveRes() {
            LogUtil.i(TAG + " onLeaveRes");
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            LogUtil.i(TAG + " onUserMuteAudio" + " uid=" + uid + " muted=" + muted);
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            LogUtil.i(TAG + " onUserMuteVideo" + " uid=" + uid + " muted=" + muted);
        }

        @Override
        public void onConnectionLost() {
            LogUtil.i(TAG + " onConnectionLost");
            Message msg = Message.obtain();
            msg.what = VideocallHandler.VIDEO_CALL_ERROR;
            msg.obj = getString(R.string.videocall_network_error);
            mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(int err) {
            LogUtil.i(TAG + " onError" + " error=" + err);
            Message msg = Message.obtain();
            msg.what = VideocallHandler.VIDEO_CALL_ERROR;
            msg.obj = getString(R.string.videocall_error);
            mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onWriteLog(Message msg) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall_new);
        initView();
        initListener();
        initData(getIntent());
        initReceiver();
        initMediaEffect();
        checkPermission();
        bindService();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void checkPermission() {

        if (callType == 0 && PermissionUtils.needGotoAlertWindowActivity(this, myApp, false)) {
        } else {
            checkAVPermission();
        }
    }

    private void checkAVPermission() {

        boolean audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean videoPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (!audioPermission && !videoPermission) {
            ActivityCompat.requestPermissions(VideoCallActivity3.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, PERMISSION_RESULT);
        } else if (!audioPermission) {
            ActivityCompat.requestPermissions(VideoCallActivity3.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RESULT);
        } else if (!videoPermission) {
            ActivityCompat.requestPermissions(VideoCallActivity3.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_RESULT);
        } else {
            // goon
            checkNetWork();
        }

        checkBluetoothPermission();
    }

    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 31) {
            boolean bluethoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            if (!bluethoothPermission) {
                ActivityCompat.requestPermissions(VideoCallActivity3.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_RESULT);
            }
        }
    }

    private static final int PERMISSION_RESULT = 0x11;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RESULT:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    Message msg = Message.obtain();
                    msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                    msg.obj = getString(R.string.need_audio_permission);
                    mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    mHandler.sendMessage(msg);
                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Message msg = Message.obtain();
                    msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                    msg.obj = getString(R.string.need_video_permission);
                    mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    mHandler.sendMessage(msg);
                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    Message msg = Message.obtain();
                    msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                    msg.obj = "缺少蓝牙连接权限";
                    mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    mHandler.sendMessage(msg);
                } else {
                    checkNetWork();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_VODE_ALERT_WINDOW) {
            checkAVPermission();
        }
    }

    private void bindService() {
        bindService(new Intent(this, VoiceFloatingService.class), serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mVoiceFloatingService = ((VoiceFloatingService.MyBinder) service).getService();
                mVoiceFloatingService.setVideoCallActivity(VideoCallActivity3.class);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mVoiceFloatingService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        layoutCallWait = findViewById(R.id.layout_call_wait);
        ivWatchHead = (ImageView) findViewById(R.id.iv_watch_head);
        tvCallState = (TextView) findViewById(R.id.tv_videocall_state);
        layoutCallIng = findViewById(R.id.layout_call_ing);
        layoutVideoView = (RelativeLayout) findViewById(R.id.layout_video_view);
        localVideoView = (FrameLayout) findViewById(R.id.local_video_view);
        remoteVideoView = (FrameLayout) findViewById(R.id.remote_video_view);
        layoutAudioMute = (LinearLayout) findViewById(R.id.layout_audio_mute);
        layoutSwitchLocalCamera = (LinearLayout) findViewById(R.id.layout_switch_local_camera);
        layoutSwitchWatchCamera = (LinearLayout) findViewById(R.id.layout_switch_watch_camera);
        layoutEndCall = (LinearLayout) findViewById(R.id.layout_end_call);
        layoutAnswer = (LinearLayout) findViewById(R.id.layout_answer);
        layoutVideoRecord = (LinearLayout) findViewById(R.id.layout_video_record);
        tvVideoRecord = (TextView) findViewById(R.id.tv_video_record);
        mTimeshow = (TextView) findViewById(R.id.tv_videocall_time);
//        setTintColor(getResources().getColor(R.color.transparent));
        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.transparent));
    }

    private void initListener() {

        layoutAudioMute.setOnClickListener(this);
        layoutSwitchLocalCamera.setOnClickListener(this);
        layoutSwitchWatchCamera.setOnClickListener(this);
        layoutEndCall.setOnClickListener(this);
        layoutAnswer.setOnClickListener(this);
        layoutVideoRecord.setOnClickListener(this);

        localVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLocalRemoteView();
            }
        });
    }

    private void initData(Intent intent) {
        mNetService = myApp.getNetService();
        myApp.callState = Const.MESSAGE_CALL_WAIT_STATE;
        mHandler = new VideocallHandler(VideoCallActivity3.this, getMainLooper());
        haveSendPrivatemsg = false;
        String eid = intent.getStringExtra(CloudBridgeUtil.KEY_NAME_EID);
        focusWatch = myApp.getCurUser().queryWatchDataByEid(eid);
        if (focusWatch == null) {
            mHandler.sendEmptyMessage(VideocallHandler.VIDEO_CALL_ERROR);
            return;
        }

        callType = intent.getIntExtra(Const.VIDEOCALL_TYPE, 0);
        if (callType == 0) {
//            mHandler.sendEmptyMessage(VideocallHandler.TO_GET_TOKEN);//调整到网络判断里面
            updateLayoutShow(0);
            mHandler.sendEmptyMessageDelayed(VideocallHandler.REQUEST_CALL_TIMEOUT, 30 * 1000);
        } else if (callType == 1) {
            JSONObject pl = (JSONObject) JSONValue.parse(intent.getStringExtra(Constants.VIDEOCALL_PARAMS));
            assert pl != null;
            analysisVideoCallParams(pl);
            //mHandler.sendEmptyMessage(VideocallHandler.GET_TOKEN_SUCCESS);
            mHandler.sendEmptyMessageDelayed(VideocallHandler.RECEIVE_CALL_TIMEOUT, 30 * 1000);
            updateLayoutShow(1);
        }
    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.e("mReceiver = " + intent.getAction());
                if (Const.ACTION_VIDEOCALL_ENDCALL.equals(intent.getAction())) {
                    mHandler.removeMessages(VideocallHandler.RECEIVE_END_CALL);
                    mHandler.sendEmptyMessage(VideocallHandler.RECEIVE_END_CALL);
                } else if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                    int state = intent.getIntExtra("state", 0);
                    LogUtil.i(TAG + " headset_plug state = " + state);
                    configAudioSetting();
                } /*else if (Const.ACTION_WATCH_NET_STATE_CHANGE.equals(intent.getAction())) {
                    if (myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE)
                        updateWatchNetState();
                }*/ /*else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    if (myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE || myApp.callState == Const.MESSAGE_CALL_WAIT_STATE) {
                        mHandler.removeMessages(VideocallHandler.REQUEST_END_CALL);
                        mHandler.sendEmptyMessage(VideocallHandler.REQUEST_END_CALL);
                    }
                }*/ else if (Const.ACTION_REQUEST_ALERT_WINDOW_CANCEL.equals(intent.getAction())) {
                    checkAVPermission();
                } else if (Const.ACTION_RECEIVE_NEW_LOCATION_NOTIFY.equals(intent.getAction())) {
                    String resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    JSONObject respObj = (JSONObject) JSONValue.parse(resp);
                    int rc = CloudBridgeUtil.getCloudMsgRC(respObj);
                    JSONObject pl = (JSONObject) respObj.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (1 == rc) {
                        int sostype = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SOS);
                        if (1 != sostype) {
                            pl.put("sn", respObj.get("SN"));
                            if ((locationPresenter != null))
                                locationPresenter.updateCurrentLocation(pl);
                        }
                    }
                } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    if (ToolUtils.getConnectionType(VideoCallActivity3.this) == ToolUtils.NETWORKTYPE_INVALID) {
                        LogUtil.e("mReceiver NETWORKTYPE_INVALID");
                        Message msg = Message.obtain();
                        msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                        msg.obj = getString(R.string.videocall_network_error);
                        mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                        mHandler.sendMessage(msg);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_VIDEOCALL_ENDCALL);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        //filter.addAction(Const.ACTION_WATCH_NET_STATE_CHANGE);
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Const.ACTION_REQUEST_ALERT_WINDOW_CANCEL);
        filter.addAction(Const.ACTION_RECEIVE_NEW_LOCATION_NOTIFY);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private void initMediaEffect() {
        MyMediaPlayerUtil.getInstance().stopMediaPlayer(myApp);
        MyMediaPlayerUtil.getInstance().requestAudioFocus(myApp);
        try {
            String musicName = "music/call_in.wav";
            if (callType == 0) {
                musicName = "music/call_out.wav";
            } else if (callType == 1) {
                musicName = "music/call_in.wav";
            }
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd(musicName);
            MyMediaPlayerUtil.getInstance().starAssetMediaPlayer(afd, myApp, AudioManager.STREAM_NOTIFICATION);
            if (callType == 1)
                VibratorUtil.startVibrate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configAudioSetting() {
        if (!PermissionUtils.hasPermissions(getApplicationContext(), Manifest.permission.BLUETOOTH)) {
            LogUtil.e("VideoCallActivity BLUETOOTH_CONNECT dennied");
            return;
        }
        if (mAudioManager == null)
            mAudioManager = (AudioManager) VideoCallActivity3.this.getSystemService(Context.AUDIO_SERVICE);
        if (countSecond >= 0)
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        if (MyMediaPlayerUtil.getInstance().isBluetoothHeadsetConnected(mAudioManager) || MyMediaPlayerUtil.getInstance().isWiredHeadsetConnected(mAudioManager)) {
            //mAudioManager.setSpeakerphoneOn(false);
            if (mMsgSdkManager != null) {
                mMsgSdkManager.setEnableSpeakerphone(false);
            }
        } else {
            //mAudioManager.setSpeakerphoneOn(true);
            if (mMsgSdkManager != null) {
                mMsgSdkManager.setEnableSpeakerphone(true);
            }
        }
    }

    /**
     * @param state 0,发起呼叫；1，接收呼叫；2，正在通话
     */
    private void updateLayoutShow(int state) {
        switch (state) {
            case 0: {
                layoutCallWait.setVisibility(View.VISIBLE);
                layoutCallIng.setVisibility(View.GONE);
                layoutEndCall.setVisibility(View.VISIBLE);
                layoutAnswer.setVisibility(View.GONE);
                layoutAudioMute.setVisibility(View.GONE);
                layoutSwitchLocalCamera.setVisibility(View.GONE);
                layoutVideoRecord.setVisibility(View.GONE);
                tvCallState.setText(getString(R.string.videocall_request_ing, focusWatch.getNickname()));
                ImageUtil.setMaskImage(ivWatchHead, R.drawable.head_2,
                        getMyApp().getHeadDrawableByFile(getResources(), focusWatch.getHeadPath(), focusWatch.getEid(), R.drawable.default_head));
            }
            break;
            case 1: {
                layoutCallWait.setVisibility(View.VISIBLE);
                layoutCallIng.setVisibility(View.GONE);
                layoutEndCall.setVisibility(View.VISIBLE);
                layoutAnswer.setVisibility(View.VISIBLE);
                layoutAudioMute.setVisibility(View.GONE);
                layoutSwitchLocalCamera.setVisibility(View.GONE);
                layoutVideoRecord.setVisibility(View.GONE);
                tvCallState.setText(getString(R.string.videocall_receive_request, focusWatch.getNickname()));
                ImageUtil.setMaskImage(ivWatchHead, R.drawable.head_2,
                        getMyApp().getHeadDrawableByFile(getResources(), focusWatch.getHeadPath(), focusWatch.getEid(), R.drawable.default_head));
            }
            break;
            case 2: {
                layoutCallWait.setVisibility(View.GONE);
                layoutCallIng.setVisibility(View.VISIBLE);
                layoutEndCall.setVisibility(View.VISIBLE);
                layoutAnswer.setVisibility(View.GONE);
                layoutAudioMute.setVisibility(View.VISIBLE);
                layoutSwitchLocalCamera.setVisibility(View.VISIBLE);
//                if (myApp.getConfigFormDeviceType(focusWatch.getDeviceType(), focusWatch.getVerCur(), focusWatch.getMachSn()).getSwitch_video_call_watchcamera())

                layoutSwitchWatchCamera.setVisibility(View.GONE);
//                else
//                    layoutSwitchWatchCamera.setVisibility(View.GONE);
                // 菊风视频通话，缩小View大小
                if (MsgSdkManager.RTC_PROBVIDER_JUPHOON.equals(rtcProvider)) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) remoteVideoView.getLayoutParams();
                    layoutParams.width = DensityUtil.dp2px(this, 264);
                    layoutParams.height = DensityUtil.dp2px(this, 216);
                    remoteVideoView.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) layoutVideoView.getLayoutParams();
                    layoutParams2.height = getWindowManager().getDefaultDisplay().getWidth() * 216 / 264;
                    layoutVideoView.setLayoutParams(layoutParams2);
                    layoutVideoView.setBackgroundResource(R.drawable.bg_videocall_remoteview);

                    // 调试 录制视频
                    layoutVideoRecord.setVisibility(View.GONE);
                }
            }
            break;
            default:
                break;
        }
    }

    private void initEngine() {
        try {
            // step1 创建 Engine 对象
            mMsgSdkManager = MsgSdkManager.getInstance(rtcProvider);
            LogUtil.e(TAG + "AppUrl voipMsgSdkInit = " + appUrl);
            mMsgSdkManager.voipMsgSdkInit(miEngineCallback, this, appId, myApp.getCurUser().getEid(), appUrl);
            // step2 模式设置(频道、音频参数、视频参数)
            mMsgSdkManager.setVideoProfile(240, 240, 10, 120);
            mMsgSdkManager.setAudioProfile(0, 5);
//            mMsgSdkManager.setAudioChangerType(myApp.getIntValue(Constants.SHARE_PREF_VIDEOCALL_CHANGER_TYPE, -1));
            mMsgSdkManager.setParameters("{\"che.audio.specify.codec\":\"G722\"}");
            // 写log
            if (BuildConfig.IS_PRINT_LOG)
                mMsgSdkManager.setLogFile(myApp.getAgorasdkLogFile().getPath());
        } catch (Exception e) {
            LogUtil.e(TAG + e.toString());
            Message msg = Message.obtain();
            msg.what = VideocallHandler.VIDEO_CALL_ERROR;
            msg.obj = getString(R.string.videocall_init_fail);
            mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
            mHandler.sendMessage(msg);
        }
    }

    private void initAccount() {

        mMsgSdkManager.voipMsgAccountInit(tokenSelf, channelName, uidSelf, MD5.md5_string(focusWatch.getImei()), callType);
    }

    private void joinChannel() {

        try {
            // 创建并加入频道
            mMsgSdkManager.enterConference();

            mMsgSdkManager.startVideo();
            mMsgSdkManager.startAudio();
        } catch (Exception e) {
            LogUtil.e(TAG + e.toString());
            Message msg = Message.obtain();
            msg.what = VideocallHandler.VIDEO_CALL_ERROR;
            msg.obj = getString(R.string.videocall_init_fail);
            mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
            mHandler.sendMessage(msg);
        }
    }

    private void setupLocalVideo() {

        LogUtil.i(TAG + " setupLocalVideo");
        if (localVideoView.getChildCount() >= 1) {
            return;
        }
        SurfaceView surfaceView = mMsgSdkManager.createRendererView(getBaseContext(), 1);
        surfaceView.setZOrderMediaOverlay(true);  //视图层是否放置在常规视图层的顶部
        localVideoView.addView(surfaceView);
        // 设置本地视频视图
        mMsgSdkManager.setupLocalVideo(surfaceView, uidSelf);
    }

    private void setupRemoteVideo(int uid) {
        LogUtil.i(TAG + " setupRemoteVideo uid = " + uid);
        if (remoteVideoView.getChildCount() >= 1) {
            return;
        }
        SurfaceView surfaceView = mMsgSdkManager.createRendererView(getBaseContext(), 2);
        remoteVideoView.addView(surfaceView);
        // 设置远端视频视图
        mMsgSdkManager.setupRemoteVideo(surfaceView, uid);
        surfaceView.setTag(uid); // for mark purpose
    }

    boolean isLeaveChannle = false;  //添加一个是否离开channel的flag，避免多次leave出现异常

    // Tutorial Step 6
    private void leaveChannel() {
        if (mMsgSdkManager != null && !isLeaveChannle) {
            mMsgSdkManager.exitConference();
            uploadVideocallTime(Constants.VIDEOCALL_CMD_EXITVOIPROOM);
            isLeaveChannle = true;
        }
    }

    private void updateWatchNetState() {

        String netState = myApp.getStringValue(focusWatch.getEid() + CloudBridgeUtil.KEY_NAME_WATCH_NET_STATE, "");
        if (netState.length() > 0) {
            netState = netState.split("_")[1].substring(1, 2);
            // 2|3|4|9 分别表示 2G|3G|4G|WIFI
            switch (netState) {
                case "2":
                case "3":
                case "4":
                    ToastUtil.show(VideoCallActivity3.this, getString(R.string.videocall_netstate_mobile));
                    break;
                case "9":
                default:
//                    ToastUtil.show(VideoCallActivity.this, getString(R.string.videocall_netstate_wifi));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.layout_audio_mute: {
                ImageView iv = (ImageView) findViewById(R.id.btn_audio_mute);
                if (iv.isSelected()) {
                    iv.setSelected(false);
                    iv.setImageResource(R.drawable.btn_audiomute_on_selecter);
                    mMsgSdkManager.openMic();
                } else {
                    iv.setSelected(true);
                    iv.setImageResource(R.drawable.btn_audiomute_off_selecter);
                    mMsgSdkManager.closeMic();
                }
            }
            break;

            case R.id.layout_switch_local_camera: {
                if (mMsgSdkManager != null) {
                    mMsgSdkManager.switchCamera();
                }
            }
            break;

            case R.id.layout_switch_watch_camera: {
                sendSwitchWatchCameraMsg();
            }
            break;

            case R.id.layout_answer: {
                mHandler.removeMessages(VideocallHandler.AGREE_ANSWER_CALL);
                mHandler.sendEmptyMessage(VideocallHandler.AGREE_ANSWER_CALL);
            }
            break;

            case R.id.layout_end_call: {
                mHandler.removeMessages(VideocallHandler.REQUEST_END_CALL);
                mHandler.sendEmptyMessage(VideocallHandler.REQUEST_END_CALL);
            }
            break;

            case R.id.layout_video_record: {
                mMsgSdkManager.onCallVideoRecord(myApp.getVideoRecordFile().getPath());
                isInVideoRecord = !isInVideoRecord;
                tvVideoRecord.setText(isInVideoRecord ? R.string.videocall_video_record_stop : R.string.videocall_video_record_start);
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVoiceFloatingService != null)
            mVoiceFloatingService.dismissFloatingView();
    }

    @Override
    protected void onPause() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isScreenOn()) {
            if (myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE || myApp.callState == Const.MESSAGE_CALL_WAIT_STATE) {
                // 6.0及以上，开启了悬浮窗权限，VoiceFloatingService已经绑定成功
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && Settings.canDrawOverlays(this) && mVoiceFloatingService != null) {
                    mVoiceFloatingService.showFloatingView();
                } else if (myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE) {
                    mHandler.removeMessages(VideocallHandler.REQUEST_END_CALL);
                    mHandler.sendEmptyMessage(VideocallHandler.REQUEST_END_CALL);
                }
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("MsgSdkManager" + " VideoCallActivity3 onDestroy");
        myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
        LogUtil.e("stopMediaPlayer  VideoCallActivity3");
        MyMediaPlayerUtil.getInstance().stopMediaPlayer(myApp);
        LogUtil.e("stopMediaPlayer complete VideoCallActivity3");
        VibratorUtil.cancelVibrate(this);
        if (mMsgSdkManager != null) {
            mMsgSdkManager.destroy();
            mMsgSdkManager = null;
        }
        unregisterReceiver(mReceiver);
        unbindService(serviceConnection);
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.e("stopMediaPlayer onDestroy VideoCallActivity3");
        super.onDestroy();
    }

    private void analysisVideoCallParams(JSONObject pl) {

        appId = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APPID);
        if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APPSECRET))
            appSecret = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APPSECRET);
        if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RTC_PROVIDER))
            rtcProvider = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RTC_PROVIDER);

        if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APP_URL))
            appUrl = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APP_URL);
        if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_DEVICE_URL))
            deviceUrl = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_DEVICE_URL);

        if (callType == 0) {
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID_OTHER))
                uidOther = (int) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID_OTHER);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN_OTHER))
                tokenOther = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN_OTHER);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN))
                tokenSelf = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN))
                authTokenSelf = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER))
                authTokenOther = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER);
        } else {
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CHANNELNAME))
                channelName = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CHANNELNAME);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID_OTHER))
                uidSelf = (int) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID_OTHER);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN_OTHER))
                tokenSelf = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN_OTHER);
            if (pl.containsKey(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER))
                authTokenSelf = (String) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER);
        }
    }

    private void getTokenFromServer() {

        channelName = myApp.getCurUser().getEid() + TimeUtil.getDayTimeStampLocal();
        uidSelf = new Random().nextInt(9) + 1;
        MyMsgData tokenMsg = new MyMsgData();
        tokenMsg.setCallback(this);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID, uidSelf);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CHANNELNAME, channelName);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RTC_VERSION, rtcVersion);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TARGETEID, focusWatch.getEid());
        tokenMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_GET_AGORA_TOKEN, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(tokenMsg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void sendVideoCallMsg() {

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL);
        pl.put(CloudBridgeUtil.KEY_NAME_SN, myApp.videoCallSn);
        pl.put(CloudBridgeUtil.KEY_NAME_SEID, myApp.getCurUser().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CHANNELNAME, channelName);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APPID, appId);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TUTKTYPE, "1");
        if (!TextUtils.isEmpty(rtcProvider))
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RTC_PROVIDER, rtcProvider);
        if (!TextUtils.isEmpty(appSecret))
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APPSECRET, appSecret);
        if (!TextUtils.isEmpty(appUrl))
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_APP_URL, appUrl);
        if (!TextUtils.isEmpty(deviceUrl))
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_DEVICE_URL, deviceUrl);

        if (!MsgSdkManager.RTC_PROBVIDER_JUPHOON.equals(rtcProvider)) {
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_UID_OTHER, uidOther);
            pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_TOKEN_OTHER, tokenOther);
            if (!TextUtils.isEmpty(authTokenOther))
                pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER, authTokenOther);
        }

        LogUtil.e("wwww send msg = " + pl.toString());
        MsgCallback callback = null;
        if (timeOut == 30)
            callback = VideoCallActivity3.this;
        if (mNetService != null)
            mNetService.sendE2EMsg(focusWatch.getEid(), myApp.videoCallSn, pl, 30 * 1000, true, callback);
    }

    private void sendReceiveCall() {
        if (mNetService != null)
            mNetService.sendJuphoonVideoCallResp(focusWatch.getEid(), myApp.videoCallSn, CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_RECEIVECALL, appUrl,deviceUrl);
    }

    private void sendEndCallMsg() {

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL_END);
        pl.put(CloudBridgeUtil.KEY_NAME_SEID, myApp.getCurUser().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_SN, myApp.videoCallSn);
        if (mNetService != null)
            mNetService.sendE2EMsg(focusWatch.getEid(), Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), pl, 30 * 1000, true, null);
    }

    private void uploadVideocallTime(String cmd) {

        if (rtcProvider == null)
            return;

        MyMsgData msg = new MyMsgData();
        msg.setCallback(this);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CHANNELNAME, channelName);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_AUTHTOKEN, authTokenSelf);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RTC_PROVIDER, rtcProvider);
        pl.put(CloudBridgeUtil.KEY_NAME_VIDEOCALL_CMD, cmd);
        pl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, TimeUtil.getTimeStampLocal());
        msg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_UPLOAD_VIDEOCALL_TIME, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(msg);
    }

    private long lastSwitchWatchCameraTime;

    private void sendSwitchWatchCameraMsg() {
        if (System.currentTimeMillis() - lastSwitchWatchCameraTime < 1000) {
            return;
        }
        lastSwitchWatchCameraTime = System.currentTimeMillis();
        mNetService.sendE2EMsg(focusWatch.getEid(), CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL_SWITCH_WATCHCAMERA, 30 * 1000, false, null);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int sn = CloudBridgeUtil.getCloudMsgSN(reqMsg);
        LogUtil.e("wwww doCallBack respMsg  = " + respMsg);
        LogUtil.e("wwww doCallBack Sn  = " + sn + ", rc = " + rc + ", cid = " + cid);
        switch (cid) {
            case CloudBridgeUtil.CID_E2E_DOWN: {
                LogUtil.e("myApp.videoCallSn  = " + myApp.videoCallSn + ", sn = " + sn);
                if (myApp.videoCallSn != sn)
                    return;
                JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                JSONObject reqPl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                int sub_action = (int) reqPl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                LogUtil.e("SUB_ACTION_VALUE_NAME_VIDEO_CALL rc = " + rc + ", sub_action = " + sub_action);
                if (sub_action == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_VIDEO_CALL) {
                    LogUtil.e("SUB_ACTION_VALUE_NAME_VIDEO_CALL rc = " + rc);
                    Message msg = Message.obtain();
                    if (rc == CloudBridgeUtil.RC_SUCCESS) {
                        int result = (int) pl.get(CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESULT);
                        msg.what = VideocallHandler.VIDEO_CALL_ERROR_RECJECT;
                        if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_RECEIVECALL == result) {
                            msg.what = VideocallHandler.RECEIVE_ANSWER_CALL;
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INPHONECALL == result) {
                            msg.what = VideocallHandler.VIDEO_CALL_ERROR_RECJECT_CALLING;
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_phonecall_ing, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INVIDEOCALL == result) {
                            msg.what = VideocallHandler.VIDEO_CALL_ERROR_RECJECT_CALLING;
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_conversation_ing, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_MOBILE_2G == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_mobile_2g, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INSILENCE == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_silence, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INHIGHTEMPER == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_high_temperature, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_INLOWMEMORY == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_low_memory, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_LOWPOWER == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_low_power, focusWatch.getNickname());
                        } else if (CloudBridgeUtil.KEY_NAME_VIDEOCALL_RESPONSE_CHARGING == result) {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_in_charging, focusWatch.getNickname());
                        } else {
                            msg.obj = VideoCallActivity3.this.getString(R.string.videocall_error_other, focusWatch.getNickname());
                        }
                    } else {
                        msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                        if (rc == CloudBridgeUtil.RC_NETERROR) { //网络连接异常
                            msg.obj = VideoCallActivity3.this.getString(R.string.network_err);
                        } else if (rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) { //对方不在线
                            msg.obj = VideoCallActivity3.this.getString(R.string.network_err);
                        } else if (rc == CloudBridgeUtil.ERROR_CODE_E2G_OFFLINE) { //对方不在线
                            msg.obj = PromptUtils.getOfflinePrompt1(myApp, focusWatch.getEid());
                        } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {  //设置超时
//                            if (myApp.callState == Const.MESSAGE_CALL_WAIT_STATE && myApp.videoCallSn == sn) {
//                                msg.obj = VideoCallActivity3.this.getString(R.string.videocall_request_fail, focusWatch.getNickname());
//                            } else {
//                                return;
//                            }
                            return;
                        } else if (rc < 0) { //网络不好,有时候手表关机服务器没有返回值,所以也存在手表不在线的可能
                            msg.obj = PromptUtils.getOfflinePrompt1(myApp, focusWatch.getEid());
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
            break;

            case CloudBridgeUtil.CID_GET_AGORA_TOKEN_DOWN: {
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    analysisVideoCallParams(pl);
                    mHandler.sendEmptyMessage(VideocallHandler.GET_TOKEN_SUCCESS);
                } else {
                    Message msg = Message.obtain();
                    msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                    msg.obj = VideoCallActivity3.this.getString(R.string.network_err);
                    mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    mHandler.sendMessage(msg);
                }
            }
            break;

            case CloudBridgeUtil.CID_E2C4_DEVICE_LONGTIME_MSG_RESP:
            case CloudBridgeUtil.CID_UPLOAD_VIDEOCALL_TIME_DOWN:
                LogUtil.i(TAG + "resp = " + respMsg);
                break;
        }
    }

    private class VideocallHandler extends Handler {

        static final int TO_GET_TOKEN = 0;
        static final int GET_TOKEN_SUCCESS = 1;
        static final int SEND_VIDEOCALL_MSG_AGAIN = 2;
        static final int RECEIVE_ANSWER_CALL = 3;
        static final int AGREE_ANSWER_CALL = 4;
        static final int START_VIDEOCALL_STATE = 5;
        static final int JOIN_CHANNEL_SUCCESS = 6;
        static final int RECEIVE_REMOTE_DATA = 7;
        static final int REQUEST_END_CALL = 8;
        static final int RECEIVE_END_CALL = 9;
        static final int UPDATE_TIME_SHOW = 10;
        static final int VIDEO_CALL_ERROR = 11;
        static final int VIDEO_CALL_ERROR_RECJECT = 12;
        static final int VIDEO_CALL_ERROR_RECJECT_CALLING = 13;
        static final int RECEIVE_CALL_TIMEOUT = 14;
        static final int REQUEST_CALL_TIMEOUT = 15;

        WeakReference<VideoCallActivity3> mActivity;

        VideocallHandler(VideoCallActivity3 mActivity, Looper looper) {
            super(looper);
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final VideoCallActivity3 context = mActivity.get();
//            if(CommonUtil.isDestroy(context)) return;

            switch (msg.what) {
                case TO_GET_TOKEN: {
                    myApp.videoCallEid = focusWatch.getEid();
//                    int videoCallVersion = WatchData.getVideoCallVersion(focusWatch);
                    rtcVersion = "2";  //1，支持米讯、声网；2，支持菊风
                    LogUtil.e("videoCall rtcVersion = " + rtcVersion);
                    context.getTokenFromServer();
                }
                break;

                case GET_TOKEN_SUCCESS: {
                    if (callType == 0) {
                        timeOut = 30;
                        myApp.videoCallSn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
                        context.sendVideoCallMsg();
                        mHandler.sendEmptyMessageDelayed(SEND_VIDEOCALL_MSG_AGAIN, 2 * 1000);
                    }
                    context.initEngine();
                    context.initAccount();
                }
                break;

                case SEND_VIDEOCALL_MSG_AGAIN: {
                    if (timeOut >= 0 && myApp.callState == Const.MESSAGE_CALL_WAIT_STATE) {
                        context.sendVideoCallMsg();
                        timeOut = timeOut - 2;
                        mHandler.sendEmptyMessageDelayed(SEND_VIDEOCALL_MSG_AGAIN, 2 * 1000);
                    }
                }
                break;

                case RECEIVE_ANSWER_CALL: {
                    if (callType == 0 && myApp.callState == Const.MESSAGE_CALL_WAIT_STATE) {
                        removeMessages(REQUEST_CALL_TIMEOUT);
                        removeMessages(START_VIDEOCALL_STATE);
                        sendEmptyMessage(START_VIDEOCALL_STATE);
                    }
                }
                break;

                case AGREE_ANSWER_CALL: {
                    if (callType == 1 && myApp.callState == Const.MESSAGE_CALL_WAIT_STATE) {
                        context.sendReceiveCall();  // e2e回电
                        removeMessages(RECEIVE_CALL_TIMEOUT);
                        removeMessages(START_VIDEOCALL_STATE);
                        sendEmptyMessage(START_VIDEOCALL_STATE);
                    }
                }
                break;

                case START_VIDEOCALL_STATE: {
                    myApp.callState = Const.MESSAGE_CALL_IN_CALL_STATE;
                    LogUtil.e("context.joinChannel() ");
                    context.joinChannel();
                    Message laterMsg = Message.obtain();
                    laterMsg.what = VideocallHandler.VIDEO_CALL_ERROR;
                    laterMsg.obj = getString(R.string.videocall_other_network_error);
                    mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    mHandler.sendMessageDelayed(laterMsg, 10 * 1000);  //点击接通之后10s收不到对方画面，视为失败

                    countSecond = 0;
                    context.updateTimeShow();
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME_SHOW, 1000);
                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(myApp);
                    VibratorUtil.cancelVibrate(context);
                    MyMediaPlayerUtil.getInstance().requestAudioFocus(myApp);
                    context.configAudioSetting();
                    context.updateLayoutShow(2);
                    context.showWatchLocation();
                    //updateWatchNetState();
                    context.uploadVideocallTime(Constants.VIDEOCALL_CMD_JOINVOIPROOM);
                }
                break;

                case JOIN_CHANNEL_SUCCESS: {
                    context.setupLocalVideo();
                }
                break;

                case RECEIVE_REMOTE_DATA: {
                    context.setupRemoteVideo(uidOther);
                    removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                    removeMessages(VideocallHandler.RECEIVE_CALL_TIMEOUT);
                }
                break;

                case REQUEST_END_CALL: {
                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(myApp);
                    VibratorUtil.cancelVibrate(context);
                    myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
                    ToastUtil.show(context, context.getString(R.string.videocall_end));
                    context.sendEndCallMsg();
                    context.leaveChannel();
                    context.dealVideocallPrivateMessage(msg.what);
                    LogUtil.e("MainActivity videoCall2 REQUEST_END_CALL before finish");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.finish();
                        }
                    });

                    LogUtil.e("MainActivity videoCall2 REQUEST_END_CALL after finish");
                }
                break;

                case RECEIVE_END_CALL: {
                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(myApp);
                    VibratorUtil.cancelVibrate(context);
                    if (myApp.callState == Const.MESSAGE_CALL_WAIT_STATE || myApp.callState == Const.MESSAGE_CALL_IN_CALL_STATE) {
                        ToastUtil.show(context, context.getString(R.string.videocall_end_by_other));
                    }
                    myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
                    context.leaveChannel();
                    context.dealVideocallPrivateMessage(msg.what);
                    context.finish();
                }
                break;

                case UPDATE_TIME_SHOW: {
                    countSecond++;
                    context.updateTimeShow();
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME_SHOW, 1000);
                }
                break;

                case RECEIVE_CALL_TIMEOUT: {
                    myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
                    ToastUtil.show(context, getString(R.string.videocall_end));
                    context.leaveChannel();
                    //sendEndCallMsg();  //接听超时，不发送挂断消息
                    //dealVideocallPrivateMessage(msg.what);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.finish();
                        }
                    }, 2 * 1000);
                }
                break;

                case REQUEST_CALL_TIMEOUT: {
                    myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
                    ToastUtil.show(context, getString(R.string.videocall_request_fail, focusWatch.getNickname()));
                    context.sendEndCallMsg();
                    context.leaveChannel();
                    context.dealVideocallPrivateMessage(msg.what);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.finish();
                        }
                    }, 2 * 1000);
                }
                break;

                case VIDEO_CALL_ERROR_RECJECT:
                case VIDEO_CALL_ERROR_RECJECT_CALLING:
                case VIDEO_CALL_ERROR: {
                    myApp.callState = Const.MESSAGE_CALL_INIT_STATE;
                    String error = getString(R.string.videocall_end);
                    if (msg.obj != null)
                        error = (String) msg.obj;
                    ToastUtil.show(context, error);
                    context.leaveChannel();
                    context.sendEndCallMsg();
                    context.dealVideocallPrivateMessage(msg.what);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.finish();
                        }
                    }, 2 * 1000);
                }
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateTimeShow() {
        long min = countSecond % (60 * 60) / 60;
        long second = countSecond % 60;
        String timeShow = (String.valueOf(min).length() > 1 ? String.valueOf(min) : "0" + String.valueOf(min))
                + ":" + (String.valueOf(second).length() > 1 ? String.valueOf(second) : "0" + String.valueOf(second));
        mTimeshow.setVisibility(View.VISIBLE);
        mTimeshow.setText(timeShow);

        if (countSecond == 4 * 60 + 50) {
            ToastUtil.show(this, getString(R.string.videocall_timeout_5_prompt));
        } else if (countSecond == 8 * 60) {
            openTimeLimitDialog();
        } else if (countSecond == 9 * 60 + 55) {
            ToastUtil.show(this, getString(R.string.videocall_timeout_10_prompt));
        }
    }

    private void checkNetWork() {

        // 去电情况下，启动页面前已经弹窗，直接sendMessage
//        if (callType == 0) {
//            mHandler.removeMessages(VideocallHandler.TO_GET_TOKEN);
//            mHandler.sendEmptyMessage(VideocallHandler.TO_GET_TOKEN);
//            return;
//        }

        switch (ToolUtils.getConnectionType(VideoCallActivity3.this)) {
            case ToolUtils.NETWORKTYPE_INVALID:
                Message msg = Message.obtain();
                msg.what = VideocallHandler.VIDEO_CALL_ERROR;
                msg.obj = getString(R.string.network_err);
                mHandler.removeMessages(VideocallHandler.VIDEO_CALL_ERROR);
                mHandler.sendMessage(msg);
                break;
            case ToolUtils.NETWORKTYPE_MOBILE:
                openSelectVideoCallDialog();
                break;
            case ToolUtils.NETWORKTYPE_WIFI:
            default:
                if (callType == 0) {
                    mHandler.removeMessages(VideocallHandler.TO_GET_TOKEN);
                    mHandler.sendEmptyMessage(VideocallHandler.TO_GET_TOKEN);
                } else if (callType == 1) {
                    mHandler.removeMessages(VideocallHandler.GET_TOKEN_SUCCESS);
                    mHandler.sendEmptyMessage(VideocallHandler.GET_TOKEN_SUCCESS);
                }
                break;
        }
    }

    private void openSelectVideoCallDialog() {

//        Dialog dlg = DialogUtil.CustomNormalDialog(VideoCallActivity3.this,
//                getString(R.string.prompt),
//                getString(R.string.videocall_request_in_mobile),
//                new DialogUtil.OnCustomDialogListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (callType == 1) {
//                            mHandler.removeMessages(VideocallHandler.REQUEST_END_CALL);
//                            mHandler.sendEmptyMessage(VideocallHandler.REQUEST_END_CALL);
//                        }
//                        VideoCallActivity3.this.finish();
//                    }
//                }, getString(R.string.cancel),
//                new DialogUtil.OnCustomDialogListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (callType == 0) {
//                            mHandler.removeMessages(VideocallHandler.TO_GET_TOKEN);
//                            mHandler.sendEmptyMessage(VideocallHandler.TO_GET_TOKEN);
//                        } else if (callType == 1) {
//                            mHandler.removeMessages(VideocallHandler.GET_TOKEN_SUCCESS);
//                            mHandler.sendEmptyMessage(VideocallHandler.GET_TOKEN_SUCCESS);
//                        }
//                    }
//                }, getString(R.string.confirm));
//        dlg.show();

        if (callType == 0) {
            mHandler.removeMessages(VideocallHandler.TO_GET_TOKEN);
            mHandler.sendEmptyMessage(VideocallHandler.TO_GET_TOKEN);
        } else if (callType == 1) {
            mHandler.removeMessages(VideocallHandler.GET_TOKEN_SUCCESS);
            mHandler.sendEmptyMessage(VideocallHandler.GET_TOKEN_SUCCESS);
        }
        ToastUtil.show(this, getString(R.string.videocall_request_in_mobile));
    }

    private void openTimeLimitDialog() {

        Dialog dlg = DialogUtil.CustomNormalDialog(VideoCallActivity3.this,
                getString(R.string.prompt),
                getString(R.string.videocall_timeout_8_prompt),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                },
                getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        mHandler.removeMessages(VideocallHandler.REQUEST_END_CALL);
                        mHandler.sendEmptyMessage(VideocallHandler.REQUEST_END_CALL);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void switchLocalRemoteView() {

        SurfaceView bigView = (SurfaceView) remoteVideoView.getChildAt(0);
        SurfaceView smallView = (SurfaceView) localVideoView.getChildAt(0);

        if (bigView != null && smallView != null) {
            bigView.setZOrderMediaOverlay(true);
            smallView.setZOrderMediaOverlay(true);
            remoteVideoView.removeView(bigView);
            localVideoView.removeView(smallView);
            //把Z-Order比喻成一座塔的话，先addView进去的就在塔上面，后面addView的在塔下面
            remoteVideoView.addView(smallView);
            localVideoView.addView(bigView);
        }
    }

    private void dealVideocallPrivateMessage(int msgType) {

        LogUtil.i(TAG + " msgType = " + msgType);
        if (haveSendPrivatemsg)
            return;  //防止重复发送
        if (callType != 0)
            return;
        if (countSecond > 0) {
            sendVideocallPrivateMessage(4, countSecond);
        } else {
            if (msgType == VideocallHandler.REQUEST_END_CALL || msgType == VideocallHandler.VIDEO_CALL_ERROR) {
                sendVideocallPrivateMessage(3, 0);
            } else if (msgType == VideocallHandler.REQUEST_CALL_TIMEOUT) {
                sendVideocallPrivateMessage(1, 0);
            } else if (msgType == VideocallHandler.VIDEO_CALL_ERROR_RECJECT || msgType == VideocallHandler.RECEIVE_END_CALL) {
                sendVideocallPrivateMessage(2, 0);
            } else if (msgType == VideocallHandler.VIDEO_CALL_ERROR_RECJECT_CALLING) {
                sendVideocallPrivateMessage(5, 0);
            } else {
                sendVideocallPrivateMessage(3, 0);
            }
        }
        haveSendPrivatemsg = true;
        sendPrivateMessageNotify();
    }

    // 通话结束类型 1 超时未接通，2 对方拒绝，3 主动挂断，4 正常接通通话
    private void sendVideocallPrivateMessage(int callType, int duration) {

        JSONObject contentJo = new JSONObject();
        contentJo.put(CloudBridgeUtil.E2C_PL_KEY_VIDEOCALL_TYPE, callType);
        contentJo.put(CloudBridgeUtil.E2C_PL_KEY_DURATION, duration);

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setmWatchId(focusWatch.getWatchId());
        entity.setmDuration(duration);
        entity.setmSrcId(myApp.getCurUser().getEid());
        entity.setmDate(TimeUtil.getTimeStampLocal());
        entity.setmIsFrom(false);
        entity.setmFamilyId(myApp.getWatchPrivateGid(focusWatch.getEid()));
        entity.setmSended(ChatMsgEntity.CHAT_SEND_STATE_SUCCESS);
        entity.setmTryTime(1);
        entity.setmAudioPath(contentJo.toString());
        entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEOCALL);
        ChatHisDao.getInstance(myApp).addChatMsg(myApp.getWatchPrivateGid(focusWatch.getEid()), entity);  //存入数据库

        MyMsgData e2c = new MyMsgData();
        e2c.setTimeout(60 * 1000);
        e2c.setFinalTimeout(60 * 1000);
        e2c.setNeedNetTimeout(true);
        e2c.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, entity.getmFamilyId());
        String key = CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE + entity.getmFamilyId() +
                CloudBridgeUtil.E2C_SPLIT_MEG +
                CloudBridgeUtil.E2C_SERVER_SET_TIME;
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key);
        JSONObject chat = new JSONObject();
        chat.put(CloudBridgeUtil.E2C_PL_KEY_TYPE, CloudBridgeUtil.E2C_PL_KEY_TYPE_VIDEOCALL);
        chat.put(CloudBridgeUtil.E2C_PL_KEY_EID, entity.getmSrcId());
        chat.put(CloudBridgeUtil.E2C_PL_KEY_DURATION, 0);
        chat.put(CloudBridgeUtil.E2C_PL_KEY_CONTENT, contentJo.toJSONString());
        pl.put(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY, chat);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        entity.setmSendStartTime(System.currentTimeMillis());
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C4_DEVICE_LONGTIME_MSG_REQ, sn, myApp.getToken(), pl));
        e2c.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, focusWatch.getDeviceProtocolVersion());
        if (mNetService != null)
            mNetService.sendNetMsg(e2c);
    }

    private void sendPrivateMessageNotify() {
        // 通知私聊界面刷新页面。按私聊界面受到消息来处理
        Intent intent = new Intent(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY);
        sendBroadcast(intent);
    }

    // 显示手表位置
    private VideoCallLocationPresenter locationPresenter;

    private void showWatchLocation() {

        locationPresenter = new VideoCallLocationPresenter(myApp, focusWatch, VideoCallActivity3.this);
        locationPresenter.setMapVisibility(MsgSdkManager.RTC_PROBVIDER_JUPHOON.equals(rtcProvider));
        locationPresenter.initMap();
        locationPresenter.reqFocusWatchLocation();
    }
}
