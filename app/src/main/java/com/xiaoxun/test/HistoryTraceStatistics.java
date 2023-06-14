package com.xiaoxun.test;

import android.os.Environment;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.utils.PointInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xilvkang on 2015/11/10.
 */
public class HistoryTraceStatistics {
    public final static String TAG = "HistoryTraceStatistics";

    private final static String DIR_PATH = "TRACESTATISTICS";

    public static void initDir() {
        File fpBase = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        File dir = new File(fpBase, DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static boolean isFileExist(String filename) {
        File fpBase = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        File dir = new File(fpBase, DIR_PATH);
        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public static void creatFile(String time, String eid) {
        File fpBase = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        File dir = new File(fpBase, DIR_PATH);
        String name = time + "_" + eid + ".report";
        File fp = new File(dir, name);
        if (!fp.exists()) {
            try {
                fp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deletFile(String time, String eid) {
        File fpBase = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        File dir = new File(fpBase, DIR_PATH);
        String name = time + "_" + eid + ".report";
        File fp = new File(dir, name);
        if (fp.exists()) {
            fp.delete();
        }
    }

    public static void saveDataToFile(String time, String eid, ArrayList<PointInfo> allpoint,String watchname) {
        TraceStatisticsData inf = new TraceStatisticsData(time, eid, allpoint);
        String title = time + "  " + eid + "  " + watchname +  "轨迹统计\n";

        String gps_title = "GPS定位：\n";
        String gps_num = "GPS定位次数：" + inf.getGPSNum() + "(" + formatPercent((float) inf.getGPSNum() / inf.getNumOfAll()*100) + "%)\n";
        String gps_range1 = "0-30m      总数：" + formatPercent(inf.getGPSStatistics().getRange1_per()) + "%(" + inf.getGPSStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getGPSStatistics().getRange1_avg() + "m\n";
        String gps_range2 = "30-50m     总数：" + formatPercent(inf.getGPSStatistics().getRange2_per()) + "%(" + inf.getGPSStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getGPSStatistics().getRange2_avg() + "m\n";
        String gps_range3 = "50-100m    总数：" + formatPercent(inf.getGPSStatistics().getRange3_per()) + "%(" + inf.getGPSStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getGPSStatistics().getRange3_avg() + "m\n";
        String gps_range4 = "100-500m   总数：" + formatPercent(inf.getGPSStatistics().getRange4_per()) + "%(" + inf.getGPSStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getGPSStatistics().getRange4_avg() + "m\n";
        String gps_range5 = ">500m      总数：" + formatPercent(inf.getGPSStatistics().getRange5_per()) + "%(" + inf.getGPSStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getGPSStatistics().getRange5_avg() + "m\n";
        String gps_result = gps_title + gps_num + gps_range1 + gps_range2 + gps_range3 + gps_range4 + gps_range5;

        String wifi_title = "WiFi定位：\n";
        String wifi_num = "WiFi定位次数：" + inf.getWifiNum() + "(" + formatPercent((float) inf.getWifiNum() / inf.getNumOfAll()*100) + "%)\n";
        String wifi_range1 = "0-30m     总数：" + formatPercent(inf.getWifiStatistics().getRange1_per()) + "%(" + inf.getWifiStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getWifiStatistics().getRange1_avg() + "m\n";
        String wifi_range2 = "30-50m    总数：" + formatPercent(inf.getWifiStatistics().getRange2_per()) + "%(" + inf.getWifiStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getWifiStatistics().getRange2_avg() + "m\n";
        String wifi_range3 = "50-100m   总数：" + formatPercent(inf.getWifiStatistics().getRange3_per()) + "%(" + inf.getWifiStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getWifiStatistics().getRange3_avg() + "m\n";
        String wifi_range4 = "100-500m  总数：" + formatPercent(inf.getWifiStatistics().getRange4_per()) + "%(" + inf.getWifiStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getWifiStatistics().getRange4_avg() + "m\n";
        String wifi_range5 = ">500m     总数：" + formatPercent(inf.getWifiStatistics().getRange5_per()) + "%(" + inf.getWifiStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getWifiStatistics().getRange5_avg() + "m\n";
        String wifi_result = wifi_title + wifi_num + wifi_range1 + wifi_range2 + wifi_range3 + wifi_range4 + wifi_range5;

        String mix_title = "混合定位：\n";
        String mix_num = "混合定位次数：" + inf.getMixNum() + "(" + formatPercent((float) inf.getMixNum() / inf.getNumOfAll()*100) + "%)\n";
        String mix_range1 = "0-30m      总数：" + formatPercent(inf.getMixStatistics().getRange1_per()) + "%(" + inf.getMixStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getMixStatistics().getRange1_avg() + "m\n";
        String mix_range2 = "30-50m     总数：" + formatPercent(inf.getMixStatistics().getRange2_per()) + "%(" + inf.getMixStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getMixStatistics().getRange2_avg() + "m\n";
        String mix_range3 = "50-100m    总数：" + formatPercent(inf.getMixStatistics().getRange3_per()) + "%(" + inf.getMixStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getMixStatistics().getRange3_avg() + "m\n";
        String mix_range4 = "100-500m   总数：" + formatPercent(inf.getMixStatistics().getRange4_per()) + "%(" + inf.getMixStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getMixStatistics().getRange4_avg() + "m\n";
        String mix_range5 = ">500m      总数：" + formatPercent(inf.getMixStatistics().getRange5_per()) + "%(" + inf.getMixStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getMixStatistics().getRange5_avg() + "m\n";
        String mix_result = mix_title + mix_num + mix_range1 + mix_range2 + mix_range3 + mix_range4 + mix_range5;

        String cell_title = "基站定位：\n";
        String cell_num = "基站定位次数：" + inf.getCellNum() + "(" + formatPercent((float) inf.getCellNum() / inf.getNumOfAll()*100) + "%)\n";
        String cell_range1 = "0-30m     总数：" + formatPercent(inf.getCellStatistics().getRange1_per()) + "%(" + inf.getCellStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getCellStatistics().getRange1_avg() + "m\n";
        String cell_range2 = "30-50m    总数：" + formatPercent(inf.getCellStatistics().getRange2_per()) + "%(" + inf.getCellStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getCellStatistics().getRange2_avg() + "m\n";
        String cell_range3 = "50-100m   总数：" + formatPercent(inf.getCellStatistics().getRange3_per()) + "%(" + inf.getCellStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getCellStatistics().getRange3_avg() + "m\n";
        String cell_range4 = "100-500m  总数：" + formatPercent(inf.getCellStatistics().getRange4_per()) + "%(" + inf.getCellStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getCellStatistics().getRange4_avg() + "m\n";
        String cell_range5 = ">500m     总数：" + formatPercent(inf.getCellStatistics().getRange5_per()) + "%(" + inf.getCellStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getCellStatistics().getRange5_avg() + "m\n";
        String cell_result = cell_title + cell_num + cell_range1 + cell_range2 + cell_range3 + cell_range4 + cell_range5;

        String other_title = "其他定位：\n";
        String other_num = "其他定位次数：" + inf.getOtherNum() + "(" + formatPercent((float) inf.getOtherNum() / inf.getNumOfAll()*100) + "%)\n";
        String other_range1 = "0-30m    总数：" + formatPercent(inf.getOtherStatistics().getRange1_per()) + "%(" + inf.getOtherStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getOtherStatistics().getRange1_avg() + "m\n";
        String other_range2 = "30-50m   总数：" + formatPercent(inf.getOtherStatistics().getRange2_per()) + "%(" + inf.getOtherStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getOtherStatistics().getRange2_avg() + "m\n";
        String other_range3 = "50-100m  总数：" + formatPercent(inf.getOtherStatistics().getRange3_per()) + "%(" + inf.getOtherStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getOtherStatistics().getRange3_avg() + "m\n";
        String other_range4 = "100-500m 总数：" + formatPercent(inf.getOtherStatistics().getRange4_per()) + "%(" + inf.getOtherStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getOtherStatistics().getRange4_avg() + "m\n";
        String other_range5 = ">500m    总数：" + formatPercent(inf.getOtherStatistics().getRange5_per()) + "%(" + inf.getOtherStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getOtherStatistics().getRange5_avg() + "m\n";
        String other_result = other_title + other_num + other_range1 + other_range2 + other_range3 + other_range4 + other_range5;

        String none_title = "none定位：\n";
        String none_num = "none定位次数：" + inf.getNoneNum() + "(" + formatPercent((float) inf.getNoneNum() / inf.getNumOfAll()*100) + "%)\n";
        String none_range1 = "0-30m     总数：" + formatPercent(inf.getNoneStatistics().getRange1_per()) + "%(" + inf.getNoneStatistics().getRange1_num() + "次)"
                + "     平均距离：" + inf.getNoneStatistics().getRange1_avg() + "m\n";
        String none_range2 = "30-50m    总数：" + formatPercent(inf.getNoneStatistics().getRange2_per()) + "%(" + inf.getNoneStatistics().getRange2_num() + "次)"
                + "     平均距离：" + inf.getNoneStatistics().getRange2_avg() + "m\n";
        String none_range3 = "50-100m   总数：" + formatPercent(inf.getNoneStatistics().getRange3_per()) + "%(" + inf.getNoneStatistics().getRange3_num() + "次)"
                + "     平均距离：" + inf.getNoneStatistics().getRange3_avg() + "m\n";
        String none_range4 = "100-500m  总数：" + formatPercent(inf.getNoneStatistics().getRange4_per()) + "%(" + inf.getNoneStatistics().getRange4_num() + "次)"
                + "     平均距离：" + inf.getNoneStatistics().getRange4_avg() + "m\n";
        String none_range5 = ">500m     总数：" + formatPercent(inf.getNoneStatistics().getRange5_per()) + "%(" + inf.getNoneStatistics().getRange5_num() + "次)"
                + "     平均距离：" + inf.getNoneStatistics().getRange5_avg() + "m\n";
        String none_result = none_title + none_num + none_range1 + none_range2 + none_range3 + none_range4 + none_range5;

        String result = title + gps_result + wifi_result + mix_result + cell_result + other_result + none_result;

        String filename = time + "_" + eid + ".report";
        if(!isFileExist(filename)){
            creatFile(time,eid);
        }else{
            deletFile(time,eid);
            creatFile(time,eid);
        }

        File fpBase = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        File dir = new File(fpBase, DIR_PATH);
        File fp = new File(dir,filename);
        try {
            FileOutputStream fos = new FileOutputStream(fp, true);
            fos.write(result.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String formatPercent(float num){
        String ret = String.format("%.2f",num);
        return ret;
    }

}
