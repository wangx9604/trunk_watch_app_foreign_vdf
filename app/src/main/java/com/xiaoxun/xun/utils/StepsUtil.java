package com.xiaoxun.xun.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.RanksStepsEntity;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhangjun5 on 2017/1/17.
 */

public class StepsUtil {

    public static boolean isHasStepSensor(Context context){
        int sensorTypeC=Sensor.TYPE_STEP_COUNTER;
        boolean isHas = false;
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager != null){
            Sensor mStepSensor = mSensorManager.getDefaultSensor(sensorTypeC);
            if(mStepSensor != null){
                isHas = true;
            }
        }
        return isHas;
    }

    public class BaseUiListener implements IUiListener {
        private Context context;
        public BaseUiListener(Context cont){
            context = cont;
        }

        @Override
        public void onComplete(Object response) {
            org.json.JSONObject jsonObject = (org.json.JSONObject) response;
            Log.d("xiaolong", "jsonObject: " + jsonObject);
            ToastUtil.show(context.getApplicationContext(), context.getString(R.string.steps_share_send_success));
        }

        @Override
        public void onError(UiError e) {
            LogUtil.i("qZone find error:"+e.toString());
            ToastUtil.show(context.getApplicationContext(), context.getString(R.string.steps_share_send_fail));
        }

        @Override
        public void onCancel() {
            ToastUtil.show(context.getApplicationContext(), context.getString(R.string.share_cancel));
        }
    }

    public static RanksStepsEntity getEntityFormRanksListByNum(ArrayList<RanksStepsEntity> ranksList, int num){
        RanksStepsEntity entity = null;
        for(int i = 0;i < ranksList.size();i++){
            RanksStepsEntity entity1 = ranksList.get(i);
            if(entity1.num == num){
                entity = entity1;
            }
        }
        return entity;
    }
    public static RanksStepsEntity getEntityFormRanksListByEid(ArrayList<RanksStepsEntity> ranksList, String eid){
        RanksStepsEntity entity = null;
        for(int i = 0;i < ranksList.size();i++){
            RanksStepsEntity entity1 = ranksList.get(i);
            if(entity1.eid.equals(eid)){
                entity = entity1;
            }
        }
        return entity;
    }
    /**
     *user:zhangjun5 time:16:03 date:2017/2/8
     *desc:根据获取到的json数据，解析为排行表的数据
     **/
    public static void getDataByJsonStr(ArrayList<RanksStepsEntity> list, String jsonStr){
        if(jsonStr == null){
            return ;
        }
        JSONArray array = (JSONArray) JSONValue.parse(jsonStr);
        for(int i = 0;i < array.size();i++){
            JSONObject object = (JSONObject)array.get(i);
            RanksStepsEntity entity = new RanksStepsEntity();
            entity.eid = (String) object.get("EID");
            entity.name = (String) object.get("Name");
            entity.steps = (int) object.get("Steps");
            entity.num = (int) object.get("Num");
            list.add(entity);
        }
    }
    /**
    *user:zhangjun5 time:19:53 date:2017/2/10
    *desc:排行表的数据,解析为json数据
    **/
    public static String parseRanksToJsonStr(ArrayList<RanksStepsEntity> list){
        String retStr;
        JSONArray array = new JSONArray();
        for(int i = 0;i<list.size();i++){
            RanksStepsEntity entity = list.get(i);
            JSONObject object = new JSONObject();
            object.put("Steps",entity.steps);
            object.put("Num",entity.num);
            object.put("Name",entity.name);
            object.put("EID",entity.eid);
            array.add(object);
        }
        retStr = array.toJSONString();
        return retStr;
    }


    /**
     * 类名称：ShareStepsActivity
     * 创建人：zhangjun5
     * 创建时间：2016/6/12 11:00
     * 方法描述：qq空间分享时，需要保存的文件
     */
    public static String SaveImageFile(String fileName,Bitmap bitmap){

        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return fileName;
    }


    /**
     * 类名称：ShareStepsActivity
     * 创建人：zhangjun5
     * 创建时间：2016/6/12 15:02
     * 方法描述：删除分享使用的多余的图片，减少icon文件夹下的数据冗余
     */
    public static void delectShareToImage( File dir){
        if(!dir.isDirectory()){
            return ;
        }
        File[] fileArray = dir.listFiles();
        if (fileArray != null && fileArray.length > 0) {
            for (File f : fileArray) {
                String fileName = f.getName();
                if (fileName.indexOf("share") != -1) {
                    f.delete();
                }
            }
        }
    }
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void initSensor(final Context context,final String getType) {
        LogUtil.e("ender sersor steps!");
        final int sensorTypeC=Sensor.TYPE_STEP_COUNTER;
        final SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        final Sensor mStepCount = mSensorManager.getDefaultSensor(sensorTypeC);
        if (mStepCount != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    try {
                        if (sensorEvent.sensor.getType() == sensorTypeC) {
                            int totalSteps = (int) sensorEvent.values[0];
                            LogUtil.e("send steps broast:"+totalSteps);
                            Intent _intent = new Intent(Const.ACTION_BROAST_SENSOR_STEPS);
                            _intent.putExtra("sensor_steps",String.valueOf(totalSteps));
                            _intent.putExtra("sensor_type",getType);
                            context.sendBroadcast(_intent);
                            mSensorManager.unregisterListener(this, mStepCount);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            },
            mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /**
     *user:zhangjun5 time:17:17 date:2017/2/6
     *desc:获取当前手表的计步排名信息
     **/
    public static void getRanksDataFromCloud(ImibabyApp myApp, String type, String eid, String saveSteps){
//        String saveSteps = getPhoneStepsByDector(myApp);
        LogUtil.e("steps:"+saveSteps);
        myApp.ranksDatasUpdateTask(saveSteps,type,eid);
    }

    public static String getPhoneStepsByFirstSteps(ImibabyApp myApp, String mTotalSteps){
        String retStr = "0";
        if(mTotalSteps == null || mTotalSteps.equals("0")){
            return retStr;
        }
        try {
            String phoneStepsPref = myApp.getStringValue(Const.SHARE_PREF_PHONT_STEPS_NEW, "0");
            if(phoneStepsPref.equals("0")){
                String saveSteps = TimeUtil.getTimeStampLocal() + "_" + mTotalSteps;
                myApp.setValue(Const.SHARE_PREF_PHONT_STEPS_NEW, saveSteps);
                return retStr;
            }
            String[] steps = phoneStepsPref.split("_");
            if (!TimeUtil.compareTodayToLastInfo(steps[0])) {
                String saveSteps = TimeUtil.getTimeStampLocal() + "_" + mTotalSteps;
                myApp.setValue(Const.SHARE_PREF_PHONT_STEPS_NEW, saveSteps);
                return retStr;
            }
            if (Integer.valueOf(mTotalSteps) > Integer.valueOf(steps[1])) {
                retStr = String.valueOf(Integer.valueOf(mTotalSteps) - Integer.valueOf(steps[1]));
            }
        }catch (Exception e){
            retStr = "0";
        }
        return retStr;
    }
    /**
    *user:zhangjun5 time:9:49 date:2017/4/19
    *desc:获取当前手机的计步数据
    **/
    public static String getPhoneStepsByDector(ImibabyApp myApp){
        String saveSteps = "0";
        String phoneStepsPref = myApp.getStringValue(Const.SHARE_PREF_PHONT_STEPS,"0");
        if(phoneStepsPref.equals("0")){
            saveSteps = "0";
        }else{
            try {
                String[] phoneSteps = phoneStepsPref.split("_");
                if (TimeUtil.compareTodayToLastInfo(phoneSteps[0])) {
                    saveSteps = String.valueOf(Integer.valueOf(phoneSteps[2]) - Integer.valueOf(phoneSteps[1]));
                    LogUtil.e("steps:"+phoneSteps[2]+":"+phoneSteps[1]+":"+saveSteps);
                } else {
                    saveSteps = "0";
                }
            }catch(Exception e){
                saveSteps = "0";
                myApp.setValue(Const.SHARE_PREF_PHONT_STEPS, saveSteps);
            }
        }
        return saveSteps;
    }

    public static String getLocalDataToTargetLevel(ImibabyApp myApp, String watchEid){
        String steps_target_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_TARGET_LEVEL, "0");
        if(steps_target_level == null || steps_target_level.equals("0")){
            return "8000";
        }else {
            return steps_target_level;
        }
    }

    public static String getLocalDataToCurSteps(ImibabyApp myApp, String watchEid) {
        String retStr = "0";
        String steps_level = myApp.getStringValue(watchEid + CloudBridgeUtil.STEPS_LEVEL, "0");
        if (steps_level == null || steps_level.equals("0")) {
            return retStr;
        } else {
            String[] array = steps_level.split("_");
            if (TimeUtil.compareTodayToLastInfo(array[0])) {
                retStr = array[1];
            }
        }
        return retStr;
    }

    public static double calcMeterBySteps(ImibabyApp myApp, String steps){
        int stepCount = Integer.parseInt(steps);
        double heightScale;
        if(myApp.getCurUser().getFocusWatch().getHeight() < 60 ){
            heightScale = 110;
        }else{
            heightScale = myApp.getCurUser().getFocusWatch().getHeight();
        }
        double meter = stepCount*heightScale*0.45*0.011;

        return meter;
    }

    public static String formatKiloByMeter(double meter,String unit_kilo,String unit_mi){
        if(meter > 1000){
            meter /= 1000;
            return String.format("%.2f%s", meter, unit_kilo);
        }else{
            return String.format("%.0f%s", meter, unit_mi);
        }
    }

    public static int calcCalBySteps(ImibabyApp myApp, String steps){
        double weight;
        if(myApp.getCurUser().getFocusWatch().getWeight() < 18 ){
            weight = 18;
        }else{
            weight = myApp.getCurUser().getFocusWatch().getWeight();
        }
        double kilo = calcMeterBySteps(myApp,steps)/1000;
        double calo = kilo * 1.036 * weight + 0.5;//四舍五入之后取整数

        return (int)calo;
    }

    public static double calcRankMeterBySteps(double heightScale, String steps){
        int stepCount = Integer.parseInt(steps);
        if(heightScale < 60 ){
            heightScale = 110;
        }
        return stepCount*heightScale*0.45*0.011;
    }

    public static int calcRankCalBySteps(double height,double weight, String steps){
        if(weight < 18 ){
            weight = 18;
        }
        double kilo = calcRankMeterBySteps(height,steps)/1000;
        double calo = kilo * 1.036 * weight + 0.5;//四舍五入之后取整数

        return (int)calo;
    }

}
