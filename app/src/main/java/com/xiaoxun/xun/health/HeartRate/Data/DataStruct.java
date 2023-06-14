package com.xiaoxun.xun.health.HeartRate.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataStruct {

    public static Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer t1, Integer t2) {
            if (t2 > t1) {
                return 1;
            } else if (t1.equals(t2)) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    public static class DayData {
        String date;
        ArrayList<BaseData> values;
        int highRate;
        int lowRate;
        int averageRate;
        int restingRate;

        public DayData(String date, ArrayList<BaseData> map) {
            this.date = date;
            values = new ArrayList<>();
            values.addAll(map);
        }

        public String getDate() {
            return date;
        }

        public ArrayList<BaseData> getValues() {
            return values;
        }

        public int getDayValueCount() {
            return (values != null ? values.size() : 0);
        }

        public void setHighRate(int rate) {
            highRate = rate;
        }

        public int getHighRate() {
            return highRate;
        }

        public void setLowRate(int rate) {
            lowRate = rate;
        }

        public int getLowRate() {
            return lowRate;
        }

        public void setAverageRate(int rate) {
            averageRate = rate;
        }

        public int getAverageRate() {
            return averageRate;
        }

        public void setRestingRate(int rate) {
            restingRate = rate;
        }

        public int getRestingRate() {
            return restingRate;
        }
    }

    public static class WeekData {
        int weekOfMonth;
        ArrayList<String> weekDates;
        ArrayList<DayData> daysValue;
        int weekHighRate;
        int weekLowRate;
        int weekAverageRate;
        int weekRestingRate;

        public WeekData(ArrayList<DayData> map) {
            weekDates = new ArrayList<>();
            daysValue = new ArrayList<>();
            daysValue.addAll(map);
            calWeekData();
        }

        public void setWeekOfMonth(int n) {
            weekOfMonth = n;
        }

        public int getWeekOfMonth() {
            return weekOfMonth;
        }

        public ArrayList<DayData> getDaysValue() {
            return daysValue;
        }

        public int getWeekHighRate() {
            return weekHighRate;
        }

        public int getWeekLowRate() {
            return weekLowRate;
        }

        public int getWeekAverageRate() {
            return weekAverageRate;
        }

        public int getWeekRestingRate() {
            return weekRestingRate;
        }

        public void setWeekDates(ArrayList<String> dates){
            weekDates.addAll(dates);
        }

        public ArrayList<String> getWeekDates(){
            return weekDates;
        }

        private void calWeekData() {
            ArrayList<Integer> highValueList = new ArrayList<>();
            ArrayList<Integer> lowValueList = new ArrayList<>();
            int averageSum = 0;
            int restingSum = 0;

            for (DayData item : daysValue) {
                highValueList.add(item.getHighRate());
                lowValueList.add(item.getLowRate());
                averageSum += item.getAverageRate();
                restingSum += item.getRestingRate();
            }

            Collections.sort(highValueList, comparator);
            Collections.sort(lowValueList, comparator);
            weekHighRate = highValueList.get(0);
            weekLowRate = lowValueList.get(lowValueList.size()-1);
            weekAverageRate = averageSum / daysValue.size();
            weekRestingRate = restingSum / daysValue.size();
        }
    }

    public static class MonthData {
        int year;
        int month;
        ArrayList<DayData> daysValue;
        int monthHighRate;
        int monthLowRate;
        int monthAverageRate;
        int monthRestingRate;

        public MonthData(int year,int month,ArrayList<DayData> map){
            this.year = year;
            this.month = month;
            daysValue = new ArrayList<>();
            daysValue.addAll(map);
            calMonthData();
        }

        public ArrayList<DayData> getMonthValue(){
            return daysValue;
        }
        public int getYear(){
            return year;
        }
        public int getMonth(){
            return month;
        }
        public int getMonthHighRate(){
            return monthHighRate;
        }
        public int getMonthLowRate(){
            return monthLowRate;
        }
        public int getMonthAverageRate(){
            return monthAverageRate;
        }
        public int getMonthRestingRate(){
            return monthRestingRate;
        }
        public void setMonth(int m){
            month = m;
        }

        private void calMonthData() {
            ArrayList<Integer> highValueList = new ArrayList<>();
            ArrayList<Integer> lowValueList = new ArrayList<>();
            int averageSum = 0;
            int restingSum = 0;

            for (DayData item : daysValue) {
                highValueList.add(item.getHighRate());
                lowValueList.add(item.getLowRate());
                averageSum += item.getAverageRate();
                restingSum += item.getRestingRate();
            }

            Collections.sort(highValueList, comparator);
            Collections.sort(lowValueList, comparator);
            monthHighRate = highValueList.get(0);
            monthLowRate = lowValueList.get(lowValueList.size()-1);
            monthAverageRate = averageSum / daysValue.size();
            monthRestingRate = restingSum / daysValue.size();
        }

    }

    public static class BaseData{
        int min;
        int rate;
        public BaseData(int m,int r){
            min = m;
            rate = r;
        }

        public int getMin(){
            return min;
        }
        public int getRate(){
            return rate;
        }
    }
}
