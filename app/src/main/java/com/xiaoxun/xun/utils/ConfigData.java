package com.xiaoxun.xun.utils;

/**
 * Created by xilvkang on 2017/11/13.
 */

public class ConfigData {
    private String deviceVersion = "";
    private String cfgVersion = "";
    //switch
    private boolean switch_trace = false;   //追踪
    private boolean switch_photo = false;   //拍照
    private boolean switch_story = false;   //故事
    private boolean switch_step = false;    //计步
    private boolean switch_function_control = false;    //功能控制
    private boolean switch_wifi = false;    //wifi
    private boolean switch_bootup = false;  //定时开机
    private boolean switch_light_sound_vib = false; //灯光声音振动
    private boolean switch_sms_whitelist = false;   //短信白名单
    private boolean switch_gallery = false; //相册
    private boolean switch_friends = false; //手表交友
    private boolean switch_wifi_download_device_software = false;   //手表仅在WiFi下下载手表固件
    private boolean switch_device_update_button = false;    //固件新版本更新按钮

    //setting
    private int setting_wihtelist = 1;  //短信白名单设置
    private int setting_callmember_max_number = 10; //通话成员最大数目

    //text
    private String txt_help_url = "";  //用户帮助url
    private String txt_light_sound_vib = "";    //灯光声音振动名字
    private String txt_anti_disturb = "";   //防打扰文案

    public ConfigData(String deviceType){
        deviceVersion = deviceType;
    }

    public void setCfgVersion(String ver){
        cfgVersion = ver;
    }
    public String getCfgVersion(){
        return cfgVersion;
    }
    public String getDeviceVersion(){
        return deviceVersion;
    }
    public void setSwitch_trace(boolean f){
        switch_trace = f;
    }
    public boolean getSwitch_trace(){
        return switch_trace;
    }
    public void setSwitch_photo(boolean f){
        switch_photo = f;
    }
    public boolean getSwitch_photo(){
        return switch_photo;
    }
    public void setSwitch_story(boolean f){
        switch_story = f;
    }
    public boolean getSwitch_story(){
        return switch_story;
    }
    public void setSwitch_step(boolean f){
        switch_step = f;
    }
    public boolean getSwitch_step(){
        return switch_step;
    }
    public void setSwitch_function_control(boolean f){
        switch_function_control = f;
    }
    public boolean getSwitch_function_control(){
        return switch_function_control;
    }
    public void setSwitch_wifi(boolean f){
        switch_wifi = f;
    }
    public boolean getSwitch_wifi(){
        return switch_wifi;
    }
    public void setSwitch_bootup(boolean f){
        switch_bootup = f;
    }
    public boolean getSwitch_bootup(){
        return switch_bootup;
    }
    public void setSwitch_light_sound_vib(boolean f){
        switch_light_sound_vib = f;
    }
    public boolean getSwitch_light_sound_vib(){
        return switch_light_sound_vib;
    }
    public void setSwitch_sms_whitelist(boolean f){
        switch_sms_whitelist = f;
    }
    public boolean getSwitch_sms_whitelist(){
        return switch_sms_whitelist;
    }
    public void setSwitch_gallery(boolean f){
        switch_gallery = f;
    }
    public boolean getSwitch_gallery(){
        return switch_gallery;
    }
    public void setSwitch_friends(boolean f){
        switch_friends = f;
    }
    public boolean getSwitch_friends(){
        return switch_friends;
    }
    public void setSwitch_wifi_download_device_software(boolean f){
        switch_wifi_download_device_software = f;
    }
    public boolean getSwitch_wifi_download_device_software(){
        return switch_wifi_download_device_software;
    }
    public void setSwitch_device_update_button(boolean f){
        switch_device_update_button = f;
    }
    public boolean getSwitch_device_update_button(){
        return switch_device_update_button;
    }
    public void setSetting_wihtelist(int mode){
        setting_wihtelist = mode;
    }
    public int getSetting_wihtelist(){
        return setting_wihtelist;
    }
    public void setSetting_callmember_max_number(int num){
        setting_callmember_max_number = num;
    }
    public int getSetting_callmember_max_number(){
        return setting_callmember_max_number;
    }
    public void setTxt_help_url(String s){
        txt_help_url = s;
    }
    public String getTxt_help_url(){
        return txt_help_url;
    }
    public void setTxt_light_sound_vib(String s){
        txt_light_sound_vib = s;
    }
    public String getTxt_light_sound_vib(){
        return txt_light_sound_vib;
    }
    public void setTxt_anti_disturb(String s){
        txt_anti_disturb = s;
    }
    public String getTxt_anti_disturb(){
        return txt_anti_disturb;
    }
}
