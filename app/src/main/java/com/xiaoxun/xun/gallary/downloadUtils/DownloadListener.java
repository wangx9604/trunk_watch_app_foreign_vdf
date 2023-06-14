package com.xiaoxun.xun.gallary.downloadUtils;

/**
 * Created by xilvkang on 2017/3/24.
 */

public interface DownloadListener {
    void onStartDownload();
    void onFinished(String result);
    void onError(String cause);
}
