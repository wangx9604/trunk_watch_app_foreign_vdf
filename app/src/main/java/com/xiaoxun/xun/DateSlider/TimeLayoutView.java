package com.xiaoxun.xun.DateSlider;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimeLayoutView extends LinearLayout{
    protected long endTime, startTime;
    protected String text;
    protected TextView monthView, topView, bottomView;

    Typeface mCustomFontType ;
    private Calendar calToday = Calendar.getInstance(); 

    public TimeLayoutView(Context context, int topTextSize, int bottomTextSize) {
        super(context);
        setupView(context, topTextSize, bottomTextSize);
    }

    @SuppressWarnings("ResourceType")
    protected void setupView(Context context, int topTextSize, int bottomTextSize) {
    	this.setGravity(Gravity.CENTER_HORIZONTAL); 
        setOrientation(VERTICAL);
		mCustomFontType = Typeface.createFromAsset(context.getAssets(),"date.ttf");
        topView = new TextView(context);
        topView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, topTextSize);
        topView.setTypeface(mCustomFontType);
        bottomView = new TextView(context);
        bottomView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bottomTextSize);
        monthView = new TextView(context);
        monthView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, topTextSize/2);
        monthView.setText("0/");
        monthView.setId(123456);
        monthView.setTypeface(mCustomFontType);
        
        RelativeLayout mLayout = new RelativeLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        mLayout.setLayoutParams(params); 
        
        RelativeLayout.LayoutParams month_params = new RelativeLayout.LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
        month_params.topMargin = 34;
        month_params.leftMargin = 35 ;
        mLayout.addView(monthView , month_params);
        RelativeLayout.LayoutParams top_params = new RelativeLayout.LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
        top_params.topMargin = 34;
        top_params.addRule(RelativeLayout.RIGHT_OF, 123456);
        mLayout.addView(topView, top_params);
        
        addView(mLayout);
        
        LinearLayout.LayoutParams bottom_text_params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
        bottom_text_params.setMargins(0, 6, 0, 0);
        addView(bottomView,bottom_text_params);
    }
    
    public void setVals(TimeObject to , boolean addColor) {
        text = to.text.toString();
        setText();
        this.startTime = to.startTime;
        this.endTime = to.endTime;
        setDayColor(addColor);
    }
    
    public void setVals(TimeLayoutView other , boolean addColor) {
        text = other.getTimeText();
        setText();
        startTime = other.getStartTime();
        endTime = other.getEndTime();
        setDayColor(addColor);
    }
    
    public void setDayColor(boolean addColor){
    	Calendar c = Calendar.getInstance();
        c.setTimeInMillis(endTime);
        Date today = c.getTime() ;
        if(addColor){
        	monthView.setTextColor(0xffff0000);
        	topView.setTextColor(0xffff0000); 
        	bottomView.setTextColor(0xffff0000);
        }else{
            if (equalsDate(calToday.getTime(), today)) {
            	monthView.setTextColor(0xff33b19e);
            	topView.setTextColor(0xff33b19e); 
            	bottomView.setTextColor(0xff33b19e);
            }
            //test day color
            else if(calToday.getTime().getDate() < today.getDate()){
            	monthView.setTextColor(0xffb5b5b4);
            	topView.setTextColor(0xffb5b5b4); 
            	bottomView.setTextColor(0xffb5b5b4);
            }
            else{
            	monthView.setTextColor(0xff000000); 
            	topView.setTextColor(0xff000000); 
            	bottomView.setTextColor(0xff000000);
            }
        }
        String sMonthText = (today.getMonth() + 1) +"/" ;
        monthView.setText(sMonthText);
    	RelativeLayout.LayoutParams month_params = (RelativeLayout.LayoutParams) monthView.getLayoutParams() ;
    	month_params.topMargin = 34;
        if(sMonthText.length()==3){
            month_params.leftMargin = 25 ;
        }else{
            month_params.leftMargin = 35 ;
        }
        monthView.setLayoutParams(month_params) ;
        monthView.requestLayout() ;
    }

    protected void setText() {
        String[] splitTime = text.split(" ");
        topView.setText(splitTime[0]);
        bottomView.setText(splitTime[1]);
    }
    
    public String getTimeText() {
        return text;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    @SuppressWarnings("deprecation") 
    private Boolean equalsDate(Date date1, Date date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate();
  
    } 
}