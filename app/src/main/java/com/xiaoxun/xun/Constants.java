package com.xiaoxun.xun;

import static com.xiaoxun.xun.Const.ACTION_HEAD;

public class Constants {

    public static final String SHARE_PREF_NAVIGATINO_AUTHORITY = "navi_authority";
    public static final int KEY_WEB_TYPE_DISCLAIMER = 8;
    public static final int KEY_WEB_TYPE_PRIVACY_POLICY = 9;
    public static final int KEY_WEB_TYPE_AGREEMENT = 10;
    public static final int OFFLINE_MESSAGE_COUNT_MAX = 1;
    public static final String APP_DISCLAIMER_URL = "https://app.xunkids.com/navigation/disclaimer.html ";
    public static final String APP_PRIVACY_POLICY_URL = "https://app.xunkids.com/uagreement";
    public static final String ACTION_GROUP_SEND_MESSAGE_NOTIFY = ACTION_HEAD + "action.group.send.message.notify";
    public static final String ACTION_PRIVATE_SEND_MESSAGE_NOTIFY = ACTION_HEAD + "action.private.send.message.notify";
    public static final String ACTION_RECEIVE_GROUP_MESSAGE_NOTIFY = ACTION_HEAD + "action.receive.group.message.notify";
    public static final String ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY = ACTION_HEAD + "action.receive.private.message.notify";
    public static final String ACTION_UPDATE_NEW_MESSAGE_NOTICE = ACTION_HEAD + "action.update.new.message.notice";

    public static final String KEY_NAME_AUTHORISE_HEARTRATE = "heartRate";
    public static final int PERMISSION_RESULT_INIT = 0xaa;
    public static final String KEY_NAME_AUTHORISE_WIFI = "wifi";
    public static final boolean CONTROL_IS_OPEN_AD = false;
    public static final String store_url = "store_url";
    public static final String login_channel = "channel";
    public static final String GOBIND="goBind";
    public static final String to_Url = "xunshoplogin://toUrl=";
    public static final String IMAGE_SELECT_MAX = "image_select_max";
    public static final String ACTION_DOMAIN_CHANGE = ACTION_HEAD + "action.domain.change";
    public static final String ACTION_PRIVACY_CHANGE = ACTION_HEAD + "action.privacy.change";
    //上课防打扰
    public static final String SILENCE_TIME_MORNING_TIMEID = "201509101229266151";
    public static final String SILENCE_TIME_AFTERNOON_TIMEID = "201509101229266152";
    public static final String SHARE_PREF_REFUSE_CLASS_DISTRUB_KEY = "refuse_class_distrub";
    public static final String SILENCE_TIME_NIGHT_TIMEID = "201509101229266153";

    public static final String APP_SCORE_TIMESTAMP = "timestamp";
    public static final String SHARE_PREF_OFFLINE_MESSAGE_COUNT = "offline_message_count";
    //个保法授权
    public static final String KEY_NAME_AUTHORISE = "auth_status";
    public static final String SHARE_PREF_AUTHORISE_WIFI = "share_pref_auth_wifi";    //wifi数据授权
    public static final String SHARE_PREF_AUTHORISE_APP_STORE = "share_pref_auth_app_store";    //应用中心数据授权
    public static final String SHARE_PREF_AUTHORISE_SPORT = "share_pref_auth_sport";    //运动数据授权
    public static final String SHARE_PREF_AUTHORISE_SPORT_GPS = "share_pref_auth_sport_gps";    //运动数据授权
    public static final String SHARE_PREF_AUTHORISE_HEART_RATE = "share_pref_auth_heart_rate";    //心率数据授权
    public static final String SHARE_PREF_AUTHORISE_AI_ENGLISH = "share_pref_auth_ai_english";    //AI英语数据授权
    public static final String SHARE_PREF_AUTHORISE_MIALBUM = "share_pref_auth_mialbum";    //小米云相册数据授权
    public static final String SHARE_PREF_AUTHORISE_MSG = "share_pref_auth_msg";    //动态数据授权
    public static final String SHARE_PREF_AUTHORISE_CALLLOG = "share_pref_auth_calllog";    //动态数据授权
    public static final String SHARE_PREF_AUTHORISE_TEMPTURE = "share_pref_auth_tempture";    //测温数据授权
    public static final String SHARE_PRIVATE_KEY = "share_private_key";    //隐私协议授权
    public static final String KEY_NAME_REGION_SELECTED = "region_selected";

    public static final String KEY_NAME_COUNTRY_SELECTED = "country_selected";
    public static final String KEY_NAME_WATCH_SELECTED = "watch_selected";
    //账号类型
    public static final String SHARE_PREF_FIELD_LOGIN_TYPE = "share_pref_login_type";
    public static final int SHARE_PREF_FIELD_LOGIN_TYPE_XUN = 1;
    public static final int SHARE_PREF_FIELD_LOGIN_TYPE_THRID = 0;
    public static final String SHARE_PREF_FIELD_LOGIN_TYPE_REQ_TIMESTAMP = "share_pref_login_type_req_timestamp";
    // 隐私安全提示
    public static final String SHARE_PREF_FIELD_PRIVACY_PROMPT = "share_pref_privacy_prompt";
    public static final String SHARE_PREF_FIELD_PRIVACY_AGREE = "share_pref_privacy_agree";
    public static final String SHARE_PREF_PUSH_MANAGE = "push_manage";
    public static final String CALENDAR_DATA = "calendar_data";
    public static final String CALENDAR_FIRST = "calendar_first";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String HCY_TOKEN = "hcy_token";
    public static final String WEEK_SIGN_INFO = "week_sign_info";
    public static final String SIGN_REMAKE_INFO = "sign_remake_info";
    public static final String BMI_SUGGEST = "bmi_suggest";
    //首页发现和商城
    public static String TYPE_FUN = "type_fun";
    public static String FIND_FUN = "find_type";
    public static String STORE_FUN = "store_type";
    public static final String FINAL_CHANNEL_MAIN = "channel_main";
    public static final String FINAL_CHANNEL_SUB_STORE = "channel_sub_store";
    public static final String FINAL_CHANNEL_SUB_FIND = "channel_sub_find";
    public static final String SZ_FIELD_BDLNG = "bdLng";
    public static final String SZ_FIELD_RADIUS = "Radius";
    public static final String SZ_FIELD_DESC = "Desc";
    public static final String SZ_FIELD_WGS84 = "wgs84";
    public static final String SZ_FIELD_LNG = "Lng";
    public static final String SZ_FIELD_CT = "coordinateType";
    public static final String SZ_FIELD_LAT = "Lat";
    public static final String SZ_FIELD_BDLAT = "bdLat";
    public static final String SZ_FIELD_NAME = "Name";
    public static final String SZ_FIELD_EFID = "Efid";
    public static final String SZ_FIELD_ONOFF = "onoff";
    // 页面跳转type
    public static final String EXTRA_TYPE_ACTIVITY = "type";
    public static final String EXTRA_EID_FROM_NOTIFY_CLICK = "eid_notify_click";

    //安全守护
    public static final String SHARE_PREF_IS_FIRST_TIME_TO_SECURITY = "is_first_time_to_security";//是否第一次进入新安全守护
    public static final String SHARE_PREF_SECURITY_MAIN_STATUS = "security_on_off_status";//安全守护开关状态缓存
    public static final String KEY_NAME_COM = "com";
    public static final String KEY_NAME_SCHOOL = "school";
    public static final String KEY_NAME_DANGER = "danger";
    public static final String KEY_NAME_SAFE = "safe";
    public static final String KEY_NAME_CITY = "city";

    //计步2.0版本的信息
    public static final String SPORT_CHALL_NAME = "name";
    public static final String SPORT_CHALL_COPYTEXT = "copytext";
    public static final String SPORT_CHALL_BRIGHTICON = "brighticon";
    public static final String SPORT_CHALL_GLOOMYICON = "gloomyicon";
    public static final String SPORT_CHALL_ISACTIVED = "isactived";
    public static final String SPORT_CHALL_ACTIVETIME = "activetime";
    public static final String SPORT_CHALL_ACTIVERATIO = "activeratio";
    public static final String SPORT_CHALL_BIGDETAILSICON = "detailsicon";
    public static final String SPORT_CHALL_MEDALTYPE = "medaltype";
    public static final String SPORT_CHALL_MEDALVALUE = "medalvalue";

    public static final String SPORT_LAST_RANK_0 = "rank_0";
    public static final String SPORT_LAST_RANK_1 = "rank_1";
    public static final String SPORT_LAST_RUNNING_STIME = "stime";
    public static final String SPORT_LAST_RUNNING_ETIME = "etime";
    public static final String SPORT_LAST_RUNNING_STEPS = "steps";
    public static final String SPORT_LAST_RUNNING_DISTANCE = "distance";
    public static final String SPORT_LAST_MEDAL_NAME = "name";
    public static final String SPORT_LAST_RANK = "latestrank";
    public static final String SPORT_RUNNING_TYPE = "type";
    public static final String SPORT_RUNNING_COUNT = "count";

    public static final String SPORT_DAYS_DATA_KEY = "k";
    public static final String SPORT_DAYS_DATA_VALUE = "v";
    public static final String SPORT_DAYS_DATA_TRUE_KEY = "k_t";
    public static final String SPORT_TOTAL_CALOR = "totalkilocalorie";
    public static final String SPORT_TOTAL_METER = "totalkilometer";

    public static final String SPORT_RANK_SELFSTEPS = "selfSteps";
    public static final String SPORT_RANK_LAMINATION = "lamination";
    public static final String SPORT_RANK_AVERAGESTEPS = "averageSteps";
    public static final String SPORT_RANK_RANK = "rank";
    public static final String SPORT_RANK_RANGERANK = "rangerank";
    public static final String SPORT_RANK_WEIGHT = "weight";
    public static final String SPORT_RANK_TYPE = "type";
    public static final String SPORT_RANK_HEIGHT = "height";
    public static final String WATCH_EID_DATA = "watchEid";

    public static final String VIDEOCALL_PARAMS = "videocall_params";
    public static final String VIDEOCALL_CMD_JOINVOIPROOM = "joinVoipRoom";
    public static final String VIDEOCALL_CMD_EXITVOIPROOM = "exitVoipRoom";

    //课程表
    public static final String SCHEDULE_CARD_INFO = "schedule_card_info";
    public static final String SCHEDULE_SETTING_FIRST = "schedule_setting_first";
    public static final String SCHEDULE_NOTICE_INFO = "schedule_notice_info";
    public static final String SHARE_PREF_FIELD_SCHEDULE_CARD_INFO = "schedule_card_info";

    // 表情
    public static final String EMOJI_CRY = "cry";
    public static final String EMOJI_NOSE = "nose";
    public static final String EMOJI_BASKETBALL = "basketball";
    public static final String EMOJI_SNOT = "snot";
    public static final String EMOJI_NAUGHTY = "naughty";
    public static final String EMOJI_SMILE = "smile";
    public static final String EMOJI_FLOWER = "flower";
    public static final String EMOJI_LAUGH = "laugh";
    public static final String EMOJI_MOON = "moon";
    public static final String EMOJI_RABBIT_ANGER = "Rabbit_anger";
    public static final String EMOJI_RABBIT_AWKWARD = "Rabbit_awkward";
    public static final String EMOJI_RABBIT_BAD_LAUGH = "Rabbit_bad_laugh";
    public static final String EMOJI_RABBIT_CRY = "Rabbit_cry";
    public static final String EMOJI_RABBIT_CUTE = "Rabbit_cute";
    public static final String EMOJI_RABBIT_DESPISE = "Rabbit_despise";
    public static final String EMOJI_RABBIT_HAPPY = "Rabbit_happy";
    public static final String EMOJI_RABBIT_LOVE = "Rabbit_love";
    public static final String EMOJI_RABBIT_SHY = "Rabbit_shy";
    public static final String EMOJI_RABBIT_SMILE = "Rabbit_smile";
    public static final String EMOJI_RABBIT_SURPRISED = "Rabbit_surprised";
    public static final String EMOJI_RABBIT_ZIBI = "Rabbit_zibi";

    public static final String SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE = "icon_name_table_update";
    public static final String SHARE_PREF_FIELD_PACKAGE_ICON_TABLE = "package_icon_table";
    public static final String SHARE_PREF_FIELD_ICON_NAME_TABLE_UPDATE_TIME = "icon_name_table_update_time";
    public static final String SHARE_PREF_FIELD_ICON_NAME_TABLE_LANG = "icon_name_table_lang";

    //远程挂失开关
    public static String SHARE_PREF_REMOTE_LOSS_KEY = "remote_loss";

    //超级省电
    public static final String SHARE_PREF_SUPER_POWER_SAVING = "super_power_saving";

}
