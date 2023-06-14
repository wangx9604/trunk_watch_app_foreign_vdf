package com.xiaoxun.xun.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaoxun.xun.utils.LogUtil;

public class MyDBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "imibaby.db";
	private static final int DB_VERSION = 11;
	//数据库表名
	public static String CONFIG_TABLE_NAME = "configs"; //用户配置信息
    public static String OFFLINE_MAP_CITY = "offlinemapcity";//以下载离线地图
	public static String NOTICE_HIS_TABLE_NAME = "notice_his";
    public static String WATCH_TABLE_NAME = "watchs";
    public static String USERS_TABLE_NAME = "users";//
    public static String SUGGEST_TABLE_NAME = "suggests";
    public static String LOCATION_TABLE_NAME = "location_his";
    public static String TRACE_TABLE_NAME = "trace";
    public static String DATEPOINT_TABLE_NAME = "datepoint";
    public static String DIALBG_TABLE_NAME = "dialbg";
//    public static String CHAT_HIS_TABLE_NAME = "chat_his";
    
    //数据库field名
	public static final String FIELD_ID = "FIELD_ID"; //id
	public static final String FIELD_INFO = "FIELD_INFO"; //配置信息
	
	public static final String FIELD_FAMILY_ID = "family_id";
	public static final String FIELD_USER_ID = "uid";   
	public static final String FIELD_EID_ID = "eid";  
	public static final String FIELD_WATCH_ID = "watch_id";
	public static final String FIELD_NICK_NAME = "nickname";	
    public static final String FIELD_WATCH_NAME = "watchname";    	
	public static final String FIELD_RELATION = "relation";
    public static final String FIELD_BIRTHDAY = "birthday";  
    public static final String FIELD_SEX = "sex";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_ORIGNAL_VER ="orignal_ver";
    public static final String FIELD_CUR_VER = "cur_ver";
    public static final String FIELD_BT_MAC = "btmac";
    
    public static final String FIELD_SERVICE_TIMEOUT = "service_timeout";
    public static final String FIELD_LIGHT ="light";
    public static final String FIELD_VOLUME = "volume";
    public static final String FIELD_SILENT = "silent";
    public static final String FIELD_SILENT_TIMER = "silent_timer";
    public static final String FIELD_HAND = "hand";
    public static final String FIELD_QR = "qrcode";
    public static final String FIELD_POWER = "power";
    public static final String FIELD_POWER_LASTTIME ="power_lasttime";
	public static final String FIELD_LOCATION_LONGITUDE = "longitude";
	public static final String FIELD_LOCATION_LATITUEDE ="latitude";
	public static final String FIELD_DESCRIPTION ="description";
	public static final String FIELD_ACCURACY= "accuracy";
	public static final String FIELD_POI = "poi";
	public static final String FIELD_CITY = "city";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_MAPTYPE = "mapType";
	public static final String FIELD_LOCATION_TIME ="location_lasttime";
    public static final String FIELD_EXIPIRE_TIME ="expiretime";	
    public static final String FIELD_OFFLINE_CITY ="offline_city";
    public static final String FIELD_OFFLINE_CITY_DOWN_FLAG ="city_down_flag";
	public static final String FIELD_OFFLINE_CITY_COMPLETE_CODE ="city_complete_code";
	public static final String FIELD_BUSINESS = "business";
	public static final String FIELD_FLOOR = "floor";
	public static final String FIELD_INDOOR = "indoor";
	public static final String FIELD_BLDG = "bldg";
	public static final String FIELD_BDID = "bdid";

//    public static final String FIELD_CHAT_SRCID = "chat_srcid";          //发送者id       
//    public static final String FIELD_CHAT_DSTID = "chat_dstid";          //接收者id
//	public static final String FIELD_CHAT_AUDIOPATH = "chat_audio_path";      //语音路径
//	public static final String FIELD_CHAT_DATE = "chat_date";           //语音录制时间
//	public static final String FIELD_CHAT_DURATION = "chat_duration";          //语音时长，单位为'秒'
//	public static final String FIELD_CHAT_PLAYED = "chat_played";      //是否播放标志
//	public static final String FIELD_CHAT_IS_FROM = "chat_is_from";       //收到或者发出标志，true表示内容是接收的，false表示内容是发送的
	
	public static final String FIELD_MAC = "mac";
	public static final String FIELD_SSID = "ssid";
	public static final String FIELD_HEAD_ID = "head_id";
	public static final String FIELD_HEAD_PATH = "head_path";
	public static final String FIELD_LOCAL_PATH = "local_path";
	public static final String FIELD_FILE_NAME = "file_name";
	public static final String FIELD_SIZE = "size";	
    public static final String FIELD_TIME = "time";
	public static final String FIELD_ICCID = "iccid";
	public static final String FIELD_SIM_NO = "sim_no";
	public static final String FIELD_CELLPHONE = "cellphone";
	public static final String FIELD_XIAOMID = "xiaomiid";
    public static final String FIELD_IMEI = "imei";
    public static final String FIELD_ADMIN_USER = "admin_user";


    
 //历史消息相关
   public static final String FIELD_NOTICE_TYPE ="notice_type" ; // 
   public static final String FIELD_NOTICE_STATUS ="notice_status" ;    	  //
   public static final String FIELD_DATE_TIME ="notice_date_time" ; //日期标识
   
 //历史轨迹相关
   public static final String FIELD_DATE ="date" ; //日期
   public static final String FIELD_RECORD ="record" ;
   public static final String FIELD_DATE_NUM ="num" ;
   
//用户行为统计   
   public static final String FIELD_SUGGEST = "suggest";//反馈建议
  
   public static final String FIELD_STATE = "state";//安装状态

	public static final String FIELD_SIM_ACTIVIE_STATE = "active_status";
	public static final String FIELD_SIM_CERTI_STATE = "certi_status";

	//相册表盘
	public static final String FIELD_DIALBG_ID = "dialbg_id";
	public static final String FIELD_DIALBG_NAME = "dialbg_name";
	public static final String FIELD_DIALBG_PATH = "dialbg_path";
	public static final String FIELD_DIALBG_STATUS = "dialbg_status";
	public static final String FIELD_DIALBG_TIME = "dialbg_time";
	public static final String FIELD_DIALBG_URL = "dialbg_url";

//新增devictype字段保存
    public static final String FIELD_DEVICE_TYPE = "deviceType";
    public static final String FIELD_WATCH_SN = "sn";

	public MyDBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.i("create db");
		db.execSQL("CREATE TABLE "+CONFIG_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_ID+" INTEGER," +//cfgid
				FIELD_INFO+" TEXT" + //cfg的值
				");");

		db.execSQL("CREATE TABLE "+NOTICE_HIS_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_WATCH_ID+" TEXT," +
				FIELD_NOTICE_TYPE+" TEXT," +				
				FIELD_DATE_TIME+" TEXT" +				 
				");");
		db.execSQL("CREATE TABLE "+WATCH_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_WATCH_ID+" TEXT," +		        
                FIELD_NICK_NAME+" TEXT," +
                FIELD_FAMILY_ID+" TEXT," +
                FIELD_EID_ID+" TEXT," +                     
                FIELD_BIRTHDAY+" TEXT," +     
                FIELD_SEX+" TEXT," +    
                FIELD_LOCATION_LATITUEDE+" TEXT," +    
                FIELD_LOCATION_LONGITUDE+" TEXT," +    
                FIELD_CUR_VER+" TEXT," +    
                FIELD_BT_MAC+" TEXT," +                  
                FIELD_ORIGNAL_VER+" TEXT," +                   
                FIELD_HEIGHT+" TEXT," +    
                FIELD_WEIGHT+" TEXT," +    
                FIELD_EXIPIRE_TIME+" TEXT," +    
				FIELD_HEAD_ID+" INTEGER," +//
				FIELD_HEAD_PATH+" TEXT," + //
				FIELD_ICCID+" TEXT," + //
				FIELD_SIM_NO+" TEXT," + //
				FIELD_IMEI+" TEXT," + //
				FIELD_SIM_ACTIVIE_STATE+" INTEGER," +//
				FIELD_SIM_CERTI_STATE+" INTEGER," +//
				FIELD_DEVICE_TYPE+" TEXT," +  //新增deviceType保存
				FIELD_WATCH_SN+" TEXT," +  //新增sn保存
				FIELD_QR+" TEXT" + //
				");");	
		db.execSQL("CREATE TABLE "+USERS_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +				
                FIELD_USER_ID+" TEXT," +
                FIELD_NICK_NAME+" TEXT," +			
                FIELD_FAMILY_ID+" TEXT," +     
                FIELD_WATCH_ID+" TEXT," +          
                FIELD_EID_ID+" TEXT," +                      
                FIELD_WATCH_NAME+" TEXT," +                     
                FIELD_RELATION+" TEXT," +      
                FIELD_HEAD_ID+" INTEGER," +//
                FIELD_HEAD_PATH+" TEXT," + //
				FIELD_CELLPHONE+" TEXT," + //
				FIELD_XIAOMID+" TEXT" +//
				");");	
		db.execSQL("CREATE TABLE "+SUGGEST_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_SUGGEST+" TEXT" + //cfg的值
				");");
		
		db.execSQL("CREATE TABLE "+LOCATION_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_EID_ID+" TEXT," +		                          
                FIELD_LOCATION_LATITUEDE+" TEXT," +    
                FIELD_LOCATION_LONGITUDE+" TEXT," +                     
                FIELD_DESCRIPTION+" TEXT," +
                FIELD_ACCURACY+" TEXT," + 
                FIELD_LOCATION_TIME+" TEXT," +
                FIELD_POI+" TEXT,"+
                FIELD_CITY+" TEXT,"+
                FIELD_TYPE+" TEXT,"+
				FIELD_MAPTYPE+" TEXT,"+
				FIELD_BUSINESS+" TEXT,"+
				FIELD_FLOOR+" TEXT,"+
				FIELD_BLDG+" TEXT,"+
				FIELD_BDID+" TEXT,"+
				FIELD_INDOOR+" TEXT"+
				");");
		db.execSQL("CREATE TABLE "+TRACE_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_EID_ID+" TEXT," +
				FIELD_DATE+" TEXT," +
				FIELD_RECORD+" TEXT" +
				");");	
		db.execSQL("CREATE TABLE "+OFFLINE_MAP_CITY+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_OFFLINE_CITY+" TEXT," +
				FIELD_OFFLINE_CITY_DOWN_FLAG+" INTEGER,"+
				FIELD_OFFLINE_CITY_COMPLETE_CODE+" INTEGER"+
				");");
		
		db.execSQL("CREATE TABLE "+DATEPOINT_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_EID_ID+" TEXT," +
				FIELD_DATE+" TEXT," +
				FIELD_DATE_NUM+" INTEGER" +
				");");

		db.execSQL("CREATE TABLE "+DIALBG_TABLE_NAME+" (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				FIELD_EID_ID+" TEXT," +
				FIELD_DIALBG_ID+" TEXT," +
				FIELD_DIALBG_NAME+" TEXT," +
				FIELD_DIALBG_URL+" TEXT," +
				FIELD_DIALBG_STATUS+" INTEGER," +
				FIELD_DIALBG_TIME+" TEXT," +
				FIELD_DIALBG_PATH+" TEXT" +
				");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    
		LogUtil.i("onUpgrade db");
        if (oldVersion<8){
			db.execSQL("DROP TABLE IF EXISTS "+CONFIG_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+NOTICE_HIS_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+OFFLINE_MAP_CITY+";");
			db.execSQL("DROP TABLE IF EXISTS "+WATCH_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+SUGGEST_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+LOCATION_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+TRACE_TABLE_NAME+";");
			db.execSQL("DROP TABLE IF EXISTS "+DATEPOINT_TABLE_NAME+";");
			onCreate(db);
		}
		if (oldVersion<11){
			db.execSQL("DROP TABLE IF EXISTS "+LOCATION_TABLE_NAME+";");
			db.execSQL("CREATE TABLE "+LOCATION_TABLE_NAME+" (" +
					"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
					FIELD_EID_ID+" TEXT," +
					FIELD_LOCATION_LATITUEDE+" TEXT," +
					FIELD_LOCATION_LONGITUDE+" TEXT," +
					FIELD_DESCRIPTION+" TEXT," +
					FIELD_ACCURACY+" TEXT," +
					FIELD_LOCATION_TIME+" TEXT," +
					FIELD_POI+" TEXT,"+
					FIELD_CITY+" TEXT,"+
					FIELD_TYPE+" TEXT,"+
					FIELD_MAPTYPE+" TEXT,"+
					FIELD_BUSINESS+" TEXT,"+
					FIELD_FLOOR+" TEXT,"+
					FIELD_BLDG+" TEXT,"+
					FIELD_BDID+" TEXT,"+
					FIELD_INDOOR+" TEXT"+
					");");
		}

	}
}
