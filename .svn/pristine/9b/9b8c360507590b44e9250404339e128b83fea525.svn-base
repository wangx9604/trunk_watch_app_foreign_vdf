package com.xiaoxun.xun.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 * Created by huangyouyang on 2018/1/18.
 */

public class NetWorkUtils {

    private static final String TAG = "NetWorkUtils ";

    public static final int NETWORKTYPE_WIFI = 0;
    public static final int NETWORKTYPE_MOBILE = 1;
    public static final int NETWORKTYPE_MOBILE_2G = 2;
    public static final int NETWORKTYPE_MOBILE_3G = 3;
    public static final int NETWORKTYPE_MOBILE_4G = 4;
    public static final int NETWORKTYPE_OTHER = 10;
    public static final int NETWORKTYPE_INVALID = -1;

    /**
     * 获取网络类型
     * @param context
     * @return 0 wifi ;1 mobile; 2 2G; 3 3G; 4 4G; 10,Other ; -1，不合法
     */
    public static int getConnectionType(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return NETWORKTYPE_INVALID;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                return NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return getMobileType(context);
            } else {
                return NETWORKTYPE_OTHER;
            }
        } else {
            return NETWORKTYPE_INVALID;
        }
    }

    //0 wifi ;1 mobile; 2 2G; 3 3G; 4 4G; 10,Other ; -1，不合法
    public static String transNetNameByNetCode(Context context) {
        int NetCode = getConnectionType(context);
        String mNetName = "-1";
        switch (NetCode) {
            case 0:
                mNetName = "WIFI";
                break;
            case 1:
                mNetName = "MOBILE";
                break;
            case 2:
                mNetName = "2G";
                break;
            case 3:
                mNetName = "3G";
                break;
            case 4:
                mNetName = "4G";
                break;
            case 10:
                mNetName = "other";
                break;
        }
        return mNetName;
    }

    private static int getMobileType(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null)
            return NETWORKTYPE_MOBILE;

        if (!PermissionUtils.hasPermissions(context, android.Manifest.permission.READ_PHONE_STATE)) {
            return NETWORKTYPE_MOBILE;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORKTYPE_MOBILE_2G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORKTYPE_MOBILE_3G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORKTYPE_MOBILE_4G;

            default:
                return NETWORKTYPE_MOBILE;
        }
    }

    // 基站信息
    public static String getCellInfoList(Context context) {

        if (!PermissionUtils.hasPermissions(context, PermissionUtils.locationPermissions)) {
            return "null";
        }

        int dbm ;
        int level ;
        StringBuilder cellDesc = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        List<CellInfo> cellInfoList = tm.getAllCellInfo();

        if (cellInfoList != null) {
            for (CellInfo cellInfo : cellInfoList) {
                if (!cellInfo.isRegistered())
                    continue;
                if (cellInfo instanceof CellInfoGsm) {
                    CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthGsm.getDbm();
                    level = cellSignalStrengthGsm.getLevel() + 1;  //from 0..4
                    cellDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=2G").append(",").append(getNetworkName(context));
                } else if (cellInfo instanceof CellInfoCdma) {
                    CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthCdma.getDbm();
                    level = cellSignalStrengthCdma.getLevel() + 1;  //from 0..4
                    cellDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=3G").append(",").append(getNetworkName(context));
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cellInfo instanceof CellInfoWcdma) {
                    CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthWcdma.getDbm();
                    level = cellSignalStrengthWcdma.getLevel() + 1;  //from 0..4
                    cellDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=3G").append(",").append(getNetworkName(context));
                } else if (cellInfo instanceof CellInfoLte) {
                    CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthLte.getDbm();
                    level = cellSignalStrengthLte.getLevel() + 1;  //from 0..4
                    cellDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=4G").append(",").append(getNetworkName(context));
                }
            }
        }
        return cellDesc.toString();
    }

    public  static void listenPhoneState(final Context context, final TextView  textView) {

        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {

                String signalinfo = signalStrength.toString();
                LogUtil.i(TAG + "listenPhoneState signalinfo=" + signalinfo);

                int dbm;
                int level = -1;
                StringBuilder sigalDesc = new StringBuilder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    level = signalStrength.getLevel();
                }
                int asu = signalStrength.getGsmSignalStrength();

                switch (getConnectionType(context)) {
                    case NETWORKTYPE_MOBILE_2G:
                        dbm = -113 + 2 * asu;
                        sigalDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=2G").append(" name=").append(getNetworkName(context));
                        break;
                    case NETWORKTYPE_MOBILE_3G:
                        dbm = -113 + 2 * asu;
                        sigalDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=3G").append(" name=").append(getNetworkName(context));
                        break;
                    case NETWORKTYPE_MOBILE_4G:
                        String[] parts = signalinfo.split(" ");
                        dbm = /*Integer.parseInt(parts[9])*/0;  //解析规则发生变化，暂且不处理
                        sigalDesc.append("dbm=").append(dbm).append(" level=").append(level).append(" type=4G").append(" name=").append(getNetworkName(context));
                        break;
                    default:
                }

                textView.setText(sigalDesc);
                super.onSignalStrengthsChanged(signalStrength);
            }
        };
        assert telephonyManager != null;
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    // 网络制式
    private static String getNetworkName(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String netWorkName = "";
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                netWorkName = "unknown";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                netWorkName = "gprs";  //2.5G
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                netWorkName = "edge";  //2.75G
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                netWorkName = "umts";  //3G移动电话技术，使用WCDMA作为底层标准
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                netWorkName = "cdma";  //CDMA的定义是一种技术标准，有其2代、2.5代、3代技术。这里CDMA指代CDMA2代技术标准的制式，中国电信在用。
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                netWorkName = "evdo_0";  //CDMA2000标准中的版本，属于3G，电信可能使用。
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                netWorkName = "evdo_a";  //CDMA2000标准中的版本，属于3G，电信可能使用。
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                netWorkName = "1xrtt";  //在CDMA2000中，通常被认为是2.5G或2.75G，速率只有其他3G的几分之一，电信可能使用。
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                netWorkName = "hsdpa";  //  建立在WCDMA上，相当于3.5G，联通可能使用。
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                netWorkName = "hsupa";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                netWorkName = "hspa";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                netWorkName = "iden";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                netWorkName = "evdo_b";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                netWorkName = "lte";  //对应准4G，各个运营商都可能使用。
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                netWorkName = "ehrpo";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                netWorkName = "hspa+";
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                netWorkName = "gsm";
                break;
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                netWorkName = "td_scdma";  // 移动3G时是这个值。
                break;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                netWorkName = "iwlan";
                break;
        }
        LogUtil.i(TAG + "getNetworkName tm.netWorkType=" + tm.getNetworkType() + " netWorkName=" + netWorkName);
        return netWorkName;
    }
}
