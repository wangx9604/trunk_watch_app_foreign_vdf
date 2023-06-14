/**
 * Creation Date:2015-1-22
 * 
 * Copyright 
 */
package com.xiaoxun.xun.utils;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;

import java.io.File;
import java.io.IOException;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-22
 * 
 */
public class MyRecorder {
    private MediaRecorder mMediaRecorder;  
    private File recAudioFile; 
    private String srcId;
    private String desId;
    private String familyId;
    private ImibabyApp myApp;
        
    
    
  
    private static MyRecorder instance = null;
    public static MyRecorder getInstance()
    {
        if (instance == null)
            instance = new MyRecorder();
        return instance;
    }
    public MyRecorder() {
         
    }
    /**
     * 先初始化参数，再调用startRecorder ，然后调用endRecorder 获取录音文件，
     * 如 取消，则调用cancelRecorder
     * @param srcid
     * @param familyid
     * @param desid
     * @param appContext
     */
    public  void setRecorderParam(String srcid,String familyid,String desid,ImibabyApp appContext){
        srcId = srcid;
        familyId = familyid;
        desId = desid;
        myApp = appContext;
    }
    private File getNextRecorderFile(){
        StringBuilder sb = new StringBuilder();
        //sb.append(srcId);
        //sb.append("_");
        //sb.append(familyId);
        //sb.append("-");
        //sb.append(Long.valueOf(System.currentTimeMillis()/1000).toString());
        sb.append(TimeUtil.getTimestampCHN());
        sb.append(Const.VOICE_FILE_SUFFIX);
        File file=  new File(ImibabyApp.getChatCacheDir(), sb.toString());
        return file; 
    }

    public File geTestRecorderFile(){        
        File file=  new File(ImibabyApp.getChatCacheDir(), "test.amr");
        return file; 
    }
	
    public void  startRecorder() throws Exception{
        mMediaRecorder = new MediaRecorder();  
        
        recAudioFile = getNextRecorderFile(); 
        if (recAudioFile.exists()) {  
            recAudioFile.delete();  
        }  
  
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);  
        mMediaRecorder.setAudioEncodingBitRate(5150);//5150
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
        mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath()); 
        mMediaRecorder.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				// TODO Auto-generated method stub
				LogUtil.d("MediaRecorder error");
			}
		});
        mMediaRecorder.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				// TODO Auto-generated method stub
				LogUtil.d("MediaRecorder info");
			}
		});
        mMediaRecorder.prepare(); 
        mMediaRecorder.start();
    }
	
    private File getAlarmRecorderFile(){
        StringBuilder sb = new StringBuilder();
        sb.append(Long.valueOf(System.currentTimeMillis()/1000).toString());
        sb.append(".amr");
        File file=  new File(ImibabyApp.getAlarmRecordDir(), sb.toString());
        return file; 
    }
    public void startAlarmRecorder() {  
        mMediaRecorder = new MediaRecorder();  
        
        recAudioFile = getAlarmRecorderFile(); 
        if (recAudioFile.exists()) {  
            recAudioFile.delete();  
        }  
  
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);  
        mMediaRecorder.setAudioEncodingBitRate(5150);//
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
        mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());  
        try {  
            mMediaRecorder.prepare();  
        } catch (IllegalStateException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        mMediaRecorder.start();  
    } 

    void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
        	mMediaRecorder.reset();
        	mMediaRecorder.release();
        	mMediaRecorder = null;
        }
    }
    public  File endRecorder(){
        File recFile;
        if (mMediaRecorder!=null) {  
        	try {
                mMediaRecorder.stop(); 
			} catch (RuntimeException e) {
				// TODO: handle exception
				if(recAudioFile != null && recAudioFile.exists()){
					recAudioFile.delete();
					recAudioFile = null;
				}
			} 
        	mMediaRecorder.reset();
            mMediaRecorder.release();  
            mMediaRecorder = null;
        }else{
			if(recAudioFile != null && recAudioFile.exists()){
				recAudioFile.delete();
				recAudioFile = null;
			}
        }
        if(recAudioFile != null){
            long len = recAudioFile.length();
            if(len < 10){
                recAudioFile = null;
            }
        }
        recFile = recAudioFile;
        recAudioFile = null;
        return recFile;
    } 
    public void cancelRecorder(){
        if (mMediaRecorder!=null) {
        	try {
                mMediaRecorder.stop();  
			} catch (Exception e) {
				// TODO: handle exception
			}
        	mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        //del canceled file
		if(recAudioFile != null && recAudioFile.exists()){
			recAudioFile.delete();
            recAudioFile = null;
		}
    }
}
