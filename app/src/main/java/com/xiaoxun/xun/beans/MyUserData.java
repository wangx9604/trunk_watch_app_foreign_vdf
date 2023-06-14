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
public class MyUserData extends UserData {
    private WatchData focusWatch;
    private LocationData location;
    private ArrayList<WatchData> watchList;
    private ArrayList<FamilyData> familyList;
    private int  isValidFamilys;//是否有效家庭  //

    public int isValidFamilys() {
        return isValidFamilys;
    }

    public void setIsValidFamilys(int isValidFamilys) {
        this.isValidFamilys = isValidFamilys;
    }

    public WatchData getFocusWatch() {
        if(focusWatch == null
			&& watchList != null
			&& watchList.size() > 0){
            focusWatch = watchList.get(0);
        }
        return focusWatch;
    }
    public void setFocusWatch(WatchData focusWatch) {
        this.focusWatch = focusWatch;
    }
    public ArrayList<WatchData> getWatchList() {
        return watchList;
    }
    public void setWatchList(ArrayList<WatchData> watchList) {
        this.watchList = watchList;
    }
    public ArrayList<FamilyData> getFamilyList() {
        return familyList;
    }
    public void setFamilyList(ArrayList<FamilyData> familyList) {
        this.familyList = familyList;
    }
	public LocationData getLocation() {
		return location;
	}
	public void setLocation(LocationData location) {
		this.location = location;
	}
	
	
	
	public ArrayList<MemberUserData> getUserDataByFamily(String familyid){
		ArrayList<MemberUserData> userlist = null;
		for(FamilyData fa:familyList){
			if(fa.getFamilyId().equals(familyid)){
				userlist = fa.getMemberList();
			}
		}
		return userlist;
	}

    public String queryNicknameByEid(String eid){
        String nickname = null;
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if (watch.getEid().equals(eid)){
                    nickname = watch.getNickname();
                    break;
                }
            }
        }
        if (nickname == null){
            for(FamilyData fa:familyList){
                for(MemberUserData data:fa.getMemberList()){
                    if (data.getEid().equals(eid)){
                        nickname = data.getNickname();
                        break;
                    }
                }
                if (nickname!=null)
                    break;
            }
        }
        if(nickname == null){
            nickname = "";
        }
        return nickname;
    }
    public MemberUserData queryUserDataByEid(String eid){
        MemberUserData user = null;
        for(FamilyData fa:familyList){
            for(MemberUserData data:fa.getMemberList()){
                if (data.getEid().equals(eid)){
                    user = data;
                    break;
                }
            }
            if (user!=null)
                break;
        }
        return user;
    }
	public ArrayList<WatchData> getWatchListByFamily(String familyid){
		ArrayList<WatchData> watchlist = null;
		for(FamilyData fa:familyList){
			if(fa.getFamilyId().equals(familyid)){
				watchlist = fa.getWatchlist();
				break;
			}
		}
		return watchlist;
	}
	public String [] queryEidsByFimily(String familyId){
		ArrayList<String> teid = new ArrayList<String>();
		for(MemberUserData data:getUserDataByFamily(familyId)){
			if(!data.getEid().equals(getEid()))
			{
				teid.add(data.getEid());
			}
		}
		for(WatchData data:getWatchListByFamily(familyId)){
			teid.add(data.getEid());
		}
		String [] eid = new String[teid.size()];
		for(int i = 0; i < teid.size();i++){
			eid[i] = teid.get(i);
		}
		return eid;
	}
	public WatchData queryWatchDataByEid(String eid){
		WatchData watch = null;
        if (getWatchList()!=null&&getWatchList().size()>0){
            for(WatchData data:watchList){
                if(data.getEid() != null && data.getEid().equals(eid)){
                    watch = data;
                    break;
                }
            }          
        }

		return watch;
	}
	public FamilyData queryFamilyByGid(String gid){
		FamilyData family = null;
        if (getFamilyList()!=null&&getFamilyList().size()>0){		
		for(FamilyData data:familyList){
			if(data.getFamilyId().equals(gid)){
				family = data;
				break;
			}
		}
        }
		return family;
	}
   public boolean isWatchEidBinded(String eid){
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if (watch.getEid().equals(eid)){
                    return true;
                }
            }
        }
        return false;
    }
   //用户名匹配，实质取的手表的iccid
   public boolean isWatchSNBinded(String sn){
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if ((watch.getQrStr() != null && watch.getQrStr().equalsIgnoreCase(sn))){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWatchImsiBinded(String imsi){
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if (watch.getImsi()!=null&&watch.getImsi().equalsIgnoreCase(imsi)){
                    return true;
                }
            }
        }
        return false;
    }


    public boolean getIsWatchByEid(String getmSrcId) {
        boolean recode = false;
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if (watch.getEid().equals(getmSrcId)){
                    recode = true;
                    break;
                }
            }
        }
        return  recode;
    }
    public String getHeadPathByEid(String getmSrcId) {
        String path = null;
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()) {
                if (watch.getEid() != null && watch.getEid().equals(getmSrcId)) {
                    path = watch.getHeadPath();
                    break;
                }
            }
        }
        if (path == null){
            for(FamilyData fa:familyList){
               for(MemberUserData data:fa.getMemberList()){
                   if (data.getEid().equals(getmSrcId)){
                       path = data.getHeadPath();
                       break;
                    }
                   if (path!=null)
                       break;
               }
            }
        }
        return path;
    }
    private  void  copyWatchInfo(WatchData dst,WatchData src){
        dst.setNickname(src.getNickname());
        dst.setSex(src.getSex());
        dst.setBirthday(src.getBirthday());
        dst.setHeight(src.getHeight());
        dst.setWeight(src.getWeight());
        dst.setHeadPath(src.getHeadPath());
    }
    private  void  copyUserInfo(MemberUserData dst,MemberUserData src){
        dst.setNickname(src.getNickname());
        dst.setHeadPath(src.getHeadPath());
    }
    public void updateUserInfo(MemberUserData SRCuser){


        for(FamilyData fa:familyList){
           for(MemberUserData data:fa.getMemberList()){
               if (data.getEid().equals(SRCuser.getEid())){
                   copyUserInfo(data, SRCuser);
                   break;
                }
           }
        }
  
    }
    public void updateDeviceInfo(WatchData SRCwatch) {
        if (getWatchList()!=null&&getWatchList().size()>0){
            for (WatchData watch:getWatchList()){
                if (watch.getEid().equals(SRCwatch.getEid())){
                    copyWatchInfo(watch, SRCwatch);
                   break;
                }
            }
        }
        if (familyList!=null&&familyList.size()>0){
            for(FamilyData fa:familyList){
               for(WatchData data:fa.getWatchlist()){
                   if (data.getEid().equals(SRCwatch.getEid())){
                       copyWatchInfo(data, SRCwatch);
                       break;
                    }
               }
            }
        }
  
    }
    public boolean isMeAdminByWatch(WatchData watch){
        String adminEid = getAdminEidByWatch(watch);
        return adminEid != null && adminEid.equals(getEid());
    }
    public String getAdminEidByWatch(WatchData watch) {
         
        for(FamilyData fa:familyList){
           if (fa.getFamilyId().equals(watch.getFamilyId())){
               return fa.getAdminEId();
           }
         }
        return null;
    }
}
