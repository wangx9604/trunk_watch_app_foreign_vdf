package com.xiaoxun.xun.securityarea.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.activitys.SecurityZoneActivityNew;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.securityarea.bean.GuardOnOffBean;
import com.xiaoxun.xun.securityarea.bean.SchoolGuardBean;
import com.xiaoxun.xun.securityarea.service.SecurityService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.StrUtil;

import net.minidev.json.JSONObject;

public class SecurityZoneMainActivity extends NormalActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImibabyApp mApp;
    private WatchData mCurWatch;

    private ImageButton mBtnBack;
    private Button btSave;

    private ToggleButton toggleBtCom;
    private ToggleButton toggleBtSchool;
    private ToggleButton toggleBtDanger;
    private ToggleButton toggleBtSafe;
    private ToggleButton toggleBtCity;
    private TextView comClick;
    private TextView schoolClick;
    private TextView dangerClick;
    private TextView safeClick;
    private TextView cityClick;
    private CardView cvCom;
    private CardView cvSchool;
    private CardView cvDanger;
    private CardView cvSafe;
    private CardView cvCity;


    private TextView watch_state;
    private LinearLayout layout_save_power_state;

    boolean hasSet;//第一次或者上一次将所有开关关闭时为false
    boolean normalHasSet = true;//判断常用区域是否设置

    private int com = 0;//常用   1开0关  默认值，用于判断是否修改-->退出时是否弹窗
    private int school = 0;//上下学守护
    private int danger = 0;//危险区域
    private int safe = 0;//安全区域
    private int city = 0;//基础城市服务
    private GuardOnOffBean mBean = new GuardOnOffBean(0, 0, 0, 0, 0);

    private SchoolGuardBean mSchoolBean;

    int code;//判断是否支持google map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ImibabyApp) getApplication();
        mCurWatch = getMyApp().getCurUser().getFocusWatch();
        code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        setContentView(R.layout.activity_security_zone_main);
        initViews();
        hasSet = getIntent().getBooleanExtra("hasSet", true);
        loadCancel();
        getOnOffStatus();
        initListener();
    }

    private void loadCancel() {
        String cancelStatus = mApp.getStringValue(mCurWatch.getEid() + Constants.SHARE_PREF_SECURITY_MAIN_STATUS, "");
        if (StrUtil.isNotBlank(cancelStatus)) {
            com = Integer.parseInt(String.valueOf(cancelStatus.charAt(0)));
            school = Integer.parseInt(String.valueOf(cancelStatus.charAt(1)));
            danger = Integer.parseInt(String.valueOf(cancelStatus.charAt(2)));
            safe = Integer.parseInt(String.valueOf(cancelStatus.charAt(3)));
            city = Integer.parseInt(String.valueOf(cancelStatus.charAt(4)));
            mBean.setCom(com);
            mBean.setSchool(school);
            mBean.setDanger(danger);
            mBean.setSafe(safe);
            mBean.setCity(city);
            if (com == 1) {
                comClick.setVisibility(View.VISIBLE);
                toggleBtCom.setChecked(true);
            } else {
                comClick.setVisibility(View.GONE);
                toggleBtCom.setChecked(false);
            }
            if (school == 1) {
                schoolClick.setVisibility(View.VISIBLE);
                toggleBtSchool.setChecked(true);
            } else {
                schoolClick.setVisibility(View.GONE);
                toggleBtSchool.setChecked(false);
            }
            if (danger == 1) {
                dangerClick.setVisibility(View.VISIBLE);
                toggleBtDanger.setChecked(true);
            } else {
                dangerClick.setVisibility(View.GONE);
                toggleBtDanger.setChecked(false);
            }
            if (safe == 1) {
                safeClick.setVisibility(View.VISIBLE);
                toggleBtSafe.setChecked(true);
            } else {
                safeClick.setVisibility(View.GONE);
                toggleBtSafe.setChecked(false);
            }
            if (city == 1) {
                cityClick.setVisibility(View.VISIBLE);
                toggleBtCity.setChecked(true);
            } else {
                cityClick.setVisibility(View.GONE);
                toggleBtCity.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int opm = mApp.getIntValue(mCurWatch.getEid() + CloudBridgeUtil.OPERATION_MODE_VALUE,
                Const.DEFAULT_OPERATIONMODE_VALUE);
        if (opm == 4) {
            String str;

            str = getString(R.string.save_power_remmind);
            watch_state.setText(str);
            layout_save_power_state.setVisibility(View.VISIBLE);

        } else {
            layout_save_power_state.setVisibility(View.GONE);
        }
        getNormalData();

    }

    private void getNormalData() {
        if (mApp.getNetService() != null) {
            SecurityService.getInstance(mApp).sendNormalAreaGetMsg(mCurWatch.getEid(), new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (rc == CloudBridgeUtil.RC_SUCCESS) {
                        JSONObject mPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        if (mPl != null) {
                            normalHasSet = mPl.get("EFID1") != null && mPl.get("EFID2") != null;
                        } else {
                            normalHasSet = false;
                        }
                    }
                }
            });
        }
    }

    private void initViews() {
        mBtnBack = findViewById(R.id.iv_title_back);

        watch_state = findViewById(R.id.watch_state);
        layout_save_power_state = findViewById(R.id.layout_save_power_state);

        toggleBtCom = findViewById(R.id.toggle_bt_normal);
        toggleBtSchool = findViewById(R.id.toggle_bt_school);
        toggleBtDanger = findViewById(R.id.toggle_bt_danger);
        toggleBtSafe = findViewById(R.id.toggle_bt_safe);
        toggleBtCity = findViewById(R.id.toggle_bt_base);
        comClick = findViewById(R.id.normal_click);
        schoolClick = findViewById(R.id.school_click);
        dangerClick = findViewById(R.id.danger_click);
        safeClick = findViewById(R.id.safe_click);
        cityClick = findViewById(R.id.base_click);
        cvCom = findViewById(R.id.cv_normal);
        cvSchool = findViewById(R.id.cv_school);
        cvDanger = findViewById(R.id.cv_danger);
        cvSafe = findViewById(R.id.cv_safe);
        cvCity = findViewById(R.id.cv_base);

        btSave = findViewById(R.id.btn_save);
    }


    private void initListener() {
        mBtnBack.setOnClickListener(this);
        btSave.setOnClickListener(this);
        toggleBtCom.setOnCheckedChangeListener(this);
        toggleBtSchool.setOnCheckedChangeListener(this);
        toggleBtDanger.setOnCheckedChangeListener(this);
        toggleBtSafe.setOnCheckedChangeListener(this);
        toggleBtCity.setOnCheckedChangeListener(this);
        cvCom.setOnClickListener(this);
        cvSchool.setOnClickListener(this);
        cvDanger.setOnClickListener(this);
        cvSafe.setOnClickListener(this);
        cvCity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnBack) {
            backPage();
        } else if (v == btSave) {
            setOnOffStatus();
            if (mBean.getCom() == 0 && mBean.getSchool() == 0 && mBean.getDanger() == 0 && mBean.getSafe() == 0 && mBean.getCity() == 0) {
                mApp.setValue(mCurWatch.getEid() + Constants.SHARE_PREF_IS_FIRST_TIME_TO_SECURITY, true);
                finish();
                return;
            }
            startActivity(new Intent(getApplicationContext(), SecurityMapMainActivity.class));
            finish();
        } else if (v == cvCom) {
            if (toggleBtCom.isChecked()) {
                if (code != ConnectionResult.SUCCESS) {
                    ToastUtils.showShort(getString(R.string.map_no_support));
                    return;
                }
                startActivity(new Intent(this, SecurityZoneActivityNew.class));
            }
        } else if (v == cvSchool) {
            if (toggleBtSchool.isChecked()) {
                if (!normalHasSet) {
                    Dialog dlg = DialogUtil.CustomNormalDialog(SecurityZoneMainActivity.this, getString(R.string.not_set_normal_area), getString(R.string.not_set_normal_area_tip),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    toggleBtSchool.setChecked(false);
                                }
                            }, getString(R.string.cancel),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    toggleBtSchool.setChecked(false);
                                    if (code != ConnectionResult.SUCCESS) {
                                        ToastUtils.showShort(getString(R.string.map_no_support));
                                        return;
                                    }
                                    startActivity(new Intent(SecurityZoneMainActivity.this, SecurityZoneActivityNew.class));
                                }
                            }, getString(R.string.security_zone_default_setting));
                    dlg.setCancelable(false);
                    dlg.show();
                } else {
                    Intent intent = new Intent(this, SecuritySchoolActivity.class);
                    intent.putExtra("school", mBean.getSchool());
                    startActivityForResult(intent, 1);
                }

            }
        } else if (v == cvDanger) {
            if (toggleBtDanger.isChecked()) {
                Intent intent = new Intent(this, DangerAreaActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_ACTIVITY, Constants.KEY_NAME_DANGER);
                startActivity(intent);
            }
        } else if (v == cvSafe) {
            if (toggleBtSafe.isChecked()) {
                Intent intent = new Intent(this, DangerAreaActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_ACTIVITY, Constants.KEY_NAME_SAFE);
                startActivity(intent);
            }
        } else if (v == cvCity) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mSchoolBean = (SchoolGuardBean) data.getSerializableExtra("school");
            if (mSchoolBean != null) {
                mBean.setSchool(Integer.parseInt(mSchoolBean.getOnoff()));
                toggleBtSchool.setChecked(mBean.getSchool() == 1);
            }

        }
    }

    //设置更新开关状态
    private void setOnOffStatus() {
        com = mBean.getCom();
        school = mBean.getSchool();
        danger = mBean.getDanger();
        safe = mBean.getSafe();
        city = mBean.getCity();
        String[] mList = new String[5];
        mList[0] = Constants.KEY_NAME_COM + "," + mBean.getCom();
        mList[1] = Constants.KEY_NAME_SCHOOL + "," + mBean.getSchool();
        mList[2] = Constants.KEY_NAME_DANGER + "," + mBean.getDanger();
        mList[3] = Constants.KEY_NAME_SAFE + "," + mBean.getSafe();
        mList[4] = Constants.KEY_NAME_CITY + "," + mBean.getCity();
        String mInfo = new Gson().toJson(mList);
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendMapSetMsg(mCurWatch.getEid(), mCurWatch.getFamilyId(), CloudBridgeUtil.KEY_GUARD_ONOFF_LIST, mInfo, new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                    if (rc == 1) {
                        Toast.makeText(SecurityZoneMainActivity.this, getString(R.string.save_successs), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        String cancel = String.valueOf(com) + school + danger + safe + city;
        mApp.setValue(mCurWatch.getEid() + Constants.SHARE_PREF_IS_FIRST_TIME_TO_SECURITY, false);
        mApp.setValue(mCurWatch.getEid() + Constants.SHARE_PREF_SECURITY_MAIN_STATUS, cancel);
    }

    //获取开关状态
    private void getOnOffStatus() {
        if (getMyApp().getNetService() == null) return;
        getMyApp().getNetService().sendMapGetMsg(mCurWatch.getEid(), CloudBridgeUtil.KEY_GUARD_ONOFF_LIST, new MsgCallback() {
            @Override
            public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                JSONObject mPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc == 1) {
                    if (mPl != null) {
                        String data = (String) mPl.get(CloudBridgeUtil.KEY_GUARD_ONOFF_LIST);
                        if (data != null) {
                            String[] mList = new Gson().fromJson(data, String[].class);
                            com = Integer.parseInt(String.valueOf(mList[0].charAt(mList[0].length() - 1)));
                            school = Integer.parseInt(String.valueOf(mList[1].charAt(mList[1].length() - 1)));
                            danger = Integer.parseInt(String.valueOf(mList[2].charAt(mList[2].length() - 1)));
                            safe = Integer.parseInt(String.valueOf(mList[3].charAt(mList[3].length() - 1)));
                            city = Integer.parseInt(String.valueOf(mList[4].charAt(mList[4].length() - 1)));
                            mBean.setCom(com);
                            mBean.setSchool(school);
                            mBean.setDanger(danger);
                            mBean.setSafe(safe);
                            mBean.setCity(city);
                        } else {
                            //还未设置过时，常用区域和上下学守护默认开启
                            com = 1;
                            school = 0;
                            danger = 0;
                            safe = 0;
                            city = 0;
                            mBean.setCom(1);
                            mBean.setSchool(1);
                            mBean.setDanger(0);
                            mBean.setSafe(0);
                            mBean.setCity(0);
                            toggleBtCom.setChecked(true);
                            toggleBtSchool.setChecked(false);
                            btSave.setText(getString(R.string.start_protection));
                            btSave.setEnabled(false);
                        }
                        if (com == 1) {
                            comClick.setVisibility(View.VISIBLE);
                            toggleBtCom.setChecked(true);
                        } else {
                            comClick.setVisibility(View.GONE);
                            toggleBtCom.setChecked(false);
                        }
                        if (school == 1) {
                            schoolClick.setVisibility(View.VISIBLE);
                            toggleBtSchool.setChecked(true);
                        } else {
                            schoolClick.setVisibility(View.GONE);
                            toggleBtSchool.setChecked(false);
                        }
                        if (danger == 1) {
                            dangerClick.setVisibility(View.VISIBLE);
                            toggleBtDanger.setChecked(true);
                        } else {
                            dangerClick.setVisibility(View.GONE);
                            toggleBtDanger.setChecked(false);
                        }
                        if (safe == 1) {
                            safeClick.setVisibility(View.VISIBLE);
                            toggleBtSafe.setChecked(true);
                        } else {
                            safeClick.setVisibility(View.GONE);
                            toggleBtSafe.setChecked(false);
                        }
                        if (city == 1) {
                            cityClick.setVisibility(View.VISIBLE);
                            toggleBtCity.setChecked(true);
                        } else {
                            cityClick.setVisibility(View.GONE);
                            toggleBtCity.setChecked(false);
                        }
                    }
                    btSave.setEnabled(false);
                }
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        btSave.setEnabled(true);
        if (buttonView == toggleBtCom) {
            if (buttonView.isChecked()) {
                comClick.setVisibility(View.VISIBLE);
                mBean.setCom(1);
            } else {
                comClick.setVisibility(View.GONE);
                mBean.setCom(0);
            }
        } else if (buttonView == toggleBtSchool) {
            if (buttonView.isPressed()) {
                if (!normalHasSet) {
                    Dialog dlg = DialogUtil.CustomNormalDialog(SecurityZoneMainActivity.this, getString(R.string.not_set_normal_area), getString(R.string.not_set_normal_area_tip),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    buttonView.setChecked(false);
                                }
                            }, getString(R.string.cancel),
                            new DialogUtil.OnCustomDialogListener() {
                                @Override
                                public void onClick(View v) {
                                    buttonView.setChecked(false);
                                    if (code != ConnectionResult.SUCCESS) {
                                        ToastUtils.showShort(getString(R.string.map_no_support));
                                        return;
                                    }
                                    startActivity(new Intent(SecurityZoneMainActivity.this, SecurityZoneActivityNew.class));
                                }
                            }, getString(R.string.security_zone_default_setting));
                    dlg.setCancelable(false);
                    dlg.show();
                } else {
                    if (buttonView.isChecked()) {
                        schoolClick.setVisibility(View.VISIBLE);
                        mBean.setSchool(1);
                    } else {
                        schoolClick.setVisibility(View.GONE);
                        mBean.setSchool(0);
                    }
                }
            }

        } else if (buttonView == toggleBtDanger) {
            if (buttonView.isChecked()) {
                dangerClick.setVisibility(View.VISIBLE);
                mBean.setDanger(1);
            } else {
                dangerClick.setVisibility(View.GONE);
                mBean.setDanger(0);
            }
        } else if (buttonView == toggleBtSafe) {
            if (buttonView.isChecked()) {
                safeClick.setVisibility(View.VISIBLE);
                mBean.setSafe(1);
            } else {
                safeClick.setVisibility(View.GONE);
                mBean.setSafe(0);
            }
        } else if (buttonView == toggleBtCity) {
            cityClick.setVisibility(View.GONE);
            if (buttonView.isChecked()) {
                mBean.setCity(1);
            } else {
                mBean.setCity(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPage();
    }

    //退出
    private void backPage() {
        if (!hasSet) {
            finish();
        } else if (com == mBean.getCom() && school == mBean.getSchool() && safe == mBean.getSafe() && danger == mBean.getDanger() && city == mBean.getCity()) {
            startActivity(new Intent(SecurityZoneMainActivity.this, SecurityMapMainActivity.class));
            finish();
        } else {
            Dialog dlg = DialogUtil.CustomNormalDialog(SecurityZoneMainActivity.this, getString(R.string.prompt), getString(R.string.security_out_save_tip),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SecurityZoneMainActivity.this, SecurityMapMainActivity.class));
                            finish();
                        }
                    }, getString(R.string.cancel),
                    new DialogUtil.OnCustomDialogListener() {
                        @Override
                        public void onClick(View v) {
                            setOnOffStatus();
                            startActivity(new Intent(SecurityZoneMainActivity.this, SecurityMapMainActivity.class));
                            finish();
                        }
                    }, getString(R.string.save_edit));
            dlg.setCancelable(false);
            dlg.show();
        }
    }
}