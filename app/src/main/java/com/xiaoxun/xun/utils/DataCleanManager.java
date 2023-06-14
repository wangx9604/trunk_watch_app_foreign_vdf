package com.xiaoxun.xun.utils;

/**
 * Created by guxiaolong on 2015/12/19.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.xiaoxun.xun.Const;

import java.io.File;

public class DataCleanManager {

    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    public static void cleanSharedPreference(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    public static void cleanFiles(Context context) {
        deleteAllFiles(context.getFilesDir());
    }

    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteAllFiles(context.getExternalFilesDir(Const.MY_BASE_DIR));
        }
    }

    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    public static void cleanApplicationData(Context context) {
        cleanInternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        cleanExternalCache(context);
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory() && directory.listFiles() != null) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    private static void deleteAllFiles(File root) {
        try {
            if (root != null && root.exists()) {
                if (root.isFile()) {
                    root.delete();
                    return;
                }
                if (root.isDirectory()) {
                    File[] files = root.listFiles();
                    if (files == null || files.length == 0) {
                        root.delete();
                        return;
                    }

                    for (File f : files) {
                        deleteAllFiles(f);
                    }
                    root.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}