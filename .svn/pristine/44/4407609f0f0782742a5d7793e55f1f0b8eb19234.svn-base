package com.xiaoxun.xun.gallary.downloadUtils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.xiaoxun.xun.gallary.ImageVedioFiles;

import java.io.File;
import java.util.HashMap;


/**
 * Created by xilvkang on 2017/4/28.
 */

public class GalleryDownloader {
    public static int DOWLOADER_WORK_TYPE_MOBILE = 1;
    public static int DOWLOADER_WORK_TYPE_WIFI = 2;

    private Context context;
    private DownloadManager manager;
    private DownloadManager.Request request;

    /**
     * 参数：1.context 上下文
     */
    public GalleryDownloader(Context context){
        this.context = context;
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

    }
    /**
     * 参数：1.url 下载链接字符串
     *      2.downloadWorkType 下载环境限制 1 移动网络 2 wifi
     *      3.name 保存的文件名
     */
    public long requstDownload(Context ctxt, String url, int downloadWorkType, String eid, String name, int type){
        request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(downloadWorkType);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        File saveFile = null;
        //String path = "";
        if(type == ImageVedioFiles.FILE_TYPE_SRC) {
            saveFile = new File(ctxt.getExternalFilesDir(null), ImageVedioFiles.SRC_PATH + eid + "/");
            //path = ImageVedioFiles.SRC_PATH + eid + "/";
        }else if(type == ImageVedioFiles.FILE_TYPE_PRE){
            saveFile = new File(ctxt.getExternalFilesDir(null), ImageVedioFiles.PREVIEW_PATH + eid + "/");
            //path = ImageVedioFiles.PREVIEW_PATH + eid + "/";
        }
        request.setDestinationUri(Uri.fromFile(saveFile));
        //request.setDestinationInExternalFilesDir(ctxt,null,path + name);
        return manager.enqueue(request);
    }

    /**
     *
     * @param downloadId 下载任务ID
     * @return HashMap key: exsit 是否存在任务
     *                      status 任务状态    DownloadManager.STATUS_SUCCESSFUL: 8  下载成功
     *                                       DownloadManager.STATUS_FAILED:  16     下载失败
     *                                       DownloadManager.STATUS_PENDING: 1     等待下载
     *                                       DownloadManager.STATUS_RUNNING: 2      正在下载
     *                                       DownloadManager.STATUS_PAUSED: 4       下载暂停
     *                      progress 任务进度
     */
    public HashMap<String,Integer> queryDownloaderMission(long downloadId){
        HashMap<String,Integer> map = new HashMap<>();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = manager.query(query);
        if (!cursor.moveToFirst()) {
            cursor.close();
            map.put("exsit",0);
            return map;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        map.put("status",status);
        long downloadedSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        long progress = downloadedSoFar/totalSize;
        map.put("progress",(int)progress);
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        return map;
    }

}
