/**
 * Creation Date:2015-1-22
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;


/**
 * Description Of The Class<br>
 *
 * @author huangqilin
 * @version 1.000, 2015-1-22
 */
public class ChatHisDao extends BaseDAO {

    public static String CHAT_HIS_TABLE_NAME = "chat_his";
    public static final String FIELD_CHAT_SRCID = "chat_srcid";          //发送者id       
    public static final String FIELD_CHAT_DSTID = "chat_dstid";          //接收者id
    public static final String FIELD_CHAT_AUDIOPATH = "chat_audio_path";      //语音路径
    public static final String FIELD_CHAT_DATE = "chat_date";           //语音录制时间
    public static final String FIELD_CHAT_DURATION = "chat_duration";          //语音时长，单位为'秒'
    public static final String FIELD_CHAT_PLAYED = "chat_played";      //是否播放标志
    public static final String FIELD_CHAT_IS_FROM = "chat_is_from";       //收到或者发出标志，true表示内容是接收的，false表示内容是发送的
    public static final String FIELD_CHAT_TYPE = "chat_type";
    public static final String FIELD_CHAT_SEND_STATE = "chat_send_state"; //语音发送状态
    public static final String FIELD_CHAT_RECORD_STATE = "chat_record_state"; //远程录音状态
    //    public static final String FIELD_CHAT_MESSAGE_CONTENT = "chat_message_content"; //非语音消息内容
//    public static final String FIELD_CHAT_SERVICE_KEY = "chat_service_key"; //语音的key值
    private static ChatHisDao instance = null;

    public static ChatHisDao getInstance(Context context) {
        if (instance == null)
            instance = new ChatHisDao(context);
        return instance;
    }

    public ChatHisDao(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
    }

    public void creatTable(ArrayList<String> newfamilyid) {
        SQLiteDatabase db = this.openWritableDb();
        if (newfamilyid != null) {
            for (String familyid : newfamilyid) {
                StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
                table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
                try {
                    db.execSQL("CREATE TABLE " + table.toString() + " (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            FIELD_CHAT_SRCID + " TEXT," +
                            FIELD_CHAT_DSTID + " TEXT," +
                            MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                            MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                            FIELD_CHAT_AUDIOPATH + " TEXT," +
                            FIELD_CHAT_DATE + " TEXT," +
                            FIELD_CHAT_SEND_STATE + " INTEGER," +
                            FIELD_CHAT_RECORD_STATE + " INTEGER," +
                            FIELD_CHAT_DURATION + " INTEGER," +//
                            FIELD_CHAT_IS_FROM + " INTEGER," + //
                            FIELD_CHAT_PLAYED + " INTEGER," +
                            FIELD_CHAT_TYPE + " INTEGER" +
                            ");");
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        db.close();
    }

    public void deleteTable(ArrayList<String> oldfamilyid) {
        SQLiteDatabase db = this.openWritableDb();
        if (oldfamilyid != null) {
            for (String familyid : oldfamilyid) {
                StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
                table.append(familyid);
                db.execSQL("DROP TABLE " + table.toString());
            }
        }
        db.close();
    }

    /**
     * 新增ChatMsg
     *
     * @param chat the chat content need to add
     * @return row ID
     */
    public long addChatMsg(String familyid, ChatMsgEntity chat) {
//        Cursor myCursor = null;
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ");
            builder.append(CHAT_HIS_TABLE_NAME + familyid);
            builder.append(" WHERE ");
            builder.append(FIELD_CHAT_DATE);
            builder.append("='");
            builder.append(chat.getmDate());
            builder.append("'");
            Cursor cursor = db.rawQuery(builder.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                cursor.close();
                db.close();
                return count;
            }

            ContentValues rowData = new ContentValues();
            rowData.clear();

            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(chat.getmWatchId()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(chat.getmFamilyId()));
            rowData.put(FIELD_CHAT_AUDIOPATH, AESUtil.getInstance().encryptDataStr(chat.getmAudioPath()));
            rowData.put(FIELD_CHAT_DATE, chat.getmDate());
            rowData.put(FIELD_CHAT_DSTID, AESUtil.getInstance().encryptDataStr(chat.getmDstId()));
            rowData.put(FIELD_CHAT_SRCID, AESUtil.getInstance().encryptDataStr(chat.getmSrcId()));
            rowData.put(FIELD_CHAT_TYPE, chat.getmType());
            rowData.put(FIELD_CHAT_DURATION, chat.getmDuration());
            rowData.put(FIELD_CHAT_SEND_STATE, chat.getmSended());
            rowData.put(FIELD_CHAT_RECORD_STATE, chat.getmForceRecordOk());
            if (chat.getmIsFrom() == true) {
                rowData.put(FIELD_CHAT_IS_FROM, 1);
            } else {
                rowData.put(FIELD_CHAT_IS_FROM, 0);
            }
            if (chat.getmPlayed() == true) {
                rowData.put(FIELD_CHAT_PLAYED, 1);
            } else {
                rowData.put(FIELD_CHAT_PLAYED, 0);
            }
            id = db.insertOrThrow(CHAT_HIS_TABLE_NAME + familyid, null, rowData);
            if (-1 == id) {
                LogUtil.e("Add chat error ! addChatMsg()");
            }
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Add chat addChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_FROM + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
                addChatMsg(familyid, chat);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        db.close();
//        closeCursor(myCursor);
        return id;
    }

    /**
     * 删除ChatMsg
     *
     * @param
     * @return
     */
    public long delChatMsg(String familyid, ChatMsgEntity chat) {
//        Cursor myCursor = null;
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            //DELETE FROM mytable WHERE Name='Test1';
            szBuff.append("DELETE FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" WHERE ");
            szBuff.append(FIELD_CHAT_DATE);
            szBuff.append("='");
            szBuff.append(chat.getmDate());
            szBuff.append("'");
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
        return id;
    }

    /**
     * 更新ChatMsg 状态
     *
     * @param
     * @return
     */
    public void updateChatMsg(String familyid, ChatMsgEntity chat, String findkey) {
//        Cursor myCursor = null;
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! updateChatMsg()");

        try {
            ContentValues rowData = new ContentValues();

            rowData.clear();

            StringBuilder szBuff = new StringBuilder();

//            szBuff.append("SELECT * FROM ");
//            szBuff.append(CHAT_HIS_TABLE_NAME+familyid);
//            szBuff.append(" WHERE ");
            szBuff.append(FIELD_CHAT_DATE);
            szBuff.append("='");
            szBuff.append(findkey);
            szBuff.append("'");

//            myCursor = db.rawQuery(szBuff.toString(), null);

            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, AESUtil.getInstance().encryptDataStr(chat.getmWatchId()));
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, AESUtil.getInstance().encryptDataStr(chat.getmFamilyId()));
            rowData.put(FIELD_CHAT_AUDIOPATH, AESUtil.getInstance().encryptDataStr(chat.getmAudioPath()));
            rowData.put(FIELD_CHAT_DATE, chat.getmDate());
            rowData.put(FIELD_CHAT_DSTID, AESUtil.getInstance().encryptDataStr(chat.getmDstId()));
            rowData.put(FIELD_CHAT_SRCID, AESUtil.getInstance().encryptDataStr(chat.getmSrcId()));
            rowData.put(FIELD_CHAT_DURATION, chat.getmDuration());
            rowData.put(FIELD_CHAT_TYPE, chat.getmType());
            rowData.put(FIELD_CHAT_SEND_STATE, chat.getmSended());
            rowData.put(FIELD_CHAT_RECORD_STATE, chat.getmForceRecordOk());
            if (chat.getmIsFrom() == true) {
                rowData.put(FIELD_CHAT_IS_FROM, 1);
            } else {
                rowData.put(FIELD_CHAT_IS_FROM, 0);
            }
            if (chat.getmPlayed() == true) {
                rowData.put(FIELD_CHAT_PLAYED, 1);
            } else {
                rowData.put(FIELD_CHAT_PLAYED, 0);
            }
            id = db.update(CHAT_HIS_TABLE_NAME + familyid, rowData, szBuff.toString(), null);
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
     * 根据familyid读取所有ChatMsg
     *
     * @param
     * @return
     */
    public void readAllChatFromFamily(String familyid, ArrayList<ChatMsgEntity> chatlist, ArrayList<ChatMsgEntity> dellist) {
        Cursor myCursor = null;
        if (chatlist == null || familyid == null) {
            LogUtil.e("chatlist is null ! readAllChatMsg()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            LogUtil.e("open db error! readAllChatMsg()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" ORDER BY " + FIELD_CHAT_DATE + " ASC"); //按时间升序查询

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +			
//                  3      FIELD_FAMILY_ID+" TEXT," +     
//                  4      FIELD_WATCH_ID+" TEXT," +               
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +                     
//                  6      FIELD_CHAT_DATE+" TEXT," +      
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," + 
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," + 
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do {
                    ChatMsgEntity chat = new ChatMsgEntity();
                    chat.setmSrcId(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    chat.setmDstId(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setmFamilyId(AESUtil.getInstance().decryptDataStr(myCursor.getString(3)));
                    chat.setmWatchId(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setmAudioPath(AESUtil.getInstance().decryptDataStr(myCursor.getString(5)));
                    chat.setmDate(myCursor.getString(6));
                    if (myCursor.getInt(7) == 1 || myCursor.getInt(7) == 4) {
                        chat.setmSended(myCursor.getInt(7));
                    } else {
                        chat.setmSended(2);
                    }
                    chat.setmForceRecordOk(myCursor.getInt(8));
                    chat.setmDuration(myCursor.getInt(9));
                    if (myCursor.getInt(10) == 1) {
                        chat.setmIsFrom(true);
                    } else {
                        chat.setmIsFrom(false);
                    }
                    if (myCursor.getInt(11) == 1) {
                        chat.setmPlayed(true);
                    } else {
                        chat.setmPlayed(false);
                    }
                    chat.setmType(myCursor.getInt(12));
                    if (chat.getmSended() == 4) {
                        dellist.add(chat);
                    } else {
                        chatlist.add(chat);
                    }
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_FROM + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
    }

    public boolean isMsgExist(String familyId, String key) {
        if (familyId == null || familyId.length() == 0 || key == null || key.length() == 0) {
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
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            szBuff.append(" WHERE " + FIELD_CHAT_DATE + " ='" + key + "'");
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

    public int getMsgCount(String familyId) {
        if (familyId == null || familyId.length() == 0) {
            return -1;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            LogUtil.e("open db error! readAllChatMsg()");
            return -1;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                cursor.close();
                db.close();
                return count;
            } else {
                closeCursor(cursor);
                db.close();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long delAllMsg(String familyid) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("DELETE FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
        return id;
    }

    public ChatMsgEntity getLatestMessage(String familyId) {
        if (familyId == null || familyId.length() == 0) {
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
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            szBuff.append(" WHERE " + FIELD_CHAT_SEND_STATE + " <> 4");
            szBuff.append(" ORDER BY " + FIELD_CHAT_DATE + " DESC");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                ChatMsgEntity chat = new ChatMsgEntity();
                chat.setmSrcId(AESUtil.decryptDataStr(cursor.getString(1)));
                chat.setmDstId(AESUtil.decryptDataStr(cursor.getString(2)));
                chat.setmFamilyId(AESUtil.decryptDataStr(cursor.getString(3)));
                chat.setmWatchId(AESUtil.decryptDataStr(cursor.getString(4)));
                chat.setmAudioPath(AESUtil.decryptDataStr(cursor.getString(5)));
                chat.setmDate(cursor.getString(6));
                if (cursor.getInt(7) == 1 || cursor.getInt(7) == 4) {
                    chat.setmSended(cursor.getInt(7));
                } else {
                    chat.setmSended(2);
                }
                chat.setmForceRecordOk(cursor.getInt(8));
                chat.setmDuration(cursor.getInt(9));
                if (cursor.getInt(10) == 1) {
                    chat.setmIsFrom(true);
                } else {
                    chat.setmIsFrom(false);
                }
                if (cursor.getInt(11) == 1) {
                    chat.setmPlayed(true);
                } else {
                    chat.setmPlayed(false);
                }
                chat.setmType(cursor.getInt(12));
                cursor.close();
                db.close();
                return chat;
            } else {
                closeCursor(cursor);
                db.close();
                return null;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            db.close();
            return null;
        }
    }
}
