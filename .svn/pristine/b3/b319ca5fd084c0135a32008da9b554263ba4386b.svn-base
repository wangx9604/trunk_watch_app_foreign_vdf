package com.xiaoxun.xun.beans;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Base64;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;

import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 设备wifi Bean
 * Created by huangyouyang on 16/7/5.
 */
public class DeviceWifiBean2 implements Serializable {

    public final static long serialVersionUID = 201911050951521L;

    public int type;  //0,wifi; 1,error desc
    public static final int TYPE_WIFI = 0;
    public static final int TYPE_ERROR_DESC = 1;
    public static final int TYPE_TITLE = 2;

    /*{
        "EID":"BAE73BE9E6B4BEF605CE787B8ACCD6B4",
        "wifiId":"20191029134734120",  //作为唯一值，时间戳app端第一次set次生成，后续都不允许被修改
        "bssid":"28:6c:07:31:77:e5",  //手动添加的wifi，写为空字符串
        "ssid:"xiaoxun",
        "pwd":"",   //实际密码，以『手表eid前16位』为密钥进行AES加密后转Base64（示例代码见下文）
        "auth_type": 9,  //wifi鉴权类型
        "strength":10,  //wifi强度
        "frequency":2412,  //频率
        "optype":0,  //操作类型。0新增1修改2删除
        "status":0,  //状态。0，正在发送给手表；1，等待手表验证；2，验证通过；3，验证失败
        "errorcode":0,  //失败原因，status=3时才有意义。1，密码错误 ……。默认值0，统称“连接失败”
        "updateTS":"20181220164734110"  //更新时间戳，服务端自行插入修改
    }*/
    public String wifiId = "";
    public String bssid = "";
    public String ssid = "";
    public String pwd = "";
    public int auth_type = 9;
    public int frequency = 2412;
    public int status = 0;
    public int errorcode = 0;
    public int strength = 0;

    public boolean isFree;
    public boolean isShow;
    public String errorDesc;
    public String title;

    public static DeviceWifiBean2 convertLocalWifiToWifiBean(String title, ScanResult scanResult) {

        DeviceWifiBean2 deviceWifiBean2 = new DeviceWifiBean2();
        deviceWifiBean2.type = TYPE_WIFI;
        deviceWifiBean2.bssid = scanResult.BSSID;
        deviceWifiBean2.ssid = scanResult.SSID;
        deviceWifiBean2.strength = calculateWifiStrength(scanResult.level);
        deviceWifiBean2.isFree = calculateIsFree(scanResult.capabilities);
        deviceWifiBean2.auth_type = calculateAuthType(scanResult.capabilities);
        deviceWifiBean2.frequency = scanResult.frequency;
        deviceWifiBean2.isShow = deviceWifiBean2.auth_type == 0 || deviceWifiBean2.auth_type == 1
                || deviceWifiBean2.auth_type == 4 || deviceWifiBean2.auth_type == 9;
        deviceWifiBean2.title = title;
        return deviceWifiBean2;
    }

    public static DeviceWifiBean2 convertWatchWifiToWifiBean(String title, JSONObject wifiObject) {

        DeviceWifiBean2 deviceWifiBean2 = new DeviceWifiBean2();
        deviceWifiBean2.type = TYPE_WIFI;
        deviceWifiBean2.bssid = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID);
        deviceWifiBean2.ssid = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
        deviceWifiBean2.strength=calculateWifiStrength((int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_STRENGTH));
        deviceWifiBean2.isFree = !(Boolean) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_ISFREE);
        deviceWifiBean2.auth_type = (int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_TYPE);
        deviceWifiBean2.isShow= deviceWifiBean2.auth_type == 0 || deviceWifiBean2.auth_type == 1
                || deviceWifiBean2.auth_type == 4 || deviceWifiBean2.auth_type == 9;
        deviceWifiBean2.title = title;
        return deviceWifiBean2;
    }

    public static DeviceWifiBean2 convertServerWifiToWifiBean(JSONObject wifiObject) {

        DeviceWifiBean2 deviceWifiBean2 = new DeviceWifiBean2();
        deviceWifiBean2.wifiId = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_ID);
        deviceWifiBean2.bssid = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_BSSID);
        deviceWifiBean2.ssid = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_SSID);
        deviceWifiBean2.pwd = (String) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_PWD2);
        deviceWifiBean2.frequency = (int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_FREQUENCY);
        deviceWifiBean2.auth_type = (int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_TYPE);
        deviceWifiBean2.status = (int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_STATE);
        if (wifiObject.containsKey(CloudBridgeUtil.KEY_DEVICE_WIFI_ERROR))
            deviceWifiBean2.errorcode = (int) wifiObject.get(CloudBridgeUtil.KEY_DEVICE_WIFI_ERROR);
        return deviceWifiBean2;
    }

    public static DeviceWifiBean2 createWifiErrorDesc(String title,String desc) {

        DeviceWifiBean2 deviceWifiBean2 = new DeviceWifiBean2();
        deviceWifiBean2.type = TYPE_ERROR_DESC;
        deviceWifiBean2.title = title;
        deviceWifiBean2.errorDesc = desc;
        return deviceWifiBean2;
    }

    public static DeviceWifiBean2 createWifiTitle(String title) {
        DeviceWifiBean2 deviceWifiBean2 = new DeviceWifiBean2();
        deviceWifiBean2.type = TYPE_TITLE;
        deviceWifiBean2.title = title;
        return deviceWifiBean2;

    }

    private static boolean calculateIsFree(String capabilities){

        if(capabilities.contains("WEP")||capabilities.contains("PSK")||capabilities.contains("EAP"))
            return false;
        return true;
    }

    private static int calculateAuthType(String capabilities){

        int auth_type;
        if (capabilities.contains("WAPI-PSK")) {
            auth_type = 4;  //
        } else if (capabilities.contains("WAPI-CERT")) {
            auth_type = 5;
        } else {
            if (capabilities.contains("WEP"))
                auth_type = 1;  //
            else if (capabilities.contains("PSK"))
                auth_type = 9;  //
            else if (capabilities.contains("EAP"))
                auth_type = 3;
            else
                auth_type = 0;
        }
        return auth_type;
    }

    private static int calculateWifiStrength(int rssi){

        int strength;
        if (rssi > -40) {
            strength = 5;
        } else if (rssi > -60) {
            strength = 4;
        } else if (rssi > -70) {
            strength = 3;
        } else if (rssi > -80) {
           strength = 2;
        } else {
            strength = 1;
        }
        return strength;
    }

    public static String calculateWifiStatusDesc(Context context,int status){

        String statusDesc;
        switch (status){
            case 0:
                statusDesc=context.getString(R.string.wifi_state_send_to_device);
                break;
            case 1:
                statusDesc = context.getString(R.string.wifi_state_verify_by_device);
                break;
            case 2:
                statusDesc = context.getString(R.string.wifi_state_verify_success);
                break;
            case 3:
                statusDesc = context.getString(R.string.wifi_state_verify_fail);
                break;
            default:
                statusDesc=context.getString(R.string.wifi_state_send_to_device);
                break;
        }
        return statusDesc;
    }

    public static String encryptPwd(String sSrc, String sKey) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
