/**
 * Creation Date:2015-2-15
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.SleepTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AppStoreUtils;
import com.xiaoxun.xun.utils.CallBack;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.MigrationDialog;
import com.xiaoxun.xun.views.RoundProgressBar;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-2-15
 */
public class BindResultActivity extends NormalActivity implements OnClickListener, MsgCallback {
    static private String TAG = "BindResultActivity";

    private int state = 0;//0: error;

    private BroadcastReceiver mBindReceiver;
    private String qrCode;
    private String verCode;
    private String sn;
    private String curWatchEid = null;
    private View layerAdminWait;
    private View layerWatchWait;
    private View layerBindSuccess;
    private TextView tvTimeoutAdmin;
    private View layerBindError;
    private View layerBindWait;
    private View layerSendAdminOK;
    private Button btnSendAdminOK;
    private TextView tvSendAdminOK;

    private Button btnOkNext;
    private Button btnErrorNext;
    private TextView tvTimeout = null;
    private TextView tvTimeoutWatch;
    private String errStr;
    private String errStr2;
    private String errStr3;
    private TextView tvErrorResult;
    private TextView tvErrorResult2;
    private TextView tvErrorResult3;
    private TextView mGuideOthers;
    private ImageButton ibHelpWeb;
    private boolean isShowTvBindErrorButton;

    private Typeface mCustomFontType;

    private RoundProgressBar mRoundProgressBar1;

    private TextView tvSendReq;
    private TextView tvWaitAdmin;
    private ImageView ivWatchAnimation;
    private AnimationDrawable animDra = null;

    private Thread reSendThread = null;

    private boolean isQueryGroups = false;

    private int adminTag = -1;

    private final static String MISTAT_CATEGORY_BIND = "bind";
    private final static String MISTAT_KEY_BIND_SEND_TO_ADMIN = "bindsend_to_admin";
    private final static String MISTAT_KEY_BIND_SEND_TO_WATCH = "bindsend_to_watch";
    private final static String MISTAT_KEY_BIND_ANSWER_ADMIN_ACK = "bindanswer_admin_ack";
    private final static String MISTAT_KEY_BIND_ANSWER_ADMIN_ACCEPT = "bindanswer_admin_accept";
    private final static String MISTAT_KEY_BIND_ANSWER_ADMIN_IGNORE = "bindanswer_admin_ignore";
    private final static String MISTAT_KEY_BIND_ANSWER_WATCH_ACCEPT = "bindanswer_watch_accept";


    private String adminAccount = null;
    private String adminXiaomiId = null;
    private String adminRelation = null;
    private String adminName = null;//管理员名称
    private String adminPhone = null;
    private String strSendReq;
    private String strWaitAdmin;
    private String snType;
    private String mDeviceType;
    private String mSendAdminOK;

    private List<WatchData> watchDataList=new ArrayList<>();//装以前手表数据的list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_result);
        //       ((TextView)findViewById(R.id.tv_title)).setText(R.string.bind_watch);
        setTintColor(getResources().getColor(R.color.welcome_bg_color));
        String txt = getIntent().getExtras().getString(Const.KEY_MSG_CONTENT);
        state = getIntent().getExtras().getInt(Const.KEY_RESULT_CODE);
        qrCode = getIntent().getExtras().getString(CloudBridgeUtil.KEY_NAME_SERINALNO);
        verCode = getIntent().getExtras().getString(CloudBridgeUtil.KEY_NAME_IMSI);

        String fullSn = getWatchSNfromQr(qrCode);
        //parse qrtxt
        if (fullSn != null) {
            snType = fullSn.substring(0, 3);//现在应该为 AA.
            sn = fullSn.substring(3);
        }

        LogUtil.d(TAG + "  " + "Iccid: " + sn + ",msg: " + txt + ",result: " + state);
        //     curWatchEid = sn;
        errStr = txt;
        if (errStr == null || errStr.isEmpty())
            errStr = getText(R.string.scan_wrong).toString();
        //initviews
        layerAdminWait = findViewById(R.id.layer_wait_admin);
        layerSendAdminOK = findViewById(R.id.layer_send_admin_ok);
        layerWatchWait = findViewById(R.id.layer_wait_watch);
        layerBindSuccess = findViewById(R.id.layer_ok);
        layerBindError = findViewById(R.id.layer_error);
        layerBindWait = findViewById(R.id.layer_wait_loading);

        btnSendAdminOK = findViewById(R.id.btn_send_admin_ok);
        btnSendAdminOK.setOnClickListener(this);
        btnOkNext = findViewById(R.id.btn_next_step_ok);
        btnErrorNext = findViewById(R.id.btn_next_step_error);
        btnOkNext.setOnClickListener(this);
        btnErrorNext.setOnClickListener(this);

        mCustomFontType = Typeface.createFromAsset(getAssets(), "date.ttf");

        tvSendAdminOK = findViewById(R.id.tv_send_admin_ok);
        tvTimeoutAdmin = findViewById(R.id.tv_timecount_admin);
        tvTimeoutWatch = findViewById(R.id.tv_timecount_watch);
        tvTimeoutAdmin.setTypeface(mCustomFontType);
        tvTimeoutWatch.setTypeface(mCustomFontType);
        mGuideOthers = findViewById(R.id.tv_guide_others);

        tvErrorResult = findViewById(R.id.bind_result_error);
        tvErrorResult2 = findViewById(R.id.bind_result_error_2);
        tvErrorResult3 = findViewById(R.id.bind_result_error_3);

        ibHelpWeb = findViewById(R.id.ib_help_web);
        mRoundProgressBar1 = findViewById(R.id.round_progressbar_1);
        tvSendReq = findViewById(R.id.tv_send_req);
        tvWaitAdmin = findViewById(R.id.tv_wait_admin);
        ivWatchAnimation = findViewById(R.id.iv_press_animation);

        initReceiver();
        if (savedInstanceState != null) {
            state = savedInstanceState.getInt("state");
            errStr = savedInstanceState.getString("errStr");
            errStr3 = savedInstanceState.getString("errStr3");
            mSendAdminOK = savedInstanceState.getString("mSendAdminOK");
        }
        if (state != 1) {
            refereshUIByState();
        } else {
            layerBindWait.setVisibility(View.VISIBLE);
            myApp.sdcardLog("onCreate  layerBindWait: VISIBLE");
            sendReqJoinWatchGroupMsg(qrCode, verCode);
        }
    }

    private String getWatchSNfromQr(String qrtxt) {
        String sn = null;

        if (qrtxt == null || qrtxt.length() < 20) {//判断是否符合规则
            return null;
        } else {
            if (qrtxt.startsWith(Const.NEW_QR_START)) {
                sn = qrtxt.substring(Const.NEW_QR_START.length());
            } else {
                return null;
            }
        }
        return sn;
    }

    private String curAdminEid = null;

    private boolean isWatchAdmin() {
        return (curWatchEid != null
                && curAdminEid != null
                && curWatchEid.equals(curAdminEid)) || adminTag == 0;
    }

    private void refreshSendAdminOK() {
        layerBindWait.setVisibility(View.GONE);
        layerBindSuccess.setVisibility(View.GONE);
        layerWatchWait.setVisibility(View.GONE);
        layerAdminWait.setVisibility(View.GONE);
        layerBindError.setVisibility(View.GONE);
        layerSendAdminOK.setVisibility(View.VISIBLE);
    }

    private void refereshUIByState() {
        refereshUIByState(false);
    }

    private void refereshUIByState(boolean isResetTimout) {
        layerBindWait.setVisibility(View.GONE);
        layerBindSuccess.setVisibility(View.GONE);
        layerWatchWait.setVisibility(View.GONE);
        layerAdminWait.setVisibility(View.GONE);
        layerSendAdminOK.setVisibility(View.GONE);
        layerBindError.setVisibility(View.GONE);

        if (state == 2) {
            captchaCount = 0;
            forceStopTimeout();
            if (animDra != null) {
                animDra.stop();
            }
            layerBindSuccess.setVisibility(View.VISIBLE);
            findViewById(R.id.layer_root).setBackgroundColor(getResources().getColor(R.color.welcome_bg_color));
        } else if (state == 1) {
            //倒计时
            if (isResetTimout) {
                captchaCount = 60 * 5;
            }
            boolean trig = false;
            if (tvTimeout == null)
                trig = true;

            if (isWatchAdmin()) {
                if (mDeviceType != null && mDeviceType.length() > 0) {
                    if(mDeviceType.equals("SW730")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_730_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    }else if (mDeviceType.equals("SW701") || mDeviceType.equals("SW710") || mDeviceType.equals("SW710_A03") ||
                        mDeviceType.equals("SW709_A03") || mDeviceType.equals("SW708_A06") || mDeviceType.equals("SW707_A05")
                            || mDeviceType.equals("SW707_A03")|| mDeviceType.equals("SW707_H01")|| mDeviceType.equals("SW709_H01")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_right_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if(mDeviceType.equals("SW305")){
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_right_top_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if (mDeviceType.equals("SW501") ) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if (mDeviceType.equals("SW502") || mDeviceType.equals("SW502B_A02")
                            || mDeviceType.equals("SW502_A03") || mDeviceType.equals("SW703")
                            || mDeviceType.equals("SW760") || mDeviceType.equals("SW706")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_press_nomic);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if (mDeviceType.equals("SW302") || mDeviceType.equals("SW303") || mDeviceType.equals("SW303_A02")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if(mDeviceType.equals("SW306") || mDeviceType.equals("SW306_A02") || mDeviceType.equals("SW307") || mDeviceType.equals("SW306_A03")){
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_306_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    } else if(mDeviceType.equals("SW705") ) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_705_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.power_button)));
                    }else if (mDeviceType.equals("SW203_A03")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animaiton_press_203);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept_203));
                    }else if(mDeviceType.equals("SW206_A02")) {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_square_left_206_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept_203));
                    }else {
                        ivWatchAnimation.setBackgroundResource(R.drawable.animation_press);
                        mGuideOthers.setText(getString(R.string.guide_watch_accept, getString(R.string.voice_button)));
                    }
                }
                layerWatchWait.setVisibility(View.VISIBLE);
                tvTimeout = tvTimeoutWatch;
                if (animDra == null) {
                    animDra = (AnimationDrawable) ivWatchAnimation.getBackground();
                    animDra.selectDrawable(0);
                    animDra.start();
                }
            } else {
                layerAdminWait.setVisibility(View.VISIBLE);
                tvTimeout = tvTimeoutAdmin;
                tvSendReq.setText(strSendReq);
                tvWaitAdmin.setText(strWaitAdmin);
            }
            if (trig) {
                mRoundProgressBar1.setProgress(5 * 60 - captchaCount);
                tvTimeout.setText(Integer.valueOf(captchaCount / 5).toString());
                tvTimeout.postDelayed(runnable, 200);
            }
        } else if (state == 0) {
            captchaCount = 0;
            forceStopTimeout();
            if (animDra != null) {
                animDra.stop();
            }

            layerBindError.setVisibility(View.VISIBLE);
            tvErrorResult.setText(errStr);
            if (errStr2 != null && errStr2.length() > 0) {
                tvErrorResult2.setText(errStr2);
            }
            if (errStr3 != null && errStr3.length() > 0) {
                tvErrorResult3.setText(errStr3);
            }
            //set bg gray
            findViewById(R.id.layer_root).setBackgroundColor(getResources().getColor(R.color.fail_bg_color_gray));
            setTintColor(getResources().getColor(R.color.fail_bg_color_gray));

            if (isShowTvBindErrorButton) {
                ibHelpWeb.setVisibility(View.GONE);
                isShowTvBindErrorButton = false;
            }
            //enter bindfail activity
            ibHelpWeb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BindResultActivity.this, ErrorPromptActivity.class);
                    intent.putExtra("deviceType", mDeviceType);
                    intent.putExtra("type", "bindfail");
                    startActivity(intent);
                }
            });
        } else if (state == 3) {
            tvSendAdminOK.setText(mSendAdminOK);
            refreshSendAdminOK();
        }
    }

    private void initReceiver() {
        // TODO Auto-generated method stub
        mBindReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                //eid ok
                LogUtil.d(TAG + "  " + "onReceive action: " + action);
                if (action == Const.ACTION_RECEIVE_REQ_JOIN_WATCH_GROUP_RESP) {
                    String resp;
                    resp = intent.getStringExtra(Const.KEY_JSON_MSG);
                    JSONObject jo = (JSONObject) JSONValue.parse(resp);
                    JSONObject pl = (JSONObject) jo.get(CloudBridgeUtil.KEY_NAME_PL);
                    int rc = CloudBridgeUtil.getCloudMsgRC(jo);
                    if (rc == -156) {
                        if (state == 1) {
                            StringBuffer buff = new StringBuffer();
                            buff.append(getString(R.string.family_admin));
                            if (adminName != null) {
                                buff.append("(");
                                buff.append(adminAccount);
                                buff.append(adminName);
                                buff.append(")");
                            }
                            buff.append(getText(R.string.bind_result_ignore));
                            errStr = buff.toString();
                            state = 0;
                            refereshUIByState();
                        }
                    } else if (rc == 1) {
                        JSONObject repl = (JSONObject) jo.get(CloudBridgeUtil.KEY_NAME_PL);
                        btnOkNext.setText(R.string.getting_watch_info);
                        btnOkNext.setEnabled(false);
                        curWatchEid = (String) repl.get("EID");
                        Log.i("cui","curWatchEid rc= "+curWatchEid);
                        sendGetAllContactReq();
                        changeStateBindOK();
                        queryGroups();

                    } else if (rc == -160) {
                        String msg = (String) jo.get("Msg");
                        if (msg == null) {
                            msg = getString(R.string.not_online);
                        }
                        //管理员
                        errStr = msg;

                        if (msg.contains(getString(R.string.family_admin))) {
                            state = 3;
                            errStr3 = getString(R.string.bind_result_fail_by_admin_offline);
                        } else {
                            state = 0;
                            isShowTvBindErrorButton = true;
                            if ("SW203_A03".equals(mDeviceType)) {
                                errStr3 = getString(R.string.bind_result_fail_by_device_offline_203);
                            } else {
                                errStr3 = getString(R.string.bind_result_fail_by_device_offline);
                            }
                            refereshUIByState();
                        }

                    } else if (rc == -171) {

                        pl = (JSONObject) jo.get(CloudBridgeUtil.KEY_NAME_PL);
                        if (pl != null) {
                            adminTag = (Integer) pl.get("sdt");
                            mDeviceType = (String) pl.get("deviceType");
                            adminName = (String) pl.get("nick");
                            adminPhone = (String) pl.get("phone");
                            adminXiaomiId = (String) pl.get("xmacc");
                            adminRelation = (String) pl.get("cw");

                            String account = (String) pl.get("acct");
                            if (account != null && account.length() > 0) {
                                if (account.equals("facebook")) {
                                    adminAccount = getString(R.string.facebook_nickname);
                                } else if (account.equals("google")) {
                                    adminAccount = getString(R.string.google_nickname);
                                } else if (account.equals("alipay") || account.equals("zfb")) {
                                    adminAccount = getString(R.string.alipay_nickename);
                                } else {
                                    adminAccount = getString(R.string.xiaomi_nickname);
                                }
                            }
                            if (!isWatchAdmin()) {
                                StringBuilder buff = new StringBuilder();
                                buff.append(getText(R.string.guide_req_send_admin));
                                if (adminName != null) {
                                    buff.append("(");
                                    buff.append(adminAccount);
                                    buff.append(adminName);
                                    buff.append(")");
                                }
                                strSendReq = buff.toString();
                                buff.delete(0, buff.length());
                                if (adminName != null) {
                                    buff.append(adminName);
                                } else {
                                    buff.append(getString(R.string.family_admin));
                                }
                                buff.append(getText(R.string.guide_admin_accept));
                                strWaitAdmin = buff.toString();
                                String adminInfo = "";
                                if (adminName != null) {
                                    adminInfo = adminRelation + "(" + adminAccount + adminName + ")";
                                }
                                mSendAdminOK = getString(R.string.send_admin_ok_info, adminInfo);
                                tvSendAdminOK.setText(mSendAdminOK);
                                refreshSendAdminOK();
                                state = 3;
                                return;
                            }
                            if (state == 0) {//已经收到offline，则忽略
                                return;
                            }
                            myApp.getBindRequsetSN().put(jo.get(CloudBridgeUtil.KEY_NAME_SN) + "", adminTag + "");
                            refereshUIByState(true);
                        }
                    }
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RECEIVE_RESPONSE_JOIN_GROUP);
        filter.addAction(Const.ACTION_RECEIVE_REQ_JOIN_WATCH_GROUP_RESP);
        registerReceiver(mBindReceiver, filter);
    }

    private boolean sendReqJoinWatchGroupMsg(String qrstr, String verifyCode) {
        LogUtil.d(TAG + "  " + "sendReqJoinWatchGroupMsg begin watch verifyCode: " + verifyCode);
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(this);

        JSONObject pl = new JSONObject();
        if (qrstr != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_QR_STR, qrstr);
        } else if (verifyCode != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_VERIFY_CODE, verifyCode);
        }
        pl.put(CloudBridgeUtil.KEY_BIND_PUSH_TYPE, 1);
        queryGroupsMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_REQ_JOIN_WATCH_GROUP, pl));
        LogUtil.e("sendReqJoinWatchGroupMsg :  " + queryGroupsMsg.getReqMsg().toJSONString());//增加log追踪验证码无效问题
        return myApp.getNetService() != null && myApp.getNetService().sendNetMsg(queryGroupsMsg);
    }

    private void clearReceiver() {
        try {
            unregisterReceiver(mBindReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private int captchaCount = 60 * 5;//200ms一跳

    private void forceStopTimeout() {
        captchaCount = -1;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // TODO Auto-generated method stub
            if (captchaCount < 0) {

                //stop it
            } else if (captchaCount > 0) {
                captchaCount--;
                mRoundProgressBar1.setProgress(5 * 60 - captchaCount);
                tvTimeout.setText(Integer.valueOf(captchaCount / 5).toString());
                tvTimeout.postDelayed(runnable, 200);
            } else {
                errStr = getText(R.string.bind_result_timeout_end).toString();
                state = 0;
                if (isWatchAdmin()) {
                    if (mDeviceType != null && mDeviceType.length() > 0) {
                        if (mDeviceType.equals("SW502B_A02") || mDeviceType.equals("SW502") || mDeviceType.equals("SW501") || mDeviceType.equals("SW302") || mDeviceType.equals("SW303")
                                ||  mDeviceType.equals("SW502_A03")) {
                            errStr3 = getString(R.string.bind_result_timeout_by_watch, getString(R.string.power_button));
                        } else if ("SW203_A03".equals(mDeviceType)) {
                            errStr3 = getString(R.string.bind_result_timeout_by_device);
                        } else {
                            errStr3 = getString(R.string.bind_result_timeout_by_watch, getString(R.string.voice_button));
                        }
                    } else {
                        errStr3 = getString(R.string.bind_result_timeout_by_watch, getString(R.string.voice_button));
                    }
                }
                refereshUIByState();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("state", state);
        outState.putString("errStr", errStr);
        outState.putString("errStr3", errStr3);
        outState.putString("mSendAdminOK", mSendAdminOK);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animDra != null) {
            animDra.stop();
        }
        clearReceiver();
    }

    @Override
    public void onBackPressed() {
        doPressBack();
    }

    private void doPressBack() {
        if (state == 0) {
            finish();
        } else if (state == 1) {

        }
    }

    @Override
    public void onClick(View v) {
        if (btnErrorNext == v) {
            finish();
        } else if (btnOkNext == v) {
            if (!isQueryGroups) {
                btnOkNext.setEnabled(false);
                queryGroups();
                return;
            }
            Intent intent = new Intent(Const.ACTION_BIND_RESULT_END);
            sendBroadcast(intent);
            if (curWatchEid != null && curWatchEid.length() > 0) {
                myApp.setFocusWatch(myApp.getCurUser().queryWatchDataByEid(curWatchEid));
            }
            if (isWatchAdmin()) {
                boolean isSW105106102 = false;
                WatchData curWatch = myApp.getCurUser().queryWatchDataByEid(curWatchEid);
                String deviceType =curWatch.getDeviceType();
                if(deviceType.equals("SW105") || deviceType.equals("SW106") || deviceType.equals("SW102") || deviceType.equals("SW203")){
                    isSW105106102 = true;
                }

                //数据迁移 目前只支持小寻，米兔暂不支持
                if (!isSW105106102){
                    watchDataList.clear();
                    List<WatchData> list= myApp.getCurUser().getWatchList();
                    int count=list.size();
                    for (int i = 0; i < list.size(); i++) {
                        String eid=list.get(i).getEid();
                        Log.i("cui","for eid="+eid);
                        if(!curWatchEid.equals(eid)){
                            if(myApp.isMeAdmin(list.get(i))){
                                watchDataList.add(list.get(i));
                            }
                        }
                    }
                    if(watchDataList.size()>0){
                        // 检测手表已经绑定了一个设备，并且新绑定的手表是管理员，开始数据迁移 不然就直接走填写资料数据的流程
                        showDialog();
                    }else{
                        Intent intent2 = new Intent(BindResultActivity.this, WatchFirstSetActivity.class);
                        intent2.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);
                        startActivity(intent2);
                        myApp.setAdminBindFlag(true);
                        finish();
                    }
                }else{
                    Intent intent2 = new Intent(BindResultActivity.this, WatchFirstSetActivity.class);
                    intent2.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);
                    startActivity(intent2);
                    myApp.setAdminBindFlag(true);
                    finish();
                }
            } else {
                String memberEid = getMyApp().getCurUser().getEid();
                if (memberEid == null) {
                    memberEid = myApp.getStringValue(Const.SHARE_PREF_FIELD_LOGIN_EID, "");
                    if (memberEid != null)
                        myApp.getCurUser().setEid(memberEid);
                }
                Intent intent2 = new Intent(BindResultActivity.this, AddCallMemberActivity.class);
                intent2.putExtra(Const.KEY_WATCH_ID, curWatchEid);
                intent2.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 0);
                intent2.putExtra("eid", memberEid);
                intent2.putExtra(Const.SET_CONTACT_ISBIND, true);
                startActivity(intent2);
                myApp.setAdminBindFlag(false);
                finish();
            }
        } else if (v == btnSendAdminOK) {
            finish();
        }

    }

    private int sendGetAllContactReq() {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(BindResultActivity.this);
        JSONObject pl = new JSONObject();


        pl.put(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(req);
        }
        return sn;
    }

    private void sendRelationSetMsg() {
        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(BindResultActivity.this);
        //set msg body
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, getMyApp().getCurUser().getCustomData().toJsonStr());

        relationMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));

        myApp.getNetService().sendNetMsg(relationMsg);
    }

    private void changeStateBindOK() {
        state = 2;
        refereshUIByState();
        getMyApp().getCurUser().setRelation(curWatchEid, getString(R.string.default_relation_text));
        sendRelationSetMsg();
    }

    private void queryGroups() {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(BindResultActivity.this);
        JSONObject param = new JSONObject();
        queryGroupsMsg.setReqMsg(myApp.obtainCloudMsgContentWithParam(CloudBridgeUtil.CID_QUERY_MYGROUPS, null, param));
        myApp.getNetService().sendNetMsg(queryGroupsMsg);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        JSONObject pl;
        LogUtil.d(TAG + "  " + "cid: " + cid);

        switch (cid) {
            case CloudBridgeUtil.CID_REQ_JOIN_WATCH_GROUP_RESP:
                LogUtil.d(TAG + " CID_REQ_JOIN_WATCH_GROUP " + "rc: " + rc);
                if (rc == -121) {
                    errStr = getString(R.string.family_count_tips);
                    state = 0;
                    refereshUIByState();
                } else if (rc == -181) {
                    errStr = getString(R.string.bind_result_binded);
                    state = 0;
                    refereshUIByState();
                } else if (rc == -161) {//验证码无效
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    //管理员
                    errStr = getString(R.string.verify_code_not_available);
                    state = 0;
                    refereshUIByState();
                } else if (rc == -160) {//不在线
                    String msg = (String) respMsg.get("Msg");
                    if (msg == null) {
                        msg = getString(R.string.not_online);
                    }
                    //管理员
                    errStr = msg;

                    if (msg.contains(getString(R.string.family_admin))) {
                        state = 3;
                        errStr3 = getString(R.string.bind_result_fail_by_admin_offline);
                    } else {
                        state = 0;
                        errStr3 = getString(R.string.bind_result_fail_by_device_offline);
                        refereshUIByState();
                    }
                } else if (rc == -191) {//设备类型不对
                    String msg = null;
                    try {
                        JSONObject rnObj = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_RN);
                        msg = (String) rnObj.get(CloudBridgeUtil.KEY_NAME_RN_INFO);
                    } catch (Exception e) {

                    }

                    if (msg == null) {
                        msg = getString(R.string.not_support_device);
                    }

                    errStr = msg;
                    state = 0;
                    refereshUIByState();
                } else if (rc == -171) {//已经转发

                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl != null) {
                        adminTag = (Integer) pl.get("sdt");
                        mDeviceType = (String) pl.get("deviceType");
                        adminName = (String) pl.get("nick");
                        adminPhone = (String) pl.get("phone");
                        adminXiaomiId = (String) pl.get("xmacc");
                        adminRelation = (String) pl.get("cw");

                        String account = (String) pl.get("acct");
                        if (account != null && account.length() > 0) {
                            if (account.equals("facebook")) {
                                adminAccount = getString(R.string.facebook_nickname);
                            } else if (account.equals("google")) {
                                adminAccount = getString(R.string.google_nickname);
                            } else if (account.equals("alipay") || account.equals("zfb")) {
                                adminAccount = getString(R.string.alipay_nickename);
                            } else {
                                adminAccount = getString(R.string.xiaomi_nickname);
                            }
                        }
                        if (!isWatchAdmin()) {
                            StringBuilder buff = new StringBuilder();
                            buff.append(getText(R.string.guide_req_send_admin));
                            if (adminName != null) {
                                buff.append("(");
                                buff.append(adminAccount);
                                buff.append(adminName);
                                buff.append(")");
                            }
                            strSendReq = buff.toString();
                            buff.delete(0, buff.length());
                            if (adminName != null) {
                                buff.append(adminName);
                            } else {
                                buff.append(getString(R.string.family_admin));
                            }
                            buff.append(getText(R.string.guide_admin_accept));
                            strWaitAdmin = buff.toString();
                            String adminInfo = "";
                            if (adminName != null) {
                                adminInfo = adminRelation + "(" + adminAccount + adminName + ")";
                            }
                            mSendAdminOK = getString(R.string.send_admin_ok_info, adminInfo);
                            tvSendAdminOK.setText(mSendAdminOK);
                            refreshSendAdminOK();
                            state = 3;
                            return;
                        }
                        if (state == 0) {//已经收到offline，则忽略
                            return;
                        }
                        myApp.getBindRequsetSN().put(respMsg.get(CloudBridgeUtil.KEY_NAME_SN) + "", adminTag + "");
                        refereshUIByState(true);
                    }

                } else if (rc == CloudBridgeUtil.RC_TIMEOUT
                        || rc == CloudBridgeUtil.RC_NETERROR
                        || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY
                        || rc == CloudBridgeUtil.RC_NOT_LOGIN) {
                    state = 0;
                    errStr = getText(R.string.net_check_alert).toString();
                    refereshUIByState();
                } else if (rc == -12) {
                    errStr = getText(R.string.bind_result_wrong).toString();
                    state = 0;
                    refereshUIByState();
                } else if (rc == -151) {
                    errStr = getString(R.string.cannot_find_watch_info);
                    state = 0;
                    refereshUIByState();
                }
                break;
            case CloudBridgeUtil.CID_GET_CONTACT_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单
                    JSONArray obj = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_CONTACT_SYNC_ARRAY);
                    String sTmp = obj.toString();
                    ArrayList<PhoneNumber> mBindWhiteList;
                    mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                    //持久化
                    myApp.setValue(curWatchEid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));
                } else {

                }
                break;
            case CloudBridgeUtil.CID_QUERY_MYGROUPS_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    myApp.parseJSONObjectGroups(CloudBridgeUtil.getCloudMsgPLArray(respMsg));
                    //添加成功
                    myApp.setIsNeedInvalidFamilyDialog(false);
                    isQueryGroups = true;
                    btnOkNext.setText(R.string.security_finish_next);
                    // 设置通话白名单默认关闭  （理论来说，该逻辑与绑定无关，只是因为早期通话白名单默认为开启）  条件：初次绑定、拿到设备eid和gid
                    if (isWatchAdmin()) {
                        WatchData curWatch = myApp.getCurUser().queryWatchDataByEid(curWatchEid);
                        if (curWatch.isDevice105()) {
                            mapsetWhiteNumberOnOff(curWatch.getEid(), curWatch.getFamilyId());
                        }
                        if (curWatch.isDevice709_H01()|| curWatch.isDevice707_H01()){
                            mapsetWhiteNumberOn(curWatch.getEid(), curWatch.getFamilyId());
                        }
                        if (curWatch.isDevice102() || curWatch.isDevice105() || curWatch.isDevice106()
                                || curWatch.isDevice302() || curWatch.isDevice303() || curWatch.isDevice303_A02()
                                || curWatch.isDevice305() || curWatch.isDevice501() || curWatch.isDevice730() || curWatch.isDevice705()
                                || curWatch.isDevice703() || curWatch.isDevice307()) {
                            mapsetSleepTime(curWatch.getEid(), curWatch.getFamilyId());
                        }

                        if (curWatch.isBindSetMode()) {
                            mapsetOperationMode(curWatch.getEid(), curWatch.getFamilyId());
                        }

                        //绑定成功后拉取一下icon配置表
                        AppStoreUtils.getInstance(this).getPackageAndIconTable(myApp);
                    }
                    myApp.setValue(Const.SHARE_PREF_CURRENT_USER_REFLECT_ID, myApp.getCurUser().getUid());
                    myApp.initMapType();
                    myApp.getNetService().getNoticeSetting(null);
                } else {
                    btnOkNext.setText(R.string.get_group_info_failed);
                }
                btnOkNext.setEnabled(true);
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject requestPL = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (requestPL.containsKey(CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST))
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_FIELD_WHITE_LIST, "0");
                    else if (requestPL.containsKey(CloudBridgeUtil.SLEEP_LIST)) {
                        JSONArray plA = new JSONArray();
                        JSONObject jsonObj = (JSONObject) JSONValue.parse((String) requestPL.get(CloudBridgeUtil.SLEEP_LIST));
                        plA.add(jsonObj);
                        myApp.setValue(myApp.getCurUser().getFocusWatch().getEid() + Const.SHARE_PREF_SLEEP_MODE_KEY, plA.toJSONString());
                    }
                }
                break;
            case CloudBridgeUtil.CID_MIGRATION_RESP:
                int Rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (Rc == CloudBridgeUtil.RC_SUCCESS) {
                    //Log.i("cui","rc = "+rc);
                    Toast.makeText(BindResultActivity.this, getString(R.string.migration_success) , Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    queryGroups();
                    Intent it = new Intent(Const.ACTION_BIND_NEW_WATCH);//刷新联系
                    sendBroadcast(it);
                    //sendBroadcast(new Intent(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW));//刷新家庭管理
                    //拉数据 联系人，群组，电子围栏信息
                    Intent intent = new Intent(BindResultActivity.this, NewMainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(Rc == -15){
                    Toast.makeText(BindResultActivity.this, getString(R.string.permission_inadequacy) , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //绑定时设置通话白名单为关闭状态
    private void mapsetWhiteNumberOnOff(String eid, String familyid) {
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapSetMsg(eid, familyid,
                    CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST, "0", BindResultActivity.this);
        }
    }
    //绑定时设置通话白名单为开启状态
    private void mapsetWhiteNumberOn(String eid, String familyid) {
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapSetMsg(eid, familyid,
                    CloudBridgeUtil.KEY_NAME_DEVICE_WHITE_LIST, "1", BindResultActivity.this);
        }
    }

    //绑定时设置休眠时段
    private void mapsetSleepTime(String eid, String familyid) {
        if (myApp.getNetService() != null) {
            /*默认关机*/
            SleepTime sleepTime = new SleepTime("21", "00", "07", "00", "1", "1", TimeUtil.getTimeStampLocal());
            JSONObject plO = new JSONObject();
            plO = SleepTime.toJsonObjectFromSleepTimeBean(plO, sleepTime);
            myApp.getNetService().sendMapSetMsg(eid, familyid,
                    CloudBridgeUtil.SLEEP_LIST, plO.toString(), BindResultActivity.this);
        }
    }

    private void mapsetOperationMode(String eid, String familyid) {
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendMapSetMsg(eid, familyid, CloudBridgeUtil.OPERATION_MODE_VALUE, Integer.toString(4), BindResultActivity.this);
        }
    }

    MigrationDialog dialog;
    private void showDialog(){
         dialog =new MigrationDialog(myApp,BindResultActivity.this,watchDataList,curWatchEid, new CallBack.ReturnCallback<String>() {
            @Override
            public void back(String obj) {
                if(obj.equals("false")){
                    Intent intent2 = new Intent(BindResultActivity.this, WatchFirstSetActivity.class);
                    intent2.putExtra(CloudBridgeUtil.KEY_NAME_EID, curWatchEid);
                    startActivity(intent2);
                    myApp.setAdminBindFlag(true);
                    dialog.dismiss();
                    finish();
                }else{
                    //Toast.makeText(BindResultActivity.this, obj , Toast.LENGTH_SHORT).show();
                    sendMapSetMsg(obj);
                }
            }
        });
        Window win = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.onWindowAttributesChanged(lp);
        dialog.show();
    }

    public void sendMapSetMsg(String oldeid) {
        MyMsgData migrationData = new MyMsgData();
        migrationData.setCallback(this);
        JSONObject pl = new JSONObject();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put("sourceEid", oldeid);
        pl.put("targetEid", curWatchEid);
        migrationData.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MIGRATION, sn, myApp.getToken(), pl));
        myApp.getNetService().sendNetMsg(migrationData);
    }
}
