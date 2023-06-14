package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.dialBg.DialBgItem;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;

public class DialBgDAO extends BaseDAO {
    private static DialBgDAO instance = null;
    public DialBgDAO(Context mContext) {
        super(mContext,MyDBOpenHelper.DIALBG_TABLE_NAME);
    }

    public static DialBgDAO getInstance(Context context){
        if(instance==null){
            instance = new DialBgDAO(context);
        }
        return instance;
    }

    public ArrayList<DialBgItem> getDialBgList(String eid){
        ArrayList<DialBgItem> list = new ArrayList<>();
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return null;

        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT * FROM ");
        szBuff.append(getTableName());
        szBuff.append(" WHERE ");
        szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
        szBuff.append("='");
        szBuff.append(AESUtil.getInstance().encryptDataStr(eid));//AESUtil.getInstance().encryptDataStr(eid)
        szBuff.append("'");
        myCursor = db.rawQuery(szBuff.toString(), null);
        if (myCursor == null || myCursor.getCount() <= 0) {
            return null;
        }
        if (myCursor.moveToFirst()){
            do {
                DialBgItem item = new DialBgItem();
                String id = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_ID));
                item.setId(id);
                String name = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_NAME));
                item.setName(name);
                String path = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_PATH));
                item.setImg_path(path);
                String url = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_URL));
                item.setUrl(url);
                String time = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_TIME));
                item.setTime(time);
                int status = myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DIALBG_STATUS));
                item.setStatus(status);
                list.add(item);
            }while (myCursor.moveToNext());
        }
        closeCursor(myCursor);
        db.close();
        LogUtil.e("Dialbg sqllist size = " + list.size());
        return list;
    }

    public void updateDialBgItem(String eid,DialBgItem item){
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("db null");
            return;
        }
        String whereClause = MyDBOpenHelper.FIELD_EID_ID + "=? and " + MyDBOpenHelper.FIELD_DIALBG_ID + "=?";
        String[] whereArgs = { AESUtil.getInstance().encryptDataStr(eid), item.getId()};//AESUtil.getInstance().encryptDataStr(eid)
        ContentValues rowData = new ContentValues();
        rowData.put(MyDBOpenHelper.FIELD_EID_ID,eid);
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_ID,item.getId());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_NAME,item.getName());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_PATH,item.getImg_path());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_STATUS,item.getStatus());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_TIME,item.getTime());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_URL,item.getUrl());
        int ret = db.update(getTableName(),rowData,whereClause,whereArgs);
        LogUtil.e("insert result = " + ret);

        db.close();
    }

    public void deleteDialBgItem(String eid,DialBgItem item){
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("db null");
            return;
        }
        String whereClause = MyDBOpenHelper.FIELD_EID_ID + "=? and " + MyDBOpenHelper.FIELD_DIALBG_ID + "=?";
        String[] whereArgs = { AESUtil.getInstance().encryptDataStr(eid), item.getId()};//AESUtil.getInstance().encryptDataStr(eid)
        int ret = db.delete(getTableName(),whereClause,whereArgs);
        LogUtil.e("insert result = " + ret);

        db.close();
    }

    public void insertDialBgItem(String eid,DialBgItem item){
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("db null");
            return;
        }
        ContentValues rowData = new ContentValues();
        rowData.put(MyDBOpenHelper.FIELD_EID_ID,AESUtil.getInstance().encryptDataStr(eid));//AESUtil.getInstance().encryptDataStr(eid)
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_ID,item.getId());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_NAME,item.getName());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_PATH,item.getImg_path());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_STATUS,item.getStatus());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_TIME,item.getTime());
        rowData.put(MyDBOpenHelper.FIELD_DIALBG_URL,item.getUrl());
        long ret = db.insertOrThrow(getTableName(),null,rowData);
        LogUtil.e("insert result = " + ret);

        db.close();
    }

    public void deleteDialBgItemById(String eid,String id){
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("db null");
            return;
        }
        String whereClause = MyDBOpenHelper.FIELD_EID_ID + "=? and " + MyDBOpenHelper.FIELD_DIALBG_ID + "=?";
        String[] whereArgs = { AESUtil.getInstance().encryptDataStr(eid), id};//AESUtil.getInstance().encryptDataStr(eid)
        int ret = db.delete(getTableName(),whereClause,whereArgs);
        LogUtil.e("insert result = " + ret);

        db.close();
    }
}
