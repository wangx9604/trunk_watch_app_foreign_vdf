/**
 * Creation Date:2015-2-27
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun.utils;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchDownloadBean;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-2-27
 */
public class CloudBridgeUtil {
    public static final String PROTOCOL_FOBIDDEN = "null";  //j禁用协议版本号 sw102禁用
    //  public static final String SW102_PROTOCOL_NUM = "00010000";   // 102不启用协议版本号
    public static final String SW_PROTOCOL_NUM = Const.SW_PROTOCOL_NUM;   //imibaby和xiaoxun协议版本号不一致，放在了Const里面

    public static final int ERROR_CODE_COMMOM_UNKNOWN_MESSAGE = -11;
    public static final int ERROR_CODE_COMMOM_GENERAL_EXCEPTION = -12;
    public static final int ERROR_CODE_COMMOM_MESSAGE_ILLEGAL = -13;
    public static final int ERROR_CODE_COMMOM_SESSION_ILLEGAL = -14;

    public static final int SUB_ACTION_HEALTH_MONITOR = 840;//健康监测消息
    public static final int SUB_ACTION_HEALTH_OUTSIDE_UPLOAD = 368; //请求手表上报户外活动数据消息

    public static final int SUB_ACTION_HEART_RATE_UPLOAD_MESSAGE = 318;
    public static final String MOTION_SCHEDULE_LIST = "sport_plan_list";
    public static final String MOTION_TOTAL_SCORE = "sport_plan_total_integral";
    public static final String MOTION_DAILY_SCORE = "sport_plan_daily_integral";

    public static final String KEY_DIAL_STORE_PUSH_ONOFF = "dialstore_push_onoff";
    //上下学守护
    public static final String KEY_NAME_SCHOOL_GUARD_LIST = "GuardList";
    public static final String KEY_NAME_SCHOOL_GUARD_ARRIVETIME = "tscl";
    public static final String KEY_NAME_SCHOOL_GUARD_LEAVETIME = "lscl";
    public static final String KEY_NAME_SCHOOL_GUARD_ARRIVETIME_INTEVAL = "tscl_fl";
    public static final String KEY_NAME_SCHOOL_GUARD_LEAVETIME_INTEVAL = "lscl_fl";
    public static final String KEY_NAME_SCHOOL_GUARD_HOLIDAY_ONOFF = "holiday_onoff";
    public static final String KEY_NAME_GUARD = "Guard";
    public static final String KEY_NAME_LOCATION = "Location";
    public static final String KEY_NAME_GUARD_GTYPE = "GType";
    public static final String KEY_NAME_TITLE = "title";

    //心率
    public static final String KEY_NAME_HEART_RATE_ALL_DAY_ON = "heart_on";
    public static final String KEY_NAME_HEART_RATE_WARNING_ONOFF = "heart_notify_onoff";
    public static final String KEY_NAME_HEART_RATE_WARNING_VALUE = "heart_notify_temp";
    public static final String KEY_NAME_HEART_RATE_WARNING_LIMIT_ONOFF = "heart_notify_limit_onoff";
    public static final String KEY_NAME_HEART_RATE_WARNING_LIMIT_VALUE = "heart_notify_limit_temp";
    public static final String KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_ONOFF = "heart_notify_high_onoff";
    public static final String KEY_NAME_HEART_RATE_REST_HIGH_LIMIT_VALUE = "heart_notify_high_temp";
    public static final String KEY_NAME_HEART_RATE_REST_LOW_LIMIT_ONOFF = "heart_notify_low_onoff";
    public static final String KEY_NAME_HEART_RATE_REST_LOW_LIMIT_VALUE = "heart_notify_low_temp";

    public static final String KEY_FOCUS_TIME_SETTING = "silence_list_new";
    public static final String KEY_FOCUS_TIME_CALL_ONOFF = "call_onoff";
    public static final String KEY_FOCUS_TIME_TIMES = "times";
    public static final String KEY_FOCUS_TIME_START = "start";
    public static final String KEY_FOCUS_TIME_END = "end";
    public static final String KEY_FOCUS_TIME_DEF = "def";
    public static final String KEY_FOCUS_TIME_ONOFF_STATUS = "onoff_status";
    public static final String MOTION_MONTH_VALUE = "motion_month_value";
    public static final String MOTION_MONTH_SCORE_VALUE = "motion_month_score_value";

    public static final String KEY_WATCH_CONTROL_CONTROL_STATUS = "control_status";
    public static final String KEY_WATCH_CONTROL_WEAR_STATUS = "wear_status";
    public static final String KEY_WATCH_CONTROL_APP_WHITELIST = "control_app_whitelist";
    public static final String KEY_WATCH_SCHEDULE_LIST_NAME = "schedule_list_name";
    public static final String KEY_WATCH_CONTROL_LOCK_TIME_LIST = "lock_time_list";
    public static final String KEY_WATCH_CONTROL_USE_LIMIT = "use_limit";
    public static final String KEY_WATCH_CONTROL_USE_TIME = "use_time";
    public static final String KEY_WATCH_CONTROL_TIME_LIMIT = "time_limit";
    public static final String KEY_SUPER_POWER_SAVING = "super_power_onoff";

    //电子围栏相关
    public static final String KEY_NAME_EFID = "EFID";
    public static final String KEY_NAME_EFID_DESC = "Desc";
    public static final String KEY_NAME_EFID_RADIUS = "Radius";
    //设置安全区域相关
    public static final String KEY_NAME_EFID_AREA = "efid";
    public static final String KEY_AREA_TYPE = "etype";
    public static final String KEY_AREA_NAME = "efname";
    public static final String KEY_AREA_DESC = "desc";
    public static final String KEY_AREA_POINTS = "points";
    public static final String KEY_AREA_EVENT = "event";
    //security
    public static final int ERROR_CODE_SECURITY_PASSWORD_INVALID = -101;
    public static final int ERROR_CODE_SECURITY_PASSWORD_EMPTY = -102;
    public static final int ERROR_CODE_SECURITY_USER_NOT_EXIST = -103;
    public static final int ERROR_CODE_SECURITY_USER_ALREADY_EXIST = -104;
    public static final int ERROR_CODE_SECURITY_USER_EMAIL_ALREADY_USED = -105;
    public static final int ERROR_CODE_SECURITY_USER_PHONE_ALREADY_USED = -106;
    public static final int ERROR_CODE_SECURITY_SEND_EMAIL_FAILED = -107;
    public static final int ERROR_CODE_SECURITY_SEND_SMS_FAILED = -108;
    public static final int ERROR_CODE_SECURITY_ACCOUNT_HAS_BEEN_ACTIVATED = -109;
    public static final int ERROR_CODE_SECURITY_ACTIVATIONCODE_ILLEGAL = -110;
    public static final int ERROR_CODE_SECURITY_ACCOUNT_HAS_NOT_BEEN_ACTIVATED = -111;
    public static final int ERROR_CODE_E2G_OFFLINE = -160;
    public static final int ERROR_CODE_E2E_OFFLINE = -160;
    public static final String E2C_PL_KEY_TYPE_VIDEOCALL = "videoCall";  //视频通话

    public static final int SUB_ACTION_VALUE_NAME_VIDEO_CALL_SWITCH_WATCHCAMERA = 118; //切换手表摄像头

    public static final String REMOTE_LOSS = "remote_loss";
    public static final String KEY_NAME_LOW_POWER_DISABLE = "lowpower_disable";

    public static final int RC_SUCCESS = 1;
    public static final int RC_HALF_SUCCESS = 0;
    public static final int CID_AREA_SET = 51051;
    public static final int CID_AREA_GET = 51061;
    public static final int CID_AREA_DELETE = 51071;
    // set & get wifi
    public static final int CID_SET_WIFI_LIST = 80191;
    public static final int CID_SET_WIFI_LIST_DOWN = 80192;
    public static final int CID_GET_WIFI_LIST = 80211;
    public static final int CID_GET_WIFI_LIST_DOWN = 80212;
    public static final int CID_SET_CALENDAR = 80221;
    public static final int CID_SET_CALENDAR_DOWN = 80222;
    public static final int CID_GET_ALL_CALENDAR = 80321;
    public static final int CID_GET_All_CALENDAR_DOWN = 80322;
    //Family WiFi
    public static final int CID_GET_FAMILY_WIFI = 52051;
    public static final int CID_GET_FAMILY_WIFI_DOWN = 52052;
    public static final int CID_SET_FAMILY_WIFI = 52041;
    public static final int CID_SET_FAMILY_WIFI_DOWN = 52042;
    public static final String KEY_GUARD_ONOFF_LIST = "efence_onoff_list";
    public static final String KEY_GUARD_EFFENCES = "efences";
    public static final String KEY_GUARD_EFFENCE = "efence";
    public static final String KEY_GUARD_LIST_NEW = "guard_list_new";
    //本地定义的错误码
    public static final int RC_TIMEOUT = -200;  //
    public static final int RC_NETERROR = -201;
    public static final int RC_SOCKET_NOTREADY = -202;
    public static final int RC_NOT_LOGIN = -203;
    public static final int RC_NULL = -204;
    public static final int RC_ACCOUNT_NOT_FOUND = -103;
    //定义CID
    //手表测试字段
    public static final int CID_WATCH_LOGIN = 10211;
    public static final int WATCH_TYPE = 200;
    public static final int CID_WATCH_LOGIN_RESP = 10212;
    //账户管理
    public static final int CID_QUERY_MYGROUPS = 20091;  //群组查询
    public static final int CID_QUERY_MYGROUPS_RESP = 20092;
    public static final int CID_USER_LOGIN = 10011;      //登陆
    public static final int CID_USER_LOGIN_RESP = 10012;
    public static final int CID_USER_LOGOUT = 10021;      //登陆
    public static final int CID_USER_LOGOUT_RESP = 10022;
    public static final int CID_REGISTER = 10031;
    public static final int CID_REGISTER_RESP = 10032;
    public static final int CID_CAPTCHA_SMS = 10111;
    public static final int CID_CAPTCHA_SMS_RESP = 10112;
    public static final int CID_RESET_PSW_BY_Captcha = 10181;
    public static final int CID_RESET_PSW_BY_Captcha_RESP = 10182;
    public static final int CID_USER_SET = 10051;     //设置用户的家庭关系等Custom字段
    public static final int CID_USER_SET_RESP = 10052;
    public static final int CID_USER_GET = 10061;
    public static final int CID_USER_GET_RESP = 10062;
    public static final int CID_THIRD_REG = 10081;  //设置第三方登陆的注册信息
    public static final int CID_THIRD_REG_RESP = 10082;
    public static final int CID_DEVICE_SET = 10251;  //设置设备的Custom字段
    public static final int CID_DEVICE_SET_RESP = 10252;
    public static final int CID_DEVICE_GET = 10261;  //获取设备的Custom字段
    public static final int CID_DEVICE_GET_RESP = 10262;
    //群组关系类
    public static final int CID_QUERY_EID_BY_SN = 70041;
    public static final int CID_QUERY_EID_BY_SN_RESP = 70042;
    public static final int CID_QUERY_EID_BY_ICCID = 70051; //通过iccid查询eid
    public static final int CID_QUERY_EID_BY_ICCID_RESP = 70052;
    public static final int CID_SET_OPPO_REGISTID = 80081;
    public static final int CID_SET_OPPO_REGISTID_RESP = 80082;

    public static final int CID_QUERY_GROUPS_BY_EID = 20081; //查询群组信息
    public static final int CID_QUERY_GROUPS_BY_EID_RESP = 20082;
    public static final int CID_QUERY_ENDPOINTS_BY_GID = 20071;  //通过GId查询终端
    public static final int CID_QUERY_ENDPOINTS_BY_GID_RESP = 20072;

    public static final int CID_DEL_GROUP = 20021;
    public static final int CID_DEL_GROUP_RESP = 20022;
    public static final int CID_SET_GROUP = 20031;
    public static final int CID_SET_GROUP_RESP = 20032;
    public static final int CID_ADD_EID_TO_GROUP = 20051;
    public static final int CID_ADD_EID_TO_GROUP_RESP = 20052;
    public static final int CID_REMOVE_EID_FROM_GROUP = 20061;
    public static final int CID_REMOVE_EID_FROM_GROUP_RESP = 20062;
    public static final int CID_MERGE_GROUPS = 20101;
    public static final int CID_MERGE_GROUPS_RESP = 20102;
    public static final int CID_MERGE_INFO_FIRST_GROUP = 20111;
    public static final int CID_MERGE_INFO_FIRST_GROUP_RESP = 20112;

    public static final int CID_REQ_JOIN_WATCH_GROUP = 20171;
    public static final int CID_REQ_JOIN_WATCH_GROUP_RESP = 20172;

    //多用途承载不同内容的cids
    public static final int CID_E2E_UP = 30011;
    public static final int CID_E2E_DOWN = 30012;

    public static final int CID_E2G_UP = 30031;
    public static final int CID_E2G_DOWN = 30032;

    public static final int CID_E2C_UP = 40111;
    public static final int CID_E2C_DOWN = 40112;

    public static final int CID_C2E = 40121;
    public static final int CID_C2E_RESP = 40122;

    public static final int CID_C2E_GET_MESSAGE = 40151;
    public static final int CID_C2E_GET_MESSAGE_RESP = 40152;

    public static final int CID_GET_PRIVATE_CHAT = 40181;
    public static final int CID_GET_PRIVATE_CHAT_RESP = 40182;

    public static final int CID_GET_PRIVATE_CHAT_MULTI_PKT = 40191;
    public static final int CID_GET_PRIVATE_CHAT_MULTI_PKT_RESP = 40192;

    public static final int CID_GET_GROUP_ALL_CHAT = 40211;
    public static final int CID_GET_GROUP_ALL_CHAT_RESP = 40212;
    //新的手表定位通知
    public static final int CID_C2E_NEW_LOCATION_NOTIFY = 50112;

    // 跟踪模式定位通知
    public static final int CID_C2E_TRACK_LOCATION_NOTIFY = 50122;
    public static final int CID_MAPSET = 60031;
    public static final int CID_MAPSET_RESP = 60032;

    //数据迁移
    public static final int CID_MIGRATION = 60041;
    public static final int CID_MIGRATION_RESP = 60042;


    public static final int CID_MAPGET = 60021;
    public static final int CID_MAPGET_RESP = 60022;

    public static final int CID_MAPGET_MGET = 60051;
    public static final int CID_MAPGET_MGET_RESP = 60052;

    public static final int CID_MAPSET_MSET = 60061;
    public static final int CID_MAPSET_MSET_RESP = 60062;

    public static final int CID_DEVICE_OFFLINE_STATE = 60071;
    public static final int CID_DEVICE_OFFLINE_STATE_RESP = 60072;

    public static final int CID_DEVICE_SIM_CHANGE = 60151;
    public static final int CID_DEVICE_SIM_CHANGE_RESP = 60152;

    public static final int CID_E2C4_DEVICE_MSG_REQ = 70061;
    public static final int CID_E2C4_DEVICE_MSG_RESP = 70062;

    public static final int CID_STATE_MSG_REQ = 70071;
    public static final int CID_STATE_MSG_RESP = 70072;

    public static final int CID_E2C4_DEVICE_LONGTIME_MSG_REQ = 70091;
    public static final int CID_E2C4_DEVICE_LONGTIME_MSG_RESP = 70092;

    //videocall
    public static final int CID_GET_AGORA_TOKEN = 70311;
    public static final int CID_GET_AGORA_TOKEN_DOWN = 70312;
    public static final int CID_UPLOAD_VIDEOCALL_TIME = 70411;
    public static final int CID_UPLOAD_VIDEOCALL_TIME_DOWN = 70412;

    public static final int CID_UPLOAD_NOTICE = 70081;
    public static final int CID_UPLOAD_NOTICE_RESP = 70082;

    //其他单一用途的cids
    //地图类
    public static final int CID_LOCATION = 50011;
    public static final int CID_LOCATION_RESP = 50012;

    public static final int CID_SET_LOCATION_TYPE = 54021;  //设置设备定位方式
    public static final int CID_SET_LOCATION_TYPE_RESP = 54022;

    public static final int CID_E2C_LIST_KEY_MSG = 40131;
    public static final int CID_E2C_LIST_KEY_MSG_RESP = 40132;

    public static final int CID_RETRIEVE_TRACE_DATA = 50031;
    public static final int CID_RETRIEVE_TRACE_DATA_RESP = 50032;

    public static final int CID_TRACE_COUNTER_DATA = 50041;
    public static final int CID_TRACE_COUNTER_DATA_RESP = 50042;

    public static final int CID_TRACE_DATA_BY_DAY = 50051;
    public static final int CID_TRACE_DATA_BY_DAY_RESP = 50052;

    public static final int CID_EFENCE_GET = 51011;
    public static final int CID_EFENCE_GET_RESP = 51012;
    public static final int CID_EFENCE_SET = 51021;
    public static final int CID_EFENCE_SET_RESP = 51022;
    public static final int CID_EFENCE_DEL = 51031;
    public static final int CID_EFENCE_DEL_RESP = 51032;

    public static final int CID_GET_PERMANENT_REQ = 52011;
    public static final int CID_GET_PERMANENT_RESP = 52012;
    public static final int CID_SET_PERMANENT_REQ = 52021;
    public static final int CID_SET_PERMANENT_RESP = 52022;
    public static final int CID_DEL_PERMANENT_REQ = 52031;
    public static final int CID_DEL_PERMANENT_RESP = 52032;

    //feedback
    public static final int CID_FEEDBACK = 90011;
    public static final int CID_FEEDBACK_RESP = 90012;
    //update

    //查询手表话费
    public static final int CID_REQ_TEL_COST = 60171;
    public static final int CID_REQ_TEL_COST_RESP = 60172;

    //C2eCounterMessage
    public static final int CID_C2E_COUNTER = 40141;
    public static final int CID_C2E_COUNTRE_RESP = 40142;
    public static final int CID_GETTIME = 70021;
    public static final int CID_GETTIME_RESP = 70022;

    public static final int CID_KICK_DOWN = 79002;
    public static final int CID_CHECK_SESSION = 70011;
    public static final int CID_CHECK_SESSION_DOWN = 70012;

    public static final int CID_MY_PING = 1;//用hello代替ping
    //sim查询和操作
    public static final int CID_SIM_OP_SEARCH = 60111;
    public static final int CID_SIM_OP_SEARCH_RSP = 60112;
    public static final int CID_SIM_OP_CHANGE = 60121;
    public static final int CID_SIM_OP_CHANGE_RSP = 60122;
    public static final int CID_SIM_OP_NOTICE = 60132;
    //trakc
    public static final int CID_TRACE_TO_SET = 53021;
    public static final int CID_TRACE_TO_SET_RESP = 53022;
    public static final int CID_TRACE_TO_GET_STATUS = 53011;
    public static final int CID_TRACE_TO_GET_STATUE_RESP = 53012;
    //获取认证参数密文
    public static final int CID_QUERY_PARAM_CIPHER = 60141;
    public static final int CID_QUERY_PARAM_CIPHER_RESP = 60142;
    public static final String KEY_NAME_PARAM = "param";
    public static final String KEY_NAME_MERCHANT_ID = "merchant_id";
    public static final String KEY_NAME_REQUESTDATA = "requestData";

    //认证流程统计

    public static final int CID_CERTI_SIM_STAT = 69011;//认证流程统计

    public static final int CID_OPT_CONTACT_REQ = 70131;
    public static final int CID_OPT_CONTACT_RESP = 70132;

    public static final int CID_GET_CONTACT_REQ = 70141;
    public static final int CID_GET_CONTACT_RESP = 70142;

    public static final int CID_TTS_REQ = 70151;
    public static final int CID_TTS_RESP = 70152;

    public static final int CID_OPT_WATCH_DOWNLOAD_LIST = 70161;
    public static final int CID_OPT_WATCH_DOWNLOAD_LIST_RESP = 70162;
    public static final int CID_GET_WATCH_DOWNLOAD_LIST = 70171;
    public static final int CID_GET_WATCH_DOWNLOAD_LIST_RESP = 70172;
    public static final int CID_GET_WATCH_MULT_SELECT = 70181;
    public static final int CID_GET_WATCH_MULT_SELECT_RESP = 70182;

    public static final int CID_GET_FUNCTION_HIGHPOWER_STATE = 60091;
    public static final int CID_GET_FUNCTION_HIGHPOWER_STATE_RESP = 60092;

    //MiOauth
    public static final int CID_MIOAUTH_REPORT_CODE = 60181;
    public static final int CID_MIOAUTH_GET_TOKEN = 60211;

    public static final int CID_GET_NOTICE_SETTING = 80121;
    public static final int CID_GET_NOTICE_SETTING_RESP = 80122;

    public static final int CID_SET_NOTICE_SETTING = 80111;
    public static final int CID_SET_NOTICE_SETTING_RESP = 80112;

    //Dialbg
    public final static int CID_DIALBG_OPERATE = 80161;
    public final static int CID_DIALBG_OPERATE_RESP = 80162;
    public final static int CID_DIALBG_GETLIST = 80171;
    public final static int CID_DIALBG_GETLIST_RESP = 80172;
    public final static int CID_SET_LANG = 80071;
    public final static int CID_SET_LANG_RESP = 80072;

    public static final int CID_GET_WATCH_NAVI_STATE = 55041;
    public static final int CID_GET_WATCH_NAVI_STATE_RESP = 55042;

    // 应用商店cid
    public static final int CID_SET_INSTALL_APP_LIST = 80131;
    public static final int CID_SET_INSTALL_APP_LIST_DOWN = 80132;
    public static final int CID_GET_INSTALL_APP_LIST = 80141;
    public static final int CID_GET_INSTALL_APP_LIST_DOWN = 80142;

    //登陆混淆key值
    public static final String ACCESS_KEY = "74CECB85AE17BB85C56FFA91FE33F6E0";//云桥分配的
    //e2c和c2e消息内容中的相关参数
    public static final String PREFIX_GP_E2C_MESSAGE = "GP/";
    public static final String PREFIX_EP_E2C_MESSAGE = "EP/";
    public static final String E2C_SERVER_SET_TIME = "#TIME#";
    public static final String E2C_SPLIT_MEG = "/MSG/";
    public static final String E2C_SPLIT_NOTICE = "/GPMSG/";
    public static final String E2C_SPLIT_ALERT = "/WARNING/";
    public static final String E2C_SPLIT_ALARM_RING = "/ALARM_RING/";
    public static final String E2C_SPLIT_ALARM_RING_1 = "/ALARM_RING/1";
    public static final String E2C_SPLIT_ALARM_RING_2 = "/ALARM_RING/2";
    public static final String E2C_SPLIT_ALARM_RING_3 = "/ALARM_RING/3";
    public static final String E2C_SPLIT_HEADIMG = "/HEAD/";
    public static final String E2C_SPLIT_SECURITYPREVIEW = "/PREVIEW/";
    public static final String E2C_PL_KEY = "Key";

    public static final int E2C_SPLIT_MEG_INT = 1;
    public static final int E2C_SPLIT_NOTICE_INT = 2;
    public static final int E2C_SPLIT_ALERT_INT = 3;

    public static final String E2C_PL_KEY_TYPE = "Type";         //content类型
    public static final String E2C_PL_KEY_EID = "EID";           //e2c消息生产者的eid
    public static final String E2C_PL_KEY_VIDEOCALL_TYPE = "callType";  //视频通话类型
    public static final String E2C_PL_KEY_DURATION = "Duration"; //语音消息持续时间
    public static final String E2C_PL_KEY_CONTENT = "Content";   //content
    public static final String E2C_PL_KEY_TYPE_VOICE = "voice";  //语音消息
    public static final String E2C_PL_KEY_TYPE_TEXT = "text";    //文字消息
    public static final String E2C_PL_KEY_TYPE_EMOJI = "emoji";    //文字消息
    public static final String E2C_PL_KEY_TYPE_ALARM_VOICE = "alarmvoice";  //闹钟语音消息

    public static final String E2C_PL_KEY_NOTICE_TYPE = "Type";    //通知类消息类型,统一即可，有需要再区分

    //离线消息类型key值定义
    public static final String OFFLINE_MSG_TYPE_STEPS = "steps"; //目标步数完成
    public static final String OFFLINE_MSG_TYPE_BATTERY = "battery"; //低电量提醒
    public static final String OFFLINE_MSG_TYPE_SOSLOC = "sosLoc"; //sos位置信息
    public static final String OFFLINE_MSG_TYPE_SMS = "sms"; //设备收到的短信消息
    public static final String OFFLINE_MSG_TYPE_SOS = "sos"; //sos语音消息
    public static final String OFFLINE_MSG_TYPE_RECORD = "record"; //监听消息
    public static final String OFFLINE_MSG_TYPE_VOICE = "voice"; //正常语音消息
    public static final String OFFlINE_MSG_TYPE_SECURITYAREA = "securityArea";//进出安全区域消息
    public static final String OFFLINE_MSG_TYPE_GROUPBODY = "groupBody"; //家庭成员变化信息
    public static final String OFFLINE_MSG_TYPE_SIMCHANGE = "simChange"; //sim卡变更消息
    public static final String OFFLINE_MSG_TYPE_DOWNLOAD = "download"; //手表下载文件完成消息
    public static final String OFFLINE_MSG_TYPE_STEPSRANKS = "stepsRank"; //手表下载文件完成消息
    public static final String OFFLINE_MSG_TYPE_IMAGE = "image"; //手表下载文件完成消息
    public static final String OFFLINE_MSG_TYPE_VIDEO = "video";
    public static final String OFFLINE_MSG_TYPE_EMOJI = "emoji";
    public static final String OFFLINE_MSG_TYPE_PHOTO = "photo";
    public static final String OFFLINE_MSG_TYPE_CLOUD_SPACE = "cloudspace";
    public static final String OFFLINE_MSG_TYPE_FLOWMETER = "flowmeter";
    public static final String OFFLINE_MSG_TYPE_OTA_UPGRADE = "ota_upgrade";
    public static final String OFFLINE_MSG_TYPE_EFENCE = "EFENCE";
    public static final String OFFLINE_MSG_TYPE_NAVIGATION = "navigation";
    public static final String OFFLINE_MSG_TYPE_OTA_UPGRADE_EX = "ota_upgrade_ex";
    public static final String OFFLINE_MSG_TYPE_APPSTORE = "appstore";
    public static final String OFFLINE_MSG_TYPE_STORY = "story_dl";
    public static final String OFFLINE_MSG_TYPE_SYSTEM = "system";

    public static final int E2C_PL_KEY_NOTICE_TYPE_JOIN_GROUP = 1;  //加入群组通知
    public static final int E2C_PL_KEY_NOTICE_TYPE_MERGE_GROUP = 3;  //加入群组通知
    public static final int E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP = 2;    //离开群组通知
    public static final int E2C_PL_KEY_NOTICE_TYPE_GET_ADMIN = 4;    //成为管理员

    public static final String E2C_PL_KEY_WARNNING_TYPE_SOS = "sos";    //告警类消息类型SOS

    public static final String KEY_BIND_PUSH_TYPE = "pushType";

    public static final String KEY_NAME_SUBTYPE = "subtype";

    /*e2c内容 pl:
        "GP/GGIIDD/WARNING/20140829010102000^" : {
        "Type":"WarningTypePower",  //低电报警
        "Eid":"xxxxxxxxxxx",
        "Level":"1"          // 当前电量值
        "Timestamp":"201501021314356"  // 时间戳
     }*/

    /*e2g通知内容pl:
     * {
	    "sub_action":160,  //低电报警
	    "Level":"1"          // 当前电量值
	    "Timestamp":"201501021314356"  // 时间戳
		"Eid":"xxxxxxxxxxxx"
	    "Key":"xxxxxxxxxxxxxxxxxxx"
	 	}*/
    public static final String E2C_PL_KEY_WARNNING_TYPE_POWER = "WarningTypePower";    //告警类消息类型 低电量

    public static final String[] TEST_EID = {"123456789132456789"};
    //定义CLOUD_KEY
    //cloud bridge message公共的key值
    public static final String KEY_NAME_VERSION = "Version";
    public static final String PL_KEY_EID = "Eid";
    public static final String KEY_NAME_EID = "EID";
    public static final String KEY_NAME_EIDS = "EIDs";
    public static final String KEY_NAME_CID = "CID";
    public static final String KEY_NAME_SN = "SN";
    public static final String KEY_NAME_SID = "SID";
    public static final String KEY_NAME_PL = "PL";
    public static final String KEY_NAME_RC = "RC";
    public static final String KEY_NAME_CP = "CP";
    public static final String KEY_NAME_RN = "RN";
    public static final String KEY_NAME_TEID = "TEID";
    public static final String KEY_NAME_TGID = "TGID";
    public static final String KEY_NAME_PERSISTENT = "Persistent";
    public static final String KEY_NAME_EIDLIST = "EIDList";
    public static final String KEY_SET_TYPE = "settype";
    public static final String KEY_NAME_RN_INFO = "info";
    public static final String KEY_NAME_REGISTID = "regId";
    public static final String KEY_SET_LANG = "lang";
    public static final String KEY_FAMILY_WIFIS = "WIFIS";
    //不同业务需要的的key
    //user相关和
    public static final String KEY_NAME_TOKEN = "Token";
    public static final String KEY_NAME_NAME = "Name";
    public static final String KEY_NAME_PASSWORD = "Password";
    public static final String KEY_NAME_UNIONID = "Uuid";
    public static final String KEY_NAME_TYPE = "Type";
    public static final String KEY_NAME_INFO = "info";
    public static final String KEY_NAME_XIAOMIID = "XiaomiId";
    public static final String KEY_NAME_MARK_KEY = "markKey";
    public static final String KEY_NAME_ADS = "ads";

    public static final String KEY_NAME_CAPTCHA = "Captcha";
    public static final String KEY_NAME_ACCESSKEY = "AccessKey";
    public static final String KEY_NAME_NICKNAME = "NickName";
    public static final String KEY_NAME_CELLPHONE = "CellPhone";
    public static final String KEY_NAME_ISACTIVATED = "IsActivated";
    public static final String KEY_NAME_ALIAS = "Alias";
    public static final String KEY_NAME_EMAIL = "Email";
    public static final String KEY_NAME_CREATETIME = "CreateTime";
    public static final String KEY_NAME_SMS = "SMS";
    public static final String KEY_NAME_STUB = "Stub";
    public static final String KEY_NAME_AGINGTIME = "AgingTime";
    public static final String KEY_NAME_FILE_LIST = "filelist";
    //groups 相关
    public static final String KEY_NAME_GID = "GID";
    public static final String KEY_NAME_DESCRIPTION = "Description";
    public static final String KEY_NAME_ENDPOINTS = "Endpoints";
    public static final String KEY_NAME_ADMIN_EID = "AdminEid";
    //插一个和groups业务有关的value值
    public static final int VALUE_TYPE_ENDPOINT_USER = 100;
    public static final int VALUE_TYPE_ENDPOINT_WATCH = 200;

    public static final int VALUE_TYPE_APP_ANDROID = 102;
    //WATCH 相关
    public static final String KEY_NAME_SERINALNO = "SerialNo";
    public static final String KEY_NAME_ICCID = "Iccid";
    public static final String KEY_NAME_SIM_NO = "SimNo";
    public static final String KEY_NAME_WATCH_IMEI = "Imei";
    public static final String KEY_NAME_WATCH_IMSI = "Imsi";
    public static final String KEY_NAME_ICCID_MD5 = "IccidMd5";

    public static final String KEY_NAME_WATCH_QR_TYPE = "QRType";
    public static final String KEY_NAME_WATCH_ICCID_ENCRYPT = "IccidEncrypt";
    public static final String KEY_NAME_WATCH_IMSI_ENCRYPT = "ImsiEncrypt";
    public static final String KEY_NAME_WATCH_IMEI_ENCRYPT = "ImeiEncrypt";
    public static final String KEY_NAME_WATCH_ICCID = "Iccid";
    //  public static final String KEY_NAME_WATCH_MSISDN = "MSISDN";

    public static final String KEY_NAME_XMPL = "xmpl";
    public static final String KEY_NAME_SEX = "Sex";
    public static final String KEY_NAME_DATE_OF_BIRTH = "DateOfBirth";
    public static final String KEY_NAME_VERSION_CUR = "VersionCur";
    public static final String KEY_NAME_VERSION_TARGET = "VersionTarget";
    public static final String KEY_NAME_VERSION_ORG = "VersionOrg";
    public static final String KEY_NAME_CREATE_TIME = "CreateTime";
    public static final String KEY_NAME_EXPIRE_TIME = "ExpireTime";
    public static final String KEY_NAME_LAT = "Lat";   //维度
    public static final String KEY_NAME_LNG = "Lng";   //经度
    public static final String KEY_NAME_LATBD = "bdLat";   //维度
    public static final String KEY_NAME_LNGBD = "bdLng";   //经度
    public static final String KEY_NAME_WEIGHT = "Weight";   //体重
    public static final String KEY_NAME_HEIGHT = "Height";   //身高
    public static final String KEY_NAME_BT_MAC = "BtMac";
    public static final String KEY_NAME_PHONE_BT_MAC = "PhoneBtMac"; // 手机BT地址

    //location 相关
    public static final String KEY_NAME_ACCESSTYPE = "accesstype";
    public static final String KEY_NAME_SMAC = "smac";
    public static final String KEY_NAME_SERVERIP = "serverip";
    public static final String KEY_NAME_CMDA = "cdma";
    public static final String KEY_NAME_IMSI = "imsi";
    public static final String KEY_NAME_IMEI = "imei";
    public static final String KEY_NAME_GPS = "gps";
    public static final String KEY_NAME_NETWORK = "network";
    public static final String KEY_NAME_TEL = "tel";
    public static final String KEY_NAME_BTS = "bts";
    public static final String KEY_NAME_NEARBTS = "nearbts";
    public static final String KEY_NAME_MMAC = "mmac";
    public static final String KEY_NAME_MAC = "macs";
    public static final String KEY_NAME_TIMESTAMP = "timestamp";
    public static final String KEY_NAME_LOCAITON_TYPE = "type";
    public static final String KEY_NAME_LOCAITON_IMEI = "imei";
    public static final String KEY_NAME_LOCAITON_LOCATION = "location";
    public static final String KEY_NAME_LOCAITON_RADIUS = "radius";
    public static final String KEY_NAME_LOCAITON_DESC = "desc";
    public static final String KEY_NAME_LOCAITON_COUNTTRY = "country";
    public static final String KEY_NAME_LOCAITON_PROVINCE = "province";
    public static final String KEY_NAME_LOCAITON_CITY = "city";
    public static final String KEY_NAME_LOCAITON_CITYCODE = "citycode";
    public static final String KEY_NAME_LOCAITON_ADCODE = "adcode";
    public static final String KEY_NAME_LOCAITON_ROAD = "road";
    public static final String KEY_NAME_LOCAITON_POI = "poi";
    public static final String KEY_NAME_LOCATION_MAPTYPE = "mapType";
    public static final String KEY_NAME_SOS = "SOS";
    public static final String KEY_NAME_LOCTYPE = "loctype";
    public static final String KEY_NAME_MAPTYPE = "mapType";
    public static final String KEY_NAME_REGION = "region";

    public static final String KEY_NAME_BUSINESS = "business";
    public static final String KEY_NAME_FLOOR = "floor";
    public static final String KEY_NAME_INDOOR = "indoor";
    public static final String KEY_NAME_BLDG = "bldg";
    public static final String KEY_NAME_BDID = "bdid";

    //voice 相关
    public static final String KEY_NAME_C2E_BEGIN = "KeyBegin";
    public static final String KEY_NAME_C2E_END = "KeyEnd";
    //setting 相关
    public static final String KEY_NAME_SETTING_LEVEL = "Level";

    public static final String KEY_NAME_SETTING_MODE = "mode_value";

    // 搜索手表好友相关
    public static final String KEY_NAME_WATCH_LIST = "list";
    public static final String KEY_NAME_WATCH_NICKNAME = "nickName";
    public static final String KEY_NAME_WATCH_HEAD = "head";

    //手表状态 相关
    public static final String KEY_NAME_WATCH_STATE = "watch_status";
    public static final String KEY_NAME_WATCH_STATE_TIMESTAMP = "timestamp";
    //    public static final String KEY_NAME_WATCH_STATE_EID = "Eid";
    public static final String KEY_NAME_WATCH_VERSION = "watch_version";
    public static final String KEY_NAME_WATCH_NET_STATE = "net_stat";

    public static final String KEY_NAME_SOS_TYPEKEY = "Typekey";
    public static final String KEY_NAME_SOS_LOCATION = "Location";
    //子操作类型 ，e2e ， c2e ,等message需要 承载不同的子操作， 
    public static final String KEY_NAME_SEID = "SEID";
    public static final String KEY_NAME_OFFLINE = "Offline";
    public static final String KEY_NAME_DEVICE_OFFLINE_STATE = "offline";
    public static final String KEY_NAME_CONTENT = "Content";
    public static final String KEY_NAME_KEY = "Key";
    public static final String KEY_NAME_KEYS = "Keys";
    public static final String KEY_NAME_NEXT_KEY = "NextKey";
    public static final String KEY_NAME_SIZE = "Size";
    public static final String KEY_NAME_IS_TRUNCATED = "IsTruncated";
    public static final String KEY_NAME_LIST = "List";
    public static final String KEY_NAME_KEY_BEGIN = "KeyBegin";
    public static final String KEY_NAME_KEY_END = "KeyEnd";
    public static final String KEY_NAME_DATE = "Date";

    public static final String KEY_NAME_SUB_ACTION = "sub_action";
    //feedback
    public static final String KEY_NAME_FEEDBACK = "feedback";

    //update
    public static final int SUB_ACTION_VALUE_NAME_WATCH_UPDATE = 208;
    public static final String KEY_NAME_WATCH_UPDATE_STATE = "upgradeStatus";
    public static final String KEY_NAME_MAP_GET_KEY = "Value";
    public static final String KEY_NAME_VERSION_CODE = "versionCode";
    public static final String KEY_NAME_VERSION_NAME = "versionName";
    public static final String KEY_NAME_VERSION_DESC = "description";
    public static final String KEY_NAME_MD5 = "md5";
    public static final String KEY_NAME_UPDATE_SIZE = "size";
    public static final String KEY_NAME_RELEASE_DATE = "releaseDate";
    public static final String KEY_NAME_DOWNLOAD_URL = "downloadUrl";
    public static final String KEY_NAME_VOICE_KEY = "Key";
    public static final String KEY_NAME_VOICE_NUM = "Num";
    public static final String KEY_NAME_GMT = "GMT";
    //custom 定制字段，custom下存储jsonstring ，里面增加自定义字段如 head_key
    public static final String KEY_NAME_CUSTOM = "Custom";
    public static final String KEY_NAME_CUSTOM_HEADKEY = "custom_headkey";
    public static final String KEY_NAME_CUSTOM_RELATION = "custom_relation";

    //HistoryTrace
    public static final String KEY_NAME_HISTORYTRACE_DAYS = "Days";

    //短信编码字段
    public static final String KEY_NAME_SMS_DATA = "SMS";

    public static final String KEY_NAME_SOURCE_TYPE = "sourceType";
    public static final String KEY_NAME_SIM_ARRAY = "simarray";
    public static final String KEY_NAME_MCCMNC = "MCCMNC";
    public static final String KEY_NAME_NUMBER = "NO";
    //alex charge status
    public static final String KEY_NAME_CHARGE_STATUS = "status";
    //efence name
    public static final String KEY_NAME_EFENCE = "EFence";
    //sim查询和操作
    public static final String KEY_SIM_SEARCH_TYPE = "TYPE";
    public static final String KEY_SIM_SEARCH_DATA = "data";
    public static final String KEY_SIM_SEARCH_BILL = "bill";
    public static final String KEY_SIM_SEARCH_BILL_MONTH = "int_value";
    public static final String KEY_SIM_SEARCH_BALANCE = "balance";
    public static final String KEY_SIM_SEARCH_TOTAL_RECHARGE = "total_charge";
    public static final String KEY_SIM_SEARCH_CALLERID = "callerid";
    public static final String KEY_SIM_SEARCH_ACCOUNT_STATUS = "account_status";
    public static final String KEY_SIM_SEARCH_ACTIVATE_STATUS = "activate_status";
    public static final String KEY_SIM_SEARCH_IDENTITY_STATUS = "identity_status";
    public static final String KEY_SIM_SEARCH_IDENTITY_MSG = "msg";
    public static final String KEY_SIM_SEARCH_PHONE_NUMBER = "phone_number";
    public static final String KEY_SIM_NOTICE_PHONE_RETURN = "return_notify";
    public static final String KEY_SIM_NOTICE_PHONE_CHANGE = "change_notify";

    public static final String KEY_SIM_SIMNO = "SIMNO";
    public static final String ACTION_SIM_OP_NOTICE = "action.simop.notice";
    public static final String SIM_OP_INTENT_STRING = "sim_op_notice_msg";
    public static final String SIM_SHAREP_CALLERID = "callerid_is_on";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMPTION_TOTAL = "consumption_total";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMPTION_CALLS = "consumption_calls";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMPTION_TRAFFIC = "consumption_traffic";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMOTION_SMS = "consumption_sms";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMOTION_CALLERID = "consumption_callerid";
    public static final String KEY_SIM_SEARCH_BILL_CONSUMOTION_OTHER = "consumption_other";
    public static final String KEY_SIM_NOTICE_PHONENUM = "SIMNO";
    public static final String KEY_DEVICE_LISTEN_PHONENUM = "phone_num";
    public static final String KEY_DEVICE_LISTEN_TYPE = "type";
    public static final String KEY_DEVICE_TYPE = "deviceType";
    public static final String KEY_NAME_MACH_SN = "MachSerialNo";//整机sn
    // key name for loc policy
    public static final String KEY_NAME_POLICY_LIST = "policylist";
    public static final String KEY_NAME_POLICY_VERSION = "version";
    public static final String KEY_NAME_POLICY_ID = "policy_id";
    public static final String KEY_NAME_POLICY_NORMAL_INTERVAL = "normal_interval";
    public static final String KEY_NAME_POLICY_SLEEP_INTERVAL = "sleep_interval";
    public static final String KEY_NAME_POLICY_FLIGHT_OUT_INTERVAL = "flightout_interval";
    public static final String KEY_NAME_POLICY_IGNORE_CELL_THRSHOLD = "ignore_cell_thrshold";
    public static final String KEY_NAME_POLICY_CELL_NO_CHANGE_STEPS = "cell_no_chg_steps";
    public static final String KEY_NAME_POLICY_CELL_PART_CHANGE_STEPS = "cell_part_chg_steps";
    public static final String KEY_NAME_POLICY_NORMAL_MOTION_STEPS = "normal_motion_steps";
    public static final String KEY_NAME_POLICY_NORMAL_TO_SLEEP_DURATION = "normal_to_sleep_dur";
    public static final String KEY_NAME_POLICY_SLEEP_MOTION_STEPS = "sleep_motion_steps";
    public static final String KEY_NAME_POLICY_SLEEP_TO_FLIGHT_STEPS = "sleep_to_flight_steps";
    public static final String KEY_NAME_POLICY_SLEEP_DEEPSLEEP_EXIT_STEPS = "deepsleep_exit_steps";
    public static final String KEY_NAME_POLICY_SLEEP_TO_DEEPSLEEP_DURATION = "to_deepsleep_dur";
    public static final String KEY_NAME_POLICY_SLEEP_TO_FLIGHT_DURATION = "to_flight_dur";
    public static final String KEY_NAME_POLICY_FLIGHT_EXIT_STEPS = "flight_exit_steps";
    public static final String KEY_NAME_POLICY_POSITIONING_CONTROL = "pos_ctrl";
    public static final String KEY_NAME_POLICY_REPORT_FREQ = "report_freq";

    //设备生成的随机验证码
    public static final String KEY_NAME_VERIFY_CODE = "verifyCode";

    //不区分加密算法的二维码字串
    public static final String KEY_NAME_QR_STR = "qrStr";

    // 联系人新增的key
    public static final String KEY_NAME_CONTACT_ID = "id";
    public static final String KEY_NAME_CONTACT_USER_EID = "user_eid";
    public static final String KEY_NAME_CONTACT_USER_GID = "user_gid";
    public static final String KEY_NAME_CONTACT_NAME = "name";
    public static final String KEY_NAME_CONTACT_NUMBER = "number";
    public static final String KEY_NAME_CONTACT_ATTRI = "attri";
    public static final String KEY_NAME_CONTACT_SUB_NUMBER = "sub_number";
    public static final String KEY_NAME_CONTACT_OPT_TYPE = "optype";
    public static final String KEY_NAME_CONTACT_RING = "ring";
    public static final String KEY_NAME_CONTACT_AVATAR = "avatar";
    public static final String KEY_NAME_CONTACT_UPDATE_TS = "updateTS";
    public static final String KEY_NAME_CONTACT_LAST_TS = "updateTS";
    public static final String KEY_NAME_CONTACT_SYNC_ARRAY = "sync_array";
    public static final String KEY_NAME_CONTACT_WEIGHT = "contact_weight";
    public static final String KEY_NAME_CONTACT_TYPE = "contactsType";

    public static final String KEY_NAME_CONTACT_UPDATE_TS_OLD = "timeStampId";

    //trakc
    public static final String KEY_TRACE_TO_MODE = "mode";
    public static final String KEY_TRACE_TO_FREQ = "freq";
    public static final String KEY_TRACE_TO_STATUS = "status";
    public static final String KEY_TRACE_TO_TIME = "time";
    public static final String KEY_TRACE_TO_VALUE = "value";
    public static final String KEY_TRACE_TO_END_TIME = "endTime";
    public static final String KEY_NAME_USER_RELOC = "user_reloc";

    // key name for specified loc mode
    public static final String KEY_NAME_LOC_SPEC_ONOFF = "value";
    public static final String KEY_NAME_LOC_SPEC_FREQ = "freq";
    public static final String KEY_NAME_LOC_SPEC_MODE = "mode";
    public static final String KEY_NAME_LOC_SPEC_ENDTIME = "endTime";

    //60151 接口设置sim卡号码不删除old的标志
    public static final String KEY_NAME_REMOVEOLD = "RemoveOld";

    public static final String KEY_NAME_DEVICES = "devices";

    public static final String KEY_NAME_PAY_PWD = "pay_pwd";
    public static final String KEY_NAME_SET_TYPE = "settype";
    public static final String KEY_NAME_PAY_ON = "pay_on";
    public static final String SHARE_PAY_PWD = "share_pay_pwd";
    public static final String SHARE_PAY_ON = "share_pay_ON";
    public static final String SHARE_User_Knowledge = "share_User_Knowledge";

    public static final String KEY_NAME_ROUTE_PLAN = "route_plan";
    public static final String KEY_NAME_NAVI_EFENCE = "EFENCE";
    public static final String KEY_NAME_DESCRIBE = "describe";
    public static final String KEY_NAME_POINTS = "points";
    //sub_action列表
    public static final int SUB_ACTION_VALUE_NAME_GET_LOCATION = 100;  //发送e2e到手表获取地址信息
    public static final int SUB_ACTION_VALUE_NAME_SET_WATCH_SETTING = 101;  //发送e2e到手表 设置手表

    //    语音消息pl结构
//    {
//    	"sub_action":102，             语音消息类型
//    	"Num":1,              语音消息数目
//    	"Key":"GP/GGIIDD/MSG/1232134243214"    第一条语音消息的key
//    }
    public static final int SUB_ACTION_VALUE_NAME_TRACE_STATES = 106;//e2g接收下发追踪信息
    public static final int SUB_ACTION_VALUE_NAME_SEND_VOICE = 105;  //发送e2e到手表 发送语音
    public static final int SUB_ACTION_VALUE_NAME_SEND_VOICE_OLD = 102;  //发送e2e到手表 发送语音
    public static final int SUB_ACTION_VALUE_NAME_FORCE_RECORD = 103;  //发送e2e到手表 强制录音
    public static final int SUB_ACTION_VALUE_NAME_FORCE_TAKE_PHOTO = 113;  //发送e2e到手表 远程拍照
    public static final int SUB_ACTION_VALUE_NAME_FORCE_VIDEO = 203;  //发送e2e到手表 远程录视频
//    public static final int SUB_ACTION_VALUE_NAME_FORCE_CLOSE =104;  //发送e2e到手表 强制关机

    //add zhengxiaoyang
    public static final int SUB_ACTION_VALUE_NAME_GET_WATCH_PARA_VALUE = 150; //发送e2e到手表获取所有参数
    /*参数key值为   LIGHT_BRIGHTNESS_LEVEL = "light_brightness_level";  //灯光
                  NOTICE_VOLUME_LEVEL = "notice_volume_level";        //提示音量
                  RECORD_VOLUME_LEVEL = "record_volume_level";        //语言音量
                  BATTERY_LEVEL = "battery_level";                    //电量百分比
                  AIRPLANE_MODE = "airplane_mode";                    //飞行模式
                  ACROSS_CITY_REMIND = "across_city_remind";          //跨城市提醒，开/关
                  EMERGENCY_CALL = "emergency_call";                  //紧急呼叫
    */
    public static final int SUB_ACTION_VALUE_NAME_GET_STORY_LIST = 114;  //发送e2e到手表,获取故事列表，7x0系列支持
    public static final int SUB_ACTION_VALUE_NAME_DEL_CHOOSE_STORY = 115;  //发送e2e到手表，删除故事，7x0系列支持
    public static final int SUB_ACTION_VALUE_NAME_APP_USAGE = 129;  //发送到手表获取到应用使用情况


    public static final int SUB_ACTION_VALUE_NAME_SET_LIGHT_BRIGHTNESS = 151;  //发送e2e到手表设置指示灯亮度
    public static final int SUB_ACTION_VALUE_NAME_SET_NOTICE_VOLUME = 152;  //发送e2e到手表设置提示音量
    public static final int SUB_ACTION_VALUE_NAME_SET_RECORD_VOLUME = 153;  //发送e2e到手表设置语言音量
    public static final int SUB_ACTION_VALUE_NAME_SET_SHUTDOWN_DEVICE = 154;  //发送e2e到手表关机     0:关机
    public static final int SUB_ACTION_VALUE_NAME_SET_AIRPLANE_MODE = 155;  //发送e2e到手表设置飞行模式  1:打开   0:关闭
    public static final int SUB_ACTION_VALUE_NAME_SET_ACROSS_CITY_REMIDD = 156;  //发送e2e到手表设置跨城市提醒
    public static final int SUB_ACTION_VALUE_NAME_SET_EMERGENCY_CALL = 157;  //发送e2e到手表设置手表紧急呼叫
    public static final int SUB_ACTION_VALUE_NAME_SET_WATCH_BUZZER = 158;  //发送e2e到手表， 找手表功能
    public static final int SUB_ACTION_VALUE_NAME_SET_VOLUME_VIBRATOR = 159;  //发送e2e到手表设置震动
    public static final int SUB_ACTION_VALUE_NAME_SET_OPEARTION_MODE = 149;  //发送e2e到手表设置运行模式
    public static final int SUB_ACTION_VALUE_NAME_SET_PHONE_NUMBER = 500;  //发送号码给手表
    public static final int SUB_ACTION_VALUE_NAME_GET_STEPS_SUM = 830;     //发送e2e到手表，获取实时总步数
    public static final int SUB_ACTION_VALUE_NAME_SMS_SEARCH = 601;     //发送e2e到设备，短信查询
    public static final int SUB_ACTION_VALUE_NAME_VOLTAGE_CURVE = 304;  //发送e2e到设备，获取设备的电量信息
    //add dengbing
    public static final int SUB_ACTION_VALUE_NAME_REPORT_BATTERY = 160;        //发送e2e到手机上报电量

    public static final int SUB_ACTION_VALUE_NAME_DEVICE_LISTEN = 303;   //手表回拨
    public static final int SUB_ACTION_VALUE_NAME_ALIPAY_SETPWD = 512;   //支付宝设置密码

    public static final int SUB_ACTION_VALUE_NAME_WATCH_SPORT = 334;  //发送到手表获取到应用使用情况

    /*e2c内容 定位pl:
      "GP/GGIIDD/WARNING/20140829010102000^":{
        "Type":"sos",  //sos报警
       "Timestamp":"201501021314356"  // 时间戳
       "Eid":"xxxxxxxxxxxx"           //手表eid
       "Typekey":"xxxxxxxxxxxxxxxxxxx"    //和语音对应的值
       "Location":{ xxxxx } //定位信息，与定时打点上传的json结构相同
      }
    e2c内容语音pl:
       {
           "Type":"sos",
           "EID":"xxxxxxxxxxxx",
           "Timestamp":"201501021314356"  // 时间戳
           "Duration":10,
           "Content":"base64"
           }

    e2g通知内容定位pl:
    * {
       "sub_action":161,  //sos
       "Timestamp":"201501021314356"  // 时间戳
       "Eid":"xxxxxxxxxxxx"           //手表eid
       "Typekey":"xxxxxxxxxxxxxxxxxxx"    //和语音对应的值
       "Key":"xxxxxxxxxxx" //对应的e2c的key值
       "Location":{ xxxxx } //定位信息，与定时打点上传的json结构相同
        }
     e2g通知内容语音pl:
        * {
           "sub_action":102,  //语音
           "Num":1,  // 语音消息数目
           "Key":"xxxxxxxxxxx" //第一条语音消息的key
            }*/
    public static final int SUB_ACTION_VALUE_NAME_SOS = 161;           //发送定位e2e到手机SOS，手表SOS发出的位置信息

    //add alert e2e
    public static final int SUB_ACTION_VALUE_NAME_NOTICE_ACROSS_CITY_REMIDD = 162;
    public static final int SUB_ACTION_VALUE_NAME_NOTICE_SAFEAREA = 163; //安全区域提醒

    public static final int SUB_ACTION_VALUE_NAME_NOTICE_SAFEDANGERDRAW = 168;  //安全危险区域进出提醒
    public static final int SUB_ACTION_VALUE_NAME_NOTICE_LOWPOWER = 164;
    public static final int SUB_ACTION_VALUE_NAME_NOTICE_BATTERY_LEVEL = 165;   //电量变化通知


    public static final int SUB_ACTION_VALUE_NAME_WATCH_STATE_CHANGE = 166;   //手表状态通知，手表发出
    /*map_mset pl
	{
		"watch_status":"20150730121634123_1"  // 1- power on/ 2-power off/ 3- flight mode
	}
	e2g pl
	 {
	       "sub_action":166
		"watch_status":"1"          // 1- power on/ 2-power off/ 3- flight mode
		"Eid": "werwerwerwerwer"  // watch eid 
		"timestamp":"20150730121634123"  // 
	 }*/

    public static final int SUB_ACTION_VALUE_NAME_GET_SILENCE_TIME_RANGE = 170;  //发送e2e到手表获取静音时间区间
    public static final int SUB_ACTION_VALUE_NAME_SET_SILENCE_TIME_RANGE = 171;  //发送e2e到手表设置静音时间区间
    public static final int SUB_ACTION_VALUE_NAME_MODIFY_SILENCE_TIME_RANGE = 172;  //发送e2e到手表修改静音时间区间
    public static final int SUB_ACTION_VALUE_NAME_DELETE_SILENCE_TIME_RANGE = 173;  //发送e2e到手表删除静音时间区间
    public static final int SUB_ACTION_VALUE_NAME_SET_ALARM_TIME_RANGE = 175;  //发送e2e到手表设置闹钟时间
    public static final int SUB_ACTION_VALUE_NAME_DELETE_ALARM_TIME_RANGE = 176;  //发送e2e到手表删除闹钟时间，手机UI需要跟新
    public static final int SUB_ACTION_VALUE_NAME_SET_SLEEP_TIME_RANGE = 177; //发送e2e到手表设置睡眠时间
    public static final int SUB_ACTION_VALUE_NAME_OTA_RESULT = 178;  //蓝牙ota升级结果
    public static final int SUB_ACTION_VALUE_NAME_UPDATE_SIM_CARD_INFO = 180; //sim卡信息更新通知
    public static final int SUB_ACTION_VALUE_NAME_REQ_ADD_NEW_FRIEND = 181;  //添加手表好友通知

    public static final int SUB_ACTION_VALUE_NAME_DELETE_ALARM_EFENCE = 186;  //电子围栏e2g通知
    public static final int SUB_ACTION_VALUE_NAME_ASK_JOIN_GROUP = 200;  //请求加入group
    //增加一个ask join 的到达回执
    public static final int RC_ACK = 7777;

    public static final int SUB_ACTION_VALUE_NAME_ANSWER_JOIN_GROUP = 201;  //应答加入group

    public static final int SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE = 202;//群组变化通知,管理员变化也加入这个序列
    //public static final int SUB_ACTION_VALUE_NAME_GROUP_ADMIN_NOTICE = 203;//管理员变化通知
    public static final int SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE = 205;//设备的联系人变化通知
    public static final int SUB_ACTION_VALUE_NAME_DOWNLOAD_CHANGE_NOTICE = 206;

    public static final int SUB_ACTION_VALUE_NAME_INSTALLAPP_LIST_CHANGE = 308;//手表appList变化通知

    public static final int SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE = 210;//手表信息编辑通知
    public static final int SUB_ACTION_VALUE_NAME_USER_CHANGE_NOTICE = 211;//个人信息编辑通知
    public static final int SUB_ACTION_VALUE_NAME_REMOVE_OUT_PRE_NOTICE = 220;//被管理员移除预通知
    public static final int SUB_ACTION_VALUE_NAME_VIDEO_CALL = 116; //tutk通信
    public static final int SUB_ACTION_VALUE_NAME_VIDEO_CALL_END = 117; //结束tutk通信
    //固件升级 打开蓝牙
    public static final int SUB_ACTION_VALUE_NAME_FIRMWARE_UPDATE_START = 300;  //发送e2e到手表打开蓝牙

    public static final int SUB_ACTION_VALUE_NAME_WATCH_STATE_CHANGE_NOTICE = 400;   //APP检测到的手表开关机状态同步给其他手机 State 1：开机 2：关机 3：飞行模式
    public static final int SUB_ACTION_VALUE_NAME_SERVER_NOTIFY = 401; //服务器通知app手表读取了语音信息
    public static final int SUB_ACTION_VALUE_NAME_FLOW_STATITICS_NOTIFY = 411; //获取手表端的流量通知

    public static final int SUB_ACTION_VALUE_NAME_HELLO = 6666;  //hello 指令
    public static final int SUB_ACTION_VALUE_NAME_TEST_PING = 9527;  //测试网速 指令
    public static final int SUB_ACTION_SERVER_TO_ENDPOINT_NOTICE = 501;//设置消息、说表状态、健康消息共用的subation
    public static final int SUB_ACTION_REQUEST_STEPS = 502;  //调试用的指令
    public static final int SUB_ACTION_REQUEST_SIGNAL = 503;  //获取手表网络信号
    public static final int SUB_ACTION_REQUEST_VERSION = 504; //请求手表版本号
    public static final int SUB_ACTION_SET_WEATHER_CITY = 510;  //设置手表显示天气对应的城市
    public static final int ONCE_GET_CHAT_LIMIT = 3; //单次c2e获取消息的条目限制
    // just for loc policy test
    public static final int SUB_ACTION_LOC_MODE_SWITCH = 106; //设置定位模式
    public static final int SUB_ACTION_LOC_POLICY_CFG = 701;  //设置手表定位策略
    public static final int SUB_ACTION_LOC_POLICY_READBACK = 702; //手表定位策略接收通知
    public static final int SUB_ACTION_AD_ON_OFF = 721;  //广告开关
    public static final int SUB_ACTION_WECHAT_NOTICE_BIND = 260; //微信通知绑定

    public static final int SUB_ACTION_ALI_MS_BIND = 269; //支付宝小程序绑定

    public static final int SUB_ACTION_WAKEUP_DEVICE = 820;//唤醒设备

    public static final int SUB_ACTION_DIABG_OPT = 306;//设备删除或者修改表盘状态通知

    public static final int SUB_ACTION_WATCH_NAVI_START = 521;
    public static final int SUB_ACTION_WATCH_NAVI_UPDATE_ROUTE_PLAN = 522;
    public static final int SUB_ACTION_WATCH_NAVI_CURRENT_POINT = 523;
    public static final int SUB_ACTION_WATCH_NAVI_NAVI_STATE = 524;
    public static final int SUB_ACTION_WATCH_NAVI_NOTICE = 531;

    public static final int CID_REQ_SEARCH_WATCH_LIST = 20311;
    public static final int CID_REQ_SEARCH_WATCH_LIST_RESP = 20312;
    public static final int CID_REQ_ADD_NEW_FIEND = 20161;
    public static final int CID_REQ_ADD_NEW_FIEND_RESP = 20162;
    public static final int CID_REMOVE_EID_FROM_FRIEND = 10291;
    public static final int CID_SCHEDULE_DATA_GET = 80511;
    public static final int CID_SCHEDULE_DATA_GET_RESP = 80512;
    public static final int CID_SCHEDULE_DATA_UPDATE = 80411;
    public static final int CID_SCHEDULE_DATA_UPDATE_RESP = 80412;
    public static final String KEY_WATCH_SCHEDULE_NOTIFY = "course_alert";
    //e2e key
    public static final String STARTHOUR = "starthour";
    public static final String STARTMIN = "startmin";
    public static final String ENDHOUR = "endhour";
    public static final String ENDMIN = "endmin";
    public static final String DAYS = "days";
    public static final String ONOFF = "onoff";
    public static final String TYPE = "type";
    public static final String TIMESTAMPID = "timeid";
    public static final String ALARM_TYPE_FLAG = "flag";
    public static final String SILENCETIME_ADVANCEOPT = "advanceop";
    public static final String SILENCETIME_CALL_IN_ONOFF = "silence_call_in_onoff";

    //Alarmclock key
    public static final String ALARM_HOUR = "hour";
    public static final String ALARM_MIN = "min";
    public static final String ALARM_DAYS = "days";
    public static final String ALARM_ONOFF = "onoff";
    public static final String ALARM_SELECTITEM = "selectid";
    public static final String ALARM_TIMESTAMPID = "timeid";
    public static final String ALARM_BELL = "bell";

    //Sos Sms
    public static final String KEY_NAME_DEVICE_SOS_SMS = "sos_sms";
    public static final String KEY_NAME_DEVICE_SOS_SMS_PHONELIST = "phonelist";
    public static final String KEY_NAME_DEVICE_SOS_SMS_PHONENUMBER = "number";

    public static final String SECURITY_ZONE_RADIUS = "zone_radius";
    public static final String SECURITY_ZONE_CENTER = "zone_center";
    public static final String SECURITY_ZONE_CENTER_BD = "zone_center_bd";
    public static final String SECURITY_ZONE_NAME = "zone_name";
    public static final String SECURITY_ZONE_ONOFF = "zone_onoff";
    public static final String SECURITY_ZONE_EFID = "zone_efid";
    public static final String SECURITY_ZONE_SEARCH_TITLE = "zone_title";
    public static final String SECURITY_ZONE_SEARCH_INFO = "zone_info";
    public static final String SECURITY_ZONE_SEARCH_CODE = "zone_code";
    public static final String SECURITY_ZONE_PREVIEW = "zone_preview";
    public static final String SECURITY_ZONE_COORDINATETYPE = "coordinateType";

    public static final String LIGHT_BRIGHTNESS_LEVEL = "light_brightness_level";
    public static final String NOTICE_VOLUME_LEVEL = "volume_level";
    public static final String NOTICE_LED_LEVEL = "led_level";
    public static final String OPERATION_MODE_VALUE = "operation_mode_value";
    public static final String OFFLINE_MODE_VALUE = "offlinemode";
    public static final String MODE_VALUE = "mode";
    public static final String PHONE_WHITE_LIST = "phone_white_list";
    public static final String HEALTH_INFO = "health_info";
    public static final String RECORD_VOLUME_LEVEL = "record_volume_level";
    public static final String SIGNAL_LEVEL = "signal_level";
    public static final String SIGNAL_LEVEL_FLAG = "signal_level_flag";
    public static final String BATTERY_LEVEL = "battery_level";
    public static final String STEPS_TARGET_LEVEL = "steps_target_level";
    public static final String STEPS_ONOFF_SETTING = "setps_setting";
    public static final String STEPS_NOTIFICATION_SETTING = "setps_notification";
    public static final String STEPS_LEVEL = "cur_steps";
    public static final String STEPS_LIST = "List";
    public static final String STEPS_CALORIES = "Calories";
    public static final String BATTERY_TIMESTAMP = "battery_timestamp";
    public static final String AIRPLANE_MODE = "airplane_mode";
    public static final String SHUTDOWN_ONOFF = "shutdown";
    public static final String VOL_VIB = "volumevibrate";
    public static final String ACROSS_CITY_REMIND = "across_city_remind";
    public static final String EMERGENCY_CALL = "emergency_call";
    public static final String SILENCE_LIST = "SilenceList";
    public static final String ALARM_CLOCK_LIST = "AlarmClockList";
    public static final String SLEEP_LIST = "SleepList";
    public static final String AUTO_ANSWER = "auto_answer";
    public static final String KEY_NAME_DEVICE_WHITE_LIST = "white_list_on";
    public static final String KEY_NAME_DEVICE_CLOUD_PHOTOS = "CloudPhotos";
    public static final String KEY_NAME_REPORT_FAULT_ONOFF = "report_fault_onoff";
    public static final String KEY_NAME_FCM_ONOFF = "fcm_onoff";
    public static final String SMS_FILTER = "sms_filter";
    public static final String WATCH_ONOFF_FLAG = CloudBridgeUtil.KEY_NAME_WATCH_STATE; //1 on 2 off
    public static final String HEAD_IMAGE_DATA = "head_image_date";
    public static final String SECURITY_ZONE_PREVIEW_DATA = "security_zone_preview_data";   //安全区域
    public static final String KEY_STORY_SWITCH = "GLOBAL:xmlyOnOff:";
    public static final String KEY_NAME_STORY_SWITCH = "xmlyOnOff";
    public static final String KEY_NAME_STORY_LOCAL_DOWNLOAD = "local_download";
    public static final String KEY_NAME_PWR_SAVING = "pwr_saving";

    // 功能控制
    public static final String FUNCTION_LIST = "functionlist";
    public static final String FUNCTION_NAME = "name";
    public static final String FUNCTION_ORDER = "order";
    public static final String FUNCTION_ONOFF = "onoff";
    public static final String FUNCTION_FLIST = "flist";

    //设备wifi相关sub_action
    public static final int SUB_ACTION_SEND_WIFI_NAME_AND_PWD = 505;//发送wifi名称和密码
    public static final int SUB_ACTION_REQUEST_DEVICE_WIFI_DATA = 506;//获取设备周边wifi信息
    public static final int SUB_ACTION_REQUEST_DEVICE_WIFI_STATE = 507;//获取设备wifi连接状态
    public static final int SUB_ACTION_SAVE_DEVICE_WIFI_DATA = 508;//操作设备保存wifi数据
    public static final int SUB_ACTION_DISCONNECT_DEVICE_WIFI = 509;//断开设备wifi连接
    public static final String KEY_AUTO_CONNECT_WIFI = "auto_connect_wifi";//设备自动连接wifi
    public static final String KEY_KEEP_WIFI_CONNECT = "keep_wifi_connect";//保持wifi连接
    //设备wifi相关key
    public static final String KEY_DEVICE_WIFI_DATA_LIST = "ssid_list";
    public static final String KEY_DEVICE_WIFI_SSID = "ssid";
    public static final String KEY_DEVICE_WIFI_BSSID = "bssid";
    public static final String KEY_WIFI_PWD = "wifipassword";
    public static final String KEY_DEVICE_WIFI_ISFREE = "need_auth";
    public static final String KEY_DEVICE_WIFI_TYPE = "auth_type";
    public static final String KEY_DEVICE_WIFI_STRENGTH = "rssi";
    public static final String KEY_DEVICE_WIFI_STATE = "status";
    public static final String KEY_DEVICE_WIFI_IS_SAVED = "is_saved";
    public static final String KEY_DEVICE_WIFI_CMD = "cmd_id";//0 query 1 modify 2 del
    public static final int KEY_DEVICE_WIFI_CMD_QUERY = 0;//0 query
    public static final int KEY_DEVICE_WIFI_CMD_MODIFY = 1;//1 modify
    public static final int KEY_DEVICE_WIFI_CMD_DEL = 2;//2 del
    public static final String KEY_DEVICE_WIFI_PROF = "prof_id";
    public static final String KEY_DEVICE_WLAN_STATE = "wlan";
    public static final String KEY_DEVICE_WLAN_STATUS = "wlan_status";
    public static final String KEY_DEVICE_WLAN_WIFI_SSID = "wlan_ssid";
    public static final String KEY_NAME_CONNECT_TYPE = "connect_type";     //网络连接状态connect_type
    public static final String KEY_NAME_STORY_DOWNLOAD_WIFI_ONLY = "story_dl_opt";
    public static final String KEY_NAME_FLOW_STATITICS_METER_LIMIT = "flowmeterthreshold";
    public static final String KEY_DEVICE_WIFI_ID = "wifiId";
    public static final String KEY_DEVICE_WIFI_PWD2 = "pwd";
    public static final String KEY_DEVICE_WIFI_ERROR = "errorcode";
    public static final String KEY_DEVICE_WIFI_FREQUENCY = "frequency";

    public static final String CITY_ID = "city_id";
    public static final String GETCITY_IS_AUTO = "is_auto";  //设置自动获取所在城市

    public static final String WATCH_AUTO_UPGRADE = "watch_auto_upgrade";
    public static final String WATCH_UPGRADE_ONLY_WIFI = "fota_wifi_only";
    public static final String DEVICE_POWER_ON_TIME = "device_power_on_time";

    public static final String PHONE_PAY_PWD = "pay_pwd";

    // 小米账号授权
    public static final String WATCH_MIOAUTH_REPORT_AUTHCODE = "code";
    public static final String WATCH_MIOAUTH_FLAG = "xiaoai_mioauth";

    // 视频通话
    public static final String KEY_NAME_VIDEOCALL_RESULT = "result";    //2正在通话中
    public static final String KEY_NAME_VIDEOCALL_TYPE = "type"; //0 呼叫，1 收到呼叫
    public static final String KEY_NAME_VIDEOCALL_TUTKTYPE = "tutkType";  //String 0 audio  1 video+audio
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_RECEIVECALL = 0;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_INPHONECALL = 1;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_INVIDEOCALL = 2;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_MOBILE_2G = 3;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_INSILENCE = 4;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_INHIGHTEMPER = 5;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_INLOWMEMORY = 6;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_LOWPOWER = 7;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_CHARGING = 8;
    public static final int KEY_NAME_VIDEOCALL_RESPONSE_ERROR_OTHER = 1000;
    public static final String KEY_NAME_VIDEOCALL_APPID = "appId";
    public static final String KEY_NAME_VIDEOCALL_CHANNELNAME = "channelName";
    public static final String KEY_NAME_VIDEOCALL_UID = "uid";
    public static final String KEY_NAME_VIDEOCALL_UID_OTHER = "uidOther";
    public static final String KEY_NAME_VIDEOCALL_TOKEN = "token";
    public static final String KEY_NAME_VIDEOCALL_TOKEN_OTHER = "tokenOther";
    public static final String KEY_NAME_VIDEOCALL_AUTHTOKEN = "authToken";
    public static final String KEY_NAME_VIDEOCALL_AUTHTOKEN_OTHER = "authTokenOther";
    public static final String KEY_NAME_VIDEOCALL_RTC_VERSION = "rtcVersion";
    public static final String KEY_NAME_VIDEOCALL_RTC_PROVIDER = "rtcProvider";
    public static final String KEY_NAME_VIDEOCALL_APP_URL = "appUrl";
    public static final String KEY_NAME_VIDEOCALL_DEVICE_URL = "deviceUrl";
    public static final String KEY_NAME_VIDEOCALL_TARGETEID = "tartEid";
    public static final String KEY_NAME_VIDEOCALL_CMD = "cmd";

    public static final String KEY_NAME_NAVIGATION_AUTHORITY = "navigation_authority";

    // 应用商店key
    public static final String KEY_APPSTORE_AUTO_UPDATE = "app_auto_update";
    public static final String KEY_APPSTORE_APP_NAME = "name";
    public static final String KEY_APPSTORE_APP_APPID = "app_id";
    public static final String KEY_APPSTORE_APP_ICON = "icon";
    public static final String KEY_APPSTORE_APP_DESCRIPTION = "description";
    public static final String KEY_APPSTORE_APP_VERSION = "version";
    public static final String KEY_APPSTORE_APP_VERSIONCODE = "version_code";
    public static final String KEY_APPSTORE_APP_DOWNLOADURL = "download_url";
    public static final String KEY_APPSTORE_APP_DEVVERSION = "dev_version";
    public static final String KEY_APPSTORE_APP_SIZE = "size";
    public static final String KEY_APPSTORE_APP_MD5 = "md5";
    public static final String KEY_APPSTORE_APP_PAGE = "page";
    public static final String KEY_APPSTORE_APP_FUNCTION = "function";
    public static final String KEY_APPSTORE_APP_OPTYPE = "optype";
    public static final String KEY_APPSTORE_APP_TYPE = "type";
    public static final String KEY_APPSTORE_APP_STATUS = "status";
    public static final String KEY_APPSTORE_APP_HIDDEN = "hidden";
    public static final String KEY_APPSTORE_APP_WIFI = "wifi";

    public static final String KEY_NAME_VIDEOCALL_APPSECRET = "appSecret";

    public static JSONObject obtainCloudMsgContent(int cid, int sn, String sid, Object pl) {
        JSONObject msg = new JSONObject();
        msg.put("CID", cid);
        if (sid != null) {
            msg.put(KEY_NAME_SID, sid);
        }
        msg.put("SN", sn);
        if (pl != null) {
            msg.put("PL", pl);
        }
        return msg;
    }

    public static JSONObject obtainCloudMsgContentWithParam(int cid, int sn, String sid, Object pl, Object param) {
        JSONObject msg = new JSONObject();
        msg.put("CID", cid);
        if (sid != null) {
            msg.put(KEY_NAME_SID, sid);
        }
        msg.put("SN", sn);
        if (pl != null) {
            msg.put("PL", pl);
        }
        if (param != null) {
            msg.put("PARAM", param);
        }

        return msg;
    }

    public static JSONObject CloudE2eMsgContent(int cid, int sn, String sid, String persistent, String[] teid, JSONObject pl) {
        JSONObject msg = new JSONObject();
        msg.put(KEY_NAME_CID, cid);
        if (sid != null) {
            msg.put(KEY_NAME_SID, sid);
        }
        msg.put(KEY_NAME_SN, sn);
        if (null != persistent) {
            msg.put(KEY_NAME_PERSISTENT, persistent);
        }
        if (null != teid) {
            msg.put(KEY_NAME_TEID, teid);
        } else {
            msg.put(KEY_NAME_TEID, CloudBridgeUtil.TEST_EID);
        }

        if (pl != null) {
            msg.put(KEY_NAME_PL, pl);
        }
        return msg;
    }

    public static JSONObject CloudE2eMsgResp(int cid, int sn, String[] teid, String sid, JSONObject pl) {
        JSONObject msg = new JSONObject();
        msg.put(CloudBridgeUtil.KEY_NAME_CID, cid);
        msg.put(CloudBridgeUtil.KEY_NAME_SN, sn);
        msg.put(CloudBridgeUtil.KEY_NAME_RC, 1);
        if (sid != null) {
            msg.put(CloudBridgeUtil.KEY_NAME_SID, sid);
        }
        if (pl != null) {
            msg.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        }
        if (null != teid) {
            msg.put(CloudBridgeUtil.KEY_NAME_TEID, teid);
        } else {
            msg.put(CloudBridgeUtil.KEY_NAME_TEID, CloudBridgeUtil.TEST_EID);
        }
        return msg;
    }

    public static JSONObject CloudMapSetContent(int cid, int sn, String sid, JSONObject pl) {
        JSONObject msg = new JSONObject();
        msg.put(KEY_NAME_CID, cid);
        msg.put(KEY_NAME_SN, sn);
        if (sid != null) {
            msg.put(KEY_NAME_SID, sid);
        }
        if (pl != null) {
            msg.put(KEY_NAME_PL, pl);
        }
        return msg;
    }

    public static JSONObject CloudE2gMsgContent(int cid, int sn, String sid, String persistent, String tgid, JSONObject pl) {
        JSONObject msg = new JSONObject();
        msg.put(KEY_NAME_CID, cid);
        if (sid != null) {
            msg.put(KEY_NAME_SID, sid);
        }
        msg.put(KEY_NAME_SN, sn);
        if (null != persistent) {
            msg.put(KEY_NAME_PERSISTENT, persistent);
        }

        msg.put(KEY_NAME_TGID, tgid);


        if (pl != null) {
            msg.put(KEY_NAME_PL, pl);
        }
        return msg;
    }

    public static int getCloudMsgCID(JSONObject msg) {
        return (Integer) msg.get("CID");
    }

    public static int getCloudMsgSN(JSONObject msg) {
        try {
            return (Integer) msg.get("SN");
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    public static int getCloudMsgRC(JSONObject msg) {
        try {
            return (Integer) msg.get(KEY_NAME_RC);
        } catch (Exception e) {
            // TODO: handle exception
            return RC_NULL;
        }
    }

    public static String getCloudMsgSEID(JSONObject msg) {
        try {
            return (String) msg.get(KEY_NAME_SEID);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public static String getCloudMsgRN(JSONObject msg) {

        try {
            return msg.get(KEY_NAME_RN).toString();
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * @param msg
     * @return msg 的pl实体
     */
    public static JSONObject getCloudMsgPL(JSONObject msg) {
        return (JSONObject) msg.get(KEY_NAME_PL);
    }

    /**
     * @param msg
     * @return msg 的pl实体 pl是 JSONArray 时候使用
     */
    public static JSONArray getCloudMsgPLArray(JSONObject msg) {
        return (JSONArray) msg.get(KEY_NAME_PL);
    }

    public static final String E2C_SPLIT_PHONELIST = "/PHONELIST/";
    public static final String E2C_SPLIT_RING = "/RING/";
    public static final String E2C_PL_KEY_FORMAT = "format";         //E2C上传资源的格式
    public static final String E2C_PL_KEY_THEME = "theme";         //风格

    public static final String KEY_NAME_VOICE = "voice";
    // 联系人新增的key
    public static final String NORMAL_DATA = "data";

    public static ArrayList<PhoneNumber> parseContactListFromJsonStr(String jsonStr) {
        ArrayList<PhoneNumber> whiteList = new ArrayList<PhoneNumber>();
        int mDefaultIdCount = 0;
        if (jsonStr != null && jsonStr.length() > 0) {
            Object obj = JSONValue.parse(jsonStr);
            JSONArray array = (JSONArray) obj;
            int size = array.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObj = (JSONObject) array.get(i);
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.id = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_ID);
                if (phoneNumber.id == null) {
                    //补上本生成的contact id
                    phoneNumber.id = TimeUtil.getTimestampCHN() + mDefaultIdCount++;//id递增
                }
                //兼容新老协议
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_NAME) != null) {
                    phoneNumber.nickname = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_NAME);
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID) != null) {
                    phoneNumber.userEid = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID);
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID) != null) {
                    phoneNumber.userGid = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID);
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_RING) != null) {
                    phoneNumber.ring = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_RING);
                }
                phoneNumber.number = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_NUMBER);
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER) != null) {
                    phoneNumber.subNumber = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER);
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT) != null) {
                    phoneNumber.weight = (Integer) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT);
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI) != null) {
                    phoneNumber.attri = (Integer) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI);
                } else {
                    phoneNumber.attri = 1000;
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR) != null) {
                    phoneNumber.avatar = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR);
                }
                String stamp = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_UPDATE_TS);
                if (stamp == null) {
                    stamp = (String) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_UPDATE_TS_OLD);
                }
                if (stamp != null && !stamp.contains("-")) {
                    stamp = stamp + "-00000000";
                }
                if (jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE) != null) {
                    phoneNumber.contactType = (Integer) jsonObj.get(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE);
                }
                phoneNumber.timeStampId = stamp;
//                if (phoneNumber.number !=null) {
                whiteList.add(phoneNumber);
//                }
            }
        }
        //排序,weight高在前，weight相同，id小的在前
        Collections.sort(whiteList, new Comparator<PhoneNumber>() {

            @Override
            public int compare(PhoneNumber lhs, PhoneNumber rhs) {
                if (lhs.weight > rhs.weight) {
                    return -1;
                } else if (lhs.weight < rhs.weight) {
                    return 1;
                } else {
                    return lhs.id.compareTo(rhs.id);
                }
            }


        });
        return whiteList;
    }

    public static String genContactListJsonStr(ArrayList<PhoneNumber> mBindWhiteList) {
        JSONArray plA = new JSONArray();
        plA.clear();
        for (int i = 0; i < mBindWhiteList.size(); i++) {
            JSONObject plO = new JSONObject();
            if (mBindWhiteList.get(i).id != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, mBindWhiteList.get(i).id);
            }
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_NUMBER, mBindWhiteList.get(i).number);
            if (mBindWhiteList.get(i).subNumber != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER, mBindWhiteList.get(i).subNumber);
            }
            if (mBindWhiteList.get(i).ring != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_RING, mBindWhiteList.get(i).ring);
            }
            if (mBindWhiteList.get(i).avatar != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR, mBindWhiteList.get(i).avatar);
            }
            if (mBindWhiteList.get(i).userEid != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, mBindWhiteList.get(i).userEid);
            }
            if (mBindWhiteList.get(i).userGid != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, mBindWhiteList.get(i).userGid);
            }
            if (mBindWhiteList.get(i).nickname != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, mBindWhiteList.get(i).nickname);
            }
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, mBindWhiteList.get(i).weight);
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, mBindWhiteList.get(i).contactType);
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI, mBindWhiteList.get(i).attri);
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_UPDATE_TS, mBindWhiteList.get(i).timeStampId);
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_UPDATE_TS_OLD, mBindWhiteList.get(i).timeStampId);
            plA.add(plO);
        }
        return plA.toString();
    }

    public static WatchDownloadBean getWatchDownloadBean(JSONObject content) {
        WatchDownloadBean bean = new WatchDownloadBean();
        if (content.get("sn") != null) {
            bean.setSn((Integer) (content.get("sn")));
        }
        bean.setFile((String) (content.get("file")));
        bean.setData((String) (content.get("data")));
        if (content.get("size") != null) {
            bean.setSize((Integer) (content.get("size")));
        }
        if (content.get("status") != null) {
            bean.setStatus((Integer) (content).get("status"));
        }
        if (content.get("type") != null) {
            bean.setType((Integer) (content.get("type")));
        }
        bean.setUpdateTS((String) (content).get("updateTS"));
        bean.setUrl((String) (content.get("url")));
        bean.setMd5((String) (content.get("md5")));
        return bean;
    }
}
