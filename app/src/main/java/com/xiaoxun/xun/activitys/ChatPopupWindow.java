package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;


public class ChatPopupWindow extends PopupWindow implements OnClickListener {
    private ImageButton mBtnDelete;
    private ImageButton mBtnDeleteAll;
    private ImageButton mBtnOutputChat;
    private ImageButton mUseCall;
    private TextView mUseCallText;
    private ImibabyApp mApp;
    private View mMenuView;
    private ImageButton mCopyBtn;
    private RelativeLayout mCopyLayout;

    public ChatPopupWindow(Activity context, String message, OnClickListener itemOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApp = (ImibabyApp) context.getApplication();
        mMenuView = inflater.inflate(R.layout.activity_pop, null);
        TextView textView = mMenuView.findViewById(R.id.tv_set_top_item);
        textView.setText(message);
        ImageButton itemBtn = mMenuView.findViewById(R.id.btn_set_top);
        itemBtn.setOnClickListener(itemOnClick);
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

                int height_top = mMenuView.findViewById(R.id.layout_set_top_item).getTop();
                int height_bottom = mMenuView.findViewById(R.id.layout_set_top_item).getBottom();
                int width_left = mMenuView.findViewById(R.id.layout_set_top_item).getLeft();
                int width_right = mMenuView.findViewById(R.id.layout_set_top_item).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height_top || y > height_bottom || x < width_left || x > width_right) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
    public ChatPopupWindow(Activity context, OnClickListener itemsOnClick, OnClickListener deleteAllMessage, OnClickListener output, OnClickListener copyMessage) {
        this(context, itemsOnClick, deleteAllMessage);
        if (copyMessage != null) {
            mCopyLayout.setVisibility(View.VISIBLE);
            mCopyBtn.setOnClickListener(copyMessage);
        }
    }

    public ChatPopupWindow(String s, Activity context, OnClickListener share, OnClickListener save) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApp = (ImibabyApp) context.getApplication();
        mMenuView = inflater.inflate(R.layout.pop_chat_activity, null);
        mBtnDelete = (ImageButton) mMenuView.findViewById(R.id.deleteitem);
        mBtnDeleteAll = (ImageButton) mMenuView.findViewById(R.id.alldelete);
        mBtnOutputChat = (ImageButton) mMenuView.findViewById(R.id.outputitem);
        mCopyBtn = (ImageButton) mMenuView.findViewById(R.id.copy_text);
        mCopyLayout = (RelativeLayout) mMenuView.findViewById(R.id.layout_copy_text);
        mUseCall = (ImageButton) mMenuView.findViewById(R.id.use_call);

        //设置按钮监听
        mBtnDelete.setOnClickListener(share);
        ((TextView) mMenuView.findViewById(R.id.tv_delete_item)).setText(context.getString(R.string.share_title));
        mBtnDeleteAll.setOnClickListener(save);
        ((TextView) mMenuView.findViewById(R.id.tv_delete_all)).setText(context.getString(R.string.save_image));

        (mMenuView.findViewById(R.id.layout_use_call)).setVisibility(View.GONE);
        (mMenuView.findViewById(R.id.layout_output_item)).setVisibility(View.GONE);
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

                int height_top = mMenuView.findViewById(R.id.pop_layout).getTop();
                int height_bottom = mMenuView.findViewById(R.id.pop_layout).getBottom();
                int width_left = mMenuView.findViewById(R.id.pop_layout).getLeft();
                int width_right = mMenuView.findViewById(R.id.pop_layout).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height_top || y > height_bottom || x < width_left || x > width_right) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    public ChatPopupWindow(Activity context, OnClickListener delete, OnClickListener deleteAll, OnClickListener output) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApp = (ImibabyApp) context.getApplication();
        mMenuView = inflater.inflate(R.layout.pop_chat_activity, null);
        mBtnDelete = mMenuView.findViewById(R.id.deleteitem);
        mBtnDeleteAll = mMenuView.findViewById(R.id.alldelete);
        mBtnOutputChat = mMenuView.findViewById(R.id.outputitem);
        mUseCall = mMenuView.findViewById(R.id.use_call);
        mUseCall.setOnClickListener(ChatPopupWindow.this);
        mUseCallText = mMenuView.findViewById(R.id.use_call_text);
        if (mApp.getmUseCall()) {
            mUseCallText.setText(R.string.use_no_call);
        }
        //设置按钮监听
        mBtnDelete.setOnClickListener(delete);
        mBtnDeleteAll.setOnClickListener(deleteAll);
        mBtnOutputChat.setOnClickListener(output);
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

                int height_top = mMenuView.findViewById(R.id.pop_layout).getTop();
                int height_bottom = mMenuView.findViewById(R.id.pop_layout).getBottom();
                int width_left = mMenuView.findViewById(R.id.pop_layout).getLeft();
                int width_right = mMenuView.findViewById(R.id.pop_layout).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height_top || y > height_bottom || x < width_left || x > width_right) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    public ChatPopupWindow(Activity context, OnClickListener delete, OnClickListener deleteAll) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApp = (ImibabyApp) context.getApplication();
        mMenuView = inflater.inflate(R.layout.pop_chat_activity, null);
        mBtnDelete = mMenuView.findViewById(R.id.deleteitem);
        mBtnDeleteAll = mMenuView.findViewById(R.id.alldelete);
        mBtnOutputChat = mMenuView.findViewById(R.id.outputitem);
        mCopyBtn = mMenuView.findViewById(R.id.copy_text);
        mCopyLayout = mMenuView.findViewById(R.id.layout_copy_text);
        mUseCall = mMenuView.findViewById(R.id.use_call);
        mUseCall.setOnClickListener(ChatPopupWindow.this);
        mUseCallText = mMenuView.findViewById(R.id.use_call_text);
        if (mApp.getmUseCall()) {
            mUseCallText.setText(R.string.use_no_call);
        }
        //设置按钮监听
        mBtnDelete.setOnClickListener(delete);
        ((TextView) mMenuView.findViewById(R.id.tv_delete_item)).setText(context.getString(R.string.delete_msg));
        mBtnDeleteAll.setOnClickListener(deleteAll);
        ((TextView) mMenuView.findViewById(R.id.tv_delete_all)).setText(context.getString(R.string.clear_messge_title));

        (mMenuView.findViewById(R.id.layout_use_call)).setVisibility(View.GONE);
        (mMenuView.findViewById(R.id.layout_output_item)).setVisibility(View.GONE);
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

                int height_top = mMenuView.findViewById(R.id.pop_layout).getTop();
                int height_bottom = mMenuView.findViewById(R.id.pop_layout).getBottom();
                int width_left = mMenuView.findViewById(R.id.pop_layout).getLeft();
                int width_right = mMenuView.findViewById(R.id.pop_layout).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height_top || y > height_bottom || x < width_left || x > width_right) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.use_call:
                mApp.setmUseCall(!mApp.getmUseCall());
                Intent intt = new Intent();
                intt.setAction(Const.ACTION_RECEIVE_CHANGE_AUDIO_MODE);
                mApp.sendBroadcast(intt);
                dismiss();
                break;

            default:
                break;
        }
    }
}
