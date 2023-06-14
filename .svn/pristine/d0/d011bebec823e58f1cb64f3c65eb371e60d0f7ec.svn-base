package com.xiaoxun.xun.networkv2.beans;

import java.util.ArrayList;

public class MotionReportBean {

    int code;
    String msg;
    ArrayList<MotionSportBean> sportList;  //体育测试
    ArrayList<MotionStepBean> stepsList;    //运动统计

    public MotionReportBean(int code, String msg,
                            ArrayList<MotionSportBean> sportList,
                            ArrayList<MotionStepBean> stepsList) {
        this.code = code;
        this.msg = msg;
        this.sportList = sportList;
        this.stepsList = stepsList;
    }

    public static class MotionSportBean{

        public MotionSportBean() {
        }

        public MotionSportBean(int type, int max, int avg, int maxrank, ArrayList<String> datas) {
            this.type = type;
            this.max = max;
            this.avg = avg;
            this.maxrank = maxrank;
            this.datas = datas;
        }

        int type;
        int max;
        int avg;
        int maxrank;
        ArrayList<String> datas;

        public int getType() {
            return type;
        }

        public int getMax() {
            return max;
        }

        public int getAvg() {
            return avg;
        }

        public int getMaxrank() {
            return maxrank;
        }

        public ArrayList<String> getDatas() {
            return datas;
        }
    }

    public static class MotionStepBean{
        public MotionStepBean() {
        }

        public MotionStepBean(String tag, int avg, int compare, int target, ArrayList<String> datas) {
            this.tag = tag;
            this.avg = avg;
            this.compare = compare;
            this.target = target;
            this.datas = datas;
        }

        String tag;
        int avg;
        int compare;
        int target;
        ArrayList<String> datas;

        public String getTag() {
            return tag;
        }

        public int getAvg() {
            return avg;
        }

        public int getCompare() {
            return compare;
        }

        public int getTarget() {
            return target;
        }

        public ArrayList<String> getDatas() {
            return datas;
        }
    }

    public int getCode() {
        return code;
    }

    public ArrayList<MotionSportBean> getSportList() {
        return sportList;
    }

    public ArrayList<MotionStepBean> getStepsList() {
        return stepsList;
    }
}
