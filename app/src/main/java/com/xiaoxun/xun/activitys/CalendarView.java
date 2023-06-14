package com.xiaoxun.xun.activitys;

 import java.text.SimpleDateFormat; 
 import java.util.ArrayList; 
 import java.util.Calendar; 
 import java.util.Date;
 import java.util.Locale;

import net.minidev.json.JSONObject;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.TimeUtil;

 import android.annotation.SuppressLint; 
 import android.content.Context;
 import android.graphics.Color; 
import android.graphics.Typeface;
 import android.util.AttributeSet; 
 import android.view.GestureDetector; 
 import android.view.GestureDetector.OnGestureListener; 
 import android.view.Gravity; 
 import android.view.MotionEvent; 
 import android.view.View; 
 import android.view.ViewGroup; 
 import android.view.View.OnTouchListener; 
 import android.view.animation.Animation; 
 import android.view.animation.Animation.AnimationListener; 
 import android.view.animation.TranslateAnimation; 
 import android.widget.BaseAdapter; 
 import android.widget.GridView;
 import android.widget.LinearLayout; 
 import android.widget.RelativeLayout; 
 import android.widget.TextView; 
import android.widget.Toast;
 import android.widget.ViewFlipper; 
import android.widget.AbsListView.LayoutParams; 

 import com.xiaoxun.xun.utils.ToastUtil;
 
 public class CalendarView extends LinearLayout implements OnTouchListener, 
        AnimationListener, OnGestureListener ,MsgCallback{ 
    public interface OnCalendarViewListener { 
        void onCalendarItemClick(CalendarView view, Date date , LinearLayout txtDay , int pointNum); 
    } 
  
//    private final static int TOP_HEIGHT = 48; 
    private final static int DEFAULT_ID = 0xff0000; 
  
    private static final int SWIPE_MIN_DISTANCE = 120; 
    private static final int SWIPE_MAX_OFF_PATH = 250; 
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    private static final int CALENDAR_GRID_WEEK_HIGHT = 71 ;

  
    private int screenWidth; 
  
    private Animation slideLeftIn; 
    private Animation slideLeftOut; 
    private Animation slideRightIn; 
    private Animation slideRightOut; 
    private ViewFlipper viewFlipper; 
    private GestureDetector mGesture = null; 
  
    private GridView gView1; 
    private GridView gView2; 
    private GridView gView3; 
  
    //boolean bIsSelection = false;
	private Calendar calStartDate = Calendar.getInstance();
	//private Calendar calSelected = Calendar.getInstance();
	private CalendarGridViewAdapter gAdapter; 
    //private CalendarGridViewAdapter gAdapter1;
    //private CalendarGridViewAdapter gAdapter3;
  
    private LinearLayout mMainLayout; 
    //private TextView mTitle; 
    private int iMonthViewCurrentDay = 0; 
    private int iMonthViewCurrentMonth = 0; 
	private int iMonthViewCurrentYear = 0; 
    private static final int caltitleLayoutID = 66; 
	private static final int calLayoutID = 55; 
	private Context mContext;
  
    //private final List<Date> markDates; 
  
    private OnCalendarViewListener mListener; 
    
    private ImibabyApp myApp;
    private WatchData curWatch ;
    private ArrayList<Date> calenderCounterList = new ArrayList<Date>();
    private ArrayList<Integer> curCalendarPointCounter = new ArrayList<Integer>();
    Calendar caleCounter = Calendar.getInstance(); 
    private CalendarGridAdapterForCallBack gAdapterGetClodeBridge ; 

    private Date curSelectDate ;
  
    public CalendarView(Context context) { 
        this(context, null); 
    } 
  
    public CalendarView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
        // TODO Auto-generated constructor stub       
		mContext = context; 
        //markDates = new ArrayList<Date>();
        
        myApp = (ImibabyApp) ((GoogleMapHistoryTraceActivity)(mContext)).getApplication();
        caleCounter.setTime(calStartDate.getTime());
        gAdapterGetClodeBridge = new CalendarGridAdapterForCallBack(mContext, caleCounter);
        calenderCounterList = (ArrayList<Date>) gAdapterGetClodeBridge.getAllItem();    //当前View包含日期。
        curCalendarPointCounter.clear() ;                                               //记录一月中当天pointer的个数。
        curWatch = ((GoogleMapHistoryTraceActivity)(mContext)).getCurWatch() ;
        getHistoryTraceCounterByDay((calenderCounterList.get(6)), (calenderCounterList.get(0))) ;

        init(); 
        
        this.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	//不需要任何操作，只是为了消费事件
        	}
        	});
    }
  
    protected void init() { 
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels; 
  
        slideLeftIn = new TranslateAnimation(screenWidth, 0, 0, 0); 
        slideLeftIn.setDuration(200); 
        slideLeftIn.setAnimationListener(this); 
        slideLeftOut = new TranslateAnimation(0, -screenWidth, 0, 0); 
        slideLeftOut.setDuration(200); 
        slideLeftOut.setAnimationListener(this); 
        slideRightIn = new TranslateAnimation(-screenWidth, 0, 0, 0); 
        slideRightIn.setDuration(200); 
        slideRightIn.setAnimationListener(this); 
        slideRightOut = new TranslateAnimation(0, screenWidth, 0, 0); 
        slideRightOut.setDuration(200); 
        slideRightOut.setAnimationListener(this); 
  
        mGesture = new GestureDetector(mContext, this); 
  
        UpdateStartDateForMonth(); 
        setOrientation(LinearLayout.HORIZONTAL); 
        mMainLayout = new LinearLayout(mContext); 
        LinearLayout.LayoutParams main_params = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        mMainLayout.setLayoutParams(main_params); 
        mMainLayout.setGravity(Gravity.CENTER_HORIZONTAL); 
        mMainLayout.setOrientation(LinearLayout.VERTICAL); 
        addView(mMainLayout); 
  
        //generateTopView(); 
  
        viewFlipper = new ViewFlipper(mContext); 
        RelativeLayout.LayoutParams fliper_params = new RelativeLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        fliper_params.addRule(RelativeLayout.BELOW, caltitleLayoutID); 
        mMainLayout.addView(viewFlipper, fliper_params);
        
        generateClaendarGirdView();         
        generateWeekGirdView();
        
        LinearLayout br = new LinearLayout(mContext); 
        br.setBackgroundColor(Color.argb(0xff, 0xe3, 0xee, 0xf4)); 
        LinearLayout.LayoutParams params_br = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, 3); 
        mMainLayout.addView(br, params_br); 
    }
    
    private int dip2px(float dipValue) { 
        final float scale = mContext.getResources().getDisplayMetrics().density; 
        return Math.round(dipValue * scale); 
    }

    /*    
    @SuppressWarnings("deprecation") 
    private void generateTopView() {          
		RelativeLayout top = new RelativeLayout(mContext); 
        top.setBackgroundColor(getResources().getColor(R.color.bg_color)); 
        LinearLayout.LayoutParams top_params = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, dip2px(TOP_HEIGHT)); 
        top.setLayoutParams(top_params); 
        mMainLayout.addView(top); 
        
		mTitle = new TextView(mContext); 
        android.widget.RelativeLayout.LayoutParams title_params = new android.widget.RelativeLayout.LayoutParams( 
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT); 
        mTitle.setLayoutParams(title_params); 
        mTitle.setTextColor(getResources().getColor(R.color.white)); 
        mTitle.setTextSize(18); 
        mTitle.setFocusableInTouchMode(true); 
        mTitle.setMarqueeRepeatLimit(-1); 
        mTitle.setEllipsize(TruncateAt.MARQUEE); 
        mTitle.setSingleLine(true); 
        mTitle.setGravity(Gravity.CENTER); 
        mTitle.setHorizontallyScrolling(true); 
        mTitle.setText("2014年9月"); 
        top.addView(mTitle); 
  
        ImageButton mLeftView = new ImageButton(mContext); 
        StateListDrawable stateListDrawableL = new StateListDrawable(); 
        Drawable lDrawableNor = new BitmapDrawable(mContext.getResources(), 
        		BitmapFactory.decodeResource(getResources(), R.drawable.return_0)); 
        Drawable lDrawablePre = new BitmapDrawable(mContext.getResources(), 
        		BitmapFactory.decodeResource(getResources(), R.drawable.return_0)); 
        stateListDrawableL.addState( 
                new int[] { -android.R.attr.state_pressed }, lDrawableNor); 
        stateListDrawableL.addState(new int[] { android.R.attr.state_pressed }, 
                lDrawablePre); 
        mLeftView.setBackgroundDrawable(stateListDrawableL); 
  
        android.widget.RelativeLayout.LayoutParams leftPP = new android.widget.RelativeLayout.LayoutParams( 
                dip2px(25), dip2px(22)); 
        leftPP.addRule(RelativeLayout.ALIGN_PARENT_LEFT); 
        leftPP.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); 
        leftPP.setMargins(20, 0, 0, 0); 
        mLeftView.setLayoutParams(leftPP); 
        mLeftView.setOnClickListener(new OnClickListener() { 
  
            @Override
            public void onClick(View v) { 
                // TODO Auto-generated method stub                 
            	viewFlipper.setInAnimation(slideRightIn); 
                viewFlipper.setOutAnimation(slideRightOut); 
                viewFlipper.showPrevious(); 
                setPrevViewItem(); 
            } 
        }); 
        top.addView(mLeftView);
    } */
  
    private void generateWeekGirdView() { 
        GridView gridView = new GridView(mContext); 
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, CALENDAR_GRID_WEEK_HIGHT); 
        gridView.setLayoutParams(params); 
        gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundColor(mContext.getResources().getColor(R.color.xiaomi_bg_grey)); 
  
        int i = screenWidth / 7; 
        int j = screenWidth - (i * 7); 
        int x = j / 2; 
        gridView.setPadding(x, 0, 0, 0);
		WeekGridAdapter weekAdapter = new WeekGridAdapter(mContext); 
        gridView.setAdapter(weekAdapter);
		mMainLayout.addView(gridView); 
    } 
  
    private void generateClaendarGirdView() {
		Calendar tempSelected2 = Calendar.getInstance(); 
        tempSelected2.setTime(calStartDate.getTime()); 
  
        gView1 = new GridView(mContext); 
        //tempSelected1.add(Calendar.MONTH, -1);
        //gAdapter1 = new CalendarGridViewAdapter(mContext, tempSelected1, markDates); 
        //gView1.setAdapter(gAdapter1);
		gView1.setId(calLayoutID); 
  
        gView2 = new CalendarGridView(mContext); 
        gAdapter = new CalendarGridViewAdapter(mContext, tempSelected2, this); 
        gView2.setAdapter(gAdapter);
		gView2.setId(calLayoutID); 
  
        gView3 = new GridView(mContext); 
        //tempSelected3.add(Calendar.MONTH, 1); 
        //gAdapter3 = new CalendarGridViewAdapter(mContext, tempSelected3, markDates); 
        //gView3.setAdapter(gAdapter3);
		gView3.setId(calLayoutID); 
  
        gView2.setOnTouchListener(this); 
        gView1.setOnTouchListener(this); 
        gView3.setOnTouchListener(this); 
  
        if (viewFlipper.getChildCount() != 0) { 
            viewFlipper.removeAllViews(); 
        } 
  
        viewFlipper.addView(gView2); 
        viewFlipper.addView(gView3); 
        viewFlipper.addView(gView1); 
  
        /*String title = calStartDate.get(Calendar.YEAR) 
                + "年"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate 
                        .get(Calendar.MONTH) + 1) + "月"; 
        mTitle.setText(title); */
    } 
  
	private void setPrevViewItem() { 
        /*iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) { 
            iMonthViewCurrentMonth = 11; 
            iMonthViewCurrentYear--; 
        } 
        calStartDate.set(Calendar.DAY_OF_MONTH, 1); 
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); 
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);*/
		
		iMonthViewCurrentDay = iMonthViewCurrentDay-7;

		if(iMonthViewCurrentDay < 0){
			iMonthViewCurrentMonth-- ;
	        if (iMonthViewCurrentMonth == 12) { 
	            iMonthViewCurrentMonth = 0; 
	            iMonthViewCurrentYear++; 
	        }
	        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); 
	        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			int dayOfMonth = calStartDate.getActualMaximum(Calendar.DAY_OF_MONTH); 
	        iMonthViewCurrentDay = dayOfMonth + iMonthViewCurrentDay ;
	        calStartDate.set(Calendar.DAY_OF_MONTH, iMonthViewCurrentDay);
	        return ;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, iMonthViewCurrentDay); 
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); 
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
	} 
    
	private void setNextViewItem() { 
        /*iMonthViewCurrentMonth++; 
        if (iMonthViewCurrentMonth == 12) { 
            iMonthViewCurrentMonth = 0; 
            iMonthViewCurrentYear++; 
        } 
        calStartDate.set(Calendar.DAY_OF_MONTH, 1); 
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); 
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);*/
		
		iMonthViewCurrentDay = iMonthViewCurrentDay+7;
		int dayOfMonth = calStartDate.getActualMaximum(Calendar.DAY_OF_MONTH); 
		if(iMonthViewCurrentDay > dayOfMonth){
			iMonthViewCurrentDay = iMonthViewCurrentDay - dayOfMonth ;
			iMonthViewCurrentMonth++ ;
	        if (iMonthViewCurrentMonth == 12) { 
	            iMonthViewCurrentMonth = 0; 
	            iMonthViewCurrentYear++; 
	        }
		}
        
        calStartDate.set(Calendar.DAY_OF_MONTH, iMonthViewCurrentDay); 
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); 
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

    } 
  
    private void UpdateStartDateForMonth() { 
    	iMonthViewCurrentDay = calStartDate.get(Calendar.DAY_OF_MONTH);
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		
		/*int iDay = 0; 
        int iFirstDayOfWeek = Calendar.MONDAY; 
        int iStartDay = iFirstDayOfWeek; 
        if (iStartDay == Calendar.MONDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        if (iStartDay == Calendar.SUNDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        //calStartDate.add(Calendar.DAY_OF_WEEK, -iDay); */
    } 
  
    //public void setMarkDates(List<Date> markDates) { 
    public void viewChanged() { 
        //this.markDates.clear(); 
        //this.markDates.addAll(markDates); 
        gAdapter.notifyDataSetChanged(); 
        //gAdapter1.notifyDataSetChanged(); 
        //gAdapter3.notifyDataSetChanged(); 
    } 
  
    public void setOnCalendarViewListener(OnCalendarViewListener listener) { 
        this.mListener = listener; 
    } 
  
    @Override
    public boolean onDown(MotionEvent e) { 
        // TODO Auto-generated method stub         
		return false; 
    } 
  
    @SuppressLint("ClickableViewAccessibility") 
    @Override
    public boolean onTouch(View v, MotionEvent event) { 
        return mGesture.onTouchEvent(event); 
    } 
  
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, 
            float velocityY) { 
        // TODO Auto-generated method stub         
		try { 
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) 
                return false; 
            // right to left swipe             
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE 
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { 
                viewFlipper.setInAnimation(slideLeftIn); 
                viewFlipper.setOutAnimation(slideLeftOut); 
                viewFlipper.showNext(); 
                setNextViewItem();   
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE 
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { 
                viewFlipper.setInAnimation(slideRightIn); 
                viewFlipper.setOutAnimation(slideRightOut); 
                viewFlipper.showPrevious(); 
                setPrevViewItem();   
            }
			
	        caleCounter.setTime(calStartDate.getTime());
	        gAdapterGetClodeBridge = new CalendarGridAdapterForCallBack(mContext, caleCounter);
	        calenderCounterList = (ArrayList<Date>) gAdapterGetClodeBridge.getAllItem();    //当前View包含日期。
	        curCalendarPointCounter.clear() ;                                               //记录一月中当天pointer的个数。
	        getHistoryTraceCounterByDay((calenderCounterList.get(6)), (calenderCounterList.get(0))) ;
	        return true ;

        } catch (Exception e) { 
            // nothing         
		} 
        return false; 
    } 
  
    @Override
    public void onLongPress(MotionEvent e) { 
        // TODO Auto-generated method stub 
    } 
  
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, 
            float distanceY) { 
        // TODO Auto-generated method stub         
		return false; 
    } 
  
    @Override
    public void onShowPress(MotionEvent e) { 
        // TODO Auto-generated method stub 
    } 
  
    @Override
    public boolean onSingleTapUp(MotionEvent e) { 
        // TODO Auto-generated method stub         
		int pos = gView2.pointToPosition((int) e.getX(), (int) e.getY());
		int pointNum ;
		
		if(curCalendarPointCounter.size()<1){
			pointNum = 0;
		}else {
			if(pos>0 && pos <=curCalendarPointCounter.size()){
			    pointNum = curCalendarPointCounter.get(pos) ;
			}else
				pointNum = 0 ;
		}
        LinearLayout txtDay = gView2.findViewById(pos+ DEFAULT_ID);
        
        if (txtDay != null) {
        	//txtDay.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.indicator));
            if (txtDay.getTag() != null) {
                Date date = (Date) txtDay.getTag();
                
                /*if(curSelectDate != null && date != null  ){
                	if( curSelectDate.compareTo(date) == 0){
                		return false ;
                	}
                }*/
                curSelectDate = date ;
                //calSelected.setTime(date); 
  
                //gAdapter.setSelectedDate(calSelected); 
                //gAdapter.notifyDataSetChanged(); 
  
                //gAdapter1.setSelectedDate(calSelected); 
                //gAdapter1.notifyDataSetChanged(); 
  
                //gAdapter3.setSelectedDate(calSelected); 
                //gAdapter3.notifyDataSetChanged(); 
                int result=Calendar.getInstance().getTime().compareTo(date);
                if(result < 0){  //时间大于今天
        			ToastUtil.showMyToast(mContext, 
						mContext.getResources().getString(R.string.set_error5),
        					Toast.LENGTH_SHORT);
        			return false ;
                }

                if (mListener != null) 
                    mListener.onCalendarItemClick(this, date , txtDay , pointNum); 
            } 
        } 
        return false; 
    } 
  
    @Override
    public void onAnimationEnd(Animation animation) { 
        // TODO Auto-generated method stub         
		generateClaendarGirdView(); 
    } 
  
    @Override
    public void onAnimationRepeat(Animation animation) { 
        // TODO Auto-generated method stub 
    } 
  
    @Override
    public void onAnimationStart(Animation animation) { 
        // TODO Auto-generated method stub 
    }
    
    public int getMonthViewCurrentMonth(){
    	return iMonthViewCurrentMonth ;
    }
    
    public ArrayList<Integer> getCurCalendarViewCounter(){
    	return curCalendarPointCounter ; 
    }
    
	private void getHistoryTraceCounterByDay(Date beginDay, Date endDay){
    	final SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

        MyMsgData retrieve = new MyMsgData();
        retrieve.setCallback(CalendarView.this);
        //set msg info
        JSONObject pl = new JSONObject();
		pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatch.getEid());
		pl.put(CloudBridgeUtil.KEY_NAME_KEY_BEGIN, TimeUtil.getReversedOrderTime(formatDateKey.format(beginDay)+"235959999"));
		pl.put(CloudBridgeUtil.KEY_NAME_KEY_END, TimeUtil.getReversedOrderTime(formatDateKey.format(endDay)+"000000000"));
        retrieve.setReqMsg( CloudBridgeUtil.obtainCloudMsgContent(
        		        CloudBridgeUtil.CID_TRACE_COUNTER_DATA,
                		Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                		myApp.getToken() , pl));
        if(null != myApp.getNetService())
        myApp.getNetService().sendNetMsg(retrieve);
	}

	@Override
	public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
    	final SimpleDateFormat formatDateKey = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

		// TODO Auto-generated method stub
		int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
		if(1 == rc)
		{
			int cid = (Integer)respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
			switch (cid) {
			case CloudBridgeUtil.CID_TRACE_COUNTER_DATA_RESP:
				JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
				boolean needChange  = false ;
				for(int i=0 ; i<7 ; i++){
					Date myDate = calenderCounterList.get(i);
					String dayKey = (TimeUtil.getReversedOrderTime(formatDateKey.format(myDate)+"000000000")).substring(0, 8) ;
					if(pl.get(dayKey) == null)
						curCalendarPointCounter.add(0);
					else {
						needChange = true ;
						curCalendarPointCounter.add((Integer) pl.get(dayKey)) ;
					}
				}
				if(needChange)
				    viewChanged() ;
			default:
				break;
			}
		}else if(rc == -202)
			ToastUtil.showMyToast(mContext, 
				mContext.getResources().getString(R.string.set_error8),
				Toast.LENGTH_SHORT);
		else if(rc < 0 )
			ToastUtil.showMyToast(mContext,  
				mContext.getResources().getString(R.string.get_error),
				Toast.LENGTH_SHORT);
	}
}
 
class WeekGridAdapter extends BaseAdapter { 
  
    final String[] titles = new String[] { "周日", "周一", "周二", "周三", "周四", "周五", "周六" }; 
    private Context mContext; 
  
    public WeekGridAdapter(Context context) { 
        this.mContext = context; 
    } 
  
    @Override
    public int getCount() { 
        return titles.length; 
    } 
  
    @Override
    public Object getItem(int position) { 
        return titles[position]; 
    } 
  
    @Override
    public long getItemId(int position) { 
        return position; 
    } 
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
        /*TextView week = new TextView(mContext); 
        android.view.ViewGroup.LayoutParams week_params = new LayoutParams( 
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 71);
		//LinearLayout.LayoutParams week_params = new LinearLayout.LayoutParams(
         //       LayoutParams.WRAP_CONTENT, 71); 
        //week_params.setMargins(0, 0, 0, 44) ;
        week.setLayoutParams(week_params); 
        week.setPadding(0, 0, 0, 0); 
        week.setGravity(Gravity.CENTER_HORIZONTAL); 
        week.setFocusable(false); 
        week.setBackgroundColor(Color.TRANSPARENT);
        week.setTextColor(mContext.getResources().getColor(R.color.dark_grey));  
        week.setText(getItem(position) + ""); 
        week.setTextSize(9) ;
        return week;*/
        
        LinearLayout itemLayout = new LinearLayout(mContext); 
        itemLayout.setGravity(Gravity.CENTER_HORIZONTAL); 
        itemLayout.setOrientation(1);
        //itemLayout.setBackgroundColor(0xff33b19e);
        itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.xiaomi_bg_grey));
        
		LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		//text_params.setMargins(0, 0, 0, 4);
        TextView week = new TextView(mContext); 
        week.setTextSize(9) ;
        week.setText(getItem(position) + ""); 
        week.setTextColor(mContext.getResources().getColor(R.color.dark_grey));  
        week.setBackgroundColor(mContext.getResources().getColor(R.color.xiaomi_bg_grey));
        itemLayout.addView(week, text_params); 

        return itemLayout;
    } 
}

class CalendarGridAdapterForCallBack extends BaseAdapter { 
    private Calendar calStartDate = Calendar.getInstance();
	private ArrayList<java.util.Date> titles; 
  
    private ArrayList<java.util.Date> getDates() {
        UpdateStartDateForMonth(); 
        ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>(); 
        for (int i = 1; i <= 7; i++) { 
            alArrayList.add(calStartDate.getTime()); 
            calStartDate.add(Calendar.DAY_OF_MONTH, 1); 
        } 
        return alArrayList; 
    } 

	public CalendarGridAdapterForCallBack(Context context, Calendar cal ) { 
        calStartDate = cal; 
        titles = getDates(); 
    }
  
    @Override
    public int getCount() { 
        return titles.size(); 
    } 
  
    @Override
    public Object getItem(int position) { 
        return titles.get(position); 
    } 
  
    @Override
    public long getItemId(int position) { 
        return position; 
    } 
  
    @Override
    public void notifyDataSetChanged() { 
        super.notifyDataSetChanged(); 
    }
    
    public Object getAllItem() { 
        return titles; 
    } 
  
    private void UpdateStartDateForMonth() { 
        int iDay = 0; 
        int iFirstDayOfWeek = Calendar.MONDAY; 
        int iStartDay = iFirstDayOfWeek; 
        if (iStartDay == Calendar.MONDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        if (iStartDay == Calendar.SUNDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay); 
        calStartDate.add(Calendar.DAY_OF_MONTH, -1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	} 
} 


class CalendarGridViewAdapter extends BaseAdapter { 

    private final static int DEFAULT_ID = 0xff0000; 
    private static final int CALENDAR_GRID_HIGHT = 118 ;
    private static final int HISTORY_TODAY_HIGHT = 50 ;
    
    private Calendar calStartDate = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance(); 

    //private List<Date> markDates; 
  
    private Context mContext;
    private CalendarView mCalendarView;
  
    private Calendar calToday = Calendar.getInstance(); 
    private ArrayList<java.util.Date> titles; 
    ArrayList<Integer> counterList ; 
    
    Typeface mCustomFontType ;
  
    private ArrayList<java.util.Date> getDates() { 
  
        UpdateStartDateForMonth(); 
  
        ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>(); 
  
        for (int i = 1; i <= 7; i++) { 
            alArrayList.add(calStartDate.getTime()); 
            calStartDate.add(Calendar.DAY_OF_MONTH, 1); 
        } 
  
        return alArrayList; 
    } 

	//public CalendarGridViewAdapter(Context context, Calendar cal, List<Date> dates , CalendarView tmpObject ) { 
    public CalendarGridViewAdapter(Context context, Calendar cal, CalendarView tmpObject ) { 
        calStartDate = cal; 
        this.mContext = context; 
        titles = getDates(); 
        //this.markDates = dates; 
        this.mCalendarView = tmpObject ;
        counterList = mCalendarView.getCurCalendarViewCounter();
        
		mCustomFontType = Typeface.createFromAsset(context.getAssets(),"date.ttf");
    } 
  
    public CalendarGridViewAdapter(Context context) { 
        this.mContext = context; 
    } 
  
    @Override
    public int getCount() { 
        return titles.size(); 
    } 
  
    @Override
    public Object getItem(int position) { 
        return titles.get(position); 
    } 
    
    public Object getAllItem() { 
        return titles; 
    } 
  
    @Override
    public long getItemId(int position) { 
        return position; 
    } 
  
    @SuppressWarnings("deprecation") 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LinearLayout itemLayout = new LinearLayout();
        LinearLayout itemLayout = new LinearLayout(mContext); 
        itemLayout.setId(position + DEFAULT_ID); 
        itemLayout.setGravity(Gravity.CENTER_HORIZONTAL); 
        itemLayout.setOrientation(1); 
        itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.xiaomi_bg_grey));

        Date myDate = (Date) getItem(position); 
        itemLayout.setTag(myDate); 
        Calendar calCalendar = Calendar.getInstance(); 
        calCalendar.setTime(myDate); 
  
        TextView textDay = new TextView(mContext);
        //textDay.setGravity(Gravity.CENTER); 
        int day = myDate.getDate();
        textDay.setTextSize(20);
		textDay.setText(String.valueOf(day)); 
        textDay.setId(position + DEFAULT_ID);
        textDay.setTypeface(mCustomFontType);
        //textDay.setBackgroundColor(0xff33b19e);

        if(myDate.getMonth() != mCalendarView.getMonthViewCurrentMonth()){
        	textDay.setTextColor(mContext.getResources().getColor(R.color.txt_grey));
        }

		LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		text_params.setMargins(0, 42, 0, 0);
        itemLayout.addView(textDay, text_params); 
  
        if (equalsDate(calToday.getTime(), myDate)) {
    		/*LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		dayParams.setMargins(0, (CALENDAR_GRID_HIGHT-HISTORY_TODAY_HIGHT)/2, 0, 0);
    		textDay.setLayoutParams(dayParams) ;
            textDay.setBackground(mContext.getResources().getDrawable(R.drawable.history_today));*/
    		textDay.setTextColor(0xff33b19e); 
        }
        
        //if(counterList.size()<1)
    	//	ToastUtil.showMyToast(mContext, mContext.getResources().getString(R.string.set_error9), Toast.LENGTH_SHORT);
        if(counterList.size()>0){
        	if(counterList.get(position)>0){
        	    /*LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		    dayParams.setMargins(0, (CALENDAR_GRID_HIGHT-HISTORY_TODAY_HIGHT)/2, 0, 0);
    		    textDay.setLayoutParams(dayParams) ;*/
    		    
    	        if (equalsDate(calToday.getTime(), myDate)) {
    	        	textDay.setTextColor(0xff33b19e); 
    	            //textDay.setBackground(mContext.getResources().getDrawable(R.drawable.history_today_mark));
    	        }else
    	        	textDay.setTextColor(0xff33b19e); 
                    //textDay.setBackground(mContext.getResources().getDrawable(R.drawable.history_mark_1));
            }
        }
        return itemLayout;
    } 
  
    @Override
    public void notifyDataSetChanged() { 
        super.notifyDataSetChanged(); 
    } 
  
    @SuppressWarnings("deprecation") 
    private Boolean equalsDate(Date date1, Date date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate();
  
    } 
  
    private void UpdateStartDateForMonth() { 
        int iDay = 0; 
        int iFirstDayOfWeek = Calendar.MONDAY; 
        int iStartDay = iFirstDayOfWeek; 
        if (iStartDay == Calendar.MONDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        if (iStartDay == Calendar.SUNDAY) { 
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; 
            if (iDay < 0) 
                iDay = 6; 
        } 
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay); 
        calStartDate.add(Calendar.DAY_OF_MONTH, -1);
	} 
  
    public void setSelectedDate(Calendar cal) { 
        calSelected = cal; 
    } 
} 

class CalendarGridView extends GridView { 

    private Context mContext; 

    public CalendarGridView(Context context) { 
        super(context); 
        mContext = context; 
        initGirdView(); 
    } 

    private void initGirdView() { 
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        setLayoutParams(params); 
        setNumColumns(7);         
		setGravity(Gravity.CENTER_VERTICAL);
		setVerticalSpacing(1);
		setHorizontalSpacing(1);
		setBackgroundColor(mContext.getResources().getColor(R.color.xiaomi_bg_grey)); 
  
        int i = mContext.getResources().getDisplayMetrics().widthPixels / 7; 
        int j = mContext.getResources().getDisplayMetrics().widthPixels - (i * 7); 
        int x = j / 2; 
        setPadding(x, 0, 0, 0);    
	} 
} 

  class StringUtil { 
    public static boolean isNullOrEmpty(String str) { 
        return str == null || str.trim().length() == 0; 
    } 

    public static boolean equals(String str1, String str2) { 
        return str1 == str2 || str1 != null && str1.equals(str2); 
    } 

    public static boolean equalsIgnoreCase(String str1, String str2) { 
        return str1 != null && str1.equalsIgnoreCase(str2); 
    } 

    public static boolean contains(String str1, String str2) { 
        return str1 != null && str1.contains(str2); 
    } 

    public static String getString(String str) { 
        return str == null ? "" : str; 
    } 
} 
 
 class NumberHelper { 
    public static String LeftPad_Tow_Zero(int str) { 
        java.text.DecimalFormat format = new java.text.DecimalFormat("00"); 
        return format.format(str); 
    } 
}
