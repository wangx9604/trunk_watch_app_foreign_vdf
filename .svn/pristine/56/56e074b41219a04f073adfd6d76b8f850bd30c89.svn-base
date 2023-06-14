package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.SosWarning;
import com.xiaoxun.xun.beans.WarningInfo;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
* 类名称：SosStartActivity
* 修改人：zhangjun5
* 修改时间：2015/11/6 19:32
* 方法描述：当sos数据来时，调用界面。显示sos过场动画，同时在这里进行数据信息记录。
*
*/
public class SosStartActivity extends NormalActivity implements OnClickListener{

    private ImageButton mNothing;
    private ImageButton mDoNow;
    private ImageView  mWatchHead;
    private ImibabyApp mApp;
    private TextView mNiceName;
    private TextView mSosInfo;
    private SosWarning soswarning;   //sos警告信息
    private String mFamilyid;
    private String mSosTime;
    private Bitmap mDeviceLocationA;
    private Bitmap mDeviceLocationB;
	private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

	private Vibrator vibrator;

	// add by qiuxiaowei 20150819 for SOS sound
    private MediaPlayer mp;
    // end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_sos_warning);
        setTintColor(getResources().getColor(R.color.color_3));
		mApp = (ImibabyApp)getApplication();
		Intent it = getIntent();
		 
		mApp.setmSosStartFlag(true);
		mNiceName = findViewById(R.id.nicename);
		mSosInfo = findViewById(R.id.sos_time);
		mWatchHead = findViewById(R.id.watch_head);
        mNothing = findViewById(R.id.nothing);
        mNothing.setOnClickListener(this);
        mDoNow = findViewById(R.id.donow);
        mDoNow.setOnClickListener(this);
 
        refreshSosInfo(it);
	}

	/**
	* 类名称：SosStartActivity
	* 修改人：zhangjun5
	* 修改时间：2015/11/9 10:19
	* 方法描述：add by qiuxiaowei 20150821  显示过场动画，声音和震动。
	*
	* 修改描述：添加sos语音列表的初始化消息，并把该信息添加到全局变量列表中。
	*
	*/
	private void refreshSosInfo(Intent it) {
	   	mFamilyid = it.getStringExtra("familyid");
		mSosTime = it.getStringExtra("sos");
		//创建sos语音列表，并添加到全局变量列表中
		HashMap<String, ArrayList<ChatMsgEntity>> mSosChatList =
				new HashMap<String, ArrayList<ChatMsgEntity>>();
		mSosChatList.put(mFamilyid,new ArrayList<ChatMsgEntity>());
		mApp.getmSosCollectList().put(mSosTime, mSosChatList);

		mApp.setmSosFamily(mFamilyid);
		for(WarningInfo warn:mApp.getmWarningMsg().get(mFamilyid)){
			if(warn.getmTimestamp().equals(mSosTime)){
				soswarning = warn.getmSos();
			}
		}
		WatchData watch = mApp.getCurUser().queryWatchDataByEid(soswarning.getmEid());
		if(watch != null){	        
	        mNiceName.setText(watch.getNickname());	        
	        mSosInfo.setText(getString(R.string.ask_for_help, TimeUtil.getSosTime(mSosTime)));
		}
        
        AnimationDrawable anim = new AnimationDrawable();

        Drawable dir = mApp.getHeadDrawableByFile(getResources(), 
        		mApp.getCurUser().getHeadPathByEid(soswarning.getmEid()),soswarning.getmEid(), R.drawable.default_head);
        mDeviceLocationA = ImageUtil.getMaskBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.max_0), dir);

        paintView(R.drawable.animation_1, DensityUtil.dip2px(mApp, (float)70.66), DensityUtil.dip2px(mApp, (float)52.33));
        anim.addFrame(new BitmapDrawable(mBitmap), 150);
        paintView(R.drawable.animation_2, DensityUtil.dip2px(mApp, (float)64.66), DensityUtil.dip2px(mApp, (float)52.33));
        anim.addFrame(new BitmapDrawable(mBitmap), 150);
        paintView(R.drawable.animation_3, DensityUtil.dip2px(mApp, (float)70.66), DensityUtil.dip2px(mApp, (float)52.33));
        anim.addFrame(new BitmapDrawable(mBitmap), 150);
        paintView(R.drawable.animation_4, DensityUtil.dip2px(mApp, (float)78), DensityUtil.dip2px(mApp, (float)52.33));
        anim.addFrame(new BitmapDrawable(mBitmap), 150);
        mWatchHead.setBackground(anim);
        anim.setOneShot(false);
        anim.start();

        // add by qiuxiaowei 20150819 for SOS sound
        if (mp == null) mp = new MediaPlayer();
        playSosSound();
        // end

		startVibrator();
	}
	// end
	
	/**
	 * 重画图片
	 */
	private void paintView(int mPictureB, int top, int left) {
        mDeviceLocationB=BitmapFactory.decodeResource(getResources(), mPictureB);
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		int w = mDeviceLocationB.getWidth();
		int h = mDeviceLocationB.getHeight();

	    mBitmap = Bitmap.createBitmap(w, h, mDeviceLocationB.getConfig());
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawBitmap(mDeviceLocationB, new Matrix(), mPaint);
		mCanvas.drawBitmap(mDeviceLocationA, left, top,mPaint);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		refreshSosInfo(intent);		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopSosSound();
		stopVibrator();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nothing:
//	        mApp.cleanTransNotice();
			mApp.setmSosStartFlag(false);
			//当关闭该sos页面时，清除该SOS界面的聊天信息。
			if(mApp.getmSosCollectList().get(mSosTime) != null)
				mApp.getmSosCollectList().remove(mSosTime);
			finish();
			break;
		case R.id.donow:
	 //       mApp.cleanTransNotice();
			WatchData watch = mApp.getCurUser().queryWatchDataByEid(soswarning.getmEid());
			if(watch == null) {
				Dialog dlg = DialogUtil.CustomALertDialog(SosStartActivity.this,
						getString(R.string.prompt),
						getString(R.string.watch_unbind_already),
						new DialogUtil.OnCustomDialogListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						},
						getText(R.string.cancel).toString(),
						new DialogUtil.OnCustomDialogListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						},
						getText(R.string.confirm).toString());
				dlg.show();
			}else {
				Intent it = new Intent(this, SosGoogleActivity.class);
				it.putExtra("familyid", mFamilyid);
				it.putExtra("sos", mSosTime);
				startActivity(it);
				finish();
			}
			break;
		default:
			break;
		}
	}

	// add by qiuxiaowei 20150819 for SOS sound
	@SuppressLint("HandlerLeak")
	private Handler autoStopHandler = new Handler() {
		@Override
    		public void handleMessage(Message msg) {
				stopSosSound();
				stopVibrator();
    		}
	};

	private void playSosSound() {
		try {
			if (mp != null) {
			    AssetManager am = getAssets();
			    AssetFileDescriptor afd = am.openFd("music/sos_sound.mp3");
			    FileDescriptor fd = afd.getFileDescriptor();
			    mp.reset();
				mp.setAudioStreamType(AudioManager.STREAM_RING);
				mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());
				mp.setVolume(1f, 1f);
				mp.setLooping(true);
			    mp.prepare();
			    mp.start();
			    // set auto stop handler
			    autoStopHandler.sendEmptyMessageDelayed(0, 6000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopSosSound(){
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
		autoStopHandler.removeMessages(0);
	}
	// end

	private void startVibrator() {
		if (vibrator == null) {
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}
		long [] pattern = {100,600,200,600};
		vibrator.vibrate(pattern, -1);
	}

	private void stopVibrator() {
		if (vibrator != null) {
			vibrator.cancel();
		}
	}
	@Override
	protected void unbindOtherWatch(String family) {
		super.unbindOtherWatch(family);
		if (family.equals(mFamilyid)){
			finish();
		}
	}
}


