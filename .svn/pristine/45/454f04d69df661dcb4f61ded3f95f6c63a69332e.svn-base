package com.xiaoxun.xun.gallary;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.gallary.dataBase.DataBaseHelper;
import com.xiaoxun.xun.gallary.dataStruct.GalleryData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by xilvkang on 2017/3/15.
 */

public class ImageVedioFiles {

    public static String ROOTPATH = Const.MY_BASE_DIR;
    public static String PREVIEW_PATH = ROOTPATH + "/Preview/";
    public static String SRC_PATH = ROOTPATH + "/Src/";
    public static String LOCALROOTPATH = Const.MY_BASE_DIR + "/";

    public static int FILE_TYPE_IMAGE = 0;
    public static int FILE_TYPE_VEDIO = 1;
    public static int FILE_TYPE_PRE = 2;
    public static int FILE_TYPE_SRC = 3;

    public static int NO_MORE_FILES_ON_CLOUD = 0;//0 has FILES 2 NO MORE FILES

    public static int GALLERY_STATUS = 0;//0 normal 1 delete choose
    public static int GALLERY_REFRESH_STATUS = 0;//0 no refresh 1 refreshing

    public static ArrayList<GalleryData> ImagevideoFiles = new ArrayList<GalleryData>();

    public static ArrayList<GalleryData> ImagevideoFiles_local = new ArrayList<>();

    public static void initFiles(Context ctxt) {
        File fp = ctxt.getExternalFilesDir(ROOTPATH);
        if (!fp.exists()) {
            fp.mkdirs();
        }
        initPreviewDir(ctxt);
        initSrcDir(ctxt);
    }

    public static void initPreviewDir(Context ctxt) {
        File fp_img = ctxt.getExternalFilesDir(PREVIEW_PATH);
        if (!fp_img.exists()) {
            fp_img.mkdirs();
        }
    }

    public static void initSrcDir(Context ctxt) {
        File fp_img = ctxt.getExternalFilesDir(SRC_PATH);
        if (!fp_img.exists()) {
            fp_img.mkdirs();
        }
    }

    public static void initEidDir(Context ctxt, String eid) {
        File fp = ctxt.getExternalFilesDir(SRC_PATH + eid + "/");
        if (!fp.exists()) {
            fp.mkdirs();
        }
        File fp_pre = ctxt.getExternalFilesDir(PREVIEW_PATH + eid + "/");
        if (!fp_pre.exists()) {
            fp.mkdirs();
        }
    }

    public static void clearFiles() {
        ImagevideoFiles.clear();
    }

    public static String getTypeName(String s) {
        String s1 = s.substring(s.indexOf(".") + 1);
        if (s1.indexOf(".") >= 0) {
            s = s1;
            s = getTypeName(s);
        }
        return s.substring(s.indexOf("."));
    }

    /**
     * get the Thumb of the Vedio
     *
     * @param filePath Vedio files path
     * @return Thumb
     */

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public static String saveBitmapToLocal(Bitmap bitmap, String path, String name) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File fp = new File(dir, name);
        try {
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(fp,false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fp.getAbsolutePath();
    }

    /**
     * delete files and Database
     *
     * @param context
     */
    public static void clearGalleryDataAndFiles(Context context) {
        File fp = new File(context.getExternalFilesDir(null), SRC_PATH);
        if (fp.exists()) {
            deleteDirWihtFile(fp);
        }
        DataBaseHelper db = new DataBaseHelper(context);
        db.deleteTableData();
        db.close();
    }

    /**
     * delete the device's Files by its eid
     *
     * @param context
     * @param eid     the device's eid
     */
    public static void clearGalleryDataAndFilesByEID(Context context, String eid) {
        File fp = new File(context.getExternalFilesDir(null), SRC_PATH + eid);
        if (fp.exists()) {
            deleteDirWihtFile(fp);
        }
        DataBaseHelper db = new DataBaseHelper(context);
        db.deleteOneData(eid);
        db.close();
    }

    /**
     * 获取文件夹大小
     *
     * @param ctxt File实例
     * @return long
     */
    public static long getFolderSize(Context ctxt) {
        File file = new File(ctxt.getExternalFilesDir(null), SRC_PATH);
        long size = 0;
        size = getDirSize(file);
        return size;
    }
    public static long getDirSize(File dir){
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return 0L;
        long ret = 0L;
        for (File file : dir.listFiles()) {
            if (file.isFile()){
                ret += file.length();
            }
            else if (file.isDirectory())
                ret += getDirSize(file); // 递规的方式删除文件夹
        }
        return ret;
    }
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static void switchDevClearData() {
        if (ImagevideoFiles != null && ImagevideoFiles.size() > 0) {
            ImagevideoFiles.clear();
        }
        if (ImagevideoFiles_local != null && ImagevideoFiles_local.size() > 0) {
            ImagevideoFiles_local.clear();
        }
    }
}
