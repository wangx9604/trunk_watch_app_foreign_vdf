package com.xiaoxun.xun.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xiaoxun.xun.R;

/**
 * 简单的toast弹窗，暂只添加文本显示，
 */
public class ToastFastUtils {

    public ToastFastUtils(FastSimpleDialogBuilder builder) {
        Context context = builder.context;
        String strText = builder.strText;
        int styleId = builder.styleId;
        int gravityResId = builder.gravityResId;
        int gravityXOffset = builder.gravityXOffset;
        int gravityYOffset = builder.gravityYOffset;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_fast_simple1, null); //加載layout下的布局
        TextView textView = view.findViewById(R.id.tv_toast);
        ConstraintLayout cl = view.findViewById(R.id.cl_toast);
        Toast toast = new Toast(context);
        if(strText !=null){
            textView.setText(strText);
        }
        if(styleId !=0){
            cl.setBackgroundResource(styleId);
        }
        if(gravityResId !=0){
            toast.setGravity(gravityResId, gravityXOffset, gravityYOffset);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static class  FastSimpleDialogBuilder{
        private final Context context;
        private String strText;
        private int styleId;
        private int gravityResId;
        private int gravityXOffset;
        private int gravityYOffset;
        public FastSimpleDialogBuilder(Context context) {
            this.context = context;
        }
        //文本
        public FastSimpleDialogBuilder setText(String resId){
            strText = resId;
            return this;
        }
        //view样式
        public FastSimpleDialogBuilder setStyle(int resId){
            styleId = resId;
            return this;
        }
        //显示位置
        public FastSimpleDialogBuilder setGravity(int gravity, int xOffset, int yOffset){
            gravityResId = gravity;
            gravityXOffset = xOffset;
            gravityYOffset = yOffset;
            return this;
        }

        public ToastFastUtils build(){
            return new ToastFastUtils(this);
        }
    }
}
