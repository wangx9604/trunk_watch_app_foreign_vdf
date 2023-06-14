package com.xiaoxun.xun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class scrollContainer extends LinearLayout{

	public scrollContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public scrollContainer(Context context,AttributeSet attr) {
		super(context,attr);
		// TODO Auto-generated constructor stub
	}
   @Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}
}
