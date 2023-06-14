package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.BlankActivity;
import com.xiaoxun.xun.beans.DialogSet;
import com.xiaoxun.xun.views.CustomerPickerView;
import com.xiaoxun.xun.views.ShareDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/* 
 * 参数说明：
 *   left和 right参数分别对应左按钮和右按钮，letf_content、right_content配套使用
 * */
public class DialogUtil {
    public static Dialog CustomALertDialog(Context context, String title, Spanned content, final OnCustomDialogListener left, String left_content,
                                           final OnCustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }
        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        right_btn.setTextColor(context.getResources().getColor(R.color.warning_red));
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static void shareDialog(Context context, String url) {
        String firsh = url.substring(url.indexOf("//")+2);
        String info[]=firsh.split("&&");
        String image="undefined",sendUrl="undefined",title="undefined";
        for(int i = 0;i<info.length;i++){
            if(info[i].contains("title")){
                title = info[i];
            }else if(info[i].contains("url")){
                sendUrl = info[i];
            }else if(info[i].contains("image")){
                image = info[i];
            }
        }
        final String imagesUrl= image.substring(image.indexOf("=")+1);
        final String actionUrl= sendUrl.substring(sendUrl.indexOf("=")+1);
        final String titles= title.substring(title.indexOf("=")+1);

        String title_utf = "";
        try {
            title_utf = URLDecoder.decode(titles, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("cui","images = "+imagesUrl +" sendUrls = "+actionUrl + " titles = "+title_utf);
        ShareDialog sharedialog = new ShareDialog(context, title_utf, "", actionUrl,imagesUrl);

        Window win = sharedialog.getWindow();
        sharedialog.setCanceledOnTouchOutside(true);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        sharedialog.onWindowAttributesChanged(lp);
        Activity activity = (Activity)context;
        if(activity == null || activity.isFinishing() || activity.isDestroyed()) return ;
        sharedialog.show();
    }

    //右键为红色警示按钮
    public static Dialog CustomALertDialog(Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                           final OnCustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }
        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        right_btn.setTextColor(context.getResources().getColor(R.color.warning_red));
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomSelectorListDialog(final Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                                  final OnCustomDialogListener right, String right_content, final OnSelectorDialogLister selector) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dlg_selecter_call, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }
        RelativeLayout selector_tip = layout.findViewById(R.id.tip_selector);
        final ImageView selector_view = layout.findViewById(R.id.selector_view);
        selector_tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selector.onClick(v)) {
                    selector_view.setBackgroundResource(R.drawable.choice_1);
                } else {
                    selector_view.setBackgroundResource(R.drawable.choice_0);
                }
            }
        });
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomSelectorDialog(final Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                              final OnCustomDialogListener right, String right_content, final OnSelectorDialogLister selector) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_2, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }
        RelativeLayout selector_tip = layout.findViewById(R.id.tip_selector);
        final ImageView selector_view = layout.findViewById(R.id.selector_view);
        selector_tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selector.onClick(v)) {
                    selector_view.setBackgroundResource(R.drawable.choice_1);
                } else {
                    selector_view.setBackgroundResource(R.drawable.choice_0);
                }
            }
        });
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }
    public static Dialog CustomSupportTitleDialog(final Context context, String title, String content, final OnCustomDialogListener complete,
                                                  String complete_content, final OnSelectorDialogLister selector) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.steps_nosupport_dialog, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        final Button complete_btn;

        complete_btn = layout.findViewById(R.id.one_btn);
        complete_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                complete.onClick(v);
                dlg.dismiss();
            }
        });

        complete_btn.setText(complete_content);

        RelativeLayout selector_tip = layout.findViewById(R.id.tip_selector);
        final ImageView selector_view = layout.findViewById(R.id.selector_view);
        selector_tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selector.onClick(v)) {
                    selector_view.setBackgroundResource(R.drawable.choice_1);
                } else {
                    selector_view.setBackgroundResource(R.drawable.choice_0);
                }
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalSpanDialog(Context context, String title, Spanned content, final OnCustomDialogListener left, String left_content,
                                                final OnCustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (content != null) {
            content_view.setVisibility(View.VISIBLE);
            contet_view_mul.setVisibility(View.GONE);
            content_view.setText(content);
            content_view.setClickable(true);
            content_view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            content_view.setVisibility(View.GONE);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCancelable(false);
        dlg.setContentView(layout);
        return dlg;
    }
    public static Dialog CustomNormalDialogSpannable(Context context, String title, CharSequence content, final OnCustomDialogListener left, String left_content,
                                            final OnCustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        content_view.setClickable(true);
        content_view.setMovementMethod(LinkMovementMethod.getInstance());
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (content != null) {
            if (content.toString().contains("\n")) {
                content_view.setVisibility(View.GONE);
                contet_view_mul.setVisibility(View.VISIBLE);
                contet_view_mul.setText(content);
            } else {
                content_view.setVisibility(View.VISIBLE);
                contet_view_mul.setVisibility(View.GONE);
                content_view.setText(content);
            }
        } else {
            content_view.setVisibility(View.GONE);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        dlg.setCancelable(false);
        return dlg;
    }
    public static Dialog CustomNormalDialog(Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                            final OnCustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (content != null) {
            if (content.contains("\n")) {
                content_view.setVisibility(View.GONE);
                contet_view_mul.setVisibility(View.VISIBLE);
                contet_view_mul.setText(content);
            } else {
                content_view.setVisibility(View.VISIBLE);
                contet_view_mul.setVisibility(View.GONE);
                content_view.setText(content);
            }
        } else {
            content_view.setVisibility(View.GONE);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalDialog(final Context context, String title, String content, final OnCustomDialogListener onclick, String btn_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (!TextUtils.isEmpty(content) && content.contains("\n")) {
            content_view.setVisibility(View.GONE);
            contet_view_mul.setVisibility(View.VISIBLE);
            contet_view_mul.setText(content);
        } else {
            content_view.setVisibility(View.VISIBLE);
            contet_view_mul.setVisibility(View.GONE);
            content_view.setText(content);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.VISIBLE);

        one_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onclick.onClick(v);
                dlg.dismiss();
            }
        });

        one_btn.setText(btn_content);
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.GONE);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalSpannedDialog(final Context context, String title, Spanned content, final OnCustomDialogListener onclick, String btn_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (content.toString().contains("\n")) {
            content_view.setVisibility(View.GONE);
            contet_view_mul.setVisibility(View.VISIBLE);
            contet_view_mul.setText(content);
        } else {
            content_view.setVisibility(View.VISIBLE);
            contet_view_mul.setVisibility(View.GONE);
            content_view.setText(content);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.VISIBLE);

        one_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onclick.onClick(v);
                dlg.dismiss();
            }
        });

        one_btn.setText(btn_content);
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.GONE);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalDialog(Context context, String title, String content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }
        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.VISIBLE);

        one_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.GONE);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public interface OnCustomDialogListener {
        void onClick(View v);
    }

    public interface OnSelectorDialogLister {
        Boolean onClick(View v);
    }

    public interface OnCustomDialogSpanClickListenr {
        void ClickableSpan();
    }

    public static int ShowCustomSystemDialog(Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                             final OnCustomDialogListener right, String right_content) {
//       Dialog dlg = CustomNormalDialog(context, title, content, onclick, btn_content);
        int key = Long.valueOf(System.currentTimeMillis()).intValue();
        ImibabyApp myApp;
        myApp = (ImibabyApp) context.getApplicationContext();
        DialogSet set = new DialogSet();
        set.titile = title;
        set.desc = content;
        set.leftBtnStr = left_content;
        set.rightBtnStr = right_content;
        set.leftListener = left;
        set.rightListener = right;

        myApp.getSysDialogSets().put(key, set);
        Intent it = new Intent(context.getApplicationContext(), BlankActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("key", key);
        myApp.startActivity(it);
        return key;
    }

    public static int ShowCustomSingleTopSystemDialog(Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                             final OnCustomDialogListener right, String right_content) {
//       Dialog dlg = CustomNormalDialog(context, title, content, onclick, btn_content);
        int key = Long.valueOf(System.currentTimeMillis()).intValue();
        ImibabyApp myApp;
        myApp = (ImibabyApp) context.getApplicationContext();
        DialogSet set = new DialogSet();
        set.titile = title;
        set.desc = content;
        set.leftBtnStr = left_content;
        set.rightBtnStr = right_content;
        set.leftListener = left;
        set.rightListener = right;

        myApp.getSysDialogSets().put(key, set);
        Intent it = new Intent(context.getApplicationContext(), BlankActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra("key", key);
        myApp.startActivity(it);
        return key;
    }

    public static Dialog CustomSelectorRechargeDialog(final Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                                      final OnCustomDialogListener right, String right_content, final OnSelectorDialogLister selector, int[] loc, int color) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_3, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            SpannableString ss = new SpannableString(context.getString(R.string.str_recharge_tips_content1));
            ss.setSpan(new ForegroundColorSpan(color), loc[0], loc[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content_view.setText(ss);
        } else {
            content_view.setVisibility(View.GONE);
        }
        RelativeLayout selector_tip = layout.findViewById(R.id.tip_selector);
        final ImageView selector_view = layout.findViewById(R.id.selector_view);
        selector_tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selector.onClick(v)) {
                    selector_view.setBackgroundResource(R.drawable.choice_1);
                } else {
                    selector_view.setBackgroundResource(R.drawable.choice_0);
                }
            }
        });
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalDialogWithSpan(final Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                                    final OnCustomDialogListener right, String right_content, int[] spanloc, final OnCustomDialogSpanClickListenr spanclick,
                                                    boolean isShowSpan) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);

        content_view.setVisibility(View.VISIBLE);
        contet_view_mul.setVisibility(View.GONE);
        content_view.setText(content);
        if(isShowSpan) {
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    spanclick.ClickableSpan();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.bg_color_orange));
                }
            };
            builder.setSpan(clickableSpan, spanloc[0], spanloc[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content_view.setText(builder);
            content_view.setClickable(true);
            content_view.setMovementMethod(LinkMovementMethod.getInstance());
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static Dialog CustomNormalDialogWithSpanColor(Context context, String title, String content, final OnCustomDialogListener left, String left_content,
                                                         final OnCustomDialogListener right, String right_content, int[] loc, int color) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        TextView contet_view_mul = layout.findViewById(R.id.content_mul);
        if (content != null) {
            SpannableString ss = new SpannableString(content);
            ss.setSpan(new ForegroundColorSpan(color), loc[0], loc[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (content.contains("\n")) {
                content_view.setVisibility(View.GONE);
                contet_view_mul.setVisibility(View.VISIBLE);
                contet_view_mul.setText(ss);
            } else {
                content_view.setVisibility(View.VISIBLE);
                contet_view_mul.setVisibility(View.GONE);
                content_view.setText(ss);
            }
        } else {
            content_view.setVisibility(View.GONE);
        }

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.GONE);

        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.VISIBLE);
        Button left_btn;
        Button right_btn;

        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v);
                dlg.dismiss();
            }
        });

        left_btn.setText(left_content);
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v);
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static int ShowCustomSpanSystemDialog(Context context, String title, Spanned content, final OnCustomDialogListener left, String left_content,
                                                 final OnCustomDialogListener right, String right_content) {
//       Dialog dlg = CustomNormalDialog(context, title, content, onclick, btn_content);
        int key = Long.valueOf(System.currentTimeMillis()).intValue();
        ImibabyApp myApp;
        myApp = (ImibabyApp) context.getApplicationContext();
        DialogSet set = new DialogSet();
        set.titile = title;
        set.description = content;
        set.leftBtnStr = left_content;
        set.rightBtnStr = right_content;
        set.leftListener = left;
        set.rightListener = right;

        myApp.getSysDialogSets().put(key, set);
        Intent it = new Intent(context.getApplicationContext(), BlankActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("key", key);
        myApp.startActivity(it);
        return key;
    }


    public static Dialog CustomUserKnowledgeDialog(final Context context, String title, String content, final OnCustomDialogListener onclick) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_1, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView content_view = layout.findViewById(R.id.content);
        content_view.setText(content);

        Button one_btn;
        one_btn = layout.findViewById(R.id.one_btn);
        one_btn.setVisibility(View.VISIBLE);

        one_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onclick.onClick(v);
                dlg.dismiss();
            }
        });

        //one_btn.setText(btn_content);
        View button_layout_2 = layout.findViewById(R.id.two_btns);
        button_layout_2.setVisibility(View.GONE);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        return dlg;
    }

    public static String height;
    public static void openHeightEditDialog(Context ctxt, final OnClickListener listener, String watchHeight) {
        height = watchHeight;
        final Dialog dlg = new Dialog(ctxt, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_height, null);
        CustomerPickerView pickHeight;
        pickHeight = layout.findViewById(R.id.height_pv);
        pickHeight.setMarginAlphaValue((float) 3.8, "H");
        int width = Params.getInstance(ctxt).getScreenWidthInt();
        TextView tvCm = layout.findViewById(R.id.tv_height_pv);
        tvCm.setPadding(width * 5 / 10 + 40 * width / 1080, 0, 0, 0);
        tvCm.setTextColor(0xffdf5600);

        Button left_btn;
        Button right_btn;
        left_btn = (Button) layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        right_btn = (Button) layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
                listener.onClick(view);
            }
        });

        // select height
        List<String> heights = new ArrayList<String>();
        for (int i = 60; i < 180; i++) {
            heights.add(i < 60 ? "0" + i : "" + i);
        }
        pickHeight.setData(heights);
        pickHeight.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                height = text;
            }
        });
        pickHeight.setSelected(Double.valueOf(height).intValue() - 60);

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        dlg.show();
    }

    public static String weight;
    public static void openWeightSelDialog(Context ctxt, final OnClickListener listener, String watchWeight) {
        weight = watchWeight;
        final Dialog dlg = new Dialog(ctxt, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_weight, null);
        CustomerPickerView pickWeight;
        pickWeight = layout.findViewById(R.id.weight_pv);
        pickWeight.setMarginAlphaValue((float) 3.8, "H");
        int width = Params.getInstance(ctxt).getScreenWidthInt();
        TextView tvKg = layout.findViewById(R.id.tv_weight_pv);
        tvKg.setPadding(width * 5 / 10 + 30 * width / 1080, 0, 0, 0);
        tvKg.setTextColor(0xffdf5600);

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
                listener.onClick(view);
            }
        });

        List<String> weights = new ArrayList<String>();
        for (int i = 8; i < 80; i++) {
            weights.add(i < 8 ? "0" + i : "" + i);
        }
        pickWeight.setData(weights);
        pickWeight.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                weight = text;
            }
        });
        pickWeight.setSelected(Double.valueOf(weight).intValue() - 8);

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(layout);
        dlg.show();
    }
}
