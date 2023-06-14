package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.AllMessageAdapter;
import com.xiaoxun.xun.adapter.MsgListAdapter;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.NoticeMsgHisDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by guxiaolong on 2016/12/17.
 */

public class NoticeTypeActivity extends NormalActivity {
    private RecyclerView mRvRecycler;
    private Group mNoMsgLy;
    private Button mSendSmsBtn;
    private ImageButton mBackBtn;
    private ImageView mWathcHead;
    private TextView mWatchName;
    private MsgListAdapter mMsgListAdapter;
    private MsgReceiver mMsgReceiver;
    private ArrayList<NoticeMsgData> mNoticeMsgDatas = new ArrayList<NoticeMsgData>();
    private AsyncTask<String, Void, ArrayList<NoticeMsgData>> mLoadNoticeMsgTask;
    private WatchData mCurWatch;
    private String mWatchEid;
    private ImageButton mNoticeSetting;

    private int mNoticeType;
    private int mNoticeMsgType;
    private String mTitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_type);
        mWatchEid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchEid);
        mNoticeType = getIntent().getIntExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_ALL);
        if (mCurWatch == null) {
            finish();
            return;
        }
        noticeTypeToNoticeMsgType();
        initViews();
        initData();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWatchEid = intent.getStringExtra(Const.KEY_WATCH_ID);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchEid);
        if (mCurWatch == null) {
            finish();
            return;
        }
        initViews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApp.setHasNewNoticeMsg(mCurWatch.getFamilyId(), mNoticeType, false);
        sendBroadcast(new Intent(Constants.ACTION_UPDATE_NEW_MESSAGE_NOTICE));
        NotificationManager notifyMng = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notifyMng.cancel(mCurWatch.getFamilyId(), Const.TITLE_BAR_NEW_NOTICE_MESSAGE + mNoticeType);
        myApp.setNoticeMsgOpenGid(mCurWatch.getFamilyId(), mNoticeType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.setNoticeMsgOpenGid(null, mNoticeType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMsgReceiver != null) {
            mMsgReceiver.unregisterReceiver(this);
        }
        if (mLoadNoticeMsgTask != null) {
            mLoadNoticeMsgTask.cancel(true);
            mLoadNoticeMsgTask = null;
        }
    }

    private void initViews() {
        mBackBtn = (ImageButton) findViewById(R.id.iv_title_back);
        mWathcHead = (ImageView) findViewById(R.id.iv_watch_head);
        mWatchName = (TextView) findViewById(R.id.tv_watch_name);
        mRvRecycler = (RecyclerView) findViewById(R.id.other_msg_list_recycler);
        mNoMsgLy = (Group) findViewById(R.id.group_no_notice);
        mSendSmsBtn = (Button) findViewById(R.id.send_sms_btn);
        mSendSmsBtn.setVisibility(View.GONE);

        findViewById(R.id.group_sms).setVisibility(View.GONE);

        mNoticeSetting = (ImageButton) findViewById(R.id.iv_notice_setting);

        mNoticeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeTypeActivity.this, NoticeManagerActivity.class);
                intent.putExtra(Const.KEY_WATCH_ID, mWatchEid);
                NoticeTypeActivity.this.startActivity(intent);
            }
        });
    }

    private void initData() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mWatchName.setText(mTitleName);
        ImageUtil.setMaskImage(mWathcHead, R.drawable.head_1, myApp.getHeadDrawableByFile(myApp.getResources(),
                mCurWatch.getHeadPath(), mWatchEid, R.drawable.small_default_head));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRecycler.setLayoutManager(llm);

        mMsgListAdapter = new MsgListAdapter(this, mNoticeMsgDatas);
        mRvRecycler.setAdapter(mMsgListAdapter);
        getNoticeMsgFromDB();
        mMsgReceiver = new MsgReceiver();
        mMsgReceiver.registerReceiver(this);
        mSendSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textStr1 = "<font color=\"#a3a3a3\">" + getString(R.string.req_telcost_prompt_msg) + "</font>";
                String smsNumber = myApp.getStringValue(mWatchEid + Const.SHARE_PREF_SMS_NUMBER, "101");
                Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithNotice(NoticeTypeActivity.this, 20, 0,
                        getString(R.string.req_telcost),
                        Html.fromHtml(textStr1),
                        smsNumber, getString(R.string.input_sms_command_hint),
                        new CustomSelectDialogUtil.CustomDialogListener() {

                            @Override
                            public void onClick(View v, String text) {
                                // TODO Auto-generated method stub
                            }
                        },
                        getText(R.string.cancel).toString(),
                        new CustomSelectDialogUtil.CustomDialogListener() {

                            @Override
                            public void onClick(View v, String text) {
                                if (text != null && text.length() > 0) {
                                    myApp.setValue(mWatchEid + Const.SHARE_PREF_SMS_NUMBER, text);
                                    sendSMSSearch(text);
                                    mSendSmsBtn.setBackgroundResource(R.drawable.speak_1);
                                    mSendSmsBtn.setClickable(false);
                                    mSendSmsBtn.setText(getString(R.string.send_sms_command));
                                } else {
                                    ToastUtil.show(NoticeTypeActivity.this, getString(R.string.command_cannot_be_null));
                                }
                            }
                        },
                        getText(R.string.confirm).toString());
                dlg.show();
            }
        });
    }

    private void noticeTypeToNoticeMsgType() {
        switch (mNoticeType) {
            case AllMessageAdapter.MESSAGE_TYPE_LOCATION:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_SOS_LOCATION;
                mTitleName = getString(R.string.location_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_BATTERY:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_BATTERY_WARNNING;
                mTitleName = getString(R.string.battery_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_FAMILY_MEMBER:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_FAMILY_CHANGE;
                mTitleName = getString(R.string.family_member_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_STEPS:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_STAEPS;
                mTitleName = getString(R.string.steps_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_SMS:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_SMS;
                mTitleName = getString(R.string.sms_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_DOWNLOAD:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_DOWNLOAD;
                mTitleName = getString(R.string.download_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_ALL:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_ALL;
                mTitleName = getString(R.string.notice_message, mCurWatch.getNickname());
                break;
            case AllMessageAdapter.MESSAGE_TYPE_SYSTEM:
                mNoticeMsgType = NoticeMsgData.MSG_TYPE_SYSTEM;
                mTitleName = getString(R.string.notice_message, mCurWatch.getNickname());
                break;
            default:
                break;
        }
    }

    private MsgCallback mMsgCallback = new MsgCallback() {
        @Override
        public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
            int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
            String[] teid = (String[]) reqMsg.get(CloudBridgeUtil.KEY_NAME_TEID);
            int rc;
            switch (cid) {
                case CloudBridgeUtil.CID_E2E_DOWN:
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    rc = CloudBridgeUtil.getCloudMsgRC(pl);
                    if (pl == null) {
                        int tmpRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                        if (tmpRc == CloudBridgeUtil.RC_NETERROR) {
                            ToastUtil.showMyToast(NoticeTypeActivity.this, getText(R.string.network_error_prompt).toString(), Toast.LENGTH_SHORT);
                        } else if (tmpRc == -160) {//手表不在线
                            ToastUtil.showMyToast(NoticeTypeActivity.this, getText(R.string.watch_offline).toString(), Toast.LENGTH_SHORT);
                        } else if (tmpRc != -160 && tmpRc < 0
                                && tmpRc != CloudBridgeUtil.RC_TIMEOUT) {//网络不好,有时候设备关机服务器没有返回值,所以也存在设备不在线的可能
                            ToastUtil.showMyToast(NoticeTypeActivity.this, getText(R.string.watch_state_unknown).toString(), Toast.LENGTH_SHORT);
                        }
                    } else {
                        if (rc == CloudBridgeUtil.RC_SUCCESS || rc == CloudBridgeUtil.RC_HALF_SUCCESS) {
                            int value = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                            if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SMS_SEARCH == value) {
                                ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.sms_command_send_success), Toast.LENGTH_SHORT);
                            }
                        } else if (rc < 0) {
                            int value = (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION);
                            if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SMS_SEARCH == value) {
                                String plmn = (String) pl.get("plmn");
                                myApp.sdcardLog("sms search device plmn: " + plmn);
                                if (rc == -1) {
                                    ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.sms_command_format_wrong), Toast.LENGTH_SHORT);
                                } else if (rc == -2) {
                                    ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.no_matched_plmn), Toast.LENGTH_SHORT);
                                } else if (rc == -3) {
                                    ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.watch_register_network_error), Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.enquiry_fee_fail) + " error:" + rc, Toast.LENGTH_SHORT);
                                }
                            } else {
                                ToastUtil.showMyToast(NoticeTypeActivity.this, getString(R.string.request_fail) + " error：" + rc, Toast.LENGTH_SHORT);
                            }
                        }
                    }
                    mSendSmsBtn.setBackgroundResource(R.drawable.btn_recording_selector);
                    mSendSmsBtn.setClickable(true);
                    mSendSmsBtn.setText(getString(R.string.calls_inquiry));
                    break;
                default:
                    break;
            }
        }
    };

    private void sendSMSSearch(String smsContent) {
        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(mMsgCallback);
        JSONObject pl = new JSONObject();
        String[] eid = new String[1];
        eid[0] = mWatchEid;
        JSONArray simarray = new JSONArray();
        JSONObject smsPL = new JSONObject();
        JSONObject smsPL1 = new JSONObject();
        JSONObject smsPL2 = new JSONObject();
        smsPL.put(CloudBridgeUtil.KEY_NAME_MCCMNC, "46000,46002,46007");
        smsPL.put(CloudBridgeUtil.KEY_NAME_NUMBER, "10086");
        smsPL.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, smsContent);
        simarray.add(smsPL);

        smsPL1.put(CloudBridgeUtil.KEY_NAME_MCCMNC, "46001,46006,46009");
        smsPL1.put(CloudBridgeUtil.KEY_NAME_NUMBER, "10010");
        smsPL1.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, smsContent);
        simarray.add(smsPL1);

        smsPL2.put(CloudBridgeUtil.KEY_NAME_MCCMNC, "46003,46005");
        smsPL2.put(CloudBridgeUtil.KEY_NAME_NUMBER, "10001");
        smsPL2.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, smsContent);
        simarray.add(smsPL2);

        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SMS_SEARCH);
        pl.put(CloudBridgeUtil.KEY_NAME_SIM_ARRAY, simarray);
        pl.put(CloudBridgeUtil.KEY_NAME_SOURCE_TYPE, 0);

        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2e.setReqMsg(CloudBridgeUtil.CloudE2eMsgContent(
                CloudBridgeUtil.CID_E2E_UP, sn, myApp.getToken(), null, eid, pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(e2e);
        }
    }

    private class MsgReceiver extends BroadcastReceiver {

        // 注册监听
        public void registerReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_RECEIVE_NOTICE_MSG);
            filter.addAction(Const.ACTION_RECEIVE_GET_DEVICE_INFO);
            filter.addAction(Const.ACTION_DOWNLOAD_HEADIMG_OK);
            filter.addAction(Const.ACTION_CLEAR_NOTICE_MESSAGE);
            context.registerReceiver(this, filter);
        }

        // 关闭监听
        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听添加和删除事件
            String action = intent.getAction();
            if (action.equals(Const.ACTION_RECEIVE_NOTICE_MSG)) {
                mNoMsgLy.setVisibility(View.GONE);
                mRvRecycler.setVisibility(View.VISIBLE);

                myApp.setHasNewNoticeMsg(mCurWatch.getFamilyId(), mNoticeType, false);
                getNoticeMsgFromDB();
            } else if (action.equals(Const.ACTION_RECEIVE_GET_DEVICE_INFO)) {
                noticeTypeToNoticeMsgType();
                mWatchName.setText(mTitleName);
            } else if (action.equals(Const.ACTION_DOWNLOAD_HEADIMG_OK)) {
                ImageUtil.setMaskImage(mWathcHead, R.drawable.head_1, myApp.getHeadDrawableByFile(myApp.getResources(),
                        mCurWatch.getHeadPath(), mWatchEid, R.drawable.small_default_head));
            } else if (action.equals(Const.ACTION_CLEAR_NOTICE_MESSAGE)) {
                getNoticeMsgFromDB();
            }
        }
    }

    private void getNoticeMsgFromDB() {
        if (mLoadNoticeMsgTask != null) {
            mLoadNoticeMsgTask.cancel(true);
            mLoadNoticeMsgTask = null;
        }

        mLoadNoticeMsgTask = new AsyncTask<String, Void, ArrayList<NoticeMsgData>>() {
            @Override
            protected ArrayList<NoticeMsgData> doInBackground(String... strings) {
                String gid = strings[0];
                ArrayList<NoticeMsgData> noticeList = new ArrayList<NoticeMsgData>();
                NoticeMsgHisDAO.getInstance(myApp.getApplicationContext()).readMsgForFamilyByType(gid, myApp.getCurUser().getEid(), noticeList, mNoticeMsgType);
                return noticeList;
            }

            @Override
            protected void onPostExecute(ArrayList<NoticeMsgData> noticeList) {
                super.onPostExecute(noticeList);
                mNoticeMsgDatas.clear();
                mNoticeMsgDatas.addAll(noticeList);
                mMsgListAdapter.notifyDataSetChanged();
                mRvRecycler.scrollToPosition(mMsgListAdapter.getItemCount() - 1);
                if (mNoticeMsgDatas.size() == 0) {
                    mNoMsgLy.setVisibility(View.VISIBLE);
                } else {
                    mNoMsgLy.setVisibility(View.GONE);
                }
            }
        }.execute(mCurWatch.getFamilyId());

    }
}
