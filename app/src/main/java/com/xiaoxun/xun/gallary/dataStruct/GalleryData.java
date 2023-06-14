package com.xiaoxun.xun.gallary.dataStruct;

import com.squareup.picasso.Target;
import com.xiaoxun.xun.gallary.downloadUtils.DownloadListener;

/**
 * Created by xilvkang on 2017/3/8.
 */

public class GalleryData {
    String eid;                         //device sign
    int type;                           //image  video  Karaoke
    String name;
    long time;
    String preview_url;                 //Image url
    String src_url;
    String local_pre_path;              //preview have been downloaded to this path
    String local_src_path;              //src have been downloaded to this path
    long reference = -1L;               //downloadManager preview id
    long reference_src = -1L;           //downloadManager src id
    int status = -1;                    //download status -1 nothing 0 preview downloaded 1 preview and src downloaded
    DownloadListener lis;
    public Target target;
    String video_share_url;             //video url to share

    public String getEid(){
        return eid;
    }
    public void setEid(String e){
        eid = e;
    }

    public int getType(){
        return type;
    }
    public void setType(int t){type = t;}

    public String getName(){
        return name;
    }
    public void setName(String n){name = n;}

    public long getTime(){
        return time;
    }
    public void setTime(long t){time = t;}

    public String getPreview_url(){return preview_url;}
    public void setPreview_url(String s){preview_url = s;}

    public String getLocal_src_path(){
        return local_src_path;
    }
    public void setLocal_src_path(String s){local_src_path = s;}

    public String getLocal_pre_path(){
        return local_pre_path;
    }
    public void setLocal_pre_path(String s){local_pre_path = s;}

    public String getSrc_url(){return src_url;}
    public void setSrc_url(String s){src_url = s;}

    public long getReference(){
        return reference;
    }
    public void setReference(long id){
        reference = id;
    }

    public int getStatus(){
        return status;
    }
    public void setStatus(int s){
        status = s;
    }

    public void setDownloadListener(DownloadListener lis){
        this.lis = lis;
    }
    public DownloadListener getDownloadListener(){
        return lis;
    }

    public void setSrcReference(long id){
        reference_src = id;
    }
    public long getSrcReference(){
        return reference_src;
    }

    public void setVideo_share_url(String url){video_share_url = url;}
    public String getVideo_share_url(){return video_share_url;}
}
