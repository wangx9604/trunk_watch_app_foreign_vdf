package com.xiaoxun.xun.utils;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by huangyouyang on 2016/9/29.
 */
public class SmsUtil {

    private static final int OP_WRITE_SMS = 15;

    public static void sendMsgToWatch(final Context context, String simNo, String sms) {

        LogUtil.d("HYY " + "sms:" + sms);
        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.putExtra("address", simNo);
        mIntent.putExtra("sms_body", sms);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setType("vnd.android-dir/mms-sms");
        context.startActivity(mIntent);
    }

    /**
     * if have permission to write sms
     */
    public static boolean isWriteEnabled(Context context) {
        int uid = getUid(context);
        Object opRes = checkOp(context, OP_WRITE_SMS, uid);

        if (opRes instanceof Integer) {
            return (Integer) opRes == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }

    /**
     * set permission to write sms
     */
    public static boolean setWriteEnabled(Context context, boolean enabled) {
        int uid = getUid(context);
        int mode = enabled ? AppOpsManager.MODE_ALLOWED
                : AppOpsManager.MODE_IGNORED;

        return setMode(context, OP_WRITE_SMS, uid, mode);
    }

    private static Object checkOp(Context context, int code, int uid) {

        AppOpsManager appOpsManager = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        Class appOpsManagerClass = appOpsManager.getClass();

        try {
            Class[] types = new Class[3];
            types[0] = Integer.TYPE;
            types[1] = Integer.TYPE;
            types[2] = String.class;
            Method checkOpMethod = appOpsManagerClass.getMethod("checkOp",types);

            Object[] args = new Object[3];
            args[0] = Integer.valueOf(code);
            args[1] = Integer.valueOf(uid);
            args[2] = context.getPackageName();
            Object result = checkOpMethod.invoke(appOpsManager, args);

            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean setMode(Context context, int code, int uid, int mode) {
        AppOpsManager appOpsManager = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        Class appOpsManagerClass = appOpsManager.getClass();

        try {
            Class[] types = new Class[4];
            types[0] = Integer.TYPE;
            types[1] = Integer.TYPE;
            types[2] = String.class;
            types[3] = Integer.TYPE;
            Method setModeMethod = appOpsManagerClass.getMethod("setMode",
                    types);

            Object[] args = new Object[4];
            args[0] = Integer.valueOf(code);
            args[1] = Integer.valueOf(uid);
            args[2] = context.getPackageName();
            args[3] = Integer.valueOf(mode);
            setModeMethod.invoke(appOpsManager, args);

            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int getUid(Context context) {
        try {
            @SuppressLint("WrongConstant")
            int uid = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_SERVICES).uid;

            return uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean isDoubleSimEnable(Context context){

        boolean isSIM1Ready=false;
        boolean isSIM2Ready=false;
        try {
            // 调用系统函数getSimStateGemini(原装android系统不支持双卡双待，所以不会有这个函数)，MTK添加此函数
            isSIM1Ready = SmsUtil.getSIMStateBySlot(context, "getSimStateGemini", 0);
            isSIM2Ready = SmsUtil.getSIMStateBySlot(context, "getSimStateGemini", 1);
        } catch (SmsUtil.GeminiMethodNotFoundException e) {
            e.printStackTrace();
            try {
                isSIM1Ready = SmsUtil.getSIMStateBySlot(context, "getSimState", 0);
                isSIM2Ready = SmsUtil.getSIMStateBySlot(context, "getSimState", 1);
            } catch (SmsUtil.GeminiMethodNotFoundException e1) {
                //Call here for next manufacturer's predicted method name if you wish
                e1.printStackTrace();
            }
        }
        LogUtil.d("HYY " + "   isSIM1Ready:" + isSIM1Ready+"   isSIM2Ready:"+isSIM2Ready);
        return isSIM1Ready && isSIM2Ready;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return isReady;
    }

    public static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = 20170103521L;
        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

    /**
     * 唤醒设备
     * <sn，seid，sub_action,PL>
     */
    public static void sendWakeUpMsgToDevice(final Context context, String eid, String simNo) {

        //封装短信内容   <time，seid，sub_action,PL>
        String identifier = "WAKE";
        String timeInfo = TimeUtil.getTimeStampLocal().substring(0, 14);   //yyyyMMddHHmmss
        String msg = "wake_up";
        StringBuffer sms = new StringBuffer();
        sms.append("<" + timeInfo + "," + eid + "," + "E" + CloudBridgeUtil.SUB_ACTION_WAKEUP_DEVICE + "," + msg + ">");
        String smsmd5 = MD5.md5_string(sms.toString());
        String smsGarble = garbleSms(timeInfo, smsmd5);
        String smswake = identifier + smsGarble;
        LogUtil.d("HYY " + "sms:" + sms);
        LogUtil.d("HYY " + "smsmd5:" + smsmd5);
        LogUtil.d("HYY " + "smsgarble:" + smsGarble);
        LogUtil.d("HYY " + "smswake:" + smswake);
        sendMsgToWatch(context, simNo,smswake);
    }

    /**
     * 在唤醒短信md5密文中插入时间信息。时间信息：yyyyMMddHHmmss
     */
    private static String garbleSms(String timeInfo, String sms) {

        StringBuffer smsBuf = new StringBuffer(sms);
        int index = Integer.parseInt(sms.substring(0, 1), Character.FORMAT);
        if ((index & 1) == 0) {
            // 偶数
            index = index + 1;
            smsBuf.insert(index, timeInfo.substring(0, 4));
            index = index + 6;
            smsBuf.insert(index, timeInfo.substring(4, 6));
            index = index + 4;
            smsBuf.insert(index, timeInfo.substring(6, 8));
            index = index + 4;
            smsBuf.insert(index, timeInfo.substring(8, 10));
            index = index + 4;
            smsBuf.insert(index, timeInfo.substring(10, 12));
            index = index + 4;
            smsBuf.insert(index, timeInfo.substring(12, 14));
        } else {
            // 奇数
            index = index + 1;
            smsBuf.insert(index, timeInfo.substring(0, 4));
            index = index + 7;
            smsBuf.insert(index, timeInfo.substring(4, 6));
            index = index + 5;
            smsBuf.insert(index, timeInfo.substring(6, 8));
            index = index + 5;
            smsBuf.insert(index, timeInfo.substring(8, 10));
            index = index + 5;
            smsBuf.insert(index, timeInfo.substring(10, 12));
            index = index + 5;
            smsBuf.insert(index, timeInfo.substring(12, 14));
        }
        return smsBuf.toString();
    }
}
