package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.beans.BattaryPower;
import com.xiaoxun.xun.beans.FamilyChangeInfo;
import com.xiaoxun.xun.beans.SosWarning;
import com.xiaoxun.xun.beans.WarningInfo;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;

public class WarningInfoDao extends BaseDAO {

	public static String WARNING_MSG_TABLE_NAME = "warning_msg";
    public static final String FIELD_WARNING_TYPE = "warning_type";          //消息类型       
    public static final String FIELD_WARNING_TIMESTAMP = "warning_timestamp";          //消息时间
	public static final String FIELD_WARNING_CONTENT = "warning_content";      //消息体

    private static WarningInfoDao instance = null;
    
	public WarningInfoDao(Context mContext) {
		super(mContext);
		// TODO Auto-generated constructor stub
	}
    public static WarningInfoDao getInstance(Context context)
    {
        if (instance == null)
            instance = new WarningInfoDao(context);
        return instance;
    }
    
    public void creatTable(ArrayList<String> newfamilyid){
        SQLiteDatabase db = this.openWritableDb();
        if(newfamilyid != null){
	        for(String familyid:newfamilyid){
	        	StringBuilder table = new StringBuilder(WARNING_MSG_TABLE_NAME);
	        	table.append(familyid);
	            db.execSQL("CREATE TABLE IF NOT EXISTS "+table.toString()+" ("+
	            		"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
	            		FIELD_WARNING_TYPE+" TEXT," +
	            		FIELD_WARNING_TIMESTAMP+" TEXT," +			
	            		FIELD_WARNING_CONTENT+" TEXT" +     
	                    ");");
	        }
        }
        db.close();
    }
    public void deleteTable(ArrayList<String> oldfamilyid){
        SQLiteDatabase db = this.openWritableDb();
        if(oldfamilyid != null){
	        for(String familyid:oldfamilyid){
	        	StringBuilder table = new StringBuilder(WARNING_MSG_TABLE_NAME);
	        	table.append(familyid);
	            db.execSQL("DROP TABLE "+table.toString());
	        }
        }
        db.close();
    }
    /**
     *    新增WarningMsg
     * @param   warning the chat content need to add
     * @return  row ID 
     */
    public  long  addWarningMsg(String familyid,WarningInfo warning)
    {
//        Cursor myCursor = null;
        long id = -1;       
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;
        
        try
        {
            ContentValues rowData = new ContentValues(); 
            rowData.clear();
                   
            rowData.put(FIELD_WARNING_TYPE, AESUtil.getInstance().encryptDataStr(String.valueOf(warning.getmWarningType())));
            rowData.put(FIELD_WARNING_TIMESTAMP, warning.getmTimestamp());
            switch (warning.getmWarningType()) {
			case 1:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmPower().toString()));
				break;
			case 2:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmSos().toString()));
				break;
			case 3:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmFamilyChange().toString()));
				break;
			default:
				break;
			}

            
            id= db.insertOrThrow(WARNING_MSG_TABLE_NAME+familyid, null, rowData); 
            if(-1 == id)
            {
            	LogUtil.e("Add Warning error ! addWarningMsg()");
            }
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "Add Warning addWarningMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
        return id;
    } 
    /**
     *    删除WarningMsg
     * @param   
     * @return  
     */
    public  long  delWarningMsg(String familyid, WarningInfo warning)
    {
//        Cursor myCursor = null;
        long id = -1;       
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;
        
        try
        {  
            StringBuilder szBuff = new StringBuilder();
            //DELETE FROM mytable WHERE Name='Test1';
            szBuff.append("DELETE FROM ");
            szBuff.append(WARNING_MSG_TABLE_NAME+familyid);
            szBuff.append(" WHERE ");
            szBuff.append(FIELD_WARNING_TIMESTAMP);
            szBuff.append("='");
            szBuff.append(warning.getmTimestamp());
            szBuff.append("'");
            db.execSQL(szBuff.toString());      
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "Warning  delWarningMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
        return id;
    } 
    /**
     *    更新WarningMsg 状态
     * @param   
     * @return 
     */
    public  void  updateWarningMsg(String familyid, WarningInfo warning)
    {
//        Cursor myCursor = null;
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
        	LogUtil.e( "open db error! updateWarningMsg()");
        
        try
        {
            ContentValues rowData = new ContentValues(); 
              
            rowData.clear();
 
            StringBuilder szBuff = new StringBuilder();
            
            szBuff.append(FIELD_WARNING_TIMESTAMP);
            szBuff.append("='");
            szBuff.append(warning.getmTimestamp());
            szBuff.append("'");       
            
            rowData.put(FIELD_WARNING_TYPE, AESUtil.getInstance().encryptDataStr(String.valueOf(warning.getmWarningType())));
            rowData.put(FIELD_WARNING_TIMESTAMP, warning.getmTimestamp()); 
            switch (warning.getmWarningType()) {
			case 1:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmPower().toString()));
				break;
			case 2:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmSos().toString()));
				break;
			case 3:
	            rowData.put(FIELD_WARNING_CONTENT, AESUtil.getInstance().encryptDataStr(warning.getmFamilyChange().toString()));
				break;
			default:
				break;
			}
           
            id = db.update(WARNING_MSG_TABLE_NAME+familyid, rowData, szBuff.toString(), null);    
            if(-1 == id)
            {
                LogUtil.e( "chat update error! updateWarningMsg()");
            }
            
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "chat  updateWarningMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
    } 
    /**
     *    根据familyid读取所有WarningMsg
     * @param   
     * @return   
     */
    public void readAllWarningFromFamily(String familyid, ArrayList<WarningInfo> warninglist){
        Cursor myCursor = null; 
        if (warninglist == null || familyid == null){
            LogUtil.e( "warninglist is null ! readAllChatMsg()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e( "open db error! readAllChatMsg()");
//		FIELD_WARNING_TYPE
//		FIELD_WARNING_TIMESTAMP		
//		FIELD_WARNING_CONTENT   
        try
        {
        	StringBuilder szBuff = new StringBuilder();
            
            szBuff.append("SELECT * FROM ");
            szBuff.append(WARNING_MSG_TABLE_NAME+familyid);
            szBuff.append(" ORDER BY "+FIELD_WARNING_TIMESTAMP+" DESC"); //按时间升序查询
            
            myCursor = db.rawQuery(szBuff.toString(), null);
            if(myCursor != null && myCursor.moveToFirst())
            {
            	do{
                    WarningInfo warning = new WarningInfo();
                    warning.setmWarningType(Integer.valueOf(AESUtil.getInstance().decryptDataStr(myCursor.getString(1))));
                    warning.setmTimestamp(myCursor.getString(2));
                    String content = AESUtil.getInstance().decryptDataStr(myCursor.getString(3));
                    switch (warning.getmWarningType()) {
        			case 1:
        				BattaryPower power = new BattaryPower();
        				power.parse(content);
        				warning.setmPower(power);	           
        				break;
        			case 2:
        				SosWarning sos = new SosWarning();
        				sos.parse(content);
        				warning.setmSos(sos);
        				break;
        			case 3:
        				FamilyChangeInfo change = new FamilyChangeInfo();
        				change.parse(content);
        				warning.setmFamilyChange(change);
        				break;
        			default:
        				break;
					}
            		warninglist.add(warning);
            	}while(myCursor.moveToNext());
            }
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "chat read readAllWarningMsg() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
    } 
}
