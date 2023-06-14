package com.xiaoxun.xun.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.xiaoxun.xun.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guxiaolong on 2016/8/31.
 */

public class DownloadManagerHelper {

    private static ExecutorService mImageThreadPool = null;
    private static final int THREAD_NUM = 4;
    protected static final int DOWNLOAD = 1;

    public static void download(final String url, final File file, final OnDownloadListener listener) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == DOWNLOAD) {
                    listener.onDownload(url, (Boolean) msg.obj);
                }
            }
        };
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                boolean success = downloadFromUrl(url, file);
                Message msg = Message.obtain(handler, DOWNLOAD, success);
                msg.sendToTarget();

            }
        });
    }

    private static boolean downloadFromUrl(String urlTarget, File file) {
        try {
            URL url = new URL(urlTarget);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[2048];
            int len;
            OutputStream os = new FileOutputStream(file);
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
                }
            }
        }
        return mImageThreadPool;
    }

    public synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdown();
            mImageThreadPool = null;
        }
    }

    public interface OnDownloadListener {
        void onDownload(String url, boolean success);
    }

    private static final String IMG_CACH_DIR = "/" + Const.MY_BASE_DIR + "/filecache";
    private static String SD_ROOT_PATH = null;
    private static String DATA_ROOT_PATH = null;

    public static String getCacheDirectory(Context context) {
        String cachePath = null;
        DATA_ROOT_PATH = context.getCacheDir().getAbsolutePath();
        SD_ROOT_PATH = context.getExternalFilesDir(null).getAbsolutePath();
        if (isSdcardAvailable()) {
            cachePath = SD_ROOT_PATH + IMG_CACH_DIR;
        } else {
            cachePath = DATA_ROOT_PATH + IMG_CACH_DIR;
        }
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return cachePath;
    }

    private static boolean isSdcardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
