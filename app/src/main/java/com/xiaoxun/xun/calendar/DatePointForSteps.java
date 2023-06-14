package com.xiaoxun.xun.calendar;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DatePointForSteps {

	public static ArrayList<DatePointForSteps> datePointList = new ArrayList<DatePointForSteps>();
	public static ArrayList<DatePointForSteps> datePointList_cur = new ArrayList<DatePointForSteps>();
	public static ArrayList<DatePointForSteps> datePointList_pre1 = new ArrayList<DatePointForSteps>();
	public static ArrayList<DatePointForSteps> datePointList_pre2 = new ArrayList<DatePointForSteps>();
	//计步使用的点数据
	public static ArrayList<DatePointForSteps> stepsPointList = new ArrayList<DatePointForSteps>();
	public static ArrayList<DatePointForSteps> stepsPointList_cur = new ArrayList<DatePointForSteps>();
	public static ArrayList<DatePointForSteps> stepsPointList_pre1 = new ArrayList<DatePointForSteps>();

	public static int curPageIndex = 1;
	public static int dataReady = 0;
	public static int curStepsPageIndex = 1;
	public static int stepsDataReady = 0;
	public static CustomDate curSelectItem = new CustomDate(1900, 1, 1);

	private Date date;
	private int num;     //数据模式中的步数
	private int calories;//数据模式增加卡路里数据

	private static Comparator<DatePointForSteps> comparator  = new Comparator<DatePointForSteps>() {
		public int compare(DatePointForSteps s1, DatePointForSteps s2) {
			if(s1.getDate().after(s2.getDate()))
				return 1;
			return -1;
		}
	};

	public DatePointForSteps(Date date, int nump) {
		this.date = date;
		this.num = nump;
	}

	public DatePointForSteps(Date date, int nump, int cal){
		this.date = date;
		this.num = nump;
		this.calories = cal;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
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
	/**
	* 类名称：DatePoint
	* 创建人：zhangjun5
	* 创建时间：2016/3/2 16:59
	* 方法描述：计步功能初始化最近70天的数据
	*/
	public static void setDateToCalenderPointListForSteps(JSONArray array){
		Calendar date = Calendar.getInstance();
		DatePointForSteps item = null;
		stepsPointList.clear();
		stepsPointList_cur.clear();
		stepsPointList_pre1.clear();
		for(int i = 0; i < Const.STEPS_CALENDER_DATE_COUNT ; i++){
			date.add(Calendar.DAY_OF_MONTH, -1);
			String dayKey = formatDateKey.format(date.getTime());
			boolean isHasSteps = false;
			for(int j = 0;j < array.size();j++){
				JSONObject keyObject = (JSONObject)array.get(j);
				String keyOnLine = TimeUtil.getReversedTimeByTime(keyObject.get("Key").toString());
				if(keyOnLine.equals(dayKey) && keyObject.get("Steps") != null
						){
					isHasSteps = true;
					try {
						if (keyObject.get("Calories") != null) {
							item = new DatePointForSteps(date.getTime(),
									(Integer) keyObject.get("Steps"),
									(Integer) keyObject.get("Calories"));
						} else {
							item = new DatePointForSteps(date.getTime(),
									(Integer) keyObject.get("Steps"),
									0);
						}
					}catch (Exception e){
						item = new DatePointForSteps(date.getTime(),
								0,
								0);
					}
				}
			}
			if(!isHasSteps){
				item = new DatePointForSteps(date.getTime(), 0, 0);
			}

			stepsPointList.add(item);
		}
		if (stepsPointList.size() > 1) { // 排序
			Collections.sort(stepsPointList, comparator);
		}
		LogUtil.v("xxx"+"  "+ stepsPointList.size());

		initTwoMonthStepsArray();
		stepsDataReady = 1;
	}
	/**
	* 类名称：DatePoint
	* 创建人：zhangjun5
	* 创建时间：2016/3/2 17:39
	* 方法描述：计步功能中的初始化两个月的数据格式，数据格式和轨迹不同
	*/
	public static void initTwoMonthStepsArray(){
		Date today = Calendar.getInstance().getTime(); //datePointList.get(datePointList.size()-1).getDate();
		int month = today.getMonth() + 1;
		int year = today.getYear() + 1900;
		int firstDayWeek = DateUtil.getWeekDayFromDate(year,
				month);
		int loc = -1;

		//cur
		stepsPointList_cur.clear();
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, 1 - firstDayWeek-date.getTime().getDate());

		for(int i = stepsPointList.size()-1;i>=0;i--){
			if(date.getTime().getDate() == stepsPointList.get(i).getDate().getDate()
					&& date.getTime().getMonth() == stepsPointList.get(i).getDate().getMonth()
					&& date.getTime().getYear() == stepsPointList.get(i).getDate().getYear()){
				loc = i;
				break;
			}
		}
		if(loc > -1){
			int todayDate = today.getDate();
			for(int i=0;i<42;i++){
				if(i<firstDayWeek + todayDate-1){
					stepsPointList_cur.add(stepsPointList.get(loc + i));
				}else{
					Calendar curDay = Calendar.getInstance();
					curDay.add(Calendar.DAY_OF_MONTH, i - firstDayWeek-todayDate+1);
					DatePointForSteps item = new DatePointForSteps(curDay.getTime(), -1, -1);
					stepsPointList_cur.add(item);
				}
			}
		} else{
			int todayDate = today.getDate();
			for(int i=0;i<42;i++){
				Calendar curDay = Calendar.getInstance();
				curDay.add(Calendar.DAY_OF_MONTH, i - firstDayWeek-todayDate+1);
				DatePointForSteps item = new DatePointForSteps(curDay.getTime(), -1, -1);
				stepsPointList_cur.add(item);
			}
		}

		//pre1
		stepsPointList_pre1.clear();
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
		for(int i = stepsPointList.size()-1;i>=0;i--){
			if(date.getTime().getDate() == stepsPointList.get(i).getDate().getDate()
					&& date.getTime().getMonth() == stepsPointList.get(i).getDate().getMonth()
					&& date.getTime().getYear() == stepsPointList.get(i).getDate().getYear()){
				loc = i;
				break;
			}
		}
		if(loc > -1){
			date = Calendar.getInstance();
			for(int i=0;i<42;i++){
				int size = stepsPointList.size();
				if(loc+i < size)
					stepsPointList_pre1.add(stepsPointList.get(loc+i));
				else{
					DatePointForSteps item = new DatePointForSteps(date.getTime(), -1, -1);
					stepsPointList_pre1.add(item);
					date.add(Calendar.DAY_OF_MONTH, -1);
				}
			}
		}else{
			LogUtil.e("xxx" + "  " + "datePointList_pre1 init error .");
		}
	}
	public static void setDateToCalenderPointList(JSONObject pl){
		Calendar date = Calendar.getInstance();
		DatePointForSteps item = null;
		
		datePointList.clear();
        for(int i=0 ; i< 92 ; i++){
        	date.add(Calendar.DAY_OF_MONTH, -1); 
        	String dayKey = formatDateKey.format(date.getTime());
        	if (pl.get(dayKey) == null){
        		item = new DatePointForSteps(date.getTime(), 0);
        	}else{
        		item = new DatePointForSteps(date.getTime(), (Integer) pl.get(dayKey));
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
					DatePointForSteps item = new DatePointForSteps(curDay.getTime(), -1);
					datePointList_cur.add(item);
				}
			}
		} else{
			int todayDate = today.getDate();
			for(int i=0;i<42;i++){
				Calendar curDay = Calendar.getInstance();
				curDay.add(Calendar.DAY_OF_MONTH, i - firstDayWeek-todayDate+1);
				DatePointForSteps item = new DatePointForSteps(curDay.getTime(), -1);
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
					DatePointForSteps item = new DatePointForSteps(date.getTime(), -1);
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
				DatePointForSteps item = new DatePointForSteps(date.getTime(), -1);
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

	private static boolean isFirstDayInMonth(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH) == 1;
	}
}
