package com.xiaoxun.xun.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.utils.LogUtil;

public class BaseDAO {
	protected Context mContext = null;
	private String  mTableName = null;

	public BaseDAO(Context mContext,String mTableName) {
		super();
		this.mContext = mContext;
		this.mTableName = mTableName;
	}
	
	public BaseDAO(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	protected void closeCursor(Cursor cursor)
	{
		try
		{
			cursor.close();
		}
		catch(Exception e)
		{}
	}	
	protected String getTableName()
	{
		return this.mTableName;
	}
	
	protected SQLiteDatabase openWritableDb()
	{
		LogUtil.i("BaseDAO::openWritableDb()");
		
		MyDBOpenHelper dbHelper = new MyDBOpenHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		if (db == null)
		{
			LogUtil.i("openWritableDb failure");
		}
		
		return db;
	}
	
	protected SQLiteDatabase openReadableDb()
	{
		LogUtil.i("BaseDAO::openReadableDb()");
		MyDBOpenHelper dbHelper = new MyDBOpenHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if (db == null)
		{
			LogUtil.i("openReadableDb failure");
		}
		return db;
	}
	
	/**
	 * 删除id对应的表项
	 * @param id
	 * @return 返回被删除的记录条数
	 */
	public int delete(long id)
	{
		SQLiteDatabase db;
		db = this.openWritableDb();
		try {
			if (db == null)
				return 0;
			
			int rows = db.delete(mTableName, "_id=?", new String[]{
					String.valueOf(id)
			});
			
			db.close();
			
			LogUtil.i("delete rows:"+rows);
			
			return rows;	
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 删除表中所有记录
	 * @return 返回被删除的记录条数
	 */
	public int deleteAll()
	{
		SQLiteDatabase db;
		db = this.openWritableDb();
		
		if (db == null)
			return 0;
		
		int affectRows = db.delete(mTableName, null, null);
		
		db.close();
		
		LogUtil.i("deleteAll rows:"+affectRows);
		
		return affectRows;
	}
}
