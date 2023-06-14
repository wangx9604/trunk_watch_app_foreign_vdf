package com.xiaoxun.xun.activitys;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChewbaccaUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private static final String TAG = "ASK CHEWBACCA";

	private final UncaughtExceptionHandler mOsDefaultHandler;

	private final ImibabyApp mApp;
	
	public ChewbaccaUncaughtExceptionHandler(ImibabyApp app, UncaughtExceptionHandler previous)
	{
	    mApp = app;
		mOsDefaultHandler = previous;
	}
	
	@SuppressWarnings("deprecation")
	public void uncaughtException(Thread thread, Throwable ex) {
		
		String appName = "bbwatch";
		try {
			PackageInfo info = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0);
			appName = appName + " v"+info.versionName+" release "+info.versionCode;
		} catch (NameNotFoundException e) {
			appName = "NA";
			e.printStackTrace();
		}
		
		Date nowtime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currTime = dateFormat.format(nowtime);
		
		String logText = "Hi. It seems that we have crashed.... Here are some details:\n"+
			"****** GMT Time: "+currTime+"\n"+
			"****** Application name: "+appName+"\n"+
			"******************************\n"+
			"****** Exception type: "+ex.getClass().getName()+"\n"+
			"****** Exception message: "+ex.getMessage()+"\n"+
			"****** Trace trace:\n"+getStackTrace(ex)+"\n"+
			"******************************\n"+
			"****** Device information:\n"+getSysInfo()+
			"******************************\n";
		
		File baseDir = mApp.getExternalFilesDir(Const.MY_BASE_DIR);
		if (!baseDir.exists())
			baseDir.mkdir();
		
		try {
			FileOutputStream fos = new FileOutputStream(mApp.getCurLogFile(),true);  
			
			fos.write(logText.getBytes());
			fos.flush();
			fos.close();
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ToastUtils.showShort("quit app by exception");
		/*        
		Notification notification = new Notification(R.drawable.icon_myhome_crash, "Oops! Didn't see that coming, I crashed.", System.currentTimeMillis());
		Intent notificationIntent = new Intent(mApp, SendBugReportUiActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra(SendBugReportUiActivity.CRASH_REPORT_TEXT, logText);		
		PendingIntent contentIntent = PendingIntent.getActivity(mApp, 0, notificationIntent, 0);
		notification.setLatestEventInfo(mApp, 
				"My home", 
				"Oops! Didn't see that coming, I crashed.",
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notifying
		NotificationManager notificationManager = (NotificationManager)mApp.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
*/
		//and sending to the OS
		if (mOsDefaultHandler != null)
		{
			LogUtil.i(TAG+"  "+"Sending the exception to OS exception handler...");
			mOsDefaultHandler.uncaughtException(thread, ex);
		}
		
		//System.exit(0);
	}

	private String getStackTrace(Throwable ex) {
		StackTraceElement[] stackTrace = ex.getStackTrace();
		StringBuilder sb = new StringBuilder();
		
		for(StackTraceElement element : stackTrace)
		{
			sb.append(element.toString());
			sb.append('\n');
		}
		
		if (ex.getCause() == null)
			return sb.toString();
		else
		{
			ex = ex.getCause();
			String cause = getStackTrace(ex);
			sb.append("*** Cause: "+ex.getClass().getName());
			sb.append('\n');
			sb.append("** Message: "+ex.getMessage());
			sb.append('\n');
			sb.append("** Stack track: "+cause);
			sb.append('\n');
			return sb.toString();
		}
	}

	private static String getSysInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("BRAND:").append(android.os.Build.BRAND).append("\n");
		sb.append("DEVICE:").append(android.os.Build.DEVICE).append("\n");
		sb.append("Build ID:").append(android.os.Build.DISPLAY).append("\n");
		sb.append("changelist number:").append(android.os.Build.ID).append("\n");
		sb.append("MODEL:").append(android.os.Build.MODEL).append("\n");
		sb.append("PRODUCT:").append(android.os.Build.PRODUCT).append("\n");
		sb.append("TAGS:").append(android.os.Build.TAGS).append("\n");
		sb.append("VERSION.INCREMENTAL:").append(android.os.Build.VERSION.INCREMENTAL).append("\n");
		sb.append("VERSION.RELEASE:").append(android.os.Build.VERSION.RELEASE).append("\n");
		return sb.toString();
	}
}
