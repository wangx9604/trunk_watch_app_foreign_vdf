package com.xiaoxun.xun.gallary.downloadUtils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.gallary.ImageVedioFiles;
import com.xiaoxun.xun.gallary.dataStruct.GalleryData;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import java.io.File;

/**
 * Created by xilvkang on 2017/5/3.
 */

public class DownloaderRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            //decode download file
            DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor c = manager.query(query);
            if(c.moveToFirst()){
                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                File fp = new File(filename);
                if(fp.exists()){
                    ImibabyApp mApp = (ImibabyApp)context.getApplicationContext();
                    Bitmap ret = ToolUtils.decryptImgFile(fp,mApp.getNetService().getAESKEY());
                    if(ret == null){
                        LogUtil.e("decode file failed!");
                        fp.delete();
                        return;
                    }
                    ToolUtils.saveDecryptimgFile(fp,ret);
                }else{
                    LogUtil.e("download file not exist!");
                    return;
                }
            }
            if(ImageVedioFiles.ImagevideoFiles.size()>0){
                for(GalleryData item : ImageVedioFiles.ImagevideoFiles){
                    if(item.getReference() == id && item.getDownloadListener() != null){
                        item.getDownloadListener().onFinished(String.valueOf(id));
                        break;
                    }
                    if(item.getSrcReference() == id && item.getDownloadListener() != null){
                        item.getDownloadListener().onFinished(String.valueOf(id));
                        break;
                    }
                }
            }
        }
    }
}
