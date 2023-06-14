/**
 * Creation Date:2015-1-28
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.FamilyMemberAdapter;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.GeneralMember;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchAllMemberData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil.AdapterItemClickListener;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.DialogUtil.OnCustomDialogListener;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-28
 */
public class FamilyMemberActivity extends NormalActivity implements OnClickListener, MsgCallback {

    FamilyData mFamily;
    ListView mMemberListView;
    private ImageButton mBtnBack;
    public ImibabyApp mApp;
    private FamilyMemberAdapter mAdapter;
    private BroadcastReceiver mMsgReceiver = null;
    private View btnAddOne;
    private View btnAddMember;
    private View btnAddInvite;
    private ImageButton mBtnHelpWeb;
    private TextView mFamilyMemberTips;

    private ArrayList<PhoneNumber> mBindWhiteList;
    private ArrayList<GeneralMember> mGeneralMemberList;
    private WatchAllMemberData allData = new WatchAllMemberData();
    private WatchData mCurWatch;
    private LoadingDialog loadingdlg;

    private String newFriendName = null;
    private String modifyFriendNameContactId = null;

    private int callmembernum = 0;//纯通话成员数量
    private int watchFriendNum = 0;//手表好友数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
        mApp = (ImibabyApp) getApplication();
        mBindWhiteList = new ArrayList<>();
        mGeneralMemberList = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Const.KEY_FAMILY_ID)) {
                String familyId = getIntent().getExtras().getString(Const.KEY_FAMILY_ID);
                if (mApp.getCurUser().getFamilyList() !=  null && mApp.getCurUser().getFamilyList().size() > 0) {
                    for (FamilyData cur : mApp.getCurUser().getFamilyList()) {
                        if (cur.getFamilyId().equals(familyId)) {
                            mFamily = cur;
                            break;
                        }
                    }
                } else {
                    finish();
                    return;
                }
            }
            //不显示设备
            if (mFamily != null) {
                mCurWatch = mFamily.getWatchlist().get(0);
                doGetContactsFromServer();//区分手表类型，采用不同接口同步联系人
                initViews();
            } else {
                finish();
            }
        } else {
            finish();
        }
        initReceivers();
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, new LoadingDialog.OnConfirmClickListener() {
            @Override
            public void confirmClick() {

            }
        });
        loadingdlg.hideReloadView();

    }

    void loadAllMemberData() {
        allData.mWatch = mCurWatch;
        //load allmemberlist
        mGeneralMemberList.clear();
        callmembernum = 0;

        //加入 群组成员，同时标志出自己,设置可编辑
        //交换 群组成员管理员到位置0 ，设置管理员节点title文字，第一非管理员节点设置title
        String myEid = myApp.getCurUser().getEid();
        String adminEid = mFamily.getAdminEId();
        boolean isMeAdmin = false;
        if (myEid.equals(adminEid)) {
            isMeAdmin = true;
        }

        //增加一个临时排序用的list；
        ArrayList<MemberUserData> tmpMemberList = new ArrayList<MemberUserData>(mFamily.getMemberList());

        int gourpPos = 0;

        //按照通话成员的顺序，添加群组成员
        for (PhoneNumber phoneNumber : mBindWhiteList) {
            MemberUserData data = getWhiteMember(phoneNumber, mFamily.getMemberList());
            if (data != null) {
                GeneralMember member = new GeneralMember();
                member.eid = phoneNumber.userEid;
                member.gid = phoneNumber.userGid;
                member.contactId = phoneNumber.id;
                member.cellnum = phoneNumber.number;
                member.subnum = phoneNumber.subNumber;
                member.nickname = phoneNumber.nickname;
                member.ring = phoneNumber.ring;
                member.avatar = phoneNumber.avatar;
                member.attri = phoneNumber.attri;
                member.type = 0;//群组成员
                member.weight = phoneNumber.weight;
                setPhoneNumberNicknameAndHead(member, phoneNumber);
                if (data.getHeadPath() != null && data.getHeadPath().length() >= 32) {
                    member.headpath = data.getHeadPath();
                }
                if (member.eid.equals(adminEid)) {
                    member.isAdmin = true;
                }
                if (isMeAdmin) {
                    member.isEdit = true;
                    if (member.eid.equals(myEid)) {
                        member.clickDialogType = 0;
                    } else {
                        member.clickDialogType = 2;
                    }
                } else if (member.eid.equals(myEid)) {
                    member.isEdit = true;
                }

                if (gourpPos == 0) {
                    member.titleStr = getText(R.string.primary_contact).toString();
                } else if (gourpPos == 1) {
                    member.titleStr = getText(R.string.base_contact).toString();
                }
                mGeneralMemberList.add(member);
                gourpPos++;
                tmpMemberList.remove(data);
            } else {
                GeneralMember member = new GeneralMember();
//                member.eid = phoneNumber.userEid;
                member.gid = phoneNumber.userGid;
                member.contactId = phoneNumber.id;
                member.cellnum = phoneNumber.number;
                member.subnum = phoneNumber.subNumber;
                member.ring = phoneNumber.ring;
                member.attri = phoneNumber.attri;
                member.avatar = phoneNumber.avatar;
                member.type = phoneNumber.contactType;
                member.weight = phoneNumber.weight;
                member.nickname = phoneNumber.nickname;
                setPhoneNumberNicknameAndHead(member, phoneNumber);
                if (phoneNumber.contactType == 2) {
                    member.type = 2;
                    member.eid = phoneNumber.userEid;
                } else {
                    member.type = 1;
                }
                if (isMeAdmin) {
                    member.isEdit = true;
                    if (phoneNumber.contactType == 2) {
                        member.clickDialogType = 5;
                    } else {
                        member.clickDialogType = 1;
                    }
                }

                if (gourpPos == 0) {
                    member.titleStr = getText(R.string.primary_contact).toString();
                } else if (gourpPos == 1) {
                    member.titleStr = getText(R.string.base_contact).toString();
                }
                mGeneralMemberList.add(member);
                gourpPos++;
                if (member.type == 1) {
                    callmembernum++;
                } else if (member.type == 2) {
                    watchFriendNum++;
                }
            }
        }

        // 处理重复联系人（同一个群组成员对应多个以上通话成员，这是异常情况，这里做兼容该异常处理）
        for (GeneralMember member : mGeneralMemberList) {
            if (member.eid == null) continue;
            long contactId = Long.parseLong(member.contactId);
            for (GeneralMember submember : mGeneralMemberList) {
                if (submember.eid != null && submember.eid.equals(member.eid)) {
                    long subContactId = Long.parseLong(submember.contactId);
                    if (contactId < subContactId) {
                        member.type = 1;//通话成员
                        member.eid = null;
                        if (isMeAdmin){
                            member.isAdmin = false;
                            member.clickDialogType = 1;
                        }
                        else
                            member.isEdit = false;
                    }
                }
            }
        }

        //添加没有号码的群组成员
        for (MemberUserData data : tmpMemberList) {
            GeneralMember member = new GeneralMember();
            member.eid = data.getEid();
            if (data.getHeadPath() != null && data.getHeadPath().length() >= 32) {
                member.headpath = data.getHeadPath();
            }
            String nickname;
            if (data.getRelation(mCurWatch.getEid()) != null) {
                nickname = data.getRelation(mCurWatch.getEid());
            } else {
                nickname = data.getNickname();
            }
            member.nickname = nickname;
            member.type = 0;//群组成员
            if (member.eid.equals(adminEid)) {
                member.isAdmin = true;
            }
            if (isMeAdmin) {
                member.isEdit = true;
                if (member.eid.equals(myEid)) {
                    member.clickDialogType = 0;
                } else {
                    member.clickDialogType = 2;
                }
            } else if (member.eid.equals(myEid)) {
                member.isEdit = true;
            }

            if (gourpPos == 0) {
                member.titleStr = getText(R.string.base_contact).toString();
            } else if (gourpPos == 1) {
                member.titleStr = getText(R.string.base_contact).toString();
            }
            mGeneralMemberList.add(member);
            gourpPos++;
        }
        //设置首要联系人
        if (mGeneralMemberList.size() > 0) {
            if (mGeneralMemberList.get(0).cellnum != null && mGeneralMemberList.get(0).cellnum.length() > 0) {//第一个联系人是有号码则是首要联系人
                mGeneralMemberList.get(0).isPrimaryContact = true;
            }
        }
        allData.mGeneralMemberList = mGeneralMemberList;
    }

    private void setPhoneNumberNicknameAndHead(GeneralMember member, PhoneNumber phoneNumber) {
        //如果自定义头像，则不用设置，如果不是，则根据attri设置
//        if (member.headpath != null && member.headpath.length() >= 32) {//md5值
//
//        } else {
//            if (phoneNumber.attri < 24) {//没有使用自定义头像,则根据attri设置头像
//                member.headpath = Integer.valueOf(phoneNumber.attri).toString();
//            }
//        }

        if (member.nickname != null && member.nickname.length() > 0) {
            return;//已经有值不要再设置
        } else if (phoneNumber.attri == 0) {
            member.nickname = mApp.getText(R.string.relation_0).toString();
        } else if (phoneNumber.attri == 1) {
            member.nickname = mApp.getText(R.string.relation_1).toString();
        } else if (phoneNumber.attri == 2) {
            member.nickname = mApp.getText(R.string.relation_2).toString();
        } else if (phoneNumber.attri == 3) {
            member.nickname = mApp.getText(R.string.relation_3).toString();
        } else if (phoneNumber.attri == 4) {
            member.nickname = mApp.getText(R.string.relation_4).toString();
        } else if (phoneNumber.attri == 5) {
            member.nickname = mApp.getText(R.string.relation_5).toString();
        } else if (phoneNumber.attri == 6) {
            member.nickname = mApp.getText(R.string.relation_6).toString();
        } else if (phoneNumber.attri == 7) {
            member.nickname = mApp.getText(R.string.relation_7).toString();
        } else if (phoneNumber.attri == 8) {
            member.nickname = mApp.getText(R.string.relation_8).toString();
        } else if (phoneNumber.attri == 9) {
            member.nickname = mApp.getText(R.string.relation_9).toString();
        } else if (phoneNumber.attri == 10) {
            member.nickname = mApp.getText(R.string.relation_10).toString();
        } else if (phoneNumber.attri == 11) {
            member.nickname = mApp.getText(R.string.relation_11).toString();
        } else if (phoneNumber.attri == 12) {
            member.nickname = mApp.getText(R.string.relation_12).toString();
        } else if (phoneNumber.attri == 13) {
            member.nickname = mApp.getText(R.string.relation_13).toString();
        } else if (phoneNumber.attri == 14) {
            member.nickname = mApp.getText(R.string.relation_14).toString();
        } else if (phoneNumber.attri == 15) {
            member.nickname = mApp.getText(R.string.relation_15).toString();
        } else if (phoneNumber.attri == 16) {
            member.nickname = mApp.getText(R.string.relation_16).toString();
        } else if (phoneNumber.attri == 17) {
            member.nickname = mApp.getText(R.string.relation_17).toString();
        } else if (phoneNumber.attri == 18) {
            member.nickname = mApp.getText(R.string.relation_18).toString();
        } else if (phoneNumber.attri == 19) {
            member.nickname = mApp.getText(R.string.relation_19).toString();
        } else if (phoneNumber.attri == 20) {
            member.nickname = mApp.getText(R.string.relation_20).toString();
        } else if (phoneNumber.attri == 21) {
            member.nickname = mApp.getText(R.string.relation_21).toString();
        } else if (phoneNumber.attri == 22) {
            member.nickname = mApp.getText(R.string.relation_22).toString();
        } else if (phoneNumber.attri == 23) {
            member.nickname = mApp.getText(R.string.relation_23).toString();
        } else {
            if (phoneNumber.nickname != null) {
                member.nickname = phoneNumber.nickname;
            }
        }
    }

    private MemberUserData getWhiteMember(PhoneNumber userData, ArrayList<MemberUserData> phoneNumberList) {
        for (MemberUserData tmp : phoneNumberList) {
            if (userData.userEid != null && tmp.getEid() != null && userData.userEid.equals(tmp.getEid())) {
                return tmp;
            }
        }
        return null;
    }

    void initReceivers() {
        mMsgReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW)) {
                    try {
                        String gid = mFamily.getFamilyId();
                        for (FamilyData cur : mApp.getCurUser().getFamilyList()) {
                            if (cur.getFamilyId().equals(gid)) {
                                mFamily = cur;
                                break;
                            }
                        }
                        initViews();
                        loadAllMemberData();
                        mAdapter.notifyDataSetInvalidated();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (action.equals(Const.ACTION_CONTACT_CHANGE)) {
                    doGetContactsFromServer();
                } else if (action.equals(Const.ACTION_ADD_CALLMEMBER_OK)) {
                    doGetContactsFromServer();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_REFRESH_ALL_GROUPS_DATA_NOW);
        filter.addAction(Const.ACTION_CONTACT_CHANGE);
        filter.addAction(Const.ACTION_ADD_CALLMEMBER_OK);

        registerReceiver(mMsgReceiver, filter);
    }

    private void initViews() {
        mBtnBack = findViewById(R.id.iv_title_back);
        mBtnBack.setOnClickListener(this);
        mBtnHelpWeb = findViewById(R.id.ib_help_web);
        mBtnHelpWeb.setVisibility(View.GONE);
        mBtnHelpWeb.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.setting_home2));

        mMemberListView = findViewById(R.id.family_member_list);
        //sort list admin to first
        btnAddOne = findViewById(R.id.add_one_btn);
        btnAddOne.setOnClickListener(this);
        btnAddMember = findViewById(R.id.add_member_btn);
        btnAddMember.setOnClickListener(this);
        btnAddInvite = findViewById(R.id.add_invite_btn);
        btnAddInvite.setOnClickListener(this);
        setAddBtns();
        mAdapter = new FamilyMemberAdapter(allData, this);
        mMemberListView.setAdapter(mAdapter);

        mMemberListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {
                //管理员才可以操作，不可以操作自己，不可以操作管理员

                final GeneralMember member = allData.mGeneralMemberList.get(position);

                if (member.clickDialogType == 0) {
                    if (isMeAdmin() && member.cellnum != null && !member.isPrimaryContact ) {//编辑自己，
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_primary_contact).toString());
                        itemList.add(getText(R.string.set_memberinfo).toString());
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            phoneBookSetPrompt(member);
                                        } else if (Cposition == 2) {
                                            addCallMember(member);
                                        }
                                    }
                                });
                        dlg.show();
                    } else if (member.isEdit) {//编辑自己，
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_memberinfo).toString());
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            addCallMember(member);
                                        }
                                    }
                                });
                        dlg.show();
                    }
                } else if (member.clickDialogType == 1) {
                    if (isMeAdmin() && member.cellnum != null && member.isPrimaryContact == false) {//我是管理员
                        //删除白名单
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_primary_contact).toString());
                        itemList.add(getText(R.string.set_memberinfo).toString());
                        itemList.add(getString(R.string.remove_call_member));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            phoneBookSetPrompt(member);
                                        } else if (Cposition == 2) {
                                            addCallMember(member);
                                        } else if (Cposition == 3) {
                                            OpenRemovePhoneMember(member);
                                        }
                                    }
                                });
                        dlg.show();
                    } else {
                        //删除白名单
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_memberinfo).toString());
                        itemList.add(getString(R.string.remove_call_member));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            addCallMember(member);
                                        } else if (Cposition == 2) {
                                            OpenRemovePhoneMember(member);
                                        }
                                    }
                                });
                        dlg.show();
                    }
                } else if (member.clickDialogType == 2) {
                    if (isMeAdmin() && member.cellnum != null && member.isPrimaryContact == false) {//我是管理员
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_primary_contact).toString());
                        itemList.add(getString(R.string.remove_group_member));
                        itemList.add(getString(R.string.transfer_admin_rights));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            phoneBookSetPrompt(member);
                                        } else if (Cposition == 2) {
                                            OpenRemoveGroupMember(member, mFamily.getFamilyId());
                                        } else if (Cposition == 3) {
                                            OpenSetGroupAdminDialog(member, mFamily.getFamilyId());
                                        }
                                    }
                                });
                        dlg.show();
                    } else {
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getString(R.string.remove_group_member));
                        itemList.add(getString(R.string.transfer_admin_rights));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            OpenRemoveGroupMember(member, mFamily.getFamilyId());
                                        } else if (Cposition == 2) {
                                            OpenSetGroupAdminDialog(member, mFamily.getFamilyId());
                                        }
                                    }
                                });
                        dlg.show();
                    }
                } else if (member.clickDialogType == 4) {

                } else if (member.clickDialogType == 5) {
                    if (isMeAdmin() && member.isPrimaryContact == false) {
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getText(R.string.set_primary_contact).toString());
                        itemList.add(getString(R.string.remove_friend_member));
                        itemList.add(getString(R.string.modify_friend_name));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            phoneBookSetPrompt(member);
                                        } else if (Cposition == 2) {
                                            String nickname = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + "\"" + "</font>";
                                            String alertStr = getString(R.string.confirm_to_remove_friend_member, nickname);
                                            Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                                                    getString(R.string.remove_friend_member),
                                                    Html.fromHtml(alertStr),
                                                    new OnCustomDialogListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                        }
                                                    },
                                                    getText(R.string.cancel).toString(),
                                                    new OnCustomDialogListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            if (member.type == 2) {
                                                                int sn;
                                                                MyMsgData myMsgData = new MyMsgData();
                                                                myMsgData.setCallback(FamilyMemberActivity.this);
                                                                //set msg body
                                                                JSONObject newPl = new JSONObject();
                                                                newPl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
                                                                newPl.put("FriendEid", member.eid);
                                                                //notice info
                                                                sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
                                                                myMsgData.setReqMsg(
                                                                        CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_REMOVE_EID_FROM_FRIEND, sn, getMyApp().getToken(), newPl));
                                                                if (getMyApp().getNetService() != null)
                                                                    getMyApp().getNetService().sendNetMsg(myMsgData);
                                                            }
                                                        }
                                                    },
                                                    getText(R.string.confirm).toString());
                                            dlg.show();

                                        } else if (Cposition == 3) {
                                            if (member.cellnum == null || member.cellnum.equals("")) {
                                                ToastUtil.show(FamilyMemberActivity.this, getString(R.string.friend_have_no_cellnum));
                                                return;
                                            }
                                            Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(FamilyMemberActivity.this,
                                                    Integer.MAX_VALUE,
                                                    0,
                                                    getText(R.string.modify_friend_name).toString(),
                                                    member.nickname, getString(R.string.please_input_friend_name), new CustomSelectDialogUtil.CustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v, String text) {
                                                        }
                                                    },
                                                    getText(R.string.cancel).toString(),
                                                    new CustomSelectDialogUtil.CustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v, String text) {

                                                            if (text.length() < 1) {
                                                                ToastUtil.showMyToast(FamilyMemberActivity.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
                                                            } else {
                                                                String nickname = text;
                                                                if (nickname.equals(member.nickname)) {

                                                                } else {
                                                                    member.nickname = nickname;
                                                                    modifyFriendNameContactId = member.contactId;
                                                                    newFriendName = member.nickname;
                                                                    sendEditCallMemberReq(member);
                                                                }
                                                            }
                                                        }
                                                    },
                                                    getText(R.string.confirm).toString());
                                            dlg.show();
                                        }
                                    }
                                });
                        dlg.show();
                    } else {
                        ArrayList<String> itemList = new ArrayList<String>();
                        itemList.add(getString(R.string.remove_friend_member));
                        itemList.add(getString(R.string.modify_friend_name));
                        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(FamilyMemberActivity.this, itemList,
                                new AdapterItemClickListener() {
                                    @Override
                                    public void onClick(View v, int Cposition) {
                                        if (Cposition == 1) {
                                            String nickname = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + "\"" + "</font>";
                                            String alertStr = getString(R.string.confirm_to_remove_friend_member, nickname);
                                            Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                                                    getString(R.string.remove_friend_member),
                                                    Html.fromHtml(alertStr),
                                                    new OnCustomDialogListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                        }
                                                    },
                                                    getText(R.string.cancel).toString(),
                                                    new OnCustomDialogListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            if (member.type == 2) {
                                                                int sn;
                                                                MyMsgData myMsgData = new MyMsgData();
                                                                myMsgData.setCallback(FamilyMemberActivity.this);
                                                                //set msg body
                                                                JSONObject newPl = new JSONObject();
                                                                newPl.put(CloudBridgeUtil.KEY_NAME_EID, myApp.getCurUser().getFocusWatch().getEid());
                                                                newPl.put("FriendEid", member.eid);
                                                                //notice info
                                                                sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
                                                                myMsgData.setReqMsg(
                                                                        CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_REMOVE_EID_FROM_FRIEND, sn, getMyApp().getToken(), newPl));
                                                                if (getMyApp().getNetService() != null)
                                                                    getMyApp().getNetService().sendNetMsg(myMsgData);
                                                            }
                                                        }
                                                    },
                                                    getText(R.string.confirm).toString());
                                            dlg.show();

                                        } else if (Cposition == 2) {
                                            if (member.cellnum == null || member.cellnum.equals("")) {
                                                ToastUtil.show(FamilyMemberActivity.this, getString(R.string.friend_have_no_cellnum));
                                                return;
                                            }
                                            Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithParams(FamilyMemberActivity.this,
                                                    Integer.MAX_VALUE,
                                                    0,
                                                    getText(R.string.modify_friend_name).toString(),
                                                    member.nickname, getString(R.string.please_input_friend_name), new CustomSelectDialogUtil.CustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v, String text) {
                                                        }
                                                    },
                                                    getText(R.string.cancel).toString(),
                                                    new CustomSelectDialogUtil.CustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v, String text) {

                                                            if (text.length() < 1) {
                                                                ToastUtil.showMyToast(FamilyMemberActivity.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
                                                            } else {
                                                                String nickname = text;
                                                                if (nickname.equals(member.nickname)) {

                                                                } else {
                                                                    member.nickname = nickname;
                                                                    modifyFriendNameContactId = member.contactId;
                                                                    newFriendName = member.nickname;
                                                                    sendEditCallMemberReq(member);
                                                                }
                                                            }
                                                        }
                                                    },
                                                    getText(R.string.confirm).toString());
                                            dlg.show();
                                        }
                                    }
                                });
                        dlg.show();
                    }
                }
            }

        });
        mMemberListView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        mFamilyMemberTips = findViewById(R.id.family_member_list_tips);
        if (mCurWatch.isSupportPhoneCall()) {
            mFamilyMemberTips.setVisibility(View.VISIBLE);
        } else {
            mFamilyMemberTips.setVisibility(View.GONE);
        }

        jumpToWhiteListView();
    }

    private void addCallMember(GeneralMember member) {
        Intent intent = new Intent(FamilyMemberActivity.this, AddCallMemberActivity.class);
        intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
        intent.putExtra("nickname", member.nickname);
        intent.putExtra("phonenum", member.cellnum);
        intent.putExtra("sub_number", member.subnum);
        intent.putExtra("ring", member.ring);
        intent.putExtra("eid", member.eid);
        intent.putExtra("gid", member.gid);
        intent.putExtra("attri", member.attri);
        intent.putExtra("avatar", member.avatar);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, member.type);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_ID, member.contactId);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, member.weight);
        startActivity(intent);
    }

    private void jumpToWhiteListView() {
        if (!myApp.getCurUser().getFocusWatch().isDevice102()) {
            String content = getString(R.string.phone_book_tips_105);
            SpannableStringBuilder builder1 = new SpannableStringBuilder(content);
            ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(FamilyMemberActivity.this, WatchManagerActivity.class);
                    intent.putExtra(Const.IF_SCROLL_TO_BOTTOM, Const.IS_SCROLL_TO_BOTTOM);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.bg_color_orange));
                }
            };
            String whiteList = getString(R.string.setting_watch_white_list);
            int start = content.indexOf(whiteList);
            if (start >= 0) {
                builder1.setSpan(clickableSpan1, start, start + whiteList.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mFamilyMemberTips.setText(builder1);
            mFamilyMemberTips.setClickable(true);
            mFamilyMemberTips.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //设置首要联系人
    private void phoneBookSetPrompt(final GeneralMember member) {
        if (member.cellnum == null || member.cellnum.length() == 0) {
            ToastUtil.showMyToast(this,
                    getString(R.string.invalid_phone_number),
                    Toast.LENGTH_SHORT);
            return;
        }
        String nickname = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + " " + member.cellnum + "\"" + "</font>";
        String alertStr = getString(R.string.confirm_to_set_primary_contact, nickname);
        Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                getText(R.string.primary_contact).toString(),
                Html.fromHtml(alertStr),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    }
                },
                getText(R.string.cancel).toString(),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        if (!loadingdlg.isShowing()) {
                            loadingdlg.changeStatus(1, getString(R.string.saving));
                            loadingdlg.show();
                        }
                        trySetPhonebook(member);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private int getNewHighWeight() {
        int newHigh = 1;
        int lastHigh = 0;
        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber phoneNumber : mBindWhiteList) {
                if (phoneNumber.weight > lastHigh) {
                    lastHigh = phoneNumber.weight;
                }
            }
        }
        newHigh = lastHigh + 1;
        return newHigh;
    }

    private void trySetPhonebook(GeneralMember member) {
        //设置联系人权重
        setHighWeightContactId = member.contactId;
        newHighWeight = getNewHighWeight();
        if (mCurWatch.isDevice102()) {
            //兼容旧的设置电话本
            //设置weight值，重排phonelist,mapset
            doSetWeightWhiteList(member.contactId);
        } else {
            sendSetContactWeightReq(mCurWatch.getEid(), mCurWatch.getFamilyId(), member);
        }
    }

    private boolean isMeAdmin() {
        String myEid = myApp.getCurUser().getEid();
        String adminEid = mFamily.getAdminEId();
        boolean isMeAdmin = false;
        if (myEid.equals(adminEid)) {
            isMeAdmin = true;
        }
        return isMeAdmin;
    }

    private int delMemberMapgetSn = 0;//删除白名单成员前先要同步，记下任务sn

    private void OpenRemovePhoneMember(final GeneralMember member) {
        String nickname = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + "\"" + "</font>";
        String alertStr = getString(R.string.confirm_to_remove, nickname);
        Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                getString(R.string.remove_member),
                Html.fromHtml(alertStr),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    }
                },
                getText(R.string.cancel).toString(),
                new OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        //del phonewhitelist
                        //先mapget，同步后再del
                        //send mapset
                        if (!loadingdlg.isShowing()) {
                            loadingdlg.changeStatus(1, getString(R.string.removing));
                            loadingdlg.show();
                        }
                        PhoneNumber pm = doSendDelWatchContact(mCurWatch, member.contactId);
                        //更新家庭成员去重后的数据
                        if (pm != null) {
                            mBindWhiteList.remove(pm);
                            sendMapsetWhiteList();
                        }
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void OpenRemoveGroupMember(final GeneralMember member, final String gid) {
        String nickname = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + "\"" + "</font>";
        String alertStr = getString(R.string.confirm_to_remove_group_member, nickname);
        Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                getString(R.string.remove_member),
                Html.fromHtml(alertStr),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    }
                },
                getText(R.string.cancel).toString(),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        if (member.contactId != null) {
                            PhoneNumber pm = doSendDelWatchContact(mCurWatch, member.contactId);
                            //更新家庭成员去重后的数据
                            if (pm != null) {
                                mBindWhiteList.remove(pm);
                                sendMapsetWhiteList();
                            }
                        }
                        reqRemoveEndpointFormGroupWithNotice(member.eid, gid, member.nickname);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void OpenSetGroupAdminDialog(final GeneralMember member, final String gid) {

        if (member.cellnum == null || "".equals(member.cellnum)) {
            ToastUtil.show(FamilyMemberActivity.this, getString(R.string.transfer_rights_fail));
            return;
        }

        String textStr1 = "<font color=\"#a3a3a3\">" + getString(R.string.confirm_to_transfer_rights_to) + "</font>";
        String textStr2 = "<font color=\"" + getResources().getColor(R.color.bg_color_orange) + "\">" + "\"" + member.nickname + "\"" + "</font>";


        Dialog dlg = DialogUtil.CustomALertDialog(FamilyMemberActivity.this,
                getString(R.string.transfer_rights),
                Html.fromHtml(textStr1 + textStr2 + "？"),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                    }
                },
                getText(R.string.cancel).toString(),
                new OnCustomDialogListener() {

                    @Override
                    public void onClick(View v) {
                        reqSetGroupAdminWithNotice(member.eid, gid, member.nickname);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }


    private int reqRemoveEndpointFormGroupWithNotice(String eid, String gid, String nickname) {
        int sn;
        MyMsgData removeMsg = new MyMsgData();
        removeMsg.setCallback(FamilyMemberActivity.this);
        //set msg body
        JSONObject newPl = new JSONObject();
        newPl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        newPl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        //notice info
        newPl.put(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE, Integer.valueOf(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP).toString());
        newPl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, TimeUtil.getTimestampCHN());
        newPl.put(CloudBridgeUtil.KEY_NAME_NICKNAME, nickname);
        newPl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        newPl.put(CloudBridgeUtil.KEY_NAME_SMS_DATA, getNoticeGroupChangeSMS(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_LEAVE_GROUP, sn, eid, mFamily.getFamilyId(), TimeUtil.getTimestampCHN()));

        removeMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_REMOVE_EID_FROM_GROUP, sn, getMyApp().getToken(), newPl));
        if (getMyApp().getNetService() != null)
            getMyApp().getNetService().sendNetMsg(removeMsg);
        return sn;
    }

    private int reqSetGroupAdminWithNotice(String eid, String gid, String nickname) {
        int sn;
        MyMsgData removeMsg = new MyMsgData();
        removeMsg.setCallback(FamilyMemberActivity.this);
        //set msg body
        JSONObject newPl = new JSONObject();
        newPl.put(CloudBridgeUtil.KEY_NAME_GID, gid);
        newPl.put(CloudBridgeUtil.KEY_NAME_ADMIN_EID, eid);
        newPl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        //notice info
        newPl.put(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE, Integer.valueOf(CloudBridgeUtil.E2C_PL_KEY_NOTICE_TYPE_GET_ADMIN).toString());
        newPl.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, TimeUtil.getTimestampCHN());
        newPl.put(CloudBridgeUtil.KEY_NAME_NICKNAME, nickname);
        newPl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_GROUP_CHANGE_NOTICE);

        removeMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_SET_GROUP, sn, getMyApp().getToken(), newPl));
        if (null != getMyApp().getNetService()) {
            getMyApp().getNetService().sendNetMsg(removeMsg);
        }
        return sn;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContactListFromLocal();
        loadAllMemberData();
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReceivers();
    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mMsgReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddBtns() {

        btnAddOne.setVisibility(View.VISIBLE);
        btnAddInvite.setVisibility(View.GONE);
        btnAddMember.setVisibility(View.GONE);
    }

    private void refreshMemberList() {
        loadAllMemberData();
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mBtnBack) {
            finish();
        } else if (v == btnAddMember) {
            //通话成员数量满
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
            if (callmembernum >= numLimit) {
                ToastUtil.showMyToast(this,
                        getText(R.string.max_contact_prompt_msg).toString(),
                        Toast.LENGTH_SHORT);
            } else {
                Intent intent = new Intent(FamilyMemberActivity.this, AddCallMemberActivity.class);
                intent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 1);
                intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
                startActivity(intent);
            }
        } else if (v == btnAddInvite) {
            Intent intent = new Intent(FamilyMemberActivity.this, DeviceQrActivity.class);
            intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
            startActivity(intent);
        } else if (v == btnAddOne) {
            Intent intent = new Intent(FamilyMemberActivity.this, AddNewMemberActivity.class);
            intent.putExtra(Const.KEY_WATCH_ID, mCurWatch.getEid());
            intent.putExtra("call_member_count", callmembernum);
            intent.putExtra("watch_friend_count", watchFriendNum);
            startActivity(intent);
        } else if (v == mBtnHelpWeb) {
            startActivity(mApp.getHelpCenterIntent(FamilyMemberActivity.this, "familyMember"));
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_REMOVE_EID_FROM_GROUP_RESP:

                if (rc == CloudBridgeUtil.RC_SUCCESS || rc == -1) {

                    JSONObject reqPl = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                    String gid = (String) reqPl.get(CloudBridgeUtil.KEY_NAME_GID);
                    String eid = (String) reqPl.get(CloudBridgeUtil.KEY_NAME_EID);
                    for (MemberUserData member : mFamily.getMemberList()) {
                        if (member.getEid().equals(eid)) {
                            mFamily.getMemberList().remove(member);
                            break;
                        }
                    }
                    refreshMemberList();
                } else {
                    // myApp.doLogout();
                    ToastUtil.showMyToast(this, getText(R.string.set_error).toString(), Toast.LENGTH_SHORT);
                }
                break;

            case CloudBridgeUtil.CID_SET_GROUP_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject reqJo = CloudBridgeUtil.getCloudMsgPL(reqMsg);
                    mFamily.setAdminEId((String) reqJo.get(CloudBridgeUtil.KEY_NAME_ADMIN_EID));

                    //refersh
                    setAddBtns();
                    refreshMemberList();
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_RESP:
                int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                int reqSn = CloudBridgeUtil.getCloudMsgSN(reqMsg);
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject maggetPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单

                    String sTmp = (String) maggetPl.get(CloudBridgeUtil.PHONE_WHITE_LIST);
                    if (sTmp != null && sTmp.length() > 0) {
                        mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                        //持久化
                        myApp.setValue(mCurWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));
                        refreshMemberList();
                    }
                } else {

                }
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                loadingdlg.dismiss();
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    //删除
                    if (delContactId != null) {
                        delLocalWhitelist(delContactId);
                        delContactId = null;
                    }
                    //设置权重
                    if (setHighWeightContactId != null) {
                        setLocalWhitelistWeight(setHighWeightContactId);
                        setHighWeightContactId = null;
                    }

                    //持久化
                    myApp.setValue(mCurWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));
                    getContactListFromLocal();
                    refreshMemberList();
                } else {
                    //失败
                    getContactListFromLocal();//重新加载本地
                    refreshMemberList();
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getText(R.string.phone_set_timeout).toString(), Toast.LENGTH_SHORT);
                    }
                }
                break;
            case CloudBridgeUtil.CID_GET_CONTACT_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);//更新白名单
                    JSONArray obj = (JSONArray) pl.get(CloudBridgeUtil.KEY_NAME_CONTACT_SYNC_ARRAY);
                    String sTmp = obj.toString();

                    mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(sTmp);
                    //持久化
                    myApp.setValue(mCurWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, sTmp);
                    refreshMemberList();
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {

                    } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_MESSAGE_ILLEGAL) {//一般错误

                    }
                }
                break;
            case CloudBridgeUtil.CID_OPT_CONTACT_RESP:
                loadingdlg.dismiss();
                if (rc == CloudBridgeUtil.RC_SUCCESS || rc == -1) {
                    //删除
                    if (delContactId != null) {
                        delLocalWhitelist(delContactId);
                        delContactId = null;
                    }
                    //设置权重
                    if (setHighWeightContactId != null) {
                        setLocalWhitelistWeight(setHighWeightContactId);
                        setHighWeightContactId = null;
                    }

                    if (modifyFriendNameContactId != null) {
                        for (PhoneNumber phoneNumber : mBindWhiteList) {
                            if (modifyFriendNameContactId.equals(phoneNumber.id)) {
                                phoneNumber.nickname = newFriendName;
                                break;
                            }
                        }
                        modifyFriendNameContactId = null;
                    }

                    //持久化
                    myApp.setValue(mCurWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));
                    getContactListFromLocal();
                    refreshMemberList();
                } else {
                    getContactListFromLocal();
                    refreshMemberList();
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this,
                                getString(R.string.network_error_prompt),
                                Toast.LENGTH_SHORT);
                    } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_MESSAGE_ILLEGAL) {//一般错误
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getText(R.string.phone_set_timeout).toString(), Toast.LENGTH_SHORT);
                    }
                }
                break;
            default:
                break;

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
        buff.append(Integer.valueOf(type).toString());
        buff.append("@");
        buff.append(addGroupGid2);
        buff.append("@");
        buff.append(timeStampLocal);
        buff.append(">");
        return buff.toString();
    }

    private void getContactListFromLocal() {
        String jsonStr = myApp.getStringValue(mCurWatch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
    }

    private JSONArray plA = new JSONArray();

    PhoneNumber sendDelWhiteList(String delContactId) {
        PhoneNumber findPhone = null;
        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber tmpphone : mBindWhiteList) {
                if (tmpphone.id.equals(delContactId)) {//电话匹配则合并
                    findPhone = tmpphone;
                    break;
                }
            }
        }
        return findPhone;
    }

    int sendMapsetWhiteList() {
        if (mBindWhiteList.size() >= 0) {
            plA.clear();
            for (int i = 0; i < mBindWhiteList.size(); i++) {
                JSONObject plO = new JSONObject();
                plO.put("number", mBindWhiteList.get(i).number);
                if (mBindWhiteList.get(i).subNumber != null && mBindWhiteList.get(i).subNumber.length() > 0) {
                    plO.put("sub_number", mBindWhiteList.get(i).subNumber);
                }
                if (mBindWhiteList.get(i).ring != null) {
                    plO.put("ring", mBindWhiteList.get(i).ring);
                }
                if (mBindWhiteList.get(i).userEid != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, mBindWhiteList.get(i).userEid);
                }

                if (mBindWhiteList.get(i).userGid != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, mBindWhiteList.get(i).userGid);
                }

                if (mBindWhiteList.get(i).nickname != null) {
                    plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, mBindWhiteList.get(i).nickname);
                }
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, mBindWhiteList.get(i).weight);
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, mBindWhiteList.get(i).contactType);
                plO.put("attri", mBindWhiteList.get(i).attri);
                plO.put("timeStampId", mBindWhiteList.get(i).timeStampId);
                plA.add(plO);
            }
        }
        if (myApp.getNetService() != null) {
            return myApp.getNetService().sendMapSetMsg(mCurWatch.getEid(), mCurWatch.getFamilyId(),
                    CloudBridgeUtil.PHONE_WHITE_LIST, plA.toString(), FamilyMemberActivity.this);
        }
        return -1;
    }

    private int sendGetAllContactReq() {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(FamilyMemberActivity.this);
        JSONObject pl = new JSONObject();

        pl.put(CloudBridgeUtil.KEY_NAME_EID, mCurWatch.getEid());
        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_GET_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(req);
        }
        return sn;
    }

    private String delContactId = null;

    private PhoneNumber doSendDelWatchContact(WatchData watch, String contactId) {
        int sn;
        delContactId = contactId;
        if (watch.isDevice102()) {
            return sendDelWhiteList(contactId);
        } else {
            sendDelContactReq(watch.getEid(), watch.getFamilyId(), contactId);
            return null;
        }

    }

    private int sendDelContactReq(String watcheid, String watchGid, String contactId) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(FamilyMemberActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_GID, watchGid);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watcheid);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, contactId);
        int opt = 2;
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_OPT_TYPE, opt);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_OPT_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(req);
        }
        return sn;
    }

    private boolean delLocalWhitelist(String delContactId) {
        boolean canDel = false;
        PhoneNumber findPhone = null;
        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber tmpphone : mBindWhiteList) {

                if (tmpphone.id.equals(delContactId)) {//电话匹配则合并
                    findPhone = tmpphone;
                    canDel = true;
                    break;
                }
            }
            mBindWhiteList.remove(findPhone);
        }
        return canDel;
    }

    //从服务器MAPGET_MGET的方式取数据
    private void getPhoneWhiteListByMapget() {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(FamilyMemberActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mCurWatch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, CloudBridgeUtil.PHONE_WHITE_LIST);
        queryGroupsMsg.setReqMsg(
                CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_MAPGET,
                        Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(),
                        myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    //区分手表类型，采用不同接口同步联系人
    private void doGetContactsFromServer() {
        if (mCurWatch.isDevice102()) {
            getPhoneWhiteListByMapget();
        } else {
            sendGetAllContactReq();
        }
    }

    private int newHighWeight = -1;
    private String setHighWeightContactId = null;

    private int sendSetContactWeightReq(String watcheid, String watchGid, GeneralMember contact) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(FamilyMemberActivity.this);
        JSONObject pl = new JSONObject();


        pl.put(CloudBridgeUtil.KEY_NAME_GID, watchGid);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watcheid);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, contact.contactId);

        int opt = 1;
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, getNewHighWeight());
        if (contact.eid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, contact.eid);
        }
        if (contact.gid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, contact.gid);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NUMBER, contact.cellnum);
        if (contact.subnum != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER, contact.subnum);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, contact.nickname);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI, contact.attri);
        if (contact.ring != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_RING, contact.ring);
        }
        if (contact.avatar != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR, contact.avatar);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, contact.type);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_OPT_TYPE, opt);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_OPT_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(req);
        }
        return sn;
    }

    private void setLocalWhitelistWeight(String setContactId) {

        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber tmpphone : mBindWhiteList) {

                if (tmpphone.id.equals(setContactId)) {//电话匹配则合并
                    tmpphone.weight = newHighWeight;
                    break;
                }
            }

        }
    }

    int doSetWeightWhiteList(String contactId) {//设置weight，重排序，mapset

        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber tmpphone : mBindWhiteList) {
                if (tmpphone.id.equals(contactId)) {//电话匹配则合并
                    tmpphone.weight = getNewHighWeight();
                    break;
                }
            }
        }
        //排序
        //排序,weight高在前，weight相同，id小的在前
        Collections.sort(mBindWhiteList, new Comparator<PhoneNumber>() {//重排序

            @Override
            public int compare(PhoneNumber lhs, PhoneNumber rhs) {
                if (lhs.weight > rhs.weight) {
                    return -1;
                } else if (lhs.weight < rhs.weight) {
                    return 1;
                } else {
                    return lhs.id.compareTo(rhs.id);
                }
            }


        });
        return sendMapsetWhiteList();
    }

    private int sendEditCallMemberReq(GeneralMember member) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(FamilyMemberActivity.this);
        JSONObject pl = new JSONObject();

        pl.put(CloudBridgeUtil.KEY_NAME_GID, mCurWatch.getFamilyId());
        pl.put(CloudBridgeUtil.KEY_NAME_EID, mCurWatch.getEid());
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, member.contactId);
        if (member.eid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, member.eid);
        }
        if (member.gid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, member.gid);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NUMBER, member.cellnum);
        if (member.subnum != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER, member.subnum);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, member.nickname);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI, member.attri);
        if (member.ring != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_RING, member.ring);
        }
        if (member.avatar != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR, member.avatar);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, member.weight);

        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, member.type);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_OPT_TYPE, 1);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_OPT_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        if (getMyApp().getNetService() != null) {
            getMyApp().getNetService().sendNetMsg(req);
        }
        return sn;
    }
}
