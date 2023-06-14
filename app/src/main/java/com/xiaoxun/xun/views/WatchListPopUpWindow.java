package com.xiaoxun.xun.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.ImageUtil;

import java.util.Collections;
import java.util.Comparator;

public class WatchListPopUpWindow extends PopupWindow {

    private LinearLayout mWatchList;
    private View mMenuView;
    private ImibabyApp mApp;
    private ViewFlipper flipper;
    private boolean mChangWatch = false;
    public WatchListPopUpWindow(final Activity context, OnClickListener itemsOnClick) {
        super(context);

        mApp = (ImibabyApp) context.getApplication();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_watch_list, null);
        flipper = mMenuView.findViewById(R.id.flipper);
        mWatchList = mMenuView.findViewById(R.id.watch_list);
        Collections.sort(mApp.getCurUser().getWatchList(), new Comparator<WatchData>() {
            @Override
            public int compare(WatchData lhs, WatchData rhs) {
//                return Collator.getInstance(Locale.CHINESE).compare(lhs.getNickname(),rhs.getNickname());
                if (lhs == null || lhs.getNickname() == null)
                    return 1;
                if (rhs == null || rhs.getNickname() == null)
                    return -1;
                return lhs.getNickname().compareTo(rhs.getNickname());
            }
        });
        for (final WatchData watch:mApp.getCurUser().getWatchList()){
            View item = inflater.inflate(R.layout.spinner_watch_list_item, null);
            ImageView mHeadView = item.findViewById(R.id.title_head_mask);
            ImageView mSelector = item.findViewById(R.id.selector);
            TextView mNiceName = item.findViewById(R.id.title_nice_name);
//            ImageView mRedDot = (ImageView) item.findViewById(R.id.indicator_1);

            ImageUtil.setMaskImage(mHeadView, R.drawable.head_1, ((ImibabyApp) context.getApplicationContext()).getHeadDrawableByFile(context.getResources(), ((ImibabyApp) context.getApplicationContext()).getCurUser().getHeadPathByEid(watch.getEid()), watch.getEid(), R.drawable.small_default_head));
            mNiceName.setText(watch.getNickname());
            if (watch.getEid().equals(mApp.getCurUser().getFocusWatch().getEid())) {
                mSelector.setVisibility(View.VISIBLE);
            } else {
                mSelector.setVisibility(View.INVISIBLE);
            }
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApp.setFocusWatch(watch);
                    mChangWatch = true;
                    flipper.showPrevious();
                }
            });
            mWatchList.addView(item);
        }
        flipper.setDisplayedChild(1);
        flipper.showNext();
        //设置ChatPopupWindow的View
        this.setContentView(mMenuView);
        //设置ChatPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置ChatPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        //设置ChatPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置ChatPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.anim.main_title_popup);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置ChatPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        flipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int num = flipper.getDisplayedChild();
                if (num == 1) {
                    if (mChangWatch) {
                        Intent it = new Intent(Const.ACTION_CHANGE_WATCH);
                        context.sendBroadcast(it);
                    }
                    Handler hand = new Handler();
                    hand.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 10);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height_top = mMenuView.findViewById(R.id.watch_list).getTop();
                int height_bottom = mMenuView.findViewById(R.id.view2).getBottom();
                int width_left = mMenuView.findViewById(R.id.watch_list).getLeft();
                int width_right = mMenuView.findViewById(R.id.watch_list).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height_top || y > height_bottom) {
                        flipper.showPrevious();
                    }
                }
                return true;
            }
        });
    }
}
