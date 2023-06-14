package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.BitmapUtilities;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.PhotoGetUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;
import com.yalantis.ucrop.UCrop;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by guxiaolong on 2016/8/22.
 */

public class WatchDetailFirstSetActivity extends NormalActivity implements View.OnClickListener, MsgCallback, LoadingDialog.OnConfirmClickListener {

    String gid;
    String eid;
    WatchData watch;
    //head edit
    private ImageView iv_head;
    //name edit
    private TextView tvNickname;
    private View lineNick;
    private ImageView ivEditNick;
    //birthday edit
    private TextView tv_birth;
    private View lineBirth;
    private CustomerPickerView pickYear;
    private CustomerPickerView pickMonth;
    private CustomerPickerView pickDay;
    private TextView tvYear;
    private TextView tvMonth;
    private TextView tvDay;

    private String year = "2012";
    private String month = "12";
    private String day = "12";

    //height edit
    private View lineHeight;
    private TextView tvHeight;

    //weight edit
    private View lineWeight;
    private TextView tvWeight;

    private ImageButton btn_back;

    private Button btn_next;

    LayoutInflater mLayoutInflater;
    private File cameraTemp = null;
    private File cropTemp = null;
    private int headE2cSn;

    private LoadingDialog loadingdlg;
    private BroadcastReceiver mMsgReceiver = null;
    private boolean editFlag = false;

    private Uri photoUri;
    private File mSaveTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_detail_first_set);

        eid = getIntent().getStringExtra(CloudBridgeUtil.KEY_NAME_EID);
        if (eid != null && eid.length() > 0) {
            myApp.setFocusWatch(myApp.getCurUser().queryWatchDataByEid(eid));
        }
        iv_head = findViewById(R.id.iv_head);
        iv_head.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendDeviceGet(eid);
            }
        }, 500);
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, this);
        loadingdlg.hideReloadView();
    }

    private void sendDeviceGet(String eid) {

        MyMsgData msg = new MyMsgData();
        msg.setCallback(WatchDetailFirstSetActivity.this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        msg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_GET, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(msg);
    }

    void initReceivers() {
        mMsgReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if (action.equals(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW)) {
                    try {
                        //管理员不可以解绑
                        initViews();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();

        filter.addAction(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW);
        registerReceiver(mMsgReceiver, filter);
    }


    private void copyWatchInfo(WatchData dst, WatchData src) {
        dst.setNickname(src.getNickname());
        dst.setSex(src.getSex());
        dst.setBirthday(src.getBirthday());
        dst.setHeight(src.getHeight());
        dst.setWeight(src.getWeight());
        dst.setHeadPath(src.getHeadPath());
        dst.setEid(src.getEid());
        dst.setFamilyId(src.getFamilyId());
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
    }

    private void initViews() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.setting_more));
        mLayoutInflater = (LayoutInflater) myApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        copyWatchInfo(watch, watch);
        if (watch.getHeight() < 60) {
            watch.setHeight(110.0);
        }
        if (watch.getWeight() < 8) {
            watch.setWeight(18.0);
        }
        if (watch.getBirthday() != null) {
            String birthday = watch.getBirthday();
            year = birthday.substring(0, 4);
            month = birthday.substring(4, 6);
            day = birthday.substring(6, 8);
        }

        btn_back = findViewById(R.id.iv_title_back);
        btn_back.setOnClickListener(this);
        btn_back.setVisibility(View.GONE);

        iv_head = findViewById(R.id.iv_head);
        iv_head.setOnClickListener(this);

        tvNickname = findViewById(R.id.tv_username);
        tvNickname.setOnClickListener(this);
        lineNick = findViewById(R.id.line_username);
        lineNick.setOnClickListener(this);
        ivEditNick = findViewById(R.id.iv_username);
        ivEditNick.setOnClickListener(this);

        lineBirth = findViewById(R.id.line_birthday);
        lineBirth.setOnClickListener(this);
        tv_birth = findViewById(R.id.tv_birthday);


        lineHeight = findViewById(R.id.line_height);
        lineHeight.setOnClickListener(this);
        tvHeight = findViewById(R.id.tv_height);

        lineWeight = findViewById(R.id.line_weight);
        lineWeight.setOnClickListener(this);
        tvWeight = findViewById(R.id.tv_weight);

        //要根据显示转换一下字符串方式
        refreshBirthTxt();
        tvNickname.setText(watch.getNickname());
        tvHeight.setText(String.format("%d%s", Double.valueOf(watch.getHeight()).intValue(), getText(R.string.str_cm)));
        tvWeight.setText(String.format("%d%s", Double.valueOf(watch.getWeight()).intValue(), getText(R.string.str_kg)));

        ImageUtil.setMaskImage(iv_head, R.drawable.head_2, getMyApp().getHeadDrawableByFile(getResources(), watch.getHeadPath(), watch.getEid(), R.drawable.default_head));

        btn_next = findViewById(R.id.btn_next_step);
        btn_next.setOnClickListener(this);
    }

    private void refreshBirthTxt() {
        if (watch.getBirthday() != null && watch.getBirthday().length() > 0) {
            StringBuilder buff = new StringBuilder();
            buff.append(year);
            buff.append("-");
            buff.append(month);
            buff.append("-");
            buff.append(day);

            tv_birth.setText(buff.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        watch = myApp.getCurUser().getFocusWatch();
        //兼容一下老的手表nickname过长问题
        if (watch.getNickname() != null && watch.getNickname().length() > 8) {
            watch.setNickname(watch.getNickname().substring(0, 8));
        }
        gid = watch.getFamilyId();
        eid = watch.getEid();

        initViews();
        initReceivers();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        myApp.getCurUser().updateDeviceInfo(watch);
        clearReceivers();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        myApp.getCurUser().updateDeviceInfo(watch);
        clearReceivers();

        if (pickYear != null) pickYear.release();
        if (pickMonth != null) pickMonth.release();
        if (pickDay != null) pickDay.release();
    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mMsgReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == iv_head) {

            ArrayList<String> itemList = new ArrayList<String>();
            itemList.add(getText(R.string.head_edit_camera).toString());
            itemList.add(getText(R.string.head_edit_pics).toString());
            Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(WatchDetailFirstSetActivity.this, getText(R.string.edit_head).toString(), itemList,
                    new CustomSelectDialogUtil.AdapterItemClickListener() {
                        @Override
                        public void onClick(View v, int position) {
                            // TODO Auto-generated method stub
                            if (position == 1) {
                                if (ActivityCompat.checkSelfPermission(WatchDetailFirstSetActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                                    mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                                    photoUri = PhotoGetUtil.startCameraCapture(myApp, WatchDetailFirstSetActivity.this, mSaveTemp);
                                } else {
                                    ActivityCompat.requestPermissions(WatchDetailFirstSetActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                                }
                            } else {
                                PhotoGetUtil.startPickHead(WatchDetailFirstSetActivity.this);
                            }
                        }
                    },
                    -1, new CustomSelectDialogUtil.CustomDialogListener() {

                        @Override
                        public void onClick(View v, String text) {
                            // TODO Auto-generated method stub

                        }
                    }, getText(R.string.cancel).toString());
            dlg.show();
        } else if (v == tvNickname
                || v == ivEditNick
                || v == lineNick) {
            Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(WatchDetailFirstSetActivity.this, Const.NICKNAME_MAXLEN,
                    0,
                    getText(R.string.edit_nickname).toString(),
                    watch.getNickname(), null, new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            // TODO Auto-generated method stub
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            // TODO Auto-generated method stub
                            String nickname = text;
                            if (nickname.length() < 1) {
                                ToastUtil.showMyToast(WatchDetailFirstSetActivity.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
                            } else {
                                watch.setNickname(nickname);
                                ((TextView) findViewById(R.id.tv_title)).setText(watch.getNickname());
                                tvNickname.setText(watch.getNickname());
                                sendDeviceSet(CloudBridgeUtil.KEY_NAME_NICKNAME, watch.getNickname());
                            }
                        }
                    },
                    getText(R.string.confirm).toString());
            dlg.show();
        } else if (v == lineBirth) {
            openBirthSelDialog();
        } else if (v == lineHeight) {
            openHeightSelDialog();
        } else if (v == lineWeight) {
            openWeightSelDialog();
        } else if (v == btn_back) {
            //finish();
        } else if (v == btn_next) {
            String nickname = tvNickname.getText().toString();
            if (nickname.length()<1){
                ToastUtil.showMyToast(WatchDetailFirstSetActivity.this, getText(R.string.wrong_nickname).toString(),  Toast.LENGTH_SHORT);
                return;
            }
//            Intent intent2 = new Intent(WatchDetailFirstSetActivity.this, FirstSetActivity.class);
            String memberEid = getMyApp().getCurUser().getEid();
            if (memberEid == null) {
                memberEid = myApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                if (memberEid != null)
                    myApp.getCurUser().setEid(memberEid);
            }
            Intent intent2 = new Intent(WatchDetailFirstSetActivity.this, AddCallMemberActivity.class);
            intent2.putExtra(Const.KEY_WATCH_ID, eid);
            intent2.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 0);
            intent2.putExtra("eid", memberEid);
            intent2.putExtra(Const.SET_CONTACT_ISBIND, true);
            startActivity(intent2);
            finish();
        }

    }

    private void openBirthSelDialog() {
        final Dialog dlg = new Dialog(WatchDetailFirstSetActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) WatchDetailFirstSetActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_birth, null);

        //init time pickviews
        pickYear = layout.findViewById(R.id.year_pv);
        pickYear.setMarginAlphaValue((float) 3.8, "H");

        pickMonth = layout.findViewById(R.id.month_pv);
        pickMonth.setMarginAlphaValue((float) 3.8, "H");

        pickDay = layout.findViewById(R.id.day_pv);
        pickDay.setMarginAlphaValue((float) 3.8, "H");

        View pickerView = layout.findViewById(R.id.birthday_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();

        View line_1 = layout.findViewById(R.id.line_1);
        View line_2 = layout.findViewById(R.id.line_2);
        line_1.setTranslationX(pickerView.getX() + width * 4 / 10);
        line_2.setTranslationX(pickerView.getX() + width * 7 / 10);
        tvYear = layout.findViewById(R.id.tv_year_pv);

        tvYear.setPadding(width * 2 / 10 + 55 * width / 1080, 0, 0, 0);
        tvYear.setTextColor(0xffdf5600);
        tvMonth = layout.findViewById(R.id.tv_month_pv);

        tvMonth.setPadding(width * 11 / 20 + 30 * width / 1080, 0, 0, 0);
        tvMonth.setTextColor(0xffdf5600);
        tvDay = layout.findViewById(R.id.tv_day_pv);

        tvDay.setPadding(width * 17 / 20 + 30 * width / 1080, 0, 0, 0);
        tvDay.setTextColor(0xffdf5600);

        List<String> years = new ArrayList<String>();
        List<String> months = new ArrayList<String>();
        List<String> days = new ArrayList<String>();

        Calendar c = Calendar.getInstance();
        int year1 = c.get(Calendar.YEAR);

        for (int i = 2000; i < year1 + 1; i++) {
            years.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 1; i < 13; i++) {
            months.add(i < 10 ? "0" + i : "" + i);
        }

        for (int i = 1; i < 32; i++) {
            days.add(i < 10 ? "0" + i : "" + i);
        }

        pickYear.setData(years);
        pickYear.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                year = text;
                int maxDay = getMaxday(year, month);
                List<String> qdays = new ArrayList<String>();
                for (int i = 1; i < maxDay + 1; i++) {
                    qdays.add(i < 10 ? "0" + i : "" + i);
                }
                pickDay.setData(qdays);
                if (Integer.valueOf(day) > maxDay) {
                    pickDay.setSelected(maxDay - 1);
                    day = Integer.valueOf(maxDay).toString();
                } else {
                    pickDay.setSelected(Integer.valueOf(day) - 1);
                }
                refreshBirthTxt();
            }
        });
        pickYear.setSelected(Integer.valueOf(year) - 2000);

        pickMonth.setData(months);
        pickMonth.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month = text;
                int maxDay = getMaxday(year, month);
                List<String> qdays = new ArrayList<String>();
                for (int i = 1; i < maxDay + 1; i++) {
                    qdays.add(i < 10 ? "0" + i : "" + i);
                }
                pickDay.setData(qdays);
                if (Integer.valueOf(day) > maxDay) {
                    pickDay.setSelected(maxDay - 1);
                    day = Integer.valueOf(maxDay).toString();
                } else {
                    pickDay.setSelected(Integer.valueOf(day) - 1);
                }
                refreshBirthTxt();
            }
        });
        pickMonth.setSelected(Integer.valueOf(month) - 1);

        int maxDay = getMaxday(year, month);
        List<String> qdays = new ArrayList<String>();
        for (int i = 1; i < maxDay + 1; i++) {
            qdays.add(i < 10 ? "0" + i : "" + i);
        }
        pickDay.setData(qdays);
        pickDay.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                day = text;
                refreshBirthTxt();
            }
        });
        pickDay.setSelected(Integer.valueOf(day) - 1);
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (watch.getBirthday() != null) {
                    String birthday = watch.getBirthday();
                    year = birthday.substring(0, 4);
                    month = birthday.substring(4, 6);
                    day = birthday.substring(6, 8);
                }
                refreshBirthTxt();
                dlg.dismiss();
            }
        });

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = TimeUtil.getTimeStampLocal();
                now = now.substring(0, 8);
                if (now.compareTo(year + month + day) >= 0 && checkDayValid(year, month, day)) {
                    watch.setBirthday(year + month + day);
                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH, watch.getBirthday());
                } else {
                    ToastUtil.showMyToast(WatchDetailFirstSetActivity.this, getResources().getString(R.string.date_error), Toast.LENGTH_SHORT);
                    String birthday = watch.getBirthday();
                    year = birthday.substring(0, 4);
                    month = birthday.substring(4, 6);
                    day = birthday.substring(6, 8);
                    refreshBirthTxt();
                }
                dlg.dismiss();
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
        dlg.show();
    }

    private int getMaxday(String year, String month) {
        {
            int y = Integer.valueOf(year);
            int m = Integer.valueOf(month);

            int dayMax = 31;

            if (m == 2) {
                if (y % 4 == 0) {
                    dayMax = 29;
                } else {
                    dayMax = 28;
                }
            } else if (m == 4 || m == 6 || m == 9 || m == 11) {
                dayMax = 30;
            }
            return dayMax;
        }
    }

    private boolean checkDayValid(String year, String month, String day) {
        int y = Integer.valueOf(year);
        int m = Integer.valueOf(month);
        int d = Integer.valueOf(day);
        int dayMax = 31;

        if (m == 2) {
            if (y % 4 == 0) {
                dayMax = 29;
            } else {
                dayMax = 28;
            }
        } else if (m == 4 || m == 6 || m == 9 || m == 11) {
            dayMax = 30;
        }

        return d <= dayMax;
    }

    private CustomerPickerView pickHeight;
    private String height;

    private void openHeightSelDialog() {
        if (watch.getHeight() < 60) {
            watch.setHeight(110.0);
        }

        height = Double.valueOf(watch.getHeight()).toString();

        final Dialog dlg = new Dialog(WatchDetailFirstSetActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) WatchDetailFirstSetActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_height, null);
        pickHeight = layout.findViewById(R.id.height_pv);
        pickHeight.setMarginAlphaValue((float) 3.8, "H");

        View pickerView = layout.findViewById(R.id.birthday_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        final int hheight = pickerView.getBackground().getMinimumHeight();

        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    watch.setHeight(Double.valueOf(height));
                    tvHeight.setText(String.format("%d%s", Double.valueOf(watch.getHeight()).intValue(), getText(R.string.str_cm)));
                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_HEIGHT, watch.getHeight());
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                dlg.dismiss();
            }
        });

        TextView tvKg = layout.findViewById(R.id.tv_height_pv);
        tvKg.setPadding(width * 5 / 10 + 40 * width / 1080, 0, 0, 0);
        tvKg.setTextColor(0xffdf5600);

        View mask_1 = layout.findViewById(R.id.iv_mask_1);
        mask_1.setTranslationX(width * 5 / 10 - 40 * width / 1080);
        mask_1.setTranslationY(hheight / 11);

        List<String> heights = new ArrayList<String>();

        for (int i = 60; i < 180; i++) {
            heights.add(i < 60 ? "0" + i : "" + i);
        }


        pickHeight.setData(heights);
        pickHeight.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                height = text;
                refreshBirthTxt();
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

    CustomerPickerView pickWeight;
    private String weight;

    private void openWeightSelDialog() {
        if (watch.getWeight() < 8) {
            watch.setWeight(18.0);
        }

        weight = Double.valueOf(watch.getWeight()).toString();

        final Dialog dlg = new Dialog(WatchDetailFirstSetActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) WatchDetailFirstSetActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_weight, null);

        pickWeight = layout.findViewById(R.id.weight_pv);
        pickWeight.setMarginAlphaValue((float) 3.8, "H");

        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        Button left_btn;
        Button right_btn;
        left_btn = layout.findViewById(R.id.cancel_btn);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        right_btn = layout.findViewById(R.id.confirm_btn);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    watch.setWeight(Double.valueOf(weight));
                    tvWeight.setText(String.format("%d%s", Double.valueOf(watch.getWeight()).intValue(), getText(R.string.str_kg)));
                    sendDeviceSet(CloudBridgeUtil.KEY_NAME_WEIGHT, watch.getWeight());
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                dlg.dismiss();
            }
        });

        TextView tvKg = layout.findViewById(R.id.tv_weight_pv);
        tvKg.setPadding(width * 5 / 10 + 30 * width / 1080, 0, 0, 0);
        tvKg.setTextColor(0xffdf5600);

        List<String> weights = new ArrayList<String>();

        for (int i = 8; i < 80; i++) {
            weights.add(i < 8 ? "0" + i : "" + i);
        }

        pickWeight.setData(weights);
        pickWeight.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                weight = text;
                refreshBirthTxt();
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

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        // TODO Auto-generated method stub
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);

        switch (cid) {
            case CloudBridgeUtil.CID_E2C_DOWN:
                myApp.sdcardLog("startPhotoZoom CID_E2C_DOWN rc:" + rc);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    if (CloudBridgeUtil.getCloudMsgSN(respMsg) == headE2cSn) {
                        sendDeviceHeadPath(watch);
                    } else {
                        JSONArray key;
                        //e2c group change ok
                        key = (JSONArray) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        //send e2e changes notice
                        sendGroupChangeE2G(reqMsg, key.get(0).toString());
                        if (myApp.getCurUser().getWatchList().size() > 0) {
                        } else {
                            myApp.doLogout("MEMBERDETAIL CID_E2C_DOWN self remove group");
                        }

                    }
                } else {
                    if (CloudBridgeUtil.getCloudMsgSN(respMsg) == headE2cSn) {
                        if (rc == CloudBridgeUtil.RC_NETERROR
                                || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                            ToastUtil.showMyToast(this,
                                    getString(R.string.network_error_prompt),
                                    Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showMyToast(this, getResources().getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                        }
                        loadingdlg.dismiss();
                        WatchDAO.getInstance(getApplicationContext()).readWatch(watch);
                        initViews();
                    }
                }

                break;
            case CloudBridgeUtil.CID_USER_SET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    finish();
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getResources().getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                }
                break;
            case CloudBridgeUtil.CID_DEVICE_SET_RESP:
                loadingdlg.dismiss();
                myApp.sdcardLog("startPhotoZoom CID_DEVICE_SET_RESP rc:" + rc);
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    //  finish();
                    ToastUtil.showMyToast(this, getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT);
                    FamilyData family = myApp.getCurUser().queryFamilyByGid(gid);
                    if (family != null) {
                        family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                    }
                    WatchDAO.getInstance(getApplicationContext()).addWatch(watch);
                    //send editnotice
                    sendDeviceSetChangeE2G(eid, gid);
                    Intent it = new Intent(Const.ACTION_ADD_WATCH_CONTACT);
                    it.putExtra("eid", eid);
                    sendBroadcast(it);
                    sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE));
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getResources().getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                    WatchDAO.getInstance(getApplicationContext()).readWatch(watch);
                    initViews();
                }
                break;
            case CloudBridgeUtil.CID_DEVICE_GET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    if (editFlag == false) {//如果已经编辑，则不刷新服务器上数据
                        //refresh watch
                        JSONObject devicePl = CloudBridgeUtil.getCloudMsgPL(respMsg);
                        myApp.parseDevicePl(watch, devicePl);
                        FamilyData family = myApp.getCurUser().queryFamilyByGid(gid);
                        if (family != null) {
                            family.setFamilyName(StrUtil.genFamilyName(family, getApplicationContext()));
                        }
                        WatchDAO.getInstance(getApplicationContext()).addWatch(watch);
                        initViews();
                    }
                } else {

                }
                break;

            case CloudBridgeUtil.CID_MAPSET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    //set info
                    sendDeviceHeadPath(watch);
                }
                break;
            case CloudBridgeUtil.CID_E2G_DOWN:
                break;
            default:
                break;
        }
    }

    private void sendDeviceSetChangeE2G(String eid, String gid) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(WatchDetailFirstSetActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);

        e2e.setReqMsg(CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), myApp.getToken(), null, gid, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2e);
    }

    private void sendDeviceHeadPath(WatchData watch) {

        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(WatchDetailFirstSetActivity.this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, watch.getCustomData().toJsonStr());
        relationMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(relationMsg);


    }

    private void sendDeviceSet(String key, Object value) {
        MyMsgData setMsg = new MyMsgData();
        setMsg.setCallback(WatchDetailFirstSetActivity.this);

        JSONObject pl = new JSONObject();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        setMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));

        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(setMsg);
        editFlag = true;
    }

    private int sendHeadImageE2c(String eid, byte[] mapBytes) {

        MyMsgData e2c = new MyMsgData();
        int sn;
        e2c.setCallback(WatchDetailFirstSetActivity.this);
        JSONObject pl = new JSONObject();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(eid);
        key.append(CloudBridgeUtil.E2C_SPLIT_HEADIMG);
        JSONObject chat = new JSONObject();
        chat.put(CloudBridgeUtil.HEAD_IMAGE_DATA, Base64.encodeToString(mapBytes, Base64.NO_WRAP));

        pl.put(key.toString(), chat);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C_UP,
                sn, myApp.getToken(), pl));
        if (getMyApp().getNetService() != null)
            getMyApp().getNetService().sendNetMsg(e2c);
        myApp.sdcardLog("startPhotoZoom sendHeadImageE2c mapBytes.size:" + mapBytes.length);
        return sn;
    }

    private void sendGroupChangeE2G(JSONObject e2cReq, String key) {
        // TODO Auto-generated method stub
        MyMsgData askJoinMsg_1 = new MyMsgData();
        askJoinMsg_1.setCallback(WatchDetailFirstSetActivity.this);
        //set msg body
        JSONObject reqPl = (JSONObject) e2cReq.get(CloudBridgeUtil.KEY_NAME_PL);

        JSONObject newPl_1 = null;
        for (Map.Entry<String, Object> entry : reqPl.entrySet()) {
            JSONObject tmp = (JSONObject) entry.getValue();
            newPl_1 = (JSONObject) tmp.clone();
            break;
        }

        if (newPl_1 != null) {
            newPl_1.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE);
            newPl_1.put(CloudBridgeUtil.KEY_NAME_VOICE_KEY, key);
            int SN = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

            newPl_1.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, getNoticeGroupChangeSMS(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP, SN, getMyApp().getCurUser().getEid(), gid, TimeUtil.getTimeStampLocal()));

            askJoinMsg_1.setReqMsg(
                    CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP, SN, getMyApp().getToken(), null, gid, newPl_1));
            if (getMyApp().getNetService() != null)
                getMyApp().getNetService().sendNetMsg(askJoinMsg_1);
        }
    }

    private String getNoticeGroupChangeSMS(int type, int sN2, String eid, String addGroupGid2,
                                           String timeStampLocal) {
        StringBuilder buff = new StringBuilder();
        buff.append("<");
        buff.append(Integer.valueOf(sN2).toString());
        buff.append(",");
        buff.append(getMyApp().getCurUser().getEid());
        buff.append(",");
        buff.append("G202");
        buff.append(",");
        buff.append(Integer.valueOf(type).toString());
        buff.append("@");
        buff.append(addGroupGid2);
        buff.append("@");
        buff.append(timeStampLocal);
        buff.append(">");
        return buff.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoGetUtil.GET_IMAGE_FROM_ALBUM) {
            try {
                if (data != null) {
                    mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                    PhotoGetUtil.startPhotoZoom(myApp, data.getData(), this, mSaveTemp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PhotoGetUtil.GET_IMAGE_FROM_CAMERA) {
            try {
                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                PhotoGetUtil.startPhotoZoom(myApp, photoUri, this, mSaveTemp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            setPicToView(data);
        }
    }

    byte[] headBitmapBytes = null;


    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        {
            Uri resultUri = UCrop.getOutput(picdata);
            Bitmap photo = null;
            cropTemp = mSaveTemp;
            try {
                photo =  BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                if (photo.getWidth() > 160 || photo.getHeight() > 160) {
                    photo = BitmapUtilities.getBitmapThumbnail(cropTemp.getPath(), 160, 160);
                }
                FileOutputStream fos = new FileOutputStream(cropTemp);
                boolean b = photo.compress(Bitmap.CompressFormat.JPEG, 70, fos);

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                myApp.sdcardLog("startPhotoZoom crop data FileNotFoundException");
            } catch (Exception e) {
                // TODO: handle exceptionCli
                myApp.sdcardLog("startPhotoZoom crop data get other Exception:" + e.getMessage());
            }

            try {
                Drawable drawable = new BitmapDrawable(getApplicationContext().getResources(), photo);

                //创建文件输出流
                OutputStream os;
                String md5;
                byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);
                md5 = MD5.md5_bytes(bitmapArray);
                headBitmapBytes = bitmapArray;

                File destFile = new File(ImibabyApp.getIconCacheDir(), md5 + ".jpg");
                cropTemp.renameTo(destFile);
                os = new FileOutputStream(destFile);
                os.write(bitmapArray);
                os.flush();

                watch.setHeadPath(md5);
                //set headmap
                if (headBitmapBytes != null) {
                    headE2cSn = sendHeadImageE2c(eid, headBitmapBytes);
                }

                ImageUtil.setMaskImage(iv_head, R.drawable.head_2, drawable);
                if (!loadingdlg.isShowing()) {
                    loadingdlg.changeStatus(1, getResources().getString(R.string.synch_szone_message));
                    loadingdlg.show();
                }
            } catch (FileNotFoundException e) {
                myApp.sdcardLog("startPhotoZoom crop data save FileNotFoundException");
            } catch (Exception e) {
                // TODO: handle exception
                myApp.sdcardLog("startPhotoZoom crop data save other Exception:" + e.getMessage());
            }
        }
    }

    @Override
    public void confirmClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(1 == requestCode && grantResults.length == 1){
            if(grantResults[0] == PERMISSION_GRANTED){
                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                photoUri = PhotoGetUtil.startCameraCapture(myApp, this, mSaveTemp);
            }else {
                Toast.makeText(WatchDetailFirstSetActivity.this,getString(R.string.camera_premission_tips),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

