package com.xiaoxun.xun.utils;

import android.content.Context;
import android.os.Environment;

import com.xiaoxun.xun.Const;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xilvkang on 2017/11/13.
 */

public class ConfigUtil {
    public static final String TAG = "ConfigUtil";

    public static final String FILE_NAME = "appCfg.cfg";
    public static final String LOC_CONFIG_PATH = Const.MY_BASE_DIR + "/" + FILE_NAME;

    /**
     * 解析Asserts下的配置文件
     * @param ctxt 应用句柄
     * @param deviceType 设备项目号
     * @return 配置对象
     */
    private static ConfigData analysisAssertsCfg(Context ctxt,String deviceType){
        ConfigData cfgData = new ConfigData(deviceType);
        try {
            InputStream is = ctxt.getAssets().open(FILE_NAME);
            int len = is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            String result = new String(buffer);

            JSONObject pl = (JSONObject)JSONValue.parse(result);
            String cfgver = (String)pl.get("cfgVersion");
            cfgData.setCfgVersion(cfgver);
            JSONArray switchpl = (JSONArray)pl.get("switch");
            analysisSwitch(cfgData,switchpl);

            JSONArray settingpl = (JSONArray)pl.get("setting");
            analysisSetting(cfgData,settingpl);

            JSONArray textpl = (JSONArray)pl.get("text");
            analysisText(cfgData,textpl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfgData;
    }

    /**
     * 解析开关类型的数据
     * @param data 需要写入配置值得对象
     * @param list 需要解析的Json
     */
    private static void analysisSwitch(ConfigData data,JSONArray list){
        for(int i=0;i<list.size();i++){
            JSONObject item = (JSONObject)list.get(i);
            String name = (String)item.get("name");
            if(name.equals("switch_trace")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_trace(true);
                } else {
                    data.setSwitch_trace(false);
                }
            }else if(name.equals("switch_photo")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_photo(true);
                } else {
                    data.setSwitch_photo(false);
                }
            }else if(name.equals("switch_story")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_story(true);
                } else {
                    data.setSwitch_story(false);
                }
            }else if(name.equals("switch_step")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_step(true);
                } else {
                    data.setSwitch_step(false);
                }
            }else if(name.equals("switch_function_control")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_function_control(true);
                } else {
                    data.setSwitch_function_control(false);
                }
            }else if(name.equals("switch_wifi")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_wifi(true);
                } else {
                    data.setSwitch_wifi(false);
                }
            }else if(name.equals("switch_bootup")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_bootup(true);
                } else {
                    data.setSwitch_bootup(false);
                }
            }else if(name.equals("switch_light_sound_vib")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_light_sound_vib(true);
                } else {
                    data.setSwitch_light_sound_vib(false);
                }
            }else if(name.equals("switch_sms_whitelist")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_sms_whitelist(true);
                } else {
                    data.setSwitch_sms_whitelist(false);
                }
            }else if(name.equals("switch_gallery")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_gallery(true);
                } else {
                    data.setSwitch_gallery(false);
                }
            }else if(name.equals("switch_friends")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_friends(true);
                } else {
                    data.setSwitch_friends(false);
                }
            }else if(name.equals("switch_wifi_download_device_software")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_wifi_download_device_software(true);
                } else {
                    data.setSwitch_wifi_download_device_software(false);
                }
            }else if(name.equals("switch_device_update_button")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                if (ret.equals("1")) {
                    data.setSwitch_device_update_button(true);
                } else {
                    data.setSwitch_device_update_button(false);
                }
            }
        }
    }

    /**
     * 解析设置类型的数据
     * @param data 需要写入配置值得对象
     * @param list 需要解析的Json
     */
    private static void analysisSetting(ConfigData data,JSONArray list){
        for(int i=0;i<list.size();i++){
            JSONObject item = (JSONObject)list.get(i);
            String name = (String) item.get("name");

            if(name.equals("setting_wihtelist")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                data.setSetting_wihtelist(Integer.valueOf(ret));
            }else if(name.equals("setting_callmember_max_number")) {
                String ret = getDataFromKey(data.getCfgVersion(), item);
                data.setSetting_callmember_max_number(Integer.valueOf(ret));
            }
        }
    }

    /**
     * 解析文案类型的数据
     * @param data 需要写入配置值得对象
     * @param list 需要解析的Json
     */
    private static void analysisText(ConfigData data,JSONArray list){
        for(int i=0;i<list.size();i++){
            JSONObject item = (JSONObject)list.get(i);
            String name = (String) item.get("name");
            if(name.equals("txt_help_url")){
                String ret = getDataFromKey(data.getCfgVersion(),item);
                data.setTxt_help_url(ret);
            }else if(name.equals("txt_light_sound_vib")){
                String ret = getDataFromKey(data.getCfgVersion(),item);
                data.setTxt_light_sound_vib(ret);
            }else if(name.equals("txt_anti_disturb")){
                String ret = getDataFromKey(data.getCfgVersion(),item);
                data.setTxt_anti_disturb(ret);
            }
        }
    }

    private static String getDataFromKey(String key,JSONObject obj){
        if(obj.containsKey(key)){
            return (String)obj.get(key);
        }else{
            return "";
        }
    }
}
