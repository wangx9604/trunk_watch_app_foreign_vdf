package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.beans.LocationData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

public class LocationDAO extends BaseDAO {
    private static LocationDAO instance = null;
    private static Context mContext;

    private LocationDAO(Context mContext) {
        super(mContext, MyDBOpenHelper.LOCATION_TABLE_NAME);
        // TODO Auto-generated constructor stub
    }

    public static LocationDAO getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new LocationDAO(context);

        return instance;
    }

    public LocationData readLocation(String eid) {
        LocationData location = null;

        Cursor myCursor = null;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            return null;
        }

        try {
            String szBuff = "SELECT * FROM " + getTableName() + " WHERE " + MyDBOpenHelper.FIELD_EID_ID + "='" + AESUtil.encryptDataStr(eid) + "'";
            myCursor = db.rawQuery(szBuff, null);

            if (myCursor.moveToNext())//已经有了
            {
                location = new LocationData();
                String string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LONGITUDE)));
                location.setLongitude(Double.valueOf(string));
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_LATITUEDE)));
                location.setLatitude(Double.valueOf(string));
                string = myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_LOCATION_TIME));
                location.setTimestamp(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_CITY)));
                location.setCity(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_DESCRIPTION)));
                location.setDescription(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_POI)));
                location.setPoi(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_ACCURACY)));
                location.setAccuracy(Float.valueOf(string));
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_TYPE)));
                location.setType(Integer.valueOf(string));
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_MAPTYPE)));
                location.setMapType(string);

                location.setWgs84Latlng(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude()));

                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BUSINESS)));
                location.setBusiness(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_INDOOR)));
                location.setIndoor(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_FLOOR)));
                location.setFloor(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BLDG)));
                location.setBldg(string);
                string = AESUtil.decryptDataStr(myCursor.getString(myCursor.getColumnIndex(MyDBOpenHelper.FIELD_BDID)));
                location.setBdid(string);
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("LOcationDAO  AddCursor() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);

        return location;

    }


    //更新或添加Location
    public void updateLocation(String eid, LocationData location) {
        int id;
        SQLiteDatabase db = this.openWritableDb();
        Cursor myCursor = null;
        if (db == null)
            LogUtil.e("open db error! updateChatMsg()");

        try {
            ContentValues rowData = new ContentValues();

            rowData.clear();
            rowData.put(MyDBOpenHelper.FIELD_EID_ID, AESUtil.encryptDataStr(eid));
            rowData.put(MyDBOpenHelper.FIELD_LOCATION_LONGITUDE, AESUtil.encryptDataStr(String.valueOf(location.getLongitude())));
            rowData.put(MyDBOpenHelper.FIELD_LOCATION_LATITUEDE, AESUtil.encryptDataStr(String.valueOf(location.getLatitude())));
            rowData.put(MyDBOpenHelper.FIELD_DESCRIPTION, AESUtil.encryptDataStr(location.getDescription()));
            rowData.put(MyDBOpenHelper.FIELD_POI, AESUtil.encryptDataStr(location.getPoi()));
            rowData.put(MyDBOpenHelper.FIELD_LOCATION_TIME, location.getTimestamp());
            rowData.put(MyDBOpenHelper.FIELD_CITY, AESUtil.encryptDataStr(location.getCity()));
            rowData.put(MyDBOpenHelper.FIELD_TYPE, AESUtil.encryptDataStr(String.valueOf(location.getType())));
            rowData.put(MyDBOpenHelper.FIELD_ACCURACY, AESUtil.encryptDataStr(String.valueOf(location.getAccuracy())));
            rowData.put(MyDBOpenHelper.FIELD_MAPTYPE, AESUtil.encryptDataStr(location.getMapType()));
            rowData.put(MyDBOpenHelper.FIELD_BUSINESS, AESUtil.encryptDataStr(location.getBusiness()));
            rowData.put(MyDBOpenHelper.FIELD_FLOOR, AESUtil.encryptDataStr(location.getFloor()));
            rowData.put(MyDBOpenHelper.FIELD_INDOOR, AESUtil.encryptDataStr(location.getIndoor()));
            rowData.put(MyDBOpenHelper.FIELD_BLDG, AESUtil.encryptDataStr(location.getBldg()));
            rowData.put(MyDBOpenHelper.FIELD_BDID, AESUtil.encryptDataStr(location.getBdid()));

            String qure = "SELECT * FROM " + getTableName() + " WHERE " + MyDBOpenHelper.FIELD_EID_ID + "='" + AESUtil.encryptDataStr(eid) + "'";
            myCursor = db.rawQuery(qure, null);
            if (myCursor.moveToNext()) {
                String szBuff = MyDBOpenHelper.FIELD_EID_ID + "='" + AESUtil.encryptDataStr(eid) + "'";
                id = db.update(MyDBOpenHelper.LOCATION_TABLE_NAME, rowData, szBuff, null);
                if (-1 == id) {
                    LogUtil.e("chat update error! updateChatMsg()");
                }
            } else {
                db.insertOrThrow(getTableName(), null, rowData);
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat  updateChatMsg() Exp:" + e.getMessage());
        }
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
