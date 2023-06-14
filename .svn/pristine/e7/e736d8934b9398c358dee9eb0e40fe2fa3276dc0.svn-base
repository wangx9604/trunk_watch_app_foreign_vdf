/**
 * Creation Date:2015-1-15
 * 
 * Copyright 
 */
package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.UserData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-15
 * 
 */
public class UserRelationDAO extends BaseDAO {
    private static UserRelationDAO instance = null;
    
    public static UserRelationDAO getInstance(Context context)
    {
        if (instance == null)
            instance = new UserRelationDAO(context);
        return instance;
    }

    public UserRelationDAO(Context mContext) {
        super(mContext,MyDBOpenHelper.USERS_TABLE_NAME);
        // TODO Auto-generated constructor stub
    }
    private boolean checkFamilyExist(FamilyData family,List<FamilyData> familyList){
        if (familyList.size()>0)
        for (FamilyData cur:familyList){
            if (cur.getFamilyId().equals(family.getFamilyId()))
             return true;
        }
        return false;
    }
    
    public  ArrayList<FamilyData> readFamilys(String userId)
    {
        ArrayList<FamilyData> familys = new ArrayList<FamilyData>();

        Cursor myCursor = null; 
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return familys;
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_USER_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(userId));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
                            
            while (myCursor.moveToNext())//已经有了
            {
                   FamilyData family = new FamilyData();
                   family.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
                   //去重
                   if (false == checkFamilyExist(family, familys)){
                   familys.add(family);
                   }
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "UserRelationDAO  readBindWathcs() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        //这里设置成null影响doLogoutNoQuit中，家庭群组列表的清空。
//        if (familys.size()==0)
//            familys = null;
        return familys;
        
    }    
    public  ArrayList<WatchData> readBindWathcs(String userId)
    {
        ArrayList<WatchData> watchs = new ArrayList<WatchData>();

        Cursor myCursor = null; 
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return watchs;
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_USER_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(userId));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
                            
            while (myCursor.moveToNext())//已经有了
            {
                   WatchData watch = new WatchData();
                   watch.setWatchId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_ID))));
                   watch.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
                   watch.setNickname(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_NAME))));
                   //去重
                   if (false == checkWatchExist(watch, watchs)){
                   watchs.add(watch);
                   }
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "UserRelationDAO  readBindWathcs() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        if (watchs.size() == 0)
            watchs = null;
        return watchs;
        
    }
    private boolean checkMemberExist(MemberUserData watch,ArrayList<MemberUserData> memberList){
        if (memberList.size()>0)
        for (MemberUserData cur:memberList){
            if (cur.getEid().equals(watch.getEid()))
             return true;
        }
        return false;
    }    
    private boolean checkWatchExist(WatchData watch,List<WatchData> watchList){
        if (watchList.size()>0)
        for (WatchData cur:watchList){
            if (cur.getWatchId().equals(watch.getWatchId()))
             return true;
        }
        return false;
    }
    public  ArrayList<WatchData> readFamilyWatchs(String familyId) {
        ArrayList<WatchData> watchs = new ArrayList<WatchData>();

        Cursor myCursor = null; 
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return watchs;
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_FAMILY_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(familyId));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
                            
            while (myCursor.moveToNext())//已经有了
            {
                WatchData watch = new WatchData();
                watch.setWatchId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_ID))));
                watch.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
               //去重
                if (false == checkWatchExist(watch, watchs)){
                watch.setNickname(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_NAME))));
                watchs.add(watch);
                }
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "UserRelationDAO  readFamilyWatchs() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        if (watchs.size() == 0)
            watchs = null;
        return watchs;
        
    }

    public  ArrayList<MemberUserData> readFamilyUsers(String familyId)
    {
        ArrayList<MemberUserData> members = new ArrayList<MemberUserData>();

        Cursor myCursor = null; 
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return members;
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_FAMILY_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(familyId));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
                            
            while (myCursor.moveToNext())//已经有了
            {
                   MemberUserData  member  = new MemberUserData();
                   member.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
                   member.setEid(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_EID_ID))));
                   member.setNickname(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_NICK_NAME))));
                   member.getCustomData().setFromJsonStr(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_RELATION))));
                   member.setUid(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_USER_ID))));
                   member.setHeadPath(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEAD_PATH))));
                    try {
                        member.setCellNum(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_CELLPHONE))));
                        member.setXiaomiId(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_XIAOMID)));
                    }catch (Exception e){
                        LogUtil.e( "UserRelationDAO  readFamilyUsers() 1 Exp:" + e.getMessage());
                    }

                   //去重
                   if (false == checkMemberExist(member, members)){
                    members.add(member);
                   }
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "UserRelationDAO  readFamilyUsers() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        if (members.size() == 0)
            members = null;
        return members;
        
    }
    public void cleanAll()
    {
        
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return;     
        try {
            db.delete(getTableName(), null, null);              
        } catch (Exception e) {
            // TODO: handle exception
        }

        db.close();
    }
    public  long  addUserRelation(UserData user,String familyId,String watchID,String watchName,String relation)
    {
        Cursor myCursor = null;
        long id = -1;       
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;
        
        try
        {
            ContentValues rowData = new ContentValues(); 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_USER_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(user.getUid()));
            szBuff.append("'");       
            szBuff.append (" AND ");
            szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(watchID));
            szBuff.append("'");  
            myCursor = db.rawQuery(szBuff.toString(), null);
              
            rowData.clear();
            rowData.put(MyDBOpenHelper.FIELD_USER_ID, AESUtil.getInstance().encryptDataStr(user.getUid()));
            rowData.put(MyDBOpenHelper.FIELD_EID_ID, AESUtil.getInstance().encryptDataStr(user.getEid()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(familyId));
            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(watchID));
            rowData.put(MyDBOpenHelper.FIELD_WATCH_NAME, AESUtil.getInstance().encryptDataStr(watchName));
            if (user.getHeadPath()!=null&&user.getHeadPath().length()>0)
                rowData.put(MyDBOpenHelper.FIELD_HEAD_PATH, AESUtil.getInstance().encryptDataStr(user.getHeadPath()));
            if (user.getNickname()!=null&&user.getNickname().length()>0)
             rowData.put(MyDBOpenHelper.FIELD_NICK_NAME, AESUtil.getInstance().encryptDataStr(user.getNickname()));
            if (relation!=null&&relation.length()>0)
             rowData.put(MyDBOpenHelper.FIELD_RELATION, AESUtil.getInstance().encryptDataStr(relation));
            if (user.getCellNum()!=null)
                rowData.put(MyDBOpenHelper.FIELD_CELLPHONE, AESUtil.getInstance().encryptDataStr(user.getCellNum()));
            if (user.getXiaomiId() != null && user.getXiaomiId().length() > 0)
                rowData.put(MyDBOpenHelper.FIELD_XIAOMID, user.getXiaomiId());
            if (myCursor.moveToNext())//已经有了
            {
                szBuff.delete(0, szBuff.length());
                szBuff.append(MyDBOpenHelper.FIELD_USER_ID);
                szBuff.append("='");
                szBuff.append(AESUtil.getInstance().encryptDataStr(user.getUid()));
                szBuff.append("'");
                szBuff.append(" AND ");
                szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
                szBuff.append("='");
                szBuff.append(AESUtil.getInstance().encryptDataStr(watchID));
                szBuff.append("'");                
                
                db.update(getTableName(), rowData, szBuff.toString(), null);                
                id = 0;
            }
            else
            {
            
                id= db.insertOrThrow(getTableName(), null, rowData); 
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "UserRelationDAO  addUserRelation() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        return id;
    }   
    public boolean cleanUsersByGid(String gid){        
        SQLiteDatabase db = this.openWritableDb();
        try {
            int rows = db.delete(getTableName(), "family_id=?", new String[]{
                    AESUtil.getInstance().encryptDataStr(gid)
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return true;    
    }

}
