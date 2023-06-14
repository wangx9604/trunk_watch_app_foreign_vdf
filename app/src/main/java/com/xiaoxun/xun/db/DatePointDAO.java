package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.calendar.DatePoint;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatePointDAO extends BaseDAO {

    private static DatePointDAO instance = null;
    final static SimpleDateFormat formatDateKey = new SimpleDateFormat(
            "yyyyMMdd", Locale.CHINA);

    public DatePointDAO(Context mContext) {
        super(mContext, MyDBOpenHelper.DATEPOINT_TABLE_NAME);
        // TODO Auto-generated constructor stub
    }

    public static DatePointDAO getInstance(Context context) {
        if (instance == null)
            instance = new DatePointDAO(context);
        return instance;
    }

    public int getDatePoint(String eid) {
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(getTableName());
            szBuff.append(" WHERE ");
            szBuff.append(MyDBOpenHelper.FIELD_EID_ID);
            szBuff.append("='");
            szBuff.append(AESUtil.getInstance().encryptDataStr(eid));
            szBuff.append("'");
            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor == null || myCursor.getCount() <= 0) {
                return 0;
            }
            if (myCursor.moveToFirst()) {
                do {
                    Date date = formatDateKey.parse(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DATE)));
                    int num = myCursor.getInt(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DATE_NUM));
                    DatePoint item = new DatePoint(date, num);
                    DatePoint.datePointList.add(item);
                    LogUtil.d("xxxx"+"  "+ DatePoint.datePointList.get(DatePoint.datePointList.size() - 1).getDate().toString() + " "
                            +DatePoint.datePointList.size() + " " + DatePoint.datePointList.get(DatePoint.datePointList.size() - 1).getPointNum());
                } while (myCursor.moveToNext());
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("get trace error" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);

        return 1;

    }

    // 添加
    public void updateDatePoint(String eid, ArrayList<DatePoint> datePointList) {
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        Cursor myCursor = null;
        if (db == null)
            LogUtil.e("open db error! updateChatMsg()");
        try {
            String whereClause = MyDBOpenHelper.FIELD_EID_ID + "=?";
            String[] whereArgs = { AESUtil.getInstance().encryptDataStr(eid) };
            int ret = db.delete(MyDBOpenHelper.DATEPOINT_TABLE_NAME,whereClause,whereArgs);
            ContentValues rowData = new ContentValues();
            for (int i = 0; i < datePointList.size(); i++) {
                rowData.clear();
                rowData.put(MyDBOpenHelper.FIELD_EID_ID, AESUtil.getInstance().encryptDataStr(eid));
                rowData.put(MyDBOpenHelper.FIELD_DATE,
                        formatDateKey.format(datePointList.get(i).getDate()));
                rowData.put(MyDBOpenHelper.FIELD_DATE_NUM, datePointList.get(i)
                        .getPointNum());
                db.insertOrThrow(getTableName(), null, rowData);
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat  updateChatMsg() Exp:" + e.getMessage());
        }
        db.close();
    }

    public void queryAllData() {
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! updateChatMsg()");

        myCursor = db.query(getTableName(), null, null, null, null, null, null);

        closeCursor(myCursor);
        db.close();
    }

    public void checkTabIsAvaliable() {
        boolean ret = false;
        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
        while (cursor.moveToNext()) {
            //遍历出表名
            if (MyDBOpenHelper.DATEPOINT_TABLE_NAME.equals(cursor.getString(0))) {
                ret = true;
                break;
            }
        }
        if (ret == false) {
            db.execSQL("CREATE TABLE " + MyDBOpenHelper.DATEPOINT_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MyDBOpenHelper.FIELD_EID_ID + " TEXT," +
                    MyDBOpenHelper.FIELD_DATE + " TEXT," +
                    MyDBOpenHelper.FIELD_DATE_NUM + " INTEGER" +
                    ");");
        }
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
