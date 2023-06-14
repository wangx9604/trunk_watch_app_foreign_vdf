package com.xiaoxun.test;

import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.PointInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by xilvkang on 2015/11/10.
 */
public class TraceStatisticsData {

    public String time;
    public String eid;
    public ArrayList<PointInfo> pl;

    private int NumOfAll = 0;               //总次数
    private ArrayList<PointDataInf> list_all = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_gps = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_wifi = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_mix = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_cell = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_other = new ArrayList<PointDataInf>();
    private ArrayList<PointDataInf> list_none = new ArrayList<PointDataInf>();

    private StatisticsDataInf gps_Statistics;
    private StatisticsDataInf wifi_Statistics;
    private StatisticsDataInf mix_Statistics;
    private StatisticsDataInf cell_Statistics;
    private StatisticsDataInf other_Statistics;
    private StatisticsDataInf none_Statistics;

    public TraceStatisticsData(String t, String e, ArrayList<PointInfo> p) {
        time = t;
        eid = e;
        pl = p;
        NumOfAll = p.size();

        initList();
        TraceStatistics();
    }

    private void initList() {
        for (int i = 0; i < pl.size(); i++) {
            PointDataInf point = new PointDataInf();
            point.accuracy = pl.get(i).radius;
            point.type = pl.get(i).type;
            list_all.add(point);

            if (pl.get(i).type.equals("G")) {
                list_gps.add(point);
            } else if (pl.get(i).type.equals("W")) {
                list_wifi.add(point);
            } else if (pl.get(i).type.equals("H")) {
                list_mix.add(point);
            } else if (pl.get(i).type.equals("C")) {
                list_cell.add(point);
            } else if (pl.get(i).type.equals("O")) {
                list_other.add(point);
            } else if (pl.get(i).type.equals("N")) {
                list_none.add(point);
            }
        }

    }

    private void TraceStatistics() {
        gps_Statistics = new StatisticsDataInf("G", list_gps.size(), list_gps);
        wifi_Statistics = new StatisticsDataInf("W", list_wifi.size(), list_wifi);
        mix_Statistics = new StatisticsDataInf("H", list_mix.size(), list_mix);
        cell_Statistics = new StatisticsDataInf("C", list_cell.size(), list_cell);
        other_Statistics = new StatisticsDataInf("O", list_other.size(), list_other);
        none_Statistics = new StatisticsDataInf("N", list_none.size(), list_none);
    }

    public int getNumOfAll() {
        return NumOfAll;
    }

    public int getGPSNum() {
        return list_gps.size();
    }

    public StatisticsDataInf getGPSStatistics() {
        return gps_Statistics;
    }

    public int getWifiNum() {
        return list_wifi.size();
    }

    public StatisticsDataInf getWifiStatistics() {
        return wifi_Statistics;
    }

    public int getMixNum() {
        return list_mix.size();
    }

    public StatisticsDataInf getMixStatistics() {
        return mix_Statistics;
    }

    public int getCellNum() {
        return list_cell.size();
    }

    public StatisticsDataInf getCellStatistics() {
        return cell_Statistics;
    }

    public int getOtherNum() {
        return list_other.size();
    }

    public StatisticsDataInf getOtherStatistics() {
        return other_Statistics;
    }

    public int getNoneNum() {
        return list_none.size();
    }

    public StatisticsDataInf getNoneStatistics() {
        return none_Statistics;
    }

    //数据信息
    class PointDataInf {
        public String type;
        public int accuracy;
    }

    //统计数据信息
    public class StatisticsDataInf {
        public String Type;
        public int Num = 0;
        public ArrayList<PointDataInf> list;

        //0-30
        private int Range1_num = 0;
        private float Range1_avg = 0;
        //30-50
        private int Range2_num = 0;
        private float Range2_avg = 0;
        //50-100
        private int Range3_num = 0;
        private float Range3_avg = 0;
        //100-500
        private int Range4_num = 0;
        private float Range4_avg = 0;
        //500-
        private int Range5_num = 0;
        private float Range5_avg = 0;

        public StatisticsDataInf(String t, int n, ArrayList<PointDataInf> l) {
            Type = t;
            Num = n;
            list = l;
            rangeAnalysis();
        }

        private void rangeAnalysis() {
            int range1all = 0;
            int range2all = 0;
            int range3all = 0;
            int range4all = 0;
            int range5all = 0;

            if (list == null || list.size() == 0) {
                LogUtil.e("TraceStatisticsData"+"  "+"list is null.");
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).accuracy >= 0 && list.get(i).accuracy <= 30) {
                    Range1_num++;
                    range1all = range1all + list.get(i).accuracy;
                } else if (list.get(i).accuracy > 30 && list.get(i).accuracy <= 50) {
                    Range2_num++;
                    range2all = range2all + list.get(i).accuracy;
                } else if (list.get(i).accuracy > 50 && list.get(i).accuracy <= 100) {
                    Range3_num++;
                    range3all = range3all + list.get(i).accuracy;
                } else if (list.get(i).accuracy > 100 && list.get(i).accuracy <= 500) {
                    Range4_num++;
                    range4all = range4all + list.get(i).accuracy;
                } else if (list.get(i).accuracy > 500) {
                    Range5_num++;
                    range5all = range5all + list.get(i).accuracy;
                }
            }
            if (Range1_num != 0)
                Range1_avg = (float) range1all / Range1_num;
            if (Range2_num != 0)
                Range2_avg = (float) range2all / Range2_num;
            if (Range3_num != 0)
                Range3_avg = (float) range3all / Range3_num;
            if (Range4_num != 0)
                Range4_avg = (float) range4all / Range4_num;
            if (Range5_num != 0)
                Range5_avg = (float) range5all / Range5_num;
        }

        public int getRange1_num() {
            return Range1_num;
        }

        public float getRange1_per() {
            if (list.size() == 0)
                return 0;
            return (float) Range1_num / Num * 100;
        }

        public float getRange1_avg() {
            return format2persent(Range1_avg);
        }

        public int getRange2_num() {
            return Range2_num;
        }

        public float getRange2_per() {
            if (list.size() == 0)
                return 0;
            return (float) Range2_num / Num * 100;
        }

        public float getRange2_avg() {
            return format2persent(Range2_avg);
        }

        public int getRange3_num() {
            return Range3_num;
        }

        public float getRange3_per() {
            if (list.size() == 0)
                return 0;
            return (float) Range3_num / Num * 100;
        }

        public float getRange3_avg() {
            return format2persent(Range3_avg);
        }

        public int getRange4_num() {
            return Range4_num;
        }

        public float getRange4_per() {
            if (list.size() == 0)
                return 0;
            return (float) Range4_num / Num * 100;
        }

        public float getRange4_avg() {
            return format2persent(Range4_avg);
        }

        public int getRange5_num() {
            return Range5_num;
        }

        public float getRange5_per() {
            if (list.size() == 0)
                return 0;
            return (float) Range5_num / Num * 100;
        }

        public float getRange5_avg() {
            return format2persent(Range5_avg);
        }
    }

    public float format2persent(float num) {
        DecimalFormat df = new DecimalFormat("0.00");
        float db = Float.parseFloat(df.format(num));
        return db;
    }
    public float format1persent(float num) {
        DecimalFormat df = new DecimalFormat("0.0");
        float db = Float.parseFloat(df.format(num));
        return db;
    }
}


