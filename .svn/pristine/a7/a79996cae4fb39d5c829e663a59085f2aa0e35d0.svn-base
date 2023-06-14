package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;

public class HistoryTraceDAO extends BaseDAO {
	private static HistoryTraceDAO instance = null;
	
	public HistoryTraceDAO(Context mContext) {
		super(mContext,MyDBOpenHelper.TRACE_TABLE_NAME);
		// TODO Auto-generated constructor stub
	}
	
	public static HistoryTraceDAO getInstance(Context context)
	{
		if (instance == null)
			instance = new HistoryTraceDAO(context);
		return instance;
	}
	
	public String getTrace(String eid , String date)
    {
        Cursor myCursor = null; 
        String record = "" ;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return "";
        
        try
        { 
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
            szBuff.append("'");
            szBuff.append(" and ");
            szBuff.append(MyDBOpenHelper.FIELD_DATE);
            szBuff.append("='");
            szBuff.append(date);
            szBuff.append("'");
            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor.moveToNext()){
                record = AESUtil.getInstance().decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_RECORD)));
            }
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "get trace error" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);

        return record;
        
    }
	
	//添加
	public void addTrace(String eid, String date , String record )
    {
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        Cursor myCursor = null; 
        if (db == null)
        	LogUtil.e( "open db error! updateChatMsg()");
        try
        {
            ContentValues rowData = new ContentValues(); 
            rowData.clear();
            rowData.put(MyDBOpenHelper.FIELD_EID_ID, AESUtil.getInstance().encryptDataStr(eid));
            rowData.put(MyDBOpenHelper.FIELD_DATE, date);            
            rowData.put(MyDBOpenHelper.FIELD_RECORD, AESUtil.getInstance().encryptDataStr(record));
            
            
            StringBuilder qure = new StringBuilder();
            qure.append("SELECT * FROM ");
            qure.append(getTableName());
            qure.append(" WHERE ");
            qure.append(MyDBOpenHelper.FIELD_EID_ID);
            qure.append("='");
            qure.append(AESUtil.getInstance().encryptDataStr(eid));
            qure.append("'");
            qure.append(" and ");
            qure.append(MyDBOpenHelper.FIELD_DATE);
            qure.append("='");
            qure.append(date);
            qure.append("'");

            myCursor = db.rawQuery(qure.toString(), null);
            if(myCursor.moveToNext()){
                StringBuilder szBuff = new StringBuilder();
                szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
                szBuff.append("='");
                szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
                szBuff.append("'");
                szBuff.append(" and ");
                szBuff.append(MyDBOpenHelper.FIELD_DATE);
                szBuff.append("='");
                szBuff.append(date);
                szBuff.append("'");
                
                id = db.update(MyDBOpenHelper.TRACE_TABLE_NAME, rowData, szBuff.toString(), null);    
                if(-1 == id)
                {
                    LogUtil.e( "chat update error! updateChatMsg()");
                }
            }else{
            	db.insertOrThrow(getTableName(), null, rowData); 
            }
            
            
            /*id = db.insertOrThrow(getTableName(), null, rowData); 
            if(-1 == id){
            	LogUtil.e("insert history trace  error ! ");
            }*/
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "chat  updateChatMsg() Exp:" + e.getMessage());
        }
        db.close();
    }


    public void deleteTrace(String eid , String date){
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return;

        try
        {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
            szBuff.append("'");
            szBuff.append(" and ");
            szBuff.append(MyDBOpenHelper.FIELD_DATE);
            szBuff.append("='");
            szBuff.append(date);
            szBuff.append("'");
            db.delete(getTableName(),szBuff.toString(),null);
        }catch (Exception e) {
            // TODO: handle exception
            LogUtil.e( "get trace error" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
    }

    public int getCount(String eid){
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return 0;
        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT * FROM ");
        szBuff.append(getTableName());
        szBuff.append(" WHERE ");
        szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
        szBuff.append("='");
        szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
        szBuff.append("'");
        myCursor = db.rawQuery(szBuff.toString(), null);
        if(myCursor == null){
            return 0;
        }
        int res = myCursor.getCount();
        db.close();
        closeCursor(myCursor);
        return res;
    }
    public void deleteFirstTrace(String eid){
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return;
        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT  ");
        szBuff.append(MyDBOpenHelper.FIELD_DATE);
        szBuff.append(" FROM ");
        szBuff.append(getTableName());
        szBuff.append(" WHERE ");
        szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
        szBuff.append("='");
        szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
        szBuff.append("'");
        myCursor = db.rawQuery(szBuff.toString(), null);
        ArrayList<String> timelist = new ArrayList<String>();
        if(myCursor.moveToFirst()){
            do{
                LogUtil.e("myCursor.getString = " + myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DATE)));
                timelist.add(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DATE)));
            }while (myCursor.moveToNext());
        }
        closeCursor(myCursor);
        LogUtil.e("delete date = " + timelist.get(0));
        szBuff.delete(0, szBuff.length());
        szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
        szBuff.append("='");
        szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
        szBuff.append("'");
        szBuff.append(" and ");
        szBuff.append(MyDBOpenHelper.FIELD_DATE);
        szBuff.append("='");
        szBuff.append(timelist.get(0));
        szBuff.append("'");
        db.delete(getTableName(),szBuff.toString(),null);
        timelist.clear();
        db.close();
        closeCursor(myCursor);
    }
	@Override
	protected void closeCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		super.closeCursor(cursor);
	}

	@Override
	protected String getTableName() {
		// TODO Auto-generated method stub
		return super.getTableName();
	}

	@Override
	protected SQLiteDatabase openWritableDb() {
		// TODO Auto-generated method stub
		return super.openWritableDb();
	}

	@Override
	protected SQLiteDatabase openReadableDb() {
		// TODO Auto-generated method stub
		return super.openReadableDb();
	}

	@Override
	public int delete(long id) {
		// TODO Auto-generated method stub
		return super.delete(id);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return super.deleteAll();
	}

}
