package com.xiaoxun.xun.calendar;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
  
public class CalendarViewAdapter<V extends View> extends PagerAdapter {  
    public static final String TAG = "CalendarViewAdapter";  
    private V[] views;  
  
    public CalendarViewAdapter(V[] views) {  
        super();  
        this.views = views;  
    }  
  
      
    @Override  
    public Object instantiateItem(ViewGroup container, int position) {  
              
//        if (((ViewPager) container).getChildCount() == views.length) {  
//            ((ViewPager) container).removeView(views[position % views.length]);  
//        }  
//          
//        ((ViewPager) container).addView(views[position % views.length], 0);  
//        return views[position % views.length];  
    	container.addView(views[position], 0);
    	return views[position];
    }  
  
    @Override  
    public int getCount() {  
        return views.length;  
    }  
  
    @Override  
    public boolean isViewFromObject(View view, Object object) {  
        return view == object;
    }  
  
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
//        ((ViewPager) container).removeView((View) container);
    	container.removeView(views[position]);
    }  
      
    public V[] getAllItems() {  
        return views;  
    }  
  
}  
