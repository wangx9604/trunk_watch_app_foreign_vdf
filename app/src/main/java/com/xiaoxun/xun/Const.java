/**
 * Creation Date:2015-1-7
 * <p/>
 * Copyright
 */
package com.xiaoxun.xun;

import com.xiaoxun.ConstDiff;
import com.xiaoxun.xun.utils.CloudBridgeUtil;


/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-7
 *
 */
public class Const {

    public static final String PACKAGE_NAME = ConstDiff.PACKAGE_NAME;
    public static final String ACTION_HEAD = ConstDiff.ACTION_HEAD;

    public static final int MSG_CONST_INT_BASE = 0x1000;
    public static final int MSG_RESPONSE_WATCH_LOCATION_OK = MSG_CONST_INT_BASE + 0;
    public static final int MSG_RESPONSE_WATCH_LOCATION_FAILED = MSG_CONST_INT_BASE + 1;
    public static final int MSG_RESPONSE_WATCH_DETAIL = MSG_CONST_INT_BASE + 2;
    public static final int MSG_RESPONSE_WATCH_FAMILY_DETAIL = MSG_CONST_INT_BASE + 3;
    public static final int MSG_RESPONSE_USER_FAMILYS = MSG_CONST_INT_BASE + 4;
    public static final int MSG_RESPONSE_USER_DETAIL = MSG_CONST_INT_BASE + 5;
    public static final int MSG_RESPONSE_WEBSOCKET_RECV = 0X2000;
    public static final int MSG_RESPONSE_WEBSOCKET_ERROR = 0X2001;
    public static final int MSG_RESPONSE_HTTP_RECV = 0X3000;
    public static final int MSG_RESTART_CLOUD_BRIDGE = 0x4000;
    public static final int MSG_RESPONSE_NETWORK_OR_WEBSOCKET_ERROR = 0X5000;
    public static final int MSG_RESPONSE_OPEN_WEBSOCKET_TIMEOUT = 0X6000;
    public static final int MSG_RESPONSE_RETRY_OPEN_WEBSOCKET = 0X7000;
    public static final String AUTHORISE_UPLOAD_RESULT_URL = "https://app.xunkids.com/authorize";
    public static final String AUTHORISE_GET_RESULT_URL = "https://app.xunkids.com/getauthorize";
    public static final String ACTION_MESSAGE_CONUNT_CHANGE = ACTION_HEAD + "action.message.count.change";

    //二维码头
    public static final String QR_START_OLD = "http://imibaby.com/qr?sn=";
    public static final String QR_START = "http://qr.imibaby.net/qr?sn=";
    public static final String NEW_QR_START = "http://app.imibaby.net/qr?sn=";
    public static final String SN_TYPE_ICCID = "AA.";
    public static final String SN_TYPE_ICCIDMD5 = "DD.";

    public static final String SW_PROTOCOL_NUM = ConstDiff.SW_PROTOCOL_NUM;    //xiaoxun version
    //正式服务器地址
    public static final String WEBSOCKET_DOMAIN = ConstDiff.WEBSOCKET_DOMAIN;//ConstDiff.WEBSOCKET_DOMAIN //sw501-online.imibaby.net//"bw.imibaby.net";//开发阶段直接连测试服务器，避免干扰线上服务器

    //测试服务器地址
    public static final String WEBSOCKET_TEST_SERVICE = ConstDiff.WEBSOCKET_TEST_SERVICE;

    public static final String DIAL_SHOP_SHOW_LONGTIME = "dial_shop_show_longtime";
    public static final String DIAL_SHOP_SHOW = "dial_shop_show";
    public static final String DIAL_SHOP_REDDOTVER_SHOW = "dial_shop_reddotVer_show";
    public static final String DIAL_SHOP_REDDOTVER_SHOW_TAG = "dial_shop_reddotVer_show_tag";
    public static final String DIAL_STORE_PUSH_ONOFF = "dial_store_push_onoff";

    //服务路径地址
//    public static final String WEBSOCKET_SSL_DIR = ":8555/svc/pipe";
    public static final String WEBSOCKET_SSL_DIR = "/svc/pipe";
    public static final String WEBSOCKET_SSL_TEST_DIR = ":8755/svc/pipe";
    public static final String WEBSOCKET_DIR = ":8080/svc/pipe";
    public static final String HTTP_DIR = ":8080/svc";

    public static final int DEFAULT_OPERATIONMODE_VALUE = 3;// 1:物联卡省电 2:物联卡普通 3:物联卡、联通卡性能 4:联通卡省电 - 默认模式为3
    public static final int DEFAULT_OFFLINEMODE_VALUE = 1;// 0 关闭 ；1 低电开启 ；2 一直开启  - 默认模式为1
    public static final int DEFAULT_OFFLINEMODE_BATTERY_LIMIT = 20;// 低电开启的低电量
    public static final String MOBILE_WULIAN_CARD = "898602B";
    public static final String UNICOM_CARD = "898601";

    public static final String SHARE_PREF_FIELD_SHOW_ALERTWINDOW_SET_DIALOG = "show_alertwindow_set_dialog";
    public static final String ACTION_REQUEST_ALERT_WINDOW_CANCEL = ACTION_HEAD + "action.request.alert.window.cancel";
    public static final String SHARE_PREF_FIELD_SHOW_NOTIFY_SET_DIALOG = "show_notify_set_dialog";

    //手表当前步数的统计
    public static final String ACTION_CLOUD_BRIDGE_STEPS_CHANGE = ACTION_HEAD + "action.cloud.bridge.steps.change";

    public static final String WEIBO_KEY = "3778688942";

//网络协议定义     start


//NET MSG ROUTE
    /**请求位置
     * parms: watchid (string),
     *        srcuid,(string),
     *        otheruid (string),(预留)
     *
     * retruns:
     *        desc (string),
     *        longitude(string),
     *        latitude     (string),
     *        accuracy (string)
     *        time(string)
     *        velocity(string)
     */
    public static final String NET_MSG_REQ_LOCATION = "req.location";

    /**发送聊天
     * parms: srcuid (string),
     *        familyid,(string),
     *        watchid,(string)
     *        otheruid, (string),(预留)
     *        type,(string) (目前只有 record)
     *        content,(string)
     *
     * retruns:
     *        recode (string),
     */
    public static final String NET_MSG_REQ_SEND_CHAT = "";

    /**设置手表参数
     * parms: srcuid (string),
     *        watchid,(string)
     *        volume,(  [1,2..5],  )
     *        volumevalue,()
     *
     *        light(  [1,2..5],  )
     *        lightvalue
     *
     *        hand
     *        handvalue ([right,left])
     *
     *
     */
    public static final String NET_MSG_REQ_SETTING_WATCH = "";

    /**设置手表定时器
     *         timetype (silent alarm )
     *         timevalue
     *        {
     *        day(1..7)
     *        start(h,m,)
     *        end(h,m,)
     *        ...
     *        }
     */
    public static final String NET_MSG_REQ_SETTING_TIMER = "";

    /**设置手表定位报告频率
     * parms: srcuid (string),
     *        watchid,(string)
     *        frequency
     */
    public static final String NET_MSG_REQ_SETTING_REPORT_FREQUENCY = "";

    /**要求手表录音
     * parms: srcuid (string),
     *        watchid,(string)
     *
     */
    public static final String NET_MSG_REQ_WATCH_RECORD = "";

    /**要求手表警报
     * parms: srcuid (string),
     *        watchid,(string)
     *
     */
    public static final String NET_MSG_REQ_WATCH_ALARM = "";


    /**绑定手表，解绑手表
     * parms: srcuid (string),
     *        watchid,(string)
     *        action(string[bind, unbind])
     */
    public static final String NET_MSG_REQ_BIND_WATCH = "";

    /**关闭手表
     * parms: srcuid (string),
     *        watchid,(string)
     *
     */
    public static final String NET_MSG_REQ_CLOSE_WATCH = "";
    //服务器通知的


    //notice msg
    /**sos通知
     * param  watchid,(string)
     *        desc (string),
     *        longitude(string),
     *        latitude     (string),
     *        accuracy (string)
     *        time(string)
     *        velocity(string)
     *
     */
    public static final String NET_MSG_NOTICE_SOS = "notice.sos";
    /**低电量通知
     * param  watchid,(string)
     *        powervalue(string)
     */
    public static final String NET_MSG_LOW_POWER = "";

    /**位置更新通知
     * param  watchid,(string)
     *        desc (string),
     *        longitude(string),
     *        latitude     (string),
     *        accuracy (string)
     *        time(string)
     *        velocity(string)
     *
     */
    public static final String NET_MSG_NOTICE_LOCATION = "notic.location";
    /**收到聊天
     * parms: srcuid (string),
     *        familyid,(string),
     *        watchid,(string)
     *        otheruid, (string),(预留)
     *        type,(string) (目前只有 record)
     *        content,(string)
     *
     */
//    public static final String NET_MSG_NOTICE_CHAT ="notice.chat";
//    //家庭成员变化通知e2e
//    public static final String NET_MSG_NOTICE_GROUP_CHANGE = "notice.groupchange";
    /**请求同步手表设置
     * PARMS:srcuid (string),
     *       watchid,(string)
     *
     *
     *
     * RETURN: srcuid (string),
     *        watchid,(string)
     *        volume,(  [1,2..5],  )
     *        volumevalue,()
     *
     *        light(  [1,2..5],  )
     *        lightvalue
     *
     *        hand
     *        handvalue ([right,left])
     *
     *
     */
    public static final String NET_MSG_GET_WATCH_SETTING = "";

    /**请求同步手表定时器
     * PARMS:srcuid (string),
     *       watchid,(string)
     *        {
     *        day(1..7)
     *        start(h,m,)
     *        end(h,m,)
     *        ...
     *        }
     */
    public static final String NET_MSG_GET_SETTING_TIMER = "";

    /**设置宝贝信息
     * PARMS:srcuid (string),
     *       watchid,(string)
     *       nickname
     *        sex
     *        birthday
     *        hight
     */
    public static final String NET_MSG_SET_BABY_DETAIL = "";

    /**设置用户信息
     * PARMS:srcuid (string),
     *       watchid,(string)
     *        nickname
     *        relation
     *
     */
    public static final String NET_MSG_SET_MY_DETAIL = "";

    /**设置用户头像
     * PARMS:srcuid (string),
     *       watchid,(string)
     *        HEAD
     *
     */
    public static final String NET_MSG_SET_MY_HEAD = "";
    /**
     * 通知其他用户绑定，解绑
     * RETURN:srcuid (string),
     *       watchid,(string)
     *       action(string[bind, unbind])
     *
     */
    public static final String NET_MSG_NOTICE_BIND = "";


//      public static final String NET_MSG_NOTICE_SETTING_CHANGE = "notice.settingchage";
    /**
     * 获取家庭成员信息
     * PARMS: srcuid (string),
     *        watchid,(string)
     *
     *  return : family_list
     */
    public static final String NET_MSG_GET_FAMILY_DETAIL = "";
    /**
     * 获取手表轨迹
     * PARMS: srcuid (string),
     *        watchid,(string)
     *
     *
     *  return : route line
     */
    public static final String NET_MSG_GET_WATCH_ROUTELINE = "";
    /**
     * 删除家庭成员
     * PARMS: srcuid (string),
     *        watchid,(string)
     *        MEMBER_ID
     *
     *  return : family_list
     */
    public static final String NET_MSG_DELETE_FAMILY_MEMBER = "";

    /**获取手表二维码
     *
     * PARMS: srcuid (string),
     *        watchid,(string)
     *
     *
     *  return : qr
     */
    public static final String NET_MSG_GET_WATCH_QR = "";
    /**
     * 合并家庭
     * PARMS: srcuid (string),
     *        watchid1,(string)
     *        watchid2,(string)
     *
     *  return :
     */
    public static final String NET_MSG_COMBINE_FAMILY = "";

    //网络协议end

    //
    //缓存目录
    public static final String MY_BASE_DIR = ConstDiff.MY_BASE_DIR; //恢复成原来的文件夹名
    public static final String MAP_CACHE_DIR = "map";
    public static final String Map_OFFLINE_DIR="offline";
    public static final String MAP_HERE_DIR = "heremap";
    public static final String Map_OFFLINE_BIADU_DIR="offline_baidu";
    public static final String CHAT_CACHE_DIR = "chat";
    public static final String ICON_CACHE_DIR = "icon";
    public static final String ALARM_RECORD_DIR = "alarm";
    public static final String LOG_CACHE_DIR = "log";
    public static final String MY_CHAT_DIR = "mychat";

    //SHARE_PREF_NAME
    public static final String SHARE_PREF_NAME = "imibaby_share";

    //Share Key
    public static final String SHARE_STEPS_CONUT = "share_steps_count";
    public static final String SHARE_STEPS_DATE = "share_steps_date";
    public static final String SHARE_STEPS_KILO = "share_steps_kilo";
    public static final String SHARE_STEPS_CALO = "share_steps_calo";

    // setting field
    public static final String SHARE_PREF_FIELD_CHANEG_MAP = "change_map";
    public static final String SHARE_PREF_FIELD_LAST_XIAOMIID = "last_xiaomiid";
    public static final String SHARE_PREF_FIELD_LAST_UID = "last_user";
    public static final String SHARE_PREF_FIELD_LOGIN_STATE = "login_stae";
    public static final String SHARE_PREF_FIELD_MY_NICKNAME = "nick_name";
    public static final String SHARE_PREF_FIELD_LOGIN_EID = "login_eid";
    public static final String SHARE_PREF_FIELD_LOGIN_TOKEN = "login_token";
    public static final String SHARE_PREF_FIELD_PUSH_TOKEN = "pushtoken";
    public static final String SHARE_PREF_FIELD_NEXT_CONTENT_KEY = "next_key";
    public static final String SHARE_PREF_FIELD_BATTERY_LEVEL = "battery_level";
    public static final String SHARE_PREF_FIELD_NEXT_FAMILY_CHANGE_KEY = "family_change_key";
    public static final String SHARE_PREF_FIELD_NEXT_WARNING_KEY = "next_warning_key";
    public static final String SHARE_PREF_FIELD_FAMILY_ADMINEID_KEY = "amdin_eid";
    public static final String SHARE_PREE_FIELD_USE_CALL_MODE = "use_call_mode";
    public static final String SHARE_PREF_FIELD_MY_LOCATION_VISIBALE_STATE = "my.location.visibale.state";
    public static final String SHARE_PREF_FIELD_MY_LOCATION_CHANGE_TIPS = "my.location.change.tips";
    public static final String SHARE_PREF_FIELD_LAST_WATCH = "lats_watch";
    public static final String SHARE_PREF_FIELD_LAST_PPSSWW = "last_ppssww";
    public static final String SHARE_PREF_FIELD_LAST_UNIONID = "last_unionid";
    public static final String SHARE_PREF_FIELD_WATCH_IS_ON = "watch_is_on";
    public static final String SHARE_PREF_FIELD_RECHARGE_TIPS = "recharge_tips";
    public static final String SHARE_PREF_FIELD_AUTO_RECEIVE = "watch_auto_receive";
    public static final String SHARE_PREF_FIELD_WHITE_LIST = "watch_white_list";
    public static final String SHARE_PREF_FIELD_SMS_FILTER = "watch_sms_filter";
    public static final String SHARE_PREF_FIELD_SOS_SMS = "watch_sos_sms";
    public static final String SHARE_PREF_FIELD_FCM_ONOFF = "watch_fcm_onoff";
    public static final String SHARE_PREF_FIELD_REPORT_FAULT_ONOFF = "report_fault_onoff";
    public static final String SHARE_PREF_FIELD_INTELLIGENT_POWERSAVING_ONOFF = "intelligent_powersaving";

    public static final String SHARE_PREF_FIELD_CLOUD_PHOTOS = "CloudPhotos";
    public static final String SHARE_PREF_FIELD_APP_NOTIFY_ONOFF = "app_notify_onoff";
    public static final String SHARE_PREF_FIELD_NO_SHOW_NOTIFY_DIALOG = "no_show_notify_dialog";
    public static final String SHARE_PREF_FIELD_AUTO_RECEIVE_NOTIFY = "watch_auto_receive_NOTIFY";
    public static final String SHARE_PREF_FIELD_WATCH_GROUP_MEMBERS = "watch_group_members";

    public static final String SHARE_PREF_FIELD_WEBSOCKET_IP_ADDR = "websocket_ip_addr";
    public static final String SHARE_PREF_FIELD_HTTP_IP_ADDR = "http_ip_addr";
    public static final String SHARE_PREF_FIELD_WEBSOCKET_SSL_IP_ADDR = "websocket_ssl_ip_addr";

    public static final String SHARE_PREF_CLOUDBRIDGE_STATE = "cloudbridge_stat";
    public static final String SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE = "cloudbridge_yestoday_stat";
    public static final String SHARE_PREF_SECURITY_ZONE_KEY = "security_zone_keyword";//安全区域中的关键key
    public static final String SHARE_PREF_SECURITY_ZONE_JASON_KEY = "security_zone_jason_keyword";//安全区域中的关键key Jason方式存储
    public static final String SHARE_TIME_CALL_OUTING_COUNT = "callphone_outing_count";
    public static final String SHARE_PREF_FIELD_IS_VALID_FAMILY = "is_valid_family";
    public static final String SHARE_PREF_EFID_GET_LAST_TIME = "efid_get_last_time";
    public static final String SHARE_PREF_EFID_IS_HAVE = "efid_IS_HAVE";
    public static final String SHARE_PREF_FIELD_DEV_SERVER_FLAG = "dev_server_flag";

    public static final String SHARE_PREF_CLEAN_CACHE_DATA_FOR_UPDATE = "clean_cache_data_for_update";
    public static final String SHARE_PREF_CLEAN_LOGIN_DATA_FOR_UPDATE = "clean_login_data_for_update";
    public static final String SHARE_PREF_BUILD_VERSION_SDK_INT = "build_version_sdk_int";
    public static final String SHARE_PREF_SIM_NOTICE_COMING = "sim_notice";
    //guide
    public static final String SHARE_PREF_FIRST_IN_HISTORY_RECORD = "first_in_hisrec";
    public static final String SHARE_PREF_IS_FIRST_MAIN = "first_choose_watch";
    public static final String SHARE_PREF_IS_FIRST_MAINMAP = "first_mainmap";
    public static final String SHARE_PREF_FIRST_IN_HISTORY_TRACE = "first_in_histra";
    public static final String SHARE_PREF_FIRST_IN_NOTICELIST = "first_in_noticelist";
    public static final String SHARE_PREF_FIRST_IN_NEW_WATCH = "first_in_new_watch";
    public static final String SHARE_PREF_CALL_BACK_NUMBER = "call_back_number";
    public static final String SHARE_PREF_CALL_BACK_ATTRI = "call_back_attri";
    public static final String SHARE_PREF_CALL_BACK_LAST_POSITION = "call_back_last_position";
    public static final String SHARE_PREF_CALL_BACK_STATE = "call_back_state";
    public static final String SHARE_PREF_LISTEN_STATE = "listen_state";
    public static final String SHARE_PREF_TIME_STAP = "time_stap";
    public static final String SHARE_PREF_EFID1_IMPORTENT_KEY = "efid1_importent_key";
    public static final String SHARE_PREF_EFID1_ASSISTANT_KEYS = "efid1_assistant_keys";
    public static final String SHARE_PREF_EFID1_IMPORTENT_KEY_TEMP = "efid1_importent_key_temp";
    public static final String SHARE_PREF_EFID1_ASSISTANT_KEYS_TEMP = "efid1_assistant_keys_temp";
    public static final String SHARE_PREF_EFID1_FIND_TIME = "efid1_find_time";
    public static final String SHARE_PREF_EFID1_UN_IMPORTENT_KEY = "efid1_un_importent_key";
    public static final String SHARE_PREF_EFID1_UN_ASSISTANT_KEYS = "efid1_un_assistant_keys";
    public static final String SHARE_PREF_EFID1_LAST_GET_TIME = "efid1_last_get_time";
    public static final String SHARE_PREF_FIELD_HAS_NEW_SPAM_SMS = "has_new_spam_sms";
    public static final String SHARE_PREF_AES_KEY="aes_key";
    public static String SHARE_PREF_KEY_FUNCTION_LIST = "function_list";
    public static String FUNCTION_NAME_DIALER = "dialer";
    public static String FUNCTION_NAME_CHATROOM = "chatroom";
    public static String FUNCTION_NAME_SPORT = "sport";
    public static String FUNCTION_NAME_SETTING = "setting";
    public static String FUNCTION_NAME_AUDIOPLAYER = "audioplayer";
    public static String FUNCTION_NAME_PETS = "pets";
    public static String FUNCTION_NAME_CAMERA = "camera";
    public static String FUNCTION_NAME_IMAGEVIEWER = "imageviewer";
    public static String FUNCTION_NAME_IMGRECOGNITION = "imgrecognition";
    public static String FUNCTION_NAME_ENGLISH = "english";
    public static String FUNCTION_NAME_AIVOICE = "aivoice";
    public static String FUNCTION_NAME_FRIENDS = "friends";
    public static String FUNCTION_NAME_QQ = "qq";
    public static String FUNCTION_NAME_ALIPAY = "alipay";
    public static String FUNCTION_NAME_NAVIGATION = "navigation";
    public static String FUNCTION_NAME_STOPWATCH = "stopwatch";

    public static String SHARE_PREF_SILENCE_MODE_KEY = "silence_mode";
    public static String SHARE_PREF_SILENCE_MODE_MORAFT_KEY = "silence_mode_mor_aft";
    public static String SHARE_PREF_SILENCE_NEW_MODE_KEY = "silence_mode_new";

    public static String SHARE_PREF_SLEEP_MODE_KEY = "sleep_mode";
    public static final String SHARE_PREF_DEVICE_REMIND_KEY="watch_remind_key";

    public static final String SHARE_PREF_CALL_LOG_DATA_FLAG = "call_log_data";
    public static final String SHARE_PREF_WATCH_DOWNLOAD_LIST = "watch_download_list";
    //track
    public static final String SHARE_PREF_ALLTRACK_STATUS = "all_track_status";
    public static final String SHARE_PREF_TRACK_STATUS = "track_status";
    public static final String SHARE_PREF_TRACE_TO_LOCAL_ENDTIME = "trace_to_local_endtime";
    public static final String SHARE_PREF_TRACE_CAN_VISIBLE= "trace_visible";

    public static final String SHARE_PREF_ALLDEVICES_LOC_TIME = "alldevices_loc_time";
    public static final String SHARE_PREF_ALLDEVICES_MODE = "alldevices_mode";

    public static final String SHARE_PREF_DEVICE_WIFI_LIST = "device_wifi_list";
    public static final String SHARE_PREF_AUTO_CONNECT_WIFI = "auto_connect_wifi";
    public static final String SHARE_PREF_KEEP_WIFI_CONNECT = "keep_wifi_connect";

    // 应用商店
    public static final String SHARE_PREF_APP_AUTO_UPDATE = "app_auto_update";

    public static final String INTENT_EXTRA_SIM_MSG = "sim_msg";

    public static final String SHARE_PREF_FIELD_STORY_VISIBLE = "story_visible";
    public static final String SHARE_PREF_FIELD_REPAIR_ONOFF = "repair_onoff";

    public static final String SHARE_PREF_FIELD_WATCH_FORECE_RECORD_STATE = "watch_force_record_state";
    public static final String SHARE_PREF_FIELD_WATCH_FORECE_TAKE_PHOTO_STATE = "watch_force_take_photo_state";

    public static final String SHARE_PREF_FIELD_HAS_NEW_GROUP_MESSAGE = "has_new_group_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_PRIVATE_MESSAGE = "has_new_private_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_LOCATION_MESSAGE = "has_new_location_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_BATTERY_MESSAGE = "has_new_battery_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_STEPS_MESSAGE = "has_new_steps_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_FAMILY_MEMBER_MESSAGE = "has_new_family_member_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_SMS_MESSAGE = "has_new_sms_message";
    public static final String SHARE_PREF_FIELD_HAS_NEW_NOTICE_MESSAGE = "has_new_notice_message";
    public static final String SHARE_PREF_LATEST_WATCH_CALLLOG_KEY = "latest_watch_calllog_contact";

    public static String SHARE_PREF_NOTICE_SETTING = "notice_setting";

    // 流量消耗记录
    public static String SHARE_PREF_OFFLINEMAP_SIZE_GAODE="offlinemap_size_gaode";
    public static String SHARE_PREF_OFFLINEMAP_SIZE_BAIDU="offlinemap_size_baidu";
    public static String SHARE_PREF_DOWNLOAD_NEWVERSION_SIZE = "download_newversion_size";

    public static String SHARE_PREF_OFFLINEMODE_PROMPT = "offlinemode_prompt";

    public static final int LOGIN_STATE_NULL = 0;
    public static final int LOGIN_STATE_CHECK_REG = 0x0100;
    public static final int LOGIN_STATE_REG_NEW = 0x0101;
    public static final int LOGIN_STATE_NEED_LOG = 0x0102;
    public static final int LOGIN_STATE_LOGIN = 0x0103;//保存登陆状态

    public static final int CHAT_INTERVAL_TIME = 5 * 60;  //间隔5分钟的聊天不显示发送时间
    //key 值定义
    public static final String KEY_VALUE_JSONOBJECT = "json_object";
    public static final String KEY_VALUE_LONGITUDE = "longitude";
    public static final String KEY_VALUE_LATITUDE = "latitude";
    public static final String KEY_VALUE_DESC = "description";
    public static final String KEY_VALUE_ACCURACY = "accuracy";

    public static final String KEY_FAMILY_ID = "family_id";
    public static final String KEY_WATCH_ID = "watch_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FAMILY_LIST_INDEX = "family_index";
    public static final String KEY_MEMBER_LIST_INDEX = "member_index";
    public static final String KEY_WATCH_LIST_INDEX = "watch_index";
    public static final String KEY_MSG_CONTENT = "msg_content";
    public static final String KEY_RESULT_CODE = "result_code";

    public static final String KEY_MEMBER_TYPE = "member_type";
    public static final String KEY_JSON_MSG = "json_msg";

    public static  final String KEY_SN_TYPE = "sn_type";

    public static final String BROADCAST_ACTION_QUIT_APP = "imibaby.quit";
    public static final String KEY_APP_UPGRADE_INFO = "app_update.info";

    public static final String KEY_NEXT_CHECK_UPDATE = "next.check.update";
    public static final String KEY_NEXT_APP_UPDATE_HINT = "next.app.updatehint";
    public static final String KEY_NEXT_WATCH_UPDATE = "next.watch.update";
    public static final String KEY_NEXT_WATCH_UPDATE_HINT = "next.watch.updatehint";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    //帮助页面类型  0，帮助页面；1，隐私政策；2，AI技能；3，AI统计
    public static final String KEY_WEB_TYPE = "web_type";
    public static final int KEY_WEB_TYPE_HELP = 0;
    public static final int KEY_WEB_TYPE_PRIVACY = 1;
    public static final int KEY_WEB_TYPE_AIVOICE = 2;
    public static final int KEY_WEB_TYPE_AISTATISTICS = 3;
    public static final int KEY_WEB_TYPE_ENGSTATISTICS = 4;
    public static final int KEY_WEB_TYPE_AIPERSONALIZE = 5;
    public static final String KEY_HELP_URL = "help_url";
    public static final String KEY_PARAMS = "params";
    public static final String KEY_AITYPE = "ai_type";

    public static final String SUFFIX_TMP_FILE = ".tmp";

    //actions 广播action统一定义到这里 以action开头，便于识别使用
    public static final String ACTION_OFFLINE_MAP_NOTIFY = ACTION_HEAD +"action.offlinemap.notify";
    public static final String ACTION_MAPMANAGER_FINISH_NOTIFY = ACTION_HEAD +"action.mapmanager.finish.notify";
    public static final String ACTION_REMOVE_FINISH_NOTIFY = ACTION_HEAD +"action.remove.finish.notify";
    public static final String ACTION_REMOVE_BEGIN_NOTIFY = ACTION_HEAD +"action.remove.begin.notify";
    public static final String ACTION_GET_EID_BY_SN_OK = ACTION_HEAD +"action.geteid.ok";
    public static final String ACTION_RECEIVE_CHAT_MSG = ACTION_HEAD +"action.receive.chatmsg";
    public static final String ACTION_BIND_RESULT_END = ACTION_HEAD +"action.receive.bindend";
    public static final String ACTION_PROCESSED_NOTIFY_OK = ACTION_HEAD +"action.processed.notify";
    public static final String ACTION_WARNNING_POWER_OK = ACTION_HEAD +"action.receive.power.ok";  //低电量接受完成
    public static final String ACTION_WARNNING_SOS_OK = ACTION_HEAD +"action.receive.sos.ok";   //sos接收完成
    public static final String ACTION_WARNNING_FAMILY_CHANGE_OK = ACTION_HEAD +"action.receive.family.change.ok"; //家庭成员变化通知接收完成
    public static final String ACTION_QUERY_ALL_GROUPS = ACTION_HEAD +"action.query.groups";
    public static final String ACTION_LOCATION_RESP = ACTION_HEAD +"action.location.resp";   //手表上报定位信息
    public static final String ACTION_WARNNING_TYPE_POWER = ACTION_HEAD +"action.warnning.power"; //低电量
    public static final String ACTION_WARNNING_TYPE_SOS = ACTION_HEAD +"action.warnning.sos"; //sos告警
    public static final String ACTION_WARNNING_TYPE_SAFEAREA = ACTION_HEAD +"action.warnning.safearea"; //安全区域
    public static final String ACTION_WARNNING_TYPE_SAFEDANGERDRAW = ACTION_HEAD +"action.warnning.safedangerdraw"; //安全危险区域进出提醒
    public static final String ACTION_RECEIVE_GROUP_CHANGE_MSG = ACTION_HEAD +"action.receive.groupchange";    //收到group变化通知广播
    public static final String ACTION_RECEIVE_BATTERY_LEVEL_CHANGE = ACTION_HEAD +"action.receive.batterychange";    //收到电量变化通知广播
    public static final String ACTION_GET_OFFLINE_CHAT_MSG = ACTION_HEAD +"action.get.offline.chat.msg";
    public static final String ACTION_RECEIVE_WATCH_STATE_CHANGE = ACTION_HEAD +"action.receive.watch.state.change";  //手表开关机状态发生变化 State 1：开机 2：关机 3：飞行模式
    public static final String ACTION_RECEIVE_NEW_LOCATION_NOTIFY = ACTION_HEAD +"action.receive.new.location.notify"; //接收到手表广播位置
    public static final String ACTION_RECEIVE_REQ_JOIN_WATCH_GROUP_RESP = ACTION_HEAD +"action.receive.join.watch.resp"; //接收到绑定设备应答通知
    public static final String ACTION_RECEIVE_ADD_NEW_FRIEND_RESP = ACTION_HEAD +"action.receive.add.new.friend.resp";
    public static final String ACTION_RECEIVE_REQ_ADD_NEW_FRIEND = ACTION_HEAD +"action.receive.request.add.new.friend";
    public static final String ACTION_MOVE_TASK_TO_FRONT = ACTION_HEAD + "action.move.task.To.front";

    public static final String ACTION_STOP_VOICE_ANIMATION = ACTION_HEAD +"action.stop.voice.animation";
    public static final String ACTION_ADAPTER_DATA_CHANGE = ACTION_HEAD +"action.adpater.data.change";
    public static final String ACTION_WATCH_INFO_CHANGE_NOTICE = ACTION_HEAD +"action.watch.change";//
    public static final String ACTION_USER_INFO_CHANGE_NOTICE = ACTION_HEAD +"action.user.change";//
    public static final String ACTION_LOOP_ALARM = ACTION_HEAD +"loopalarm";
    public static final String ACTION_HEART_BEAT_ALARM = ACTION_HEAD +"heart.beat.alarm";
    public static final String ACTION_ADD_WATCH_CONTACT = ACTION_HEAD +"action.add.watch.contact";//添加手表联系人
    public static final String ACTION_AUTO_LOCATION_ALARM = "com.imibaby.auto.location.alarm";
    public static final String ACTION_RECEIVE_SMS_MSG = ACTION_HEAD +"action.receive.sms";
    public static final String ACTION_RECEIVE_SILENCETIME_UPDATE = ACTION_HEAD +"action.silenecetime.update"; //防打扰时段数据变化
    public static final String ACTION_RECEIVE_NOTICE_MSG = ACTION_HEAD +"acion.notice.msg";
    public static final String ACTION_RECEIVE_DEVICE_WLAN_STATE = ACTION_HEAD +"action.receive.device.wlan.state";//接受到设备wlan状态
    public static final String ACTION_RECEIVE_GET_DEVICE_INFO = ACTION_HEAD +"action.receive.get.device.info";//请求设备信息通知
    public static final String ACTION_RECEIVE_SET_DEVICE_INFO = ACTION_HEAD +"action.receive.set.device.info";//设置设备信息返回消息通知
    public static final String ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE = ACTION_HEAD +"action.receive.set.device.info.change";//设置设备信息变化消息通知
    public static final String ACTION_RECEIVE_SEND_IMAGE_DATA = ACTION_HEAD +"action.receive.send.image.data";//上传头像返回消息通知
    public static final String ACTION_BAND_NETSERVICE_IS_OK = ACTION_HEAD +"action.band.netservice.is.ok";
    public static final String ACTION_DEVICE_OFFLINE_STATE_UPDATE = ACTION_HEAD +"action.device.state.update";
    public static final String ACTION_VIDEOCALL_ENDCALL = ACTION_HEAD + "action.videocall.endcall";

    public static final String KEY_WATCH_STATE = CloudBridgeUtil.KEY_NAME_WATCH_STATE;
    public static final String KEY_WATCH_STATE_TIMESTAMP = CloudBridgeUtil.KEY_NAME_WATCH_STATE_TIMESTAMP;
    public static final int WATCH_STATE_INIT = 0;// initial value
    public static final int WATCH_STATE_POWER_ON = 1;
    public static final int WATCH_STATE_POWER_OFF = 2;
    public static final int WATCH_STATE_FLIGHT = 3;
    public static final int WATCH_STATE_MAYBE_POWER_OFF = 4;    //app has not received battery notice after 24h
    public static final int WATCH_STATE_POWER_OFF_LOW_POWER = 7;// internal value for app,not saved in mapset
    public static final int WATCH_STATE_POWER_ON_BY_CHECK = 8;// internal value for app,not saved in mapset

    //alex watch status
    public static final int WATCH_CHARGE_IS_ON = 1;
    public static final int WATCH_CHARGE_IS_OFF = 0;

    public static final int WATCH_STATE_PARAM_BATTERY = 0;
    public static final int WATCH_STATE_PARAM_STATE = 1;
    public static final int WATCH_STATE_PARAM_TRACE = 2;


    public static final String ACTION_RECEIVE_CHANGE_AUDIO_MODE = ACTION_HEAD +"action.change.audio.mode";     //切换语音播放模式
    //
    public static final String ACTION_UNBIND_RESET_FOCUS_WATCH = ACTION_HEAD +"action.unbind.resetwatch";

    public static final String ACTION_UNBIND_OTHER_WATCH = ACTION_HEAD +"action.unbind.otherwatch";

    public static final String ACTION_BIND_NEW_WATCH = ACTION_HEAD +"action.bind.NEW.watch";

    //收到告警类消息
    public static final String ACTION_RECEIVE_ALERT_NOTICE = ACTION_HEAD +"action.receive.alertnotice";
    //收到请求加入群组消息
    public static final String ACTION_RECEIVE_REQ_JOIN_GROUP = ACTION_HEAD +"action.receive.reqjoingroup";
    //收到请求加入群组回复消息
    public static final String ACTION_RECEIVE_RESPONSE_JOIN_GROUP = ACTION_HEAD +"action.receive.resojoingroup";

    public static final String ACTION_REFRESH_ALL_GROUPS_DATA_NOW = ACTION_HEAD +"action.refresh.allgroups";//群组信息更新，需要ui各界面刷新
    public static final String ACTION_REFRESH_MESSAGE_LIST = ACTION_HEAD +"action.refresh.message.list";
    //掉线
    public static final String ACTION_NO_PONG_OFFLINE = ACTION_HEAD +"action.pong.offline";
    //掉线
    public static final String ACTION_CHANGE_WATCH = ACTION_HEAD +"action.change.watch";
    public static final String ACTION_APP_DOWNLOADED = ACTION_HEAD +"action.app.downloaded";
    public static final String ACTION_RESEND_CHAT = ACTION_HEAD +"action.resend.chat";
    public static final String ACTION_PLAY_RECORD_COMPLETION = ACTION_HEAD +"action.play.record.completion";
    //选择了需要下载的离线地图
    public static final String ACTION_SELECT_OFFLINE_MAP = ACTION_HEAD +"action.select.offline";
    //追踪模式下的数据通知
    public static final String ACTION_SELECT_TARCE_TO_MODE = "action.select.trace.to.mode";
    //下载头像ok
    public static final String ACTION_DOWNLOAD_HEADIMG_OK = ACTION_HEAD +"action.downlaod.headimg.ok";
    //升级app版本结果
    public static final String ACTION_APP_UPGRADE_RESULT = ACTION_HEAD +"action.upgrade.result";

    //升级watch版本结果
    public static final String ACTION_WATCH_UPGRADE_RESULT = ACTION_HEAD +"action.watch.upgrade.result";

    //升级app版本结果
    public static final String ACTION_WATCH_VERINFO_RESULT = ACTION_HEAD +"action.watch.verinfo.result";

    //获取watch groups结果
    public static final String ACTION_GET_WATCH_GROUPS_END = ACTION_HEAD +"action.watchgroups.end";
    //ota参数check
    public static final String ACTION_FIRMWARE_UPDATE_CHECKRESULT = ACTION_HEAD +"action.firmware.update.check";

    // APP执行手表关机后，手表在离线收到短信设置通知后返回的关机E2E结果
    public static final String ACTION_WATCH_SHUTDOWN_CHECKRESULT = ACTION_HEAD +"action.watch.shutdown.check";

    //APP执行闹钟设置/闹钟删除后,手表在离线收到短信设置通知后返回的设置的E2E结果
    public static final String ACTION_WATCH_ALARM_SETTING_CHECKRESULT = ACTION_HEAD +"action.watch.alarm.setting.check";
    public static final String ACTION_WATCH_ALARM_DELETE_CHECKRESULT = ACTION_HEAD +"action.watch.alarm.delete.check";

    //APP执行勿扰时段设置/删除/静音时段改变,手表在离线收到短信设置通知后后返回设置的E2E结果
    public static final String ACTION_WATCH_SLIENCE_TIME_SET_CHECKRESULT = ACTION_HEAD +"action.watch.slience_time.set.check";
    public static final String ACTION_WATCH_SLIENCE_TIME_CHANGED_CHECKRESULT = ACTION_HEAD +"action.watch.slience_time.changed.check";
    public static final String ACTION_WATCH_SLIENCE_TIME_DELETE_CHECKRESULT = ACTION_HEAD +"action.watch.slience_time.delete.check";
    public static final String ACTION_WATCH_HEALTH_DATA_NOTICE = ACTION_HEAD +"action.watch.health_data.notice";

    public static final String ACTION_RECEIVE_OTA_RESULT = ACTION_HEAD +"action.watch.ota.result";
    //ping delta
    public static final String ACTION_APP_PING_RESULT = ACTION_HEAD +"action.ping.result";
    //定位动画切花通知
    public static final String ACTION_LOCATION_ANIM_CHANGE = ACTION_HEAD +"action.location.anim.change";
    public static final String ACTION_LOCATION_OK_ANIM_CHANGE = ACTION_HEAD +"action.location.ok.anim.change";
    //刷新地图marker
    public  static final String ACTION_REFRESH_WATCH_MARKER = ACTION_HEAD +"action.referesh.watchmarker";

    //刷新主界面title
    public  static final String ACTION_REFRESH_WATCH_TITLE = ACTION_HEAD +"action.referesh.watchtitle";
    public static final String ACTION_CLOUD_BRIDGE_STATE_CHANGE = ACTION_HEAD +"action.cloud.bridge.state.change";
    //手表信号的更新
    public static final String ACTION_CLOUD_BRIDGE_SIGNAL_CHANGE = ACTION_HEAD +"action.cloud.bridge.signal.change";
    //手表网络状态的更新
    public static final String ACTION_WATCH_NET_STATE_CHANGE = ACTION_HEAD +"action.watch.net.state.change";
    //设置关系和号码结束
    public static final String ACTION_SET_RELATION_END = ACTION_HEAD +"action.set.relation.end";

    public  static final String ACTION_CHECK_WEBSOCKET_STATE = ACTION_HEAD +"action.check.websocket.state";

    //联系人发送变化
    public static final String ACTION_CONTACT_CHANGE = ACTION_HEAD +"action.contact.change";
    //增加联系人完成
    public static final String ACTION_ADD_CALLMEMBER_OK = ACTION_HEAD +"action.add.callmember";
    //发送添加好友请求成功
    public static final String ACTION_REQUEST_ADDFRIEND_OK = ACTION_HEAD +"action.request.addfriend";
    //install_app_list发生变化的通知
    public static final String ACTION_INSTALL_APPLIST_CHANGE = ACTION_HEAD +"action.install.applist.change";

    //手表下载发生变化
    public static final String ACTION_WATCH_DOWNLOAD_CHANGE = ACTION_HEAD +"action.watch.download.change";
    public static final String ACTION_STORY_VISIBLE_CHANGE = ACTION_HEAD +"action.story.visible.change";
    public static final String ACTION_WATCH_DOWNLOAD_NOTICE = ACTION_HEAD +"action.watch.download.notice";
    public static final String ACTION_WATCH_GET_STORY_LIST_7XX = ACTION_HEAD +"action.watch.get.story.list.7xx";
    public static final String ACTION_WATCH_DEL_STORY_CHOOSE_7XX = ACTION_HEAD +"action.watch.del.story.choose.7xx";

    //获取联系人成功
    public static final String ACTION_GET_CONTACT_SUCCESS = ACTION_HEAD +"action.get.contact.success";
    //电量更新时间间隔
    //通知栏点击清除
    public static final String ACTION_NOTIFICATION_CLEAR = ACTION_HEAD +"action.notification.clear";
    public static final String ACTION_NOTIFICATION_FRONTSERVICE = ACTION_HEAD +"action.notification.frontservice";

    public static final String ACTION_CLEAR_MESSAGE = ACTION_HEAD +"action.clear.message";
    public static final String ACTION_CLEAR_NOTICE_MESSAGE = ACTION_HEAD +"action.clear.notice.message";
    //计步发出的广播
    public static final String ACTION_BROAST_SENSOR_STEPS = ACTION_HEAD + "action.broast.sensor.steps";

    // 相册表盘删除广播
    public static final String ACTION_DIALBG_DELETE =  ACTION_HEAD + "action.dialbg.delete";
    // 小红点拉取成功
    public static final String ACTION_FUNCTION_REDDOT_REQ_SUCCESS = ACTION_HEAD+"action.reddot.reqsuccess";

    public static final String KEY_NOTIFICATION_ID = "key.notification.id";

    public static final long GET_WATCH_BATTERY_TIME = 5 * 60 * 1000;

    public static final long MAX_LOW_BATTERY_POWEROFF_TIME = 90 * 60 * 1000;

    //reset app
    public static final String ACTION_RECREATE_APP = ACTION_HEAD +"action.reset.app";
    // RESTART service
    public static final String ACTION_RESTART_SERVICE = ACTION_HEAD +"action.restart.service";
    public static final String MASK_TIME_KEY = "99999999999999999";
    public static final String FUTURE_TIME_KEY = "21000101010101001";
    public static final long YMD_REVERSED_MASK_8 = 99999999;
    public static final long HMSS_REVERSED_MASK_9 = 999999999;
    public static final String DEFAULT_NEXT_KEY = "**********";

    //步行路劲最大时长
    public static final float WALK_STAP_MAX_TIME = 30 * 60;
    //步行路劲最大距离
    public static final float WALK_STAP_MAX_DISTANCE = 1000;
    //规划路线颜色
    public static final int ROUTE_SALE_COLOR = 0Xff33b19e;
    //路线最小刷新距离间隔,单位  米
    public static final float ROUTE_REFRESH_MIN_DIS = 1;
    //路线规划最短刷新时间间隔，单位  毫秒
    public static final float ROUTE_REFRESH_MIN_TIME_SPLIT = 2000;
    //开始规划路线阈值
    public static final float ROUTE_PLAN_THRESHOLD = 200;

    //最低电量变化通知阈值
    public static final int BATTERY_MIN_LEVEL_VALUE = 25;
    public static final int BATTERY_MIN_LEVEL_VALUE_501 = 20;

    //低电量关机门限值
    public static final int BATTERY_LEVEL_VALUE_POWEROFF = 5;

    //手表设置防抖时间
    public static final int WATCH_OP_DEBOUNCE_TIME = 15 * 1000;// 10S
    //声音震动默认值
    public static final int DEFAULT_LIGHT_LEVEL = 10;
    public static final int DEFAULT_VOLUME_LEVEL = 6;
    public static final int DEFAULT_VOLUME_LEVEL_501 = 4;
    public static final int DEFAULT_REMIND_TYPE = 3;
    //一天时间毫秒数
    //一天时间毫秒数
    public static final long ONE_DAY_MILLISECOND = 24 * 60 * 60 * 1000;
    public static final long HALF_HOUR_MILLISECOND = 30 * 60 * 1000;


    //定位视图放缩级别精度阈值,单位米
    public static final int LOCATION_ZOOM_VALUE = 150;
    //定位操作锁定超时时间
    public static final int LOCATION_CLICK_CLOCK_VALUE = 60;
    public static final int LOCATION_CLICK_ANIM_VALUE = 3; //定位动画最长播放时间
    public static final int LOCATION_AFTER_SUCCESS_CLOCK_VALUE = 30; //定位成功后，再次定位的锁定时间
    //支付接口变量
    public static final String PAY_URL = "http://ppay";
    public static final String MB_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFDTgFtlQj5vaQvBPdocv9LlUsDRsP6PGj+/yn8rHiZEHjH8UnmOrG5Au0pMBnkYjk3f/uTYyEz2dqEMrmF7pg3c5XiRQew3JXyp4/d0KeT/ri+tp88yD1R2VjL33Q2aZ/VEsoYcoo0EpIoa9BRH7dc6G3W+g8myqOxit2skMQoQIDAQAB";
    public static final String KEY_WEBVIEW_URL = "key.webview.url";
    public static final String KEY_WEBVIEW_TITLE = "key.webview.title";

    public static final int UPDATE_DOWN_PROGRESS = 0x101;
    public static final int UPDATE_DOWN_OK = 0x102;
    public static final int UPDATE_DOWN_ERROR = 0x103;

    public static final int UPDATE_DOWN_APP_PROGRESS = 0x104;
    public static final int UPDATE_DOWN_APP_OK = 0x105;
    public static final int UPDATE_DOWN_APP_ERROR = 0x106;

    public static final int UPDATE_CONNECT_BLUETOOTH_TIMEOUT = 0x107;

    //通知栏消息id
    public static final int TITLE_BAR_LOWER_BATTERY = 10;
    public static final int TITLE_BAR_NEW_CHAT_MSG = 11;
    public static final int TITLE_BAR_ADD_FAMILY = 12;
    public static final int TITLE_BAR_SAFE_AREA_IN = 13;
    public static final int TITLE_BAR_SAFE_AREA_OUT = 14;
    public static final int SIM_NOTIFICATION_MSG_ACCOUNT = 15;
    public static final int SIM_NOTIFICATION_MSG_CALLERID = 16;
    public static final int SIM_NOTIFICATION_MSG_RECHARGE = 17;
    public static final int SIM_NOTIFICATION_MSG_BALANCE = 18;
    public static final int SIM_NOTIFICATION_MSG_IDENTITY_STATUS = 19;
    public static final int SIM_NOTIFICATION_MSG_PHONE_RETURN = 20;

    public static final int TITLE_BAR_NEW_SMS_MSG = 30;
    public static final int TITLE_BAR_NOTICE_LOW_BATTERY_MSG = 31;
    public static final int TITLE_BAR_NOTICE_STEPS_MSG = 32;
    public static final int TITLE_BAR_NOTICE_FAMILY_CHANGE = 33;
    public static final int TITLE_BAR_NOTICE_DOWNLOAD_MSG = 34;

    public static final int TITLE_BAR_NEW_GROUP_MESSAGE = 40;
    public static final int TITLE_BAR_NEW_PRIVATE_MESSAGE = 41;
    public static final int TITLE_BAR_NEW_NOTICE_MESSAGE = 42;


    public static final int RETRIEVE_TRACE_TIMEOUT = 20000;//10S //轮训间隔最小为20s。所以此处应为20s
    public static final int RETRIEVE_TRACE_COUNTER_TIMEOUT = 20000;//12S

    public static final int WATCH_BACKHOME_LOCATION_TIMEOUT = 60*2;// S
    public static final int WATCH_BACKHOME_LOCATION_FAIL_TIMEOUT = 60;


    // 可开启的最大安全区域数量
    public static final int MAX_SWTICHED_ON_SECURITY_ZONE_COUNT = 10;
    public static final int MAX_SECURITY_ZONE_RADIA = 500;


    // Max switched on alarm clock count.
    public static final int MAX_SWTICHED_ON_ALARMCLOCK_COUNT = 5;

    public static final String SILENCE_TIME_MORNING_TIMEID = "201509101229266151";
    public static final String SILENCE_TIME_AFTERNOON_TIMEID = "201509101229266152";
   // public static final String SHARE_PREF_ADULT_NUMBER_KEY = "adult_phone_number";
    public static final String SHARE_PREF_DEVICE_CONTACT_KEY = "device_contact";
    public static final String SHARE_PREF_UPDATE_DEVICE_CONTACT_TIME_KEY = "update_device_contact_time";
    public static final String SHARE_PREF_USERINFO_DEFAULT_HEADPATH = "userinfo_headpath";
    //语音缓存清理
    public static final int OLD_CHAT_DELETE_TIME = 30; //语音时间上限单位天
    public static final int OLD_CHAT_DELETE_COUNTER = 150; //语音条数上限
    public static final int HAS_REMOVE_CHAT_COUNTER = 30;//已删除语音保存条数上限

    //微信ID KEY
    public static final String WECHAT_APP_ID = ConstDiff.WECHAT_APP_ID;
    public static final String WECHAT_APP_SECRET = ConstDiff.WECHAT_APP_SECRET;
    //小米统计key
    public static final String MI_STATE_ID = ConstDiff.MI_STATE_ID;
    public static final String MI_STATE_KEY = ConstDiff.MI_STATE_KEY;
    public static final Long clientId = ConstDiff.clientId;

    public static final String QQ_APP_ID = ConstDiff.QQ_APP_ID;

    public static final int TIME_TO_SLEEP_WHEN_WAKEUP_IN_SLEEP_MODE = 10 * 60 * 1000;

    //首页定位动画相关
    public static  final int NORMAl_ANIM_PERIOD_TIME = 3*1000;
    public static  final int PHONE_LOCATION_TIME = 15*1000;
    public static  final int LOCATION_ANIM_TIME = 500;
    public static  final int LOCATION_AGGREGATION_DISTANCE = 50;
    //语音文件后缀名
    public static  final String VOICE_FILE_SUFFIX = ".amr.ini";
    public static  final String IMAGE_FILE_SUFFIX = ".png";
    public static final String VIDEO_FILE_SUFFIX = ".mp4";


	//来电显示状态
    public static  final int CALLER_ID_ON = 0;
    public static  final int CALLER_ID_OFF = 1;

    //安全区域本地学校更新周期
    public static final long PERMANENT_TIME = 60*1000*60*12;
//sim卡的认证状态变化刷新当前手表
    public static final String ACTION_SIM_CERTI_CHANGE_REFRESH_FOCUS_WATCH = "action.simcerti.refresh.focuswatch";

    //计步拉取日历显示天数
    public static final int STEPS_CALENDER_DATE_COUNT = 70;
    public static final String SETPS_CUR_TIMEKEY = "timeKeyValue";

    //service监控调用周期
    public static final int SERVICE_MONITOR_TIME = 10*60*1000;//单位ms

    //通话联系人总数限制
    public static final int CONTACT_MAX_LIMIT = 10;
    public static final int CONTACT_MAX_LIMIT_106 = 20;
    public static final int CONTACT_NUM_MAX_LIMIT = 40;
    public static final int CONTACT_GROUP_NUM_MAX_LIMIT = 10;

    public static final int NICKNAME_MAXLEN = 16;

    public static final int CALL_NICKNAME_MAXLEN = 256;

    //充电电量更新时间
    public static final int CHARGE_STATUS_CHECK_TIMEOUT = 30*60*1000;

    public static final String SHARE_PREF_SMS_NUMBER = "sms_number";

    //绑定设备最大个数
    public static final int DEVICE_MAX_LIMIT = 10;

    public static final int MESSAGE_TYPE_DEVICE_SMS = 10;

    public static final String SHARE_PREF_CURRENT_USER_REFLECT_ID = "current_user_reflect_id";

    // 记录用户拒绝升级的时间
    public static final String USER_NOT_UPDATETIME = "user_not_updatetime";
    //广告相关变量
    public static final String SHARE_PREF_AD_UPDATE_TIME = "share_pref_ad_update_time";
    public static final String SHARE_PREF_AD_UPDATE_DATA = "share_pref_ad_update_data";
    public static final String SHARE_PREF_AD_MAINPAGE_ONOFF = "share_pref_ad_mainpage_onoff";
    public static final String SHARE_PREF_AD_SPLASH_ONOFF = "share_pref_ad_splash_onoff";
    public static final String CHECK_AD_INTERVAL_TIME = "check_ad_interval_time";
    public static final String CHECK_AD_CURID_SHOWNUM = "check_ad_curid_shownum";
    public static final String CHECK_AD_MAIN_RESUME_INSURABLE_TIME = "check_ad_main_resume_insurable_time";
    public static final String SHARE_PREF_AD_HAVING_INSURABLE = "share_pref_ad_having_insurable";
    public static final String SHARE_PREF_AD_INSURABLE_PAST = "share_pref_ad_insurable_past";
    public static final String SHARE_PREF_AD_PUSH_VALUE = "share_pref_ad_push_value";
    public static final String SHARE_PREF_TOP_MESSAGE_INFO = "share_pref_top_message_info";
    public static final String SHARE_PREF_PRIVACY_POLICY_AGREED = "share_pref_privacy_policy_agreed";

    //小红点
    public static final String SHARE_PREF_FUNCTION_REDDOT = "share_pref_function_reddot";
    public static final String SHARE_PREF_FUNCTION_REDDOT_TIMESTAMP = "share_pref_function_reddot_timestamp";
    // 需要小红点的功能
    public static final String SHARE_PREF_FUNCTION_XIAOAI = "xiaoai";

    //记录设置是否成功的标识
    public static final String SETTING_RESULt = "setting_result";
    public static final int SETTING_SUCCESS = 0;
    public static final int SETTING_FAIL = 1;

    public static String IF_SCROLL_TO_BOTTOM="ifScrollToBottom";
    public static int IS_SCROLL_TO_BOTTOM=1;
    public static int SCROLL_TO_BOTTOM_POST_DELAYED=200;

    public static String SHARE_PREF_OFFLINEMAP_HAVE_NEWVERSION_FLAG="offlinemap_hasnew_flag";
    public static String SHARE_PREF_OFFLINEMAP_DOWNLOAD_SUCCESS_OR_CHECKUPDATE="offlinemap_download_success_or_checkupdate";
    public static long TIME_OFFLINEMAP_CHECK_UPDATE_INTERVAL = 30 * 24 * 60 * 60 ;    //检查离线地图更新的时间间隔 单位：秒

    public static String KEY_MAP_TIME="time";
    public static String KEY_MAP_TITLE="title";
    public static String KEY_MAP_STATUS="status";
    public static String KEY_MAP_IMG="img";
    public static String KEY_MAP_ALARMOBJECT="alarmObject";
    public static String KEY_MAP_SILENCEOBJECT="silenceObject";
    public static String KEY_MAP_SLEEPOBJECT="sleepObject";
    public static String KEY_EXTRA_BELLSELECT="bell_select";
    public static String KEY_MAP_WATCH_MODE_TITLE="mode_title";
    public static String KEY_MAP_WATCH_MODE_DETAIL="mode_detail";

    public static String APN_ID="id=";
    public static String APN_TYPE="apn=";
    public static String APN_PORT="proxy_port=";
    public static String APN_ACCOUTNAME="accountName=";
    public static String APN_HOMEPAGE="homepage=http://wap.uni-info.com.cn";
    public static String APN_TYPE_DEFAULT="default";
    public static String APN_IMEI_MD5_SALT="xiaoxun";

    public static String DEVICE_TYPE_102="SW102";
    public static String DEVICE_TYPE_105="SW105";
    public static String DEVICE_TYPE_302="SW302";
    public static String DEVICE_TYPE_303="SW303";
    public static String DEVICE_TYPE_501="SW501";

    //广告相关信息
    public static final String KEY_NAME_SPLASH_ON_OFF= "GLOBAL:splashOnOff:SW501_XUN";//"splashOnOff";
    public static final String KEY_NAME_MAIN_AD_ON_OFF= "GLOBAL:mainADOnOff:SW501_XUN";//"mainADOnOff";
    public static final String SHARE_PREF_FIELD_IS_SHOW_SATELLITE = "is_show_satellite";

    public static String XIMALAYA_APP_SECRET = "c2f21ca2940a954b7abbfbdd75e621d7";
    public static final String ACTION_CONTROL_PLAY_PAUSE = "com.ximalaya.ting.android.ACTION_CONTROL_START_PAUSE";
    public static final String ACTION_CONTROL_PLAY_NEXT = "com.ximalaya.ting.android.ACTION_CONTROL_PLAY_NEXT";
    public static final String SHARE_TO_QQ_URL = "http://www.xunkids.com/product_all.html";
    //计步排行相关设置
    public static final String SHARE_PREF_NO_SUPPORT_STEPS_FLAG = "share_pref_no_support_steps_flag";
    public static final String SHARE_PREF_PHONT_STEPS = "share_pref_phone_steps";
    public static final String SHARE_PREF_PHONT_STEPS_NEW = "share_pref_phone_steps_new";
    public static final String SHARE_PREF_RANKS_DATA_JSON = "share_pref_ranks_data_json";
    public static final String ACTION_CLOUD_BRIDGE_RANKS_DATA = ACTION_HEAD +"action.cloud.bridge.ranks.data";
    public static final String APP_RANK_HTTPS_TEST_URL = "https://steps-test.xunkids.com/steps";

    public static final String APP_RANK_AREA_HTTPS_TEST_URL = "https://steps-test.xunkids.com/dash";

    //故事历史搜索词
    public static final String SHARE_PREF_SEARCH_SUGGEST_WORD = "share_pref_search_suggest_word";
    public static final String SHARE_PREF_XIMALAYA_TRACK_ID = "ximalaya_track_id";
    public static final String SHARE_PREF_XIMALAYA_ALBUM_ID = "ximalaya_album_id";
    public static final String SHARE_PREF_IS_WATCH_DOWNLOAD_MESSAGE ="isWatchDownLoadMessage";
    //流量统计设置
    public static final String SHARE_PREF_FLOW_STATITICS = "share_pref_flow_statitics";
    //流量统计
    public static final String ACTION_BROAST_FLOW_STATITICS = ACTION_HEAD + "action.broast.flow.statitics";
    public static final String ACTION_BROAST_DISCOVERY_WARN = ACTION_HEAD + "action.broast.discovery.warn";
    public static final String ACTION_BROAST_DISCOVERY_FIND = ACTION_HEAD + "action.broast.discovery.find";
    public static final String DISCOVERY_WARN_INFO = "discovery_warn_info";

    // 检查更新
    public static final String CHECK_UPDATE_PROJECT = "project";
    public static final String CHECK_UPDATE_UNIKEY = "unikey";
    public static final String CHECK_UPDATE_VERSIONCODE = "versionCode";
    public static final String CHECK_UPDATE_VERSIONNAME = "versionName";
    public static final String CHECK_UPDATE_PACKAGENAME = "packageName";
    public static final String CHECK_UPDATE_DEVICETYPE = "deviceType";

    public static final int SECURITY_ZONE_CLEAR_DATA_VERSION = 7133;

    //oppo推送信息
    public static final String oppo_appkey = ConstDiff.oppo_appkey;
    public static final String oppo_appsecret = ConstDiff.oppo_appsecret;

    public static final String SET_CONTACT_ISBIND = "isBind";
    public static final String VIDEOCALL_TYPE = "videocall_type"; //0,发起呼叫；1，接收呼叫
    // 视频通话状态
    public static final int MESSAGE_CALL_INIT_STATE = 0;   //初始状态、通话结束
    public static final int MESSAGE_CALL_WAIT_STATE = 1;  //呼叫或被呼叫过程中
    public static final int MESSAGE_CALL_IN_CALL_STATE = 2;  //通话中
    // 视频通话参数
    public static final String VIDEO_CALL_PUSH_MESSAGE = "video_call_push_msg";
    //应用统计本地缓存
    public static final String SHARE_PREF_APP_STATITICS_CACHE = "share_pref_app_statitics_cache";

    public static final String ACTION_WATCH_NAVI_START = ACTION_HEAD + "action.watch.navi.start";
    public static final String ACTION_WATCH_NAVI_CURRENT_POINT = ACTION_HEAD + "action.watch.navi.cuerrent.point";
    public static final String ACTION_WATCH_NAVI_END = ACTION_HEAD + "action.watch.navi.end";

    //内容平台播放器
    public static final String SHARE_PREF_FIELD_ONLINE_MUSIC_DATA = "online_music_data";
    public static final String SHARE_PREF_FIELD_ONLINE_MUSIC_CURSONG = "online_music_cursong";

    public static final String ONLINE_MEDIA_CONTENT_URL = "https://story.xunkids.com/cmsfrontend/index";
    public static final String ONLINE_MEDIA_LIST_URL = "https://story.xunkids.com/cmsfrontend/media/";
    public static final String ONLINE_MEDIA_COVERURL = "online_media_coverurl";
    public static final String ONLINE_MEDIA_DURATION = "online_media_duration";
    public static final String ONLINE_MEDIA_PROVIDER = "online_media_provider";
    public static final String ONLINE_MEDIA_SIZE = "online_media_size";
    public static final String ONLINE_MEDIA_NAME = "online_media_name";
    public static final String ONLINE_MEDIA_PLAYURL = "online_media_playurl";

    public static final String MAPFRAGMENT_CONFIGURABLE_BTN_URL = "http://lbsyun.baidu.com/indoor/indoormap/Cooperative?invite_code=RTEVQATE";

    public static final String DEFAULT_AGREEMENT_URL = "https://app.xunkids.com/302_XUN/agreementOnly.html";
    public static final String DEFAULT_PRIVACY_URL = "https://app.xunkids.com/302_XUN/privacy.html";
}
