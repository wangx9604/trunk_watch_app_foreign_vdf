/**
 * Creation Date:2015-1-15
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import java.util.ArrayList;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-15
 * 
 */
public class FamilyData {
    public ArrayList<WatchData> watchlist = new ArrayList<WatchData>();
    public ArrayList<MemberUserData> memberList = new ArrayList<MemberUserData>();
    private String familyName; //根据手表生成
    private String description; //根据成员生成
    private String familyId;//gid 
    private String adminEId;//管理员id
    private String nextContentKey; //获取下一次family消息内容的key
    private String nextFamilyChangeNotifyKey; //获取下一次家庭成员变化通知的key
	private String nextWarningKey; //获取下一次warning的key
    private boolean isSel = false;//用作family列表选择用，每次加载时候需要重置一次
    private boolean isLongPressed = false;//用作family列表选择用，每次加载时候需要重置一次
    
    public ArrayList<WatchData> getWatchlist() {
        return watchlist;
    }
    public void setWatchlist(ArrayList<WatchData> watchlist) {
        this.watchlist = watchlist;
    }
    public ArrayList<MemberUserData> getMemberList() {
        return memberList;
    }
    public void setUserList(ArrayList<MemberUserData> userList) {
        this.memberList = userList;
    }
    public String getFamilyName() {
        return familyName;
    }
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFamilyId() {
        return familyId;
    }
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
    public boolean isSel() {
        return isSel;
    }
    public void setSel(boolean isSel) {
        this.isSel = isSel;
    }
    public boolean isLongPressed() {
        return isLongPressed;
    }
    public void setLongPressed(boolean isLongPressed) {
        this.isLongPressed = isLongPressed;
    }
	public String getNextContentKey() {
		return nextContentKey;
	}
	public void setNextContentKey(String nextContentKey) {
		this.nextContentKey = nextContentKey;
	}
    public String getNextFamilyChangeNotifyKey() {
		return nextFamilyChangeNotifyKey;
	}
	public void setNextFamilyChangeNotifyKey(String nextFamilyChangeNotifyKey) {
		this.nextFamilyChangeNotifyKey = nextFamilyChangeNotifyKey;
	}
	public String getNextWarningKey() {
		return nextWarningKey;
	}
	public void setNextWarningKey(String nextWarningKey) {
		this.nextWarningKey = nextWarningKey;
	}
    public int getAllMemberSize(){
        int size =0 ;
        if (memberList!=null)
            size+=memberList.size();
        
        if (watchlist!=null)
            size+=watchlist.size();
        
        return size;
    }
    
    public String[] getTeidsExceptSelf(String myEid){
        String [] Teids = null;
        int size = getAllMemberSize();
        Teids = new String[size-1];
        int ct2 =0;

        if (getMemberList().size()>0){
            
            for(MemberUserData member:getMemberList()){
                if (!member.getEid().equals(myEid)){
                    Teids[ct2] = member.getEid();
                    ct2++;
                    }
            }
        }
        
        if (getWatchlist().size()>0){
            for(WatchData watch:getWatchlist()){ 
                Teids[ct2] = watch.getEid();
                ct2++; 
            }  
        }
        return Teids;
    }
    public String getAdminEId() {
        return adminEId;
    }
    public void setAdminEId(String adminEId) {
        this.adminEId = adminEId;
    }
}
