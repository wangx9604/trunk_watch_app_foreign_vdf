/**
 * 
 */
package com.xiaoxun.xun.utils;

import android.os.Environment;
import android.util.Log;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description Of The Class<br>
 *
 * @author  liutianxiang
 * @version 1.000, 2015-1-8
 * 
 */
public final class LogUtil {
	/**
	 * 
	 */
	public enum LOGLEVEL
	{
		VERBOSE(0x0),
		DEBUG(0x1),
		INFO(0x2),
		WARNING(0x3),
		ERROR(0X4),
		NON(0x5);	
		int mnLevel = 0;
		LOGLEVEL(int nLevel)
		{
			mnLevel = nLevel;
		}
		
		public int GetValue()
		{
			return mnLevel;
		}
		
	}

    private static LOGLEVEL Log_Level = LOGLEVEL.VERBOSE;
	private final static String sTag = "IMIBABY";
	public LogUtil() {
	}

	public static void v(String sMsg)
	{
		if(Log_Level.GetValue() > LOGLEVEL.VERBOSE.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.v(sTag, sMsg);
	}
	
	public static void d(String sMsg)
	{
		if(Log_Level.GetValue() > LOGLEVEL.DEBUG.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.d(sTag, sMsg);
	}

	public static void d(String sMsg,Throwable t)
	{
		if(Log_Level.GetValue() > LOGLEVEL.DEBUG.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.d(sTag, sMsg,t);
	}
	
	public static void i(String sMsg)
	{
		if(Log_Level.GetValue() > LOGLEVEL.INFO.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.i(sTag, sMsg);
	}
	
	public static void w(String sMsg)
	{
		if(Log_Level.GetValue() > LOGLEVEL.WARNING.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.w(sTag, sMsg);
	}

	public static void w(String sMsg, Throwable tr)
	{
		if(Log_Level.GetValue() > LOGLEVEL.WARNING.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.w(sTag, sMsg,tr);
	}
	
	public static void e(String sMsg)
	{
		if(Log_Level.GetValue() > LOGLEVEL.ERROR.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.e(sTag, sMsg);
	}
	
	public static void e(String sMsg, Throwable tr)
	{
		if(Log_Level.GetValue() > LOGLEVEL.ERROR.GetValue()||!BuildConfig.ISDEBUG)
			return;
		if(null != sMsg)
			Log.e(sTag, sMsg,tr);
	}

	public static void oo_sdcardLog(String sMsg){
		if(true) return;
//        try {
//            PackageInfo info = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0);
//            appName = appName + " v"+info.versionName+" release "+info.versionCode;
//        } catch (NameNotFoundException e) {
//            appName = "NA";
//            e.printStackTrace();
//        }
        if (!BuildConfig.ISDEBUG)
			return;
        Date nowtime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currTime = dateFormat.format(nowtime);
        
        String logText = "bbwatch SDCARD LOG:\n"+
            "****** GMT Time: "+currTime+"\n"+
            "****** Trace trace:\n"+sMsg+"\n"+
            "******************************\n";

        File baseDir = new File(Const.MY_BASE_DIR);
        if (!baseDir.exists())
            baseDir.mkdir();
        
        try {
            //检测文件大小，过大则重命名
            File logfile = new File(baseDir.getAbsoluteFile(),  "logFile.log");
            long size =  logfile.length();
            if (size>1024*300){
                File oldfile = new File(baseDir.getAbsoluteFile(),  "logFile"+TimeUtil.getTimeStampLocal()+".log");
                logfile.renameTo(oldfile);
            }
            FileOutputStream fos = new FileOutputStream(new File(baseDir.getAbsoluteFile(),  "logFile.log"),true);
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
    }
	 public static int getLineNumber() {
		 return Thread.currentThread().getStackTrace()[2].getLineNumber();
	 } 

	public static String getFileName() {
		return Thread.currentThread().getStackTrace()[2].getFileName();
	}
}
