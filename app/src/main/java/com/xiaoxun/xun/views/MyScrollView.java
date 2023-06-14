/**
 * Creation Date:2015-5-13
 * 
 * Copyright 
 */
package com.xiaoxun.xun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-5-13
 * 
 */
public class MyScrollView extends ScrollView {
    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public MyScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
    }
    
    public MyScrollView(Context context) {
            super(context);
    }
   
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
            
    return false;
    }    
}
