/**
 * Creation Date:2015-6-17
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-6-17 */
public class WatchUpDateInfo implements Serializable{
	private static final long serialVersionUID = 7326893352544500972L;
	
	public static final String WATCH_UPDATE_INFO = "WATCH_UPDATE_INFO";
	
	private String btMac;
    private File  downloadFile;
    private int newVerCode;
    private String curVer;
    private String newVerName;
    private String newReleaseNote;
    private String fullJsonStr;
    private String downLoadUrl;
	private String md5;
	public String getBtMac() {
		return btMac;
	}
	public void setBtMac(String btMac) {
	    if(null!=btMac) {
	        String [] datas  = btMac.split(":");
	        ArrayList<String> dd = new ArrayList<String>();
	        for(String data : datas) {
	            dd.add(data);
	        }
	        Collections.reverse(dd);
	        this.btMac = dd.toString()
	                        .replace(',', ':')
	                        .replaceAll("[\\[\\]\\s]", "");
	    }
	}
	public File getDownloadFile() {
		return downloadFile;
	}
	public void setDownloadFile(File downloadFile) {
		this.downloadFile = downloadFile;
	}
	public int getNewVerCode() {
		return newVerCode;
	}
	public void setNewVerCode(int newVerCode) {
		this.newVerCode = newVerCode;
	}
	public String getNewVerName() {
		return newVerName;
	}
	public void setNewVerName(String newVerName) {
		this.newVerName = newVerName;
	}
	public String getNewReleaseNote() {
		return newReleaseNote;
	}
	public void setNewReleaseNote(String newReleaseNote) {
		this.newReleaseNote = newReleaseNote;
	}
    public String getFullJsonStr() {
        return fullJsonStr;
    }
    public void setFullJsonStr(String fullJsonStr) {
        this.fullJsonStr = fullJsonStr;
    }
    public String getDownLoadUrl() {
        return downLoadUrl;
    }
    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }
    public String getCurVer() {
        return curVer;
    }
    public void setCurVer(String curVer) {
        this.curVer = curVer;
    }

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
    public String toString() {
        return "WatchUpDateInfo [btMac=" + btMac + ", downloadFile="
                + downloadFile + ", newVerCode=" + newVerCode + ", curVer="
                + curVer + ", newVerName=" + newVerName + ", newReleaseNote="
                + newReleaseNote + ", fullJsonStr=" + fullJsonStr
                + ", downLoadUrl=" + downLoadUrl + "]";
    }
    
}
