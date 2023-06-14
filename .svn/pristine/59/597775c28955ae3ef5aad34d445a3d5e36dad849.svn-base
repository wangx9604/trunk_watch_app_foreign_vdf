/**
 *
 */
package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;

/**
 * @author LiuDF
 * @version V1.0
 * @date 2013-6-18 下午2:11:43
 * @Description: TODO(添加描述)
 */
public class NoticeMsgHisDAO extends BaseDAO {


    private static NoticeMsgHisDAO instance = null;
    public static String NOTICE_MSG_HIS_TABLE_NAME = "nnnnn_msg_his";          //消息数据库列表前缀
    public static final String FIELD_NOTICE_MSG_SRCID = "msg_srcid";          //发送者id
    public static final String FIELD_NOTICE_MSG_DSTID = "msg_dstid";          //接收者id
    public static final String FIELD_NOTICE_MSG_TIME = "msg_time";               //消息时间
    public static final String FIELD_NOTICE_MSG_TYPE = "msg_type";            //消息类型
    public static final String FIELD_NOTICE_MSG_STATUS = "msg_status";        //消息状态
    public static final String FIELD_NOTICE_MSG_CONTENT = "msg_message_content"; //消息内容

    public static NoticeMsgHisDAO getInstance(Context context) {
        if (instance == null)
            instance = new NoticeMsgHisDAO(context);
        return instance;
    }

    public NoticeMsgHisDAO(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
    }

    //	public void creatTable(ArrayList<String> newfamilyid){
//		SQLiteDatabase db = this.openWritableDb();
//		if(newfamilyid != null){
//			for(String familyid:newfamilyid){
//				StringBuilder table = new StringBuilder(NOTICE_MSG_HIS_TABLE_NAME);
//				table.append(familyid).append();
////                CREATE TABLE IF NOT EXISTS
//				try{
//					db.execSQL("CREATE TABLE "+table.toString()+" ("+
//							"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//							FIELD_CHAT_SRCID+" TEXT," +
//							FIELD_CHAT_DSTID+" TEXT," +
//							MyDBOpenHelper.FIELD_FAMILY_ID+" TEXT," +
//							MyDBOpenHelper.FIELD_WATCH_ID+" TEXT," +
//							");");
//				}catch (SQLException e){
//					e.printStackTrace();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//		db.close();
//	}
    public void deleteTable(String oldfamilyid, String userid) {
        SQLiteDatabase db = this.openWritableDb();
        if (oldfamilyid != null) {
            StringBuilder table = new StringBuilder(NOTICE_MSG_HIS_TABLE_NAME);
            table.append(oldfamilyid).append(userid);
            db.execSQL("DROP TABLE " + table.toString());
        }
        db.close();
    }

    /**
     * 新增Msg
     *
     * @param msg the chat content need to add
     * @return row ID
     */
    public long addNoticeMsg(String familyid, String userid, NoticeMsgData msg) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            ContentValues rowData = new ContentValues();
            rowData.clear();

            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(msg.getmDeviceid()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(msg.getmGroupid()));
            rowData.put(FIELD_NOTICE_MSG_CONTENT, AESUtil.getInstance().encryptDataStr(msg.getmContent()));
            rowData.put(FIELD_NOTICE_MSG_DSTID, AESUtil.getInstance().encryptDataStr(msg.getmDstid()));
            rowData.put(FIELD_NOTICE_MSG_SRCID, AESUtil.getInstance().encryptDataStr(msg.getmSrcid()));
            rowData.put(FIELD_NOTICE_MSG_TIME, msg.getmTimeStamp());
            rowData.put(FIELD_NOTICE_MSG_STATUS, msg.getmStatus());
            rowData.put(FIELD_NOTICE_MSG_TYPE, msg.getmType());

            id = db.insertOrThrow(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid, null, rowData);
            if (-1 == id) {
                LogUtil.e("Add chat error ! addChatMsg()");
            }
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Add chat addChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(NOTICE_MSG_HIS_TABLE_NAME);
            table.append(familyid).append(userid);
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_NOTICE_MSG_SRCID + " TEXT," +
                        FIELD_NOTICE_MSG_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_NOTICE_MSG_TIME + " TEXT," +
                        FIELD_NOTICE_MSG_TYPE + " INTEGER," +
                        FIELD_NOTICE_MSG_STATUS + " INTEGER," +
                        FIELD_NOTICE_MSG_CONTENT + " TEXT" +
                        ");");
                addNoticeMsg(familyid, userid, msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        return id;
    }

    /**
     * 删除Msg
     *
     * @param
     * @return
     */
    public long delChatMsg(String familyid, String userid, NoticeMsgData msg) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            //DELETE FROM mytable WHERE Name='Test1';
            szBuff.append("DELETE FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            szBuff.append(" WHERE ");
            szBuff.append(FIELD_NOTICE_MSG_TIME);
            szBuff.append("='");
            szBuff.append(msg.getmTimeStamp());
            szBuff.append("'");
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
        return id;
    }

    /**
     * 更新Msg 状态
     *
     * @param
     * @return
     */
    public void updateNoticeMsg(String familyid, String userid, NoticeMsgData msg, String findkey) {
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! updateChatMsg()");

        try {
            ContentValues rowData = new ContentValues();

            rowData.clear();

            StringBuilder szBuff = new StringBuilder();

            szBuff.append(FIELD_NOTICE_MSG_TIME);
            szBuff.append("='");
            szBuff.append(findkey);
            szBuff.append("'");


            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(msg.getmDeviceid()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(msg.getmGroupid()));
            rowData.put(FIELD_NOTICE_MSG_CONTENT, AESUtil.getInstance().encryptDataStr(msg.getmContent()));
            rowData.put(FIELD_NOTICE_MSG_DSTID, AESUtil.getInstance().encryptDataStr(msg.getmDstid()));
            rowData.put(FIELD_NOTICE_MSG_SRCID, AESUtil.getInstance().encryptDataStr(msg.getmSrcid()));
            rowData.put(FIELD_NOTICE_MSG_TIME, msg.getmTimeStamp());
            rowData.put(FIELD_NOTICE_MSG_STATUS, msg.getmStatus());
            rowData.put(FIELD_NOTICE_MSG_TYPE, msg.getmType());
            id = db.update(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid, rowData, szBuff.toString(), null);
            if (-1 == id) {
                LogUtil.e("chat update error! updateChatMsg()");
            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat  updateChatMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
    }

    /**
     * 根据familyid读取所有消息
     *
     * @param
     * @return
     */
    public void readAllMsgForFamily(String familyid, String userid, ArrayList<NoticeMsgData> chatlist) {
        Cursor myCursor = null;
        if (chatlist == null || familyid == null || userid == null) {
            LogUtil.e("chatlist is null ! readAllMsgForFamily()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! readAllMsgForFamily()");
        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_STATUS + " <> " + NoticeMsgData.MSG_STATUS_DELETE);
            szBuff.append(" ORDER BY " + FIELD_NOTICE_MSG_TIME + " ASC"); //按时间升序查询

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
                do {
                    NoticeMsgData chat = new NoticeMsgData();
                    chat.setmSrcid(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    chat.setmDstid(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setmGroupid(AESUtil.getInstance().decryptDataStr(myCursor.getString(3)));
                    chat.setmDeviceid(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setmTimeStamp(myCursor.getString(5));
                    chat.setmType(myCursor.getInt(6));
                    chat.setmStatus(myCursor.getInt(7));
                    chat.setmContent(AESUtil.getInstance().decryptDataStr(myCursor.getString(8)));

                    chatlist.add(chat);
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtil.e("chat read readAllMsgForFamily() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(NOTICE_MSG_HIS_TABLE_NAME);
            table.append(familyid).append(userid);
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_NOTICE_MSG_SRCID + " TEXT," +
                        FIELD_NOTICE_MSG_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_NOTICE_MSG_TIME + " TEXT," +
                        FIELD_NOTICE_MSG_TYPE + " INTEGER," +
                        FIELD_NOTICE_MSG_STATUS + " INTEGER," +
                        FIELD_NOTICE_MSG_CONTENT + " TEXT" +
                        ");");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
    }

    public void readMsgForFamilyByType(String familyid, String userid, ArrayList<NoticeMsgData> noticeList, int type) {
        if (type == NoticeMsgData.MSG_TYPE_ALL) {
            readAllMsgForFamily(familyid, userid, noticeList);
            return;
        }
        Cursor myCursor = null;
        if (noticeList == null || familyid == null || userid == null) {
            LogUtil.e("chatlist is null ! readAllMsgForFamily()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! readAllMsgForFamily()");
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_STATUS + " <> " + NoticeMsgData.MSG_STATUS_DELETE);
            szBuff.append(" AND (");
            if (type == NoticeMsgData.MSG_TYPE_SAFE_AREA || type == NoticeMsgData.MSG_TYPE_SOS_LOCATION || type == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW
                    || type == NoticeMsgData.MSG_TYPE_NAVIGATION) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SOS_LOCATION);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_AREA);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_NAVIGATION);
            } else if (type == NoticeMsgData.MSG_TYPE_CHANGE_SIM || type == NoticeMsgData.MSG_TYPE_SMS
                    || type == NoticeMsgData.MSG_TYPE_FLOWMETER || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE
                    || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CHANGE_SIM);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SMS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_FLOWMETER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX);
            } else if (type == NoticeMsgData.MSG_TYPE_STAEPS || type == NoticeMsgData.MSG_TYPE_STAEPSRANKS) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPSRANKS);
            } else if (type == NoticeMsgData.MSG_TYPE_DOWNLOAD || type == NoticeMsgData.MSG_TYPE_CLOUD_SPACE
                    || type == NoticeMsgData.MSG_TYPE_APPMANAGER || type == NoticeMsgData.MSG_TYPE_STORY) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_DOWNLOAD);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CLOUD_SPACE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_APPMANAGER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STORY);
            } else {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + type);
            }
            szBuff.append(") ORDER BY " + FIELD_NOTICE_MSG_TIME + " ASC"); //按时间升序查询
            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
                do {
                    NoticeMsgData chat = new NoticeMsgData();
                    chat.setmSrcid(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    chat.setmDstid(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setmGroupid(AESUtil.getInstance().decryptDataStr(myCursor.getString(3)));
                    chat.setmDeviceid(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setmTimeStamp(myCursor.getString(5));
                    chat.setmType(myCursor.getInt(6));
                    chat.setmStatus(myCursor.getInt(7));
                    chat.setmContent(AESUtil.getInstance().decryptDataStr(myCursor.getString(8)));
                    noticeList.add(chat);
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtil.e("chat read readAllMsgForFamily() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(NOTICE_MSG_HIS_TABLE_NAME);
            table.append(familyid).append(userid);
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_NOTICE_MSG_SRCID + " TEXT," +
                        FIELD_NOTICE_MSG_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_NOTICE_MSG_TIME + " TEXT," +
                        FIELD_NOTICE_MSG_TYPE + " INTEGER," +
                        FIELD_NOTICE_MSG_STATUS + " INTEGER," +
                        FIELD_NOTICE_MSG_CONTENT + " TEXT" +
                        ");");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
    }

    public boolean isMsgExist(String familyId, String userId, String key) {
        if (familyId == null || familyId.length() == 0 || key == null || key.length() == 0
                || userId == null || userId.length() == 0) {
            return false;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("open db error! readAllChatMsg()");
            return false;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyId + userId);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_TIME + " ='" + key + "'");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                db.close();
                return true;
            } else {
                closeCursor(cursor);
                db.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false;
        }
    }

    public NoticeMsgData getLatestMessage(String familyId, String userId) {
        if (familyId == null || familyId.length() == 0 || userId == null || userId.length() == 0) {
            return null;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("open db error! readAllChatMsg()");
            return null;
        }
        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyId + userId);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_STATUS + " <> " + NoticeMsgData.MSG_STATUS_DELETE);
            szBuff.append(" ORDER BY " + FIELD_NOTICE_MSG_TIME + " DESC");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                NoticeMsgData chat = new NoticeMsgData();
                chat.setmSrcid(AESUtil.getInstance().decryptDataStr(cursor.getString(1)));
                chat.setmDstid(AESUtil.getInstance().decryptDataStr(cursor.getString(2)));
                chat.setmGroupid(AESUtil.getInstance().decryptDataStr(cursor.getString(3)));
                chat.setmDeviceid(AESUtil.getInstance().decryptDataStr(cursor.getString(4)));
                chat.setmTimeStamp(cursor.getString(5));
                chat.setmType(cursor.getInt(6));
                chat.setmStatus(cursor.getInt(7));
                chat.setmContent(AESUtil.getInstance().decryptDataStr(cursor.getString(8)));
                cursor.close();
                db.close();
                return chat;
            } else {
                closeCursor(cursor);
                db.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return null;
        }
    }

    public NoticeMsgData getLatestMessage(String familyId, String userId, int type) {
        if (familyId == null || familyId.length() == 0 || userId == null || userId.length() == 0) {
            return null;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("open db error! readAllChatMsg()");
            return null;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyId + userId);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_STATUS + " <> " + NoticeMsgData.MSG_STATUS_DELETE);
            szBuff.append(" AND (");
            if (type == NoticeMsgData.MSG_TYPE_SAFE_AREA || type == NoticeMsgData.MSG_TYPE_SOS_LOCATION || type == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW
                    || type == NoticeMsgData.MSG_TYPE_NAVIGATION) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SOS_LOCATION);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_AREA);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_NAVIGATION);
            } else if (type == NoticeMsgData.MSG_TYPE_CHANGE_SIM || type == NoticeMsgData.MSG_TYPE_SMS
                    || type == NoticeMsgData.MSG_TYPE_FLOWMETER || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE
                    || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CHANGE_SIM);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SMS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_FLOWMETER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX);
            } else if (type == NoticeMsgData.MSG_TYPE_STAEPS || type == NoticeMsgData.MSG_TYPE_STAEPSRANKS) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPSRANKS);
            } else if (type == NoticeMsgData.MSG_TYPE_DOWNLOAD || type == NoticeMsgData.MSG_TYPE_CLOUD_SPACE
                    || type == NoticeMsgData.MSG_TYPE_APPMANAGER || type == NoticeMsgData.MSG_TYPE_STORY) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_DOWNLOAD);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CLOUD_SPACE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_APPMANAGER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STORY);
            } else {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + type);
            }
            szBuff.append(") ORDER BY " + FIELD_NOTICE_MSG_TIME + " DESC");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                NoticeMsgData chat = new NoticeMsgData();
                chat.setmSrcid(AESUtil.getInstance().decryptDataStr(cursor.getString(1)));
                chat.setmDstid(AESUtil.getInstance().decryptDataStr(cursor.getString(2)));
                chat.setmGroupid(AESUtil.getInstance().decryptDataStr(cursor.getString(3)));
                chat.setmDeviceid(AESUtil.getInstance().decryptDataStr(cursor.getString(4)));
                chat.setmTimeStamp(cursor.getString(5));
                chat.setmType(cursor.getInt(6));
                chat.setmStatus(cursor.getInt(7));
                chat.setmContent(AESUtil.getInstance().decryptDataStr(cursor.getString(8)));
                cursor.close();
                db.close();
                return chat;
            } else {
                closeCursor(cursor);
                db.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return null;
        }
    }

    public void readSpamMsgByFamilyId(String familyid, String userid, ArrayList<NoticeMsgData> spamSmsList) {
        Cursor myCursor = null;
        if (spamSmsList == null || familyid == null || userid == null) {
            LogUtil.e("spamSmsList is null ! readAllMsgForFamily()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! readAllMsgForFamily()");
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            szBuff.append(" WHERE " + FIELD_NOTICE_MSG_STATUS + " = " + NoticeMsgData.MSG_STATUS_SPAM);
            szBuff.append(" ORDER BY " + FIELD_NOTICE_MSG_TIME + " ASC"); //按时间升序查询

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
                do {
                    NoticeMsgData chat = new NoticeMsgData();
                    chat.setmSrcid(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    chat.setmDstid(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setmGroupid(AESUtil.getInstance().decryptDataStr(myCursor.getString(3)));
                    chat.setmDeviceid(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setmTimeStamp(myCursor.getString(5));
                    chat.setmType(myCursor.getInt(6));
                    chat.setmStatus(myCursor.getInt(7));
                    chat.setmContent(AESUtil.getInstance().decryptDataStr(myCursor.getString(8)));
                    spamSmsList.add(chat);
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtil.e("chat read readSpamMsgByFamilyId() Exp:" + e.getMessage());
        }
        db.close();
        closeCursor(myCursor);
    }

    public long deleteAllMsg(String familyid, String userid) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("DELETE FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
        return id;
    }

    public long deleteAllMsgByType(String familyid, String userid, int type) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("DELETE FROM ");
            szBuff.append(NOTICE_MSG_HIS_TABLE_NAME + familyid + userid);
            szBuff.append(" WHERE (");
            if (type == NoticeMsgData.MSG_TYPE_SAFE_AREA || type == NoticeMsgData.MSG_TYPE_SOS_LOCATION || type == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW
                    || type == NoticeMsgData.MSG_TYPE_NAVIGATION) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SOS_LOCATION);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_AREA);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_NAVIGATION);
            } else if (type == NoticeMsgData.MSG_TYPE_CHANGE_SIM || type == NoticeMsgData.MSG_TYPE_SMS
                    || type == NoticeMsgData.MSG_TYPE_FLOWMETER || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE
                    || type == NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CHANGE_SIM);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_SMS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_FLOWMETER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX);
            } else if (type == NoticeMsgData.MSG_TYPE_STAEPS || type == NoticeMsgData.MSG_TYPE_STAEPSRANKS) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPS);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STAEPSRANKS);
            } else if (type == NoticeMsgData.MSG_TYPE_DOWNLOAD || type == NoticeMsgData.MSG_TYPE_CLOUD_SPACE
                    || type == NoticeMsgData.MSG_TYPE_APPMANAGER || type == NoticeMsgData.MSG_TYPE_STORY) {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_DOWNLOAD);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_CLOUD_SPACE);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_APPMANAGER);
                szBuff.append(" OR " + FIELD_NOTICE_MSG_TYPE + " = " + NoticeMsgData.MSG_TYPE_STORY);
            } else {
                szBuff.append(FIELD_NOTICE_MSG_TYPE + " = " + type);
            }
            szBuff.append(")");
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
        return id;
    }
}
