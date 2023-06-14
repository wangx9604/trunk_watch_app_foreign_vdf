package com.xiaoxun.xun.calendar;

import android.content.Context;

import com.xiaoxun.xun.db.DatePointDAO;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DatePoint {
	
	public static ArrayList<DatePoint> datePointList = new ArrayList<DatePoint>();
	public static ArrayList<DatePoint> datePointList_cur = new ArrayList<DatePoint>();
	public static ArrayList<DatePoint> datePointList_pre1 = new ArrayList<DatePoint>();
	public static ArrayList<DatePoint> datePointList_pre2 = new ArrayList<DatePoint>();
	
	public static int curPageIndex = 2;
	public static int dataReady = 0;
	public static CustomDate curSelectItem = new CustomDate(1900, 1, 1);
	
	private Date date;
	private int num;
	
	private static Comparator<DatePoint> comparator  = new Comparator<DatePoint>() {
		public int compare(DatePoint s1, DatePoint s2) {
			if(s1.getDate().after(s2.getDate()))
				return 1;
			return -1;
		}
	};

	public DatePoint(Date date, int nump) {
		this.date = date;
		this.num = nump;
	}

	public void setDate(Date date){
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	}

	public void setPointNum(int nump){
		this.num = nump;
	}
	
	public int getPointNum() {
		return num;
	}
	
	final static SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd",Locale.CHINA);
	
	public static void setDateToCalenderPointList(JSONObject pl){
		Calendar date = Calendar.getInstance();
		DatePoint item = null;
		
		datePointList.clear();
        for(int i=0 ; i< 92 ; i++){
        	date.add(Calendar.DAY_OF_MONTH, -1); 
        	String dayKey = formatDateKey.format(date.getTime());
        	if (pl.get(dayKey) == null){
        		item = new DatePoint(date.getTime(), 0);
        	}else{
        		item = new DatePoint(date.getTime(), (Integer) pl.get(dayKey));
        	}
        	datePointList.add(item);        	
        }
		if (datePointList.size() > 1) { // 排序
			Collections.sort(datePointList, comparator);
		}
		LogUtil.v("xxx"+"  "+ datePointList.size());
                
        initThreeMonthArray();
        dataReady = 1;
	}
		
	public static void setPointListToThreeMonthArray(){
        initThreeMonthArray();
        dataReady = 1;
	}
	
	private static void initThreeMonthArray(){
		Date today = Calendar.getInstance().getTime(); //datePointList.get(datePointList.size()-1).getDate();
		int month = today.getMonth() + 1;
		int year = today.getYear() + 1900;
		int firstDayWeek = DateUtil.getWeekDayFromDate(year,
				month);
		int loc = -1;
		
		//cur
		datePointList_cur.clear();
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, 1 - firstDayWeek-date.getTime().getDate());
		
		for(int i = datePointList.size()-1;i>=0;i--){
			if(date.getTime().getDate() == datePointList.get(i).getDate().getDate()
					&& date.getTime().getMonth() == datePointList.get(i).getDate().getMonth()
					&& date.getTime().getYear() == datePointList.get(i).getDate().getYear()){
				loc = i;
				break;
			}
		}
		if(loc > -1){
			int todayDate = today.getDate();
			for(int i=0;i<42;i++){
				if(i<firstDayWeek + todayDate-1){
					datePointList_cur.add(datePointList.get(loc + i));
				}else{
					Calendar curDay = Calendar.getInstance();
					curDay.add(Calendar.DAY_OF_MONTH, i - firstDayWeek-todayDate+1);
					DatePoint item = new DatePoint(curDay.getTime(), -1);
					datePointList_cur.add(item);
				}
			}
		} else{
			int todayDate = today.getDate();
			for(int i=0;i<42;i++){
				Calendar curDay = Calendar.getInstance();
				curDay.add(Calendar.DAY_OF_MONTH, i - firstDayWeek-todayDate+1);
				DatePoint item = new DatePoint(curDay.getTime(), -1);
				datePointList_cur.add(item);
			}
		}
		
		//pre1
		datePointList_pre1.clear();
		int month_pre1 = month - 1;
		if(month_pre1 == 0){
			month_pre1 = 12;
			year = year - 1;
		}
		date = Calendar.getInstance();
		date.set(Calendar.DATE, 1);
		date.add(Calendar.MONTH, -1);
		firstDayWeek = DateUtil.getWeekDayFromDate(year,month_pre1);
		date.add(Calendar.DAY_OF_MONTH, 0 - firstDayWeek);
		loc = -1;
		for(int i = datePointList.size()-1;i>=0;i--){
			if(date.getTime().getDate() == datePointList.get(i).getDate().getDate()
					&& date.getTime().getMonth() == datePointList.get(i).getDate().getMonth()
					&& date.getTime().getYear() == datePointList.get(i).getDate().getYear()){
				loc = i;
				break;
			}
		}
		if(loc > -1){
			date = Calendar.getInstance();
			for(int i=0;i<42;i++){
				int size = datePointList.size();
				if(loc+i < size)
					datePointList_pre1.add(datePointList.get(loc+i));
				else{
					DatePoint item = new DatePoint(date.getTime(), -1);
					datePointList_pre1.add(item);
					date.add(Calendar.DAY_OF_MONTH, -1);
				}
			}
		}else{
			LogUtil.e("xxx" + "  " + "datePointList_pre1 init error .");
		}
		
		//pre2
		datePointList_pre2.clear();
		int month_pre2 = month_pre1 - 1;
		if(month_pre2 == 0){
			month_pre2 = 12;
			year = year - 1;
		}
		loc = -1;
		date = Calendar.getInstance();
		date.set(Calendar.DATE, 1);
		date.add(Calendar.MONTH, -2);
		int firstDayWeek_pre2 = DateUtil.getWeekDayFromDate(year,
				month_pre2);
		//date.add(Calendar.DAY_OF_MONTH, 0-firstDayWeek_pre2);
		
		for(int i=0;i<datePointList.size();i++){
			if(date.getTime().getDate() == datePointList.get(i).getDate().getDate()
					&& date.getTime().getMonth() == datePointList.get(i).getDate().getMonth()
					&& date.getTime().getYear() == datePointList.get(i).getDate().getYear()){
				loc = i;
				break;
			}
		}
		if(loc > -1) {
			for(int i=0;i<firstDayWeek_pre2;i++){
				date.add(Calendar.DAY_OF_MONTH, -firstDayWeek_pre2 + i);
				DatePoint item = new DatePoint(date.getTime(), -1);
				datePointList_pre2.add(item);
			}
			for (int i = 0; i < 42; i++) {
				datePointList_pre2.add(datePointList.get(loc + i));
			}
		}else{
			LogUtil.e("xxx"+"  "+"datePointList_pre2 init error .");
		}

		LogUtil.v("xxx"+"  "+"init list over.");
	}
	
	public static void setDatePointListToLocal(Context context,String eid){
		DatePointDAO.getInstance(context).updateDatePoint(eid, datePointList);
	}
	private static boolean isFirstDayInMonth(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH) == 1;
	}

	public static void clearDatePointLists(){
		datePointList.clear();
		datePointList_cur.clear();
		datePointList_pre1.clear();
		datePointList_pre2.clear();
	}
}
