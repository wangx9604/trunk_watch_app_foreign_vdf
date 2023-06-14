package com.xiaoxun.xun.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.OnMonthChangeListener;

import java.util.Calendar;

/**
 * Created by xilvkang on 2015/12/16.
 */
public class monthSelect extends PopupWindow implements
        android.view.View.OnClickListener {
    private Context mContext;
    private ImageButton preImgBtn, nextImgBtn;
    private ViewFlipper flipper;
    private View mView;
    private TextView monthText;
    private int curMonth; //0当前月 1 上个月 2 上2个月
    private OnMonthChangeListener listenerMonth;


    public monthSelect(Context context, int month, OnMonthChangeListener listener) {
        super(context);
        mContext = context;
        curMonth = month;
        listenerMonth = listener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.bill_calendar, null);
        flipper = mView.findViewById(R.id.flipper);
        preImgBtn = mView.findViewById(R.id.btnPreMonth);
        preImgBtn.setOnClickListener(this);
        nextImgBtn = mView.findViewById(R.id.btnNextMonth);
        nextImgBtn.setOnClickListener(this);
        monthText = mView.findViewById(R.id.tvCurrentMonth);

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int top = flipper.getTop();
                int bottom = flipper.getBottom();
                int y = (int) motionEvent.getY();
                int x = (int) motionEvent.getX();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < top || y > bottom) {
                        showNextFromDown();
                    }
                }
                return true;
            }
        });

        //设置ChatPopupWindow的View
        this.setContentView(mView);
        //设置ChatPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置ChatPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
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
                if (flipper.getDisplayedChild() == 1) {
                    Handler mhandler = new Handler();
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 10);
                    //dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        updateMonthText();
    }

    private void updateMonthText() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;

        if (curMonth == 0) {
            preImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.left_small_arrow_0));
            nextImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.right_small_arrow_1));
        } else if (curMonth == 1) {
            cal.add(Calendar.MONTH, -1);
            month = cal.get(Calendar.MONTH) + 1;
            preImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.left_small_arrow_0));
            nextImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.right_small_arrow_0));
        } else if (curMonth == 2) {
            cal.add(Calendar.MONTH, -2);
            month = cal.get(Calendar.MONTH) + 1;
            preImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.left_small_arrow_1));
            nextImgBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.right_small_arrow_0));
        }
        int year = cal.get(Calendar.YEAR);
        monthText.setText(year + "年" + month + "月");
        listenerMonth.monthChange(curMonth,month);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPreMonth:
                if(curMonth<2)
                    curMonth++;
                break;
            case R.id.btnNextMonth:
                if(curMonth>0)
                    curMonth--;
                break;
        }
        updateMonthText();
    }

    public void showNextFromDown() {
        flipper.showPrevious();
    }

    public void showNextFromUp() {
        flipper.setDisplayedChild(1);
        flipper.showNext();
    }

    public void showMonth() {
        //flipper.showNext();
        showNextFromUp();
    }

}