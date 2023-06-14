package com.xiaoxun.xun.views;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;


public class ForceRecordPopUpWindow extends PopupWindow {
    private View mMenuView;

    private ImibabyApp mApp;
    public ForceRecordPopUpWindow(Activity context,OnClickListener itemsOnClick) {
        super(context);
        mApp = (ImibabyApp)context.getApplication();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_force_record, null);
        
        
        //设置ChatPopupWindow的View
        this.setContentView(mMenuView);
        //设置ChatPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置ChatPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        //设置ChatPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置ChatPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置ChatPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        Runnable action = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			    dismiss();
			}
		};
		
        mMenuView.postDelayed(action, 2000);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
             
            public boolean onTouch(View v, MotionEvent event) {
                 
                int height_top = mMenuView.findViewById(R.id.luying).getTop();
                int height_bottom = mMenuView.findViewById(R.id.luying).getBottom();
                int width_left = mMenuView.findViewById(R.id.luying).getLeft();
                int width_right = mMenuView.findViewById(R.id.luying).getRight();
                int y=(int) event.getY();
                int x=(int) event.getX();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height_top || y>height_bottom ||x<width_left || x>width_right){
                        dismiss();
                    }
                }               
                return true;
            }
        });
  
    }
}
