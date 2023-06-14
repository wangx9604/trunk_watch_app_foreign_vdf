package com.xiaoxun.xun.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.XiaoMiDialogSelectAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSelectDialogUtil {
    private static EditText inputText;
    private static ImageView delete_keyword;
    public static Dialog CustomInputDialog(Context context, String title, final CustomDialogListener left, String left_content,
                                           final CustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        inputText = layout.findViewById(R.id.input);

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //left.onClick(v);
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v, inputText.getText().toString());
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

    public static Dialog CustomInputDialogWithParams(Context context, int maxlen, int inputType, String title, String text, String hite, final CustomDialogListener left, String left_content,
                                                     final CustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        inputText = layout.findViewById(R.id.input);
        if (inputType != 0)
            inputText.setInputType(inputType);

        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlen)});


        if (text != null) {
            String setText;
            if (text.length() > maxlen) {
                setText = text.substring(0, maxlen);
            } else {
                setText = text;
            }
            inputText.setText(setText);
            inputText.setSelection(setText.length());
        }
        if (hite != null) {
            inputText.setHint(hite);
        }
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (left != null) {
                    left.onClick(v, inputText.getText().toString());
                }
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v, inputText.getText().toString());
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

    private static boolean isShowPwd=false;
    public static Dialog CustomInputPwdDialog(Context context, int maxlen, int inputType, String title, String text, String hite, final CustomDialogListener left, String left_content,
                                                     final CustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input_pwd, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        inputText = layout.findViewById(R.id.input);
        if (inputType != 0)
            inputText.setInputType(inputType);

        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlen)});

        if (text != null) {
            String setText;
            if (text.length() > maxlen) {
                setText = text.substring(0, maxlen);
            } else {
                setText = text;
            }
            inputText.setText(setText);
            inputText.setSelection(setText.length());
        }
        if (hite != null) {
            inputText.setHint(hite);
        }
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (left != null) {
                    left.onClick(v, inputText.getText().toString());
                }
                isShowPwd = false;
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                right.onClick(v, inputText.getText().toString());
                isShowPwd = false;
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        final ImageView pwd_btn;
        pwd_btn= layout.findViewById(R.id.btn_pwd_visible);
        pwd_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowPwd = !isShowPwd;
                if (isShowPwd) {
                    pwd_btn.setImageResource(R.drawable.pwd_visiable);
                    inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pwd_btn.setImageResource(R.drawable.btn_pwd_unvisiable_selector);
                    inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                //切换后将EditText光标置于末尾
                CharSequence charSequence = inputText.getText();
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

    public static Dialog CustomItemSelectDialog(Context context, ArrayList<String> stringList,
                                                final AdapterItemClickListener itemClickListener, int defaultSelect) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_item_select, null);

        //ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            //HashMap<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", R.drawable.day_select);
            map.put("info", stringList.get(i));
            listItem.add(map);
        }

        XiaoMiDialogSelectAdapter adapter = new XiaoMiDialogSelectAdapter(context, listItem, R.layout.xiaomi_dialog_item_select_adapte,
                new String[]{"img", "info"},
                new int[]{R.id.iv_selectimg1, R.id.iv_selecttext1}, defaultSelect - 1);

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                itemClickListener.onClick(view, position + 1);
                dlg.dismiss();
            }
        });
        //adapter.notifyDataSetChanged();

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

    public static Dialog CustomItemSelectDialogWithNoIndicator(Context context, ArrayList<String> stringList,
                                                               final AdapterItemClickListener itemClickListener) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_item_select, null);
        List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("info", stringList.get(i));
            listItem.add(map);
        }

        XiaoMiDialogSelectAdapter adapter = new XiaoMiDialogSelectAdapter(context, listItem, R.layout.xiaomi_dialog_item_select_adapte,
                new String[]{"info"},
                new int[]{R.id.iv_selecttext1}, -1);

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClickListener.onClick(view, position + 1);
                dlg.dismiss();
            }
        });
        //adapter.notifyDataSetChanged();

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

    public static Dialog CustomItemSelectDialogWithTitle(Context context, String title, ArrayList<String> stringList,
                                                         final AdapterItemClickListener itemClickListener, int defaultSelect, final CustomDialogListener left, String left_content) {
        return CustomItemSelectDialogWithTitle(context, title, null, stringList, itemClickListener, defaultSelect, left, left_content);
    }

    public static Dialog CustomItemSelectDialogWithTitle(Context context, String title, String funcDesc, ArrayList<String> stringList,
                                                         final AdapterItemClickListener itemClickListener, int defaultSelect, final CustomDialogListener left, String left_content) {
        return CustomItemSelectDialogWithTitle(context, title, funcDesc, stringList, itemClickListener, defaultSelect, left, left_content, null, null, false);
    }

    /**
     * @param isNeedTwoConfirm 是否需要二次确认。如果不需要，底部一个“取消”按钮，点击item后即选择该选项；否则，底部两个按钮，选中item后，点击确定才会选择该选项。
     */
    public static Dialog CustomItemSelectDialogWithTitle(Context context, String title, String funcDesc, ArrayList<String> stringList,
                                                         final AdapterItemClickListener itemClickListener, final int defaultSelect, final CustomDialogListener left, String left_content,
                                                         final CustomDialogListener right, String right_content, final boolean isNeedTwoConfirm) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_item_select_with_title, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView func_desc = layout.findViewById(R.id.func_desc);
        if (funcDesc != null) {
            func_desc.setVisibility(View.VISIBLE);
            func_desc.setText(funcDesc);
        } else {
            func_desc.setVisibility(View.GONE);
        }

        //ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            //HashMap<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", R.drawable.day_select);
            map.put("info", stringList.get(i));
            if (i == (defaultSelect - 1))
                map.put("select", true);
            else
                map.put("select", false);
            listItem.add(map);
        }

        final XiaoMiDialogSelectAdapter adapter = new XiaoMiDialogSelectAdapter(context, listItem, R.layout.xiaomi_dialog_item_select_adapte,
                new String[]{"img", "info", "select"},
                new int[]{R.id.iv_selectimg1, R.id.iv_selecttext1}, -1);

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listItem.size(); i++) {
                    Map<String, Object> item = listItem.get(i);
                    if (i == position) {
                        item.put("select", true);
                    } else {
                        item.put("select", false);
                    }
                }
                adapter.notifyDataSetChanged();
                itemClickListener.onClick(view, position + 1);
                if (!isNeedTwoConfirm)
                    dlg.dismiss();
            }
        });

        View layoutOneBtn=layout.findViewById(R.id.layout_one_btn);
        View layoutTwoBtn=layout.findViewById(R.id.layout_two_btns);
        if (isNeedTwoConfirm) {
            layoutOneBtn.setVisibility(View.GONE);
            layoutTwoBtn.setVisibility(View.VISIBLE);
        } else {
            layoutOneBtn.setVisibility(View.VISIBLE);
            layoutTwoBtn.setVisibility(View.GONE);
        }

        Button left_btn;
        if (isNeedTwoConfirm) {
            left_btn = layout.findViewById(R.id.btn_left);
        } else {
            left_btn = layout.findViewById(R.id.btn_cancel);
        }
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                left.onClick(v, null);
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        Button right_btn;
        right_btn = layout.findViewById(R.id.btn_right);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                right.onClick(v, null);
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

    public static Dialog CustomItemMultSelectDialog(Context context, ArrayList<String> stringList, String title,
                                                    final CustomDialogListener left, String left_content,
                                                    final CustomDialogListener right, String right_content, String custom) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_mult_select, null);

        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("info", stringList.get(i));
            if (custom.substring(i, i + 1).equals("0")) {
                map.put("img", R.drawable.select_2);
                map.put("flag", "0");
            } else {
                map.put("img", R.drawable.select_0);
                map.put("flag", "1");
            }

            listItem.add(map);
        }

        final SimpleAdapter adapter = new SimpleAdapter(context, listItem, R.layout.xiaomi_dialog_mult_select_adapte,
                new String[]{"info", "img"},
                new int[]{R.id.iv_selecttext1, R.id.iv_selectimg1});

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flag = (String) listItem.get(position).get("flag");
                if (flag.equals("0")) {
                    listItem.get(position).put("flag", "1");
                    listItem.get(position).put("img", R.drawable.select_0);
                } else if (flag.equals("1")) {
                    listItem.get(position).put("flag", "0");
                    listItem.get(position).put("img", R.drawable.select_2);
                }
                adapter.notifyDataSetChanged();
                // TODO Auto-generated method stub
            }
        });

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //left.onClick(v);
                left.onClick(v, "");
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String select = "";
                for (int i = 0; i < listItem.size(); i++) {
                    String flag = (String) listItem.get(i).get("flag");
                    if (flag.equals("0")) {
                        select = select + "0";
                    } else if (flag.equals("1")) {
                        select = select + "1";
                    }
                    if (i != listItem.size() - 1) {
                        select = select + ",";
                    }
                }
                right.onClick(v, select);
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


    public interface CustomDialogListener {
        void onClick(View v, String text);
    }

    public interface AdapterItemClickListener {
        void onClick(View v, int position);
    }


    public static Dialog CustomItemMultSelectDialogSilence(Context context, ArrayList<String> stringList, String title, String days,
                                                           final CustomDialogListener left, String left_content,
                                                           final CustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_mult_select, null);

        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("info", stringList.get(i));
            if (days != null && days.length() == 7 && days.substring(i, i + 1).equals("1")) {
                map.put("img", R.drawable.select_0);
                map.put("flag", "1");
            } else {
                map.put("img", R.drawable.select_2);
                map.put("flag", "0");
            }
            listItem.add(map);
        }

        final SimpleAdapter adapter = new SimpleAdapter(context, listItem, R.layout.xiaomi_dialog_mult_select_adapte,
                new String[]{"info", "img"},
                new int[]{R.id.iv_selecttext1, R.id.iv_selectimg1});

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flag = (String) listItem.get(position).get("flag");
                if (flag.equals("0")) {
                    listItem.get(position).put("flag", "1");
                    listItem.get(position).put("img", R.drawable.select_0);
                } else if (flag.equals("1")) {
                    listItem.get(position).put("flag", "0");
                    listItem.get(position).put("img", R.drawable.select_2);
                }
                adapter.notifyDataSetChanged();
                // TODO Auto-generated method stub
            }
        });

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String select = "";
                for (int i = 0; i < listItem.size(); i++) {
                    String flag = (String) listItem.get(i).get("flag");
                    if (flag.equals("0")) {
                        select = select + "0";
                    } else if (flag.equals("1")) {
                        select = select + "1";
                    }
                    if (i != listItem.size() - 1) {
                        select = select + ",";
                    }
                }
                left.onClick(v, select);
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String select = "";
                for (int i = 0; i < listItem.size(); i++) {
                    String flag = (String) listItem.get(i).get("flag");
                    if (flag.equals("0")) {
                        select = select + "0";
                    } else if (flag.equals("1")) {
                        select = select + "1";
                    }
                    if (i != listItem.size() - 1) {
                        select = select + ",";
                    }
                }
                right.onClick(v, select);
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
    //trakc

    /**
     * 类名称：CustomSelectDialogUtil
     * 创建人：zhangjun5
     * 创建时间：2016/3/29 13:34
     * 方法描述：个人设备页中的跟踪模式的选择对话框
     */
    public static Dialog CustomItemMultSelectDialogSilence(Context context, ArrayList<String> stringList, String title,
                                                           boolean isPrompt, String prompt,
                                                           boolean isWarn, String warining, String days,
                                                           final CustomDialogListener left, String left_content,
                                                           final CustomDialogListener right, String right_content) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_mult_select, null);

        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);
        TextView prompt_view = layout.findViewById(R.id.prompt_content);
        TextView warining_info = layout.findViewById(R.id.warning_info);
        if (isPrompt) {
            prompt_view.setVisibility(View.VISIBLE);
            prompt_view.setText(prompt);
        } else {
            prompt_view.setVisibility(View.GONE);
        }
        if (isWarn) {
            warining_info.setVisibility(View.VISIBLE);
            warining_info.setText(warining);
        } else {
            warining_info.setVisibility(View.GONE);
        }

        final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();//
        for (int i = 0; i < stringList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("info", stringList.get(i));
            if (days != null && days.length() == stringList.size() && days.substring(i, i + 1).equals("1")) {
                map.put("img", R.drawable.select_0);
                map.put("flag", "1");
            } else {
                map.put("img", R.drawable.select_2);
                map.put("flag", "0");
            }
            listItem.add(map);
        }

        final SimpleAdapter adapter = new SimpleAdapter(context, listItem, R.layout.xiaomi_dialog_mult_select_adapte,
                new String[]{"info", "img"},
                new int[]{R.id.iv_selecttext1, R.id.iv_selectimg1});

        ListView itemListView = (layout.findViewById(R.id.item_select_list));
        itemListView.setClickable(true);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < listItem.size(); i++) {
                    listItem.get(i).put("flag", "0");
                    listItem.get(i).put("img", R.drawable.select_2);
                }
                String flag = (String) listItem.get(position).get("flag");
                if (flag.equals("0")) {
                    listItem.get(position).put("flag", "1");
                    listItem.get(position).put("img", R.drawable.select_0);
                }
                adapter.notifyDataSetChanged();
                // TODO Auto-generated method stub
            }
        });

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String select = "";
                for (int i = 0; i < listItem.size(); i++) {
                    String flag = (String) listItem.get(i).get("flag");
                    if (flag.equals("0")) {
                        select = select + "0";
                    } else if (flag.equals("1")) {
                        select = select + "1";
                    }
                }
                left.onClick(v, select);
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String select = "";
                for (int i = 0; i < listItem.size(); i++) {
                    String flag = (String) listItem.get(i).get("flag");
                    if (flag.equals("0")) {
                        select = select + "0";
                    } else if (flag.equals("1")) {
                        select = select + "1";
                    }
                }
                right.onClick(v, select);
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

    public static Dialog CustomInputDialogWithSelect(Context context, String title, Spanned content, final CustomDialogListener textview,
                                                     String text, final CustomDialogListener left, String left_content,
                                                     final CustomDialogListener right, String right_content, Drawable selectIcon, final CustomDialogListener select) {
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input_select, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        TextView content_view = layout.findViewById(R.id.content);
        if (content != null) {
            content_view.setText(content);
        } else {
            content_view.setVisibility(View.GONE);
        }

        final TextView input = layout.findViewById(R.id.input);
        input.setText(text);
        input.setClickable(true);
        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textview.onClick(v, input.getText().toString());
                dlg.dismiss();
            }
        });

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v, input.getText().toString());
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content);

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v, input.getText().toString());
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        ImageView selectView = layout.findViewById(R.id.select);
        selectView.setImageDrawable(selectIcon);
        selectView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                select.onClick(v, null);
                dlg.dismiss();
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        if (text == null || text.length() == 0) {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
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

    public static Dialog CustomInputDialogWithSelect(Context context, int maxlen, int inputType, String title, Spanned content,
                                                     String text, String hint, final CustomDialogListener left, String left_content,
                                                     final CustomDialogListener right, String right_content, Drawable selectIcon, final CustomDialogListener select){
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input_select_et, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        TextView content_view = layout.findViewById(R.id.content);
        if(content != null){
            content_view.setText(content);
        }else{
            content_view.setVisibility(View.GONE);
        }
        delete_keyword = layout.findViewById(R.id.delete_keyword);
        inputText = layout.findViewById(R.id.input);
        if (inputType!=0)
            inputText.setInputType(inputType);

        inputText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxlen) });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    delete_keyword.setVisibility(View.VISIBLE);
                } else {
                    delete_keyword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (text!=null){
            //需要做截断保护
            if(text.length()>maxlen){
                text = text.substring(0,maxlen);
            }
            inputText.setText(text);
            inputText.setSelection(text.length());
        }
        if (hint!=null){
            inputText.setHint(hint);
        }
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v, inputText.getText().toString());
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content) ;

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v , inputText.getText().toString());
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        ImageView selectView = layout.findViewById(R.id.select);
        selectView.setImageDrawable(selectIcon);
        selectView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                select.onClick(v, null);
                dlg.dismiss();
            }
        });


        delete_keyword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.setText("");
                delete_keyword.setVisibility(View.GONE);
            }
        });


        // set a large value put it in bottom
        Window w = dlg.getWindow();
        if (text == null || text.length() == 0) {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
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

    public static Dialog CustomInputDialogWithNotice(Context context, int maxlen, int inputType, String title, Spanned content,
                                                     String text, String hint, final CustomDialogListener left, String left_content,
                                                     final CustomDialogListener right, String right_content){
        final Dialog dlg = new Dialog(context, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_input_notice, null);
        TextView title_view = layout.findViewById(R.id.title);
        title_view.setText(title);

        TextView content_view = layout.findViewById(R.id.content);
        if(content != null){
            content_view.setText(content);
        }else{
            content_view.setVisibility(View.GONE);
        }
        delete_keyword = layout.findViewById(R.id.delete_keyword);
        inputText = layout.findViewById(R.id.input);
        if (inputType!=0)
            inputText.setInputType(inputType);

        inputText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxlen) });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    delete_keyword.setVisibility(View.VISIBLE);
                } else {
                    delete_keyword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (text!=null){
            //需要做截断保护
            if(text.length()>maxlen){
                text = text.substring(0,maxlen);
            }
            inputText.setText(text);
            inputText.setSelection(text.length());
        }
        if (hint!=null){
            inputText.setHint(hint);
        }
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                left.onClick(v, inputText.getText().toString());
                dlg.dismiss();
            }
        });
        left_btn.setText(left_content) ;

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                right.onClick(v , inputText.getText().toString());
                dlg.dismiss();
            }
        });
        right_btn.setText(right_content);

        delete_keyword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.setText("");
                delete_keyword.setVisibility(View.GONE);
            }
        });


        // set a large value put it in bottom
        Window w = dlg.getWindow();
        if (text == null || text.length() == 0) {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
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

}
