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

public class WatchLocationPopUpWindow extends PopupWindow {
    private View mMenuView;

    private ImibabyApp mApp;
    
    public WatchLocationPopUpWindow(Activity context,OnClickListener itemsOnClick) {
        super(context);
        mApp = (ImibabyApp)context.getApplication();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_watch_location, null);
 
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
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
             
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();  
                return true;
            }
        });
  
    }
}
