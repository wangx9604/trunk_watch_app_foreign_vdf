package com.xiaoxun.xun.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import com.xiaoxun.xun.ImibabyApp;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MyMediaPlayerUtil {
	
	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;
    private static MyMediaPlayerUtil instance = null;
    public static MyMediaPlayerUtil getInstance()
    {
        if (instance == null)
            instance = new MyMediaPlayerUtil();
        return instance;
    }
    public MyMediaPlayerUtil() {

    }

	/**
	 * 判断蓝牙耳机是否连接
	 *
	 * @return 蓝牙耳机是否连接
	 * @param mAudioManager
	 */
	public boolean isBluetoothHeadsetConnected(AudioManager mAudioManager) {

		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		LogUtil.i("HEADSET.isBluetoothHeadsetConnected = " + adapter.getProfileConnectionState(BluetoothProfile.HEADSET));
		LogUtil.i("A2DP.isBluetoothHeadsetConnected = " + adapter.getProfileConnectionState(BluetoothProfile.A2DP));
		return BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
	}

	// 判断连接有线耳机
	public boolean isWiredHeadsetConnected(AudioManager audioManager) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
			for (AudioDeviceInfo device : devices) {
				int deviceType = device.getType();
				LogUtil.i(" BLUETOOTH deviceType = " + deviceType);
				if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET)
					return true;
			}
			return false;
		} else {
			return audioManager.isWiredHeadsetOn();
		}
	}
    
    public MediaPlayer StarMediaPlayer(String mPath, ImibabyApp mApp,Boolean type){
		if(mPath == null || mApp == null){
			return null;
		}
    	if(mMediaPlayer == null){
    		mMediaPlayer = new MediaPlayer();
    	}
		if(mAudioManager == null){
			mAudioManager = (AudioManager)mApp.getSystemService(Context.AUDIO_SERVICE);
		}
		mMediaPlayer.reset();
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
		LogUtil.d("huangqilin audiomanager 1");
		try {
			if(mAudioManager!= null && type){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					mAudioManager.setSpeakerphoneOn(false);
					mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
					LogUtil.d("huangqilin audiomanager 2");
				}else{
					mAudioManager.setSpeakerphoneOn(false);
					mAudioManager.setMode(AudioManager.MODE_IN_CALL);
					LogUtil.d("huangqilin audiomanager 3");
				}
			}else{
				mAudioManager.setSpeakerphoneOn(true);
				mAudioManager.setMode(AudioManager.MODE_NORMAL);

				LogUtil.d("huangqilin audiomanager 4");
			}
			requestAudioFocus(mApp);
			if(type) {
				LogUtil.d("huangqilin audiomanager 41");
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				LogUtil.d("huangqilin audiomanager 42");
			}else{
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			}

			FileInputStream fileInputStream = new FileInputStream(mPath);
			int length = fileInputStream.available();
			byte[] buffer = new byte[length];
			fileInputStream.read(buffer);
            fileInputStream.close();
			byte[] tmp = AESUtil.getInstance().decrypt(buffer);
			File file = new File(mApp.getCacheDir(), "tmp.amr.ini");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(tmp);
			fos.close();

			mMediaPlayer.setDataSource(file.getPath());
			file.delete();
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mMediaPlayer;
    }

	public MediaPlayer starAssetMediaPlayer(AssetFileDescriptor afd, Context mApp) {

		if (afd == null || mApp == null) {
			return null;
		}
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mApp.getSystemService(Context.AUDIO_SERVICE);
		}
		mMediaPlayer.reset();
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
		try {
			FileDescriptor fd;
			fd = afd.getFileDescriptor();
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(fd, afd.getStartOffset(), afd.getLength());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
		}
		return mMediaPlayer;
	}

	public MediaPlayer starAssetMediaPlayer(AssetFileDescriptor afd, ImibabyApp mApp, int streamtype) {

		if (afd == null || mApp == null) {
			return null;
		}
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mApp.getSystemService(Context.AUDIO_SERVICE);
		}
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}

		try {
			// 申请音频焦点
			requestAudioFocus(mApp);
			// 是否连接蓝牙耳机
//			if (isBluetoothHeadsetConnected(mAudioManager) ) {
//				enterScoMode();
//			} else {
//				exitSCOMode();
//			}

			FileDescriptor fd;
			fd = afd.getFileDescriptor();
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(fd, afd.getStartOffset(), afd.getLength());
			mMediaPlayer.setLooping(true);
			mMediaPlayer.setAudioStreamType(streamtype);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mMediaPlayer;
	}

	public void requestAudioFocus(ImibabyApp mApp){
		if(mAudioManager == null){
			mAudioManager = (AudioManager)mApp.getSystemService(Context.AUDIO_SERVICE);
		}
		mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}
    public void abandonAudioFocus(ImibabyApp mApp){
		mApp.mAudioPath = null;
		if(mMediaPlayer != null) {
			mMediaPlayer.reset();
			if(mMediaPlayer.isPlaying()){
				mMediaPlayer.stop();
			}
		}
		if(mAudioManager != null){
			LogUtil.d("huangqilin audiomanager 5");
			mAudioManager.setSpeakerphoneOn(true);
			mAudioManager.setMode(AudioManager.MODE_NORMAL);
			LogUtil.d("huangqilin audiomanager 6");
			mAudioManager.abandonAudioFocus(audioFocusChangeListener);
		}
	}
    public void stopMediaPlayer(ImibabyApp mApp){
		abandonAudioFocus(mApp);
    }

	// voice replay
	public void replayMediaplayer(int type){
		if (mMediaPlayer != null && mAudioManager!= null && mMediaPlayer.isPlaying()) {
			try{
				mMediaPlayer.stop();
				if(type == 1) {
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
						mAudioManager.setSpeakerphoneOn(false);
						mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
					} else {
						mAudioManager.setSpeakerphoneOn(false);
						mAudioManager.setMode(AudioManager.MODE_IN_CALL);
					}
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				}else if(type == 2){
					mAudioManager.setSpeakerphoneOn(true);
					mAudioManager.setMode(AudioManager.MODE_NORMAL);
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				}
				mMediaPlayer.prepare();
				mMediaPlayer.seekTo(0);
				mMediaPlayer.start();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	AudioManager.OnAudioFocusChangeListener audioFocusChangeListener= new AudioManager.OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			switch(focusChange){
				case AudioManager.AUDIOFOCUS_LOSS:
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
						if(mMediaPlayer != null) {
							mMediaPlayer.stop();
						}
					break;
				default:
					break;
			}
		}
	};
}
