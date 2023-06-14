package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guxiaolong on 2016/7/21.
 */

public class ImageDownloadHelper {
    /**
     * 下载image的线程池
     */
    private ExecutorService mImageThreadPool = null;
    /**
     * 文件缓存的工具类
     */
    private FileCacheUtil mFileCacheUtil = null;
    /**
     * 线程池中线程的数量
     */
    private static final int THREAD_NUM = 2;
    /**
     * 缩略图的宽
     */
    private static final int REQ_WIDTH = 640;
    /**
     * 缩略图的高
     */
    private static final int REQ_HEIGHT = 640;

    protected static final int DOWNLOAD = 1;

    private Context mContext;

    public ImageDownloadHelper(Context context) {
        this.mContext = context;
        mFileCacheUtil = new FileCacheUtil(context);
    }

    /**
     * 下载一张图片，先从内存缓存中找，如果没有则去文件缓存中找，如果还没有就从网络中下载
     *
     * @param url
     * @param listener
     * @return
     */
    public Bitmap downloadImage(final String url, final OnImageDownloadListener listener) {
        final String subUrl = url.replaceAll("[^\\w]", "");
        Bitmap bitmap = showCacheBitmap(subUrl);
        if (bitmap != null) {
            return bitmap;
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == DOWNLOAD) {
                        listener.onImageDownload(url, (Bitmap) msg.obj);
                    }
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getImageFromUrl(url);
                    Message msg = Message.obtain(handler, DOWNLOAD, bitmap);
                    msg.sendToTarget();

                    mFileCacheUtil.addBitmapToFile(subUrl, bitmap);
                    BitmapLruCacheHelper.getInstance().addBitmapToMemCache(subUrl, bitmap);
                }
            });
        }
        return null;
    }

    /**
     * 显示缓存中的图片
     *
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url) {
        Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(url);
        if (bitmap != null) {
            return bitmap;
        } else {
            bitmap = mFileCacheUtil.getBitmapFromFile(url);
            if (bitmap != null) {
                BitmapLruCacheHelper.getInstance().addBitmapToMemCache(url, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 从url中获取bitmap
     * @param url
     * @return
     */
    public Bitmap getImageFromUrl(String url) {
        HttpURLConnection conn = null;
        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(30 * 1000);
            conn.setDoInput(true);

            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                is.close();
                byte[] data = out.toByteArray();
                if (data.length > 256 * 1024) {
                    return BitmapUtil.decodeSampledBitmapFromByteArray(data, REQ_WIDTH, REQ_HEIGHT);
                } else {
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            } else {
                Log.e("ImageDownloadHelper", "responseCode: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取线程池实例
     */
    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 取消当前的任务
     */
    public synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdown();
            mImageThreadPool = null;
        }
    }

    /**
     * 操作下载后的图片的回调接口
     */
    public interface OnImageDownloadListener {
        void onImageDownload(String url, Bitmap bitmap);
    }
}
