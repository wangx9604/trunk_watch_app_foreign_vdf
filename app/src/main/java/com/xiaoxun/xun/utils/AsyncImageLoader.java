package com.xiaoxun.xun.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.collection.LruCache;
import android.util.Base64;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;

import net.minidev.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class AsyncImageLoader implements MsgCallback{

	private static final int IMAGE_CACHE_MIN_SIZE = 1024 * 1024 * 2; //2M
	private static File imageCacheDir = null;
	private Drawable defaultIcon = null;
	private LruCache<String, Drawable> lruImageCache;
	private ImibabyApp myApp;
	private HashMap<String,String> imageMd5Map=new HashMap<>();

	public enum ImageType{
		app_advertisement_icon1,
		app_advertisement_icon2,
		app_normal_icon,
		unkown_type
	}

	public AsyncImageLoader(ImibabyApp app) {

	    myApp = app;
		int cacheSize = IMAGE_CACHE_MIN_SIZE;
        
		ActivityManager manager = (ActivityManager)myApp.getSystemService(Context.ACTIVITY_SERVICE);
		int memClass = manager.getMemoryClass();
		int suitTableMemorySize = (memClass*1024*1024/8);
		LogUtil.i("mem size=" + suitTableMemorySize);

		if (cacheSize < suitTableMemorySize)
			cacheSize = suitTableMemorySize;
		LogUtil.i("ImageCacheSize="+cacheSize);
		
		lruImageCache = new LruCache<String,Drawable>(cacheSize){
			@Override
			protected int sizeOf(String key, Drawable value) {
				int size = 0;
				BitmapDrawable drawable = (BitmapDrawable)value;
				Bitmap bitmap;
				if (drawable != null) {
					bitmap = drawable.getBitmap();
					if (bitmap != null)
						size = bitmap.getWidth() * bitmap.getHeight() * 4;
				}	
				return size;
			}
		};
	}
	
	public boolean put(String keyUrl,Drawable drawable) {

		if (keyUrl == null || keyUrl.length() <= 0 || drawable == null)
			return false;

		lruImageCache.put(keyUrl, drawable);
		return true;
	}

	public Drawable get(String keyUrl) {
		return lruImageCache.get(keyUrl);
	}

	private Drawable getDefaultDrawable(int defaultId) {
		defaultIcon = myApp.getResources().getDrawable(defaultId);
		return defaultIcon;
	}

	// 最终暴漏出去的方法
	public Drawable load(int defaultId, Object context, String imageUrl, String eid, final ImageCallback imageCallback, boolean autoDown) {
		return this.load(defaultId, context, imageUrl, eid, 0, imageCallback, autoDown);
	}
	/**
	 * 
	 * @param context
	 * @param imageUrl
	 * @param imageCallback: 
	 * @return
	 */ 
	public Drawable load(int defaultId,Object context,String imageUrl,String eid,final int targetWidth,final ImageCallback imageCallback,boolean autoDown) {

		Drawable drawable = null;
		
//		LogUtil.i("AsyncImageLoader::load() imageUrl="+imageUrl);
		if (imageUrl == null || imageUrl.length() <= 0)
			return null;
		
		//内存中获取
		drawable = lruImageCache.get(imageUrl);
		if (drawable != null) {
//			LogUtil.i("imageUrl="+imageUrl+" already in lruImageCache");
			return drawable;
		}

		// 默认头像的drawable
		drawable = getDefaultDrawable(defaultId);

		// 从本地存储获取
		File destFile = new File(ImibabyApp.getIconCacheDir(), imageUrl + ".jpg");
		if (destFile.exists()) {
			BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), destFile.getPath());
			Bitmap original =bitdrawable.getBitmap();
			if (original != null) {
				drawable = bitdrawable;
				put(imageUrl, drawable);    //添加到内存中
				return drawable;
			}
		} else {
			// 从服务器获取
			if (autoDown) {
				imageMd5Map.put(eid, imageUrl);
				sendHeadImageC2E(eid);
			}
		}

		return drawable;
	}

    private void sendHeadImageC2E(String eid) {

        MyMsgData c2e = new MyMsgData();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_HEADIMG);
        
        c2e.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.E2C_PL_KEY, key.toString());
        c2e.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_C2E_GET_MESSAGE, pl));
        if(myApp.getNetService() != null)
		myApp.getNetService().sendNetMsg(c2e);
    }	

    public interface ImageCallback{  
        void imageLoaded(Object context, Drawable imageDrawable, String imageUrl);
    }  

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

		int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
		JSONObject pl;
		switch (cid) {
			case CloudBridgeUtil.CID_C2E_GET_MESSAGE_RESP:
				int rcMapGet = CloudBridgeUtil.getCloudMsgRC(respMsg);
				if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
					pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
					byte[] bitmapArray;
					Intent intt;

					try {
						bitmapArray = Base64.decode((String) pl.get(CloudBridgeUtil.HEAD_IMAGE_DATA), Base64.NO_WRAP);
						// 将头像文件存储在本地
						String md5 = MD5.md5_bytes(bitmapArray) ;
						// 20170516修改：因下载文件的MD5值可能与原来不一致，导致一直下载（死循环），这里做修改，MD5值不一致的话取下载前的MD5值   by hyy
						String key = (String) ((JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL)).get(CloudBridgeUtil.E2C_PL_KEY);
						String eid = "******";
						if (key != null && key.length() >= 35){
							eid = key.substring(3, 35);
						}
						if (imageMd5Map.get(eid) != null && !imageMd5Map.get(eid).equals(md5)) {
							// 如果md5值不一致，说明文件在上传或者下载过程中被损坏了，需要防止异常出现，经测试没有发生异常
							md5 = imageMd5Map.get(eid);
							imageMd5Map.remove(eid);
						} else {
						}
						File headfile = new File(ImibabyApp.getIconCacheDir(), md5 + ".jpg");
						FileOutputStream out = new FileOutputStream(headfile);
						out.write(bitmapArray);
						out.close();

						//通知接受到头像下载完成消息，显示头像的地方都要监听此广播
						intt = new Intent();
						intt.setAction(Const.ACTION_DOWNLOAD_HEADIMG_OK);
						myApp.sendBroadcast(intt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			default:
				break;
		}
	}
}
