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
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.WatchDAO;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.BitmapUtilities;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.MD5;
import com.xiaoxun.xun.utils.PhotoGetUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomSettingView;
import com.xiaoxun.xun.views.CustomerPickerView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by huangyouyang on 2016/11/30.
 */

public class DeviceDetailActivity extends NormalActivity implements View.OnClickListener {

    private ImageButton mBtnBack;
    private TextView mTvTitle;
    private ImageView mIvHead;
    private View mLineHead;
    private View mLineUsername;
    private TextView mTvNickname;
    private ImageView mEditNickname;
    private View mNeedAdmit;
    private LoadingDialog loadingDialog;

    private CustomSettingView mLayoutPhone;
    private CustomSettingView mLayoutSex;
    private CustomSettingView mLayoutBirthday;
    private CustomSettingView mLayoutHeight;
    private CustomSettingView mLayoutWeight;
    private CustomSettingView mInnerLayoutQr;
    private View mLayoutQr;

    private BroadcastReceiver mMsgReceiver;
    private NetService mNetService;
    private WatchData focusWatch;
    private int tempWatchSex;
    private String tempWatchNickname;
    private String tempWatchBirthday;
    private double tempWatchWeight;
    private double tempWatchHeight;
    private String tempWatchHeadPath;
    private String eid;

    private boolean editFlag;
    private final int GET_IMAGE_FROM_ALBUM=1;
    private final int GET_IMAGE_FROM_CAMERA=2;
    private final int GET_IMAGE_FROM_ZOOM=3;
    private File cameraTemp = null;
    private File cropTemp = null;

    private Uri photoUri;
    private File mSaveTemp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        mNetService=myApp.getNetService();
        eid = getIntent().getStringExtra("eid");
        if (eid != null && eid.length() > 0) {
            myApp.setFocusWatch(myApp.getCurUser().queryWatchDataByEid(eid));
        }else {
            eid=myApp.getCurUser().getFocusWatch().getEid();
        }

        initView();
        initListener();
        initReceiver();
        if (mNetService != null)
            mNetService.sendDeviceGet(eid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        focusWatch=myApp.getCurUser().getFocusWatch();
        reflushWatchInfo();
        updateViewByAdminByAuth();
        ifFromStepsActivity();
    }

    private void initView() {

        mTvTitle = findViewById(R.id.tv_title);
        mBtnBack = findViewById(R.id.iv_title_back);
        mLineHead=findViewById(R.id.line_head);
        mIvHead = findViewById(R.id.iv_head);
        mLineUsername = findViewById(R.id.line_username);
        mTvNickname = findViewById(R.id.tv_username);
        mEditNickname = findViewById(R.id.iv_edit_username);
        mNeedAdmit = findViewById(R.id.tv_need_admin);

        mLayoutPhone = findViewById(R.id.layout_phone_number);
        mLayoutSex = findViewById(R.id.layout_sex);
        mLayoutBirthday = findViewById(R.id.layout_birthday);
        /**
         * 隐藏生日;性别
         */
        mLayoutBirthday.setVisibility(View.GONE);
        mLayoutSex.setVisibility(View.GONE);

        mLayoutHeight = findViewById(R.id.layout_height);
        mLayoutWeight = findViewById(R.id.layout_weight);
        mInnerLayoutQr= findViewById(R.id.layout_qrcode);
        mLayoutQr = findViewById(R.id.layout_rl_qrcode);

        loadingDialog = new LoadingDialog(this, R.style.Theme_DataSheet, new LoadingDialog.OnConfirmClickListener() {
            @Override
            public void confirmClick() {
            }
        });
        loadingDialog.hideReloadView();
    }

    private void initListener() {

        mBtnBack.setOnClickListener(this);
        mIvHead.setOnClickListener(this);
        mLineUsername.setOnClickListener(this);
        mTvNickname.setOnClickListener(this);
        mEditNickname.setOnClickListener(this);
        mLayoutPhone.setOnClickListener(this);
        mLayoutSex.setOnClickListener(this);
        mLayoutBirthday.setOnClickListener(this);
        mLayoutHeight.setOnClickListener(this);
        mLayoutWeight.setOnClickListener(this);
        mLayoutQr.setOnClickListener(this);
    }

    private void reflushWatchInfo() {

        initializeData();
        initTempData();
        mTvTitle.setText(focusWatch.getNickname());
        mTvNickname.setText(focusWatch.getNickname());
        if (focusWatch.isWatch() || focusWatch.isDevice206_A02()) {
            String isSteps = getIntent().getStringExtra("isSteps");
            if(null != isSteps && "1".equals(isSteps)) {mLayoutPhone.setVisibility(View.GONE);}
            else {
                mLayoutPhone.setTitle(getString(R.string.phone_number));
                mLayoutPhone.setVisibility(View.VISIBLE);
            }
        } else {
            mLayoutPhone.setTitle(getString(R.string.device_number));
            mLayoutPhone.setVisibility(View.GONE);
        }
        mLayoutPhone.setState(StrUtil.formatPhoneNumber(focusWatch.getCellNum()));

        mLayoutSex.setState(focusWatch.getSex() > 0 ? getString(R.string.male) : getString(R.string.female));
        mLayoutHeight.setState(String.format("%d%s", Double.valueOf(focusWatch.getHeight()).intValue(), getText(R.string.str_cm)));
        mLayoutWeight.setState(String.format("%d%s", Double.valueOf(focusWatch.getWeight()).intValue(), getText(R.string.str_kg)));
        String birthday = focusWatch.getBirthday();
        mLayoutBirthday.setState(birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6, 8));

        ImageUtil.setMaskImage(mIvHead, R.drawable.head_2, getMyApp().getHeadDrawableByFile(getResources(), focusWatch.getHeadPath(), focusWatch.getEid(), R.drawable.default_head));
    }

    private void initializeData() {

        if (focusWatch.getHeight() < 60) {
            focusWatch.setHeight(110.0);
        }
        if (focusWatch.getWeight() < 8) {
            focusWatch.setWeight(18.0);
        }
        if (focusWatch.getBirthday() == null) {
            focusWatch.setBirthday("20121212");
        }
    }

    private void initTempData() {

        tempWatchNickname = focusWatch.getNickname();
        tempWatchSex = focusWatch.getSex();
        tempWatchBirthday = focusWatch.getBirthday();
        tempWatchWeight = focusWatch.getWeight();
        tempWatchHeight = focusWatch.getHeight();
        tempWatchHeadPath = focusWatch.getHeadPath();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.iv_head:
                openPhotoEditDialog();
                break;
            case R.id.line_username:
            case R.id.tv_username:
            case R.id.iv_edit_username:
                openNameEditDialog();
                break;

            case R.id.layout_phone_number:
                Intent it = new Intent(DeviceDetailActivity.this, SetDeviceNumberActivity.class);
                it.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
                startActivity(it);
                break;

            case R.id.layout_sex:
                openSexEditDialog();
                break;

            case R.id.layout_height:
                openHeightEditDialog();
                break;

            case R.id.layout_weight:
                openWeightSelDialog();
                break;

            case R.id.layout_birthday:
                openBirthEditDialog();
                break;

            case R.id.layout_rl_qrcode:
                Intent intent = new Intent(DeviceDetailActivity.this, DeviceQrActivity.class);
                intent.putExtra(Const.KEY_WATCH_ID, focusWatch.getEid());
                startActivity(intent);
                break;

            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(1 == requestCode && grantResults.length == 1){
            if(grantResults[0] == PERMISSION_GRANTED){
                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                photoUri = PhotoGetUtil.startCameraCapture(myApp, this, mSaveTemp);
            } else {
                Toast.makeText(DeviceDetailActivity.this,getString(R.string.camera_premission_tips),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openPhotoEditDialog(){
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getText(R.string.head_edit_camera).toString());
        itemList.add(getText(R.string.head_edit_pics).toString());
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(DeviceDetailActivity.this, getText(R.string.edit_head).toString(), itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        if (position == 1) {
                            if(ActivityCompat.checkSelfPermission(DeviceDetailActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED){
                                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                                photoUri = PhotoGetUtil.startCameraCapture(myApp, DeviceDetailActivity.this, mSaveTemp);
                            }else {
                                ActivityCompat.requestPermissions(DeviceDetailActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                            }
                        } else {
                            PhotoGetUtil.startPickHead(DeviceDetailActivity.this);
                        }
                    }
                },
                -1, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                }, getText(R.string.cancel).toString());
        dlg.show();
    }

    private void openNameEditDialog() {

        Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(DeviceDetailActivity.this,Const.NICKNAME_MAXLEN,
                0,
                getText(R.string.edit_nickname).toString(),
                focusWatch.getNickname(), null, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                        String nickname = text;
                        if (nickname.length() < 1) {
                            ToastUtil.showMyToast(DeviceDetailActivity.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
                        } else {
                            mTvTitle.setText(nickname);
                            mTvNickname.setText(nickname);
                            tempWatchNickname = nickname;
                            if (mNetService != null)
                                mNetService.sendDeviceSet(focusWatch.getEid(),CloudBridgeUtil.KEY_NAME_NICKNAME, nickname);
                            editFlag = true;
                        }
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void openSexEditDialog() {

        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getText(R.string.female).toString());
        itemList.add(getText(R.string.male).toString());
        int sexSel = 1;
        if (focusWatch.getSex() > 0)
            sexSel = 2;
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(DeviceDetailActivity.this, getText(R.string.edit_sex).toString(), itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        int sex=0;
                        if (position == 1) {
                            sex=0;
                        } else {
                            sex=1;
                        }
                        mLayoutSex.setState(sex>0?getString(R.string.male):getString(R.string.female));
                        tempWatchSex = sex;
                        if (mNetService != null)
                            mNetService.sendDeviceSet(focusWatch.getEid(),CloudBridgeUtil.KEY_NAME_SEX, sex);
                        editFlag = true;
                    }
                },
                sexSel, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                }, getText(R.string.cancel).toString());
        dlg.show();
    }


    private String height;
    private void openHeightEditDialog() {

        if (focusWatch.getHeight() < 60) {
            focusWatch.setHeight(110.0);
        }
        height = Double.valueOf(focusWatch.getHeight()).toString();
        final Dialog dlg = new Dialog(DeviceDetailActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) DeviceDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_height, null);
        CustomerPickerView pickHeight;
        pickHeight = layout.findViewById(R.id.height_pv);
        pickHeight.setMarginAlphaValue((float) 3.8, "H");
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        TextView tvCm = layout.findViewById(R.id.tv_height_pv);
        tvCm.setPadding(width * 5 / 10 + 40 * width / 1080, 0, 0, 0);
        tvCm.setTextColor(0xffdf5600);

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
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double tempHeight=Double.valueOf(height);
                    mLayoutHeight.setState(String.format("%d%s", tempHeight.intValue(), getText(R.string.str_cm)));
                    tempWatchHeight = tempHeight;
                    if (mNetService != null)
                        mNetService.sendDeviceSet(focusWatch.getEid(),CloudBridgeUtil.KEY_NAME_HEIGHT, tempHeight);
                    editFlag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dlg.dismiss();
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

    private String weight;
    private void openWeightSelDialog() {

        if (focusWatch.getWeight() < 8) {
            focusWatch.setWeight(18.0);
        }
        weight = Double.valueOf(focusWatch.getWeight()).toString();

        final Dialog dlg = new Dialog(DeviceDetailActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) DeviceDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_weight, null);
        CustomerPickerView pickWeight;
        pickWeight = layout.findViewById(R.id.weight_pv);
        pickWeight.setMarginAlphaValue((float) 3.8, "H");
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
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
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double tempWeight=Double.valueOf(weight);
                    mLayoutWeight.setState(String.format("%d%s", tempWeight.intValue(), getText(R.string.str_kg)));
                    tempWatchWeight = tempWeight;
                    if (mNetService != null)
                        mNetService.sendDeviceSet(focusWatch.getEid(),CloudBridgeUtil.KEY_NAME_WEIGHT, tempWeight);
                    editFlag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dlg.dismiss();
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

    private String year ;
    private String month ;
    private String day ;
    CustomerPickerView pickDay; //不同月份，日期会不一样
    private void openBirthEditDialog() {

        if (focusWatch.getBirthday() == null) {
            focusWatch.setBirthday("20121212");
        }
        year=focusWatch.getBirthday().substring(0,4);
        month=focusWatch.getBirthday().substring(4,6);
        day=focusWatch.getBirthday().substring(6,8);

        final Dialog dlg = new Dialog(DeviceDetailActivity.this, R.style.Theme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) DeviceDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.xiaomi_dialog_sel_birth, null);
        //init time pickviews
        CustomerPickerView pickYear = layout.findViewById(R.id.year_pv);
        pickYear.setMarginAlphaValue((float) 3.8, "H");
        CustomerPickerView pickMonth = layout.findViewById(R.id.month_pv);
        pickMonth.setMarginAlphaValue((float) 3.8, "H");
        pickDay = layout.findViewById(R.id.day_pv);
        pickDay.setMarginAlphaValue((float) 3.8, "H");
        View pickerView = layout.findViewById(R.id.birthday_picker_view);
        int width = Params.getInstance(getApplicationContext()).getScreenWidthInt();
        View line_1 = layout.findViewById(R.id.line_1);
        View line_2 = layout.findViewById(R.id.line_2);
        line_1.setTranslationX(pickerView.getX() + width * 4 / 10);
        line_2.setTranslationX(pickerView.getX() + width * 7 / 10);
        TextView tvYear = layout.findViewById(R.id.tv_year_pv);
        tvYear.setPadding(width * 2 / 10 + 55 * width / 1080, 0, 0, 0);
        tvYear.setTextColor(0xffdf5600);
        TextView tvMonth = layout.findViewById(R.id.tv_month_pv);
        tvMonth.setPadding(width * 11 / 20 + 30 * width / 1080, 0, 0, 0);
        tvMonth.setTextColor(0xffdf5600);
        TextView tvDay = layout.findViewById(R.id.tv_day_pv);
        tvDay.setPadding(width * 17 / 20 + 30 * width / 1080, 0, 0, 0);
        tvDay.setTextColor(0xffdf5600);

        List<String> years = new ArrayList<>();
        List<String> months = new ArrayList<>();
        List<String> days = new ArrayList<>();
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
                int maxDay = TimeUtil.getMaxday(year, month);
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
                reflushWatchInfo();
            }
        });
        pickYear.setSelected(Integer.valueOf(year) - 2000);
        pickMonth.setData(months);
        pickMonth.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month = text;
                int maxDay = TimeUtil. getMaxday(year, month);
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
            }
        });
        pickMonth.setSelected(Integer.valueOf(month) - 1);
        int maxDay = TimeUtil.getMaxday(year, month);
        List<String> qdays = new ArrayList<String>();
        for (int i = 1; i < maxDay + 1; i++) {
            qdays.add(i < 10 ? "0" + i : "" + i);
        }
        pickDay.setData(qdays);
        pickDay.setOnSelectListener(new CustomerPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                day = text;
            }
        });
        pickDay.setSelected(Integer.valueOf(day) - 1);

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
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempBirthday = year + month + day;
                if (Long.parseLong(tempBirthday) <= Long.parseLong(TimeUtil.getTimeStampLocal().substring(0,8))) {
                    mLayoutBirthday.setState(tempBirthday.substring(0, 4) + "-" + tempBirthday.substring(4, 6) + "-" + tempBirthday.substring(6, 8));
                    tempWatchBirthday = tempBirthday;
                    if (mNetService != null)
                        mNetService.sendDeviceSet(focusWatch.getEid(), CloudBridgeUtil.KEY_NAME_DATE_OF_BIRTH, tempBirthday);
                    editFlag = true;
                } else {
                    ToastUtil.showMyToast(DeviceDetailActivity.this, getString(R.string.date_error), Toast.LENGTH_SHORT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
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

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {
        Uri resultUri = UCrop.getOutput(picdata);
        Bitmap photo = null;
        cropTemp = mSaveTemp;
        try {
            photo =  BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
            if (photo.getWidth() > 720 || photo.getHeight() > 720) {
                photo = BitmapUtilities.getBitmapThumbnail(cropTemp.getPath(), 720, 720);
            }
            FileOutputStream fos = new FileOutputStream(cropTemp);
            photo.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            if (cropTemp.length() > 45 * 1024)
                photo.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(cropTemp));
            if (cropTemp.length() > 45 * 1024)
                photo.compress(Bitmap.CompressFormat.JPEG, 10, new FileOutputStream(cropTemp));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
        }

        Drawable drawable = new BitmapDrawable(getApplicationContext().getResources(), photo);
        try {
            //创建文件输出流
            OutputStream os;
            String md5;
            byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);
            md5 = MD5.md5_bytes(bitmapArray);

            byte[] headBitmapBytes = bitmapArray;
            File destFile = new File(ImibabyApp.getIconCacheDir(), md5 + ".jpg");
            cropTemp.renameTo(destFile);
            // photo.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(destFile));
            os = new FileOutputStream(destFile);
            os.write(bitmapArray);
            os.flush();

            // 保存图像、更新View显示、保存到服务器
            focusWatch.setHeadPath(md5);
            ImageUtil.setMaskImage(mIvHead, R.drawable.head_2, drawable);
            if (headBitmapBytes != null) {
                if (mNetService != null)
                    mNetService.sendHeadImageE2c(1, focusWatch.getEid(), headBitmapBytes);
                editFlag = true;
            }
            if (!loadingDialog.isShowing()) {
                loadingDialog.changeStatus(1, getString(R.string.synch_szone_message));
                loadingDialog.show();
            }
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
        }
    }

    private void updateViewByAdminByAuth() {

        if (myApp.getCurUser().isMeAdminByWatch(focusWatch)) {
            mNeedAdmit.setVisibility(View.GONE);
        } else {
            mLayoutSex.setClickable(false);
            mLayoutSex.setIvArrow(null);
            mLayoutHeight.setClickable(false);
            mLayoutHeight.setIvArrow(null);
            mLayoutWeight.setClickable(false);
            mLayoutWeight.setIvArrow(null);
            mLayoutBirthday.setClickable(false);
            mLayoutBirthday.setIvArrow(null);
            mLayoutPhone.setClickable(false);
            mLayoutPhone.setIvArrow(null);
            mIvHead.setClickable(false);
            mEditNickname.setClickable(false);
            mTvNickname.setClickable(false);
            mLineUsername.setClickable(false);
            mInnerLayoutQr.setIvArrow(null);
        }
        if (focusWatch.isDevice102()){
            mLayoutPhone.setClickable(false);
            mLayoutPhone.setIvArrow(null);
        }
    }

    private void ifFromStepsActivity() {

        String isSteps = getIntent().getStringExtra("isSteps");
        if (isSteps != null) {
            if (isSteps.equals("1")) {
                mLineHead.setVisibility(View.GONE);
                mLayoutBirthday.setVisibility(View.GONE);
                mLayoutSex.setVisibility(View.GONE);
                mLayoutQr.setVisibility(View.GONE);
                mLayoutPhone.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMsgReceiver);
    }

    private void initReceiver() {

        mMsgReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_RECEIVE_GET_DEVICE_INFO) || action.equals(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW)) {
                    if (!editFlag) {
                        reflushWatchInfo();
                    }
                } else if (action.equals(Const.ACTION_RECEIVE_SET_DEVICE_INFO)) {
                    editFlag = false;
                    int setResult = intent.getIntExtra(Const.SETTING_RESULt, Const.SETTING_SUCCESS);
                    if (setResult == Const.SETTING_SUCCESS){
                        focusWatch.setNickname(tempWatchNickname);
                        focusWatch.setBirthday(tempWatchBirthday);
                        focusWatch.setWeight(tempWatchWeight);
                        focusWatch.setHeight(tempWatchHeight);
                        focusWatch.setSex(tempWatchSex);
                        WatchDAO.getInstance(getApplicationContext()).addWatch(focusWatch);
                        DeviceDetailActivity.this.sendBroadcast(new Intent(Const.ACTION_RECEIVE_SET_DEVICE_INFO_CHANGE));
                    }else if (setResult == Const.SETTING_FAIL){
                        focusWatch.setHeadPath(tempWatchHeadPath);
                    }
                    reflushWatchInfo();
                }else if(action.equals(Const.ACTION_RECEIVE_SEND_IMAGE_DATA)){
                    loadingDialog.dismiss();
                } else if (action.equals(Const.ACTION_DOWNLOAD_HEADIMG_OK)) {
                    ImageUtil.setMaskImage(mIvHead, R.drawable.head_2, getMyApp().getHeadDrawableByFile(getResources(), focusWatch.getHeadPath(), focusWatch.getEid(), R.drawable.default_head));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW);
        filter.addAction(Const.ACTION_RECEIVE_GET_DEVICE_INFO);
        filter.addAction(Const.ACTION_RECEIVE_SET_DEVICE_INFO);
        filter.addAction(Const.ACTION_RECEIVE_SEND_IMAGE_DATA);
        filter.addAction(Const.ACTION_DOWNLOAD_HEADIMG_OK);
        registerReceiver(mMsgReceiver, filter);
    }
}