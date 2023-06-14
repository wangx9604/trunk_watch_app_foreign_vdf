package com.xiaoxun.xun.gallary.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaoxun.xun.gallary.dataStruct.GalleryData;

import java.util.ArrayList;

/**
 * Created by xilvkang on 2017/3/30.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String name = "Gallery.db"; //数据库名称

    private static final int version = 4; //数据库版本

    public static final String GALLERY_TABLE_NAME = "GALLERY_IMAGE";
    public static final String EID = "EID";
    public static final String TYPE = "TYPE";
    public static final String NAME = "NAME";
    public static final String TIME = "TIME";
    public static final String PREVIEW_URL = "PREVIEW_URL";
    public static final String SRC_URL = "SRC_URL";
    public static final String LOCAL_PATH = "LOCAL_PATH";
    public static final String LOCAL_PRE_PATH = "LOCAL_PRE_PATH";
    public static final String REFERENCE = "REFERENCE";
    public static final String SRC_REFERENCE = "SRC_REFERENCE";
    public static final String STATUS = "STATUS";
    public static final String VIDEO_SHARE_URL = "SHARE_URL";


    private SQLiteDatabase db;

    public DataBaseHelper(Context context) {
        super(context, name, null, version);
        db = getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + GALLERY_TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                EID + " TEXT," +
                TYPE + " INTEGER," +
                NAME + " TEXT," +
                TIME + " INTEGER," +
                PREVIEW_URL + " TEXT," +
                SRC_URL + " TEXT," +
                LOCAL_PATH + " TEXT," +
                LOCAL_PRE_PATH + " TEXT," +
                REFERENCE + " INTEGER," +
                SRC_REFERENCE + " INTEGER," +
                STATUS + " INTEGER," +
                VIDEO_SHARE_URL + " TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GALLERY_TABLE_NAME + ";");
        }
    }

    public ArrayList<GalleryData> getImageVideoBySize(String eid, String end, int size){
        ArrayList<GalleryData> list = new ArrayList<GalleryData>();
        Cursor myCursor = null;

        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT * FROM ");
        szBuff.append(GALLERY_TABLE_NAME);
        szBuff.append(" WHERE ");
        szBuff.append(EID);
        szBuff.append(" ='");
        szBuff.append(eid);
        szBuff.append("'");
        szBuff.append(" AND ");
        szBuff.append(TIME);
        szBuff.append(" >= '");
        szBuff.append(end);
        szBuff.append("'");
        szBuff.append(" LIMIT ");
        szBuff.append(size);

        myCursor = db.rawQuery(szBuff.toString(), null);
        if (myCursor != null && myCursor.moveToFirst()) {
            do {
                GalleryData item = new GalleryData();
                item.setEid(myCursor.getString(myCursor.getColumnIndex(EID)));
                item.setType(myCursor.getInt(myCursor.getColumnIndex(TYPE)));
                item.setName(myCursor.getString(myCursor.getColumnIndex(NAME)));
                item.setTime(myCursor.getLong(myCursor.getColumnIndex(TIME)));
                item.setPreview_url(myCursor.getString(myCursor.getColumnIndex(PREVIEW_URL)));
                item.setSrc_url(myCursor.getString(myCursor.getColumnIndex(SRC_URL)));
                item.setLocal_src_path(myCursor.getString(myCursor.getColumnIndex(LOCAL_PATH)));
                item.setLocal_pre_path(myCursor.getString(myCursor.getColumnIndex(LOCAL_PRE_PATH)));
                item.setReference(myCursor.getInt(myCursor.getColumnIndex(REFERENCE)));
                item.setSrcReference(myCursor.getInt(myCursor.getColumnIndex(SRC_REFERENCE)));
                item.setStatus(myCursor.getInt(myCursor.getColumnIndex(STATUS)));
                item.setVideo_share_url(myCursor.getString(myCursor.getColumnIndex(VIDEO_SHARE_URL)));
                list.add(item);
            } while (myCursor.moveToNext());

            myCursor.close();
        }

        return list;
    }

    public ArrayList<GalleryData> getImageVideo(String eid,String begin,String end) {
        ArrayList<GalleryData> list = new ArrayList<GalleryData>();
        Cursor myCursor = null;

        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT * FROM ");
        szBuff.append(GALLERY_TABLE_NAME);
        szBuff.append(" WHERE ");
        szBuff.append(EID);
        szBuff.append(" ='");
        szBuff.append(eid);
        szBuff.append("'");
        szBuff.append(" AND ");
        szBuff.append(TIME);
        szBuff.append(" <= '");
        szBuff.append(begin);
        szBuff.append("'");
        if(!end.equals("")) {
            szBuff.append(" AND ");
            szBuff.append(TIME);
            szBuff.append(" >= '");
            szBuff.append(end);
            szBuff.append("'");
        }

        myCursor = db.rawQuery(szBuff.toString(), null);
        if (myCursor != null && myCursor.moveToFirst()) {
            do {
                GalleryData item = new GalleryData();
                item.setEid(myCursor.getString(myCursor.getColumnIndex(EID)));
                item.setType(myCursor.getInt(myCursor.getColumnIndex(TYPE)));
                item.setName(myCursor.getString(myCursor.getColumnIndex(NAME)));
                item.setTime(myCursor.getLong(myCursor.getColumnIndex(TIME)));
                item.setPreview_url(myCursor.getString(myCursor.getColumnIndex(PREVIEW_URL)));
                item.setSrc_url(myCursor.getString(myCursor.getColumnIndex(SRC_URL)));
                item.setLocal_src_path(myCursor.getString(myCursor.getColumnIndex(LOCAL_PATH)));
                item.setLocal_pre_path(myCursor.getString(myCursor.getColumnIndex(LOCAL_PRE_PATH)));
                item.setReference(myCursor.getInt(myCursor.getColumnIndex(REFERENCE)));
                item.setSrcReference(myCursor.getInt(myCursor.getColumnIndex(SRC_REFERENCE)));
                item.setStatus(myCursor.getInt(myCursor.getColumnIndex(STATUS)));
                item.setVideo_share_url(myCursor.getString(myCursor.getColumnIndex(VIDEO_SHARE_URL)));
                list.add(item);
            } while (myCursor.moveToNext());

            myCursor.close();
        }

        return list;
    }

    public GalleryData getItem(String name,String eid){
        GalleryData item = new GalleryData();

        StringBuilder szBuff = new StringBuilder();
        szBuff.append("SELECT * FROM ");
        szBuff.append(GALLERY_TABLE_NAME);
        szBuff.append(" WHERE ");
        szBuff.append(EID);
        szBuff.append(" ='");
        szBuff.append(eid);
        szBuff.append("'");
        szBuff.append(" AND ");
        szBuff.append(NAME);
        szBuff.append(" = '");
        szBuff.append(name);
        szBuff.append("'");

        Cursor mycursor = db.rawQuery(szBuff.toString(), null);
        if(mycursor != null && mycursor.moveToFirst()){
            item.setEid(mycursor.getString(mycursor.getColumnIndex(EID)));
            item.setType(mycursor.getInt(mycursor.getColumnIndex(TYPE)));
            item.setName(mycursor.getString(mycursor.getColumnIndex(NAME)));
            item.setTime(mycursor.getLong(mycursor.getColumnIndex(TIME)));
            item.setPreview_url(mycursor.getString(mycursor.getColumnIndex(PREVIEW_URL)));
            item.setSrc_url(mycursor.getString(mycursor.getColumnIndex(SRC_URL)));
            item.setLocal_src_path(mycursor.getString(mycursor.getColumnIndex(LOCAL_PATH)));
            item.setLocal_pre_path(mycursor.getString(mycursor.getColumnIndex(LOCAL_PRE_PATH)));
            item.setReference(mycursor.getInt(mycursor.getColumnIndex(REFERENCE)));
            item.setSrcReference(mycursor.getInt(mycursor.getColumnIndex(SRC_REFERENCE)));
            item.setStatus(mycursor.getInt(mycursor.getColumnIndex(STATUS)));
            item.setVideo_share_url(mycursor.getString(mycursor.getColumnIndex(VIDEO_SHARE_URL)));
        }else{
            item = null;
        }
        mycursor.close();
        return item;
    }

    public void addItem(GalleryData item) {
        ContentValues rowData = new ContentValues();
        rowData.clear();
        rowData.put(EID, item.getEid());
        rowData.put(TYPE, item.getType());
        rowData.put(NAME, item.getName());
        rowData.put(TIME, item.getTime());
        rowData.put(PREVIEW_URL, item.getPreview_url());
        rowData.put(SRC_URL, item.getSrc_url());
        rowData.put(LOCAL_PATH, item.getLocal_src_path());
        rowData.put(LOCAL_PRE_PATH, item.getLocal_pre_path());
        rowData.put(REFERENCE, item.getReference());
        rowData.put(SRC_REFERENCE, item.getSrcReference());
        rowData.put(STATUS, item.getStatus());
        rowData.put(VIDEO_SHARE_URL,item.getVideo_share_url());
        db.insertOrThrow(GALLERY_TABLE_NAME, null, rowData);

    }

    public void upgradeItem(GalleryData item){
        ContentValues rowData = new ContentValues();
        rowData.clear();
        rowData.put(EID, item.getEid());
        rowData.put(TYPE, item.getType());
        rowData.put(NAME, item.getName());
        rowData.put(TIME, item.getTime());
        rowData.put(PREVIEW_URL, item.getPreview_url());
        rowData.put(SRC_URL, item.getSrc_url());
        rowData.put(LOCAL_PATH, item.getLocal_src_path());
        rowData.put(LOCAL_PRE_PATH, item.getLocal_pre_path());
        rowData.put(REFERENCE, item.getReference());
        rowData.put(SRC_REFERENCE, item.getSrcReference());
        rowData.put(STATUS, item.getStatus());
        rowData.put(VIDEO_SHARE_URL,item.getVideo_share_url());
        String where = NAME + " = ? AND " + EID + " = ?";

        db.update(GALLERY_TABLE_NAME,rowData,where,new String[]{item.getName(),item.getEid()});
    }

    public void deleteItem(GalleryData item) {
        StringBuilder szBuff = new StringBuilder();
        szBuff.append(EID);
        szBuff.append("='");
        szBuff.append(item.getEid());
        szBuff.append("'");
        szBuff.append(" and ");
        szBuff.append(NAME);
        szBuff.append("='");
        szBuff.append(item.getName());
        szBuff.append("'");
        db.delete(GALLERY_TABLE_NAME, szBuff.toString(), null);

    }

    public void deleteOneData(String eid) {
        StringBuilder szBuff = new StringBuilder();
        szBuff.append(EID);
        szBuff.append("='");
        szBuff.append(eid);
        szBuff.append("'");
        db.delete(GALLERY_TABLE_NAME, szBuff.toString(), null);

    }

    public void deleteTableData(){
        db.delete(GALLERY_TABLE_NAME,null,null);
    }

    public void close(){
        db.close();
    }
}
