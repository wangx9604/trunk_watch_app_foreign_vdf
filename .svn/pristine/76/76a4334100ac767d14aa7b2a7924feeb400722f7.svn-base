package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

/**
 * @author  Liutianxiang
 * @date 2013-7-25 下午3:20:01
 * @version V1.0
 * @Description: TODO(添加描述)
 */
public class WatchDAO extends BaseDAO {
	private static WatchDAO instance = null;
	
	public static WatchDAO getInstance(Context context)
	{
		if (instance == null)
			instance = new WatchDAO(context);
		return instance;
	}

	public WatchDAO(Context mContext) {
		super(mContext,MyDBOpenHelper.WATCH_TABLE_NAME);
		// TODO Auto-generated constructor stub
	}
    public void readWatchDetail(WatchData watch){
        if (watch!=null){
        WatchData  dwatch = readWatch(watch);
        watch.setNickname(dwatch.getNickname());
        watch.setFamilyId(dwatch.getFamilyId());
        watch.setBirthday(dwatch.getBirthday());
        
        watch.setSex(dwatch.getSex());
        watch.setVerCur(dwatch.getVerCur());
        watch.setVerOrg(dwatch.getVerOrg());
        watch.setExpireTime(dwatch.getExpireTime());
        watch.setHeight(dwatch.getHeight());
        watch.setWeight(dwatch.getWeight());
        
        watch.getCurLocation().setLatitude(dwatch.getCurLocation().getLatitude());
        watch.getCurLocation().setLongitude(dwatch.getCurLocation().getLongitude());  
        
        watch.setFamilyId(dwatch.getFamilyId());
      
        //其他必要的数据
        }
    }
    public  WatchData readWatch(WatchData watch)
    {
        Cursor myCursor = null; 
        SQLiteDatabase db = this.openWritableDb();
        if (db == null || watch == null)
            return watch;
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
              
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(watch.getWatchId()));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
                            
            if (myCursor.moveToNext())//已经有了
            {
//                watch = new WatchData();
//                watch.setWatchId(watchId);
                String name = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_NICK_NAME)));
                if (name!=null&&name.length()>0)
                {
                    watch.setNickname(name);
                }
                watch.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
                watch.setHeadId(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEAD_ID)));

                String path = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEAD_PATH)));
                if (path!=null&&path.length()>0)
                {
                    watch.setHeadPath(path);
                }
                String eid = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_EID_ID)));
                watch.setEid(eid);
                
                String birthday = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BIRTHDAY)));
                if (birthday!=null&&birthday.length()>0)
                {
                    watch.setBirthday(birthday);
                }

                String sex = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SEX));
                if (sex!=null&&sex.length()>0)
                {
                    if (sex.endsWith("1"))
                     watch.setSex(1);
                    else
                     watch.setSex(0);
                }

                String curVer = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_CUR_VER)));
                if (curVer!=null&&curVer.length()>0)
                {
                    watch.setVerCur(curVer);
                }
                
                String btmac = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BT_MAC)));
                if (btmac!=null&&btmac.length()>0)
                {
                    watch.setBtMac(btmac);
                }
                
                String orgVer = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_ORIGNAL_VER)));
                if (orgVer!=null&&orgVer.length()>0)
                {
                    watch.setVerOrg(orgVer);
                }
                String height = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEIGHT));
                if (height!=null&&height.length()>0)
                {
                    watch.setHeight(Double.valueOf(height));
                }
                String weight = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WEIGHT));
                if (weight!=null&&weight.length()>0)
                {
                    watch.setWeight(Double.valueOf(weight));
                }
                String longit = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LONGITUDE)));
                if (longit!=null&&longit.length()>0)
                {
                    watch.getCurLocation().setLongitude(Double.valueOf(longit));
                }
                String latit = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LATITUEDE)));
                if (latit!=null&&latit.length()>0)
                {
                    watch.getCurLocation().setLatitude(Double.valueOf(latit));
                }  
//                watch.getCurLocation().setLatLng(new LatLng(Double.valueOf(latit), Double.valueOf(longit)));
                String exiptime_tmp = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_EXIPIRE_TIME));
                if(exiptime_tmp != null) {
                    if (exiptime_tmp.substring(0, 2).equals("##")) {
                        watch.setBrandType(exiptime_tmp.substring(2));
                    } else if (exiptime_tmp.contains("##")) {
                        String[] arrayTmp = exiptime_tmp.split("##");
                        if(arrayTmp.length >= 2) {
                            watch.setExpireTime(arrayTmp[0]);
                            watch.setBrandType(arrayTmp[1]);
                        }else{
                            watch.setExpireTime(arrayTmp[0]);
                        }
                    } else if(exiptime_tmp.length() > 0){
                        watch.setExpireTime(exiptime_tmp);
                    }
                }

                try {
                String iccid = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_ICCID)));
                watch.setIccid(iccid);

                String simNo = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_NO)));
                watch.setCellNum(simNo);

                String imei = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_IMEI)));
                watch.setImei(imei);
                //新增deviceType
                String deviceType = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DEVICE_TYPE)));
                watch.setDeviceType(deviceType);
                    //新增sn
                    String sn = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_SN)));
                    watch.setMachSn(sn);

                String qrstr = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_QR));
                watch.setQrStr(qrstr);
                }catch (Exception e){
                    LogUtil.e( "WatchyDAO  readWatch() 1 Exp:" + e.getMessage());
                }
                watch.setSimActiveStatus(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_ACTIVIE_STATE)));
                watch.setSimCertiStatus(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_CERTI_STATE)));
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "WatchyDAO  readWatch() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);

        return watch;
        
    }

    public WatchData readWatchByEid(String watchEid, WatchData watch) {
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return watch;
        if (watch == null)
            watch = new WatchData();

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.encryptDataStr(watchEid));
            szBuff.append("'");

            myCursor = db.rawQuery(szBuff.toString(), null);

            LogUtil.e("HYY " + "myCursor.moveToNext()=="+myCursor.moveToNext());
            if (myCursor.moveToNext())//已经有了
            {
//                watch = new WatchData();
//                watch.setWatchId(watchId);
                String name = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_NICK_NAME)));
                if (name != null && name.length() > 0) {
                    watch.setNickname(name);
                }
                watch.setFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FAMILY_ID))));
                watch.setHeadId(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEAD_ID)));

                String path = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEAD_PATH)));
                if (path != null && path.length() > 0) {
                    watch.setHeadPath(path);
                }
                String eid = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_EID_ID)));
                watch.setEid(eid);
                String watchId = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_ID)));
                watch.setWatchId(watchId);

                String birthday = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BIRTHDAY)));
                if (birthday != null && birthday.length() > 0) {
                    watch.setBirthday(birthday);
                }

                String sex = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SEX));
                if (sex != null && sex.length() > 0) {
                    if (sex.endsWith("1"))
                        watch.setSex(1);
                    else
                        watch.setSex(0);
                }

                String curVer = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_CUR_VER)));
                if (curVer != null && curVer.length() > 0) {
                    watch.setVerCur(curVer);
                }

                String btmac = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BT_MAC)));
                if (btmac != null && btmac.length() > 0) {
                    watch.setBtMac(btmac);
                }

                String orgVer = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_ORIGNAL_VER)));
                if (orgVer != null && orgVer.length() > 0) {
                    watch.setVerOrg(orgVer);
                }
                String height = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_HEIGHT));
                if (height != null && height.length() > 0) {
                    watch.setHeight(Double.valueOf(height));
                }
                String weight = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WEIGHT));
                if (weight != null && weight.length() > 0) {
                    watch.setWeight(Double.valueOf(weight));
                }
                String longit = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LONGITUDE)));
                if (longit != null && longit.length() > 0) {
                    watch.getCurLocation().setLongitude(Double.valueOf(longit));
                }
                String latit = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LATITUEDE)));
                if (latit != null && latit.length() > 0) {
                    watch.getCurLocation().setLatitude(Double.valueOf(latit));
                }
//                watch.getCurLocation().setLatLng(new LatLng(Double.valueOf(latit), Double.valueOf(longit)));
                String exiptime_tmp = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_EXIPIRE_TIME));
                if(exiptime_tmp != null) {
                    if (exiptime_tmp.substring(0, 2).equals("##")) {
                        watch.setBrandType(exiptime_tmp.substring(2));
                    } else if (exiptime_tmp.contains("##")) {
                        String[] arrayTmp = exiptime_tmp.split("##");
                        watch.setExpireTime(arrayTmp[0]);
                        watch.setBrandType(arrayTmp[1]);
                    } else if(exiptime_tmp.length() > 0){
                        watch.setExpireTime(exiptime_tmp);
                    }
                }
                try {
                    String iccid = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_ICCID)));
                    watch.setIccid(iccid);

                    String simNo = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_NO)));
                    watch.setCellNum(simNo);

                    String imei = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_IMEI)));
                    watch.setImei(imei);
                    //新增deviceType
                    String deviceType = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DEVICE_TYPE)));
                    watch.setDeviceType(deviceType);
                    //新增sn
                    String sn = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_WATCH_SN)));
                    watch.setMachSn(sn);
                    String qrstr = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_QR));
                    watch.setQrStr(qrstr);
                } catch (Exception e) {
                    LogUtil.e("WatchyDAO  readWatch() 1 Exp:" + e.getMessage());
                }
                watch.setSimActiveStatus(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_ACTIVIE_STATE)));
                watch.setSimCertiStatus(myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_SIM_CERTI_STATE)));
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("WatchyDAO  readWatch() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);

        return watch;

    }

    public long  saveWatchDetail(WatchData watch){
        //添加或update
        return addWatch(watch);
//        Cursor myCursor = null;
//        long id = -1;       
//        SQLiteDatabase db = this.openWritableDb();
//        if (db == null)
//            return -1;
//        
//        try
//        {
//            ContentValues rowData = new ContentValues(); 
//            StringBuilder szBuff = new StringBuilder();
//              
//            szBuff.append("SELECT * FROM ");
//            szBuff.append(getTableName());
//            szBuff.append(" WHERE ");
//            szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
//            szBuff.append("='");
//            szBuff.append(watch.getWatchId());
//            szBuff.append("'");       
//
//            myCursor = db.rawQuery(szBuff.toString(), null);
//              
//            rowData.clear();
//            rowData.put(MyDBOpenHelper.FIELD_NICK_NAME,watch.getNickname());
//                
//            if (myCursor.moveToNext())//已经有了
//            {
//                szBuff.delete(0, szBuff.length());
//                szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
//                szBuff.append("='");
//                szBuff.append(watch.getWatchId());
//                szBuff.append("'");
//                
//                db.update(getTableName(), rowData, szBuff.toString(), null);                
//                id = 0;
//            }
//            else
//            {
//            
//              //  id= db.insertOrThrow(getTableName(), null, rowData); 
//            }
//            
//        }catch (Exception e) {
//            // TODO: handle exception
//            LogUtil.e( "Watch  saveWatchNickName() Exp:" + e.getMessage());
//        }
//        db.close();
//        closeCursor(myCursor);
//        return id;
    }
    //没有必要单独设置，直接更新watch整个数据
    private  long  saveWatchNickName(String nickname,String watchId)
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
            szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(watchId));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
              
            rowData.clear();
            rowData.put(MyDBOpenHelper.FIELD_NICK_NAME,AESUtil.getInstance().encryptDataStr(nickname));
                
            if (myCursor.moveToNext())//已经有了
            {
                szBuff.delete(0, szBuff.length());
                szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
                szBuff.append("='");
                szBuff.append(AESUtil.getInstance().encryptDataStr(watchId));
                szBuff.append("'");
                
                db.update(getTableName(), rowData, szBuff.toString(), null);                
                id = 0;
            }
            else
            {
            
              //  id= db.insertOrThrow(getTableName(), null, rowData); 
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "Watch  saveWatchNickName() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        return id;
    }
    

    /**
     *    新增WatchData 已经有则更新
     * @param   watch the watch need to add or update
     * @return  row ID 
     */
    public  long  addWatch(WatchData watch)
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
            szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(watch.getWatchId()));
            szBuff.append("'");       

            myCursor = db.rawQuery(szBuff.toString(), null);
              
            rowData.clear();
 
            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(watch.getWatchId()));
            rowData.put(MyDBOpenHelper.FIELD_EID_ID, AESUtil.getInstance().encryptDataStr(watch.getEid()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(watch.getFamilyId()));
            rowData.put(MyDBOpenHelper.FIELD_HEAD_ID, watch.getHeadId());

            rowData.put(MyDBOpenHelper.FIELD_SEX, Integer.valueOf(watch.getSex()).toString());
            
            if (watch.getBirthday()!=null)
                rowData.put(MyDBOpenHelper.FIELD_BIRTHDAY, AESUtil.getInstance().encryptDataStr(watch.getBirthday()));

            if (watch.getVerCur()!=null)
                rowData.put(MyDBOpenHelper.FIELD_CUR_VER, AESUtil.getInstance().encryptDataStr(watch.getVerCur()));

            if (watch.getBtMac()!=null)
                rowData.put(MyDBOpenHelper.FIELD_BT_MAC, AESUtil.getInstance().encryptDataStr(watch.getBtMac()));

            if (watch.getVerOrg()!=null)
                rowData.put(MyDBOpenHelper.FIELD_ORIGNAL_VER, AESUtil.getInstance().encryptDataStr(watch.getVerCur()));
            
            if (watch.getExpireTime()!=null)
                rowData.put(MyDBOpenHelper.FIELD_EXIPIRE_TIME, watch.getExpireTime());
            if (watch.getBrandType() != null){
                if(rowData.get(MyDBOpenHelper.FIELD_EXIPIRE_TIME) != null) {
                    rowData.put(MyDBOpenHelper.FIELD_EXIPIRE_TIME,
                            rowData.get(MyDBOpenHelper.FIELD_EXIPIRE_TIME) + "##" + watch.getBrandType());
                }else{
                    rowData.put(MyDBOpenHelper.FIELD_EXIPIRE_TIME, "##" + watch.getBrandType());
                }
            }
//            rowData.put(MyDBOpenHelper.FIELD_LOCATION_LONGITUDE, Double.valueOf(watch.getCurLocation().getLongitude()).toString()); 
//
//            rowData.put(MyDBOpenHelper.FIELD_LOCATION_LATITUEDE, Double.valueOf(watch.getCurLocation().getLatitude()).toString()); 

            rowData.put(MyDBOpenHelper.FIELD_HEIGHT, Double.valueOf(watch.getHeight()).toString()); 
            
            rowData.put(MyDBOpenHelper.FIELD_WEIGHT, Double.valueOf(watch.getWeight()).toString()); 

            
            if (watch.getNickname()!=null&&watch.getNickname().length()>0)
             rowData.put(MyDBOpenHelper.FIELD_NICK_NAME, AESUtil.getInstance().encryptDataStr(watch.getNickname()));
            
            if (watch.getHeadPath()!=null&&watch.getHeadPath().length()>0)
             rowData.put(MyDBOpenHelper.FIELD_HEAD_PATH, AESUtil.getInstance().encryptDataStr(watch.getHeadPath()));

            //新增iccid simno
            if (watch.getIccid()!=null&&watch.getIccid().length()>0)
                rowData.put(MyDBOpenHelper.FIELD_ICCID, AESUtil.getInstance().encryptDataStr(watch.getIccid()));
            if (watch.getCellNum()!=null&&watch.getCellNum().length()>0)
                rowData.put(MyDBOpenHelper.FIELD_SIM_NO, AESUtil.getInstance().encryptDataStr(watch.getCellNum()));
            if (watch.getImei()!=null&&watch.getImei().length()>0)
                rowData.put(MyDBOpenHelper.FIELD_IMEI, AESUtil.getInstance().encryptDataStr(watch.getImei()));
            if (watch.getDeviceType()!=null&&watch.getDeviceType().length()>0)
                rowData.put(MyDBOpenHelper.FIELD_DEVICE_TYPE, AESUtil.getInstance().encryptDataStr(watch.getDeviceType()));
            if (watch.getQrStr() != null && watch.getQrStr().length() > 0) {
                rowData.put(MyDBOpenHelper.FIELD_QR, watch.getQrStr());
            }
            if (!TextUtils.isEmpty(watch.getMachSn())) {
                rowData.put(MyDBOpenHelper.FIELD_WATCH_SN, AESUtil.getInstance().encryptDataStr(watch.getMachSn()));
            }
            rowData.put(MyDBOpenHelper.FIELD_SIM_CERTI_STATE, watch.getSimCertiStatus());
            rowData.put(MyDBOpenHelper.FIELD_SIM_ACTIVIE_STATE, watch.getSimActiveStatus());

            if (myCursor.moveToNext())//已经有了
            {
                szBuff.delete(0, szBuff.length());
                szBuff.append(MyDBOpenHelper.FIELD_WATCH_ID);
                szBuff.append("='");
                szBuff.append(AESUtil.getInstance().encryptDataStr(watch.getWatchId()));
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
            LogUtil.e( "Watch  AddWatch() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
        return id;
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

}
