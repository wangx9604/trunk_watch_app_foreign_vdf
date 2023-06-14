package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.xiaoxun.xun.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by guxiaolong on 2016/7/21.
 */

public class FileCacheUtil {
    /**
     * 图片缓存的相对路径
     */
    private static final String IMG_CACH_DIR = "/" + Const.MY_BASE_DIR + "/imgcache";

    /**
     * 手机缓存目录
     */
    private static String DATA_ROOT_PATH = null;
    /**
     * sd卡根目录
     */
    private static String SD_ROOT_PATH = null;

    /**
     * 缓存的扩展名
     */
    private static final String CACHE_TAIL = ".cach";

    /**
     * 最大缓存空间,单位是mb
     */
    private static final int CACHE_SIZE = 4;

    /**
     * sd卡内存低于此值时将会清理缓存,单位是mb
     */
    private static final int NEED_TO_CLEAN = 10;

    /**
     * 上下文
     */
    private Context mContext;

    private static final String TAG = "BitmapFileCacheUtils";

    public FileCacheUtil(Context context) {
        this.mContext = context;
        DATA_ROOT_PATH = context.getCacheDir().getAbsolutePath();
        SD_ROOT_PATH = context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 从缓存中获取一张图片
     */
    public Bitmap getBitmapFromFile(String key) {
        if (key == null) {
            return null;
        }
        String filename = getCacheDirectory() + File.separator + convertKeyToFilename(key);
        File file = new File(filename);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filename);
            if (bitmap == null) {
                file.delete();
            } else {
                updateFileModifiedTime(filename);
                LogUtil.e("get file from sdcard cache success...");
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 将图片存入文件缓存
     */
    public void addBitmapToFile(String key, Bitmap bm) {
        if (bm == null || key == null) {
            return;
        }
        //视情况清除部分缓存
        removeCache(getCacheDirectory());
        String filename = convertKeyToFilename(key);
        File dir = new File(getCacheDirectory());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        try {
            OutputStream out = new FileOutputStream(file);//这里需要注意，如果指定目录不存在，应该先调用mkdirs生成目录，否则可能创建文件失
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            LogUtil.e("add file to sdcard cache success...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件缓存路径
     *
     * @return
     */
    private String getCacheDirectory() {
        String cachePath = null;
        if (isSdcardAvailable()) {
            cachePath = SD_ROOT_PATH + IMG_CACH_DIR;
        } else {
            cachePath = DATA_ROOT_PATH + IMG_CACH_DIR;
        }
        return cachePath;
    }

    /**
     * 清除40%的缓存，这些缓存被删除的优先级根据近期使用时间排列,越久没被使用，越容易被删除
     */
    private void removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        double total_size = 0;
        for (File file : files) {
            total_size += file.length();
        }
        total_size = total_size / 1024 / 1024;
        if (total_size > CACHE_SIZE) {
            LogUtil.e("remove cache from sdcard cache...");
            int removeFactor = (int) (files.length * 0.4);
//            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            try {
                Arrays.sort(files, new FileLastModifiedComparator());
            } catch (Exception e) {
                
            }
            for (int i = 0; i < removeFactor; i++) {
                files[i].delete();
            }
        }
    }

    /**
     * 判断sd卡是否可用
     *
     * @return
     */
    private boolean isSdcardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 将关键字转化为文件名
     */
    private String convertKeyToFilename(String key) {
        if (key == null) {
            return "";
        }
        return key.hashCode() + CACHE_TAIL;
    }

    /**
     * 更新文件最后修改时间
     */
    private void updateFileModifiedTime(String path) {
        File file = new File(path);
        file.setLastModified(System.currentTimeMillis());
    }

    private class FileLastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if(lhs == null && rhs == null){
                return 0;
            }
            if(lhs == null){
                return -1;
            }
            if(rhs == null){
                return 1;
            }
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;
            } else if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
