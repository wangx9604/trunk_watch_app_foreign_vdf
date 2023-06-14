package com.xiaoxun.xun.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

/**
 * Created by guxiaolong on 2017/6/9.
 */

public class AddNewMemberActivity extends NormalActivity implements View.OnClickListener {

    private ImageButton mBackBtn;
    private TextView mTitleText;
    private String mWatchEid;
    private WatchData mCurWatch;
    private int mCallMemberCount;
    private int mWatchFriendCount;

    private TextView tvAddMemberDesc;
    private RelativeLayout mScanCodeLayout;
    private TextView mScanCodeTitle;
    private RelativeLayout mWechatLayout;
    private TextView mWechatTitle;
    private RelativeLayout mSmsLayout;
    private TextView mSmsTitle;
    private RelativeLayout mAddCallMemeberLayout;
    private TextView mAddCallMemberTitle;
    private View mCallMemberLayout;

    private View mViewAddFriend;
    private RelativeLayout mLayoutAddFriend;

    private View mAddFriendCover;
    private View mWechatCover;
    private View mSmsCover;
    private View mAddCallMemberCover;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        mWatchEid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        mCallMemberCount = getIntent().getIntExtra("call_member_count", 0);
        mWatchFriendCount = getIntent().getIntExtra("watch_friend_count", 0);
        mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchEid);

        initView();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_ADD_CALLMEMBER_OK)){
                    finish();
                } else if(action.equals(Const.ACTION_REQUEST_ADDFRIEND_OK)){
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ADD_CALLMEMBER_OK);
        filter.addAction(Const.ACTION_REQUEST_ADDFRIEND_OK);
        registerReceiver(mReceiver, filter);
    }

    private void initView() {

        mBackBtn = findViewById(R.id.iv_title_back);
        mBackBtn.setOnClickListener(this);

        mTitleText = findViewById(R.id.tv_title);
        mTitleText.setText(R.string.add_new_member);

        tvAddMemberDesc = findViewById(R.id.tv_add_member_desc);
        tvAddMemberDesc.setText(R.string.group_member_info);
        if (mCurWatch.isDevice710() && !mCurWatch.isDevice730() || mCurWatch.isDevice705() || mCurWatch.isDevice703()|| mCurWatch.isDevice707_H01()|| mCurWatch.isDevice709_H01())
            tvAddMemberDesc.setText(R.string.group_member_info_710);

        mScanCodeLayout = findViewById(R.id.layout_scan_code);
        mScanCodeLayout.setOnClickListener(this);

        mWechatLayout = findViewById(R.id.layout_wechat);
        mWechatLayout.setOnClickListener(this);
        mWechatTitle = findViewById(R.id.tv_wechat);

        mSmsLayout = findViewById(R.id.layout_sms);
        mSmsLayout.setOnClickListener(this);
        mSmsTitle = findViewById(R.id.tv_sms);

        mAddCallMemeberLayout = findViewById(R.id.layout_add_call_member);
        mAddCallMemeberLayout.setOnClickListener(this);
        mAddCallMemberTitle = findViewById(R.id.tv_add_call_member);
        mCallMemberLayout = findViewById(R.id.layout_call_member);

        mViewAddFriend = findViewById(R.id.layout_add_friend);
        mLayoutAddFriend = findViewById(R.id.layout_add_watch_friend);
        mLayoutAddFriend.setOnClickListener(this);

        mWechatCover = findViewById(R.id.cover_wechat);
        mSmsCover = findViewById(R.id.cover_sms);
        mAddCallMemberCover = findViewById(R.id.cover_add_call_member);
        mAddFriendCover = findViewById(R.id.cover_add_watch_friend);

        if (myApp.getCurUser().isMeAdminByWatch(mCurWatch)) {
            mWechatTitle.setText(R.string.wechat_invite_member);
            mSmsTitle.setText(R.string.sms_invite_member);
            mAddCallMemberTitle.setText(R.string.add_new_member);
            mWechatCover.setVisibility(View.GONE);
            mSmsCover.setVisibility(View.GONE);
            mAddCallMemberCover.setVisibility(View.GONE);
            mAddFriendCover.setVisibility(View.GONE);
            mWechatLayout.setClickable(true);
            mSmsLayout.setClickable(true);
            mAddCallMemeberLayout.setClickable(true);
            mLayoutAddFriend.setClickable(true);
        } else {
            mWechatTitle.setText(getString(R.string.wechat_invite_member) +"("+ getString(R.string.need_admin_add) +")");
            mSmsTitle.setText(getString(R.string.sms_invite_member) +"("+ getString(R.string.need_admin_add)+")");
            mAddCallMemberTitle.setText(getString(R.string.add_new_member) +"("+ getString(R.string.need_admin_add)+")");
            mWechatCover.setVisibility(View.VISIBLE);
            mSmsCover.setVisibility(View.VISIBLE);
            mAddCallMemberCover.setVisibility(View.VISIBLE);
            mAddFriendCover.setVisibility(View.VISIBLE);
            mWechatLayout.setClickable(false);
            mSmsLayout.setClickable(false);
            mAddCallMemeberLayout.setClickable(false);
            mLayoutAddFriend.setClickable(false);
        }

        if (mCurWatch.isSupportWatchFriend()) {
            mViewAddFriend.setVisibility(View.VISIBLE);
        } else {
            mViewAddFriend.setVisibility(View.GONE);
        }

        if (mCurWatch.isSupportPhoneCall()) {
            mCallMemberLayout.setVisibility(View.VISIBLE);
        } else {
            mCallMemberLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mScanCodeLayout) {
            int familyCount = myApp.getCurUser().queryFamilyByGid(mCurWatch.getFamilyId()).getMemberList().size();
            if (familyCount >= 10) {
                ToastUtil.show(AddNewMemberActivity.this, getString(R.string.family_count_tips));
                return;
            }
            Intent intent = new Intent(AddNewMemberActivity.this, DeviceQrActivity.class);
            intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
            startActivity(intent);
        } else if (v == mWechatLayout) {

        } else if (v == mSmsLayout) {

        } else if (v == mAddCallMemeberLayout) {
            int numLimit = Const.CONTACT_MAX_LIMIT;
            if (mCurWatch.isDevice102()) {
                numLimit = Const.CONTACT_MAX_LIMIT;
            } else if (mCurWatch.isDevice106()) {
                numLimit = Const.CONTACT_MAX_LIMIT_106;
            } else if (mCurWatch.isDevice105() && !myApp.isControledByVersion(mCurWatch, true, "T10")) {
                numLimit = Const.CONTACT_MAX_LIMIT;
            } else {
                numLimit = Const.CONTACT_NUM_MAX_LIMIT;
            }
            if (mCallMemberCount >= numLimit) {
                ToastUtil.showMyToast(this,
                        getText(R.string.max_contact_prompt_msg).toString(),
                        Toast.LENGTH_SHORT);
            } else {
                Intent intent = new Intent(AddNewMemberActivity.this, AddCallMemberActivity.class);
                intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 1);
                intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
                startActivity(intent);
            }
        } else if (v==mLayoutAddFriend) {
            if (mWatchFriendCount >= 10)
                ToastUtil.showMyToast(this,
                        getText(R.string.max_watch_friend_prompt_msg).toString(),
                        Toast.LENGTH_SHORT);
            else
                startActivity(new Intent(AddNewMemberActivity.this, AddNewFriendActivity.class));
        }else if (v == mBackBtn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
