/**
 * Creation Date:2015-1-7
 * 
 * Copyright 
 */
package com.xiaoxun.xun;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xiaoxun.xun.utils.LogUtil;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-7
 * 
 */
public class Params {

    public static final String  PACKAGE_NAME_MITU = "com.imibaby.client";
    public static final String  PACKAGE_NAME_XUN = "com.xiaoxun.xunoversea";
    public static final String  PACKAGE_NAME_XTR = "com.xiaotongren.watch";

    private static Params instance;

    private Context context;
    private SharedPreferences prefs;

    private String projectID;
    private String channelID;   
    private String osVersionName;
    private String userAgent;
    private String screenWidth;
    private String screenHeight;
    private List<String> imsiList;
    private List<String> imeiList;
    private String plmn;
    private String smsc;
    private String appID;
    private int appVersion;
    private String appVerName;
    private String mcc;
    private String mnc;
    private String cid;
    private String lac;
    private String deviceCode;
    private int ramSize;
    private int romSize;
    private long sdcardSize;    
    private String uid;

    public int getScreenWidthInt() {
        if (screenWidthInt == 0) {
            getScreenResolution();
        }
        return screenWidthInt;
    }

    public int getScreenHeightInt() {
        if (screenHeightInt == 0) {
            getScreenResolution();
        }
        return screenHeightInt;
    }

    private int screenWidthInt;
    private int screenHeightInt;    
    private Params(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(Params.class.getName(), 0);
    }

    public static Params getInstance(Context context) {
        if (instance == null) {
            instance = new Params(context);
        }
        return instance;
    }

    public String getProjectID() {
        if (projectID == null) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                projectID = info.metaData.getString("project_id");
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return projectID;
    }
    
    public String getChannelID() {
        if (channelID == null) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                channelID = info.metaData.getString("TD_CHANNEL_ID");
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return channelID;
    }
    public String getOsVersionName() {
        if (osVersionName == null) {
            osVersionName = "android" + Build.VERSION.RELEASE;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) 
        {
            
        }
        return osVersionName;
    }

    public String getUserAgent() {
        if (userAgent == null) {
            userAgent = Build.MANUFACTURER.replaceAll("_", "\\\\_") + "_" + Build.BRAND.replaceAll("_", "\\\\_") + "_"
                    + Build.MODEL.replaceAll("_", "\\\\_");
        }
        return userAgent;
    }

    public String getScreenWidth() {
        if (screenWidth == null) {
            getScreenResolution();
        }
        return screenWidth;
    }

    public String getScreenHeight() {
        if (screenHeight == null) {
            getScreenResolution();
        }
        return screenHeight;
    }

    private void getScreenResolution() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        screenWidth = String.valueOf(metrics.widthPixels);
        screenHeight = String.valueOf(metrics.heightPixels);
        screenWidthInt = metrics.widthPixels;
        screenHeightInt = metrics.heightPixels;     
    }

    public synchronized List<String> getImsiList() {
        if (imsiList == null) {
            imsiList = new ArrayList<String>();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getSubscriberIdGemini = null;
            Method getSubscriberId = null;
            try {
                getSubscriberIdGemini = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (getSubscriberIdGemini == null) {
                try {
                    getSubscriberId = TelephonyManager.class.getDeclaredMethod("getSubscriberId", int.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (getSubscriberIdGemini != null) {
                    String imsi = (String) getSubscriberIdGemini.invoke(tm, 0);
                    if (imsi != null) {
                        imsiList.add(imsi);
                    }
                    imsi = (String) getSubscriberIdGemini.invoke(tm, 1);
                    if (imsi != null) {
                        imsiList.add(imsi);
                    }
                } else if (getSubscriberId != null) {
                    String imsi = (String) getSubscriberId.invoke(tm, 0);
                    if (imsi != null) {
                        imsiList.add(imsi);
                    }
                    imsi = (String) getSubscriberId.invoke(tm, 1);
                    if (imsi != null) {
                        imsiList.add(imsi);
                    }
                } else {
                    String imsi = tm.getSubscriberId();
                    if (imsi != null) {
                        imsiList.add(imsi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imsiList.size() <= 0) {
                imsiList.add("111111111111111");
            }
        }
        return imsiList;
    }

    public synchronized List<String> getImeiList() {
        if (imeiList == null) {
            imeiList = new ArrayList<String>();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getDeviceIdGemini = null;
            Method getDeviceId = null;
            try {
                getDeviceIdGemini = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (getDeviceIdGemini == null) {
                try {
                    getDeviceId = TelephonyManager.class.getDeclaredMethod("getDeviceId", int.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (getDeviceIdGemini != null) {
                    String imei = (String) getDeviceIdGemini.invoke(tm, 0);
                    if (imei != null) {
                        imeiList.add(imei);
                    }
                    imei = (String) getDeviceIdGemini.invoke(tm, 1);
                    if (imei != null) {
                        imeiList.add(imei);
                    }
                } else if (getDeviceId != null) {
                    String imei = (String) getDeviceId.invoke(tm, 0);
                    if (imei != null) {
                        imeiList.add(imei);
                    }
                    imei = (String) getDeviceId.invoke(tm, 1);
                    if (imei != null) {
                        imeiList.add(imei);
                    }
                } else {
                    String imei = tm.getDeviceId();
                    if (imei != null) {
                        imeiList.add(imei);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imeiList.size() <= 0) {
                imeiList.add("222222222222222");
            }
        }
        return imeiList;
    }

    public String getPlmn() {
        if (plmn == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            plmn = tm.getNetworkOperator();
        }
        return plmn;
    }

    public String getSmsc() {
        if (smsc == null) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                        new String[] { "service_center" }, null, null, "date DESC LIMIT 10");
                while (cursor.moveToNext()) {
                    smsc = cursor.getString(0);
                    if (smsc != null && !smsc.equals("")) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return smsc;
    }

    public String getAppID() {
        if (appID == null) {
            appID = "10003018";
        }
        return appID;
    }

    public int getAppVersion() {
        if (appVersion == 0) {
//          String sdkVersionCode = context.getString(MyR.string.com_android_miogameplat_version);
//          appVersion = Integer.valueOf(sdkVersionCode);
            try {
                appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode ; 
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return appVersion;
    }
    public String getAppVerName() {
        if (appVerName == null) {
//          String sdkVersionCode = context.getString(MyR.string.com_android_miogameplat_version);
//          appVersion = Integer.valueOf(sdkVersionCode);
            try {
                appVerName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName ; 
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return appVerName;      
    }
    public String getMcc() {
        if (mcc == null) {
            getLocation();
        }
        return mcc;
    }

    public String getMnc() {
        if (mnc == null) {
            getLocation();
        }
        return mnc;
    }

    public String getCid() {
        if (cid == null) {
            getLocation();
        }
        return cid;
    }

    public String getLac() {
        if (lac == null) {
            getLocation();
        }
        return lac;
    }

    private void getLocation() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String plmn = telephonyManager.getNetworkOperator();
        if (plmn != null && plmn.length() > 3) {
            mcc = plmn.substring(0, 3);
            mnc = plmn.substring(3);
        }
        GsmCellLocation cellLocation = null;
        if (telephonyManager.getCellLocation() instanceof GsmCellLocation) {
            cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        }
        if (cellLocation != null) {
            lac = String.valueOf(cellLocation.getLac());
            cid = String.valueOf(cellLocation.getCid());
        }
    }

    public String getDeviceCode() {
        if (deviceCode == null) {
            deviceCode = prefs.getString("deviceCode", null);
            if (deviceCode == null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = getImeiList().toString();
                if (imei == null) {
                    imei = "000000000000000"; // 15
                }
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                String wifiMac = wm.getConnectionInfo().getMacAddress();
                if (wifiMac == null) {
                    wifiMac = "0000000000000000"; // 16
                }
                String btMac;
                try {
                    BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                    btMac = ba.getAddress();
                    if (btMac == null) {
                        btMac = "0000000000000000"; // 16
                    }           
                } catch (Exception e) {
                    btMac = "0000000000000000";
                }
    
                String cpuSerial = getCPUSerial();
                JSONObject json = new JSONObject();
                try {
                    json.put("imei", imei);
                    json.put("wifiMac", wifiMac);
                    json.put("btMac", btMac);
                    json.put("cpuSerial", cpuSerial);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deviceCode = json.toString();
                prefs.edit().putString("deviceCode", deviceCode).commit();
            }
        }
        return deviceCode;
    }

    private String getCPUSerial() {
        String cpuAddress = "9999999999999999";// 16
        InputStreamReader ir = null;
        LineNumberReader input = null;
        try {
            LogUtil.w("cat /proc/cpuinfo");
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            ir = new InputStreamReader(pp.getInputStream());
            input = new LineNumberReader(ir);
            for (int i = 0; i < 100; i++) {
                String str = input.readLine();
                LogUtil.w(str);
                if (str != null) {
                    if (str.indexOf("Serial") > -1) {
                        String strCPU = str.substring(str.indexOf(":") + 1);
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cpuAddress;
    }

    public int getRamSize() {
        if (ramSize == 0) {
            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                LogUtil.w("cat /proc/meminfo");
                Process pp = Runtime.getRuntime().exec("cat /proc/meminfo");
                ir = new InputStreamReader(pp.getInputStream());
                input = new LineNumberReader(ir);
                for (int i = 0; i < 100; i++) {
                    String str = input.readLine();
                    LogUtil.w(str);
                    if (str != null) {
                        if (str.indexOf("MemTotal") > -1) {
                            String memTotal = str.substring(str.indexOf(":") + 1, str.length() - 2);
                            ramSize = Integer.parseInt(memTotal.trim());
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (ir != null) {
                    try {
                        ir.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ramSize;
    }

    public int getRomSize() {
        if (romSize == 0) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            romSize = (int) (availableBlocks * blockSize / 1024);
        }
        return romSize;
    }

    public boolean isSystemApp() {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserID() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUuid(){
          final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);    
          final String tmDevice, tmSerial, androidId;    
          tmDevice = "" + tm.getDeviceId();   
          tmSerial = "" + tm.getSimSerialNumber();    
          androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);    
          UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());    
          String uniqueId = deviceUuid.toString();
          LogUtil.d("uuid="+uniqueId);
          return uniqueId;
         }
    
    private static final String MTK_GEMINI = "MTK_GEMINI";
    private static final int GEMINI_SIM_1 = 0; //Phone.GEMINI_SIM_1
    private static final int GEMINI_SIM_2 = 1; //Phone.GEMINI_SIM_2
    private static final Uri PREFERRED_APN_URI =
            Uri.parse("content://telephony/carriers/preferapn");
    private static final Uri PREFERRED_APN_URI_GEMINI =
            Uri.parse("content://telephony/carriers_gemini/preferapn");
    private static final String GPRS_CONNECTION_SIM_SETTING = "gprs_connection_sim_setting";

    private String detectDualSIMFeature() {
        try {
            Class<?> featureOptionClass = Class.forName("com.mediatek.featureoption.FeatureOption");
            Field mtkGeminiSupportField = featureOptionClass.getDeclaredField("MTK_GEMINI_SUPPORT");
            if (mtkGeminiSupportField.getBoolean(featureOptionClass)) {
                return MTK_GEMINI;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public String getCurrApn()
    {
        String apnStr= "wifi";
        //wifi状态
          ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);           
          State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
      try
      {
        if (State.CONNECTED  != state)
        {
            Uri preferredApnUri = PREFERRED_APN_URI;            
            //
            if (detectDualSIMFeature().equals(MTK_GEMINI))//mtk双卡
            {
                long selectedSimId = Settings.System.getLong(context.getContentResolver(),
                        GPRS_CONNECTION_SIM_SETTING, -1);
                LogUtil.i("getCurrApn selectedSimId:" + selectedSimId);
                if (selectedSimId == GEMINI_SIM_1) {
                    preferredApnUri = PREFERRED_APN_URI;
                } else if (selectedSimId == GEMINI_SIM_2) {
                    preferredApnUri = PREFERRED_APN_URI_GEMINI;
                }       
            }

            Cursor  cursor_current = context.getContentResolver().query(preferredApnUri, null, null, null, null);
            if(cursor_current != null && cursor_current.moveToFirst()){
                apnStr = cursor_current.getString(cursor_current.getColumnIndex("apn"));
                
                cursor_current.close();
             }          
            
        }
      }
      catch (Exception e) {
        // TODO: handle exception
          LogUtil.e("getCurrApn err:"+e.getMessage());
    }
        return apnStr;
    }

    public String getTimeStamp() {
        // TODO Auto-generated method stub
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String szNow = dateFormat.format(new Date());
        return szNow;
    }   
    
    public int dip2px(float dpValue) {
          final float scale = context.getResources().getDisplayMetrics().density;
          return (int) (dpValue * scale + 0.5f);
    }

}
